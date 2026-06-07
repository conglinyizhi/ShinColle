package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HostileConfigExposureRegressionTest {
    private record HostileConfigExpectation(
            String translationKey,
            String configField,
            String runtimeUsageSnippet
    ) {
    }

    private static final Path CONFIG_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/Config.kt");
    private static final Path CONFIG_SCREEN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/gui/ShincolleConfigScreen.kt");
    private static final Path HOSTILE_SPAWN_MANAGER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/event/HostileSpawnManager.kt");
    private static final Path EN_US_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");

    private static final List<HostileConfigExpectation> EXPECTATIONS = List.of(
            new HostileConfigExpectation("config.shincolle.hostileMobSpawnGroups", "HOSTILE_MOB_SPAWN_GROUPS",
                    "int groups = Math.max(1, Config.hostileMobSpawnGroups);"),
            new HostileConfigExpectation("config.shincolle.hostileMobSpawnGroupMin", "HOSTILE_MOB_SPAWN_GROUP_MIN",
                    "int shipMin = Math.max(1, Config.hostileMobSpawnGroupMin);"),
            new HostileConfigExpectation("config.shincolle.hostileMobSpawnGroupMax", "HOSTILE_MOB_SPAWN_GROUP_MAX",
                    "int rangeMax = Config.hostileMobSpawnGroupMax - shipMin;")
    );

    @Test
    void hostileSpawnGroupControlsShouldRemainExposedAndUsed() throws IOException {
        String config = Files.readString(CONFIG_SOURCE);
        String configScreen = Files.readString(CONFIG_SCREEN_SOURCE);
        String hostileSpawnManager = Files.readString(HOSTILE_SPAWN_MANAGER_SOURCE);
        String enUs = Files.readString(EN_US_LANG);

        for (HostileConfigExpectation expectation : EXPECTATIONS) {
            assertTrue(config.contains(expectation.configField()),
                    () -> "Expected Config to keep defining " + expectation.configField());
            assertTrue(configScreen.contains("Component.translatable(\"" + expectation.translationKey() + "\")")
                            && configScreen.contains("Config." + expectation.configField() + ".get()")
                            && configScreen.contains("Config." + expectation.configField() + "::set"),
                    () -> "Expected ShincolleConfigScreen to keep exposing " + expectation.configField());
            assertTrue(hostileSpawnManager.contains(expectation.runtimeUsageSnippet()),
                    () -> "Expected HostileSpawnManager to keep using " + expectation.configField());
            assertTrue(enUs.contains("\"" + expectation.translationKey() + "\""),
                    () -> "Expected en_us to keep defining " + expectation.translationKey());
        }
    }
}
