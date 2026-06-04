package org.trp.shincolle.item

import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.item.ItemStack
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.utility.RecipePaperData

class RecipePaperDataTest {
    @Test
    fun recipePaperDataShouldPersistLegacyGridAndStoredPreviewResultSlots() {
        val host = ItemStack(ModItems.RECIPE_PAPER.get())
        val grid = MutableList(9) { ItemStack.EMPTY }
        val firstIngredient = ItemStack(ModItems.GRUDGE.get(), 3)
        val secondIngredient = ItemStack(ModItems.ABYSS_NUGGET.get(), 2)
        val result = ItemStack(ModItems.MODERN_KIT.get())

        grid[0] = firstIngredient
        grid[4] = secondIngredient

        RecipePaperData.saveRecipeGrid(host, RegistryAccess.EMPTY, grid, result)

        val stored = host.get(DataComponents.CUSTOM_DATA)!!.copyTag().getList(RECIPE_TAG, CompoundTag.TAG_COMPOUND.toInt())

        assertTrue(stored.size == 3)
        assertTrue(containsSlot(stored, 0))
        assertTrue(containsSlot(stored, 4))
        assertTrue(containsSlot(stored, 9))
    }

    @Test
    fun recipePaperDataShouldReturnEmptyStacksWithoutRegistryContext() {
        val host = ItemStack(ModItems.RECIPE_PAPER.get())
        val grid = MutableList(9) { ItemStack.EMPTY }
        val result = ItemStack(ModItems.TRAINING_BOOK.get())

        RecipePaperData.saveRecipeGrid(host, RegistryAccess.EMPTY, grid, result)

        val restoredGrid = RecipePaperData.loadRecipeGrid(host, RegistryAccess.EMPTY)
        val restoredResult = RecipePaperData.loadStoredRecipeResult(host, RegistryAccess.EMPTY)

        assertTrue(!RecipePaperData.hasAnyRecipeIngredient(restoredGrid))
        assertTrue(restoredResult.isEmpty)
    }

    private fun containsSlot(list: ListTag, slot: Int): Boolean {
        for (index in 0 until list.size) {
            if (list.getCompound(index).getInt(SLOT_TAG) == slot) {
                return true
            }
        }
        return false
    }

    companion object {
        private const val RECIPE_TAG = "Recipe"
        private const val SLOT_TAG = "Slot"
    }
}
