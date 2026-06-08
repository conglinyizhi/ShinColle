package org.trp.shincolle.event

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

import org.junit.jupiter.api.Assertions.assertTrue

class BlockEntityRendererRegistrationRegressionTest {
    private val CLIENT_EVENT_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.kt")

    private val BLOCK_ENTITIES_REQUIRING_RENDERERS: List<String> = listOf(
            "SMALL_SHIPYARD",
            "LARGE_SHIPYARD",
            "DESK"
    )

    @Test
    fun blockEntitiesWithCustomVisualsShouldKeepRendererRegistrations() {
        val clientEvents = Files.readString(CLIENT_EVENT_SOURCE)

        for (fieldName in BLOCK_ENTITIES_REQUIRING_RENDERERS) {
            val registration = "event.registerBlockEntityRenderer(ModBlockEntities." + fieldName + ".get(),"
            assertTrue(clientEvents.contains(registration)) {
                "Block entity " + fieldName + " should keep a client renderer registration"
            }
        }
    }
}
