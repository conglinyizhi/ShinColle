package org.trp.shincolle.entity.base.path;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipLegacyNavigationPolicyTest {

    @Test
    void sameNavigationTargetShouldUseLegacyDriftTolerance() {
        ShipLegacyNavigationPolicy.Target origin = new ShipLegacyNavigationPolicy.Target(0, 64, 0);

        assertTrue(ShipLegacyNavigationPolicy.isSameNavigationTarget(
                origin, new ShipLegacyNavigationPolicy.Target(3, 64, 0)));
        assertFalse(ShipLegacyNavigationPolicy.isSameNavigationTarget(
                origin, new ShipLegacyNavigationPolicy.Target(4, 64, 0)));
        assertFalse(ShipLegacyNavigationPolicy.isSameNavigationTarget(null, origin));
    }

    @Test
    void stuckProgressShouldResetOnlyForFreshPathsOrRealTargetChanges() {
        assertTrue(ShipLegacyNavigationPolicy.shouldResetStuckProgress(false, true));
        assertTrue(ShipLegacyNavigationPolicy.shouldResetStuckProgress(true, false));
        assertFalse(ShipLegacyNavigationPolicy.shouldResetStuckProgress(true, true));
    }

    @Test
    void setPathLogsShouldTriggerForFailureChangeTargetChangeOrThrottleExpiry() {
        ShipLegacyNavigationPolicy.Target target = new ShipLegacyNavigationPolicy.Target(8, 64, 8);
        ShipLegacyNavigationPolicy.Target nearby = new ShipLegacyNavigationPolicy.Target(10, 64, 8);
        ShipLegacyNavigationPolicy.Target far = new ShipLegacyNavigationPolicy.Target(12, 64, 8);

        assertTrue(ShipLegacyNavigationPolicy.shouldLogSetPath(false, true, target, target, 0, 0));
        assertTrue(ShipLegacyNavigationPolicy.shouldLogSetPath(false, false, target, far, 0, 0));
        assertFalse(ShipLegacyNavigationPolicy.shouldLogSetPath(false, false, target, nearby,
                ShipLegacyNavigationPolicy.NAVIGATION_SET_PATH_LOG_INTERVAL - 1, 0));
        assertTrue(ShipLegacyNavigationPolicy.shouldLogSetPath(false, false, target, nearby,
                ShipLegacyNavigationPolicy.NAVIGATION_SET_PATH_LOG_INTERVAL, 0));
    }

    @Test
    void navigationEventLogsShouldRespectTargetDriftAndThrottle() {
        ShipLegacyNavigationPolicy.Target target = new ShipLegacyNavigationPolicy.Target(8, 64, 8);
        ShipLegacyNavigationPolicy.Target nearby = new ShipLegacyNavigationPolicy.Target(10, 64, 8);
        ShipLegacyNavigationPolicy.Target far = new ShipLegacyNavigationPolicy.Target(12, 64, 8);

        assertTrue(ShipLegacyNavigationPolicy.shouldLogNavigationEvent(target, target, 0, Integer.MIN_VALUE));
        assertTrue(ShipLegacyNavigationPolicy.shouldLogNavigationEvent(target, far, 0, 0));
        assertFalse(ShipLegacyNavigationPolicy.shouldLogNavigationEvent(target, nearby,
                ShipLegacyNavigationPolicy.NAVIGATION_DEBUG_LOG_INTERVAL - 1, 0));
        assertTrue(ShipLegacyNavigationPolicy.shouldLogNavigationEvent(target, nearby,
                ShipLegacyNavigationPolicy.NAVIGATION_DEBUG_LOG_INTERVAL, 0));
    }

    @Test
    void timeoutPolicyShouldClampSpeedAndRetryAfterDoubleLimit() {
        assertEquals(6000.0D, ShipLegacyNavigationPolicy.calculateTimeoutLimit(1.0D, 0.0D));
        assertFalse(ShipLegacyNavigationPolicy.shouldRetryTimedOutPath(200L, 100.0D));
        assertTrue(ShipLegacyNavigationPolicy.shouldRetryTimedOutPath(201L, 100.0D));
        assertFalse(ShipLegacyNavigationPolicy.shouldRetryTimedOutPath(1L, 0.0D));
    }
}
