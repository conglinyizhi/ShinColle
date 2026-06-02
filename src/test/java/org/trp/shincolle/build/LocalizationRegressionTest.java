package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalizationRegressionTest {
    private static final Path EN_US_LANG = Path.of("src/main/resources/assets/shincolle/lang/en_us.json");
    private static final Path ZH_CN_LANG = Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json");
    private static final Path ZH_TW_LANG = Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json");
    private static final Path JA_JP_LANG = Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json");

    private static final List<String> REQUIRED_KEYS = List.of(
            "config.jade.plugin_shincolle.ship",
            "config.jade.plugin_shincolle.shipyard",
            "gui.shincolle.creative_infinite",
            "gui.shincolle.radar.zoom.tooltip",
            "gui.shincolle.radar.clear.tooltip",
            "gui.shincolle.radar.action.recall.tooltip",
            "gui.shincolle.radar.action.open.tooltip",
            "item.shincolle.debug_inspector",
            "item.shincolle.debug_inspector.desc",
            "item.shincolle.debug_inspector.desc2"
    );

    private static final Set<String> EXPECTED_ZH_CN_EXTRA_KEYS = Set.of(
            "chat.shincolle.settargetclass",
            "chat.shincolle.wrench.unatkshow",
            "gui.shincolle.book.chap1.title17",
            "inter.shincolle.bee1.desc",
            "inter.shincolle.bee1.name",
            "inter.shincolle.bee2.desc",
            "inter.shincolle.bee2.name",
            "inter.shincolle.bee3.desc",
            "inter.shincolle.bee3.name",
            "inter.shincolle.flower",
            "item.shincolle.AbyssMetal1.name",
            "item.shincolle.RepairGoddess.name",
            "item.shincolle.deskitembook",
            "item.shincolle.deskitemradar",
            "item.shincolle.hostile_egg_l",
            "item.shincolle.repairgoddess",
            "tile.shincolle.BlockGrudge.name",
            "tile.shincolle.BlockGrudgeHeavy.name"
    );

    @Test
    void allSupportedLanguagesShouldDefineRequiredIntegrationAndUiKeys() throws IOException {
        assertContainsKeys(EN_US_LANG, REQUIRED_KEYS);
        assertContainsKeys(ZH_CN_LANG, REQUIRED_KEYS);
        assertContainsKeys(ZH_TW_LANG, REQUIRED_KEYS);
        assertContainsKeys(JA_JP_LANG, REQUIRED_KEYS);
    }

    @Test
    void englishLanguageShouldRemainTheCompleteBaselineForRecentlyAddedKeys() throws IOException {
        String enUs = Files.readString(EN_US_LANG);

        assertTrue(enUs.contains("\"config.jade.plugin_shincolle.ship\": \"Ship Info\""),
                "English language file should define the Jade ship config entry");
        assertTrue(enUs.contains("\"config.jade.plugin_shincolle.shipyard\": \"Shipyard Info\""),
                "English language file should define the Jade shipyard config entry");
        assertTrue(enUs.contains("\"gui.shincolle.radar.zoom.tooltip\": \"Cycle radar zoom range\""),
                "English language file should define the radar zoom tooltip");
        assertTrue(enUs.contains("\"gui.shincolle.radar.clear.tooltip\": \"Clear the current ship selection\""),
                "English language file should define the radar clear tooltip");
    }

    @Test
    void simplifiedChineseShouldCoverAllEnglishKeys() throws IOException {
        Set<String> englishKeys = readKeys(EN_US_LANG);
        Set<String> simplifiedChineseKeys = readKeys(ZH_CN_LANG);

        List<String> missing = englishKeys.stream()
                .filter(key -> !simplifiedChineseKeys.contains(key))
                .sorted()
                .toList();

        assertTrue(missing.isEmpty(),
                () -> "Simplified Chinese language file should cover all English keys, missing: "
                        + String.join(", ", missing));
    }

    @Test
    void simplifiedChineseExtraKeysShouldStayWithinKnownLegacyAliasAllowlist() throws IOException {
        Set<String> englishKeys = readKeys(EN_US_LANG);
        Set<String> simplifiedChineseKeys = readKeys(ZH_CN_LANG);

        Set<String> extras = new TreeSet<>(simplifiedChineseKeys);
        extras.removeAll(englishKeys);

        assertTrue(extras.equals(EXPECTED_ZH_CN_EXTRA_KEYS),
                () -> "Simplified Chinese extra keys should stay limited to known legacy aliases, found: "
                        + String.join(", ", extras));
    }

    private static void assertContainsKeys(Path file, List<String> keys) throws IOException {
        String content = Files.readString(file);
        for (String key : keys) {
            assertTrue(content.contains("\"" + key + "\""),
                    () -> file + " should define language key " + key);
        }
    }

    private static Set<String> readKeys(Path file) throws IOException {
        return Files.readAllLines(file).stream()
                .map(String::trim)
                .filter(line -> line.startsWith("\""))
                .map(line -> line.substring(1, line.indexOf('"', 1)))
                .collect(java.util.stream.Collectors.toSet());
    }
}
