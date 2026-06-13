package org.trp.shincolle.block

import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue

class FrameBlockRegressionTest {

    private val frameBlockSource: Path = Path.of("src/main/java/org/trp/shincolle/block/FrameBlock.kt")
    private val modBlocksSource: Path = Path.of("src/main/java/org/trp/shincolle/init/ModBlocks.kt")
    private val blockframeRecipe: Path = Path.of("src/main/resources/data/shincolle/recipe/blockframe.json")

    @Test
    fun frameBlockShouldStayRegisteredAsClimbableUtilityBlock() {
        val blockSource: String = Files.readString(frameBlockSource)
        val modBlocksSrc: String = Files.readString(modBlocksSource)
        val recipeSource: String = Files.readString(blockframeRecipe)

        assertTrue(modBlocksSrc.contains("FRAME_BLOCK = BLOCKS.register(\"blockframe\"")) {
            "ModBlocks should keep registering the legacy frame block"
        }
        assertTrue(blockSource.contains("return true;")) {
            "FrameBlock should stay climbable like the legacy frame"
        }
        assertTrue(blockSource.contains("entity.resetFallDistance();")) {
            "FrameBlock should keep cancelling ladder fall damage"
        }
        assertTrue(recipeSource.contains("\"id\": \"shincolle:blockframe\"")) {
            "Frame block recipe should stay present"
        }
        assertTrue(recipeSource.contains("\"count\": 16")) {
            "Frame block recipe should keep the legacy high output"
        }
    }
}
