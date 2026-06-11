package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliGettingStartedLayoutTest {
    private val INTRO =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/getting_started/intro.json")
    private val RESOURCES =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/getting_started/resources.json")
    private val EN_US = Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN = Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun gettingStartedEntriesShouldKeepInterleavedTextAndRecipeLayout() {
        val intro = Files.readString(INTRO)
        val resources = Files.readString(RESOURCES)
        val enUs = Files.readString(EN_US)
        val zhCn = Files.readString(ZH_CN)

        assertTrue(intro.contains("patchouli.shincolle.entry.intro.page1b.title")) {
            "Intro entry should keep the extra reading-path text page before the first crafting page"
        }
        assertTrue(resources.contains("patchouli.shincolle.entry.resources.page1b.title")) {
            "Resources entry should keep a text buffer before grudge compression recipes"
        }
        assertTrue(resources.contains("patchouli.shincolle.entry.resources.page2c.title")) {
            "Resources entry should keep a text buffer before metal compression recipes"
        }
        assertTrue(resources.contains("patchouli.shincolle.entry.resources.page3c.title")) {
            "Resources entry should keep a text buffer before ammo container recipes"
        }
        assertTrue(resources.contains("patchouli.shincolle.entry.resources.page4c.title")) {
            "Resources entry should keep a text buffer before ammo unpacking recipes"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.resources.page3c.text")) {
            "English translations should cover the added getting-started layout text"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.resources.page3c.text")) {
            "Chinese translations should cover the added getting-started layout text"
        }
    }
}
