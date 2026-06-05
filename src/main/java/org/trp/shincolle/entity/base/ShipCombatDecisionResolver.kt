package org.trp.shincolle.entity.base

internal object ShipCombatDecisionResolver {
    fun shouldClearAfterStuck(recovery: ShipMovementRecoveryState): Boolean {
        return ShipRecoveryDecisionResolver.shouldClearAfterStuck(
            recovery.stuckTicks(), ShipAiNumbers.PASSIVE_COMBAT_STUCK_TICK_LIMIT
        )
    }

    fun shouldClearAfterMoveFailures(failCount: Int): Boolean {
        return ShipRecoveryDecisionResolver.shouldClearAfterMoveFailures(
            failCount, ShipAiNumbers.PASSIVE_COMBAT_MOVE_FAIL_LIMIT
        )
    }
}
