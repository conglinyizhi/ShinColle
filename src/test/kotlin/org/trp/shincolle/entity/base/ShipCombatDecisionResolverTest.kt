package org.trp.shincolle.entity.base

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShipCombatDecisionResolverTest {

    @Test
    fun `passive combat move failure threshold should remain exclusive and centralized`() {
        assertThat(
            ShipCombatDecisionResolver.shouldClearAfterMoveFailures(ShipAiNumbers.PASSIVE_COMBAT_MOVE_FAIL_LIMIT)
        ).isFalse()
        assertThat(
            ShipCombatDecisionResolver.shouldClearAfterMoveFailures(ShipAiNumbers.PASSIVE_COMBAT_MOVE_FAIL_LIMIT + 1)
        ).isTrue()
    }

    @Test
    fun `passive combat stuck threshold should remain exclusive and centralized`() {
        val recovery = ShipMovementRecoveryState()

        recovery.trackProgress(net.minecraft.world.phys.Vec3.ZERO)
        repeat(ShipAiNumbers.PASSIVE_COMBAT_STUCK_TICK_LIMIT) {
            recovery.trackProgress(net.minecraft.world.phys.Vec3.ZERO)
        }
        assertThat(ShipCombatDecisionResolver.shouldClearAfterStuck(recovery)).isFalse()

        recovery.trackProgress(net.minecraft.world.phys.Vec3.ZERO)
        assertThat(ShipCombatDecisionResolver.shouldClearAfterStuck(recovery)).isTrue()
    }

    @Test
    fun `passive combat teleport recovery should use shared distance and cooldown policy`() {
        val recovery = ShipMovementRecoveryState()
        val nearby = ShipRecoveryDecisionResolver.State(
            false,
            ShipAiNumbers.PASSIVE_COMBAT_TELEPORT_DISTANCE_SQ,
            ShipAiNumbers.PASSIVE_COMBAT_TELEPORT_DISTANCE_SQ
        )
        val remote = ShipRecoveryDecisionResolver.State(
            false,
            ShipAiNumbers.PASSIVE_COMBAT_TELEPORT_DISTANCE_SQ + 1.0,
            ShipAiNumbers.PASSIVE_COMBAT_TELEPORT_DISTANCE_SQ
        )

        assertThat(
            ShipBrainRecoverySupport.shouldTryTeleportRecovery(
                recovery,
                nearby,
                ShipAiNumbers.PASSIVE_COMBAT_TELEPORT_COOLDOWN_TICKS
            )
        ).isFalse()
        assertThat(
            ShipBrainRecoverySupport.shouldTryTeleportRecovery(
                recovery,
                remote,
                ShipAiNumbers.PASSIVE_COMBAT_TELEPORT_COOLDOWN_TICKS
            )
        ).isFalse()

        repeat(ShipAiNumbers.PASSIVE_COMBAT_TELEPORT_COOLDOWN_TICKS - 1) {
            ShipBrainRecoverySupport.shouldTryTeleportRecovery(recovery, remote, ShipAiNumbers.PASSIVE_COMBAT_TELEPORT_COOLDOWN_TICKS)
        }

        assertThat(
            ShipBrainRecoverySupport.shouldTryTeleportRecovery(
                recovery,
                remote,
                ShipAiNumbers.PASSIVE_COMBAT_TELEPORT_COOLDOWN_TICKS
            )
        ).isTrue()
    }
}
