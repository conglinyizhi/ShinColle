package org.trp.shincolle.integration.jei

import net.minecraft.world.item.ItemStack

/**
 * Wraps an equipment development recipe for JEI display.
 * Each recipe represents one equipment type candidate with example material amounts.
 *
 * @param inputs  4 material ItemStacks (Grudge, Abyss Metal, Ammo, Polymetal) with counts
 * @param fuel    Fuel ItemStack (typically Lava Bucket)
 * @param outputs Example equipment stacks of this type (may show multiple variants)
 */
class EquipmentRecipeWrapper(
    inputs: List<ItemStack>,
    val fuel: ItemStack,
    outputs: List<ItemStack>
) {
    val inputs: List<ItemStack> = inputs.toList()
    val outputs: List<ItemStack> = outputs.toList()
}
