package org.trp.shincolle.client.renderer.layer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import org.trp.shincolle.client.model.IGlowableModel
import org.trp.shincolle.client.renderer.ShincolleRenderTypes.Companion.getFlatGlow

@Suppress("UNCHECKED_CAST")
class GenericGlowLayer<T : LivingEntity, M : EntityModel<T>>(
    renderer: RenderLayerParent<T, out EntityModel<T>>,
    private val glowTexture: ResourceLocation
) : RenderLayer<T, M>(renderer as RenderLayerParent<T, M>) {
    override fun render(
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTick: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        if (entity.isInvisible && entity.isInvisibleTo(Minecraft.getInstance().player)) {
            return
        }

        val vertexConsumer = bufferSource.getBuffer(getFlatGlow(this.glowTexture))

        var color = -0x1
        if (entity.isInvisible) {
            color = 0x26FFFFFF
        }

        val parentModel = this.parentModel
        if (parentModel is IGlowableModel) {
            parentModel.renderGlow(
                poseStack,
                vertexConsumer,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                color
            )
        }
    }
}
