package org.trp.shincolle.entity;

final class AircraftBrainDecisionResolver {
    private AircraftBrainDecisionResolver() {
    }

    static boolean canAttackMissionTarget(State state) {
        if (!state.targetPresent() || !state.targetAlive()) {
            return false;
        }
        return state.lightAircraft() ? state.hasLightAmmo() : state.hasHeavyAmmo();
    }

    static boolean shouldStartAttack(State state) {
        return canAttackMissionTarget(state)
                && state.missionTick() > AircraftAiNumbers.ATTACK_ACTIVATION_TICKS;
    }

    static double attackRangeSqr(State state) {
        float attackRange = state.lightAircraft()
                ? AircraftAiNumbers.ATTACK_RANGE_LIGHT
                : AircraftAiNumbers.ATTACK_RANGE_HEAVY;
        return attackRange * attackRange;
    }

    static double attackMoveSpeed(State state) {
        if (state.attackDelay() > 0) {
            return AircraftAiNumbers.ATTACK_SPEED_SLOW;
        }
        return state.distanceSqr() > attackRangeSqr(state)
                ? AircraftAiNumbers.ATTACK_SPEED_FAST
                : AircraftAiNumbers.ATTACK_SPEED_SLOW;
    }

    static boolean shouldFire(State state) {
        return canAttackMissionTarget(state)
                && state.attackDelay() <= 0
                && state.hasLineOfSight()
                && state.distanceSqr() < attackRangeSqr(state);
    }

    record State(
            boolean targetPresent,
            boolean targetAlive,
            boolean lightAircraft,
            boolean hasLightAmmo,
            boolean hasHeavyAmmo,
            int missionTick,
            int attackDelay,
            boolean hasLineOfSight,
            double distanceSqr
    ) {
    }
}
