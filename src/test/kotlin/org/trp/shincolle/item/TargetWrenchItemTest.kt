package org.trp.shincolle.item

import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems

class TargetWrenchItemTest {

    @Test
    fun targetWrenchShouldStoreAndClearMarkedWaypointCoordinates() {
        val item = ModItems.TARGET_WRENCH.get() as TargetWrenchItem
        val stack = ItemStack(item)
        val marked = BlockPos(12, 34, 56)

        assertFalse(hasMarked(item, stack))

        setMarked(item, stack, marked)

        assertTrue(hasMarked(item, stack))
        assertEquals(marked, getMarked(item, stack))

        clearMarked(item, stack)

        assertFalse(hasMarked(item, stack))
        assertEquals(BlockPos.ZERO, getMarked(item, stack))
        assertFalse(stack.has(DataComponents.CUSTOM_DATA))
    }

    @Test
    fun targetWrenchTooltipShouldExposeLocalizedHintsAndMarkedPosition() {
        val item = ModItems.TARGET_WRENCH.get() as TargetWrenchItem
        val stack = ItemStack(item)
        val tooltip = mutableListOf<Component>()

        setMarked(item, stack, BlockPos(1, 2, 3))
        item.appendHoverText(stack, TooltipContext.EMPTY, tooltip, TooltipFlag.Default.NORMAL)

        assertEquals(4, tooltip.size)
        assertEquals("gui.shincolle.wrench1", translationKey(tooltip[0]))
        assertEquals("gui.shincolle.wrench2", translationKey(tooltip[1]))
        assertEquals("gui.shincolle.wrench3", translationKey(tooltip[2]))
        assertTrue(tooltip[3].string.contains("1 2 3"))
    }

    private fun translationKey(component: Component): String {
        return (component.contents as TranslatableContents).key
    }

    private fun hasMarked(item: TargetWrenchItem, stack: ItemStack): Boolean {
        val method = TargetWrenchItem::class.java.getDeclaredMethod("hasMarked", ItemStack::class.java)
        method.isAccessible = true
        return method.invoke(item, stack) as Boolean
    }

    private fun getMarked(item: TargetWrenchItem, stack: ItemStack): BlockPos {
        val method = TargetWrenchItem::class.java.getDeclaredMethod("getMarked", ItemStack::class.java)
        method.isAccessible = true
        return method.invoke(item, stack) as BlockPos
    }

    private fun setMarked(item: TargetWrenchItem, stack: ItemStack, pos: BlockPos) {
        val method = TargetWrenchItem::class.java.getDeclaredMethod("setMarked", ItemStack::class.java, BlockPos::class.java)
        method.isAccessible = true
        method.invoke(item, stack, pos)
    }

    private fun clearMarked(item: TargetWrenchItem, stack: ItemStack) {
        val method = TargetWrenchItem::class.java.getDeclaredMethod("clearMarked", ItemStack::class.java)
        method.isAccessible = true
        method.invoke(item, stack)
    }
}
