package org.trp.shincolle.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.trp.shincolle.entity.base.EntityShipBase;

public class ClientProxy {
    public static boolean isLocalPlayerOwner(EntityShipBase ship) {
        Player localPlayer = Minecraft.getInstance().player;
        return localPlayer != null && ship.isOwnedBy(localPlayer);
    }
}
