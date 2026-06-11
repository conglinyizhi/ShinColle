package org.trp.shincolle.block

import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue

class VolBlockRegressionTest {

    private val VOL_BLOCK_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/block/VolBlock.kt")
    private val MOD_BLOCKS_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/init/ModBlocks.kt")
    private val MOD_ITEMS_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.kt")
    private val VOL_BLOCK_RECIPE: Path =
            Path.of("src/main/resources/data/shincolle/recipes/blockvolblock.json")
    private val VOL_BLOCK_RECIPE_ALT: Path =
            Path.of("src/main/resources/data/shincolle/recipes/blockvolblock_2.json")
    private val VOL_CORE_RECIPE: Path =
            Path.of("src/main/resources/data/shincolle/recipes/blockvolcore.json")
    private val VOL_CORE_RECIPE_ALT: Path =
            Path.of("src/main/resources/data/shincolle/recipes/blockvolcore_3.json")

    @Test
    fun volBlockShouldStayRestoredForLegacyVolcanoCraftChain() {
        val blockSource = Files.readString(VOL_BLOCK_SOURCE)
        val modBlocksSource = Files.readString(MOD_BLOCKS_SOURCE)
        val modItemsSource = Files.readString(MOD_ITEMS_SOURCE)
        val volBlockRecipe = Files.readString(VOL_BLOCK_RECIPE)
        val volBlockRecipeAlt = Files.readString(VOL_BLOCK_RECIPE_ALT)
        val volCoreRecipe = Files.readString(VOL_CORE_RECIPE)
        val volCoreRecipeAlt = Files.readString(VOL_CORE_RECIPE_ALT)

        assertTrue(modBlocksSource.contains("VOL_BLOCK = BLOCKS.register(\"blockvolblock\"")) {
            "ModBlocks should keep registering the legacy volcano block"
        }
        assertTrue(modItemsSource.contains("VOL_BLOCK = ITEMS.register(\"blockvolblock\"")) {
            "ModItems should keep exposing the volcano block as an item"
        }
        assertTrue(blockSource.contains("return true;")) {
            "VolBlock should stay valid as a beacon base like the legacy block"
        }
        assertTrue(volBlockRecipe.contains("\"id\": \"shincolle:blockvolblock\"")) {
            "VolBlock recipe should stay present"
        }
        assertTrue(volBlockRecipe.contains("\"item\": \"shincolle:grudge_block\"")) {
            "VolBlock recipe should keep using the legacy grudge block"
        }
        assertTrue(volBlockRecipeAlt.contains("\"id\": \"shincolle:blockvolblock\"")) {
            "VolBlock should keep the mirrored legacy recipe variant"
        }
        assertTrue(volCoreRecipe.contains("\"item\": \"shincolle:blockvolblock\"")) {
            "VolCore recipe should keep consuming the restored volcano block"
        }
        assertTrue(volCoreRecipeAlt.contains("\"id\": \"shincolle:blockvolcore\"")) {
            "VolCore should keep the mirrored legacy recipe variant"
        }
    }
}
