package org.trp.shincolle.init

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

class ItemLocalizationRegressionTest {
    @Test
    fun registeredItemNamesShouldRemainLocalizedInMaintainedLanguages() {
        val modItems = Files.readString(MOD_ITEMS_SOURCE)
        val itemKeys = mutableListOf<String>()

        readRegisteredIds(modItems, ITEM_REGISTRATION_PATTERN).forEach { itemId ->
            itemKeys += "item.shincolle.$itemId"
        }
        readRegisteredIds(modItems, BOSS_EGG_REGISTRATION_PATTERN).forEach { itemId ->
            itemKeys += "item.shincolle.${itemId}_boss_egg"
        }

        assertTrue(itemKeys.isNotEmpty())

        itemKeys.forEach { key ->
            assertLocalizedInMaintainedLanguages(key)
        }
    }

    private fun readRegisteredIds(source: String, pattern: Pattern): List<String> {
        val ids = mutableListOf<String>()
        val matcher = pattern.matcher(source)
        while (matcher.find()) {
            ids += matcher.group(1)
        }
        return ids
    }

    private fun assertLocalizedInMaintainedLanguages(key: String) {
        LANGUAGE_SOURCES.forEach { languageSource ->
            val source = Files.readString(languageSource)
            assertTrue(source.contains("\"$key\"")) { "Expected maintained languages to define $key" }
        }
    }

    companion object {
        private val MOD_ITEMS_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/init/ModItems.java")
        private val LANGUAGE_SOURCES = listOf(
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
        )
        private val ITEM_REGISTRATION_PATTERN: Pattern = Pattern.compile("ITEMS\\.register\\(\"([a-z0-9_]+)\"")
        private val BOSS_EGG_REGISTRATION_PATTERN: Pattern = Pattern.compile("registerBossEgg\\(\"([a-z0-9_]+)\"")
    }
}
