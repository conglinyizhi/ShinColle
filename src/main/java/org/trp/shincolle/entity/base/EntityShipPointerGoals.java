package org.trp.shincolle.entity.base;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

class EntityShipPointerMoveGoal extends Goal {
    private static final double TARGET_REACH_SQR = 1.0D;

    private final EntityShipBase ship;
    private final double speed;
    private int nextPathTick;

    EntityShipPointerMoveGoal(EntityShipBase ship, double speed) {
        this.ship = ship;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return ship.hasPointerTarget()
                && !ship.isOrderedToSit()
                && !ship.isInSittingPose()
                && !ship.isPassenger()
                && !ship.isInDeadPose();
    }

    @Override
    public boolean canContinueToUse() {
        if (!ship.hasPointerTarget() || ship.isOrderedToSit() || ship.isInSittingPose() || ship.isPassenger() || ship.isInDeadPose()) {
            return false;
        }
        Vec3 target = ship.getPointerTarget();
        return target != null && ship.distanceToSqr(target) > TARGET_REACH_SQR;
    }

    @Override
    public void start() {
        this.nextPathTick = 0;
        moveToTarget();
    }

    private Vec3 lastRawTarget;

    @Override
    public void tick() {
        if (!ship.hasPointerTarget()) {
            return;
        }

        Vec3 rawTarget = ship.getRawPointerTarget();
        if (rawTarget == null) {
            return;
        }

        if (lastRawTarget == null || rawTarget.distanceToSqr(lastRawTarget) > 0.01D) {
            this.nextPathTick = 0;
            this.lastRawTarget = rawTarget;
        }

        ship.resetInteractionEmotionState();

        if (this.nextPathTick-- <= 0) {
            this.nextPathTick = 10;
            moveToTarget();
        }
    }

    @Override
    public void stop() {
        ship.getNavigation().stop();
    }

    private void moveToTarget() {
        Vec3 target = ship.getPointerTarget();
        if (target != null) {
            ship.getNavigation().moveTo(target.x, target.y, target.z, this.speed);
        }
    }
}

class EntityShipPointerLookTargetGoal extends Goal {
    private final EntityShipBase ship;

    EntityShipPointerLookTargetGoal(EntityShipBase ship) {
        this.ship = ship;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return ship.hasPointerTargetEntity()
                && ship.getPointerTargetEntity() != null
                && !ship.isInDeadPose();
    }

    @Override
    public boolean canContinueToUse() {
        return ship.hasPointerTargetEntity()
                && ship.getPointerTargetEntity() != null
                && !ship.isInDeadPose();
    }

    @Override
    public void tick() {
        Entity target = ship.getPointerTargetEntity();
        if (target != null) {
            ship.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }
    }
}
