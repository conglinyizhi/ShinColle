package org.trp.shincolle.utility;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public final class RecipePaperData {
    private static final String RECIPE_TAG = "Recipe";
    private static final String SLOT_TAG = "Slot";
    private static final int GRID_SIZE = 9;
    private static final int RESULT_SLOT = 9;

    private RecipePaperData() {
    }

    public static ItemStack[] loadRecipeGrid(ItemStack hostStack, HolderLookup.Provider registries) {
        ItemStack[] grid = new ItemStack[GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            grid[i] = ItemStack.EMPTY;
        }

        CustomData customData = hostStack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return grid;
        }

        CompoundTag tag = customData.copyTag();
        if (!tag.contains(RECIPE_TAG, 9)) {
            return grid;
        }

        ListTag list = tag.getList(RECIPE_TAG, 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag itemTag = list.getCompound(i);
            int slot = itemTag.getInt(SLOT_TAG);
            if (slot >= 0 && slot < GRID_SIZE) {
                grid[slot] = ItemStack.parseOptional(registries, itemTag);
            }
        }

        return grid;
    }

    public static void saveRecipeGrid(ItemStack hostStack, HolderLookup.Provider registries, List<ItemStack> grid, ItemStack result) {
        ListTag list = new ListTag();
        for (int slot = 0; slot < Math.min(grid.size(), GRID_SIZE); slot++) {
            ItemStack stack = grid.get(slot);
            if (stack.isEmpty()) {
                continue;
            }

            CompoundTag itemTag = new CompoundTag();
            stack.save(registries, itemTag);
            itemTag.putInt(SLOT_TAG, slot);
            list.add(itemTag);
        }

        if (!result.isEmpty()) {
            CompoundTag resultTag = new CompoundTag();
            result.save(registries, resultTag);
            resultTag.putInt(SLOT_TAG, RESULT_SLOT);
            list.add(resultTag);
        }

        hostStack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY,
                data -> data.update(tag -> tag.put(RECIPE_TAG, list)));
    }

    public static ItemStack loadStoredRecipeResult(ItemStack hostStack, HolderLookup.Provider registries) {
        CustomData customData = hostStack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return ItemStack.EMPTY;
        }

        CompoundTag tag = customData.copyTag();
        if (!tag.contains(RECIPE_TAG, 9)) {
            return ItemStack.EMPTY;
        }

        ListTag list = tag.getList(RECIPE_TAG, 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag itemTag = list.getCompound(i);
            if (itemTag.getInt(SLOT_TAG) == RESULT_SLOT) {
                return ItemStack.parseOptional(registries, itemTag);
            }
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack getRecipePreviewResult(Level level, List<ItemStack> grid) {
        CraftingInput input = CraftingInput.of(3, 3, grid);
        Optional<RecipeHolder<net.minecraft.world.item.crafting.CraftingRecipe>> recipe =
                level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level);
        return recipe.map(holder -> holder.value().assemble(input, level.registryAccess()))
                .orElse(ItemStack.EMPTY);
    }

    public static boolean hasAnyRecipeIngredient(List<ItemStack> grid) {
        for (ItemStack stack : grid) {
            if (!stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasAnyRecipeIngredient(ItemStack[] grid) {
        for (ItemStack stack : grid) {
            if (!stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
