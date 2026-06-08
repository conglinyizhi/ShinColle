package org.trp.shincolle.build

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.LinkedHashSet
import org.junit.jupiter.api.Assertions.assertTrue

class JsonDuplicateKeyRegressionTest {
    private val LANG_ROOT: Path = Path.of("src/main/resources/assets/shincolle/lang")
    private val PATCHOULI_ROOT: Path =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us")

    @Test
    fun languageFilesShouldNotContainDuplicateKeys() {
        val issues = ArrayList<String>()
        val stream = Files.list(LANG_ROOT)
        try {
            stream
                .filter { path -> Files.isRegularFile(path) }
                .filter { path -> path.toString().endsWith(".json") }
                .forEach { file ->
                    issues.addAll(findDuplicateObjectKeys(file))
                }
        } finally {
            stream.close()
        }
        assertTrue(issues.isEmpty()) {
            "Language JSON files must not contain duplicate keys: " + issues.joinToString(", ")
        }
    }

    @Test
    fun patchouliManualJsonShouldNotContainDuplicateKeys() {
        val issues = ArrayList<String>()
        val stream = Files.walk(PATCHOULI_ROOT)
        try {
            stream
                .filter { path -> Files.isRegularFile(path) }
                .filter { path -> path.toString().endsWith(".json") }
                .forEach { file ->
                    issues.addAll(findDuplicateObjectKeys(file))
                }
        } finally {
            stream.close()
        }
        assertTrue(issues.isEmpty()) {
            "Patchouli manual JSON files must not contain duplicate keys: " + issues.joinToString(", ")
        }
    }

    private fun findDuplicateObjectKeys(file: Path): List<String> {
        val content = Files.readString(file)
        val scanner = JsonScanner(content)
        val duplicates = scanner.findDuplicateObjectKeys()
        val issues = ArrayList<String>()
        for (key in duplicates) {
            issues.add(file.toString() + " -> " + key)
        }
        return issues
    }

    private class JsonScanner(private val content: String) {
        private var index: Int = 0

        fun findDuplicateObjectKeys(): Set<String> {
            skipWhitespace()
            return readValueForDuplicates()
        }

        private fun readValueForDuplicates(): Set<String> {
            skipWhitespace()
            if (index >= content.length) {
                return emptySet()
            }
            val current = content[index]
            if (current == '{') {
                return readObjectForDuplicates()
            }
            if (current == '[') {
                return readArrayForDuplicates()
            }
            if (current == '"') {
                readString()
                return emptySet()
            }
            readPrimitive()
            return emptySet()
        }

        private fun readObjectForDuplicates(): Set<String> {
            expect('{')
            skipWhitespace()
            val seen = LinkedHashSet<String>()
            val duplicates = LinkedHashSet<String>()
            if (peek('}')) {
                index++
                return duplicates
            }
            while (index < content.length) {
                val key = readString()
                if (!seen.add(key)) {
                    duplicates.add(key)
                }
                skipWhitespace()
                expect(':')
                duplicates.addAll(readValueForDuplicates())
                skipWhitespace()
                if (peek(',')) {
                    index++
                    skipWhitespace()
                    continue
                }
                expect('}')
                return duplicates
            }
            throw IllegalStateException("Unterminated JSON object")
        }

        private fun readArrayForDuplicates(): Set<String> {
            expect('[')
            skipWhitespace()
            val duplicates = LinkedHashSet<String>()
            if (peek(']')) {
                index++
                return duplicates
            }
            while (index < content.length) {
                duplicates.addAll(readValueForDuplicates())
                skipWhitespace()
                if (peek(',')) {
                    index++
                    skipWhitespace()
                    continue
                }
                expect(']')
                return duplicates
            }
            throw IllegalStateException("Unterminated JSON array")
        }

        private fun readString(): String {
            expect('"')
            val builder = StringBuilder()
            while (index < content.length) {
                val current = content[index++]
                if (current == '\\') {
                    if (index >= content.length) {
                        throw IllegalStateException("Invalid escape sequence")
                    }
                    val escaped = content[index++]
                    builder.append('\\').append(escaped)
                    if (escaped == 'u') {
                        for (i in 0..3) {
                            if (index >= content.length) {
                                throw IllegalStateException("Invalid unicode escape")
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
            throw IllegalStateException("Unterminated JSON string")
        }

        private fun readPrimitive() {
            while (index < content.length) {
                val current = content[index]
                if (current == ',' || current == '}' || current == ']' || Character.isWhitespace(current)) {
                    return
                }
                index++
            }
        }

        private fun skipWhitespace() {
            while (index < content.length && Character.isWhitespace(content[index])) {
                index++
            }
        }

        private fun peek(expected: Char): Boolean {
            return index < content.length && content[index] == expected
        }

        private fun expect(expected: Char) {
            if (!peek(expected)) {
                throw IllegalStateException("Expected '" + expected + "' at position " + index)
            }
            index++
            skipWhitespace()
        }
    }
}
