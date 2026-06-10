package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliMiscItemsManualTest {
    private val MISC_ITEMS_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/items/misc_items.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun miscItemsEntryShouldDocumentTrainingBookRestrictionsAndRange() {
        val entry = Files.readString(MISC_ITEMS_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.misc_items.page2b.title")) {
            "Misc items entry should include a dedicated Training Manual level-range page"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.misc_items.page2b.text")) {
            "English manual should document Training Manual restrictions and level range"
        }
        assertTrue(enUs.contains("configured minimum and maximum range")) {
            "English manual should mention the configured Training Manual level range"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.misc_items.page2b.text")) {
            "Chinese manual should document Training Manual restrictions and level range"
        }
        assertTrue(zhCn.contains("最小值与最大值之间随机提升等级")) {
            "Chinese manual should mention the random level gain within the configured range"
        }
    }
}
