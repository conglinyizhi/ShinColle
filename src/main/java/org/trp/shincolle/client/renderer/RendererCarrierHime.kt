package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelCarrierHime
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityCarrierHime

class RendererCarrierHime(context: EntityRendererProvider.Context) :
    MobRenderer<EntityCarrierHime?, ModelCarrierHime<EntityCarrierHime?>?>(
        context,
        ModelCarrierHime<EntityCarrierHime>(context.bakeLayer(ModelCarrierHime.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityCarrierHime?, ModelCarrierHime<EntityCarrierHime?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityCarrierHime): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityCarrierHime, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityCarrierHime): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/carrier_hime.png")
        private const val MODEL_SCALE = 1.0f
    }
}
