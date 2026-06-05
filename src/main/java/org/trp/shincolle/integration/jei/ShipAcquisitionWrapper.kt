package org.trp.shincolle.integration.jei

import net.minecraft.world.item.ItemStack

/**
 * Wraps ship acquisition info for JEI display.
 *
 * @param shipEgg        The ship spawn egg output
 * @param sourceIcons    Icons representing acquisition sources
 * @param sourceLangKeys Language keys describing each source
 */
data class ShipAcquisitionWrapper(
    val shipEgg: ItemStack,
    val sourceIcons: List<ItemStack>,
    val sourceLangKeys: List<String>
)
