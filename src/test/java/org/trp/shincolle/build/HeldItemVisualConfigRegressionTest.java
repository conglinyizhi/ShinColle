package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HeldItemVisualConfigRegressionTest {
    private record VisualConfigExpectation(
            String translationKey,
            String configValueField,
            String runtimeField,
            String runtimeUsageSnippet
    ) {
    }

    private static final Path CONFIG_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/Config.java");
    private static final Path CONFIG_SCREEN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/gui/ShincolleConfigScreen.java");
    private static final Path HELD_ITEM_LAYER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/renderer/layer/ShipHeldItemLayer.java");

    private static final List<VisualConfigExpectation> EXPECTATIONS = List.of(
            new VisualConfigExpectation("config.shincolle.scaleHeldItem", "CLIENT_SCALE_HELD_ITEM", "scaleHeldItem",
                    "float itemScale = org.trp.shincolle.Config.scaleHeldItem * 1.8F;"),
            new VisualConfigExpectation("config.shincolle.offsetHeldItemX", "CLIENT_OFFSET_HELD_ITEM_X", "offsetHeldItemX",
                    "float ox = (offset[0] + org.trp.shincolle.Config.offsetHeldItemX) / 16.0F;"),
            new VisualConfigExpectation("config.shincolle.offsetHeldItemY", "CLIENT_OFFSET_HELD_ITEM_Y", "offsetHeldItemY",
                    "float oy = (offset[1] + org.trp.shincolle.Config.offsetHeldItemY) / 16.0F;"),
            new VisualConfigExpectation("config.shincolle.offsetHeldItemZ", "CLIENT_OFFSET_HELD_ITEM_Z", "offsetHeldItemZ",
                    "float oz = (offset[2] + org.trp.shincolle.Config.offsetHeldItemZ) / 16.0F;")
    );

    @Test
    void heldItemVisualConfigEntriesShouldStayConnectedToRuntimeUsage() throws IOException {
        String config = Files.readString(CONFIG_SOURCE);
        String configScreen = Files.readString(CONFIG_SCREEN_SOURCE);
        String heldItemLayer = Files.readString(HELD_ITEM_LAYER_SOURCE);

        for (VisualConfigExpectation expectation : EXPECTATIONS) {
            assertTrue(config.contains("public static final ModConfigSpec.DoubleValue " + expectation.configValueField() + ";"),
                    () -> "Expected Config to keep defining " + expectation.configValueField());
            assertTrue(config.contains(expectation.runtimeField() + " = " + expectation.configValueField() + ".get().floatValue();"),
                    () -> "Expected Config.onLoad to keep syncing runtime field " + expectation.runtimeField());
            assertTrue(configScreen.contains("Component.translatable(\"" + expectation.translationKey() + "\")")
                            && configScreen.contains("Config." + expectation.configValueField() + ".get()")
                            && configScreen.contains("Config." + expectation.configValueField() + "::set"),
                    () -> "Expected ShincolleConfigScreen to keep exposing " + expectation.configValueField());
            assertTrue(heldItemLayer.contains(expectation.runtimeUsageSnippet()),
                    () -> "Expected ShipHeldItemLayer to keep using runtime field " + expectation.runtimeField());
        }
    }
}
