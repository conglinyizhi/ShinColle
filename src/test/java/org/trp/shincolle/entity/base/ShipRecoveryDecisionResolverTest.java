package org.trp.shincolle.entity.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipRecoveryDecisionResolverTest {

    @Test
    void forcedRecoveryShouldAlwaysBeEligibleForTeleportAttempt() {
        assertTrue(ShipRecoveryDecisionResolver.shouldAttemptTeleport(
                new ShipRecoveryDecisionResolver.State(true, 0.0D, 256.0D)));
    }

    @Test
    void nonForcedRecoveryShouldRequireLongDistance() {
        assertFalse(ShipRecoveryDecisionResolver.shouldAttemptTeleport(
                new ShipRecoveryDecisionResolver.State(false, 256.0D, 256.0D)));
        assertTrue(ShipRecoveryDecisionResolver.shouldAttemptTeleport(
                new ShipRecoveryDecisionResolver.State(false, 256.01D, 256.0D)));
    }

    @Test
    void clearThresholdsShouldUseExclusiveLimits() {
        assertFalse(ShipRecoveryDecisionResolver.shouldClearAfterStuck(120, 120));
        assertTrue(ShipRecoveryDecisionResolver.shouldClearAfterStuck(121, 120));
        assertFalse(ShipRecoveryDecisionResolver.shouldClearAfterMoveFailures(40, 40));
        assertTrue(ShipRecoveryDecisionResolver.shouldClearAfterMoveFailures(41, 40));
    }
}
