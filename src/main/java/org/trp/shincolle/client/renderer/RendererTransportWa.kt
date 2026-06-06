package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelTransportWa
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityTransportWa

class RendererTransportWa(context: EntityRendererProvider.Context) :
    MobRenderer<EntityTransportWa?, ModelTransportWa<EntityTransportWa?>?>(
        context,
        ModelTransportWa<EntityTransportWa>(context.bakeLayer(ModelTransportWa.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityTransportWa?, ModelTransportWa<EntityTransportWa?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityTransportWa): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityTransportWa, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityTransportWa): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/transport_wa.png")
        private const val MODEL_SCALE = 0.34f
    }
}
