package org.trp.shincolle.item;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipePaperTooltipRegressionTest {

    private static final Path RECIPE_PAPER_ITEM_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/item/RecipePaperItem.java");
    @Test
    void recipePaperTooltipShouldKeepStoredPreviewResultCompatibility() throws IOException {
        String itemSource = Files.readString(RECIPE_PAPER_ITEM_SOURCE);

        assertTrue(itemSource.contains("RecipePaperData.loadRecipeGrid"),
                "RecipePaperItem should keep using the shared recipe-paper data helper");
        assertTrue(itemSource.contains("ItemStack result = RecipePaperData.loadStoredRecipeResult(stack, context.registries());"),
                "RecipePaperItem should prefer the stored slot-9 result like the legacy tooltip");
        assertTrue(itemSource.contains("if (result.isEmpty() && context.level() != null) {"),
                "RecipePaperItem should only fall back to live recipe recomputation when no stored result exists");
    }
}
