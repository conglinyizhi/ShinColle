package org.trp.shincolle.item

import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import org.trp.shincolle.crafting.ShipyardRecipes
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.function.UnaryOperator

class RandomShipSpawnEggItem(
    fallbackType: Supplier<out EntityType<out Mob?>?>,
    shipClass: ShipClass?,
    private val largeShipyardEgg: Boolean,
    primaryColor: Int,
    secondaryColor: Int,
    properties: Properties
) : ShipSpawnEggItem(fallbackType, shipClass, primaryColor, secondaryColor, properties) {
    override fun useOn(context: UseOnContext): InteractionResult {
        injectRandomEntityData(context.level, context.player, context.itemInHand)
        return super.useOn(context)
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(hand)
        injectRandomEntityData(level, player, stack)
        return super.use(level, player, hand)
    }

    private fun injectRandomEntityData(level: Level, player: Player?, stack: ItemStack) {
        if (level.isClientSide || stack.isEmpty()) {
            return
        }

        val entityType = ShipyardRecipes.rollShipEntityType(this.largeShipyardEgg, stack)
        val key = BuiltInRegistries.ENTITY_TYPE.getKey(entityType)
        if (key == null) {
            return
        }

        stack.update<CustomData?>(
            DataComponents.ENTITY_DATA,
            CustomData.EMPTY,
            UnaryOperator { existingData: CustomData? ->
                existingData!!.update(
                    Consumer { tag: CompoundTag? ->
                        tag!!.putString("id", key.toString())
                        if (player != null && !tag.hasUUID("Owner")) {
                            tag.putUUID("Owner", player.uuid)
                        }
                        if (!tag.contains("Tame")) {
                            tag.putBoolean("Tame", true)
                        }
                    })
            })
    }
}
