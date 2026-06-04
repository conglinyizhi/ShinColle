package org.trp.shincolle.item;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyEquipCreativeSortRegressionTest {
    private static final Path MOD_TABS = Path.of("src/main/java/org/trp/shincolle/init/ModTabs.java");
    private static final Path MOD_ITEMS = Path.of("src/main/java/org/trp/shincolle/init/ModItems.java");

    @Test
    void creativeTabUsesSortedLegacyEquipInsertion() throws Exception {
        String modTabs = Files.readString(MOD_TABS);
        assertTrue(modTabs.contains("addSortedLegacyEquipVariants(output, ModItems.EQUIP_CANNON)"));
        assertTrue(modTabs.contains("addSortedLegacyEquipVariants(output, ModItems.EQUIP_TORPEDO)"));
        assertTrue(modTabs.contains("addSortedLegacyEquipVariants(output, ModItems.EQUIP_AIRPLANE)"));
    }
}
