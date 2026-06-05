package org.trp.shincolle.entity.base

import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.Config
import org.trp.shincolle.entity.EntityAircraftBase
import org.trp.shincolle.entity.projectile.EntityAbyssMissile
import org.trp.shincolle.entity.projectile.EntityAbyssMissile.MoveType
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.init.ModSounds
import org.trp.shincolle.item.CombatRationItem.getVariant
import org.trp.shincolle.item.LegacyEquipItem
import org.trp.shincolle.item.LegacyEquipItem.getEquipTypeId
import org.trp.shincolle.item.LegacyEquipItem.getVariant
import kotlin.math.max
import kotlin.math.min

internal class EntityShipBaseCombat(private val ship: EntityShipBase) {
    private var aircraftRecoveryTick = 0
    private var aircraftLaunchDelay = 20
    private var aircraftLaunchTypeLight = false

    fun canUseLightAmmo(): Boolean {
        return this.ship.isStateGuiBtn1()
                && this.ship.isStateLightAttack
                && this.ship.ammoLight > 0
    }

    fun canUseHeavyAmmo(): Boolean {
        return this.ship.isStateGuiBtn2()
                && this.ship.isStateHeavyAttack
                && this.ship.ammoHeavy > 0
    }

    fun canUseMeleeAttack(): Boolean {
        return this.ship.isStateCanMelee
    }

    fun canUseLightAircraft(): Boolean {
        return this.ship.isStateGuiBtn3()
                && this.ship.isStateLightAircraftAttack
                && this.ship.hasAirLight()
                && this.ship.ammoLight >= AIRCRAFT_LIGHT_AMMO_COST
    }

    fun canUseHeavyAircraft(): Boolean {
        return this.ship.isStateGuiBtn4()
                && this.ship.isStateHeavyAircraftAttack
                && this.ship.hasAirHeavy()
                && this.ship.ammoHeavy >= AIRCRAFT_HEAVY_AMMO_COST
    }

    fun hasAircraftAttackEnabled(): Boolean {
        return canUseLightAircraft() || canUseHeavyAircraft()
    }

    fun tickAircraftRecovery() {
        if (!this.ship.supportsAircraftCombat()) {
            return
        }

        val maxLight = this.maxAircraftLight
        val maxHeavy = this.maxAircraftHeavy
        if (maxLight <= 0 && maxHeavy <= 0) {
            return
        }

        if (this.ship.getNumAircraftLight() <= 0 && this.ship.getNumAircraftHeavy() <= 0 && this.ship.tickCount < 20) {
            this.ship.setNumAircraftLight(maxLight)
            this.ship.setNumAircraftHeavy(maxHeavy)
        }

        if (this.ship.getNumAircraftLight() > maxLight) {
            this.ship.setNumAircraftLight(maxLight)
        }
        if (this.ship.getNumAircraftHeavy() > maxHeavy) {
            this.ship.setNumAircraftHeavy(maxHeavy)
        }

        this.aircraftRecoveryTick--
        if (this.aircraftRecoveryTick > 0) {
            return
        }

        this.aircraftRecoveryTick = max(20, AIRCRAFT_RECOVERY_BASE_DELAY)
        if (this.ship.getNumAircraftLight() < maxLight) {
            this.ship.setNumAircraftLight(this.ship.getNumAircraftLight() + 1)
        }
        if (this.ship.getNumAircraftHeavy() < maxHeavy) {
            this.ship.setNumAircraftHeavy(this.ship.getNumAircraftHeavy() + 1)
        }
    }

    fun tryPerformAircraftCycle(target: Entity?): Boolean {
        if (!this.ship.supportsAircraftCombat()) {
            return false
        }
        if (this.ship.level() !is ServerLevel) {
            return false
        }
        if (target == null || !target.isAlive) {
            return false
        }

        this.aircraftLaunchDelay--
        if (!this.ship.isStateLightAircraftAttack) {
            this.aircraftLaunchTypeLight = false
        }
        if (!this.ship.isStateHeavyAircraftAttack) {
            this.aircraftLaunchTypeLight = true
        }

        if (this.aircraftLaunchDelay > 0) {
            return false
        }

        var launched = false
        if (this.aircraftLaunchTypeLight) {
            launched = performLightAircraftAttack(target)
            if (!launched) {
                launched = performHeavyAircraftAttack(target)
            }
        } else {
            launched = performHeavyAircraftAttack(target)
            if (!launched) {
                launched = performLightAircraftAttack(target)
            }
        }

        this.aircraftLaunchTypeLight = !this.aircraftLaunchTypeLight
        if (launched) {
            val lightDelay = this.ship.legacyShipStats.getLightDelay()
            val heavyDelay = this.ship.legacyShipStats.getHeavyDelay()
            val delay = max(20, max(lightDelay, heavyDelay))
            this.aircraftLaunchDelay = delay
            return true
        }

        this.aircraftLaunchDelay = AIRCRAFT_COOLDOWN_FALLBACK
        return false
    }

    fun recalculateAmmoCounts() {
        if (this.ship.hasCreativeDebugger()) {
            this.ship.ammoLight = 30000
            this.ship.ammoHeavy = 30000
            return
        }
        var light = 0
        var heavy = 0
        for (i in 0..<this.ship.inventory.getSlots()) {
            val stack = this.ship.inventory.getStackInSlot(i)
            if (stack.isEmpty()) {
                continue
            }
            if (isLightAmmo(stack)) {
                light += stack.getCount() * AMMO_LIGHT_VALUE
            } else if (isLightAmmoContainer(stack)) {
                light += stack.getCount() * AMMO_LIGHT_CONTAINER_VALUE
            } else if (isHeavyAmmo(stack)) {
                heavy += stack.getCount() * AMMO_HEAVY_VALUE
            } else if (isHeavyAmmoContainer(stack)) {
                heavy += stack.getCount() * AMMO_HEAVY_CONTAINER_VALUE
            }
        }
        this.ship.ammoLight = light
        this.ship.ammoHeavy = heavy
    }

    fun performLightAttack(target: Entity?) {
        if (!canUseLightAmmo()) {
            return
        }
        if (this.ship.level() !is ServerLevel) {
            return
        }
        if (target == null || !target.isAlive) {
            return
        }
        if (isSameOwner(target)) {
            return
        }
        if (!consumeLightAmmo(1)) {
            return
        }

        var damage = this.ship.legacyShipStats.getFirepower()
        if (damage <= 0.0f) {
            damage = 2.0f
        }
        target.hurt(this.ship.damageSources().mobAttack(this.ship), damage)
        this.ship.spawnLightAttackTargetParticles(serverLevel, target)
        this.ship.spawnLightAttackMuzzleParticles(serverLevel, target)
        this.ship.playSound(
            ModSounds.SHIP_FIRELIGHT.get(), max(0.0f, Config.volumeAttack),
            this.ship.getRandom().nextFloat() * 0.12f + 0.98f
        )
        this.ship.attackTick = 50
        this.ship.fuel = this.ship.fuel - Config.fuelConsumeActionLight
        this.ship.applyEmotesReaction(3)
    }

    fun performHeavyAttack(target: Entity?): Boolean {
        if (!canUseHeavyAmmo()) {
            return false
        }
        if (this.ship.level() !is ServerLevel) {
            return false
        }
        if (target == null || !target.isAlive) {
            return false
        }
        if (isSameOwner(target)) {
            return false
        }
        if (Config.enableFiringLineCheck && !hasClearFiringLine(target)) {
            return false
        }
        if (!consumeHeavyAmmo(1)) {
            return false
        }

        var damage = this.ship.legacyShipStats.getFirepower()
        if (damage <= 0.0f) {
            damage = 4.0f
        }
        val missileDamage: Float = damage * HEAVY_MISSILE_DAMAGE_MULTIPLIER

        val missile = createHeavyMissile(serverLevel, target, missileDamage)
        serverLevel.addFreshEntity(missile)
        this.ship.playSound(
            ModSounds.SHIP_FIREHEAVY.get(), max(0.0f, Config.volumeAttack),
            this.ship.getRandom().nextFloat() * 0.12f + 0.83f
        )
        this.ship.attackTick = 50
        this.ship.fuel = this.ship.fuel - Config.fuelConsumeActionHeavy
        this.ship.applyEmotesReaction(3)
        return true
    }

    private fun createHeavyMissile(serverLevel: ServerLevel, target: Entity?, damage: Float): EntityAbyssMissile {
        val specialAmmoVariant = this.ship.getSpecialAmmoVariant()
        val torpedoSpeedLevel = this.ship.getTorpedoSpeedLevel()

        var moveType = MoveType.DIRECT
        var speed: Float = HEAVY_MISSILE_SPEED
        var accY1 = 1.04f
        var accY2 = 1.04f
        var explosionRadius: Float = HEAVY_MISSILE_EXPLOSION_RADIUS
        val presetVelocity: Vec3? = null

        if (torpedoSpeedLevel > 0) {
            moveType = MoveType.TORPEDO
            speed += torpedoSpeedLevel * TORPEDO_SPEED_STEP
            accY2 = 1.05f + torpedoSpeedLevel * TORPEDO_ACCEL_STEP
        }

        if (specialAmmoVariant == 5) {
            moveType = MoveType.ARC_HOMING
            accY1 = 0.9f
            accY2 = 0.9f
            explosionRadius += 0.5f
        } else if (specialAmmoVariant == 8) {
            moveType = MoveType.ARC
            explosionRadius += 1.0f
        } else if (specialAmmoVariant == 6) {
            moveType = MoveType.DIRECT
            accY1 = -0.045f
            accY2 = -0.045f
        }

        val missile = EntityAbyssMissile(
            serverLevel, this.ship, target, damage,
            moveType, speed, accY1, accY2, presetVelocity, HEAVY_MISSILE_LIFE, explosionRadius
        )
        if (specialAmmoVariant == 8) {
            missile.markClusterMain()
        } else if (specialAmmoVariant == 5) {
            missile.markBlackHole()
        }
        configureAmmoEffects(missile)
        return missile
    }

    private fun configureAmmoEffects(missile: EntityAbyssMissile) {
        for (i in 0..<this.ship.inventory.getSlots()) {
            val stack = this.ship.inventory.getStackInSlot(i)
            if (stack.isEmpty() || stack.getItem() !is LegacyEquipItem) {
                continue
            }
            if (equipItem.getEquipTypeId(stack) != 29) {
                continue
            }

            val variant: Int = equipItem.getVariant(stack)
            when (variant) {
                0 -> missile.addImpactEffect(MobEffects.POISON, 0, 120, 50)
                1 -> missile.addImpactEffect(MobEffects.POISON, 1, 120, 70)
                3 -> missile.addImpactEffect(MobEffects.CONFUSION, 0, 120, 50)
                4 -> missile.addImpactEffect(MobEffects.WITHER, 0, 100, 25)
                6 -> missile.addImpactEffect(MobEffects.LEVITATION, 0, 100, 50)
                7 -> addEnchantShellEffects(missile, stack)
                else -> {}
            }
        }
    }

    private fun addEnchantShellEffects(missile: EntityAbyssMissile, stack: ItemStack) {
        val customData = stack.get<CustomData?>(DataComponents.CUSTOM_DATA)
        if (customData == null) {
            return
        }

        val tag = customData.copyTag()
        if (!tag.contains(TAG_POTION_LIST, Tag.TAG_LIST.toInt())) {
            return
        }

        val effectList = tag.getList(TAG_POTION_LIST, Tag.TAG_COMPOUND.toInt())
        for (i in effectList.indices) {
            val effectTag = effectList.getCompound(i)
            val effect = BuiltInRegistries.MOB_EFFECT.byId(effectTag.getInt(TAG_POTION_ID))
            if (effect == null) {
                continue
            }
            missile.addImpactEffect(
                Holder.direct<MobEffect?>(effect),
                effectTag.getInt(TAG_POTION_LEVEL),
                effectTag.getInt(TAG_POTION_TIME),
                effectTag.getInt(TAG_POTION_CHANCE)
            )
        }
    }

    fun consumeHeavyAmmo(amount: Int): Boolean {
        if (amount <= 0) {
            return true
        }
        if (this.ship.hasCreativeDebugger()) {
            return true
        }
        var remaining = amount
        var i = 0
        while (i < this.ship.inventory.getSlots() && remaining > 0) {
            val stack = this.ship.inventory.getStackInSlot(i)
            if (stack.isEmpty()) {
                i++
                continue
            }
            if (isHeavyAmmo(stack)) {
                val take = min(stack.getCount(), remaining)
                val updated = stack.copy()
                updated.shrink(take)
                this.ship.inventory.setStackInSlot(i, updated)
                remaining -= take
            } else if (isHeavyAmmoContainer(stack)) {
                if (stack.getCount() <= 0) {
                    i++
                    continue
                }
                val updated = stack.copy()
                updated.shrink(1)
                this.ship.inventory.setStackInSlot(i, updated)

                val used = min(remaining, AMMO_HEAVY_CONTAINER_VALUE)
                val leftover: Int = AMMO_HEAVY_CONTAINER_VALUE - used
                remaining -= used

                if (leftover > 0) {
                    insertAmmoRemainder(ModItems.AMMO_HEAVY.get(), leftover, i)
                }
            }
            i++
        }
        return remaining <= 0
    }

    fun consumeLightAmmo(amount: Int): Boolean {
        if (amount <= 0) {
            return true
        }
        if (this.ship.hasCreativeDebugger()) {
            return true
        }
        var remaining = amount
        var i = 0
        while (i < this.ship.inventory.getSlots() && remaining > 0) {
            val stack = this.ship.inventory.getStackInSlot(i)
            if (stack.isEmpty()) {
                i++
                continue
            }
            if (isLightAmmo(stack)) {
                val take = min(stack.getCount(), remaining)
                val updated = stack.copy()
                updated.shrink(take)
                this.ship.inventory.setStackInSlot(i, updated)
                remaining -= take
            } else if (isLightAmmoContainer(stack)) {
                if (stack.getCount() <= 0) {
                    i++
                    continue
                }
                val updated = stack.copy()
                updated.shrink(1)
                this.ship.inventory.setStackInSlot(i, updated)

                val used = min(remaining, AMMO_LIGHT_CONTAINER_VALUE)
                val leftover: Int = AMMO_LIGHT_CONTAINER_VALUE - used
                remaining -= used

                if (leftover > 0) {
                    insertAmmoRemainder(ModItems.AMMO_LIGHT.get(), leftover, i)
                }
            }
            i++
        }
        return remaining <= 0
    }

    private fun isLightAmmo(stack: ItemStack): Boolean {
        return stack.`is`(ModItems.AMMO_LIGHT.get())
    }

    fun isSameOwnerTarget(target: Entity?): Boolean {
        return isSameOwner(target)
    }

    fun returnAircraftToDeck(lightAircraft: Boolean) {
        if (!this.ship.supportsAircraftCombat()) {
            return
        }
        if (lightAircraft) {
            val max = this.maxAircraftLight
            this.ship.setNumAircraftLight(min(max, this.ship.getNumAircraftLight() + 1))
        } else {
            val max = this.maxAircraftHeavy
            this.ship.setNumAircraftHeavy(min(max, this.ship.getNumAircraftHeavy() + 1))
        }
    }

    private fun isLightAmmoContainer(stack: ItemStack): Boolean {
        return stack.`is`(ModItems.AMMO_LIGHT_CONTAINER.get())
    }

    private fun isHeavyAmmo(stack: ItemStack): Boolean {
        return stack.`is`(ModItems.AMMO_HEAVY.get())
    }

    private fun isHeavyAmmoContainer(stack: ItemStack): Boolean {
        return stack.`is`(ModItems.AMMO_HEAVY_CONTAINER.get())
    }

    private fun insertAmmoRemainder(item: Item, count: Int, avoidSlot: Int) {
        if (count <= 0) {
            return
        }
        var remaining = count
        val maxStackSize = item.getDefaultInstance().getMaxStackSize()
        while (remaining > 0) {
            val stack = ItemStack(item, min(remaining, maxStackSize))
            var leftover = stack
            var i = 0
            while (i < this.ship.inventory.getSlots() && !leftover.isEmpty()) {
                if (i == avoidSlot) {
                    i++
                    continue
                }
                leftover = this.ship.inventory.insertItem(i, leftover, false)
                i++
            }
            val inserted = stack.getCount() - leftover.getCount()
            remaining -= inserted
            if (!leftover.isEmpty()) {
                if (this.ship.level() is ServerLevel) {
                    serverLevel.addFreshEntity(
                        ItemEntity(
                            serverLevel,
                            this.ship.getX(),
                            this.ship.getY(),
                            this.ship.getZ(),
                            leftover
                        )
                    )
                }
                remaining -= leftover.getCount()
            }
        }
    }

    private val maxAircraftLight: Int
        get() = 8 + this.ship.level / 5 + (this.ship.level * this.ship.getAircraftLightLevelBonus()).toInt()

    private val maxAircraftHeavy: Int
        get() = 4 + this.ship.level / 10 + (this.ship.level * this.ship.getAircraftHeavyLevelBonus()).toInt()

    private fun performLightAircraftAttack(target: Entity?): Boolean {
        if (!canUseLightAircraft()) {
            return false
        }
        return spawnAircraft(target, true)
    }

    private fun performHeavyAircraftAttack(target: Entity?): Boolean {
        if (!canUseHeavyAircraft()) {
            return false
        }
        return spawnAircraft(target, false)
    }

    private fun spawnAircraft(target: Entity?, lightAircraft: Boolean): Boolean {
        val type = this.ship.getAttackAircraftType(lightAircraft)
        if (type == null || this.ship.level() !is ServerLevel) {
            return false
        }

        val spawned: Entity? = type.create(serverLevel)
        if (spawned !is EntityAircraftBase) {
            return false
        }

        if (lightAircraft) {
            if (!consumeLightAmmo(AIRCRAFT_LIGHT_AMMO_COST)) {
                return false
            }
            this.ship.setNumAircraftLight(max(0, this.ship.getNumAircraftLight() - 1))
            this.ship.fuel = this.ship.fuel - Config.fuelConsumeActionLightAircraft
        } else {
            if (!consumeHeavyAmmo(AIRCRAFT_HEAVY_AMMO_COST)) {
                return false
            }
            this.ship.setNumAircraftHeavy(max(0, this.ship.getNumAircraftHeavy() - 1))
            this.ship.fuel = this.ship.fuel - Config.fuelConsumeActionHeavyAircraft
        }

        val launchY = this.ship.getY() + this.ship.getAircraftLaunchHeight()
        spawned.moveTo(this.ship.getX(), launchY, this.ship.getZ(), this.ship.getYRot(), this.ship.getXRot())
        spawned.initCarrierMission(this.ship, target, lightAircraft)
        serverLevel.addFreshEntity(spawned)

        this.ship.attackTick = 50
        this.ship.applyEmotesReaction(3)
        return true
    }

    fun resetAircraftLaunchDelay() {
        val lightDelay = this.ship.legacyShipStats.getLightDelay()
        val heavyDelay = this.ship.legacyShipStats.getHeavyDelay()
        this.aircraftLaunchDelay = max(20, max(lightDelay, heavyDelay))
    }

    /**
     * Quick check: is there at least MIN_FIRE_CLEAR_DISTANCE of open space
     * between the ship and the target? Prevents missiles from exploding
     * on blocks right in front of the ship (self-damage / 炸膛).
     */
    private fun hasClearFiringLine(target: Entity): Boolean {
        val level = this.ship.level()
        val start = this.ship.getEyePosition()
        val end = target.getEyePosition()
        val dir = end.subtract(start)
        val dist = dir.length()
        if (dist <= MIN_FIRE_CLEAR_DISTANCE) {
            return false // Target too close, don't risk self-damage
        }
        // Raycast from ship toward target, check for blocks
        val ctx = ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.ship)
        val hit = level.clip(ctx) as BlockHitResult
        if (hit.getType() == HitResult.Type.BLOCK) {
            val blockDist = start.distanceTo(hit.getLocation())
            if (blockDist < MIN_FIRE_CLEAR_DISTANCE) {
                return false // Block too close, skip firing
            }
        }
        return true
    }

    /**
     * Check if the target is a ship (TamableAnimal) owned by the same player.
     */
    private fun isSameOwner(target: Entity?): Boolean {
        val shipOwnerId = this.ship.getOwnerUUID()
        if (shipOwnerId == null) return false

        if (target is Player) {
            return shipOwnerId == target.getUUID()
        }
        if (target is EntityShipBase) {
            return shipOwnerId == target.getOwnerUUID()
        }
        if (target is TamableAnimal) {
            return shipOwnerId == target.getOwnerUUID()
        }
        if (target is EntityMountBase) {
            val host = target.getHost()
            if (host != null) {
                return shipOwnerId == host.getOwnerUUID()
            }
            return shipOwnerId == target.getHostUUID()
        }
        if (target is EntityAircraftBase) {
            return shipOwnerId == target.getOwnerUUID()
        }
        return false
    }

    companion object {
        private const val HEAVY_MISSILE_DAMAGE_MULTIPLIER = 1.4f
        private const val HEAVY_MISSILE_SPEED = 0.7f
        private const val HEAVY_MISSILE_LIFE = 200
        private const val HEAVY_MISSILE_EXPLOSION_RADIUS = 3.5f
        private const val MIN_FIRE_CLEAR_DISTANCE = 4.5f
        private const val TORPEDO_SPEED_STEP = 0.025f
        private const val TORPEDO_ACCEL_STEP = 0.004f
        private const val TAG_POTION_LIST = "PList"
        private const val TAG_POTION_ID = "PID"
        private const val TAG_POTION_LEVEL = "PLV"
        private const val TAG_POTION_TIME = "PTick"
        private const val TAG_POTION_CHANCE = "PChance"

        private const val AMMO_LIGHT_VALUE = 30
        private const val AMMO_LIGHT_CONTAINER_VALUE = 270
        private const val AMMO_HEAVY_VALUE = 15
        private const val AMMO_HEAVY_CONTAINER_VALUE = 135
        private const val AIRCRAFT_LIGHT_AMMO_COST = 6
        private const val AIRCRAFT_HEAVY_AMMO_COST = 2
        private const val AIRCRAFT_RECOVERY_BASE_DELAY = 120
        private const val AIRCRAFT_COOLDOWN_FALLBACK = 40
    }
}
