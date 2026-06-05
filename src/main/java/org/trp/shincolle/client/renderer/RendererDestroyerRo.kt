package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelDestroyerRo
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityDestroyerRo

class RendererDestroyerRo(context: EntityRendererProvider.Context) :
    MobRenderer<EntityDestroyerRo?, ModelDestroyerRo<EntityDestroyerRo?>?>(
        context,
        ModelDestroyerRo<EntityDestroyerRo?>(context.bakeLayer(ModelDestroyerRo.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityDestroyerRo?, ModelDestroyerRo<EntityDestroyerRo?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityDestroyerRo): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityDestroyerRo, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityDestroyerRo): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/destroyer_ro.png")
        private const val MODEL_SCALE = 0.34f
    }
}
