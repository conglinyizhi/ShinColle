package org.trp.shincolle.menu

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

import org.junit.jupiter.api.Assertions.assertTrue

class ShipTaskSideButtonRegressionTest {
    private val MENU_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/menu/ShipContainerMenu.kt")
    private val SCREEN_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/client/screen/ShipInventoryScreen.kt")
    private val INVENTORY_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/inventory/ShipInventoryHandler.kt")

    @Test
    fun taskSideButtonsShouldOnlyCoverTheLegacyEighteenSideBits() {
        val menu = Files.readString(MENU_SOURCE)
        val screen = Files.readString(SCREEN_SOURCE)

        assertTrue(screen.contains("for (int i = 0; i < 18; i++) {")) {
            "Task-side UI should only send the legacy 18 side bits"
        }
        assertTrue(menu.contains("if (id >= ACTION_SIDE_TOGGLE_BASE && id < ACTION_SIDE_TOGGLE_BASE + 18) {")) {
            "Task-side button handling should stop before bit 18 so meta/ore/nbt toggles stay separate"
        }
        assertTrue(menu.contains("ship.setStateMinor(STATE_MINOR_TASK_SIDE, ship.getStateMinor(STATE_MINOR_TASK_SIDE) ^ (1 << 18));")) {
            "Metadata matching toggle should remain a dedicated task-side flag"
        }
        assertTrue(menu.contains("ship.setStateMinor(STATE_MINOR_TASK_SIDE, ship.getStateMinor(STATE_MINOR_TASK_SIDE) ^ (1 << 19));")) {
            "Ore matching toggle should remain a dedicated task-side flag"
        }
        assertTrue(menu.contains("ship.setStateMinor(STATE_MINOR_TASK_SIDE, ship.getStateMinor(STATE_MINOR_TASK_SIDE) ^ (1 << 20));")) {
            "NBT matching toggle should remain a dedicated task-side flag"
        }
    }

    @Test
    fun craftingTaskUiShouldReflectLegacyWorkingSlotOccupancy() {
        val screen = Files.readString(SCREEN_SOURCE)

        assertTrue(screen.contains("private static final int CRAFTING_WORK_START_SLOT = 12;")) {
            "Crafting task UI should use the legacy 12..20 working-slot range"
        }
        assertTrue(screen.contains("boolean occupied = !this.menu.getShip().getInventory().getStackInSlot(CRAFTING_WORK_START_SLOT + i).isEmpty();")) {
            "Crafting task UI should inspect the actual working-slot occupancy"
        }
        assertTrue(screen.contains("occupied ? Sprites.SHIP_INV_SLOT_OCCUPIED_U : Sprites.SHIP_INV_SLOT_OVERLAY_U, Sprites.SHIP_INV_SLOT_OVERLAY_V")) {
            "Crafting task UI should preserve the legacy empty/full background distinction"
        }
    }

    @Test
    fun aiSettingsTabsShouldKeepTheLegacyTwelvePageStructure() {
        val screen = Files.readString(SCREEN_SOURCE)

        assertTrue(screen.contains("private static final int SETTINGS_TAB_12 = 12;")) {
            "AI settings UI should keep the legacy twelve-page tab range"
        }
        assertTrue(screen.contains("for (int tab = SETTINGS_TAB_1; tab <= SETTINGS_TAB_12; tab++) {")) {
            "AI settings tab clicks should still cover legacy pages 1 through 12"
        }
        assertTrue(screen.contains("int tab = Math.max(SETTINGS_TAB_1, Math.min(SETTINGS_TAB_12, this.activeSettingsTab));")) {
            "AI settings page indicator should clamp against the legacy twelve-page range"
        }
    }

    @Test
    fun shipInventoryShiftClickShouldPreferEquipSlotsForEquipItems() {
        val menu = Files.readString(MENU_SOURCE)
        val inventory = Files.readString(INVENTORY_SOURCE)

        assertTrue(inventory.contains("public static TagKey<Item> getEquipItemsTag() {")) {
            "Ship inventory handler should expose the equip tag for menu-side routing checks"
        }
        assertTrue(menu.contains("boolean shipEquipCandidate = stack.getItem() instanceof org.trp.shincolle.item.LegacyEquipItem")) {
            "Ship inventory shift-click should identify equip candidates the same way slot validation does"
        }
        assertTrue(menu.contains("if (index >= EQUIP_SLOTS && shipEquipCandidate) {")) {
            "Shift-clicking from ship storage should special-case equip items"
        }
        assertTrue(menu.contains("if (!this.moveItemStackTo(stack, 0, EQUIP_SLOTS, false)\n                            && !this.moveItemStackTo(stack, FIRST_PLAYER_SLOT, END_PLAYER_SLOT, true)) {")) {
            "Equip items from ship storage should try equip slots before falling back to the player inventory"
        }
    }
}
