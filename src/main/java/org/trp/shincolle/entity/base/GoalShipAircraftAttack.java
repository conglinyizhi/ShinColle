package org.trp.shincolle.entity.base;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.trp.shincolle.entity.EntityAircraftBase;

import java.util.EnumSet;

public class GoalShipAircraftAttack extends Goal {

    private final EntityAircraftBase host;
    private final ShipMovementCoordinator movement;
    private Entity target;
    private Vec3 randPos;
    private double distSq;
    private float rangeSq;

    public GoalShipAircraftAttack(EntityAircraftBase host) {
        this.host = host;
        this.movement = new ShipMovementCoordinator(host, ShipMovementCoordinator.PRIORITY_COMBAT);
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        Entity targetEntity = this.host.getMissionTarget();
        if (!canAttackMissionTarget(targetEntity)) {
            return false;
        }
        
        if (this.host.getMissionTick() > 20) {
            this.target = targetEntity;
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        float attackRange = this.host.isMissionLightAircraft() ? 6.0F : 16.0F;
        this.rangeSq = attackRange * attackRange;
        this.movement.reset();
        this.updateRandomPos();
    }

    @Override
    public boolean canContinueToUse() {
        Entity targetEntity = this.host.getMissionTarget();
        if (!canAttackMissionTarget(targetEntity)) {
            return false;
        }
        this.target = targetEntity;
        return true;
    }

    @Override
    public void stop() {
        this.target = null;
        this.randPos = null;
        this.movement.stop();
    }

    @Override
    public void tick() {
        if (this.target == null) return;

        this.distSq = this.host.distanceToSqr(this.target.getX(), this.target.getY() + 2.0D, this.target.getZ());

        if ((this.host.tickCount & 0xF) == 0 || this.randPos == null || this.host.getNavigation().isDone()) {
            updateRandomPos();
        }

        double speed;
        if (this.host.getAttackDelay() > 0) {
            speed = 0.3D;
        } else {
            speed = this.distSq > this.rangeSq ? 0.6D : 0.3D;
        }
        this.movement.moveTo(this.randPos, speed);

        if (this.host.getAttackDelay() <= 0 && this.host.hasLineOfSight(this.target) && this.distSq < this.rangeSq) {
            if (this.host.isMissionLightAircraft() && this.host.hasAmmoLight()) {
                this.host.attackWithLightAmmo(this.target);
            } else if (!this.host.isMissionLightAircraft() && this.host.hasAmmoHeavy()) {
                this.host.attackWithHeavyAmmo(this.target);
            }
        }
    }

    private void updateRandomPos() {
        Entity ref = this.target != null ? this.target : this.host;
        this.randPos = this.host.getRandomCruisePos(ref);
    }

    private boolean canAttackMissionTarget(Entity targetEntity) {
        if (targetEntity == null || !targetEntity.isAlive()) {
            return false;
        }
        return this.host.isMissionLightAircraft()
                ? this.host.hasAmmoLight()
                : this.host.hasAmmoHeavy();
    }
}
