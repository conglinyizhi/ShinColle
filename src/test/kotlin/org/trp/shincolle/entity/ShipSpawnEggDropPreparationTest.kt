package org.trp.shincolle.entity

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ShipSpawnEggDropPreparationTest {
    private val SOURCE =
        Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt")

    @Test
    fun shipDeathSpawnEggShouldKeepNoExpAndGlintPreparation() {
        val source = Files.readString(SOURCE)

        assertTrue(source.contains("private fun createShipSpawnEggStack(): ItemStack {")) {
            "Ship death flow should keep the preserved spawn egg builder"
        }
        assertTrue(source.contains("shipTag.putBoolean(spawnEggTagName, true)")) {
            "Preserved spawn eggs should keep the legacy spawn-egg marker"
        }
        assertTrue(source.contains("if (this.isHostileShipMob) {\n            shipTag.putBoolean(TAG_SPAWN_EGG_NO_EXP, true)\n        }")) {
            "Hostile ships should keep marking preserved spawn eggs as no-exp-cost"
        }
        assertTrue(source.contains("egg.set<CustomData?>(DataComponents.ENTITY_DATA, CustomData.of(shipTag))")) {
            "Preserved spawn eggs should keep writing full entity data into ENTITY_DATA"
        }
        assertTrue(source.contains("egg.set<Boolean?>(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)")) {
            "Preserved spawn eggs should keep the glint override for visual distinction"
        }
    }
}
