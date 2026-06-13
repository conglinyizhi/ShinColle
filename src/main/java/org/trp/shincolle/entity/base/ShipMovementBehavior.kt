package org.trp.shincolle.entity.base

import com.google.common.collect.ImmutableMap
import net.minecraft.world.entity.ai.behavior.Behavior
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.memory.MemoryStatus
import org.trp.shincolle.Shincolle.Companion.debugLog
import org.trp.shincolle.entity.base.ShipBrainMemory.RecoveryStateMemory

@Suppress("LongParameterList")
internal abstract class ShipMovementBehavior<Target>(
    private val recoveryMemoryType: MemoryModuleType<RecoveryStateMemory?>,
    private val stuckTickLimit: Int,
    private val moveFailLimit: Int,
    private val moveFailLogInterval: Int,
    private val teleportDistanceSq: Double,
    private val teleportCooldownTicks: Int,
    protected val movementSpeed: Double,
    private val debugLabel: String
) : Behavior<EntityShipBase>(
    ImmutableMap.of<MemoryModuleType<*>, MemoryStatus>()
) {
    protected val recovery = ShipMovementRecoveryState()
    protected var nextPathTick = 0

    protected abstract fun coordinator(ship: EntityShipBase): ShipMovementCoordinator
    protected abstract fun resolveTarget(ship: EntityShipBase): Target?
    protected abstract fun computeTargetKey(target: Target): Any?
    protected abstract fun distanceSqTo(ship: EntityShipBase, target: Target): Double
    protected abstract fun moveTo(ship: EntityShipBase, target: Target): Boolean
    protected abstract fun tryTeleport(ship: EntityShipBase, target: Target): Boolean
    protected abstract fun setWalkAndLookMemory(ship: EntityShipBase, target: Target, closeEnoughDist: Int)

    protected open fun teleportDistanceSqTo(ship: EntityShipBase, target: Target): Double {
        return distanceSqTo(ship, target)
    }

    protected open fun onTargetChanged(ship: EntityShipBase) {
        coordinator(ship).reset()
    }

    protected fun resetForTargetChange(ship: EntityShipBase) {
        this.nextPathTick = 0
        this.recovery.reset(ship.position())
        syncRecoveryMemory(ship)
        onTargetChanged(ship)
    }

    protected fun clearMoveState(ship: EntityShipBase) {
        this.nextPathTick = 0
        ShipBrainRecoverySupport.clearMovementRuntime(
            ship, this.recovery, this.recoveryMemoryType, coordinator(ship)
        )
    }

    protected fun syncRecoveryMemory(ship: EntityShipBase) {
        ShipBrainRecoverySupport.syncRecoveryMemory(
            ship, this.recoveryMemoryType, this.recovery, this.stuckTickLimit
        )
    }

    protected fun trackProgress(ship: EntityShipBase) {
        this.recovery.trackProgress(ship.position())
    }

    protected fun tryTeleportRecovery(ship: EntityShipBase, target: Target, force: Boolean): Boolean {
        val state = ShipRecoveryDecisionResolver.State(
            force, teleportDistanceSqTo(ship, target), this.teleportDistanceSq
        )
        val teleported = ShipBrainRecoverySupport.shouldTryTeleportRecovery(
            this.recovery, state, this.teleportCooldownTicks
        ) && tryTeleport(ship, target)
        if (teleported) {
            debugLog(
                "[SCMoveDiag] {} teleportRecovery ship={} target={} force={} distSq={}",
                this.debugLabel, ship.uuid, target, force, state.distanceSqr
            )
            this.nextPathTick = 0
            this.recovery.reset(ship.position())
            syncRecoveryMemory(ship)
        }
        return teleported
    }

    protected fun handleMoveFailure(
        ship: EntityShipBase,
        target: Target,
        onClear: () -> Unit
    ) {
        val failCount = ShipBrainRecoverySupport.recordMoveFailureAndSync(
            ship, this.recovery, this.recoveryMemoryType, this.stuckTickLimit
        )
        if (this.recovery.shouldLogMoveFailure(ship.tickCount, this.moveFailLogInterval)) {
            debugLog(
                "[SCMoveDiag] {} moveFail ship={} target={} failCount={}",
                this.debugLabel, ship.uuid, target, failCount
            )
        }
        if (failCount > this.moveFailLimit) {
            if (tryTeleportRecovery(ship, target, true)) {
                return
            }
            debugLog(
                "[SCMoveDiag] {} failClear ship={} target={} failCount={}",
                this.debugLabel, ship.uuid, target, this.recovery.moveFailCount()
            )
            onClear()
            clearMoveState(ship)
        } else {
            this.nextPathTick = 2
        }
    }
}
