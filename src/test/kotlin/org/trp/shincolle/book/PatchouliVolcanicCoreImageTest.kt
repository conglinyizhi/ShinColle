package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliVolcanicCoreImageTest {
    private val LOGISTICS_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/systems/logistics_and_energy.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun logisticsEntryShouldIncludeVolcanicCoreReferenceImage() {
        val entry = Files.readString(LOGISTICS_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.logistics_and_energy.page5.title")) {
            "Logistics entry should include a dedicated volcanic core reference image page"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guivolcore.png")) {
            "Logistics entry should reference the preserved volcanic core GUI texture"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.logistics_and_energy.page5.text")) {
            "English manual should describe the volcanic core reference image page"
        }
        assertTrue(enUs.contains("fuel input and energy release")) {
            "English volcanic core page should mention fuel input and energy release"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.logistics_and_energy.page5.text")) {
            "Chinese manual should describe the volcanic core reference image page"
        }
        assertTrue(zhCn.contains("燃料输入与能量释放")) {
            "Chinese volcanic core page should mention fuel input and energy release"
        }
    }
}
