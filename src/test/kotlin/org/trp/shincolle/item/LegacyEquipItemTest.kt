package org.trp.shincolle.item

import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.DeferredItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.CreativeTabVariantHelper
import org.trp.shincolle.init.ModItems

class LegacyEquipItemTest {

    @Test
    fun legacyEquipShouldClampVariantsAndResolveLegacyMappings() {
        val cannon = ModItems.EQUIP_CANNON.get() as LegacyEquipItem
        val airplane = ModItems.EQUIP_AIRPLANE.get() as LegacyEquipItem
        val torpedo = ModItems.EQUIP_TORPEDO.get() as LegacyEquipItem

        val cannonBase = cannon.createVariantStack(-1)
        val cannonVariant = cannon.createVariantStack(2)
        val cannonClamped = cannon.createVariantStack(99)

        assertEquals(16, cannon.variantCount)
        assertEquals(0, cannon.getVariant(cannonBase))
        assertEquals(2, cannon.getVariant(cannonVariant))
        assertEquals(15, cannon.getVariant(cannonClamped))
        assertEquals(0, cannon.getEquipTypeId(cannonBase))
        assertEquals(1, cannon.getEquipTypeId(cannonVariant))
        assertEquals(0, cannon.getEquipId(cannonBase))
        assertEquals(201, cannon.getEquipId(cannonVariant))
        assertEquals(1503, cannon.getEquipId(cannonClamped))
        assertEquals(0, cannon.getModelVariant(cannonBase))
        assertEquals(1, cannon.getModelVariant(cannonVariant))
        assertEquals(2, cannon.getModelVariant(cannonClamped))

        val airplaneVariant = airplane.createVariantStack(21)
        assertEquals(22, airplane.variantCount)
        assertEquals(21, airplane.getVariant(airplaneVariant))
        assertEquals(9, airplane.getEquipTypeId(airplaneVariant))
        assertEquals(2109, airplane.getEquipId(airplaneVariant))
        assertEquals(1, airplane.getModelVariant(airplaneVariant))

        val torpedoVariant = torpedo.createVariantStack(6)
        assertEquals(7, torpedo.variantCount)
        assertEquals(6, torpedo.getVariant(torpedoVariant))
        assertEquals(5, torpedo.getEquipTypeId(torpedoVariant))
        assertEquals(605, torpedo.getEquipId(torpedoVariant))
        assertEquals(0, torpedo.getModelVariant(torpedoVariant))
    }

    @Test
    fun legacyEquipShouldUseLegacyTranslationKeysAndExposeAllCreativeVariants() {
        val cannon = ModItems.EQUIP_CANNON.get() as LegacyEquipItem
        val torpedo = ModItems.EQUIP_TORPEDO.get() as LegacyEquipItem
        val airplane = ModItems.EQUIP_AIRPLANE.get() as LegacyEquipItem

        val baseKey = (cannon.getName(cannon.createVariantStack(0)).contents as TranslatableContents).key
        val variantKey = (cannon.getName(cannon.createVariantStack(3)).contents as TranslatableContents).key

        assertEquals("item.shincolle.EquipCannon.name", baseKey)
        assertEquals("item.shincolle.EquipCannon3.name", variantKey)

        val directVariants = mutableListOf<ItemStack>()
        cannon.addAllVariantsToCreativeTab(CollectingOutput(directVariants))
        assertEquals(cannon.variantCount, directVariants.size)
        directVariants.forEachIndexed { index, stack -> assertEquals(index, cannon.getVariant(stack)) }

        val sortedVariants = mutableListOf<ItemStack>()
        CreativeTabVariantHelper.addSortedLegacyEquipVariants(CollectingOutput(sortedVariants), ModItems.EQUIP_CANNON)
        assertEquals(cannon.variantCount, sortedVariants.size)
        sortedVariants.forEachIndexed { index, stack ->
            assertEquals(index, cannon.getVariant(stack))
            assertEquals(cannon.getEquipId(cannon.createVariantStack(index)), cannon.getEquipId(stack))
        }

        assertSortedLegacyCreativeVariants(torpedo, ModItems.EQUIP_TORPEDO)
        assertSortedLegacyCreativeVariants(airplane, ModItems.EQUIP_AIRPLANE)
    }

    private fun assertSortedLegacyCreativeVariants(item: LegacyEquipItem, deferredItem: DeferredItem<Item>) {
        val sortedVariants = mutableListOf<ItemStack>()

        CreativeTabVariantHelper.addSortedLegacyEquipVariants(CollectingOutput(sortedVariants), deferredItem)

        assertEquals(item.variantCount, sortedVariants.size)
        sortedVariants.forEachIndexed { index, stack ->
            assertEquals(index, item.getVariant(stack))
            assertEquals(item.getEquipId(item.createVariantStack(index)), item.getEquipId(stack))
        }
    }

    private class CollectingOutput(private val stacks: MutableList<ItemStack>) : CreativeModeTab.Output {
        override fun accept(stack: ItemStack, visibility: CreativeModeTab.TabVisibility) {
            stacks += stack.copy()
        }
    }
}
