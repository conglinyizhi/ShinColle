package org.trp.shincolle.item

import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.Level
import java.util.function.Consumer
import java.util.function.UnaryOperator

class GrudgeItem(properties: Properties) : Item(properties) {
    fun getVariant(stack: ItemStack): Int {
        val customData = stack.get<CustomData?>(DataComponents.CUSTOM_DATA)
        if (customData == null) {
            return 0
        }
        return Mth.clamp(customData.copyTag().getInt(TAG_VARIANT), 0, 1)
    }

    fun getModelVariant(stack: ItemStack): Int {
        return getVariant(stack)
    }

    fun createVariantStack(variant: Int): ItemStack {
        val clamped = Mth.clamp(variant, 0, 1)
        val stack = ItemStack(this)
        if (clamped > 0) {
            stack.update<CustomData?>(
                DataComponents.CUSTOM_DATA, CustomData.EMPTY,
                UnaryOperator { data: CustomData? ->
                    data!!.update(Consumer { tag: CompoundTag? ->
                        tag!!.putInt(
                            TAG_VARIANT,
                            clamped
                        )
                    })
                })
        }
        return stack
    }

    fun addAllVariantsToCreativeTab(output: CreativeModeTab.Output) {
        output.accept(createVariantStack(0))
        output.accept(createVariantStack(1))
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        return InteractionResultHolder.fail<ItemStack?>(player.getItemInHand(hand))
    }

    override fun getName(stack: ItemStack): Component {
        val variant = getVariant(stack)
        return Component.translatable(if (variant == 0) "item.shincolle.grudge" else "item.shincolle.Grudge1.name")
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        if (getVariant(stack) > 0) {
            tooltipComponents.add(
                Component.translatable("tile.shincolle.BlockGrudgeXP.name").withStyle(ChatFormatting.LIGHT_PURPLE)
            )
        }
    }

    companion object {
        private const val TAG_VARIANT = "LegacyVariant"
    }
}
