package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliMultiblockShipyardImageTest {
    private val MULTIBLOCK_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/systems/multiblock_structures.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun multiblockStructuresEntryShouldIncludeLargeShipyardReferenceImage() {
        val entry = Files.readString(MULTIBLOCK_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.multiblock_structures.page2.title")) {
            "Multiblock structures entry should include a dedicated large-shipyard reference image page"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guilargeshipyard.png")) {
            "Multiblock structures entry should reference the preserved large shipyard GUI texture"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.multiblock_structures.page2.text")) {
            "English manual should describe the multiblock large-shipyard reference image page"
        }
        assertTrue(enUs.contains("construction and equipment development")) {
            "English multiblock page should mention the preserved large-shipyard workflow"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.multiblock_structures.page2.text")) {
            "Chinese manual should describe the multiblock large-shipyard reference image page"
        }
        assertTrue(zhCn.contains("建造与装备开发")) {
            "Chinese multiblock page should mention the preserved large-shipyard workflow"
        }
    }
}
