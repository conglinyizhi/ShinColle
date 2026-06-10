package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliAutomationRouteImageTest {
    private val AUTOMATION_JOBS_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/systems/automation_jobs.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun automationJobsEntryShouldIncludeAutomationRouteReferenceImage() {
        val entry = Files.readString(AUTOMATION_JOBS_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.automation_jobs.page4.title")) {
            "Automation jobs entry should include a dedicated automation-route reference image page"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guicrane.png")) {
            "Automation jobs entry should reference the preserved crane GUI texture for route guidance"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.automation_jobs.page4.text")) {
            "English manual should describe the automation-route reference image page"
        }
        assertTrue(enUs.contains("waypoint-based automation routes")) {
            "English automation-route page should mention waypoint-based automation routes"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.automation_jobs.page4.text")) {
            "Chinese manual should describe the automation-route reference image page"
        }
        assertTrue(zhCn.contains("航点自动化路线")) {
            "Chinese automation-route page should mention waypoint-based automation routes"
        }
    }
}
