package org.trp.shincolle.book

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue

class PatchouliManualContentRegressionTest {
    private val EN_AUTOMATION = Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/items/automation_tools.json")
    private val EN_FLEET_CONTROL = Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/systems/fleet_control.json")
    private val EN_FLEET_SUPPORT = Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/items/fleet_support_tools.json")
    private val ZH_CN_LANG = Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun automationEntryDocumentsMatchingSwitchesAndSideMatrix() {
        val json = Files.readString(EN_AUTOMATION)
        assertTrue(json.contains("patchouli.shincolle.entry.automation_tools.page3.title"))
        assertTrue(json.contains("patchouli.shincolle.entry.automation_tools.page4.title"))
        assertTrue(json.contains("patchouli.shincolle.entry.automation_tools.page5.title"))
    }

    @Test
    fun fleetEntriesDocumentScepterUsageInSimplifiedChinese() {
        val fleetControl = Files.readString(EN_FLEET_CONTROL)
        val fleetSupport = Files.readString(EN_FLEET_SUPPORT)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(fleetControl.contains("patchouli.shincolle.entry.fleet_control.page3.title"))
        assertTrue(fleetSupport.contains("patchouli.shincolle.entry.fleet_support_tools.page4.title"))
        assertTrue(zhCn.contains("patchouli.shincolle.entry.fleet_control.page3.text"))
        assertTrue(zhCn.contains("patchouli.shincolle.entry.fleet_support_tools.page4.text"))
        assertTrue(zhCn.contains("patchouli.shincolle.entry.automation_tools.page4.text"))
    }
}
