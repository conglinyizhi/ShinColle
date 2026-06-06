package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelDestroyerInazuma
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityDestroyerInazuma

class RendererDestroyerInazuma(context: EntityRendererProvider.Context) :
    MobRenderer<EntityDestroyerInazuma?, ModelDestroyerInazuma<EntityDestroyerInazuma?>?>(
        context,
        ModelDestroyerInazuma<EntityDestroyerInazuma>(context.bakeLayer(ModelDestroyerInazuma.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(
            GenericGlowLayer<EntityDestroyerInazuma?, ModelDestroyerInazuma<EntityDestroyerInazuma?>?>(
                this,
                TEXTURE
            )
        )
    }

    override fun getTextureLocation(entity: EntityDestroyerInazuma): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityDestroyerInazuma, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityDestroyerInazuma): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/destroyer_inazuma.png")
        private const val MODEL_SCALE = 0.34f
    }
}
