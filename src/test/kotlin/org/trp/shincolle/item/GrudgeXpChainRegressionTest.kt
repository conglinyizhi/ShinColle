package org.trp.shincolle.item

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class GrudgeXpChainRegressionTest {
    @Test
    fun grudgeXpChainShouldKeepLegacyVariantRecipesAndBlockLinks() {
        val blocksSource = Files.readString(MOD_BLOCKS_SOURCE)
        val modernKitRecipe = Files.readString(MODERNKIT_RECIPE)
        val trainingBookRecipe = Files.readString(TRAININGBOOK_RECIPE)
        val grudgeXpRecipe = Files.readString(GRUDGEXP_RECIPE)
        val grudgeXpBlockRecipe = Files.readString(GRUDGEXP_BLOCK_RECIPE)

        assertTrue(blocksSource.contains("GRUDGE_XP_BLOCK = BLOCKS.register(\"grudge_xp_block\""))
        assertTrue(grudgeXpRecipe.contains("\"minecraft:custom_data\": \"{LegacyVariant:1}\""))
        assertTrue(grudgeXpBlockRecipe.contains("\"minecraft:custom_data\": \"{LegacyVariant:1}\""))
        assertTrue(modernKitRecipe.contains("\"item\": \"shincolle:grudge_xp_block\""))
        assertTrue(trainingBookRecipe.contains("\"item\": \"shincolle:grudge_xp_block\""))
    }

    companion object {
        private val MOD_BLOCKS_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/init/ModBlocks.java")
        private val MODERNKIT_RECIPE: Path = Path.of("src/main/resources/data/shincolle/recipe/modernkit.json")
        private val TRAININGBOOK_RECIPE: Path = Path.of("src/main/resources/data/shincolle/recipe/trainingbook.json")
        private val GRUDGEXP_RECIPE: Path = Path.of("src/main/resources/data/shincolle/recipe/grudge_xp.json")
        private val GRUDGEXP_BLOCK_RECIPE: Path = Path.of("src/main/resources/data/shincolle/recipe/grudge_xp_block.json")
    }
}
