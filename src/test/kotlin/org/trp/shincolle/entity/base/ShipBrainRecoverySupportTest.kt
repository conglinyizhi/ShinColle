package org.trp.shincolle.entity.base

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShipBrainRecoverySupportTest {

    @Test
    fun `should try teleport recovery should reject nearby non forced targets`() {
        val recovery = ShipMovementRecoveryState()
        val state = ShipRecoveryDecisionResolver.State(false, 64.0, 256.0)

        assertThat(ShipBrainRecoverySupport.shouldTryTeleportRecovery(recovery, state, 0)).isFalse()
    }

    @Test
    fun `should try teleport recovery should respect forced cooldown throttling`() {
        val recovery = ShipMovementRecoveryState()
        val state = ShipRecoveryDecisionResolver.State(true, 1.0, 256.0)

        assertThat(ShipBrainRecoverySupport.shouldTryTeleportRecovery(recovery, state, 2)).isTrue()
        assertThat(ShipBrainRecoverySupport.shouldTryTeleportRecovery(recovery, state, 2)).isFalse()
        assertThat(ShipBrainRecoverySupport.shouldTryTeleportRecovery(recovery, state, 2)).isFalse()
        assertThat(ShipBrainRecoverySupport.shouldTryTeleportRecovery(recovery, state, 2)).isTrue()
    }

    @Test
    fun `should try teleport recovery should respect distance cooldown for non forced targets`() {
        val recovery = ShipMovementRecoveryState()
        val state = ShipRecoveryDecisionResolver.State(false, 512.0, 256.0)

        assertThat(ShipBrainRecoverySupport.shouldTryTeleportRecovery(recovery, state, 1)).isFalse()
        assertThat(ShipBrainRecoverySupport.shouldTryTeleportRecovery(recovery, state, 1)).isTrue()
    }
}
