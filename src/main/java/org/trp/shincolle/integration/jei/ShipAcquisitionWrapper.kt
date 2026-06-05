package org.trp.shincolle.integration.jei

import net.minecraft.world.item.ItemStack

@JvmRecord
data class ShipAcquisitionWrapper(
    val shipEgg: ItemStack?,
    val sourceIcons: MutableList<ItemStack?>?,
    val sourceLangKeys: MutableList<String?>?
)
