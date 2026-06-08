package org.trp.shincolle.event

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

import org.junit.jupiter.api.Assertions.assertTrue

class ItemCustomRendererRegressionTest {
    private data class ItemRendererExpectation(
            val itemFieldName: String,
            val itemSource: Path,
            val itemClassName: String,
            val rendererClassName: String
    )

    private val ITEM_REGISTRY_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.kt")

    private val ITEMS_REQUIRING_CUSTOM_RENDERERS = listOf(
            ItemRendererExpectation(
                    "DESK",
                    Path.of("src/main/java/org/trp/shincolle/item/DeskBlockItem.kt"),
                    "DeskBlockItem",
                    "DeskItemRenderer"
            ),
            ItemRendererExpectation(
                    "SMALL_SHIPYARD",
                    Path.of("src/main/java/org/trp/shincolle/item/SmallShipyardBlockItem.kt"),
                    "SmallShipyardBlockItem",
                    "SmallShipyardItemRenderer"
            )
    )

    @Test
    fun customRenderedBlockItemsShouldKeepClientRendererInitialization() {
        val itemRegistry = Files.readString(ITEM_REGISTRY_SOURCE)

        for (expectation in ITEMS_REQUIRING_CUSTOM_RENDERERS) {
            val itemSource = Files.readString(expectation.itemSource)

            assertTrue(itemRegistry.contains("public static final DeferredItem<Item> " + expectation.itemFieldName + " =")) {
                "Expected item field " + expectation.itemFieldName + " to remain registered in ModItems"
            }
            assertTrue(itemRegistry.contains("new " + expectation.itemClassName + "(")
                            || itemRegistry.contains("new org.trp.shincolle.item." + expectation.itemClassName + "(")) {
                "Expected item " + expectation.itemFieldName + " to keep using " + expectation.itemClassName
            }
            assertTrue(itemSource.contains("void initializeClient(")) {
                expectation.itemClassName + " should keep initializeClient for custom item rendering"
            }
            assertTrue(itemSource.contains("getCustomRenderer()")) {
                expectation.itemClassName + " should keep exposing a custom renderer"
            }
            assertTrue(itemSource.contains("new " + expectation.rendererClassName + "(")) {
                expectation.itemClassName + " should keep constructing " + expectation.rendererClassName
            }
        }
    }
}
