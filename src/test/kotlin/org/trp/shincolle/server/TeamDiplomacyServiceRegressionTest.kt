package org.trp.shincolle.server

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue

class TeamDiplomacyServiceRegressionTest {
    private val TEAM_DIPLOMACY_SERVICE: Path =
            Path.of("src/main/java/org/trp/shincolle/server/TeamDiplomacyService.kt")

    @Test
    fun diplomacyActionsShouldOnlySyncDeskStateAfterRealRelationChanges() {
        val source: String = Files.readString(TEAM_DIPLOMACY_SERVICE)

        assertTrue(source.contains("boolean changed = applyDiplomacyAction(diplomacy, owner, action, target);")) {
            "TeamDiplomacyService should centralize diplomacy action dispatch before deciding whether to sync"
        }
        assertTrue(source.contains("static boolean applyDiplomacyAction(TeamDiplomacySavedData diplomacy, UUID owner, int action, UUID target)")) {
            "TeamDiplomacyService should expose a shared dispatch helper for executable tests"
        }
        assertTrue(source.contains("if (changed && player instanceof ServerPlayer serverPlayer) {\n            sendDeskDiplomacySync(serverPlayer);\n        }")) {
            "Desk diplomacy sync should only be sent after a real relation change"
        }
    }
}
