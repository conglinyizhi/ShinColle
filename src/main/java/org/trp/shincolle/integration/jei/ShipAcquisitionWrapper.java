package org.trp.shincolle.integration.jei;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ShipAcquisitionWrapper(
        ItemStack shipEgg,
        List<ItemStack> sourceIcons,
        List<String> sourceLangKeys
) {
}
