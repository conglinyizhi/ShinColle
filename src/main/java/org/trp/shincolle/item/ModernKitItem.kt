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
import org.trp.shincolle.Shincolle.Companion.debugLog
import org.trp.shincolle.entity.base.EntityShipBase

class ModernKitItem(properties: Properties) : Item(properties.stacksTo(1)) {
    @JvmRecord
    data class MaxedFeedback(val message: Component?, val actionBar: Boolean)

    override fun interactLivingEntity(
        stack: ItemStack,
        player: Player,
        interactionTarget: LivingEntity,
        usedHand: InteractionHand
    ): InteractionResult {
        if (interactionTarget !is EntityShipBase) {
            return InteractionResult.PASS
        }
        debugLog(
            "ModernKit interact target={} ship={} ownerMatch={} client={} hand={} itemCount={}",
            interactionTarget.type.toShortString(),
            interactionTarget.uuid,
            interactionTarget.isOwnedBy(player),
            player.level().isClientSide,
            usedHand,
            stack.count
        )
        if (!interactionTarget.isOwnedBy(player)) {
            return InteractionResult.PASS
        }
        if (player.level().isClientSide) {
            if (!interactionTarget.legacyShipStats.hasBonusCapacity()) {
                debugLog("ModernKit client fail ship={} bonusesMaxed=true", interactionTarget.uuid)
                return InteractionResult.FAIL
            }
            debugLog("ModernKit client success ship={}", interactionTarget.uuid)
            return InteractionResult.sidedSuccess(true)
        }

        val beforeBonus = IntArray(6)
        for (i in beforeBonus.indices) {
            beforeBonus[i] = interactionTarget.getAttrBonus(i)
        }

        if (!interactionTarget.interactModernKit(player, stack)) {
            debugLog("ModernKit noEffect ship={} bonusesMaxed=true", interactionTarget.uuid)
            if (Config.modernKitNotifyWhenMaxed) {
                val feedback: MaxedFeedback = maxedFeedback()
                player.displayClientMessage(feedback.message ?: Component.empty(), feedback.actionBar)
            }
            return InteractionResult.FAIL
        }

        var appliedAttrId = -1
        for (i in beforeBonus.indices) {
            if (interactionTarget.getAttrBonus(i) > beforeBonus[i]) {
                appliedAttrId = i
                break
            }
        }

        debugLog(
            "ModernKit applied ship={} attrId={} newAttrBonus={} bonuses={}/{}/{}/{}/{}/{} creative={}",
            interactionTarget.uuid,
            appliedAttrId,
            if (appliedAttrId >= 0) interactionTarget.getAttrBonus(appliedAttrId) else -1,
            interactionTarget.getAttrBonus(0),
            interactionTarget.getAttrBonus(1),
            interactionTarget.getAttrBonus(2),
            interactionTarget.getAttrBonus(3),
            interactionTarget.getAttrBonus(4),
            interactionTarget.getAttrBonus(5),
            player.abilities.instabuild
        )
        debugLog("ModernKit consumed ship={} remaining={}", interactionTarget.uuid, stack.count)
        return InteractionResult.sidedSuccess(false)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        tooltipComponents.add(Component.translatable("gui.shincolle.modernkit").withStyle(ChatFormatting.GOLD))
    }

    companion object {
        fun maxedFeedback(): MaxedFeedback {
            return MaxedFeedback(
                Component.translatable("chat.shincolle.modernkit.maxed"),
                Config.modernKitNotifyWhenMaxedActionBar
            )
        }
    }
}
