package org.trp.shincolle.entity.base

import net.minecraft.world.phys.Vec3

class ShipMovementRecoveryState {
    private var moveFailCount = 0
    private var lastMoveFailLogTick = Int.MIN_VALUE
    private var stuckTicks = 0
    private var teleportCooldown = 0
    private var forcedTeleportCooldown = 0
    private var lastProgressPos: Vec3? = null

    fun reset(currentPos: Vec3?) {
        this.moveFailCount = 0
        this.lastMoveFailLogTick = Int.MIN_VALUE
        this.stuckTicks = 0
        this.teleportCooldown = 0
        this.forcedTeleportCooldown = 0
        this.lastProgressPos = currentPos
    }

    fun clear() {
        this.moveFailCount = 0
        this.lastMoveFailLogTick = Int.MIN_VALUE
        this.stuckTicks = 0
        this.teleportCooldown = 0
        this.forcedTeleportCooldown = 0
        this.lastProgressPos = null
    }

    fun trackProgress(currentPos: Vec3) {
        if (this.lastProgressPos == null) {
            this.lastProgressPos = currentPos
            this.stuckTicks = 0
            return
        }

        if (currentPos.distanceToSqr(this.lastProgressPos) < PROGRESS_DISTANCE_SQR) {
            this.stuckTicks++
        } else {
            this.stuckTicks = 0
            this.forcedTeleportCooldown = 0
            this.lastProgressPos = currentPos
        }
    }

    fun recordMoveFailure(): Int {
        return ++this.moveFailCount
    }

    fun clearMoveFailures() {
        this.moveFailCount = 0
        this.lastMoveFailLogTick = Int.MIN_VALUE
    }

    fun clearTeleportCooldown() {
        this.teleportCooldown = 0
    }

    fun moveFailCount(): Int {
        return this.moveFailCount
    }

    fun shouldLogMoveFailure(currentTick: Int, intervalTicks: Int): Boolean {
        if (this.moveFailCount <= 1 || currentTick - this.lastMoveFailLogTick >= intervalTicks) {
            this.lastMoveFailLogTick = currentTick
            return true
        }
        return false
    }

    fun stuckTicks(): Int {
        return this.stuckTicks
    }

    fun isStuckLongerThan(stuckTickLimit: Int): Boolean {
        return this.stuckTicks > stuckTickLimit
    }

    fun shouldTryTeleport(
        force: Boolean,
        distanceSqr: Double,
        teleportDistanceSqr: Double,
        cooldownTicks: Int
    ): Boolean {
        if (!force && distanceSqr <= teleportDistanceSqr) {
            return false
        }
        if (!force && this.teleportCooldown++ < cooldownTicks) {
            return false
        }

        this.teleportCooldown = 0
        return true
    }

    fun shouldTryTeleportThrottled(
        force: Boolean,
        distanceSqr: Double,
        teleportDistanceSqr: Double,
        cooldownTicks: Int
    ): Boolean {
        if (!force) {
            return shouldTryTeleport(false, distanceSqr, teleportDistanceSqr, cooldownTicks)
        }
        if (this.forcedTeleportCooldown > 0) {
            this.forcedTeleportCooldown--
            return false
        }

        this.forcedTeleportCooldown = cooldownTicks
        this.teleportCooldown = 0
        return true
    }

    companion object {
        private const val PROGRESS_DISTANCE_SQR = 0.04
    }
}
