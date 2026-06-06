package org.trp.shincolle.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import org.trp.shincolle.block.entity.DeskBlockEntity
import java.util.function.Consumer
import java.util.function.Function

class DeskBlock : BaseEntityBlock(
    Properties.of()
        .strength(1.0f)
        .explosionResistance(60.0f)
        .noOcclusion()
) {
    init {
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue<Direction?, Direction?>(FACING, Direction.NORTH)
        )
    }

    override fun codec(): MapCodec<out BaseEntityBlock?> {
        return CODEC
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return DeskBlockEntity(pos, state)
    }

    public override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.ENTITYBLOCK_ANIMATED
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return this.defaultBlockState()
            .setValue<Direction?, Direction?>(FACING, context.getHorizontalDirection().getOpposite())
    }

    public override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue<Direction?, Direction?>(FACING, rotation.rotate(state.getValue<Direction?>(FACING)))
    }

    public override fun mirror(state: BlockState, mirror: Mirror): BlockState {
        return state.rotate(mirror.getRotation(state.getValue<Direction?>(FACING)))
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }

        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is DeskBlockEntity && player is ServerPlayer) {
            player.openMenu(blockEntity, Consumer { buffer: RegistryFriendlyByteBuf? ->
                buffer!!.writeInt(0)
                buffer.writeBlockPos(pos)
            })
            return InteractionResult.CONSUME
        }
        return InteractionResult.PASS
    }

    companion object {
        val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
        private val CODEC: MapCodec<DeskBlock?> =
            simpleCodec<DeskBlock?>(Function { properties: Properties? -> DeskBlock() })
    }
}
