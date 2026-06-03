package org.trp.shincolle.menu;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipePaperMenuRegressionTest {
    private static final Path RECIPE_PAPER_MENU_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/menu/RecipePaperMenu.java");

    @Test
    void recipePaperShouldKeepLegacyPreviewOnlyResultSlotAndDisableShiftMove() throws IOException {
        String source = Files.readString(RECIPE_PAPER_MENU_SOURCE);

        assertTrue(source.contains("if (slotId == 9) {\n            // Legacy recipe paper uses the output slot as a preview only.\n            return;\n        }"),
                "Recipe paper result slot should remain preview-only like the legacy container");
        assertTrue(source.contains("RecipePaperData.saveRecipeGrid(this.hostStack, this.level.registryAccess(), grid, this.craftResult.getItem(0));"),
                "Recipe paper should save the preview result into its persisted recipe payload like the legacy container");
        assertTrue(source.contains("public ItemStack quickMoveStack(Player player, int index) {\n        return ItemStack.EMPTY;\n    }"),
                "Recipe paper should keep legacy shift-click disabled");
        assertFalse(source.contains("placeGhostIngredient"),
                "Recipe paper should not repurpose shift-click into ghost ingredient insertion");
    }

    @Test
    void recipePaperShouldCloseWhenTheOriginalHandStackChanges() throws IOException {
        String source = Files.readString(RECIPE_PAPER_MENU_SOURCE);

        assertTrue(source.contains("return player.getItemInHand(hand) == hostStack;"),
                "Recipe paper menu should stay bound to the original stack reference in the original hand");
        assertFalse(source.contains("ItemStack.isSameItemSameComponents(player.getItemInHand(hand), hostStack)"),
                "Recipe paper menu should not stay open just because another matching stack exists in that hand");
    }
}
