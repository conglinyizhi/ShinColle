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
    private static final Path ZH_CN_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json");

    private static final List<String> TARGET_WRENCH_TOOLTIP_KEYS = List.of(
            "gui.shincolle.wrench1",
            "gui.shincolle.wrench2",
            "gui.shincolle.wrench3"
    );

    @Test
    void targetWrenchTooltipKeysShouldRemainLocalizedInEnglishAndSimplifiedChinese() throws IOException {
        String source = Files.readString(TARGET_WRENCH_SOURCE);
        String enUs = Files.readString(EN_US_LANG);
        String zhCn = Files.readString(ZH_CN_LANG);

        for (String key : TARGET_WRENCH_TOOLTIP_KEYS) {
            assertTrue(source.contains("Component.translatable(\"" + key + "\")"),
                    () -> "TargetWrenchItem should keep using translation key " + key);
            assertTrue(enUs.contains("\"" + key + "\""),
                    () -> "English language file should define " + key);
            assertTrue(zhCn.contains("\"" + key + "\""),
                    () -> "Simplified Chinese language file should define " + key);
        }
    }
}
