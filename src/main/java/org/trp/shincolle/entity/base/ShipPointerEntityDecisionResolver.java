package org.trp.shincolle.entity.base;

final class ShipPointerEntityDecisionResolver {
    private ShipPointerEntityDecisionResolver() {
    }

    static Decision resolve(State state) {
        double attackRangeSqr = attackRangeSqr(state.shipWidth(), state.targetWidth());
        double preferredRangeSqr = preferredRangeSqr(state, attackRangeSqr);
        boolean hasRangedAttack = state.canUseLightAmmo()
                || state.canUseHeavyAmmo()
                || state.hasAircraftAttackEnabled();
        double stopRangeSqr = hasRangedAttack
                ? preferredRangeSqr + ShipAiNumbers.POINTER_ENTITY_PATH_REFRESH_DISTANCE_SQR
                : preferredRangeSqr;
        boolean needsCloser = state.targetPresent() && state.distanceSqr() > stopRangeSqr;
        boolean cannotSee = state.targetPresent()
                && !state.hasLineOfSight()
                && state.distanceSqr() > preferredRangeSqr * 0.5D;
        return new Decision(
                preferredRangeSqr,
                stopRangeSqr,
                needsCloser,
                cannotSee,
                needsCloser || cannotSee,
                attackRangeSqr,
                hasRangedAttack,
                state.canUseMeleeAttack()
        );
    }

    static int aimDelayTicks(int shipLevel) {
        return Math.max(ShipAiNumbers.POINTER_ENTITY_AIM_DELAY_MIN,
                (int) (ShipAiNumbers.POINTER_ENTITY_AIM_DELAY_SCALE
                        * (ShipAiNumbers.POINTER_ENTITY_AIM_LEVEL_CAP - shipLevel)
                        / ShipAiNumbers.POINTER_ENTITY_AIM_LEVEL_CAP)
                        + ShipAiNumbers.POINTER_ENTITY_AIM_DELAY_BASE);
    }

    static boolean shouldFireLightAttack(AttackState state) {
        return state.canUseLightAmmo()
                && elapsed(state.tickCount(), state.lastLightShotTick()) >= Math.max(1, state.lightAttackInterval());
    }

    static boolean shouldFireHeavyAttack(AttackState state) {
        return state.canUseHeavyAmmo()
                && elapsed(state.tickCount(), state.lastHeavyShotTick()) >= Math.max(1, state.heavyAttackInterval());
    }

    static boolean shouldFireMeleeAttack(AttackState state) {
        return state.canUseMeleeAttack()
                && state.targetDistanceSqr() <= state.attackRangeSqr()
                && elapsed(state.tickCount(), state.lastMeleeAttackTick()) >= Math.max(1, state.meleeAttackInterval());
    }

    private static int elapsed(int tickCount, int lastActionTick) {
        return tickCount - lastActionTick;
    }

    private static double preferredRangeSqr(State state, double attackRangeSqr) {
        if (state.canUseLightAmmo() || state.canUseHeavyAmmo()) {
            double range = Math.max(2.0D, state.attackRange());
            return range * range;
        }
        if (state.hasAircraftAttackEnabled()) {
            double range = Math.max(24.0D, state.attackRange() * 1.5D);
            return range * range;
        }
        return attackRangeSqr;
    }

    private static double attackRangeSqr(double shipWidth, double targetWidth) {
        double width = shipWidth * 2.0F;
        double reach = width * width + targetWidth;
        return Math.max(reach, ShipAiNumbers.POINTER_ENTITY_ATTACK_RANGE_SQR);
    }

    record State(
            boolean targetPresent,
            double distanceSqr,
            boolean hasLineOfSight,
            boolean canUseLightAmmo,
            boolean canUseHeavyAmmo,
            boolean hasAircraftAttackEnabled,
            boolean canUseMeleeAttack,
            double attackRange,
            double shipWidth,
            double targetWidth
    ) {
    }

    record Decision(
            double preferredRangeSqr,
            double stopRangeSqr,
            boolean needsCloser,
            boolean cannotSee,
            boolean shouldChase,
            double attackRangeSqr,
            boolean hasRangedAttack,
            boolean canMeleeAttack
    ) {
    }

    record AttackState(
            int tickCount,
            boolean canUseLightAmmo,
            int lightAttackInterval,
            int lastLightShotTick,
            boolean canUseHeavyAmmo,
            int heavyAttackInterval,
            int lastHeavyShotTick,
            boolean canUseMeleeAttack,
            double targetDistanceSqr,
            double attackRangeSqr,
            int meleeAttackInterval,
            int lastMeleeAttackTick
    ) {
    }
}
