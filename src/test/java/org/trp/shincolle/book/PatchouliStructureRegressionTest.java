package org.trp.shincolle.book;

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

class PatchouliStructureRegressionTest {
    private static final Path PATCHOULI_ROOT =
            Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us");
    private static final Path CATEGORY_ROOT = PATCHOULI_ROOT.resolve("categories");
    private static final Path ENTRY_ROOT = PATCHOULI_ROOT.resolve("entries");
    private static final Path SHINCOLLE_ASSET_ROOT =
            Path.of("src/main/resources/assets/shincolle");
    private static final Pattern STRING_FIELD_PATTERN_TEMPLATE =
            Pattern.compile("\"%s\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern NUMERIC_FIELD_PATTERN_TEMPLATE =
            Pattern.compile("\"%s\"\\s*:\\s*(-?\\d+)");
    private static final Pattern PAGES_PATTERN =
            Pattern.compile("\"pages\"\\s*:\\s*\\[(.*?)]", Pattern.DOTALL);

    @Test
    void patchouliCategoriesShouldKeepRequiredFields() throws IOException {
        List<String> issues = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(CATEGORY_ROOT)) {
            for (Path json : (Iterable<Path>) stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))::iterator) {
                String content = Files.readString(json);
                assertStringField(content, json, "name", issues);
                assertStringField(content, json, "description", issues);
                String icon = readStringField(content, "icon");
                if (icon == null) {
                    issues.add(relativePath(json) + " missing field icon");
                } else {
                    assertIconResolvable(icon, json, issues);
                }
                assertNumericField(content, json, "sortnum", issues);
            }
        }

        assertTrue(issues.isEmpty(),
                () -> "Patchouli categories must keep required fields: " + String.join(", ", issues));
    }

    @Test
    void patchouliEntriesShouldKeepRequiredFieldsAndReferenceExistingCategories() throws IOException {
        Set<String> existingCategories;
        try (Stream<Path> stream = Files.walk(CATEGORY_ROOT)) {
            existingCategories = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(path -> stripJsonExtension(CATEGORY_ROOT.relativize(path).toString().replace('\\', '/')))
                    .collect(Collectors.toCollection(TreeSet::new));
        }

        List<String> issues = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(ENTRY_ROOT)) {
            for (Path json : (Iterable<Path>) stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))::iterator) {
                String content = Files.readString(json);
                assertStringField(content, json, "name", issues);
                String icon = readStringField(content, "icon");
                if (icon == null) {
                    issues.add(relativePath(json) + " missing field icon");
                } else {
                    assertIconResolvable(icon, json, issues);
                }
                String category = readStringField(content, "category");
                if (category == null || category.isBlank()) {
                    issues.add(ENTRY_ROOT.relativize(json) + " missing field category");
                } else {
                    String normalizedCategory = category.startsWith("shincolle:")
                            ? category.substring("shincolle:".length())
                            : category;
                    if (!existingCategories.contains(normalizedCategory)) {
                        issues.add(ENTRY_ROOT.relativize(json) + " references missing category " + category);
                    }
                }

                String pagesContent = readArrayContent(content, "pages");
                if (pagesContent == null || pagesContent.isBlank()) {
                    issues.add(ENTRY_ROOT.relativize(json) + " missing non-empty pages array");
                }
            }
        }

        assertTrue(issues.isEmpty(),
                () -> "Patchouli entries must keep required fields and valid categories: " + String.join(", ", issues));
    }

    private static void assertStringField(String content, Path file, String field, List<String> issues) {
        if (readStringField(content, field) == null) {
            issues.add(relativePath(file) + " missing field " + field);
        }
    }

    private static void assertNumericField(String content, Path file, String field, List<String> issues) {
        Pattern pattern = Pattern.compile(String.format(NUMERIC_FIELD_PATTERN_TEMPLATE.pattern(), field));
        if (!pattern.matcher(content).find()) {
            issues.add(relativePath(file) + " missing field " + field);
        }
    }

    private static String readStringField(String content, String field) {
        Pattern pattern = Pattern.compile(String.format(STRING_FIELD_PATTERN_TEMPLATE.pattern(), field));
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static String readArrayContent(String content, String field) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\\[(.*?)]", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private static void assertIconResolvable(String icon, Path file, List<String> issues) {
        if (!icon.startsWith("shincolle:")) {
            return;
        }
        String resourcePath = icon.substring("shincolle:".length());
        Path itemModel = SHINCOLLE_ASSET_ROOT.resolve("models/item").resolve(resourcePath + ".json");
        Path blockModel = SHINCOLLE_ASSET_ROOT.resolve("models/block").resolve(resourcePath + ".json");
        if (!Files.exists(itemModel) && !Files.exists(blockModel)) {
            issues.add(relativePath(file) + " references missing icon model " + icon);
        }
    }

    private static String relativePath(Path file) {
        if (file.startsWith(CATEGORY_ROOT)) {
            return CATEGORY_ROOT.relativize(file).toString().replace('\\', '/');
        }
        if (file.startsWith(ENTRY_ROOT)) {
            return ENTRY_ROOT.relativize(file).toString().replace('\\', '/');
        }
        return PATCHOULI_ROOT.relativize(file).toString().replace('\\', '/');
    }

    private static String stripJsonExtension(String value) {
        return value.endsWith(".json") ? value.substring(0, value.length() - 5) : value;
    }
}
