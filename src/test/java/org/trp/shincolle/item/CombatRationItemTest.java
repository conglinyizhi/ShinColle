package org.trp.shincolle.item;

import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;
import org.trp.shincolle.init.ModItems;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatRationItemTest {

    @Test
    void combatRationShouldClampVariantsAndExposeAllCreativeStacks() {
        CombatRationItem item = (CombatRationItem) ModItems.COMBAT_RATION.get();

        ItemStack base = item.createVariantStack(-1);
        ItemStack variant = item.createVariantStack(4);
        ItemStack clamped = item.createVariantStack(99);

        assertEquals(6, item.getVariantCount());
        assertEquals(0, item.getVariant(base));
        assertEquals(4, item.getVariant(variant));
        assertEquals(5, item.getVariant(clamped));
        assertEquals(0, item.getModelVariant(base));
        assertEquals(4, item.getModelVariant(variant));
        assertEquals(5, item.getModelVariant(clamped));

        List<ItemStack> stacks = new ArrayList<>();
        item.addAllVariantsToCreativeTab(new CollectingOutput(stacks));
        assertEquals(item.getVariantCount(), stacks.size());
        for (int i = 0; i < stacks.size(); i++) {
            assertEquals(i, item.getVariant(stacks.get(i)));
        }
    }

    @Test
    void combatRationShouldUseLegacyTranslationKeysAndStatTables() {
        CombatRationItem item = (CombatRationItem) ModItems.COMBAT_RATION.get();

        String baseKey = ((TranslatableContents) item.getName(item.createVariantStack(0)).getContents()).getKey();
        String variantKey = ((TranslatableContents) item.getName(item.createVariantStack(5)).getContents()).getKey();

        assertEquals("item.shincolle.CombatRation.name", baseKey);
        assertEquals("item.shincolle.CombatRation5.name", variantKey);
        assertEquals(900, CombatRationItem.getFoodValue(-1));
        assertEquals(100, CombatRationItem.getFoodValue(4));
        assertEquals(900, CombatRationItem.getFoodValue(99));
        assertEquals(1400, CombatRationItem.getMoraleValue(-1));
        assertEquals(3000, CombatRationItem.getMoraleValue(4));
        assertEquals(4000, CombatRationItem.getMoraleValue(99));
        assertEquals(9, CombatRationItem.getFuelGainMin(0));
        assertEquals(18, CombatRationItem.getFuelGainMax(0));
        assertEquals(1, CombatRationItem.getFuelGainMin(4));
        assertEquals(2, CombatRationItem.getFuelGainMax(4));
    }

    @Test
    void combatRationShouldRollFuelGainWithinExpectedRange() {
        int baseRoll = CombatRationItem.rollFuelGain(RandomSource.create(1234L), 0);
        int smallRoll = CombatRationItem.rollFuelGain(RandomSource.create(5678L), 4);

        assertTrue(baseRoll >= CombatRationItem.getFuelGainMin(0));
        assertTrue(baseRoll <= CombatRationItem.getFuelGainMax(0));
        assertTrue(smallRoll >= CombatRationItem.getFuelGainMin(4));
        assertTrue(smallRoll <= CombatRationItem.getFuelGainMax(4));
    }

    private record CollectingOutput(List<ItemStack> stacks) implements CreativeModeTab.Output {
        @Override
        public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
            stacks.add(stack.copy());
        }
    }
}
