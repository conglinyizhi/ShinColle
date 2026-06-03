package org.trp.shincolle.init;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BossEggLocalizationRegressionTest {
    private static final Path MOD_ITEMS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.java");
    private static final List<Path> LANGUAGE_SOURCES = List.of(
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
    );
    private static final List<String> BOSS_EGG_KEYS = List.of(
            "item.shincolle.carrier_kaga_boss_egg",
            "item.shincolle.carrier_akagi_boss_egg",
            "item.shincolle.bb_kongou_boss_egg",
            "item.shincolle.bb_hiei_boss_egg",
            "item.shincolle.bb_haruna_boss_egg",
            "item.shincolle.bb_kirishima_boss_egg"
    );

    @Test
    void registeredBossEggNamesShouldRemainLocalizedInMaintainedLanguages() throws IOException {
        String modItems = Files.readString(MOD_ITEMS_SOURCE);

        for (String key : BOSS_EGG_KEYS) {
            assertRegisteredBossEggSourceStillUses(key, modItems);
            assertLocalizedInMaintainedLanguages(key);
        }
    }

    private static void assertRegisteredBossEggSourceStillUses(String key, String modItems) {
        String bossEggId = key.substring("item.shincolle.".length(), key.length() - "_boss_egg".length());
        assertTrue(modItems.contains("registerBossEgg(\"" + bossEggId + "\""),
                () -> "Expected ModItems to keep registering boss egg " + bossEggId);
    }

    private static void assertLocalizedInMaintainedLanguages(String key) throws IOException {
        for (Path languageSource : LANGUAGE_SOURCES) {
            String source = Files.readString(languageSource);
            assertTrue(source.contains("\"" + key + "\""),
                    () -> "Expected maintained languages to define " + key);
        }
    }
}
