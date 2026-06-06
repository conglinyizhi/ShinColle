package org.trp.shincolle.client.screen

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Inventory
import net.neoforged.neoforge.network.PacketDistributor
import org.trp.shincolle.client.gui.component.Sprites
import org.trp.shincolle.client.renderer.BookRenderer
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.menu.DeskMenu
import org.trp.shincolle.network.*
import org.trp.shincolle.reference.Values
import org.trp.shincolle.server.PlayerStateService
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class DeskScreen(menu: DeskMenu, inventory: Inventory, title: Component) :
    AbstractContainerScreen<DeskMenu>(menu, inventory, title) {

    private var pageId = 0
    private var chapId = 0
    private var entityTemp: LivingEntity? = null
    private var targetRotateX = 0f
    private var targetRotateY = 0f
    private var currentRotateX = 0f
    private var currentRotateY = 0f
    private var prevRotateX = 0f
    private var prevRotateY = 0f
    private var targetScale = 30f
    private var currentScale = 30f
    private var prevScale = 30f
    private var lastXMouse = 0.0
    private var lastYMouse = 0.0
    private var guiFunc = 0

    private var radarZoomLv = 0
    private val shipList = mutableListOf<RadarEntity>()
    private val diplomacyPlayers = mutableListOf<PlayerEntry>()
    private val selectedShips = mutableSetOf<UUID>()
    private var tickGUI = 0
    private val listNum = IntArray(5)
    private var guiScale = PREFERRED_GUI_SCALE
    private var guiScaleInv = 1.0f / PREFERRED_GUI_SCALE

    init {
        this.imageWidth = (BASE_GUI_WIDTH * this.guiScale).toInt()
        this.imageHeight = (BASE_GUI_HEIGHT * this.guiScale).toInt()
    }

    override fun init() {
        updateGuiScale()
        super.init()
        this.leftPos = (this.width - this.imageWidth) / 2
        this.topPos = (this.height - this.imageHeight) / 2

        when (menu.deskType) {
            0 -> { this.guiFunc = menu.guiFunc; this.radarZoomLv = menu.radarZoom; this.chapId = menu.chapter; this.pageId = menu.page }
            1 -> { this.guiFunc = 1; this.radarZoomLv = 0 }
            2 -> { this.guiFunc = 2; this.chapId = menu.chapter; this.pageId = menu.page }
        }
    }

    private fun syncBookState() {
        if (menu.deskType == 2 || (menu.deskType == 0 && guiFunc == 2)) {
            PacketDistributor.sendToServer(C2SBookStatePayload(chapId, pageId))
        }
    }

    private fun syncDeskGui() {
        if (menu.deskType == 0) {
            PacketDistributor.sendToServer(C2SDeskGuiPayload(guiFunc, radarZoomLv))
        }
    }

    override fun removed() {
        super.removed()
        this.selectedShips.clear()
        DeskDiplomacySync.clear()
        syncBookState()
        syncDeskGui()
    }

    override fun containerTick() {
        super.containerTick()
        this.tickGUI++
        if ((chapId == 4 || chapId == 5) && entityTemp is EntityShipBase) {
            val ship = entityTemp as EntityShipBase
            ship.tickCount++
            if (ship.attackTick > 0) {
                ship.attackTick = ship.attackTick - 1
            }
            if (ship.isSprinting) {
                ship.travel(net.minecraft.world.phys.Vec3(1.0, 0.0, 0.0))
            } else {
                ship.walkAnimation.setSpeed(0.0f)
            }
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        this.renderTooltip(guiGraphics, mouseX, mouseY)

        when (guiFunc) {
            1 -> drawRadarHoverText(guiGraphics, toGuiX(mouseX.toDouble()), toGuiY(mouseY.toDouble()), mouseX, mouseY)
            2 -> drawBookHoverText(guiGraphics, toGuiX(mouseX.toDouble()), toGuiY(mouseY.toDouble()), mouseX, mouseY)
            3, 4 -> drawDiplomacyHoverText(guiGraphics, toGuiX(mouseX.toDouble()), toGuiY(mouseY.toDouble()), mouseX, mouseY)
        }
    }

    private fun drawPageButtons(guiGraphics: GuiGraphics, mx: Int, my: Int) {
        if (chapId < 0 || chapId >= Values.PageLimit.size) return

        if (mx in 50..80 && my in 180..195) {
            if (pageId > 0) {
                guiGraphics.blit(BookRenderer.GUI_BOOK, 53, 182,
                    Sprites.DESK_BOOK_PREV_BTN_U.toFloat(), Sprites.DESK_BOOK_PREV_BTN_V.toFloat(),
                    Sprites.DESK_BOOK_PREV_BTN_W, Sprites.DESK_BOOK_PREV_BTN_H, 256, 256)
            }
        } else if (mx in 170..200 && my in 180..195) {
            if (pageId < Values.PageLimit[chapId]!!!!) {
                guiGraphics.blit(BookRenderer.GUI_BOOK, 175, 182,
                    Sprites.DESK_BOOK_NEXT_BTN_U.toFloat(), Sprites.DESK_BOOK_NEXT_BTN_V.toFloat(),
                    Sprites.DESK_BOOK_NEXT_BTN_W, Sprites.DESK_BOOK_NEXT_BTN_H, 256, 256)
            }
        }
    }

    private fun drawBookHoverText(guiGraphics: GuiGraphics, mx: Int, my: Int, mouseX: Int, mouseY: Int) {
        if (mx in 243..256) {
            if (my in 34..121) {
                val getbtn = (my - 34) / 12
                if (getbtn in 0 until 7) {
                    val strChap = Component.translatable("gui.shincolle.book.chap$getbtn.title").string
                    guiGraphics.renderTooltip(this.font, Component.literal(strChap), mouseX, mouseY)
                    return
                }
            }
        }

        val bookID = chapId * 1000 + pageId
        val cont = Values.BookList[bookID] ?: return

        for (getc in cont) {
            if (getc == null || getc.size < 5 || getc[0] != 2) continue
            val xa = if (getc[1] == 1) (getc[2] + 133 - 1) else (getc[2] + 13 - 1)
            val ya = getc[3] + 48
            if (mx in (xa - 1) until (xa + 17) && my in (ya - 1) until (ya + 17)) {
                val stack = Values.ItemIconMap[getc[4].toShort()]
                if (stack != null && !stack.isEmpty) {
                    guiGraphics.renderTooltip(this.font, stack, mouseX, mouseY)
                }
                break
            }
        }
    }

    private fun drawRadarHoverText(guiGraphics: GuiGraphics, mx: Int, my: Int, mouseX: Int, mouseY: Int) {
        if (mx in RADAR_ZOOM_X1..RADAR_ZOOM_X2 && my in RADAR_ZOOM_Y1..RADAR_ZOOM_Y2) {
            guiGraphics.renderTooltip(this.font, Component.translatable("gui.shincolle.radar.zoom.tooltip"), mouseX, mouseY)
            return
        }
        if (mx in RADAR_CLEAR_X1..RADAR_CLEAR_X2 && my in RADAR_CLEAR_Y1..RADAR_CLEAR_Y2) {
            guiGraphics.renderTooltip(this.font, Component.translatable("gui.shincolle.radar.clear.tooltip"), mouseX, mouseY)
            return
        }
        if (selectedShips.isNotEmpty() && mx in RADAR_ACTION_X1..RADAR_ACTION_X2 && my in RADAR_ACTION_Y1..RADAR_ACTION_Y2) {
            val key = if (menu.deskType == 0) "gui.shincolle.radar.action.recall.tooltip" else "gui.shincolle.radar.action.open.tooltip"
            guiGraphics.renderTooltip(this.font, Component.translatable(key), mouseX, mouseY)
            return
        }
        val list = mutableListOf<Component>()
        for (obj in shipList) {
            if (obj != null && obj.ship != null && mx < obj.pixelx + 4.0 && mx > obj.pixelx - 2.0 && my < obj.pixelz + 4.0 && my > obj.pixelz - 2.0) {
                list.add(obj.ship!!.name)
            }
        }
        if (list.isNotEmpty()) {
            guiGraphics.renderComponentTooltip(this.font, list, mouseX, mouseY)
        }
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.pose().pushPose()
        guiGraphics.pose().translate(this.leftPos.toFloat(), this.topPos.toFloat(), 0f)
        guiGraphics.pose().scale(this.guiScale, this.guiScale, 1.0f)

        if (menu.deskType == 0) {
            drawBaseBackground(guiGraphics)
        }

        when (guiFunc) {
            1 -> {
                drawRadarBackground(guiGraphics)
                drawRadarIcon(guiGraphics)
                drawMoraleIcon(guiGraphics)
                drawRadarText(guiGraphics)
            }
            2 -> {
                BookRenderer.drawBookBase(guiGraphics, 0, 0, chapId, pageId)
                BookRenderer.drawBookContent(guiGraphics, 0, 0, pageId, chapId, this.guiScale)

                if (chapId == 4 || chapId == 5) {
                    updateEntityTemp()
                    updateModelTransforms()
                    renderBookEntity(guiGraphics, 0, 0, partialTick)
                    renderShipNameIcons(guiGraphics, 0, 0)
                    renderPoseControls(guiGraphics, 0, 0)
                    BookRenderer.drawStateFlags(guiGraphics, 0, 0, entityTemp)
                    drawNoText(guiGraphics, 0, 0)
                }

                drawPageButtons(guiGraphics, toGuiX(mouseX.toDouble()), toGuiY(mouseY.toDouble()))
            }
            3, 4 -> drawDiplomacyScreen(guiGraphics)
        }

        guiGraphics.pose().popPose()
    }

    private fun drawBaseBackground(guiGraphics: GuiGraphics) {
        guiGraphics.blit(BookRenderer.GUI_DESK, 0, 0, 0f, 0f, 256, 192, 256, 256)
        if (guiFunc > 0) {
            val u = (guiFunc - 1) * 16
            val xOffset = when (guiFunc) { 1 -> 3; 2 -> 22; 3 -> 41; 4 -> 60; else -> 0 }
            if (u >= 0) {
                guiGraphics.blit(BookRenderer.GUI_DESK, xOffset, 2, u.toFloat(), 192f, 16, 16, 256, 256)
            }
        }
    }

    private fun drawRadarBackground(guiGraphics: GuiGraphics) {
        guiGraphics.blit(BookRenderer.GUI_RADAR, 0, 0, 0f, 0f, 256, 192, 256, 256)
        val texty = 192 + this.radarZoomLv * 8
        guiGraphics.blit(BookRenderer.GUI_RADAR, 9, 160, 24f, texty.toFloat(), 44, 8, 256, 256)
        for (i in 0 until 5) {
            val actualShipIndex = listNum[0] + i
            if (actualShipIndex < shipList.size && isSelectedShip(shipList[actualShipIndex])) {
                guiGraphics.blit(BookRenderer.GUI_RADAR, 142, 25 + i * 32,
                    Sprites.DESK_RADAR_SELECTED_ROW_U.toFloat(), Sprites.DESK_RADAR_SELECTED_ROW_V.toFloat(),
                    Sprites.DESK_RADAR_SELECTED_ROW_W, Sprites.DESK_RADAR_SELECTED_ROW_H, 256, 256)
            }
        }
        if (selectedShips.isNotEmpty()) {
            guiGraphics.blit(BookRenderer.GUI_RADAR, 88, 159,
                Sprites.DESK_RADAR_ACTION_BTN_U.toFloat(), Sprites.DESK_RADAR_ACTION_BTN_V.toFloat(),
                Sprites.DESK_RADAR_ACTION_BTN_W, Sprites.DESK_RADAR_ACTION_BTN_H, 256, 256)
        }
    }

    private fun drawRadarIcon(guiGraphics: GuiGraphics) {
        if (minecraft == null || minecraft!!.player == null || minecraft!!.level == null) return

        val ox = minecraft!!.player!!.x
        val oy = minecraft!!.player!!.y
        val oz = minecraft!!.player!!.z
        val radarScale = Math.pow(2.0, radarZoomLv.toDouble()).toFloat()

        shipList.clear()

        for (entity in minecraft!!.level!!.entitiesForRendering()) {
            if (entity is EntityShipBase && entity.isAlive && !entity.isRemoved && entity.ownerUUID != null && entity.ownerUUID == minecraft!!.player!!.uuid) {
                var px = (entity.x - ox) * radarScale
                val py = entity.y - oy
                var pz = (entity.z - oz) * radarScale
                px = Mth.clamp(px, -64.0, 64.0)
                pz = Mth.clamp(pz, -64.0, 64.0)

                val getent = RadarEntity(entity)
                getent.pixelx = 69.0 + px
                getent.pixely = py
                getent.pixelz = 88.0 + pz
                shipList.add(getent)

                val color = if (isSelectedShip(getent)) 0xFFFF0000.toInt() else 0xFFFFAFC9.toInt()
                guiGraphics.fill(69 + px.toInt(), 88 + pz.toInt(), 72 + px.toInt(), 91 + pz.toInt(), color)
            }
        }
    }

    private fun drawMoraleIcon(guiGraphics: GuiGraphics) {
        var texty = 37
        for (i in 0 until 5) {
            val index = listNum[0] + i
            if (index >= shipList.size) break

            val s = shipList[index]
            if (s != null && s.ship is EntityShipBase) {
                val s2 = s.ship as EntityShipBase
                val ix = getMoraleLevel(s2.morale) * 11
                guiGraphics.blit(BookRenderer.GUI_NAME_ICON0, 237, texty - 1, ix.toFloat(), 240f, 11, 11, 256, 256)
            }
            texty += 32
        }
    }

    private fun getMoraleLevel(morale: Int): Int {
        if (morale >= 12000) return 0
        if (morale >= 5000) return 1
        if (morale >= 2000) return 2
        return 3
    }

    private fun drawRadarText(guiGraphics: GuiGraphics) {
        var texty = 27
        for (i in 0 until 5) {
            val index = listNum[0] + i
            if (index >= shipList.size) break

            val s = shipList[index]
            if (s == null || s.ship !is EntityShipBase) continue
            val s2 = s.ship as EntityShipBase

            guiGraphics.drawString(this.font, s.ship!!.name.string, 147, texty, 0xFFFFFF, false)

            val str = "LV ${ChatFormatting.YELLOW}${s2.level}   ${ChatFormatting.GOLD}${s2.health.toInt()}${ChatFormatting.RED} / ${s2.maxHealth.toInt()}"
            val str2 = "Pos: ${ChatFormatting.YELLOW}${Mth.ceil(s.ship!!.x)}, ${Mth.ceil(s.ship!!.z)}  H: ${ChatFormatting.YELLOW}${s.ship!!.y.toInt()}"

            guiGraphics.pose().pushPose()
            guiGraphics.pose().scale(0.8f, 0.8f, 1.0f)
            guiGraphics.drawString(this.font, str, (147 / 0.8f).toInt(), ((texty + 12) / 0.8f).toInt(), 0xFF00FFFF.toInt(), false)
            guiGraphics.drawString(this.font, str2, (147 / 0.8f).toInt(), ((texty + 21) / 0.8f).toInt(), 0xFFA000A0.toInt(), false)
            guiGraphics.pose().popPose()

            texty += 32
        }
    }

    private fun drawDiplomacyScreen(guiGraphics: GuiGraphics) {
        if (minecraft == null || minecraft!!.player == null || minecraft!!.level == null) return

        updateDiplomacyPlayers()
        drawDiplomacyButtons(guiGraphics)

        val titleKey = if (guiFunc == 3) "gui.shincolle.team.allylist" else "gui.shincolle.team.banlist"
        guiGraphics.drawString(this.font, Component.translatable(titleKey), 10, 28, 0xFFFFFF, false)
        guiGraphics.drawString(this.font, Component.translatable("gui.shincolle.team.diplomacy_hint"), 10, 40, 0xB0B0B0, false)

        val startIndex = diplomacyScrollIndex
        val endIndex = minOf(diplomacyPlayers.size, startIndex + DIPLOMACY_VISIBLE_ROWS)
        var y = DIPLOMACY_LIST_Y
        for (index in startIndex until endIndex) {
            val entry = diplomacyPlayers[index]
            val color = when (entry.relation) { 1 -> 0x55FFFF.toInt(); 2 -> 0xFFAA00.toInt(); else -> 0xFFFFFF }
            val bgColor = if (entry.selected) 0x50505090.toInt() else 0x30000000.toInt()
            guiGraphics.fill(8, y - 2, 248, y + 18, bgColor)
            guiGraphics.drawString(this.font, entry.name, 14, y, color, false)
            val state = when (entry.relation) {
                1 -> Component.translatable("gui.shincolle.team.state.ally")
                2 -> Component.translatable("gui.shincolle.team.state.hostile")
                else -> Component.translatable("gui.shincolle.team.state.neutral")
            }
            guiGraphics.drawString(this.font, state, 180, y, color, false)
            y += DIPLOMACY_ROW_HEIGHT
        }

        if (diplomacyPlayers.size > DIPLOMACY_VISIBLE_ROWS) {
            val page = "${startIndex + 1}-$endIndex / ${diplomacyPlayers.size}"
            guiGraphics.drawString(this.font, page, 150, 40, 0xB0B0B0, false)
        }
    }

    private fun drawDiplomacyButtons(guiGraphics: GuiGraphics) {
        val selected = selectedDiplomacyEntry
        var topLabel = Component.empty()
        var topColor = 0x7F7F7F
        if (selected != null) {
            val activeRelation = if (guiFunc == 3) selected.relation == 1 else selected.relation == 2
            topLabel = Component.translatable(
                if (activeRelation)
                    if (guiFunc == 3) "gui.shincolle.team.break" else "gui.shincolle.team.unban"
                else
                    if (guiFunc == 3) "gui.shincolle.team.ally" else "gui.shincolle.team.ban"
            )
            topColor = if (activeRelation) 0xFFD54F.toInt() else if (guiFunc == 3) 0x55FFFF.toInt() else 0xFFAA00.toInt()
        }

        guiGraphics.drawString(this.font, topLabel, DIPLOMACY_BUTTON_LEFT, DIPLOMACY_BUTTON_TOP_Y, topColor, false)
        guiGraphics.drawString(this.font, Component.translatable("gui.shincolle.team.back"), DIPLOMACY_BUTTON_LEFT, DIPLOMACY_BUTTON_BOTTOM_Y, 0xFFFFFF, false)
    }

    private fun drawDiplomacyHoverText(guiGraphics: GuiGraphics, mx: Int, my: Int, mouseX: Int, mouseY: Int) {
        val startIndex = diplomacyScrollIndex
        val endIndex = minOf(diplomacyPlayers.size, startIndex + DIPLOMACY_VISIBLE_ROWS)
        var y = DIPLOMACY_LIST_Y
        for (index in startIndex until endIndex) {
            if (mx in 8..248 && my in (y - 2)..(y + 18)) {
                val entry = diplomacyPlayers[index]
                guiGraphics.renderTooltip(this.font, Component.literal(entry.name), mouseX, mouseY)
                return
            }
            y += DIPLOMACY_ROW_HEIGHT
        }
    }

    private fun updateDiplomacyPlayers() {
        if (minecraft == null || minecraft!!.player == null || minecraft!!.level == null) {
            diplomacyPlayers.clear()
            return
        }

        val previouslySelected = diplomacyPlayers.firstOrNull { it.selected }?.uuid
        diplomacyPlayers.clear()

        val entriesByUuid = linkedMapOf<UUID, PlayerEntry>()

        for (uuid in DeskDiplomacySync.getAllies()) {
            if (uuid == null || uuid == minecraft!!.player!!.uuid) continue
            val entry = PlayerEntry().apply {
                this.uuid = uuid
                this.name = formatDiplomacyName(uuid)
                this.relation = 1
                this.selected = uuid == previouslySelected
            }
            entriesByUuid[uuid] = entry
        }

        for (uuid in DeskDiplomacySync.getBanned()) {
            if (uuid == null || uuid == minecraft!!.player!!.uuid) continue
            val entry = entriesByUuid.getOrPut(uuid) {
                PlayerEntry().apply {
                    this.uuid = uuid
                    this.name = formatDiplomacyName(uuid)
                    this.selected = uuid == previouslySelected
                }
            }
            entry.relation = 2
        }

        for (player in minecraft!!.level!!.players()) {
            if (player == minecraft!!.player) continue
            val entry = entriesByUuid.getOrPut(player.uuid) {
                PlayerEntry().apply {
                    this.uuid = player.uuid
                    this.selected = player.uuid == previouslySelected
                }
            }
            entry.name = player.name.string
            entry.relation = if (DeskDiplomacySync.isBanned(entry.uuid)) 2 else if (DeskDiplomacySync.isAlly(entry.uuid)) 1 else 0
        }
        diplomacyPlayers.addAll(entriesByUuid.values)
        diplomacyPlayers.sortBy { it.name.lowercase() }
        clampDiplomacyScroll()
    }

    private fun formatDiplomacyName(uuid: UUID): String {
        if (uuid == null) return "-"
        val leaderName = DeskDiplomacySync.getLeaderName(uuid)
        val teamName = DeskDiplomacySync.getTeamName(uuid)
        return when {
            leaderName!!.isNotBlank() && teamName!!.isNotBlank() -> "$leaderName - $teamName"
            leaderName!!.isNotBlank() -> leaderName!!
            teamName!!.isNotBlank() -> teamName!!
            else -> "${uuid.toString().substring(0, 8)}…"
        }
    }

    private var diplomacyScrollIndex: Int
        get() = listNum[if (guiFunc == 3) 3 else 4]
        set(value) { listNum[if (guiFunc == 3) 3 else 4] = value }

    private fun getDiplomacyMaxScroll(): Int {
        return maxOf(0, diplomacyPlayers.size - DIPLOMACY_VISIBLE_ROWS)
    }

    private fun clampDiplomacyScroll() {
        diplomacyScrollIndex = Mth.clamp(diplomacyScrollIndex, 0, getDiplomacyMaxScroll())
    }

    private fun drawNoText(guiGraphics: GuiGraphics, x: Int, y: Int) {
        if (pageId > 0 && entityTemp is EntityShipBase) {
            val ship = entityTemp as EntityShipBase
            val str = "No. $pageId"
            val color = if (chapId == 4) 0xAA0000 else 0x00AAAA
            guiGraphics.drawString(this.font, str, x + 55, y + 32, color, false)
        }
    }

    private fun renderShipNameIcons(guiGraphics: GuiGraphics, x: Int, y: Int) {
        if (entityTemp !is EntityShipBase) return
        val ship = entityTemp as EntityShipBase

        val shipType = ship.getStateMinor(19)
        val typeXY = Values.ShipTypeIconMap[shipType.toByte()]

        val shipClass = ship.getStateMinor(EntityShipBase.STATE_MINOR_SHIP_CLASS)
        val classXY = Values.ShipNameIconMap[shipClass] ?: return

        if (typeXY != null) {
            RenderSystem.setShaderTexture(0, BookRenderer.GUI_NAME_ICON0)
            guiGraphics.blit(BookRenderer.GUI_NAME_ICON0, x + 23, y + 53, typeXY[0].toFloat(), typeXY[1].toFloat(), 28, 28, 256, 256)
        }

        val iconTexture = if (classXY[0] >= 101) BookRenderer.GUI_NAME_ICON2 else BookRenderer.GUI_NAME_ICON1
        var offY = 0
        if (classXY[0] == 4 || classXY[0] == 6) offY = -10
        else if (classXY[0] >= 101) offY = 10

        RenderSystem.setShaderTexture(0, iconTexture)
        guiGraphics.blit(iconTexture, x + 30, y + 94 + offY, classXY[1].toFloat(), classXY[2].toFloat(), 11, 59, 256, 256)
    }

    private fun renderPoseControls(guiGraphics: GuiGraphics, x: Int, y: Int) {
        // Kept as empty per original Java implementation
    }

    private fun updateEntityTemp() {
        var classID = -1
        if (pageId > 0) {
            classID = when {
                chapId == 4 && pageId - 1 < Values.ShipBookList.size -> Values.ShipBookList[pageId - 1]!!
                chapId == 5 && pageId - 1 < Values.EnemyBookList.size -> Values.EnemyBookList[pageId - 1]!!
                else -> -1
            }
        }

        if (classID < 0) { entityTemp = null; return }

        val type = getEntityTypeFromClassID(classID) ?: run { entityTemp = null; return }

        if (entityTemp == null || entityTemp!!.type !== type) {
            if (minecraft?.level != null) {
                entityTemp = type.create(minecraft!!.level) as? LivingEntity
                if (entityTemp is EntityShipBase) {
                    val ship = entityTemp as EntityShipBase
                    ship.level = 1
                    ship.ammoLight = 100
                    ship.ammoHeavy = 100
                    ship.fuel = 100
                }
                currentRotateX = 0f; currentRotateY = 0f; targetRotateX = 0f; targetRotateY = 0f
                currentScale = 30f; targetScale = 30f; prevRotateX = 0f; prevRotateY = 0f; prevScale = 30f
            }
        }
    }

    private fun getEntityTypeFromClassID(classID: Int): EntityType<*>? {
        return when (classID) {
            0 -> ModEntities.DESTROYER_I.get(); 1 -> ModEntities.DESTROYER_RO.get()
            2 -> ModEntities.DESTROYER_HA.get(); 3 -> ModEntities.DESTROYER_NI.get()
            9 -> ModEntities.HEAVY_CRUISER_RI.get(); 10 -> ModEntities.HEAVY_CRUISER_NE.get()
            12 -> ModEntities.CARRIER_WO.get(); 13 -> ModEntities.BATTLESHIP_RU.get()
            14 -> ModEntities.BATTLESHIP_TA.get(); 15 -> ModEntities.BATTLESHIP_RE.get()
            16 -> ModEntities.TRANSPORT_WA.get(); 17 -> ModEntities.SUBM_KA.get()
            18 -> ModEntities.SUBM_YO.get(); 19 -> ModEntities.SUBM_SO.get()
            20 -> ModEntities.CARRIER_HIME.get(); 21 -> ModEntities.AIRFIELD_HIME.get()
            26 -> ModEntities.BATTLESHIP_HIME.get(); 27 -> ModEntities.DESTROYER_HIME.get()
            28 -> ModEntities.HARBOUR_HIME.get(); 29 -> ModEntities.ISOLATED_HIME.get()
            30 -> ModEntities.MIDWAY_HIME.get(); 31 -> ModEntities.NORTHERN_HIME.get()
            44 -> ModEntities.SUBM_HIME.get(); 72 -> ModEntities.SSNH.get()
            33 -> ModEntities.CARRIER_W_DEMON.get(); 49 -> ModEntities.CA_HIME.get()
            36 -> ModEntities.DESTROYER_SHIMAKAZE.get(); 37 -> ModEntities.BATTLESHIP_NAGATO.get()
            38 -> ModEntities.SUBM_U511.get(); 39 -> ModEntities.SUBM_RO500.get()
            46 -> ModEntities.BATTLESHIP_YAMATO.get(); 47 -> ModEntities.CARRIER_KAGA.get()
            48 -> ModEntities.CARRIER_AKAGI.get(); 51 -> ModEntities.DESTROYER_AKATSUKI.get()
            52 -> ModEntities.DESTROYER_HIBIKI.get(); 53 -> ModEntities.DESTROYER_IKAZUCHI.get()
            54 -> ModEntities.DESTROYER_INAZUMA.get(); 56 -> ModEntities.CRUISER_TENRYUU.get()
            57 -> ModEntities.CRUISER_TATSUTA.get(); 58 -> ModEntities.CRUISER_ATAGO.get()
            59 -> ModEntities.CRUISER_TAKAO.get(); 60 -> ModEntities.BB_KONGOU.get()
            61 -> ModEntities.BB_HIEI.get(); 62 -> ModEntities.BB_HARUNA.get()
            63 -> ModEntities.BB_KIRISHIMA.get()
            else -> null
        }
    }

    private fun handleBookModelControls(btn: Int) {
        if (entityTemp !is EntityShipBase) return
        val ship = entityTemp as EntityShipBase

        when (btn) {
            1 -> {
                ship.isOrderedToSit = !ship.isOrderedToSit
                ship.setStateEmotion(1, if (ship.random.nextInt(2) == 0) 4 else 0, false)
                ship.setStateEmotion(7, if (ship.random.nextInt(2) == 0) 4 else 0, false)
            }
            2 -> ship.isSprinting = !ship.isSprinting
            3 -> {
                ship.attackTick = 50
                ship.setStateEmotion(5, ship.random.nextInt(4), false)
            }
            4 -> {
                ship.setStateEmotion(7, if (ship.random.nextInt(2) == 0) 4 else 0, false)
                ship.isShiftKeyDown = ship.random.nextInt(5) == 0
                ship.setStateFlag(2, ship.random.nextInt(8) == 0)
                ship.setStateEmotion(1, ship.random.nextInt(10), false)
            }
            else -> {
                if (btn in 5..20) {
                    val bit = btn - 5
                    val stats = ship.getStateEmotion(0)
                    val newValue = ((stats shr bit) and 1) == 0
                    ship.setStateEmotion(0, stats xor (1 shl bit), false)

                    val options = ship.equipOptions
                    if (bit < options.size) {
                        ship.setEquipFlag(options[bit]!!.key!!, newValue)
                    }
                }
            }
        }
    }

    private fun isCollected(classID: Int): Boolean {
        if (minecraft == null || minecraft!!.player == null) return false
        if (minecraft!!.player!!.isCreative) return true
        return PlayerStateService.hasCollectedShip(minecraft!!.player!!, classID)
    }

    private fun renderBookEntity(guiGraphics: GuiGraphics, x: Int, y: Int, partialTick: Float) {
        if (entityTemp != null) {
            val classID = when {
                chapId == 4 && pageId - 1 < Values.ShipBookList.size -> Values.ShipBookList[pageId - 1]!!
                chapId == 5 && pageId - 1 < Values.EnemyBookList.size -> Values.EnemyBookList[pageId - 1]!!
                else -> -1
            }

            val collected = isCollected(classID)

            val renderScale = prevScale + (currentScale - prevScale) * partialTick
            val rotX = prevRotateX + (currentRotateX - prevRotateX) * partialTick
            val rotY = prevRotateY + (currentRotateY - prevRotateY) * partialTick

            val px = x + 72
            val py = y + 110 + (renderScale * 1.1f).toInt()

            guiGraphics.pose().pushPose()
            guiGraphics.pose().translate(px.toFloat(), py.toFloat(), 50.0f)
            guiGraphics.pose().scale(-renderScale, renderScale, renderScale)
            guiGraphics.pose().mulPose(org.joml.Quaternionf().rotateZ(Math.toRadians(180.0).toFloat()))
            guiGraphics.pose().translate(0.0, 0.7, 0.0)
            guiGraphics.pose().mulPose(org.joml.Quaternionf().rotateY(Math.toRadians(rotY.toDouble()).toFloat()))
            guiGraphics.pose().mulPose(org.joml.Quaternionf().rotateX(Math.toRadians(rotX.toDouble()).toFloat()))
            guiGraphics.pose().translate(0.0, -0.7, 0.0)

            if (!collected) {
                RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f)
            }

            val dispatcher = minecraft!!.entityRenderDispatcher
            dispatcher.setRenderShadow(false)
            RenderSystem.runAsFancy {
                if (entityTemp!!.vehicle is EntityMountBase) {
                    val mount = entityTemp!!.vehicle as EntityMountBase
                    val seatPos = mount.seatPos
                    guiGraphics.pose().pushPose()
                    guiGraphics.pose().translate(seatPos[2].toDouble(), seatPos[1].toDouble(), seatPos[0].toDouble())
                    dispatcher.render(entityTemp!!, 0.0, 0.0, 0.0, 0f, 1f, guiGraphics.pose(), guiGraphics.bufferSource(), 15728880)
                    guiGraphics.pose().popPose()
                    dispatcher.render(mount, 0.0, 0.0, 0.0, 0f, 1f, guiGraphics.pose(), guiGraphics.bufferSource(), 15728880)
                } else {
                    dispatcher.render(entityTemp!!, 0.0, 0.0, 0.0, 0f, 1f, guiGraphics.pose(), guiGraphics.bufferSource(), 15728880)
                }
            }
            guiGraphics.flush()
            dispatcher.setRenderShadow(true)

            if (!collected) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            }

            guiGraphics.pose().popPose()
        }
    }

    private fun updateModelTransforms() {
        prevRotateX = currentRotateX
        prevRotateY = currentRotateY
        prevScale = currentScale
        val smoothingFactor = 0.7f
        currentRotateX += (targetRotateX - currentRotateX) * smoothingFactor
        currentRotateY += (targetRotateY - currentRotateY) * smoothingFactor
        currentScale += (targetScale - currentScale) * smoothingFactor
    }

    private fun updateGuiScale() {
        val maxScaleX = this.width.toFloat() / BASE_GUI_WIDTH
        val maxScaleY = this.height.toFloat() / BASE_GUI_HEIGHT
        val allowedScale = minOf(PREFERRED_GUI_SCALE, minOf(maxScaleX, maxScaleY))
        guiScale = Mth.clamp(allowedScale, MIN_GUI_SCALE, PREFERRED_GUI_SCALE)
        guiScaleInv = 1.0f / guiScale
        this.imageWidth = (BASE_GUI_WIDTH * guiScale).toInt()
        this.imageHeight = (BASE_GUI_HEIGHT * guiScale).toInt()
    }

    private fun toGuiX(mouseX: Double): Int {
        return ((mouseX - this.leftPos) * guiScaleInv).toInt()
    }

    private fun toGuiY(mouseY: Double): Int {
        return ((mouseY - this.topPos) * guiScaleInv).toInt()
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
        val mx = (mouseX - leftPos) * guiScaleInv
        val my = (mouseY - topPos) * guiScaleInv
        val dx = mx - lastXMouse
        val dy = my - lastYMouse
        lastXMouse = mx
        lastYMouse = my

        if (kotlin.math.abs(dx) > 20 || kotlin.math.abs(dy) > 20) return super.mouseDragged(mouseX, mouseY, button, dragX, dragY)

        if ((chapId == 4 || chapId == 5) && mx > 8 && mx < 117 && my > 47 && my < 154) {
            if (dx != 0.0) targetRotateY += (dx * 3.0f).toFloat()
            if (dy != 0.0) targetRotateX = Mth.clamp(targetRotateX + (dy * 2.0f).toFloat(), -90.0f, 90.0f)
            return true
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        val mx = (mouseX - leftPos) * guiScaleInv
        val my = (mouseY - topPos) * guiScaleInv

        if (menu.deskType == 1) {
            if (scrollY > 0) { if (listNum[0] > 0) listNum[0]-- }
            else if (scrollY < 0) { if (listNum[0] < shipList.size - 1) listNum[0]++ }
            return true
        }

        if ((guiFunc == 3 || guiFunc == 4) && mx in 8.0..248.0 && my in (DIPLOMACY_LIST_Y - 2).toDouble()..(DIPLOMACY_LIST_Y - 2 + DIPLOMACY_VISIBLE_ROWS * DIPLOMACY_ROW_HEIGHT).toDouble()) {
            if (scrollY > 0) diplomacyScrollIndex--
            else if (scrollY < 0) diplomacyScrollIndex++
            clampDiplomacyScroll()
            return true
        }

        if ((chapId == 4 || chapId == 5) && mx > 8 && mx < 117 && my > 47 && my < 154) {
            if (scrollY > 0) targetScale += 5.0f
            else if (scrollY < 0) targetScale -= 5.0f
            targetScale = Mth.clamp(targetScale, 10.0f, 150.0f)
            return true
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }

    override fun renderLabels(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        // Intentionally empty per original Java implementation
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val mx = toGuiX(mouseX)
        val my = toGuiY(mouseY)
        lastXMouse = mx.toDouble()
        lastYMouse = my.toDouble()

        if (menu.deskType == 0) {
            if (my in 2..18) {
                when {
                    mx in 3..19 -> { setDeskFunction(1); return true }
                    mx in 22..38 -> { setDeskFunction(2); return true }
                    mx in 41..57 -> { setDeskFunction(3); return true }
                    mx in 60..76 -> { setDeskFunction(4); return true }
                }
            }
        }

        if (guiFunc == 1) {
            if (mx in RADAR_ZOOM_X1..RADAR_ZOOM_X2 && my in RADAR_ZOOM_Y1..RADAR_ZOOM_Y2) {
                radarZoomLv = (radarZoomLv + 1) % 3
                syncDeskGui()
                return true
            }
            if (mx in RADAR_CLEAR_X1..RADAR_CLEAR_X2 && my in RADAR_CLEAR_Y1..RADAR_CLEAR_Y2) {
                selectedShips.clear()
                return true
            }
            if (mx in 142..250) {
                for (i in 0 until 5) {
                    val ry = 25 + i * 32
                    if (my in ry..(ry + 31)) {
                        val index = listNum[0] + i
                        if (index < shipList.size) {
                            val shipUuid = getShipUuid(shipList[index])
                            if (shipUuid == null) return true
                            val sameSelection = selectedShips.size == 1 && selectedShips.contains(shipUuid)
                            if (hasShiftDown() && menu.deskType == 0) {
                                if (!selectedShips.remove(shipUuid)) selectedShips.add(shipUuid)
                                return true
                            }
                            if (sameSelection) {
                                openRadarSelectedShip(shipUuid)
                                return true
                            }
                            selectedShips.clear()
                            selectedShips.add(shipUuid)
                        }
                        return true
                    }
                }
            }
            if (selectedShips.isNotEmpty() && mx in RADAR_ACTION_X1..RADAR_ACTION_X2 && my in RADAR_ACTION_Y1..RADAR_ACTION_Y2) {
                handleRadarActionButton()
                return true
            }
            selectedShips.clear()
            return true
        }

        if (guiFunc == 3 || guiFunc == 4) {
            if (handleDiplomacyButtonClick(mx, my)) return true
            val startIndex = diplomacyScrollIndex
            val endIndex = minOf(diplomacyPlayers.size, startIndex + DIPLOMACY_VISIBLE_ROWS)
            var y = DIPLOMACY_LIST_Y
            for (index in startIndex until endIndex) {
                if (mx in 8..248 && my in (y - 2)..(y + 18)) {
                    selectDiplomacyEntry(diplomacyPlayers[index])
                    return true
                }
                y += DIPLOMACY_ROW_HEIGHT
            }
            return true
        }

        if (guiFunc == 2) {
            if (my in 180..195) {
                if (mx in 50..80) {
                    if (pageId > 0) {
                        pageId -= if (button == 1) 10 else 1
                        if (pageId < 0) pageId = 0
                        syncBookState()
                    }
                    return true
                }
                if (mx in 170..200) {
                    if (chapId in Values.PageLimit.indices && pageId < Values.PageLimit[chapId]!!!!) {
                        pageId += if (button == 1) 10 else 1
                        if (pageId > Values.PageLimit[chapId]!!!!) pageId = Values.PageLimit[chapId]!!!!
                        syncBookState()
                    }
                    return true
                }
            }

            if (mx in 243..256 && my in 34..121) {
                val getbtn = (my - 34) / 12
                if (getbtn in 0 until 7) {
                    chapId = getbtn
                    pageId = 0
                    syncBookState()
                    syncDeskGui()
                    return true
                }
            }

            if (entityTemp != null && (chapId == 4 || chapId == 5)) {
                if (mx in 22..30) {
                    if (my in 158..166) { handleBookModelControls(1); return true }
                    if (my in 169..177) { handleBookModelControls(3); return true }
                }
                if (mx in 33..41) {
                    if (my in 158..166) { handleBookModelControls(2); return true }
                    if (my in 169..177) { handleBookModelControls(4); return true }
                }
                var drawIdx = 0
                val startIdx = if (entityTemp is EntityShipBase && (entityTemp as EntityShipBase).hasShipMounts()) 1 else 0
                for (i in startIdx until 16) {
                    if (i >= (entityTemp as EntityShipBase).getStateMinor(13) ?: 0!!!!) break
                    val dx = 45 + (drawIdx % 8) * 9
                    val dy = 158 + (drawIdx / 8) * 9
                    if (mx in dx..(dx + 7) && my in dy..(dy + 9)) {
                        handleBookModelControls(5 + i)
                        return true
                    }
                    drawIdx++
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    private fun openRadarSelectedShip(shipUuid: UUID) {
        if (minecraft == null || minecraft!!.player == null) return
        PacketDistributor.sendToServer(C2SDeskOpenShipPayload(shipUuid))
        minecraft!!.player!!.closeContainer()
    }

    private fun setDeskFunction(func: Int) {
        selectedShips.clear()
        guiFunc = if (guiFunc == func) 0 else func
        syncDeskGui()
    }

    private fun handleRadarActionButton() {
        if (menu.deskType == 0) {
            summonSelectedShipsToDesk()
        } else if (selectedShips.size == 1) {
            openRadarSelectedShip(selectedShips.iterator().next())
        }
    }

    private fun summonSelectedShipsToDesk() {
        if (selectedShips.isEmpty() || minecraft == null || minecraft!!.player == null) return
        PacketDistributor.sendToServer(C2SDeskSummonPayload(ArrayList(selectedShips)))
    }

    private fun isSelectedShip(radarEntity: RadarEntity): Boolean {
        val shipUuid = getShipUuid(radarEntity)
        return shipUuid != null && selectedShips.contains(shipUuid)
    }

    private fun getShipUuid(radarEntity: RadarEntity): UUID? {
        return radarEntity.ship?.uuid
    }

    private fun handleDiplomacyClick(entry: PlayerEntry, btn: Int) {
        if (entry.uuid == null || minecraft == null || minecraft!!.player == null) return

        val action = if (guiFunc == 3) {
            if (btn == 1) C2STeamDiplomacyPayload.ACTION_REMOVE_ALLY else C2STeamDiplomacyPayload.ACTION_ADD_ALLY
        } else {
            if (btn == 1) C2STeamDiplomacyPayload.ACTION_REMOVE_BANNED else C2STeamDiplomacyPayload.ACTION_ADD_BANNED
        }
        entry.relation = if (btn == 1) 0 else if (guiFunc == 3) 1 else 2
        PacketDistributor.sendToServer(C2STeamDiplomacyPayload(action, entry.uuid))
    }

    private fun selectDiplomacyEntry(selected: PlayerEntry) {
        for (entry in diplomacyPlayers) {
            entry.selected = entry === selected
        }
    }

    private fun handleDiplomacyButtonClick(mx: Int, my: Int): Boolean {
        if (mx < DIPLOMACY_BUTTON_LEFT || mx > DIPLOMACY_BUTTON_RIGHT) return false
        if (my in DIPLOMACY_BUTTON_TOP_Y..(DIPLOMACY_BUTTON_TOP_Y + 10)) {
            val selected = selectedDiplomacyEntry
            if (selected != null) {
                handleDiplomacyClick(selected, if (selected.relation == (if (guiFunc == 3) 1 else 2)) 1 else 0)
            }
            return true
        }
        if (my in DIPLOMACY_BUTTON_BOTTOM_Y..(DIPLOMACY_BUTTON_BOTTOM_Y + 10)) {
            setDeskFunction(0)
            return true
        }
        return false
    }

    private val selectedDiplomacyEntry: PlayerEntry?
        get() {
            val startIndex = diplomacyScrollIndex
            val endIndex = minOf(diplomacyPlayers.size, startIndex + DIPLOMACY_VISIBLE_ROWS)
            var y = DIPLOMACY_LIST_Y
            for (index in startIndex until endIndex) {
                val entry = diplomacyPlayers[index]
                if (entry.selected) return entry
                y += DIPLOMACY_ROW_HEIGHT
            }
            return null
        }

    private class RadarEntity(val ship: Entity?) {
        var pixelx = 0.0
        var pixely = 0.0
        var pixelz = 0.0
        var posX = 0
        var posY = 0
        var posZ = 0
    }

    private class PlayerEntry {
        var uuid: UUID? = null
        var name = ""
        var relation = 0
        var selected = false
    }

    companion object {
        private const val PREFERRED_GUI_SCALE = 1.25f
        private const val MIN_GUI_SCALE = 1.0f
        private const val BASE_GUI_WIDTH = 256
        private const val BASE_GUI_HEIGHT = 192
        private const val DIPLOMACY_VISIBLE_ROWS = 5
        private const val DIPLOMACY_LIST_Y = 56
        private const val DIPLOMACY_ROW_HEIGHT = 24
        const val DIPLOMACY_ROW_BOX_HEIGHT = 20
        private const val DIPLOMACY_BUTTON_LEFT = 8
        private const val DIPLOMACY_BUTTON_RIGHT = 54
        private const val DIPLOMACY_BUTTON_TOP_Y = 158
        private const val DIPLOMACY_BUTTON_BOTTOM_Y = 172
        private const val RADAR_ZOOM_X1 = 9; private const val RADAR_ZOOM_X2 = 53
        private const val RADAR_ZOOM_Y1 = 160; private const val RADAR_ZOOM_Y2 = 168
        private const val RADAR_CLEAR_X1 = 9; private const val RADAR_CLEAR_X2 = 53
        private const val RADAR_CLEAR_Y1 = 172; private const val RADAR_CLEAR_Y2 = 182
        private const val RADAR_ACTION_X1 = 88; private const val RADAR_ACTION_X2 = 132
        private const val RADAR_ACTION_Y1 = 159; private const val RADAR_ACTION_Y2 = 169
    }
}
