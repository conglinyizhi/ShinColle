package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliRadarImageTest {
    private val DESK_AND_RADAR_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/systems/desk_and_radar.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun deskAndRadarEntryShouldIncludeRadarReferenceImage() {
        val entry = Files.readString(DESK_AND_RADAR_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.desk_and_radar.page3.title")) {
            "Desk and radar entry should include a dedicated radar reference image page"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guideskradar.png")) {
            "Desk and radar entry should reference the preserved radar GUI texture"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.desk_and_radar.page3.text")) {
            "English manual should describe the radar reference image page"
        }
        assertTrue(enUs.contains("desk radar panel")) {
            "English radar page should mention the desk radar panel layout"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.desk_and_radar.page3.text")) {
            "Chinese manual should describe the radar reference image page"
        }
        assertTrue(zhCn.contains("办公桌雷达面板")) {
            "Chinese radar page should mention the desk radar panel layout"
        }
    }
}
