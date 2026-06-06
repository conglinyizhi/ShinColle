package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelCarrierKaga
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityCarrierKaga

class RendererCarrierKaga(context: EntityRendererProvider.Context) :
    MobRenderer<EntityCarrierKaga?, ModelCarrierKaga<EntityCarrierKaga?>?>(
        context,
        ModelCarrierKaga<EntityCarrierKaga>(context.bakeLayer(ModelCarrierKaga.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityCarrierKaga?, ModelCarrierKaga<EntityCarrierKaga?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityCarrierKaga): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityCarrierKaga, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityCarrierKaga): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/carrier_kaga.png")
        private const val MODEL_SCALE = 0.34f
    }
}
