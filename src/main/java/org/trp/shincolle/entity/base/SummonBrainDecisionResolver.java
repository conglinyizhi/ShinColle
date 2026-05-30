package org.trp.shincolle.entity.base;

final class SummonBrainDecisionResolver {
    private SummonBrainDecisionResolver() {
    }

    static boolean shouldAttack(State state) {
        return state.hasAliveAttackTarget();
    }

    static boolean shouldChaseAttackTarget(State state) {
        return shouldAttack(state) && state.targetDistanceSq() > state.attackRangeSq();
    }

    static boolean shouldPerformAttack(State state) {
        return shouldAttack(state)
                && state.targetDistanceSq() <= state.attackRangeSq()
                && state.attackDelay() <= 0;
    }

    static boolean shouldFollowCarrier(State state) {
        return state.carrierPresent()
                && state.carrierAlive()
                && state.carrierDistanceSq() > SummonAiNumbers.FOLLOW_CARRIER_DISTANCE_SQ
                && !state.hasAttackTarget();
    }

    static boolean shouldRandomStroll(State state) {
        return state.carrierPresent()
                && state.carrierAlive()
                && !state.hasAttackTarget()
                && !shouldFollowCarrier(state)
                && state.randomStrollRollHit();
    }

    record State(
            boolean carrierPresent,
            boolean carrierAlive,
            double carrierDistanceSq,
            boolean hasAttackTarget,
            boolean hasAliveAttackTarget,
            boolean randomStrollRollHit,
            double targetDistanceSq,
            double attackRangeSq,
            int attackDelay
    ) {
    }
}
