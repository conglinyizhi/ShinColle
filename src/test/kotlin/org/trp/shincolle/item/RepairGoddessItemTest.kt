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

class RepairGoddessItemTest {

    @Test
    fun repairGoddessShouldAlwaysFoilAndExposeSingleTooltipLine() {
        val item = ModItems.REPAIR_GODDESS.get() as RepairGoddessItem
        val stack = ItemStack(item)
        val tooltip = mutableListOf<Component?>()

        assertTrue(item.isFoil(stack))
        assertEquals(16, item.defaultMaxStackSize)

        item.appendHoverText(stack, TooltipContext.EMPTY, tooltip, TooltipFlag.Default.NORMAL)

        assertEquals(1, tooltip.size)
        assertEquals("gui.shincolle.repairgoddess", translationKey(tooltip[0]!!))
    }

    private fun translationKey(component: Component): String {
        return (component.contents as TranslatableContents).key
    }
}
