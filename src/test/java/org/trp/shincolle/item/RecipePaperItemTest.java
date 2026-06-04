package org.trp.shincolle.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;
import org.trp.shincolle.init.ModItems;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipePaperItemTest {

    @Test
    void recipePaperTooltipHelperShouldRenderStoredResultAndIngredientLines() {
        List<ItemStack> grid = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            grid.add(ItemStack.EMPTY);
        }
        grid.set(0, new ItemStack(ModItems.GRUDGE.get(), 2));
        grid.set(4, new ItemStack(ModItems.ABYSS_NUGGET.get(), 1));

        ItemStack result = new ItemStack(ModItems.MODERN_KIT.get());
        List<Component> tooltip = new ArrayList<>();

        RecipePaperItem.appendRecipePreviewTooltip(tooltip, grid, result);

        assertEquals(4, tooltip.size());
        assertEquals("gui.shincolle.recipepaper.result", translationKey(tooltip.get(0)));
        assertTrue(tooltip.get(0).getString().contains(ModItems.MODERN_KIT.get().getName(result).getString()));
        assertEquals("gui.shincolle.recipepaper.material", translationKey(tooltip.get(1)));
        assertTrue(tooltip.get(2).getString().contains(ModItems.GRUDGE.get().getName(grid.get(0)).getString()));
        assertTrue(tooltip.get(3).getString().contains(ModItems.ABYSS_NUGGET.get().getName(grid.get(4)).getString()));
    }

    private static String translationKey(Component component) {
        return ((TranslatableContents) component.getContents()).getKey();
    }
}
