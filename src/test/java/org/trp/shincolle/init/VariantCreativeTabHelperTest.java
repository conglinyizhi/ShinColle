package org.trp.shincolle.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;
import org.trp.shincolle.item.AbyssNuggetItem;
import org.trp.shincolle.item.CombatRationItem;
import org.trp.shincolle.item.GrudgeItem;
import org.trp.shincolle.item.PointerItem;
import org.trp.shincolle.item.ShipTankItem;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VariantCreativeTabHelperTest {

    @Test
    void variantHelpersShouldEmitResolvedVariantStacks() {
        assertShipTankVariants();
        assertCombatRationVariants();
        assertGrudgeVariants();
        assertAbyssNuggetVariants();
        assertPointerVariants();
    }

    private static void assertShipTankVariants() {
        ShipTankItem item = (ShipTankItem) ModItems.SHIP_TANK.get();
        List<ItemStack> stacks = new ArrayList<>();

        ModItems.addShipTankVariants(new CollectingOutput(stacks));

        assertEquals(item.getVariantCount(), stacks.size());
        for (int i = 0; i < stacks.size(); i++) {
            assertEquals(i, item.getVariant(stacks.get(i)));
        }
    }

    private static void assertCombatRationVariants() {
        CombatRationItem item = (CombatRationItem) ModItems.COMBAT_RATION.get();
        List<ItemStack> stacks = new ArrayList<>();

        ModItems.addCombatRationVariants(new CollectingOutput(stacks));

        assertEquals(item.getVariantCount(), stacks.size());
        for (int i = 0; i < stacks.size(); i++) {
            assertEquals(i, item.getVariant(stacks.get(i)));
        }
    }

    private static void assertGrudgeVariants() {
        GrudgeItem item = (GrudgeItem) ModItems.GRUDGE.get();
        List<ItemStack> stacks = new ArrayList<>();

        ModItems.addGrudgeVariants(new CollectingOutput(stacks));

        assertEquals(2, stacks.size());
        assertEquals(0, item.getVariant(stacks.get(0)));
        assertEquals(1, item.getVariant(stacks.get(1)));
    }

    private static void assertAbyssNuggetVariants() {
        AbyssNuggetItem item = (AbyssNuggetItem) ModItems.ABYSS_NUGGET.get();
        List<ItemStack> stacks = new ArrayList<>();

        ModItems.addAbyssNuggetVariants(new CollectingOutput(stacks));

        assertEquals(2, stacks.size());
        assertEquals(0, item.getVariant(stacks.get(0)));
        assertEquals(1, item.getVariant(stacks.get(1)));
    }

    private static void assertPointerVariants() {
        PointerItem item = (PointerItem) ModItems.POINTER_ITEM.get();
        List<ItemStack> stacks = new ArrayList<>();

        ModItems.addPointerVariants(new CollectingOutput(stacks));

        assertEquals(3, stacks.size());
        for (int i = 0; i < stacks.size(); i++) {
            assertEquals(i, item.getMode(stacks.get(i)));
        }
    }

    private record CollectingOutput(List<ItemStack> stacks) implements CreativeModeTab.Output {
        @Override
        public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
            stacks.add(stack.copy());
        }
    }
}
