package org.trp.shincolle.init;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemLocalizationRegressionTest {
    private static final Path MOD_ITEMS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.java");
    private static final List<Path> LANGUAGE_SOURCES = List.of(
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
    );
    private static final Pattern ITEM_REGISTRATION_PATTERN =
            Pattern.compile("ITEMS\\.register\\(\"([a-z0-9_]+)\"");
    private static final Pattern BOSS_EGG_REGISTRATION_PATTERN =
            Pattern.compile("registerBossEgg\\(\"([a-z0-9_]+)\"");

    @Test
    void registeredItemNamesShouldRemainLocalizedInMaintainedLanguages() throws IOException {
        String modItems = Files.readString(MOD_ITEMS_SOURCE);
        List<String> itemKeys = new ArrayList<>();

        for (String itemId : readRegisteredIds(modItems, ITEM_REGISTRATION_PATTERN)) {
            itemKeys.add("item.shincolle." + itemId);
        }
        for (String itemId : readRegisteredIds(modItems, BOSS_EGG_REGISTRATION_PATTERN)) {
            itemKeys.add("item.shincolle." + itemId + "_boss_egg");
        }

        assertTrue(!itemKeys.isEmpty(),
                "Expected ModItems to keep declaring localized item registrations");

        for (String key : itemKeys) {
            assertLocalizedInMaintainedLanguages(key);
        }
    }

    private static List<String> readRegisteredIds(String source, Pattern pattern) {
        List<String> ids = new ArrayList<>();
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            ids.add(matcher.group(1));
        }
        return ids;
    }

    private static void assertLocalizedInMaintainedLanguages(String key) throws IOException {
        for (Path languageSource : LANGUAGE_SOURCES) {
            String source = Files.readString(languageSource);
            assertTrue(source.contains("\"" + key + "\""),
                    () -> "Expected maintained languages to define " + key);
        }
    }
}
