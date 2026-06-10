package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliUtilityBlocksTest {
    private val UTILITY_BLOCKS_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/systems/utility_blocks.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun utilityBlocksEntryShouldDocumentHeavyGrudgeDecorationBlock() {
        val entry = Files.readString(UTILITY_BLOCKS_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.utility_blocks.page4.title")) {
            "Utility blocks entry should dedicate a page to the heavy grudge decoration block"
        }
        assertTrue(entry.contains("\"item\": \"shincolle:grudge_heavy_deco_block\"")) {
            "Utility blocks entry should spotlight the restored heavy grudge decoration block item"
        }
        assertTrue(entry.contains("patchouli.shincolle.entry.utility_blocks.page5.title")) {
            "Utility blocks entry should keep the ore source page after the new decoration block page"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.utility_blocks.page4.text")) {
            "English language file should provide utility block documentation for the heavy grudge decoration block"
        }
        assertTrue(enUs.contains("works as a beacon base")) {
            "English utility block documentation should mention the beacon-base behavior"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.utility_blocks.page4.text")) {
            "Chinese language file should provide utility block documentation for the heavy grudge decoration block"
        }
        assertTrue(zhCn.contains("可作为信标底座")) {
            "Chinese utility block documentation should mention the beacon-base behavior"
        }
    }
}
