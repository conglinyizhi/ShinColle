package org.trp.shincolle.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TargetProtectionServiceRegressionTest {
    private static final Path TARGET_PROTECTION_SERVICE =
            Path.of("src/main/java/org/trp/shincolle/server/TargetProtectionService.java");

    @Test
    void targetProtectionEntrypointsShouldGuardNullPlayers() throws IOException {
        String source = Files.readString(TARGET_PROTECTION_SERVICE);

        assertTrue(source.contains("public static void toggleUnattackableTarget(Player player, Entity entity) {\n        if (player == null) {\n            return;\n        }"),
                "TargetProtectionService should ignore null players before toggling unattackable targets");
        assertTrue(source.contains("public static void showUnattackableTargets(Player player) {\n        if (player == null) {\n            return;\n        }"),
                "TargetProtectionService should ignore null players before listing unattackable targets");
        assertTrue(source.contains("public static void togglePlayerTarget(Player player, Entity entity) {\n        if (player == null) {\n            return;\n        }"),
                "TargetProtectionService should ignore null players before toggling player target rules");
        assertTrue(source.contains("public static void showPlayerTargets(Player player) {\n        if (player == null) {\n            return;\n        }"),
                "TargetProtectionService should ignore null players before listing player target rules");
    }
}
