package org.trp.shincolle.integration.jei;

import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Wraps a shipyard recipe for JEI display.
 * Each recipe represents one candidate ship type with example material amounts.
 *
 * @param inputs     4 material ItemStacks (Grudge, Abyss Metal, Ammo, Polymetal) with counts
 * @param fuel       Fuel ItemStack (typically Lava Bucket)
 * @param outputs    Possible output ship spawn eggs (usually 1 per recipe)
 */
public record ShipyardRecipeWrapper(
        List<ItemStack> inputs,
        ItemStack fuel,
        List<ItemStack> outputs
) {
    public ShipyardRecipeWrapper {
        inputs = List.copyOf(inputs);
        outputs = List.copyOf(outputs);
    }
}
