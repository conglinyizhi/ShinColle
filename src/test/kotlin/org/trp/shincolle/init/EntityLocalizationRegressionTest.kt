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
        val entityIds = readRegisteredEntityIds(modEntities)

        assertTrue(entityIds.isNotEmpty())

        entityIds.forEach { entityId ->
            val key = "entity.shincolle.$entityId"
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
            val source = Files.readString(languageSource)
            assertTrue(source.contains("\"$key\"")) { "Expected maintained languages to define $key" }
        }
    }

    companion object {
        private val MOD_ENTITIES_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/init/ModEntities.java")
        private val LANGUAGE_SOURCES = listOf(
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
        )
        private val ENTITY_REGISTRATION_PATTERN: Pattern = Pattern.compile("ENTITY_TYPES\\.register\\(\"([a-z0-9_]+)\"")
    }
}
