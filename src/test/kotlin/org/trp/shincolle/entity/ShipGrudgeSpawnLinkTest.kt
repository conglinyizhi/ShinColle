package org.trp.shincolle.entity

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ShipGrudgeSpawnLinkTest {
    private val SOURCE =
        Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt")

    @Test
    fun shipDeathShouldKeepSpawningShipGrudgeWithPreservedEggAndOwner() {
        val source = Files.readString(SOURCE)

        assertTrue(source.contains("private fun spawnShipGrudge() {")) {
            "Ship death flow should keep the dedicated Ship Grudge spawn helper"
        }
        assertTrue(source.contains("val spawnEgg = createShipSpawnEggStack()")) {
            "Ship Grudge spawning should keep reusing the preserved spawn-egg payload"
        }
        assertTrue(source.contains("val grudge = EntityShipGrudge(\n            this.level(), this.x, this.y + 0.5,\n            this.z, spawnEgg, this.ownerUUID\n        )")) {
            "Ship Grudge spawning should keep forwarding owner UUID and the preserved egg stack"
        }
        assertTrue(source.contains("this.level().addFreshEntity(grudge)")) {
            "Ship death flow should keep publishing Ship Grudge back into the level"
        }
    }
}
