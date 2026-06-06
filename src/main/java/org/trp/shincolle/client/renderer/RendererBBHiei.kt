package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelBBHiei
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityBBHiei

class RendererBBHiei(context: EntityRendererProvider.Context) : MobRenderer<EntityBBHiei, ModelBBHiei<EntityBBHiei>>(
    context,
    ModelBBHiei<EntityBBHiei>(context.bakeLayer(ModelBBHiei.LAYER_LOCATION)),
    0.5f
) {
    init {
        this.addLayer(GenericGlowLayer<EntityBBHiei, ModelBBHiei<EntityBBHiei>>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityBBHiei): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityBBHiei, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityBBHiei): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/bb_hiei.png")
        private const val MODEL_SCALE = 0.34f
    }
}
