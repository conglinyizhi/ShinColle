package org.trp.shincolle.item;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ModernKitMaxedFeedbackRegressionTest {
    private static final Path MODERN_KIT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/item/ModernKitItem.java");
    private static final Path CONFIG_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/Config.java");
    private static final Path EN_US_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");
    private static final Path ZH_CN_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json");

    @Test
    void modernKitShouldNotifyWhenModernizationIsAlreadyMaxed() throws IOException {
        String modernKitSource = Files.readString(MODERN_KIT_SOURCE);
        String configSource = Files.readString(CONFIG_SOURCE);
        String enUs = Files.readString(EN_US_LANG);
        String zhCn = Files.readString(ZH_CN_LANG);

        assertTrue(modernKitSource.contains("if (Config.modernKitNotifyWhenMaxed) {"),
                "ModernKit should guard maxed-out feedback behind a config toggle");
        assertTrue(modernKitSource.contains("MaxedFeedback feedback = maxedFeedback();"),
                "ModernKit should route maxed-out feedback through the shared helper");

        assertTrue(configSource.contains("define(\"modernKitNotifyWhenMaxed\", modernKitNotifyWhenMaxed)"),
                "Config should expose a toggle for maxed modernization feedback");
        assertTrue(configSource.contains("define(\"modernKitNotifyWhenMaxedActionBar\", modernKitNotifyWhenMaxedActionBar)"),
                "Config should expose a toggle for action-bar style maxed modernization feedback");

        assertTrue(enUs.contains("\"chat.shincolle.modernkit.maxed\": \"This ship cannot be modernized any further.\""),
                "English localization should describe maxed modernization feedback");
        assertTrue(zhCn.contains("\"chat.shincolle.modernkit.maxed\": \"这位舰娘已经无法继续进行近代化改修了。\""),
                "Chinese localization should describe maxed modernization feedback");
    }
}
