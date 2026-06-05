package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelCruiserTenryuu
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityCruiserTenryuu

class RendererCruiserTenryuu(context: EntityRendererProvider.Context) :
    MobRenderer<EntityCruiserTenryuu?, ModelCruiserTenryuu<EntityCruiserTenryuu?>?>(
        context,
        ModelCruiserTenryuu<EntityCruiserTenryuu?>(context.bakeLayer(ModelCruiserTenryuu.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(
            GenericGlowLayer<EntityCruiserTenryuu?, ModelCruiserTenryuu<EntityCruiserTenryuu?>?>(
                this,
                TEXTURE
            )
        )
    }

    override fun getTextureLocation(entity: EntityCruiserTenryuu): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityCruiserTenryuu, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityCruiserTenryuu): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/cruiser_tenryuu.png")
        private const val MODEL_SCALE = 0.34f
    }
}
