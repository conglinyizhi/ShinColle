package org.trp.shincolle.build

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue

class HeldItemVisualConfigRegressionTest {
    private data class VisualConfigExpectation(
        val translationKey: String,
        val configValueField: String,
        val runtimeField: String,
        val runtimeUsageSnippet: String
    )

    private val CONFIG_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/Config.kt")
    private val CONFIG_SCREEN_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/client/gui/ShincolleConfigScreen.kt")
    private val HELD_ITEM_LAYER_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/client/renderer/layer/ShipHeldItemLayer.kt")

    private val EXPECTATIONS = listOf(
        VisualConfigExpectation("config.shincolle.scaleHeldItem", "CLIENT_SCALE_HELD_ITEM", "scaleHeldItem",
            "float itemScale = org.trp.shincolle.Config.scaleHeldItem * 1.8F;"),
        VisualConfigExpectation("config.shincolle.offsetHeldItemX", "CLIENT_OFFSET_HELD_ITEM_X", "offsetHeldItemX",
            "float ox = (offset[0] + org.trp.shincolle.Config.offsetHeldItemX) / 16.0F;"),
        VisualConfigExpectation("config.shincolle.offsetHeldItemY", "CLIENT_OFFSET_HELD_ITEM_Y", "offsetHeldItemY",
            "float oy = (offset[1] + org.trp.shincolle.Config.offsetHeldItemY) / 16.0F;"),
        VisualConfigExpectation("config.shincolle.offsetHeldItemZ", "CLIENT_OFFSET_HELD_ITEM_Z", "offsetHeldItemZ",
            "float oz = (offset[2] + org.trp.shincolle.Config.offsetHeldItemZ) / 16.0F;")
    )

    @Test
    fun heldItemVisualConfigEntriesShouldStayConnectedToRuntimeUsage() {
        val config = Files.readString(CONFIG_SOURCE)
        val configScreen = Files.readString(CONFIG_SCREEN_SOURCE)
        val heldItemLayer = Files.readString(HELD_ITEM_LAYER_SOURCE)

        for (expectation in EXPECTATIONS) {
            assertTrue(config.contains("public static final ModConfigSpec.DoubleValue " + expectation.configValueField + ";")) {
                "Expected Config to keep defining " + expectation.configValueField
            }
            assertTrue(config.contains(expectation.runtimeField + " = " + expectation.configValueField + ".get().floatValue();")) {
                "Expected Config.onLoad to keep syncing runtime field " + expectation.runtimeField
            }
            assertTrue(configScreen.contains("Component.translatable(\"" + expectation.translationKey + "\")")
                    && configScreen.contains("Config." + expectation.configValueField + ".get()")
                    && configScreen.contains("Config." + expectation.configValueField + "::set")) {
                "Expected ShincolleConfigScreen to keep exposing " + expectation.configValueField
            }
            assertTrue(heldItemLayer.contains(expectation.runtimeUsageSnippet)) {
                "Expected ShipHeldItemLayer to keep using runtime field " + expectation.runtimeField
            }
        }
    }
}
