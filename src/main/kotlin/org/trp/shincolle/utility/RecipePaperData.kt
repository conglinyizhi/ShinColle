package org.trp.shincolle.utility

import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level

object RecipePaperData {
    private const val RECIPE_TAG = "Recipe"
    private const val SLOT_TAG = "Slot"
    private const val GRID_SIZE = 9

    @JvmStatic
    fun loadRecipeGrid(hostStack: ItemStack, registries: HolderLookup.Provider): Array<ItemStack> {
        val grid = Array(GRID_SIZE) { ItemStack.EMPTY }
        val customData = hostStack.get(DataComponents.CUSTOM_DATA) ?: return grid
        val tag = customData.copyTag()
        if (!tag.contains(RECIPE_TAG, 9)) {
            return grid
        }

        val list = tag.getList(RECIPE_TAG, 10)
        for (i in 0 until list.size) {
            val itemTag = list.getCompound(i)
            val slot = itemTag.getInt(SLOT_TAG)
            if (slot in 0 until GRID_SIZE) {
                grid[slot] = ItemStack.parseOptional(registries, itemTag)
            }
        }
        return grid
    }

    @JvmStatic
    fun saveRecipeGrid(hostStack: ItemStack, registries: HolderLookup.Provider, grid: List<ItemStack>) {
        val list = ListTag()
        for (slot in 0 until minOf(grid.size, GRID_SIZE)) {
            val stack = grid[slot]
            if (stack.isEmpty) {
                continue
            }

            val itemTag = stack.save(registries) as CompoundTag
            itemTag.putInt(SLOT_TAG, slot)
            list.add(itemTag)
        }

        hostStack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY) { data ->
            data.update { tag -> tag.put(RECIPE_TAG, list) }
        }
    }

    @JvmStatic
    fun getRecipePreviewResult(level: Level, grid: List<ItemStack>): ItemStack {
        val input = CraftingInput.of(3, 3, grid)
        val recipe = level.recipeManager.getRecipeFor(RecipeType.CRAFTING, input, level)
        return if (recipe.isPresent) {
            recipe.get().value().assemble(input, level.registryAccess())
        } else {
            ItemStack.EMPTY
        }
    }

    @JvmStatic
    fun hasAnyRecipeIngredient(grid: List<ItemStack>): Boolean {
        return grid.any { !it.isEmpty }
    }

    @JvmStatic
    fun hasAnyRecipeIngredient(grid: Array<ItemStack>): Boolean {
        return grid.any { !it.isEmpty }
    }
}
