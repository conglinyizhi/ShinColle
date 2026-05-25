package org.trp.shincolle.entity.base;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class ShipTaskRuntime {
    private static final int NO_TASK = 0;

    private final ShipMovementCoordinator movement;
    private int lastTaskId = NO_TASK;

    ShipTaskRuntime(EntityShipBase ship) {
        this.movement = new ShipMovementCoordinator(ship);
    }

    public void beginTaskTick(int taskId) {
        if (this.lastTaskId != taskId) {
            this.movement.reset();
            this.lastTaskId = taskId;
        }
    }

    public void clearTask() {
        if (this.lastTaskId != NO_TASK) {
            this.movement.reset();
            this.lastTaskId = NO_TASK;
        }
    }

    public boolean moveTo(Vec3 target, double speed) {
        return this.movement.moveTo(target, speed);
    }

    public boolean moveTo(Entity target, double speed) {
        return this.movement.moveTo(target, speed);
    }
}
