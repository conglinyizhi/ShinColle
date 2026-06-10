package org.trp.shincolle.item

import net.minecraft.util.RandomSource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TrainingBookItemTest {

    @Test
    fun trainingBookShouldClampConfiguredLevelGainRange() {
        assertEquals(2..5, TrainingBookItem.levelGainRange(2, 5))
        assertEquals(4..4, TrainingBookItem.levelGainRange(4, 1))
    }

    @Test
    fun trainingBookShouldRollWithinConfiguredLevelGainRange() {
        val fixed = TrainingBookItem.rollLevelGain(RandomSource.create(1234L), 3, 3)
        val ranged = TrainingBookItem.rollLevelGain(RandomSource.create(5678L), 2, 5)
        val reversed = TrainingBookItem.rollLevelGain(RandomSource.create(9012L), 4, 1)

        assertEquals(3, fixed)
        assertTrue(ranged in 2..5)
        assertEquals(4, reversed)
    }
}
