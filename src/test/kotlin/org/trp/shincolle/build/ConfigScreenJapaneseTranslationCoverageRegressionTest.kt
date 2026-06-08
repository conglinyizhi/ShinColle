package org.trp.shincolle.build

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.TreeSet
import java.util.regex.Pattern
import java.util.stream.Collectors

class ConfigScreenJapaneseTranslationCoverageRegressionTest {
    private val CONFIG_SCREEN_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/client/gui/ShincolleConfigScreen.kt")
    private val JA_JP_LANG: Path =
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json")
    private val CONFIG_KEY_PATTERN: Pattern =
            Pattern.compile("Component\\.translatable\\(\"(config\\.shincolle\\.[^\"]+)\"")

    @Test
    fun japaneseShouldCoverEveryConfigScreenTranslationKey() {
        val japaneseKeys = readKeys(JA_JP_LANG)
        val configScreen = Files.readString(CONFIG_SCREEN_SOURCE)
        val matcher = CONFIG_KEY_PATTERN.matcher(configScreen)
        val missing = TreeSet<String>()

        while (matcher.find()) {
            val key = matcher.group(1)!!
            if (!japaneseKeys.contains(key)) {
                missing.add(key)
            }
        }

        assertTrue(missing.isEmpty()) {
            "Japanese language file should cover every ShincolleConfigScreen translation key, missing: " +
                    missing.joinToString(", ")
        }
    }

    private fun readKeys(file: Path): Set<String> {
        return Files.readAllLines(file).stream()
                .map(String::trim)
                .filter { line -> line.startsWith("\"") }
                .map { line -> line.substring(1, line.indexOf('"', 1)) }
                .collect(Collectors.toSet())
    }
}
