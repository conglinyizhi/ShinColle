package org.trp.shincolle.book;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PatchouliTranslationKeyCoverageRegressionTest {
    private static final Path PATCHOULI_ROOT =
            Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us");
    private static final Path EN_US_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");
    private static final Path ZH_CN_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json");
    private static final Pattern PATCHOULI_KEY_PATTERN =
            Pattern.compile("patchouli\\.shincolle\\.[A-Za-z0-9_./-]+");

    @Test
    void patchouliEntriesShouldOnlyReferenceExistingEnglishTranslationKeys() throws IOException {
        assertPatchouliEntriesReferenceExistingTranslationKeys(EN_US_LANG, "English");
    }

    @Test
    void patchouliEntriesShouldOnlyReferenceExistingChineseTranslationKeys() throws IOException {
        assertPatchouliEntriesReferenceExistingTranslationKeys(ZH_CN_LANG, "Chinese");
    }

    private static void assertPatchouliEntriesReferenceExistingTranslationKeys(Path langFile, String label) throws IOException {
        String lang = Files.readString(langFile);
        List<String> missing = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(PATCHOULI_ROOT)) {
            for (Path json : (Iterable<Path>) stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))::iterator) {
                String content = Files.readString(json);
                Matcher matcher = PATCHOULI_KEY_PATTERN.matcher(content);
                while (matcher.find()) {
                    String key = matcher.group();
                    if (!lang.contains("\"" + key + "\"")) {
                        missing.add(PATCHOULI_ROOT.relativize(json) + " -> " + key);
                    }
                }
            }
        }

        assertTrue(missing.isEmpty(),
                () -> "Patchouli entries must only reference existing " + label
                        + " translation keys, missing: " + String.join(", ", missing));
    }
}
