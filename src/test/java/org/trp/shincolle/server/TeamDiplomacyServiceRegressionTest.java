package org.trp.shincolle.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TeamDiplomacyServiceRegressionTest {
    private static final Path TEAM_DIPLOMACY_SERVICE =
            Path.of("src/main/java/org/trp/shincolle/server/TeamDiplomacyService.java");

    @Test
    void diplomacyActionsShouldOnlySyncDeskStateAfterRealRelationChanges() throws IOException {
        String source = Files.readString(TEAM_DIPLOMACY_SERVICE);

        assertTrue(source.contains("boolean changed;"),
                "TeamDiplomacyService should explicitly track whether a diplomacy action changed saved data");
        assertTrue(source.contains("changed = diplomacy.addAlly(owner, target);"),
                "Adding allies should report whether the diplomacy state really changed");
        assertTrue(source.contains("changed = diplomacy.removeAlly(owner, target);"),
                "Removing allies should report whether the diplomacy state really changed");
        assertTrue(source.contains("changed = diplomacy.addBanned(owner, target);"),
                "Adding hostile targets should report whether the diplomacy state really changed");
        assertTrue(source.contains("changed = diplomacy.removeBanned(owner, target);"),
                "Removing hostile targets should report whether the diplomacy state really changed");
        assertTrue(source.contains("if (changed && player instanceof ServerPlayer serverPlayer) {\n            sendDeskDiplomacySync(serverPlayer);\n        }"),
                "Desk diplomacy sync should only be sent after a real relation change");
    }
}
