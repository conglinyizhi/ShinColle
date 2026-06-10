package org.trp.shincolle.item

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.trp.shincolle.init.ModItems

class DeskItemRadarTest {

    @Test
    fun deskItemRadarShouldKeepLegacyDescriptionId() {
        val item = ModItems.DESK_ITEM_RADAR.get() as DeskItemRadar

        assertEquals("item.shincolle.deskitemradar.name", item.descriptionId)
    }
}
