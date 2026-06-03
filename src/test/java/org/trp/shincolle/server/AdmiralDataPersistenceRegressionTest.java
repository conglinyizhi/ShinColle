package org.trp.shincolle.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AdmiralDataPersistenceRegressionTest {
    private static final Path ADMIRAL_DATA_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/attachment/AdmiralData.java");

    @Test
    void admiralDataShouldPersistRosterSelectionAndMarriageStateFields() throws IOException {
        String source = Files.readString(ADMIRAL_DATA_SOURCE);

        assertTrue(source.contains("private final UUID[][] teams = new UUID[TEAM_COUNT][SLOT_COUNT];"),
                "AdmiralData should persist per-team roster assignments");
        assertTrue(source.contains("private final boolean[][] selectionStates = new boolean[TEAM_COUNT][SLOT_COUNT];"),
                "AdmiralData should persist per-slot pointer selection state");
        assertTrue(source.contains("private final int[] formationIDs = new int[TEAM_COUNT];"),
                "AdmiralData should persist team formation ids");
        assertTrue(source.contains("private final String[] teamNames = new String[TEAM_COUNT];"),
                "AdmiralData should persist custom team names");
        assertTrue(source.contains("private boolean hasReceivedBook = false;"),
                "AdmiralData should persist whether the player already received the starter manual");
        assertTrue(source.contains("private int marriedShipCount = 0;"),
                "AdmiralData should persist the owned married ship counter");
        assertTrue(source.contains("private boolean ringFlightActive = false;"),
                "AdmiralData should persist the marriage ring flight toggle");
        assertTrue(source.contains("slotTag.putBoolean(\"Selected\", selectionStates[i][j]);"),
                "Serialized roster slots should keep pointer selection state");
        assertTrue(source.contains("teamTag.putInt(\"Formation\", formationIDs[i]);"),
                "Serialized teams should keep their formation id");
        assertTrue(source.contains("teamTag.putString(\"Name\", teamNames[i]);"),
                "Serialized teams should keep their custom display names");
        assertTrue(source.contains("nbt.putBoolean(\"HasReceivedBook\", hasReceivedBook);"),
                "Serialized admiral data should keep the starter manual flag");
        assertTrue(source.contains("nbt.putInt(\"MarriedShipCount\", marriedShipCount);"),
                "Serialized admiral data should keep the married ship counter");
        assertTrue(source.contains("nbt.putBoolean(\"RingFlightActive\", ringFlightActive);"),
                "Serialized admiral data should keep the ring flight toggle");
        assertTrue(source.contains("selectionStates[i][j] = slotTag.getBoolean(\"Selected\");"),
                "Deserialized roster slots should restore pointer selection state");
        assertTrue(source.contains("formationIDs[i] = teamTag.getInt(\"Formation\");"),
                "Deserialized teams should restore formation ids");
        assertTrue(source.contains("teamNames[i] = teamTag.getString(\"Name\");"),
                "Deserialized teams should restore custom names");
        assertTrue(source.contains("hasReceivedBook = nbt.getBoolean(\"HasReceivedBook\");"),
                "Deserialized admiral data should restore the starter manual flag");
        assertTrue(source.contains("marriedShipCount = Math.max(0, nbt.getInt(\"MarriedShipCount\"));"),
                "Deserialized admiral data should clamp and restore the married ship counter");
        assertTrue(source.contains("ringFlightActive = nbt.getBoolean(\"RingFlightActive\");"),
                "Deserialized admiral data should restore the ring flight toggle");
    }

    @Test
    void admiralDataShouldSanitizeInvalidRestoredState() throws IOException {
        String source = Files.readString(ADMIRAL_DATA_SOURCE);

        assertTrue(source.contains("currentTeamID = Math.max(0, Math.min(TEAM_COUNT - 1, currentTeamID));"),
                "Restored current team ids should clamp into the valid team range");
        assertTrue(source.contains("if (teamNames[i] == null || teamNames[i].isBlank()) {"),
                "Restored team names should fall back to legacy defaults when missing");
        assertTrue(source.contains("teamNames[i] = \"Team \" + (i + 1);"),
                "Blank restored team names should sanitize back to numbered legacy defaults");
        assertTrue(source.contains("formationIDs[i] = Math.max(0, formationIDs[i]);"),
                "Restored formation ids should sanitize back to non-negative values");
        assertTrue(source.contains("selectionStates[i][j] = true;"),
                "Fresh admiral data should default all pointer selection states to enabled");
    }
}
