package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelAirfieldHime
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityAirfieldHime

class RendererAirfieldHime(context: EntityRendererProvider.Context) :
    MobRenderer<EntityAirfieldHime?, ModelAirfieldHime<EntityAirfieldHime?>?>(
        context,
        ModelAirfieldHime<EntityAirfieldHime?>(context.bakeLayer(ModelAirfieldHime.LAYER_LOCATION)),
        0.5f
    ) {
    init {
        this.addLayer(GenericGlowLayer<EntityAirfieldHime?, ModelAirfieldHime<EntityAirfieldHime?>?>(this, TEXTURE))
    }

    override fun getTextureLocation(entity: EntityAirfieldHime): ResourceLocation {
        return TEXTURE
    }

    override fun scale(entity: EntityAirfieldHime, poseStack: PoseStack, partialTickTime: Float) {
        val s = LegacyScale.getScale(entity, this.model)
        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: EntityAirfieldHime): Float {
        if (entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/entity/airfield_hime.png")
        private const val MODEL_SCALE = 0.34f
    }
}
