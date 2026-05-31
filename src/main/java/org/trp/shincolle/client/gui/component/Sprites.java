package org.trp.shincolle.client.gui.component;

import net.minecraft.resources.ResourceLocation;
import org.trp.shincolle.Shincolle;

/**
 * Central registry for all GUI texture and sprite locations.
 * <p>
 * All screen textures are 256×256 PNG sheets. This class provides:
 * <ul>
 *   <li>Texture {@link ResourceLocation} constants</li>
 *   <li>Helper methods to create per-screen sprite locations</li>
 *   <li>Named constants for commonly used UV regions (as future migration target for {@code blitSprite})</li>
 * </ul>
 * <p>
 * Migration note: when moving from {@code blit(texture, x, y, u, v, w, h, 256, 256)} to
 * {@code blitSprite(sprite, x, y, w, h)}, each (u, v, w, h) quad on a texture should become
 * a dedicated {@link ResourceLocation} in this class.
 */
public final class Sprites {

    private Sprites() {}

    // ---- Texture sheet locations (all 256×256) ----

    /** Ship inventory screen background. */
    public static final ResourceLocation T_SHIP_INVENTORY = gui("guishipinventory.png");
    /** Desk screen background. */
    public static final ResourceLocation T_DESK = gui("guidesk.png");
    /** Desk radar view. */
    public static final ResourceLocation T_RADAR = gui("guideskradar.png");
    /** Book (cyclopedia) page. */
    public static final ResourceLocation T_BOOK = gui("guideskbook.png");
    /** Book side panel / status markers. */
    public static final ResourceLocation T_BOOK2 = gui("guideskbook2.png");
    /** Name icons page 0 (morale, ship type). */
    public static final ResourceLocation T_NAME_ICON0 = gui("guinameicon0.png");
    /** Name icons page 1 (ship names). */
    public static final ResourceLocation T_NAME_ICON1 = gui("guinameicon1.png");
    /** Name icons page 2 (enemy names). */
    public static final ResourceLocation T_NAME_ICON2 = gui("guinameicon2.png");
    /** Formation screen background. */
    public static final ResourceLocation T_FORMATION = gui("guiformation.png");
    /** Crane screen background. */
    public static final ResourceLocation T_CRANE = gui("guicrane.png");
    /** Large shipyard background. */
    public static final ResourceLocation T_LARGE_SHIPYARD = gui("guilargeshipyard.png");
    /** Small shipyard background. */
    public static final ResourceLocation T_SMALL_SHIPYARD = gui("guismallshipyard.png");
    /** Volcano core extractor background. */
    public static final ResourceLocation T_VOLCORE = gui("guivolcore.png");
    /** Recipe paper background. */
    public static final ResourceLocation T_RECIPE_PAPER = gui("guirecipepaper.png");
    /** Book embedded illustration. */
    public static final ResourceLocation T_BOOK_PIC01 = gui("book/bookpic01.png");

    // ---- Well-known UV-region constants (for legacy blit()) ----
    // These are here as documentation anchors. When migrating to blitSprite,
    // replace each (texture, u, v, w, h) group with a dedicated sprite ResourceLocation.

    // ---- VolCore texture ----
    /** Active state indicator for the toggle button. */
    public static final int VOLCORE_BTN_ACTIVE_U = 12, VOLCORE_BTN_ACTIVE_V = 166;
    public static final int VOLCORE_BTN_W = 13, VOLCORE_BTN_H = 13;
    /** VolCore toggle screen-local position. */
    public static final int VOLCORE_BTN_X = 7, VOLCORE_BTN_Y = 6;

    // ---- Crane texture ----
    /** Active state indicator (top-left). */
    public static final int CRANE_BTN_ACTIVE_U = 176, CRANE_BTN_ACTIVE_V = 0;
    public static final int CRANE_BTN_ACTIVE_W = 13, CRANE_BTN_ACTIVE_H = 13;
    /** Metadata-check indicator. */
    public static final int CRANE_CHK_METADATA_U = 176, CRANE_CHK_METADATA_V = 13;
    public static final int CRANE_CHK_METADATA_W = 11, CRANE_CHK_METADATA_H = 11;
    /** OreDict-check indicator. */
    public static final int CRANE_CHK_OREDICT_U = 176, CRANE_CHK_OREDICT_V = 24;
    public static final int CRANE_CHK_OREDICT_W = 11, CRANE_CHK_OREDICT_H = 11;
    /** NBT-check indicator. */
    public static final int CRANE_CHK_NBT_U = 176, CRANE_CHK_NBT_V = 46;
    public static final int CRANE_CHK_NBT_W = 11, CRANE_CHK_NBT_H = 11;
    /** Redstone mode 1 indicator. */
    public static final int CRANE_RED_MODE1_U = 176, CRANE_RED_MODE1_V = 57;
    public static final int CRANE_RED_MODE1_W = 11, CRANE_RED_MODE1_H = 11;
    /** Redstone mode 2 indicator. */
    public static final int CRANE_RED_MODE2_U = 176, CRANE_RED_MODE2_V = 68;
    public static final int CRANE_RED_MODE2_W = 11, CRANE_RED_MODE2_H = 11;
    /** Disabled load/unload button icon. */
    public static final int CRANE_BTN_DISABLED_U = 176, CRANE_BTN_DISABLED_V = 35;
    public static final int CRANE_BTN_DISABLED_W = 11, CRANE_BTN_DISABLED_H = 11;
    /** Disabled overlay strip. */
    public static final int CRANE_DISABLED_OVERLAY_U = 0, CRANE_DISABLED_OVERLAY_V = 201;
    public static final int CRANE_DISABLED_OVERLAY_W = 160, CRANE_DISABLED_OVERLAY_H = 16;
    /** Liquid mode 0 indicator. */
    public static final int CRANE_LIQ_MODE0_U = 202, CRANE_LIQ_MODE0_V = 101;
    public static final int CRANE_LIQ_MODE0_W = 13, CRANE_LIQ_MODE0_H = 13;
    /** Liquid mode 1 indicator. */
    public static final int CRANE_LIQ_MODE1_U = 176, CRANE_LIQ_MODE1_V = 101;
    public static final int CRANE_LIQ_MODE1_W = 13, CRANE_LIQ_MODE1_H = 13;
    /** Liquid mode 2 indicator. */
    public static final int CRANE_LIQ_MODE2_U = 189, CRANE_LIQ_MODE2_V = 101;
    public static final int CRANE_LIQ_MODE2_W = 13, CRANE_LIQ_MODE2_H = 13;
    /** Energy mode 0 indicator. */
    public static final int CRANE_ENERGY_MODE0_U = 202, CRANE_ENERGY_MODE0_V = 114;
    public static final int CRANE_ENERGY_MODE0_W = 13, CRANE_ENERGY_MODE0_H = 13;
    /** Energy mode 1 indicator. */
    public static final int CRANE_ENERGY_MODE1_U = 176, CRANE_ENERGY_MODE1_V = 114;
    public static final int CRANE_ENERGY_MODE1_W = 13, CRANE_ENERGY_MODE1_H = 13;
    /** Energy mode 2 indicator. */
    public static final int CRANE_ENERGY_MODE2_U = 189, CRANE_ENERGY_MODE2_V = 114;
    public static final int CRANE_ENERGY_MODE2_W = 13, CRANE_ENERGY_MODE2_H = 13;
    /** Slot item in normal mode. */
    public static final int CRANE_SLOT_ON_U = 0, CRANE_SLOT_ON_V = 217;
    public static final int CRANE_SLOT_ON_W = 18, CRANE_SLOT_ON_H = 18;
    /** Slot item in disabled/off mode. */
    public static final int CRANE_SLOT_OFF_U = 19, CRANE_SLOT_OFF_V = 217;
    public static final int CRANE_SLOT_OFF_W = 18, CRANE_SLOT_OFF_H = 18;

    // ---- Formation texture ----
    /** Selected ship slot highlight bar. */
    public static final int FORMATION_SLOT_HIGHLIGHT_U = 3, FORMATION_SLOT_HIGHLIGHT_V = 192;
    public static final int FORMATION_SLOT_HIGHLIGHT_W = 108, FORMATION_SLOT_HIGHLIGHT_H = 27;
    /** Buff bar background track. */
    public static final int FORMATION_BAR_BG_U = 0, FORMATION_BAR_BG_V = 220;
    public static final int FORMATION_BAR_BG_W = 20, FORMATION_BAR_BG_H = 4;

    // ---- ShipInventory texture ----
    /** Inventory page indicator (selected page highlight). */
    public static final int SHIP_INV_PAGE_INDICATOR_U = 74, SHIP_INV_PAGE_INDICATOR_V = 214;
    public static final int SHIP_INV_PAGE_INDICATOR_W = 6, SHIP_INV_PAGE_INDICATOR_H = 34;
    /** Inventory page slash overlay (locked page). */
    public static final int SHIP_INV_PAGE_SLASH_U = 80, SHIP_INV_PAGE_SLASH_V = 214;
    public static final int SHIP_INV_PAGE_SLASH_W = 6, SHIP_INV_PAGE_SLASH_H = 34;
    /** Settings tab indicator highlight. */
    public static final int SHIP_INV_TAB_INDICATOR_U = 74, SHIP_INV_TAB_INDICATOR_V = 214;
    public static final int SHIP_INV_TAB_INDICATOR_W = 6, SHIP_INV_TAB_INDICATOR_H = 11;
    /** On/Off toggle: on state UV origin. */
    public static final int SHIP_INV_TOGGLE_ON_U = 0;
    /** On/Off toggle: off state UV origin. */
    public static final int SHIP_INV_TOGGLE_OFF_U = 11;
    /** On/Off toggle V. */
    public static final int SHIP_INV_TOGGLE_V = 214;
    /** On/Off toggle size. */
    public static final int SHIP_INV_TOGGLE_W = 11, SHIP_INV_TOGGLE_H = 11;
    /** Slider track bar. */
    public static final int SHIP_INV_SLIDER_TRACK_U = 31, SHIP_INV_SLIDER_TRACK_V = 214;
    public static final int SHIP_INV_SLIDER_TRACK_W = 43, SHIP_INV_SLIDER_TRACK_H = 3;
    /** Slider drag knob. */
    public static final int SHIP_INV_SLIDER_KNOB_U = 22, SHIP_INV_SLIDER_KNOB_V = 214;
    public static final int SHIP_INV_SLIDER_KNOB_W = 9, SHIP_INV_SLIDER_KNOB_H = 9;
    /** AI task selection background strip. */
    public static final int SHIP_INV_TASK_BG_U = 87, SHIP_INV_TASK_BG_V = 214;
    public static final int SHIP_INV_TASK_BG_W = 64, SHIP_INV_TASK_BG_H = 16;
    /** AI task selection background strip 2. */
    public static final int SHIP_INV_TASK_BG2_U = 151, SHIP_INV_TASK_BG2_V = 237;
    public static final int SHIP_INV_TASK_BG2_W = 64, SHIP_INV_TASK_BG2_H = 16;
    /** AI side-toggle row background. */
    public static final int SHIP_INV_SIDE_ROW_BG_U = 151, SHIP_INV_SIDE_ROW_BG_V = 214;
    public static final int SHIP_INV_SIDE_ROW_BG_W = 66, SHIP_INV_SIDE_ROW_BG_H = 11;
    /** Slot overlay (empty). */
    public static final int SHIP_INV_SLOT_OVERLAY_U = 33, SHIP_INV_SLOT_OVERLAY_V = 225;
    public static final int SHIP_INV_SLOT_OVERLAY_W = 18, SHIP_INV_SLOT_OVERLAY_H = 18;
    /** Slot overlay (occupied). */
    public static final int SHIP_INV_SLOT_OCCUPIED_U = 51, SHIP_INV_SLOT_OCCUPIED_V = 225;
    public static final int SHIP_INV_SLOT_OCCUPIED_W = 18, SHIP_INV_SLOT_OCCUPIED_H = 18;
    /** Ship inventory morale icon V (on name icon texture). */
    public static final int SHIP_INV_MORALE_ICON_V = 240;
    /** Ship inventory morale icon size. */
    public static final int SHIP_INV_MORALE_ICON_W = 11, SHIP_INV_MORALE_ICON_H = 11;
    /** Ship name icon size. */
    public static final int SHIP_INV_NAME_ICON_W = 11, SHIP_INV_NAME_ICON_H = 59;
    /** Ship type icon size. */
    public static final int SHIP_INV_TYPE_ICON_W = 28, SHIP_INV_TYPE_ICON_H = 28;

    // ---- Large shipyard texture ----
    /** Selected material row background. */
    public static final int SHIPYARD_LARGE_SELECTION_BG_U = 0, SHIPYARD_LARGE_SELECTION_BG_V = 223;
    public static final int SHIPYARD_LARGE_SELECTION_BG_W = 48, SHIPYARD_LARGE_SELECTION_BG_H = 30;
    /** Material selection icon. */
    public static final int SHIPYARD_LARGE_SELECTION_ICON_U = 208, SHIPYARD_LARGE_SELECTION_ICON_V = 64;
    public static final int SHIPYARD_LARGE_SELECTION_ICON_W = 18, SHIPYARD_LARGE_SELECTION_ICON_H = 18;
    /** Inventory mode indicator (toggle). */
    public static final int SHIPYARD_LARGE_INV_MODE_ICON_U = 208, SHIPYARD_LARGE_INV_MODE_ICON_V = 82;
    public static final int SHIPYARD_LARGE_INV_MODE_ICON_W = 25, SHIPYARD_LARGE_INV_MODE_ICON_H = 20;

    // ---- Small shipyard texture ----
    /** Build type indicator icon U and size. */
    public static final int SHIPYARD_SMALL_BUILD_ICON_U = 176;
    public static final int SHIPYARD_SMALL_BUILD_ICON_W = 18, SHIPYARD_SMALL_BUILD_ICON_H = 18;

    // ---- Desk textures (book / desk / radar / nameicon0) ----
    /** Book prev-page button. */
    public static final int DESK_BOOK_PREV_BTN_U = 0, DESK_BOOK_PREV_BTN_V = 192;
    public static final int DESK_BOOK_PREV_BTN_W = 18, DESK_BOOK_PREV_BTN_H = 10;
    /** Book next-page button. */
    public static final int DESK_BOOK_NEXT_BTN_U = 0, DESK_BOOK_NEXT_BTN_V = 202;
    public static final int DESK_BOOK_NEXT_BTN_W = 18, DESK_BOOK_NEXT_BTN_H = 10;
    /** Radar selected ship row highlight. */
    public static final int DESK_RADAR_SELECTED_ROW_U = 68, DESK_RADAR_SELECTED_ROW_V = 192;
    public static final int DESK_RADAR_SELECTED_ROW_W = 108, DESK_RADAR_SELECTED_ROW_H = 31;
    /** Radar action button. */
    public static final int DESK_RADAR_ACTION_BTN_U = 24, DESK_RADAR_ACTION_BTN_V = 216;
    public static final int DESK_RADAR_ACTION_BTN_W = 44, DESK_RADAR_ACTION_BTN_H = 10;

    // ---- Helper methods ----

    /** Shorthand for {@code ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/" + path)}. */
    public static ResourceLocation gui(String path) {
        return ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/" + path);
    }

    /** Shorthand for {@code ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, path)}. */
    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, path);
    }
}
