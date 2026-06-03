package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;

class LanguagePlaceholderRegressionTest {
    private static final Path EN_US =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");
    private static final Path JA_JP =
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json");
    private static final Path ZH_CN =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json");
    private static final Path ZH_TW =
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json");

    @Test
    void maintainedLanguagesShouldNotContainAwaitingTranslationPlaceholders() throws IOException {
        for (Path path : new Path[]{EN_US, JA_JP, ZH_CN, ZH_TW}) {
            String content = Files.readString(path).toLowerCase();
            assertFalse(content.contains("awaiting translation"),
                    path + " should not ship placeholder translation markers");
        }
    }
}
