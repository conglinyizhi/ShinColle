package org.trp.shincolle.item

import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.neoforged.neoforge.common.DeferredSpawnEggItem
import org.trp.shincolle.Shincolle.Companion.diagnosticLog
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.function.UnaryOperator

open class OwnedSpawnEggItem(
    private val typeSupplier: Supplier<out EntityType<out Mob?>?>,
    primaryColor: Int,
    secondaryColor: Int,
    properties: Properties
) : DeferredSpawnEggItem(
    typeSupplier, primaryColor, secondaryColor, properties
) {
    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.getPlayer()
        if (player != null && !context.getLevel().isClientSide) {
            val stack = context.getItemInHand()

            val customData = stack.get<CustomData?>(DataComponents.ENTITY_DATA)
            var isResurrection = false
            var costLevel = 0
            if (customData != null) {
                val tag = customData.copyTag()
                if (tag.getBoolean(TAG_SHINCOLLE_SPAWN_EGG)) {
                    isResurrection = true
                    if (!tag.getBoolean(TAG_SPAWN_EGG_NO_EXP)) {
                        costLevel = tag.getInt("ShipLevel") / 3
                    }
                }
            }

            if (isResurrection && costLevel > 0 && !player.isCreative()) {
                if (player.experienceLevel < costLevel) {
                    player.displayClientMessage(Component.translatable("chat.shincolle:levelfail"), false)
                    return InteractionResult.FAIL
                }
            }

            ensureOwnedEntityData(stack, player, "useOn")

            val result = super.useOn(context)
            if (result.consumesAction() && isResurrection && costLevel > 0 && !player.isCreative()) {
                player.giveExperienceLevels(-costLevel)
            }
            return result
        }
        return super.useOn(context)
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(hand)

        val hitresult: HitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY)
        if (hitresult.getType() != HitResult.Type.BLOCK) {
            return super.use(level, player, hand)
        } else if (level !is ServerLevel) {
            return InteractionResultHolder.success<ItemStack?>(stack)
        } else {
            val customData = stack.get<CustomData?>(DataComponents.ENTITY_DATA)
            var isResurrection = false
            var costLevel = 0
            if (customData != null) {
                val tag = customData.copyTag()
                if (tag.getBoolean(TAG_SHINCOLLE_SPAWN_EGG)) {
                    isResurrection = true
                    if (!tag.getBoolean(TAG_SPAWN_EGG_NO_EXP)) {
                        costLevel = tag.getInt("ShipLevel") / 3
                    }
                }
            }

            if (isResurrection && costLevel > 0 && !player.isCreative()) {
                if (player.experienceLevel < costLevel) {
                    player.displayClientMessage(Component.translatable("chat.shincolle:levelfail"), false)
                    return InteractionResultHolder.fail<ItemStack?>(stack)
                }
            }

            ensureOwnedEntityData(stack, player, "use")
            val result = super.use(level, player, hand)
            if (result.getResult().consumesAction() && isResurrection && costLevel > 0 && !player.isCreative()) {
                player.giveExperienceLevels(-costLevel)
            }
            return result
        }
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
        val customData = stack.get<CustomData?>(DataComponents.ENTITY_DATA)
        if (customData != null) {
            val tag = customData.copyTag()
            if (tag.getBoolean(TAG_SHINCOLLE_SPAWN_EGG)) {
                val costLevel = if (tag.getBoolean(TAG_SPAWN_EGG_NO_EXP)) 0 else tag.getInt("ShipLevel") / 3
                tooltipComponents.add(
                    Component.translatable("gui.shincolle.eggText").append(" " + costLevel).withStyle(
                        ChatFormatting.AQUA
                    )
                )
            }
        }
    }

    private fun ensureOwnedEntityData(stack: ItemStack, player: Player, source: String?) {
        stack.update<CustomData?>(
            DataComponents.ENTITY_DATA,
            CustomData.EMPTY,
            UnaryOperator { existingData: CustomData? ->
                existingData!!.update(
                    Consumer { tag: CompoundTag? ->
                        if (!tag!!.hasUUID("Owner")) {
                            tag.putUUID("Owner", player.getUUID())
                        }
                        if (!tag.contains("Tame")) {
                            tag.putBoolean("Tame", true)
                        }
                        if (!tag.contains("id")) {
                            tag.putString(
                                "id",
                                BuiltInRegistries.ENTITY_TYPE.getKey(this.typeSupplier.get()).toString()
                            )
                        }
                    })
            })

        val customData = stack.get<CustomData?>(DataComponents.ENTITY_DATA)
        if (customData == null) {
            diagnosticLog("[SCSpawnDiag] ownedEggDataMissing source={} player={}", source, player.getUUID())
            return
        }
        val tag = customData.copyTag()
        diagnosticLog(
            "[SCSpawnDiag] ownedEggPrepared source={} player={} ownerPresent={} tame={} entityId={}",
            source, player.getUUID(), tag.hasUUID("Owner"), tag.getBoolean("Tame"), tag.getString("id")
        )
    }

    companion object {
        private const val TAG_SHINCOLLE_SPAWN_EGG = "ShincolleSpawnEgg"
        private const val TAG_SPAWN_EGG_NO_EXP = "ShincolleSpawnEggNoExpCost"
    }
}
