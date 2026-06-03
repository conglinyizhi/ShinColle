package org.trp.shincolle.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HostileDropServiceRegressionTest {
    private static final Path HOSTILE_DROP_SERVICE =
            Path.of("src/main/java/org/trp/shincolle/server/HostileDropService.java");

    @Test
    void hostileDropServiceShouldGuardNullEventInputs() throws IOException {
        String source = Files.readString(HOSTILE_DROP_SERVICE);

        assertTrue(source.contains("public static void handleLivingDrops(LivingDropsEvent event) {\n        if (event == null) {\n            return;\n        }"),
                "HostileDropService should ignore null LivingDropsEvent inputs");
        assertTrue(source.contains("Entity target = event.getEntity();\n        if (target == null) {\n            return;\n        }"),
                "HostileDropService should ignore drops events without a target entity");
        assertTrue(source.contains("if (event.getSource() == null) {\n            return;\n        }"),
                "HostileDropService should ignore drops events without a damage source before reading the attacker");
    }
}
