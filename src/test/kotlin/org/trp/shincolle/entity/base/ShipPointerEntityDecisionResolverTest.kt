package org.trp.shincolle.entity.base

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShipPointerEntityDecisionResolverTest {

    @Test
    fun `ammo attack should use ship attack range and stop after small padding`() {
        val inRange = ShipPointerEntityDecisionResolver.resolve(
            stateBuilder()
                .canUseLightAmmo(true)
                .attackRange(12.0)
                .distanceSqr(12.0 * 12.0 + ShipAiNumbers.POINTER_ENTITY_PATH_REFRESH_DISTANCE_SQR)
                .build()
        )
        val tooFar = ShipPointerEntityDecisionResolver.resolve(
            stateBuilder()
                .canUseHeavyAmmo(true)
                .attackRange(12.0)
                .distanceSqr(12.0 * 12.0 + ShipAiNumbers.POINTER_ENTITY_PATH_REFRESH_DISTANCE_SQR + 0.01)
                .build()
        )

        assertThat(inRange.preferredRangeSqr).isEqualTo(144.0)
        assertThat(inRange.shouldChase).isFalse()
        assertThat(tooFar.needsCloser).isTrue()
        assertThat(tooFar.shouldChase).isTrue()
    }

    @Test
    fun `aircraft attack should use longer carrier range floor`() {
        val shortStatRange = ShipPointerEntityDecisionResolver.resolve(
            stateBuilder()
                .hasAircraftAttackEnabled(true)
                .attackRange(8.0)
                .distanceSqr(24.0 * 24.0)
                .build()
        )
        val scaledStatRange = ShipPointerEntityDecisionResolver.resolve(
            stateBuilder()
                .hasAircraftAttackEnabled(true)
                .attackRange(20.0)
                .distanceSqr(30.0 * 30.0 + ShipAiNumbers.POINTER_ENTITY_PATH_REFRESH_DISTANCE_SQR + 0.01)
                .build()
        )

        assertThat(shortStatRange.preferredRangeSqr).isEqualTo(24.0 * 24.0)
        assertThat(shortStatRange.shouldChase).isFalse()
        assertThat(scaledStatRange.preferredRangeSqr).isEqualTo(30.0 * 30.0)
        assertThat(scaledStatRange.shouldChase).isTrue()
    }

    @Test
    fun `melee attack should use body reach and minimum attack range`() {
        val minimumRange = ShipPointerEntityDecisionResolver.resolve(
            stateBuilder()
                .canUseMeleeAttack(true)
                .shipWidth(0.6)
                .targetWidth(0.6)
                .distanceSqr(ShipAiNumbers.POINTER_ENTITY_ATTACK_RANGE_SQR)
                .build()
        )
        val largeBodyRange = ShipPointerEntityDecisionResolver.resolve(
            stateBuilder()
                .canUseMeleeAttack(true)
                .shipWidth(2.0)
                .targetWidth(1.0)
                .distanceSqr(17.01)
                .build()
        )

        assertThat(minimumRange.attackRangeSqr).isEqualTo(ShipAiNumbers.POINTER_ENTITY_ATTACK_RANGE_SQR)
        assertThat(minimumRange.shouldChase).isFalse()
        assertThat(largeBodyRange.attackRangeSqr).isEqualTo(17.0)
        assertThat(largeBodyRange.needsCloser).isTrue()
    }

    @Test
    fun `lost line of sight should chase when target is not already near preferred range`() {
        val nearBlind = ShipPointerEntityDecisionResolver.resolve(
            stateBuilder()
                .canUseLightAmmo(true)
                .attackRange(10.0)
                .hasLineOfSight(false)
                .distanceSqr(50.0)
                .build()
        )
        val farBlind = ShipPointerEntityDecisionResolver.resolve(
            stateBuilder()
                .canUseLightAmmo(true)
                .attackRange(10.0)
                .hasLineOfSight(false)
                .distanceSqr(50.01)
                .build()
        )

        assertThat(nearBlind.cannotSee).isFalse()
        assertThat(nearBlind.shouldChase).isFalse()
        assertThat(farBlind.cannotSee).isTrue()
        assertThat(farBlind.shouldChase).isTrue()
    }

    @Test
    fun `missing target should not chase even when distance would otherwise require movement`() {
        val decision = ShipPointerEntityDecisionResolver.resolve(
            stateBuilder()
                .targetPresent(false)
                .canUseLightAmmo(true)
                .attackRange(10.0)
                .hasLineOfSight(false)
                .distanceSqr(10_000.0)
                .build()
        )

        assertThat(decision.needsCloser).isFalse()
        assertThat(decision.cannotSee).isFalse()
        assertThat(decision.shouldChase).isFalse()
    }

    @Test
    fun `aim delay should shrink with ship level and keep minimum`() {
        assertThat(ShipPointerEntityDecisionResolver.aimDelayTicks(0)).isEqualTo(30)
        assertThat(ShipPointerEntityDecisionResolver.aimDelayTicks(75)).isEqualTo(20)
        assertThat(ShipPointerEntityDecisionResolver.aimDelayTicks(150)).isEqualTo(10)
        assertThat(ShipPointerEntityDecisionResolver.aimDelayTicks(300))
            .isEqualTo(ShipAiNumbers.POINTER_ENTITY_AIM_DELAY_MIN)
    }

    @Test
    fun `light attack should require ammo and elapsed cadence`() {
        assertThat(
            ShipPointerEntityDecisionResolver.shouldFireLightAttack(
                attackStateBuilder()
                    .canUseLightAmmo(true)
                    .lightAttackInterval(5)
                    .tickCount(25)
                    .lastLightShotTick(20)
                    .build()
            )
        ).isTrue()
        assertThat(
            ShipPointerEntityDecisionResolver.shouldFireLightAttack(
                attackStateBuilder()
                    .canUseLightAmmo(true)
                    .lightAttackInterval(5)
                    .tickCount(24)
                    .lastLightShotTick(20)
                    .build()
            )
        ).isFalse()
        assertThat(
            ShipPointerEntityDecisionResolver.shouldFireLightAttack(
                attackStateBuilder()
                    .canUseLightAmmo(false)
                    .lightAttackInterval(5)
                    .tickCount(25)
                    .lastLightShotTick(20)
                    .build()
            )
        ).isFalse()
    }

    @Test
    fun `heavy attack should require ammo and elapsed cadence`() {
        assertThat(
            ShipPointerEntityDecisionResolver.shouldFireHeavyAttack(
                attackStateBuilder()
                    .canUseHeavyAmmo(true)
                    .heavyAttackInterval(7)
                    .tickCount(27)
                    .lastHeavyShotTick(20)
                    .build()
            )
        ).isTrue()
        assertThat(
            ShipPointerEntityDecisionResolver.shouldFireHeavyAttack(
                attackStateBuilder()
                    .canUseHeavyAmmo(true)
                    .heavyAttackInterval(7)
                    .tickCount(26)
                    .lastHeavyShotTick(20)
                    .build()
            )
        ).isFalse()
        assertThat(
            ShipPointerEntityDecisionResolver.shouldFireHeavyAttack(
                attackStateBuilder()
                    .canUseHeavyAmmo(false)
                    .heavyAttackInterval(7)
                    .tickCount(27)
                    .lastHeavyShotTick(20)
                    .build()
            )
        ).isFalse()
    }

    @Test
    fun `melee attack should require reach and elapsed cadence`() {
        assertThat(
            ShipPointerEntityDecisionResolver.shouldFireMeleeAttack(
                attackStateBuilder()
                    .canUseMeleeAttack(true)
                    .targetDistanceSqr(4.0)
                    .attackRangeSqr(4.0)
                    .meleeAttackInterval(4)
                    .tickCount(24)
                    .lastMeleeAttackTick(20)
                    .build()
            )
        ).isTrue()
        assertThat(
            ShipPointerEntityDecisionResolver.shouldFireMeleeAttack(
                attackStateBuilder()
                    .canUseMeleeAttack(true)
                    .targetDistanceSqr(4.01)
                    .attackRangeSqr(4.0)
                    .meleeAttackInterval(4)
                    .tickCount(24)
                    .lastMeleeAttackTick(20)
                    .build()
            )
        ).isFalse()
        assertThat(
            ShipPointerEntityDecisionResolver.shouldFireMeleeAttack(
                attackStateBuilder()
                    .canUseMeleeAttack(true)
                    .targetDistanceSqr(4.0)
                    .attackRangeSqr(4.0)
                    .meleeAttackInterval(4)
                    .tickCount(23)
                    .lastMeleeAttackTick(20)
                    .build()
            )
        ).isFalse()
    }

    private fun stateBuilder(): StateBuilder = StateBuilder()

    private fun attackStateBuilder(): AttackStateBuilder = AttackStateBuilder()

    private class StateBuilder {
        private var targetPresent = true
        private var distanceSqr = 0.0
        private var hasLineOfSight = true
        private var canUseLightAmmo = false
        private var canUseHeavyAmmo = false
        private var hasAircraftAttackEnabled = false
        private var canUseMeleeAttack = false
        private var attackRange = 10.0
        private var shipWidth = 0.6
        private var targetWidth = 0.6

        fun targetPresent(targetPresent: Boolean) = apply { this.targetPresent = targetPresent }
        fun distanceSqr(distanceSqr: Double) = apply { this.distanceSqr = distanceSqr }
        fun hasLineOfSight(hasLineOfSight: Boolean) = apply { this.hasLineOfSight = hasLineOfSight }
        fun canUseLightAmmo(canUseLightAmmo: Boolean) = apply { this.canUseLightAmmo = canUseLightAmmo }
        fun canUseHeavyAmmo(canUseHeavyAmmo: Boolean) = apply { this.canUseHeavyAmmo = canUseHeavyAmmo }
        fun hasAircraftAttackEnabled(hasAircraftAttackEnabled: Boolean) = apply { this.hasAircraftAttackEnabled = hasAircraftAttackEnabled }
        fun canUseMeleeAttack(canUseMeleeAttack: Boolean) = apply { this.canUseMeleeAttack = canUseMeleeAttack }
        fun attackRange(attackRange: Double) = apply { this.attackRange = attackRange }
        fun shipWidth(shipWidth: Double) = apply { this.shipWidth = shipWidth }
        fun targetWidth(targetWidth: Double) = apply { this.targetWidth = targetWidth }

        fun build(): ShipPointerEntityDecisionResolver.State {
            return ShipPointerEntityDecisionResolver.State(
                targetPresent,
                distanceSqr,
                hasLineOfSight,
                canUseLightAmmo,
                canUseHeavyAmmo,
                hasAircraftAttackEnabled,
                canUseMeleeAttack,
                attackRange,
                shipWidth,
                targetWidth
            )
        }
    }

    private class AttackStateBuilder {
        private var tickCount = 0
        private var canUseLightAmmo = false
        private var lightAttackInterval = 1
        private var lastLightShotTick = 0
        private var canUseHeavyAmmo = false
        private var heavyAttackInterval = 1
        private var lastHeavyShotTick = 0
        private var canUseMeleeAttack = false
        private var targetDistanceSqr = 0.0
        private var attackRangeSqr = 0.0
        private var meleeAttackInterval = 1
        private var lastMeleeAttackTick = 0

        fun tickCount(tickCount: Int) = apply { this.tickCount = tickCount }
        fun canUseLightAmmo(canUseLightAmmo: Boolean) = apply { this.canUseLightAmmo = canUseLightAmmo }
        fun lightAttackInterval(lightAttackInterval: Int) = apply { this.lightAttackInterval = lightAttackInterval }
        fun lastLightShotTick(lastLightShotTick: Int) = apply { this.lastLightShotTick = lastLightShotTick }
        fun canUseHeavyAmmo(canUseHeavyAmmo: Boolean) = apply { this.canUseHeavyAmmo = canUseHeavyAmmo }
        fun heavyAttackInterval(heavyAttackInterval: Int) = apply { this.heavyAttackInterval = heavyAttackInterval }
        fun lastHeavyShotTick(lastHeavyShotTick: Int) = apply { this.lastHeavyShotTick = lastHeavyShotTick }
        fun canUseMeleeAttack(canUseMeleeAttack: Boolean) = apply { this.canUseMeleeAttack = canUseMeleeAttack }
        fun targetDistanceSqr(targetDistanceSqr: Double) = apply { this.targetDistanceSqr = targetDistanceSqr }
        fun attackRangeSqr(attackRangeSqr: Double) = apply { this.attackRangeSqr = attackRangeSqr }
        fun meleeAttackInterval(meleeAttackInterval: Int) = apply { this.meleeAttackInterval = meleeAttackInterval }
        fun lastMeleeAttackTick(lastMeleeAttackTick: Int) = apply { this.lastMeleeAttackTick = lastMeleeAttackTick }

        fun build(): ShipPointerEntityDecisionResolver.AttackState {
            return ShipPointerEntityDecisionResolver.AttackState(
                tickCount,
                canUseLightAmmo,
                lightAttackInterval,
                lastLightShotTick,
                canUseHeavyAmmo,
                heavyAttackInterval,
                lastHeavyShotTick,
                canUseMeleeAttack,
                targetDistanceSqr,
                attackRangeSqr,
                meleeAttackInterval,
                lastMeleeAttackTick
            )
        }
    }
}
