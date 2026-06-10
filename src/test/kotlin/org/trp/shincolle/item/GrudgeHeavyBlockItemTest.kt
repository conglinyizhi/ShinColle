package org.trp.shincolle.item

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.trp.shincolle.crafting.ShipyardRecipes
import org.trp.shincolle.init.ModItems

class GrudgeHeavyBlockItemTest {

    @Test
    fun grudgeHeavyBlockShouldExposeStoredMaterialsAndFuelInTooltip() {
        val item = ModItems.GRUDGE_HEAVY_BLOCK.get() as GrudgeHeavyBlockItem
        val stack = ItemStack(item)
        ShipyardRecipes.putHeavyGrudgeStorageTag(stack, intArrayOf(12, 34, 56, 78), 90)

        val tooltip = mutableListOf<Component?>()
        item.appendHoverText(stack, TooltipContext.EMPTY, tooltip, TooltipFlag.Default.NORMAL)

        assertEquals(5, tooltip.size)
        assertTrue(tooltip[0]!!.string.startsWith("12 "))
        assertTrue(tooltip[1]!!.string.startsWith("34 "))
        assertTrue(tooltip[2]!!.string.startsWith("56 "))
        assertTrue(tooltip[3]!!.string.startsWith("78 "))
        assertEquals("gui.shincolle.heavygrudge.fuel", translationKey(tooltip[4]!!))
        assertEquals(90, (tooltip[4]!!.contents as TranslatableContents).args[0])
    }

    private fun translationKey(component: Component): String {
        return (component.contents as TranslatableContents).key
    }
}
