package org.trp.shincolle.init

import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.trp.shincolle.item.AbyssNuggetItem
import org.trp.shincolle.item.CombatRationItem
import org.trp.shincolle.item.GrudgeItem
import org.trp.shincolle.item.PointerItem
import org.trp.shincolle.item.ShipTankItem

class VariantCreativeTabHelperTest {

    @Test
    fun variantHelpersShouldEmitResolvedVariantStacks() {
        assertShipTankVariants()
        assertCombatRationVariants()
        assertGrudgeVariants()
        assertAbyssNuggetVariants()
        assertPointerVariants()
    }

    private fun assertShipTankVariants() {
        val item = ModItems.SHIP_TANK.get() as ShipTankItem
        val stacks = mutableListOf<ItemStack>()

        ModItems.addShipTankVariants(CollectingOutput(stacks))

        assertEquals(item.variantCount, stacks.size)
        stacks.forEachIndexed { index, stack -> assertEquals(index, item.getVariant(stack)) }
    }

    private fun assertCombatRationVariants() {
        val item = ModItems.COMBAT_RATION.get() as CombatRationItem
        val stacks = mutableListOf<ItemStack>()

        ModItems.addCombatRationVariants(CollectingOutput(stacks))

        assertEquals(item.variantCount, stacks.size)
        stacks.forEachIndexed { index, stack -> assertEquals(index, item.getVariant(stack)) }
    }

    private fun assertGrudgeVariants() {
        val item = ModItems.GRUDGE.get() as GrudgeItem
        val stacks = mutableListOf<ItemStack>()

        ModItems.addGrudgeVariants(CollectingOutput(stacks))

        assertEquals(2, stacks.size)
        assertEquals(0, item.getVariant(stacks[0]))
        assertEquals(1, item.getVariant(stacks[1]))
    }

    private fun assertAbyssNuggetVariants() {
        val item = ModItems.ABYSS_NUGGET.get() as AbyssNuggetItem
        val stacks = mutableListOf<ItemStack>()

        ModItems.addAbyssNuggetVariants(CollectingOutput(stacks))

        assertEquals(2, stacks.size)
        assertEquals(0, item.getVariant(stacks[0]))
        assertEquals(1, item.getVariant(stacks[1]))
    }

    private fun assertPointerVariants() {
        val item = ModItems.POINTER_ITEM.get() as PointerItem
        val stacks = mutableListOf<ItemStack>()

        ModItems.addPointerVariants(CollectingOutput(stacks))

        assertEquals(3, stacks.size)
        stacks.forEachIndexed { index, stack -> assertEquals(index, item.getMode(stack)) }
    }

    private class CollectingOutput(private val stacks: MutableList<ItemStack>) : CreativeModeTab.Output {
        override fun accept(stack: ItemStack, visibility: CreativeModeTab.TabVisibility) {
            stacks += stack.copy()
        }
    }
}
