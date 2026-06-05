package org.trp.shincolle.entity.base

internal object ShipPointerPointDecisionResolver {
    fun shouldResetForNewTarget(state: State): Boolean {
        return !state.hasPreviousTarget
                || state.targetShiftSqr > ShipAiNumbers.TARGET_SWITCH_DISTANCE_SQ
    }

    fun hasReachedTarget(state: State): Boolean {
        return state.distanceToTargetSqr <= ShipAiNumbers.POINTER_MOVE_REACH_SQR
    }

    fun shouldClearAfterStuck(state: State): Boolean {
        return ShipRecoveryDecisionResolver.shouldClearAfterStuck(
            state.stuckTicks, ShipAiNumbers.MOVE_STUCK_TICK_LIMIT
        )
    }

    fun shouldClearAfterMoveFailures(state: State): Boolean {
        return ShipRecoveryDecisionResolver.shouldClearAfterMoveFailures(
            state.moveFailCount, ShipAiNumbers.MOVE_FAIL_LIMIT
        )
    }

    @JvmRecord
    internal data class State(
        val hasPreviousTarget: Boolean,
        val targetShiftSqr: Double,
        val distanceToTargetSqr: Double,
        val stuckTicks: Int,
        val moveFailCount: Int
    )
}
