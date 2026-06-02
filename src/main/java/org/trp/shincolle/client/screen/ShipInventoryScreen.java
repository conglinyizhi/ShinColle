package org.trp.shincolle.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.trp.shincolle.client.gui.component.IconButton;
import org.trp.shincolle.client.gui.component.Sprites;
import org.trp.shincolle.client.gui.component.TooltipBuilder;

import org.trp.shincolle.entity.EntityDestroyerIkazuchi;
import org.trp.shincolle.entity.EntityDestroyerInazuma;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.menu.ShipContainerMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * Ship inventory screen — the largest screen in the mod.
 * <p>
 * Phase 3a R1: tab selectors → {@link IconButton}, tooltips → {@link TooltipBuilder}.
 */
public class ShipInventoryScreen extends AbstractContainerScreen<ShipContainerMenu> {

    // Textures
    private static final ResourceLocation TEXTURE_BG = Sprites.T_SHIP_INVENTORY;
    private static final ResourceLocation TEXTURE_ICON0 = Sprites.T_NAME_ICON0;
    private static final ResourceLocation TEXTURE_ICON1 = Sprites.T_NAME_ICON1;
    private static final ResourceLocation TEXTURE_ICON2 = Sprites.T_NAME_ICON2;

    // Ship type/name icon maps
    private static final int[] DEFAULT_SHIP_TYPE_ICON = {41, 0};
    private static final int[] DEFAULT_SHIP_NAME_ICON = {1, 0, 0};
    private static final Map<Byte, int[]> SHIP_TYPE_ICON_MAP = createShipTypeIconMap();
    private static final Map<Integer, int[]> SHIP_NAME_ICON_MAP = createShipNameIconMap();

    // Detail tab constants
    private static final int DETAIL_TAB_BASIC = 1;
    private static final int DETAIL_TAB_STATUS = 2;
    private static final int DETAIL_TAB_MISC = 3;

    // Settings tab constants
    private static final int SETTINGS_TAB_1 = 1;
    private static final int SETTINGS_TAB_3 = 3;
    private static final int SETTINGS_TAB_4 = 4;
    private static final int SETTINGS_TAB_6 = 6;
    private static final int SETTINGS_TAB_7 = 7;
    private static final int SETTINGS_TAB_8 = 8;
    private static final int SETTINGS_TAB_12 = 12;

    // Layout constants
    private static final int APPEARANCE_MAX_ITEMS = 16;
    private static final int APPEARANCE_COLS = 4;
    private static final int TOGGLE_SIZE = 11;
    private static final int TOGGLE_X = 174;
    private static final int TOGGLE_ROW_1_Y = 131;
    private static final int TOGGLE_ROW_2_Y = 144;
    private static final int TOGGLE_ROW_STEP = 13;
    private static final int APPEARANCE_GRID_X = 176;
    private static final int APPEARANCE_GRID_Y = 157;
    private static final int APPEARANCE_GAP_X = 16;
    private static final int APPEARANCE_GAP_Y = 13;
    private static final int SLIDER_NONE = -1;
    private static final int SLIDER_FOLLOW_MIN = 0;
    private static final int SLIDER_FOLLOW_MAX = 1;
    private static final int SLIDER_FLEE_HP = 2;
    private static final int SLIDER_WP_STAY = 3;
    private static final int SLIDER_RATION_MORALE = 4;
    private static final int MODEL_BOX_HALF_WIDTH = 150;
    private static final int MODEL_BOX_TOP = 170;
    private static final int MODEL_BOX_BOTTOM = 110;
    private static final int MODEL_BOX_HALF_WIDTH_GATTAI = 175;
    private static final int MODEL_BOX_TOP_GATTAI = 195;
    private static final int MODEL_BOX_BOTTOM_GATTAI = 130;
    private static final float MODEL_SCALE_GATTAI_MULTIPLIER = 0.90F;
    private static final int HELD_MAIN_COL = 1, HELD_MAIN_ROW = 5;
    private static final int HELD_OFF_COL = 2, HELD_OFF_ROW = 5;
    private static final int CRAFTING_WORK_START_SLOT = 12;
    private static final int LEGACY_LABEL_COLOR = 0xFFFFFF;
    private static final int LEGACY_LABEL_SHADOW_COLOR = 0x301010;

    // Morale tables
    private static final float[] LEGACY_MORALE_NEUTRAL =
        {0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    private static final float[] LEGACY_MORALE_LEVEL_0 =
        {0.0F, 1.25F, 1.25F, 1.25F, 1.25F, 0.2F, 1.4F, 0.15F, 4.0F, 1.2F, 1.2F, 1.2F, 1.5F, 1.5F, 1.5F, 0.25F, 0.5F, 0.5F, 0.5F, 0.5F, 0.25F};
    private static final float[] LEGACY_MORALE_LEVEL_1 =
        {0.0F, 1.1F, 1.1F, 1.1F, 1.1F, 0.1F, 1.2F, 0.08F, 2.0F, 1.1F, 1.1F, 1.1F, 1.25F, 1.25F, 1.25F, 0.12F, 0.25F, 0.25F, 0.25F, 0.25F, 0.15F};
    private static final float[] LEGACY_MORALE_LEVEL_3 =
        {0.0F, 0.9F, 0.9F, 0.9F, 0.9F, -0.1F, 0.8F, -0.08F, -2.0F, 0.9F, 0.9F, 0.9F, 0.75F, 0.75F, 0.75F, -0.12F, -0.25F, -0.25F, -0.25F, -0.25F, -0.1F};
    private static final float[] LEGACY_MORALE_LEVEL_4 =
        {0.0F, 0.75F, 0.75F, 0.75F, 0.75F, -0.2F, 0.6F, -0.15F, -4.0F, 0.8F, 0.8F, 0.8F, 0.5F, 0.5F, 0.5F, -0.25F, -0.5F, -0.5F, -0.5F, -0.5F, -0.2F};

    // State
    private int activeDetailTab = DETAIL_TAB_BASIC;
    private int activeSettingsTab = SETTINGS_TAB_1;
    private int appearancePage = 0;
    private int activeSlider = SLIDER_NONE;
    private int sliderBarPos = 0;
    private boolean canMelee, lightAttack, heavyAttack, lightAircraftAttack, heavyAircraftAttack, ringEffect;
    private int followMinDistance, followMaxDistance, fleeHpPercent;
    private boolean passiveAttack, onSight, pvpMode, antiAir, antiSub, timeKeeping, pickItem, autoPump;
    private int rationMorale;
    private boolean appearance, mount;
    private final List<IconButton> pageButtons = new ArrayList<>();
    private int taskId, taskSideFlags;

    // Widget references for visibility management
    private final List<IconButton> settingsTabButtons = new ArrayList<>();
    private final List<IconButton> toggleButtons = new ArrayList<>();
    private final java.util.List<Integer> toggleParentTabs = new java.util.ArrayList<>();
    private final List<BooleanSupplier> toggleVisibilitySuppliers = new ArrayList<>();

    public ShipInventoryScreen(ShipContainerMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = 256;
        this.imageHeight = 214;
    }

    @Override
    protected void init() {
        super.init();
        syncStateFromMenu();

        // ---- Inventory page buttons (left column) — respect unlock state ----
        int[] pageYs = {18, 54, 90};
        for (int i = 0; i < 3; i++) {
            int page = i;
            IconButton pageBtn = IconButton.builder(TEXTURE_BG)
                    .pos(this.leftPos + 62, this.topPos + pageYs[i])
                    .size(6, 34)
                    .uv(-1, -1)
                    .hoverUv(Sprites.SHIP_INV_PAGE_INDICATOR_U, Sprites.SHIP_INV_PAGE_INDICATOR_V)
                    .activeState(() -> this.menu.getInventoryPage() == page)
                    .onPress(() -> {
                        if (page == 0 || this.menu.getUnlockedStoragePages() >= page) {
                            sendMenuButton(ShipContainerMenu.PAGE_BUTTON_0 + page);
                        }
                    })
                    .build();
            if (page > 0) pageBtn.active = false; // initially locked until sync
            this.addRenderableWidget(pageBtn);
            pageButtons.add(pageBtn);
        }

        // ---- Detail tab buttons (thin indicator bars, same visual style as original) ----
        addDetailTab(DETAIL_TAB_BASIC, 18);
        addDetailTab(DETAIL_TAB_STATUS, 54);
        addDetailTab(DETAIL_TAB_MISC, 90);
        // ---- Settings tab buttons (right edge) ----
        // Column 1 (tabs 1-6): x=239; Column 2 (tabs 7-12): x=246
        for (int tab = SETTINGS_TAB_1; tab <= SETTINGS_TAB_12; tab++) {
            addSettingsTab(tab);
        }

        // ---- Toggle buttons ----
        addToggle(SETTINGS_TAB_1, TOGGLE_X, TOGGLE_ROW_1_Y, () -> this.canMelee,
                ShipContainerMenu.TOGGLE_BUTTON_CAN_MELEE, () -> true);
        addToggle(SETTINGS_TAB_1, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP, () -> this.lightAttack,
                ShipContainerMenu.TOGGLE_BUTTON_LIGHT_ATTACK, () -> this.menu.getShip().isStateGuiBtn1());
        addToggle(SETTINGS_TAB_1, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 2, () -> this.heavyAttack,
                ShipContainerMenu.TOGGLE_BUTTON_HEAVY_ATTACK, () -> this.menu.getShip().isStateGuiBtn2());
        addToggle(SETTINGS_TAB_1, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 3, () -> this.lightAircraftAttack,
                ShipContainerMenu.TOGGLE_BUTTON_LIGHT_AIRCRAFT, () -> this.menu.getShip().isStateGuiBtn3());
        addToggle(SETTINGS_TAB_1, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 4, () -> this.heavyAircraftAttack,
                ShipContainerMenu.TOGGLE_BUTTON_HEAVY_AIRCRAFT, () -> this.menu.getShip().isStateGuiBtn4());
        addToggle(SETTINGS_TAB_1, TOGGLE_X, TOGGLE_ROW_2_Y + TOGGLE_ROW_STEP * 4, () -> this.ringEffect,
                ShipContainerMenu.TOGGLE_BUTTON_RING_EFFECT, () -> true);

        addToggle(SETTINGS_TAB_3, TOGGLE_X, TOGGLE_ROW_1_Y, () -> this.passiveAttack,
                ShipContainerMenu.TOGGLE_BUTTON_PASSIVE_ATTACK, () -> true);
        addToggle(SETTINGS_TAB_3, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP, () -> this.onSight,
                ShipContainerMenu.TOGGLE_BUTTON_ON_SIGHT, () -> true);
        addToggle(SETTINGS_TAB_3, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 2, () -> this.pvpMode,
                ShipContainerMenu.TOGGLE_BUTTON_PVP, () -> true);
        addToggle(SETTINGS_TAB_3, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 3, () -> this.antiAir,
                ShipContainerMenu.TOGGLE_BUTTON_ANTI_AIR, () -> true);
        addToggle(SETTINGS_TAB_3, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 4, () -> this.antiSub,
                ShipContainerMenu.TOGGLE_BUTTON_ANTI_SUB, () -> true);
        addToggle(SETTINGS_TAB_3, TOGGLE_X, TOGGLE_ROW_2_Y + TOGGLE_ROW_STEP * 4, () -> this.timeKeeping,
                ShipContainerMenu.TOGGLE_BUTTON_TIMEKEEP, () -> true);

        addToggle(SETTINGS_TAB_4, TOGGLE_X, TOGGLE_ROW_1_Y, () -> this.pickItem,
                ShipContainerMenu.TOGGLE_BUTTON_PICK_ITEM, () -> this.menu.getShip().supportsItemPickup());
        addToggle(SETTINGS_TAB_4, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP, () -> this.autoPump,
                ShipContainerMenu.TOGGLE_BUTTON_AUTO_PUMP, () -> true);

        addToggle(SETTINGS_TAB_6, TOGGLE_X, TOGGLE_ROW_1_Y, () -> this.appearance,
                ShipContainerMenu.TOGGLE_BUTTON_SHOW_HELD, () -> true);
        addToggle(SETTINGS_TAB_6, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP, () -> this.mount,
                ShipContainerMenu.TOGGLE_BUTTON_MOUNT, () -> true);
    }

    private void addDetailTab(int tabId, int y) {
        this.addRenderableWidget(IconButton.builder(TEXTURE_BG)
                .pos(this.leftPos + 135, this.topPos + y)
                .size(6, 34)
                .uv(-1, -1)
                .hoverUv(Sprites.SHIP_INV_PAGE_INDICATOR_U, Sprites.SHIP_INV_PAGE_INDICATOR_V)
                .activeState(() -> this.activeDetailTab == tabId)
                .onPress(() -> this.activeDetailTab = tabId)
                .build());
    }


    private void addSettingsTab(int tab) {
        int curTab = (tab - 1) % 6;
        int x = tab <= 6 ? 239 : 246;
        int y = 131 + curTab * TOGGLE_ROW_STEP;
        IconButton btn = IconButton.builder(TEXTURE_BG)
                .pos(this.leftPos + x, this.topPos + y)
                .size(6, TOGGLE_SIZE)
                .uv(-1, -1)
                .hoverUv(74, 214)
                .activeState(() -> this.activeSettingsTab == tab)
                .onPress(() -> { this.activeSettingsTab = tab; this.activeSlider = SLIDER_NONE; })
                .build();
        this.addRenderableWidget(btn);
        settingsTabButtons.add(btn);
    }

    private void addToggle(int parentTab, int x, int y,
                           BooleanSupplier state, int buttonId,
                           BooleanSupplier visible) {
        IconButton btn = IconButton.builder(TEXTURE_BG)
                .pos(this.leftPos + x, this.topPos + y)
                .size(TOGGLE_SIZE, TOGGLE_SIZE)
                .uv(-1, -1)
                .hoverUv(-1, -1)
                .activeState(state)
                .onPress(() -> sendMenuButton(buttonId))
                .build();
        btn.visible = false;
        btn.active = false;
        this.addRenderableWidget(btn);
        this.toggleButtons.add(btn);
        this.toggleParentTabs.add(parentTab);
        this.toggleVisibilitySuppliers.add(visible);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderLegacyHoverTooltips(guiGraphics, mouseX, mouseY);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(TEXTURE_BG, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        drawLockedInventoryPageOverlays(guiGraphics);
        drawInventoryPageIndicator(guiGraphics);
        drawDetailTabIndicator(guiGraphics);
        drawSettingsTabIndicator(guiGraphics);
        drawToggleStateMarks(guiGraphics);
        drawShipAndNameIcons(guiGraphics);
        drawTaskIcons(guiGraphics);
        drawShipEntityModel(guiGraphics, mouseX, mouseY);
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        drawLabel(guiGraphics, this.menu.getShip().getName().getString(), 8, 6);
        drawTopRightStatus(guiGraphics);

        if (this.activeDetailTab == DETAIL_TAB_BASIC) {
            drawLabel(guiGraphics, tr("gui.shincolle.kills"), 75, 20);
            drawValueRight(guiGraphics, String.valueOf(this.menu.getShipKills()), 135, 30, 0xFFFFFF);
            drawLabel(guiGraphics, tr("gui.shincolle.exp"), 75, 41);
            drawValueRight(guiGraphics, String.valueOf(this.menu.getShipExp()), 135, 51, 0xFFFFFF);
            drawLabel(guiGraphics, tr("gui.shincolle.ammolight"), 75, 62);
            drawValueRight(guiGraphics, String.valueOf(this.menu.getAmmoLightSynced()), 135, 72, 0xFFFFFF);
            drawLabel(guiGraphics, tr("gui.shincolle.ammoheavy"), 75, 83);
            drawValueRight(guiGraphics, String.valueOf(this.menu.getAmmoHeavySynced()), 135, 93, 0xFFFFFF);
            drawLabel(guiGraphics, tr("gui.shincolle.grudge"), 75, 104);
            drawValueRight(guiGraphics, String.valueOf(this.menu.getShipFuel()), 135, 114, 0xFFFFFF);
        } else if (this.activeDetailTab == DETAIL_TAB_STATUS) {
            drawLabel(guiGraphics, tr("gui.shincolle.firepower1"), 75, 20);
            drawValueRight(guiGraphics, String.format("%.0f", this.menu.getShipFirepower()), 135, 30, getModernizationColor(this.menu.getShip().getAttrBonus(1)));
            drawLabel(guiGraphics, tr("gui.shincolle.armor"), 75, 41);
            drawValueRight(guiGraphics, String.format("%.1f%%", this.menu.getShipArmor() * 100.0f), 135, 51, getModernizationColor(this.menu.getShip().getAttrBonus(2)));
            drawLabel(guiGraphics, tr("gui.shincolle.attackspeed"), 75, 62);
            drawValueRight(guiGraphics, String.format("%.2f", this.menu.getShipReloadSpeed()), 135, 72, getModernizationColor(this.menu.getShip().getAttrBonus(3)));
            drawLabel(guiGraphics, tr("gui.shincolle.movespeed"), 75, 83);
            drawValueRight(guiGraphics, String.format("%.2f", this.menu.getShipMoveSpeed()), 135, 93, getModernizationColor(this.menu.getShip().getAttrBonus(4)));
            drawLabel(guiGraphics, tr("gui.shincolle.range"), 75, 104);
            drawValueRight(guiGraphics, String.format("%.1f", this.menu.getShipRange()), 135, 114, getModernizationColor(this.menu.getShip().getAttrBonus(5)));
        } else {
            drawLabel(guiGraphics, tr("gui.shincolle.marriage"), 75, 20);
            drawValueRight(guiGraphics, this.menu.isMarried() ? tr("gui.shincolle.married") : tr("gui.shincolle.unmarried"), 135, 30, 0xFFFF00);
            drawLabel(guiGraphics, tr("gui.shincolle.formation.formation"), 75, 41);
            drawValueRight(guiGraphics, tr("gui.shincolle.formation.format0"), 135, 51, 0xFFFFFF);
            if (this.menu.getShip().supportsAircraftCombat()) {
                drawLabel(guiGraphics, tr("gui.shincolle.airplanelight"), 75, 83);
                drawValueRight(guiGraphics, String.valueOf(this.menu.getAircraftLight()), 135, 93, 0xFFFF00);
                drawLabel(guiGraphics, tr("gui.shincolle.airplaneheavy"), 75, 104);
                drawValueRight(guiGraphics, String.valueOf(this.menu.getAircraftHeavy()), 135, 114, 0xFFFF00);
            }
        }

        // Settings tab labels
        switch (this.activeSettingsTab) {
            case 1 -> {
                drawLabel(guiGraphics, tr("gui.shincolle.canmelee"), 187, 133);
                if (this.menu.getShip().isStateGuiBtn1()) drawLabel(guiGraphics, tr("gui.shincolle.canlightattack"), 187, 146);
                if (this.menu.getShip().isStateGuiBtn2()) drawLabel(guiGraphics, tr("gui.shincolle.canheavyattack"), 187, 159);
                if (this.menu.getShip().isStateGuiBtn3()) drawLabel(guiGraphics, tr("gui.shincolle.canairlightattack"), 187, 172);
                if (this.menu.getShip().isStateGuiBtn4()) drawLabel(guiGraphics, tr("gui.shincolle.canairheavyattack"), 187, 185);
                drawLabel(guiGraphics, tr("gui.shincolle.auraeffect"), 187, 198);
            }
            case 2 -> {
                drawLabel(guiGraphics, tr("gui.shincolle.followmin"), 174, 134);
                drawLabel(guiGraphics, tr("gui.shincolle.followmax"), 174, 158);
                drawLabel(guiGraphics, tr("gui.shincolle.fleehp"), 174, 182);
                drawValueLeft(guiGraphics, String.valueOf(getFollowMinDisplayValue()), 174, 145, 0xFFFFFF);
                drawValueLeft(guiGraphics, String.valueOf(getFollowMaxDisplayValue()), 174, 169, 0xFFFFFF);
                drawValueLeft(guiGraphics, String.valueOf(getFleeHpDisplayValue()), 174, 193, 0xFFFFFF);
            }
            case 3 -> {
                drawLabel(guiGraphics, tr("gui.shincolle.targetAI"), 187, 133);
                drawLabel(guiGraphics, tr("gui.shincolle.onsightAI"), 187, 146);
                drawLabel(guiGraphics, tr("gui.shincolle.ai.pvp"), 187, 159);
                drawLabel(guiGraphics, tr("gui.shincolle.ai.aa"), 187, 172);
                drawLabel(guiGraphics, tr("gui.shincolle.ai.asm"), 187, 185);
                drawLabel(guiGraphics, tr("gui.shincolle.ai.timekeeper"), 187, 198);
            }
            case 4 -> {
                if (this.menu.getShip().supportsItemPickup()) drawLabel(guiGraphics, tr("gui.shincolle.ai.pickitem"), 187, 133);
                drawLabel(guiGraphics, tr("gui.shincolle.autopump"), 187, 146);
            }
            case 5 -> {
                drawLabel(guiGraphics, tr("gui.shincolle.ai.wpstay"), 174, 134);
                drawLabel(guiGraphics, tr("gui.shincolle.autocombatration"), 174, 158);
                drawValueLeft(guiGraphics, getWpStayDisplay(), 174, 145, 0xFFFFFF);
                drawValueLeft(guiGraphics, getRationMoraleDisplay(), 174, 169, 0xFFFFFF);
            }
            case 6 -> {
                drawLabel(guiGraphics, tr("gui.shincolle.showhelditem"), 187, 133);
                drawLabel(guiGraphics, tr("gui.shincolle.equip.mount"), 187, 146);
                drawAppearanceLabels(guiGraphics);
            }
            case 7 -> {
                drawLabel(guiGraphics, tr("gui.shincolle.crane.usemeta"), 187, 159);
                drawLabel(guiGraphics, tr("gui.shincolle.crane.useoredict"), 187, 172);
                drawLabel(guiGraphics, tr("gui.shincolle.crane.usenbt"), 187, 185);
            }
            case 8 -> {
                drawLabel(guiGraphics, tr("gui.shincolle.ai.inputside"), 177, 133);
                drawLabel(guiGraphics, tr("gui.shincolle.ai.outputside"), 177, 159);
                drawLabel(guiGraphics, tr("gui.shincolle.ai.fuelside"), 177, 185);
            }
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        syncStateFromMenu();

        // Update page button active states based on unlock status
        int unlocked = this.menu.getUnlockedStoragePages();
        for (int i = 0; i < pageButtons.size(); i++) {
            pageButtons.get(i).active = i == 0 || unlocked >= i;
        }
        // Show toggle buttons for the active settings tab
        for (int i = 0; i < toggleButtons.size(); i++) {
            boolean matches = i < toggleParentTabs.size() && toggleParentTabs.get(i) == this.activeSettingsTab;
            boolean enabled = matches && i < toggleVisibilitySuppliers.size() && toggleVisibilitySuppliers.get(i).getAsBoolean();
            toggleButtons.get(i).visible = enabled;
            toggleButtons.get(i).active = enabled;
        }
    }

    // ---- Mouse interaction ----

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int x = (int) mouseX - this.leftPos;
            int y = (int) mouseY - this.topPos;

            // Slider drag starts
            if (tryStartSliderDrag(x, y)) return true;

            // Tab 7/8 special interactions (task selection, side toggles)
            if (handleTab7Click(x, y)) return true;
            if (handleTab8Click(x, y)) return true;
            if (handleAppearanceGridClick(x, y)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && this.activeSlider != SLIDER_NONE) {
            int x = (int) mouseX - this.leftPos;
            this.sliderBarPos = Mth.clamp(x - 191, 0, 42);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.activeSlider != SLIDER_NONE) {
            sendSliderValue();
            this.activeSlider = SLIDER_NONE;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean tryStartSliderDrag(int x, int y) {
        if (this.activeSettingsTab == 2) {
            if (inside(x, y, 187, 145, 237, 154)) return startSlider(SLIDER_FOLLOW_MIN, x);
            if (inside(x, y, 187, 169, 237, 178)) return startSlider(SLIDER_FOLLOW_MAX, x);
            if (inside(x, y, 187, 193, 237, 202)) return startSlider(SLIDER_FLEE_HP, x);
        }
        if (this.activeSettingsTab == 5) {
            if (inside(x, y, 187, 145, 237, 154)) return startSlider(SLIDER_WP_STAY, x);
            if (inside(x, y, 187, 169, 237, 178)) return startSlider(SLIDER_RATION_MORALE, x);
        }
        return false;
    }

    private boolean startSlider(int slider, int x) {
        this.activeSlider = slider;
        this.sliderBarPos = Mth.clamp(x - 191, 0, 42);
        return true;
    }

    private boolean handleTab7Click(int x, int y) {
        if (this.activeSettingsTab != SETTINGS_TAB_7) return false;
        // Task selection
        if (inside(x, y, 174, 136, 238, 152)) {
            int newTask = (x - 174) / 16 + 1;
            sendMenuButton(ShipContainerMenu.ACTION_TASK_SELECT_BASE + newTask);
            return true;
        }
        if (inside(x, y, 177, 157, 188, 168)) { sendMenuButton(ShipContainerMenu.ACTION_TASK_META_TOGGLE); return true; }
        if (inside(x, y, 177, 170, 188, 181)) { sendMenuButton(ShipContainerMenu.ACTION_TASK_ORE_TOGGLE); return true; }
        if (inside(x, y, 177, 183, 188, 194)) { sendMenuButton(ShipContainerMenu.ACTION_TASK_NBT_TOGGLE); return true; }
        return false;
    }

    private boolean handleTab8Click(int x, int y) {
        if (this.activeSettingsTab != SETTINGS_TAB_8) return false;
        for (int i = 0; i < 18; i++) {
            int dx = i % 6 * 11, dy = (i / 6) * 26;
            if (inside(x, y, 173 + dx, 144 + dy, 173 + dx + 10, 144 + dy + 10)) {
                sendMenuButton(ShipContainerMenu.ACTION_SIDE_TOGGLE_BASE + i);
                return true;
            }
        }
        return false;
    }

    private boolean handleAppearanceGridClick(int x, int y) {
        if (this.activeSettingsTab != SETTINGS_TAB_6) return false;
        List<?> options = this.menu.getEquipOptions();
        int count = options.size();
        for (int i = 0; i < Math.min(count, APPEARANCE_MAX_ITEMS); i++) {
            int col = i % APPEARANCE_COLS, row = i / APPEARANCE_COLS;
            int bx = APPEARANCE_GRID_X + col * APPEARANCE_GAP_X;
            int by = APPEARANCE_GRID_Y + row * APPEARANCE_GAP_Y;
            if (inside(x, y, bx, by, bx + TOGGLE_SIZE, by + TOGGLE_SIZE)) {
                sendMenuButton(this.menu.getEquipOptionButtonId(i));
                return true;
            }
        }
        return false;
    }

    // ---- Sync, helpers, drawing (unchanged structure) ----

    private void syncStateFromMenu() {
        this.canMelee = this.menu.isCanMeleeEnabled();
        this.lightAttack = this.menu.isLightAttackEnabled();
        this.heavyAttack = this.menu.isHeavyAttackEnabled();
        this.lightAircraftAttack = this.menu.isLightAircraftAttackEnabled();
        this.heavyAircraftAttack = this.menu.isHeavyAircraftAttackEnabled();
        this.ringEffect = this.menu.isRingEffectEnabled();
        this.followMinDistance = this.menu.getFollowMinDistance();
        this.followMaxDistance = this.menu.getFollowMaxDistance();
        this.fleeHpPercent = this.menu.getFleeHpPercent();
        this.passiveAttack = this.menu.isPassiveAttackEnabled();
        this.onSight = this.menu.isOnSightEnabled();
        this.pvpMode = this.menu.isPvpEnabled();
        this.antiAir = this.menu.isAntiAirEnabled();
        this.antiSub = this.menu.isAntiSubEnabled();
        this.timeKeeping = this.menu.isTimeKeepingEnabled();
        this.pickItem = this.menu.isPickItemEnabled();
        this.autoPump = this.menu.isAutoPumpEnabled();
        this.rationMorale = this.menu.getRationMoraleThreshold();
        this.appearance = this.menu.isAppearanceEnabled();
        this.mount = this.menu.isMountEnabled();
        this.taskId = this.menu.getTaskId();
        this.taskSideFlags = this.menu.getTaskSideFlags();
    }

    private void sendMenuButton(int id) {
        if (Minecraft.getInstance().gameMode != null) {
            Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }

    // ---- Drawing helpers (kept from original for visual fidelity) ----

    private void drawInventoryPageIndicator(GuiGraphics g) {
        int y = switch (this.menu.getInventoryPage()) { case 1 -> 54; case 2 -> 90; default -> 18; };
        g.blit(TEXTURE_BG, this.leftPos + 62, this.topPos + y, Sprites.SHIP_INV_PAGE_INDICATOR_U, Sprites.SHIP_INV_PAGE_INDICATOR_V, Sprites.SHIP_INV_PAGE_INDICATOR_W, Sprites.SHIP_INV_PAGE_INDICATOR_H, 256, 256);
    }

    private void drawDetailTabIndicator(GuiGraphics g) {
        int y = switch (this.activeDetailTab) {
            case DETAIL_TAB_STATUS -> 54;
            case DETAIL_TAB_MISC -> 90;
            default -> 18;
        };
        g.blit(TEXTURE_BG, this.leftPos + 135, this.topPos + y,
                Sprites.SHIP_INV_PAGE_INDICATOR_U, Sprites.SHIP_INV_PAGE_INDICATOR_V,
                Sprites.SHIP_INV_PAGE_INDICATOR_W, Sprites.SHIP_INV_PAGE_INDICATOR_H, 256, 256);
    }

    private void drawLockedInventoryPageOverlays(GuiGraphics g) {
        int unlocked = this.menu.getUnlockedStoragePages();
        if (unlocked <= 0) { drawPageSlash(g, 54); drawPageSlash(g, 90); }
        else if (unlocked == 1) drawPageSlash(g, 90);
    }

    private void drawPageSlash(GuiGraphics g, int y) {
        g.blit(TEXTURE_BG, this.leftPos + 62, this.topPos + y, Sprites.SHIP_INV_PAGE_SLASH_U, Sprites.SHIP_INV_PAGE_SLASH_V, Sprites.SHIP_INV_PAGE_SLASH_W, Sprites.SHIP_INV_PAGE_SLASH_H, 256, 256);
    }


    private void drawSettingsTabIndicator(GuiGraphics g) {
        int tab = Math.max(SETTINGS_TAB_1, Math.min(SETTINGS_TAB_12, this.activeSettingsTab));
        int curTab = (tab - 1) % 6, y = 131 + curTab * TOGGLE_ROW_STEP, x = tab <= 6 ? 239 : 246;
        g.blit(TEXTURE_BG, this.leftPos + x, this.topPos + y, Sprites.SHIP_INV_TAB_INDICATOR_U, Sprites.SHIP_INV_TAB_INDICATOR_V, Sprites.SHIP_INV_TAB_INDICATOR_W, Sprites.SHIP_INV_TAB_INDICATOR_H, 256, 256);
    }

    private void drawToggleStateMarks(GuiGraphics g) {
        switch (this.activeSettingsTab) {
            case 1 -> drawAiPage1ToggleMarks(g);
            case 2 -> drawFollowSliderTab(g);
            case 3 -> drawAiPage3ToggleMarks(g);
            case 4 -> drawAiPage4ToggleMarks(g);
            case 5 -> drawRationSliderTab(g);
            case 6 -> drawAppearanceToggleMarks(g);
            case 7 -> drawAIPage7Background(g);
            case 8 -> drawAIPage8Background(g);
        }
    }

    private void drawOnOff(GuiGraphics g, int x, int y, boolean on) {
        g.blit(TEXTURE_BG, this.leftPos + x, this.topPos + y, on ? Sprites.SHIP_INV_TOGGLE_ON_U : Sprites.SHIP_INV_TOGGLE_OFF_U, Sprites.SHIP_INV_TOGGLE_V, Sprites.SHIP_INV_TOGGLE_W, Sprites.SHIP_INV_TOGGLE_H, 256, 256);
    }

    private void drawAiPage1ToggleMarks(GuiGraphics g) {
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y, this.canMelee);
        if (this.menu.getShip().isStateGuiBtn1()) drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP, this.lightAttack);
        if (this.menu.getShip().isStateGuiBtn2()) drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 2, this.heavyAttack);
        if (this.menu.getShip().isStateGuiBtn3()) drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 3, this.lightAircraftAttack);
        if (this.menu.getShip().isStateGuiBtn4()) drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 4, this.heavyAircraftAttack);
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_2_Y + TOGGLE_ROW_STEP * 4, this.ringEffect);
    }

    private void drawAiPage3ToggleMarks(GuiGraphics g) {
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y, this.passiveAttack);
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP, this.onSight);
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 2, this.pvpMode);
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 3, this.antiAir);
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP * 4, this.antiSub);
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_2_Y + TOGGLE_ROW_STEP * 4, this.timeKeeping);
    }

    private void drawAiPage4ToggleMarks(GuiGraphics g) {
        if (this.menu.getShip().supportsItemPickup()) drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y, this.pickItem);
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y + TOGGLE_ROW_STEP, this.autoPump);
    }

    private void drawFollowSliderTab(GuiGraphics g) {
        int fMin = this.activeSlider == SLIDER_FOLLOW_MIN ? this.sliderBarPos : (int)((Math.max(1,this.followMinDistance)-1)/30f*42);
        int fMax = this.activeSlider == SLIDER_FOLLOW_MAX ? this.sliderBarPos : (int)((Math.max(2,this.followMaxDistance)-2)/30f*42);
        int fHp  = this.activeSlider == SLIDER_FLEE_HP    ? this.sliderBarPos : (int)(Math.max(0,Math.min(100,this.fleeHpPercent))/100f*42);
        drawSlider(g, 191, 148, fMin); drawSlider(g, 191, 172, fMax); drawSlider(g, 191, 196, fHp);
    }

    private void drawRationSliderTab(GuiGraphics g) {
        int wp  = this.activeSlider == SLIDER_WP_STAY ? this.sliderBarPos : (int)(Math.max(0,this.menu.getWpStaySetting())*0.0625f*42);
        int rat = this.activeSlider == SLIDER_RATION_MORALE ? this.sliderBarPos : (int)((Math.max(1,Math.min(4,this.rationMorale))-1)*14f);
        drawSlider(g, 191, 148, Mth.clamp(wp, 0, 42)); drawSlider(g, 191, 172, rat);
    }

    private void drawSlider(GuiGraphics g, int x, int y, int pos) {
        int p = Mth.clamp(pos, 0, 42);
        g.blit(TEXTURE_BG, this.leftPos + x, this.topPos + y, Sprites.SHIP_INV_SLIDER_TRACK_U, Sprites.SHIP_INV_SLIDER_TRACK_V, Sprites.SHIP_INV_SLIDER_TRACK_W, Sprites.SHIP_INV_SLIDER_TRACK_H, 256, 256);
        g.blit(TEXTURE_BG, this.leftPos + x - 4 + p, this.topPos + y - 3, Sprites.SHIP_INV_SLIDER_KNOB_U, Sprites.SHIP_INV_SLIDER_KNOB_V, Sprites.SHIP_INV_SLIDER_KNOB_W, Sprites.SHIP_INV_SLIDER_KNOB_H, 256, 256);
    }

    private void drawAppearanceToggleMarks(GuiGraphics g) {
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_1_Y, this.appearance);
        drawOnOff(g, TOGGLE_X, TOGGLE_ROW_2_Y, this.mount);
        int count = this.menu.getEquipOptionCount();
        for (int i = 0; i < Math.min(count, APPEARANCE_MAX_ITEMS); i++) {
            int col = i % APPEARANCE_COLS, row = i / APPEARANCE_COLS;
            drawOnOff(g, APPEARANCE_GRID_X + col * APPEARANCE_GAP_X, APPEARANCE_GRID_Y + row * APPEARANCE_GAP_Y, this.menu.isEquipOptionEnabled(i));
        }
    }

    private void drawAIPage7Background(GuiGraphics g) {
        int tside = this.taskSideFlags;
        g.blit(TEXTURE_BG, this.leftPos + 174, this.topPos + 136, Sprites.SHIP_INV_TASK_BG_U, Sprites.SHIP_INV_TASK_BG_V, Sprites.SHIP_INV_TASK_BG_W, Sprites.SHIP_INV_TASK_BG_H, 256, 256);
        g.blit(TEXTURE_BG, this.leftPos + 174, this.topPos + 138, Sprites.SHIP_INV_TASK_BG2_U, Sprites.SHIP_INV_TASK_BG2_V, Sprites.SHIP_INV_TASK_BG2_W, Sprites.SHIP_INV_TASK_BG2_H, 256, 256);
        int taskType = this.taskId;
        if (taskType >= 1 && taskType <= 4) {
            g.blit(TEXTURE_BG, this.leftPos + 174 + (taskType-1)*16, this.topPos + 136, 87+(taskType-1)*16, 230, 16, 16, 256, 256);
        }
        g.blit(TEXTURE_BG, this.leftPos + 177, this.topPos + 157, 0, (tside & (1<<18))!=0?236:225, 11, 11, 256, 256);
        g.blit(TEXTURE_BG, this.leftPos + 177, this.topPos + 170, 11, (tside & (1<<19))!=0?236:225, 11, 11, 256, 256);
        g.blit(TEXTURE_BG, this.leftPos + 177, this.topPos + 183, 22, (tside & (1<<20))!=0?236:225, 11, 11, 256, 256);
    }

    private void drawAIPage8Background(GuiGraphics g) {
        int tside = this.taskSideFlags;
        for (int y : new int[]{144, 170, 196}) {
            g.blit(TEXTURE_BG, this.leftPos + 173, this.topPos + y, Sprites.SHIP_INV_SIDE_ROW_BG_U, Sprites.SHIP_INV_SIDE_ROW_BG_V, Sprites.SHIP_INV_SIDE_ROW_BG_W, Sprites.SHIP_INV_SIDE_ROW_BG_H, 256, 256);
        }
        for (int i = 0; i < 18; ++i) {
            if ((tside & (1 << i)) != 0) {
                int dx = i % 6 * 11, dy = i / 6 * 26;
                g.blit(TEXTURE_BG, this.leftPos + 173 + dx, this.topPos + 144 + dy, 151 + dx, 225, 11, 11, 256, 256);
            }
        }
    }

    // ---- Top-right status ----

    private void drawTopRightStatus(GuiGraphics g) {
        String lvLabel = tr("gui.shincolle.level"), hpLabel = tr("gui.shincolle.hp");
        int lv = this.menu.getShipLevel(), hpCur = Math.round(this.menu.getShipHealth()), hpMax = Math.round(this.menu.getShipMaxHealth());
        int hpColor = getModernizationColor(this.menu.getShip().getAttrBonus(0));
        int lvColor = lv < 150 ? 0xFFFFFF : 0xFFD700;
        int hpCurColor = hpCur < hpMax ? getDarkerColor(hpColor, 0.8F) : hpColor;
        g.drawString(this.font, lvLabel, 231 - this.font.width(lvLabel), 6, 0x00FFFF, true);
        g.drawString(this.font, hpLabel, 145 - this.font.width(hpLabel), 6, 0x00FFFF, true);
        g.drawString(this.font, String.valueOf(lv), this.imageWidth - 6 - this.font.width(String.valueOf(lv)), 6, lvColor, true);
        g.drawString(this.font, String.valueOf(hpCur), 147, 6, hpCurColor, true);
        g.drawString(this.font, "/" + hpMax, 148 + this.font.width(String.valueOf(hpCur)), 6, hpColor, true);
    }

    // ---- Tooltip system ----

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        // Legacy tooltips kept here via renderLegacyHoverTooltips
    }

    private void renderLegacyHoverTooltips(GuiGraphics g, int mx, int my) {
        if (isHovering(mx, my, 239, 18, 11, 11)) { renderMoraleTooltip(g, mx, my); return; }
        if (isHovering(mx, my, 145, 4, 57, 11)) { renderModernizationHpTooltip(g, mx, my); return; }
        if (this.activeSettingsTab == SETTINGS_TAB_7) { renderAIPage7Tooltips(g, mx, my); return; }
        if (this.activeSettingsTab == SETTINGS_TAB_8) { renderAIPage8Tooltips(g, mx, my); return; }
        renderFixedToggleTooltips(g, mx, my);
    }

    private void renderAIPage7Tooltips(GuiGraphics g, int mx, int my) {
        int x = mx - this.leftPos, y = my - this.topPos;
        if (inside(x, y, 174, 136, 238, 152)) {
            int idx = (x - 174) / 16 + 1;
            String key = switch (idx) { case 1 -> "gui.shincolle.ai.cooking"; case 2 -> "gui.shincolle.ai.fishing"; case 3 -> "gui.shincolle.ai.mining"; case 4 -> "gui.shincolle.ai.crafting"; default -> null; };
            if (key != null) TooltipBuilder.of(key).renderIfNotEmpty(g, this.font, mx, my);
        } else if (inside(x, y, 177, 157, 188, 168)) TooltipBuilder.of("gui.shincolle.crane.usemeta").renderIfNotEmpty(g, this.font, mx, my);
        else if (inside(x, y, 177, 170, 188, 181)) TooltipBuilder.of("gui.shincolle.crane.useoredict").renderIfNotEmpty(g, this.font, mx, my);
        else if (inside(x, y, 177, 183, 188, 194)) TooltipBuilder.of("gui.shincolle.crane.usenbt").renderIfNotEmpty(g, this.font, mx, my);
    }

    private void renderAIPage8Tooltips(GuiGraphics g, int mx, int my) {
        int x = mx - this.leftPos, y = my - this.topPos;
        if (inside(x, y, 173, 144, 238, 155)) TooltipBuilder.of("gui.shincolle.ai.inputside").renderIfNotEmpty(g, this.font, mx, my);
        else if (inside(x, y, 173, 170, 238, 181)) TooltipBuilder.of("gui.shincolle.ai.outputside").renderIfNotEmpty(g, this.font, mx, my);
        else if (inside(x, y, 173, 196, 238, 207)) TooltipBuilder.of("gui.shincolle.ai.fuelside").renderIfNotEmpty(g, this.font, mx, my);
    }

    private void renderFixedToggleTooltips(GuiGraphics g, int mx, int my) {
        if (this.activeSettingsTab == SETTINGS_TAB_6) {
            if (isHovering(mx, my, TOGGLE_X, TOGGLE_ROW_1_Y, TOGGLE_SIZE, TOGGLE_SIZE))
                TooltipBuilder.of("gui.shincolle.showhelditem").renderIfNotEmpty(g, this.font, mx, my);
            else if (isHovering(mx, my, TOGGLE_X, TOGGLE_ROW_2_Y, TOGGLE_SIZE, TOGGLE_SIZE))
                TooltipBuilder.of("gui.shincolle.equip.mount").renderIfNotEmpty(g, this.font, mx, my);
            renderEquipOptionTooltips(g, mx, my);
        }
    }

    private void renderEquipOptionTooltips(GuiGraphics g, int mx, int my) {
        int x = mx - this.leftPos, y = my - this.topPos;
        int count = this.menu.getEquipOptionCount();
        for (int i = 0; i < Math.min(count, APPEARANCE_MAX_ITEMS); i++) {
            int col = i % APPEARANCE_COLS, row = i / APPEARANCE_COLS;
            int bx = APPEARANCE_GRID_X + col * APPEARANCE_GAP_X, by = APPEARANCE_GRID_Y + row * APPEARANCE_GAP_Y;
            if (inside(x, y, bx, by, bx + TOGGLE_SIZE, by + TOGGLE_SIZE)) {
                g.renderComponentTooltip(this.font, List.of(this.menu.getEquipOptionLabel(i)), mx, my);
                return;
            }
        }
    }

    private void renderMoraleTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        EntityShipBase ship = this.menu.getShip();
        float[] b = getLegacyMoraleBuffs(ship.getMorale());
        TooltipBuilder tip = TooltipBuilder.create()
                .addColored("gui.shincolle.morale" + getMoraleLevel(ship.getMorale()), ChatFormatting.GOLD);
        addMoraleStat(tip, ChatFormatting.RED, tr("gui.shincolle.firepower1"), "x %.0f %% / %.0f %%", b[1]*100, b[2]*100);
        addMoraleStat(tip, ChatFormatting.RED, tr("gui.shincolle.firepower2"), "x %.0f %% / %.0f %%", b[3]*100, b[4]*100);
        addMoraleStat(tip, ChatFormatting.WHITE, tr("gui.shincolle.attackspeed"), "x %.0f %%", b[6]*100);
        addMoraleStat(tip, ChatFormatting.LIGHT_PURPLE, tr("gui.shincolle.range"), "+ %.1f", b[8]);
        addMoraleStat(tip, ChatFormatting.AQUA, tr("gui.shincolle.critical"), "x %.0f %%", b[9]*100);
        addMoraleStat(tip, ChatFormatting.YELLOW, tr("gui.shincolle.doublehit"), "x %.0f %%", b[10]*100);
        addMoraleStat(tip, ChatFormatting.GOLD, tr("gui.shincolle.triplehit"), "x %.0f %%", b[11]*100);
        addMoraleStat(tip, ChatFormatting.RED, tr("gui.shincolle.missreduce"), "x %.0f %%", b[12]*100);
        addMoraleStat(tip, ChatFormatting.YELLOW, tr("gui.shincolle.antiair"), "x %.0f %%", b[13]*100);
        addMoraleStat(tip, ChatFormatting.AQUA, tr("gui.shincolle.antiss"), "x %.0f %%", b[14]*100);
        addMoraleStat(tip, ChatFormatting.WHITE, tr("gui.shincolle.armor"), "+ %.0f %%", b[5]*100);
        addMoraleStat(tip, ChatFormatting.GOLD, tr("gui.shincolle.dodge"), "+ %.0f %%", b[15]*100);
        addMoraleStat(tip, ChatFormatting.GREEN, tr("gui.shincolle.equip.xp"), "+ %.0f %%", b[16]*100);
        addMoraleStat(tip, ChatFormatting.DARK_PURPLE, tr("gui.shincolle.equip.grudge"), "+ %.0f %%", b[17]*100);
        addMoraleStat(tip, ChatFormatting.DARK_AQUA, tr("gui.shincolle.equip.ammo"), "+ %.0f %%", b[18]*100);
        addMoraleStat(tip, ChatFormatting.DARK_GREEN, tr("gui.shincolle.equip.hpres"), "+ %.0f %%", b[19]*100);
        addMoraleStat(tip, ChatFormatting.DARK_RED, tr("gui.shincolle.equip.kb"), "+ %.0f %%", b[20]*100);
        addMoraleStat(tip, ChatFormatting.GRAY, tr("gui.shincolle.movespeed"), "+ %.2f", b[7]);
        tip.renderIfNotEmpty(guiGraphics, this.font, mouseX, mouseY);
    }

    private void addMoraleStat(TooltipBuilder tip, ChatFormatting color, String label, String format, Object... args) {
        tip.add(Component.literal(label + ": ").withStyle(color)
                .append(Component.literal(String.format(format, args)).withStyle(ChatFormatting.WHITE)));
    }

    private void renderModernizationHpTooltip(GuiGraphics g, int mx, int my) {
        TooltipBuilder.of(tr("gui.shincolle.modernlevel") + " " + this.menu.getShip().getAttrBonus(0))
                .renderIfNotEmpty(g, this.font, mx, my);
    }

    private float[] getLegacyMoraleBuffs(int morale) {
        if (morale > 5100) return LEGACY_MORALE_LEVEL_0;
        if (morale > 3900) return LEGACY_MORALE_LEVEL_1;
        if (morale > 2100) return LEGACY_MORALE_NEUTRAL;
        if (morale > 900) return LEGACY_MORALE_LEVEL_3;
        return LEGACY_MORALE_LEVEL_4;
    }

    // ---- Display value helpers ----

    private int getFollowMinDisplayValue() {
        return this.activeSlider == SLIDER_FOLLOW_MIN ? (int)(this.sliderBarPos/42f*30+1) : this.followMinDistance;
    }
    private int getFollowMaxDisplayValue() {
        return this.activeSlider == SLIDER_FOLLOW_MAX ? (int)(this.sliderBarPos/42f*30+2) : this.followMaxDistance;
    }
    private int getFleeHpDisplayValue() {
        return this.activeSlider == SLIDER_FLEE_HP ? (int)(this.sliderBarPos/42f*100) : this.fleeHpPercent;
    }
    private String getWpStayDisplay() {
        int v = this.activeSlider == SLIDER_WP_STAY ? Math.max(0,Math.min(16,(int)(this.sliderBarPos/(42f*0.0625f)))) : Math.max(0,this.menu.getWpStaySetting());
        return formatWpStay(v);
    }
    private String getRationMoraleDisplay() {
        int t = this.activeSlider == SLIDER_RATION_MORALE ? Math.max(1,Math.min(4,this.sliderBarPos/14+1)) : Math.max(1,Math.min(4,this.rationMorale));
        return tr("gui.shincolle.morale" + t);
    }
    private String formatWpStay(int v) {
        if (v <= 0) return "OFF";
        if (v <= 5) return (v*5) + "s";
        if (v <= 10) return (v-5) + "m";
        return (v-10) + "h";
    }

    private void sendSliderValue() {
        int v = switch (this.activeSlider) {
            case SLIDER_FOLLOW_MIN -> (int)(this.sliderBarPos/42f*30+1);
            case SLIDER_FOLLOW_MAX -> (int)(this.sliderBarPos/42f*30+2);
            case SLIDER_FLEE_HP -> (int)(this.sliderBarPos/42f*100);
            case SLIDER_WP_STAY -> Math.max(0,Math.min(16,(int)(this.sliderBarPos/(42f*0.0625f))));
            case SLIDER_RATION_MORALE -> Math.max(1,Math.min(4,this.sliderBarPos/14+1));
            default -> -1;
        };
        int base = switch (this.activeSlider) {
            case SLIDER_FOLLOW_MIN -> ShipContainerMenu.SLIDER_FOLLOW_MIN_BASE;
            case SLIDER_FOLLOW_MAX -> ShipContainerMenu.SLIDER_FOLLOW_MAX_BASE;
            case SLIDER_FLEE_HP -> ShipContainerMenu.SLIDER_FLEE_HP_BASE;
            case SLIDER_WP_STAY -> ShipContainerMenu.SLIDER_WP_STAY_BASE;
            case SLIDER_RATION_MORALE -> ShipContainerMenu.SLIDER_RATION_MORALE_BASE;
            default -> 0;
        };
        if (v >= 0) sendMenuButton(base + v);
    }

    // ---- Color helpers ----
    private int getModernizationColor(int level) {
        float r = (float)level/3f-0.5f;
        if (r >= 0.5f) return 0xFF0000;
        if (r >= 0.0f) { int g = (int)(255f*(1f-r*2f)); return 0xFF0000 + (g<<8); }
        float s = r + 0.5f; int b = (int)(255f*(1f-s*2f)); return 0xFFFF00 + b;
    }
    private int getDarkerColor(int color, float dark) {
        return (int)(((color>>16)&0xFF)*dark)<<16 | (int)(((color>>8)&0xFF)*dark)<<8 | (int)((color&0xFF)*dark);
    }

    // ---- Text helpers ----
    private String tr(String key) { return Component.translatable(key).getString(); }
    private void drawLabel(GuiGraphics g, String text, int x, int y) {
        g.drawString(this.font, text, x+1, y+1, LEGACY_LABEL_SHADOW_COLOR, false);
        g.drawString(this.font, text, x, y, LEGACY_LABEL_COLOR, false);
    }
    private void drawValueLeft(GuiGraphics g, String text, int x, int y, int color) {
        g.drawString(this.font, text, x+1, y+1, LEGACY_LABEL_SHADOW_COLOR, false);
        g.drawString(this.font, text, x, y, color, false);
    }
    private void drawValueRight(GuiGraphics g, String text, int xRight, int y, int color) {
        int x = xRight - this.font.width(text);
        g.drawString(this.font, text, x+1, y+1, LEGACY_LABEL_SHADOW_COLOR, false);
        g.drawString(this.font, text, x, y, color, false);
    }
    private boolean inside(int x, int y, int x1, int y1, int x2, int y2) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }
    private boolean isHovering(int mx, int my, int x, int y, int w, int h) {
        return mx >= this.leftPos + x && mx < this.leftPos + x + w && my >= this.topPos + y && my < this.topPos + y + h;
    }
    private String trimLabelToWidth(String text, int maxWidth) {
        if (this.font.width(text) <= maxWidth) return text;
        String e = "..."; int end = text.length();
        while (end > 0 && this.font.width(text.substring(0,end)+e) > maxWidth) end--;
        return end <= 0 ? e : text.substring(0,end) + e;
    }

    // ---- Ship icons & model ----

    private void drawShipAndNameIcons(GuiGraphics g) {
        EntityShipBase ship = this.menu.getShip();
        int shipType = ship.getStateMinor(EntityShipBase.STATE_MINOR_FACTION_ID);
        int shipClass = ship.getStateMinor(EntityShipBase.STATE_MINOR_SHIP_CLASS);
        if (isRaidenGattaiState(ship)) { shipType = 2; shipClass = 55; }
        int[] typeUv = SHIP_TYPE_ICON_MAP.getOrDefault((byte)shipType, DEFAULT_SHIP_TYPE_ICON);
        int[] nameData = SHIP_NAME_ICON_MAP.getOrDefault(shipClass, DEFAULT_SHIP_NAME_ICON);
        int rarity = ship.getStateMinor(EntityShipBase.STATE_MINOR_RARITY);
        boolean isGod = rarity > 99;
        int frameU = 0, frameV = isGod ? 0 : 43, frameW = isGod ? 40 : 30, frameH = isGod ? 42 : 30;
        g.blit(TEXTURE_ICON0, this.leftPos+165, this.topPos+18, frameU, frameV, frameW, frameH, 256, 256);
        g.blit(TEXTURE_ICON0, this.leftPos+167, this.topPos+22, typeUv[0], typeUv[1], Sprites.SHIP_INV_TYPE_ICON_W, Sprites.SHIP_INV_TYPE_ICON_H, 256, 256);
        g.blit(TEXTURE_ICON0, this.leftPos+239, this.topPos+18, getMoraleLevel(ship.getMorale())*Sprites.SHIP_INV_MORALE_ICON_W, Sprites.SHIP_INV_MORALE_ICON_V, Sprites.SHIP_INV_MORALE_ICON_W, Sprites.SHIP_INV_MORALE_ICON_H, 256, 256);
        ResourceLocation tex = nameData[0] < 100 ? TEXTURE_ICON1 : TEXTURE_ICON2;
        int offY = nameData[0] == 4 ? -10 : (nameData[0] == 6 ? -10 : 0);
        if (nameData[0] >= 100) offY = 10;
        g.blit(tex, this.leftPos+176, this.topPos+63+offY, nameData[1], nameData[2], Sprites.SHIP_INV_NAME_ICON_W, Sprites.SHIP_INV_NAME_ICON_H, 256, 256);
    }

    private void drawShipEntityModel(GuiGraphics g, int mx, int my) {
        EntityShipBase ship = this.menu.getShip();
        float[] mp = ship.getModelPos();
        boolean gattai = isRaidenGattaiState(ship);
        int mX = this.leftPos + 218 + Mth.floor(mp[0]), mY = this.topPos + 100 + Mth.floor(mp[1]);
        float sm = gattai ? MODEL_SCALE_GATTAI_MULTIPLIER : 1f;
        int sc = Math.max(16, Mth.floor(mp[3]*sm));
        int hw = gattai ? MODEL_BOX_HALF_WIDTH_GATTAI : MODEL_BOX_HALF_WIDTH;
        int tp = gattai ? MODEL_BOX_TOP_GATTAI : MODEL_BOX_TOP;
        int bt = gattai ? MODEL_BOX_BOTTOM_GATTAI : MODEL_BOX_BOTTOM;
        if (gattai) {
            renderEntityWithPassengers(g, mX, mY, sc, mx, my, ship);
        } else {
            renderEntityWithPassengers(g, mX, mY, sc, mx - (mX - 3), my - (mY - (this.topPos+60)), ship);
        }
    }

    private boolean isRaidenGattaiState(EntityShipBase ship) {
        return (ship instanceof EntityDestroyerIkazuchi || ship instanceof EntityDestroyerInazuma) && ship.getRidingState() > 1;
    }

    public static void renderEntityWithPassengers(GuiGraphics g, int x, int y, int scale, float mx, float my, LivingEntity entity) {
        float f = (float)Math.atan((x - mx) / 40.0F);
        float f1 = (float)Math.atan((y - 50.0F - my) / 40.0F);
        var pose = g.pose();
        pose.pushPose();
        pose.translate(x, y, 50.0D);
        pose.scale(scale, scale, -scale);
        Quaternionf q1 = (new Quaternionf()).rotateZ((float)Math.PI);
        Quaternionf q2 = (new Quaternionf()).rotateX(f1*20f*0.017453292f);
        q1.mul(q2);
        pose.mulPose(q1);
        float byo=entity.yBodyRotO, by=entity.yBodyRot, yr=entity.getYRot(), xr=entity.getXRot(), yho=entity.yHeadRotO, yh=entity.yHeadRot;
        entity.yBodyRotO = 180f+f*20f; entity.yBodyRot = 180f+f*20f; entity.setYRot(180f+f*40f);
        entity.yHeadRotO = entity.getYRot(); entity.yHeadRot = entity.getYRot(); entity.setXRot(-f1*20f);
        EntityRenderDispatcher d = Minecraft.getInstance().getEntityRenderDispatcher();
        q2.conjugate(); d.overrideCameraOrientation(q2); d.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> {
            d.render(entity, 0,0,0, 0f,1f, pose, g.bufferSource(), 15728880);
            for (Entity pass : entity.getPassengers()) {
                float pbyo=0,pby=0,pyr=0,pxr=0,pyho=0,pyh=0;
                if (pass instanceof LivingEntity lp) { pbyo=lp.yBodyRotO; pby=lp.yBodyRot; pyr=lp.getYRot(); pxr=lp.getXRot(); pyho=lp.yHeadRotO; pyh=lp.yHeadRot;
                    lp.yBodyRotO=entity.yBodyRotO; lp.yBodyRot=entity.yBodyRot; lp.setYRot(entity.getYRot()); lp.yHeadRotO=entity.yHeadRotO; lp.yHeadRot=entity.yHeadRot; lp.setXRot(entity.getXRot()); }
                pose.pushPose();
                Vec3 rp = entity.getPassengerRidingPosition(pass);
                double inv = 1.0/scale;
                pose.translate((rp.x-entity.getX())*inv, (rp.y-entity.getY())*inv+0.09, (rp.z-entity.getZ())*inv);
                pose.translate(0,0,0.2);
                d.render(pass, 0,0,0, 0f,1f, pose, g.bufferSource(), 15728880);
                pose.popPose();
                if (pass instanceof LivingEntity lp) { lp.yBodyRotO=pbyo; lp.yBodyRot=pby; lp.setYRot(pyr); lp.setXRot(pxr); lp.yHeadRotO=pyho; lp.yHeadRot=pyh; }
            }
        });
        g.flush();
        d.setRenderShadow(true);
        entity.yBodyRotO=byo; entity.yBodyRot=by; entity.setYRot(yr); entity.setXRot(xr); entity.yHeadRotO=yho; entity.yHeadRot=yh;
        pose.popPose();
    }

    private int getMoraleLevel(int morale) {
        if (morale > 5100) return 0; if (morale > 3900) return 1; if (morale > 2100) return 2; if (morale > 900) return 3; return 4;
    }

    // ---- Task icons ----

    private void drawTaskIcons(GuiGraphics g) {
        if (this.menu.getInventoryPage() != 0) return;
        int u=0,v=0;
        switch (this.taskId) {
            case 1 -> { u=151; v=236; }
            case 2 -> { u=167; v=236; }
            case 3 -> { u=183; v=236; }
            case 4 -> { u=199; v=236; }
            default -> { return; }
        }
        g.blit(TEXTURE_BG, this.leftPos+25, this.topPos+107, Sprites.SHIP_INV_SLOT_OVERLAY_U, Sprites.SHIP_INV_SLOT_OVERLAY_V, Sprites.SHIP_INV_SLOT_OVERLAY_W, Sprites.SHIP_INV_SLOT_OVERLAY_H, 256, 256);
        g.blit(TEXTURE_BG, this.leftPos+26, this.topPos+109, u, v, Sprites.SHIP_INV_SLOT_OVERLAY_W, Sprites.SHIP_INV_SLOT_OVERLAY_H, 256, 256);
        if (this.taskId == 1) { drawSlotOverlay(g, HELD_MAIN_COL, HELD_MAIN_ROW, Sprites.SHIP_INV_SLOT_OVERLAY_U, Sprites.SHIP_INV_SLOT_OVERLAY_V); drawSlotOverlay(g, HELD_OFF_COL, HELD_OFF_ROW, Sprites.SHIP_INV_SLOT_OVERLAY_U, Sprites.SHIP_INV_SLOT_OVERLAY_V); }
        if (this.taskId == 2) drawSlotOverlay(g, HELD_MAIN_COL, HELD_MAIN_ROW, Sprites.SHIP_INV_SLOT_OVERLAY_U, Sprites.SHIP_INV_SLOT_OVERLAY_V);
        if (this.taskId == 3) drawSlotOverlay(g, HELD_MAIN_COL, HELD_MAIN_ROW, Sprites.SHIP_INV_SLOT_OVERLAY_U, Sprites.SHIP_INV_SLOT_OVERLAY_V);
        if (this.taskId == 4) { drawSlotOverlay(g, HELD_MAIN_COL, HELD_MAIN_ROW, Sprites.SHIP_INV_SLOT_OVERLAY_U, Sprites.SHIP_INV_SLOT_OVERLAY_V); drawCraftingSlots(g); }
    }

    private void drawSlotOverlay(GuiGraphics guiGraphics, int col, int row, int u, int v) {
        guiGraphics.blit(TEXTURE_BG, this.leftPos+8+col*18, this.topPos+18+row*18, u, v, Sprites.SHIP_INV_SLOT_OVERLAY_W, Sprites.SHIP_INV_SLOT_OVERLAY_H, 256, 256);
    }

    private void drawCraftingSlots(GuiGraphics guiGraphics) {
        for (int i = 0; i < 9; i++) {
            int col = i%3, row = i/3+2;
            boolean occupied = !this.menu.getShip().getInventory().getStackInSlot(CRAFTING_WORK_START_SLOT + i).isEmpty();
            drawSlotOverlay(guiGraphics, col, row, occupied ? Sprites.SHIP_INV_SLOT_OCCUPIED_U : Sprites.SHIP_INV_SLOT_OVERLAY_U, Sprites.SHIP_INV_SLOT_OVERLAY_V);
        }
    }

    private void drawAppearanceLabels(GuiGraphics g) {
        drawLabel(g, tr("gui.shincolle.appearance"), 177, 159);
    }

    // ---- Ship icon data maps ----

    private static Map<Byte, int[]> createShipTypeIconMap() {
        Map<Byte, int[]> map = new HashMap<>();
        map.put((byte)7, new int[]{12,74}); map.put((byte)-1, new int[]{41,0}); map.put((byte)1, new int[]{41,29});
        map.put((byte)2, new int[]{41,58}); map.put((byte)3, new int[]{41,87}); map.put((byte)6, new int[]{70,0});
        map.put((byte)5, new int[]{70,29}); map.put((byte)4, new int[]{70,58}); map.put((byte)10, new int[]{70,87});
        map.put((byte)8, new int[]{99,0}); map.put((byte)9, new int[]{99,58});
        return map;
    }

    private static Map<Integer, int[]> createShipNameIconMap() {
        Map<Integer, int[]> map = new HashMap<>();
        int[] data = {
            0,1,0,0, 1,1,11,0, 2,1,22,0, 3,1,33,0, 4,1,44,0, 5,1,55,0, 6,1,66,0, 7,1,77,0,
            8,1,88,0, 9,1,99,0, 10,1,110,0, 11,1,121,0, 12,1,132,0, 13,1,143,0, 14,1,154,0,
            15,1,165,0, 16,1,176,0, 17,1,187,0, 18,1,198,0, 19,1,209,0, 64,1,220,0,
            20,2,0,59, 21,2,11,59, 22,2,22,59, 23,2,33,59, 24,2,187,59, 25,2,176,59, 26,2,66,59,
            27,2,77,59, 28,2,88,59, 29,2,99,59, 30,2,110,59, 31,2,121,59, 32,2,132,59, 33,2,154,59,
            34,2,44,59, 35,2,165,59, 36,101,0,0, 37,101,11,0, 38,101,198,0, 39,101,209,0, 40,2,143,59,
            41,2,55,59, 43,2,209,59, 44,2,231,59, 45,2,198,59, 46,101,22,0, 47,101,33,0, 48,101,44,0,
            49,2,220,59, 50,2,242,59, 51,101,55,0, 52,101,66,0, 53,101,77,0, 54,101,88,0, 55,101,99,0,
            56,101,110,0, 57,101,121,0, 58,101,132,0, 59,101,143,0, 60,101,154,0, 61,101,165,0,
            62,101,176,0, 63,101,187,0, 65,3,0,118, 66,3,11,118, 67,3,22,118, 68,3,33,118, 69,3,44,118,
            70,3,55,118, 71,3,66,118, 72,3,77,118, 73,3,88,118, 74,3,99,118, 75,3,110,118, 76,3,121,118,
            77,3,132,118, 78,3,143,118, 79,3,154,118, 80,3,165,118, 81,3,176,118, 82,3,187,118,
            83,4,0,177, 84,4,11,177
        };
        for (int i = 0; i < data.length; i += 4) {
            map.put(data[i], new int[]{data[i+1], data[i+2], data[i+3]});
        }
        return map;
    }
}
