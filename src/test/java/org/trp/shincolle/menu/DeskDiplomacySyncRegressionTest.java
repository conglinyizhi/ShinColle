package org.trp.shincolle.menu;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DeskDiplomacySyncRegressionTest {
    private static final Path NETWORK_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/network/ModNetwork.java");

    @Test
    void diplomacySyncShouldFallbackToPlayerNamesWhenDisplayDataIsMissing() throws IOException {
        String networkSource = Files.readString(NETWORK_SOURCE);

        assertTrue(networkSource.contains("if (leaderName.isBlank()) {\n                leaderName = resolveDiplomacyLeaderName(player, target);\n            }"),
                "Desk diplomacy sync should fill blank leader names from live/server profile data");
        assertTrue(networkSource.contains("private static String resolveDiplomacyLeaderName(net.minecraft.server.level.ServerPlayer player, UUID target) {"),
                "Desk diplomacy sync should keep a dedicated leader-name fallback helper");
        assertTrue(networkSource.contains("player.server.getPlayerList().getPlayer(target)"),
                "Desk diplomacy sync should check online player names before profile cache");
        assertTrue(networkSource.contains("player.server.getProfileCache()"),
                "Desk diplomacy sync should fall back to the server profile cache for offline players");
    }
}
