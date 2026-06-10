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

class ShipSpawnEggItemTest {

    @Test
    fun shipSpawnEggShouldKeepShipClassAndResurrectionLevelTooltip() {
        val item = ModItems.DESTROYER_I_SPAWN_EGG.get() as ShipSpawnEggItem
        val stack = ItemStack(item)
        val tag = CompoundTag()
        tag.putBoolean("ShincolleSpawnEgg", true)
        tag.putInt("ShipLevel", 15)
        stack.set(DataComponents.ENTITY_DATA, CustomData.of(tag))

        assertEquals(ShipClass.DESTROYER, item.shipClass)

        val tooltip = mutableListOf<Component?>()
        item.appendHoverText(stack, TooltipContext.EMPTY, tooltip, TooltipFlag.Default.NORMAL)

        assertEquals(1, tooltip.size)
        assertEquals("gui.shincolle.eggText", translationKey(tooltip[0]!!))
        assertTrue(tooltip[0]!!.string.contains("5"))
    }

    private fun translationKey(component: Component): String {
        return (component.contents as TranslatableContents).key
    }
}
