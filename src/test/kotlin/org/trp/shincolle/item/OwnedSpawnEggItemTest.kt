package org.trp.shincolle.item

import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems

class OwnedSpawnEggItemTest {

    @Test
    fun ownedSpawnEggShouldOnlyShowLevelTooltipForResurrectionEggsAndHonorNoExpFlag() {
        val item = ModItems.DESTROYER_I_SPAWN_EGG.get() as OwnedSpawnEggItem

        val plainStack = ItemStack(item)
        val plainTooltip = mutableListOf<Component?>()
        item.appendHoverText(plainStack, TooltipContext.EMPTY, plainTooltip, TooltipFlag.Default.NORMAL)
        assertTrue(plainTooltip.isEmpty())

        val taggedStack = ItemStack(item)
        val tag = CompoundTag()
        tag.putBoolean("ShincolleSpawnEgg", true)
        tag.putBoolean("ShincolleSpawnEggNoExpCost", true)
        tag.putInt("ShipLevel", 99)
        taggedStack.set(DataComponents.ENTITY_DATA, CustomData.of(tag))

        val taggedTooltip = mutableListOf<Component?>()
        item.appendHoverText(taggedStack, TooltipContext.EMPTY, taggedTooltip, TooltipFlag.Default.NORMAL)

        assertEquals(1, taggedTooltip.size)
        assertEquals("gui.shincolle.eggText", translationKey(taggedTooltip[0]!!))
        assertTrue(taggedTooltip[0]!!.string.contains("0"))
    }

    private fun translationKey(component: Component): String {
        return (component.contents as TranslatableContents).key
    }
}
