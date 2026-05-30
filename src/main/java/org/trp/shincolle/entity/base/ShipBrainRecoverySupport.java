package org.trp.shincolle.entity.base;

import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

final class ShipBrainRecoverySupport {
    private ShipBrainRecoverySupport() {
    }

    static void clearWalkAndLookMemory(EntityShipBase ship) {
        Brain<?> brain = ship.getBrain();
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
        brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    static void clearMovementRuntime(EntityShipBase ship,
                                     ShipMovementRecoveryState recovery,
                                     MemoryModuleType<ShipBrainMemory.RecoveryStateMemory> recoveryMemoryType,
                                     ShipMovementCoordinator movement) {
        recovery.clear();
        ship.getBrain().eraseMemory(recoveryMemoryType);
        clearWalkAndLookMemory(ship);
        movement.stop();
    }

    static void resetMovementRuntime(EntityShipBase ship,
                                     ShipMovementRecoveryState recovery,
                                     MemoryModuleType<ShipBrainMemory.RecoveryStateMemory> recoveryMemoryType,
                                     ShipMovementCoordinator movement,
                                     int stuckLimit) {
        recovery.reset(ship.position());
        ship.getBrain().setMemory(recoveryMemoryType,
                ShipBrainMemory.recoveryState(recovery, recovery.isStuckLongerThan(stuckLimit)));
        movement.stop();
    }

    static boolean shouldTryTeleportRecovery(ShipMovementRecoveryState recovery,
                                             ShipRecoveryDecisionResolver.State recoveryState,
                                             int cooldownTicks) {
        return ShipRecoveryDecisionResolver.shouldAttemptTeleport(recoveryState)
                && recovery.shouldTryTeleportThrottled(recoveryState.force(), recoveryState.distanceSqr(),
                recoveryState.teleportDistanceSqr(), cooldownTicks);
    }

    static int recordMoveFailureAndSync(EntityShipBase ship,
                                        ShipMovementRecoveryState recovery,
                                        MemoryModuleType<ShipBrainMemory.RecoveryStateMemory> recoveryMemoryType,
                                        int stuckLimit) {
        int failCount = recovery.recordMoveFailure();
        ship.getBrain().setMemory(recoveryMemoryType,
                ShipBrainMemory.recoveryState(recovery, recovery.isStuckLongerThan(stuckLimit)));
        return failCount;
    }

    static void clearMoveFailuresAndSync(EntityShipBase ship,
                                         ShipMovementRecoveryState recovery,
                                         MemoryModuleType<ShipBrainMemory.RecoveryStateMemory> recoveryMemoryType,
                                         int stuckLimit) {
        recovery.clearMoveFailures();
        ship.getBrain().setMemory(recoveryMemoryType,
                ShipBrainMemory.recoveryState(recovery, recovery.isStuckLongerThan(stuckLimit)));
    }

    static void syncRecoveryMemory(EntityShipBase ship,
                                     MemoryModuleType<ShipBrainMemory.RecoveryStateMemory> memoryType,
                                     ShipMovementRecoveryState recovery, int stuckLimit) {
        ship.getBrain().setMemory(memoryType,
                ShipBrainMemory.recoveryState(recovery, recovery.isStuckLongerThan(stuckLimit)));
    }
}
