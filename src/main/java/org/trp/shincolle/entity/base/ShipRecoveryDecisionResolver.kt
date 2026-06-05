package org.trp.shincolle.entity.base

internal object ShipRecoveryDecisionResolver {
    fun shouldAttemptTeleport(state: State): Boolean {
        return state.force || state.distanceSqr > state.teleportDistanceSqr
    }

    fun shouldClearAfterStuck(stuckTicks: Int, stuckTickLimit: Int): Boolean {
        return stuckTicks > stuckTickLimit
    }

    fun shouldClearAfterMoveFailures(failCount: Int, failLimit: Int): Boolean {
        return failCount > failLimit
    }

    @JvmRecord
    internal data class State(
        val force: Boolean,
        val distanceSqr: Double,
        val teleportDistanceSqr: Double
    )
}
