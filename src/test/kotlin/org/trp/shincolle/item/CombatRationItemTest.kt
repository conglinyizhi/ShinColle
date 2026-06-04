package org.trp.shincolle.item

import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.util.RandomSource
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems

class CombatRationItemTest {

    @Test
    fun combatRationShouldClampVariantsAndExposeAllCreativeStacks() {
        val item = ModItems.COMBAT_RATION.get() as CombatRationItem

        val base = item.createVariantStack(-1)
        val variant = item.createVariantStack(4)
        val clamped = item.createVariantStack(99)

        assertEquals(6, item.variantCount)
        assertEquals(0, item.getVariant(base))
        assertEquals(4, item.getVariant(variant))
        assertEquals(5, item.getVariant(clamped))
        assertEquals(0, item.getModelVariant(base))
        assertEquals(4, item.getModelVariant(variant))
        assertEquals(5, item.getModelVariant(clamped))

        val stacks = mutableListOf<ItemStack>()
        item.addAllVariantsToCreativeTab(CollectingOutput(stacks))
        assertEquals(item.variantCount, stacks.size)
        stacks.forEachIndexed { index, stack -> assertEquals(index, item.getVariant(stack)) }
    }

    @Test
    fun combatRationShouldUseLegacyTranslationKeysAndStatTables() {
        val item = ModItems.COMBAT_RATION.get() as CombatRationItem

        val baseKey = (item.getName(item.createVariantStack(0)).contents as TranslatableContents).key
        val variantKey = (item.getName(item.createVariantStack(5)).contents as TranslatableContents).key

        assertEquals("item.shincolle.CombatRation.name", baseKey)
        assertEquals("item.shincolle.CombatRation5.name", variantKey)
        assertEquals(900, CombatRationItem.getFoodValue(-1))
        assertEquals(100, CombatRationItem.getFoodValue(4))
        assertEquals(900, CombatRationItem.getFoodValue(99))
        assertEquals(1400, CombatRationItem.getMoraleValue(-1))
        assertEquals(3000, CombatRationItem.getMoraleValue(4))
        assertEquals(4000, CombatRationItem.getMoraleValue(99))
        assertEquals(9, CombatRationItem.getFuelGainMin(0))
        assertEquals(18, CombatRationItem.getFuelGainMax(0))
        assertEquals(1, CombatRationItem.getFuelGainMin(4))
        assertEquals(2, CombatRationItem.getFuelGainMax(4))
    }

    @Test
    fun combatRationShouldRollFuelGainWithinExpectedRange() {
        val baseRoll = CombatRationItem.rollFuelGain(RandomSource.create(1234L), 0)
        val smallRoll = CombatRationItem.rollFuelGain(RandomSource.create(5678L), 4)

        assertTrue(baseRoll >= CombatRationItem.getFuelGainMin(0))
        assertTrue(baseRoll <= CombatRationItem.getFuelGainMax(0))
        assertTrue(smallRoll >= CombatRationItem.getFuelGainMin(4))
        assertTrue(smallRoll <= CombatRationItem.getFuelGainMax(4))
    }

    private class CollectingOutput(private val stacks: MutableList<ItemStack>) : CreativeModeTab.Output {
        override fun accept(stack: ItemStack, visibility: CreativeModeTab.TabVisibility) {
            stacks += stack.copy()
        }
    }
}
