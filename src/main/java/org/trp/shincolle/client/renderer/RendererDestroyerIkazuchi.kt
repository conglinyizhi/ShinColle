package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelDestroyerIkazuchi
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityDestroyerIkazuchi

class RendererDestroyerIkazuchi(context: EntityRendererProvider.Context) :
    MobRenderer<EntityDestroyerIkazuchi?, ModelDestroyerIkazuchi<EntityDestroyerIkazuchi?>?>(
        context,
        ModelDestroyerIkazuchi<EntityDestroyerIkazuchi>(context.bakeLayer(ModelDestroyerIkazuchi.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(
            GenericGlowLayer<EntityDestroyerIkazuchi?, ModelDestroyerIkazuchi<EntityDestroyerIkazuchi?>?>(
                this,
                TEXTURE
            )
        )
    }

    override fun getTextureLocation(entity: EntityDestroyerIkazuchi): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityDestroyerIkazuchi, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityDestroyerIkazuchi): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/destroyer_ikazuchi.png")
        private const val MODEL_SCALE = 0.34f
    }
}
