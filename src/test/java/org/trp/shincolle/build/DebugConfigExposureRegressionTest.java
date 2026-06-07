package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DebugConfigExposureRegressionTest {
    private record DebugConfigExpectation(String translationKey, String configField) {
    }

    private static final Path CONFIG_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/Config.kt");
    private static final Path CONFIG_SCREEN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/gui/ShincolleConfigScreen.kt");
    private static final Path EN_US_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");

    private static final List<DebugConfigExpectation> EXPECTATIONS = List.of(
            new DebugConfigExpectation("config.shincolle.debugLogging", "DEBUG_LOGGING"),
            new DebugConfigExpectation("config.shincolle.debugPerformanceLogging", "DEBUG_PERFORMANCE_LOGGING"),
            new DebugConfigExpectation("config.shincolle.debugPerfSlowShipTickMs", "DEBUG_PERF_SLOW_SHIP_TICK_MS"),
            new DebugConfigExpectation("config.shincolle.debugPerfSlowTaskTickMs", "DEBUG_PERF_SLOW_TASK_TICK_MS"),
            new DebugConfigExpectation("config.shincolle.debugPerfSlowBlockEntityTickMs", "DEBUG_PERF_SLOW_BLOCK_ENTITY_TICK_MS"),
            new DebugConfigExpectation("config.shincolle.debugPerfSlowProjectileTickMs", "DEBUG_PERF_SLOW_PROJECTILE_TICK_MS"),
            new DebugConfigExpectation("config.shincolle.debugPerfSlowServerTickMs", "DEBUG_PERF_SLOW_SERVER_TICK_MS"),
            new DebugConfigExpectation("config.shincolle.debugPerfMinLogIntervalTicks", "DEBUG_PERF_MIN_LOG_INTERVAL_TICKS")
    );

    @Test
    void debugConfigGroupShouldExposeAllKeyDiagnosticControls() throws IOException {
        String config = Files.readString(CONFIG_SOURCE);
        String configScreen = Files.readString(CONFIG_SCREEN_SOURCE);
        String enUs = Files.readString(EN_US_LANG);

        for (DebugConfigExpectation expectation : EXPECTATIONS) {
            assertTrue(config.contains(expectation.configField()),
                    () -> "Expected Config to keep defining " + expectation.configField());
            assertTrue(configScreen.contains("Component.translatable(\"" + expectation.translationKey() + "\")")
                            && configScreen.contains("Config." + expectation.configField() + ".get()")
                            && configScreen.contains("Config." + expectation.configField() + "::set"),
                    () -> "Expected ShincolleConfigScreen to keep exposing " + expectation.configField());
            assertTrue(enUs.contains("\"" + expectation.translationKey() + "\""),
                    () -> "Expected en_us to keep defining " + expectation.translationKey());
        }
    }
}
