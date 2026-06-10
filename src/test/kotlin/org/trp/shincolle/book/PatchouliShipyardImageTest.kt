package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliShipyardImageTest {
    private val SHIPYARD_TOOLS_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/items/shipyard_tools.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun shipyardToolsEntryShouldIncludeLargeShipyardReferenceImage() {
        val entry = Files.readString(SHIPYARD_TOOLS_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.shipyard_tools.page10.title")) {
            "Shipyard tools entry should include a dedicated page for the large shipyard reference image"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guilargeshipyard.png")) {
            "Shipyard tools entry should reference the preserved large shipyard GUI texture"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.shipyard_tools.page10.text")) {
            "English manual should describe the large shipyard reference image page"
        }
        assertTrue(enUs.contains("repeated-build controls")) {
            "English manual should mention repeated-build controls on the large shipyard page"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.shipyard_tools.page10.text")) {
            "Chinese manual should describe the large shipyard reference image page"
        }
        assertTrue(zhCn.contains("连续建造开关")) {
            "Chinese manual should mention repeated-build controls on the large shipyard page"
        }
    }

    @Test
    fun shipyardToolsEntryShouldIncludeSmallShipyardReferenceImage() {
        val entry = Files.readString(SHIPYARD_TOOLS_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.shipyard_tools.page9.title")) {
            "Shipyard tools entry should include a dedicated page for the small shipyard reference image"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guismallshipyard.png")) {
            "Shipyard tools entry should reference the preserved small shipyard GUI texture"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.shipyard_tools.page9.text")) {
            "English manual should describe the small shipyard reference image page"
        }
        assertTrue(enUs.contains("resource inputs and fuel management")) {
            "English manual should mention planning resource inputs and fuel management on the small shipyard page"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.shipyard_tools.page9.text")) {
            "Chinese manual should describe the small shipyard reference image page"
        }
        assertTrue(zhCn.contains("资源投入与燃料管理")) {
            "Chinese manual should mention resource inputs and fuel management on the small shipyard page"
        }
    }
}
