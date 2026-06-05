package org.trp.shincolle.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.trp.shincolle.block.entity.VolCoreBlockEntity
import org.trp.shincolle.init.ModBlockEntities
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.ToIntFunction

class VolCoreBlock : BaseEntityBlock(
    Properties.of()
        .strength(6.0f, 600.0f)
        .requiresCorrectToolForDrops()
        .sound(SoundType.METAL)
        .lightLevel(ToIntFunction { state: BlockState? -> 15 })
) {
    override fun codec(): MapCodec<out BaseEntityBlock?> {
        return CODEC
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return VolCoreBlockEntity(pos, state)
    }

    public override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
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
        if (blockEntity is VolCoreBlockEntity && player is ServerPlayer) {
            player.openMenu(blockEntity, Consumer { buffer: RegistryFriendlyByteBuf? ->
                buffer!!.writeBlockPos(pos)
            })
            return InteractionResult.CONSUME
        }
        return InteractionResult.PASS
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T?>
    ): BlockEntityTicker<T?>? {
        if (level.isClientSide) return null
        return createTickerHelper<VolCoreBlockEntity?, T?>(
            type,
            ModBlockEntities.VOL_CORE.get(),
            BlockEntityTicker { level: Level?, pos: BlockPos?, state: BlockState?, blockEntity: VolCoreBlockEntity? ->
                VolCoreBlockEntity.Companion.serverTick(
                    level,
                    pos,
                    state,
                    blockEntity
                )
            })
    }

    companion object {
        val CODEC: MapCodec<VolCoreBlock?> =
            simpleCodec<VolCoreBlock?>(Function { properties: Properties? -> VolCoreBlock() })
    }
}
