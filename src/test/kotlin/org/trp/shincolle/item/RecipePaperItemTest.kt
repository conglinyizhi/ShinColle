package org.trp.shincolle.item

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.item.ItemStack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems

class RecipePaperItemTest {

    @Test
    fun recipePaperTooltipHelperShouldRenderStoredResultAndIngredientLines() {
        val grid = MutableList(9) { ItemStack.EMPTY }
        grid[0] = ItemStack(ModItems.GRUDGE.get(), 2)
        grid[4] = ItemStack(ModItems.ABYSS_NUGGET.get(), 1)

        val result = ItemStack(ModItems.MODERN_KIT.get())
        val tooltip = mutableListOf<Component?>()

        RecipePaperItem.appendRecipePreviewTooltip(tooltip, grid, result)

        assertEquals(4, tooltip.size)
        assertEquals("gui.shincolle.recipepaper.result", translationKey(tooltip[0]!!))
        assertTrue(tooltip[0]!!.string.contains(ModItems.MODERN_KIT.get()!!.getName(result).string))
        assertEquals("gui.shincolle.recipepaper.material", translationKey(tooltip[1]!!))
        assertTrue(tooltip[2]!!.string.contains(ModItems.GRUDGE.get()!!.getName(grid[0]).string))
        assertTrue(tooltip[3]!!.string.contains(ModItems.ABYSS_NUGGET.get()!!.getName(grid[4]).string))
    }

    private fun translationKey(component: Component): String {
        return (component.contents as TranslatableContents).key
    }
}
