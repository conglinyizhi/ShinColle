package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelBBKongou
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityBBKongou

class RendererBBKongou(context: EntityRendererProvider.Context) :
    MobRenderer<EntityBBKongou?, ModelBBKongou<EntityBBKongou?>?>(
        context,
        ModelBBKongou<EntityBBKongou?>(context.bakeLayer(ModelBBKongou.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityBBKongou?, ModelBBKongou<EntityBBKongou?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityBBKongou): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityBBKongou, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityBBKongou): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/bb_kongou.png")
        private const val MODEL_SCALE = 0.34f
    }
}
