package org.trp.shincolle.build

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.TreeSet
import java.util.regex.Pattern

import org.junit.jupiter.api.Assertions.assertTrue

class RecipeStructureRegressionTest {
    private val RECIPE_ROOT: Path = Path.of("src/main/resources/data/shincolle/recipe")
    private val SHINCOLLE_ASSET_ROOT: Path = Path.of("src/main/resources/assets/shincolle")
    private val ALLOWED_RECIPE_TYPES: Set<String> = setOf(
            "minecraft:crafting_shaped",
            "minecraft:crafting_shapeless"
    )
    private val TYPE_PATTERN: Pattern =
            Pattern.compile("\"type\"\\s*:\\s*\"([^\"]+)\"")
    private val ITEM_REFERENCE_PATTERN: Pattern =
            Pattern.compile("\"item\"\\s*:\\s*\"(shincolle:[^\"]+)\"")

    @Test
    fun recipesShouldKeepSupportedTypesAndResolvableShincolleItemReferences() {
        val issues = ArrayList<String>()

        Files.walk(RECIPE_ROOT).use { stream ->
            for (json in stream
                    .filter(Files::isRegularFile)
                    .filter { it.toString().endsWith(".json") }
                    .toList()) {
                val content = Files.readString(json)
                val relative = RECIPE_ROOT.relativize(json).toString().replace('\\', '/')

                val type = readFirst(TYPE_PATTERN, content)
                if (type == null || !ALLOWED_RECIPE_TYPES.contains(type)) {
                    issues.add(relative + " uses unsupported recipe type " + type)
                }

                val parsed = JsonScanner(content).readValue()
                if (parsed !is Map<*, *>) {
                    issues.add(relative + " does not parse into a JSON object")
                    continue
                }
                val root = parsed as Map<*, *>

                val resultRef = readResultReference(root["result"])
                if (resultRef == null) {
                    issues.add(relative + " is missing result.id or result.item")
                } else {
                    assertResolvableItemReference(resultRef, relative + " result", issues)
                }

                val matcher = ITEM_REFERENCE_PATTERN.matcher(content)
                val itemRefs = TreeSet<String>()
                while (matcher.find()) {
                    itemRefs.add(matcher.group(1)!!)
                }
                for (itemRef in itemRefs) {
                    assertResolvableItemReference(itemRef, relative + " ingredient", issues)
                }
            }
        }

        assertTrue(issues.isEmpty()) {
            "Recipes must keep supported structure and resolvable shincolle references: " +
                    issues.joinToString(", ")
        }
    }

    private fun assertResolvableItemReference(resourceLocation: String, owner: String, issues: MutableList<String>) {
        if (!resourceLocation.startsWith("shincolle:")) {
            return
        }
        val path = resourceLocation.substring("shincolle:".length)
        val itemModel = SHINCOLLE_ASSET_ROOT.resolve("models/item").resolve(path + ".json")
        val blockModel = SHINCOLLE_ASSET_ROOT.resolve("models/block").resolve(path + ".json")
        if (!Files.exists(itemModel) && !Files.exists(blockModel)) {
            issues.add(owner + " references missing item/block model " + resourceLocation)
        }
    }

    private fun readFirst(pattern: Pattern, content: String): String? {
        val matcher = pattern.matcher(content)
        return if (matcher.find()) matcher.group(1)!! else null
    }

    @Suppress("UNCHECKED_CAST")
    private fun readResultReference(resultNode: Any?): String? {
        if (resultNode !is Map<*, *>) {
            return null
        }
        val id = resultNode["id"]
        if (id is String) {
            return id
        }
        val item = resultNode["item"]
        return item as? String
    }

    private class JsonScanner(private val content: String) {
        private var index = 0

        fun readValue(): Any? {
            skipWhitespace()
            if (index >= content.length) {
                throw IllegalStateException("empty json")
            }
            val current = content[index]
            if (current == '{') {
                return readObject()
            }
            if (current == '[') {
                return readArray()
            }
            if (current == '"') {
                return readString()
            }
            if (current == '-' || current.isDigit()) {
                return readNumber()
            }
            if (content.startsWith("true", index)) {
                index += 4
                return true
            }
            if (content.startsWith("false", index)) {
                index += 5
                return false
            }
            if (content.startsWith("null", index)) {
                index += 4
                return null
            }
            throw IllegalStateException("unexpected token at position $index")
        }

        fun readObject(): MutableMap<String, Any?> {
            expect('{')
            val result = LinkedHashMap<String, Any?>()
            skipWhitespace()
            if (peek('}')) {
                index++
                return result
            }
            while (true) {
                val key = readString()
                skipWhitespace()
                expect(':')
                result[key] = readValue()
                skipWhitespace()
                if (peek(',')) {
                    index++
                    skipWhitespace()
                    continue
                }
                expect('}')
                return result
            }
        }

        fun readArray(): MutableList<Any?> {
            expect('[')
            val result = ArrayList<Any?>()
            skipWhitespace()
            if (peek(']')) {
                index++
                return result
            }
            while (true) {
                result.add(readValue())
                skipWhitespace()
                if (peek(',')) {
                    index++
                    skipWhitespace()
                    continue
                }
                expect(']')
                return result
            }
        }

        fun readString(): String {
            expect('"')
            val builder = StringBuilder()
            while (index < content.length) {
                val current = content[index++]
                if (current == '\\') {
                    if (index >= content.length) {
                        throw IllegalStateException("invalid escape")
                    }
                    val escaped = content[index++]
                    builder.append('\\').append(escaped)
                    if (escaped == 'u') {
                        for (i in 0 until 4) {
                            if (index >= content.length) {
                                throw IllegalStateException("invalid unicode escape")
                            }
                            builder.append(content[index++])
                        }
                    }
                    continue
                }
                if (current == '"') {
                    return builder.toString()
                }
                builder.append(current)
            }
            throw IllegalStateException("unterminated string")
        }

        fun readNumber(): Number {
            val start = index
            if (peek('-')) {
                index++
            }
            while (index < content.length && content[index].isDigit()) {
                index++
            }
            if (peek('.')) {
                index++
                while (index < content.length && content[index].isDigit()) {
                    index++
                }
            }
            if (peek('e') || peek('E')) {
                index++
                if (peek('+') || peek('-')) {
                    index++
                }
                while (index < content.length && content[index].isDigit()) {
                    index++
                }
            }
            val token = content.substring(start, index)
            return if (token.contains(".") || token.contains("e") || token.contains("E"))
                token.toDouble()
            else
                token.toLong()
        }

        fun skipWhitespace() {
            while (index < content.length && content[index].isWhitespace()) {
                index++
            }
        }

        fun peek(expected: Char): Boolean {
            return index < content.length && content[index] == expected
        }

        fun expect(expected: Char) {
            if (!peek(expected)) {
                throw IllegalStateException("expected '$expected' at position $index")
            }
            index++
            skipWhitespace()
        }
    }
}
