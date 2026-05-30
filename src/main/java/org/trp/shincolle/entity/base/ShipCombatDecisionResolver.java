package org.trp.shincolle.entity.base;

final class ShipCombatDecisionResolver {
    private ShipCombatDecisionResolver() {
    }

    static boolean shouldClearAfterStuck(ShipMovementRecoveryState recovery) {
        return ShipRecoveryDecisionResolver.shouldClearAfterStuck(
                recovery.stuckTicks(), ShipAiNumbers.PASSIVE_COMBAT_STUCK_TICK_LIMIT);
    }

    static boolean shouldClearAfterMoveFailures(int failCount) {
        return ShipRecoveryDecisionResolver.shouldClearAfterMoveFailures(
                failCount, ShipAiNumbers.PASSIVE_COMBAT_MOVE_FAIL_LIMIT);
    }
}
