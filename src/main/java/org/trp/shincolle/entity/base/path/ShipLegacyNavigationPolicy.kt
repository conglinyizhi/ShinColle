package org.trp.shincolle.entity.base.path

import kotlin.math.max

internal object ShipLegacyNavigationPolicy {
    const val NAVIGATION_DEBUG_LOG_INTERVAL: Int = 200
    const val NAVIGATION_SET_PATH_LOG_INTERVAL: Int = 100
    const val SAME_NAVIGATION_TARGET_SQR: Double = 9.0

    fun isSameNavigationTarget(previousTarget: Target?, nextTarget: Target?): Boolean {
        return previousTarget != null && nextTarget != null && previousTarget.distanceToSqr(nextTarget) <= SAME_NAVIGATION_TARGET_SQR
    }

    fun shouldResetStuckProgress(hadActivePath: Boolean, sameNavigationTarget: Boolean): Boolean {
        return !hadActivePath || !sameNavigationTarget
    }

    fun shouldLogSetPath(
        loggedPathFailure: Boolean, failure: Boolean, loggedTarget: Target?,
        logTarget: Target?, totalTicks: Int, lastSetPathLogTick: Int
    ): Boolean {
        if (loggedPathFailure != failure) {
            return true
        }
        if (!isSameNavigationTarget(loggedTarget, logTarget)) {
            return true
        }
        return totalTicks - lastSetPathLogTick >= NAVIGATION_SET_PATH_LOG_INTERVAL
    }

    fun shouldLogNavigationEvent(
        lastLogTarget: Target?,
        currentTarget: Target?,
        totalTicks: Int,
        lastLogTick: Int
    ): Boolean {
        return !isSameNavigationTarget(
            lastLogTarget,
            currentTarget
        ) || lastLogTick == Int.MIN_VALUE || totalTicks - lastLogTick >= NAVIGATION_DEBUG_LOG_INTERVAL
    }

    fun calculateTimeoutLimit(distance: Double, speed: Double): Double {
        return distance / max(0.01, speed) * 60.0
    }

    fun shouldRetryTimedOutPath(timeoutTimer: Long, timeoutLimit: Double): Boolean {
        return timeoutLimit > 0.0 && timeoutTimer > timeoutLimit * 2.0
    }

    @JvmRecord
    internal data class Target(val x: Int, val y: Int, val z: Int) {
        private fun distanceToSqr(other: Target): Double {
            val dx = (this.x - other.x).toDouble()
            val dy = (this.y - other.y).toDouble()
            val dz = (this.z - other.z).toDouble()
            return dx * dx + dy * dy + dz * dz
        }
    }
}
