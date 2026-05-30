package org.trp.shincolle.entity.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipFollowDecisionResolverTest {

    @Test
    void followTeleportShouldIgnoreNearbyNonForcedTargets() {
        ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();

        assertFalse(ShipFollowDecisionResolver.shouldTryTeleport(
                new ShipFollowDecisionResolver.State(recovery, false, 64.0D)));
    }

    @Test
    void followTeleportShouldRespectForcedCooldown() {
        ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();
        ShipFollowDecisionResolver.State state =
                new ShipFollowDecisionResolver.State(recovery, true, 1.0D);

        assertTrue(ShipFollowDecisionResolver.shouldTryTeleport(state));
        assertFalse(ShipFollowDecisionResolver.shouldTryTeleport(state));
        assertFalse(ShipFollowDecisionResolver.shouldTryTeleport(state));
        for (int i = 0; i < ShipAiNumbers.FOLLOW_TELEPORT_COOLDOWN_TICKS - 2; i++) {
            assertFalse(ShipFollowDecisionResolver.shouldTryTeleport(state));
        }
        assertTrue(ShipFollowDecisionResolver.shouldTryTeleport(state));
    }
}
