package org.trp.shincolle.entity.base

import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.FlyingMob
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.player.Player
import org.trp.shincolle.Shincolle.Companion.diagnosticLog
import org.trp.shincolle.entity.EntityAircraftBase
import org.trp.shincolle.entity.base.ShipBrainMemory.PassiveCombatStateMemory
import org.trp.shincolle.menu.ShipContainerMenu
import org.trp.shincolle.server.TargetProtectionService.isPlayerConfiguredTargetClass
import org.trp.shincolle.server.TargetProtectionService.isUnattackableTargetClass
import org.trp.shincolle.server.TeamDiplomacyService.isDiplomaticAlly
import org.trp.shincolle.server.TeamDiplomacyService.isDiplomaticBanned
import java.util.function.Predicate
import java.util.function.ToDoubleFunction
import kotlin.math.max
import kotlin.math.min

internal class EntityShipBasePassiveCombat(private val ship: EntityShipBase) {
    private var passiveTargetScanTick = 0
    private var passiveTargetSightTick = 0
    private var passiveMeleeCooldownTick = 0
    private var passiveLightCooldownTick = 0
    private var passiveHeavyCooldownTick = 0
    private var nextCombatDiagTick = 0
    private var isFirstEngagementWaiting = false
    private var passiveLastHurtByMobTimestamp = 0
    private var passiveLastOwnerHurtByTimestamp = 0
    private var passiveLastOwnerHurtMobTimestamp = 0

    fun tickTargeting() {
        if (!canFight()) {
            clearTarget()
            return
        }

        val targetEntity = this.ship.pointerTargetEntity

        val pointerTarget = if (targetEntity is LivingEntity) targetEntity else null

        if (pointerTarget != null && canAttackTarget(pointerTarget, false, true)) {
            if (this.ship.getTarget() !== pointerTarget) {
                setPassiveCombatTarget(pointerTarget, true)
            }
        }

        val currentTarget = this.ship.getTarget()
        if (currentTarget != null) {
            val maxLostDistance: Double = this.passiveAcquireRangeSqr * PASSIVE_TARGET_LOST_DISTANCE_MULTIPLIER
            if (!isValidPassiveTarget(currentTarget) || this.ship.distanceToSqr(currentTarget) > maxLostDistance) {
                clearTarget()
            }
        }

        tryAcquireRevengeTargets()

        if (this.ship.getTarget() != null) {
            return
        }

        if (this.ship.getStateFlag(STATE_FLAG_PASSIVE_ATTACK)) {
            return
        }

        if (this.passiveTargetScanTick-- > 0) {
            return
        }
        this.passiveTargetScanTick = PASSIVE_TARGET_SCAN_INTERVAL

        tryAcquireNearbyCombatTarget()
    }

    fun updateActionState(): PassiveCombatStateMemory {
        val target = this.ship.getTarget()
        if (target == null) {
            return ShipBrainMemory.noPassiveCombatState()
        }

        var isRevenge = (target === this.ship.getLastHurtByMob())
        if (!isRevenge && this.ship.getOwner() != null) {
            isRevenge = (target === this.ship.getOwner()!!.getLastHurtByMob() || target === this.ship.getOwner()!!
                .getLastHurtMob())
        }

        val isCommanded = (target === this.ship.pointerTargetEntity)

        if (!isAttackAllowed(target, isRevenge, isCommanded)) {
            clearTarget()
            return ShipBrainMemory.noPassiveCombatState()
        }

        if (this.passiveMeleeCooldownTick > 0) {
            this.passiveMeleeCooldownTick--
        }
        if (this.passiveLightCooldownTick > 0) {
            this.passiveLightCooldownTick--
        }
        if (this.passiveHeavyCooldownTick > 0) {
            this.passiveHeavyCooldownTick--
        }

        val distanceSqr = this.ship.distanceToSqr(target)
        val onSight = this.ship.hasLineOfSight(target)
        if (onSight) {
            this.passiveTargetSightTick++
        } else {
            this.passiveTargetSightTick = 0
            if (this.ship.getStateFlag(STATE_FLAG_ON_SIGHT)) {
                clearTarget()
                return ShipBrainMemory.noPassiveCombatState()
            }
        }

        val combat = this.ship.combat
        val preferredRangeSqr = getPassivePreferredRangeSqr(target)
        val hasRangedAttack = combat.canUseLightAmmo()
                || combat.canUseHeavyAmmo()
                || combat.hasAircraftAttackEnabled()
        val stopRangeSqr = if (hasRangedAttack) preferredRangeSqr + 1.0 else preferredRangeSqr

        val needsCloser = distanceSqr > stopRangeSqr
        val cannotSee = !onSight && distanceSqr > preferredRangeSqr * 0.5
        val hasAttackMeans = hasRangedAttack || combat.canUseMeleeAttack()
        logCombatStateIfNeeded(
            target,
            distanceSqr,
            preferredRangeSqr,
            stopRangeSqr,
            needsCloser,
            cannotSee,
            hasAttackMeans
        )

        val needsMovement = needsCloser || cannotSee
        return PassiveCombatStateMemory(
            target.getUUID(),
            target.isAlive,
            target.position(),
            distanceSqr,
            preferredRangeSqr,
            stopRangeSqr,
            needsCloser,
            cannotSee,
            hasAttackMeans,
            this.ship.hasPointerTarget(),
            needsMovement,
            needsMovement && !this.ship.hasPointerTarget() && hasAttackMeans,
            this.passiveMoveSpeed,
            this.passiveTargetSightTick
        )
    }

    fun tickAttacks(state: PassiveCombatStateMemory) {
        val target = this.ship.getTarget()
        if (target == null || !state.hasTarget() || state.needsMovement) {
            return
        }

        if (!this.ship.shouldFollowOwner() && !this.ship.hasPointerTarget()) {
            this.ship.getMoveControl().setWantedPosition(
                this.ship.getX(), this.ship.getY(), this.ship.getZ(), 0.0
            )
        }

        if (!this.isFirstEngagementWaiting) {
            this.isFirstEngagementWaiting = true
            resetPassiveCombatCooldowns()
            return
        }

        if (state.sightTicks < this.passiveAimTime) {
            return
        }

        val combat = this.ship.combat
        if (combat.hasAircraftAttackEnabled()) {
            combat.tryPerformAircraftCycle(target)
        }

        if (combat.canUseLightAmmo() && this.passiveLightCooldownTick <= 0) {
            this.ship.performLightAttack(target)
            this.passiveLightCooldownTick = max(1, this.ship.legacyShipStats.lightDelay)
        }

        if (combat.canUseHeavyAmmo() && this.passiveHeavyCooldownTick <= 0) {
            this.ship.performHeavyAttack(target)
            this.passiveHeavyCooldownTick = max(1, this.ship.legacyShipStats.heavyDelay)
        }

        if (combat.canUseMeleeAttack()
            && this.passiveMeleeCooldownTick <= 0 && state.distanceSqr <= getPassiveAttackRangeSqr(target)
        ) {
            this.ship.doHurtTarget(target)
            this.passiveMeleeCooldownTick = max(1, this.ship.legacyShipStats.meleeDelay)
        }
    }

    private fun logCombatStateIfNeeded(
        target: LivingEntity, distanceSqr: Double, preferredRangeSqr: Double,
        stopRangeSqr: Double, needsCloser: Boolean, cannotSee: Boolean,
        hasAttackMeans: Boolean
    ) {
        if (this.ship.tickCount < this.nextCombatDiagTick) {
            return
        }
        this.nextCombatDiagTick = this.ship.tickCount + 40
        val owner = this.ship.getOwner()
        val ownerDistSq = if (owner == null) -1.0 else this.ship.distanceToSqr(owner)
        diagnosticLog(
            "[SCCombatDiag] tickActions ship={} target={} distanceSqr={} preferredRangeSqr={} stopRangeSqr={} needsCloser={} cannotSee={} hasAttackMeans={} ownerPresent={} ownerDistSq={} shouldFollow={} followReason={} pointer={}",
            this.ship.getUUID(),
            target.getUUID(),
            distanceSqr,
            preferredRangeSqr,
            stopRangeSqr,
            needsCloser,
            cannotSee,
            hasAttackMeans,
            owner != null,
            ownerDistSq,
            this.ship.shouldFollowOwner(),
            this.ship.explainFollowBlockReason(),
            this.ship.hasPointerTarget() || this.ship.hasPointerTargetEntity()
        )
    }

    fun clearTarget() {
        if (this.ship.getTarget() != null) {
            this.ship.setTarget(null)
        }
        this.passiveTargetSightTick = 0
        this.isFirstEngagementWaiting = false
    }

    private fun tryAcquireRevengeTargets() {
        tryAcquireSelfRevengeTarget()
        tryAcquireOwnerRevengeTarget()
    }

    private fun tryAcquireSelfRevengeTarget() {
        val attacker = this.ship.getLastHurtByMob()
        val timestamp = this.ship.getLastHurtByMobTimestamp()
        if (attacker != null && timestamp != this.passiveLastHurtByMobTimestamp) {
            this.passiveLastHurtByMobTimestamp = timestamp
            tryPromoteRevengeTarget(attacker)
        }
    }

    private fun tryAcquireOwnerRevengeTarget() {
        val owner = this.ship.getOwner()
        if (owner == null || this.ship.distanceToSqr(owner) > PASSIVE_OWNER_REVENGE_DISTANCE_SQR) {
            return
        }

        val ownerAttacker = owner.getLastHurtByMob()
        val ownerAttackerTimestamp = owner.getLastHurtByMobTimestamp()
        if (ownerAttacker != null && ownerAttackerTimestamp != this.passiveLastOwnerHurtByTimestamp) {
            this.passiveLastOwnerHurtByTimestamp = ownerAttackerTimestamp
            tryPromoteRevengeTarget(ownerAttacker)
        }

        val ownerTarget = owner.getLastHurtMob()
        val ownerTargetTimestamp = owner.getLastHurtMobTimestamp()
        if (ownerTarget != null && ownerTargetTimestamp != this.passiveLastOwnerHurtMobTimestamp) {
            this.passiveLastOwnerHurtMobTimestamp = ownerTargetTimestamp
            tryPromoteRevengeTarget(ownerTarget)
        }
    }

    private fun tryPromoteRevengeTarget(candidate: LivingEntity?) {
        if (!canAttackTarget(candidate, true, false)) {
            return
        }

        val currentTarget = this.ship.getTarget()
        if (currentTarget == null || !isValidPassiveTarget(currentTarget)) {
            setPassiveCombatTarget(candidate, true)
            return
        }

        if (this.ship.distanceToSqr(candidate) < this.ship.distanceToSqr(currentTarget)) {
            setPassiveCombatTarget(candidate, true)
        }
    }

    private fun tryAcquireNearbyCombatTarget() {
        val range = this.passiveAcquireRange
        val rangeY: Double = range * PASSIVE_TARGET_VERTICAL_RANGE_FACTOR
        val candidates = this.ship.level().getEntitiesOfClass<LivingEntity?>(
            LivingEntity::class.java,
            this.ship.getBoundingBox().inflate(range, rangeY, range),
            Predicate { target: LivingEntity? -> canAttackTarget(target, false, false) })
        if (candidates.isEmpty()) {
            return
        }

        val prioritized = pickPrioritizedTargets(candidates)
        prioritized.sortWith(Comparator.comparingDouble<LivingEntity?>(ToDoubleFunction { entity: LivingEntity? ->
            this.ship.distanceToSqr(
                entity
            )
        }))

        val selected: LivingEntity?
        if (prioritized.size > 2) {
            val pickBound = min(PASSIVE_TARGET_CHOICE_RANDOM_TOP, prioritized.size)
            selected = prioritized.get(this.ship.getRandom().nextInt(pickBound))
        } else {
            selected = prioritized.get(0)
        }

        setPassiveCombatTarget(selected, true)
    }

    private fun pickPrioritizedTargets(candidates: MutableList<LivingEntity>): MutableList<LivingEntity> {
        if (candidates.isEmpty()) {
            return candidates
        }

        val prioritized: MutableList<LivingEntity> = ArrayList<LivingEntity>()

        if (this.ship.getStateFlag(STATE_FLAG_ANTI_AIR)) {
            for (target in candidates) {
                if (isAntiAirTarget(target)) {
                    prioritized.add(target)
                }
            }
        }

        if (prioritized.isEmpty() && this.ship.getStateFlag(STATE_FLAG_ANTI_SUB)) {
            for (target in candidates) {
                if (isAntiSubTarget(target)) {
                    prioritized.add(target)
                }
            }
        }

        if (prioritized.isEmpty() && this.ship.getStateFlag(STATE_FLAG_PVP)) {
            for (target in candidates) {
                if (isPlayerOrShip(target)) {
                    prioritized.add(target)
                }
            }
        }

        return if (prioritized.isEmpty()) candidates else prioritized
    }

    private fun isAntiAirTarget(target: LivingEntity?): Boolean {
        return target is EntityAircraftBase || target is FlyingMob
    }

    private fun isAntiSubTarget(target: LivingEntity): Boolean {
        if (target.isInvisible()) {
            return true
        }
        return target is EntityShipBase && target.isInWaterOrBubble()
    }

    private fun setPassiveCombatTarget(target: LivingEntity?, resetCooldown: Boolean) {
        if (target == null) {
            clearTarget()
            return
        }

        this.ship.setTarget(target)
        this.passiveTargetSightTick = 0
        this.isFirstEngagementWaiting = false

        if (resetCooldown) {
            resetPassiveCombatCooldowns()
        }
    }

    private fun resetPassiveCombatCooldowns() {
        val aimTime = this.passiveAimTime
        this.passiveTargetScanTick = PASSIVE_TARGET_SCAN_INTERVAL

        if (this.passiveMeleeCooldownTick <= 20) {
            this.passiveMeleeCooldownTick = 20
        }
        if (this.passiveLightCooldownTick <= aimTime) {
            this.passiveLightCooldownTick = aimTime
        }
        if (this.passiveHeavyCooldownTick <= aimTime * 2) {
            this.passiveHeavyCooldownTick = aimTime * 2
        }

        this.ship.combat.resetAircraftLaunchDelay()
    }

    private fun shouldRetreatForLowHealth(): Boolean {
        val fleeHp = Mth.clamp(this.ship.getStateMinor(STATE_MINOR_FLEE_HP), 0, 100)
        if (fleeHp <= 0) {
            return false
        }
        return this.ship.getHealth() <= this.ship.getMaxHealth() * (fleeHp / 100.0f)
    }

    private fun isValidPassiveTarget(target: LivingEntity?): Boolean {
        if (target == null) return false

        val isRevenge = (target === this.ship.getLastHurtByMob())
        val isCommanded = (target === this.ship.pointerTargetEntity)
        return canAttackTarget(target, isRevenge, isCommanded)
    }

    private fun canAttackTarget(target: LivingEntity?, revengeContext: Boolean, commandContext: Boolean): Boolean {
        if (target == null) return false
        if (!isAttackAllowed(target, revengeContext, commandContext)) {
            return false
        }

        if (target === this.ship) {
            return false
        }
        if (!target.isAlive) {
            return false
        }
        if (target.isSpectator()) {
            return false
        }

        if (target is Player && target.getAbilities().invulnerable) {
            return false
        }

        if (isUnattackableTargetClass(target)) {
            return false
        }

        if (commandContext) {
            return true
        }

        val pvpEnabled = this.ship.getStateFlag(STATE_FLAG_PVP)

        if (isPlayerOrShip(target) && !pvpEnabled) {
            return false
        }

        if (target.isInvisible() && this.ship.getStateMinor(38) < 1 && this.ship.getStateMinor(39) < 1) {
            return false
        }

        if (isFriendlyTarget(target)) {
            return false
        }

        if (isDiplomaticAlly(target)) {
            return false
        }

        if (this.ship.ownerUUID != null) {
            if (target is Enemy) {
                return true
            }
            if (target is EntityShipBase && target.ownerUUID == null) {
                return true
            }
            if (isPlayerConfiguredTargetClass(target)) {
                return true
            }
            if (isDiplomaticBanned(target)) {
                return true
            }
            return isPlayerOrShip(target) && pvpEnabled
        }

        if (target is EntityShipBase && target.ownerUUID == null) {
            return false
        }

        if (target is Enemy) {
            return true
        }

        if (isPlayerOrShip(target)) {
            return true
        }

        if (target is TamableAnimal) {
            return target.ownerUUID != null
        }

        return revengeContext
    }

    private fun isFriendlyTarget(target: Entity?): Boolean {
        if (target == null) {
            return false
        }

        if (target === this.ship) {
            return true
        }

        if (sharesOwner(target)) {
            return true
        }

        if (target is EntityMountBase) {
            val host = target.host
            if (host != null) {
                return host.ownerUUID == this.ship.ownerUUID
            }
            return target.hostUUID == this.ship.ownerUUID
        }

        return target is EntityShipBase
                && this.ship.ownerUUID == null && target.ownerUUID == null
    }

    private fun sharesOwner(target: Entity?): Boolean {
        if (this.ship.ownerUUID == null) {
            return false
        }

        if (target is Player) {
            return this.ship.ownerUUID == target.getUUID()
        }

        if (target is TamableAnimal) {
            return this.ship.ownerUUID == target.ownerUUID
        }

        return false
    }

    private fun isPlayerOrShip(target: Entity?): Boolean {
        return target is Player || target is EntityShipBase
    }

    private fun isUnattackableTargetClass(target: LivingEntity?): Boolean {
        return isUnattackableTargetClass(this.ship, target)
    }

    private fun isPlayerConfiguredTargetClass(target: Entity?): Boolean {
        return isPlayerConfiguredTargetClass(this.ship, target)
    }

    private fun isDiplomaticAlly(target: Entity?): Boolean {
        return isDiplomaticAlly(this.ship, target)
    }

    private fun isDiplomaticBanned(target: Entity?): Boolean {
        return isDiplomaticBanned(this.ship, target)
    }

    private val passiveAimTime: Int
        get() = max(5, (20.0f * (150 - this.ship.level) / 150.0f).toInt() + 10)

    private val passiveAcquireRange: Double
        get() {
            var range = max(2.0, this.ship.legacyShipStats.attackRange.toDouble())
            if (this.ship.combat.hasAircraftAttackEnabled()) {
                range = max(range, this.ship.legacyShipStats.attackRange * 1.5)
            }
            return range
        }

    private val passiveAcquireRangeSqr: Double
        get() {
            val range = this.passiveAcquireRange
            return range * range
        }

    private fun getPassiveAttackRangeSqr(target: Entity): Double {
        val width = (this.ship.getBbWidth() * 2.0f).toDouble()
        val reach = width * width + target.getBbWidth()
        return max(reach, 4.0)
    }

    private fun getPassivePreferredRangeSqr(target: Entity): Double {
        val combat = this.ship.combat

        if (combat.canUseLightAmmo() || combat.canUseHeavyAmmo()) {
            val range = max(2.0, this.ship.legacyShipStats.attackRange.toDouble())
            return range * range
        }

        if (combat.hasAircraftAttackEnabled()) {
            val range = max(24.0, this.ship.legacyShipStats.attackRange * 1.5)
            return range * range
        }

        return getPassiveAttackRangeSqr(target)
    }

    private val passiveMoveSpeed: Double
        get() {
            val speed = this.ship.legacyShipStats.moveSpeed * 3.0
            return Mth.clamp(
                speed,
                ShipAiNumbers.PASSIVE_COMBAT_MOVE_SPEED_MIN,
                ShipAiNumbers.PASSIVE_COMBAT_MOVE_SPEED_MAX
            )
        }

    private fun canFight(): Boolean {
        if (shouldRetreatForLowHealth() || this.ship.isInSittingPose ||
            this.ship.isInDeadPose || this.ship.isPassenger() || this.ship.isVehicle()
        ) {
            return false
        }
        return !this.ship.isNoFuel && !this.ship.isNonCombatShip
    }

    private fun isAttackAllowed(target: LivingEntity?, isRevenge: Boolean, isCommanded: Boolean): Boolean {
        if (!canFight()) return false

        if (isRevenge || isCommanded) return true

        return !this.ship.getStateFlag(STATE_FLAG_PASSIVE_ATTACK)
    }

    companion object {
        private const val STATE_FLAG_ON_SIGHT = 12
        private const val STATE_FLAG_PVP = 18
        private const val STATE_FLAG_ANTI_AIR = 19
        private const val STATE_FLAG_ANTI_SUB = 20
        private const val STATE_FLAG_PASSIVE_ATTACK = 21

        private val STATE_MINOR_FLEE_HP = ShipContainerMenu.STATE_MINOR_FLEE_HP

        private const val PASSIVE_TARGET_SCAN_INTERVAL = 8
        private const val PASSIVE_TARGET_CHOICE_RANDOM_TOP = 3
        private val PASSIVE_OWNER_REVENGE_DISTANCE_SQR = 32 * 32
        private const val PASSIVE_TARGET_VERTICAL_RANGE_FACTOR = 0.75
        private const val PASSIVE_TARGET_LOST_DISTANCE_MULTIPLIER = 1.5
    }
}
