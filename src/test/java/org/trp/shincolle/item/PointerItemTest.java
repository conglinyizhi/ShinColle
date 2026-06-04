package org.trp.shincolle.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;
import org.trp.shincolle.init.ModItems;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PointerItemTest {

    @Test
    void pointerShouldClampModesCycleAndExposeCreativeVariants() {
        PointerItem item = (PointerItem) ModItems.POINTER_ITEM.get();

        ItemStack base = item.createVariantStack(-1);
        ItemStack group = item.createVariantStack(PointerItem.MODE_GROUP);
        ItemStack formation = item.createVariantStack(99);

        assertEquals(PointerItem.MODE_SINGLE, item.getMode(base));
        assertEquals(PointerItem.MODE_GROUP, item.getMode(group));
        assertEquals(PointerItem.MODE_FORMATION, item.getMode(formation));
        assertEquals(PointerItem.MODE_SINGLE, item.getModelVariant(base));
        assertEquals(PointerItem.MODE_GROUP, item.getModelVariant(group));
        assertEquals(PointerItem.MODE_FORMATION, item.getModelVariant(formation));

        assertEquals(PointerItem.MODE_GROUP, item.cycleMode(base));
        assertEquals(PointerItem.MODE_GROUP, item.getMode(base));
        assertEquals(PointerItem.MODE_FORMATION, item.cycleMode(base));
        assertEquals(PointerItem.MODE_FORMATION, item.getMode(base));
        assertEquals(PointerItem.MODE_SINGLE, item.cycleMode(base));
        assertEquals(PointerItem.MODE_SINGLE, item.getMode(base));
        assertTrue(!base.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA));

        List<ItemStack> stacks = new ArrayList<>();
        item.addAllVariantsToCreativeTab(new CollectingOutput(stacks));
        assertEquals(3, stacks.size());
        for (int i = 0; i < stacks.size(); i++) {
            assertEquals(i, item.getMode(stacks.get(i)));
        }
    }

    @Test
    void pointerShouldUseStableModeTranslationKeys() {
        assertEquals("gui.shincolle.pointer0", PointerItem.getModeTranslationKey(-1));
        assertEquals("gui.shincolle.pointer0", PointerItem.getModeTranslationKey(PointerItem.MODE_SINGLE));
        assertEquals("gui.shincolle.pointer1", PointerItem.getModeTranslationKey(PointerItem.MODE_GROUP));
        assertEquals("gui.shincolle.pointer2", PointerItem.getModeTranslationKey(PointerItem.MODE_FORMATION));
        assertEquals("gui.shincolle.pointer0", PointerItem.getModeTranslationKey(99));
    }

    private record CollectingOutput(List<ItemStack> stacks) implements CreativeModeTab.Output {
        @Override
        public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
            stacks.add(stack.copy());
        }
    }
}
