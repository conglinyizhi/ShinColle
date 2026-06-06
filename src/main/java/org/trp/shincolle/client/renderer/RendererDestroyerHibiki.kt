package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelDestroyerHibiki
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityDestroyerHibiki

class RendererDestroyerHibiki(context: EntityRendererProvider.Context) :
    MobRenderer<EntityDestroyerHibiki, ModelDestroyerHibiki<EntityDestroyerHibiki>>(
        context,
        ModelDestroyerHibiki<EntityDestroyerHibiki>(context.bakeLayer(ModelDestroyerHibiki.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(
            GenericGlowLayer<EntityDestroyerHibiki, ModelDestroyerHibiki<EntityDestroyerHibiki>>(
                this,
                TEXTURE
            )
        )
    }

    override fun getTextureLocation(entity: EntityDestroyerHibiki): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityDestroyerHibiki, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityDestroyerHibiki): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/destroyer_hibiki.png")
        private const val MODEL_SCALE = 0.34f
    }
}
