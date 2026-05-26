package org.trp.shincolle.entity.base;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.trp.shincolle.Shincolle;

import java.util.EnumSet;

final class EntityShipGuardGoal extends Goal {
    private static final int GUARD_MOVE_FAIL_LIMIT = 40;
    private static final int GUARD_STUCK_TICK_LIMIT = 120;
    private static final int GUARD_TELEPORT_COOLDOWN_TICKS = 100;
    private static final double GUARD_TELEPORT_DISTANCE_SQ = 256.0D;

    private final EntityShipBase ship;
    private final ShipMovementCoordinator movement;
    private final double speed;
    private final ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();
    private int nextPathTick;

    EntityShipGuardGoal(EntityShipBase ship, double speed) {
        this.ship = ship;
        this.movement = new ShipMovementCoordinator(ship);
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
        ShipGuardTarget guardTarget = ship.getGuardTarget();
        if (guardTarget.isBlock()) {
            return guardTarget.isIn(ship.level());
        }
        if (guardTarget.isEntity()) {
            Entity guarded = ship.getGuardedEntity();
            return guarded != null && guarded.isAlive();
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        this.nextPathTick = 0;
        this.recovery.reset(ship.position());
        this.movement.reset();
    }

    @Override
    public void tick() {
        Entity guardedEntity = ship.getGuardedEntity();
        ShipGuardTarget guardTarget = ship.getGuardTarget();

        int timer = ship.getStateTimer(18);
        boolean isSummoning = timer > 0;
        if (isSummoning) {
            ship.setStateTimer(18, timer - 1);
        }

        Vec3 target;
        if (guardTarget.isEntity() && guardedEntity != null) {
            if (ship.getGuardedPos(3) != EntityShipBase.getLegacyDimensionId(guardedEntity.level())) {
                ship.setGuardedPos(-1, -1, -1, EntityShipBase.getLegacyDimensionId(guardedEntity.level()), ShipGuardTarget.Type.ENTITY.legacyId());
            }
            target = guardedEntity.position();
        } else {
            target = guardTarget.blockCenter();
        }
        double distSq = ship.distanceToSqr(target.x, ship.getY(), target.z);

        double stopDistanceSq = guardTarget.isEntity() ? 9.0D : 0.5D;
        if (distSq > stopDistanceSq) {
            this.recovery.trackProgress(ship.position());
            if (tryTeleportRecovery(target, guardedEntity, distSq, false)) {
                return;
            }
            if (this.recovery.isStuckLongerThan(GUARD_STUCK_TICK_LIMIT)) {
                if (tryTeleportRecovery(target, guardedEntity, distSq, true)) {
                    return;
                }
                Shincolle.debugLog("GuardGoal stuckDisable ship={} target={} stuckTicks={}",
                        ship.getUUID(), target, this.recovery.stuckTicks());
                disableGuardState();
                return;
            }
            if (this.nextPathTick-- <= 0 || ship.getNavigation().isDone()) {
                this.nextPathTick = 10;
                if (!this.movement.moveTo(target, speed)) {
                    int failCount = this.recovery.recordMoveFailure();
                    Shincolle.debugLog("GuardGoal moveFail ship={} target={} failCount={}",
                            ship.getUUID(), target, failCount);
                    if (failCount > GUARD_MOVE_FAIL_LIMIT) {
                        if (tryTeleportRecovery(target, guardedEntity, distSq, true)) {
                            return;
                        }
                        Shincolle.debugLog("GuardGoal failDisable ship={} target={} failCount={}",
                                ship.getUUID(), target, this.recovery.moveFailCount());
                        disableGuardState();
                        return;
                    }
                } else {
                    this.recovery.clearMoveFailures();
                }
            }
        } else {
            this.nextPathTick = 0;
            this.recovery.reset(ship.position());
            this.movement.stop();
        }

        if (guardedEntity instanceof LivingEntity livingEntity) {
            ship.getLookControl().setLookAt(livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ(), 60.0F, 60.0F);
        } else if (guardedEntity != null) {
            ship.getLookControl().setLookAt(guardedEntity.getX(), guardedEntity.getY(), guardedEntity.getZ(), 60.0F, 60.0F);
        } else if (isSummoning || distSq < 16.0D) {
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

    private boolean tryTeleportRecovery(Vec3 target, Entity guardedEntity, double distSq, boolean force) {
        if (!this.recovery.shouldTryTeleport(force, distSq, GUARD_TELEPORT_DISTANCE_SQ,
                GUARD_TELEPORT_COOLDOWN_TICKS)) {
            return false;
        }

        boolean teleported = guardedEntity instanceof LivingEntity livingGuarded
                ? this.movement.teleportNearLiving(livingGuarded, 0.75D)
                : this.movement.teleportNearPoint(target, 0.75D);
        if (!teleported) {
            return false;
        }

        Shincolle.debugLog("GuardGoal teleportRecovery ship={} target={} force={} distSq={}",
                ship.getUUID(), target, force, distSq);
        this.nextPathTick = 0;
        this.recovery.reset(ship.position());
        return true;
    }

    private void disableGuardState() {
        this.nextPathTick = 0;
        this.recovery.clear();
        this.movement.stop();
        ship.setStateFlag(EntityShipBase.STATE_FLAG_DISABLE_GUARD_POS, true);
        ship.clearGuardTarget();
    }
}
