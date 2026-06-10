package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliShipInventoryImageTest {
    private val SHIP_CARE_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/systems/ship_care.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun shipCareEntryShouldIncludeShipInventoryReferenceImage() {
        val entry = Files.readString(SHIP_CARE_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.ship_care.page4.title")) {
            "Ship care entry should include a dedicated ship inventory reference image page"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guishipinventory.png")) {
            "Ship care entry should reference the preserved ship inventory GUI texture"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.ship_care.page4.text")) {
            "English manual should describe the ship inventory reference image page"
        }
        assertTrue(enUs.contains("equipment, items, and AI settings")) {
            "English ship inventory page should mention equipment, items, and AI settings"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.ship_care.page4.text")) {
            "Chinese manual should describe the ship inventory reference image page"
        }
        assertTrue(zhCn.contains("装备、物品与 AI 设定")) {
            "Chinese ship inventory page should mention equipment, items, and AI settings"
        }
    }
}
