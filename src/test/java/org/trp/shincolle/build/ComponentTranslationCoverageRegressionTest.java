package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComponentTranslationCoverageRegressionTest {
    private static final Path JAVA_ROOT = Path.of("src/main/java");
    private static final Path EN_US_LANG = Path.of("src/main/resources/assets/shincolle/lang/en_us.json");
    private static final Pattern BLOCK_COMMENT_PATTERN = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    private static final Pattern LINE_COMMENT_PATTERN = Pattern.compile("//.*");
    private static final Pattern TRANSLATABLE_PATTERN = Pattern.compile(
            "(?:^|\\W)(?:net\\.minecraft\\.network\\.chat\\.)?Component\\.translatable\\(\"([^\"]+)\"\\s*(?:,|\\))");

    @Test
    void englishLanguageShouldCoverAllLiteralComponentTranslationKeys() throws IOException {
        Set<String> englishKeys = readKeys(EN_US_LANG);
        List<String> missing = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(JAVA_ROOT)) {
            for (Path javaFile : (Iterable<Path>) stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))::iterator) {
                String source = stripComments(Files.readString(javaFile));
                Matcher matcher = TRANSLATABLE_PATTERN.matcher(source);
                Set<String> fileKeys = new TreeSet<>();
                while (matcher.find()) {
                    fileKeys.add(matcher.group(1));
                }
                for (String key : fileKeys) {
                    if (!englishKeys.contains(key)) {
                        missing.add(javaFile + " -> " + key);
                    }
                }
            }
        }

        assertTrue(missing.isEmpty(),
                () -> "English language file must cover all literal Component.translatable keys: "
                        + String.join(", ", missing));
    }

    private static String stripComments(String source) {
        String withoutBlockComments = BLOCK_COMMENT_PATTERN.matcher(source).replaceAll("");
        return LINE_COMMENT_PATTERN.matcher(withoutBlockComments).replaceAll("");
    }

    private static Set<String> readKeys(Path file) throws IOException {
        return Files.readAllLines(file).stream()
                .map(String::trim)
                .filter(line -> line.startsWith("\""))
                .map(line -> line.substring(1, line.indexOf('"', 1)))
                .collect(Collectors.toSet());
    }
}
