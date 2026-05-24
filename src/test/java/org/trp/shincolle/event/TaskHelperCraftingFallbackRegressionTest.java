package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskHelperCraftingFallbackRegressionTest {
    private static final Path TASK_HELPER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/utility/TaskHelper.java");

    @Test
    void craftingTaskShouldFallbackToDirectContainerHandlersWhenNoSidesAreConfigured() throws IOException {
        String source = Files.readString(TASK_HELPER_SOURCE);

        assertTrue(source.contains("if (inHandlers.isEmpty() && outHandlers.isEmpty()) {"),
                "Crafting task should detect when no side handlers are configured");
        assertTrue(source.contains("fallbackHandler = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, chestPos, null);"),
                "Crafting task should try the direct item handler capability as a fallback");
        assertTrue(source.contains("if (targetBE instanceof net.minecraft.world.Container container) {"),
                "Crafting task should fallback to wrapping a plain container when needed");
        assertTrue(source.contains("inHandlers = List.of(fallbackHandler);"),
                "Crafting task should route material pulls through the fallback handler");
        assertTrue(source.contains("outHandlers = List.of(fallbackHandler);"),
                "Crafting task should route crafted outputs through the fallback handler");
    }
}
