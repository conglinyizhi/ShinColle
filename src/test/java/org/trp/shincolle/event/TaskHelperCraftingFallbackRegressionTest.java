package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskHelperCraftingFallbackRegressionTest {
    private static final Path TASK_HELPER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/utility/TaskHelper.kt");

    @Test
    void craftingTaskShouldFallbackOnlyToPairedInventoryContainersWhenNoSidesAreConfigured() throws IOException {
        String source = Files.readString(TASK_HELPER_SOURCE);
        int fallbackStart = source.indexOf("if (inHandlers.isEmpty() && outHandlers.isEmpty()) {");
        int fallbackEnd = source.indexOf("int maxCraft = host.getLevel() / 20 + 1;");
        String fallbackBlock = source.substring(fallbackStart, fallbackEnd);

        assertTrue(fallbackStart >= 0 && fallbackEnd > fallbackStart,
                "Crafting task should keep an explicit fallback block for unconfigured side handlers");
        assertTrue(fallbackBlock.contains("if (inHandlers.isEmpty() && outHandlers.isEmpty()) {"),
                "Crafting task should detect when no side handlers are configured");
        assertTrue(fallbackBlock.contains("if (!(targetBE instanceof net.minecraft.world.Container container)) {"),
                "Crafting task should reject non-inventory block entities when no sided handlers are configured");
        assertTrue(!fallbackBlock.contains("level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, chestPos, null)"),
                "Crafting task should not fallback to a generic direct item-handler capability");
        assertTrue(fallbackBlock.contains("IItemHandler fallbackHandler = new net.neoforged.neoforge.items.wrapper.InvWrapper(container);"),
                "Crafting task should only fallback to wrapping the paired inventory container");
        assertTrue(fallbackBlock.contains("inHandlers = List.of(fallbackHandler);"),
                "Crafting task should route material pulls through the fallback handler");
        assertTrue(fallbackBlock.contains("outHandlers = List.of(fallbackHandler);"),
                "Crafting task should route crafted outputs through the fallback handler");
    }
}
