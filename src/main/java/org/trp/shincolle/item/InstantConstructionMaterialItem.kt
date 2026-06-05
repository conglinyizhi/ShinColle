package org.trp.shincolle.item

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

class InstantConstructionMaterialItem(properties: Properties) : Item(properties) {
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
        tooltipComponents.add(Component.translatable("gui.shincolle.instantconmat").withStyle(ChatFormatting.AQUA))
        tooltipComponents.add(Component.translatable("gui.shincolle.instantconmat.slot").withStyle(ChatFormatting.GRAY))
    }
}
