package org.trp.shincolle.item

import net.minecraft.world.item.ItemStack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems

class KaitaiHammerItemTest {

    @Test
    fun kaitaiHammerShouldReturnDamagedCraftingRemainderUntilDurabilityRunsOut() {
        val item = ModItems.KAITAI_HAMMER.get() as KaitaiHammerItem
        val fresh = ItemStack(item)

        assertTrue(item.hasCraftingRemainingItem(fresh))

        val remainder = item.getCraftingRemainingItem(fresh)
        assertEquals(item, remainder.item)
        assertEquals(1, remainder.count)
        assertEquals(1, remainder.damageValue)

        val nearlyBroken = ItemStack(item)
        nearlyBroken.damageValue = nearlyBroken.maxDamage - 1
        val brokenRemainder = item.getCraftingRemainingItem(nearlyBroken)

        assertTrue(brokenRemainder.isEmpty)
    }
}
