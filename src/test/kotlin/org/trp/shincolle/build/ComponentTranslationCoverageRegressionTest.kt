package org.trp.shincolle.build

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.TreeSet
import java.util.regex.Pattern
import java.util.stream.Collectors
import java.util.stream.Stream

class ComponentTranslationCoverageRegressionTest {
    private val JAVA_ROOT: Path = Path.of("src/main/java")
    private val EN_US_LANG: Path = Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val BLOCK_COMMENT_PATTERN: Pattern = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL)
    private val LINE_COMMENT_PATTERN: Pattern = Pattern.compile("//.*")
    private val TRANSLATABLE_PATTERN: Pattern = Pattern.compile(
            "(?:^|\\W)(?:net\\.minecraft\\.network\\.chat\\.)?Component\\.translatable\\(\"([^\"]+)\"\\s*(?:,|\\))")

    @Test
    fun englishLanguageShouldCoverAllLiteralComponentTranslationKeys() {
        val englishKeys = readKeys(EN_US_LANG)
        val missing = ArrayList<String>()

        Files.walk(JAVA_ROOT).use { stream ->
            for (javaFile in stream
                    .filter(Files::isRegularFile)
                    .filter { path -> path.toString().endsWith(".kt") }
                    .toList()) {
                val source = stripComments(Files.readString(javaFile))
                val matcher = TRANSLATABLE_PATTERN.matcher(source)
                val fileKeys = TreeSet<String>()
                while (matcher.find()) {
                    fileKeys.add(matcher.group(1)!!)
                }
                for (key in fileKeys) {
                    if (!englishKeys.contains(key)) {
                        missing.add(javaFile.toString() + " -> " + key)
                    }
                }
            }
        }

        assertTrue(missing.isEmpty()) {
            "English language file must cover all literal Component.translatable keys: " +
                    missing.joinToString(", ")
        }
    }

    private fun stripComments(source: String): String {
        val withoutBlockComments = BLOCK_COMMENT_PATTERN.matcher(source).replaceAll("")
        return LINE_COMMENT_PATTERN.matcher(withoutBlockComments).replaceAll("")
    }

    private fun readKeys(file: Path): Set<String> {
        return Files.readAllLines(file).stream()
                .map(String::trim)
                .filter { line -> line.startsWith("\"") }
                .map { line -> line.substring(1, line.indexOf('"', 1)) }
                .collect(Collectors.toSet())
    }
}
