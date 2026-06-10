package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliUiGuideTest {
    private val UI_GUIDE_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/systems/ui_guide.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun uiGuideShouldDocumentNonClickableDeskShipyardAndRecipePaperElements() {
        val entry = Files.readString(UI_GUIDE_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.ui_guide.page5.title")) {
            "UI guide should include the desk interface reference page"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guidesk.png")) {
            "UI guide should reference the desk GUI texture"
        }
        assertTrue(entry.contains("patchouli.shincolle.entry.ui_guide.page6.title")) {
            "UI guide should include the shipyard and recipe paper page"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.ui_guide.page6.text")) {
            "English manual should describe shipyard and recipe paper read-only elements"
        }
        assertTrue(enUs.contains("result slot is only a preview")) {
            "English UI guide should explain that Recipe Paper result slot is preview-only"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.ui_guide.page6.text")) {
            "Chinese manual should describe shipyard and recipe paper read-only elements"
        }
        assertTrue(zhCn.contains("结果槽只负责预览")) {
            "Chinese UI guide should explain that Recipe Paper result slot is preview-only"
        }
    }
}
