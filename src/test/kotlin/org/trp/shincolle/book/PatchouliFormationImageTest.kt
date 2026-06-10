package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliFormationImageTest {
    private val FLEET_CONTROL_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/systems/fleet_control.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun fleetControlEntryShouldIncludeFormationReferenceImage() {
        val entry = Files.readString(FLEET_CONTROL_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.fleet_control.page5.title")) {
            "Fleet control entry should include a dedicated formation reference image page"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guiformation.png")) {
            "Fleet control entry should reference the preserved formation GUI texture"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.fleet_control.page5.text")) {
            "English manual should describe the formation reference image page"
        }
        assertTrue(enUs.contains("Line Ahead, Double Line, Diamond, Echelon, Line Abreast")) {
            "English formation page should mention the preserved legacy formation roles"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.fleet_control.page5.text")) {
            "Chinese manual should describe the formation reference image page"
        }
        assertTrue(zhCn.contains("单纵、复纵、轮形、梯形、单横")) {
            "Chinese formation page should mention the preserved legacy formation roles"
        }
    }
}
