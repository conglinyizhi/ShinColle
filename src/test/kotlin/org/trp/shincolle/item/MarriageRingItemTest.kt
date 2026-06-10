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
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems

class MarriageRingItemTest {

    @Test
    fun marriageRingShouldReflectLegacyActiveFlagInFoilAndTooltip() {
        val item = ModItems.MARRIAGE_RING.get() as MarriageRingItem
        val inactive = ItemStack(item)
        val active = ItemStack(item)
        val activeTag = CompoundTag()
        activeTag.putBoolean("LegacyActive", true)
        active.set(DataComponents.CUSTOM_DATA, CustomData.of(activeTag))

        assertFalse(MarriageRingItem.isActive(inactive))
        assertTrue(MarriageRingItem.isActive(active))
        assertFalse(item.isFoil(inactive))
        assertTrue(item.isFoil(active))

        val inactiveTooltip = mutableListOf<Component?>()
        item.appendHoverText(inactive, TooltipContext.EMPTY, inactiveTooltip, TooltipFlag.Default.NORMAL)
        assertEquals(1, inactiveTooltip.size)
        assertEquals("gui.shincolle.ring.off", translationKey(inactiveTooltip[0]!!))

        val activeTooltip = mutableListOf<Component?>()
        item.appendHoverText(active, TooltipContext.EMPTY, activeTooltip, TooltipFlag.Default.NORMAL)
        assertEquals(1, activeTooltip.size)
        assertEquals("gui.shincolle.ring.on", translationKey(activeTooltip[0]!!))
    }

    private fun translationKey(component: Component): String {
        return (component.contents as TranslatableContents).key
    }
}
