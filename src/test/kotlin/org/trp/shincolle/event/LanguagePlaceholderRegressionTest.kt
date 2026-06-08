package org.trp.shincolle.event

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

import org.junit.jupiter.api.Assertions.assertFalse

class LanguagePlaceholderRegressionTest {
    private val EN_US: Path =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val JA_JP: Path =
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json")
    private val ZH_CN: Path =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")
    private val ZH_TW: Path =
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")

    @Test
    fun maintainedLanguagesShouldNotContainAwaitingTranslationPlaceholders() {
        for (path in arrayOf(EN_US, JA_JP, ZH_CN, ZH_TW)) {
            val content = Files.readString(path).lowercase()
            assertFalse(content.contains("awaiting translation")) {
                path.toString() + " should not ship placeholder translation markers"
            }
        }
    }
}
