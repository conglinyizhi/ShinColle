package org.trp.shincolle.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.BlockHitResult
import org.trp.shincolle.block.entity.CraneBlockEntity
import org.trp.shincolle.init.ModBlockEntities
import java.util.function.Function

class CraneBlock : BaseEntityBlock(
    Properties.of()
        .strength(1.0f, 10.0f)
        .sound(SoundType.METAL)
        .noOcclusion()
) {
    init {
        this.registerDefaultState(this.stateDefinition.any().setValue<Boolean?, Boolean?>(POWERED, false))
    }

    override fun codec(): MapCodec<out BaseEntityBlock?> {
        return CODEC
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return CraneBlockEntity(pos, state)
    }

    public override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(POWERED)
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
        if (blockEntity is CraneBlockEntity && player is ServerPlayer) {
            if (blockEntity.ownerUUID == null) {
                blockEntity.ownerUUID = player.uuid
                blockEntity.ownerName = player.name.string
            }
            player.openMenu(blockEntity, pos)
            return InteractionResult.CONSUME
        }
        return InteractionResult.PASS
    }

    public override fun getSignal(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int {
        return if (state.getValue<Boolean?>(POWERED)) 15 else 0
    }

    public override fun getDirectSignal(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        direction: Direction
    ): Int {
        return if (state.getValue<Boolean?>(POWERED)) 15 else 0
    }

    public override fun isSignalSource(state: BlockState): Boolean {
        return true
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T?>
    ): BlockEntityTicker<T?>? {
        return createTickerHelper<CraneBlockEntity?, T?>(
            type,
            ModBlockEntities.CRANE.get(),
            BlockEntityTicker { level: Level?, pos: BlockPos?, state: BlockState?, be: CraneBlockEntity? ->
                if (level != null && pos != null && state != null && be != null) {
                    CraneBlockEntity.Companion.tick(
                        level,
                        pos,
                        state,
                        be
                    )
                }
            })
    }

    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        super.setPlacedBy(level, pos, state, placer, stack)
        if (!level.isClientSide && placer is Player) {
            val crane = level.getBlockEntity(pos)
            if (crane is CraneBlockEntity) {
                crane.ownerUUID = placer.uuid
                crane.ownerName = placer.name.string
            }
        }
    }

    companion object {
        val CODEC: MapCodec<CraneBlock?> =
            simpleCodec<CraneBlock?>(Function { properties: Properties? -> CraneBlock() })
        val POWERED: BooleanProperty = BlockStateProperties.POWERED
    }
}
