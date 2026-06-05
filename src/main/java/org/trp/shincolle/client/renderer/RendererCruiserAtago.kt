package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelCruiserAtago
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityCruiserAtago

class RendererCruiserAtago(context: EntityRendererProvider.Context) :
    MobRenderer<EntityCruiserAtago?, ModelCruiserAtago<EntityCruiserAtago?>?>(
        context,
        ModelCruiserAtago<EntityCruiserAtago?>(context.bakeLayer(ModelCruiserAtago.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityCruiserAtago?, ModelCruiserAtago<EntityCruiserAtago?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityCruiserAtago): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityCruiserAtago, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityCruiserAtago): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/cruiser_atago.png")
        private const val MODEL_SCALE = 0.34f
    }
}
