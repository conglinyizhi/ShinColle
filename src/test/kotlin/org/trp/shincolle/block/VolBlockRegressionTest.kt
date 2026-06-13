package org.trp.shincolle.block

import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue

class VolBlockRegressionTest {

    private val volBlockSource: Path = Path.of("src/main/java/org/trp/shincolle/block/VolBlock.kt")
    private val modBlocksSource: Path = Path.of("src/main/java/org/trp/shincolle/init/ModBlocks.kt")
    private val modItemsSource: Path = Path.of("src/main/java/org/trp/shincolle/init/ModItems.kt")
    private val volBlockRecipe: Path =
            Path.of("src/main/resources/data/shincolle/recipe/blockvolblock.json")
    private val volBlockRecipeAlt: Path =
            Path.of("src/main/resources/data/shincolle/recipe/blockvolblock_2.json")
    private val volCoreRecipe: Path =
            Path.of("src/main/resources/data/shincolle/recipe/blockvolcore.json")
    private val volCoreRecipeAlt: Path =
            Path.of("src/main/resources/data/shincolle/recipe/blockvolcore_3.json")

    @Test
    fun volBlockShouldStayRestoredForLegacyVolcanoCraftChain() {
        val blockSource = Files.readString(volBlockSource)
        val modBlocksSrc = Files.readString(modBlocksSource)
        val modItemsSrc = Files.readString(modItemsSource)
        val volBlockText = Files.readString(volBlockRecipe)
        val volBlockTextAlt = Files.readString(volBlockRecipeAlt)
        val volCoreText = Files.readString(volCoreRecipe)
        val volCoreTextAlt = Files.readString(volCoreRecipeAlt)

        assertTrue(modBlocksSrc.contains("VOL_BLOCK = BLOCKS.register(\"blockvolblock\"")) {
            "ModBlocks should keep registering the legacy volcano block"
        }
        assertTrue(modItemsSrc.contains("VOL_BLOCK = ITEMS.register(\"blockvolblock\"")) {
            "ModItems should keep exposing the volcano block as an item"
        }
        assertTrue(blockSource.contains("return true;")) {
            "VolBlock should stay valid as a beacon base like the legacy block"
        }
        assertTrue(volBlockText.contains("\"id\": \"shincolle:blockvolblock\"")) {
            "VolBlock recipe should stay present"
        }
        assertTrue(volBlockText.contains("\"item\": \"shincolle:grudge_block\"")) {
            "VolBlock recipe should keep using the legacy grudge block"
        }
        assertTrue(volBlockTextAlt.contains("\"id\": \"shincolle:blockvolblock\"")) {
            "VolBlock should keep the mirrored legacy recipe variant"
        }
        assertTrue(volCoreText.contains("\"item\": \"shincolle:blockvolblock\"")) {
            "VolCore recipe should keep consuming the restored volcano block"
        }
        assertTrue(volCoreTextAlt.contains("\"id\": \"shincolle:blockvolcore\"")) {
            "VolCore should keep the mirrored legacy recipe variant"
        }
    }
}
