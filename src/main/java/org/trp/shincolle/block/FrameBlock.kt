package org.trp.shincolle.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.Function

class FrameBlock : Block(
    Properties.of()
        .strength(0.1f, 40.0f)
        .sound(SoundType.METAL)
        .noOcclusion()
) {
    init {
        this.registerDefaultState(this.stateDefinition.any().setValue<Direction?, Direction?>(FACING, Direction.NORTH))
    }

    override fun codec(): MapCodec<out Block?> {
        return CODEC
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

    public override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    public override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return FRAME_SHAPE
    }

    public override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return FRAME_SHAPE
    }

    public override fun getVisualShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return Shapes.empty()
    }

    fun isLadder(state: BlockState?, level: BlockGetter?, pos: BlockPos?, entity: LivingEntity?): Boolean {
        return true
    }

    public override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        entity.resetFallDistance()
        if (entity.getDeltaMovement().y < -0.1) {
            entity.setDeltaMovement(entity.getDeltaMovement().x, -0.1, entity.getDeltaMovement().z)
        }
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
    }

    companion object {
        val CODEC: MapCodec<FrameBlock?> =
            simpleCodec<FrameBlock?>(Function { properties: Properties? -> FrameBlock() })
        val FACING: DirectionProperty = HorizontalDirectionalBlock.FACING
        private val FRAME_SHAPE: VoxelShape = box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0)
    }
}
