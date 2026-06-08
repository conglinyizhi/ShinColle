package org.trp.shincolle.entity.base

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SummonBrainDecisionResolverTest {

    @Test
    fun `attack should require alive target`() {
        assertThat(
            SummonBrainDecisionResolver.shouldAttack(
                stateBuilder().hasAttackTarget(true).hasAliveAttackTarget(true).build()
            )
        ).isTrue()
        assertThat(
            SummonBrainDecisionResolver.shouldAttack(
                stateBuilder().hasAttackTarget(true).hasAliveAttackTarget(false).build()
            )
        ).isFalse()
    }

    @Test
    fun `chase should only continue while target is alive and out of range`() {
        assertThat(
            SummonBrainDecisionResolver.shouldChaseAttackTarget(
                stateBuilder()
                    .hasAttackTarget(true)
                    .hasAliveAttackTarget(true)
                    .targetDistanceSq(16.01)
                    .attackRangeSq(16.0)
                    .build()
            )
        ).isTrue()
        assertThat(
            SummonBrainDecisionResolver.shouldChaseAttackTarget(
                stateBuilder()
                    .hasAttackTarget(true)
                    .hasAliveAttackTarget(true)
                    .targetDistanceSq(16.0)
                    .attackRangeSq(16.0)
                    .build()
            )
        ).isFalse()
        assertThat(
            SummonBrainDecisionResolver.shouldChaseAttackTarget(
                stateBuilder()
                    .hasAttackTarget(true)
                    .hasAliveAttackTarget(false)
                    .targetDistanceSq(20.0)
                    .attackRangeSq(16.0)
                    .build()
            )
        ).isFalse()
    }

    @Test
    fun `perform attack should require alive target cooldown and in range`() {
        assertThat(
            SummonBrainDecisionResolver.shouldPerformAttack(
                stateBuilder()
                    .hasAttackTarget(true)
                    .hasAliveAttackTarget(true)
                    .targetDistanceSq(16.0)
                    .attackRangeSq(16.0)
                    .attackDelay(0)
                    .build()
            )
        ).isTrue()
        assertThat(
            SummonBrainDecisionResolver.shouldPerformAttack(
                stateBuilder()
                    .hasAttackTarget(true)
                    .hasAliveAttackTarget(true)
                    .targetDistanceSq(16.01)
                    .attackRangeSq(16.0)
                    .attackDelay(0)
                    .build()
            )
        ).isFalse()
        assertThat(
            SummonBrainDecisionResolver.shouldPerformAttack(
                stateBuilder()
                    .hasAttackTarget(true)
                    .hasAliveAttackTarget(true)
                    .targetDistanceSq(16.0)
                    .attackRangeSq(16.0)
                    .attackDelay(1)
                    .build()
            )
        ).isFalse()
    }

    @Test
    fun `follow carrier should require alive carrier distance and no attack target`() {
        assertThat(
            SummonBrainDecisionResolver.shouldFollowCarrier(
                stateBuilder().carrierDistanceSq(SummonAiNumbers.FOLLOW_CARRIER_DISTANCE_SQ + 0.01).build()
            )
        ).isTrue()
        assertThat(
            SummonBrainDecisionResolver.shouldFollowCarrier(
                stateBuilder().carrierDistanceSq(SummonAiNumbers.FOLLOW_CARRIER_DISTANCE_SQ).build()
            )
        ).isFalse()
        assertThat(
            SummonBrainDecisionResolver.shouldFollowCarrier(
                stateBuilder().carrierPresent(false).build()
            )
        ).isFalse()
        assertThat(
            SummonBrainDecisionResolver.shouldFollowCarrier(
                stateBuilder().carrierAlive(false).build()
            )
        ).isFalse()
        assertThat(
            SummonBrainDecisionResolver.shouldFollowCarrier(
                stateBuilder().hasAttackTarget(true).build()
            )
        ).isFalse()
    }

    @Test
    fun `random stroll should not race carrier follow or combat movement`() {
        assertThat(
            SummonBrainDecisionResolver.shouldRandomStroll(
                stateBuilder().carrierDistanceSq(0.0).randomStrollRollHit(true).build()
            )
        ).isTrue()
        assertThat(
            SummonBrainDecisionResolver.shouldRandomStroll(
                stateBuilder().carrierPresent(false).randomStrollRollHit(true).build()
            )
        ).isFalse()
        assertThat(
            SummonBrainDecisionResolver.shouldRandomStroll(
                stateBuilder().carrierAlive(false).randomStrollRollHit(true).build()
            )
        ).isFalse()
        assertThat(
            SummonBrainDecisionResolver.shouldRandomStroll(
                stateBuilder().hasAttackTarget(true).randomStrollRollHit(true).build()
            )
        ).isFalse()
        assertThat(
            SummonBrainDecisionResolver.shouldRandomStroll(
                stateBuilder()
                    .carrierDistanceSq(SummonAiNumbers.FOLLOW_CARRIER_DISTANCE_SQ + 0.01)
                    .randomStrollRollHit(true)
                    .build()
            )
        ).isFalse()
        assertThat(
            SummonBrainDecisionResolver.shouldRandomStroll(
                stateBuilder().randomStrollRollHit(false).build()
            )
        ).isFalse()
    }

    private fun stateBuilder(): StateBuilder = StateBuilder()

    private class StateBuilder {
        private var carrierPresent = true
        private var carrierAlive = true
        private var carrierDistanceSq = 0.0
        private var hasAttackTarget = false
        private var hasAliveAttackTarget = false
        private var randomStrollRollHit = false
        private var targetDistanceSq = 0.0
        private var attackRangeSq = SummonAiNumbers.DEFAULT_ATTACK_RANGE_SQ.toDouble()
        private var attackDelay = 0

        fun carrierPresent(carrierPresent: Boolean) = apply { this.carrierPresent = carrierPresent }
        fun carrierAlive(carrierAlive: Boolean) = apply { this.carrierAlive = carrierAlive }
        fun carrierDistanceSq(carrierDistanceSq: Double) = apply { this.carrierDistanceSq = carrierDistanceSq }
        fun hasAttackTarget(hasAttackTarget: Boolean) = apply { this.hasAttackTarget = hasAttackTarget }
        fun hasAliveAttackTarget(hasAliveAttackTarget: Boolean) = apply { this.hasAliveAttackTarget = hasAliveAttackTarget }
        fun randomStrollRollHit(randomStrollRollHit: Boolean) = apply { this.randomStrollRollHit = randomStrollRollHit }
        fun targetDistanceSq(targetDistanceSq: Double) = apply { this.targetDistanceSq = targetDistanceSq }
        fun attackRangeSq(attackRangeSq: Double) = apply { this.attackRangeSq = attackRangeSq }
        fun attackDelay(attackDelay: Int) = apply { this.attackDelay = attackDelay }

        fun build(): SummonBrainDecisionResolver.State {
            return SummonBrainDecisionResolver.State(
                carrierPresent,
                carrierAlive,
                carrierDistanceSq,
                hasAttackTarget,
                hasAliveAttackTarget,
                randomStrollRollHit,
                targetDistanceSq,
                attackRangeSq,
                attackDelay
            )
        }
    }
}
