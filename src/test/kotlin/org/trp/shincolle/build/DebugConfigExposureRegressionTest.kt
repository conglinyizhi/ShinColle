package org.trp.shincolle.build

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue

class DebugConfigExposureRegressionTest {
    private data class DebugConfigExpectation(val translationKey: String, val configField: String)

    private val CONFIG_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/Config.kt")
    private val CONFIG_SCREEN_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/client/gui/ShincolleConfigScreen.kt")
    private val EN_US_LANG: Path =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")

    private val EXPECTATIONS = listOf(
        DebugConfigExpectation("config.shincolle.debugLogging", "DEBUG_LOGGING"),
        DebugConfigExpectation("config.shincolle.debugPerformanceLogging", "DEBUG_PERFORMANCE_LOGGING"),
        DebugConfigExpectation("config.shincolle.debugPerfSlowShipTickMs", "DEBUG_PERF_SLOW_SHIP_TICK_MS"),
        DebugConfigExpectation("config.shincolle.debugPerfSlowTaskTickMs", "DEBUG_PERF_SLOW_TASK_TICK_MS"),
        DebugConfigExpectation("config.shincolle.debugPerfSlowBlockEntityTickMs", "DEBUG_PERF_SLOW_BLOCK_ENTITY_TICK_MS"),
        DebugConfigExpectation("config.shincolle.debugPerfSlowProjectileTickMs", "DEBUG_PERF_SLOW_PROJECTILE_TICK_MS"),
        DebugConfigExpectation("config.shincolle.debugPerfSlowServerTickMs", "DEBUG_PERF_SLOW_SERVER_TICK_MS"),
        DebugConfigExpectation("config.shincolle.debugPerfMinLogIntervalTicks", "DEBUG_PERF_MIN_LOG_INTERVAL_TICKS")
    )

    @Test
    fun debugConfigGroupShouldExposeAllKeyDiagnosticControls() {
        val config = Files.readString(CONFIG_SOURCE)
        val configScreen = Files.readString(CONFIG_SCREEN_SOURCE)
        val enUs = Files.readString(EN_US_LANG)

        for (expectation in EXPECTATIONS) {
            assertTrue(config.contains(expectation.configField)) {
                "Expected Config to keep defining " + expectation.configField
            }
            assertTrue(configScreen.contains("Component.translatable(\"" + expectation.translationKey + "\")")
                    && configScreen.contains("Config." + expectation.configField + ".get()")
                    && configScreen.contains("Config." + expectation.configField + "::set")) {
                "Expected ShincolleConfigScreen to keep exposing " + expectation.configField
            }
            assertTrue(enUs.contains("\"" + expectation.translationKey + "\"")) {
                "Expected en_us to keep defining " + expectation.translationKey
            }
        }
    }
}
