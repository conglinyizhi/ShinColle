package org.trp.shincolle.event

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedHashSet
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class LegacyVariantModelPropertyRegistrationRegressionTest {
    private val ITEM_MODEL_ROOT: Path =
            Path.of("src/main/resources/assets/shincolle/models/item")
    private val CLIENT_EVENT_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.kt")
    private val ITEM_REGISTRY_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.kt")
    private val LEGACY_VARIANT_REGISTRATION_PATTERN: Pattern =
            Pattern.compile("registerLegacyVariantProperty\\(ModItems\\.([A-Z0-9_]+)\\.get\\(\\)\\);")
    private val ITEM_REGISTRATION_PATTERN: Pattern =
            Pattern.compile("public static final DeferredItem<Item>\\s+([A-Z0-9_]+)\\s*=\\s*ITEMS\\.register\\(\"([a-z0-9_]+)\"")

    @Test
    fun legacyVariantItemModelsShouldKeepMatchingClientPropertyRegistrations() {
        val expectedFields = findLegacyVariantBackedItemFields()
        val clientEvents = Files.readString(CLIENT_EVENT_SOURCE)

        val registeredFields = ArrayList<String>()
        val registrationMatcher = LEGACY_VARIANT_REGISTRATION_PATTERN.matcher(clientEvents)
        while (registrationMatcher.find()) {
            registeredFields.add(registrationMatcher.group(1)!!)
        }

        assertEquals(expectedFields.size, LinkedHashSet(registeredFields).size) {
            "Legacy variant model property registrations should not contain duplicates"
        }
        assertEquals(expectedFields, LinkedHashSet(registeredFields)) {
            "Every item model using shincolle:legacy_variant should keep a matching client property registration"
        }
        assertTrue(clientEvents.contains("ItemProperties.register(item, LEGACY_VARIANT_MODEL_PROPERTY,")) {
            "Client setup should keep registering the shared legacy variant model property"
        }
    }

    private fun findLegacyVariantBackedItemFields(): Set<String> {
        val itemFields = LinkedHashSet<String>()
        val itemRegistryNamesToFields = readItemRegistryNamesToFields()

        Files.walk(ITEM_MODEL_ROOT).use { stream ->
            for (file in stream
                    .filter { Files.isRegularFile(it) }
                    .filter { it.toString().endsWith(".json") }
                    .iterator()) {
                val source = Files.readString(file)
                if (!source.contains("\"shincolle:legacy_variant\"")) {
                    continue
                }

                val baseName = stripJsonExtension(file.fileName.toString())
                itemFields.add(resolveModItemsFieldName(baseName, itemRegistryNamesToFields))
            }
        }

        return itemFields
    }

    private fun readItemRegistryNamesToFields(): Map<String, String> {
        val registryNamesToFields = HashMap<String, String>()
        val itemRegistry = Files.readString(ITEM_REGISTRY_SOURCE)
        val matcher = ITEM_REGISTRATION_PATTERN.matcher(itemRegistry)
        while (matcher.find()) {
            registryNamesToFields[matcher.group(2)!!] = matcher.group(1)!!
        }
        return registryNamesToFields
    }

    private fun stripJsonExtension(fileName: String): String {
        return fileName.substring(0, fileName.length - ".json".length)
    }

    private fun resolveModItemsFieldName(itemModelName: String, itemRegistryNamesToFields: Map<String, String>): String {
        val fieldName = itemRegistryNamesToFields[itemModelName]
        assertTrue(fieldName != null) {
            "Expected item model " + itemModelName + " to map to a registered ModItems field"
        }
        return fieldName!!
    }
}
