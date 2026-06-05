package org.trp.shincolle.item

import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import java.util.function.Consumer
import java.util.function.UnaryOperator

class AbyssNuggetItem(properties: Properties) : Item(properties) {
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

    override fun getName(stack: ItemStack): Component {
        return Component.translatable(
            if (getVariant(stack) == 0)
                "item.shincolle.AbyssNugget.name"
            else
                "item.shincolle.AbyssNugget1.name"
        )
    }

    companion object {
        private const val TAG_VARIANT = "LegacyVariant"
    }
}
