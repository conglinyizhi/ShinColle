package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliHudImageTest {
    private val SHIP_STATUS_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/systems/ship_status.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun shipStatusEntryShouldIncludeLegacyHudReferenceImage() {
        val entry = Files.readString(SHIP_STATUS_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.ship_status.page4.title")) {
            "Ship status entry should include a dedicated legacy HUD reference page"
        }
        assertTrue(entry.contains("shincolle:textures/gui/guihud.png")) {
            "Ship status entry should reference the preserved HUD GUI texture"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.ship_status.page4.text")) {
            "English manual should describe the legacy HUD reference image page"
        }
        assertTrue(enUs.contains("alert markers")) {
            "English HUD page should mention alert markers"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.ship_status.page4.text")) {
            "Chinese manual should describe the legacy HUD reference image page"
        }
        assertTrue(zhCn.contains("警示标记")) {
            "Chinese HUD page should mention alert markers"
        }
    }
}
