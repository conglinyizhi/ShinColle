package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelSubmRo500
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntitySubmRo500

class RendererSubmRo500(context: EntityRendererProvider.Context) :
    MobRenderer<EntitySubmRo500?, ModelSubmRo500<EntitySubmRo500?>?>(
        context,
        ModelSubmRo500<EntitySubmRo500>(context.bakeLayer(ModelSubmRo500.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntitySubmRo500?, ModelSubmRo500<EntitySubmRo500?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntitySubmRo500): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntitySubmRo500, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntitySubmRo500): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/subm_ro_500.png")
        private const val MODEL_SCALE = 0.34f
    }
}
