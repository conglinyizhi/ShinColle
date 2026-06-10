package org.trp.shincolle.item

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems

class DeskItemBookTest {

    @Test
    fun deskItemBookShouldExposePatchouliBookIdAndTooltipHints() {
        val item = ModItems.DESK_ITEM_BOOK.get() as DeskItemBook
        val stack = ItemStack(item)
        val tooltip = mutableListOf<Component?>()

        item.appendHoverText(stack, TooltipContext.EMPTY, tooltip, TooltipFlag.Default.NORMAL)

        assertEquals("item.shincolle.deskitembook.name", item.descriptionId)
        assertEquals("shincolle:shincolle_manual", DeskItemBook.PATCHOULI_BOOK_ID.toString())
        assertEquals(2, tooltip.size)
        assertEquals("tooltip.shincolle.deskitembook.open_manual", translationKey(tooltip[0]!!))
        assertEquals("tooltip.shincolle.deskitembook.patchouli_manual", translationKey(tooltip[1]!!))
    }

    private fun translationKey(component: Component): String {
        return (component.contents as TranslatableContents).key
    }
}
