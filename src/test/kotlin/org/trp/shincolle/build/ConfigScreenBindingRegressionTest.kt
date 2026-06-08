package org.trp.shincolle.build

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.TreeSet
import java.util.regex.Pattern

class ConfigScreenBindingRegressionTest {
    private val CONFIG_SCREEN_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/client/gui/ShincolleConfigScreen.kt")
    private val CONFIG_GET_PATTERN: Pattern =
            Pattern.compile("Config\\.([A-Z0-9_]+)\\.get\\(\\)")
    private val CONFIG_SET_PATTERN: Pattern =
            Pattern.compile("Config\\.([A-Z0-9_]+)::set")

    @Test
    fun everyConfigValueShownInConfigScreenShouldKeepItsSaveBinding() {
        val configScreen = Files.readString(CONFIG_SCREEN_SOURCE)
        val referencedFields = collectMatches(configScreen, CONFIG_GET_PATTERN)
        val savedFields = collectMatches(configScreen, CONFIG_SET_PATTERN)
        val missingSaveBindings = TreeSet(referencedFields)
        missingSaveBindings.removeAll(savedFields)

        assertTrue(missingSaveBindings.isEmpty()) {
            "Every Config field shown in ShincolleConfigScreen should keep a matching save consumer: " +
                    missingSaveBindings.joinToString(", ")
        }
        assertTrue(configScreen.contains("builder.setSavingRunnable(() -> {")) {
            "ShincolleConfigScreen should keep an explicit saving runnable"
        }
        assertTrue(configScreen.contains("Config.SPEC.save();")) {
            "ShincolleConfigScreen should keep saving the common config spec"
        }
        assertTrue(configScreen.contains("Config.CLIENT_SPEC.save();")) {
            "ShincolleConfigScreen should keep saving the client config spec"
        }
    }

    private fun collectMatches(source: String, pattern: Pattern): Set<String> {
        val matches = TreeSet<String>()
        val matcher = pattern.matcher(source)
        while (matcher.find()) {
            matches.add(matcher.group(1)!!)
        }
        return matches
    }
}
