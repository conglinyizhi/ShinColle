package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigScreenChineseTranslationCoverageRegressionTest {
    private static final Path CONFIG_SCREEN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/gui/ShincolleConfigScreen.kt");
    private static final Path ZH_CN_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json");
    private static final Pattern CONFIG_KEY_PATTERN =
            Pattern.compile("Component\\.translatable\\(\"(config\\.shincolle\\.[^\"]+)\"");

    @Test
    void simplifiedChineseShouldCoverEveryConfigScreenTranslationKey() throws IOException {
        Set<String> chineseKeys = readKeys(ZH_CN_LANG);
        String configScreen = Files.readString(CONFIG_SCREEN_SOURCE);
        Matcher matcher = CONFIG_KEY_PATTERN.matcher(configScreen);
        Set<String> missing = new TreeSet<>();

        while (matcher.find()) {
            String key = matcher.group(1);
            if (!chineseKeys.contains(key)) {
                missing.add(key);
            }
        }

        assertTrue(missing.isEmpty(),
                () -> "Simplified Chinese language file should cover every ShincolleConfigScreen translation key, missing: "
                        + String.join(", ", missing));
    }

    private static Set<String> readKeys(Path file) throws IOException {
        return Files.readAllLines(file).stream()
                .map(String::trim)
                .filter(line -> line.startsWith("\""))
                .map(line -> line.substring(1, line.indexOf('"', 1)))
                .collect(Collectors.toSet());
    }
}
