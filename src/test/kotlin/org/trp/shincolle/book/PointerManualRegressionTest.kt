package org.trp.shincolle.book

import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue

class PointerManualRegressionTest {
    private val EN_US: Path =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val ZH_CN: Path =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")

    @Test
    fun manualsShouldDocumentLegacyPointerLeftClickRules() {
        val en = Files.readString(EN_US)
        val zh = Files.readString(ZH_CN)

        assertTrue(en.contains("Sprint+sneak-left-click in the air")) {
            "English manual should describe the legacy clear-team pointer shortcut"
        }
        assertTrue(en.contains("Left-click\$() on a non-ship entity records that entity class")) {
            "English manual should describe the legacy target-class capture behavior"
        }
        assertTrue(zh.contains("冲刺+潜行左键空挥")) {
            "Chinese manual should describe the legacy clear-team pointer shortcut"
        }
        assertTrue(zh.contains("左键\$()命中非舰娘实体时：将该实体类加入攻击目标类别")) {
            "Chinese manual should describe the legacy target-class capture behavior"
        }
    }

    @Test
    fun manualsShouldDocumentLegacyPointerRightClickRules() {
        val en = Files.readString(EN_US)
        val zh = Files.readString(ZH_CN)

        assertTrue(en.contains("Sprint+right-click")) {
            "English manual should describe move-only guard commands"
        }
        assertTrue(en.contains("Sneak+right-click on an owned ship")) {
            "English manual should describe opening the owned ship menu"
        }
        assertTrue(zh.contains("冲刺+右键")) {
            "Chinese manual should describe move-only guard commands"
        }
        assertTrue(zh.contains("潜行+右键己方舰娘")) {
            "Chinese manual should describe opening the owned ship menu"
        }
    }
}
