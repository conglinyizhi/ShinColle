package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.HumanoidArm
import org.trp.shincolle.entity.EntityShipFishingHook

class RendererShipFishingHook(context: EntityRendererProvider.Context) :
    EntityRenderer<EntityShipFishingHook>(context) {
    override fun render(
        entity: EntityShipFishingHook,
        yaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        val bobbing = Mth.cos((entity.tickCount + partialTicks) * 0.15f) * 0.05f - 0.25f

        poseStack.pushPose()
        poseStack.translate(0.0, bobbing + 0.25, 0.0)
        poseStack.scale(0.5f, 0.5f, 0.5f)
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation())

        val vertexConsumer = buffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity)))
        val pose = poseStack.last()

        vertexConsumer.addVertex(pose, -0.5f, -0.5f, 0.0f).setColor(255, 255, 255, 255).setUv(0.0f, 1.0f).setOverlay(0)
            .setLight(packedLight).setNormal(pose, 0.0f, 1.0f, 0.0f)
        vertexConsumer.addVertex(pose, 0.5f, -0.5f, 0.0f).setColor(255, 255, 255, 255).setUv(1.0f, 1.0f).setOverlay(0)
            .setLight(packedLight).setNormal(pose, 0.0f, 1.0f, 0.0f)
        vertexConsumer.addVertex(pose, 0.5f, 0.5f, 0.0f).setColor(255, 255, 255, 255).setUv(1.0f, 0.0f).setOverlay(0)
            .setLight(packedLight).setNormal(pose, 0.0f, 1.0f, 0.0f)
        vertexConsumer.addVertex(pose, -0.5f, 0.5f, 0.0f).setColor(255, 255, 255, 255).setUv(0.0f, 0.0f).setOverlay(0)
            .setLight(packedLight).setNormal(pose, 0.0f, 1.0f, 0.0f)
        poseStack.popPose()

        val host = entity.getHost()
        if (host != null) {
            val hostX = Mth.lerp(partialTicks.toDouble(), host.xo, host.x)
            val hostY = Mth.lerp(partialTicks.toDouble(), host.yo, host.y)
            val hostZ = Mth.lerp(partialTicks.toDouble(), host.zo, host.z)

            val hookBaseX = Mth.lerp(partialTicks.toDouble(), entity.xo, entity.x)
            val hookBaseY = Mth.lerp(partialTicks.toDouble(), entity.yo, entity.y)
            val hookBaseZ = Mth.lerp(partialTicks.toDouble(), entity.zo, entity.z)


            val bodyRot = Mth.lerp(partialTicks, host.yBodyRotO, host.yBodyRot) * (Mth.PI / 180.0f)
            val sin = Mth.sin(bodyRot).toDouble()
            val cos = Mth.cos(bodyRot).toDouble()


            val sideOffset = (if (host.mainArm == HumanoidArm.RIGHT) 1.0 else -1.0) * 0.35

            val forwardOffset = 0.3


            val tipX = hostX - cos * sideOffset - sin * forwardOffset

            var tipY = hostY + (host.bbHeight * 0.45)
            val tipZ = hostZ - sin * sideOffset + cos * forwardOffset


            if (host.isCrouching()) tipY -= 0.15
            if (host.isInSittingPose) tipY -= host.bbHeight * 0.3


            val actualHookLocalY = bobbing + 0.25


            val dx = tipX - hookBaseX
            val dy = tipY - (hookBaseY + actualHookLocalY)
            val dz = tipZ - hookBaseZ

            val lineConsumer = buffer.getBuffer(RenderType.lineStrip())
            val linePose = poseStack.last()


            for (i in 0..16) {
                val t = i / 16.0f
                val lx = (dx * t).toFloat()

                val ly = (actualHookLocalY + (dy * t) - (t * (1.0f - t) * 0.5f)).toFloat()
                val lz = (dz * t).toFloat()

                lineConsumer.addVertex(linePose, lx, ly, lz)
                    .setColor(150, 150, 150, 255)
                    .setNormal(linePose, 0.0f, 1.0f, 0.0f)
            }
        }

        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight)
    }

    override fun getTextureLocation(entity: EntityShipFishingHook): ResourceLocation {
        return FISH_PARTICLES
    }

    companion object {
        private val FISH_PARTICLES: ResourceLocation =
            ResourceLocation.withDefaultNamespace("textures/entity/fishing_hook.png")
    }
}
