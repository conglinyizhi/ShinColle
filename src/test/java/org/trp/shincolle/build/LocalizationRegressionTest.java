package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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

    private static void assertContainsKeys(Path file, List<String> keys) throws IOException {
        String content = Files.readString(file);
        for (String key : keys) {
            assertTrue(content.contains("\"" + key + "\""),
                    () -> file + " should define language key " + key);
        }
    }
}
