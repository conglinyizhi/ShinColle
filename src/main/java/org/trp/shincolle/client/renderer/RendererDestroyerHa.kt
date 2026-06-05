package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelDestroyerHa
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityDestroyerHa

class RendererDestroyerHa(context: EntityRendererProvider.Context) :
    MobRenderer<EntityDestroyerHa?, ModelDestroyerHa<EntityDestroyerHa?>?>(
        context,
        ModelDestroyerHa<EntityDestroyerHa?>(context.bakeLayer(ModelDestroyerHa.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityDestroyerHa?, ModelDestroyerHa<EntityDestroyerHa?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityDestroyerHa): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityDestroyerHa, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityDestroyerHa): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/destroyer_ha.png")
        private const val MODEL_SCALE = 0.34f
    }
}
