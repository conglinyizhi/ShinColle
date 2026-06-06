package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelHeavyCruiserRi
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityHeavyCruiserRi

class RendererHeavyCruiserRi(context: EntityRendererProvider.Context) :
    MobRenderer<EntityHeavyCruiserRi?, ModelHeavyCruiserRi<EntityHeavyCruiserRi?>?>(
        context,
        ModelHeavyCruiserRi<EntityHeavyCruiserRi>(context.bakeLayer(ModelHeavyCruiserRi.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(
            GenericGlowLayer<EntityHeavyCruiserRi?, ModelHeavyCruiserRi<EntityHeavyCruiserRi?>?>(
                this,
                TEXTURE
            )
        )
    }

    override fun getTextureLocation(entity: EntityHeavyCruiserRi): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityHeavyCruiserRi, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityHeavyCruiserRi): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/heavy_cruiser_ri.png")
        private const val MODEL_SCALE = 0.34f
    }
}
