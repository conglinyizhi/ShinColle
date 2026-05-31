package org.trp.shincolle.integration.jei;

import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Wraps an equipment development recipe for JEI display.
 * Each recipe represents one equipment type candidate with example material amounts.
 *
 * @param inputs     4 material ItemStacks (Grudge, Abyss Metal, Ammo, Polymetal) with counts
 * @param fuel       Fuel ItemStack (typically Lava Bucket)
 * @param outputs    Example equipment stacks of this type (may show multiple variants)
 */
public record EquipmentRecipeWrapper(
        List<ItemStack> inputs,
        ItemStack fuel,
        List<ItemStack> outputs
) {
    public EquipmentRecipeWrapper {
        inputs = List.copyOf(inputs);
        outputs = List.copyOf(outputs);
    }
}
