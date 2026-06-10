package org.trp.shincolle.item

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems

class RandomShipSpawnEggItemTest {

    @Test
    fun randomShipSpawnEggsShouldKeepSmallAndLargeShipyardFlags() {
        val smallEgg = ModItems.SHIPSPAWNEGGS.get() as RandomShipSpawnEggItem
        val largeEgg = ModItems.SHIPSPAWNEGGL.get() as RandomShipSpawnEggItem

        assertEquals(ShipClass.DESTROYER, smallEgg.shipClass)
        assertFalse(largeShipyardEgg(smallEgg))

        assertEquals(ShipClass.PRINCESS, largeEgg.shipClass)
        assertTrue(largeShipyardEgg(largeEgg))
    }

    private fun largeShipyardEgg(item: RandomShipSpawnEggItem): Boolean {
        val field = RandomShipSpawnEggItem::class.java.getDeclaredField("largeShipyardEgg")
        field.isAccessible = true
        return field.getBoolean(item)
    }
}
