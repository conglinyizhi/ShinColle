package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelMidwayHime
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityMidwayHime

class RendererMidwayHime(context: EntityRendererProvider.Context) :
    MobRenderer<EntityMidwayHime?, ModelMidwayHime<EntityMidwayHime?>?>(
        context,
        ModelMidwayHime<EntityMidwayHime?>(context.bakeLayer(ModelMidwayHime.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityMidwayHime?, ModelMidwayHime<EntityMidwayHime?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityMidwayHime): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityMidwayHime, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityMidwayHime): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/midway_hime.png")
        private const val MODEL_SCALE = 0.34f
    }
}
