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

class BlockLocalizationRegressionTest {
    private static final Path MOD_BLOCKS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModBlocks.java");
    private static final List<Path> LANGUAGE_SOURCES = List.of(
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
    );
    private static final Pattern BLOCK_REGISTRATION_PATTERN =
            Pattern.compile("BLOCKS\\.register\\(\"([a-z0-9_]+)\"");

    @Test
    void registeredBlockNamesShouldRemainLocalizedInMaintainedLanguages() throws IOException {
        String modBlocks = Files.readString(MOD_BLOCKS_SOURCE);
        List<String> blockKeys = new ArrayList<>();

        for (String blockId : readRegisteredIds(modBlocks)) {
            blockKeys.add("block.shincolle." + blockId);
        }

        assertTrue(!blockKeys.isEmpty(),
                "Expected ModBlocks to keep declaring localized block registrations");

        for (String key : blockKeys) {
            assertLocalizedInMaintainedLanguages(key);
        }
    }

    private static List<String> readRegisteredIds(String source) {
        List<String> ids = new ArrayList<>();
        Matcher matcher = BLOCK_REGISTRATION_PATTERN.matcher(source);
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
