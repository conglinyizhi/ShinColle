package org.trp.shincolle.entity.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.trp.shincolle.entity.EntityAircraftBase;
import org.trp.shincolle.init.ModSounds;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class EntitySummonBase extends EntityShincolleSimpleMob {

    protected static final int LIFETIME_TICKS = 1200;

    protected UUID carrierId;
    protected UUID targetId;
    protected int missionTick;
    protected int numAmmoLight;
    protected int numAmmoHeavy;
    protected int scaleLevel;
    protected float attackRangeSq;

    protected EntitySummonBase(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    public void initSummon(EntityShipBase carrier, Entity target, int scaleLevel) {
        if (carrier == null) {
            return;
        }
        this.carrierId = carrier.getUUID();
        this.targetId = target == null ? null : target.getUUID();
        this.missionTick = 0;
        this.scaleLevel = scaleLevel;

        this.setOwnerUUID(carrier.getOwnerUUID());
        this.setTame(true, false);

        float maxHealth = 10.0f + carrier.getLegacyShipStats().getMaxHealth() * 0.2f;
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
        this.setHealth(maxHealth);

        float speed = 0.25f + carrier.getLegacyShipStats().getMoveSpeed() * 0.1f;
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);

        float damage = Math.max(2.0f, carrier.getLegacyShipStats().getFirepower() * 0.5f);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damage);

        this.attackRangeSq = 16.0f * 16.0f;

        if (target instanceof LivingEntity livingTarget) {
            this.setTarget(livingTarget);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.carrierId != null) {
            compound.putUUID("CarrierId", this.carrierId);
        }
        if (this.targetId != null) {
            compound.putUUID("TargetId", this.targetId);
        }
        compound.putInt("MissionTick", this.missionTick);
        compound.putInt("NumAmmoLight", this.numAmmoLight);
        compound.putInt("NumAmmoHeavy", this.numAmmoHeavy);
        compound.putInt("ScaleLevel", this.scaleLevel);
        compound.putFloat("AttackRangeSq", this.attackRangeSq);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.carrierId = compound.hasUUID("CarrierId") ? compound.getUUID("CarrierId") : null;
        this.targetId = compound.hasUUID("TargetId") ? compound.getUUID("TargetId") : null;
        this.missionTick = compound.getInt("MissionTick");
        this.numAmmoLight = compound.getInt("NumAmmoLight");
        this.numAmmoHeavy = compound.getInt("NumAmmoHeavy");
        this.scaleLevel = compound.getInt("ScaleLevel");
        this.attackRangeSq = compound.getFloat("AttackRangeSq");
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            updateServerLogic();
        }
    }

    protected void updateServerLogic() {
        this.missionTick++;

        EntityShipBase carrier = getCarrier();
        if (carrier == null || !carrier.isAlive()) {
            this.discard();
            return;
        }

        if (checkReturnToCarrier(carrier)) {
            handleReturnToCarrier(carrier);
            return;
        }

        if (this.getTarget() == null || !this.getTarget().isAlive()) {
            Entity currentTarget = getMissionTarget();
            if (currentTarget instanceof LivingEntity livingTarget && livingTarget.isAlive()) {
                this.setTarget(livingTarget);
            } else {
                Entity carrierTarget = carrier.getTarget();
                if (carrierTarget instanceof LivingEntity livingTarget && livingTarget.isAlive()) {
                    this.setTarget(livingTarget);
                    this.targetId = carrierTarget.getUUID();
                } else {
                    this.setTarget(null);
                }
            }
        }
    }

    protected boolean checkReturnToCarrier(EntityShipBase carrier) {
        if (this.missionTick >= LIFETIME_TICKS) {
            return true;
        }
        if (this.numAmmoLight <= 0 && this.numAmmoHeavy <= 0) {
            return true;
        }
        return false;
    }

    protected void handleReturnToCarrier(EntityShipBase carrier) {
        double distSq = this.distanceToSqr(carrier);
        if (distSq <= 16.0D) {
            returnSummonResources(carrier);
            this.discard();
        } else {
            this.getNavigation().moveTo(carrier, 1.2D);
            if (this.tickCount % 20 == 0 && this.distanceToSqr(carrier) > 1024.0D) {
                this.discard();
            }
        }
    }

    protected void returnSummonResources(EntityShipBase carrier) {

    }

    @Nullable
    protected EntityShipBase getCarrier() {
        if (this.carrierId == null || !(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        Entity entity = serverLevel.getEntity(this.carrierId);
        if (entity instanceof EntityShipBase ship) {
            return ship;
        }
        return null;
    }

    @Nullable
    protected Entity getMissionTarget() {
        if (this.targetId == null || !(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        return serverLevel.getEntity(this.targetId);
    }
}
