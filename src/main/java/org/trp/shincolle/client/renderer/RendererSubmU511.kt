package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelSubmU511
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntitySubmU511

class RendererSubmU511(context: EntityRendererProvider.Context) :
    MobRenderer<EntitySubmU511, ModelSubmU511<EntitySubmU511>>(
        context,
        ModelSubmU511<EntitySubmU511>(context.bakeLayer(ModelSubmU511.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntitySubmU511, ModelSubmU511<EntitySubmU511>>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntitySubmU511): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntitySubmU511, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntitySubmU511): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/subm_u_511.png")
        private const val MODEL_SCALE = 0.34f
    }
}
