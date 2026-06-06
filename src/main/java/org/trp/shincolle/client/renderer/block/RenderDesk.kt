package org.trp.shincolle.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.trp.shincolle.Shincolle
import org.trp.shincolle.block.DeskBlock
import org.trp.shincolle.block.entity.DeskBlockEntity
import org.trp.shincolle.client.model.ModelBlockDesk
import org.trp.shincolle.client.model.ModelBlockDeskLarge

class RenderDesk(context: BlockEntityRendererProvider.Context) : BlockEntityRenderer<DeskBlockEntity> {
    private val modelSingle: ModelBlockDesk
    private val modelDouble: ModelBlockDeskLarge

    init {
        this.modelSingle = ModelBlockDesk(context.bakeLayer(ModelBlockDesk.LAYER_LOCATION))
        this.modelDouble = ModelBlockDeskLarge(context.bakeLayer(ModelBlockDeskLarge.LAYER_LOCATION))
    }

    override fun render(
        blockEntity: DeskBlockEntity, partialTick: Float, poseStack: PoseStack, bufferSource: MultiBufferSource,
        packedLight: Int, packedOverlay: Int
    ) {
        val level = blockEntity.getLevel()
        if (level == null) {
            renderSingle(poseStack, bufferSource, packedLight, packedOverlay, 90.0f)
            return
        }

        val pos = blockEntity.getBlockPos()
        val state = blockEntity.getBlockState()
        if (!state.hasProperty<Direction?>(DeskBlock.FACING)) return

        val facing = state.getValue<Direction>(DeskBlock.FACING)
        val angle: Float = getYaw(facing)

        val dirRight = facing.getClockWise()
        val dirLeft = facing.getCounterClockWise()

        val rightPos = pos.relative(dirRight)
        val isRightAlreadyChained = this.canConnectTo(level, rightPos, dirRight, state)
        val connectToRight = this.canConnectTo(level, pos, dirRight, state) && !isRightAlreadyChained
        val connectToLeft = this.canConnectTo(level, pos, dirLeft, state)

        if (connectToRight) {
            return
        }

        val isDouble = connectToLeft && !isRightAlreadyChained

        poseStack.pushPose()
        poseStack.translate(0.5, 1.5, 0.5)
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f))
        poseStack.mulPose(Axis.YP.rotationDegrees(angle))

        val consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE))
        if (isDouble) {
            modelDouble.renderToBuffer(poseStack, consumer, packedLight, packedOverlay, -0x1)
        } else {
            modelSingle.renderToBuffer(poseStack, consumer, packedLight, packedOverlay, -0x1)
        }
        poseStack.popPose()
    }

    private fun renderSingle(
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int,
        angle: Float
    ) {
        poseStack.pushPose()
        poseStack.translate(0.5, 1.5, 0.5)
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f))
        poseStack.mulPose(Axis.YP.rotationDegrees(angle))

        val consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE))
        modelSingle.renderToBuffer(poseStack, consumer, packedLight, packedOverlay, -0x1)
        poseStack.popPose()
    }

    private fun canConnectTo(level: Level, pos: BlockPos, direction: Direction, requiredState: BlockState): Boolean {
        val neighborPos = pos.relative(direction)
        val neighborState = level.getBlockState(neighborPos)
        if (neighborState.`is`(requiredState.getBlock())) {
            if (neighborState.hasProperty<Direction?>(DeskBlock.FACING) && requiredState.hasProperty<Direction?>(
                    DeskBlock.FACING
                )
            ) {
                return neighborState.getValue<Direction?>(DeskBlock.FACING) == requiredState.getValue<Direction?>(
                    DeskBlock.FACING
                )
            }
        }
        return false
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/block/blockdesk.png")

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
