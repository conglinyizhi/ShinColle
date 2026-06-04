package org.trp.shincolle.item

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class PointerAndInstantConstructionLocalizationRegressionTest {
    @Test
    fun instantConstructionTooltipShouldRemainLocalizedInMaintainedLanguages() {
        INSTANT_CONSTRUCTION_KEYS.forEach { key ->
            assertLocalizedInMaintainedLanguages(key)
        }
    }

    private fun assertLocalizedInMaintainedLanguages(key: String) {
        LANGUAGE_SOURCES.forEach { languageSource ->
            val source = Files.readString(languageSource)
            assertTrue(source.contains("\"$key\"")) { "Expected maintained languages to define $key" }
        }
    }

    companion object {
        private val LANGUAGE_SOURCES = listOf(
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
        )
        private val INSTANT_CONSTRUCTION_KEYS = listOf(
            "gui.shincolle.instantconmat",
            "gui.shincolle.instantconmat.slot"
        )
    }
}
