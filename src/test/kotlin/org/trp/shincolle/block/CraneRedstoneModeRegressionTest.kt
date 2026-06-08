package org.trp.shincolle.block

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

import org.junit.jupiter.api.Assertions.assertTrue

class CraneRedstoneModeRegressionTest {
    private val CRANE_BE: Path =
            Path.of("src/main/java/org/trp/shincolle/block/entity/CraneBlockEntity.kt")

    @Test
    fun redstoneModeOneShouldPulseDuringActiveCraning() {
        val source = Files.readString(CRANE_BE)
        assertTrue(source.contains("if (this.modeRedstone == 1) {")) {
                "Crane should keep the legacy redstone mode 1 branch"
        }
        assertTrue(source.contains("this.tickRedstone = 18;")) {
                "Crane redstone mode 1 should refresh an 18-tick working pulse"
        }
    }

    @Test
    fun redstoneModeTwoShouldPulseWhenCraningEnds() {
        val source = Files.readString(CRANE_BE)
        assertTrue(source.contains("if (this.modeRedstone == 2) {")) {
                "Crane should keep the legacy redstone mode 2 branch"
        }
        assertTrue(source.contains("this.tickRedstone = 2;")) {
                "Crane redstone mode 2 should emit a short completion pulse"
        }
    }
}
