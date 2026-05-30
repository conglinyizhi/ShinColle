package org.trp.shincolle.entity.base;

final class ShipPointerPointDecisionResolver {
    private ShipPointerPointDecisionResolver() {
    }

    static boolean shouldResetForNewTarget(State state) {
        return !state.hasPreviousTarget()
                || state.targetShiftSqr() > ShipAiNumbers.TARGET_SWITCH_DISTANCE_SQ;
    }

    static boolean hasReachedTarget(State state) {
        return state.distanceToTargetSqr() <= ShipAiNumbers.POINTER_MOVE_REACH_SQR;
    }

    static boolean shouldClearAfterStuck(State state) {
        return ShipRecoveryDecisionResolver.shouldClearAfterStuck(
                state.stuckTicks(), ShipAiNumbers.MOVE_STUCK_TICK_LIMIT);
    }

    static boolean shouldClearAfterMoveFailures(State state) {
        return ShipRecoveryDecisionResolver.shouldClearAfterMoveFailures(
                state.moveFailCount(), ShipAiNumbers.MOVE_FAIL_LIMIT);
    }

    record State(
            boolean hasPreviousTarget,
            double targetShiftSqr,
            double distanceToTargetSqr,
            int stuckTicks,
            int moveFailCount
    ) {
    }
}
