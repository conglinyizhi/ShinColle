package org.trp.shincolle.init

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

class EntityLocalizationRegressionTest {
    @Test
    fun registeredEntityNamesShouldRemainLocalizedInMaintainedLanguages() {
        val modEntities = Files.readString(MOD_ENTITIES_SOURCE)
        val entityKeys = readRegisteredEntityIds(modEntities).map { entityId -> "entity.shincolle.$entityId" }.toSet()

        assertTrue(entityKeys.isNotEmpty())

        entityKeys.forEach { key ->
            assertLocalizedInMaintainedLanguages(key)
        }
    }

    private fun readRegisteredEntityIds(modEntities: String): List<String> {
        val entityIds = mutableListOf<String>()
        val matcher = ENTITY_REGISTRATION_PATTERN.matcher(modEntities)
        while (matcher.find()) {
            entityIds += matcher.group(1)
        }
        return entityIds
    }

    private fun assertLocalizedInMaintainedLanguages(key: String) {
        LANGUAGE_SOURCES.forEach { languageSource ->
            val languageMap = readLanguageMap(languageSource)
            assertTrue(languageMap.containsKey(key)) {
                "Expected ${languageSource.fileName} to define $key"
            }
        }
    }

    private fun readLanguageMap(path: Path): Map<String, String> {
        val content = Files.readString(path)
        val map = mutableMapOf<String, String>()
        val matcher = LANGUAGE_ENTRY_PATTERN.matcher(content)
        while (matcher.find()) {
            map[matcher.group(1)] = matcher.group(2)
        }
        return map
    }

    companion object {
        private val MOD_ENTITIES_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/init/ModEntities.kt")
        private val LANGUAGE_SOURCES = listOf(
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
        )
        private val ENTITY_REGISTRATION_PATTERN: Pattern = Pattern.compile(
            "ENTITY_TYPES\\.register<.*?>\\(\\s*\"([a-z0-9_]+)\"",
            Pattern.DOTALL
        )
        private val LANGUAGE_ENTRY_PATTERN: Pattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"")
    }
}
