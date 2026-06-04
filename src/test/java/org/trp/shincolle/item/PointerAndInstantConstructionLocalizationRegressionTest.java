package org.trp.shincolle.item;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PointerAndInstantConstructionLocalizationRegressionTest {
    private static final Path INSTANT_CONSTRUCTION_ITEM_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/item/InstantConstructionMaterialItem.java");
    private static final List<Path> LANGUAGE_SOURCES = List.of(
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
    );
    private static final List<String> INSTANT_CONSTRUCTION_KEYS = List.of(
            "gui.shincolle.instantconmat",
            "gui.shincolle.instantconmat.slot"
    );

    @Test
    void instantConstructionTooltipShouldRemainLocalizedInMaintainedLanguages() throws IOException {
        String source = Files.readString(INSTANT_CONSTRUCTION_ITEM_SOURCE);

        for (String key : INSTANT_CONSTRUCTION_KEYS) {
            assertTrue(source.contains("Component.translatable(\"" + key + "\")"),
                    () -> "InstantConstructionMaterialItem should keep using translation key " + key);
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
