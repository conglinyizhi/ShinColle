package org.trp.shincolle.item

import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.neoforged.neoforge.network.PacketDistributor
import org.trp.shincolle.block.entity.CraneBlockEntity
import org.trp.shincolle.block.entity.IWaypoint
import org.trp.shincolle.network.C2SWaypointActionPayload
import org.trp.shincolle.server.TargetProtectionService
import java.util.function.Consumer
import java.util.function.UnaryOperator

class TargetWrenchItem(properties: Properties) : Item(properties) {
    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player
        if (player == null) return InteractionResult.PASS

        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS
        }

        val clickedPos = context.clickedPos
        val level = context.level
        val be = level.getBlockEntity(clickedPos)

        val isWaypoint = be is IWaypoint
        val isContainer = be is BaseContainerBlockEntity || be is CraneBlockEntity

        if (!isWaypoint && !isContainer) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("chat.shincolle.wrench.wrongtile"), true)
            }
            clearMarked(context.itemInHand)
            return InteractionResult.FAIL
        }

        val stack = context.itemInHand

        if (!hasMarked(stack)) {
            setMarked(stack, clickedPos)
            if (!level.isClientSide) {
                player.displayClientMessage(
                    Component.literal(ChatFormatting.AQUA.toString() + "Marked: " + clickedPos.x + " " + clickedPos.y + " " + clickedPos.z),
                    true
                )
            }
            return InteractionResult.SUCCESS
        }

        val markedPos = getMarked(stack)

        if (markedPos == clickedPos) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("chat.shincolle.wrench.samepoint"), true)
            }
            clearMarked(stack)
            return InteractionResult.FAIL
        }

        if (level.isClientSide) {
            PacketDistributor.sendToServer(
                C2SWaypointActionPayload(
                    2,
                    markedPos.x, markedPos.y, markedPos.z,
                    clickedPos.x, clickedPos.y, clickedPos.z
                )
            )
        }
        clearMarked(stack)

        return InteractionResult.SUCCESS
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(hand)
        if (player.isShiftKeyDown()) {
            return InteractionResultHolder.pass<ItemStack?>(stack)
        }

        if (!level.isClientSide) {
            showPlayerTargets(player)
        }
        return InteractionResultHolder.sidedSuccess<ItemStack?>(stack, level.isClientSide)
    }

    override fun onLeftClickEntity(stack: ItemStack, player: Player, entity: Entity): Boolean {
        if (player.level().isClientSide || player.isShiftKeyDown()) {
            return false
        }

        togglePlayerTarget(player, entity)
        return true
    }

    private fun hasMarked(stack: ItemStack): Boolean {
        val tag = stack.get<CustomData?>(DataComponents.CUSTOM_DATA)
        if (tag == null) return false
        return tag.copyTag().contains(TAG_MARKED_Y)
    }

    private fun getMarked(stack: ItemStack): BlockPos {
        val tag = stack.get<CustomData?>(DataComponents.CUSTOM_DATA)
        if (tag == null) return BlockPos.ZERO
        val nbt = tag.copyTag()
        return BlockPos(nbt.getInt(TAG_MARKED_X), nbt.getInt(TAG_MARKED_Y), nbt.getInt(TAG_MARKED_Z))
    }

    private fun setMarked(stack: ItemStack, pos: BlockPos) {
        stack.update<CustomData?>(
            DataComponents.CUSTOM_DATA,
            CustomData.EMPTY,
            UnaryOperator { data: CustomData? ->
                data!!.update(Consumer { tag: CompoundTag? ->
                    tag!!.putInt(TAG_MARKED_X, pos.x)
                    tag.putInt(TAG_MARKED_Y, pos.y)
                    tag.putInt(TAG_MARKED_Z, pos.z)
                })
            }
        )
    }

    private fun clearMarked(stack: ItemStack) {
        val existing = stack.get<CustomData?>(DataComponents.CUSTOM_DATA)
        if (existing == null) return
        val nbt = existing.copyTag()
        nbt.remove(TAG_MARKED_X)
        nbt.remove(TAG_MARKED_Y)
        nbt.remove(TAG_MARKED_Z)
        if (nbt.isEmpty()) {
            stack.remove<CustomData?>(DataComponents.CUSTOM_DATA)
        } else {
            stack.set<CustomData?>(
                DataComponents.CUSTOM_DATA,
                CustomData.of(nbt)
            )
        }
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
        tooltipComponents.add(Component.translatable("gui.shincolle.wrench1").withStyle(ChatFormatting.YELLOW))
        tooltipComponents.add(Component.translatable("gui.shincolle.wrench2").withStyle(ChatFormatting.YELLOW))
        tooltipComponents.add(Component.translatable("gui.shincolle.wrench3").withStyle(ChatFormatting.YELLOW))
        if (hasMarked(stack)) {
            val p = getMarked(stack)
            tooltipComponents.add(Component.literal(ChatFormatting.AQUA.toString() + "Marked: " + p.x + " " + p.y + " " + p.z))
        }
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (entity is Player) {
            val isHeld = entity.mainHandItem == stack || entity.offhandItem == stack

            if (!isHeld) {
                if (hasMarked(stack)) {
                    clearMarked(stack)
                }
            } else if (level.isClientSide && isSelected && hasMarked(stack)) {
                if (level.gameTime % 40 == 0L) {
                    val pos = getMarked(stack)
                    entity.displayClientMessage(
                        Component.literal(ChatFormatting.AQUA.toString() + "Marked: " + pos.x + " " + pos.y + " " + pos.z),
                        true
                    )
                }
            }
        }
    }

    private fun toggleUnattackableTarget(player: Player?, entity: Entity?) {
        TargetProtectionService.toggleUnattackableTarget(player, entity)
    }

    private fun showUnattackableTargets(player: Player?) {
        TargetProtectionService.showUnattackableTargets(player)
    }

    private fun togglePlayerTarget(player: Player?, entity: Entity?) {
        TargetProtectionService.togglePlayerTarget(player, entity)
    }

    private fun showPlayerTargets(player: Player?) {
        TargetProtectionService.showPlayerTargets(player)
    }

    companion object {
        private const val TAG_MARKED_X = "MarkedX"
        private const val TAG_MARKED_Y = "MarkedY"
        private const val TAG_MARKED_Z = "MarkedZ"
    }
}
