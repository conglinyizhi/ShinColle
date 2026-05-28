package org.trp.shincolle.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.trp.shincolle.block.entity.IWaypoint;
import org.trp.shincolle.network.C2SWaypointActionPayload;
import org.trp.shincolle.server.TargetProtectionService;

import java.util.List;

public class TargetWrenchItem extends Item {

    private static final String TAG_MARKED_X = "MarkedX";
    private static final String TAG_MARKED_Y = "MarkedY";
    private static final String TAG_MARKED_Z = "MarkedZ";

    public TargetWrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        BlockPos clickedPos = context.getClickedPos();
        var level = context.getLevel();
        var be = level.getBlockEntity(clickedPos);

        boolean isWaypoint = be instanceof IWaypoint;
        boolean isContainer = be instanceof BaseContainerBlockEntity || be instanceof org.trp.shincolle.block.entity.CraneBlockEntity;

        if (!isWaypoint && !isContainer) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("chat.shincolle.wrench.wrongtile"), true);
            }
            clearMarked(context.getItemInHand());
            return InteractionResult.FAIL;
        }

        ItemStack stack = context.getItemInHand();

        if (!hasMarked(stack)) {
            setMarked(stack, clickedPos);
            if (!level.isClientSide) {
                player.displayClientMessage(
                    Component.literal(ChatFormatting.AQUA + "Marked: " + clickedPos.getX() + " " + clickedPos.getY() + " " + clickedPos.getZ()),
                    true
                );
            }
            return InteractionResult.SUCCESS;
        }

        BlockPos markedPos = getMarked(stack);

        if (markedPos.equals(clickedPos)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("chat.shincolle.wrench.samepoint"), true);
            }
            clearMarked(stack);
            return InteractionResult.FAIL;
        }

        if (level.isClientSide) {
            PacketDistributor.sendToServer(new C2SWaypointActionPayload(
                2,
                markedPos.getX(), markedPos.getY(), markedPos.getZ(),
                clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()
            ));
        }
        clearMarked(stack);

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level level, Player player, net.minecraft.world.InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }

        if (!level.isClientSide) {
            showPlayerTargets(player);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (player.level().isClientSide || player.isShiftKeyDown()) {
            return false;
        }

        togglePlayerTarget(player, entity);
        return true;
    }

    private boolean hasMarked(ItemStack stack) {
        var tag = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if (tag == null) return false;
        return tag.copyTag().contains(TAG_MARKED_Y);
    }

    private BlockPos getMarked(ItemStack stack) {
        var tag = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if (tag == null) return BlockPos.ZERO;
        var nbt = tag.copyTag();
        return new BlockPos(nbt.getInt(TAG_MARKED_X), nbt.getInt(TAG_MARKED_Y), nbt.getInt(TAG_MARKED_Z));
    }

    private void setMarked(ItemStack stack, BlockPos pos) {
        stack.update(
            net.minecraft.core.component.DataComponents.CUSTOM_DATA,
            net.minecraft.world.item.component.CustomData.EMPTY,
            data -> data.update(tag -> {
                tag.putInt(TAG_MARKED_X, pos.getX());
                tag.putInt(TAG_MARKED_Y, pos.getY());
                tag.putInt(TAG_MARKED_Z, pos.getZ());
            })
        );
    }

    private void clearMarked(ItemStack stack) {
        var existing = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if (existing == null) return;
        var nbt = existing.copyTag();
        nbt.remove(TAG_MARKED_X);
        nbt.remove(TAG_MARKED_Y);
        nbt.remove(TAG_MARKED_Z);
        if (nbt.isEmpty()) {
            stack.remove(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        } else {
            stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(nbt));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("gui.shincolle.wrench1").withStyle(ChatFormatting.YELLOW));
        tooltipComponents.add(Component.translatable("gui.shincolle.wrench2").withStyle(ChatFormatting.YELLOW));
        tooltipComponents.add(Component.translatable("gui.shincolle.wrench3").withStyle(ChatFormatting.YELLOW));
        if (hasMarked(stack)) {
            BlockPos p = getMarked(stack);
            tooltipComponents.add(Component.literal(ChatFormatting.AQUA + "Marked: " + p.getX() + " " + p.getY() + " " + p.getZ()));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, net.minecraft.world.level.Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player) {
            boolean isHeld = player.getMainHandItem() == stack || player.getOffhandItem() == stack;
            
            if (!isHeld) {
                if (hasMarked(stack)) {
                    clearMarked(stack);
                }
            } else if (level.isClientSide && isSelected && hasMarked(stack)) {
                if (level.getGameTime() % 40 == 0) {
                    BlockPos pos = getMarked(stack);
                    player.displayClientMessage(
                        Component.literal(net.minecraft.ChatFormatting.AQUA + "Marked: " + pos.getX() + " " + pos.getY() + " " + pos.getZ()),
                        true
                    );
                }
            }
        }
    }

    private void toggleUnattackableTarget(Player player, Entity entity) {
        TargetProtectionService.toggleUnattackableTarget(player, entity);
    }

    private void showUnattackableTargets(Player player) {
        TargetProtectionService.showUnattackableTargets(player);
    }

    private void togglePlayerTarget(Player player, Entity entity) {
        TargetProtectionService.togglePlayerTarget(player, entity);
    }

    private void showPlayerTargets(Player player) {
        TargetProtectionService.showPlayerTargets(player);
    }
}
