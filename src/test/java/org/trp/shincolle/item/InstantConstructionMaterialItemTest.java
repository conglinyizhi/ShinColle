package org.trp.shincolle.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;
import org.trp.shincolle.init.ModItems;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InstantConstructionMaterialItemTest {

    @Test
    void instantConstructionMaterialShouldExposeLocalizedTooltipLines() {
        InstantConstructionMaterialItem item = (InstantConstructionMaterialItem) ModItems.INSTANT_CON_MAT.get();
        ItemStack stack = new ItemStack(item);
        List<Component> tooltip = new ArrayList<>();

        item.appendHoverText(stack, null, tooltip, null);

        assertEquals(2, tooltip.size());
        assertEquals("gui.shincolle.instantconmat", translationKey(tooltip.get(0)));
        assertEquals("gui.shincolle.instantconmat.slot", translationKey(tooltip.get(1)));
    }

    private static String translationKey(Component component) {
        return ((TranslatableContents) component.getContents()).getKey();
    }
}
