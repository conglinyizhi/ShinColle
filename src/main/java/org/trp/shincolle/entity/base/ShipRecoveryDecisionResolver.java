package org.trp.shincolle.entity.base;

final class ShipRecoveryDecisionResolver {
    private ShipRecoveryDecisionResolver() {
    }

    static boolean shouldAttemptTeleport(State state) {
        return state.force() || state.distanceSqr() > state.teleportDistanceSqr();
    }

    static boolean shouldClearAfterStuck(int stuckTicks, int stuckTickLimit) {
        return stuckTicks > stuckTickLimit;
    }

    static boolean shouldClearAfterMoveFailures(int failCount, int failLimit) {
        return failCount > failLimit;
    }

    record State(
            boolean force,
            double distanceSqr,
            double teleportDistanceSqr
    ) {
    }
}
