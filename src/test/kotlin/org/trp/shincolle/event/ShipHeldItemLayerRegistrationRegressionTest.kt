package org.trp.shincolle.event

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class ShipHeldItemLayerRegistrationRegressionTest {
    private val CLIENT_EVENT_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.kt")
    private val HELD_ITEM_LAYER_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/client/renderer/layer/ShipHeldItemLayer.kt")

    @Test
    fun shipRenderersShouldKeepSharedHeldItemLayerHook() {
        val clientEvents = Files.readString(CLIENT_EVENT_SOURCE)
        val heldItemLayer = Files.readString(HELD_ITEM_LAYER_SOURCE)

        assertTrue(clientEvents.contains("public static void addRenderLayers(EntityRenderersEvent.AddLayers event) {")) {
            "Client event bus should keep the AddLayers hook for ship held items"
        }
        assertTrue(clientEvents.contains("for (var entry : ModEntities.ENTITY_TYPES.getEntries()) {")) {
            "Held item layer registration should keep iterating over mod entity types"
        }
        assertTrue(
            clientEvents.contains("renderer instanceof LivingEntityRenderer<?, ?> livingRenderer")
                && clientEvents.contains("livingRenderer.getModel() instanceof ShipModelBaseAdv")
        ) {
            "Held item layer registration should keep filtering to living renderers using ShipModelBaseAdv"
        }
        assertTrue(clientEvents.contains("addHeldItemLayerUnchecked(livingRenderer);")) {
            "Ship models should keep using the shared held item layer helper"
        }
        assertTrue(clientEvents.contains("renderer.addLayer(new ShipHeldItemLayer(renderer));")) {
            "Held item layer helper should keep attaching ShipHeldItemLayer"
        }

        assertTrue(heldItemLayer.contains("extends RenderLayer<T, M>")) {
            "ShipHeldItemLayer should remain a render layer implementation"
        }
        assertTrue(heldItemLayer.contains("if (!(this.getParentModel() instanceof ShipModelBaseAdv<?> shipModel)) {")) {
            "ShipHeldItemLayer should keep guarding against non-ship parent models"
        }
    }
}
