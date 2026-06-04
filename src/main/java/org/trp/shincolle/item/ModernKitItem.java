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
import org.trp.shincolle.Config;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.entity.base.EntityShipBase;

import java.util.List;

public class ModernKitItem extends Item {
    record MaxedFeedback(Component message, boolean actionBar) {
    }

    public ModernKitItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (!(interactionTarget instanceof EntityShipBase ship)) {
            return InteractionResult.PASS;
        }
        Shincolle.debugLog("ModernKit interact target={} ship={} ownerMatch={} client={} hand={} itemCount={}",
                interactionTarget.getType().toShortString(), ship.getUUID(), ship.isOwnedBy(player), player.level().isClientSide, usedHand, stack.getCount());
        if (!ship.isOwnedBy(player)) {
            return InteractionResult.PASS;
        }
        if (player.level().isClientSide) {
            if (!ship.getLegacyShipStats().hasBonusCapacity()) {
                Shincolle.debugLog("ModernKit client fail ship={} bonusesMaxed=true", ship.getUUID());
                return InteractionResult.FAIL;
            }
            Shincolle.debugLog("ModernKit client success ship={}", ship.getUUID());
            return InteractionResult.sidedSuccess(true);
        }

        int[] beforeBonus = new int[6];
        for (int i = 0; i < beforeBonus.length; i++) {
            beforeBonus[i] = ship.getAttrBonus(i);
        }

        if (!ship.interactModernKit(player, stack)) {
            Shincolle.debugLog("ModernKit noEffect ship={} bonusesMaxed=true", ship.getUUID());
            if (Config.modernKitNotifyWhenMaxed) {
                MaxedFeedback feedback = maxedFeedback();
                player.displayClientMessage(feedback.message(), feedback.actionBar());
            }
            return InteractionResult.FAIL;
        }

        int appliedAttrId = -1;
        for (int i = 0; i < beforeBonus.length; i++) {
            if (ship.getAttrBonus(i) > beforeBonus[i]) {
                appliedAttrId = i;
                break;
            }
        }

        Shincolle.debugLog("ModernKit applied ship={} attrId={} newAttrBonus={} bonuses={}/{}/{}/{}/{}/{} creative={}",
                ship.getUUID(), appliedAttrId, appliedAttrId >= 0 ? ship.getAttrBonus(appliedAttrId) : -1,
                ship.getAttrBonus(0), ship.getAttrBonus(1), ship.getAttrBonus(2),
                ship.getAttrBonus(3), ship.getAttrBonus(4), ship.getAttrBonus(5),
                player.getAbilities().instabuild);
        Shincolle.debugLog("ModernKit consumed ship={} remaining={}", ship.getUUID(), stack.getCount());
        return InteractionResult.sidedSuccess(false);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("gui.shincolle.modernkit").withStyle(ChatFormatting.GOLD));
    }

    static MaxedFeedback maxedFeedback() {
        return new MaxedFeedback(
                Component.translatable("chat.shincolle.modernkit.maxed"),
                Config.modernKitNotifyWhenMaxedActionBar);
    }
}
