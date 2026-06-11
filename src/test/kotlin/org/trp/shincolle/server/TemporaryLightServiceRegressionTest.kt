package org.trp.shincolle.server

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TemporaryLightServiceRegressionTest {
    private val SERVICE_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/server/TemporaryLightService.kt")
    private val EVENT_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/event/ModEventBusEvents.kt")
    private val SHIP_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt")

    @Test
    fun temporaryLightServiceShouldUseMinecraftLightBlocksWithLegacyLifetime() {
        val service = Files.readString(SERVICE_SOURCE)

        assertTrue(service.contains("Blocks.LIGHT")) {
            "Temporary light service should use minecraft:light blocks for runtime illumination"
        }
        assertTrue(service.contains("const val TEMP_LIGHT_LIFETIME_TICKS: Int = 120")) {
            "Temporary light service should keep the legacy 120-tick lifetime"
        }
        assertTrue(service.contains("setValue(LightBlock.LEVEL, DEFAULT_LIGHT_LEVEL)")) {
            "Temporary light service should refresh active lights to full brightness"
        }
        assertTrue(service.contains("serverLevel.removeBlock(pos, false)")) {
            "Temporary light service should remove expired temporary light blocks"
        }
    }

    @Test
    fun shipEquipLightingShouldRefreshAndServerTickShouldCleanUp() {
        val eventSource = Files.readString(EVENT_SOURCE)
        val shipSource = Files.readString(SHIP_SOURCE)

        assertTrue(eventSource.contains("TemporaryLightService.tick(serverLevel)")) {
            "Server tick post hook should advance temporary light cleanup"
        }
        assertTrue(shipSource.contains("TemporaryLightService.refreshLight(serverLevel, target.blockPosition(), this.uuid)")) {
            "Flare targets should refresh temporary light blocks at the target position"
        }
        assertTrue(shipSource.contains("TemporaryLightService.refreshLight(serverLevel, this.blockPosition(), this.uuid)")) {
            "Searchlight self-light should refresh temporary light blocks at the ship position"
        }
    }
}
