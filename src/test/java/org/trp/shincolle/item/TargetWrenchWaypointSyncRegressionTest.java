package org.trp.shincolle.item;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TargetWrenchWaypointSyncRegressionTest {
    private static final Path TARGET_WRENCH_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/item/TargetWrenchItem.java");
    private static final Path NETWORK_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/network/ModNetwork.java");
    private static final Path WAYPOINT_SERVICE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/server/WaypointService.java");

    @Test
    void waypointPairingShouldSendPayloadAndClearMarkedStateOnBothSides() throws IOException {
        String targetWrench = Files.readString(TARGET_WRENCH_SOURCE);

        assertTrue(targetWrench.contains("PacketDistributor.sendToServer(new C2SWaypointActionPayload("),
                "Second waypoint/container click should send a server payload instead of only changing client state");
        assertTrue(targetWrench.contains("if (level.isClientSide) {\n            PacketDistributor.sendToServer(new C2SWaypointActionPayload("),
                "Waypoint pairing should still send the logical-side payload from the client branch");
    }

    @Test
    void waypointPayloadShouldBeHandledByServiceAndWriteBlockStateServerSide() throws IOException {
        String network = Files.readString(NETWORK_SOURCE);
        String waypointService = Files.readString(WAYPOINT_SERVICE_SOURCE);

        assertTrue(network.contains("C2SWaypointActionPayload.TYPE"),
                "Waypoint action payload should be registered");
        assertTrue(network.contains("WaypointService.handleAction(player, payload.action(), pos1, pos2);"),
                "Network handler should delegate waypoint business logic to the service layer");
        assertTrue(waypointService.contains("wpFrom.setNextPos(to);"),
                "Waypoint-to-waypoint pairing should write through the waypoint block entity setter");
        assertTrue(waypointService.contains("wpTo.setLastPos(from);"),
                "Reverse waypoint link should write through the waypoint block entity setter");
        assertTrue(waypointService.contains("waypoint.setChestPos(containerPos);"),
                "Waypoint-to-container pairing should write through the waypoint block entity setter");
    }
}
