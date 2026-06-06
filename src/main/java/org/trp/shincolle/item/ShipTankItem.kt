package org.trp.shincolle.item

import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.context.UseOnContext
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidUtil
import java.util.function.Consumer
import java.util.function.UnaryOperator

class ShipTankItem(properties: Properties) : Item(properties.stacksTo(1)) {
    val variantCount: Int
        get() = CAPACITY_BY_VARIANT.size

    fun getVariant(stack: ItemStack): Int {
        val customData = stack.get<CustomData?>(DataComponents.CUSTOM_DATA)
        if (customData == null) {
            return 0
        }

        val raw = customData.copyTag().getInt(TAG_VARIANT)
        return Mth.clamp(raw, 0, CAPACITY_BY_VARIANT.size - 1)
    }

    fun getModelVariant(stack: ItemStack): Int {
        return getVariant(stack)
    }

    fun createVariantStack(variant: Int): ItemStack {
        val clamped = Mth.clamp(variant, 0, CAPACITY_BY_VARIANT.size - 1)
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
        for (i in CAPACITY_BY_VARIANT.indices) {
            output.accept(createVariantStack(i))
        }
    }

    override fun getName(stack: ItemStack): Component {
        val variant = getVariant(stack)
        val suffix = if (variant > 0) variant.toString() else ""
        return Component.translatable("item.shincolle.ShipTank" + suffix + ".name")
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)

        val capacity: Int = getCapacity(stack)
        tooltipComponents.add(Component.translatable("gui.shincolle.shiptank").withStyle(ChatFormatting.GRAY))

        val fluid = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY)
        val amount = fluid.getAmount()

        if (fluid.isEmpty()) {
            tooltipComponents.add(
                Component.literal(amount.toString() + " / " + capacity + " mB")
                    .withStyle(ChatFormatting.DARK_AQUA)
            )
            return
        }

        tooltipComponents.add(
            Component.literal(amount.toString() + " / " + capacity + " mB ").withStyle(ChatFormatting.AQUA)
                .append(fluid.getHoverName().copy().withStyle(ChatFormatting.AQUA))
        )
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.getLevel()
        val player = context.getPlayer()
        val stack = context.getItemInHand()
        val pos = context.getClickedPos()
        val side = context.getClickedFace()
        val hand = context.getHand()

        if (level.isClientSide) {
            return InteractionResult.PASS
        }

        val itemHandlerOpt = FluidUtil.getFluidHandler(stack)
        if (itemHandlerOpt.isEmpty()) {
            return InteractionResult.PASS
        }

        val itemHandler = itemHandlerOpt.get()

        val blockHandlerOpt = FluidUtil.getFluidHandler(level, pos, side)
        if (blockHandlerOpt.isPresent()) {
            val blockHandler = blockHandlerOpt.get()
            val contained = itemHandler.getFluidInTank(0)
            val transferred = if (contained.isEmpty())
                FluidUtil.tryFluidTransfer(itemHandler, blockHandler, itemHandler.getTankCapacity(0), true)
            else
                FluidUtil.tryFluidTransfer(blockHandler, itemHandler, contained, true)

            if (!transferred.isEmpty()) {
                if (player != null) {
                    player.setItemInHand(hand, itemHandler.getContainer())
                }
                return InteractionResult.sidedSuccess(level.isClientSide)
            }
        }

        val contained = itemHandler.getFluidInTank(0)
        if (contained.isEmpty()) {
            val pickup = FluidUtil.tryPickUpFluid(stack, player, level, pos, side)
            if (pickup.isSuccess()) {
                if (player != null) {
                    player.setItemInHand(hand, pickup.getResult())
                }
                return InteractionResult.sidedSuccess(level.isClientSide)
            }
            return InteractionResult.PASS
        }

        val clickedState = level.getBlockState(pos)
        val placePos = if (clickedState.canBeReplaced()) pos else pos.relative(side)
        val placed = FluidUtil.tryPlaceFluid(player, level, hand, placePos, stack, contained)
        if (placed.isSuccess()) {
            if (player != null) {
                player.setItemInHand(hand, placed.getResult())
            }
            return InteractionResult.sidedSuccess(level.isClientSide)
        }

        return InteractionResult.PASS
    }

    companion object {
        private const val TAG_VARIANT = "LegacyVariant"
        private val CAPACITY_BY_VARIANT = intArrayOf(32000, 128000, 512000, 2048000)

        fun getCapacity(variant: Int): Int {
            val clamped = Mth.clamp(variant, 0, CAPACITY_BY_VARIANT.size - 1)
            return CAPACITY_BY_VARIANT[clamped]
        }

        @JvmStatic
        fun getCapacity(stack: ItemStack): Int {
            if (stack.getItem() is ShipTankItem) {
                return Companion.getCapacity((stack.item as ShipTankItem).getVariant(stack))
            }
            return CAPACITY_BY_VARIANT[0]
        }
    }
}
