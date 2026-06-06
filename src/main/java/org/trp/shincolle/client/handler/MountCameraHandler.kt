package org.trp.shincolle.client.handler

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderFrameEvent
import net.neoforged.neoforge.client.event.RenderPlayerEvent
import org.trp.shincolle.Shincolle
import org.trp.shincolle.entity.base.EntityMountBase

@EventBusSubscriber(modid = Shincolle.MODID, value = [Dist.CLIENT])
object MountCameraHandler {
    private var isCameraHijacked = false

    @SubscribeEvent
    fun onRenderFrame(event: RenderFrameEvent.Pre?) {
        val mc = Minecraft.getInstance()
        val player: Player? = mc.player
        if (player == null) return

        if (player.getVehicle() is EntityMountBase) {
            val mount = player.getVehicle() as EntityMountBase
            val host: Entity? = mount.getHost()
            if (host != null) {
                if (mc.getCameraEntity() !== host) {
                    mc.setCameraEntity(host)
                    isCameraHijacked = true
                }

                if (host is LivingEntity) {
                    host.setXRot(player.getXRot())
                    host.setYRot(player.getYRot())
                    host.xRotO = player.xRotO
                    host.yRotO = player.yRotO
                    host.yHeadRot = player.getYHeadRot()
                    host.yHeadRotO = player.yHeadRotO
                    host.yBodyRot = player.yBodyRot
                    host.yBodyRotO = player.yBodyRotO
                }
            }
        } else if (isCameraHijacked) {
            if (mc.getCameraEntity() !== player) {
                mc.setCameraEntity(player)
            }
            isCameraHijacked = false
        }
    }

    @SubscribeEvent
    fun onRenderPlayerPre(event: RenderPlayerEvent.Pre) {
        if (isCameraHijacked && event.getEntity() === Minecraft.getInstance().player) {
            if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                return
            }
            event.setCanceled(true)
        }
    }
}
