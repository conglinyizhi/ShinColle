package org.trp.shincolle.client.screen

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import org.trp.shincolle.client.gui.component.IconButton
import org.trp.shincolle.client.gui.component.Sprites
import org.trp.shincolle.client.gui.component.TooltipBuilder
import org.trp.shincolle.entity.EntityDestroyerIkazuchi
import org.trp.shincolle.entity.EntityDestroyerInazuma
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.item.DebugInspectorItem
import org.trp.shincolle.menu.ShipContainerMenu
import org.trp.shincolle.Shincolle

class ShipInventoryScreen(menu: ShipContainerMenu, playerInventory: Inventory, title: Component) :
    AbstractContainerScreen<ShipContainerMenu>(menu, playerInventory, title) {

    private var activeDetailTab = DETAIL_TAB_BASIC
    private var activeSettingsTab = SETTINGS_TAB_1
    private val settingsTabButtons = mutableListOf<IconButton>()

    private var canMelee = false
    private var lightAttack = false
    private var heavyAttack = false
    private var lightAircraftAttack = false
    private var heavyAircraftAttack = false
    private var ringEffect = false
    private var followMinDistance = 0
    private var followMaxDistance = 0
    private var fleeHpPercent = 0
    private var passiveAttack = false
    private var onSight = false
    private var pvpMode = false
    private var antiAir = false
    private var antiSub = false
    private var timeKeeping = false
    private var pickItem = false
    private var autoPump = false
    private var rationMorale = 0
    private var appearance = false
    private var mount = false
    private var taskId = 0
    private var taskSideFlags = 0
    private var activeSlider = SLIDER_NONE
    private var sliderBarPos = 0
    private val pageButtons = mutableListOf<IconButton>()
    private val toggleButtons = mutableListOf<IconButton>()
    private val toggleParentTabs = mutableListOf<Int>()
    private val toggleVisibilitySuppliers = mutableListOf<() -> Boolean>()

    init {
        this.imageWidth = 256
        this.imageHeight = 214
        this.inventoryLabelX = 8
        this.inventoryLabelY = this.imageHeight - 94
    }

    override fun init() {
        super.init()

        for (page in 0 until 3) {
            val pageBtn = IconButton.builder(TEXTURE_BG)
                .pos(this.leftPos + 44, this.topPos + 18 + page * 36)
                .size(18, 36).uv(34, 192).hoverUv(52, 192)
                .activeState { page == 0 || menu.unlockedStoragePages >= page }
                .onPress {
                    Minecraft.getInstance().gameMode?.handleInventoryButtonClick(
                        menu.containerId, ShipContainerMenu.PAGE_BUTTON_0 + page
                    )
                }.build()
            if (page > 0) pageBtn.active = false
            this.addRenderableWidget(pageBtn)
            pageButtons.add(pageBtn)
        }

        addDetailTab(DETAIL_TAB_BASIC, 18)
        addDetailTab(DETAIL_TAB_STATUS, 54)
        addDetailTab(DETAIL_TAB_MISC, 90)

        for (tab in SETTINGS_TAB_1..SETTINGS_TAB_12) addSettingsTab(tab)

        addToggle(SETTINGS_TAB_1, TOGGLE_X, TOGGLE_ROW_1_Y, { this.canMelee }, ShipContainerMenu.TOGGLE_BUTTON_CAN_MELEE) { true }
        addToggle(SETTINGS_TAB_1, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP, { this.lightAttack }, ShipContainerMenu.TOGGLE_BUTTON_LIGHT_ATTACK) { menu.ship.isStateGuiBtn1 }
        addToggle(SETTINGS_TAB_1, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 2, { this.heavyAttack }, ShipContainerMenu.TOGGLE_BUTTON_HEAVY_ATTACK) { menu.ship.isStateGuiBtn2 }
        addToggle(SETTINGS_TAB_1, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 3, { this.lightAircraftAttack }, ShipContainerMenu.TOGGLE_BUTTON_LIGHT_AIRCRAFT) { menu.ship.isStateGuiBtn3 }
        addToggle(SETTINGS_TAB_1, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 4, { this.heavyAircraftAttack }, ShipContainerMenu.TOGGLE_BUTTON_HEAVY_AIRCRAFT) { menu.ship.isStateGuiBtn4 }
        addToggle(SETTINGS_TAB_1, TOGGLE_X, TOGGLE_ROW_2_Y + TOGGLE_ROW_STEP * 4, { this.ringEffect }, ShipContainerMenu.TOGGLE_BUTTON_RING_EFFECT) { true }

        addToggle(SETTINGS_TAB_3, TOGGLE_X, TOGGLE_ROW_1_Y, { this.passiveAttack }, ShipContainerMenu.TOGGLE_BUTTON_PASSIVE_ATTACK) { true }
        addToggle(SETTINGS_TAB_3, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP, { this.onSight }, ShipContainerMenu.TOGGLE_BUTTON_ON_SIGHT) { true }
        addToggle(SETTINGS_TAB_3, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 2, { this.pvpMode }, ShipContainerMenu.TOGGLE_BUTTON_PVP) { true }
        addToggle(SETTINGS_TAB_3, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 3, { this.antiAir }, ShipContainerMenu.TOGGLE_BUTTON_ANTI_AIR) { true }
        addToggle(SETTINGS_TAB_3, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 4, { this.antiSub }, ShipContainerMenu.TOGGLE_BUTTON_ANTI_SUB) { true }
        addToggle(SETTINGS_TAB_3, TOGGLE_X, TOGGLE_ROW_2_Y + TOGGLE_ROW_STEP * 4, { this.timeKeeping }, ShipContainerMenu.TOGGLE_BUTTON_TIMEKEEP) { true }

        addToggle(SETTINGS_TAB_4, TOGGLE_X, TOGGLE_ROW_1_Y, { this.pickItem }, ShipContainerMenu.TOGGLE_BUTTON_PICK_ITEM) { menu.ship.supportsItemPickup() }
        addToggle(SETTINGS_TAB_4, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP, { this.autoPump }, ShipContainerMenu.TOGGLE_BUTTON_AUTO_PUMP) { true }

        addToggle(SETTINGS_TAB_6, TOGGLE_X, TOGGLE_ROW_1_Y, { this.appearance }, ShipContainerMenu.TOGGLE_BUTTON_SHOW_HELD) { true }
        addToggle(SETTINGS_TAB_6, TOGGLE_X, TOGGLE_ROW_2_Y, { this.mount }, ShipContainerMenu.TOGGLE_BUTTON_MOUNT) { true }
    }

    private fun addDetailTab(tabId: Int, y: Int) {
        this.addRenderableWidget(IconButton.builder(TEXTURE_BG)
            .pos(this.leftPos + 135, this.topPos + y).size(6, 34).uv(-1, -1)
            .hoverUv(Sprites.SHIP_INV_PAGE_INDICATOR_U, Sprites.SHIP_INV_PAGE_INDICATOR_V)
            .activeState { this.activeDetailTab == tabId }.onPress { this.activeDetailTab = tabId }.build())
    }

    private fun addSettingsTab(tab: Int) {
        val curTab = (tab - 1) % 6
        val x = if (tab <= 6) 239 else 246
        val y = 131 + curTab * TOGGLE_ROW_STEP
        val btn = IconButton.builder(TEXTURE_BG)
            .pos(this.leftPos + x, this.topPos + y).size(6, TOGGLE_SIZE).uv(-1, -1).hoverUv(74, 214)
            .activeState { this.activeSettingsTab == tab }
            .onPress { this.activeSettingsTab = tab; this.activeSlider = SLIDER_NONE }.build()
        this.addRenderableWidget(btn); settingsTabButtons.add(btn)
    }

    private fun addToggle(parentTab: Int, x: Int, y: Int, state: () -> Boolean, buttonId: Int, visible: () -> Boolean) {
        val btn = IconButton.builder(TEXTURE_BG)
            .pos(this.leftPos + x, this.topPos + y).size(TOGGLE_SIZE, TOGGLE_SIZE).uv(-1, -1).hoverUv(-1, -1)
            .activeState(state).onPress { sendMenuButton(buttonId) }.build()
        btn.visible = false; btn.active = false
        this.addRenderableWidget(btn); this.toggleButtons.add(btn)
        this.toggleParentTabs.add(parentTab); this.toggleVisibilitySuppliers.add(visible)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        renderLegacyHoverTooltips(guiGraphics, mouseX, mouseY); this.renderTooltip(guiGraphics, mouseX, mouseY)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc()
        guiGraphics.blit(TEXTURE_BG, this.leftPos, this.topPos, 0f, 0f, this.imageWidth, this.imageHeight, 256, 256)
        drawLockedInventoryPageOverlays(guiGraphics); drawInventoryPageIndicator(guiGraphics)
        drawDetailTabIndicator(guiGraphics); drawSettingsTabIndicator(guiGraphics)
        drawToggleStateMarks(guiGraphics); drawShipAndNameIcons(guiGraphics)
        drawTaskIcons(guiGraphics); drawShipEntityModel(guiGraphics, mouseX, mouseY)
        RenderSystem.disableBlend()
    }

    override fun renderLabels(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        drawPlainText(guiGraphics, menu.ship.name.string, 8, 6, 0x000000); drawTopRightStatus(guiGraphics)

        if (activeDetailTab == DETAIL_TAB_BASIC) {
            drawLabel(guiGraphics, Component.translatable("gui.shincolle.kills").string, 75, 20)
            drawValueRight(guiGraphics, menu.shipKills.toString(), 135, 30, 0xFFFFFF)
            drawLabel(guiGraphics, Component.translatable("gui.shincolle.exp").string, 75, 41)
            drawValueRight(guiGraphics, menu.shipExp.toString(), 135, 51, 0xFFFFFF)
            drawLabel(guiGraphics, Component.translatable("gui.shincolle.ammolight").string, 75, 62)
            drawValueRightLegacy(guiGraphics, getResourceDisplay(menu.ammoLightSynced.toString()), 135, 72, getResourceDisplayColor())
            drawLabel(guiGraphics, Component.translatable("gui.shincolle.ammoheavy").string, 75, 83)
            drawValueRightLegacy(guiGraphics, getResourceDisplay(menu.ammoHeavySynced.toString()), 135, 93, getResourceDisplayColor())
            drawLabel(guiGraphics, Component.translatable("gui.shincolle.grudge").string, 75, 104)
            drawValueRightLegacy(guiGraphics, getResourceDisplay(menu.shipFuel.toString()), 135, 114, getResourceDisplayColor())
        } else if (activeDetailTab == DETAIL_TAB_STATUS) {
            drawLabel(guiGraphics, Component.translatable("gui.shincolle.firepower1").string, 75, 20)
            drawValueRight(guiGraphics, String.format("%.0f", menu.shipFirepower), 135, 30, getModernizationColor(menu.ship.getAttrBonus(1)))
            drawLabel(guiGraphics, Component.translatable("gui.shincolle.armor").string, 75, 41)
            drawValueRight(guiGraphics, String.format("%.1f%%", menu.shipArmor * 100f), 135, 51, getModernizationColor(menu.ship.getAttrBonus(2)))
            drawLabel(guiGraphics, Component.translatable("gui.shincolle.attackspeed").string, 75, 62)
            drawValueRight(guiGraphics, String.format("%.2f", menu.shipReloadSpeed), 135, 72, getModernizationColor(menu.ship.getAttrBonus(3)))
            drawLabel(guiGraphics, Component.translatable("gui.shincolle.movespeed").string, 75, 83)
            drawValueRight(guiGraphics, String.format("%.2f", menu.shipMoveSpeed), 135, 93, getModernizationColor(menu.ship.getAttrBonus(4)))
            drawLabel(guiGraphics, Component.translatable("gui.shincolle.range").string, 75, 104)
            drawValueRight(guiGraphics, String.format("%.1f", menu.shipRange), 135, 114, getModernizationColor(menu.ship.getAttrBonus(5)))
        } else {
            drawLabel(guiGraphics, Component.translatable("gui.shincolle.marriage").string, 75, 20)
            drawValueRight(guiGraphics, if (menu.isMarried) Component.translatable("gui.shincolle.married").string else Component.translatable("gui.shincolle.unmarried").string, 135, 30, 0xFFFF00)
            drawLabel(guiGraphics, Component.translatable("gui.shincolle.formation.formation").string, 75, 41)
            drawValueRight(guiGraphics, Component.translatable("gui.shincolle.formation.format0").string, 135, 51, 0xFFFFFF)
            if (menu.ship.supportsAircraftCombat()) {
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.airplanelight").string, 75, 83)
                drawValueRight(guiGraphics, menu.aircraftLight.toString(), 135, 93, 0xFFFF00)
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.airplaneheavy").string, 75, 104)
                drawValueRight(guiGraphics, menu.aircraftHeavy.toString(), 135, 114, 0xFFFF00)
            }
        }

        when (activeSettingsTab) {
            1 -> {
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.canmelee").string, 187, 133)
                if (menu.ship.isStateGuiBtn1) drawLabel(guiGraphics, Component.translatable("gui.shincolle.canlightattack").string, 187, 146)
                if (menu.ship.isStateGuiBtn2) drawLabel(guiGraphics, Component.translatable("gui.shincolle.canheavyattack").string, 187, 159)
                if (menu.ship.isStateGuiBtn3) drawLabel(guiGraphics, Component.translatable("gui.shincolle.canairlightattack").string, 187, 172)
                if (menu.ship.isStateGuiBtn4) drawLabel(guiGraphics, Component.translatable("gui.shincolle.canairheavyattack").string, 187, 185)
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.auraeffect").string, 187, 198)
            }
            2 -> {
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.followmin").string, 174, 134)
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.followmax").string, 174, 158)
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.fleehp").string, 174, 182)
                drawValueLeft(guiGraphics, getFollowMinDisplayValue().toString(), 174, 145, 0xFFFFFF)
                drawValueLeft(guiGraphics, getFollowMaxDisplayValue().toString(), 174, 169, 0xFFFFFF)
                drawValueLeft(guiGraphics, getFleeHpDisplayValue().toString(), 174, 193, 0xFFFFFF)
            }
            3 -> {
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.targetAI").string, 187, 133)
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.onsightAI").string, 187, 146)
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.ai.pvp").string, 187, 159)
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.ai.aa").string, 187, 172)
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.ai.asm").string, 187, 185)
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.ai.timekeeper").string, 187, 198)
            }
            4 -> {
                if (menu.ship.supportsItemPickup()) drawLabel(guiGraphics, Component.translatable("gui.shincolle.ai.pickitem").string, 187, 133)
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.autopump").string, 187, 146)
            }
            5 -> {
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.ai.wpstay").string, 174, 134)
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.autocombatration").string, 174, 158)
                drawValueLeft(guiGraphics, getWpStayDisplay(), 174, 145, 0xFFFFFF)
                drawValueLeft(guiGraphics, getRationMoraleDisplay(), 174, 169, 0xFFFFFF)
            }
            6 -> {
                drawCenteredLabel(guiGraphics, Component.translatable("gui.shincolle.showhelditem").string, 212, 133)
                drawCenteredLabel(guiGraphics, Component.translatable("gui.shincolle.equip.mount").string, 212, 146)
                drawAppearanceLabels(guiGraphics)
            }
            7 -> {
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.crane.usemeta").string, 187, 159)
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.crane.useoredict").string, 187, 172)
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.crane.usenbt").string, 187, 185)
            }
            8 -> {
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.ai.inputside").string, 177, 133)
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.ai.outputside").string, 177, 159)
                drawLabel(guiGraphics, Component.translatable("gui.shincolle.ai.fuelside").string, 177, 185)
            }
        }
    }

    override fun containerTick() {
        super.containerTick(); syncStateFromMenu()
        val unlocked = menu.unlockedStoragePages
        for (i in pageButtons.indices) pageButtons[i].active = i == 0 || unlocked >= i
        for (i in toggleButtons.indices) {
            val matches = i < toggleParentTabs.size && toggleParentTabs[i] == this.activeSettingsTab
            val enabled = matches && i < toggleVisibilitySuppliers.size && toggleVisibilitySuppliers[i]()
            toggleButtons[i].visible = enabled; toggleButtons[i].active = enabled
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            val x = mouseX.toInt() - this.leftPos; val y = mouseY.toInt() - this.topPos
            if (tryStartSliderDrag(x, y)) return true
            if (handleTab7Click(x, y)) return true
            if (handleTab8Click(x, y)) return true
            if (handleAppearanceGridClick(x, y)) return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
        if (button == 0 && activeSlider != SLIDER_NONE) {
            sliderBarPos = Mth.clamp(mouseX.toInt() - this.leftPos - 191, 0, 42); return true
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 && activeSlider != SLIDER_NONE) {
            sendSliderValue(); activeSlider = SLIDER_NONE; return true
        }
        return super.mouseReleased(mouseX, mouseY, button)
    }

    private fun tryStartSliderDrag(x: Int, y: Int): Boolean {
        if (activeSettingsTab == 2) {
            if (inside(x, y, 187, 145, 237, 154)) return startSlider(SLIDER_FOLLOW_MIN, x)
            if (inside(x, y, 187, 169, 237, 178)) return startSlider(SLIDER_FOLLOW_MAX, x)
            if (inside(x, y, 187, 193, 237, 202)) return startSlider(SLIDER_FLEE_HP, x)
        }
        if (activeSettingsTab == 5) {
            if (inside(x, y, 187, 145, 237, 154)) return startSlider(SLIDER_WP_STAY, x)
            if (inside(x, y, 187, 169, 237, 178)) return startSlider(SLIDER_RATION_MORALE, x)
        }
        return false
    }

    private fun startSlider(slider: Int, x: Int): Boolean {
        activeSlider = slider; sliderBarPos = Mth.clamp(x - 191, 0, 42); return true
    }

    private fun handleTab7Click(x: Int, y: Int): Boolean {
        if (activeSettingsTab != SETTINGS_TAB_7) return false
        if (inside(x, y, 174, 136, 238, 152)) {
            sendMenuButton(ShipContainerMenu.ACTION_TASK_SELECT_BASE + (x - 174) / 16 + 1); return true
        }
        if (inside(x, y, 177, 157, 188, 168)) { sendMenuButton(ShipContainerMenu.ACTION_TASK_META_TOGGLE); return true }
        if (inside(x, y, 177, 170, 188, 181)) { sendMenuButton(ShipContainerMenu.ACTION_TASK_ORE_TOGGLE); return true }
        if (inside(x, y, 177, 183, 188, 194)) { sendMenuButton(ShipContainerMenu.ACTION_TASK_NBT_TOGGLE); return true }
        return false
    }

    private fun handleTab8Click(x: Int, y: Int): Boolean {
        if (activeSettingsTab != SETTINGS_TAB_8) return false
        for (i in 0 until 18) {
            val dx = i % 6 * 11; val dy = i / 6 * 26
            if (inside(x, y, 173 + dx, 144 + dy, 173 + dx + 10, 144 + dy + 10)) {
                sendMenuButton(ShipContainerMenu.ACTION_SIDE_TOGGLE_BASE + i); return true
            }
        }
        return false
    }

    private fun handleAppearanceGridClick(x: Int, y: Int): Boolean {
        if (activeSettingsTab != SETTINGS_TAB_6) return false
        val count = menu.equipOptions?.size ?: 0
        for (i in 0 until minOf(count, APPEARANCE_MAX_ITEMS)) {
            val col = i % APPEARANCE_COLS; val row = i / APPEARANCE_COLS
            val bx = APPEARANCE_GRID_X + col * APPEARANCE_GAP_X; val by = APPEARANCE_GRID_Y + row * APPEARANCE_GAP_Y
            if (inside(x, y, bx, by, bx + TOGGLE_SIZE, by + TOGGLE_SIZE)) { sendMenuButton(menu.getEquipOptionButtonId(i)); return true }
        }
        return false
    }

    private fun syncStateFromMenu() {
        this.canMelee = menu.isCanMeleeEnabled; this.lightAttack = menu.isLightAttackEnabled
        this.heavyAttack = menu.isHeavyAttackEnabled; this.lightAircraftAttack = menu.isLightAircraftAttackEnabled
        this.heavyAircraftAttack = menu.isHeavyAircraftAttackEnabled; this.ringEffect = menu.isRingEffectEnabled
        this.followMinDistance = menu.followMinDistance; this.followMaxDistance = menu.followMaxDistance
        this.fleeHpPercent = menu.fleeHpPercent; this.passiveAttack = menu.isPassiveAttackEnabled
        this.onSight = menu.isOnSightEnabled; this.pvpMode = menu.isPvpEnabled; this.antiAir = menu.isAntiAirEnabled
        this.antiSub = menu.isAntiSubEnabled; this.timeKeeping = menu.isTimeKeepingEnabled
        this.pickItem = menu.isPickItemEnabled; this.autoPump = menu.isAutoPumpEnabled
        this.rationMorale = menu.rationMoraleThreshold; this.appearance = menu.isAppearanceEnabled
        this.mount = menu.isMountEnabled; this.taskId = menu.taskId; this.taskSideFlags = menu.taskSideFlags
    }

    private fun sendMenuButton(id: Int) {
        Minecraft.getInstance().gameMode?.handleInventoryButtonClick(this.menu.containerId, id)
    }

    private fun drawInventoryPageIndicator(g: GuiGraphics) {
        val y = when (menu.getInventoryPage()) { 1 -> 54; 2 -> 90; else -> 18 }
        g.blit(TEXTURE_BG, this.leftPos + 62, this.topPos + y,
            Sprites.SHIP_INV_PAGE_INDICATOR_U.toFloat(), Sprites.SHIP_INV_PAGE_INDICATOR_V.toFloat(),
            Sprites.SHIP_INV_PAGE_INDICATOR_W, Sprites.SHIP_INV_PAGE_INDICATOR_H, 256, 256)
    }

    private fun drawDetailTabIndicator(g: GuiGraphics) {
        val y = when (activeDetailTab) { DETAIL_TAB_STATUS -> 54; DETAIL_TAB_MISC -> 90; else -> 18 }
        g.blit(TEXTURE_BG, this.leftPos + 135, this.topPos + y,
            Sprites.SHIP_INV_PAGE_INDICATOR_U.toFloat(), Sprites.SHIP_INV_PAGE_INDICATOR_V.toFloat(),
            Sprites.SHIP_INV_PAGE_INDICATOR_W, Sprites.SHIP_INV_PAGE_INDICATOR_H, 256, 256)
    }

    private fun drawLockedInventoryPageOverlays(g: GuiGraphics) {
        val unlocked = menu.unlockedStoragePages
        if (unlocked <= 0) { drawPageSlash(g, 54); drawPageSlash(g, 90) } else if (unlocked == 1) drawPageSlash(g, 90)
    }

    private fun drawPageSlash(g: GuiGraphics, y: Int) {
        g.blit(TEXTURE_BG, this.leftPos + 62, this.topPos + y,
            Sprites.SHIP_INV_PAGE_SLASH_U.toFloat(), Sprites.SHIP_INV_PAGE_SLASH_V.toFloat(),
            Sprites.SHIP_INV_PAGE_SLASH_W, Sprites.SHIP_INV_PAGE_SLASH_H, 256, 256)
    }

    private fun drawSettingsTabIndicator(g: GuiGraphics) {
        val tab = maxOf(SETTINGS_TAB_1, minOf(SETTINGS_TAB_12, activeSettingsTab))
        val curTab = (tab - 1) % 6; val y = 131 + curTab * TOGGLE_ROW_STEP; val x = if (tab <= 6) 239 else 246
        g.blit(TEXTURE_BG, this.leftPos + x, this.topPos + y,
            Sprites.SHIP_INV_TAB_INDICATOR_U.toFloat(), Sprites.SHIP_INV_TAB_INDICATOR_V.toFloat(),
            Sprites.SHIP_INV_TAB_INDICATOR_W, Sprites.SHIP_INV_TAB_INDICATOR_H, 256, 256)
    }

    private fun drawToggleStateMarks(g: GuiGraphics) {
        when (activeSettingsTab) { 1 -> drawAiPage1ToggleMarks(g); 2 -> drawFollowSliderTab(g)
            3 -> drawAiPage3ToggleMarks(g); 4 -> drawAiPage4ToggleMarks(g); 5 -> drawRationSliderTab(g)
            6 -> drawAppearanceToggleMarks(g); 7 -> drawAIPage7Background(g); 8 -> drawAIPage8Background(g) }
    }

    private fun drawOnOff(g: GuiGraphics, x: Int, y: Int, on: Boolean) {
        g.blit(TEXTURE_BG, this.leftPos + x, this.topPos + y,
            (if (on) Sprites.SHIP_INV_TOGGLE_ON_U else Sprites.SHIP_INV_TOGGLE_OFF_U).toFloat(),
            Sprites.SHIP_INV_TOGGLE_V.toFloat(), Sprites.SHIP_INV_TOGGLE_W, Sprites.SHIP_INV_TOGGLE_H, 256, 256)
    }

    private fun drawAiPage1ToggleMarks(g: GuiGraphics) {
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y, this.canMelee)
        if (menu.ship.isStateGuiBtn1) drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP, this.lightAttack)
        if (menu.ship.isStateGuiBtn2) drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 2, this.heavyAttack)
        if (menu.ship.isStateGuiBtn3) drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 3, this.lightAircraftAttack)
        if (menu.ship.isStateGuiBtn4) drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 4, this.heavyAircraftAttack)
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_2_Y + TOGGLE_ROW_STEP * 4, this.ringEffect)
    }

    private fun drawAiPage3ToggleMarks(g: GuiGraphics) {
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y, this.passiveAttack); drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP, this.onSight)
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 2, this.pvpMode); drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 3, this.antiAir)
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 4, this.antiSub); drawOnOff(g, TOGGLE_X, TOGGLE_ROW_2_Y + TOGGLE_ROW_STEP * 4, this.timeKeeping)
    }

    private fun drawAiPage4ToggleMarks(g: GuiGraphics) {
        if (menu.ship.supportsItemPickup()) drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y, this.pickItem)
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP, this.autoPump)
    }

    private fun drawFollowSliderTab(g: GuiGraphics) {
        val fMin = if (activeSlider == SLIDER_FOLLOW_MIN) sliderBarPos else ((maxOf(1, followMinDistance) - 1) / 30f * 42).toInt()
        val fMax = if (activeSlider == SLIDER_FOLLOW_MAX) sliderBarPos else ((maxOf(2, followMaxDistance) - 2) / 30f * 42).toInt()
        val fHp = if (activeSlider == SLIDER_FLEE_HP) sliderBarPos else (maxOf(0, minOf(100, fleeHpPercent)) / 100f * 42).toInt()
        drawSlider(g, 191, 148, fMin); drawSlider(g, 191, 172, fMax); drawSlider(g, 191, 196, fHp)
    }

    private fun drawRationSliderTab(g: GuiGraphics) {
        val wp = if (activeSlider == SLIDER_WP_STAY) sliderBarPos else (maxOf(0, menu.wpStaySetting) * 0.0625f * 42).toInt()
        val rat = if (activeSlider == SLIDER_RATION_MORALE) sliderBarPos else ((maxOf(1, minOf(4, rationMorale)) - 1) * 14f).toInt()
        drawSlider(g, 191, 148, Mth.clamp(wp, 0, 42)); drawSlider(g, 191, 172, rat)
    }

    private fun drawSlider(g: GuiGraphics, x: Int, y: Int, pos: Int) {
        val p = Mth.clamp(pos, 0, 42)
        g.blit(TEXTURE_BG, this.leftPos + x, this.topPos + y,
            Sprites.SHIP_INV_SLIDER_TRACK_U.toFloat(), Sprites.SHIP_INV_SLIDER_TRACK_V.toFloat(),
            Sprites.SHIP_INV_SLIDER_TRACK_W, Sprites.SHIP_INV_SLIDER_TRACK_H, 256, 256)
        g.blit(TEXTURE_BG, this.leftPos + x - 4 + p, this.topPos + y - 3,
            Sprites.SHIP_INV_SLIDER_KNOB_U.toFloat(), Sprites.SHIP_INV_SLIDER_KNOB_V.toFloat(),
            Sprites.SHIP_INV_SLIDER_KNOB_W, Sprites.SHIP_INV_SLIDER_KNOB_H, 256, 256)
    }

    private fun drawAppearanceToggleMarks(g: GuiGraphics) {
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y, this.appearance); drawOnOff(g, TOGGLE_X, TOGGLE_ROW_2_Y, this.mount)
        val count = menu.equipOptionCount
        for (i in 0 until minOf(count, APPEARANCE_MAX_ITEMS)) {
            val col = i % APPEARANCE_COLS; val row = i / APPEARANCE_COLS
            drawOnOff(g, APPEARANCE_GRID_X + col * APPEARANCE_GAP_X, APPEARANCE_GRID_Y + row * APPEARANCE_GAP_Y, menu.isEquipOptionEnabled(i))
        }
    }

    private fun drawAIPage7Background(g: GuiGraphics) {
        val tside = this.taskSideFlags
        g.blit(TEXTURE_BG, this.leftPos + 174, this.topPos + 136,
            Sprites.SHIP_INV_TASK_BG_U.toFloat(), Sprites.SHIP_INV_TASK_BG_V.toFloat(),
            Sprites.SHIP_INV_TASK_BG_W, Sprites.SHIP_INV_TASK_BG_H, 256, 256)
        g.blit(TEXTURE_BG, this.leftPos + 174, this.topPos + 138,
            Sprites.SHIP_INV_TASK_BG2_U.toFloat(), Sprites.SHIP_INV_TASK_BG2_V.toFloat(),
            Sprites.SHIP_INV_TASK_BG2_W, Sprites.SHIP_INV_TASK_BG2_H, 256, 256)
        val taskType = this.taskId
        if (taskType in 1..4) g.blit(TEXTURE_BG, this.leftPos + 174 + (taskType - 1) * 16, this.topPos + 136,
            (87 + (taskType - 1) * 16).toFloat(), 230f, 16, 16, 256, 256)
        g.blit(TEXTURE_BG, this.leftPos + 177, this.topPos + 157, 0f, if (tside and (1 shl 18) != 0) 236f else 225f, 11, 11, 256, 256)
        g.blit(TEXTURE_BG, this.leftPos + 177, this.topPos + 170, 11f, if (tside and (1 shl 19) != 0) 236f else 225f, 11, 11, 256, 256)
        g.blit(TEXTURE_BG, this.leftPos + 177, this.topPos + 183, 22f, if (tside and (1 shl 20) != 0) 236f else 225f, 11, 11, 256, 256)
    }

    private fun drawAIPage8Background(g: GuiGraphics) {
        val tside = this.taskSideFlags
        for (y in intArrayOf(144, 170, 196)) g.blit(TEXTURE_BG, this.leftPos + 173, this.topPos + y,
            Sprites.SHIP_INV_SIDE_ROW_BG_U.toFloat(), Sprites.SHIP_INV_SIDE_ROW_BG_V.toFloat(),
            Sprites.SHIP_INV_SIDE_ROW_BG_W, Sprites.SHIP_INV_SIDE_ROW_BG_H, 256, 256)
        for (i in 0 until 18) if (tside and (1 shl i) != 0) {
            val dx = i % 6 * 11; val dy = i / 6 * 26
            g.blit(TEXTURE_BG, this.leftPos + 173 + dx, this.topPos + 144 + dy, (151 + dx).toFloat(), 225f, 11, 11, 256, 256)
        }
    }

    private fun drawTopRightStatus(g: GuiGraphics) {
        val lvLabel = Component.translatable("gui.shincolle.level").string; val hpLabel = Component.translatable("gui.shincolle.hp").string
        val lv = menu.shipLevel; val hpCur = Mth.floor(menu.shipHealth); val hpMax = Mth.floor(menu.shipMaxHealth)
        val hpColor = getModernizationColor(menu.ship.getAttrBonus(0)); val lvColor = if (lv < 150) 0xFFFFFF else 0xFFD700
        val hpCurColor = if (hpCur < hpMax) getDarkerColor(hpColor, 0.8f) else hpColor
        g.drawString(this.font, lvLabel, 231 - this.font.width(lvLabel), 6, 0x00FFFF, true)
        g.drawString(this.font, hpLabel, 145 - this.font.width(hpLabel), 6, 0x00FFFF, true)
        g.drawString(this.font, lv.toString(), this.imageWidth - 6 - this.font.width(lv.toString()), 6, lvColor, true)
        g.drawString(this.font, hpCur.toString(), 147, 6, hpCurColor, true)
        g.drawString(this.font, "/$hpMax", 148 + this.font.width(hpCur.toString()), 6, hpColor, true)
    }

    override fun renderTooltip(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        super.renderTooltip(guiGraphics, mouseX, mouseY)
    }

    private fun renderLegacyHoverTooltips(g: GuiGraphics, mx: Int, my: Int) {
        if (isHovering(mx, my, 239, 18, 11, 11)) { renderMoraleTooltip(g, mx, my); return }
        if (isHovering(mx, my, 145, 4, 57, 11)) { renderModernizationHpTooltip(g, mx, my); return }
        if (activeSettingsTab == SETTINGS_TAB_7) { renderAIPage7Tooltips(g, mx, my); return }
        if (activeSettingsTab == SETTINGS_TAB_8) { renderAIPage8Tooltips(g, mx, my); return }
        renderFixedToggleTooltips(g, mx, my)
    }

    private fun renderAIPage7Tooltips(g: GuiGraphics, mx: Int, my: Int) {
        val x = mx - this.leftPos; val y = my - this.topPos
        if (inside(x, y, 174, 136, 238, 152)) {
            val idx = (x - 174) / 16 + 1
            val key = when (idx) { 1 -> "gui.shincolle.ai.cooking"; 2 -> "gui.shincolle.ai.fishing"; 3 -> "gui.shincolle.ai.mining"; 4 -> "gui.shincolle.ai.crafting"; else -> null }
            if (key != null) TooltipBuilder.of(key).renderIfNotEmpty(g, this.font, mx, my)
        } else if (inside(x, y, 177, 157, 188, 168)) TooltipBuilder.of("gui.shincolle.crane.usemeta").renderIfNotEmpty(g, this.font, mx, my)
        else if (inside(x, y, 177, 170, 188, 181)) TooltipBuilder.of("gui.shincolle.crane.useoredict").renderIfNotEmpty(g, this.font, mx, my)
        else if (inside(x, y, 177, 183, 188, 194)) TooltipBuilder.of("gui.shincolle.crane.usenbt").renderIfNotEmpty(g, this.font, mx, my)
    }

    private fun renderAIPage8Tooltips(g: GuiGraphics, mx: Int, my: Int) {
        val x = mx - this.leftPos; val y = my - this.topPos
        if (inside(x, y, 173, 144, 238, 155)) TooltipBuilder.of("gui.shincolle.ai.inputside").renderIfNotEmpty(g, this.font, mx, my)
        else if (inside(x, y, 173, 170, 238, 181)) TooltipBuilder.of("gui.shincolle.ai.outputside").renderIfNotEmpty(g, this.font, mx, my)
        else if (inside(x, y, 173, 196, 238, 207)) TooltipBuilder.of("gui.shincolle.ai.fuelside").renderIfNotEmpty(g, this.font, mx, my)
    }

    private fun renderFixedToggleTooltips(g: GuiGraphics, mx: Int, my: Int) {
        if (activeSettingsTab == SETTINGS_TAB_6) {
            if (isHovering(mx, my, TOGGLE_X, TOGGLE_ROW_1_Y, TOGGLE_SIZE, TOGGLE_SIZE)) TooltipBuilder.of("gui.shincolle.showhelditem").renderIfNotEmpty(g, this.font, mx, my)
            else if (isHovering(mx, my, TOGGLE_X, TOGGLE_ROW_2_Y, TOGGLE_SIZE, TOGGLE_SIZE)) TooltipBuilder.of("gui.shincolle.equip.mount").renderIfNotEmpty(g, this.font, mx, my)
            renderEquipOptionTooltips(g, mx, my)
        }
    }

    private fun renderEquipOptionTooltips(g: GuiGraphics, mx: Int, my: Int) {
        val x = mx - this.leftPos; val y = my - this.topPos; val count = menu.equipOptionCount
        for (i in 0 until minOf(count, APPEARANCE_MAX_ITEMS)) {
            val col = i % APPEARANCE_COLS; val row = i / APPEARANCE_COLS
            val bx = APPEARANCE_GRID_X + col * APPEARANCE_GAP_X; val by = APPEARANCE_GRID_Y + row * APPEARANCE_GAP_Y
            if (inside(x, y, bx, by, bx + TOGGLE_SIZE, by + TOGGLE_SIZE)) { g.renderComponentTooltip(this.font, listOf(menu.getEquipOptionLabel(i)), mx, my); return }
        }
    }

    private fun renderMoraleTooltip(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        val ship = menu.ship; val b = getLegacyMoraleBuffs(ship.morale)
        val tip = TooltipBuilder.create().addColored("gui.shincolle.morale${getMoraleLevel(ship.morale)}", ChatFormatting.GOLD)
        addMoraleStat(tip, ChatFormatting.RED, Component.translatable("gui.shincolle.firepower1").string, "x %.0f %% / %.0f %%", b[1] * 100, b[2] * 100)
        addMoraleStat(tip, ChatFormatting.RED, Component.translatable("gui.shincolle.firepower2").string, "x %.0f %% / %.0f %%", b[3] * 100, b[4] * 100)
        addMoraleStat(tip, ChatFormatting.WHITE, Component.translatable("gui.shincolle.attackspeed").string, "x %.0f %%", b[6] * 100)
        addMoraleStat(tip, ChatFormatting.LIGHT_PURPLE, Component.translatable("gui.shincolle.range").string, "+ %.1f", b[8])
        addMoraleStat(tip, ChatFormatting.AQUA, Component.translatable("gui.shincolle.critical").string, "x %.0f %%", b[9] * 100)
        addMoraleStat(tip, ChatFormatting.YELLOW, Component.translatable("gui.shincolle.doublehit").string, "x %.0f %%", b[10] * 100)
        addMoraleStat(tip, ChatFormatting.GOLD, Component.translatable("gui.shincolle.triplehit").string, "x %.0f %%", b[11] * 100)
        addMoraleStat(tip, ChatFormatting.RED, Component.translatable("gui.shincolle.missreduce").string, "x %.0f %%", b[12] * 100)
        addMoraleStat(tip, ChatFormatting.YELLOW, Component.translatable("gui.shincolle.antiair").string, "x %.0f %%", b[13] * 100)
        addMoraleStat(tip, ChatFormatting.AQUA, Component.translatable("gui.shincolle.antiss").string, "x %.0f %%", b[14] * 100)
        addMoraleStat(tip, ChatFormatting.WHITE, Component.translatable("gui.shincolle.armor").string, "+ %.0f %%", b[5] * 100)
        addMoraleStat(tip, ChatFormatting.GOLD, Component.translatable("gui.shincolle.dodge").string, "+ %.0f %%", b[15] * 100)
        addMoraleStat(tip, ChatFormatting.GREEN, Component.translatable("gui.shincolle.equip.xp").string, "+ %.0f %%", b[16] * 100)
        addMoraleStat(tip, ChatFormatting.DARK_PURPLE, Component.translatable("gui.shincolle.equip.grudge").string, "+ %.0f %%", b[17] * 100)
        addMoraleStat(tip, ChatFormatting.DARK_AQUA, Component.translatable("gui.shincolle.equip.ammo").string, "+ %.0f %%", b[18] * 100)
        addMoraleStat(tip, ChatFormatting.DARK_GREEN, Component.translatable("gui.shincolle.equip.hpres").string, "+ %.0f %%", b[19] * 100)
        addMoraleStat(tip, ChatFormatting.DARK_RED, Component.translatable("gui.shincolle.equip.kb").string, "+ %.0f %%", b[20] * 100)
        addMoraleStat(tip, ChatFormatting.GRAY, Component.translatable("gui.shincolle.movespeed").string, "+ %.2f", b[7])
        tip.renderIfNotEmpty(guiGraphics, this.font, mouseX, mouseY)
    }

    private fun addMoraleStat(tip: TooltipBuilder, color: ChatFormatting, label: String, format: String, vararg args: Any) {
        tip.add(Component.literal("$label: ").withStyle(color).append(Component.literal(String.format(format, *args)).withStyle(ChatFormatting.WHITE)))
    }

    private fun renderModernizationHpTooltip(g: GuiGraphics, mx: Int, my: Int) {
        TooltipBuilder.of(Component.translatable("gui.shincolle.modernlevel").string + " " + menu.ship.getAttrBonus(0)).renderIfNotEmpty(g, this.font, mx, my)
    }

    private fun drawShipAndNameIcons(g: GuiGraphics) {
        val ship = menu.ship
        var shipType = ship.getStateMinor(EntityShipBase.STATE_MINOR_FACTION_ID)
        var shipClass = ship.getStateMinor(EntityShipBase.STATE_MINOR_SHIP_CLASS)
        if (isRaidenGattaiState(ship)) { shipType = 2; shipClass = 55 }
        val typeUv = SHIP_TYPE_ICON_MAP[shipType.toByte()] ?: DEFAULT_SHIP_TYPE_ICON
        val nameData = SHIP_NAME_ICON_MAP[shipClass] ?: DEFAULT_SHIP_NAME_ICON
        val rarity = ship.getStateMinor(EntityShipBase.STATE_MINOR_RARITY)
        val isGod = rarity > 99
        val frameW = if (isGod) 40 else 30; val frameH = if (isGod) 42 else 30
        g.blit(TEXTURE_ICON0, this.leftPos + 165, this.topPos + 18, 0f, if (isGod) 0f else 43f, frameW, frameH, 256, 256)
        g.blit(TEXTURE_ICON0, this.leftPos + 167, this.topPos + 22, typeUv[0].toFloat(), typeUv[1].toFloat(), Sprites.SHIP_INV_TYPE_ICON_W, Sprites.SHIP_INV_TYPE_ICON_H, 256, 256)
        g.blit(TEXTURE_ICON0, this.leftPos + 239, this.topPos + 18,
            (getMoraleLevel(ship.morale) * Sprites.SHIP_INV_MORALE_ICON_W).toFloat(), Sprites.SHIP_INV_MORALE_ICON_V.toFloat(),
            Sprites.SHIP_INV_MORALE_ICON_W, Sprites.SHIP_INV_MORALE_ICON_H, 256, 256)
        val tex = if (nameData[0] < 100) TEXTURE_ICON1 else TEXTURE_ICON2
        var offY = if (nameData[0] == 4 || nameData[0] == 6) -10 else 0
        if (nameData[0] >= 101) offY = 10
        g.blit(tex, this.leftPos + 176, this.topPos + 63 + offY, nameData[1].toFloat(), nameData[2].toFloat(), Sprites.SHIP_INV_NAME_ICON_W, Sprites.SHIP_INV_NAME_ICON_H, 256, 256)
    }

    private fun drawShipEntityModel(g: GuiGraphics, mx: Int, my: Int) {
        val ship = menu.ship; val mp = ship.modelPos!!
        val gattai = isRaidenGattaiState(ship)
        val mX = this.leftPos + 218 + Mth.floor(mp[0]); val mY = this.topPos + 100 + Mth.floor(mp[1])
        val sm = if (gattai) MODEL_SCALE_GATTAI_MULTIPLIER else 1f
        val sc = maxOf(16, Mth.floor(mp[3] * sm))
        if (gattai) renderEntityWithPassengers(g, mX, mY, sc, mx.toFloat(), my.toFloat(), ship)
        else renderEntityWithPassengers(g, mX, mY, sc, mx - (mX - 3).toFloat(), my - (mY - (this.topPos + 60)).toFloat(), ship)
    }
    private fun isRaidenGattaiState(ship: EntityShipBase): Boolean =
        (ship is EntityDestroyerIkazuchi || ship is EntityDestroyerInazuma) && ship.ridingState > 1

    private fun drawTaskIcons(g: GuiGraphics) {
        if (menu.getInventoryPage() != 0) return
        val (u, v) = when (this.taskId) { 1 -> 151 to 236; 2 -> 167 to 236; 3 -> 183 to 236; 4 -> 199 to 236; else -> return }
        g.blit(TEXTURE_BG, this.leftPos + 25, this.topPos + 107,
            Sprites.SHIP_INV_SLOT_OVERLAY_U.toFloat(), Sprites.SHIP_INV_SLOT_OVERLAY_V.toFloat(),
            Sprites.SHIP_INV_SLOT_OVERLAY_W, Sprites.SHIP_INV_SLOT_OVERLAY_H, 256, 256)
        g.blit(TEXTURE_BG, this.leftPos + 26, this.topPos + 109, u.toFloat(), v.toFloat(),
            Sprites.SHIP_INV_SLOT_OVERLAY_W, Sprites.SHIP_INV_SLOT_OVERLAY_H, 256, 256)
        if (taskId == 1 || taskId == 2 || taskId == 3) drawSlotOverlay(g, HELD_MAIN_COL, HELD_MAIN_ROW, Sprites.SHIP_INV_SLOT_OVERLAY_U, Sprites.SHIP_INV_SLOT_OVERLAY_V)
        if (taskId == 1) drawSlotOverlay(g, HELD_OFF_COL, HELD_OFF_ROW, Sprites.SHIP_INV_SLOT_OVERLAY_U, Sprites.SHIP_INV_SLOT_OVERLAY_V)
        if (taskId == 4) { drawSlotOverlay(g, HELD_MAIN_COL, HELD_MAIN_ROW, Sprites.SHIP_INV_SLOT_OVERLAY_U, Sprites.SHIP_INV_SLOT_OVERLAY_V); drawCraftingSlots(g) }
    }

    private fun drawSlotOverlay(g: GuiGraphics, col: Int, row: Int, u: Int, v: Int) {
        g.blit(TEXTURE_BG, this.leftPos + 8 + col * 18, this.topPos + 18 + row * 18, u.toFloat(), v.toFloat(),
            Sprites.SHIP_INV_SLOT_OVERLAY_W, Sprites.SHIP_INV_SLOT_OVERLAY_H, 256, 256)
    }

    private fun drawCraftingSlots(g: GuiGraphics) {
        for (i in 0 until 9) {
            val col = i % 3; val row = i / 3 + 2
            val occupied = !menu.ship.inventory!!.getStackInSlot(CRAFTING_WORK_START_SLOT + i).isEmpty
            drawSlotOverlay(g, col, row, if (occupied) Sprites.SHIP_INV_SLOT_OCCUPIED_U else Sprites.SHIP_INV_SLOT_OVERLAY_U, Sprites.SHIP_INV_SLOT_OVERLAY_V)
        }
    }

    private fun drawAppearanceLabels(g: GuiGraphics) {
        drawCenteredLabel(g, Component.translatable("gui.shincolle.appearance").string, 212, APPEARANCE_TITLE_Y)
    }

    private fun getFollowMinDisplayValue() = if (activeSlider == SLIDER_FOLLOW_MIN) (sliderBarPos / 42f * 30 + 1).toInt() else followMinDistance
    private fun getFollowMaxDisplayValue() = if (activeSlider == SLIDER_FOLLOW_MAX) (sliderBarPos / 42f * 30 + 2).toInt() else followMaxDistance
    private fun getFleeHpDisplayValue() = if (activeSlider == SLIDER_FLEE_HP) (sliderBarPos / 42f * 100).toInt() else fleeHpPercent

    private fun getWpStayDisplay(): String {
        val v = if (activeSlider == SLIDER_WP_STAY) maxOf(0, minOf(16, (sliderBarPos / (42f * 0.0625f)).toInt())) else maxOf(0, menu.wpStaySetting)
        return formatWpStay(v)
    }

    private fun getRationMoraleDisplay(): String {
        val t = if (activeSlider == SLIDER_RATION_MORALE) maxOf(1, minOf(4, sliderBarPos / 14 + 1)) else maxOf(1, minOf(4, rationMorale))
        return Component.translatable("gui.shincolle.morale$t").string
    }

    private fun formatWpStay(v: Int): String = when { v <= 0 -> "OFF"; v <= 5 -> "${v * 5}s"; v <= 10 -> "${v - 5}m"; else -> "${v - 10}h" }

    private fun sendSliderValue() {
        val v = when (activeSlider) {
            SLIDER_FOLLOW_MIN -> (sliderBarPos / 42f * 30 + 1).toInt()
            SLIDER_FOLLOW_MAX -> (sliderBarPos / 42f * 30 + 2).toInt()
            SLIDER_FLEE_HP -> (sliderBarPos / 42f * 100).toInt()
            SLIDER_WP_STAY -> maxOf(0, minOf(16, (sliderBarPos / (42f * 0.0625f)).toInt()))
            SLIDER_RATION_MORALE -> maxOf(1, minOf(4, sliderBarPos / 14 + 1))
            else -> -1
        }
        val base = when (activeSlider) {
            SLIDER_FOLLOW_MIN -> ShipContainerMenu.SLIDER_FOLLOW_MIN_BASE; SLIDER_FOLLOW_MAX -> ShipContainerMenu.SLIDER_FOLLOW_MAX_BASE
            SLIDER_FLEE_HP -> ShipContainerMenu.SLIDER_FLEE_HP_BASE; SLIDER_WP_STAY -> ShipContainerMenu.SLIDER_WP_STAY_BASE
            SLIDER_RATION_MORALE -> ShipContainerMenu.SLIDER_RATION_MORALE_BASE; else -> 0
        }
        if (v >= 0) sendMenuButton(base + v)
    }

    private fun getModernizationColor(level: Int): Int {
        val r = level / 3f - 0.5f
        if (r >= 0.5f) return 0xFF0000; if (r >= 0.0f) { val g = (255f * (1f - r * 2f)).toInt(); return 0xFF0000 + (g shl 8) }
        val s = r + 0.5f; val b = (255f * (1f - s * 2f)).toInt(); return 0xFFFF00 + b
    }

    private fun getDarkerColor(color: Int, dark: Float) = ((color shr 16 and 0xFF) * dark).toInt() shl 16 or (((color shr 8 and 0xFF) * dark).toInt() shl 8) or ((color and 0xFF) * dark).toInt()

    private fun drawLabel(g: GuiGraphics, text: String, x: Int, y: Int) = drawOutlinedText(g, text, x, y, LEGACY_LABEL_COLOR)
    private fun drawCenteredLabel(g: GuiGraphics, text: String, centerX: Int, y: Int) = drawOutlinedText(g, text, centerX - this.font.width(text) / 2, y, LEGACY_LABEL_COLOR)
    private fun drawPlainText(g: GuiGraphics, text: String, x: Int, y: Int, color: Int) = g.drawString(this.font, text, x, y, color, false)
    private fun drawValueLeft(g: GuiGraphics, text: String, x: Int, y: Int, color: Int) = drawOutlinedText(g, text, x, y, color)
    private fun drawValueRight(g: GuiGraphics, text: String, xRight: Int, y: Int, color: Int) = drawOutlinedText(g, text, xRight - this.font.width(text), y, color)

    private fun drawValueRightLegacy(g: GuiGraphics, text: String, xRight: Int, y: Int, color: Int) {
        val x = xRight - this.font.width(text)
        g.drawString(this.font, text, x + 1, y + 1, 0x301010, false)
        g.drawString(this.font, text, x, y, color, false)
    }

    private fun drawOutlinedText(g: GuiGraphics, text: String, x: Int, y: Int, color: Int) {
        g.drawString(this.font, text, x - 1, y, LEGACY_LABEL_OUTLINE_COLOR, false)
        g.drawString(this.font, text, x + 1, y, LEGACY_LABEL_OUTLINE_COLOR, false)
        g.drawString(this.font, text, x, y - 1, LEGACY_LABEL_OUTLINE_COLOR, false)
        g.drawString(this.font, text, x, y + 1, LEGACY_LABEL_OUTLINE_COLOR, false)
        g.drawString(this.font, text, x, y, color, false)
    }

    private fun getResourceDisplay(fallback: String) = if (menu.isCreativeDebuggerActive) DebugInspectorItem.creativeInfiniteLabel().string else fallback
    private fun getResourceDisplayColor() = if (menu.isCreativeDebuggerActive) 0xFFD700 else 0xFFFFFF
    private fun getMoraleLevel(morale: Int): Int = when { morale > 5100 -> 0; morale > 3900 -> 1; morale > 2100 -> 2; morale > 900 -> 3; else -> 4 }

    companion object {
        val TEXTURE_BG: ResourceLocation = Sprites.T_SHIP_INVENTORY
        private val TEXTURE_ICON0: ResourceLocation = Sprites.T_NAME_ICON0
        private val TEXTURE_ICON1: ResourceLocation = Sprites.T_NAME_ICON1
        private val TEXTURE_ICON2: ResourceLocation = Sprites.T_NAME_ICON2

        const val DETAIL_TAB_BASIC = 0; const val DETAIL_TAB_STATUS = 1; const val DETAIL_TAB_MISC = 2
        const val SETTINGS_TAB_1 = 1; const val SETTINGS_TAB_2 = 2; const val SETTINGS_TAB_3 = 3; const val SETTINGS_TAB_4 = 4
        const val SETTINGS_TAB_5 = 5; const val SETTINGS_TAB_6 = 6; const val SETTINGS_TAB_7 = 7; const val SETTINGS_TAB_8 = 8
        const val SETTINGS_TAB_9 = 9; const val SETTINGS_TAB_10 = 10; const val SETTINGS_TAB_11 = 11; const val SETTINGS_TAB_12 = 12
        const val TOGGLE_SIZE = 11; const val TOGGLE_ROW_STEP = 13; const val TOGGLE_X = 174; const val TOGGLE_ROW_1_Y = 131; const val TOGGLE_ROW_2_Y = 144
        const val APPEARANCE_COLS = 4; const val APPEARANCE_GAP_X = 16; const val APPEARANCE_GAP_Y = 13
        const val APPEARANCE_GRID_X = 173; const val APPEARANCE_GRID_Y = 171; const val APPEARANCE_TITLE_Y = 159; const val APPEARANCE_MAX_ITEMS = 8
        const val HELD_MAIN_COL = 0; const val HELD_MAIN_ROW = 3; const val HELD_OFF_COL = 1; const val HELD_OFF_ROW = 3; const val CRAFTING_WORK_START_SLOT = 18
        const val MODEL_BOX_HALF_WIDTH = 57; const val MODEL_BOX_TOP = 91; const val MODEL_BOX_BOTTOM = 60
        const val MODEL_BOX_HALF_WIDTH_GATTAI = 72; const val MODEL_BOX_TOP_GATTAI = 85; const val MODEL_BOX_BOTTOM_GATTAI = 15
        const val MODEL_SCALE_GATTAI_MULTIPLIER = 1.3f
        const val SLIDER_NONE = -1; const val SLIDER_FOLLOW_MIN = 0; const val SLIDER_FOLLOW_MAX = 1; const val SLIDER_FLEE_HP = 2; const val SLIDER_WP_STAY = 3; const val SLIDER_RATION_MORALE = 4
        const val LEGACY_LABEL_COLOR = 0x000000; const val LEGACY_LABEL_OUTLINE_COLOR = 0xFFFFFF
        private val LEGACY_MORALE_LEVEL_0 = floatArrayOf(1f, 1.1f, 1.1f, 1.1f, 1.1f, 1.2f, 1.2f, 0.2f, 1.5f, 1.5f, 1.5f, 1.5f, 0f, 1.2f, 1.2f, 1.2f, 2.5f, 1.5f, 1.5f, 1.5f, 1.5f)
        private val LEGACY_MORALE_LEVEL_1 = floatArrayOf(1f, 1.1f, 1.1f, 1.1f, 1.1f, 0.8f, 1.1f, 0.06f, 1f, 1.2f, 1.2f, 1.2f, 0.03f, 1.1f, 1.1f, 1.2f, 1.5f, 1.2f, 1.2f, 1.2f, 1.2f)
        private val LEGACY_MORALE_NEUTRAL = floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f, 1f, 0f, 1f, 0.5f, 0.5f, 0.5f, 0.3f, 1f, 1f, 1f, 1f, 1f, 1f, 0.9f, 0.8f)
        private val LEGACY_MORALE_LEVEL_3 = floatArrayOf(1f, 0.9f, 0.9f, 1f, 1f, 0.9f, 0.9f, 0f, 1f, 0.3f, 0.3f, 0.3f, 0.3f, 0.9f, 0.9f, 0.9f, 0.8f, 0.9f, 0.9f, 0.9f, 0.8f)
        private val LEGACY_MORALE_LEVEL_4 = floatArrayOf(1f, 0.6f, 0.6f, 0.8f, 0.8f, 0.8f, 0.8f, -0.3f, 1f, 0.1f, 0.1f, 0.1f, 0.5f, 0.8f, 0.8f, 0.6f, 0.8f, 0.8f, 0.8f, 0.8f, 0.8f)

        fun getLegacyMoraleBuffs(morale: Int): FloatArray = when {
            morale > 5100 -> LEGACY_MORALE_LEVEL_0; morale > 3900 -> LEGACY_MORALE_LEVEL_1; morale > 2100 -> LEGACY_MORALE_NEUTRAL; morale > 900 -> LEGACY_MORALE_LEVEL_3; else -> LEGACY_MORALE_LEVEL_4
        }

        fun renderEntityWithPassengers(g: GuiGraphics, x: Int, y: Int, scale: Int, mx: Float, my: Float, entity: LivingEntity) {
            val f = Math.atan(((x - mx) / 40.0f).toDouble()).toFloat()
            val f1 = Math.atan(((y - 50.0f - my) / 40.0f).toDouble()).toFloat()
            val pose = g.pose(); pose.pushPose(); pose.translate(x.toDouble(), y.toDouble(), 50.0)
            pose.scale(scale.toFloat(), scale.toFloat(), -scale.toFloat())
            val q1 = Quaternionf().rotateZ(Math.PI.toFloat()); val q2 = Quaternionf().rotateX(f1 * 20f * 0.017453292f); q1.mul(q2); pose.mulPose(q1)
            val byo = entity.yBodyRotO; val by = entity.yBodyRot; val yr = entity.yRot; val xr = entity.xRot; val yho = entity.yHeadRotO; val yh = entity.yHeadRot
            entity.yBodyRotO = 180f + f * 20f; entity.yBodyRot = 180f + f * 20f; entity.setYRot(180f + f * 40f); entity.yHeadRotO = entity.yRot; entity.yHeadRot = entity.yRot; entity.setXRot(-f1 * 20f)
            val d = Minecraft.getInstance().entityRenderDispatcher; q2.conjugate(); d.overrideCameraOrientation(q2); d.setRenderShadow(false)
            RenderSystem.runAsFancy {
                d.render(entity, 0.0, 0.0, 0.0, 0f, 1f, pose, g.bufferSource(), 15728880)
                for (pass in entity.passengers) {
                    var pbyo = 0f; var pby = 0f; var pyr = 0f; var pxr = 0f; var pyho = 0f; var pyh = 0f
                    if (pass is LivingEntity) {
                        pbyo = pass.yBodyRotO; pby = pass.yBodyRot; pyr = pass.yRot; pxr = pass.xRot; pyho = pass.yHeadRotO; pyh = pass.yHeadRot
                        pass.yBodyRotO = entity.yBodyRotO; pass.yBodyRot = entity.yBodyRot; pass.setYRot(entity.yRot); pass.yHeadRotO = entity.yHeadRotO; pass.yHeadRot = entity.yHeadRot; pass.setXRot(entity.xRot)
                    }
                    pose.pushPose(); val rp = entity.getPassengerRidingPosition(pass); val inv = 1.0 / scale
                    pose.translate((rp.x - entity.x) * inv, (rp.y - entity.y) * inv + 0.09, (rp.z - entity.z) * inv); pose.translate(0.0, 0.0, 0.2)
                    d.render(pass, 0.0, 0.0, 0.0, 0f, 1f, pose, g.bufferSource(), 15728880); pose.popPose()
                    if (pass is LivingEntity) { pass.yBodyRotO = pbyo; pass.yBodyRot = pby; pass.setYRot(pyr); pass.setXRot(pxr); pass.yHeadRotO = pyho; pass.yHeadRot = pyh }
                }
            }
            g.flush(); d.setRenderShadow(true)
            entity.yBodyRotO = byo; entity.yBodyRot = by; entity.setYRot(yr); entity.setXRot(xr); entity.yHeadRotO = yho; entity.yHeadRot = yh; pose.popPose()
        }

        private val SHIP_TYPE_ICON_MAP: Map<Byte, IntArray> = mapOf(
            (-1).toByte() to intArrayOf(41, 0), (1).toByte() to intArrayOf(41, 29), (2).toByte() to intArrayOf(41, 58),
            (3).toByte() to intArrayOf(41, 87), (4).toByte() to intArrayOf(70, 58), (5).toByte() to intArrayOf(70, 29),
            (6).toByte() to intArrayOf(70, 0), (7).toByte() to intArrayOf(12, 74), (8).toByte() to intArrayOf(99, 0),
            (9).toByte() to intArrayOf(99, 58), (10).toByte() to intArrayOf(70, 87)
        )
        private val DEFAULT_SHIP_TYPE_ICON = intArrayOf(0, 0)

        private val SHIP_NAME_ICON_MAP: Map<Int, IntArray> = run {
            val data = intArrayOf(0,1,0,0, 1,1,11,0, 2,1,22,0, 3,1,33,0, 4,1,44,0, 5,1,55,0, 6,1,66,0, 7,1,77,0,
                8,1,88,0, 9,1,99,0, 10,1,110,0, 11,1,121,0, 12,1,132,0, 13,1,143,0, 14,1,154,0, 15,1,165,0,
                16,1,176,0, 17,1,187,0, 18,1,198,0, 19,1,209,0, 64,1,220,0, 20,2,0,59, 21,2,11,59, 22,2,22,59,
                23,2,33,59, 24,2,187,59, 25,2,176,59, 26,2,66,59, 27,2,77,59, 28,2,88,59, 29,2,99,59, 30,2,110,59,
                31,2,121,59, 32,2,132,59, 33,2,154,59, 34,2,44,59, 35,2,165,59, 36,101,0,0, 37,101,11,0, 38,101,198,0,
                39,101,209,0, 40,2,143,59, 41,2,55,59, 43,2,209,59, 44,2,231,59, 45,2,198,59, 46,101,22,0, 47,101,33,0,
                48,101,44,0, 49,2,220,59, 50,2,242,59, 51,101,55,0, 52,101,66,0, 53,101,77,0, 54,101,88,0, 55,101,99,0,
                56,101,110,0, 57,101,121,0, 58,101,132,0, 59,101,143,0, 60,101,154,0, 61,101,165,0, 62,101,176,0,
                63,101,187,0, 65,3,0,118, 66,3,11,118, 67,3,22,118, 68,3,33,118, 69,3,44,118, 70,3,55,118, 71,3,66,118,
                72,3,77,118, 73,3,88,118, 74,3,99,118, 75,3,110,118, 76,3,121,118, 77,3,132,118, 78,3,143,118,
                79,3,154,118, 80,3,165,118, 81,3,176,118, 82,3,187,118, 83,4,0,177, 84,4,11,177)
            val map = mutableMapOf<Int, IntArray>(); var i = 0
            while (i < data.size) { map[data[i]] = intArrayOf(data[i + 1], data[i + 2], data[i + 3]); i += 4 }; map
        }
        private val DEFAULT_SHIP_NAME_ICON = intArrayOf(0, 0, 0)

        private fun inside(x: Int, y: Int, x1: Int, y1: Int, x2: Int, y2: Int) = x >= x1 && x < x2 && y >= y1 && y < y2
    }

    private fun isHovering(mx: Int, my: Int, x: Int, y: Int, w: Int, h: Int) =
        mx >= this.leftPos + x && mx < this.leftPos + x + w && my >= this.topPos + y && my < this.topPos + y + h
}
