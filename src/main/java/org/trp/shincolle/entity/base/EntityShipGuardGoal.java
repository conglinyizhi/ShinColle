package org.trp.shincolle.entity.base;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

final class EntityShipGuardGoal extends Goal {
    private final EntityShipBase ship;
    private final double speed;
    private int nextPathTick;

    EntityShipGuardGoal(EntityShipBase ship, double speed) {
        this.ship = ship;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (ship.isOrderedToSit() || ship.isInSittingPose() || ship.isInDeadPose() || ship.isPassenger()) {
            return false;
        }
        if (ship.getStateFlag(EntityShipBase.STATE_FLAG_DISABLE_GUARD_POS)) {
            return false;
        }
        if (ship.getGuardedPos(4) != 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        this.nextPathTick = 0;
    }

    @Override
    public void tick() {
        int gx = ship.getGuardedPos(0);
        int gy = ship.getGuardedPos(1);
        int gz = ship.getGuardedPos(2);
        
        int timer = ship.getStateTimer(18);
        boolean isSummoning = timer > 0;
        if (isSummoning) {
            ship.setStateTimer(18, timer - 1);
        }

        Vec3 target = new Vec3(gx + 0.5, gy, gz + 0.5);
        double distSq = ship.distanceToSqr(target.x, ship.getY(), target.z);
        
        if (distSq > 0.5D) {
            if (this.nextPathTick-- <= 0 || ship.getNavigation().isDone()) {
                this.nextPathTick = 10;
                ship.getNavigation().moveTo(target.x, target.y, target.z, speed);
            }
        } else {
            this.nextPathTick = 0;
            ship.getNavigation().stop();
        }

        if (!isSummoning) {
            ship.setGuardedPos(gx, gy, gz, ship.getGuardedPos(3), 0);
            ship.getNavigation().stop();
            return;
        }

        if (isSummoning || distSq < 16.0D) {
            lookAtOwnerOrNearestPlayer();
        } else {
            ship.getLookControl().setLookAt(target.x, target.y + ship.getEyeHeight(), target.z, 30.0F, 30.0F);
        }
    }

    private void lookAtOwnerOrNearestPlayer() {
        LivingEntity lookTarget = ship.getOwner();
        if (lookTarget == null || ship.distanceToSqr(lookTarget) > 1024.0D) {
            lookTarget = ship.level().getNearestPlayer(ship, 32.0D);
        }
        
        if (lookTarget != null) {
            ship.getLookControl().setLookAt(lookTarget.getX(), lookTarget.getEyeY(), lookTarget.getZ(), 60.0F, 60.0F);
        } else {
            float yaw = ship.getYRot();
            double rad = -yaw * 0.017453292F;
            double tx = ship.getX() + Math.sin(rad) * 5.0D;
            double ty = ship.getEyeY();
            double tz = ship.getZ() + Math.cos(rad) * 5.0D;
            ship.getLookControl().setLookAt(tx, ty, tz, 60.0F, 60.0F);
        }
    }
}
