package org.trp.shincolle.item

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.base.EntitySummonBase

class KaitaiHammerItem(properties: Properties) : Item(properties.stacksTo(1).durability(20)) {
    override fun hasCraftingRemainingItem(stack: ItemStack): Boolean {
        return true
    }

    override fun getCraftingRemainingItem(stack: ItemStack): ItemStack {
        val remainder = stack.copy()
        remainder.setCount(1)
        remainder.setDamageValue(remainder.getDamageValue() + 1)
        if (remainder.getDamageValue() >= remainder.getMaxDamage()) {
            return ItemStack.EMPTY
        }
        return remainder
    }

    override fun onLeftClickEntity(stack: ItemStack, player: Player, entity: Entity): Boolean {
        if (!player.level().isClientSide) {
            if (entity is EntityShipBase) {
                if (entity.isOwnedBy(player) || player.hasPermissions(2)) {
                    entity.applyParticleEmotion(8)
                    entity.applyEmotesAOE(10.0, 6, false)
                    entity.hurt(player.damageSources().fellOutOfWorld(), Float.MAX_VALUE)

                    if (!player.getAbilities().instabuild) {
                        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND)
                    }
                    return true
                }
            } else if (entity is EntitySummonBase) {
                if (entity is TamableAnimal) {
                    if (summon.isOwnedBy(player) || player.hasPermissions(2)) {
                        entity.discard()
                        if (!player.getAbilities().instabuild) {
                            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND)
                        }
                        return true
                    }
                }
            }
        }
        return false
    }
}
