package org.trp.shincolle.event

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class ShipAmmoAutosupplyRegressionTest {
    private val SHIP_BASE: Path =
        Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt")

    @Test
    fun autoSupplyShouldStillConsumeFuelItems() {
        val source = Files.readString(SHIP_BASE)
        assertTrue(source.contains("if (this.getFuel() <= 0) {")) {
            "Auto-supply should still handle fuel replenishment"
        }
        assertTrue(source.contains("this.consumeItemInInventory(ModItems.GRUDGE.get())")) {
            "Auto-supply should still consume grudge items for fuel"
        }
    }

    @Test
    fun autoSupplyShouldNotConvertAmmoItemsIntoHiddenAmmoPools() {
        val source = Files.readString(SHIP_BASE)
        assertFalse(source.contains("this.consumeItemInInventory(ModItems.AMMO_LIGHT.get())")) {
            "Ammo auto-supply should not secretly consume light ammo items anymore"
        }
        assertFalse(source.contains("this.consumeItemInInventory(ModItems.AMMO_LIGHT_CONTAINER.get())")) {
            "Ammo auto-supply should not secretly consume light ammo containers anymore"
        }
        assertFalse(source.contains("this.consumeItemInInventory(ModItems.AMMO_HEAVY.get())")) {
            "Ammo auto-supply should not secretly consume heavy ammo items anymore"
        }
        assertFalse(source.contains("this.consumeItemInInventory(ModItems.AMMO_HEAVY_CONTAINER.get())")) {
            "Ammo auto-supply should not secretly consume heavy ammo containers anymore"
        }
        assertFalse(source.contains("this.setAmmoLight((int) (30 * modAmmo))")) {
            "Ammo auto-supply should not rebuild light ammo from a hidden conversion path"
        }
        assertFalse(source.contains("this.setAmmoHeavy((int) (15 * modAmmo))")) {
            "Ammo auto-supply should not rebuild heavy ammo from a hidden conversion path"
        }
    }
}
