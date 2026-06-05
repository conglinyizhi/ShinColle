package org.trp.shincolle.entity.base

internal object MountBrainDecisionResolver {
    fun shouldFollowHost(state: State): Boolean {
        if (!state.hostPresent || !state.hostAlive || state.hostOrderedToSit || state.mountPassenger) {
            return false
        }
        if (state.hostHasPointerTarget || state.hasGuardTarget) {
            return true
        }
        if (!state.ownerPresent) {
            return false
        }

        val maxDistSq = (state.followMaxConfig * state.followMaxConfig
                + state.mountWidth * MountAiNumbers.FOLLOW_WIDTH_PADDING)
        return state.ownerDistanceSq > maxDistSq
    }

    fun shouldRangeAttack(state: State): Boolean {
        return state.hostPresent
                && state.hostAlive
                && !state.hostOrderedToSit && !state.mountPassenger && state.hasAliveAttackTarget
    }

    fun isAttackAimed(state: State): Boolean {
        return state.aimTick >= state.aimRequiredTicks
    }

    fun isTargetWithinAttackRange(state: State): Boolean {
        return state.targetDistanceSq <= state.attackRangeSq
    }

    fun shouldFireLight(state: State): Boolean {
        return shouldRangeAttack(state)
                && isAttackAimed(state)
                && isTargetWithinAttackRange(state)
                && state.hostLightAttackEnabled
                && state.hostLightAmmo > 0 && state.lightDelay <= 0
    }

    fun shouldFireHeavy(state: State): Boolean {
        return shouldRangeAttack(state)
                && isAttackAimed(state)
                && isTargetWithinAttackRange(state)
                && state.hostHeavyAttackEnabled
                && state.hostHeavyAmmo > 0 && state.heavyDelay <= 0
    }

    fun shouldRandomStroll(state: State): Boolean {
        return state.hostPresent
                && state.hostAlive
                && !state.hostOrderedToSit && !state.mountPassenger && !state.hasAttackTarget && !shouldFollowHost(state) && state.randomStrollRollHit
    }

    @JvmRecord
    internal data class State(
        val hostPresent: Boolean,
        val hostAlive: Boolean,
        val hostOrderedToSit: Boolean,
        val mountPassenger: Boolean,
        val hostHasPointerTarget: Boolean,
        val hasGuardTarget: Boolean,
        val ownerPresent: Boolean,
        val ownerDistanceSq: Double,
        val mountWidth: Double,
        val followMaxConfig: Int,
        val hasAttackTarget: Boolean,
        val hasAliveAttackTarget: Boolean,
        val randomStrollRollHit: Boolean,
        val aimTick: Int,
        val aimRequiredTicks: Int,
        val targetDistanceSq: Double,
        val attackRangeSq: Double,
        val hostLightAttackEnabled: Boolean,
        val hostLightAmmo: Int,
        val lightDelay: Int,
        val hostHeavyAttackEnabled: Boolean,
        val hostHeavyAmmo: Int,
        val heavyDelay: Int
    )
}
