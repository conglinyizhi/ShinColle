package org.trp.shincolle.item

import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems

class AbyssNuggetItemTest {

    @Test
    fun abyssNuggetShouldClampVariantsAndExposeBothCreativeStacks() {
        val item = ModItems.ABYSS_NUGGET.get() as AbyssNuggetItem

        val base = item.createVariantStack(-1)
        val variant = item.createVariantStack(1)
        val clamped = item.createVariantStack(99)

        assertEquals(0, item.getVariant(base))
        assertEquals(1, item.getVariant(variant))
        assertEquals(1, item.getVariant(clamped))
        assertEquals(0, item.getModelVariant(base))
        assertEquals(1, item.getModelVariant(variant))

        val stacks = mutableListOf<ItemStack>()
        item.addAllVariantsToCreativeTab(CollectingOutput(stacks))
        assertEquals(2, stacks.size)
        assertEquals(0, item.getVariant(stacks[0]))
        assertEquals(1, item.getVariant(stacks[1]))
    }

    @Test
    fun abyssNuggetShouldUseLegacyTranslationKeysPerVariant() {
        val item = ModItems.ABYSS_NUGGET.get() as AbyssNuggetItem

        val baseKey = (item.getName(item.createVariantStack(0)).contents as TranslatableContents).key
        val variantKey = (item.getName(item.createVariantStack(1)).contents as TranslatableContents).key

        assertEquals("item.shincolle.AbyssNugget.name", baseKey)
        assertEquals("item.shincolle.AbyssNugget1.name", variantKey)
    }

    private class CollectingOutput(private val stacks: MutableList<ItemStack>) : CreativeModeTab.Output {
        override fun accept(stack: ItemStack, visibility: CreativeModeTab.TabVisibility) {
            stacks += stack.copy()
        }
    }
}
