package org.trp.shincolle.item

import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EmotionParticleType
import org.trp.shincolle.entity.base.EntityShipBase
import java.util.function.Consumer
import java.util.function.UnaryOperator

class MarriageRingItem(properties: Properties) : Item(properties) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(hand)
        if (!level.isClientSide) {
            val next: Boolean = !isActive(stack)
            setActive(stack, next)
            player.displayClientMessage(
                Component.translatable(if (next) "gui.shincolle.ring.on" else "gui.shincolle.ring.off"),
                true
            )
        }
        return InteractionResultHolder.sidedSuccess<ItemStack?>(stack, level.isClientSide)
    }

    override fun isFoil(stack: ItemStack): Boolean {
        return isActive(stack) || super.isFoil(stack)
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (!level.isClientSide && isActive(stack) && entity is Player) {
            if (entity.tickCount % 64 == 0) {
                applyAuraToNearbyShips(entity, level)
            }
        }
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
        tooltipComponents.add(
            Component.translatable(if (isActive(stack)) "gui.shincolle.ring.on" else "gui.shincolle.ring.off")
                .withStyle(if (isActive(stack)) ChatFormatting.AQUA else ChatFormatting.GRAY)
        )
    }

    private fun applyAuraToNearbyShips(player: Player, level: Level) {
        val area = player.getBoundingBox().inflate(6.0, 5.0, 6.0)
        val nearbyShips = level.getEntitiesOfClass<EntityShipBase?>(EntityShipBase::class.java, area)

        for (ship in nearbyShips) {
            if (ship == null || !ship.isAlive() || !ship.isTame() || !ship.isOwnedBy(player)) {
                continue
            }

            ship.setEmotionPrimary(EntityShipBase.EMOTION_HAPPY)
            if (ship.getRandom().nextInt(5) == 0) {
                ship.applyParticleEmotion(EmotionParticleType.HEART)
            }
        }
    }

    companion object {
        private const val TAG_ACTIVE = "LegacyActive"

        fun isActive(stack: ItemStack): Boolean {
            val customData = stack.get<CustomData?>(DataComponents.CUSTOM_DATA)
            return customData != null && customData.copyTag().getBoolean(TAG_ACTIVE)
        }

        private fun setActive(stack: ItemStack, active: Boolean) {
            stack.update<CustomData?>(
                DataComponents.CUSTOM_DATA, CustomData.EMPTY,
                UnaryOperator { data: CustomData? ->
                    data!!.update(Consumer { tag: CompoundTag? ->
                        tag!!.putBoolean(
                            TAG_ACTIVE,
                            active
                        )
                    })
                })
        }
    }
}
