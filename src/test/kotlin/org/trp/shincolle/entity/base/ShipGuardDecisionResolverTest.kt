package org.trp.shincolle.entity.base

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShipGuardDecisionResolverTest {

    @Test
    fun `stop distance should depend on guard target kind`() {
        assertThat(
            ShipGuardDecisionResolver.stopDistanceSqr(stateBuilder().entityGuard(true).liveEntityTarget(true).build())
        ).isEqualTo(ShipAiNumbers.GUARD_ENTITY_STOP_DISTANCE_SQ)
        assertThat(
            ShipGuardDecisionResolver.stopDistanceSqr(stateBuilder().entityGuard(false).blockTarget(true).build())
        ).isEqualTo(ShipAiNumbers.GUARD_BLOCK_STOP_DISTANCE_SQ)
    }

    @Test
    fun `movement should stop once inside resolved guard radius`() {
        assertThat(
            ShipGuardDecisionResolver.shouldMove(
                stateBuilder()
                    .entityGuard(true)
                    .liveEntityTarget(true)
                    .distanceSqr(ShipAiNumbers.GUARD_ENTITY_STOP_DISTANCE_SQ)
                    .build()
            )
        ).isFalse()
        assertThat(
            ShipGuardDecisionResolver.shouldMove(
                stateBuilder()
                    .entityGuard(true)
                    .liveEntityTarget(true)
                    .distanceSqr(ShipAiNumbers.GUARD_ENTITY_STOP_DISTANCE_SQ + 0.01)
                    .build()
            )
        ).isTrue()
        assertThat(
            ShipGuardDecisionResolver.shouldMove(
                stateBuilder()
                    .entityGuard(false)
                    .blockTarget(true)
                    .distanceSqr(ShipAiNumbers.GUARD_BLOCK_STOP_DISTANCE_SQ)
                    .build()
            )
        ).isFalse()
    }

    @Test
    fun `owner look fallback should only apply without guarded entity`() {
        assertThat(
            ShipGuardDecisionResolver.shouldLookAtOwnerOrPlayer(
                stateBuilder()
                    .hasGuardedEntity(false)
                    .distanceSqr(ShipAiNumbers.GUARD_NEAR_LOOK_DISTANCE_SQ - 0.01)
                    .build()
            )
        ).isTrue()
        assertThat(
            ShipGuardDecisionResolver.shouldLookAtOwnerOrPlayer(
                stateBuilder()
                    .hasGuardedEntity(false)
                    .summoning(true)
                    .distanceSqr(ShipAiNumbers.GUARD_NEAR_LOOK_DISTANCE_SQ + 100.0)
                    .build()
            )
        ).isTrue()
        assertThat(
            ShipGuardDecisionResolver.shouldLookAtOwnerOrPlayer(
                stateBuilder()
                    .hasGuardedEntity(true)
                    .summoning(true)
                    .distanceSqr(0.0)
                    .build()
            )
        ).isFalse()
    }

    @Test
    fun `target resolution should differentiate missing target and dimension drift`() {
        assertThat(ShipGuardDecisionResolver.hasResolvedTarget(stateBuilder().build())).isFalse()
        assertThat(ShipGuardDecisionResolver.hasResolvedTarget(stateBuilder().liveEntityTarget(true).build())).isTrue()
        assertThat(ShipGuardDecisionResolver.hasResolvedTarget(stateBuilder().blockTarget(true).build())).isTrue()

        assertThat(
            ShipGuardDecisionResolver.shouldSyncEntityDimension(
                stateBuilder()
                    .entityGuard(true)
                    .liveEntityTarget(true)
                    .hasGuardedEntity(true)
                    .guardDimensionId(0)
                    .guardedEntityDimensionId(0)
                    .build()
            )
        ).isFalse()
        assertThat(
            ShipGuardDecisionResolver.shouldSyncEntityDimension(
                stateBuilder()
                    .entityGuard(true)
                    .liveEntityTarget(true)
                    .hasGuardedEntity(true)
                    .guardDimensionId(0)
                    .guardedEntityDimensionId(1)
                    .build()
            )
        ).isTrue()
    }

    @Test
    fun `guard move fail threshold should delegate to shared recovery policy`() {
        assertThat(ShipGuardDecisionResolver.shouldClearAfterMoveFailures(ShipAiNumbers.MOVE_FAIL_LIMIT)).isFalse()
        assertThat(ShipGuardDecisionResolver.shouldClearAfterMoveFailures(ShipAiNumbers.MOVE_FAIL_LIMIT + 1)).isTrue()
    }

    private fun stateBuilder(): StateBuilder {
        return StateBuilder()
    }

    private class StateBuilder {
        private var entityGuard = false
        private var liveEntityTarget = false
        private var blockTarget = false
        private var hasGuardedEntity = false
        private var distanceSqr = 0.0
        private var summoning = false
        private var guardDimensionId = 0
        private var guardedEntityDimensionId = 0

        fun entityGuard(entityGuard: Boolean): StateBuilder {
            this.entityGuard = entityGuard
            return this
        }

        fun hasGuardedEntity(hasGuardedEntity: Boolean): StateBuilder {
            this.hasGuardedEntity = hasGuardedEntity
            return this
        }

        fun liveEntityTarget(liveEntityTarget: Boolean): StateBuilder {
            this.liveEntityTarget = liveEntityTarget
            return this
        }

        fun blockTarget(blockTarget: Boolean): StateBuilder {
            this.blockTarget = blockTarget
            return this
        }

        fun distanceSqr(distanceSqr: Double): StateBuilder {
            this.distanceSqr = distanceSqr
            return this
        }

        fun summoning(summoning: Boolean): StateBuilder {
            this.summoning = summoning
            return this
        }

        fun guardDimensionId(guardDimensionId: Int): StateBuilder {
            this.guardDimensionId = guardDimensionId
            return this
        }

        fun guardedEntityDimensionId(guardedEntityDimensionId: Int): StateBuilder {
            this.guardedEntityDimensionId = guardedEntityDimensionId
            return this
        }

        fun build(): ShipGuardDecisionResolver.State {
            return ShipGuardDecisionResolver.State(
                entityGuard,
                liveEntityTarget,
                blockTarget,
                hasGuardedEntity,
                distanceSqr,
                summoning,
                guardDimensionId,
                guardedEntityDimensionId
            )
        }
    }
}
