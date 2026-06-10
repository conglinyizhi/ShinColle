package org.trp.shincolle.entity

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ShipSpawnEggStateResetTest {
    private val SOURCE =
        Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt")

    @Test
    fun shipDeathSpawnEggShouldResetHealthAndDeathTimers() {
        val source = Files.readString(SOURCE)

        assertTrue(source.contains("shipTag.putFloat(\"Health\", this.maxHealth)")) {
            "Preserved spawn eggs should restore Health to max before serialization"
        }
        assertTrue(source.contains("shipTag.putShort(\"DeathTime\", 0.toShort())")) {
            "Preserved spawn eggs should clear DeathTime before reuse"
        }
        assertTrue(source.contains("shipTag.putShort(\"HurtTime\", 0.toShort())")) {
            "Preserved spawn eggs should clear HurtTime before reuse"
        }
    }
}
