package org.trp.shincolle.entity.base

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShipFollowDecisionResolverTest {

    @Test
    fun `follow teleport should ignore nearby non forced targets`() {
        val recovery = ShipMovementRecoveryState()

        assertThat(
            ShipFollowDecisionResolver.shouldTryTeleport(
                ShipFollowDecisionResolver.State(recovery, false, 64.0)
            )
        ).isFalse()
    }

    @Test
    fun `follow teleport should respect forced cooldown`() {
        val recovery = ShipMovementRecoveryState()
        val state = ShipFollowDecisionResolver.State(recovery, true, 1.0)

        assertThat(ShipFollowDecisionResolver.shouldTryTeleport(state)).isTrue()
        assertThat(ShipFollowDecisionResolver.shouldTryTeleport(state)).isFalse()
        assertThat(ShipFollowDecisionResolver.shouldTryTeleport(state)).isFalse()
        repeat(ShipAiNumbers.FOLLOW_TELEPORT_COOLDOWN_TICKS - 2) {
            assertThat(ShipFollowDecisionResolver.shouldTryTeleport(state)).isFalse()
        }
        assertThat(ShipFollowDecisionResolver.shouldTryTeleport(state)).isTrue()
    }
}
