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

class DebugInspectorItemTest {

    @Test
    fun debugInspectorShouldAlwaysFoilAndExposeStoredBucketDiagnostics() {
        val item = ModItems.DEBUG_INSPECTOR.get() as DebugInspectorItem
        val stack = ItemStack(item)
        val tag = CompoundTag()
        tag.putInt("BucketRepairCount", 3)
        tag.putString("BucketRepairShip", "Test Ship")
        tag.putLong("BucketRepairGameTime", 1200L)
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))

        assertTrue(item.isFoil(stack))
        assertEquals("∞", DebugInspectorItem.creativeInfiniteLabel().string)

        val tooltip = mutableListOf<Component?>()
        item.appendHoverText(stack, TooltipContext.EMPTY, tooltip, TooltipFlag.Default.NORMAL)

        assertEquals(5, tooltip.size)
        assertEquals("item.shincolle.debug_inspector.desc", translationKey(tooltip[0]!!))
        assertEquals("item.shincolle.debug_inspector.desc2", translationKey(tooltip[1]!!))
        assertEquals("item.shincolle.debug_inspector.bucket_count", translationKey(tooltip[2]!!))
        assertEquals("item.shincolle.debug_inspector.bucket_ship", translationKey(tooltip[3]!!))
        assertEquals("item.shincolle.debug_inspector.bucket_time", translationKey(tooltip[4]!!))
    }

    private fun translationKey(component: Component): String {
        return (component.contents as TranslatableContents).key
    }
}
