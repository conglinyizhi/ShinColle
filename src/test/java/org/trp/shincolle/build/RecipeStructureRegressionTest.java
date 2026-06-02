package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipeStructureRegressionTest {
    private static final Path RECIPE_ROOT = Path.of("src/main/resources/data/shincolle/recipe");
    private static final Path SHINCOLLE_ASSET_ROOT = Path.of("src/main/resources/assets/shincolle");
    private static final Set<String> ALLOWED_RECIPE_TYPES = Set.of(
            "minecraft:crafting_shaped",
            "minecraft:crafting_shapeless"
    );
    private static final Pattern TYPE_PATTERN =
            Pattern.compile("\"type\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern ITEM_REFERENCE_PATTERN =
            Pattern.compile("\"item\"\\s*:\\s*\"(shincolle:[^\"]+)\"");

    @Test
    void recipesShouldKeepSupportedTypesAndResolvableShincolleItemReferences() throws IOException {
        List<String> issues = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(RECIPE_ROOT)) {
            for (Path json : (Iterable<Path>) stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))::iterator) {
                String content = Files.readString(json);
                String relative = RECIPE_ROOT.relativize(json).toString().replace('\\', '/');

                String type = readFirst(TYPE_PATTERN, content);
                if (type == null || !ALLOWED_RECIPE_TYPES.contains(type)) {
                    issues.add(relative + " uses unsupported recipe type " + type);
                }

                Object parsed = new JsonScanner(content).readValue();
                if (!(parsed instanceof Map<?, ?> root)) {
                    issues.add(relative + " does not parse into a JSON object");
                    continue;
                }

                String resultRef = readResultReference(root.get("result"));
                if (resultRef == null) {
                    issues.add(relative + " is missing result.id or result.item");
                } else {
                    assertResolvableItemReference(resultRef, relative + " result", issues);
                }

                Matcher matcher = ITEM_REFERENCE_PATTERN.matcher(content);
                Set<String> itemRefs = new TreeSet<>();
                while (matcher.find()) {
                    itemRefs.add(matcher.group(1));
                }
                for (String itemRef : itemRefs) {
                    assertResolvableItemReference(itemRef, relative + " ingredient", issues);
                }
            }
        }

        assertTrue(issues.isEmpty(),
                () -> "Recipes must keep supported structure and resolvable shincolle references: "
                        + String.join(", ", issues));
    }

    private static void assertResolvableItemReference(String resourceLocation, String owner, List<String> issues) {
        if (!resourceLocation.startsWith("shincolle:")) {
            return;
        }
        String path = resourceLocation.substring("shincolle:".length());
        Path itemModel = SHINCOLLE_ASSET_ROOT.resolve("models/item").resolve(path + ".json");
        Path blockModel = SHINCOLLE_ASSET_ROOT.resolve("models/block").resolve(path + ".json");
        if (!Files.exists(itemModel) && !Files.exists(blockModel)) {
            issues.add(owner + " references missing item/block model " + resourceLocation);
        }
    }

    private static String readFirst(Pattern pattern, String content) {
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? matcher.group(1) : null;
    }

    @SuppressWarnings("unchecked")
    private static String readResultReference(Object resultNode) {
        if (!(resultNode instanceof Map<?, ?> resultMap)) {
            return null;
        }
        Object id = resultMap.get("id");
        if (id instanceof String idString) {
            return idString;
        }
        Object item = resultMap.get("item");
        return item instanceof String itemString ? itemString : null;
    }

    private static final class JsonScanner {
        private final String content;
        private int index;

        private JsonScanner(String content) {
            this.content = content;
        }

        private Object readValue() {
            skipWhitespace();
            if (index >= content.length()) {
                throw new IllegalStateException("empty json");
            }
            char current = content.charAt(index);
            if (current == '{') {
                return readObject();
            }
            if (current == '[') {
                return readArray();
            }
            if (current == '"') {
                return readString();
            }
            if (current == '-' || Character.isDigit(current)) {
                return readNumber();
            }
            if (content.startsWith("true", index)) {
                index += 4;
                return Boolean.TRUE;
            }
            if (content.startsWith("false", index)) {
                index += 5;
                return Boolean.FALSE;
            }
            if (content.startsWith("null", index)) {
                index += 4;
                return null;
            }
            throw new IllegalStateException("unexpected token at position " + index);
        }

        private Map<String, Object> readObject() {
            expect('{');
            Map<String, Object> result = new LinkedHashMap<>();
            skipWhitespace();
            if (peek('}')) {
                index++;
                return result;
            }
            while (true) {
                String key = readString();
                skipWhitespace();
                expect(':');
                result.put(key, readValue());
                skipWhitespace();
                if (peek(',')) {
                    index++;
                    skipWhitespace();
                    continue;
                }
                expect('}');
                return result;
            }
        }

        private List<Object> readArray() {
            expect('[');
            List<Object> result = new ArrayList<>();
            skipWhitespace();
            if (peek(']')) {
                index++;
                return result;
            }
            while (true) {
                result.add(readValue());
                skipWhitespace();
                if (peek(',')) {
                    index++;
                    skipWhitespace();
                    continue;
                }
                expect(']');
                return result;
            }
        }

        private String readString() {
            expect('"');
            StringBuilder builder = new StringBuilder();
            while (index < content.length()) {
                char current = content.charAt(index++);
                if (current == '\\') {
                    if (index >= content.length()) {
                        throw new IllegalStateException("invalid escape");
                    }
                    char escaped = content.charAt(index++);
                    builder.append('\\').append(escaped);
                    if (escaped == 'u') {
                        for (int i = 0; i < 4; i++) {
                            if (index >= content.length()) {
                                throw new IllegalStateException("invalid unicode escape");
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
            throw new IllegalStateException("unterminated string");
        }

        private Number readNumber() {
            int start = index;
            if (peek('-')) {
                index++;
            }
            while (index < content.length() && Character.isDigit(content.charAt(index))) {
                index++;
            }
            if (peek('.')) {
                index++;
                while (index < content.length() && Character.isDigit(content.charAt(index))) {
                    index++;
                }
            }
            if (peek('e') || peek('E')) {
                index++;
                if (peek('+') || peek('-')) {
                    index++;
                }
                while (index < content.length() && Character.isDigit(content.charAt(index))) {
                    index++;
                }
            }
            String token = content.substring(start, index);
            return token.contains(".") || token.contains("e") || token.contains("E")
                    ? Double.parseDouble(token)
                    : Long.parseLong(token);
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
                throw new IllegalStateException("expected '" + expected + "' at position " + index);
            }
            index++;
            skipWhitespace();
        }
    }
}
