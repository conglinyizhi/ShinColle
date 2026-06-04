package org.trp.shincolle.item;

import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;
import net.neoforged.neoforge.registries.DeferredItem;
import org.trp.shincolle.init.ModItems;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LegacyEquipItemTest {

    @Test
    void legacyEquipShouldClampVariantsAndResolveLegacyMappings() {
        LegacyEquipItem cannon = (LegacyEquipItem) ModItems.EQUIP_CANNON.get();
        LegacyEquipItem airplane = (LegacyEquipItem) ModItems.EQUIP_AIRPLANE.get();
        LegacyEquipItem torpedo = (LegacyEquipItem) ModItems.EQUIP_TORPEDO.get();

        ItemStack cannonBase = cannon.createVariantStack(-1);
        ItemStack cannonVariant = cannon.createVariantStack(2);
        ItemStack cannonClamped = cannon.createVariantStack(99);

        assertEquals(16, cannon.getVariantCount());
        assertEquals(0, cannon.getVariant(cannonBase));
        assertEquals(2, cannon.getVariant(cannonVariant));
        assertEquals(15, cannon.getVariant(cannonClamped));
        assertEquals(0, cannon.getEquipTypeId(cannonBase));
        assertEquals(1, cannon.getEquipTypeId(cannonVariant));
        assertEquals(0, cannon.getEquipId(cannonBase));
        assertEquals(201, cannon.getEquipId(cannonVariant));
        assertEquals(1503, cannon.getEquipId(cannonClamped));
        assertEquals(0, cannon.getModelVariant(cannonBase));
        assertEquals(1, cannon.getModelVariant(cannonVariant));
        assertEquals(2, cannon.getModelVariant(cannonClamped));

        ItemStack airplaneVariant = airplane.createVariantStack(21);
        assertEquals(22, airplane.getVariantCount());
        assertEquals(21, airplane.getVariant(airplaneVariant));
        assertEquals(9, airplane.getEquipTypeId(airplaneVariant));
        assertEquals(2109, airplane.getEquipId(airplaneVariant));
        assertEquals(1, airplane.getModelVariant(airplaneVariant));

        ItemStack torpedoVariant = torpedo.createVariantStack(6);
        assertEquals(7, torpedo.getVariantCount());
        assertEquals(6, torpedo.getVariant(torpedoVariant));
        assertEquals(5, torpedo.getEquipTypeId(torpedoVariant));
        assertEquals(605, torpedo.getEquipId(torpedoVariant));
        assertEquals(0, torpedo.getModelVariant(torpedoVariant));
    }

    @Test
    void legacyEquipShouldUseLegacyTranslationKeysAndExposeAllCreativeVariants() {
        LegacyEquipItem cannon = (LegacyEquipItem) ModItems.EQUIP_CANNON.get();
        LegacyEquipItem torpedo = (LegacyEquipItem) ModItems.EQUIP_TORPEDO.get();
        LegacyEquipItem airplane = (LegacyEquipItem) ModItems.EQUIP_AIRPLANE.get();

        String baseKey = ((TranslatableContents) cannon.getName(cannon.createVariantStack(0)).getContents()).getKey();
        String variantKey = ((TranslatableContents) cannon.getName(cannon.createVariantStack(3)).getContents()).getKey();

        assertEquals("item.shincolle.EquipCannon.name", baseKey);
        assertEquals("item.shincolle.EquipCannon3.name", variantKey);

        List<ItemStack> directVariants = new ArrayList<>();
        cannon.addAllVariantsToCreativeTab(new CollectingOutput(directVariants));
        assertEquals(cannon.getVariantCount(), directVariants.size());
        for (int i = 0; i < directVariants.size(); i++) {
            assertEquals(i, cannon.getVariant(directVariants.get(i)));
        }

        List<ItemStack> sortedVariants = new ArrayList<>();
        ModItems.addSortedLegacyEquipVariants(new CollectingOutput(sortedVariants), ModItems.EQUIP_CANNON);
        assertEquals(cannon.getVariantCount(), sortedVariants.size());
        for (int i = 0; i < sortedVariants.size(); i++) {
            assertEquals(i, cannon.getVariant(sortedVariants.get(i)));
            assertEquals(cannon.getEquipId(cannon.createVariantStack(i)), cannon.getEquipId(sortedVariants.get(i)));
        }

        assertSortedLegacyCreativeVariants(torpedo, ModItems.EQUIP_TORPEDO);
        assertSortedLegacyCreativeVariants(airplane, ModItems.EQUIP_AIRPLANE);
    }

    private static void assertSortedLegacyCreativeVariants(LegacyEquipItem item, DeferredItem<net.minecraft.world.item.Item> deferredItem) {
        List<ItemStack> sortedVariants = new ArrayList<>();

        ModItems.addSortedLegacyEquipVariants(new CollectingOutput(sortedVariants), deferredItem);

        assertEquals(item.getVariantCount(), sortedVariants.size());
        for (int i = 0; i < sortedVariants.size(); i++) {
            assertEquals(i, item.getVariant(sortedVariants.get(i)));
            assertEquals(item.getEquipId(item.createVariantStack(i)), item.getEquipId(sortedVariants.get(i)));
        }
    }

    private record CollectingOutput(List<ItemStack> stacks) implements CreativeModeTab.Output {
        @Override
        public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
            stacks.add(stack.copy());
        }
    }
}
