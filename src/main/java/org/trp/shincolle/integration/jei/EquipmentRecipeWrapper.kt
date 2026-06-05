package org.trp.shincolle.integration.jei

import net.minecraft.world.item.ItemStack
import java.util.List

/**
 * Wraps an equipment development recipe for JEI display.
 * Each recipe represents one equipment type candidate with example material amounts.
 * 
 * @param inputs     4 material ItemStacks (Grudge, Abyss Metal, Ammo, Polymetal) with counts
 * @param fuel       Fuel ItemStack (typically Lava Bucket)
 * @param outputs    Example equipment stacks of this type (may show multiple variants)
 */
class EquipmentRecipeWrapper(
    inputs: MutableList<ItemStack?>?,
    val fuel: ItemStack?,
    outputs: MutableList<ItemStack?>?
) {
    val inputs: MutableList<ItemStack?>?
    val outputs: MutableList<ItemStack?>?

    init {
        var inputs = inputs
        var outputs = outputs
        inputs = List.copyOf<ItemStack?>(inputs)
        outputs = List.copyOf<ItemStack?>(outputs)
        this.inputs = inputs
        this.outputs = outputs
    }
}
