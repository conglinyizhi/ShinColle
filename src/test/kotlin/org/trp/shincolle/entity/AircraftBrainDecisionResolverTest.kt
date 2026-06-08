package org.trp.shincolle.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AircraftBrainDecisionResolverTest {

    @Test
    fun `light aircraft should require alive target and light ammo`() {
        assertThat(
            AircraftBrainDecisionResolver.canAttackMissionTarget(
                stateBuilder().lightAircraft(true).hasLightAmmo(true).build()
            )
        ).isTrue()
        assertThat(
            AircraftBrainDecisionResolver.canAttackMissionTarget(
                stateBuilder().lightAircraft(true).hasLightAmmo(false).hasHeavyAmmo(true).build()
            )
        ).isFalse()
        assertThat(
            AircraftBrainDecisionResolver.canAttackMissionTarget(
                stateBuilder().lightAircraft(true).targetAlive(false).hasLightAmmo(true).build()
            )
        ).isFalse()
    }

    @Test
    fun `heavy aircraft should require alive target and heavy ammo`() {
        assertThat(
            AircraftBrainDecisionResolver.canAttackMissionTarget(
                stateBuilder().lightAircraft(false).hasHeavyAmmo(true).build()
            )
        ).isTrue()
        assertThat(
            AircraftBrainDecisionResolver.canAttackMissionTarget(
                stateBuilder().lightAircraft(false).hasLightAmmo(true).hasHeavyAmmo(false).build()
            )
        ).isFalse()
        assertThat(
            AircraftBrainDecisionResolver.canAttackMissionTarget(
                stateBuilder().targetPresent(false).hasHeavyAmmo(true).build()
            )
        ).isFalse()
    }

    @Test
    fun `attack should start only after activation ticks`() {
        assertThat(
            AircraftBrainDecisionResolver.shouldStartAttack(
                stateBuilder().missionTick(AircraftAiNumbers.ATTACK_ACTIVATION_TICKS).hasLightAmmo(true).build()
            )
        ).isFalse()
        assertThat(
            AircraftBrainDecisionResolver.shouldStartAttack(
                stateBuilder().missionTick(AircraftAiNumbers.ATTACK_ACTIVATION_TICKS + 1).hasLightAmmo(true).build()
            )
        ).isTrue()
    }

    @Test
    fun `attack range should follow aircraft type`() {
        assertThat(
            AircraftBrainDecisionResolver.attackRangeSqr(stateBuilder().lightAircraft(true).build())
        ).isEqualTo(36.0)
        assertThat(
            AircraftBrainDecisionResolver.attackRangeSqr(stateBuilder().lightAircraft(false).build())
        ).isEqualTo(256.0)
    }

    @Test
    fun `attack movement should slow while cooldown is active`() {
        assertThat(
            AircraftBrainDecisionResolver.attackMoveSpeed(
                stateBuilder().attackDelay(1).distanceSqr(400.0).build()
            )
        ).isEqualTo(AircraftAiNumbers.ATTACK_SPEED_SLOW)
    }

    @Test
    fun `attack movement should close fast until within range`() {
        assertThat(
            AircraftBrainDecisionResolver.attackMoveSpeed(
                stateBuilder().lightAircraft(true).attackDelay(0).distanceSqr(36.01).build()
            )
        ).isEqualTo(AircraftAiNumbers.ATTACK_SPEED_FAST)
        assertThat(
            AircraftBrainDecisionResolver.attackMoveSpeed(
                stateBuilder().lightAircraft(true).attackDelay(0).distanceSqr(36.0).build()
            )
        ).isEqualTo(AircraftAiNumbers.ATTACK_SPEED_SLOW)
    }

    @Test
    fun `fire should require ammo cooldown line of sight and strict range`() {
        assertThat(
            AircraftBrainDecisionResolver.shouldFire(
                stateBuilder()
                    .hasLightAmmo(true)
                    .attackDelay(0)
                    .hasLineOfSight(true)
                    .distanceSqr(35.99)
                    .build()
            )
        ).isTrue()
        assertThat(
            AircraftBrainDecisionResolver.shouldFire(
                stateBuilder()
                    .hasLightAmmo(true)
                    .attackDelay(0)
                    .hasLineOfSight(true)
                    .distanceSqr(36.0)
                    .build()
            )
        ).isFalse()
        assertThat(
            AircraftBrainDecisionResolver.shouldFire(
                stateBuilder()
                    .hasLightAmmo(true)
                    .attackDelay(1)
                    .hasLineOfSight(true)
                    .distanceSqr(35.99)
                    .build()
            )
        ).isFalse()
        assertThat(
            AircraftBrainDecisionResolver.shouldFire(
                stateBuilder()
                    .hasLightAmmo(true)
                    .attackDelay(0)
                    .hasLineOfSight(false)
                    .distanceSqr(35.99)
                    .build()
            )
        ).isFalse()
        assertThat(
            AircraftBrainDecisionResolver.shouldFire(
                stateBuilder()
                    .hasLightAmmo(false)
                    .attackDelay(0)
                    .hasLineOfSight(true)
                    .distanceSqr(35.99)
                    .build()
            )
        ).isFalse()
    }

    private fun stateBuilder(): StateBuilder = StateBuilder()

    private class StateBuilder {
        private var targetPresent = true
        private var targetAlive = true
        private var lightAircraft = true
        private var hasLightAmmo = false
        private var hasHeavyAmmo = false
        private var missionTick = 0
        private var attackDelay = 0
        private var hasLineOfSight = true
        private var distanceSqr = 0.0

        fun targetPresent(targetPresent: Boolean) = apply { this.targetPresent = targetPresent }
        fun targetAlive(targetAlive: Boolean) = apply { this.targetAlive = targetAlive }
        fun lightAircraft(lightAircraft: Boolean) = apply { this.lightAircraft = lightAircraft }
        fun hasLightAmmo(hasLightAmmo: Boolean) = apply { this.hasLightAmmo = hasLightAmmo }
        fun hasHeavyAmmo(hasHeavyAmmo: Boolean) = apply { this.hasHeavyAmmo = hasHeavyAmmo }
        fun missionTick(missionTick: Int) = apply { this.missionTick = missionTick }
        fun attackDelay(attackDelay: Int) = apply { this.attackDelay = attackDelay }
        fun hasLineOfSight(hasLineOfSight: Boolean) = apply { this.hasLineOfSight = hasLineOfSight }
        fun distanceSqr(distanceSqr: Double) = apply { this.distanceSqr = distanceSqr }

        fun build(): AircraftBrainDecisionResolver.State {
            return AircraftBrainDecisionResolver.State(
                targetPresent,
                targetAlive,
                lightAircraft,
                hasLightAmmo,
                hasHeavyAmmo,
                missionTick,
                attackDelay,
                hasLineOfSight,
                distanceSqr
            )
        }
    }
}
