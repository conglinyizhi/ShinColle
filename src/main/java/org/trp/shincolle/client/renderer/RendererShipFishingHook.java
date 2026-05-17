package org.trp.shincolle.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.trp.shincolle.entity.EntityShipFishingHook;
import org.trp.shincolle.entity.base.EntityShipBase;

public class RendererShipFishingHook extends EntityRenderer<EntityShipFishingHook> {
    private static final ResourceLocation FISH_PARTICLES = ResourceLocation.withDefaultNamespace("textures/entity/fishing_hook.png");

    public RendererShipFishingHook(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntityShipFishingHook entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float bobbing = Mth.cos((entity.tickCount + partialTicks) * 0.15f) * 0.05f - 0.25f;
        
        poseStack.pushPose();
        poseStack.translate(0.0D, bobbing + 0.25D, 0.0D);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity)));
        PoseStack.Pose pose = poseStack.last();
        
        vertexConsumer.addVertex(pose, -0.5f, -0.5f, 0.0f).setColor(255, 255, 255, 255).setUv(0.0f, 1.0f).setOverlay(0).setLight(packedLight).setNormal(pose, 0.0f, 1.0f, 0.0f);
        vertexConsumer.addVertex(pose, 0.5f, -0.5f, 0.0f).setColor(255, 255, 255, 255).setUv(1.0f, 1.0f).setOverlay(0).setLight(packedLight).setNormal(pose, 0.0f, 1.0f, 0.0f);
        vertexConsumer.addVertex(pose, 0.5f, 0.5f, 0.0f).setColor(255, 255, 255, 255).setUv(1.0f, 0.0f).setOverlay(0).setLight(packedLight).setNormal(pose, 0.0f, 1.0f, 0.0f);
        vertexConsumer.addVertex(pose, -0.5f, 0.5f, 0.0f).setColor(255, 255, 255, 255).setUv(0.0f, 0.0f).setOverlay(0).setLight(packedLight).setNormal(pose, 0.0f, 1.0f, 0.0f);
        poseStack.popPose();

        EntityShipBase host = entity.getHost();
        if (host != null) {
            
            double hostX = Mth.lerp(partialTicks, host.xo, host.getX());
            double hostY = Mth.lerp(partialTicks, host.yo, host.getY());
            double hostZ = Mth.lerp(partialTicks, host.zo, host.getZ());

            double hookBaseX = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double hookBaseY = Mth.lerp(partialTicks, entity.yo, entity.getY());
            double hookBaseZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());

            
            float bodyRot = Mth.lerp(partialTicks, host.yBodyRotO, host.yBodyRot) * (Mth.PI / 180.0F);
            double sin = Mth.sin(bodyRot);
            double cos = Mth.cos(bodyRot);

            
            double sideOffset = (host.getMainArm() == HumanoidArm.RIGHT ? 1.0D : -1.0D) * 0.35D;
            
            double forwardOffset = 0.3D;

            
            double tipX = hostX - cos * sideOffset - sin * forwardOffset;
            
            double tipY = hostY + (host.getBbHeight() * 0.45D);
            double tipZ = hostZ - sin * sideOffset + cos * forwardOffset;

            
            if (host.isCrouching()) tipY -= 0.15D;
            if (host.getIsSitting()) tipY -= host.getBbHeight() * 0.3D;

            
            double actualHookLocalY = bobbing + 0.25D;

            
            double dx = tipX - hookBaseX;
            double dy = tipY - (hookBaseY + actualHookLocalY);
            double dz = tipZ - hookBaseZ;

            VertexConsumer lineConsumer = buffer.getBuffer(RenderType.lineStrip());
            PoseStack.Pose linePose = poseStack.last();

            
            for (int i = 0; i <= 16; ++i) {
                float t = i / 16.0F;
                float lx = (float)(dx * t);
                
                float ly = (float)(actualHookLocalY + (dy * t) - (t * (1.0F - t) * 0.5F)); 
                float lz = (float)(dz * t);

                lineConsumer.addVertex(linePose, lx, ly, lz)
                        .setColor(150, 150, 150, 255) 
                        .setNormal(linePose, 0.0f, 1.0f, 0.0f);
            }
        }

        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityShipFishingHook entity) {
        return FISH_PARTICLES;
    }
}
