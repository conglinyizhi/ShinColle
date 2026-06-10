package org.trp.shincolle.entity

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ShipSpawnEggLoadRecoveryTest {
    private val SOURCE =
        Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBaseSerialization.kt")

    @Test
    fun shipLoadedFromSpawnEggShouldResetDeathState() {
        val source = Files.readString(SOURCE)

        assertTrue(source.contains("if (compound.getBoolean(EntityShipBase.spawnEggTagName)) {")) {
            "Spawn-egg restoration should keep checking the preserved spawn-egg marker"
        }
        assertTrue(source.contains("this.ship.resetDeathStateForSpawnEgg()")) {
            "Spawn-egg restoration should keep resetting death state after load"
        }
    }
}
