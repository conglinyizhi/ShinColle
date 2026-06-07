package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientConfigCoverageRegressionTest {
    private record ExposedClientConfig(String translationKey, String configField) {
    }

    private record HiddenClientConfig(String configField, String runtimeEvidence) {
    }

    private static final Path CONFIG_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/Config.kt");
    private static final Path CONFIG_SCREEN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/gui/ShincolleConfigScreen.kt");
    private static final Path EN_US_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");

    private static final List<ExposedClientConfig> EXPOSED_CONFIGS = List.of(
            new ExposedClientConfig("config.shincolle.canTimeKeeping", "SHIP_CAN_TIMEKEEPING"),
            new ExposedClientConfig("config.shincolle.volumeTimeKeeping", "SHIP_VOLUME_TIMEKEEPING"),
            new ExposedClientConfig("config.shincolle.volumeShip", "SHIP_VOLUME_GENERAL"),
            new ExposedClientConfig("config.shincolle.volumeAttack", "SHIP_VOLUME_ATTACK"),
            new ExposedClientConfig("config.shincolle.scaleHeldItem", "CLIENT_SCALE_HELD_ITEM"),
            new ExposedClientConfig("config.shincolle.offsetHeldItemX", "CLIENT_OFFSET_HELD_ITEM_X"),
            new ExposedClientConfig("config.shincolle.offsetHeldItemY", "CLIENT_OFFSET_HELD_ITEM_Y"),
            new ExposedClientConfig("config.shincolle.offsetHeldItemZ", "CLIENT_OFFSET_HELD_ITEM_Z"),
            new ExposedClientConfig("config.shincolle.useMiSansFont", "USE_MISANS_FONT"),
            new ExposedClientConfig("config.shincolle.miSansOnlyForLegacyLogs", "MISANS_ONLY_LEGACY_LOGS")
    );

    private static final List<HiddenClientConfig> HIDDEN_CONFIGS = List.of(
            new HiddenClientConfig("CUSTOM_SOUND_RATES",
                    "customSoundRates = parseCustomSoundRates(CUSTOM_SOUND_RATES.get());")
    );

    @Test
    void scalarClientConfigDefinitionsShouldRemainCoveredByConfigScreen() throws IOException {
        String config = Files.readString(CONFIG_SOURCE);
        String configScreen = Files.readString(CONFIG_SCREEN_SOURCE);
        String enUs = Files.readString(EN_US_LANG);

        for (ExposedClientConfig expectation : EXPOSED_CONFIGS) {
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

    @Test
    void listBasedClientConfigShouldRemainDocumentedAsIntentionallyHiddenFromConfigScreen() throws IOException {
        String config = Files.readString(CONFIG_SOURCE);
        String configScreen = Files.readString(CONFIG_SCREEN_SOURCE);

        for (HiddenClientConfig expectation : HIDDEN_CONFIGS) {
            assertTrue(config.contains(expectation.configField()),
                    () -> "Expected Config to keep defining " + expectation.configField());
            assertTrue(config.contains(expectation.runtimeEvidence()),
                    () -> "Expected Config.onLoad to keep syncing " + expectation.configField());
            assertFalse(configScreen.contains("Config." + expectation.configField() + ".get()"),
                    () -> "Expected ShincolleConfigScreen to keep " + expectation.configField()
                            + " out of the current scalar-focused UI");
        }
    }
}
