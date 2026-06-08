package org.trp.shincolle.item

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class ModernKitMaxedFeedbackRegressionTest {
    @Test
    fun modernKitShouldNotifyWhenModernizationIsAlreadyMaxed() {
        val modernKitSource = Files.readString(MODERN_KIT_SOURCE)
        val configSource = Files.readString(CONFIG_SOURCE)
        val enUs = Files.readString(EN_US_LANG)
        val zhCn = Files.readString(ZH_CN_LANG)

        assertTrue(modernKitSource.contains("if (Config.modernKitNotifyWhenMaxed) {"))
        assertTrue(modernKitSource.contains("MaxedFeedback feedback = maxedFeedback();"))

        assertTrue(configSource.contains("define(\"modernKitNotifyWhenMaxed\", modernKitNotifyWhenMaxed)"))
        assertTrue(configSource.contains("define(\"modernKitNotifyWhenMaxedActionBar\", modernKitNotifyWhenMaxedActionBar)"))

        assertTrue(enUs.contains("\"chat.shincolle.modernkit.maxed\": \"This ship cannot be modernized any further.\""))
        assertTrue(zhCn.contains("\"chat.shincolle.modernkit.maxed\": \"这位舰娘已经无法继续进行近代化改修了。\""))
    }

    companion object {
        private val MODERN_KIT_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/item/ModernKitItem.java")
        private val CONFIG_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/Config.java")
        private val EN_US_LANG: Path = Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
        private val ZH_CN_LANG: Path = Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")
    }
}
