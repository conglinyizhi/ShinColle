package org.trp.shincolle.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;
import org.trp.shincolle.init.ModItems;
import org.trp.shincolle.utility.RecipePaperData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipePaperDataTest {
    private static final String RECIPE_TAG = "Recipe";
    private static final String SLOT_TAG = "Slot";

    @Test
    void recipePaperDataShouldPersistLegacyGridAndStoredPreviewResultSlots() {
        ItemStack host = new ItemStack(ModItems.RECIPE_PAPER.get());
        List<ItemStack> grid = new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));
        ItemStack firstIngredient = new ItemStack(ModItems.GRUDGE.get(), 3);
        ItemStack secondIngredient = new ItemStack(ModItems.ABYSS_NUGGET.get(), 2);
        ItemStack result = new ItemStack(ModItems.MODERN_KIT.get());

        grid.set(0, firstIngredient);
        grid.set(4, secondIngredient);

        RecipePaperData.saveRecipeGrid(host, RegistryAccess.EMPTY, grid, result);

        ListTag stored = host.get(DataComponents.CUSTOM_DATA).copyTag().getList(RECIPE_TAG, CompoundTag.TAG_COMPOUND);

        assertTrue(stored.size() == 3);
        assertTrue(containsSlot(stored, 0));
        assertTrue(containsSlot(stored, 4));
        assertTrue(containsSlot(stored, 9));
    }

    @Test
    void recipePaperDataShouldReturnEmptyStacksWithoutRegistryContext() {
        ItemStack host = new ItemStack(ModItems.RECIPE_PAPER.get());
        List<ItemStack> grid = new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));
        ItemStack result = new ItemStack(ModItems.TRAINING_BOOK.get());

        RecipePaperData.saveRecipeGrid(host, RegistryAccess.EMPTY, grid, result);

        ItemStack[] restoredGrid = RecipePaperData.loadRecipeGrid(host, RegistryAccess.EMPTY);
        ItemStack restoredResult = RecipePaperData.loadStoredRecipeResult(host, RegistryAccess.EMPTY);

        assertTrue(!RecipePaperData.hasAnyRecipeIngredient(restoredGrid));
        assertTrue(restoredResult.isEmpty());
    }

    private static boolean containsSlot(ListTag list, int slot) {
        for (int i = 0; i < list.size(); i++) {
            if (list.getCompound(i).getInt(SLOT_TAG) == slot) {
                return true;
            }
        }
        return false;
    }
}
