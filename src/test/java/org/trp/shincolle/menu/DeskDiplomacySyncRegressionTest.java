package org.trp.shincolle.menu;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DeskDiplomacySyncRegressionTest {
    private static final Path TEAM_DIPLOMACY_SERVICE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/server/TeamDiplomacyService.java");

    @Test
    void diplomacySyncShouldFallbackToPlayerNamesWhenDisplayDataIsMissing() throws IOException {
        String serviceSource = Files.readString(TEAM_DIPLOMACY_SERVICE_SOURCE);

        assertTrue(serviceSource.contains("if (leaderName.isBlank()) {\n                leaderName = resolveDiplomacyLeaderName(player, target);\n            }"),
                "Desk diplomacy sync should fill blank leader names from live/server profile data");
        assertTrue(serviceSource.contains("private static String resolveDiplomacyLeaderName(ServerPlayer player, UUID target) {"),
                "Desk diplomacy sync should keep a dedicated leader-name fallback helper");
        assertTrue(serviceSource.contains("player.server.getPlayerList().getPlayer(target)"),
                "Desk diplomacy sync should check online player names before profile cache");
        assertTrue(serviceSource.contains("player.server.getProfileCache()"),
                "Desk diplomacy sync should fall back to the server profile cache for offline players");
    }
}
