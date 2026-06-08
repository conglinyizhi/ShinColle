package org.trp.shincolle.event

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

import org.junit.jupiter.api.Assertions.assertTrue

class TaskHelperCookingFallbackRegressionTest {
    private val TASK_HELPER_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/utility/TaskHelper.kt")

    @Test
    fun cookingFallbackHandlerShouldSplitInputFuelAndOutputSlots() {
        val source = Files.readString(TASK_HELPER_SOURCE)

        assertTrue(source.contains("if (!(targetBE instanceof net.minecraft.world.WorldlyContainer)\n                        || fallbackHandler == null\n                        || fallbackHandler.getSlots() != 3) {")) {
                "Cooking fallback should only apply to furnace-like sided containers with the legacy three-slot layout"
        }
        assertTrue(source.contains("inHandlers = List.of(singleSlotView(fallbackHandler, 0));")) {
                "Cooking fallback should expose only slot 0 as the input slot"
        }
        assertTrue(source.contains("fuelHandlers = List.of(singleSlotView(fallbackHandler, 1));")) {
                "Cooking fallback should expose only slot 1 as the fuel slot"
        }
        assertTrue(source.contains("outHandlers = List.of(singleSlotView(fallbackHandler, 2));")) {
                "Cooking fallback should expose only slot 2 as the output slot"
        }
    }

    @Test
    fun cookingSidedHandlersShouldTryAllAccessibleSlots() {
        val source = Files.readString(TASK_HELPER_SOURCE)

        assertTrue(source.contains("canFit += simulateInsertAcrossHandler(handler, mainStack);")) {
                "Cooking should simulate capacity across every accessible sided input slot"
        }
        assertTrue(source.contains("canFit += simulateInsertAcrossHandler(handler, offhandStack);")) {
                "Cooking should simulate capacity across every accessible sided fuel slot"
        }
        assertTrue(source.contains("remaining = insertAcrossHandler(handler, remaining, false);")) {
                "Cooking should insert across every accessible sided slot instead of only slot 0"
        }
        assertTrue(source.contains("for (int slot = 0; slot < handler.getSlots() && !remaining.isEmpty(); slot++)")) {
                "Cooking sided inserts should iterate the full handler slot range"
        }
    }
}
