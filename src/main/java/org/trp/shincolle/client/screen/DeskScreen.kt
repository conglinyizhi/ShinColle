package org.trp.shincolle.client

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import org.trp.shincolle.entity.base.EntityShipBase

object ClientProxy {
    fun isLocalPlayerOwner(ship: EntityShipBase): Boolean {
        val localPlayer: Player? = Minecraft.getInstance().player
        return localPlayer != null && ship.isOwnedBy(localPlayer)
    }
}
