package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelDestroyerAkatsuki
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityDestroyerAkatsuki

class RendererDestroyerAkatsuki(context: EntityRendererProvider.Context) :
    MobRenderer<EntityDestroyerAkatsuki?, ModelDestroyerAkatsuki<EntityDestroyerAkatsuki?>?>(
        context,
        ModelDestroyerAkatsuki<EntityDestroyerAkatsuki>(context.bakeLayer(ModelDestroyerAkatsuki.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(
            GenericGlowLayer<EntityDestroyerAkatsuki?, ModelDestroyerAkatsuki<EntityDestroyerAkatsuki?>?>(
                this,
                TEXTURE
            )
        )
    }

    override fun getRenderType(
        entity: EntityDestroyerAkatsuki,
        bodyVisible: Boolean,
        translucent: Boolean,
        glowing: Boolean
    ): RenderType? {
        return RenderType.entityTranslucent(getTextureLocation(entity))
    }

    override fun getTextureLocation(entity: EntityDestroyerAkatsuki): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityDestroyerAkatsuki, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityDestroyerAkatsuki): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/destroyer_akatsuki.png")
        private const val MODEL_SCALE = 0.34f
    }
}
