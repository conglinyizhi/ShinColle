package org.trp.shincolle.entity.base

internal object SummonBrainDecisionResolver {
    fun shouldAttack(state: State): Boolean {
        return state.hasAliveAttackTarget
    }

    fun shouldChaseAttackTarget(state: State): Boolean {
        return shouldAttack(state) && state.targetDistanceSq > state.attackRangeSq
    }

    fun shouldPerformAttack(state: State): Boolean {
        return shouldAttack(state)
                && state.targetDistanceSq <= state.attackRangeSq && state.attackDelay <= 0
    }

    fun shouldFollowCarrier(state: State): Boolean {
        return state.carrierPresent
                && state.carrierAlive
                && state.carrierDistanceSq > SummonAiNumbers.FOLLOW_CARRIER_DISTANCE_SQ && !state.hasAttackTarget
    }

    fun shouldRandomStroll(state: State): Boolean {
        return state.carrierPresent
                && state.carrierAlive
                && !state.hasAttackTarget && !shouldFollowCarrier(state) && state.randomStrollRollHit
    }

    @JvmRecord
    internal data class State(
        val carrierPresent: Boolean,
        val carrierAlive: Boolean,
        val carrierDistanceSq: Double,
        val hasAttackTarget: Boolean,
        val hasAliveAttackTarget: Boolean,
        val randomStrollRollHit: Boolean,
        val targetDistanceSq: Double,
        val attackRangeSq: Double,
        val attackDelay: Int
    )
}
