package org.trp.shincolle.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.trp.shincolle.block.entity.WayPointBlockEntity;
import org.trp.shincolle.init.ModBlockEntities;
import org.trp.shincolle.item.TargetWrenchItem;

import javax.annotation.Nullable;

public class WayPointBlock extends BaseEntityBlock {

    public static final MapCodec<WayPointBlock> CODEC = simpleCodec(properties -> new WayPointBlock());

    public WayPointBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(0.0F, 0.0F)
                .noOcclusion());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WayPointBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(player.getUsedItemHand());
        
        if (stack.getItem() instanceof TargetWrenchItem && !player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                if (level.getBlockEntity(pos) instanceof WayPointBlockEntity wp) {
                    if (wp.getOwnerUUID() != null && !wp.getOwnerUUID().equals(player.getUUID())) {
                        player.displayClientMessage(Component.translatable("chat.shincolle.wrongowner"), true);
                        return InteractionResult.FAIL;
                    }
                    wp.nextWpStayTime();
                    player.displayClientMessage(
                        Component.translatable("chat.shincolle.waypoint.setstaytime", wp.getStayTimeDisplay()),
                        true
                    );
                }
            }
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                             @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && placer instanceof Player player) {
            if (level.getBlockEntity(pos) instanceof WayPointBlockEntity wp) {
                wp.setOwnerUUID(player.getUUID());
                wp.setOwnerName(player.getName().getString());
            }
        }
    }

    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        if (player.hasPermissions(2)) return true;
        if (level.getBlockEntity(pos) instanceof WayPointBlockEntity wp) {
            if (wp.getOwnerUUID() == null) return true;
            return wp.getOwnerUUID().equals(player.getUUID());
        }
        return false;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && canHarvestBlock(state, level, pos, player)) {
            var stack = new ItemStack(this);
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.WAYPOINT.get(), WayPointBlockEntity::tick);
    }
}
