package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskHelperCookingFallbackRegressionTest {
    private static final Path TASK_HELPER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/utility/TaskHelper.java");

    @Test
    void cookingFallbackHandlerShouldSplitInputFuelAndOutputSlots() throws IOException {
        String source = Files.readString(TASK_HELPER_SOURCE);

        assertTrue(source.contains("inHandlers = List.of(singleSlotView(fallbackHandler, 0));"),
                "Cooking fallback should expose only slot 0 as the input slot");
        assertTrue(source.contains("fuelHandlers = List.of(singleSlotView(fallbackHandler, 1));"),
                "Cooking fallback should expose only slot 1 as the fuel slot");
        assertTrue(source.contains("outHandlers = List.of(singleSlotView(fallbackHandler, 2));"),
                "Cooking fallback should expose only slot 2 as the output slot");
    }
}
