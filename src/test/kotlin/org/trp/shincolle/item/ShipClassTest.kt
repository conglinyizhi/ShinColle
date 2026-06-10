package org.trp.shincolle.item

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ShipClassTest {

    @Test
    fun shipClassShouldKeepLegacyCodesAndSpawnEggTextures() {
        assertEquals("DD", ShipClass.DESTROYER.code)
        assertEquals("shipspawnegg0", ShipClass.DESTROYER.textureName)

        assertEquals("SS", ShipClass.SUBMARINE.code)
        assertEquals("shipspawnegg5", ShipClass.SUBMARINE.textureName)

        assertEquals("PR", ShipClass.PRINCESS.code)
        assertEquals("shipspawnegg7", ShipClass.PRINCESS.textureName)

        assertEquals("CV", ShipClass.AIRCRAFT_CARRIER.code)
        assertEquals("shipspawnegg8", ShipClass.AIRCRAFT_CARRIER.textureName)
    }
}
