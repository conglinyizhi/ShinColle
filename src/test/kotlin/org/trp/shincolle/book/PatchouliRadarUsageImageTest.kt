package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliRadarUsageImageTest {
    private val RADAR_USAGE_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/systems/radar_usage.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun radarUsageEntryShouldIncludeRadarReferenceImage() {
        val entry = Files.readString(RADAR_USAGE_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.radar_usage.page2.title")) {
            "Radar usage entry should include a dedicated radar reference image page"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guideskradar.png")) {
            "Radar usage entry should reference the preserved desk radar GUI texture"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.radar_usage.page2.text")) {
            "English manual should describe the radar-usage reference image page"
        }
        assertTrue(enUs.contains("original desk radar layout")) {
            "English radar-usage page should mention the original desk radar layout"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.radar_usage.page2.text")) {
            "Chinese manual should describe the radar-usage reference image page"
        }
        assertTrue(zhCn.contains("办公桌雷达的原始布局")) {
            "Chinese radar-usage page should mention the original desk radar layout"
        }
    }
}
