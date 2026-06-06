package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelBBKirishima
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityBBKirishima

class RendererBBKirishima(context: EntityRendererProvider.Context) :
    MobRenderer<EntityBBKirishima, ModelBBKirishima<EntityBBKirishima>>(
        context,
        ModelBBKirishima<EntityBBKirishima>(context.bakeLayer(ModelBBKirishima.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityBBKirishima, ModelBBKirishima<EntityBBKirishima>>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityBBKirishima): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityBBKirishima, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityBBKirishima): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/bb_kirishima.png")
        private const val MODEL_SCALE = 0.34f
    }
}
