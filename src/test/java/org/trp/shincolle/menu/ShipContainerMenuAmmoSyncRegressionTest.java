package org.trp.shincolle.menu;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipContainerMenuAmmoSyncRegressionTest {
    private static final Path MENU_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/menu/ShipContainerMenu.kt");

    @Test
    void menuClickPathShouldRecalculateShipInventoryDerivedStats() throws IOException {
        String source = Files.readString(MENU_SOURCE);
        assertTrue(source.contains("public void clicked(int slotId, int button, ClickType clickType, Player player)"),
                "Ship container menu should keep an explicit clicked override");
        assertTrue(source.contains("this.ship.onInventoryChanged();"),
                "Menu click handling should trigger ship inventory recalculation");
    }

    @Test
    void broadcastChangesShouldRefreshDerivedAmmoCounts() throws IOException {
        String source = Files.readString(MENU_SOURCE);
        assertTrue(source.contains("public void broadcastChanges() {"),
                "Ship container menu should keep a broadcastChanges override");
        assertTrue(source.contains("if (!this.ship.level().isClientSide) {\n            this.ship.onInventoryChanged();\n        }\n        super.broadcastChanges();"),
                "Broadcast changes should refresh ship-derived counters before syncing");
    }

    @Test
    void pagedShipSlotsShouldRefreshOnSetInsertAndTake() throws IOException {
        String source = Files.readString(MENU_SOURCE);
        assertTrue(source.contains("ship.getInventory().setStackInSlot(idx, stack);\n            setChanged();"),
                "Paged ship slot set() should route through setChanged");
        assertTrue(source.contains("ship.getInventory().setStackInSlot(idx, inserted);"),
                "Paged ship slot safeInsert() should write back to ship inventory");
        assertTrue(source.contains("ItemStack taken = super.safeTake(count, decrement, player);"),
                "Paged ship slot safeTake() should keep an explicit take hook");
        assertTrue(source.contains("if (!taken.isEmpty() && !ship.level().isClientSide) {\n                ship.onInventoryChanged();\n            }"),
                "Paged ship slot safeTake() should refresh derived counts after removals");
        assertTrue(source.contains("public void setChanged() {\n            if (!ship.level().isClientSide) {\n                ship.onInventoryChanged();\n            }\n            super.setChanged();\n        }"),
                "Paged ship slot setChanged() should refresh ship-derived counters");
    }
}
