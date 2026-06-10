package org.trp.shincolle.item

import net.minecraft.network.chat.Component
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class ScaledTextTooltipDataTest {

    @Test
    fun scaledTextTooltipDataShouldPreserveLinesAndScale() {
        val lines = mutableListOf<Component?>(Component.literal("alpha"), Component.literal("beta"))
        val data = ScaledTextTooltipData(lines, 0.75f)

        assertSame(lines, data.lines)
        assertEquals(0.75f, data.scale)
        assertEquals("alpha", data.lines!![0]!!.string)
        assertEquals("beta", data.lines!![1]!!.string)
    }
}
