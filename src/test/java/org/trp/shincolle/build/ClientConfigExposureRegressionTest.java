package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientConfigExposureRegressionTest {
    private record ClientConfigExpectation(
            String translationKey,
            String configField,
            String runtimeEvidence
    ) {
    }

    private static final Path CONFIG_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/Config.java");
    private static final Path CONFIG_SCREEN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/gui/ShincolleConfigScreen.java");
    private static final Path EN_US_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");

    private static final List<ClientConfigExpectation> EXPECTATIONS = List.of(
            new ClientConfigExpectation("config.shincolle.volumeTimeKeeping", "SHIP_VOLUME_TIMEKEEPING",
                    "volumeTimeKeeping = SHIP_VOLUME_TIMEKEEPING.get().floatValue();"),
            new ClientConfigExpectation("config.shincolle.volumeShip", "SHIP_VOLUME_GENERAL",
                    "volumeShip = SHIP_VOLUME_GENERAL.get().floatValue();"),
            new ClientConfigExpectation("config.shincolle.volumeAttack", "SHIP_VOLUME_ATTACK",
                    "volumeAttack = SHIP_VOLUME_ATTACK.get().floatValue();"),
            new ClientConfigExpectation("config.shincolle.scaleHeldItem", "CLIENT_SCALE_HELD_ITEM",
                    "scaleHeldItem = CLIENT_SCALE_HELD_ITEM.get().floatValue();"),
            new ClientConfigExpectation("config.shincolle.offsetHeldItemX", "CLIENT_OFFSET_HELD_ITEM_X",
                    "offsetHeldItemX = CLIENT_OFFSET_HELD_ITEM_X.get().floatValue();"),
            new ClientConfigExpectation("config.shincolle.offsetHeldItemY", "CLIENT_OFFSET_HELD_ITEM_Y",
                    "offsetHeldItemY = CLIENT_OFFSET_HELD_ITEM_Y.get().floatValue();"),
            new ClientConfigExpectation("config.shincolle.offsetHeldItemZ", "CLIENT_OFFSET_HELD_ITEM_Z",
                    "offsetHeldItemZ = CLIENT_OFFSET_HELD_ITEM_Z.get().floatValue();"),
            new ClientConfigExpectation("config.shincolle.useMiSansFont", "USE_MISANS_FONT",
                    "useMiSansFont = USE_MISANS_FONT.get();"),
            new ClientConfigExpectation("config.shincolle.miSansOnlyForLegacyLogs", "MISANS_ONLY_LEGACY_LOGS",
                    "miSansOnlyForLegacyLogs = MISANS_ONLY_LEGACY_LOGS.get();")
    );

    @Test
    void importantClientConfigEntriesShouldRemainExposedInConfigScreen() throws IOException {
        String config = Files.readString(CONFIG_SOURCE);
        String configScreen = Files.readString(CONFIG_SCREEN_SOURCE);
        String enUs = Files.readString(EN_US_LANG);

        for (ClientConfigExpectation expectation : EXPECTATIONS) {
            assertTrue(config.contains("public static final ModConfigSpec.")
                            && config.contains(expectation.configField()),
                    () -> "Expected Config to keep defining " + expectation.configField());
            assertTrue(config.contains(expectation.runtimeEvidence()),
                    () -> "Expected Config.onLoad to keep syncing " + expectation.configField());
            assertTrue(configScreen.contains("Component.translatable(\"" + expectation.translationKey() + "\")")
                            && configScreen.contains("Config." + expectation.configField() + ".get()")
                            && configScreen.contains("Config." + expectation.configField() + "::set"),
                    () -> "Expected ShincolleConfigScreen to keep exposing " + expectation.configField());
            assertTrue(enUs.contains("\"" + expectation.translationKey() + "\""),
                    () -> "Expected en_us to keep defining " + expectation.translationKey());
        }
    }
}
