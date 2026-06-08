package org.trp.shincolle.entity.base

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShipRecoveryDecisionResolverTest {

    @Test
    fun `forced recovery should always be eligible for teleport attempt`() {
        assertThat(
            ShipRecoveryDecisionResolver.shouldAttemptTeleport(
                ShipRecoveryDecisionResolver.State(true, 0.0, 256.0)
            )
        ).isTrue()
    }

    @Test
    fun `non forced recovery should require long distance`() {
        assertThat(
            ShipRecoveryDecisionResolver.shouldAttemptTeleport(
                ShipRecoveryDecisionResolver.State(false, 256.0, 256.0)
            )
        ).isFalse()
        assertThat(
            ShipRecoveryDecisionResolver.shouldAttemptTeleport(
                ShipRecoveryDecisionResolver.State(false, 256.01, 256.0)
            )
        ).isTrue()
    }

    @Test
    fun `clear thresholds should use exclusive limits`() {
        assertThat(ShipRecoveryDecisionResolver.shouldClearAfterStuck(120, 120)).isFalse()
        assertThat(ShipRecoveryDecisionResolver.shouldClearAfterStuck(121, 120)).isTrue()
        assertThat(ShipRecoveryDecisionResolver.shouldClearAfterMoveFailures(40, 40)).isFalse()
        assertThat(ShipRecoveryDecisionResolver.shouldClearAfterMoveFailures(41, 40)).isTrue()
    }
}
