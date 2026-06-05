package org.trp.shincolle.entity.base

import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.Shincolle.Companion.debugLog

class ShipTaskRuntime internal constructor(private val ship: EntityShipBase) {
    private val movement: ShipMovementCoordinator
    private val recovery: ShipMovementRecoveryState
    private var lastTaskTarget: Vec3? = null
    private var lastTaskId: Int = NO_TASK

    init {
        this.movement = ShipMovementCoordinator(ship, ShipMovementCoordinator.Companion.PRIORITY_TASK)
        this.recovery = ShipMovementRecoveryState()
    }

    fun beginTaskTick(taskId: Int) {
        if (this.lastTaskId != taskId) {
            this.movement.reset()
            this.recovery.reset(this.ship.position())
            this.lastTaskTarget = null
            this.lastTaskId = taskId
        }
    }

    fun clearTask() {
        if (this.lastTaskId != NO_TASK) {
            this.movement.stop()
            this.recovery.clear()
            this.lastTaskTarget = null
            this.lastTaskId = NO_TASK
        }
    }

    fun moveTo(target: Vec3?, speed: Double): Boolean {
        return this.movement.moveTo(target, speed)
    }

    fun moveToTaskPoint(target: Vec3?, speed: Double): Boolean {
        if (target == null) {
            return false
        }
        if (this.lastTaskTarget == null || this.lastTaskTarget!!.distanceToSqr(target) > 0.01) {
            this.lastTaskTarget = target
            this.recovery.reset(this.ship.position())
        }

        this.recovery.trackProgress(this.ship.position())
        val distanceSqr = this.ship.distanceToSqr(target)
        val force = this.recovery.isStuckLongerThan(TASK_STUCK_TICK_LIMIT)
        if (tryTeleportRecovery(target, distanceSqr, force)) {
            return true
        }

        val moved = this.movement.moveTo(target, speed)
        if (moved) {
            this.recovery.clearMoveFailures()
            return true
        }

        val failCount = this.recovery.recordMoveFailure()
        if (this.recovery.shouldLogMoveFailure(this.ship.tickCount, TASK_MOVE_FAIL_LOG_INTERVAL)) {
            debugLog(
                "[SCMoveDiag] TaskMove moveFail ship={} task={} target={} failCount={} distanceSqr={}",
                this.ship.getUUID(), this.lastTaskId, target, failCount, distanceSqr
            )
        }
        if (failCount > TASK_MOVE_FAIL_LIMIT && tryTeleportRecovery(target, distanceSqr, true)) {
            return true
        }
        return false
    }

    fun moveTo(target: Entity, speed: Double): Boolean {
        return this.movement.moveTo(target, speed)
    }

    private fun tryTeleportRecovery(target: Vec3?, distanceSqr: Double, force: Boolean): Boolean {
        if (!this.recovery.shouldTryTeleportThrottled(
                force, distanceSqr,
                TASK_TELEPORT_DISTANCE_SQ, TASK_TELEPORT_COOLDOWN_TICKS
            )
        ) {
            return false
        }
        if (!this.movement.teleportNearPoint(target, 0.75)) {
            return false
        }

        debugLog(
            "[SCMoveDiag] TaskMove teleportRecovery ship={} task={} target={} force={} distanceSqr={} stuckTicks={}",
            this.ship.getUUID(), this.lastTaskId, target, force, distanceSqr, this.recovery.stuckTicks()
        )
        this.recovery.reset(this.ship.position())
        return true
    }

    companion object {
        private const val NO_TASK = 0
        private const val TASK_MOVE_FAIL_LIMIT = 40
        private const val TASK_MOVE_FAIL_LOG_INTERVAL = 20
        private const val TASK_STUCK_TICK_LIMIT = 120
        private const val TASK_TELEPORT_COOLDOWN_TICKS = 100
        private const val TASK_TELEPORT_DISTANCE_SQ = 256.0
    }
}
