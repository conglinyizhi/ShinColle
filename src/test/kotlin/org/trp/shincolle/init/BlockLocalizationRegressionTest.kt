package org.trp.shincolle.init

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

class BlockLocalizationRegressionTest {
    @Test
    fun registeredBlockNamesShouldRemainLocalizedInMaintainedLanguages() {
        val modBlocks = Files.readString(MOD_BLOCKS_SOURCE)
        val blockKeys = readRegisteredIds(modBlocks).map { blockId -> "block.shincolle.$blockId" }

        assertTrue(blockKeys.isNotEmpty())

        blockKeys.forEach { key ->
            assertLocalizedInMaintainedLanguages(key)
        }
    }

    private fun readRegisteredIds(source: String): List<String> {
        val ids = mutableListOf<String>()
        val matcher = BLOCK_REGISTRATION_PATTERN.matcher(source)
        while (matcher.find()) {
            ids += matcher.group(1)
        }
        return ids
    }

    private fun assertLocalizedInMaintainedLanguages(key: String) {
        LANGUAGE_SOURCES.forEach { languageSource ->
            val source = Files.readString(languageSource)
            assertTrue(source.contains("\"$key\"")) { "Expected maintained languages to define $key" }
        }
    }

    companion object {
        private val MOD_BLOCKS_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/init/ModBlocks.java")
        private val LANGUAGE_SOURCES = listOf(
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
        )
        private val BLOCK_REGISTRATION_PATTERN: Pattern = Pattern.compile("BLOCKS\\.register\\(\"([a-z0-9_]+)\"")
    }
}
