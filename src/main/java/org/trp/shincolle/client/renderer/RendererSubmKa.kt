package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelSubmKa
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntitySubmKa

class RendererSubmKa(context: EntityRendererProvider.Context) : MobRenderer<EntitySubmKa, ModelSubmKa<EntitySubmKa>>(
    context,
    ModelSubmKa<EntitySubmKa>(context.bakeLayer(ModelSubmKa.LAYER_LOCATION)),
    0.5f
) {
    init {
        this.addLayer(GenericGlowLayer<EntitySubmKa, ModelSubmKa<EntitySubmKa>>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntitySubmKa): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntitySubmKa, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntitySubmKa): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/subm_ka.png")
        private const val MODEL_SCALE = 0.34f
    }
}
