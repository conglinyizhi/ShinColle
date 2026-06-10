package org.trp.shincolle.item

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityShipBase
import kotlin.math.max

class TrainingBookItem(properties: Properties) : Item(properties) {
    override fun interactLivingEntity(
        stack: ItemStack,
        player: Player,
        interactionTarget: LivingEntity,
        usedHand: InteractionHand
    ): InteractionResult {
        if (interactionTarget !is EntityShipBase) {
            return InteractionResult.PASS
        }

        if (!interactionTarget.isTame || !interactionTarget.isOwnedBy(player)) {
            return InteractionResult.PASS
        }

        if (player.level().isClientSide) {
            return InteractionResult.sidedSuccess(true)
        }

        val minLevelGain = Config.trainingBookLevelMin
        val maxLevelGain = max(minLevelGain, Config.trainingBookLevelMax)
        val levelGain = rollLevelGain(player.random, minLevelGain, maxLevelGain)

        if (!interactionTarget.addTrainingBookLevel(levelGain)) {
            return InteractionResult.FAIL
        }

        if (!player.abilities.instabuild) {
            stack.shrink(1)
        }

        return InteractionResult.sidedSuccess(false)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
        tooltipComponents.add(Component.translatable("gui.shincolle.trainingbook").withStyle(ChatFormatting.GOLD))
    }

    companion object {
        fun levelGainRange(minLevelGain: Int, maxLevelGain: Int): IntRange {
            return minLevelGain..max(minLevelGain, maxLevelGain)
        }

        fun rollLevelGain(random: net.minecraft.util.RandomSource, minLevelGain: Int, maxLevelGain: Int): Int {
            val range = levelGainRange(minLevelGain, maxLevelGain)
            return if (range.first == range.last) {
                range.first
            } else {
                range.first + random.nextInt(range.last - range.first + 1)
            }
        }
    }
}
