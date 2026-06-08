package org.trp.shincolle.book

import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.HashMap
import java.util.TreeSet
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertTrue

class PatchouliStructureRegressionTest {
    private val PATCHOULI_ROOT: Path =
            Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us")
    private val CATEGORY_ROOT: Path = PATCHOULI_ROOT.resolve("categories")
    private val ENTRY_ROOT: Path = PATCHOULI_ROOT.resolve("entries")
    private val SHINCOLLE_ASSET_ROOT: Path =
            Path.of("src/main/resources/assets/shincolle")
    private val RECIPE_ROOT: Path =
            Path.of("src/main/resources/data/shincolle/recipe")
    private val EN_US_LANG: Path =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val STRING_FIELD_PATTERN_TEMPLATE: Pattern =
            Pattern.compile("\"%s\"\\s*:\\s*\"([^\"]+)\"")
    private val NUMERIC_FIELD_PATTERN_TEMPLATE: Pattern =
            Pattern.compile("\"%s\"\\s*:\\s*(-?\\d+)")
    private val CRAFTING_PAGE_PATTERN: Pattern =
            Pattern.compile("\\{[^{}]*\"type\"\\s*:\\s*\"patchouli:crafting\"[^{}]*}", Pattern.DOTALL)
    private val ENTITY_PAGE_PATTERN: Pattern =
            Pattern.compile("\\{[^{}]*\"type\"\\s*:\\s*\"patchouli:entity\"[^{}]*}", Pattern.DOTALL)

    @Test
    fun patchouliCategoriesShouldKeepRequiredFields() {
        val issues = ArrayList<String>()

        Files.walk(CATEGORY_ROOT).use { stream ->
            for (json in Iterable { stream.filter(Files::isRegularFile).filter { path -> path.toString().endsWith(".json") }.iterator() }) {
                val content = Files.readString(json)
                assertStringField(content, json, "name", issues)
                assertStringField(content, json, "description", issues)
                val icon = readStringField(content, "icon")
                if (icon == null) {
                    issues.add(relativePath(json) + " missing field icon")
                } else {
                    assertIconResolvable(icon, json, issues)
                }
                assertNumericField(content, json, "sortnum", issues)
            }
        }

        assertTrue(issues.isEmpty()) {
            "Patchouli categories must keep required fields: " + issues.joinToString(", ")
        }
    }

    @Test
    fun patchouliEntriesShouldKeepRequiredFieldsAndReferenceExistingCategories() {
        val englishLang = Files.readString(EN_US_LANG)
        val existingCategories = Files.walk(CATEGORY_ROOT).use { stream ->
            stream
                    .filter(Files::isRegularFile)
                    .filter { path -> path.toString().endsWith(".json") }
                    .map { path -> stripJsonExtension(CATEGORY_ROOT.relativize(path).toString().replace('\\', '/')) }
                    .collect(Collectors.toCollection { TreeSet<String>() })
        }
        val existingRecipes = Files.walk(RECIPE_ROOT).use { stream ->
            stream
                    .filter(Files::isRegularFile)
                    .filter { path -> path.toString().endsWith(".json") }
                    .map { path -> stripJsonExtension(RECIPE_ROOT.relativize(path).toString().replace('\\', '/')) }
                    .collect(Collectors.toCollection { TreeSet<String>() })
        }

        val issues = ArrayList<String>()
        val categoryEntryCounts = HashMap<String, Int>()
        val categorySortnums = HashMap<String, HashMap<Int, String>>()
        Files.walk(ENTRY_ROOT).use { stream ->
            for (json in Iterable { stream.filter(Files::isRegularFile).filter { path -> path.toString().endsWith(".json") }.iterator() }) {
                val content = Files.readString(json)
                assertStringField(content, json, "name", issues)
                val icon = readStringField(content, "icon")
                if (icon == null) {
                    issues.add(ENTRY_ROOT.relativize(json).toString() + " missing field icon")
                } else {
                    assertIconResolvable(icon, json, issues)
                }
                val category = readStringField(content, "category")
                if (category == null || category.isBlank()) {
                    issues.add(ENTRY_ROOT.relativize(json).toString() + " missing field category")
                } else {
                    val normalizedCategory = if (category.startsWith("shincolle:"))
                            category.substring("shincolle:".length)
                        else
                            category
                    if (!existingCategories.contains(normalizedCategory)) {
                        issues.add(ENTRY_ROOT.relativize(json).toString() + " references missing category " + category)
                    } else {
                        categoryEntryCounts.merge(normalizedCategory, 1, Integer::sum)
                    }

                    val sortnum = readNumericField(content, "sortnum")
                    if (sortnum == null) {
                        issues.add(ENTRY_ROOT.relativize(json).toString() + " missing field sortnum")
                    } else {
                        val seenSortnums =
                                categorySortnums.computeIfAbsent(normalizedCategory) { HashMap() }
                        val previous = seenSortnums.putIfAbsent(sortnum, ENTRY_ROOT.relativize(json).toString().replace('\\', '/'))
                        if (previous != null) {
                            issues.add(normalizedCategory + " has duplicate sortnum " + sortnum
                                    + " for " + previous + " and " + ENTRY_ROOT.relativize(json).toString().replace('\\', '/'))
                        }
                    }
                }

                val pagesContent = readArrayContent(content, "pages")
                if (pagesContent == null || pagesContent.isBlank()) {
                    issues.add(ENTRY_ROOT.relativize(json).toString() + " missing non-empty pages array")
                }

                validatePageReferences(content, json, existingRecipes, englishLang, issues)
            }
        }

        for (category in existingCategories) {
            if (!categoryEntryCounts.containsKey(category)) {
                issues.add("category " + category + " has no entries")
            }
        }

        assertTrue(issues.isEmpty()) {
            "Patchouli entries must keep required fields and valid categories: " + issues.joinToString(", ")
        }
    }

    private fun assertStringField(content: String, file: Path, field: String, issues: MutableList<String>) {
        if (readStringField(content, field) == null) {
            issues.add(relativePath(file) + " missing field " + field)
        }
    }

    private fun assertNumericField(content: String, file: Path, field: String, issues: MutableList<String>) {
        val pattern = Pattern.compile(String.format(NUMERIC_FIELD_PATTERN_TEMPLATE.pattern(), field))
        if (!pattern.matcher(content).find()) {
            issues.add(relativePath(file) + " missing field " + field)
        }
    }

    private fun readStringField(content: String, field: String): String? {
        val pattern = Pattern.compile(String.format(STRING_FIELD_PATTERN_TEMPLATE.pattern(), field))
        val matcher = pattern.matcher(content)
        return if (matcher.find()) matcher.group(1)!! else null
    }

    private fun readNumericField(content: String, field: String): Int? {
        val pattern = Pattern.compile(String.format(NUMERIC_FIELD_PATTERN_TEMPLATE.pattern(), field))
        val matcher = pattern.matcher(content)
        return if (matcher.find()) Integer.parseInt(matcher.group(1)!!) else null
    }

    private fun readArrayContent(content: String, field: String): String? {
        val pattern = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\\[(.*?)]", Pattern.DOTALL)
        val matcher = pattern.matcher(content)
        return if (matcher.find()) matcher.group(1)!!.trim() else null
    }

    private fun assertIconResolvable(icon: String, file: Path, issues: MutableList<String>) {
        if (!icon.startsWith("shincolle:")) {
            return
        }
        val resourcePath = icon.substring("shincolle:".length)
        val itemModel = SHINCOLLE_ASSET_ROOT.resolve("models/item").resolve(resourcePath + ".json")
        val blockModel = SHINCOLLE_ASSET_ROOT.resolve("models/block").resolve(resourcePath + ".json")
        if (!Files.exists(itemModel) && !Files.exists(blockModel)) {
            issues.add(relativePath(file) + " references missing icon model " + icon)
        }
    }

    private fun validatePageReferences(
            content: String,
            file: Path,
            existingRecipes: Set<String>,
            englishLang: String,
            issues: MutableList<String>
    ) {
        val relative = ENTRY_ROOT.relativize(file).toString().replace('\\', '/')
        val craftingMatcher = CRAFTING_PAGE_PATTERN.matcher(content)
        while (craftingMatcher.find()) {
            val page = craftingMatcher.group()
            val recipe = readStringField(page, "recipe")
            if (recipe != null && recipe.startsWith("shincolle:")) {
                val normalizedRecipe = recipe.substring("shincolle:".length)
                if (!existingRecipes.contains(normalizedRecipe)) {
                    issues.add(relative + " references missing recipe " + recipe)
                }
            }
        }

        val entityMatcher = ENTITY_PAGE_PATTERN.matcher(content)
        while (entityMatcher.find()) {
            val page = entityMatcher.group()
            val entity = readStringField(page, "entity")
            if (entity != null && entity.startsWith("shincolle:")) {
                val entityKey = "\"entity.shincolle." + entity.substring("shincolle:".length) + "\""
                if (!englishLang.contains(entityKey)) {
                    issues.add(relative + " references entity without English translation " + entity)
                }
            }
        }
    }

    private fun relativePath(file: Path): String {
        return when {
            file.startsWith(CATEGORY_ROOT) -> CATEGORY_ROOT.relativize(file).toString().replace('\\', '/')
            file.startsWith(ENTRY_ROOT) -> ENTRY_ROOT.relativize(file).toString().replace('\\', '/')
            else -> PATCHOULI_ROOT.relativize(file).toString().replace('\\', '/')
        }
    }

    private fun stripJsonExtension(value: String): String {
        return if (value.endsWith(".json")) value.substring(0, value.length - 5) else value
    }
}
