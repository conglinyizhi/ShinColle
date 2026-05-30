package org.trp.shincolle.entity.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipPointerPointDecisionResolverTest {

    @Test
    void targetResetShouldRequireMissingPreviousTargetOrMeaningfulShift() {
        assertTrue(ShipPointerPointDecisionResolver.shouldResetForNewTarget(
                new ShipPointerPointDecisionResolver.State(false, 0.0D, 9.0D, 0, 0)));
        assertFalse(ShipPointerPointDecisionResolver.shouldResetForNewTarget(
                new ShipPointerPointDecisionResolver.State(true, ShipAiNumbers.TARGET_SWITCH_DISTANCE_SQ, 9.0D, 0, 0)));
        assertTrue(ShipPointerPointDecisionResolver.shouldResetForNewTarget(
                new ShipPointerPointDecisionResolver.State(true, ShipAiNumbers.TARGET_SWITCH_DISTANCE_SQ + 0.0001D, 9.0D, 0, 0)));
    }

    @Test
    void targetReachShouldUseCentralizedReachDistance() {
        assertTrue(ShipPointerPointDecisionResolver.hasReachedTarget(
                new ShipPointerPointDecisionResolver.State(true, 0.0D, ShipAiNumbers.POINTER_MOVE_REACH_SQR, 0, 0)));
        assertFalse(ShipPointerPointDecisionResolver.hasReachedTarget(
                new ShipPointerPointDecisionResolver.State(true, 0.0D, ShipAiNumbers.POINTER_MOVE_REACH_SQR + 0.0001D, 0, 0)));
    }

    @Test
    void clearThresholdsShouldDelegateToSharedRecoveryPolicy() {
        assertFalse(ShipPointerPointDecisionResolver.shouldClearAfterStuck(
                new ShipPointerPointDecisionResolver.State(true, 0.0D, 9.0D, ShipAiNumbers.MOVE_STUCK_TICK_LIMIT, 0)));
        assertTrue(ShipPointerPointDecisionResolver.shouldClearAfterStuck(
                new ShipPointerPointDecisionResolver.State(true, 0.0D, 9.0D, ShipAiNumbers.MOVE_STUCK_TICK_LIMIT + 1, 0)));
        assertFalse(ShipPointerPointDecisionResolver.shouldClearAfterMoveFailures(
                new ShipPointerPointDecisionResolver.State(true, 0.0D, 9.0D, 0, ShipAiNumbers.MOVE_FAIL_LIMIT)));
        assertTrue(ShipPointerPointDecisionResolver.shouldClearAfterMoveFailures(
                new ShipPointerPointDecisionResolver.State(true, 0.0D, 9.0D, 0, ShipAiNumbers.MOVE_FAIL_LIMIT + 1)));
    }
}
