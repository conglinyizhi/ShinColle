package org.trp.shincolle.entity

internal object AircraftBrainDecisionResolver {
    fun canAttackMissionTarget(state: State): Boolean {
        if (!state.targetPresent || !state.targetAlive) {
            return false
        }
        return if (state.lightAircraft) state.hasLightAmmo else state.hasHeavyAmmo
    }

    fun shouldStartAttack(state: State): Boolean {
        return canAttackMissionTarget(state)
                && state.missionTick > AircraftAiNumbers.ATTACK_ACTIVATION_TICKS
    }

    fun attackRangeSqr(state: State): Double {
        val attackRange = if (state.lightAircraft)
            AircraftAiNumbers.ATTACK_RANGE_LIGHT
        else
            AircraftAiNumbers.ATTACK_RANGE_HEAVY
        return (attackRange * attackRange).toDouble()
    }

    fun attackMoveSpeed(state: State): Double {
        if (state.attackDelay > 0) {
            return AircraftAiNumbers.ATTACK_SPEED_SLOW
        }
        return if (state.distanceSqr > attackRangeSqr(state))
            AircraftAiNumbers.ATTACK_SPEED_FAST
        else
            AircraftAiNumbers.ATTACK_SPEED_SLOW
    }

    fun shouldFire(state: State): Boolean {
        return canAttackMissionTarget(state)
                && state.attackDelay <= 0 && state.hasLineOfSight
                && state.distanceSqr < attackRangeSqr(state)
    }

    @JvmRecord
    internal data class State(
        val targetPresent: Boolean,
        val targetAlive: Boolean,
        val lightAircraft: Boolean,
        val hasLightAmmo: Boolean,
        val hasHeavyAmmo: Boolean,
        val missionTick: Int,
        val attackDelay: Int,
        val hasLineOfSight: Boolean,
        val distanceSqr: Double
    )
}
