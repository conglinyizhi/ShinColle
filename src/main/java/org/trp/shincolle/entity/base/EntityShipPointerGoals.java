package org.trp.shincolle.entity.base;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.trp.shincolle.Shincolle;

import java.util.EnumSet;

class EntityShipPointerMoveGoal extends Goal {
    private static final double TARGET_REACH_SQR = 1.0D;
    private static final int POINTER_MOVE_FAIL_LIMIT = 40;
    private static final int POINTER_MOVE_STUCK_TICK_LIMIT = 120;

    private final EntityShipBase ship;
    private final ShipMovementCoordinator movement;
    private final double speed;
    private int nextPathTick;
    private int moveFailCount;
    private int stuckTicks;
    private Vec3 lastProgressPos;

    EntityShipPointerMoveGoal(EntityShipBase ship, double speed) {
        this.ship = ship;
        this.movement = new ShipMovementCoordinator(ship);
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
        this.moveFailCount = 0;
        this.stuckTicks = 0;
        this.lastProgressPos = ship.position();
        this.movement.reset();
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
            this.moveFailCount = 0;
            this.stuckTicks = 0;
            this.lastProgressPos = ship.position();
        }

        ship.resetInteractionEmotionState();
        trackProgress();
        if (this.stuckTicks > POINTER_MOVE_STUCK_TICK_LIMIT) {
            Shincolle.debugLog("PointerGoal stuckClear ship={} target={} stuckTicks={}",
                    ship.getUUID(), ship.getPointerTarget(), this.stuckTicks);
            ship.clearPointerTarget();
            this.movement.stop();
            return;
        }

        if (this.nextPathTick-- <= 0) {
            this.nextPathTick = 10;
            moveToTarget();
        }
    }

    @Override
    public void stop() {
        this.movement.stop();
    }

    private void moveToTarget() {
        Vec3 target = ship.getPointerTarget();
        if (target != null) {
            if (!this.movement.moveTo(target, this.speed)) {
                this.moveFailCount++;
                Shincolle.debugLog("PointerGoal moveFail ship={} target={} failCount={}",
                        ship.getUUID(), target, this.moveFailCount);
                if (this.moveFailCount > POINTER_MOVE_FAIL_LIMIT) {
                    Shincolle.debugLog("PointerGoal failClear ship={} target={} failCount={}",
                            ship.getUUID(), target, this.moveFailCount);
                    ship.clearPointerTarget();
                    this.movement.stop();
                }
            } else {
                this.moveFailCount = 0;
            }
        }
    }

    private void trackProgress() {
        Vec3 currentPos = ship.position();
        if (this.lastProgressPos == null) {
            this.lastProgressPos = currentPos;
            this.stuckTicks = 0;
            return;
        }

        if (currentPos.distanceToSqr(this.lastProgressPos) < 0.04D) {
            this.stuckTicks++;
        } else {
            this.stuckTicks = 0;
            this.lastProgressPos = currentPos;
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
