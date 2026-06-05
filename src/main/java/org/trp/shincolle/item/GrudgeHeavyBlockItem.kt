package org.trp.shincolle.item

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.block.Block
import org.trp.shincolle.crafting.ShipyardRecipes

class GrudgeHeavyBlockItem(block: Block, properties: Properties) : BlockItem(block, properties) {
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)

        val mats = ShipyardRecipes.getHeavyGrudgeMatsTag(stack)
        if (mats == null) {
            return
        }

        tooltipComponents.add(
            Component.literal(mats[0].toString() + " ").withStyle(ChatFormatting.WHITE)
                .append(Component.translatable("gui.shincolle.grudge").withStyle(ChatFormatting.WHITE))
        )
        tooltipComponents.add(
            Component.literal(mats[1].toString() + " ").withStyle(ChatFormatting.RED)
                .append(Component.translatable("item.shincolle.abyss_metal").withStyle(ChatFormatting.RED))
        )
        tooltipComponents.add(
            Component.literal(mats[2].toString() + " ").withStyle(ChatFormatting.GREEN)
                .append(Component.translatable("gui.shincolle.ammolight").withStyle(ChatFormatting.GREEN))
        )
        tooltipComponents.add(
            Component.literal(mats[3].toString() + " ").withStyle(ChatFormatting.AQUA)
                .append(Component.translatable("item.shincolle.abyss_polymetal").withStyle(ChatFormatting.AQUA))
        )

        val fuel = ShipyardRecipes.getHeavyGrudgeFuelTag(stack)
        if (fuel > 0) {
            tooltipComponents.add(
                Component.translatable("gui.shincolle.heavygrudge.fuel", fuel).withStyle(ChatFormatting.GOLD)
            )
        }
    }
}
