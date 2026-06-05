package org.trp.shincolle.client.gui.component

import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle

/**
 * Central registry for all GUI texture and sprite locations.
 * 
 * 
 * All screen textures are 256×256 PNG sheets. This class provides:
 * 
 *  * Texture [ResourceLocation] constants
 *  * Helper methods to create per-screen sprite locations
 *  * Named constants for commonly used UV regions (as future migration target for `blitSprite`)
 * 
 * 
 * 
 * Migration note: when moving from `blit(texture, x, y, u, v, w, h, 256, 256)` to
 * `blitSprite(sprite, x, y, w, h)`, each (u, v, w, h) quad on a texture should become
 * a dedicated [ResourceLocation] in this class.
 */
object Sprites {
    // ---- Texture sheet locations (all 256×256) ----
    /** Ship inventory screen background.  */
    val T_SHIP_INVENTORY: ResourceLocation = gui("guishipinventory.png")

    /** Desk screen background.  */
    val T_DESK: ResourceLocation = gui("guidesk.png")

    /** Desk radar view.  */
    val T_RADAR: ResourceLocation = gui("guideskradar.png")

    /** Book (cyclopedia) page.  */
    val T_BOOK: ResourceLocation = gui("guideskbook.png")

    /** Book side panel / status markers.  */
    val T_BOOK2: ResourceLocation = gui("guideskbook2.png")

    /** Name icons page 0 (morale, ship type).  */
    val T_NAME_ICON0: ResourceLocation = gui("guinameicon0.png")

    /** Name icons page 1 (ship names).  */
    val T_NAME_ICON1: ResourceLocation = gui("guinameicon1.png")

    /** Name icons page 2 (enemy names).  */
    val T_NAME_ICON2: ResourceLocation = gui("guinameicon2.png")

    /** Formation screen background.  */
    val T_FORMATION: ResourceLocation = gui("guiformation.png")

    /** Crane screen background.  */
    val T_CRANE: ResourceLocation = gui("guicrane.png")

    /** Large shipyard background.  */
    val T_LARGE_SHIPYARD: ResourceLocation = gui("guilargeshipyard.png")

    /** Small shipyard background.  */
    val T_SMALL_SHIPYARD: ResourceLocation = gui("guismallshipyard.png")

    /** Volcano core extractor background.  */
    val T_VOLCORE: ResourceLocation = gui("guivolcore.png")

    /** Recipe paper background.  */
    val T_RECIPE_PAPER: ResourceLocation = gui("guirecipepaper.png")

    /** Book embedded illustration.  */
    val T_BOOK_PIC01: ResourceLocation = gui("book/bookpic01.png")

    // ---- Well-known UV-region constants (for legacy blit()) ----
    // These are here as documentation anchors. When migrating to blitSprite,
    // replace each (texture, u, v, w, h) group with a dedicated sprite ResourceLocation.
    // ---- VolCore texture ----
    /** Active state indicator for the toggle button.  */
    const val VOLCORE_BTN_ACTIVE_U: Int = 12
    const val VOLCORE_BTN_ACTIVE_V: Int = 166
    const val VOLCORE_BTN_W: Int = 13
    const val VOLCORE_BTN_H: Int = 13

    /** VolCore toggle screen-local position.  */
    const val VOLCORE_BTN_X: Int = 7
    const val VOLCORE_BTN_Y: Int = 6

    // ---- Crane texture ----
    /** Active state indicator (top-left).  */
    const val CRANE_BTN_ACTIVE_U: Int = 176
    const val CRANE_BTN_ACTIVE_V: Int = 0
    const val CRANE_BTN_ACTIVE_W: Int = 13
    const val CRANE_BTN_ACTIVE_H: Int = 13

    /** Metadata-check indicator.  */
    const val CRANE_CHK_METADATA_U: Int = 176
    const val CRANE_CHK_METADATA_V: Int = 13
    const val CRANE_CHK_METADATA_W: Int = 11
    const val CRANE_CHK_METADATA_H: Int = 11

    /** OreDict-check indicator.  */
    const val CRANE_CHK_OREDICT_U: Int = 176
    const val CRANE_CHK_OREDICT_V: Int = 24
    const val CRANE_CHK_OREDICT_W: Int = 11
    const val CRANE_CHK_OREDICT_H: Int = 11

    /** NBT-check indicator.  */
    const val CRANE_CHK_NBT_U: Int = 176
    const val CRANE_CHK_NBT_V: Int = 46
    const val CRANE_CHK_NBT_W: Int = 11
    const val CRANE_CHK_NBT_H: Int = 11

    /** Redstone mode 1 indicator.  */
    const val CRANE_RED_MODE1_U: Int = 176
    const val CRANE_RED_MODE1_V: Int = 57
    const val CRANE_RED_MODE1_W: Int = 11
    const val CRANE_RED_MODE1_H: Int = 11

    /** Redstone mode 2 indicator.  */
    const val CRANE_RED_MODE2_U: Int = 176
    const val CRANE_RED_MODE2_V: Int = 68
    const val CRANE_RED_MODE2_W: Int = 11
    const val CRANE_RED_MODE2_H: Int = 11

    /** Disabled load/unload button icon.  */
    const val CRANE_BTN_DISABLED_U: Int = 176
    const val CRANE_BTN_DISABLED_V: Int = 35
    const val CRANE_BTN_DISABLED_W: Int = 11
    const val CRANE_BTN_DISABLED_H: Int = 11

    /** Disabled overlay strip.  */
    const val CRANE_DISABLED_OVERLAY_U: Int = 0
    const val CRANE_DISABLED_OVERLAY_V: Int = 201
    const val CRANE_DISABLED_OVERLAY_W: Int = 160
    const val CRANE_DISABLED_OVERLAY_H: Int = 16

    /** Liquid mode 0 indicator.  */
    const val CRANE_LIQ_MODE0_U: Int = 202
    const val CRANE_LIQ_MODE0_V: Int = 101
    const val CRANE_LIQ_MODE0_W: Int = 13
    const val CRANE_LIQ_MODE0_H: Int = 13

    /** Liquid mode 1 indicator.  */
    const val CRANE_LIQ_MODE1_U: Int = 176
    const val CRANE_LIQ_MODE1_V: Int = 101
    const val CRANE_LIQ_MODE1_W: Int = 13
    const val CRANE_LIQ_MODE1_H: Int = 13

    /** Liquid mode 2 indicator.  */
    const val CRANE_LIQ_MODE2_U: Int = 189
    const val CRANE_LIQ_MODE2_V: Int = 101
    const val CRANE_LIQ_MODE2_W: Int = 13
    const val CRANE_LIQ_MODE2_H: Int = 13

    /** Energy mode 0 indicator.  */
    const val CRANE_ENERGY_MODE0_U: Int = 202
    const val CRANE_ENERGY_MODE0_V: Int = 114
    const val CRANE_ENERGY_MODE0_W: Int = 13
    const val CRANE_ENERGY_MODE0_H: Int = 13

    /** Energy mode 1 indicator.  */
    const val CRANE_ENERGY_MODE1_U: Int = 176
    const val CRANE_ENERGY_MODE1_V: Int = 114
    const val CRANE_ENERGY_MODE1_W: Int = 13
    const val CRANE_ENERGY_MODE1_H: Int = 13

    /** Energy mode 2 indicator.  */
    const val CRANE_ENERGY_MODE2_U: Int = 189
    const val CRANE_ENERGY_MODE2_V: Int = 114
    const val CRANE_ENERGY_MODE2_W: Int = 13
    const val CRANE_ENERGY_MODE2_H: Int = 13

    /** Slot item in normal mode.  */
    const val CRANE_SLOT_ON_U: Int = 0
    const val CRANE_SLOT_ON_V: Int = 217
    const val CRANE_SLOT_ON_W: Int = 18
    const val CRANE_SLOT_ON_H: Int = 18

    /** Slot item in disabled/off mode.  */
    const val CRANE_SLOT_OFF_U: Int = 19
    const val CRANE_SLOT_OFF_V: Int = 217
    const val CRANE_SLOT_OFF_W: Int = 18
    const val CRANE_SLOT_OFF_H: Int = 18

    // ---- Formation texture ----
    /** Selected ship slot highlight bar.  */
    const val FORMATION_SLOT_HIGHLIGHT_U: Int = 3
    const val FORMATION_SLOT_HIGHLIGHT_V: Int = 192
    const val FORMATION_SLOT_HIGHLIGHT_W: Int = 108
    const val FORMATION_SLOT_HIGHLIGHT_H: Int = 27

    /** Buff bar background track.  */
    const val FORMATION_BAR_BG_U: Int = 0
    const val FORMATION_BAR_BG_V: Int = 220
    const val FORMATION_BAR_BG_W: Int = 20
    const val FORMATION_BAR_BG_H: Int = 4

    // ---- ShipInventory texture ----
    /** Inventory page indicator (selected page highlight).  */
    const val SHIP_INV_PAGE_INDICATOR_U: Int = 74
    const val SHIP_INV_PAGE_INDICATOR_V: Int = 214
    const val SHIP_INV_PAGE_INDICATOR_W: Int = 6
    const val SHIP_INV_PAGE_INDICATOR_H: Int = 34

    /** Inventory page slash overlay (locked page).  */
    const val SHIP_INV_PAGE_SLASH_U: Int = 80
    const val SHIP_INV_PAGE_SLASH_V: Int = 214
    const val SHIP_INV_PAGE_SLASH_W: Int = 6
    const val SHIP_INV_PAGE_SLASH_H: Int = 34

    /** Settings tab indicator highlight.  */
    const val SHIP_INV_TAB_INDICATOR_U: Int = 74
    const val SHIP_INV_TAB_INDICATOR_V: Int = 214
    const val SHIP_INV_TAB_INDICATOR_W: Int = 6
    const val SHIP_INV_TAB_INDICATOR_H: Int = 11

    /** On/Off toggle: on state UV origin.  */
    const val SHIP_INV_TOGGLE_ON_U: Int = 0

    /** On/Off toggle: off state UV origin.  */
    const val SHIP_INV_TOGGLE_OFF_U: Int = 11

    /** On/Off toggle V.  */
    const val SHIP_INV_TOGGLE_V: Int = 214

    /** On/Off toggle size.  */
    const val SHIP_INV_TOGGLE_W: Int = 11
    const val SHIP_INV_TOGGLE_H: Int = 11

    /** Slider track bar.  */
    const val SHIP_INV_SLIDER_TRACK_U: Int = 31
    const val SHIP_INV_SLIDER_TRACK_V: Int = 214
    const val SHIP_INV_SLIDER_TRACK_W: Int = 43
    const val SHIP_INV_SLIDER_TRACK_H: Int = 3

    /** Slider drag knob.  */
    const val SHIP_INV_SLIDER_KNOB_U: Int = 22
    const val SHIP_INV_SLIDER_KNOB_V: Int = 214
    const val SHIP_INV_SLIDER_KNOB_W: Int = 9
    const val SHIP_INV_SLIDER_KNOB_H: Int = 9

    /** AI task selection background strip.  */
    const val SHIP_INV_TASK_BG_U: Int = 87
    const val SHIP_INV_TASK_BG_V: Int = 214
    const val SHIP_INV_TASK_BG_W: Int = 64
    const val SHIP_INV_TASK_BG_H: Int = 16

    /** AI task selection background strip 2.  */
    const val SHIP_INV_TASK_BG2_U: Int = 151
    const val SHIP_INV_TASK_BG2_V: Int = 237
    const val SHIP_INV_TASK_BG2_W: Int = 64
    const val SHIP_INV_TASK_BG2_H: Int = 16

    /** AI side-toggle row background.  */
    const val SHIP_INV_SIDE_ROW_BG_U: Int = 151
    const val SHIP_INV_SIDE_ROW_BG_V: Int = 214
    const val SHIP_INV_SIDE_ROW_BG_W: Int = 66
    const val SHIP_INV_SIDE_ROW_BG_H: Int = 11

    /** Slot overlay (empty).  */
    const val SHIP_INV_SLOT_OVERLAY_U: Int = 33
    const val SHIP_INV_SLOT_OVERLAY_V: Int = 225
    const val SHIP_INV_SLOT_OVERLAY_W: Int = 18
    const val SHIP_INV_SLOT_OVERLAY_H: Int = 18

    /** Slot overlay (occupied).  */
    const val SHIP_INV_SLOT_OCCUPIED_U: Int = 51
    const val SHIP_INV_SLOT_OCCUPIED_V: Int = 225
    const val SHIP_INV_SLOT_OCCUPIED_W: Int = 18
    const val SHIP_INV_SLOT_OCCUPIED_H: Int = 18

    /** Ship inventory morale icon V (on name icon texture).  */
    const val SHIP_INV_MORALE_ICON_V: Int = 240

    /** Ship inventory morale icon size.  */
    const val SHIP_INV_MORALE_ICON_W: Int = 11
    const val SHIP_INV_MORALE_ICON_H: Int = 11

    /** Ship name icon size.  */
    const val SHIP_INV_NAME_ICON_W: Int = 11
    const val SHIP_INV_NAME_ICON_H: Int = 59

    /** Ship type icon size.  */
    const val SHIP_INV_TYPE_ICON_W: Int = 28
    const val SHIP_INV_TYPE_ICON_H: Int = 28

    // ---- Large shipyard texture ----
    /** Selected material row background.  */
    const val SHIPYARD_LARGE_SELECTION_BG_U: Int = 0
    const val SHIPYARD_LARGE_SELECTION_BG_V: Int = 223
    const val SHIPYARD_LARGE_SELECTION_BG_W: Int = 48
    const val SHIPYARD_LARGE_SELECTION_BG_H: Int = 30

    /** Material selection icon.  */
    const val SHIPYARD_LARGE_SELECTION_ICON_U: Int = 208
    const val SHIPYARD_LARGE_SELECTION_ICON_V: Int = 64
    const val SHIPYARD_LARGE_SELECTION_ICON_W: Int = 18
    const val SHIPYARD_LARGE_SELECTION_ICON_H: Int = 18

    /** Inventory mode indicator (toggle).  */
    const val SHIPYARD_LARGE_INV_MODE_ICON_U: Int = 208
    const val SHIPYARD_LARGE_INV_MODE_ICON_V: Int = 82
    const val SHIPYARD_LARGE_INV_MODE_ICON_W: Int = 25
    const val SHIPYARD_LARGE_INV_MODE_ICON_H: Int = 20

    // ---- Small shipyard texture ----
    /** Build type indicator icon U and size.  */
    const val SHIPYARD_SMALL_BUILD_ICON_U: Int = 176
    const val SHIPYARD_SMALL_BUILD_ICON_W: Int = 18
    const val SHIPYARD_SMALL_BUILD_ICON_H: Int = 18

    // ---- Desk textures (book / desk / radar / nameicon0) ----
    /** Book prev-page button.  */
    const val DESK_BOOK_PREV_BTN_U: Int = 0
    const val DESK_BOOK_PREV_BTN_V: Int = 192
    const val DESK_BOOK_PREV_BTN_W: Int = 18
    const val DESK_BOOK_PREV_BTN_H: Int = 10

    /** Book next-page button.  */
    const val DESK_BOOK_NEXT_BTN_U: Int = 0
    const val DESK_BOOK_NEXT_BTN_V: Int = 202
    const val DESK_BOOK_NEXT_BTN_W: Int = 18
    const val DESK_BOOK_NEXT_BTN_H: Int = 10

    /** Radar selected ship row highlight.  */
    const val DESK_RADAR_SELECTED_ROW_U: Int = 68
    const val DESK_RADAR_SELECTED_ROW_V: Int = 192
    const val DESK_RADAR_SELECTED_ROW_W: Int = 108
    const val DESK_RADAR_SELECTED_ROW_H: Int = 31

    /** Radar action button.  */
    const val DESK_RADAR_ACTION_BTN_U: Int = 24
    const val DESK_RADAR_ACTION_BTN_V: Int = 216
    const val DESK_RADAR_ACTION_BTN_W: Int = 44
    const val DESK_RADAR_ACTION_BTN_H: Int = 10

    // ---- Helper methods ----
    /** Shorthand for `ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/" + path)`.  */
    fun gui(path: String?): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/gui/" + path)
    }

    /** Shorthand for `ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, path)`.  */
    fun rl(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, path)
    }
}
