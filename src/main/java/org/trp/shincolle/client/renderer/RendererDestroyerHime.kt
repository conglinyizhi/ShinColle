package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelDestroyerHime
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityDestroyerHime

class RendererDestroyerHime(context: EntityRendererProvider.Context) :
    MobRenderer<EntityDestroyerHime, ModelDestroyerHime<EntityDestroyerHime>>(
        context,
        ModelDestroyerHime<EntityDestroyerHime>(context.bakeLayer(ModelDestroyerHime.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityDestroyerHime, ModelDestroyerHime<EntityDestroyerHime>>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityDestroyerHime): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityDestroyerHime, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityDestroyerHime): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/destroyer_hime.png")
        private const val MODEL_SCALE = 0.34f
    }
}
