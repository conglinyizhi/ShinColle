package org.trp.shincolle.item

import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems

class ShipTankItemTest {

    @Test
    fun shipTankShouldClampVariantsAndExposeAllCreativeStacks() {
        val item = ModItems.SHIP_TANK.get() as ShipTankItem

        val base = item.createVariantStack(-1)
        val variant = item.createVariantStack(2)
        val clamped = item.createVariantStack(99)

        assertEquals(4, item.variantCount)
        assertEquals(0, item.getVariant(base))
        assertEquals(2, item.getVariant(variant))
        assertEquals(3, item.getVariant(clamped))
        assertEquals(0, item.getModelVariant(base))
        assertEquals(2, item.getModelVariant(variant))
        assertEquals(3, item.getModelVariant(clamped))

        val stacks = mutableListOf<ItemStack>()
        item.addAllVariantsToCreativeTab(CollectingOutput(stacks))
        assertEquals(item.variantCount, stacks.size)
        stacks.forEachIndexed { index, stack -> assertEquals(index, item.getVariant(stack)) }
    }

    @Test
    fun shipTankShouldUseLegacyTranslationKeysAndCapacityTables() {
        val item = ModItems.SHIP_TANK.get() as ShipTankItem

        val baseKey = (item.getName(item.createVariantStack(0)).contents as TranslatableContents).key
        val variantKey = (item.getName(item.createVariantStack(3)).contents as TranslatableContents).key

        assertEquals("item.shincolle.ShipTank.name", baseKey)
        assertEquals("item.shincolle.ShipTank3.name", variantKey)
        assertEquals(32000, ShipTankItem.getCapacity(-1))
        assertEquals(512000, ShipTankItem.getCapacity(2))
        assertEquals(2048000, ShipTankItem.getCapacity(99))
        assertEquals(512000, ShipTankItem.getCapacity(item.createVariantStack(2)))
        assertEquals(32000, ShipTankItem.getCapacity(ItemStack(Items.STICK)))
    }

    private class CollectingOutput(private val stacks: MutableList<ItemStack>) : CreativeModeTab.Output {
        override fun accept(stack: ItemStack, visibility: CreativeModeTab.TabVisibility) {
            stacks += stack.copy()
        }
    }
}
