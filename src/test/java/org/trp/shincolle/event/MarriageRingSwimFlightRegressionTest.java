package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarriageRingSwimFlightRegressionTest {
    private static final Path CONFIG_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/Config.java");
    private static final Path RING_SERVICE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/server/MarriageRingService.java");
    private static final Path PLAYER_STATE_SERVICE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/server/PlayerStateService.java");
    private static final Path ADMIRAL_DATA_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/attachment/AdmiralData.java");

    @Test
    void swimFlightAbilityShouldNoLongerBeMarkedAsParityOnly() throws IOException {
        String config = Files.readString(CONFIG_SOURCE);

        assertFalse(config.contains("Kept for config parity, no direct NeoForge flight mode yet"),
                "Swim-flight config should no longer advertise the feature as unmigrated");
    }

    @Test
    void swimFlightAbilityShouldBeHandledThroughMarriageRingService() throws IOException {
        String ringService = Files.readString(RING_SERVICE_SOURCE);
        String playerState = Files.readString(PLAYER_STATE_SERVICE_SOURCE);
        String admiralData = Files.readString(ADMIRAL_DATA_SOURCE);

        assertTrue(ringService.contains("if (Config.ringAbilitySwimFlight >= 0 && marriedCount >= Config.ringAbilitySwimFlight)"),
                "Marriage ring tick abilities should still honor the legacy swim-flight threshold");
        assertTrue(ringService.contains("if (player.isInWaterOrBubble()) {\n                enableRingFlight(player);"),
                "Swim-flight should enable while the ring owner stays in water");
        assertTrue(ringService.contains("player.getAbilities().mayfly = true;"),
                "Swim-flight should grant temporary flight permission");
        assertTrue(ringService.contains("player.getAbilities().flying = true;"),
                "Swim-flight should immediately lift the player into flying state like the legacy ring");
        assertTrue(ringService.contains("serverPlayer.onUpdateAbilities();"),
                "Swim-flight ability changes should be synced to the client");
        assertTrue(ringService.contains("if (!player.getAbilities().instabuild) {\n            player.getAbilities().flying = false;\n            player.getAbilities().mayfly = false;"),
                "Swim-flight teardown should not remove creative flight");

        assertTrue(playerState.contains("public static boolean isRingFlightActive(Player player)"),
                "PlayerStateService should own the ring-flight state boundary");
        assertTrue(playerState.contains("public static void setRingFlightActive(Player player, boolean active)"),
                "PlayerStateService should own ring-flight state writes");
        assertTrue(admiralData.contains("private boolean ringFlightActive = false;"),
                "AdmiralData should persist whether flight was granted by the marriage ring");
    }
}
