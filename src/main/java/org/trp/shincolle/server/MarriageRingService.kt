package org.trp.shincolle.server

import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.DamageTypeTags
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.Config
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.item.MarriageRingItem
import kotlin.math.max
import kotlin.math.min

object MarriageRingService {
    @JvmStatic
    fun applyTickAbilities(player: Player?) {
        if (player == null || !hasActiveMarriageRing(player)) {
            disableRingFlight(player)
            return
        }

        val marriedCount = PlayerStateService.getOwnedMarriedShipCount(player)

        if (Config.ringAbilityWaterBreathing >= 0 && marriedCount >= Config.ringAbilityWaterBreathing && player.isInWaterOrBubble()
            && player.airSupply < player.maxAirSupply
        ) {
            player.setAirSupply(player.maxAirSupply)
        }

        if (Config.ringAbilityFireImmunity >= 0 && marriedCount >= Config.ringAbilityFireImmunity && (player.isOnFire() || player.remainingFireTicks > 0)) {
            player.clearFire()
        }

        if (Config.ringAbilitySwimFlight >= 0 && marriedCount >= Config.ringAbilitySwimFlight) {
            if (player.isInWaterOrBubble()) {
                enableRingFlight(player)
            } else {
                disableRingFlight(player)
            }
        } else {
            disableRingFlight(player)
        }
    }

    @JvmStatic
    fun getUnderwaterBreakSpeedMultiplier(player: Player?): Float {
        if (player == null || Config.ringAbilityUnderwaterDigCap <= 0 || !hasActiveMarriageRing(player) || !player.isInWaterOrBubble()) {
            return 1.0f
        }

        val marriedCount = PlayerStateService.getOwnedMarriedShipCount(player)
        if (marriedCount <= 0) {
            return 1.0f
        }

        val effectiveCount = min(marriedCount, Config.ringAbilityUnderwaterDigCap)
        val digBoost = effectiveCount * 0.2f + 1.0f
        return 5.0f * digBoost
    }

    fun shouldCancelFireDamage(player: Player?, source: DamageSource?): Boolean {
        if (player == null || source == null || !source.`is`(DamageTypeTags.IS_FIRE) || Config.ringAbilityFireImmunity < 0 || !hasActiveMarriageRing(
                player
            )
        ) {
            return false
        }

        return PlayerStateService.getOwnedMarriedShipCount(player) >= Config.ringAbilityFireImmunity
    }

    /**
     * Handle fire damage event: cancels if the player has fire immunity from marriage ring.
     * Returns true if the damage was cancelled.
     */
    @JvmStatic
    fun handleFireDamageEvent(player: Player?, source: DamageSource?): Boolean {
        if (!shouldCancelFireDamage(player, source)) return false
        if (player!!.isOnFire()) {
            player.clearFire()
        }
        return true
    }

    @JvmStatic
    fun getUnderwaterFogDistanceMultiplier(player: Player?): Float {
        if (player == null || Config.ringAbilityUnderwaterFogCap < 0 || !hasActiveMarriageRing(player) || !player.isInWaterOrBubble()) {
            return 1.0f
        }

        val marriedCount = PlayerStateService.getOwnedMarriedShipCount(player)
        if (Config.ringAbilityUnderwaterFogCap == 0) {
            return if (marriedCount >= 0) 8.0f else 1.0f
        }

        var fogFactor =
            (Config.ringAbilityUnderwaterFogCap - marriedCount).toFloat() / Config.ringAbilityUnderwaterFogCap.toFloat()
        fogFactor = max(0.0001f, fogFactor)
        return min(8.0f, 1.0f / fogFactor)
    }

    fun hasActiveMarriageRing(player: Player?): Boolean {
        if (player == null) {
            return false
        }
        return !findActiveMarriageRing(player).isEmpty()
    }

    fun findActiveMarriageRing(player: Player?): ItemStack {
        if (player == null) {
            return ItemStack.EMPTY
        }

        for (stack in player.inventory.items) {
            if (isActiveMarriageRingStack(stack)) {
                return stack
            }
        }

        for (stack in player.inventory.offhand) {
            if (isActiveMarriageRingStack(stack)) {
                return stack
            }
        }

        for (stack in player.inventory.armor) {
            if (isActiveMarriageRingStack(stack)) {
                return stack
            }
        }

        return ItemStack.EMPTY
    }

    private fun isActiveMarriageRingStack(stack: ItemStack): Boolean {
        val item = stack.item
        return !stack.isEmpty() && item === ModItems.MARRIAGE_RING.get() && item is MarriageRingItem
                && MarriageRingItem.isActive(stack)
    }

    private fun enableRingFlight(player: Player?) {
        if (player !is ServerPlayer) {
            return
        }
        if (player.abilities.instabuild || player.abilities.flying || PlayerStateService.isRingFlightActive(
                player
            )
        ) {
            return
        }

        player.abilities.mayfly = true
        player.abilities.flying = true
        PlayerStateService.setRingFlightActive(player, true)
        player.onUpdateAbilities()
    }

    private fun disableRingFlight(player: Player?) {
        if (player !is ServerPlayer || !PlayerStateService.isRingFlightActive(player)) {
            return
        }

        if (!player.abilities.instabuild) {
            player.abilities.flying = false
            player.abilities.mayfly = false
        }
        PlayerStateService.setRingFlightActive(player, false)
        player.onUpdateAbilities()
    }
}
