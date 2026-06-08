package org.trp.shincolle.build

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue

class HostileConfigExposureRegressionTest {
    private data class HostileConfigExpectation(
        val translationKey: String,
        val configField: String,
        val runtimeUsageSnippet: String
    )

    private val CONFIG_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/Config.kt")
    private val CONFIG_SCREEN_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/client/gui/ShincolleConfigScreen.kt")
    private val HOSTILE_SPAWN_MANAGER_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/event/HostileSpawnManager.kt")
    private val EN_US_LANG: Path =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")

    private val EXPECTATIONS = listOf(
        HostileConfigExpectation("config.shincolle.hostileMobSpawnGroups", "HOSTILE_MOB_SPAWN_GROUPS",
            "int groups = Math.max(1, Config.hostileMobSpawnGroups);"),
        HostileConfigExpectation("config.shincolle.hostileMobSpawnGroupMin", "HOSTILE_MOB_SPAWN_GROUP_MIN",
            "int shipMin = Math.max(1, Config.hostileMobSpawnGroupMin);"),
        HostileConfigExpectation("config.shincolle.hostileMobSpawnGroupMax", "HOSTILE_MOB_SPAWN_GROUP_MAX",
            "int rangeMax = Config.hostileMobSpawnGroupMax - shipMin;")
    )

    @Test
    fun hostileSpawnGroupControlsShouldRemainExposedAndUsed() {
        val config = Files.readString(CONFIG_SOURCE)
        val configScreen = Files.readString(CONFIG_SCREEN_SOURCE)
        val hostileSpawnManager = Files.readString(HOSTILE_SPAWN_MANAGER_SOURCE)
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
            assertTrue(hostileSpawnManager.contains(expectation.runtimeUsageSnippet)) {
                "Expected HostileSpawnManager to keep using " + expectation.configField
            }
            assertTrue(enUs.contains("\"" + expectation.translationKey + "\"")) {
                "Expected en_us to keep defining " + expectation.translationKey
            }
        }
    }
}
