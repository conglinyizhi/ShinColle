package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MiningConfigLevelGateRegressionTest {
    private static final Path CONFIG_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/Config.java");

    @Test
    void miningConfigShouldPreserveLegacyZeroShipLevelRequirement() throws IOException {
        String source = Files.readString(CONFIG_SOURCE);

        assertTrue(source.contains("Integer.parseInt(parts[7])"),
                "Mining config parser should preserve legacy ship level gates, including zero");
        assertTrue(!source.contains("Math.max(1, Integer.parseInt(parts[7]))"),
                "Mining config parser should not clamp the legacy ship level gate to one");
    }
}
