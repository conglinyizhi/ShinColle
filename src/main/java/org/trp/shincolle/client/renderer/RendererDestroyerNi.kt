package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelDestroyerNi
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityDestroyerNi

class RendererDestroyerNi(context: EntityRendererProvider.Context) :
    MobRenderer<EntityDestroyerNi?, ModelDestroyerNi<EntityDestroyerNi?>?>(
        context,
        ModelDestroyerNi<EntityDestroyerNi>(context.bakeLayer(ModelDestroyerNi.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityDestroyerNi?, ModelDestroyerNi<EntityDestroyerNi?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityDestroyerNi): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityDestroyerNi, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityDestroyerNi): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/destroyer_ni.png")
        private const val MODEL_SCALE = 0.34f
    }
}
