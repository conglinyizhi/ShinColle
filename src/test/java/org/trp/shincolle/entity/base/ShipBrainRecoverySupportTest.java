package org.trp.shincolle.entity.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipBrainRecoverySupportTest {

    @Test
    void shouldTryTeleportRecoveryShouldRejectNearbyNonForcedTargets() {
        ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();
        ShipRecoveryDecisionResolver.State state =
                new ShipRecoveryDecisionResolver.State(false, 64.0D, 256.0D);

        assertFalse(ShipBrainRecoverySupport.shouldTryTeleportRecovery(recovery, state, 0),
                "Shared recovery support should preserve the non-forced distance gate");
    }

    @Test
    void shouldTryTeleportRecoveryShouldRespectForcedCooldownThrottling() {
        ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();
        ShipRecoveryDecisionResolver.State state =
                new ShipRecoveryDecisionResolver.State(true, 1.0D, 256.0D);

        assertTrue(ShipBrainRecoverySupport.shouldTryTeleportRecovery(recovery, state, 2),
                "Shared recovery support should allow the first forced recovery attempt");
        assertFalse(ShipBrainRecoverySupport.shouldTryTeleportRecovery(recovery, state, 2),
                "Shared recovery support should throttle repeated forced recovery attempts");
        assertFalse(ShipBrainRecoverySupport.shouldTryTeleportRecovery(recovery, state, 2),
                "Shared recovery support should keep honoring the configured cooldown");
        assertTrue(ShipBrainRecoverySupport.shouldTryTeleportRecovery(recovery, state, 2),
                "Shared recovery support should retry forced recovery after cooldown expiry");
    }

    @Test
    void shouldTryTeleportRecoveryShouldRespectDistanceCooldownForNonForcedTargets() {
        ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();
        ShipRecoveryDecisionResolver.State state =
                new ShipRecoveryDecisionResolver.State(false, 512.0D, 256.0D);

        assertFalse(ShipBrainRecoverySupport.shouldTryTeleportRecovery(recovery, state, 1),
                "Shared recovery support should respect the non-forced cooldown before retrying");
        assertTrue(ShipBrainRecoverySupport.shouldTryTeleportRecovery(recovery, state, 1),
                "Shared recovery support should allow non-forced recovery once cooldown passes");
    }
}
