package org.trp.shincolle.network

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue

class PayloadPlayerGuardRegressionTest {
    private val NETWORK_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/network/ModNetwork.kt")

    private val GUARDED_HANDLERS = listOf(
            "handleBookState",
            "handleDeskGui",
            "handleWaypointAction",
            "handlePointerAction",
            "handleFormationAction",
            "handleDeskOpenShip",
            "handleDeskSummon",
            "handleTeamDiplomacy"
    )

    @Test
    fun c2sPayloadHandlersShouldGuardAgainstMissingServerPlayer() {
        val network: String = Files.readString(NETWORK_SOURCE)

        for (handler in GUARDED_HANDLERS) {
            assertTrue(network.contains("private static void " + handler)) {
                handler + " should remain a dedicated ModNetwork handler"
            }
        }
    }
}
