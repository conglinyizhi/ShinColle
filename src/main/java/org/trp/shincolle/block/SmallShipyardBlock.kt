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
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import org.trp.shincolle.block.entity.SmallShipyardBlockEntity
import org.trp.shincolle.init.ModBlockEntities
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.ToIntFunction

class SmallShipyardBlock : BaseEntityBlock(
    Block.Properties.of()
        .strength(10.0f)
        .lightLevel(ToIntFunction { state: BlockState? -> if (state!!.getValue<Boolean?>(ACTIVE)) 8 else 4 })
        .noOcclusion()
) {
    init {
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue<Direction?, Direction?>(FACING, Direction.NORTH)
                .setValue<Boolean?, Boolean?>(ACTIVE, false)
        )
    }

    override fun codec(): MapCodec<out BaseEntityBlock?> {
        return CODEC
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return SmallShipyardBlockEntity(pos, state)
    }

    public override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.ENTITYBLOCK_ANIMATED
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(FACING, ACTIVE)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return this.defaultBlockState()
            .setValue<Direction?, Direction?>(FACING, context.getHorizontalDirection().getOpposite())
            .setValue<Boolean?, Boolean?>(ACTIVE, false)
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
        if (blockEntity is SmallShipyardBlockEntity && player is ServerPlayer) {
            player.openMenu(blockEntity, Consumer { buffer: RegistryFriendlyByteBuf? -> buffer!!.writeBlockPos(pos) })
            return InteractionResult.CONSUME
        }
        return InteractionResult.PASS
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T?>
    ): BlockEntityTicker<T?>? {
        if (level.isClientSide) {
            return null
        }
        return createTickerHelper<SmallShipyardBlockEntity?, T?>(
            blockEntityType,
            ModBlockEntities.SMALL_SHIPYARD.get(),
            BlockEntityTicker { level: Level?, pos: BlockPos?, state: BlockState?, blockEntity: SmallShipyardBlockEntity? ->
                SmallShipyardBlockEntity.Companion.serverTick(
                    level,
                    pos,
                    state,
                    blockEntity
                )
            })
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean
    ) {
        if (!state.`is`(newState.getBlock())) {
            val blockEntity = level.getBlockEntity(pos)
            if (blockEntity is SmallShipyardBlockEntity) {
                blockEntity.dropContents()
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    companion object {
        val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
        val ACTIVE: BooleanProperty = BooleanProperty.create("active")
        private val CODEC: MapCodec<SmallShipyardBlock?> =
            simpleCodec<SmallShipyardBlock?>(Function { properties: Properties? -> SmallShipyardBlock() })
    }
}
