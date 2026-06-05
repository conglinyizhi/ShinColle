package org.trp.shincolle.entity.base

import java.util.List
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

internal object ShipBrainActivityResolver {
    fun resolveActiveModes(state: State, following: Boolean): MutableList<Mode?> {
        if (state.hasPointerTarget && state.canMove) {
            return List.of<Mode?>(Mode.COMMAND, Mode.COMBAT, Mode.FOLLOW, Mode.IDLE)
        }
        if (state.hasAttackTarget) {
            return List.of<Mode?>(Mode.COMBAT, Mode.IDLE)
        }
        if (state.canMove && state.canGuard) {
            return List.of<Mode?>(Mode.GUARD, Mode.FOLLOW, Mode.IDLE)
        }
        if (shouldFollowOwner(state, following)) {
            return List.of<Mode?>(Mode.FOLLOW, Mode.IDLE)
        }
        return List.of<Mode?>(Mode.IDLE)
    }

    fun describeDesiredActivity(state: State): String {
        if (state.hasPointerTarget && state.canMove) {
            return Mode.COMMAND.name
        }
        if (state.hasAttackTarget) {
            return Mode.COMBAT.name
        }
        if (state.canMove && state.canGuard) {
            return Mode.GUARD.name
        }
        if (shouldFollowOwner(state, false)) {
            return Mode.FOLLOW.name
        }
        return Mode.IDLE.name
    }

    fun shouldFollowOwner(state: State, following: Boolean): Boolean {
        if (!state.shouldFollowOwner || !state.canMove || !state.ownerPresent || state.hasAttackTarget) {
            return false
        }
        if (state.ownerHasCombatRation) {
            return state.ownerDistanceSq > ShipAiNumbers.FOLLOW_RATION_DISTANCE_SQ
        }

        val minDist = resolveFollowMinDistance(state.followMinConfig)
        val maxDist = resolveFollowMaxDistance(state.followMaxConfig, minDist)
        if (following) {
            return state.ownerDistanceSq > minDist * minDist
        }
        return state.ownerDistanceSq > maxDist * maxDist
    }

    fun shouldContinueFollow(state: FollowState): Boolean {
        if (!state.shouldFollow || !state.canMove || !state.hasSameDimensionOwnerPosition) {
            return false
        }
        if (state.ownerHasCombatRation) {
            return state.ownerDistanceSq > ShipAiNumbers.FOLLOW_RATION_DISTANCE_SQ
        }

        val minDist = resolveFollowMinDistance(state.followMinConfig)
        return state.ownerDistanceSq > minDist * minDist
    }

    fun resolveFollowMinDistance(configured: Int): Float {
        if (configured <= 0) {
            return ShipAiNumbers.FOLLOW_OWNER_MIN_DIST
        }
        return clamp(
            configured,
            ShipAiNumbers.FOLLOW_MIN_DIST_CONFIG_MIN,
            ShipAiNumbers.FOLLOW_MIN_DIST_CONFIG_MAX
        ).toFloat()
    }

    fun resolveFollowMaxDistance(configured: Int, minDist: Float): Float {
        if (configured <= 0) {
            return max(ShipAiNumbers.FOLLOW_OWNER_MAX_DIST, minDist + ShipAiNumbers.FOLLOW_MAX_DIST_PADDING)
        }
        val minValue = max(ShipAiNumbers.FOLLOW_MAX_DIST_CONFIG_MIN, floor(minDist.toDouble()).toInt() + 1)
        return clamp(configured, minValue, ShipAiNumbers.FOLLOW_MAX_DIST_CONFIG_MAX).toFloat()
    }

    private fun clamp(value: Int, min: Int, max: Int): Int {
        return min(max(value, min), max)
    }

    @JvmRecord
    internal data class State(
        val canMove: Boolean,
        val hasPointerTarget: Boolean,
        val hasAttackTarget: Boolean,
        val canGuard: Boolean,
        val shouldFollowOwner: Boolean,
        val ownerPresent: Boolean,
        val ownerHasCombatRation: Boolean,
        val ownerDistanceSq: Double,
        val followMinConfig: Int,
        val followMaxConfig: Int
    )

    @JvmRecord
    internal data class FollowState(
        val canMove: Boolean,
        val shouldFollow: Boolean,
        val hasSameDimensionOwnerPosition: Boolean,
        val ownerHasCombatRation: Boolean,
        val ownerDistanceSq: Double,
        val followMinConfig: Int
    )

    internal enum class Mode {
        COMMAND,
        GUARD,
        FOLLOW,
        COMBAT,
        IDLE
    }
}
