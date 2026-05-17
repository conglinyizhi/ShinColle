package org.trp.shincolle.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.entity.base.EntityMountBase;

@EventBusSubscriber(modid = Shincolle.MODID, value = Dist.CLIENT)
public class MountCameraHandler {

    private static boolean isCameraHijacked = false;

    @SubscribeEvent
    public static void onRenderFrame(RenderFrameEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        if (player.getVehicle() instanceof EntityMountBase mount) {
            Entity host = mount.getHost();
            if (host != null) {
                if (mc.getCameraEntity() != host) {
                    mc.setCameraEntity(host);
                    isCameraHijacked = true;
                }

                if (host instanceof LivingEntity livingHost) {
                    livingHost.setXRot(player.getXRot());
                    livingHost.setYRot(player.getYRot());
                    livingHost.xRotO = player.xRotO;
                    livingHost.yRotO = player.yRotO;
                    livingHost.yHeadRot = player.getYHeadRot();
                    livingHost.yHeadRotO = player.yHeadRotO;
                    livingHost.yBodyRot = player.yBodyRot;
                    livingHost.yBodyRotO = player.yBodyRotO;
                }
            }
        } else if (isCameraHijacked) {
            if (mc.getCameraEntity() != player) {
                mc.setCameraEntity(player);
            }
            isCameraHijacked = false;
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (isCameraHijacked && event.getEntity() == Minecraft.getInstance().player) {
            if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                return;
            }
            event.setCanceled(true);
        }
    }
}
