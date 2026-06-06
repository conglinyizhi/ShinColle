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

    @JvmStatic
    @SubscribeEvent
    fun onRenderFrame(event: RenderFrameEvent.Pre?) {
        val mc = Minecraft.getInstance()
        val player: Player? = mc.player
        if (player == null) return

        if (player.vehicle is EntityMountBase) {
            val mount = player.vehicle as EntityMountBase
            val host: Entity? = mount.host
            if (host != null) {
                if (mc.cameraEntity !== host) {
                    mc.setCameraEntity(host)
                    isCameraHijacked = true
                }

                if (host is LivingEntity) {
                    host.xRot = player.xRot
                    host.yRot = player.yRot
                    host.xRotO = player.xRotO
                    host.yRotO = player.yRotO
                    host.yHeadRot = player.yHeadRot
                    host.yHeadRotO = player.yHeadRotO
                    host.yBodyRot = player.yBodyRot
                    host.yBodyRotO = player.yBodyRotO
                }
            }
        } else if (isCameraHijacked) {
            if (mc.cameraEntity !== player) {
                mc.setCameraEntity(player)
            }
            isCameraHijacked = false
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onRenderPlayerPre(event: RenderPlayerEvent.Pre) {
        if (isCameraHijacked && event.entity === Minecraft.getInstance().player) {
            if (Minecraft.getInstance().options.cameraType.isFirstPerson()) {
                return
            }
            event.isCanceled = true
        }
    }
}
