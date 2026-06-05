package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelIsolatedHime
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityIsolatedHime

class RendererIsolatedHime(context: EntityRendererProvider.Context) :
    MobRenderer<EntityIsolatedHime?, ModelIsolatedHime<EntityIsolatedHime?>?>(
        context,
        ModelIsolatedHime<EntityIsolatedHime?>(context.bakeLayer(ModelIsolatedHime.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityIsolatedHime?, ModelIsolatedHime<EntityIsolatedHime?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityIsolatedHime): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityIsolatedHime, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityIsolatedHime): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    override fun getRenderType(
        entity: EntityIsolatedHime,
        bodyVisible: Boolean,
        translucent: Boolean,
        glowing: Boolean
    ): RenderType? {
        return RenderType.entityCutoutNoCull(getTextureLocation(entity))
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/isolated_hime.png")
        private const val MODEL_SCALE = 0.34f
    }
}
