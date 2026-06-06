package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelCarrierWDemon
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityCarrierWDemon

class RendererCarrierWDemon(context: EntityRendererProvider.Context) :
    MobRenderer<EntityCarrierWDemon, ModelCarrierWDemon<EntityCarrierWDemon>>(
        context,
        ModelCarrierWDemon<EntityCarrierWDemon>(context.bakeLayer(ModelCarrierWDemon.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityCarrierWDemon, ModelCarrierWDemon<EntityCarrierWDemon>>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityCarrierWDemon): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityCarrierWDemon, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityCarrierWDemon): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/carrier_w_demon.png")
        private const val MODEL_SCALE = 1.0f
    }
}
