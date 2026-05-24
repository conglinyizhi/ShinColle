package org.trp.shincolle.block;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class VolBlockRegressionTest {

    private static final Path VOL_BLOCK_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/block/VolBlock.java");
    private static final Path MOD_BLOCKS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModBlocks.java");
    private static final Path MOD_ITEMS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.java");
    private static final Path VOL_BLOCK_RECIPE =
            Path.of("src/main/resources/data/shincolle/recipe/blockvolblock.json");
    private static final Path VOL_CORE_RECIPE =
            Path.of("src/main/resources/data/shincolle/recipe/blockvolcore.json");

    @Test
    void volBlockShouldStayRestoredForLegacyVolcanoCraftChain() throws IOException {
        String blockSource = Files.readString(VOL_BLOCK_SOURCE);
        String modBlocksSource = Files.readString(MOD_BLOCKS_SOURCE);
        String modItemsSource = Files.readString(MOD_ITEMS_SOURCE);
        String volBlockRecipe = Files.readString(VOL_BLOCK_RECIPE);
        String volCoreRecipe = Files.readString(VOL_CORE_RECIPE);

        assertTrue(modBlocksSource.contains("VOL_BLOCK = BLOCKS.register(\"blockvolblock\""),
                "ModBlocks should keep registering the legacy volcano block");
        assertTrue(modItemsSource.contains("VOL_BLOCK = ITEMS.register(\"blockvolblock\""),
                "ModItems should keep exposing the volcano block as an item");
        assertTrue(blockSource.contains("return true;"),
                "VolBlock should stay valid as a beacon base like the legacy block");
        assertTrue(volBlockRecipe.contains("\"id\": \"shincolle:blockvolblock\""),
                "VolBlock recipe should stay present");
        assertTrue(volBlockRecipe.contains("\"item\": \"shincolle:grudge_block\""),
                "VolBlock recipe should keep using the legacy grudge block");
        assertTrue(volCoreRecipe.contains("\"item\": \"shincolle:blockvolblock\""),
                "VolCore recipe should keep consuming the restored volcano block");
    }
}
