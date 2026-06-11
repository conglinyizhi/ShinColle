package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliShipSpawnEggManualTest {
    private val ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/items/shipyard_tools.json")
    private val EN_US = Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN = Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun shipyardToolsShouldDocumentSmallAndLargeSpawnEggs() {
        val entry = Files.readString(ENTRY)
        val enUs = Files.readString(EN_US)
        val zhCn = Files.readString(ZH_CN)

        assertTrue(entry.contains("patchouli.shincolle.entry.shipyard_tools.page11.title")) {
            "Shipyard tools entry should include a dedicated small spawn egg page"
        }
        assertTrue(entry.contains("\"item\": \"shincolle:shipspawneggs\"")) {
            "Shipyard tools entry should spotlight the small ship spawn egg"
        }
        assertTrue(entry.contains("patchouli.shincolle.entry.shipyard_tools.page12.title")) {
            "Shipyard tools entry should include a dedicated large spawn egg page"
        }
        assertTrue(entry.contains("\"item\": \"shincolle:shipspawneggl\"")) {
            "Shipyard tools entry should spotlight the large ship spawn egg"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.shipyard_tools.page11.text")) {
            "English manual should describe the small ship spawn egg page"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.shipyard_tools.page12.text")) {
            "English manual should describe the large ship spawn egg page"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.shipyard_tools.page11.text")) {
            "Chinese manual should describe the small ship spawn egg page"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.shipyard_tools.page12.text")) {
            "Chinese manual should describe the large ship spawn egg page"
        }
    }
}
