package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliRecipePaperImageTest {
    private val MANUAL_TOOLS_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/items/manual_tools.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun manualToolsEntryShouldIncludeRecipePaperReferenceImage() {
        val entry = Files.readString(MANUAL_TOOLS_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.manual_tools.page1c.title")) {
            "Manual tools entry should include a dedicated Recipe Paper reference image page"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guirecipepaper.png")) {
            "Manual tools entry should reference the preserved Recipe Paper GUI texture"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.manual_tools.page1c.text")) {
            "English manual should describe the Recipe Paper reference image page"
        }
        assertTrue(enUs.contains("preview-only")) {
            "English Recipe Paper page should explain the preview-only result slot"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.manual_tools.page1c.text")) {
            "Chinese manual should describe the Recipe Paper reference image page"
        }
        assertTrue(zhCn.contains("只用于预览")) {
            "Chinese Recipe Paper page should explain the preview-only result slot"
        }
    }
}
