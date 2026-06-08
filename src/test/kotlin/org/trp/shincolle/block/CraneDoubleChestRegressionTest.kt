package org.trp.shincolle.block

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

import org.junit.jupiter.api.Assertions.assertTrue

class CraneDoubleChestRegressionTest {
    private val CRANE_BE: Path =
            Path.of("src/main/java/org/trp/shincolle/block/entity/CraneBlockEntity.kt")

    @Test
    fun craneShouldCombineAdjacentChestHandlersForItemTransfer() {
        val source = Files.readString(CRANE_BE)
        assertTrue(source.contains("private IItemHandler combinedChestHandler = null;")) {
                "Crane should keep a combined chest handler for double-chest item logic"
        }
        assertTrue(source.contains("this.combinedChestHandler = createCombinedChestHandler(handler);")) {
                "Crane should build a combined chest handler when validating the paired chest"
        }
        assertTrue(source.contains("new CombinedItemHandler(primary, adjacent);")) {
                "Crane should merge primary and adjacent chest handlers for item transfer"
        }
    }

    @Test
    fun craneItemTransferAndThresholdChecksShouldUseCombinedChestHandler() {
        val source = Files.readString(CRANE_BE)
        assertTrue(source.contains("IItemHandler invFrom = isLoading ? this.combinedChestHandler : this.craningShip.getInventory();")) {
                "Crane loading should read from the combined chest inventory"
        }
        assertTrue(source.contains("IItemHandler invTo = isLoading ? this.craningShip.getInventory() : this.combinedChestHandler;")) {
                "Crane unloading should write into the combined chest inventory"
        }
        assertTrue(source.contains("matchesRequestedAmounts(this.combinedChestHandler, 9, true)")) {
                "Crane excess-mode checks should count both halves of a double chest"
        }
        assertTrue(source.contains("matchesRequestedAmounts(this.combinedChestHandler, 0, false)")) {
                "Crane remain-mode checks should count both halves of a double chest"
        }
    }
}
