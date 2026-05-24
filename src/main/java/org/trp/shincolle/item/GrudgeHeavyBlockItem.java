package org.trp.shincolle.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.trp.shincolle.crafting.ShipyardRecipes;

import java.util.List;

public class GrudgeHeavyBlockItem extends BlockItem {
    public GrudgeHeavyBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int[] mats = ShipyardRecipes.getHeavyGrudgeMatsTag(stack);
        if (mats == null) {
            return;
        }

        tooltipComponents.add(Component.literal(mats[0] + " ").withStyle(ChatFormatting.WHITE)
                .append(Component.translatable("gui.shincolle.grudge").withStyle(ChatFormatting.WHITE)));
        tooltipComponents.add(Component.literal(mats[1] + " ").withStyle(ChatFormatting.RED)
                .append(Component.translatable("item.shincolle.abyss_metal").withStyle(ChatFormatting.RED)));
        tooltipComponents.add(Component.literal(mats[2] + " ").withStyle(ChatFormatting.GREEN)
                .append(Component.translatable("gui.shincolle.ammolight").withStyle(ChatFormatting.GREEN)));
        tooltipComponents.add(Component.literal(mats[3] + " ").withStyle(ChatFormatting.AQUA)
                .append(Component.translatable("item.shincolle.abyss_polymetal").withStyle(ChatFormatting.AQUA)));

        int fuel = ShipyardRecipes.getHeavyGrudgeFuelTag(stack);
        if (fuel > 0) {
            tooltipComponents.add(Component.translatable("gui.shincolle.heavygrudge.fuel", fuel).withStyle(ChatFormatting.GOLD));
        }
    }
}
