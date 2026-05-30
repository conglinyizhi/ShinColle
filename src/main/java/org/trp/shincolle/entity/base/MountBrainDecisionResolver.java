package org.trp.shincolle.entity.base;

final class MountBrainDecisionResolver {
    private MountBrainDecisionResolver() {
    }

    static boolean shouldFollowHost(State state) {
        if (!state.hostPresent() || !state.hostAlive() || state.hostOrderedToSit() || state.mountPassenger()) {
            return false;
        }
        if (state.hostHasPointerTarget() || state.hasGuardTarget()) {
            return true;
        }
        if (!state.ownerPresent()) {
            return false;
        }

        double maxDistSq = state.followMaxConfig() * state.followMaxConfig()
                + state.mountWidth() * MountAiNumbers.FOLLOW_WIDTH_PADDING;
        return state.ownerDistanceSq() > maxDistSq;
    }

    static boolean shouldRangeAttack(State state) {
        return state.hostPresent()
                && state.hostAlive()
                && !state.hostOrderedToSit()
                && !state.mountPassenger()
                && state.hasAliveAttackTarget();
    }

    static boolean isAttackAimed(State state) {
        return state.aimTick() >= state.aimRequiredTicks();
    }

    static boolean isTargetWithinAttackRange(State state) {
        return state.targetDistanceSq() <= state.attackRangeSq();
    }

    static boolean shouldFireLight(State state) {
        return shouldRangeAttack(state)
                && isAttackAimed(state)
                && isTargetWithinAttackRange(state)
                && state.hostLightAttackEnabled()
                && state.hostLightAmmo() > 0
                && state.lightDelay() <= 0;
    }

    static boolean shouldFireHeavy(State state) {
        return shouldRangeAttack(state)
                && isAttackAimed(state)
                && isTargetWithinAttackRange(state)
                && state.hostHeavyAttackEnabled()
                && state.hostHeavyAmmo() > 0
                && state.heavyDelay() <= 0;
    }

    static boolean shouldRandomStroll(State state) {
        return state.hostPresent()
                && state.hostAlive()
                && !state.hostOrderedToSit()
                && !state.mountPassenger()
                && !state.hasAttackTarget()
                && !shouldFollowHost(state)
                && state.randomStrollRollHit();
    }

    record State(
            boolean hostPresent,
            boolean hostAlive,
            boolean hostOrderedToSit,
            boolean mountPassenger,
            boolean hostHasPointerTarget,
            boolean hasGuardTarget,
            boolean ownerPresent,
            double ownerDistanceSq,
            double mountWidth,
            int followMaxConfig,
            boolean hasAttackTarget,
            boolean hasAliveAttackTarget,
            boolean randomStrollRollHit,
            int aimTick,
            int aimRequiredTicks,
            double targetDistanceSq,
            double attackRangeSq,
            boolean hostLightAttackEnabled,
            int hostLightAmmo,
            int lightDelay,
            boolean hostHeavyAttackEnabled,
            int hostHeavyAmmo,
            int heavyDelay
    ) {
    }
}
