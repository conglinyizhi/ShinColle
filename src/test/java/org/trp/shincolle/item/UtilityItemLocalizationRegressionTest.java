package org.trp.shincolle.item;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilityItemLocalizationRegressionTest {
    private static final List<Path> LANGUAGE_SOURCES = List.of(
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
    );
    private static final List<String> UTILITY_ITEM_KEYS = List.of(
            "item.shincolle.ShinComb.name",
            "item.shincolle.blockcrane",
            "item.shincolle.blockdesk",
            "item.shincolle.recipepaper",
            "item.shincolle.shipspawneggl",
            "item.shincolle.shipspawneggs",
            "item.shincolle.small_shipyard"
    );

    @Test
    void registeredUtilityItemsShouldRemainLocalizedInMaintainedLanguages() throws IOException {
        for (String key : UTILITY_ITEM_KEYS) {
            assertLocalizedInMaintainedLanguages(key);
        }
    }

    private static void assertLocalizedInMaintainedLanguages(String key) throws IOException {
        for (Path languageSource : LANGUAGE_SOURCES) {
            String source = Files.readString(languageSource);
            assertTrue(source.contains("\"" + key + "\""),
                    () -> "Expected maintained languages to define " + key);
        }
    }
}
