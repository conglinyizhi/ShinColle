package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliCraneImageTest {
    private val AUTOMATION_BLOCKS_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/systems/automation_blocks.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun automationBlocksEntryShouldIncludeCraneReferenceImage() {
        val entry = Files.readString(AUTOMATION_BLOCKS_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.automation_blocks.page3.title")) {
            "Automation blocks entry should include a dedicated crane reference image page"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guicrane.png")) {
            "Automation blocks entry should reference the preserved crane GUI texture"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.automation_blocks.page3.text")) {
            "English manual should describe the crane reference image page"
        }
        assertTrue(enUs.contains("loading, unloading, and filter options")) {
            "English crane page should mention loading, unloading, and filter options"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.automation_blocks.page3.text")) {
            "Chinese manual should describe the crane reference image page"
        }
        assertTrue(zhCn.contains("装载、卸载与过滤选项")) {
            "Chinese crane page should mention loading, unloading, and filter options"
        }
    }
}
