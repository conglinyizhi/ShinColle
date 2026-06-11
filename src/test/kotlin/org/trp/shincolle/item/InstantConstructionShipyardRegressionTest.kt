package org.trp.shincolle.item

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InstantConstructionShipyardRegressionTest {
    private val SMALL_MENU_SOURCE =
        Path.of("src/main/java/org/trp/shincolle/menu/SmallShipyardMenu.kt")
    private val SMALL_BLOCK_ENTITY_SOURCE =
        Path.of("src/main/java/org/trp/shincolle/block/entity/SmallShipyardBlockEntity.kt")
    private val LARGE_BLOCK_ENTITY_SOURCE =
        Path.of("src/main/java/org/trp/shincolle/block/entity/LargeShipyardBlockEntity.kt")

    @Test
    fun instantConstructionMaterialShouldKeepDedicatedSmallShipyardFuelRouting() {
        val menuSource = Files.readString(SMALL_MENU_SOURCE)
        val blockEntitySource = Files.readString(SMALL_BLOCK_ENTITY_SOURCE)

        assertTrue(menuSource.contains("else if (stack.`is`(ModItems.INSTANT_CON_MAT.get())) {\n                    if (!this.moveItemStackTo(stack, SLOT_FUEL, SLOT_FUEL + 1, false)) {")) {
            "Small shipyard menu should keep routing Instant Construction Material into the dedicated fuel slot"
        }
        assertTrue(blockEntitySource.contains("if (fuel.isEmpty() || fuel.`is`(ModItems.INSTANT_CON_MAT.get())) {\n            return false\n        }")) {
            "Small shipyard fuel consumption should keep excluding Instant Construction Material from normal fuel burn"
        }
        assertTrue(blockEntitySource.contains("if (stack.isEmpty() || !stack.`is`(ModItems.INSTANT_CON_MAT.get())) {\n            return false\n        }")) {
            "Small shipyard instant-build path should keep requiring Instant Construction Material in the fuel slot"
        }
    }

    @Test
    fun instantConstructionMaterialShouldKeepLargeShipyardWideSlotScan() {
        val largeBlockEntitySource = Files.readString(LARGE_BLOCK_ENTITY_SOURCE)

        assertTrue(largeBlockEntitySource.contains("for (i in SLOT_IO_START..SLOT_IO_END) {")) {
            "Large shipyard should keep scanning every IO slot for Instant Construction Material"
        }
        assertTrue(largeBlockEntitySource.contains("if (stack.isEmpty() || !stack.`is`(ModItems.INSTANT_CON_MAT.get())) {\n                continue\n            }")) {
            "Large shipyard should keep skipping non-instant-build stacks when scanning IO slots"
        }
        assertTrue(largeBlockEntitySource.contains("this.powerConsumed += Config.largeShipyardBuildSpeed * Config.largeShipyardInstantTicks")) {
            "Large shipyard should keep converting Instant Construction Material into accelerated build ticks"
        }
    }
}
