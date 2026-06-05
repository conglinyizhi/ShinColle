package org.trp.shincolle.client.renderer.item

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.model.geom.EntityModelSet
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.Shincolle
import org.trp.shincolle.client.model.ModelBlockDesk

class DeskItemRenderer(dispatcher: BlockEntityRenderDispatcher, modelSet: EntityModelSet) :
    BlockEntityWithoutLevelRenderer(dispatcher, modelSet) {
    private val model: ModelBlockDesk

    init {
        this.model = ModelBlockDesk(modelSet.bakeLayer(ModelBlockDesk.LAYER_LOCATION))
    }

    override fun renderByItem(
        stack: ItemStack, displayContext: ItemDisplayContext, poseStack: PoseStack,
        bufferSource: MultiBufferSource, packedLight: Int, packedOverlay: Int
    ) {
        poseStack.pushPose()
        poseStack.translate(0.5, 1.5, 0.5)
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f))

        val consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE))
        this.model.renderToBuffer(poseStack, consumer, packedLight, packedOverlay, -0x1)
        poseStack.popPose()
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/block/blockdesk.png")
    }
}
