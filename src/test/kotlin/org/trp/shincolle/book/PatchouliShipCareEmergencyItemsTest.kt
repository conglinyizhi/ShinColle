package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliShipCareEmergencyItemsTest {
    private val SHIP_CARE_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/systems/ship_care.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun shipCareEntryShouldDocumentEmergencySustainRoles() {
        val entry = Files.readString(SHIP_CARE_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.ship_care.page2c.title")) {
            "Ship care entry should include a dedicated emergency-sustain guidance page"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.ship_care.page2c.text")) {
            "English manual should document Repair Goddess and Zero Fighter sustain roles"
        }
        assertTrue(enUs.contains("stacks to 16")) {
            "English ship-care page should mention the Repair Goddess stack size"
        }
        assertTrue(enUs.contains("raise morale")) {
            "English ship-care page should mention Zero Fighter morale recovery"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.ship_care.page2c.text")) {
            "Chinese manual should document Repair Goddess and Zero Fighter sustain roles"
        }
        assertTrue(zhCn.contains("最多可堆叠 16 个")) {
            "Chinese ship-care page should mention the Repair Goddess stack size"
        }
        assertTrue(zhCn.contains("补士气")) {
            "Chinese ship-care page should mention Zero Fighter morale recovery"
        }
    }
}
