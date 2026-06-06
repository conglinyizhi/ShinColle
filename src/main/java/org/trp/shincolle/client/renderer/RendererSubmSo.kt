package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelSubmSo
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntitySubmSo

class RendererSubmSo(context: EntityRendererProvider.Context) : MobRenderer<EntitySubmSo, ModelSubmSo<EntitySubmSo>>(
    context,
    ModelSubmSo<EntitySubmSo>(context.bakeLayer(ModelSubmSo.LAYER_LOCATION)),
    0.5f
) {
    init {
        this.addLayer(GenericGlowLayer<EntitySubmSo, ModelSubmSo<EntitySubmSo>>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntitySubmSo): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntitySubmSo, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntitySubmSo): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/subm_so.png")
        private const val MODEL_SCALE = 0.34f
    }
}
