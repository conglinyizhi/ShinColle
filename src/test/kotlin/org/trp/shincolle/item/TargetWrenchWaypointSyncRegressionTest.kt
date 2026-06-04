package org.trp.shincolle.item

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class TargetWrenchWaypointSyncRegressionTest {
    @Test
    fun waypointPayloadShouldBeHandledByServiceAndWriteBlockStateServerSide() {
        val network = Files.readString(NETWORK_SOURCE)
        val waypointService = Files.readString(WAYPOINT_SERVICE_SOURCE)

        assertTrue(network.contains("C2SWaypointActionPayload.TYPE"))
        assertTrue(network.contains("WaypointService.handleAction(player, payload.action(), pos1, pos2);"))
        assertTrue(waypointService.contains("wpFrom.setNextPos(to);"))
        assertTrue(waypointService.contains("wpTo.setLastPos(from);"))
        assertTrue(waypointService.contains("waypoint.setChestPos(containerPos);"))
    }

    companion object {
        private val NETWORK_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/network/ModNetwork.java")
        private val WAYPOINT_SERVICE_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/server/WaypointService.java")
    }
}
