package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelNorthernHime
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityNorthernHime

class RendererNorthernHime(context: EntityRendererProvider.Context) :
    MobRenderer<EntityNorthernHime?, ModelNorthernHime<EntityNorthernHime?>?>(
        context,
        ModelNorthernHime<EntityNorthernHime?>(context.bakeLayer(ModelNorthernHime.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityNorthernHime?, ModelNorthernHime<EntityNorthernHime?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityNorthernHime): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityNorthernHime, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityNorthernHime): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/northern_hime.png")
        private const val MODEL_SCALE = 0.34f
    }
}
