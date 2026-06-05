package org.trp.shincolle.item

import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ItemFrame
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomData
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract
import org.trp.shincolle.entity.base.EntityShipBase
import java.util.function.Consumer
import java.util.function.UnaryOperator
import kotlin.math.min

class DebugInspectorItem(properties: Properties) : Item(properties) {
    override fun isFoil(stack: ItemStack): Boolean {
        return true
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
        tooltipComponents.add(
            Component.translatable("item.shincolle.debug_inspector.desc").withStyle(ChatFormatting.AQUA)
        )
        tooltipComponents.add(
            Component.translatable("item.shincolle.debug_inspector.desc2").withStyle(ChatFormatting.GRAY)
        )

        val customData = stack.get<CustomData?>(DataComponents.CUSTOM_DATA)
        if (customData == null) {
            return
        }

        val tag = customData.copyTag()
        val repairCount = tag.getInt(TAG_BUCKET_REPAIR_COUNT)
        if (repairCount > 0) {
            tooltipComponents.add(
                Component.translatable("item.shincolle.debug_inspector.bucket_count", repairCount)
                    .withStyle(ChatFormatting.GOLD)
            )
            val shipName = tag.getString(TAG_BUCKET_REPAIR_SHIP)
            if (!shipName.isEmpty()) {
                tooltipComponents.add(
                    Component.translatable("item.shincolle.debug_inspector.bucket_ship", shipName)
                        .withStyle(ChatFormatting.DARK_AQUA)
                )
            }
            val gameTime = tag.getLong(TAG_BUCKET_REPAIR_GAME_TIME)
            if (gameTime > 0L) {
                tooltipComponents.add(
                    Component.translatable("item.shincolle.debug_inspector.bucket_time", gameTime)
                        .withStyle(ChatFormatting.DARK_GRAY)
                )
            }
        }
    }

    override fun interactLivingEntity(
        stack: ItemStack,
        player: Player,
        target: LivingEntity,
        hand: InteractionHand
    ): InteractionResult {
        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS
        }
        if (target is EntityShipBase) {
            inspectShip(player as ServerPlayer, target)
            return InteractionResult.SUCCESS
        }
        return InteractionResult.PASS
    }

    companion object {
        private const val MAX_CHAT_LENGTH = 8000
        private const val TAG_BUCKET_REPAIR_COUNT = "BucketRepairCount"
        private const val TAG_BUCKET_REPAIR_GAME_TIME = "BucketRepairGameTime"
        private const val TAG_BUCKET_REPAIR_SHIP = "BucketRepairShip"

        @JvmStatic
        fun creativeInfiniteLabel(): Component {
            return Component.translatable("gui.shincolle.creative_infinite").withStyle(ChatFormatting.GOLD)
        }

        @JvmStatic
        fun markBucketRepairTriggered(stack: ItemStack, ship: EntityShipBase) {
            stack.update<CustomData?>(DataComponents.CUSTOM_DATA, CustomData.EMPTY, UnaryOperator { data: CustomData? ->
                data!!.update(
                    Consumer { tag: CompoundTag? ->
                        tag!!.putInt(TAG_BUCKET_REPAIR_COUNT, tag.getInt(TAG_BUCKET_REPAIR_COUNT) + 1)
                        tag.putLong(TAG_BUCKET_REPAIR_GAME_TIME, ship.level().getGameTime())
                        tag.putString(TAG_BUCKET_REPAIR_SHIP, ship.getName().getString())
                    })
            })
        }

        @JvmStatic
        fun handleItemFrameInteract(event: EntityInteract) {
            if (event.getLevel().isClientSide) return
            val player = event.getEntity()
            val held = event.getItemStack()
            if (held.isEmpty() || held.getItem() !is DebugInspectorItem) return
            if (event.getTarget() !is ItemFrame) return

            event.setCanceled(true)

            val frameStack: ItemStack = frame.getItem()
            if (frameStack.isEmpty()) {
                player.displayClientMessage(Component.literal("Item frame is empty"), false)
                return
            }

            inspectItemStack(player as ServerPlayer, frameStack)
        }

        private fun inspectShip(player: ServerPlayer, ship: EntityShipBase) {
            val sb = StringBuilder()
            sb.append("=== Ship Debug Info ===\n")
            sb.append("Type: ").append(ship.getType().builtInRegistryHolder().key().location()).append("\n")
            sb.append("UUID: ").append(ship.getUUID()).append("\n")
            sb.append("Position: ").append(formatPos(ship)).append("\n")
            sb.append("Health: ").append(String.format("%.1f / %.1f", ship.getHealth(), ship.getMaxHealth()))
                .append("\n")
            sb.append("Owner: ").append(if (ship.getOwnerUUID() != null) ship.getOwnerUUID() else "none").append("\n")
            sb.append("Tame: ").append(ship.isTame()).append("\n")
            sb.append("Level: ").append(ship.getLevel()).append("\n")
            sb.append("ShipKills: ").append(ship.getShipKills()).append("\n")
            sb.append("Fuel: ").append(ship.getFuel()).append("\n")
            sb.append("Ammo L/H: ").append(ship.getAmmoLight()).append(" / ").append(ship.getAmmoHeavy()).append("\n")
            sb.append("Morale: ").append(ship.getMorale()).append("\n")
            sb.append("Married: ").append(ship.isStateMarried()).append("\n")

            val target: Entity? = ship.getTarget()
            if (target != null) {
                sb.append("Target: ").append(target.getType().builtInRegistryHolder().key().location()).append(" ")
                    .append(target.getUUID()).append("\n")
            } else {
                sb.append("Target: none\n")
            }

            for (i in 0..7) {
                sb.append("Emotion[").append(i).append("]: ").append(ship.getStateEmotion(i)).append("\n")
            }
            for (i in 0..15) {
                val v = ship.getStateMinor(i)
                if (v != 0) {
                    sb.append("Minor[").append(i).append("]: ").append(v).append("\n")
                }
            }

            val nav = ship.getNavigation()
            if (nav != null) {
                sb.append("Navigation: ").append(if (nav.isDone()) "idle" else "moving").append("\n")
                if (nav.getPath() != null) {
                    sb.append("Path nodes: ").append(nav.getPath()!!.getNodeCount()).append("\n")
                }
            }

            sb.append("OnGround: ").append(ship.onGround()).append("\n")
            sb.append("InWater: ").append(ship.isInWater()).append("\n")
            sb.append("Passengers: ").append(ship.getPassengers().size).append("\n")

            sendChatWithCopy(player, sb.toString())
        }

        private fun inspectItemStack(player: ServerPlayer, stack: ItemStack) {
            val sb = StringBuilder()
            sb.append("=== Item Debug Info ===\n")
            sb.append("Item: ").append(stack.getItem()).append("\n")
            sb.append("Count: ").append(stack.getCount()).append("\n")

            val components = stack.getComponents()
            sb.append("Components (").append(components.size()).append("):\n")
            for (entry in components) {
                sb.append("  ").append(entry.type()).append(" = ").append(entry.value()).append("\n")
            }
            val customData = stack.get<CustomData?>(DataComponents.CUSTOM_DATA)
            if (customData != null) {
                sb.append("CustomData: ").append(customData.copyTag()).append("\n")
            } else {
                sb.append("CustomData: null\n")
            }

            sendChatWithCopy(player, sb.toString())
        }

        private fun sendChatWithCopy(player: ServerPlayer, text: String) {
            var remaining = text
            var part = 0
            while (!remaining.isEmpty()) {
                val end = min(remaining.length, MAX_CHAT_LENGTH)
                val chunk = remaining.substring(0, end)
                remaining = remaining.substring(end)

                val msg = Component.literal(chunk)

                if (part == 0) {
                    val copyHint = Component.literal(" [COPY]")
                        .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                        .withStyle(UnaryOperator { style: Style? ->
                            style!!
                                .withClickEvent(ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text))
                                .withHoverEvent(
                                    HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        Component.literal("Click to copy all debug info")
                                    )
                                )
                        })
                    msg.append(copyHint)
                }

                player.sendSystemMessage(msg)
                part++
            }
        }

        private fun formatPos(entity: Entity): String {
            return String.format(
                "(%.2f, %.2f, %.2f) dim=%s",
                entity.getX(), entity.getY(), entity.getZ(),
                entity.level().dimension().location()
            )
        }
    }
}
