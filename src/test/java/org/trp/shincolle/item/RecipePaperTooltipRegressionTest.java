package org.trp.shincolle.item;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipePaperTooltipRegressionTest {

    private static final Path RECIPE_PAPER_ITEM_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/item/RecipePaperItem.java");
    private static final Path RECIPE_PAPER_DATA_JAVA_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/utility/RecipePaperData.java");

    @Test
    void recipePaperTooltipShouldNotDependOnKotlinRuntime() throws IOException {
        String itemSource = Files.readString(RECIPE_PAPER_ITEM_SOURCE);
        String utilitySource = Files.readString(RECIPE_PAPER_DATA_JAVA_SOURCE);

        assertTrue(itemSource.contains("RecipePaperData.loadRecipeGrid"),
                "RecipePaperItem should keep using the shared recipe-paper data helper");
        assertTrue(itemSource.contains("ItemStack result = RecipePaperData.loadStoredRecipeResult(stack, context.registries());"),
                "RecipePaperItem should prefer the stored slot-9 result like the legacy tooltip");
        assertTrue(itemSource.contains("if (result.isEmpty() && context.level() != null) {"),
                "RecipePaperItem should only fall back to live recipe recomputation when no stored result exists");
        assertTrue(utilitySource.contains("public final class RecipePaperData"),
                "RecipePaperData should remain available as a Java utility class");
        assertTrue(utilitySource.contains("private static final int RESULT_SLOT = 9;"),
                "RecipePaperData should preserve the legacy stored preview-result slot id");
        assertTrue(utilitySource.contains("public static ItemStack loadStoredRecipeResult(ItemStack hostStack, HolderLookup.Provider registries) {"),
                "RecipePaperData should expose a helper for reading the legacy stored preview result");
        assertTrue(utilitySource.contains("resultTag.putInt(SLOT_TAG, RESULT_SLOT);"),
                "RecipePaperData should persist the preview result as slot 9 like the legacy recipe paper format");
        assertFalse(Files.exists(Path.of("src/main/kotlin/org/trp/shincolle/utility/RecipePaperData.kt")),
                "RecipePaperData.kt should stay removed to avoid Kotlin runtime crashes in tooltips");
    }
}
