package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceHygieneRegressionTest {
    private static final Path RESOURCE_ROOT = Path.of("src/main/resources/assets/shincolle");
    private static final Path DATA_ROOT = Path.of("src/main/resources/data/shincolle");

    @Test
    void resourcesShouldNotContainKnownDesktopIniJunkFiles() throws IOException {
        try (Stream<Path> stream = Files.walk(RESOURCE_ROOT)) {
            boolean hasDesktopIni = stream
                    .filter(Files::isRegularFile)
                    .anyMatch(path -> path.getFileName().toString().equalsIgnoreCase("desktop.ini"));
            assertFalse(hasDesktopIni,
                    "Resource tree must not contain desktop.ini junk files from Windows explorer");
        }
    }

    @Test
    void resourcesShouldNotContainCaseOnlyDuplicatePaths() throws IOException {
        Map<String, Path> seen = new HashMap<>();

        try (Stream<Path> stream = Files.walk(RESOURCE_ROOT)) {
            for (Path path : (Iterable<Path>) stream.filter(Files::isRegularFile)::iterator) {
                String relative = RESOURCE_ROOT.relativize(path).toString().replace('\\', '/');
                String folded = relative.toLowerCase(Locale.ROOT);
                Path previous = seen.putIfAbsent(folded, path);
                assertFalse(previous != null && !previous.equals(path),
                        () -> "Resource tree must not contain case-only duplicate files: "
                                + RESOURCE_ROOT.relativize(previous) + " and " + relative);
            }
        }
    }

    @Test
    void assetAndDataJsonFilesShouldRemainParseable() throws IOException {
        try (Stream<Path> stream = Stream.concat(Files.walk(RESOURCE_ROOT), Files.walk(DATA_ROOT))) {
            var broken = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(this::validateJsonFile)
                    .filter(result -> result != null)
                    .collect(Collectors.toList());

            assertTrue(broken.isEmpty(),
                    () -> "All asset/data JSON files must remain parseable: " + String.join(", ", broken));
        }
    }

    private String validateJsonFile(Path path) {
        try {
            parseJson(Files.readString(path));
            return null;
        } catch (Exception e) {
            return path + " -> " + e.getMessage();
        }
    }

    private void parseJson(String content) {
        JsonScanner scanner = new JsonScanner(content);
        scanner.readValue();
        scanner.ensureFullyConsumed();
    }

    private static final class JsonScanner {
        private final String content;
        private int index;

        private JsonScanner(String content) {
            this.content = content;
        }

        private void readValue() {
            skipWhitespace();
            if (index >= content.length()) {
                throw new IllegalStateException("empty content");
            }
            char current = content.charAt(index);
            if (current == '{') {
                readObject();
                return;
            }
            if (current == '[') {
                readArray();
                return;
            }
            if (current == '"') {
                readString();
                return;
            }
            if (current == '-' || Character.isDigit(current)) {
                readNumber();
                return;
            }
            if (content.startsWith("true", index)) {
                index += 4;
                return;
            }
            if (content.startsWith("false", index)) {
                index += 5;
                return;
            }
            if (content.startsWith("null", index)) {
                index += 4;
                return;
            }
            throw new IllegalStateException("unexpected token at position " + index);
        }

        private void readObject() {
            expect('{');
            skipWhitespace();
            if (peek('}')) {
                index++;
                return;
            }
            while (true) {
                readString();
                skipWhitespace();
                expect(':');
                readValue();
                skipWhitespace();
                if (peek(',')) {
                    index++;
                    skipWhitespace();
                    continue;
                }
                expect('}');
                return;
            }
        }

        private void readArray() {
            expect('[');
            skipWhitespace();
            if (peek(']')) {
                index++;
                return;
            }
            while (true) {
                readValue();
                skipWhitespace();
                if (peek(',')) {
                    index++;
                    skipWhitespace();
                    continue;
                }
                expect(']');
                return;
            }
        }

        private void readString() {
            expect('"');
            while (index < content.length()) {
                char current = content.charAt(index++);
                if (current == '\\') {
                    if (index >= content.length()) {
                        throw new IllegalStateException("invalid escape at end of string");
                    }
                    char escaped = content.charAt(index++);
                    if (escaped == 'u') {
                        for (int i = 0; i < 4; i++) {
                            if (index >= content.length() || !isHex(content.charAt(index))) {
                                throw new IllegalStateException("invalid unicode escape");
                            }
                            index++;
                        }
                    }
                    continue;
                }
                if (current == '"') {
                    return;
                }
                if (current < 0x20) {
                    throw new IllegalStateException("unescaped control character in string");
                }
            }
            throw new IllegalStateException("unterminated string");
        }

        private void readNumber() {
            if (peek('-')) {
                index++;
            }
            readDigits();
            if (peek('.')) {
                index++;
                readDigits();
            }
            if (peek('e') || peek('E')) {
                index++;
                if (peek('+') || peek('-')) {
                    index++;
                }
                readDigits();
            }
        }

        private void readDigits() {
            int start = index;
            while (index < content.length() && Character.isDigit(content.charAt(index))) {
                index++;
            }
            if (start == index) {
                throw new IllegalStateException("invalid number at position " + index);
            }
        }

        private void skipWhitespace() {
            while (index < content.length() && Character.isWhitespace(content.charAt(index))) {
                index++;
            }
        }

        private void ensureFullyConsumed() {
            skipWhitespace();
            if (index != content.length()) {
                throw new IllegalStateException("trailing content at position " + index);
            }
        }

        private boolean peek(char expected) {
            return index < content.length() && content.charAt(index) == expected;
        }

        private void expect(char expected) {
            if (!peek(expected)) {
                throw new IllegalStateException("expected '" + expected + "' at position " + index);
            }
            index++;
            skipWhitespace();
        }

        private boolean isHex(char value) {
            return (value >= '0' && value <= '9')
                    || (value >= 'a' && value <= 'f')
                    || (value >= 'A' && value <= 'F');
        }
    }
}
