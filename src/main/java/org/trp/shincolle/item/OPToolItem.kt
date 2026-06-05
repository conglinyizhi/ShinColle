package org.trp.shincolle.item

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

class OPToolItem(properties: Properties) : Item(properties.stacksTo(1)) {
    override fun isFoil(stack: ItemStack): Boolean {
        return true
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        tooltipComponents.add(Component.translatable("gui.shincolle.optool1").withStyle(ChatFormatting.RED))
        tooltipComponents.add(Component.translatable("gui.shincolle.optool2").withStyle(ChatFormatting.AQUA))
    }
}
