package org.trp.shincolle.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.trp.shincolle.block.entity.VolCoreBlockEntity;
import org.trp.shincolle.init.ModBlockEntities;

import javax.annotation.Nullable;

public class VolCoreBlock extends BaseEntityBlock {
    public static final MapCodec<VolCoreBlock> CODEC = simpleCodec(properties -> new VolCoreBlock());

    public VolCoreBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(6.0F, 600.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL)
                .lightLevel(state -> 15));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VolCoreBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof VolCoreBlockEntity volCore && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(volCore, buffer -> {
                buffer.writeBlockPos(pos);
            });
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return createTickerHelper(type, ModBlockEntities.VOL_CORE.get(), VolCoreBlockEntity::serverTick);
    }
}
