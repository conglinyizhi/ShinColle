package org.trp.shincolle.server

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import org.trp.shincolle.entity.base.EntityShipBase

object TargetProtectionService {
    @JvmStatic
    fun isUnattackableTargetClass(ship: EntityShipBase?, target: LivingEntity?): Boolean {
        if (ship == null || target == null) {
            return false
        }
        if (ship.level() !is ServerLevel) {
            return false
        }
        return UnattackableTargetData.Companion.get(serverLevel).contains(target.javaClass.getName())
    }

    @JvmStatic
    fun isPlayerConfiguredTargetClass(ship: EntityShipBase?, target: Entity?): Boolean {
        if (ship == null || target == null) {
            return false
        }
        if (ship.level() !is ServerLevel) {
            return false
        }
        return PlayerTargetListSavedData.Companion.get(serverLevel)
            .contains(ship.getOwnerUUID(), target.javaClass.getName())
    }

    @JvmStatic
    fun toggleUnattackableTarget(player: Player?, entity: Entity?) {
        if (player == null) {
            return
        }
        if ((player.level() !is ServerLevel) || !player.hasPermissions(2) || entity == null) {
            return
        }

        val className = entity.javaClass.getName()
        val added: Boolean = UnattackableTargetData.Companion.get(serverLevel).toggle(className)
        val prefix: Component =
            Component.translatable(if (added) "chat.shincolle.optool.add" else "chat.shincolle.optool.remove")
        player.displayClientMessage(prefix.copy().append(" " + className), false)
    }

    @JvmStatic
    fun showUnattackableTargets(player: Player?) {
        if (player == null) {
            return
        }
        if (player.level() !is ServerLevel) {
            return
        }

        player.displayClientMessage(
            Component.translatable("chat.shincolle.optool.show").withStyle(ChatFormatting.GOLD),
            false
        )
        for (className in UnattackableTargetData.Companion.get(serverLevel).entries()) {
            player.displayClientMessage(Component.literal(className).withStyle(ChatFormatting.AQUA), false)
        }
    }

    @JvmStatic
    fun togglePlayerTarget(player: Player?, entity: Entity?) {
        if (player == null) {
            return
        }
        if (player.level() !is ServerLevel || entity == null) {
            return
        }

        val className = entity.javaClass.getName()
        val added: Boolean = PlayerTargetListSavedData.Companion.get(serverLevel).toggle(player.getUUID(), className)
        val prefix: Component =
            Component.translatable(if (added) "chat.shincolle.target.add" else "chat.shincolle.target.remove")
        player.displayClientMessage(prefix.copy().append(" " + className), false)
    }

    @JvmStatic
    fun showPlayerTargets(player: Player?) {
        if (player == null) {
            return
        }
        if (player.level() !is ServerLevel) {
            return
        }

        player.displayClientMessage(
            Component.translatable("gui.shincolle.targetAI").withStyle(ChatFormatting.GOLD),
            false
        )
        for (className in PlayerTargetListSavedData.Companion.get(serverLevel).entries(player.getUUID())) {
            player.displayClientMessage(Component.literal(className).withStyle(ChatFormatting.AQUA), false)
        }
    }
}
