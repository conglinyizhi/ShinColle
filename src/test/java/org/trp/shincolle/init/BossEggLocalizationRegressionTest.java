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
            "item.shincolle.destroyer_hime_boss_egg",
            "item.shincolle.ca_hime_boss_egg",
            "item.shincolle.carrier_hime_boss_egg",
            "item.shincolle.battleship_hime_boss_egg",
            "item.shincolle.airfield_hime_boss_egg",
            "item.shincolle.harbour_hime_boss_egg",
            "item.shincolle.isolated_hime_boss_egg",
            "item.shincolle.midway_hime_boss_egg",
            "item.shincolle.northern_hime_boss_egg",
            "item.shincolle.carrier_w_demon_boss_egg",
            "item.shincolle.battleship_nagato_boss_egg",
            "item.shincolle.battleship_yamato_boss_egg",
            "item.shincolle.subm_ka_boss_egg",
            "item.shincolle.subm_yo_boss_egg",
            "item.shincolle.subm_so_boss_egg",
            "item.shincolle.subm_hime_boss_egg",
            "item.shincolle.ssnh_boss_egg",
            "item.shincolle.subm_u511_boss_egg",
            "item.shincolle.subm_ro500_boss_egg",
            "item.shincolle.destroyer_i_boss_egg",
            "item.shincolle.destroyer_ro_boss_egg",
            "item.shincolle.destroyer_ha_boss_egg",
            "item.shincolle.destroyer_ni_boss_egg",
            "item.shincolle.heavy_cruiser_ri_boss_egg",
            "item.shincolle.heavy_cruiser_ne_boss_egg",
            "item.shincolle.carrier_wo_boss_egg",
            "item.shincolle.battleship_ru_boss_egg",
            "item.shincolle.battleship_ta_boss_egg",
            "item.shincolle.battleship_re_boss_egg",
            "item.shincolle.transport_wa_boss_egg",
            "item.shincolle.destroyer_akatsuki_boss_egg",
            "item.shincolle.destroyer_hibiki_boss_egg",
            "item.shincolle.destroyer_ikazuchi_boss_egg",
            "item.shincolle.destroyer_inazuma_boss_egg",
            "item.shincolle.destroyer_shimakaze_boss_egg",
            "item.shincolle.cruiser_tenryuu_boss_egg",
            "item.shincolle.cruiser_tatsuta_boss_egg",
            "item.shincolle.cruiser_takao_boss_egg",
            "item.shincolle.cruiser_atago_boss_egg",
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
