package org.trp.shincolle.entity.base;

import java.util.List;

final class ShipBrainActivityResolver {
    private ShipBrainActivityResolver() {
    }

    static List<Mode> resolveActiveModes(State state, boolean following) {
        if (state.hasPointerTarget() && state.canMove()) {
            return List.of(Mode.COMMAND, Mode.COMBAT, Mode.FOLLOW, Mode.IDLE);
        }
        if (state.hasAttackTarget()) {
            return List.of(Mode.COMBAT, Mode.IDLE);
        }
        if (state.canMove() && state.canGuard()) {
            return List.of(Mode.GUARD, Mode.FOLLOW, Mode.IDLE);
        }
        if (shouldFollowOwner(state, following)) {
            return List.of(Mode.FOLLOW, Mode.IDLE);
        }
        return List.of(Mode.IDLE);
    }

    static String describeDesiredActivity(State state) {
        if (state.hasPointerTarget() && state.canMove()) {
            return Mode.COMMAND.name();
        }
        if (state.hasAttackTarget()) {
            return Mode.COMBAT.name();
        }
        if (state.canMove() && state.canGuard()) {
            return Mode.GUARD.name();
        }
        if (shouldFollowOwner(state, false)) {
            return Mode.FOLLOW.name();
        }
        return Mode.IDLE.name();
    }

    static boolean shouldFollowOwner(State state, boolean following) {
        if (!state.shouldFollowOwner() || !state.canMove() || !state.ownerPresent() || state.hasAttackTarget()) {
            return false;
        }
        if (state.ownerHasCombatRation()) {
            return state.ownerDistanceSq() > ShipAiNumbers.FOLLOW_RATION_DISTANCE_SQ;
        }

        float minDist = resolveFollowMinDistance(state.followMinConfig());
        float maxDist = resolveFollowMaxDistance(state.followMaxConfig(), minDist);
        if (following) {
            return state.ownerDistanceSq() > minDist * minDist;
        }
        return state.ownerDistanceSq() > maxDist * maxDist;
    }

    static boolean shouldContinueFollow(FollowState state) {
        if (!state.shouldFollow() || !state.canMove() || !state.hasSameDimensionOwnerPosition()) {
            return false;
        }
        if (state.ownerHasCombatRation()) {
            return state.ownerDistanceSq() > ShipAiNumbers.FOLLOW_RATION_DISTANCE_SQ;
        }

        float minDist = resolveFollowMinDistance(state.followMinConfig());
        return state.ownerDistanceSq() > minDist * minDist;
    }

    static float resolveFollowMinDistance(int configured) {
        if (configured <= 0) {
            return ShipAiNumbers.FOLLOW_OWNER_MIN_DIST;
        }
        return clamp(configured, ShipAiNumbers.FOLLOW_MIN_DIST_CONFIG_MIN, ShipAiNumbers.FOLLOW_MIN_DIST_CONFIG_MAX);
    }

    static float resolveFollowMaxDistance(int configured, float minDist) {
        if (configured <= 0) {
            return Math.max(ShipAiNumbers.FOLLOW_OWNER_MAX_DIST, minDist + ShipAiNumbers.FOLLOW_MAX_DIST_PADDING);
        }
        int minValue = Math.max(ShipAiNumbers.FOLLOW_MAX_DIST_CONFIG_MIN, (int) Math.floor(minDist) + 1);
        return clamp(configured, minValue, ShipAiNumbers.FOLLOW_MAX_DIST_CONFIG_MAX);
    }

    private static int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    record State(
            boolean canMove,
            boolean hasPointerTarget,
            boolean hasAttackTarget,
            boolean canGuard,
            boolean shouldFollowOwner,
            boolean ownerPresent,
            boolean ownerHasCombatRation,
            double ownerDistanceSq,
            int followMinConfig,
            int followMaxConfig
    ) {
    }

    record FollowState(
            boolean canMove,
            boolean shouldFollow,
            boolean hasSameDimensionOwnerPosition,
            boolean ownerHasCombatRation,
            double ownerDistanceSq,
            int followMinConfig
    ) {
    }

    enum Mode {
        COMMAND,
        GUARD,
        FOLLOW,
        COMBAT,
        IDLE
    }
}
