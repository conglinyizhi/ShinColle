package org.trp.shincolle.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import org.trp.shincolle.Shincolle
import org.trp.shincolle.block.SmallShipyardBlock
import org.trp.shincolle.block.entity.SmallShipyardBlockEntity
import org.trp.shincolle.client.model.ModelSmallShipyard

class RenderSmallShipyard(context: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<SmallShipyardBlockEntity> {
    private val model: ModelSmallShipyard

    init {
        this.model = ModelSmallShipyard(context.bakeLayer(ModelSmallShipyard.LAYER_LOCATION))
    }

    override fun render(
        blockEntity: SmallShipyardBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val state = blockEntity.getBlockState()
        val facing = if (state.hasProperty<Direction?>(SmallShipyardBlock.FACING))
            state.getValue<Direction>(SmallShipyardBlock.FACING)
        else
            Direction.NORTH
        val isActive =
            state.hasProperty<Boolean?>(SmallShipyardBlock.ACTIVE) && state.getValue<Boolean?>(SmallShipyardBlock.ACTIVE)
        val texture: ResourceLocation = if (isActive) TEXTURE_ON else TEXTURE_OFF

        poseStack.pushPose()
        poseStack.translate(0.5, 1.5, 0.5)
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f))
        poseStack.mulPose(Axis.YP.rotationDegrees(getYaw(facing)))

        val consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture))
        model.renderToBuffer(poseStack, consumer, packedLight, packedOverlay, -0x1)
        poseStack.popPose()
    }

    companion object {
        private val TEXTURE_ON: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/block/blocksmallshipyardon.png")
        private val TEXTURE_OFF: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/block/blocksmallshipyardoff.png")

        private fun getYaw(direction: Direction): Float {
            return when (direction) {
                Direction.EAST -> 90.0f
                Direction.SOUTH -> 180.0f
                Direction.WEST -> -90.0f
                else -> 0.0f
            }
        }
    }
}
