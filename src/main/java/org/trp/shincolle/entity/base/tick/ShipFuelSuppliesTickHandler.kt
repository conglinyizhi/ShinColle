package org.trp.shincolle.entity.base.tick

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.FluidUtil
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import org.trp.shincolle.Config
import org.trp.shincolle.api.ApiCallSafety
import org.trp.shincolle.api.consumable.IShipConsumable
import org.trp.shincolle.entity.base.EmotionParticleType
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.inventory.ShipInventoryHandler
import org.trp.shincolle.item.CombatRationItem
import org.trp.shincolle.item.LegacyEquipItem
import org.trp.shincolle.menu.ShipContainerMenu
import java.util.Comparator
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Fuel and supply upkeep: fuel decay, automatic recovery, ration consumption,
 * item pickup, pumping and XP bottling.
 */
object ShipFuelSuppliesTickHandler : ShipTickHandler {

    override fun tick(ship: EntityShipBase): Boolean {
        ship.combat.tickAircraftRecovery()

        if (ship.shouldRetreatForLowHealth()) {
            ship.pickupMovement.stop()
        } else {
            tickAutoPickupItems(ship)
        }

        tickAutoPump(ship)
        tickAutoRation(ship)

        tickFuelDecay(ship)
        tickAutoRecovery(ship)
        tickAutoSupplies(ship)

        return true
    }

    private fun tickFuelDecay(ship: EntityShipBase) {
        if (ship.isHostileShipMob) {
            return
        }
        if (ship.tickCount % Config.fuelDecayInterval != 0) {
            return
        }
        if (ship.fuel <= 0) {
            return
        }

        var consume = ship.getStateMinor(EntityShipBase.STATE_MINOR_GRUDGE_CONSUMPTION)

        val dist = sqrt(ship.distanceToSqr(ship.xo, ship.yo, ship.zo))
        consume += (dist * Config.fuelMoveDecayFactor).toInt()

        ship.fuel -= consume
    }

    private fun tickAutoRecovery(ship: EntityShipBase) {
        if (ship.isHostileShipMob) {
            return
        }

        if ((ship.tickCount and 0x1F) == 0
            && ship.health < ship.maxHealth * EntityShipBase.AUTO_HEAL_THRESHOLD_RATIO
        ) {
            if (ship.consumeItemInInventory(ModItems.BUCKET_REPAIR.get())) {
                ship.recordCreativeDebuggerBucketRepair()
                ship.heal(ship.maxHealth * EntityShipBase.AUTO_HEAL_FAST_RATIO + EntityShipBase.AUTO_HEAL_FAST_FLAT)
                if (ship.supportsAircraftCombat()) {
                    ship.numAircraftLight += 1
                    ship.numAircraftHeavy += 1
                }
                ship.applyParticleEmotion(EmotionParticleType.HEART)
            }
        }

        if ((ship.tickCount and 0xFF) == 0 && ship.health < ship.maxHealth) {
            ship.heal(ship.maxHealth * EntityShipBase.AUTO_HEAL_SLOW_RATIO + EntityShipBase.AUTO_HEAL_SLOW_FLAT)
        }
    }

    private fun tickAutoSupplies(ship: EntityShipBase) {
        if (ship.level().isClientSide || ship.isHostileShipMob) {
            return
        }

        if (ship.fuel <= 0) {
            val modFuel = ship.legacyShipStats.getBuffedAttr(17)
            if (ship.consumeItemInInventory(ModItems.GRUDGE.get())) {
                ship.fuel = (300 * modFuel).toInt()
                applyAutoSupplyEffects(ship)
            } else if (ship.consumeItemInInventory(ModItems.GRUDGE_BLOCK.get())) {
                ship.fuel = (2700 * modFuel).toInt()
                applyAutoSupplyEffects(ship)
            } else {
                val supplied = tryConsumableAutoSupply(ship, modFuel)
                if (supplied) {
                    applyAutoSupplyEffects(ship)
                }
            }
        }
    }

    private fun applyAutoSupplyEffects(ship: EntityShipBase) {
        if (ship.emotesTick <= 0) {
            ship.emotesTick = 40
            when (ship.random.nextInt(3)) {
                0 -> ship.applyParticleEmotion(EmotionParticleType.DROOL)
                1 -> ship.applyParticleEmotion(EmotionParticleType.BLINK)
                else -> ship.applyParticleEmotion(EmotionParticleType.SIGH)
            }
        }
    }

    private fun tryConsumableAutoSupply(ship: EntityShipBase, modFuel: Float): Boolean {
        val inv = ship.inventory ?: return false
        for (i in 0..<inv.slots) {
            val stack = inv.getStackInSlot(i)
            if (stack.isEmpty()) continue
            val item = stack.item
            if (item !is IShipConsumable) continue
            val canSupply = ApiCallSafety.runWithDefault(
                "IShipConsumable.canAutoSupplyGrudge", false
            ) { item.canAutoSupplyGrudge(stack, ship) }
            if (!canSupply) {
                continue
            }
            val amount = ApiCallSafety.runWithDefault(
                "IShipConsumable.getAutoSupplyGrudgeAmount", 0
            ) { item.getAutoSupplyGrudgeAmount(stack, ship) }
            if (amount > 0) {
                ship.fuel = (amount * modFuel).toInt()
                val updated = stack.copy()
                updated.shrink(1)
                inv.setStackInSlot(i, updated)
                return true
            }
        }
        return false
    }

    private fun tickAutoRation(ship: EntityShipBase) {
        if ((ship.tickCount % EntityShipBase.AUTO_RATION_INTERVAL_TICKS) != 0) {
            return
        }

        val threshold = max(1, min(4, ship.getStateMinor(ShipContainerMenu.STATE_MINOR_RATION_MORALE)))
        if (ship.getMoraleLevel() < threshold) {
            return
        }

        if (ship.fuel >= EntityShipBase.AUTO_RATION_MAX_FUEL && ship.health >= ship.maxHealth) {
            return
        }

        consumeOneCombatRation(ship)
    }

    private fun consumeOneCombatRation(ship: EntityShipBase): Boolean {
        val slotCount = ship.accessibleInventorySlotCount
        for (i in ShipInventoryHandler.equipSlotCount..<slotCount) {
            val stack = ship.inventory!!.getStackInSlot(i)
            if (stack.isEmpty()) {
                continue
            }

            if (stack.item !is CombatRationItem) {
                continue
            }

            val variant: Int = (stack.item as CombatRationItem).getVariant(stack)
            ship.applyCombatRationEffect(variant)
            stack.shrink(1)
            if (stack.isEmpty()) {
                ship.inventory.setStackInSlot(i, ItemStack.EMPTY)
            } else {
                ship.inventory.setStackInSlot(i, stack)
            }

            return true
        }

        return false
    }

    private fun tickAutoPickupItems(ship: EntityShipBase) {
        if (!ship.supportsItemPickup()) {
            ship.pickupMovement.stop()
            return
        }
        if (!ship.getStateFlag(ShipContainerMenu.STATE_FLAG_PICK_ITEM)) {
            ship.pickupMovement.stop()
            return
        }
        if ((ship.tickCount % EntityShipBase.PICK_ITEM_SCAN_INTERVAL_TICKS) != 0) {
            return
        }
        if (ship.isSitting || ship.isPassenger() || ship.isVehicle() || ship.isInDeadPose) {
            ship.pickupMovement.stop()
            return
        }
        if (ship.hasPointerTarget() || ship.hasPointerTargetEntity() || ship.target != null) {
            ship.pickupMovement.stop()
            return
        }
        if (!hasCargoRoom(ship)) {
            ship.pickupMovement.stop()
            return
        }

        val target = findNearestPickItem(ship)
        if (target == null) {
            ship.pickupMovement.stop()
            return
        }

        if (ship.distanceToSqr(target) <= 9.0) {
            tryPickupItemEntity(ship, target)
            ship.pickupMovement.stop()
        } else {
            ship.pickupMovement.moveTo(target, 1.0)
        }
    }

    private fun hasCargoRoom(ship: EntityShipBase): Boolean {
        val slotCount = ship.accessibleInventorySlotCount
        for (i in ShipInventoryHandler.equipSlotCount..<slotCount) {
            val stack = ship.inventory!!.getStackInSlot(i)
            if (stack.isEmpty()) {
                return true
            }
            val limit = min(stack.maxStackSize, ship.inventory.getSlotLimit(i))
            if (stack.count < limit) {
                return true
            }
        }
        return false
    }

    private fun findNearestPickItem(ship: EntityShipBase): ItemEntity? {
        val followCap = max(2.0, ship.getStateMinor(ShipContainerMenu.STATE_MINOR_FOLLOW_MAX).toDouble())
        val statRange = max(2.0, ship.legacyShipStats.attackRange * 0.5 + 2.0)
        val pickRange = min(followCap + 2.0, statRange)

        val scanBox = ship.boundingBox.inflate(pickRange, pickRange * 0.5 + 1.0, pickRange)
        val items = ship.level().getEntitiesOfClass<ItemEntity>(
            ItemEntity::class.java, scanBox
        ) { item -> item.isAlive && !item.item.isEmpty && !item.hasPickUpDelay() }
        if (items.isEmpty()) {
            return null
        }

        items.sortWith(Comparator { a, b ->
            java.lang.Double.compare(ship.distanceToSqr(a), ship.distanceToSqr(b))
        })
        return items[0]
    }

    private fun tryPickupItemEntity(ship: EntityShipBase, itemEntity: ItemEntity) {
        val stack = itemEntity.item
        if (stack.isEmpty()) {
            return
        }

        val originalCount = stack.count
        val remaining = insertIntoCargo(ship, stack.copy())
        val inserted = originalCount - remaining.count
        if (inserted <= 0) {
            return
        }

        if (remaining.isEmpty()) {
            itemEntity.discard()
        } else {
            itemEntity.setItem(remaining)
        }

        ship.playSound(
            SoundEvents.ITEM_PICKUP, 0.2f,
            ((ship.random.nextFloat() - ship.random.nextFloat()) * 0.7f + 1.0f) * 2.0f
        )
    }

    private fun insertIntoCargo(ship: EntityShipBase, stack: ItemStack): ItemStack {
        var remaining = stack
        val slotCount = ship.accessibleInventorySlotCount
        var i: Int = ShipInventoryHandler.equipSlotCount
        while (i < slotCount && !remaining.isEmpty()) {
            remaining = ship.inventory!!.insertItem(i, remaining, false)
            i++
        }
        return remaining
    }

    private fun tickAutoPump(ship: EntityShipBase) {
        if (!ship.getStateFlag(ShipContainerMenu.STATE_FLAG_AUTO_PUMP)) {
            return
        }
        if (!hasLiquidDrumEquip(ship)) {
            return
        }
        if (ship.isSitting || ship.isPassenger() || ship.isVehicle() || ship.isInDeadPose) {
            return
        }

        tickAutoPumpXp(ship)

        if ((ship.tickCount % EntityShipBase.AUTO_PUMP_INTERVAL_TICKS) != 0) {
            return
        }

        val sourcePos = findNearbyPumpSource(ship)
        if (sourcePos == null) {
            return
        }

        val fluidState = ship.level().getFluidState(sourcePos)
        if (!fluidState.`is`(Fluids.WATER) && !fluidState.`is`(Fluids.LAVA)) {
            return
        }

        val pumpedFluid = FluidStack(fluidState.type, FluidType.BUCKET_VOLUME)

        if (tryStorePumpedFluid(ship, pumpedFluid)) {
            if (ship.level() is ServerLevel) {
                ship.level().setBlockAndUpdate(sourcePos, Blocks.AIR.defaultBlockState())
            }
            val sound = if (fluidState.`is`(Fluids.LAVA)) SoundEvents.BUCKET_FILL_LAVA else SoundEvents.BUCKET_FILL
            ship.playSound(sound, 0.5f, ship.random.nextFloat() * 0.4f + 0.8f)
        }
    }

    private fun hasLiquidDrumEquip(ship: EntityShipBase): Boolean {
        val equipSlots = min(ShipInventoryHandler.equipSlotCount, ship.inventory!!.slots)
        for (slot in 0..<equipSlots) {
            val stack = ship.inventory.getStackInSlot(slot)
            if (stack.isEmpty() || stack.item !is LegacyEquipItem) {
                continue
            }
            if ((stack.item as LegacyEquipItem).getEquipTypeId(stack) == EntityShipBase.EQUIP_TYPE_DRUM
                && (stack.item as LegacyEquipItem).getVariant(stack) == EntityShipBase.EQUIP_DRUM_VARIANT_LIQUID
            ) {
                return true
            }
        }
        return false
    }

    private fun findNearbyPumpSource(ship: EntityShipBase): BlockPos? {
        val center = ship.blockPosition()
        var bestPos: BlockPos? = null
        var bestDist = Double.MAX_VALUE

        for (dx in -3..3) {
            for (dy in -1..1) {
                for (dz in -3..3) {
                    val pos = center.offset(dx, dy, dz)
                    val fluidState = ship.level().getFluidState(pos)
                    if (fluidState.isEmpty || !fluidState.isSource) {
                        continue
                    }
                    if (!fluidState.`is`(Fluids.WATER) && !fluidState.`is`(Fluids.LAVA)) {
                        continue
                    }
                    val dist = pos.distToCenterSqr(ship.position())
                    if (dist < bestDist) {
                        bestDist = dist
                        bestPos = pos
                    }
                }
            }
        }

        return bestPos
    }

    private fun tryStorePumpedFluid(ship: EntityShipBase, pumpedFluid: FluidStack): Boolean {
        val slotCount = ship.accessibleInventorySlotCount
        for (i in ShipInventoryHandler.equipSlotCount..<slotCount) {
            val stack = ship.inventory!!.getStackInSlot(i)
            if (stack.isEmpty()) {
                continue
            }

            val extracted = ship.inventory.extractItem(i, 1, false)
            if (extracted.isEmpty()) {
                continue
            }

            val handlerOptional = FluidUtil.getFluidHandler(extracted)
            if (handlerOptional.isEmpty) {
                val remainder = ship.inventory.insertItem(i, extracted, false)
                if (!remainder.isEmpty() && ship.level() is ServerLevel) {
                    val serverLevel = ship.level() as ServerLevel
                    serverLevel.addFreshEntity(
                        ItemEntity(
                            serverLevel,
                            ship.x,
                            ship.y,
                            ship.z,
                            remainder
                        )
                    )
                }
                continue
            }

            val handler = handlerOptional.get()
            if (handler.fill(pumpedFluid.copy(), IFluidHandler.FluidAction.SIMULATE) < pumpedFluid.amount) {
                val remainder = ship.inventory.insertItem(i, extracted, false)
                if (!remainder.isEmpty() && ship.level() is ServerLevel) {
                    val serverLevel = ship.level() as ServerLevel
                    serverLevel.addFreshEntity(
                        ItemEntity(
                            serverLevel,
                            ship.x,
                            ship.y,
                            ship.z,
                            remainder
                        )
                    )
                }
                continue
            }

            val filled = handler.fill(pumpedFluid.copy(), IFluidHandler.FluidAction.EXECUTE)
            val container = handler.container
            val remaining = ship.inventory.insertItem(i, container, false)
            if (!remaining.isEmpty() && ship.level() is ServerLevel) {
                val serverLevel = ship.level() as ServerLevel
                serverLevel.addFreshEntity(ItemEntity(serverLevel, ship.x, ship.y, ship.z, remaining))
            }
            if (filled >= pumpedFluid.amount) {
                return true
            }
        }
        return false
    }

    private fun tickAutoPumpXp(ship: EntityShipBase) {
        if ((ship.tickCount % EntityShipBase.AUTO_PUMP_XP_INTERVAL_TICKS) != 0) {
            return
        }

        if (ship.level() !is ServerLevel) {
            return
        }

        val orbs = ship.level().getEntitiesOfClass<ExperienceOrb>(
            ExperienceOrb::class.java,
            ship.boundingBox.inflate(7.0)
        )
        if (!orbs.isEmpty()) {
            for (orb in orbs) {
                if (!orb.isAlive) {
                    continue
                }

                val distSqr = ship.distanceToSqr(orb)
                if (distSqr > 9.0) {
                    val pull = ship.position().add(0.0, 0.4, 0.0)
                        .subtract(orb.position())
                        .normalize()
                        .scale(0.25)
                    orb.deltaMovement = orb.deltaMovement.add(pull)
                } else {
                    ship.setStateMinor(
                        EntityShipBase.STATE_MINOR_PUMPED_XP,
                        ship.getStateMinor(EntityShipBase.STATE_MINOR_PUMPED_XP) + orb.value
                    )
                    orb.discard()
                }
            }
        }

        var bottleSlot = findFirstCargoItem(ship, Items.GLASS_BOTTLE)
        while (bottleSlot >= 0 && ship.getStateMinor(EntityShipBase.STATE_MINOR_PUMPED_XP) >= EntityShipBase.XP_BOTTLE_COST) {
            val extracted = ship.inventory!!.extractItem(bottleSlot, 1, false)
            if (extracted.isEmpty()) {
                break
            }

            ship.setStateMinor(
                EntityShipBase.STATE_MINOR_PUMPED_XP,
                ship.getStateMinor(EntityShipBase.STATE_MINOR_PUMPED_XP) - EntityShipBase.XP_BOTTLE_COST
            )

            val remaining = insertIntoCargo(ship, ItemStack(Items.EXPERIENCE_BOTTLE))
            if (!remaining.isEmpty() && ship.level() is ServerLevel) {
                val serverLevel = ship.level() as ServerLevel
                serverLevel.addFreshEntity(ItemEntity(serverLevel, ship.x, ship.y, ship.z, remaining))
            }

            bottleSlot = findFirstCargoItem(ship, Items.GLASS_BOTTLE)
        }
    }

    private fun findFirstCargoItem(ship: EntityShipBase, item: net.minecraft.world.item.Item?): Int {
        val slotCount = ship.accessibleInventorySlotCount
        for (i in ShipInventoryHandler.equipSlotCount..<slotCount) {
            val stack = ship.inventory!!.getStackInSlot(i)
            if (!stack.isEmpty() && item != null && stack.`is`(item)) {
                return i
            }
        }
        return -1
    }
}
