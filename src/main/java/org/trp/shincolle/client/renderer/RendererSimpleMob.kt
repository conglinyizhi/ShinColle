package org.trp.shincolle.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob
import org.trp.shincolle.client.model.IGlowableModel
import org.trp.shincolle.client.renderer.layer.GenericGlowLayer
import org.trp.shincolle.entity.EntityAircraftBase
import org.trp.shincolle.entity.base.EntityShipBase

@Suppress("UNCHECKED_CAST")
open class RendererSimpleMob<T : Mob, M : EntityModel<T>>(
    context: EntityRendererProvider.Context,
    model: M,
    shadowSize: Float,
    private val modelScale: Float,
    private val texture: ResourceLocation
) : MobRenderer<T, M>(context, model, shadowSize) {
    init {
        if (model is IGlowableModel) {
            @Suppress("UNCHECKED_CAST")
            this.addLayer(GenericGlowLayer<T, M>(this, texture))
        }
    }

    override fun getTextureLocation(entity: T): ResourceLocation {
        return texture
    }

    override fun scale(entity: T, poseStack: PoseStack, partialTickTime: Float) {
        var s = LegacyScale.getScale(entity, this.model)

        if (s == 0.34f && this.modelScale != 0.34f) {
            s = this.modelScale
        }

        if (s == 0.34f && entity is EntityAircraftBase) {
            s = if (entity.isMissionLightAircraft) 0.5f else 0.6f
        }

        poseStack.scale(s, s, s)
    }

    override fun getFlipDegrees(entity: T): Float {
        if (entity is EntityShipBase && entity.isInDeadPose) {
            return 0.0f
        }
        return super.getFlipDegrees(entity)
    }
}
