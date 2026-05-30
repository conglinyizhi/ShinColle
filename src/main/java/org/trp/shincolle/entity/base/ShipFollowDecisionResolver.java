package org.trp.shincolle.entity.base;

final class ShipFollowDecisionResolver {
    private ShipFollowDecisionResolver() {
    }

    static boolean shouldTryTeleport(State state) {
        return ShipBrainRecoverySupport.shouldTryTeleportRecovery(
                state.recovery(),
                new ShipRecoveryDecisionResolver.State(
                        state.forceRecovery(),
                        state.ownerDistanceSqr(),
                        ShipAiNumbers.TELEPORT_DISTANCE_SQ
                ),
                ShipAiNumbers.FOLLOW_TELEPORT_COOLDOWN_TICKS
        );
    }

    record State(
            ShipMovementRecoveryState recovery,
            boolean forceRecovery,
            double ownerDistanceSqr
    ) {
    }
}
