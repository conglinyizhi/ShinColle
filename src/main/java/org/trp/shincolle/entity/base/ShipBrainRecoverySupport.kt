package org.trp.shincolle.entity.base

import net.minecraft.world.entity.ai.behavior.PositionTracker
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.memory.WalkTarget
import org.trp.shincolle.entity.base.ShipBrainMemory.RecoveryStateMemory

internal object ShipBrainRecoverySupport {
    fun clearWalkAndLookMemory(ship: EntityShipBase) {
        val brain = ship.getBrain()
        brain.eraseMemory<WalkTarget?>(MemoryModuleType.WALK_TARGET)
        brain.eraseMemory<PositionTracker?>(MemoryModuleType.LOOK_TARGET)
        brain.eraseMemory<Long?>(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)
    }

    fun clearMovementRuntime(
        ship: EntityShipBase,
        recovery: ShipMovementRecoveryState,
        recoveryMemoryType: MemoryModuleType<RecoveryStateMemory?>,
        movement: ShipMovementCoordinator
    ) {
        recovery.clear()
        ship.getBrain().eraseMemory<RecoveryStateMemory?>(recoveryMemoryType)
        clearWalkAndLookMemory(ship)
        movement.stop()
    }

    fun resetMovementRuntime(
        ship: EntityShipBase,
        recovery: ShipMovementRecoveryState,
        recoveryMemoryType: MemoryModuleType<RecoveryStateMemory?>,
        movement: ShipMovementCoordinator,
        stuckLimit: Int
    ) {
        recovery.reset(ship.position())
        ship.getBrain().setMemory<RecoveryStateMemory?>(
            recoveryMemoryType,
            ShipBrainMemory.recoveryState(recovery, recovery.isStuckLongerThan(stuckLimit))
        )
        movement.stop()
    }

    fun shouldTryTeleportRecovery(
        recovery: ShipMovementRecoveryState,
        recoveryState: ShipRecoveryDecisionResolver.State,
        cooldownTicks: Int
    ): Boolean {
        return ShipRecoveryDecisionResolver.shouldAttemptTeleport(recoveryState)
                && recovery.shouldTryTeleportThrottled(
            recoveryState.force, recoveryState.distanceSqr,
            recoveryState.teleportDistanceSqr, cooldownTicks
        )
    }

    fun recordMoveFailureAndSync(
        ship: EntityShipBase,
        recovery: ShipMovementRecoveryState,
        recoveryMemoryType: MemoryModuleType<RecoveryStateMemory?>,
        stuckLimit: Int
    ): Int {
        val failCount = recovery.recordMoveFailure()
        ship.getBrain().setMemory<RecoveryStateMemory?>(
            recoveryMemoryType,
            ShipBrainMemory.recoveryState(recovery, recovery.isStuckLongerThan(stuckLimit))
        )
        return failCount
    }

    fun clearMoveFailuresAndSync(
        ship: EntityShipBase,
        recovery: ShipMovementRecoveryState,
        recoveryMemoryType: MemoryModuleType<RecoveryStateMemory?>,
        stuckLimit: Int
    ) {
        recovery.clearMoveFailures()
        ship.getBrain().setMemory<RecoveryStateMemory?>(
            recoveryMemoryType,
            ShipBrainMemory.recoveryState(recovery, recovery.isStuckLongerThan(stuckLimit))
        )
    }

    fun syncRecoveryMemory(
        ship: EntityShipBase,
        memoryType: MemoryModuleType<RecoveryStateMemory?>,
        recovery: ShipMovementRecoveryState, stuckLimit: Int
    ) {
        ship.getBrain().setMemory<RecoveryStateMemory?>(
            memoryType,
            ShipBrainMemory.recoveryState(recovery, recovery.isStuckLongerThan(stuckLimit))
        )
    }
}
