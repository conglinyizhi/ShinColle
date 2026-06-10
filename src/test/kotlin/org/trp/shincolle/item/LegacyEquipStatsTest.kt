package org.trp.shincolle.item

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class LegacyEquipStatsTest {

    @Test
    fun legacyEquipStatsShouldKeepKnownMainAndMiscTables() {
        val baseCannon = LegacyEquipStats.getMainAttrs(0)
        assertNotNull(baseCannon)
        assertEquals(LegacyEquipStats.ATTR_COUNT, baseCannon!!.size)
        assertEquals(2.0f, baseCannon[1])
        assertEquals(0.1f, baseCannon[6])
        assertEquals(-0.01f, baseCannon[7])

        val hpCannon = LegacyEquipStats.getMainAttrs(1200)
        assertNotNull(hpCannon)
        assertEquals(30.0f, hpCannon!![0])
        assertEquals(0.04f, hpCannon[5])

        val ammoMisc = LegacyEquipStats.getMiscAttrs(729)
        assertNotNull(ammoMisc)
        assertEquals(intArrayOf(2, 29, 3500, 1000, 2, 1).toList(), ammoMisc!!.toList())

        assertNull(LegacyEquipStats.getMainAttrs(-1))
        assertNull(LegacyEquipStats.getMiscAttrs(-1))
    }
}
