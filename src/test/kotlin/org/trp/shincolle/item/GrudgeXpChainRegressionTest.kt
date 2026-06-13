package org.trp.shincolle.item

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class GrudgeXpChainRegressionTest {
    @Test
    fun grudgeXpChainShouldKeepLegacyVariantRecipesAndBlockLinks() {
        val blocksSrc = Files.readString(modBlocksSource)
        val modernKitText = Files.readString(modernKitRecipe)
        val trainingBookText = Files.readString(trainingBookRecipe)
        val grudgeXpText = Files.readString(grudgeXpRecipe)
        val grudgeXpBlockText = Files.readString(grudgeXpBlockRecipe)

        assertTrue(blocksSrc.contains("GRUDGE_XP_BLOCK = BLOCKS.register(\"grudge_xp_block\""))
        assertTrue(grudgeXpText.contains("\"minecraft:custom_data\": \"{LegacyVariant:1}\""))
        assertTrue(grudgeXpBlockText.contains("\"minecraft:custom_data\": \"{LegacyVariant:1}\""))
        assertTrue(modernKitText.contains("\"item\": \"shincolle:grudge_xp_block\""))
        assertTrue(trainingBookText.contains("\"item\": \"shincolle:grudge_xp_block\""))
    }

    companion object {
        private val modBlocksSource: Path = Path.of("src/main/java/org/trp/shincolle/init/ModBlocks.java")
        private val modernKitRecipe: Path = Path.of("src/main/resources/data/shincolle/recipe/modernkit.json")
        private val trainingBookRecipe: Path = Path.of("src/main/resources/data/shincolle/recipe/trainingbook.json")
        private val grudgeXpRecipe: Path = Path.of("src/main/resources/data/shincolle/recipe/grudge_xp.json")
        private val grudgeXpBlockRecipe: Path =
            Path.of("src/main/resources/data/shincolle/recipe/grudge_xp_block.json")
    }
}
