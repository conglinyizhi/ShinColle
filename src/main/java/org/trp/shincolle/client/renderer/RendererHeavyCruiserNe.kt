package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelHeavyCruiserNe
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityHeavyCruiserNe

class RendererHeavyCruiserNe(context: EntityRendererProvider.Context) :
    MobRenderer<EntityHeavyCruiserNe?, ModelHeavyCruiserNe<EntityHeavyCruiserNe?>?>(
        context,
        ModelHeavyCruiserNe<EntityHeavyCruiserNe?>(context.bakeLayer(ModelHeavyCruiserNe.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(
            GenericGlowLayer<EntityHeavyCruiserNe?, ModelHeavyCruiserNe<EntityHeavyCruiserNe?>?>(
                this,
                TEXTURE
            )
        )
    }

    override fun getTextureLocation(entity: EntityHeavyCruiserNe): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityHeavyCruiserNe, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityHeavyCruiserNe): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/heavy_cruiser_ne.png")
        private const val MODEL_SCALE = 0.34f
    }
}
