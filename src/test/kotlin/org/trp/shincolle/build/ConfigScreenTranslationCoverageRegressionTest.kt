package org.trp.shincolle.build

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.TreeSet
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors
import org.junit.jupiter.api.Assertions.assertTrue

class ConfigScreenTranslationCoverageRegressionTest {
    private val CONFIG_SCREEN_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/client/gui/ShincolleConfigScreen.kt")
    private val EN_US_LANG: Path =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val CONFIG_KEY_PATTERN: Pattern =
        Pattern.compile("Component\\.translatable\\(\"(config\\.shincolle\\.[^\"]+)\"")

    @Test
    fun englishLanguageShouldCoverEveryConfigScreenTranslationKey() {
        val englishKeys = readKeys(EN_US_LANG)
        val configScreen = Files.readString(CONFIG_SCREEN_SOURCE)
        val matcher: Matcher = CONFIG_KEY_PATTERN.matcher(configScreen)
        val missing = TreeSet<String>()

        while (matcher.find()) {
            val key = matcher.group(1)!!
            if (!englishKeys.contains(key)) {
                missing.add(key)
            }
        }

        assertTrue(missing.isEmpty()) {
            "English language file should cover every ShincolleConfigScreen translation key, missing: " +
                missing.joinToString(", ")
        }
    }

    private fun readKeys(file: Path): Set<String> {
        return Files.readAllLines(file).stream()
            .map { line -> line.trim() }
            .filter { line -> line.startsWith("\"") }
            .map { line -> line.substring(1, line.indexOf('"', 1)) }
            .collect(Collectors.toSet())
    }
}
