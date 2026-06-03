package org.trp.shincolle.item;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TargetWrenchTooltipLocalizationRegressionTest {
    private static final Path TARGET_WRENCH_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/item/TargetWrenchItem.java");
    private static final Path EN_US_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");
    private static final Path JA_JP_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json");
    private static final Path ZH_CN_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json");
    private static final Path ZH_TW_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json");

    private static final List<String> TARGET_WRENCH_TOOLTIP_KEYS = List.of(
            "gui.shincolle.wrench1",
            "gui.shincolle.wrench2",
            "gui.shincolle.wrench3"
    );

    @Test
    void targetWrenchTooltipKeysShouldRemainLocalizedInMaintainedLanguages() throws IOException {
        String source = Files.readString(TARGET_WRENCH_SOURCE);
        List<String> languageSources = List.of(
                Files.readString(EN_US_LANG),
                Files.readString(JA_JP_LANG),
                Files.readString(ZH_CN_LANG),
                Files.readString(ZH_TW_LANG)
        );

        for (String key : TARGET_WRENCH_TOOLTIP_KEYS) {
            assertTrue(source.contains("Component.translatable(\"" + key + "\")"),
                    () -> "TargetWrenchItem should keep using translation key " + key);
            for (String languageSource : languageSources) {
                assertTrue(languageSource.contains("\"" + key + "\""),
                        () -> "Maintained language files should define " + key);
            }
        }
    }
}
