package org.trp.shincolle.entity.base

import kotlin.math.max

internal object ShipPointerEntityDecisionResolver {
    fun resolve(state: State): Decision {
        val attackRangeSqr = attackRangeSqr(state.shipWidth, state.targetWidth)
        val preferredRangeSqr = preferredRangeSqr(state, attackRangeSqr)
        val hasRangedAttack = state.canUseLightAmmo
                || state.canUseHeavyAmmo
                || state.hasAircraftAttackEnabled
        val stopRangeSqr = if (hasRangedAttack)
            preferredRangeSqr + ShipAiNumbers.POINTER_ENTITY_PATH_REFRESH_DISTANCE_SQR
        else
            preferredRangeSqr
        val needsCloser = state.targetPresent && state.distanceSqr > stopRangeSqr
        val cannotSee = state.targetPresent
                && !state.hasLineOfSight && state.distanceSqr > preferredRangeSqr * 0.5
        return Decision(
            preferredRangeSqr,
            stopRangeSqr,
            needsCloser,
            cannotSee,
            needsCloser || cannotSee,
            attackRangeSqr,
            hasRangedAttack,
            state.canUseMeleeAttack
        )
    }

    fun aimDelayTicks(shipLevel: Int): Int {
        return max(
            ShipAiNumbers.POINTER_ENTITY_AIM_DELAY_MIN,
            (ShipAiNumbers.POINTER_ENTITY_AIM_DELAY_SCALE
                    * (ShipAiNumbers.POINTER_ENTITY_AIM_LEVEL_CAP - shipLevel)
                    / ShipAiNumbers.POINTER_ENTITY_AIM_LEVEL_CAP).toInt() + ShipAiNumbers.POINTER_ENTITY_AIM_DELAY_BASE
        )
    }

    fun shouldFireLightAttack(state: AttackState): Boolean {
        return state.canUseLightAmmo
                && elapsed(state.tickCount, state.lastLightShotTick) >= max(1, state.lightAttackInterval)
    }

    fun shouldFireHeavyAttack(state: AttackState): Boolean {
        return state.canUseHeavyAmmo
                && elapsed(state.tickCount, state.lastHeavyShotTick) >= max(1, state.heavyAttackInterval)
    }

    fun shouldFireMeleeAttack(state: AttackState): Boolean {
        return state.canUseMeleeAttack
                && state.targetDistanceSqr <= state.attackRangeSqr && elapsed(
            state.tickCount,
            state.lastMeleeAttackTick
        ) >= max(1, state.meleeAttackInterval)
    }

    fun shouldClearAfterStuck(recovery: ShipMovementRecoveryState): Boolean {
        return ShipRecoveryDecisionResolver.shouldClearAfterStuck(
            recovery.stuckTicks(), ShipAiNumbers.POINTER_ENTITY_STUCK_TICK_LIMIT
        )
    }

    fun shouldClearAfterMoveFailures(failCount: Int): Boolean {
        return ShipRecoveryDecisionResolver.shouldClearAfterMoveFailures(
            failCount, ShipAiNumbers.POINTER_ENTITY_MOVE_FAIL_LIMIT
        )
    }

    private fun elapsed(tickCount: Int, lastActionTick: Int): Int {
        return tickCount - lastActionTick
    }

    private fun preferredRangeSqr(state: State, attackRangeSqr: Double): Double {
        if (state.canUseLightAmmo || state.canUseHeavyAmmo) {
            val range = max(2.0, state.attackRange)
            return range * range
        }
        if (state.hasAircraftAttackEnabled) {
            val range = max(24.0, state.attackRange * 1.5)
            return range * range
        }
        return attackRangeSqr
    }

    private fun attackRangeSqr(shipWidth: Double, targetWidth: Double): Double {
        val width = shipWidth * 2.0f
        val reach = width * width + targetWidth
        return max(reach, ShipAiNumbers.POINTER_ENTITY_ATTACK_RANGE_SQR)
    }

    @JvmRecord
    internal data class State(
        val targetPresent: Boolean,
        val distanceSqr: Double,
        val hasLineOfSight: Boolean,
        val canUseLightAmmo: Boolean,
        val canUseHeavyAmmo: Boolean,
        val hasAircraftAttackEnabled: Boolean,
        val canUseMeleeAttack: Boolean,
        val attackRange: Double,
        val shipWidth: Double,
        val targetWidth: Double
    )

    @JvmRecord
    internal data class Decision(
        val preferredRangeSqr: Double,
        val stopRangeSqr: Double,
        val needsCloser: Boolean,
        val cannotSee: Boolean,
        val shouldChase: Boolean,
        val attackRangeSqr: Double,
        val hasRangedAttack: Boolean,
        val canMeleeAttack: Boolean
    )

    @JvmRecord
    internal data class AttackState(
        val tickCount: Int,
        val canUseLightAmmo: Boolean,
        val lightAttackInterval: Int,
        val lastLightShotTick: Int,
        val canUseHeavyAmmo: Boolean,
        val heavyAttackInterval: Int,
        val lastHeavyShotTick: Int,
        val canUseMeleeAttack: Boolean,
        val targetDistanceSqr: Double,
        val attackRangeSqr: Double,
        val meleeAttackInterval: Int,
        val lastMeleeAttackTick: Int
    )
}
