package org.trp.shincolle.event

import net.minecraft.client.Minecraft
import net.minecraft.world.level.material.FogType
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ViewportEvent.RenderFog
import org.trp.shincolle.Shincolle
import org.trp.shincolle.server.MarriageRingService.getUnderwaterFogDistanceMultiplier

@EventBusSubscriber(modid = Shincolle.MODID, value = [Dist.CLIENT])
object ClientForgeEventBusEvents {
    @JvmStatic
    @SubscribeEvent
    fun onRenderFog(event: RenderFog) {
        if (event.getType() != FogType.WATER) {
            return
        }

        val player = Minecraft.getInstance().player
        if (player == null) {
            return
        }

        val multiplier = getUnderwaterFogDistanceMultiplier(player)
        if (multiplier <= 1.0f) {
            return
        }

        event.scaleNearPlaneDistance(multiplier)
        event.scaleFarPlaneDistance(multiplier)
        event.setCanceled(true)
    }
}
