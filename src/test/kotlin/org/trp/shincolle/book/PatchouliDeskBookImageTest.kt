package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliDeskBookImageTest {
    private val MANUAL_TOOLS_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/items/manual_tools.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun manualToolsEntryShouldIncludeDeskBookReferenceImage() {
        val entry = Files.readString(MANUAL_TOOLS_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.manual_tools.page1a.title")) {
            "Manual tools entry should include a dedicated Desk Book reference image page"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guideskbook.png")) {
            "Manual tools entry should reference the preserved Desk Book GUI texture"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.manual_tools.page1a.text")) {
            "English manual should describe the Desk Book reference image page"
        }
        assertTrue(enUs.contains("chapter navigation")) {
            "English Desk Book page should mention chapter navigation"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.manual_tools.page1a.text")) {
            "Chinese manual should describe the Desk Book reference image page"
        }
        assertTrue(zhCn.contains("章节导航")) {
            "Chinese Desk Book page should mention chapter navigation"
        }
    }
}
