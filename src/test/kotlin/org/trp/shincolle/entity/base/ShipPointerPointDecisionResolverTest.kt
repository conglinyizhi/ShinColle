package org.trp.shincolle.entity.base

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShipPointerPointDecisionResolverTest {

    @Test
    fun `target reset should require missing previous target or meaningful shift`() {
        assertThat(
            ShipPointerPointDecisionResolver.shouldResetForNewTarget(
                ShipPointerPointDecisionResolver.State(false, 0.0, 9.0, 0, 0)
            )
        ).isTrue()
        assertThat(
            ShipPointerPointDecisionResolver.shouldResetForNewTarget(
                ShipPointerPointDecisionResolver.State(true, ShipAiNumbers.TARGET_SWITCH_DISTANCE_SQ, 9.0, 0, 0)
            )
        ).isFalse()
        assertThat(
            ShipPointerPointDecisionResolver.shouldResetForNewTarget(
                ShipPointerPointDecisionResolver.State(true, ShipAiNumbers.TARGET_SWITCH_DISTANCE_SQ + 0.0001, 9.0, 0, 0)
            )
        ).isTrue()
    }

    @Test
    fun `target reach should use centralized reach distance`() {
        assertThat(
            ShipPointerPointDecisionResolver.hasReachedTarget(
                ShipPointerPointDecisionResolver.State(true, 0.0, ShipAiNumbers.POINTER_MOVE_REACH_SQR, 0, 0)
            )
        ).isTrue()
        assertThat(
            ShipPointerPointDecisionResolver.hasReachedTarget(
                ShipPointerPointDecisionResolver.State(true, 0.0, ShipAiNumbers.POINTER_MOVE_REACH_SQR + 0.0001, 0, 0)
            )
        ).isFalse()
    }

    @Test
    fun `clear thresholds should delegate to shared recovery policy`() {
        assertThat(
            ShipPointerPointDecisionResolver.shouldClearAfterStuck(
                ShipPointerPointDecisionResolver.State(true, 0.0, 9.0, ShipAiNumbers.MOVE_STUCK_TICK_LIMIT, 0)
            )
        ).isFalse()
        assertThat(
            ShipPointerPointDecisionResolver.shouldClearAfterStuck(
                ShipPointerPointDecisionResolver.State(true, 0.0, 9.0, ShipAiNumbers.MOVE_STUCK_TICK_LIMIT + 1, 0)
            )
        ).isTrue()
        assertThat(
            ShipPointerPointDecisionResolver.shouldClearAfterMoveFailures(
                ShipPointerPointDecisionResolver.State(true, 0.0, 9.0, 0, ShipAiNumbers.MOVE_FAIL_LIMIT)
            )
        ).isFalse()
        assertThat(
            ShipPointerPointDecisionResolver.shouldClearAfterMoveFailures(
                ShipPointerPointDecisionResolver.State(true, 0.0, 9.0, 0, ShipAiNumbers.MOVE_FAIL_LIMIT + 1)
            )
        ).isTrue()
    }
}
