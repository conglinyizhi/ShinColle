package org.trp.shincolle.entity.base

import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.menu.ShipContainerMenu
import java.util.*

object ShipBrainMemory {
    fun pointerTarget(ship: EntityShipBase): PointerTargetMemory {
        val rawTarget = ship.getRawPointerTarget()
        val target = ship.getPointerTarget()
        val targetEntity = ship.getPointerTargetEntity()
        val hasEntityTargetCommand = ship.hasPointerTargetEntity()
        val entityDistanceSqr = if (targetEntity == null) -1.0 else ship.distanceToSqr(targetEntity)
        val combat = ship.getCombat()
        val entityDecision = ShipPointerEntityDecisionResolver.resolve(
            ShipPointerEntityDecisionResolver.State(
                targetEntity != null,
                entityDistanceSqr,
                targetEntity != null && ship.hasLineOfSight(targetEntity),
                combat.canUseLightAmmo(),
                combat.canUseHeavyAmmo(),
                combat.hasAircraftAttackEnabled(),
                combat.canUseMeleeAttack(),
                ship.getLegacyShipStats().getAttackRange().toDouble(),
                ship.getBbWidth().toDouble(),
                if (targetEntity == null) 0.0 else targetEntity.getBbWidth().toDouble()
            )
        )
        return PointerTargetMemory(
            ship.hasPointerTarget(),
            rawTarget,
            target,
            ship.getPointerTargetRemainingTicks(),
            hasEntityTargetCommand,
            if (targetEntity != null) targetEntity.getUUID() else null,
            targetEntity != null && targetEntity.isAlive(),
            if (targetEntity != null) targetEntity.position() else null,
            ship.getPointerTargetEntityRemainingTicks(),
            entityDistanceSqr,
            entityDecision.preferredRangeSqr,
            entityDecision.stopRangeSqr,
            entityDecision.needsCloser,
            entityDecision.cannotSee,
            entityDecision.shouldChase,
            entityDecision.attackRangeSqr,
            entityDecision.hasRangedAttack,
            entityDecision.canMeleeAttack
        )
    }

    fun guardTarget(ship: EntityShipBase): GuardTargetMemory {
        val target = ship.getGuardTarget()
        val guarded = ship.getGuardedEntity()
        val disabled = ship.getStateFlag(EntityShipBase.Companion.STATE_FLAG_DISABLE_GUARD_POS)
        val canGuard = target.isActive()
                && !disabled && (target.isBlock() && target.isIn(ship.level())
                || target.isEntity() && guarded != null && guarded.isAlive())
        return GuardTargetMemory(
            target,
            canGuard,
            disabled,
            if (guarded != null) guarded.getUUID() else null,
            guarded != null && guarded.isAlive(),
            if (guarded != null) guarded.position() else null,
            if (target.isBlock()) target.blockCenter() else null,
            target.dimensionId
        )
    }

    fun followState(ship: EntityShipBase): FollowStateMemory {
        val owner = ship.getOwner()
        val ownerHasCombatRation = owner is Player && ship.playerHasCombatRation(owner)
        return FollowStateMemory(
            ship.shouldFollowOwner(),
            ship.explainFollowBlockReason(),
            owner != null,
            if (owner != null) owner.getUUID() else null,
            if (owner != null) owner.position() else null,
            if (owner != null) owner.getEyeY() else 0.0,
            if (owner != null) EntityShipBase.Companion.getLegacyDimensionId(owner.level()) else 0,
            ownerHasCombatRation,
            if (owner == null) -1.0 else ship.distanceToSqr(owner),
            ship.getStateMinor(ShipContainerMenu.STATE_MINOR_FOLLOW_MIN),
            ship.getStateMinor(ShipContainerMenu.STATE_MINOR_FOLLOW_MAX)
        )
    }

    fun recoveryState(state: ShipMovementRecoveryState, forceRecovery: Boolean): RecoveryStateMemory {
        return RecoveryStateMemory(state.stuckTicks(), state.moveFailCount(), forceRecovery)
    }

    fun noPassiveCombatState(): PassiveCombatStateMemory {
        return PassiveCombatStateMemory(
            null,
            false,
            null,
            0.0,
            0.0,
            0.0,
            false,
            false,
            false,
            false,
            false,
            false,
            0.0,
            0
        )
    }

    @JvmRecord
    data class PointerTargetMemory(
        val hasPointTarget: Boolean,
        val rawPointTarget: Vec3?,
        val adjustedPointTarget: Vec3?,
        val pointTargetRemainingTicks: Long,
        val hasEntityTargetCommand: Boolean,
        val entityTargetId: UUID?,
        val entityTargetAlive: Boolean,
        val entityTargetPos: Vec3?,
        val entityTargetRemainingTicks: Long,
        val entityDistanceSqr: Double,
        val entityPreferredRangeSqr: Double,
        val entityStopRangeSqr: Double,
        val entityNeedsCloser: Boolean,
        val entityCannotSee: Boolean,
        val entityShouldChase: Boolean,
        val entityAttackRangeSqr: Double,
        val entityHasRangedAttack: Boolean,
        val entityCanMeleeAttack: Boolean
    ) {
        fun hasAnyTarget(): Boolean {
            return this.hasPointTarget || this.hasEntityTargetCommand
        }

        fun hasAdjustedPointTarget(): Boolean {
            return this.hasPointTarget && this.adjustedPointTarget != null
        }
    }

    @JvmRecord
    data class GuardTargetMemory(
        val target: ShipGuardTarget?,
        val canGuard: Boolean,
        val disabled: Boolean,
        val guardedEntityId: UUID?,
        val guardedEntityAlive: Boolean,
        val guardedEntityPos: Vec3?,
        val blockCenter: Vec3?,
        val dimensionId: Int
    ) {
        fun hasBlockTarget(): Boolean {
            return this.target!!.isBlock() && this.blockCenter != null
        }

        fun hasLiveEntityTarget(): Boolean {
            return this.target!!.isEntity() && this.guardedEntityAlive && this.guardedEntityPos != null
        }
    }

    @JvmRecord
    data class FollowStateMemory(
        val shouldFollow: Boolean,
        val blockReason: String?,
        val ownerPresent: Boolean,
        val ownerId: UUID?,
        val ownerPos: Vec3?,
        val ownerEyeY: Double,
        val ownerDimensionId: Int,
        val ownerHasCombatRation: Boolean,
        val ownerDistanceSq: Double,
        val followMinConfig: Int,
        val followMaxConfig: Int
    )

    @JvmRecord
    data class RecoveryStateMemory(
        val stuckTicks: Int,
        val moveFailCount: Int,
        val forceRecovery: Boolean
    )

    @JvmRecord
    data class PassiveCombatStateMemory(
        val targetId: UUID?,
        val targetAlive: Boolean,
        val targetPos: Vec3?,
        val distanceSqr: Double,
        val preferredRangeSqr: Double,
        val stopRangeSqr: Double,
        val needsCloser: Boolean,
        val cannotSee: Boolean,
        val hasAttackMeans: Boolean,
        val hasPointerTarget: Boolean,
        val needsMovement: Boolean,
        val shouldChase: Boolean,
        val moveSpeed: Double,
        val sightTicks: Int
    ) {
        fun hasTarget(): Boolean {
            return this.targetAlive && this.targetId != null && this.targetPos != null
        }
    }
}
