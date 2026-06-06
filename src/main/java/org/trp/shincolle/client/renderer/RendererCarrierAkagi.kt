package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelCarrierAkagi
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityCarrierAkagi

class RendererCarrierAkagi(context: EntityRendererProvider.Context) :
    MobRenderer<EntityCarrierAkagi?, ModelCarrierAkagi<EntityCarrierAkagi?>?>(
        context,
        ModelCarrierAkagi<EntityCarrierAkagi>(context.bakeLayer(ModelCarrierAkagi.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityCarrierAkagi?, ModelCarrierAkagi<EntityCarrierAkagi?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityCarrierAkagi): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityCarrierAkagi, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityCarrierAkagi): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/carrier_akagi.png")
        private const val MODEL_SCALE = 0.34f
    }
}
