package org.trp.shincolle.utility

import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.CraftingRecipe
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.UnaryOperator
import kotlin.math.min

object RecipePaperData {
    private const val RECIPE_TAG = "Recipe"
    private const val SLOT_TAG = "Slot"
    private const val GRID_SIZE = 9
    private const val RESULT_SLOT = 9

    @JvmStatic
    fun loadRecipeGrid(hostStack: ItemStack, registries: HolderLookup.Provider): Array<ItemStack?> {
        val grid = arrayOfNulls<ItemStack>(GRID_SIZE)
        for (i in 0..<GRID_SIZE) {
            grid[i] = ItemStack.EMPTY
        }

        val customData = hostStack.get<CustomData?>(DataComponents.CUSTOM_DATA) ?: return grid

        val tag = customData.copyTag()
        if (!tag.contains(RECIPE_TAG, 9)) {
            return grid
        }

        val list = tag.getList(RECIPE_TAG, 10)
        for (i in list.indices) {
            val itemTag = list.getCompound(i)
            val slot = itemTag.getInt(SLOT_TAG)
            if (slot >= 0 && slot < GRID_SIZE) {
                grid[slot] = ItemStack.parseOptional(registries, itemTag)
            }
        }

        return grid
    }

    fun saveRecipeGrid(
        hostStack: ItemStack,
        registries: HolderLookup.Provider,
        grid: MutableList<ItemStack>,
        result: ItemStack
    ) {
        val list = ListTag()
        for (slot in 0..<min(grid.size, GRID_SIZE)) {
            val stack = grid.get(slot)
            if (stack.isEmpty()) {
                continue
            }

            val itemTag = CompoundTag()
            stack.save(registries, itemTag)
            itemTag.putInt(SLOT_TAG, slot)
            list.add(itemTag)
        }

        if (!result.isEmpty()) {
            val resultTag = CompoundTag()
            result.save(registries, resultTag)
            resultTag.putInt(SLOT_TAG, RESULT_SLOT)
            list.add(resultTag)
        }

        hostStack.update<CustomData?>(
            DataComponents.CUSTOM_DATA, CustomData.EMPTY,
            UnaryOperator { data: CustomData? ->
                data!!.update(Consumer { tag: CompoundTag? ->
                    tag!!.put(
                        RECIPE_TAG,
                        list
                    )
                })
            })
    }

    @JvmStatic
    fun loadStoredRecipeResult(hostStack: ItemStack, registries: HolderLookup.Provider): ItemStack {
        val customData = hostStack.get<CustomData?>(DataComponents.CUSTOM_DATA)
        if (customData == null) {
            return ItemStack.EMPTY
        }

        val tag = customData.copyTag()
        if (!tag.contains(RECIPE_TAG, 9)) {
            return ItemStack.EMPTY
        }

        val list = tag.getList(RECIPE_TAG, 10)
        for (i in list.indices) {
            val itemTag = list.getCompound(i)
            if (itemTag.getInt(SLOT_TAG) == RESULT_SLOT) {
                return ItemStack.parseOptional(registries, itemTag)
            }
        }

        return ItemStack.EMPTY
    }

    fun getRecipePreviewResult(level: Level, grid: MutableList<ItemStack?>): ItemStack {
        val input = CraftingInput.of(3, 3, grid)
        val recipe =
            level.getRecipeManager().getRecipeFor<CraftingInput?, CraftingRecipe?>(RecipeType.CRAFTING, input, level)
        return recipe.map<ItemStack>(Function { holder: RecipeHolder<CraftingRecipe?>? ->
            holder!!.value().assemble(input, level.registryAccess())
        })
            .orElse(ItemStack.EMPTY)
    }

    fun hasAnyRecipeIngredient(grid: MutableList<ItemStack>): Boolean {
        for (stack in grid) {
            if (!stack.isEmpty()) {
                return true
            }
        }
        return false
    }

    fun hasAnyRecipeIngredient(grid: Array<ItemStack>): Boolean {
        for (stack in grid) {
            if (!stack.isEmpty()) {
                return true
            }
        }
        return false
    }
}
