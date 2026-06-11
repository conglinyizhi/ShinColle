package org.trp.shincolle.block

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GrudgeHeavyDecoBlockTest {

    private val BLOCK_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/block/GrudgeHeavyDecoBlock.kt")
    private val MOD_BLOCKS_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/init/ModBlocks.kt")
    private val MOD_ITEMS_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/init/ModItems.kt")
    private val CREATIVE_TAB_CONTENTS_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/init/ShinColleCreativeTabContents.kt")
    private val LOOT_TABLE: Path =
        Path.of("src/main/resources/data/shincolle/loot_table/blocks/grudge_heavy_deco_block.json")
    private val BLOCKSTATE: Path =
        Path.of("src/main/resources/assets/shincolle/blockstates/grudge_heavy_deco_block.json")
    private val ITEM_MODEL: Path =
        Path.of("src/main/resources/assets/shincolle/models/item/grudge_heavy_deco_block.json")

    @Test
    fun grudgeHeavyDecoBlockShouldStayRegisteredAsLegacyDecorationBlock() {
        val blockSource = Files.readString(BLOCK_SOURCE)
        val modBlocksSource = Files.readString(MOD_BLOCKS_SOURCE)
        val modItemsSource = Files.readString(MOD_ITEMS_SOURCE)
        val creativeTabContentsSource = Files.readString(CREATIVE_TAB_CONTENTS_SOURCE)
        val lootTable = Files.readString(LOOT_TABLE)
        val blockstate = Files.readString(BLOCKSTATE)
        val itemModel = Files.readString(ITEM_MODEL)

        assertTrue(modBlocksSource.contains("val GRUDGE_HEAVY_DECO_BLOCK: DeferredBlock<Block?> = BLOCKS.register<Block?>(")) {
            "ModBlocks should register the restored decoration block"
        }
        assertTrue(modBlocksSource.contains("\"grudge_heavy_deco_block\"")) {
            "ModBlocks should keep the legacy decoration block id"
        }
        assertTrue(modBlocksSource.contains(".strength(3.0f)")) {
            "Decoration block should keep legacy hardness 3.0"
        }
        assertTrue(modBlocksSource.contains(".lightLevel(ToIntFunction { state: BlockState? -> 15 })")) {
            "Decoration block should emit light level 15"
        }
        assertTrue(modItemsSource.contains("val GRUDGE_HEAVY_DECO_BLOCK: DeferredItem<Item?> = ITEMS.register<Item?>(")) {
            "ModItems should expose the restored decoration block as an item"
        }
        assertTrue(modItemsSource.contains("\"grudge_heavy_deco_block\"")) {
            "ModItems should keep the legacy decoration item id"
        }
        assertTrue(creativeTabContentsSource.contains("output.accept(ModItems.GRUDGE_HEAVY_DECO_BLOCK.get())")) {
            "Creative tab should include the restored decoration block"
        }
        assertTrue(blockSource.contains("return true")) {
            "Decoration block should remain valid as a beacon base"
        }
        assertTrue(blockstate.contains("\"model\": \"shincolle:block/grudge_heavy_block\"")) {
            "Decoration block should reuse the heavy grudge block model"
        }
        assertTrue(itemModel.contains("\"parent\": \"shincolle:block/grudge_heavy_block\"")) {
            "Decoration block item should reuse the heavy grudge block model"
        }
        assertTrue(lootTable.contains("\"name\": \"shincolle:grudge_heavy_deco_block\"")) {
            "Decoration block should drop itself"
        }
    }
}
