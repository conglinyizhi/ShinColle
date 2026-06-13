package org.trp.shincolle.entity.base

import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.api.ApiCallSafety
import org.trp.shincolle.api.consumable.IShipConsumable
import org.trp.shincolle.api.equip.IShipEquip
import org.trp.shincolle.api.equip.ShipEquipRegistry
import org.trp.shincolle.api.equip.ShipEquipSpecialEffect
import org.trp.shincolle.entity.projectile.EntityAbyssMissile
import org.trp.shincolle.inventory.ShipInventoryHandler
import org.trp.shincolle.item.LegacyEquipItem
import org.trp.shincolle.item.LegacyEquipStats

/**
 * Centralizes equipment iteration and effect resolution for a ship.
 *
 * Eliminates the duplicated `for (i in 0..<inv.slots)` loops that appear in
 * bonus collection, ammo configuration, attack notifications and death protection.
 */
@Suppress(
    "MagicNumber",
    "LongMethod",
    "CyclomaticComplexMethod",
    "ReturnCount",
    "LoopWithTooManyJumpStatements"
)
internal class ShipEquipFacade(private val ship: EntityShipBase) {

    private val inventory get() = ship.inventory

    /** Iterates over the equipment-only slots. */
    inline fun forEachEquipSlot(action: (stack: ItemStack, item: Item, typeId: Int) -> Unit) {
        val inv = inventory ?: return
        val limit = minOf(ShipInventoryHandler.equipSlotCount, inv.slots)
        for (slot in 0..<limit) {
            val stack = inv.getStackInSlot(slot)
            if (stack.isEmpty) continue
            val item = stack.item
            val typeId = resolveTypeId(item, stack)
            action(stack, item, typeId)
        }
    }

    /** Iterates over every slot in the ship's inventory. */
    inline fun forEachInventorySlot(action: (stack: ItemStack, item: Item, typeId: Int) -> Unit) {
        val inv = inventory ?: return
        for (slot in 0..<inv.slots) {
            val stack = inv.getStackInSlot(slot)
            if (stack.isEmpty) continue
            val item = stack.item
            val typeId = resolveTypeId(item, stack)
            action(stack, item, typeId)
        }
    }

    /** Iterates over every slot and yields only registered third-party [IShipEquip] items. */
    inline fun forEachShipEquip(
        action: (stack: ItemStack, equip: IShipEquip, typeId: Int, effect: ShipEquipSpecialEffect?) -> Unit
    ) {
        forEachInventorySlot { stack, item, typeId ->
            if (item !is IShipEquip || typeId < 0) return@forEachInventorySlot
            val effect = ShipEquipRegistry.getEffect(typeId)
            action(stack, item, typeId, effect)
        }
    }

    /**
     * Collects attribute bonuses and equipment counters from the equip slots.
     *
     * Mirrors the legacy [EntityShipBase.collectEquipBonuses] logic and writes the
     * drum/compass/flare/searchlight/special-ammo/torpedo counters back to the ship.
     */
    fun collectEquipBonuses(): FloatArray {
        val equipBonuses = FloatArray(LegacyEquipStats.ATTR_COUNT)
        var drumCount = 0
        var compassCount = 0
        var flareCount = 0
        var searchlightCount = 0
        var specialAmmoVariant = -1
        var torpedoSpeedLevel = 0

        forEachEquipSlot { stack, item, equipTypeId ->
            val stats: FloatArray? = when (item) {
                is LegacyEquipItem -> {
                    when (equipTypeId) {
                        EntityShipBase.EQUIP_TYPE_DRUM -> drumCount++
                        EntityShipBase.EQUIP_TYPE_COMPASS -> compassCount++
                        EntityShipBase.EQUIP_TYPE_FLARE -> flareCount++
                        EntityShipBase.EQUIP_TYPE_SEARCHLIGHT -> searchlightCount++
                    }

                    if (equipTypeId == 29) {
                        val variant = item.getVariant(stack)
                        if (variant == 5 || variant == 6 || variant == 8) {
                            specialAmmoVariant = maxOf(specialAmmoVariant, variant)
                        }
                    } else if (equipTypeId == 5) {
                        val variant = item.getVariant(stack)
                        if (variant >= 3) {
                            val speed = when (variant) {
                                3, 4 -> 1
                                5 -> 2
                                6 -> 3
                                else -> 0
                            }
                            torpedoSpeedLevel = maxOf(torpedoSpeedLevel, speed)
                        }
                    }

                    LegacyEquipStats.getMainAttrs(item.getEquipId(stack))
                }

                is IShipEquip -> {
                    if (equipTypeId >= 0) {
                        val effect = ShipEquipRegistry.getEffect(equipTypeId)
                        if (effect != null) {
                            val count = ApiCallSafety.runWithDefault(
                                "ShipEquipSpecialEffect.collectCount", 0
                            ) { effect.collectCount(ship, stack) }
                            when (equipTypeId) {
                                EntityShipBase.EQUIP_TYPE_DRUM -> drumCount += count
                                EntityShipBase.EQUIP_TYPE_COMPASS -> compassCount += count
                                EntityShipBase.EQUIP_TYPE_FLARE -> flareCount += count
                                EntityShipBase.EQUIP_TYPE_SEARCHLIGHT -> searchlightCount += count
                            }
                        }
                    }
                    ApiCallSafety.runNullable("IShipEquip.getMainAttributes") {
                        item.getMainAttributes(stack)
                    }
                }

                else -> null
            }

            if (stats != null) {
                val len = minOf(equipBonuses.size, stats.size)
                for (i in 0..<len) {
                    equipBonuses[i] += stats[i]
                }
            }

            val enchantStats: FloatArray? = when (item) {
                is LegacyEquipItem -> item.getEnchantmentBonusAttributes(stack)
                is IShipEquip -> ApiCallSafety.runNullable(
                    "IShipEquip.getEnchantmentBonusAttributes"
                ) { item.getEnchantmentBonusAttributes(stack) }
                else -> null
            }

            if (enchantStats != null) {
                val len = minOf(equipBonuses.size, enchantStats.size)
                for (i in 0..<len) {
                    equipBonuses[i] += enchantStats[i]
                }
            }
        }

        ship.setStateMinor(EntityShipBase.STATE_MINOR_EQUIP_DRUM, drumCount)
        ship.setStateMinor(EntityShipBase.STATE_MINOR_EQUIP_COMPASS, compassCount)
        ship.setStateMinor(EntityShipBase.STATE_MINOR_EQUIP_FLARE, flareCount)
        ship.setStateMinor(EntityShipBase.STATE_MINOR_EQUIP_SEARCHLIGHT, searchlightCount)
        ship.setStateMinor(EntityShipBase.STATE_MINOR_EQUIP_SPECIAL_AMMO, specialAmmoVariant)
        ship.setStateMinor(EntityShipBase.STATE_MINOR_EQUIP_TORPEDO_SPEED, torpedoSpeedLevel)

        return equipBonuses
    }

    /** Applies legacy equipment and registered third-party equipment effects to [missile]. */
    fun configureAmmoEffects(missile: EntityAbyssMissile) {
        forEachInventorySlot { stack, item, typeId ->
            when (item) {
                is LegacyEquipItem -> {
                    if (typeId != 29) return@forEachInventorySlot
                    val variant: Int = item.getVariant(stack)
                    when (variant) {
                        0 -> missile.addImpactEffect(MobEffects.POISON, 0, 120, 50)
                        1 -> missile.addImpactEffect(MobEffects.POISON, 1, 120, 70)
                        3 -> missile.addImpactEffect(MobEffects.CONFUSION, 0, 120, 50)
                        4 -> missile.addImpactEffect(MobEffects.WITHER, 0, 100, 25)
                        6 -> missile.addImpactEffect(MobEffects.LEVITATION, 0, 100, 50)
                        7 -> ship.combat.addEnchantShellEffects(missile, stack)
                        else -> {}
                    }
                }

                is IShipEquip -> {
                    if (typeId < 0) return@forEachInventorySlot
                    val effect = ShipEquipRegistry.getEffect(typeId)
                    if (effect != null) {
                        ApiCallSafety.run("ShipEquipSpecialEffect.applyToMissile") {
                            effect.applyToMissile(ship, missile, stack)
                        }
                    }
                }

                else -> {}
            }
        }
    }

    /** Notifies all registered third-party equipment of a light attack. */
    fun notifyEquipOnLightAttack(target: Entity?) {
        forEachShipEquip { stack, _, _, effect ->
            if (effect == null) return@forEachShipEquip
            ApiCallSafety.runWithDefault(
                "ShipEquipSpecialEffect.onLightAttack", false
            ) { effect.onLightAttack(ship, stack, target) }
        }
    }

    /** Notifies all registered third-party equipment of a heavy attack. */
    fun notifyEquipOnHeavyAttack(target: Entity?, missile: EntityAbyssMissile) {
        forEachShipEquip { stack, _, _, effect ->
            if (effect == null) return@forEachShipEquip
            ApiCallSafety.runWithDefault(
                "ShipEquipSpecialEffect.onHeavyAttack", false
            ) { effect.onHeavyAttack(ship, stack, target, missile) }
        }
    }

    /** Attempts to prevent death using a registered [IShipConsumable] item. */
    fun tryConsumableDeathProtection(source: net.minecraft.world.damagesource.DamageSource): Boolean {
        val inv = inventory ?: return false
        for (slot in 0..<inv.slots) {
            val stack = inv.getStackInSlot(slot)
            if (stack.isEmpty) continue
            val item = stack.item
            if (item !is IShipConsumable) continue
            val canPrevent = ApiCallSafety.runWithDefault(
                "IShipConsumable.canPreventDeath", false
            ) { item.canPreventDeath(stack, ship, source) }
            if (!canPrevent) continue
            val prevented = ApiCallSafety.runWithDefault(
                "IShipConsumable.onPreventDeath", false
            ) { item.onPreventDeath(stack, ship, source) }
            if (prevented) {
                ship.customHurtTime = 120
                return true
            }
        }
        return false
    }

    /** Resolves a stable equipment type id for legacy or third-party equipment. */
    fun resolveTypeId(item: Item, stack: ItemStack): Int {
        return when (item) {
            is LegacyEquipItem -> item.getEquipTypeId(stack)
            is IShipEquip -> ApiCallSafety.runWithDefault(
                "IShipEquip.getEquipTypeId", -1
            ) { item.getEquipTypeId(stack) }
            else -> -1
        }
    }
}
