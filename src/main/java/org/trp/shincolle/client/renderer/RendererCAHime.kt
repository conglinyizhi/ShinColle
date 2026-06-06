package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelCAHime
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityCAHime

class RendererCAHime(context: EntityRendererProvider.Context) : MobRenderer<EntityCAHime?, ModelCAHime<EntityCAHime?>?>(
    context,
    ModelCAHime<EntityCAHime>(context.bakeLayer(ModelCAHime.LAYER_LOCATION)),
    0.5f
) {
    init {
        this.addLayer(GenericGlowLayer<EntityCAHime?, ModelCAHime<EntityCAHime?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityCAHime): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityCAHime, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityCAHime): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/ca_hime.png")
        private const val MODEL_SCALE = 0.34f
    }
}
