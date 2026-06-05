package org.trp.shincolle.item

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

class BucketRepairItem(properties: Properties) : Item(properties) {
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
        tooltipComponents.add(Component.translatable("gui.shincolle.bucketrepair").withStyle(ChatFormatting.AQUA))
        tooltipComponents.add(
            Component.translatable("gui.shincolle.bucketrepair.aircraft").withStyle(ChatFormatting.GRAY)
        )
    }
}
