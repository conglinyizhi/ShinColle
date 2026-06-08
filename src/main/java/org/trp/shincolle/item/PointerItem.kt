package org.trp.shincolle.item

import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuConstructor
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.menu.FormationMenu
import org.trp.shincolle.network.C2SPointerActionPayload
import org.trp.shincolle.network.ModNetwork.sendToServer
import java.util.*
import java.util.function.Consumer
import java.util.function.UnaryOperator

class PointerItem(properties: Properties) : Item(properties.stacksTo(1)) {
    fun getMode(stack: ItemStack): Int {
        val customData = stack.get<CustomData?>(DataComponents.CUSTOM_DATA)
        if (customData == null) {
            return MODE_SINGLE
        }

        val raw = customData.copyTag().getInt(TAG_VARIANT)
        return Mth.clamp(raw, MODE_SINGLE, MODE_COUNT - 1)
    }

    fun getModelVariant(stack: ItemStack): Int {
        return getMode(stack)
    }

    fun cycleMode(stack: ItemStack): Int {
        val next: Int = (getMode(stack) + 1) % MODE_COUNT
        setMode(stack, next)
        return next
    }

    fun setMode(stack: ItemStack, mode: Int) {
        val clamped = Mth.clamp(mode, MODE_SINGLE, MODE_COUNT - 1)
        if (clamped == MODE_SINGLE) {
            stack.remove<CustomData?>(DataComponents.CUSTOM_DATA)
            return
        }

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

    fun createVariantStack(mode: Int): ItemStack {
        val stack = ItemStack(this)
        setMode(stack, mode)
        return stack
    }

    fun addAllVariantsToCreativeTab(output: CreativeModeTab.Output) {
        for (mode in MODE_SINGLE..<MODE_COUNT) {
            output.accept(createVariantStack(mode))
        }
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
        if (context.level() == null || !context.level()!!.isClientSide()) {
            return
        }
        appendClientHoverText(stack, tooltipComponents)
    }

    private fun appendClientHoverText(stack: ItemStack?, tooltipComponents: MutableList<Component?>?) {
        try {
            val helperClass = Class.forName("org.trp.shincolle.client.PointerItemClientHelper")
            helperClass.getMethod(
                "appendHoverText",
                PointerItem::class.java,
                ItemStack::class.java,
                MutableList::class.java
            )
                .invoke(null, this, stack, tooltipComponents)
        } catch (ignored: ReflectiveOperationException) {
            // Client helper is optional on server-only test/runtime paths.
        }
    }

    override fun isFoil(stack: ItemStack): Boolean {
        return true
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(hand)
        if (!level.isClientSide) {
            if (player.isShiftKeyDown() && getMode(stack) == MODE_FORMATION) {
                player.openMenu(
                    SimpleMenuProvider(
                        MenuConstructor { id: Int, inv: Inventory?, p: Player? -> FormationMenu(id, inv!!) },
                        Component.translatable("gui.shincolle.formation.title")
                    )
                )
                return InteractionResultHolder.success<ItemStack?>(stack)
            }
        }
        return InteractionResultHolder.pass<ItemStack?>(stack)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player
        if (player != null && player.isShiftKeyDown()) {
            return InteractionResult.PASS
        }

        val level = context.level
        if (level.isClientSide && player != null) {
            val mode = getMode(context.itemInHand)
            if (mode == MODE_FORMATION) {
                val blockPos = context.clickedPos
                val pos = Vec3.atBottomCenterOf(blockPos).add(0.0, 1.0, 0.0)
                sendToServer(C2SPointerActionPayload(2, Optional.empty<UUID?>(), Optional.of<Vec3?>(pos)))
                return InteractionResult.SUCCESS
            }
        }
        return InteractionResult.PASS
    }

    override fun interactLivingEntity(
        stack: ItemStack,
        player: Player,
        target: LivingEntity,
        hand: InteractionHand
    ): InteractionResult {
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS
        }

        if (player.level().isClientSide) {
            val mode = getMode(stack)
            if (mode == MODE_FORMATION) {
                if (target is EntityShipBase && target.isOwnedBy(player)) {
                    return if (player.level().isClientSide) InteractionResult.SUCCESS else InteractionResult.PASS
                } else {
                    sendToServer(
                        C2SPointerActionPayload(
                            1,
                            Optional.of<UUID?>(target.uuid),
                            Optional.empty<Vec3?>()
                        )
                    )
                    return InteractionResult.SUCCESS
                }
            }
        }
        return InteractionResult.PASS
    }

    fun onSwingMiss(player: Player, stack: ItemStack?) {
        if (player.level().isClientSide) {
            sendToServer(C2SPointerActionPayload(0, Optional.empty<UUID?>(), Optional.empty<Vec3?>()))
        }
    }

    companion object {
        const val TAG_VARIANT = "LegacyVariant"

        const val MODE_SINGLE: Int = 0
        const val MODE_GROUP: Int = 1
        const val MODE_FORMATION: Int = 2

        private const val MODE_COUNT = 3

        @JvmStatic
        fun getModeTranslationKey(mode: Int): String {
            return when (mode) {
                MODE_GROUP -> "gui.shincolle.pointer1"
                MODE_FORMATION -> "gui.shincolle.pointer2"
                else -> "gui.shincolle.pointer0"
            }
        }
    }
}
