package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliDeskTargetImageTest {
    private val DESK_AND_RADAR_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/systems/desk_and_radar.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun deskAndRadarEntryShouldIncludeTargetManagementReferenceImage() {
        val entry = Files.readString(DESK_AND_RADAR_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.desk_and_radar.page6.title")) {
            "Desk and radar entry should include a dedicated target-management reference page"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guidesktarget.png")) {
            "Desk and radar entry should reference the preserved target-management GUI texture"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.desk_and_radar.page6.text")) {
            "English manual should describe the target-management reference image page"
        }
        assertTrue(enUs.contains("alliance and ban lists")) {
            "English desk target page should mention alliance and ban lists"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.desk_and_radar.page6.text")) {
            "Chinese manual should describe the target-management reference image page"
        }
        assertTrue(zhCn.contains("同盟/禁攻列表")) {
            "Chinese desk target page should mention alliance and protected-target lists"
        }
    }
}
