package org.trp.shincolle.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.trp.shincolle.entity.base.EntityShipBase;

import java.util.List;

public class ModernKitItem extends Item {
    public ModernKitItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (!(interactionTarget instanceof EntityShipBase ship)) {
            return InteractionResult.PASS;
        }
        if (!ship.isOwnedBy(player)) {
            return InteractionResult.PASS;
        }
        if (player.level().isClientSide) {
            return InteractionResult.sidedSuccess(true);
        }

        int attrId = player.getRandom().nextInt(6);
        ship.setAttrBonus(attrId, ship.getAttrBonus(attrId) + 1);
        ship.setHealth(Math.min(ship.getMaxHealth(), ship.getHealth() + 1.0F));

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.sidedSuccess(false);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("gui.shincolle.modernkit").withStyle(ChatFormatting.GOLD));
    }
}
