package org.trp.shincolle.item;

import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;
import org.trp.shincolle.init.ModItems;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GrudgeItemTest {

    @Test
    void grudgeShouldClampVariantsAndExposeBothCreativeStacks() {
        GrudgeItem item = (GrudgeItem) ModItems.GRUDGE.get();

        ItemStack base = item.createVariantStack(-1);
        ItemStack variant = item.createVariantStack(1);
        ItemStack clamped = item.createVariantStack(99);

        assertEquals(0, item.getVariant(base));
        assertEquals(1, item.getVariant(variant));
        assertEquals(1, item.getVariant(clamped));
        assertEquals(0, item.getModelVariant(base));
        assertEquals(1, item.getModelVariant(variant));

        List<ItemStack> stacks = new ArrayList<>();
        item.addAllVariantsToCreativeTab(new CollectingOutput(stacks));
        assertEquals(2, stacks.size());
        assertEquals(0, item.getVariant(stacks.get(0)));
        assertEquals(1, item.getVariant(stacks.get(1)));
    }

    @Test
    void grudgeShouldUseLegacyTranslationKeysPerVariant() {
        GrudgeItem item = (GrudgeItem) ModItems.GRUDGE.get();

        String baseKey = ((TranslatableContents) item.getName(item.createVariantStack(0)).getContents()).getKey();
        String variantKey = ((TranslatableContents) item.getName(item.createVariantStack(1)).getContents()).getKey();

        assertEquals("item.shincolle.grudge", baseKey);
        assertEquals("item.shincolle.Grudge1.name", variantKey);
    }

    private record CollectingOutput(List<ItemStack> stacks) implements CreativeModeTab.Output {
        @Override
        public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
            stacks.add(stack.copy());
        }
    }
}
