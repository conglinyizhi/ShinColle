package org.trp.shincolle.event

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.junit.jupiter.api.Assertions.assertTrue

class EntityRendererRegistrationRegressionTest {
    private val MOD_ENTITIES_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/init/ModEntities.kt")
    private val CLIENT_EVENT_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.kt")
    private val ENTITY_FIELD_PATTERN: Pattern = Pattern.compile(
            "public static final DeferredHolder<EntityType<\\?>, EntityType<[^>]+>>\\s+([A-Z0-9_]+)\\s*=\\s*ENTITY_TYPES\\.register\\(\"([a-z0-9_]+)\"")

    @Test
    fun registeredEntitiesShouldKeepClientRendererRegistrations() {
        val modEntities = Files.readString(MOD_ENTITIES_SOURCE)
        val clientEvents = Files.readString(CLIENT_EVENT_SOURCE)
        val missing = ArrayList<String>()

        val matcher = ENTITY_FIELD_PATTERN.matcher(modEntities)
        while (matcher.find()) {
            val fieldName = matcher.group(1)!!
            val registryId = matcher.group(2)!!
            val registration = "event.registerEntityRenderer(ModEntities." + fieldName + ".get(),"
            if (!clientEvents.contains(registration)) {
                missing.add(fieldName + " (" + registryId + ")")
            }
        }

        assertTrue(missing.isEmpty()) {
            "Every registered entity should keep a client renderer registration: " +
                    missing.joinToString(", ")
        }
    }
}
