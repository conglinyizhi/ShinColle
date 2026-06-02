package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonDuplicateKeyRegressionTest {
    private static final Path LANG_ROOT = Path.of("src/main/resources/assets/shincolle/lang");
    private static final Path PATCHOULI_ROOT =
            Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us");

    @Test
    void languageFilesShouldNotContainDuplicateKeys() throws IOException {
        List<String> issues = new ArrayList<>();
        try (Stream<Path> stream = Files.list(LANG_ROOT)) {
            for (Path file : (Iterable<Path>) stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))::iterator) {
                issues.addAll(findDuplicateObjectKeys(file));
            }
        }
        assertTrue(issues.isEmpty(),
                () -> "Language JSON files must not contain duplicate keys: " + String.join(", ", issues));
    }

    @Test
    void patchouliManualJsonShouldNotContainDuplicateKeys() throws IOException {
        List<String> issues = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(PATCHOULI_ROOT)) {
            for (Path file : (Iterable<Path>) stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))::iterator) {
                issues.addAll(findDuplicateObjectKeys(file));
            }
        }
        assertTrue(issues.isEmpty(),
                () -> "Patchouli manual JSON files must not contain duplicate keys: " + String.join(", ", issues));
    }

    private static List<String> findDuplicateObjectKeys(Path file) throws IOException {
        String content = Files.readString(file);
        JsonScanner scanner = new JsonScanner(content);
        Set<String> duplicates = scanner.findDuplicateObjectKeys();
        List<String> issues = new ArrayList<>();
        for (String key : duplicates) {
            issues.add(file + " -> " + key);
        }
        return issues;
    }

    private static final class JsonScanner {
        private final String content;
        private int index;

        private JsonScanner(String content) {
            this.content = content;
        }

        private Set<String> findDuplicateObjectKeys() {
            skipWhitespace();
            return readValueForDuplicates();
        }

        private Set<String> readValueForDuplicates() {
            skipWhitespace();
            if (index >= content.length()) {
                return Set.of();
            }
            char current = content.charAt(index);
            if (current == '{') {
                return readObjectForDuplicates();
            }
            if (current == '[') {
                return readArrayForDuplicates();
            }
            if (current == '"') {
                readString();
                return Set.of();
            }
            readPrimitive();
            return Set.of();
        }

        private Set<String> readObjectForDuplicates() {
            expect('{');
            skipWhitespace();
            Set<String> seen = new LinkedHashSet<>();
            Set<String> duplicates = new LinkedHashSet<>();
            if (peek('}')) {
                index++;
                return duplicates;
            }
            while (index < content.length()) {
                String key = readString();
                if (!seen.add(key)) {
                    duplicates.add(key);
                }
                skipWhitespace();
                expect(':');
                duplicates.addAll(readValueForDuplicates());
                skipWhitespace();
                if (peek(',')) {
                    index++;
                    skipWhitespace();
                    continue;
                }
                expect('}');
                return duplicates;
            }
            throw new IllegalStateException("Unterminated JSON object");
        }

        private Set<String> readArrayForDuplicates() {
            expect('[');
            skipWhitespace();
            Set<String> duplicates = new LinkedHashSet<>();
            if (peek(']')) {
                index++;
                return duplicates;
            }
            while (index < content.length()) {
                duplicates.addAll(readValueForDuplicates());
                skipWhitespace();
                if (peek(',')) {
                    index++;
                    skipWhitespace();
                    continue;
                }
                expect(']');
                return duplicates;
            }
            throw new IllegalStateException("Unterminated JSON array");
        }

        private String readString() {
            expect('"');
            StringBuilder builder = new StringBuilder();
            while (index < content.length()) {
                char current = content.charAt(index++);
                if (current == '\\') {
                    if (index >= content.length()) {
                        throw new IllegalStateException("Invalid escape sequence");
                    }
                    char escaped = content.charAt(index++);
                    builder.append('\\').append(escaped);
                    if (escaped == 'u') {
                        for (int i = 0; i < 4; i++) {
                            if (index >= content.length()) {
                                throw new IllegalStateException("Invalid unicode escape");
                            }
                            builder.append(content.charAt(index++));
                        }
                    }
                    continue;
                }
                if (current == '"') {
                    return builder.toString();
                }
                builder.append(current);
            }
            throw new IllegalStateException("Unterminated JSON string");
        }

        private void readPrimitive() {
            while (index < content.length()) {
                char current = content.charAt(index);
                if (current == ',' || current == '}' || current == ']' || Character.isWhitespace(current)) {
                    return;
                }
                index++;
            }
        }

        private void skipWhitespace() {
            while (index < content.length() && Character.isWhitespace(content.charAt(index))) {
                index++;
            }
        }

        private boolean peek(char expected) {
            return index < content.length() && content.charAt(index) == expected;
        }

        private void expect(char expected) {
            if (!peek(expected)) {
                throw new IllegalStateException("Expected '" + expected + "' at position " + index);
            }
            index++;
            skipWhitespace();
        }
    }
}
