package org.trp.shincolle.item

import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomData
import java.util.function.Consumer
import java.util.function.UnaryOperator
import kotlin.math.max

class CombatRationItem(properties: Properties) : Item(properties.stacksTo(16)) {
    val variantCount: Int
        get() = FOOD_VALUE.size

    fun getVariant(stack: ItemStack): Int {
        val customData = stack.get<CustomData?>(DataComponents.CUSTOM_DATA)
        if (customData == null) {
            return 0
        }

        val raw = customData.copyTag().getInt(TAG_VARIANT)
        return Mth.clamp(raw, 0, FOOD_VALUE.size - 1)
    }

    fun getModelVariant(stack: ItemStack): Int {
        return getVariant(stack)
    }

    fun createVariantStack(variant: Int): ItemStack {
        val clamped = Mth.clamp(variant, 0, FOOD_VALUE.size - 1)
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
        for (i in FOOD_VALUE.indices) {
            output.accept(createVariantStack(i))
        }
    }

    override fun getName(stack: ItemStack): Component {
        val variant = getVariant(stack)
        val suffix = if (variant > 0) variant.toString() else ""
        return Component.translatable("item.shincolle.CombatRation" + suffix + ".name")
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)

        val variant = getVariant(stack)
        val lines = Component.translatable("gui.shincolle.combatration" + variant).getString().split("<br>".toRegex())
            .dropLastWhile { it.isEmpty() }.toTypedArray()
        for (line in lines) {
            val trimmed = line.trim { it <= ' ' }
            if (!trimmed.isEmpty()) {
                tooltipComponents.add(Component.literal(trimmed).withStyle(ChatFormatting.GRAY))
            }
        }

        tooltipComponents.add(
            Component.literal("+" + getMoraleValue(variant) + " ")
                .append(Component.translatable("gui.shincolle.combatration"))
                .withStyle(ChatFormatting.LIGHT_PURPLE)
        )

        tooltipComponents.add(
            Component.literal("+" + getFuelGainMin(variant) + "~" + getFuelGainMax(variant) + " ")
                .append(Component.translatable("item.shincolle.grudge"))
                .withStyle(ChatFormatting.RED)
        )
    }

    companion object {
        private const val TAG_VARIANT = "LegacyVariant"
        private val FOOD_VALUE = intArrayOf(900, 3600, 1200, 3900, 100, 900)
        private val MORALE_VALUE = intArrayOf(1400, 1800, 1600, 2000, 3000, 4000)

        fun getFoodValue(variant: Int): Int {
            val clamped = Mth.clamp(variant, 0, FOOD_VALUE.size - 1)
            return FOOD_VALUE[clamped]
        }

        @JvmStatic
        fun getMoraleValue(variant: Int): Int {
            val clamped = Mth.clamp(variant, 0, MORALE_VALUE.size - 1)
            return MORALE_VALUE[clamped]
        }

        fun getFuelGainMin(variant: Int): Int {
            return max(1, getFoodValue(variant) / 100)
        }

        fun getFuelGainMax(variant: Int): Int {
            return getFuelGainMin(variant) * 2
        }

        @JvmStatic
        fun rollFuelGain(random: RandomSource, variant: Int): Int {
            val min: Int = getFuelGainMin(variant)
            return min + random.nextInt(min + 1)
        }
    }
}
