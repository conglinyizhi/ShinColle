package org.trp.shincolle.book

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PatchouliOpToolManualTest {
    private val OP_TOOLS_ENTRY =
        Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/items/op_tools.json")
    private val EN_US_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN_LANG =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun opToolEntryShouldDocumentServerWideProtectedTargetScope() {
        val entry = Files.readString(OP_TOOLS_ENTRY)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(entry.contains("patchouli.shincolle.entry.op_tools.page4b.title")) {
            "OP tools entry should include a dedicated permission-and-scope page"
        }
        assertTrue(enUs.contains("patchouli.shincolle.entry.op_tools.page4b.text")) {
            "English manual should document OP Tool permission and scope rules"
        }
        assertTrue(enUs.contains("server-wide")) {
            "English OP Tool page should explain that the protected-target list is server-wide"
        }
        assertTrue(enUs.contains("under the crosshair")) {
            "English OP Tool page should explain that the tool reads the current crosshair target"
        }
        assertTrue(zhCn.contains("patchouli.shincolle.entry.op_tools.page4b.text")) {
            "Chinese manual should document OP Tool permission and scope rules"
        }
        assertTrue(zhCn.contains("服务器全局")) {
            "Chinese OP Tool page should explain that the protected-target list is server-wide"
        }
    }
}
