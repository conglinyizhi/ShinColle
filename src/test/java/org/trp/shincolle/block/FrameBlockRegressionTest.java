package org.trp.shincolle.block;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FrameBlockRegressionTest {

    private static final Path FRAME_BLOCK_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/block/FrameBlock.kt");
    private static final Path MOD_BLOCKS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModBlocks.kt");
    private static final Path BLOCKFRAME_RECIPE =
            Path.of("src/main/resources/data/shincolle/recipe/blockframe.json");

    @Test
    void frameBlockShouldStayRegisteredAsClimbableUtilityBlock() throws IOException {
        String blockSource = Files.readString(FRAME_BLOCK_SOURCE);
        String modBlocksSource = Files.readString(MOD_BLOCKS_SOURCE);
        String recipeSource = Files.readString(BLOCKFRAME_RECIPE);

        assertTrue(modBlocksSource.contains("FRAME_BLOCK = BLOCKS.register(\"blockframe\""),
                "ModBlocks should keep registering the legacy frame block");
        assertTrue(blockSource.contains("return true;"),
                "FrameBlock should stay climbable like the legacy frame");
        assertTrue(blockSource.contains("entity.resetFallDistance();"),
                "FrameBlock should keep cancelling ladder fall damage");
        assertTrue(recipeSource.contains("\"id\": \"shincolle:blockframe\""),
                "Frame block recipe should stay present");
        assertTrue(recipeSource.contains("\"count\": 16"),
                "Frame block recipe should keep the legacy high output");
    }
}
