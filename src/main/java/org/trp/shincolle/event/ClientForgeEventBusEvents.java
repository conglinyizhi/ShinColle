package org.trp.shincolle.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.server.MarriageRingService;

@EventBusSubscriber(modid = Shincolle.MODID, value = Dist.CLIENT)
public final class ClientForgeEventBusEvents {
    private ClientForgeEventBusEvents() {
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (event.getType() != FogType.WATER) {
            return;
        }

        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        float multiplier = MarriageRingService.getUnderwaterFogDistanceMultiplier(player);
        if (multiplier <= 1.0F) {
            return;
        }

        event.scaleNearPlaneDistance(multiplier);
        event.scaleFarPlaneDistance(multiplier);
        event.setCanceled(true);
    }
}
