package org.trp.shincolle.item;

import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;
import org.trp.shincolle.init.ModItems;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShipTankItemTest {

    @Test
    void shipTankShouldClampVariantsAndExposeAllCreativeStacks() {
        ShipTankItem item = (ShipTankItem) ModItems.SHIP_TANK.get();

        ItemStack base = item.createVariantStack(-1);
        ItemStack variant = item.createVariantStack(2);
        ItemStack clamped = item.createVariantStack(99);

        assertEquals(4, item.getVariantCount());
        assertEquals(0, item.getVariant(base));
        assertEquals(2, item.getVariant(variant));
        assertEquals(3, item.getVariant(clamped));
        assertEquals(0, item.getModelVariant(base));
        assertEquals(2, item.getModelVariant(variant));
        assertEquals(3, item.getModelVariant(clamped));

        List<ItemStack> stacks = new ArrayList<>();
        item.addAllVariantsToCreativeTab(new CollectingOutput(stacks));
        assertEquals(item.getVariantCount(), stacks.size());
        for (int i = 0; i < stacks.size(); i++) {
            assertEquals(i, item.getVariant(stacks.get(i)));
        }
    }

    @Test
    void shipTankShouldUseLegacyTranslationKeysAndCapacityTables() {
        ShipTankItem item = (ShipTankItem) ModItems.SHIP_TANK.get();

        String baseKey = ((TranslatableContents) item.getName(item.createVariantStack(0)).getContents()).getKey();
        String variantKey = ((TranslatableContents) item.getName(item.createVariantStack(3)).getContents()).getKey();

        assertEquals("item.shincolle.ShipTank.name", baseKey);
        assertEquals("item.shincolle.ShipTank3.name", variantKey);
        assertEquals(32000, ShipTankItem.getCapacity(-1));
        assertEquals(512000, ShipTankItem.getCapacity(2));
        assertEquals(2048000, ShipTankItem.getCapacity(99));
        assertEquals(512000, ShipTankItem.getCapacity(item.createVariantStack(2)));
        assertEquals(32000, ShipTankItem.getCapacity(new ItemStack(Items.STICK)));
    }

    private record CollectingOutput(List<ItemStack> stacks) implements CreativeModeTab.Output {
        @Override
        public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
            stacks.add(stack.copy());
        }
    }
}
