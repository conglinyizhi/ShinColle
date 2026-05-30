package org.trp.shincolle.entity.base;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipBrainActivityResolverTest {

    @Test
    void pointerCommandShouldOutrankCombatFollowAndIdle() {
        ShipBrainActivityResolver.State state = stateBuilder()
                .hasPointerTarget(true)
                .hasAttackTarget(true)
                .canGuard(true)
                .ownerDistanceSq(400.0D)
                .build();

        assertEquals(List.of(
                        ShipBrainActivityResolver.Mode.COMMAND,
                        ShipBrainActivityResolver.Mode.COMBAT,
                        ShipBrainActivityResolver.Mode.FOLLOW,
                        ShipBrainActivityResolver.Mode.IDLE),
                ShipBrainActivityResolver.resolveActiveModes(state, false));
        assertEquals("COMMAND", ShipBrainActivityResolver.describeDesiredActivity(state));
    }

    @Test
    void pointerEntityCommandShouldUseSameCommandActivityAsPointerPoint() {
        ShipBrainActivityResolver.State state = stateBuilder()
                .hasPointerTarget(true)
                .ownerDistanceSq(0.0D)
                .build();

        assertEquals(List.of(
                        ShipBrainActivityResolver.Mode.COMMAND,
                        ShipBrainActivityResolver.Mode.COMBAT,
                        ShipBrainActivityResolver.Mode.FOLLOW,
                        ShipBrainActivityResolver.Mode.IDLE),
                ShipBrainActivityResolver.resolveActiveModes(state, false));
        assertEquals("COMMAND", ShipBrainActivityResolver.describeDesiredActivity(state));
    }

    @Test
    void combatShouldSuppressFollowWhenNoPointerCommandExists() {
        ShipBrainActivityResolver.State state = stateBuilder()
                .hasAttackTarget(true)
                .canGuard(true)
                .ownerDistanceSq(400.0D)
                .build();

        assertEquals(List.of(
                        ShipBrainActivityResolver.Mode.COMBAT,
                        ShipBrainActivityResolver.Mode.IDLE),
                ShipBrainActivityResolver.resolveActiveModes(state, false));
        assertEquals("COMBAT", ShipBrainActivityResolver.describeDesiredActivity(state));
    }

    @Test
    void guardShouldOutrankFollowWhenNoCommandOrCombatExists() {
        ShipBrainActivityResolver.State state = stateBuilder()
                .canGuard(true)
                .ownerDistanceSq(400.0D)
                .build();

        assertEquals(List.of(
                        ShipBrainActivityResolver.Mode.GUARD,
                        ShipBrainActivityResolver.Mode.FOLLOW,
                        ShipBrainActivityResolver.Mode.IDLE),
                ShipBrainActivityResolver.resolveActiveModes(state, false));
        assertEquals("GUARD", ShipBrainActivityResolver.describeDesiredActivity(state));
    }

    @Test
    void followShouldUseMaxDistanceToStartAndMinDistanceToContinue() {
        ShipBrainActivityResolver.State belowStart = stateBuilder()
                .ownerDistanceSq(ShipAiNumbers.FOLLOW_OWNER_MAX_DIST * ShipAiNumbers.FOLLOW_OWNER_MAX_DIST)
                .build();
        ShipBrainActivityResolver.State aboveStart = stateBuilder()
                .ownerDistanceSq(square(ShipAiNumbers.FOLLOW_OWNER_MAX_DIST + 0.5D))
                .build();
        ShipBrainActivityResolver.State aboveMinWhileFollowing = stateBuilder()
                .ownerDistanceSq(square(ShipAiNumbers.FOLLOW_OWNER_MIN_DIST + 0.5D))
                .build();
        ShipBrainActivityResolver.State belowMinWhileFollowing = stateBuilder()
                .ownerDistanceSq(square(ShipAiNumbers.FOLLOW_OWNER_MIN_DIST - 0.5D))
                .build();

        assertFalse(ShipBrainActivityResolver.shouldFollowOwner(belowStart, false));
        assertTrue(ShipBrainActivityResolver.shouldFollowOwner(aboveStart, false));
        assertTrue(ShipBrainActivityResolver.shouldFollowOwner(aboveMinWhileFollowing, true));
        assertFalse(ShipBrainActivityResolver.shouldFollowOwner(belowMinWhileFollowing, true));
    }

    @Test
    void followShouldNotCompeteWithCombatTarget() {
        ShipBrainActivityResolver.State combat = stateBuilder()
                .hasAttackTarget(true)
                .ownerDistanceSq(square(ShipAiNumbers.FOLLOW_OWNER_MAX_DIST + 0.5D))
                .build();

        assertFalse(ShipBrainActivityResolver.shouldFollowOwner(combat, false));
        assertFalse(ShipBrainActivityResolver.shouldFollowOwner(combat, true));
    }

    @Test
    void followDistanceConfigShouldClampAndKeepMaxAboveMin() {
        float min = ShipBrainActivityResolver.resolveFollowMinDistance(99);
        float max = ShipBrainActivityResolver.resolveFollowMaxDistance(1, min);

        assertEquals(ShipAiNumbers.FOLLOW_MIN_DIST_CONFIG_MAX, min);
        assertEquals(ShipAiNumbers.FOLLOW_MAX_DIST_CONFIG_MAX, max);
    }

    @Test
    void combatRationShouldUseShortFollowThreshold() {
        ShipBrainActivityResolver.State close = stateBuilder()
                .ownerHasCombatRation(true)
                .ownerDistanceSq(ShipAiNumbers.FOLLOW_RATION_DISTANCE_SQ)
                .build();
        ShipBrainActivityResolver.State far = stateBuilder()
                .ownerHasCombatRation(true)
                .ownerDistanceSq(ShipAiNumbers.FOLLOW_RATION_DISTANCE_SQ + 0.01D)
                .build();

        assertFalse(ShipBrainActivityResolver.shouldFollowOwner(close, false));
        assertTrue(ShipBrainActivityResolver.shouldFollowOwner(far, false));
    }

    @Test
    void followContinuationShouldUseUnifiedResolverState() {
        ShipBrainActivityResolver.FollowState blockedByDimension = followStateBuilder()
                .hasSameDimensionOwnerPosition(false)
                .ownerDistanceSq(400.0D)
                .build();
        ShipBrainActivityResolver.FollowState closeWhileRationed = followStateBuilder()
                .ownerHasCombatRation(true)
                .ownerDistanceSq(ShipAiNumbers.FOLLOW_RATION_DISTANCE_SQ)
                .build();
        ShipBrainActivityResolver.FollowState farWhileRationed = followStateBuilder()
                .ownerHasCombatRation(true)
                .ownerDistanceSq(ShipAiNumbers.FOLLOW_RATION_DISTANCE_SQ + 0.01D)
                .build();
        ShipBrainActivityResolver.FollowState belowMinDistance = followStateBuilder()
                .followMinConfig((int) ShipAiNumbers.FOLLOW_OWNER_MIN_DIST)
                .ownerDistanceSq(square(ShipAiNumbers.FOLLOW_OWNER_MIN_DIST) - 0.01D)
                .build();
        ShipBrainActivityResolver.FollowState aboveMinDistance = followStateBuilder()
                .followMinConfig((int) ShipAiNumbers.FOLLOW_OWNER_MIN_DIST)
                .ownerDistanceSq(square(ShipAiNumbers.FOLLOW_OWNER_MIN_DIST) + 0.01D)
                .build();

        assertFalse(ShipBrainActivityResolver.shouldContinueFollow(blockedByDimension));
        assertFalse(ShipBrainActivityResolver.shouldContinueFollow(closeWhileRationed));
        assertTrue(ShipBrainActivityResolver.shouldContinueFollow(farWhileRationed));
        assertFalse(ShipBrainActivityResolver.shouldContinueFollow(belowMinDistance));
        assertTrue(ShipBrainActivityResolver.shouldContinueFollow(aboveMinDistance));
    }

    @Test
    void idleShouldBeOnlyActivityWhenMovementOrOwnerFollowIsUnavailable() {
        ShipBrainActivityResolver.State noOwner = stateBuilder()
                .ownerPresent(false)
                .ownerDistanceSq(400.0D)
                .build();
        ShipBrainActivityResolver.State cannotMove = stateBuilder()
                .canMove(false)
                .hasPointerTarget(true)
                .canGuard(true)
                .ownerDistanceSq(400.0D)
                .build();

        assertEquals(List.of(ShipBrainActivityResolver.Mode.IDLE),
                ShipBrainActivityResolver.resolveActiveModes(noOwner, false));
        assertEquals(List.of(ShipBrainActivityResolver.Mode.IDLE),
                ShipBrainActivityResolver.resolveActiveModes(cannotMove, false));
    }

    private static double square(double value) {
        return value * value;
    }

    private static StateBuilder stateBuilder() {
        return new StateBuilder();
    }

    private static FollowStateBuilder followStateBuilder() {
        return new FollowStateBuilder();
    }

    private static final class StateBuilder {
        private boolean canMove = true;
        private boolean hasPointerTarget;
        private boolean hasAttackTarget;
        private boolean canGuard;
        private boolean shouldFollowOwner = true;
        private boolean ownerPresent = true;
        private boolean ownerHasCombatRation;
        private double ownerDistanceSq;
        private int followMinConfig;
        private int followMaxConfig;

        private StateBuilder canMove(boolean canMove) {
            this.canMove = canMove;
            return this;
        }

        private StateBuilder hasPointerTarget(boolean hasPointerTarget) {
            this.hasPointerTarget = hasPointerTarget;
            return this;
        }

        private StateBuilder hasAttackTarget(boolean hasAttackTarget) {
            this.hasAttackTarget = hasAttackTarget;
            return this;
        }

        private StateBuilder canGuard(boolean canGuard) {
            this.canGuard = canGuard;
            return this;
        }

        private StateBuilder ownerPresent(boolean ownerPresent) {
            this.ownerPresent = ownerPresent;
            return this;
        }

        private StateBuilder ownerHasCombatRation(boolean ownerHasCombatRation) {
            this.ownerHasCombatRation = ownerHasCombatRation;
            return this;
        }

        private StateBuilder ownerDistanceSq(double ownerDistanceSq) {
            this.ownerDistanceSq = ownerDistanceSq;
            return this;
        }

        private ShipBrainActivityResolver.State build() {
            return new ShipBrainActivityResolver.State(
                    this.canMove,
                    this.hasPointerTarget,
                    this.hasAttackTarget,
                    this.canGuard,
                    this.shouldFollowOwner,
                    this.ownerPresent,
                    this.ownerHasCombatRation,
                    this.ownerDistanceSq,
                    this.followMinConfig,
                    this.followMaxConfig
            );
        }
    }

    private static final class FollowStateBuilder {
        private boolean canMove = true;
        private boolean shouldFollow = true;
        private boolean hasSameDimensionOwnerPosition = true;
        private boolean ownerHasCombatRation;
        private double ownerDistanceSq;
        private int followMinConfig;

        private FollowStateBuilder canMove(boolean canMove) {
            this.canMove = canMove;
            return this;
        }

        private FollowStateBuilder shouldFollow(boolean shouldFollow) {
            this.shouldFollow = shouldFollow;
            return this;
        }

        private FollowStateBuilder hasSameDimensionOwnerPosition(boolean hasSameDimensionOwnerPosition) {
            this.hasSameDimensionOwnerPosition = hasSameDimensionOwnerPosition;
            return this;
        }

        private FollowStateBuilder ownerHasCombatRation(boolean ownerHasCombatRation) {
            this.ownerHasCombatRation = ownerHasCombatRation;
            return this;
        }

        private FollowStateBuilder ownerDistanceSq(double ownerDistanceSq) {
            this.ownerDistanceSq = ownerDistanceSq;
            return this;
        }

        private FollowStateBuilder followMinConfig(int followMinConfig) {
            this.followMinConfig = followMinConfig;
            return this;
        }

        private ShipBrainActivityResolver.FollowState build() {
            return new ShipBrainActivityResolver.FollowState(
                    this.canMove,
                    this.shouldFollow,
                    this.hasSameDimensionOwnerPosition,
                    this.ownerHasCombatRation,
                    this.ownerDistanceSq,
                    this.followMinConfig
            );
        }
    }
}
