package org.trp.shincolle.entity.base;

final class ShipGuardDecisionResolver {
    private ShipGuardDecisionResolver() {
    }

    static double stopDistanceSqr(State state) {
        return state.entityGuard()
                ? ShipAiNumbers.GUARD_ENTITY_STOP_DISTANCE_SQ
                : ShipAiNumbers.GUARD_BLOCK_STOP_DISTANCE_SQ;
    }

    static boolean hasResolvedTarget(State state) {
        return state.liveEntityTarget() || state.blockTarget();
    }

    static boolean shouldSyncEntityDimension(State state) {
        return state.liveEntityTarget()
                && state.hasGuardedEntity()
                && state.guardDimensionId() != state.guardedEntityDimensionId();
    }

    static boolean shouldMove(State state) {
        return state.distanceSqr() > stopDistanceSqr(state);
    }

    static boolean shouldLookAtOwnerOrPlayer(State state) {
        return !state.hasGuardedEntity() && (state.summoning() || state.distanceSqr() < ShipAiNumbers.GUARD_NEAR_LOOK_DISTANCE_SQ);
    }

    record State(
            boolean entityGuard,
            boolean liveEntityTarget,
            boolean blockTarget,
            boolean hasGuardedEntity,
            double distanceSqr,
            boolean summoning,
            int guardDimensionId,
            int guardedEntityDimensionId
    ) {
    }
}
