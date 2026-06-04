package org.trp.shincolle.item

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems

class PointerItemTest {

    @Test
    fun pointerShouldClampModesCycleAndExposeCreativeVariants() {
        val item = ModItems.POINTER_ITEM.get() as PointerItem

        val base = item.createVariantStack(-1)
        val group = item.createVariantStack(PointerItem.MODE_GROUP)
        val formation = item.createVariantStack(99)

        assertEquals(PointerItem.MODE_SINGLE, item.getMode(base))
        assertEquals(PointerItem.MODE_GROUP, item.getMode(group))
        assertEquals(PointerItem.MODE_FORMATION, item.getMode(formation))
        assertEquals(PointerItem.MODE_SINGLE, item.getModelVariant(base))
        assertEquals(PointerItem.MODE_GROUP, item.getModelVariant(group))
        assertEquals(PointerItem.MODE_FORMATION, item.getModelVariant(formation))

        assertEquals(PointerItem.MODE_GROUP, item.cycleMode(base))
        assertEquals(PointerItem.MODE_GROUP, item.getMode(base))
        assertEquals(PointerItem.MODE_FORMATION, item.cycleMode(base))
        assertEquals(PointerItem.MODE_FORMATION, item.getMode(base))
        assertEquals(PointerItem.MODE_SINGLE, item.cycleMode(base))
        assertEquals(PointerItem.MODE_SINGLE, item.getMode(base))
        assertFalse(base.has(DataComponents.CUSTOM_DATA))

        val stacks = mutableListOf<ItemStack>()
        item.addAllVariantsToCreativeTab(CollectingOutput(stacks))
        assertEquals(3, stacks.size)
        stacks.forEachIndexed { index, stack -> assertEquals(index, item.getMode(stack)) }
    }

    @Test
    fun pointerShouldUseStableModeTranslationKeys() {
        assertEquals("gui.shincolle.pointer0", PointerItem.getModeTranslationKey(-1))
        assertEquals("gui.shincolle.pointer0", PointerItem.getModeTranslationKey(PointerItem.MODE_SINGLE))
        assertEquals("gui.shincolle.pointer1", PointerItem.getModeTranslationKey(PointerItem.MODE_GROUP))
        assertEquals("gui.shincolle.pointer2", PointerItem.getModeTranslationKey(PointerItem.MODE_FORMATION))
        assertEquals("gui.shincolle.pointer0", PointerItem.getModeTranslationKey(99))
    }

    private class CollectingOutput(private val stacks: MutableList<ItemStack>) : CreativeModeTab.Output {
        override fun accept(stack: ItemStack, visibility: CreativeModeTab.TabVisibility) {
            stacks += stack.copy()
        }
    }
}
