package org.trp.shincolle.item;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GrudgeXpChainRegressionTest {

    private static final Path MOD_BLOCKS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModBlocks.java");
    private static final Path MODERNKIT_RECIPE =
            Path.of("src/main/resources/data/shincolle/recipe/modernkit.json");
    private static final Path TRAININGBOOK_RECIPE =
            Path.of("src/main/resources/data/shincolle/recipe/trainingbook.json");
    private static final Path GRUDGEXP_RECIPE =
            Path.of("src/main/resources/data/shincolle/recipe/grudge_xp.json");
    private static final Path GRUDGEXP_BLOCK_RECIPE =
            Path.of("src/main/resources/data/shincolle/recipe/grudge_xp_block.json");

    @Test
    void grudgeXpLegacyCraftChainShouldStayRestored() throws IOException {
        String blocksSource = Files.readString(MOD_BLOCKS_SOURCE);
        String modernKitRecipe = Files.readString(MODERNKIT_RECIPE);
        String trainingBookRecipe = Files.readString(TRAININGBOOK_RECIPE);
        String grudgeXpRecipe = Files.readString(GRUDGEXP_RECIPE);
        String grudgeXpBlockRecipe = Files.readString(GRUDGEXP_BLOCK_RECIPE);

        assertTrue(blocksSource.contains("GRUDGE_XP_BLOCK = BLOCKS.register(\"grudge_xp_block\""),
                "ModBlocks should keep registering the legacy GrudgeXP block");
        assertTrue(grudgeXpRecipe.contains("\"minecraft:custom_data\": \"{LegacyVariant:1}\""),
                "GrudgeXP recipe should output the legacy variant-1 grudge item");
        assertTrue(grudgeXpBlockRecipe.contains("\"minecraft:custom_data\": \"{LegacyVariant:1}\""),
                "GrudgeXP block recipe should keep consuming the variant-1 grudge item");
        assertTrue(modernKitRecipe.contains("\"item\": \"shincolle:grudge_xp_block\""),
                "ModernKit recipe should keep using GrudgeXP blocks");
        assertTrue(trainingBookRecipe.contains("\"item\": \"shincolle:grudge_xp_block\""),
                "TrainingBook recipe should keep using GrudgeXP blocks");
    }
}
