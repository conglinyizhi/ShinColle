package org.trp.shincolle.item

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class TooltipLocalizationRegressionTest {
    @Test
    fun tooltipFallbackKeysShouldStayLocalizedAndReadable() {
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(enUs.contains("\"item.shincolle.modernkit\": \"Modernization Toolkit\""))
        assertTrue(zhCn.contains("\"item.shincolle.modernkit\": \"近代化改修工具\""))
        assertTrue(enUs.contains("\"block.shincolle.blockvolblock\": \"Abyssal Volcano Block\""))
        assertTrue(zhCn.contains("\"block.shincolle.blockvolblock\": \"深海火山方块\""))
    }

    companion object {
        private val EN_US_LANG: Path = Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
        private val ZH_CN_LANG: Path = Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")
    }
}
