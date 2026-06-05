package org.trp.shincolle.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.trp.shincolle.block.entity.WayPointBlockEntity
import org.trp.shincolle.init.ModBlockEntities
import org.trp.shincolle.item.TargetWrenchItem
import java.util.function.Function

class WayPointBlock : BaseEntityBlock(
    Properties.of()
        .strength(0.0f, 0.0f)
        .noOcclusion()
) {
    override fun codec(): MapCodec<out BaseEntityBlock?> {
        return CODEC
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WayPointBlockEntity(pos, state)
    }

    public override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE
    }

    public override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return Shapes.empty()
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        val stack = player.getItemInHand(player.getUsedItemHand())

        if (stack.getItem() is TargetWrenchItem && !player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                if (level.getBlockEntity(pos) is WayPointBlockEntity) {
                    if (wp.getOwnerUUID() != null && wp.getOwnerUUID() != player.getUUID()) {
                        player.displayClientMessage(Component.translatable("chat.shincolle.wrongowner"), true)
                        return InteractionResult.FAIL
                    }
                    wp.nextWpStayTime()
                    player.displayClientMessage(
                        Component.translatable("chat.shincolle.waypoint.setstaytime", wp.getStayTimeDisplay()),
                        true
                    )
                }
            }
            return InteractionResult.SUCCESS
        }

        return InteractionResult.PASS
    }

    override fun setPlacedBy(
        level: Level, pos: BlockPos, state: BlockState,
        placer: LivingEntity?, stack: ItemStack
    ) {
        super.setPlacedBy(level, pos, state, placer, stack)
        if (!level.isClientSide && placer is Player) {
            if (level.getBlockEntity(pos) is WayPointBlockEntity) {
                wp.setOwnerUUID(placer.getUUID())
                wp.setOwnerName(placer.getName().getString())
            }
        }
    }

    override fun canHarvestBlock(state: BlockState, level: BlockGetter, pos: BlockPos, player: Player): Boolean {
        if (player.hasPermissions(2)) return true
        if (level.getBlockEntity(pos) is WayPointBlockEntity) {
            if (wp.getOwnerUUID() == null) return true
            return wp.getOwnerUUID() == player.getUUID()
        }
        return false
    }

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        if (!level.isClientSide && canHarvestBlock(state, level, pos, player)) {
            val stack = ItemStack(this)
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false)
            }
        }
        return super.playerWillDestroy(level, pos, state, player)
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T?>
    ): BlockEntityTicker<T?>? {
        return createTickerHelper<WayPointBlockEntity?, T?>(
            type,
            ModBlockEntities.WAYPOINT.get(),
            BlockEntityTicker { level: Level?, pos: BlockPos?, state: BlockState?, be: WayPointBlockEntity? ->
                WayPointBlockEntity.Companion.tick(
                    level,
                    pos,
                    state,
                    be
                )
            })
    }

    companion object {
        val CODEC: MapCodec<WayPointBlock?> =
            simpleCodec<WayPointBlock?>(Function { properties: Properties? -> WayPointBlock() })
    }
}
