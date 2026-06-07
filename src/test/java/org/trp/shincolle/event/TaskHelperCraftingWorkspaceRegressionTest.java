package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskHelperCraftingWorkspaceRegressionTest {
    private static final Path TASK_HELPER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/utility/TaskHelper.kt");

    @Test
    void craftingShouldUseDedicatedWorkingSlotsInsteadOfWholeInventory() throws IOException {
        String source = Files.readString(TASK_HELPER_SOURCE);

        assertTrue(source.contains("private static final int CRAFTING_WORK_START_SLOT = 12;"),
                "Crafting task should reserve the old working area starting at slot 12");
        assertTrue(source.contains("private static final int CRAFTING_WORK_SLOT_COUNT = 9;"),
                "Crafting task should reserve 9 working slots");
        assertTrue(source.contains("ItemStack workStack = inv.getStackInSlot(CRAFTING_WORK_START_SLOT + slot);"),
                "Crafting should read ingredients from the dedicated working slots");
        assertTrue(source.contains("inv.setStackInSlot(CRAFTING_WORK_START_SLOT + slot, workStack);"),
                "Crafting should stage pulled materials into the dedicated working slots");
        assertTrue(source.contains("private static ItemStack pullCraftingIngredient(List<IItemHandler> inHandlers, ItemStack template,"),
                "Crafting ingredient pulls should only depend on the paired container handlers");
        assertTrue(!source.contains("InventoryHelper.getAndRemoveItem(\n                shipInv, template, 1, checkMeta, checkNbt, checkOre, craftWorkingSlots())"),
                "Crafting should not pull fallback ingredients from the ship inventory outside the dedicated working slots");
    }
}
