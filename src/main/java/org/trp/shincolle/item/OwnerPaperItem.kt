package org.trp.shincolle.item

import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.Level
import java.util.*
import java.util.function.Consumer
import java.util.function.UnaryOperator

class OwnerPaperItem(properties: Properties) : Item(properties.stacksTo(1)) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(hand)
        if (!level.isClientSide && !player.isShiftKeyDown()) {
            stack.update<CustomData?>(
                DataComponents.CUSTOM_DATA, CustomData.EMPTY,
                UnaryOperator { data: CustomData? ->
                    data!!.update(Consumer { tag: CompoundTag? ->
                        Companion.writeOwnerSignature(
                            tag!!,
                            player.name.string,
                            player.uuid
                        )
                    })
                })
        }
        return InteractionResultHolder.sidedSuccess<ItemStack?>(stack, level.isClientSide)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        val customData = stack.get<CustomData?>(DataComponents.CUSTOM_DATA)
        if (customData == null) {
            return
        }
        val tag = customData.copyTag()
        if (tag.contains(SIGN_NAME_A)) {
            val id = tag.getString(SIGN_ID_A)
            tooltipComponents.add(
                Component.literal(id + " ").withStyle(ChatFormatting.RED)
                    .append(Component.literal(tag.getString(SIGN_NAME_A)).withStyle(ChatFormatting.AQUA))
            )
        }
        if (tag.contains(SIGN_NAME_B)) {
            val id = tag.getString(SIGN_ID_B)
            tooltipComponents.add(
                Component.literal(id + " ").withStyle(ChatFormatting.RED)
                    .append(Component.literal(tag.getString(SIGN_NAME_B)).withStyle(ChatFormatting.AQUA))
            )
        }
    }

    companion object {
        private const val SIGN_NAME_A = "SignNameA"
        private const val SIGN_NAME_B = "SignNameB"
        private const val SIGN_ID_A = "SignIDA"
        private const val SIGN_ID_B = "SignIDB"
        private const val SIGN_POS = "signPos"

        fun writeOwnerSignature(tag: CompoundTag, playerName: String, playerUuid: UUID) {
            val firstWrite = !tag.contains(SIGN_NAME_A)
            if (firstWrite) {
                tag.putString(SIGN_NAME_A, playerName)
                tag.putString(SIGN_NAME_B, "")
                tag.putString(SIGN_ID_A, playerUuid.toString())
                tag.putString(SIGN_ID_B, "")
                tag.putBoolean(SIGN_POS, false)
                return
            }

            if (tag.getBoolean(SIGN_POS)) {
                tag.putString(SIGN_NAME_A, playerName)
                tag.putString(SIGN_ID_A, playerUuid.toString())
                tag.putBoolean(SIGN_POS, false)
            } else {
                tag.putString(SIGN_NAME_B, playerName)
                tag.putString(SIGN_ID_B, playerUuid.toString())
                tag.putBoolean(SIGN_POS, true)
            }
        }
    }
}
