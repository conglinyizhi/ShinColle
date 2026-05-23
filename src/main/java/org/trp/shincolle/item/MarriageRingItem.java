package org.trp.shincolle.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import org.trp.shincolle.entity.base.EmotionParticleType;
import org.trp.shincolle.entity.base.EntityShipBase;

import java.util.List;

public class MarriageRingItem extends Item {
    private static final String TAG_ACTIVE = "LegacyActive";

    public MarriageRingItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            boolean next = !isActive(stack);
            setActive(stack, next);
            player.displayClientMessage(
                    Component.translatable(next ? "gui.shincolle.ring.on" : "gui.shincolle.ring.off"),
                    true
            );
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isActive(stack) || super.isFoil(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && isActive(stack) && entity instanceof Player player) {
            if (player.tickCount % 64 == 0) {
                applyAuraToNearbyShips(player, level);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(
                Component.translatable(isActive(stack) ? "gui.shincolle.ring.on" : "gui.shincolle.ring.off")
                        .withStyle(isActive(stack) ? ChatFormatting.AQUA : ChatFormatting.GRAY)
        );
    }

    private void applyAuraToNearbyShips(Player player, Level level) {
        AABB area = player.getBoundingBox().inflate(6.0, 5.0, 6.0);
        List<EntityShipBase> nearbyShips = level.getEntitiesOfClass(EntityShipBase.class, area);

        for (EntityShipBase ship : nearbyShips) {
            if (ship == null || !ship.isAlive() || !ship.isTame() || !ship.isOwnedBy(player)) {
                continue;
            }

            ship.setEmotionPrimary(EntityShipBase.EMOTION_HAPPY);
            if (ship.getRandom().nextInt(5) == 0) {
                ship.applyParticleEmotion(EmotionParticleType.HEART);
            }
        }
    }

    public static boolean isActive(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        return customData != null && customData.copyTag().getBoolean(TAG_ACTIVE);
    }

    private static void setActive(ItemStack stack, boolean active) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY,
                data -> data.update(tag -> tag.putBoolean(TAG_ACTIVE, active)));
    }
}
