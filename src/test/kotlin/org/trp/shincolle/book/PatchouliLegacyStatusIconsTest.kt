package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliLegacyStatusIconsTest {
    private val UI_GUIDE_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/systems/ui_guide.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun uiGuideShouldIncludeLegacyStatusIconReferencePage() {
        val entry = Files.readString(UI_GUIDE_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.ui_guide.page1b.title")) {
            "UI guide should include a dedicated legacy status icon reference page"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guinameicon0.png")) {
            "UI guide should reference the preserved legacy name icon page 0 texture"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guinameicon1.png")) {
            "UI guide should reference the preserved legacy name icon page 1 texture"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guinameicon2.png")) {
            "UI guide should reference the preserved legacy name icon page 2 texture"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.ui_guide.page1b.text")) {
            "English manual should describe the legacy status icon reference page"
        }
        assertTrue(enUs.contains("morale icon language")) {
            "English legacy-status-icon page should mention the shared morale icon language"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.ui_guide.page1b.text")) {
            "Chinese manual should describe the legacy status icon reference page"
        }
        assertTrue(zhCn.contains("旧版图标条")) {
            "Chinese legacy-status-icon page should mention the preserved legacy icon strips"
        }
    }
}
