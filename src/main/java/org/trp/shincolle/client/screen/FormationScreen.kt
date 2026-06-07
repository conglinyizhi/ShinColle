package org.trp.shincolle.client.screen

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.trp.shincolle.Shincolle
import org.trp.shincolle.attachment.AdmiralData
import org.trp.shincolle.client.gui.component.Sprites
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.menu.FormationMenu
import org.trp.shincolle.network.C2SFormationActionPayload
import org.trp.shincolle.utility.FormationHelper
import java.util.*

class FormationScreen(menu: FormationMenu, playerInventory: Inventory, title: Component) :
    AbstractContainerScreen<FormationMenu>(menu, playerInventory, title) {

    private var selectedSlot = 0
    private lateinit var nameBox: EditBox
    private var editingName = false
    private var tickWaitSync = 0
    private var tickGUI = 0
    private val spotPos = Array(2) { FloatArray(6) }
    private val spotPosTarget = Array(2) { IntArray(6) }
    private val buffBar = FloatArray(21)
    private val buffBarTarget = FloatArray(21)
    private val totalFP = FloatArray(6)
    private val unbuffedAttrs = FloatArray(21)
    private var teamNameStr = ""
    private var lastShipRowClickTime = 0L
    private var lastShipRowClicked = -1

    init {
        this.imageWidth = 256
        this.imageHeight = 192
    }

    override fun init() {
        super.init()
        nameBox = EditBox(this.font, this.leftPos + 100, this.topPos + 180, 150, 12, Component.empty())
        nameBox.setTextColor(0xFFFF55)
        nameBox.setEditable(false)
        nameBox.setBordered(true)
        nameBox.setMaxLength(250)
        nameBox.isVisible = false
        addRenderableWidget(nameBox)
        editingName = false; tickWaitSync = 0; tickGUI = 0
        for (i in 0 until 6) { spotPos[0][i] = 25f; spotPos[1][i] = 25f; spotPosTarget[0][i] = 25; spotPosTarget[1][i] = 25 }
        buffBar.fill(0f); buffBarTarget.fill(0f)
        sendAction(7, 0, 0, "", Optional.empty<UUID?>())
        updateData()
    }

    override fun containerTick() { super.containerTick() }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick)
        super.render(graphics, mouseX, mouseY, partialTick)
        tickGUI++; if (tickWaitSync > 0) tickWaitSync--
        updateAnimation(); if (tickGUI % 32 == 0) updateData()
        this.renderTooltip(graphics, mouseX, mouseY)
    }

    private fun updateAnimation() {
        for (i in 0 until 6) {
            if (spotPos[0][i] != spotPosTarget[0][i].toFloat()) spotPos[0][i] += Math.signum((spotPosTarget[0][i] - spotPos[0][i]).toDouble()).toFloat()
            if (spotPos[1][i] != spotPosTarget[1][i].toFloat()) spotPos[1][i] += Math.signum((spotPosTarget[1][i] - spotPos[1][i]).toDouble()).toFloat()
        }
        for (i in buffBar.indices) {
            if (kotlin.math.abs(buffBar[i] - buffBarTarget[i]) > 0.1f) buffBar[i] += (buffBarTarget[i] - buffBar[i]) * 0.1f
            else buffBar[i] = buffBarTarget[i]
        }
    }

    private fun updateData() {
        val data = menu.admiralData
        val currentTeam = data.getCurrentTeamID()
        val ships = getShipsForTeam(data, currentTeam)
        val selectedShip = ships[selectedSlot]
        if (selectedShip != null && selectedShip.legacyShipStats != null) {
            for (i in 0 until 21) unbuffedAttrs[i] = selectedShip!!.legacyShipStats!!.getRawAttr(i)
        } else unbuffedAttrs.fill(0f)
        totalFP.fill(0f)
        for (ship in ships) {
            if (ship == null || ship.legacyShipStats == null) continue
            totalFP[0] += ship.legacyShipStats!!.getBuffedAttr(1); totalFP[1] += ship.legacyShipStats!!.getBuffedAttr(2)
            totalFP[2] += ship.legacyShipStats!!.getBuffedAttr(3); totalFP[3] += ship.legacyShipStats!!.getBuffedAttr(4)
            totalFP[4] += ship.legacyShipStats!!.getBuffedAttr(13); totalFP[5] += ship.legacyShipStats!!.getBuffedAttr(14)
        }
        teamNameStr = data.getTeamName(currentTeam) ?: ""
    }

    override fun renderBg(graphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0f, 0f, this.imageWidth, this.imageHeight, 256, 256)
        val data = menu.admiralData; val currentTeam = data.getCurrentTeamID()
        if (selectedSlot in 0 until AdmiralData.SLOT_COUNT) {
            graphics.blit(TEXTURE, this.leftPos + 142, this.topPos + 5 + selectedSlot * 27,
                Sprites.FORMATION_SLOT_HIGHLIGHT_U.toFloat(), Sprites.FORMATION_SLOT_HIGHLIGHT_V.toFloat(),
                Sprites.FORMATION_SLOT_HIGHLIGHT_W, Sprites.FORMATION_SLOT_HIGHLIGHT_H, 256, 256)
        }
        graphics.blit(TEXTURE, this.leftPos + 18 + currentTeam * 12, this.topPos + 167, (111 + currentTeam * 9).toFloat(), 207f, 9, 11, 256, 256)
        val formation = data.getFormationID(currentTeam)
        graphics.blit(TEXTURE, this.leftPos + 18 + formation * 18, this.topPos + 149, (111 + formation * 15).toFloat(), 192f, 15, 15, 256, 256)
        val targets = FORMATION_POSITIONS.getOrDefault(formation, FORMATION_POSITIONS.getValue(0))
        for (i in 0 until 6) { spotPosTarget[0][i] = targets[0][i]; spotPosTarget[1][i] = targets[1][i] }
        for (i in 0 until AdmiralData.SLOT_COUNT) {
            val dotV = if (i == selectedSlot) 195 else 192
            graphics.blit(TEXTURE, this.leftPos + spotPos[0][i].toInt(), this.topPos + spotPos[1][i].toInt(), 0f, dotV.toFloat(), 3, 3, 256, 256)
        }
        drawFormationBuffBars(graphics, this.leftPos, this.topPos, formation, selectedSlot)
        drawMoraleIcons(graphics, this.leftPos, this.topPos, data, currentTeam)
    }

    override fun renderLabels(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        val data = menu.admiralData; val currentTeam = data.getCurrentTeamID()
        val teamName = data.getTeamName(currentTeam)
        if (teamName != null && !editingName) {
            graphics.drawString(this.font, teamName, 100, 182, 0xFFFF55, true)
            if (this::nameBox.isInitialized && nameBox.value != teamName) nameBox.setValue(teamName)
        }
        drawFormationText(graphics, data, currentTeam); drawShipListText(graphics, data, currentTeam)
        drawAttributeLabels(graphics); handleHoveringText(graphics, mouseX, mouseY, data, currentTeam)
        if (tickWaitSync > 0) graphics.drawString(this.font, String.format("%.1f", tickWaitSync * 0.05), 190, 171, 0xFFFF55, false)
        val radarLabel = Component.translatable("gui.shincolle.radar.tname")
        graphics.drawString(this.font, radarLabel, 70 - this.font.width(radarLabel) / 2, 182, 0xFFFF55, true)
    }

    private fun drawAttributeLabels(graphics: GuiGraphics) {
        graphics.pose().pushPose(); graphics.pose().scale(0.75f, 0.75f, 0.75f)
        val labels = arrayOf("${ChatFormatting.RED}${tr("gui.shincolle.firepower1")}", "${ChatFormatting.GREEN}${tr("gui.shincolle.torpedo")}",
            "${ChatFormatting.RED}${tr("gui.shincolle.airfirepower")}", "${ChatFormatting.GREEN}${tr("gui.shincolle.airtorpedo")}",
            "${ChatFormatting.WHITE}${tr("gui.shincolle.attackspeed")}", "${ChatFormatting.LIGHT_PURPLE}${tr("gui.shincolle.range")}",
            "${ChatFormatting.AQUA}${tr("gui.shincolle.critical")}", "${ChatFormatting.YELLOW}${tr("gui.shincolle.doublehit")}",
            "${ChatFormatting.GOLD}${tr("gui.shincolle.triplehit")}", "${ChatFormatting.RED}${tr("gui.shincolle.missreduce")}",
            "${ChatFormatting.YELLOW}${tr("gui.shincolle.antiair")}", "${ChatFormatting.AQUA}${tr("gui.shincolle.antiss")}",
            "${ChatFormatting.WHITE}${tr("gui.shincolle.armor")}", "${ChatFormatting.GOLD}${tr("gui.shincolle.dodge")}",
            "${ChatFormatting.DARK_PURPLE}${tr("gui.shincolle.equip.grudge")}", "${ChatFormatting.DARK_GREEN}${tr("gui.shincolle.equip.hpres")}",
            "${ChatFormatting.YELLOW}${tr("gui.shincolle.equip.kb")}", "${ChatFormatting.GRAY}${tr("gui.shincolle.movespeed")}")
        val xP = floatArrayOf(12f,12f,12f,12f,12f,12f,69f,69f,69f,69f,69f,69f,126f,126f,126f,126f,126f,126f)
        val yP = floatArrayOf(60f,80f,100f,120f,140f,160f,60f,80f,100f,120f,140f,160f,60f,80f,100f,120f,140f,160f)
        for (i in labels.indices) graphics.drawString(this.font, labels[i], xP[i].toInt(), yP[i].toInt(), 0xFFFFFF, false)
        graphics.pose().popPose()
    }

    private fun handleHoveringText(g: GuiGraphics, mx: Int, my: Int, data: AdmiralData, currentTeam: Int) {
        val x = mx - this.leftPos; val y = my - this.topPos
        val ships = getShipsForTeam(data, currentTeam)
        if (ships[selectedSlot] != null && x in 4..137 && y in 44..144) {
            val attrId = getHoveredAttributeId(x, y)
            if (attrId != -1) {
                val ship = ships[selectedSlot]!!
                val fv = FormationHelper.getFormationBuffs(data.getFormationID(currentTeam), selectedSlot)!![attrId]
                val rv = unbuffedAttrs[attrId]; val bv = ship!!.legacyShipStats!!.getBuffedAttr(attrId)
                val prefix = if ((fv > (if (attrId == 8 || attrId == 7) 0f else 1f) && attrId < 15) || (fv > 0f && attrId >= 15)) "+" else ""
                val display = when {
                    attrId == 8 || attrId == 7 -> "$prefix${"%.2f".format(fv)} : ${ChatFormatting.GRAY}${"%.2f".format(rv)}${ChatFormatting.WHITE} -> ${ChatFormatting.YELLOW}${"%.2f".format(bv)}"
                    attrId == 15 || attrId == 17 || attrId == 19 || attrId == 20 -> "$prefix${"%.0f%%".format(fv * 100)} : ${ChatFormatting.GRAY}${"%.1f%%".format(rv * 100)}${ChatFormatting.WHITE} -> ${ChatFormatting.YELLOW}${"%.1f%%".format(bv * 100)}"
                    attrId in 9..14 -> "$prefix${"%.0f%%".format((fv - 1f) * 100)} : ${ChatFormatting.GRAY}${"%.1f%%".format(rv * 100)}${ChatFormatting.WHITE} -> ${ChatFormatting.YELLOW}${"%.1f%%".format(bv * 100)}"
                    else -> "$prefix${"%.0f%%".format((fv - 1f) * 100)} : ${ChatFormatting.GRAY}${"%.1f".format(rv)}${ChatFormatting.WHITE} -> ${ChatFormatting.YELLOW}${"%.1f".format(bv)}"
                }
                g.renderTooltip(this.font, Component.literal(display), mx, my)
            }
        } else if (x in 46..137 && y in 4..42) {
            g.renderComponentTooltip(this.font, listOf(
                Component.translatable("gui.shincolle.formation.totalfirepower").withStyle(ChatFormatting.LIGHT_PURPLE),
                Component.translatable("gui.shincolle.firepower1").append(": ").append(Component.literal("%.1f".format(totalFP[0])).withStyle(ChatFormatting.RED)),
                Component.translatable("gui.shincolle.torpedo").append(": ").append(Component.literal("%.1f".format(totalFP[1])).withStyle(ChatFormatting.GREEN)),
                Component.translatable("gui.shincolle.airfirepower").append(": ").append(Component.literal("%.1f".format(totalFP[2])).withStyle(ChatFormatting.RED)),
                Component.translatable("gui.shincolle.airtorpedo").append(": ").append(Component.literal("%.1f".format(totalFP[3])).withStyle(ChatFormatting.GREEN)),
                Component.translatable("gui.shincolle.antiair").append(": ").append(Component.literal("%.1f".format(totalFP[4])).withStyle(ChatFormatting.YELLOW)),
                Component.translatable("gui.shincolle.antiss").append(": ").append(Component.literal("%.1f".format(totalFP[5])).withStyle(ChatFormatting.AQUA))
            ), mx, my)
        }
    }

    private fun getHoveredAttributeId(mx: Int, my: Int): Int {
        for (row in BAR_ROWS.indices) {
            if (my < BAR_ROWS[row] + 5) return when { mx < 51 -> byteArrayOf(1,2,3,4,6,8)[row].toInt(); mx < 94 -> byteArrayOf(9,10,11,12,13,14)[row].toInt(); else -> byteArrayOf(5,15,17,19,20,7)[row].toInt() }
        }
        return -1
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (editingName && this::nameBox.isInitialized && nameBox.mouseClicked(mouseX, mouseY, button)) return true
        val x = (mouseX - leftPos).toInt(); val y = (mouseY - topPos).toInt()
        val data = menu.admiralData; val ships = getShipsForTeam(data, data.getCurrentTeamID())
        if (y in 149..163) for (i in 0 until 6) if (x in (18 + i * 18) until (33 + i * 18)) { sendAction(1, i, 0, "", Optional.empty<UUID?>()); return true }
        if (y in 167..177) for (i in 0 until 9) if (x in (18 + i * 12) until (27 + i * 12)) {
            if (data.getCurrentTeamID() == i) sendAction(7, 0, 0, "", Optional.empty<UUID?>()) else sendAction(0, i, 0, "", Optional.empty<UUID?>()); return true
        }
        if (x in 142..249) for (i in 0 until 6) {
            val rowY = 5 + i * 27; if (y !in rowY..(rowY + 26)) continue
            if (button == 1) { if (data.getShipUUID(data.getCurrentTeamID(), i) != null) { sendAction(3, i, 0, "", Optional.empty<UUID?>()); tickWaitSync = 20 }; selectedSlot = i; return true }
            if (hasShiftDown()) { val su = findAssignableSelectedShip(data, data.getCurrentTeamID(), i); if (su.isPresent) { sendAction(5, i, 0, "", su); selectedSlot = i; tickWaitSync = 20 }; return true }
            val now = System.currentTimeMillis(); val dc = lastShipRowClicked == i && now - lastShipRowClickTime <= 250
            lastShipRowClicked = i; lastShipRowClickTime = now
            if (dc && ships[i] != null) { selectedSlot = i; sendAction(8, i, 0, "", Optional.empty<UUID?>()) } else selectedSlot = i
            return true
        }
        if (y in 170..179) {
            if (x in 159..188 && tickWaitSync == 0) { val t = (selectedSlot + 1) % 6; sendAction(6, selectedSlot, t, "", Optional.empty<UUID?>()); sendAction(7, 0, 0, "", Optional.empty<UUID?>()); selectedSlot = t; tickWaitSync = 40; return true }
            if (x in 203..232 && tickWaitSync == 0) { val t = (selectedSlot + 5) % 6; sendAction(6, selectedSlot, t, "", Optional.empty<UUID?>()); sendAction(7, 0, 0, "", Optional.empty<UUID?>()); selectedSlot = t; tickWaitSync = 40; return true }
        }
        if (x in 46..93 && y in 180..191) { toggleNameEdit(); return true }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (editingName && this::nameBox.isInitialized && nameBox.keyPressed(keyCode, scanCode, modifiers)) return true
        if (editingName && (keyCode == 257 || keyCode == 335)) { submitNameEdit(); return true }
        if (editingName && keyCode == 256) { cancelNameEdit(); return true }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun charTyped(codePoint: Char, modifiers: Int): Boolean {
        if (editingName && this::nameBox.isInitialized && nameBox.charTyped(codePoint, modifiers)) return true
        return super.charTyped(codePoint, modifiers)
    }
    private fun sendAction(action: Int, p1: Int, p2: Int, s: String, uuid: Optional<UUID>) {
        Minecraft.getInstance().connection?.send(ServerboundCustomPayloadPacket(C2SFormationActionPayload(action, p1, p2, s, uuid)))
    }

    private fun drawFormationText(g: GuiGraphics, data: AdmiralData, ct: Int) {
        val ft = Component.translatable("gui.shincolle.formation.format${data.getFormationID(ct)}")
        val pt = Component.translatable("gui.shincolle.formation.position").append(" ").append(Component.literal((selectedSlot + 1).toString()))
        g.pose().pushPose(); g.pose().scale(0.75f, 0.75f, 0.75f)
        g.drawString(this.font, ft, (115 - this.font.width(ft) / 2).toInt(), 18, 0xFFFFFF, false)
        g.drawString(this.font, pt, (115 - this.font.width(pt) / 2).toInt(), 30, 0xFFFFFF, false)
        g.pose().popPose()
    }

    private fun drawShipListText(g: GuiGraphics, data: AdmiralData, ct: Int) {
        val ships = getShipsForTeam(data, ct); g.pose().pushPose(); g.pose().scale(0.75f, 0.75f, 0.75f)
        for (i in 0 until AdmiralData.SLOT_COUNT) {
            val ty = 14 + i * 36; val ship = ships[i]
            if (ship != null) {
                val name = (ship!!.displayName ?: Component.literal("")).string
                g.drawString(this.font, name, 210, ty, 0xFFFFFF, false)
                g.drawString(this.font, "${ChatFormatting.AQUA}LV ${ChatFormatting.YELLOW}${ship.level}   ${ChatFormatting.GOLD}${ship.health.toInt()} / ${ChatFormatting.RED}${ship.maxHealth.toInt()}", 195, ty + 14, 0xFFFFFF, false)
            } else {
                val uuid = data.getShipUUID(ct, i)
                val ns: MutableComponent = Component.translatable("gui.shincolle.formation.nosignal").withStyle(ChatFormatting.DARK_RED, ChatFormatting.OBFUSCATED)
                g.drawString(this.font, ns.append(Component.literal(" UID: " + (uuid?.toString()?.substring(0, 8) ?: "-1")).withStyle { it.withColor(ChatFormatting.GRAY).withObfuscated(false) }), 195, ty, 0xFFFFFF, false)
            }
        }
        g.pose().popPose()
    }

    private fun drawMoraleIcons(g: GuiGraphics, left: Int, top: Int, data: AdmiralData, tid: Int) {
        for ((i, ship) in getShipsForTeam(data, tid).withIndex()) {
            if (ship == null) continue
            g.blit(NAME_ICON_TEXTURE, left + 145, top + 8 + i * 27, (getMoraleIconIndex(ship.morale) * 11).toFloat(), 240f, 11, 11, 256, 256)
        }
    }

    private fun getMoraleIconIndex(morale: Int) = when { morale > 5100 -> 0; morale > 3900 -> 1; morale > 2100 -> 2; morale > 900 -> 3; else -> 4 }

    private fun drawFormationBuffBars(g: GuiGraphics, left: Int, top: Int, fid: Int, sid: Int) {
        for (i in 0..2) for (j in 0..5) g.blit(TEXTURE, left + BAR_COLS[i], top + BAR_ROWS[j],
            Sprites.FORMATION_BAR_BG_U.toFloat(), Sprites.FORMATION_BAR_BG_V.toFloat(), Sprites.FORMATION_BAR_BG_W, Sprites.FORMATION_BAR_BG_H, 256, 256)
        val value = FormationHelper.getFormationBuffs(fid, sid)!!
        val attrIds = byteArrayOf(1,2,3,4,6,8,9,10,11,12,13,14,5,15,17,19,20,7)
        val colMap = intArrayOf(0,0,0,0,0,0,1,1,1,1,1,1,2,2,2,2,2,2)
        val rowMap = intArrayOf(0,1,2,3,4,5,0,1,2,3,4,5,0,1,2,3,4,5)
        for (idx in attrIds.indices) {
            val aid = attrIds[idx].toInt(); if (aid < 0 || aid >= value.size) continue
            var lm = 20f; var base = 1f
            if (aid == 7) { lm /= 0.5f; base = 0f } else if (aid == 8) { lm /= 10f; base = 0f } else if (aid in 15..20) base = 0f
            buffBarTarget[aid] = (value!![aid] - base) * lm
            val len = kotlin.math.abs(buffBar[aid]).toInt(); if (len <= 0) continue
            g.blit(TEXTURE, left + BAR_COLS[colMap[idx]] + (if (buffBar[aid] > 0) BAR_LENGTH else -len + BAR_LENGTH), top + BAR_ROWS[rowMap[idx]], 0f, (if (buffBar[aid] > 0) 230 else 225).toFloat(), len, 4, 256, 256)
        }
    }

    private fun getShipsForTeam(data: AdmiralData, tid: Int): Array<EntityShipBase?> {
        val ships = arrayOfNulls<EntityShipBase>(AdmiralData.SLOT_COUNT)
        for (i in 0 until AdmiralData.SLOT_COUNT) {
            val uuid = data.getShipUUID(tid, i) ?: continue
            if (minecraft?.level != null) for (e in minecraft!!.level!!.entitiesForRendering()) { if (e.uuid == uuid && e is EntityShipBase) { ships[i] = e; break } }
        }
        return ships
    }

    private fun findAssignableSelectedShip(data: AdmiralData, ct: Int, ts: Int): Optional<UUID> {
        if (minecraft?.player == null || minecraft!!.level == null) return Optional.empty<UUID?>()
        val cu = data.getShipUUID(ct, ts)
        val candidates = minecraft!!.level!!.getEntitiesOfClass(EntityShipBase::class.java, minecraft!!.player!!.boundingBox.inflate(64.0)) {
            it.isAlive && !it.isRemoved && !it.isInDeadPose && it.isPointerSelected && minecraft!!.player!!.uuid == it.ownerUUID
        }.sortedBy { it.distanceToSqr(minecraft!!.player!!) }
        for (ship in candidates) { val uuid = ship.uuid; if (uuid == cu) return Optional.of(uuid); if (!data.isShipInTeam(ct, uuid)) return Optional.of(uuid) }
        return Optional.empty<UUID?>()
    }

    private fun toggleNameEdit() {
        if (!editingName) { nameBox.setEditable(true); nameBox.isFocused = true; nameBox.isVisible = true; nameBox.setValue(menu.admiralData.getTeamName(menu.admiralData.getCurrentTeamID()) ?: ""); editingName = true }
        else submitNameEdit()
    }

    private fun submitNameEdit() { if (!editingName) return; sendAction(4, 0, 0, nameBox.value.trim(), Optional.empty<UUID?>()); cancelNameEdit() }
    private fun cancelNameEdit() { nameBox.setEditable(false); nameBox.isFocused = false; nameBox.isVisible = false; editingName = false }
    private fun tr(key: String) = Component.translatable(key).string

    companion object {
        private val TEXTURE: ResourceLocation = Sprites.T_FORMATION
        private val NAME_ICON_TEXTURE = ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/guinameicon0.png")
        private const val BAR_LENGTH = 20
        private val BAR_ROWS = intArrayOf(54, 69, 84, 99, 114, 129)
        private val BAR_COLS = intArrayOf(9, 52, 95)
        private val FORMATION_POSITIONS = mapOf(
            0 to arrayOf(intArrayOf(25,25,25,25,25,25), intArrayOf(25,25,25,25,25,25)),
            1 to arrayOf(intArrayOf(25,25,25,25,25,25), intArrayOf(9,15,21,27,33,39)),
            2 to arrayOf(intArrayOf(21,29,21,29,21,29), intArrayOf(25,25,16,16,34,34)),
            3 to arrayOf(intArrayOf(25,25,15,35,25,25), intArrayOf(29,15,26,26,36,23)),
            4 to arrayOf(intArrayOf(40,34,28,22,16,10), intArrayOf(9,15,21,27,33,39)),
            5 to arrayOf(intArrayOf(40,34,28,22,16,10), intArrayOf(25,25,25,25,25,25))
        )
    }
}
