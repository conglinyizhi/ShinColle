package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelSubmHime
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntitySubmHime

class RendererSubmHime(context: EntityRendererProvider.Context) :
    MobRenderer<EntitySubmHime?, ModelSubmHime<EntitySubmHime?>?>(
        context,
        ModelSubmHime<EntitySubmHime>(context.bakeLayer(ModelSubmHime.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntitySubmHime?, ModelSubmHime<EntitySubmHime?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntitySubmHime): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntitySubmHime, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntitySubmHime): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/subm_hime.png")
        private const val MODEL_SCALE = 0.34f
    }
}
