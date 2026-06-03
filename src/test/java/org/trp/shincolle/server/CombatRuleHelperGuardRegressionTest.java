package org.trp.shincolle.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatRuleHelperGuardRegressionTest {
    private static final Path TEAM_DIPLOMACY_SERVICE =
            Path.of("src/main/java/org/trp/shincolle/server/TeamDiplomacyService.java");
    private static final Path TARGET_PROTECTION_SERVICE =
            Path.of("src/main/java/org/trp/shincolle/server/TargetProtectionService.java");

    @Test
    void diplomacyAndTargetProtectionHelpersShouldGuardNullRuleInputs() throws IOException {
        String diplomacySource = Files.readString(TEAM_DIPLOMACY_SERVICE);
        String protectionSource = Files.readString(TARGET_PROTECTION_SERVICE);

        assertTrue(diplomacySource.contains("public static boolean isDiplomaticAlly(EntityShipBase ship, Entity target) {\n        if (ship == null || target == null) {\n            return false;\n        }"),
                "TeamDiplomacyService ally checks should reject null ship/target inputs");
        assertTrue(diplomacySource.contains("public static boolean isDiplomaticBanned(EntityShipBase ship, Entity target) {\n        if (ship == null || target == null) {\n            return false;\n        }"),
                "TeamDiplomacyService hostile checks should reject null ship/target inputs");
        assertTrue(protectionSource.contains("public static boolean isUnattackableTargetClass(EntityShipBase ship, LivingEntity target) {\n        if (ship == null || target == null) {\n            return false;\n        }"),
                "TargetProtectionService unattackable-target checks should reject null ship/target inputs");
        assertTrue(protectionSource.contains("public static boolean isPlayerConfiguredTargetClass(EntityShipBase ship, Entity target) {\n        if (ship == null || target == null) {\n            return false;\n        }"),
                "TargetProtectionService player target-list checks should reject null ship/target inputs");
    }
}
