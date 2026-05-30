package org.trp.shincolle.entity.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipGuardDecisionResolverTest {

    @Test
    void stopDistanceShouldDependOnGuardTargetKind() {
        assertEquals(ShipAiNumbers.GUARD_ENTITY_STOP_DISTANCE_SQ,
                ShipGuardDecisionResolver.stopDistanceSqr(stateBuilder().entityGuard(true).liveEntityTarget(true).build()));
        assertEquals(ShipAiNumbers.GUARD_BLOCK_STOP_DISTANCE_SQ,
                ShipGuardDecisionResolver.stopDistanceSqr(stateBuilder().entityGuard(false).blockTarget(true).build()));
    }

    @Test
    void movementShouldStopOnceInsideResolvedGuardRadius() {
        assertFalse(ShipGuardDecisionResolver.shouldMove(stateBuilder()
                .entityGuard(true)
                .liveEntityTarget(true)
                .distanceSqr(ShipAiNumbers.GUARD_ENTITY_STOP_DISTANCE_SQ)
                .build()));
        assertTrue(ShipGuardDecisionResolver.shouldMove(stateBuilder()
                .entityGuard(true)
                .liveEntityTarget(true)
                .distanceSqr(ShipAiNumbers.GUARD_ENTITY_STOP_DISTANCE_SQ + 0.01D)
                .build()));
        assertFalse(ShipGuardDecisionResolver.shouldMove(stateBuilder()
                .entityGuard(false)
                .blockTarget(true)
                .distanceSqr(ShipAiNumbers.GUARD_BLOCK_STOP_DISTANCE_SQ)
                .build()));
    }

    @Test
    void ownerLookFallbackShouldOnlyApplyWithoutGuardedEntity() {
        assertTrue(ShipGuardDecisionResolver.shouldLookAtOwnerOrPlayer(stateBuilder()
                .hasGuardedEntity(false)
                .distanceSqr(ShipAiNumbers.GUARD_NEAR_LOOK_DISTANCE_SQ - 0.01D)
                .build()));
        assertTrue(ShipGuardDecisionResolver.shouldLookAtOwnerOrPlayer(stateBuilder()
                .hasGuardedEntity(false)
                .summoning(true)
                .distanceSqr(ShipAiNumbers.GUARD_NEAR_LOOK_DISTANCE_SQ + 100.0D)
                .build()));
        assertFalse(ShipGuardDecisionResolver.shouldLookAtOwnerOrPlayer(stateBuilder()
                .hasGuardedEntity(true)
                .summoning(true)
                .distanceSqr(0.0D)
                .build()));
    }

    @Test
    void targetResolutionShouldDifferentiateMissingTargetAndDimensionDrift() {
        assertFalse(ShipGuardDecisionResolver.hasResolvedTarget(stateBuilder().build()));
        assertTrue(ShipGuardDecisionResolver.hasResolvedTarget(stateBuilder().liveEntityTarget(true).build()));
        assertTrue(ShipGuardDecisionResolver.hasResolvedTarget(stateBuilder().blockTarget(true).build()));

        assertFalse(ShipGuardDecisionResolver.shouldSyncEntityDimension(stateBuilder()
                .entityGuard(true)
                .liveEntityTarget(true)
                .hasGuardedEntity(true)
                .guardDimensionId(0)
                .guardedEntityDimensionId(0)
                .build()));
        assertTrue(ShipGuardDecisionResolver.shouldSyncEntityDimension(stateBuilder()
                .entityGuard(true)
                .liveEntityTarget(true)
                .hasGuardedEntity(true)
                .guardDimensionId(0)
                .guardedEntityDimensionId(1)
                .build()));
    }

    private static StateBuilder stateBuilder() {
        return new StateBuilder();
    }

    private static final class StateBuilder {
        private boolean entityGuard;
        private boolean liveEntityTarget;
        private boolean blockTarget;
        private boolean hasGuardedEntity;
        private double distanceSqr;
        private boolean summoning;
        private int guardDimensionId;
        private int guardedEntityDimensionId;

        private StateBuilder entityGuard(boolean entityGuard) {
            this.entityGuard = entityGuard;
            return this;
        }

        private StateBuilder hasGuardedEntity(boolean hasGuardedEntity) {
            this.hasGuardedEntity = hasGuardedEntity;
            return this;
        }

        private StateBuilder liveEntityTarget(boolean liveEntityTarget) {
            this.liveEntityTarget = liveEntityTarget;
            return this;
        }

        private StateBuilder blockTarget(boolean blockTarget) {
            this.blockTarget = blockTarget;
            return this;
        }

        private StateBuilder distanceSqr(double distanceSqr) {
            this.distanceSqr = distanceSqr;
            return this;
        }

        private StateBuilder summoning(boolean summoning) {
            this.summoning = summoning;
            return this;
        }

        private StateBuilder guardDimensionId(int guardDimensionId) {
            this.guardDimensionId = guardDimensionId;
            return this;
        }

        private StateBuilder guardedEntityDimensionId(int guardedEntityDimensionId) {
            this.guardedEntityDimensionId = guardedEntityDimensionId;
            return this;
        }

        private ShipGuardDecisionResolver.State build() {
            return new ShipGuardDecisionResolver.State(
                    this.entityGuard,
                    this.liveEntityTarget,
                    this.blockTarget,
                    this.hasGuardedEntity,
                    this.distanceSqr,
                    this.summoning,
                    this.guardDimensionId,
                    this.guardedEntityDimensionId
            );
        }
    }
}
