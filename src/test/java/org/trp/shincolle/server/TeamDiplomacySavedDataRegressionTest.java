package org.trp.shincolle.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TeamDiplomacySavedDataRegressionTest {
    private static final Path TEAM_DIPLOMACY_SAVED_DATA =
            Path.of("src/main/java/org/trp/shincolle/server/TeamDiplomacySavedData.java");
    private static final Path TEAM_DIPLOMACY_SERVICE =
            Path.of("src/main/java/org/trp/shincolle/server/TeamDiplomacyService.java");

    @Test
    void diplomacyDisplayDataShouldOnlyMarkSavedDataDirtyAfterRealChanges() throws IOException {
        String source = Files.readString(TEAM_DIPLOMACY_SAVED_DATA);

        assertTrue(source.contains("public boolean setDisplayData(UUID owner, String teamName, String leaderName) {"),
                "TeamDiplomacySavedData should report whether display metadata actually changed");
        assertTrue(source.contains("if (owner == null) {\n            return false;\n        }"),
                "TeamDiplomacySavedData should reject null owners before touching diplomacy state");
        assertTrue(source.contains("String nextTeamName = teamName == null ? \"\" : teamName;"),
                "Diplomacy display metadata should normalize null team names before comparison");
        assertTrue(source.contains("String nextLeaderName = leaderName == null ? \"\" : leaderName;"),
                "Diplomacy display metadata should normalize null leader names before comparison");
        assertTrue(source.contains("if (entry.teamName.equals(nextTeamName) && entry.leaderName.equals(nextLeaderName)) {\n            return false;\n        }"),
                "Diplomacy display metadata should reject no-op writes before marking SavedData dirty");
        assertTrue(source.contains("entry.teamName = nextTeamName;"),
                "Diplomacy display metadata should persist the normalized team name when it really changes");
        assertTrue(source.contains("entry.leaderName = nextLeaderName;"),
                "Diplomacy display metadata should persist the normalized leader name when it really changes");
        assertTrue(source.contains("setDirty();\n        return true;"),
                "Diplomacy display metadata should only mark SavedData dirty after a real change");
    }

    @Test
    void diplomacyServiceShouldRefreshDisplayMetadataThroughSavedDataBeforeSync() throws IOException {
        String source = Files.readString(TEAM_DIPLOMACY_SERVICE);

        assertTrue(source.contains("updateDiplomacyDisplayData(player, diplomacy);"),
                "Desk diplomacy sync should continue refreshing display metadata through the service");
        assertTrue(source.contains("String teamName = data.getTeamName(data.getCurrentTeamID());"),
                "Diplomacy display metadata should still come from the player's current team name");
        assertTrue(source.contains("String leaderName = player.getName().getString();"),
                "Diplomacy display metadata should still come from the current player name");
        assertTrue(source.contains("diplomacy.setDisplayData(player.getUUID(), teamName, leaderName);"),
                "Diplomacy service should route display metadata updates through TeamDiplomacySavedData");
    }

    @Test
    void diplomacyEntryViewsShouldStayReadOnly() throws IOException {
        String source = Files.readString(TEAM_DIPLOMACY_SAVED_DATA);

        assertTrue(source.contains("return java.util.Collections.unmodifiableSet(this.allies);"),
                "Diplomacy ally views should stay read-only so callers cannot bypass SavedData dirty tracking");
        assertTrue(source.contains("return java.util.Collections.unmodifiableSet(this.banned);"),
                "Diplomacy banned views should stay read-only so callers cannot bypass SavedData dirty tracking");
    }
}
