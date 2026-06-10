package org.trp.shincolle.item

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems

class OPToolItemTest {

    @Test
    fun opToolShouldAlwaysFoilAndExposeBothHotkeyTooltipLines() {
        val item = ModItems.OP_TOOL.get() as OPToolItem
        val stack = ItemStack(item)
        val tooltip = mutableListOf<Component?>()

        assertTrue(item.isFoil(stack))

        item.appendHoverText(stack, TooltipContext.EMPTY, tooltip, TooltipFlag.Default.NORMAL)

        assertEquals(2, tooltip.size)
        assertEquals("gui.shincolle.optool1", translationKey(tooltip[0]!!))
        assertEquals("gui.shincolle.optool2", translationKey(tooltip[1]!!))
    }

    private fun translationKey(component: Component): String {
        return (component.contents as TranslatableContents).key
    }
}
