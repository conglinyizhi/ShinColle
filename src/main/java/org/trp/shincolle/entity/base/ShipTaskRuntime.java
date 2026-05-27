package org.trp.shincolle.entity.base;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.trp.shincolle.Shincolle;

public final class ShipTaskRuntime {
    private static final int NO_TASK = 0;
    private static final int TASK_MOVE_FAIL_LIMIT = 40;
    private static final int TASK_MOVE_FAIL_LOG_INTERVAL = 20;
    private static final int TASK_STUCK_TICK_LIMIT = 120;
    private static final int TASK_TELEPORT_COOLDOWN_TICKS = 100;
    private static final double TASK_TELEPORT_DISTANCE_SQ = 256.0D;

    private final EntityShipBase ship;
    private final ShipMovementCoordinator movement;
    private final ShipMovementRecoveryState recovery;
    private Vec3 lastTaskTarget;
    private int lastTaskId = NO_TASK;

    ShipTaskRuntime(EntityShipBase ship) {
        this.ship = ship;
        this.movement = new ShipMovementCoordinator(ship, ShipMovementCoordinator.PRIORITY_TASK);
        this.recovery = new ShipMovementRecoveryState();
    }

    public void beginTaskTick(int taskId) {
        if (this.lastTaskId != taskId) {
            this.movement.reset();
            this.recovery.reset(this.ship.position());
            this.lastTaskTarget = null;
            this.lastTaskId = taskId;
        }
    }

    public void clearTask() {
        if (this.lastTaskId != NO_TASK) {
            this.movement.stop();
            this.recovery.clear();
            this.lastTaskTarget = null;
            this.lastTaskId = NO_TASK;
        }
    }

    public boolean moveTo(Vec3 target, double speed) {
        return this.movement.moveTo(target, speed);
    }

    public boolean moveToTaskPoint(Vec3 target, double speed) {
        if (target == null) {
            return false;
        }
        if (this.lastTaskTarget == null || this.lastTaskTarget.distanceToSqr(target) > 0.01D) {
            this.lastTaskTarget = target;
            this.recovery.reset(this.ship.position());
        }

        this.recovery.trackProgress(this.ship.position());
        double distanceSqr = this.ship.distanceToSqr(target);
        boolean force = this.recovery.isStuckLongerThan(TASK_STUCK_TICK_LIMIT);
        if (tryTeleportRecovery(target, distanceSqr, force)) {
            return true;
        }

        boolean moved = this.movement.moveTo(target, speed);
        if (moved) {
            this.recovery.clearMoveFailures();
            return true;
        }

        int failCount = this.recovery.recordMoveFailure();
        if (this.recovery.shouldLogMoveFailure(this.ship.tickCount, TASK_MOVE_FAIL_LOG_INTERVAL)) {
            Shincolle.debugLog("TaskMove moveFail ship={} task={} target={} failCount={} distanceSqr={}",
                    this.ship.getUUID(), this.lastTaskId, target, failCount, distanceSqr);
        }
        if (failCount > TASK_MOVE_FAIL_LIMIT && tryTeleportRecovery(target, distanceSqr, true)) {
            return true;
        }
        return false;
    }

    public boolean moveTo(Entity target, double speed) {
        return this.movement.moveTo(target, speed);
    }

    private boolean tryTeleportRecovery(Vec3 target, double distanceSqr, boolean force) {
        if (!this.recovery.shouldTryTeleportThrottled(force, distanceSqr,
                TASK_TELEPORT_DISTANCE_SQ, TASK_TELEPORT_COOLDOWN_TICKS)) {
            return false;
        }
        if (!this.movement.teleportNearPoint(target, 0.75D)) {
            return false;
        }

        Shincolle.debugLog("TaskMove teleportRecovery ship={} task={} target={} force={} distanceSqr={} stuckTicks={}",
                this.ship.getUUID(), this.lastTaskId, target, force, distanceSqr, this.recovery.stuckTicks());
        this.recovery.reset(this.ship.position());
        return true;
    }
}
