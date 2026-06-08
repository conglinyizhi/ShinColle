package org.trp.shincolle.item

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class TargetWrenchTooltipLocalizationRegressionTest {
    @Test
    fun targetWrenchTooltipKeysShouldRemainLocalizedInMaintainedLanguages() {
        val languageSources = listOf(
            Files.readString(EN_US_LANG),
            Files.readString(JA_JP_LANG),
            Files.readString(ZH_CN_LANG),
            Files.readString(ZH_TW_LANG)
        )

        TARGET_WRENCH_TOOLTIP_KEYS.forEach { key ->
            languageSources.forEach { languageSource ->
                assertTrue(languageSource.contains("\"$key\"")) { "Maintained language files should define $key" }
            }
        }
    }

    companion object {
        private val EN_US_LANG: Path = Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
        private val JA_JP_LANG: Path = Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json")
        private val ZH_CN_LANG: Path = Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")
        private val ZH_TW_LANG: Path = Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
        private val TARGET_WRENCH_TOOLTIP_KEYS = listOf(
            "gui.shincolle.wrench1",
            "gui.shincolle.wrench2",
            "gui.shincolle.wrench3"
        )
    }
}
