package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelHarbourHime
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityHarbourHime

class RendererHarbourHime(context: EntityRendererProvider.Context) :
    MobRenderer<EntityHarbourHime, ModelHarbourHime<EntityHarbourHime>>(
        context,
        ModelHarbourHime<EntityHarbourHime>(context.bakeLayer(ModelHarbourHime.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityHarbourHime, ModelHarbourHime<EntityHarbourHime>>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityHarbourHime): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityHarbourHime, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityHarbourHime): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/harbour_hime.png")
        private const val MODEL_SCALE = 0.34f
    }
}
