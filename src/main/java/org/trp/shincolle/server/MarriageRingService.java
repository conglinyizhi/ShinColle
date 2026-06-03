package org.trp.shincolle.server;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import org.trp.shincolle.Config;
import org.trp.shincolle.init.ModItems;
import org.trp.shincolle.item.MarriageRingItem;

public final class MarriageRingService {
    private MarriageRingService() {
    }

    public static void applyTickAbilities(Player player) {
        if (player == null || !hasActiveMarriageRing(player)) {
            disableRingFlight(player);
            return;
        }

        int marriedCount = PlayerStateService.getOwnedMarriedShipCount(player);

        if (Config.ringAbilityWaterBreathing >= 0
                && marriedCount >= Config.ringAbilityWaterBreathing
                && player.isInWaterOrBubble()
                && player.getAirSupply() < player.getMaxAirSupply()) {
            player.setAirSupply(player.getMaxAirSupply());
        }

        if (Config.ringAbilityFireImmunity >= 0
                && marriedCount >= Config.ringAbilityFireImmunity
                && (player.isOnFire() || player.getRemainingFireTicks() > 0)) {
            player.clearFire();
        }

        if (Config.ringAbilitySwimFlight >= 0 && marriedCount >= Config.ringAbilitySwimFlight) {
            if (player.isInWaterOrBubble()) {
                enableRingFlight(player);
            } else {
                disableRingFlight(player);
            }
        } else {
            disableRingFlight(player);
        }
    }

    public static float getUnderwaterBreakSpeedMultiplier(Player player) {
        if (player == null
                || Config.ringAbilityUnderwaterDigCap <= 0
                || !hasActiveMarriageRing(player)
                || !player.isInWaterOrBubble()) {
            return 1.0F;
        }

        int marriedCount = PlayerStateService.getOwnedMarriedShipCount(player);
        if (marriedCount <= 0) {
            return 1.0F;
        }

        int effectiveCount = Math.min(marriedCount, Config.ringAbilityUnderwaterDigCap);
        float digBoost = effectiveCount * 0.2F + 1.0F;
        return 5.0F * digBoost;
    }

    public static boolean shouldCancelFireDamage(Player player, net.minecraft.world.damagesource.DamageSource source) {
        if (player == null
                || source == null
                || !source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE)
                || Config.ringAbilityFireImmunity < 0
                || !hasActiveMarriageRing(player)) {
            return false;
        }

        return PlayerStateService.getOwnedMarriedShipCount(player) >= Config.ringAbilityFireImmunity;
    }

    /**
     * Handle fire damage event: cancels if the player has fire immunity from marriage ring.
     * Returns true if the damage was cancelled.
     */
    public static boolean handleFireDamageEvent(Player player, net.minecraft.world.damagesource.DamageSource source) {
        if (!shouldCancelFireDamage(player, source)) return false;
        if (player.isOnFire()) {
            player.clearFire();
        }
        return true;
    }

    public static float getUnderwaterFogDistanceMultiplier(Player player) {
        if (player == null
                || Config.ringAbilityUnderwaterFogCap < 0
                || !hasActiveMarriageRing(player)
                || !player.isInWaterOrBubble()) {
            return 1.0F;
        }

        int marriedCount = PlayerStateService.getOwnedMarriedShipCount(player);
        if (Config.ringAbilityUnderwaterFogCap == 0) {
            return marriedCount >= 0 ? 8.0F : 1.0F;
        }

        float fogFactor = (float) (Config.ringAbilityUnderwaterFogCap - marriedCount) / (float) Config.ringAbilityUnderwaterFogCap;
        fogFactor = Math.max(0.0001F, fogFactor);
        return Math.min(8.0F, 1.0F / fogFactor);
    }

    public static boolean hasActiveMarriageRing(Player player) {
        if (player == null) {
            return false;
        }
        return !findActiveMarriageRing(player).isEmpty();
    }

    public static ItemStack findActiveMarriageRing(Player player) {
        if (player == null) {
            return ItemStack.EMPTY;
        }

        for (ItemStack stack : player.getInventory().items) {
            if (isActiveMarriageRingStack(stack)) {
                return stack;
            }
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (isActiveMarriageRingStack(stack)) {
                return stack;
            }
        }

        for (ItemStack stack : player.getInventory().armor) {
            if (isActiveMarriageRingStack(stack)) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    private static boolean isActiveMarriageRingStack(ItemStack stack) {
        Item item = stack.getItem();
        return !stack.isEmpty()
                && item == ModItems.MARRIAGE_RING.get()
                && item instanceof MarriageRingItem
                && MarriageRingItem.isActive(stack);
    }

    private static void enableRingFlight(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (player.getAbilities().instabuild || player.getAbilities().flying || PlayerStateService.isRingFlightActive(player)) {
            return;
        }

        player.getAbilities().mayfly = true;
        player.getAbilities().flying = true;
        PlayerStateService.setRingFlightActive(player, true);
        serverPlayer.onUpdateAbilities();
    }

    private static void disableRingFlight(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || !PlayerStateService.isRingFlightActive(player)) {
            return;
        }

        if (!player.getAbilities().instabuild) {
            player.getAbilities().flying = false;
            player.getAbilities().mayfly = false;
        }
        PlayerStateService.setRingFlightActive(player, false);
        serverPlayer.onUpdateAbilities();
    }
}
