package org.trp.shincolle.build

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.HashMap
import java.util.Locale
import java.util.stream.Collectors
import java.util.stream.Stream

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class ResourceHygieneRegressionTest {
    private val RESOURCE_ROOT: Path = Path.of("src/main/resources/assets/shincolle")
    private val DATA_ROOT: Path = Path.of("src/main/resources/data/shincolle")

    @Test
    fun resourcesShouldNotContainKnownDesktopIniJunkFiles() {
        Files.walk(RESOURCE_ROOT).use { stream ->
            val hasDesktopIni = stream
                    .filter(Files::isRegularFile)
                    .anyMatch { it.fileName.toString().equals("desktop.ini", ignoreCase = true) }
            assertFalse(hasDesktopIni) {
                "Resource tree must not contain desktop.ini junk files from Windows explorer"
            }
        }
    }

    @Test
    fun resourcesShouldNotContainCaseOnlyDuplicatePaths() {
        val seen = HashMap<String, Path>()

        Files.walk(RESOURCE_ROOT).use { stream ->
            for (path in stream.filter(Files::isRegularFile).toList()) {
                val relative = RESOURCE_ROOT.relativize(path).toString().replace('\\', '/')
                val folded = relative.lowercase(Locale.ROOT)
                val previous = seen.putIfAbsent(folded, path)
                assertFalse(previous != null && previous != path) {
                    "Resource tree must not contain case-only duplicate files: " +
                            RESOURCE_ROOT.relativize(previous) + " and " + relative
                }
            }
        }
    }

    @Test
    fun assetAndDataJsonFilesShouldRemainParseable() {
        Stream.concat(Files.walk(RESOURCE_ROOT), Files.walk(DATA_ROOT)).use { stream ->
            val broken = stream
                    .filter(Files::isRegularFile)
                    .filter { it.toString().endsWith(".json") }
                    .map { validateJsonFile(it) }
                    .filter { it != null }
                    .collect(Collectors.toList())

            assertTrue(broken.isEmpty()) {
                "All asset/data JSON files must remain parseable: " + broken.joinToString(", ")
            }
        }
    }

    private fun validateJsonFile(path: Path): String? {
        return try {
            parseJson(Files.readString(path))
            null
        } catch (e: Exception) {
            "$path -> ${e.message}"
        }
    }

    private fun parseJson(content: String) {
        val scanner = JsonScanner(content)
        scanner.readValue()
        scanner.ensureFullyConsumed()
    }

    private class JsonScanner(private val content: String) {
        private var index = 0

        fun readValue() {
            skipWhitespace()
            if (index >= content.length) {
                throw IllegalStateException("empty content")
            }
            val current = content[index]
            if (current == '{') {
                readObject()
                return
            }
            if (current == '[') {
                readArray()
                return
            }
            if (current == '"') {
                readString()
                return
            }
            if (current == '-' || current.isDigit()) {
                readNumber()
                return
            }
            if (content.startsWith("true", index)) {
                index += 4
                return
            }
            if (content.startsWith("false", index)) {
                index += 5
                return
            }
            if (content.startsWith("null", index)) {
                index += 4
                return
            }
            throw IllegalStateException("unexpected token at position $index")
        }

        fun readObject() {
            expect('{')
            skipWhitespace()
            if (peek('}')) {
                index++
                return
            }
            while (true) {
                readString()
                skipWhitespace()
                expect(':')
                readValue()
                skipWhitespace()
                if (peek(',')) {
                    index++
                    skipWhitespace()
                    continue
                }
                expect('}')
                return
            }
        }

        fun readArray() {
            expect('[')
            skipWhitespace()
            if (peek(']')) {
                index++
                return
            }
            while (true) {
                readValue()
                skipWhitespace()
                if (peek(',')) {
                    index++
                    skipWhitespace()
                    continue
                }
                expect(']')
                return
            }
        }

        fun readString() {
            expect('"')
            while (index < content.length) {
                val current = content[index++]
                if (current == '\\') {
                    if (index >= content.length) {
                        throw IllegalStateException("invalid escape at end of string")
                    }
                    val escaped = content[index++]
                    if (escaped == 'u') {
                        for (i in 0 until 4) {
                            if (index >= content.length || !isHex(content[index])) {
                                throw IllegalStateException("invalid unicode escape")
                            }
                            index++
                        }
                    }
                    continue
                }
                if (current == '"') {
                    return
                }
                if (current < '\u0020') {
                    throw IllegalStateException("unescaped control character in string")
                }
            }
            throw IllegalStateException("unterminated string")
        }

        fun readNumber() {
            if (peek('-')) {
                index++
            }
            readDigits()
            if (peek('.')) {
                index++
                readDigits()
            }
            if (peek('e') || peek('E')) {
                index++
                if (peek('+') || peek('-')) {
                    index++
                }
                readDigits()
            }
        }

        fun readDigits() {
            val start = index
            while (index < content.length && content[index].isDigit()) {
                index++
            }
            if (start == index) {
                throw IllegalStateException("invalid number at position $index")
            }
        }

        fun skipWhitespace() {
            while (index < content.length && content[index].isWhitespace()) {
                index++
            }
        }

        fun ensureFullyConsumed() {
            skipWhitespace()
            if (index != content.length) {
                throw IllegalStateException("trailing content at position $index")
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

        fun isHex(value: Char): Boolean {
            return (value >= '0' && value <= '9')
                    || (value >= 'a' && value <= 'f')
                    || (value >= 'A' && value <= 'F')
        }
    }
}
