package org.trp.shincolle.network;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PayloadPlayerGuardRegressionTest {
    private static final Path NETWORK_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/network/ModNetwork.java");

    private static final List<String> GUARDED_HANDLERS = List.of(
            "handleBookState",
            "handleDeskGui",
            "handleWaypointAction",
            "handlePointerAction",
            "handleFormationAction",
            "handleDeskOpenShip",
            "handleDeskSummon",
            "handleTeamDiplomacy"
    );

    @Test
    void c2sPayloadHandlersShouldGuardAgainstMissingServerPlayer() throws IOException {
        String network = Files.readString(NETWORK_SOURCE);

        for (String handler : GUARDED_HANDLERS) {
            assertTrue(network.contains("private static void " + handler),
                    handler + " should remain a dedicated ModNetwork handler");
        }
    }
}
