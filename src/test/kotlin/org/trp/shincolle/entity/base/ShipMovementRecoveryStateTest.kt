package org.trp.shincolle.entity.base

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShipMovementRecoveryStateTest {

    @Test
    fun `throttled forced teleport should not retry every tick after failure`() {
        val recovery = ShipMovementRecoveryState()

        assertThat(recovery.shouldTryTeleportThrottled(true, 1.0, 256.0, 3)).isTrue()
        assertThat(recovery.shouldTryTeleportThrottled(true, 1.0, 256.0, 3)).isFalse()
        assertThat(recovery.shouldTryTeleportThrottled(true, 1.0, 256.0, 3)).isFalse()
        assertThat(recovery.shouldTryTeleportThrottled(true, 1.0, 256.0, 3)).isFalse()
        assertThat(recovery.shouldTryTeleportThrottled(true, 1.0, 256.0, 3)).isTrue()
    }

    @Test
    fun `clear should reset forced teleport cooldown`() {
        val recovery = ShipMovementRecoveryState()

        assertThat(recovery.shouldTryTeleportThrottled(true, 1.0, 256.0, 100)).isTrue()
        assertThat(recovery.shouldTryTeleportThrottled(true, 1.0, 256.0, 100)).isFalse()

        recovery.clear()

        assertThat(recovery.shouldTryTeleportThrottled(true, 1.0, 256.0, 100)).isTrue()
    }

    @Test
    fun `move failure logging should be rate limited and resettable`() {
        val recovery = ShipMovementRecoveryState()

        recovery.recordMoveFailure()
        assertThat(recovery.shouldLogMoveFailure(10, 20)).isTrue()

        recovery.recordMoveFailure()
        assertThat(recovery.shouldLogMoveFailure(29, 20)).isFalse()

        recovery.recordMoveFailure()
        assertThat(recovery.shouldLogMoveFailure(30, 20)).isTrue()

        recovery.clearMoveFailures()
        recovery.recordMoveFailure()
        assertThat(recovery.shouldLogMoveFailure(31, 20)).isTrue()
    }

    @Test
    fun `non forced teleport should keep distance and cooldown policy`() {
        val recovery = ShipMovementRecoveryState()

        assertThat(recovery.shouldTryTeleportThrottled(false, 64.0, 256.0, 0)).isFalse()
        assertThat(recovery.shouldTryTeleportThrottled(false, 512.0, 256.0, 0)).isTrue()
    }

    @Test
    fun `stuck limit comparison should stay centralized`() {
        val recovery = ShipMovementRecoveryState()

        assertThat(recovery.isStuckLongerThan(0)).isFalse()
    }
}
