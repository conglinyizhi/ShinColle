package org.trp.shincolle.menu

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class RecipePaperMenuRegressionTest {
    private val RECIPE_PAPER_MENU_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/menu/RecipePaperMenu.kt")

    @Test
    fun recipePaperShouldKeepLegacyPreviewOnlyResultSlotAndDisableShiftMove() {
        val source = Files.readString(RECIPE_PAPER_MENU_SOURCE)

        assertTrue(source.contains("if (isPreviewResultSlot(slotId)) {")) {
            "Recipe paper result slot guard should stay routed through the shared preview-slot helper"
        }
        assertTrue(source.contains("RecipePaperData.saveRecipeGrid(this.hostStack, this.level.registryAccess(), grid, this.craftResult.getItem(0));")) {
            "Recipe paper should save the preview result into its persisted recipe payload like the legacy container"
        }
        assertTrue(source.contains("public ItemStack quickMoveStack(Player player, int index) {\n        return ItemStack.EMPTY;\n    }")) {
            "Recipe paper should keep legacy shift-click disabled"
        }
        assertFalse(source.contains("placeGhostIngredient")) {
            "Recipe paper should not repurpose shift-click into ghost ingredient insertion"
        }
    }

    @Test
    fun recipePaperShouldCloseWhenTheOriginalHandStackChanges() {
        val source = Files.readString(RECIPE_PAPER_MENU_SOURCE)

        assertTrue(source.contains("return isStillBoundToHostStack(player.getItemInHand(hand), hostStack);")) {
            "Recipe paper menu should keep delegating host-stack validation through the shared helper"
        }
        assertFalse(source.contains("ItemStack.isSameItemSameComponents(player.getItemInHand(hand), hostStack)")) {
            "Recipe paper menu should not stay open just because another matching stack exists in that hand"
        }
    }
}
