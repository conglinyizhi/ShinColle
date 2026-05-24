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
        assertTrue(utilitySource.contains("public final class RecipePaperData"),
                "RecipePaperData should remain available as a Java utility class");
        assertFalse(Files.exists(Path.of("src/main/kotlin/org/trp/shincolle/utility/RecipePaperData.kt")),
                "RecipePaperData.kt should stay removed to avoid Kotlin runtime crashes in tooltips");
    }
}
