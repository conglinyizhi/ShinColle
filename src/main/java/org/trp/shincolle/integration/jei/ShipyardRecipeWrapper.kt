package org.trp.shincolle.integration.jei

import net.minecraft.world.item.ItemStack

/**
 * Wraps a shipyard recipe for JEI display.
 * Each recipe represents one candidate ship type with example material amounts.
 *
 * @param inputs   4 material ItemStacks (Grudge, Abyss Metal, Ammo, Polymetal) with counts
 * @param fuel     Fuel ItemStack (typically Lava Bucket)
 * @param outputs  Possible output ship spawn eggs (usually 1 per recipe)
 */
class ShipyardRecipeWrapper(
    inputs: List<ItemStack>,
    val fuel: ItemStack,
    outputs: List<ItemStack>
) {
    val inputs: List<ItemStack> = inputs.toList()
    val outputs: List<ItemStack> = outputs.toList()
}
