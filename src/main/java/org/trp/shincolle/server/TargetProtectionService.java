package org.trp.shincolle.server;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.trp.shincolle.entity.base.EntityShipBase;

public final class TargetProtectionService {
    private TargetProtectionService() {
    }

    public static boolean isUnattackableTargetClass(EntityShipBase ship, LivingEntity target) {
        if (!(ship.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        return UnattackableTargetData.get(serverLevel).contains(target.getClass().getName());
    }

    public static boolean isPlayerConfiguredTargetClass(EntityShipBase ship, Entity target) {
        if (!(ship.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        return PlayerTargetListSavedData.get(serverLevel).contains(ship.getOwnerUUID(), target.getClass().getName());
    }

    public static void toggleUnattackableTarget(Player player, Entity entity) {
        if (!(player.level() instanceof ServerLevel serverLevel) || !player.hasPermissions(2) || entity == null) {
            return;
        }

        String className = entity.getClass().getName();
        boolean added = UnattackableTargetData.get(serverLevel).toggle(className);
        Component prefix = Component.translatable(added ? "chat.shincolle.optool.add" : "chat.shincolle.optool.remove");
        player.displayClientMessage(prefix.copy().append(" " + className), false);
    }

    public static void showUnattackableTargets(Player player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        player.displayClientMessage(Component.translatable("chat.shincolle.optool.show").withStyle(ChatFormatting.GOLD), false);
        for (String className : UnattackableTargetData.get(serverLevel).entries()) {
            player.displayClientMessage(Component.literal(className).withStyle(ChatFormatting.AQUA), false);
        }
    }

    public static void togglePlayerTarget(Player player, Entity entity) {
        if (!(player.level() instanceof ServerLevel serverLevel) || entity == null) {
            return;
        }

        String className = entity.getClass().getName();
        boolean added = PlayerTargetListSavedData.get(serverLevel).toggle(player.getUUID(), className);
        Component prefix = Component.translatable(added ? "chat.shincolle.target.add" : "chat.shincolle.target.remove");
        player.displayClientMessage(prefix.copy().append(" " + className), false);
    }

    public static void showPlayerTargets(Player player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        player.displayClientMessage(Component.translatable("gui.shincolle.targetAI").withStyle(ChatFormatting.GOLD), false);
        for (String className : PlayerTargetListSavedData.get(serverLevel).entries(player.getUUID())) {
            player.displayClientMessage(Component.literal(className).withStyle(ChatFormatting.AQUA), false);
        }
    }
}
