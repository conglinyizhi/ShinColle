package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelDestroyerI
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityDestroyerI

class RendererDestroyerI(context: EntityRendererProvider.Context) :
    MobRenderer<EntityDestroyerI, ModelDestroyerI<EntityDestroyerI>>(
        context,
        ModelDestroyerI<EntityDestroyerI>(context.bakeLayer(ModelDestroyerI.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityDestroyerI, ModelDestroyerI<EntityDestroyerI>>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityDestroyerI): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityDestroyerI, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityDestroyerI): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/destroyer_i.png")
        private const val MODEL_SCALE = 0.34f
    }
}
