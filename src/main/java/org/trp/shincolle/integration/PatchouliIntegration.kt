package org.trp.shincolle.integration

import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import vazkii.patchouli.api.PatchouliAPI

/**
 * Isolated Patchouli integration to avoid NoClassDefFoundError when
 * Patchouli is not installed. This class is only loaded when
 * [net.neoforged.neoforge.common.ModList.isLoaded] reports Patchouli present.
 */
object PatchouliIntegration {
    fun hasBook(bookId: ResourceLocation): Boolean {
        return !PatchouliAPI.get().isStub() && !PatchouliAPI.get().getBookStack(bookId).isEmpty()
    }

    fun openBook(player: Player, bookId: ResourceLocation) {
        if (player.level().isClientSide) {
            PatchouliAPI.get().openBookGUI(bookId)
        } else if (player is ServerPlayer) {
            PatchouliAPI.get().openBookGUI(player, bookId)
        }
    }
}
