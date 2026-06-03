package org.trp.shincolle.item;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilityItemLocalizationRegressionTest {
    private static final Path MOD_ITEMS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.java");
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
        String modItems = Files.readString(MOD_ITEMS_SOURCE);

        for (String key : UTILITY_ITEM_KEYS) {
            assertRegisteredUtilityItemSourceStillUses(key, modItems);
            assertLocalizedInMaintainedLanguages(key);
        }
    }

    private static void assertRegisteredUtilityItemSourceStillUses(String key, String modItems) {
        String itemId = switch (key) {
            case "item.shincolle.ShinComb.name" -> "shincomb";
            case "item.shincolle.blockcrane" -> "blockcrane";
            case "item.shincolle.blockdesk" -> "blockdesk";
            case "item.shincolle.recipepaper" -> "recipepaper";
            case "item.shincolle.shipspawneggl" -> "shipspawneggl";
            case "item.shincolle.shipspawneggs" -> "shipspawneggs";
            case "item.shincolle.small_shipyard" -> "small_shipyard";
            default -> throw new IllegalStateException("Unexpected utility item key: " + key);
        };

        assertTrue(modItems.contains("register(\"" + itemId + "\""),
                () -> "Expected ModItems to keep registering utility item " + itemId);
    }

    private static void assertLocalizedInMaintainedLanguages(String key) throws IOException {
        for (Path languageSource : LANGUAGE_SOURCES) {
            String source = Files.readString(languageSource);
            assertTrue(source.contains("\"" + key + "\""),
                    () -> "Expected maintained languages to define " + key);
        }
    }
}
