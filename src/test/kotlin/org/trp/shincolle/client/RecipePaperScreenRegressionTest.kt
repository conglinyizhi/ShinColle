package org.trp.shincolle.client

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.nio.file.Files
import java.nio.file.Path

class RecipePaperScreenRegressionTest {
    private val SCREEN_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/client/screen/RecipePaperScreen.kt")
    private val LANGUAGE_SOURCES: List<Path> = listOf(
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
        Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
        Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
    )
    private val RECIPE_PAPER_KEYS: List<String> = listOf(
        "gui.shincolle.recipepaper.title",
        "gui.shincolle.recipepaper.material"
    )

    @Test
    fun recipePaperScreenShouldDrawReadableSectionHeaders() {
        val source = Files.readString(SCREEN_SOURCE)
        assertTrue(source.contains("drawSectionLabel(guiGraphics, Component.translatable(\"gui.shincolle.recipepaper.material\"), 29, 6);")) {
            "Recipe Paper screen should draw the materials header explicitly"
        }
        assertTrue(source.contains("drawSectionLabel(guiGraphics, Component.translatable(\"gui.shincolle.recipepaper.result\"), 114, 24);")) {
            "Recipe Paper screen should draw the result header explicitly"
        }
        assertTrue(source.contains("SECTION_COLOR = 0xFFF1C8")) {
            "Recipe Paper section headers should use a brighter foreground color for readability"
        }
        assertTrue(source.contains("SECTION_SHADOW_COLOR")) {
            "Recipe Paper section headers should keep a darker shadow pass for contrast"
        }
    }

    @Test
    fun recipePaperScreenShouldKeepMaintainedLanguageLabels() {
        for (languageSource in LANGUAGE_SOURCES) {
            val source = Files.readString(languageSource)
            for (key in RECIPE_PAPER_KEYS) {
                assertTrue(source.contains("\"" + key + "\"")) {
                    "Expected maintained languages to define " + key
                }
            }
        }
    }
}
