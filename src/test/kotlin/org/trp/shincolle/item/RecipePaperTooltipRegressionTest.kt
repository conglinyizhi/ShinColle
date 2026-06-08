package org.trp.shincolle.item

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class RecipePaperTooltipRegressionTest {
    @Test
    fun recipePaperTooltipShouldKeepStoredPreviewResultCompatibility() {
        val itemSource = Files.readString(RECIPE_PAPER_ITEM_SOURCE)

        assertTrue(itemSource.contains("RecipePaperData.loadRecipeGrid"))
        assertTrue(itemSource.contains("ItemStack result = RecipePaperData.loadStoredRecipeResult(stack, context.registries());"))
        assertTrue(itemSource.contains("if (result.isEmpty() && context.level() != null) {"))
        assertTrue(itemSource.contains("appendRecipePreviewTooltip(tooltipComponents, inputList, result);"))
    }

    companion object {
        private val RECIPE_PAPER_ITEM_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/item/RecipePaperItem.java")
    }
}
