package org.trp.shincolle.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ToyAirplaneItem extends Item {
    public ToyAirplaneItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("gui.shincolle.toyairplane").withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Component.translatable("gui.shincolle.toyairplane.aircraft").withStyle(ChatFormatting.GRAY));
    }
}
