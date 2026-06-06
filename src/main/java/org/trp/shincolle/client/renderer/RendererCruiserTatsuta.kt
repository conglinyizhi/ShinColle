package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelCruiserTatsuta
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityCruiserTatsuta

class RendererCruiserTatsuta(context: EntityRendererProvider.Context) :
    MobRenderer<EntityCruiserTatsuta, ModelCruiserTatsuta<EntityCruiserTatsuta>>(
        context,
        ModelCruiserTatsuta<EntityCruiserTatsuta>(context.bakeLayer(ModelCruiserTatsuta.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(
            GenericGlowLayer<EntityCruiserTatsuta, ModelCruiserTatsuta<EntityCruiserTatsuta>>(
                this,
                TEXTURE
            )
        )
    }

    override fun getTextureLocation(entity: EntityCruiserTatsuta): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityCruiserTatsuta, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityCruiserTatsuta): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/cruiser_tatsuta.png")
        private const val MODEL_SCALE = 0.34f
    }
}
