package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelSSNH
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntitySSNH

class RendererSSNH(context: EntityRendererProvider.Context) : MobRenderer<EntitySSNH?, ModelSSNH<EntitySSNH?>?>(
    context,
    ModelSSNH<EntitySSNH?>(context.bakeLayer(ModelSSNH.LAYER_LOCATION)),
    0.5f
) {
    init {
        this.addLayer(GenericGlowLayer<EntitySSNH?, ModelSSNH<EntitySSNH?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntitySSNH): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntitySSNH, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntitySSNH): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/subm_hime_new.png")
        private const val MODEL_SCALE = 0.34f
    }
}
