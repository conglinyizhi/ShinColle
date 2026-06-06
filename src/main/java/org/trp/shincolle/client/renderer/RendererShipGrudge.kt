package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelShipGrudge
import org.trp.shincolle.entity.EntityShipGrudge

class RendererShipGrudge(context: EntityRendererProvider.Context) : EntityRenderer<EntityShipGrudge>(context) {
    private val model: ModelShipGrudge<EntityShipGrudge>

    init {
        this.model = ModelShipGrudge<EntityShipGrudge>(context.bakeLayer(ModelShipGrudge.LAYER_LOCATION))
    }

    override fun render(
        entity: EntityShipGrudge, entityYaw: Float, partialTicks: Float, poseStack: PoseStack,
        buffer: MultiBufferSource, packedLight: Int
    ) {
        poseStack.pushPose()
        val age = entity.tickCount + partialTicks
        val wobble = Mth.cos(age * 0.12f) * 0.5f
        val alpha = if (wobble < 0.0f) 0.9f + wobble else 0.9f - wobble
        val scale = if (wobble < 0.0f) 0.25f - wobble * 0.5f else 0.25f + wobble * 1.25f
        val color = (Mth.clamp((alpha * 255.0f).toInt(), 0, 255) shl 24) or 0xFFFFFF

        poseStack.translate(0.0, 0.1, 0.0)
        poseStack.scale(scale, scale, scale)
        this.model.setDynamicRotation(age)

        val consumer = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE))
        this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, color)
        poseStack.popPose()
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)
    }

    override fun getTextureLocation(entity: EntityShipGrudge): ResourceLocation {
        return TEXTURE
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/modelbasicentityitem.png")
    }
}
