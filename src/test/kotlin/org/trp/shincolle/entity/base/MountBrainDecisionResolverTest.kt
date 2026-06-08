package org.trp.shincolle.entity.base

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MountBrainDecisionResolverTest {

    @Test
    fun `follow should start for pointer or guard before owner distance checks`() {
        assertThat(
            MountBrainDecisionResolver.shouldFollowHost(
                stateBuilder().hostHasPointerTarget(true).ownerPresent(false).build()
            )
        ).isTrue()
        assertThat(
            MountBrainDecisionResolver.shouldFollowHost(
                stateBuilder().hasGuardTarget(true).ownerPresent(false).build()
            )
        ).isTrue()
    }

    @Test
    fun `follow should use owner distance when no command or guard target exists`() {
        val maxDistSq = 8.0 * 8.0 + MountAiNumbers.FOLLOW_WIDTH_PADDING

        assertThat(
            MountBrainDecisionResolver.shouldFollowHost(
                stateBuilder().ownerDistanceSq(maxDistSq).followMaxConfig(8).mountWidth(1.0).build()
            )
        ).isFalse()
        assertThat(
            MountBrainDecisionResolver.shouldFollowHost(
                stateBuilder().ownerDistanceSq(maxDistSq + 0.01).followMaxConfig(8).mountWidth(1.0).build()
            )
        ).isTrue()
    }

    @Test
    fun `follow should not start when host or mount state blocks movement`() {
        assertThat(MountBrainDecisionResolver.shouldFollowHost(stateBuilder().hostPresent(false).build())).isFalse()
        assertThat(MountBrainDecisionResolver.shouldFollowHost(stateBuilder().hostAlive(false).build())).isFalse()
        assertThat(MountBrainDecisionResolver.shouldFollowHost(stateBuilder().hostOrderedToSit(true).build())).isFalse()
        assertThat(MountBrainDecisionResolver.shouldFollowHost(stateBuilder().mountPassenger(true).build())).isFalse()
        assertThat(MountBrainDecisionResolver.shouldFollowHost(stateBuilder().ownerPresent(false).build())).isFalse()
    }

    @Test
    fun `range attack should require usable host state and alive target`() {
        assertThat(
            MountBrainDecisionResolver.shouldRangeAttack(
                stateBuilder().hasAttackTarget(true).hasAliveAttackTarget(true).build()
            )
        ).isTrue()
        assertThat(
            MountBrainDecisionResolver.shouldRangeAttack(
                stateBuilder().hostAlive(false).hasAliveAttackTarget(true).build()
            )
        ).isFalse()
        assertThat(
            MountBrainDecisionResolver.shouldRangeAttack(
                stateBuilder().hasAttackTarget(true).hasAliveAttackTarget(false).build()
            )
        ).isFalse()
        assertThat(
            MountBrainDecisionResolver.shouldRangeAttack(
                stateBuilder().hostOrderedToSit(true).hasAliveAttackTarget(true).build()
            )
        ).isFalse()
        assertThat(
            MountBrainDecisionResolver.shouldRangeAttack(
                stateBuilder().mountPassenger(true).hasAliveAttackTarget(true).build()
            )
        ).isFalse()
    }

    @Test
    fun `light attack should require aim range ammo and cooldown`() {
        assertThat(
            MountBrainDecisionResolver.shouldFireLight(
                stateBuilder()
                    .hasAttackTarget(true)
                    .hasAliveAttackTarget(true)
                    .aimTick(20)
                    .aimRequiredTicks(20)
                    .targetDistanceSq(16.0)
                    .attackRangeSq(16.0)
                    .hostLightAttackEnabled(true)
                    .hostLightAmmo(1)
                    .lightDelay(0)
                    .build()
            )
        ).isTrue()
        assertThat(
            MountBrainDecisionResolver.shouldFireLight(
                stateBuilder()
                    .hasAttackTarget(true)
                    .hasAliveAttackTarget(true)
                    .aimTick(19)
                    .aimRequiredTicks(20)
                    .targetDistanceSq(16.0)
                    .attackRangeSq(16.0)
                    .hostLightAttackEnabled(true)
                    .hostLightAmmo(1)
                    .lightDelay(0)
                    .build()
            )
        ).isFalse()
        assertThat(
            MountBrainDecisionResolver.shouldFireLight(
                stateBuilder()
                    .hasAttackTarget(true)
                    .hasAliveAttackTarget(true)
                    .aimTick(20)
                    .aimRequiredTicks(20)
                    .targetDistanceSq(16.01)
                    .attackRangeSq(16.0)
                    .hostLightAttackEnabled(true)
                    .hostLightAmmo(1)
                    .lightDelay(0)
                    .build()
            )
        ).isFalse()
        assertThat(
            MountBrainDecisionResolver.shouldFireLight(
                stateBuilder()
                    .hasAttackTarget(true)
                    .hasAliveAttackTarget(true)
                    .aimTick(20)
                    .aimRequiredTicks(20)
                    .targetDistanceSq(16.0)
                    .attackRangeSq(16.0)
                    .hostLightAttackEnabled(true)
                    .hostLightAmmo(0)
                    .lightDelay(0)
                    .build()
            )
        ).isFalse()
        assertThat(
            MountBrainDecisionResolver.shouldFireLight(
                stateBuilder()
                    .hasAttackTarget(true)
                    .hasAliveAttackTarget(true)
                    .aimTick(20)
                    .aimRequiredTicks(20)
                    .targetDistanceSq(16.0)
                    .attackRangeSq(16.0)
                    .hostLightAttackEnabled(true)
                    .hostLightAmmo(1)
                    .lightDelay(1)
                    .build()
            )
        ).isFalse()
    }

    @Test
    fun `heavy attack should require aim range ammo and cooldown`() {
        assertThat(
            MountBrainDecisionResolver.shouldFireHeavy(
                stateBuilder()
                    .hasAttackTarget(true)
                    .hasAliveAttackTarget(true)
                    .aimTick(15)
                    .aimRequiredTicks(15)
                    .targetDistanceSq(9.0)
                    .attackRangeSq(9.0)
                    .hostHeavyAttackEnabled(true)
                    .hostHeavyAmmo(1)
                    .heavyDelay(0)
                    .build()
            )
        ).isTrue()
        assertThat(
            MountBrainDecisionResolver.shouldFireHeavy(
                stateBuilder()
                    .hasAttackTarget(true)
                    .hasAliveAttackTarget(true)
                    .aimTick(15)
                    .aimRequiredTicks(15)
                    .targetDistanceSq(9.01)
                    .attackRangeSq(9.0)
                    .hostHeavyAttackEnabled(true)
                    .hostHeavyAmmo(1)
                    .heavyDelay(0)
                    .build()
            )
        ).isFalse()
        assertThat(
            MountBrainDecisionResolver.shouldFireHeavy(
                stateBuilder()
                    .hasAttackTarget(true)
                    .hasAliveAttackTarget(true)
                    .aimTick(15)
                    .aimRequiredTicks(15)
                    .targetDistanceSq(9.0)
                    .attackRangeSq(9.0)
                    .hostHeavyAttackEnabled(false)
                    .hostHeavyAmmo(1)
                    .heavyDelay(0)
                    .build()
            )
        ).isFalse()
        assertThat(
            MountBrainDecisionResolver.shouldFireHeavy(
                stateBuilder()
                    .hasAttackTarget(true)
                    .hasAliveAttackTarget(true)
                    .aimTick(15)
                    .aimRequiredTicks(15)
                    .targetDistanceSq(9.0)
                    .attackRangeSq(9.0)
                    .hostHeavyAttackEnabled(true)
                    .hostHeavyAmmo(1)
                    .heavyDelay(2)
                    .build()
            )
        ).isFalse()
    }

    @Test
    fun `random stroll should not race follow or combat movement`() {
        assertThat(
            MountBrainDecisionResolver.shouldRandomStroll(
                stateBuilder().ownerDistanceSq(0.0).randomStrollRollHit(true).build()
            )
        ).isTrue()
        assertThat(
            MountBrainDecisionResolver.shouldRandomStroll(
                stateBuilder().hostAlive(false).randomStrollRollHit(true).build()
            )
        ).isFalse()
        assertThat(
            MountBrainDecisionResolver.shouldRandomStroll(
                stateBuilder().hasAttackTarget(true).randomStrollRollHit(true).build()
            )
        ).isFalse()
        assertThat(
            MountBrainDecisionResolver.shouldRandomStroll(
                stateBuilder().hostHasPointerTarget(true).randomStrollRollHit(true).build()
            )
        ).isFalse()
        assertThat(
            MountBrainDecisionResolver.shouldRandomStroll(
                stateBuilder().randomStrollRollHit(false).build()
            )
        ).isFalse()
    }

    private fun stateBuilder(): StateBuilder {
        return StateBuilder()
    }

    private class StateBuilder {
        private var hostPresent = true
        private var hostAlive = true
        private var hostOrderedToSit = false
        private var mountPassenger = false
        private var hostHasPointerTarget = false
        private var hasGuardTarget = false
        private var ownerPresent = true
        private var ownerDistanceSq = 0.0
        private var mountWidth = 1.0
        private var followMaxConfig = 8
        private var hasAttackTarget = false
        private var hasAliveAttackTarget = false
        private var randomStrollRollHit = false
        private var aimTick = 0
        private var aimRequiredTicks = 0
        private var targetDistanceSq = 0.0
        private var attackRangeSq = 0.0
        private var hostLightAttackEnabled = false
        private var hostLightAmmo = 0
        private var lightDelay = 0
        private var hostHeavyAttackEnabled = false
        private var hostHeavyAmmo = 0
        private var heavyDelay = 0

        fun hostPresent(hostPresent: Boolean) = apply { this.hostPresent = hostPresent }
        fun hostAlive(hostAlive: Boolean) = apply { this.hostAlive = hostAlive }
        fun hostOrderedToSit(hostOrderedToSit: Boolean) = apply { this.hostOrderedToSit = hostOrderedToSit }
        fun mountPassenger(mountPassenger: Boolean) = apply { this.mountPassenger = mountPassenger }
        fun hostHasPointerTarget(hostHasPointerTarget: Boolean) = apply { this.hostHasPointerTarget = hostHasPointerTarget }
        fun hasGuardTarget(hasGuardTarget: Boolean) = apply { this.hasGuardTarget = hasGuardTarget }
        fun ownerPresent(ownerPresent: Boolean) = apply { this.ownerPresent = ownerPresent }
        fun ownerDistanceSq(ownerDistanceSq: Double) = apply { this.ownerDistanceSq = ownerDistanceSq }
        fun mountWidth(mountWidth: Double) = apply { this.mountWidth = mountWidth }
        fun followMaxConfig(followMaxConfig: Int) = apply { this.followMaxConfig = followMaxConfig }
        fun hasAttackTarget(hasAttackTarget: Boolean) = apply { this.hasAttackTarget = hasAttackTarget }
        fun hasAliveAttackTarget(hasAliveAttackTarget: Boolean) = apply { this.hasAliveAttackTarget = hasAliveAttackTarget }
        fun randomStrollRollHit(randomStrollRollHit: Boolean) = apply { this.randomStrollRollHit = randomStrollRollHit }
        fun aimTick(aimTick: Int) = apply { this.aimTick = aimTick }
        fun aimRequiredTicks(aimRequiredTicks: Int) = apply { this.aimRequiredTicks = aimRequiredTicks }
        fun targetDistanceSq(targetDistanceSq: Double) = apply { this.targetDistanceSq = targetDistanceSq }
        fun attackRangeSq(attackRangeSq: Double) = apply { this.attackRangeSq = attackRangeSq }
        fun hostLightAttackEnabled(hostLightAttackEnabled: Boolean) = apply { this.hostLightAttackEnabled = hostLightAttackEnabled }
        fun hostLightAmmo(hostLightAmmo: Int) = apply { this.hostLightAmmo = hostLightAmmo }
        fun lightDelay(lightDelay: Int) = apply { this.lightDelay = lightDelay }
        fun hostHeavyAttackEnabled(hostHeavyAttackEnabled: Boolean) = apply { this.hostHeavyAttackEnabled = hostHeavyAttackEnabled }
        fun hostHeavyAmmo(hostHeavyAmmo: Int) = apply { this.hostHeavyAmmo = hostHeavyAmmo }
        fun heavyDelay(heavyDelay: Int) = apply { this.heavyDelay = heavyDelay }

        fun build(): MountBrainDecisionResolver.State {
            return MountBrainDecisionResolver.State(
                hostPresent,
                hostAlive,
                hostOrderedToSit,
                mountPassenger,
                hostHasPointerTarget,
                hasGuardTarget,
                ownerPresent,
                ownerDistanceSq,
                mountWidth,
                followMaxConfig,
                hasAttackTarget,
                hasAliveAttackTarget,
                randomStrollRollHit,
                aimTick,
                aimRequiredTicks,
                targetDistanceSq,
                attackRangeSq,
                hostLightAttackEnabled,
                hostLightAmmo,
                lightDelay,
                hostHeavyAttackEnabled,
                hostHeavyAmmo,
                heavyDelay
            )
        }
    }
}
