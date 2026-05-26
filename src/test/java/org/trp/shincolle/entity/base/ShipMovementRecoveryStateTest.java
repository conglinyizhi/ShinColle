package org.trp.shincolle.entity.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipMovementRecoveryStateTest {
    @Test
    void throttledForcedTeleportShouldNotRetryEveryTickAfterFailure() {
        ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();

        assertTrue(recovery.shouldTryTeleportThrottled(true, 1.0D, 256.0D, 3),
                "First forced recovery should run immediately");
        assertFalse(recovery.shouldTryTeleportThrottled(true, 1.0D, 256.0D, 3),
                "Forced recovery should be throttled after a failed attempt");
        assertFalse(recovery.shouldTryTeleportThrottled(true, 1.0D, 256.0D, 3),
                "Forced recovery should keep honoring the cooldown");
        assertFalse(recovery.shouldTryTeleportThrottled(true, 1.0D, 256.0D, 3),
                "Cooldown should cover the configured number of skipped ticks");
        assertTrue(recovery.shouldTryTeleportThrottled(true, 1.0D, 256.0D, 3),
                "Forced recovery should retry after the cooldown expires");
    }

    @Test
    void clearShouldResetForcedTeleportCooldown() {
        ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();

        assertTrue(recovery.shouldTryTeleportThrottled(true, 1.0D, 256.0D, 100),
                "First forced recovery should run immediately");
        assertFalse(recovery.shouldTryTeleportThrottled(true, 1.0D, 256.0D, 100),
                "Forced recovery should be throttled before reset");

        recovery.clear();

        assertTrue(recovery.shouldTryTeleportThrottled(true, 1.0D, 256.0D, 100),
                "Clearing recovery state should clear stale forced recovery cooldown");
    }

    @Test
    void moveFailureLoggingShouldBeRateLimitedAndResettable() {
        ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();

        recovery.recordMoveFailure();

        assertTrue(recovery.shouldLogMoveFailure(10, 20),
                "First move failure should be visible immediately");

        recovery.recordMoveFailure();

        assertFalse(recovery.shouldLogMoveFailure(29, 20),
                "Repeated move failures should be hidden until the interval passes");

        recovery.recordMoveFailure();

        assertTrue(recovery.shouldLogMoveFailure(30, 20),
                "Repeated move failures should be visible once the interval passes");

        recovery.clearMoveFailures();
        recovery.recordMoveFailure();

        assertTrue(recovery.shouldLogMoveFailure(31, 20),
                "Clearing move failures should also clear stale log throttling");
    }

    @Test
    void nonForcedTeleportShouldKeepDistanceAndCooldownPolicy() {
        ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();

        assertFalse(recovery.shouldTryTeleportThrottled(false, 64.0D, 256.0D, 0),
                "Non-forced recovery should still ignore nearby targets");
        assertTrue(recovery.shouldTryTeleportThrottled(false, 512.0D, 256.0D, 0),
                "Non-forced recovery should still allow distant targets once cooldown allows");
    }

    @Test
    void stuckLimitComparisonShouldStayCentralized() {
        ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();

        assertFalse(recovery.isStuckLongerThan(0),
                "Fresh recovery state should not be considered stuck");
    }
}
