package org.trp.shincolle.item

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.TooltipFlag
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems

class InstantConstructionMaterialItemTest {

    @Test
    fun instantConstructionMaterialShouldExposeLocalizedTooltipLines() {
        val item = ModItems.INSTANT_CON_MAT.get() as InstantConstructionMaterialItem
        val stack = ItemStack(item)
        val tooltip = mutableListOf<Component>()

        item.appendHoverText(stack, TooltipContext.EMPTY, tooltip, TooltipFlag.Default.NORMAL)

        assertEquals(2, tooltip.size)
        assertEquals("gui.shincolle.instantconmat", translationKey(tooltip[0]))
        assertEquals("gui.shincolle.instantconmat.slot", translationKey(tooltip[1]))
    }

    private fun translationKey(component: Component): String {
        return (component.contents as TranslatableContents).key
    }
}
