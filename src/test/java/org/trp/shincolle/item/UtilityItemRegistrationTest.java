package org.trp.shincolle.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;
import org.trp.shincolle.init.ModItems;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class UtilityItemRegistrationTest {

    @Test
    void utilityItemsShouldResolveFromDeferredRegistrations() {
        assertRegistered(ModItems.SHIN_COMB.get());
        assertRegistered(ModItems.CRANE.get());
        assertRegistered(ModItems.DESK.get());
        assertRegistered(ModItems.RECIPE_PAPER.get());
        assertRegistered(ModItems.SHIPSPAWNEGGL.get());
        assertRegistered(ModItems.SHIPSPAWNEGGS.get());
        assertRegistered(ModItems.SMALL_SHIPYARD.get());
    }

    private static void assertRegistered(Item item) {
        assertNotNull(item);
        assertNotSame(Items.AIR, item);
    }
}
