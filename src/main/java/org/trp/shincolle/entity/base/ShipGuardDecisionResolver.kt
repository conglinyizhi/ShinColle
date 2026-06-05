package org.trp.shincolle.entity.base

internal object ShipGuardDecisionResolver {
    fun stopDistanceSqr(state: State): Double {
        return if (state.entityGuard)
            ShipAiNumbers.GUARD_ENTITY_STOP_DISTANCE_SQ
        else
            ShipAiNumbers.GUARD_BLOCK_STOP_DISTANCE_SQ
    }

    fun hasResolvedTarget(state: State): Boolean {
        return state.liveEntityTarget || state.blockTarget
    }

    fun shouldSyncEntityDimension(state: State): Boolean {
        return state.liveEntityTarget
                && state.hasGuardedEntity
                && state.guardDimensionId != state.guardedEntityDimensionId
    }

    fun shouldMove(state: State): Boolean {
        return state.distanceSqr > stopDistanceSqr(state)
    }

    fun shouldLookAtOwnerOrPlayer(state: State): Boolean {
        return !state.hasGuardedEntity && (state.summoning || state.distanceSqr < ShipAiNumbers.GUARD_NEAR_LOOK_DISTANCE_SQ)
    }

    fun shouldClearAfterStuck(recovery: ShipMovementRecoveryState): Boolean {
        return ShipRecoveryDecisionResolver.shouldClearAfterStuck(
            recovery.stuckTicks(), ShipAiNumbers.MOVE_STUCK_TICK_LIMIT
        )
    }

    fun shouldClearAfterMoveFailures(failCount: Int): Boolean {
        return ShipRecoveryDecisionResolver.shouldClearAfterMoveFailures(
            failCount, ShipAiNumbers.MOVE_FAIL_LIMIT
        )
    }

    @JvmRecord
    internal data class State(
        val entityGuard: Boolean,
        val liveEntityTarget: Boolean,
        val blockTarget: Boolean,
        val hasGuardedEntity: Boolean,
        val distanceSqr: Double,
        val summoning: Boolean,
        val guardDimensionId: Int,
        val guardedEntityDimensionId: Int
    )
}
