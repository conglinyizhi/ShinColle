package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShincolleDebugLoggingRegressionTest {
    private static final Path CONFIG_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/Config.kt");
    private static final Path MOD_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/Shincolle.kt");
    private static final Path MAIN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle");

    @Test
    void debugLoggingShouldBeConfigGatedAndDisabledByDefault() throws IOException {
        String config = Files.readString(CONFIG_SOURCE);
        String mod = Files.readString(MOD_SOURCE);

        assertTrue(config.contains("public static boolean debugLogging = false;"),
                "Verbose diagnostics should be disabled during normal gameplay");
        assertTrue(config.contains(".define(\"debugLogging\", debugLogging)"),
                "Debug logging should be exposed as a common config option");
        assertTrue(mod.contains("public static void debugLog(String message, Object... args)"),
                "Debug diagnostics should go through one helper");
        assertTrue(mod.contains("if (!Config.debugLogging)"),
                "Debug helper should suppress diagnostics unless explicitly enabled");
    }

    @Test
    void shincolleDebugPrefixShouldOnlyAppearInCentralHelper() throws IOException {
        long directPrefixCount;
        try (Stream<Path> paths = Files.walk(MAIN_SOURCE)) {
            directPrefixCount = paths
                    .filter(path -> path.toString().endsWith(".kt"))
                    .map(path -> {
                        try {
                            return Files.readString(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(source -> source.contains("[ShinColleDebug]"))
                    .count();
        }

        assertEquals(1, directPrefixCount,
                "Only the central Shincolle.debugLog helper should write the debug prefix");
    }
}
