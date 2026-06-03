package org.trp.shincolle.entity.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.init.ModSounds;
import com.mojang.serialization.Dynamic;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class EntitySummonBase extends EntityShincolleSimpleMob {

    protected UUID carrierId;
    protected UUID targetId;
    protected int missionTick;
    protected int numAmmoLight;
    protected int numAmmoHeavy;
    protected float attackRangeSq;
    protected boolean resourcesReturned;
    private final ShipMovementCoordinator returnMovement;
    private final ShipMovementCoordinator attackMovement;
    private final ShipMovementCoordinator followMovement;
    private final ShipMovementRecoveryState returnRecovery;
    private int returnTicks;

    protected EntitySummonBase(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.returnMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMMAND);
        this.attackMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMBAT);
        this.followMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_NORMAL);
        this.returnRecovery = new ShipMovementRecoveryState();
        this.numAmmoLight = SummonAiNumbers.INITIAL_LIGHT_AMMO;
        this.numAmmoHeavy = 0;
        this.attackRangeSq = SummonAiNumbers.DEFAULT_ATTACK_RANGE_SQ;
        this.resourcesReturned = false;
    }

    public void performAttack(LivingEntity target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        EntityShipBase carrier = getCarrier();
        float damage = SummonAiNumbers.ATTACK_DAMAGE_DEFAULT;
        if (carrier != null) {
            damage = Math.max(SummonAiNumbers.DAMAGE_MIN, carrier.getLegacyShipStats().getFirepower() * SummonAiNumbers.ATTACK_DAMAGE_CARRIER_FACTOR);
        }
        
        target.hurt(this.damageSources().mobAttack(this), damage);
        
        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIT, target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ(),
                SummonAiNumbers.CRIT_PARTICLE_COUNT, SummonAiNumbers.CRIT_PARTICLE_OFFSET, SummonAiNumbers.CRIT_PARTICLE_OFFSET,
                SummonAiNumbers.CRIT_PARTICLE_OFFSET, SummonAiNumbers.CRIT_PARTICLE_SPEED);
        this.playSound(ModSounds.SHIP_FIRELIGHT.get(), SummonAiNumbers.ATTACK_SOUND_VOLUME, SummonAiNumbers.ATTACK_SOUND_PITCH);
    }

    public float getAttackRangeSq() {
        return this.attackRangeSq;
    }

    public void initSummon(EntityShipBase carrier, Entity target, int scaleLevel) {
        if (carrier == null) {
            return;
        }
        this.carrierId = carrier.getUUID();
        this.targetId = target == null ? null : target.getUUID();
        this.missionTick = 0;
        this.resourcesReturned = false;
        resetReturnState();
        this.setScaleLevel(scaleLevel);

        double offsetX = (this.random.nextDouble() * SummonAiNumbers.INIT_SUMMON_OFFSET_RANGE - SummonAiNumbers.INIT_SUMMON_OFFSET_CENTER);
        double offsetZ = (this.random.nextDouble() * SummonAiNumbers.INIT_SUMMON_OFFSET_RANGE - SummonAiNumbers.INIT_SUMMON_OFFSET_CENTER);
        this.moveTo(this.getX() + offsetX, this.getY(), this.getZ() + offsetZ, this.getYRot(), this.getXRot());

        this.setOwnerUUID(carrier.getOwnerUUID());
        this.setTame(true, false);

        float maxHealth = SummonAiNumbers.HEALTH_BASE + carrier.getLegacyShipStats().getMaxHealth() * SummonAiNumbers.HEALTH_SCALE_FACTOR;
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
        this.setHealth(maxHealth);

        float speed = SummonAiNumbers.SPEED_BASE + carrier.getLegacyShipStats().getMoveSpeed() * SummonAiNumbers.SPEED_SCALE_FACTOR;
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);

        float damage = Math.max(SummonAiNumbers.DAMAGE_MIN, carrier.getLegacyShipStats().getFirepower() * SummonAiNumbers.DAMAGE_SCALE_FACTOR);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damage);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(SummonAiNumbers.FOLLOW_RANGE_ATTR);

        this.attackRangeSq = SummonAiNumbers.DEFAULT_ATTACK_RANGE_SQ * SummonAiNumbers.DEFAULT_ATTACK_RANGE_SQ;

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
        compound.putFloat("AttackRangeSq", this.attackRangeSq);
        compound.putBoolean("ResourcesReturned", this.resourcesReturned);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.carrierId = compound.hasUUID("CarrierId") ? compound.getUUID("CarrierId") : null;
        this.targetId = compound.hasUUID("TargetId") ? compound.getUUID("TargetId") : null;
        this.missionTick = compound.getInt("MissionTick");
        this.numAmmoLight = compound.getInt("NumAmmoLight");
        this.numAmmoHeavy = compound.getInt("NumAmmoHeavy");
        this.attackRangeSq = compound.getFloat("AttackRangeSq");
        this.resourcesReturned = compound.getBoolean("ResourcesReturned");
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
        if (carrier == null) {
            // Carrier no longer exists in the world; cannot return resources
            this.discard();
            return;
        }
        if (!carrier.isAlive()) {
            // Carrier is dead but still exists; return resources before discarding
            returnSummonResourcesOnce(carrier);
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
                resetReturnState();
            } else {
                Entity carrierTarget = carrier.getTarget();
                if (carrierTarget instanceof LivingEntity livingTarget && livingTarget.isAlive()) {
                    this.setTarget(livingTarget);
                    this.targetId = carrierTarget.getUUID();
                    resetReturnState();
                } else {
                    this.setTarget(null);
                    handleReturnToCarrier(carrier);
                    return;
                }
            }
        }
    }

    protected boolean checkReturnToCarrier(EntityShipBase carrier) {
        if (this.missionTick >= SummonAiNumbers.LIFETIME_TICKS) {
            return true;
        }
        if (this.numAmmoLight <= 0 && this.numAmmoHeavy <= 0) {
            return true;
        }
        return false;
    }

    protected void handleReturnToCarrier(EntityShipBase carrier) {
        this.returnTicks++;
        double distSq = this.distanceToSqr(carrier);
        if (distSq <= SummonAiNumbers.RETURN_REACH_DISTANCE_SQ && this.missionTick > SummonAiNumbers.RETURN_MIN_MISSION_TICKS) {
            returnSummonResourcesOnce(carrier);
            this.discard();
            resetReturnState();
        } else {
            this.returnMovement.moveTo(carrier, SummonAiNumbers.RETURN_MOVE_SPEED);
            if (trackReturnRecovery(carrier, distSq)) {
                return;
            }
            if (this.returnTicks > SummonAiNumbers.RETURN_FAILSAFE_TICKS
                    && this.returnRecovery.isStuckLongerThan(SummonAiNumbers.RETURN_FAILSAFE_TICKS)) {
                Shincolle.debugLog("[SCMoveDiag] SummonReturn failsafeDiscard summon={} carrier={} distanceSqr={} returnTicks={} stuckTicks={}",
                        this.getUUID(), carrier.getUUID(), distSq, this.returnTicks, this.returnRecovery.stuckTicks());
                returnSummonResourcesOnce(carrier);
                this.discard();
                resetReturnState();
            }
        }
    }

    private boolean trackReturnRecovery(EntityShipBase carrier, double distanceSqr) {
        this.returnRecovery.trackProgress(this.position());
        boolean force = this.returnRecovery.isStuckLongerThan(SummonAiNumbers.RETURN_STUCK_TICK_LIMIT);
        if (!force && (this.tickCount % SummonAiNumbers.RETURN_RECOVERY_POLL_INTERVAL) != 0) {
            return false;
        }
        if (!this.returnRecovery.shouldTryTeleportThrottled(force, distanceSqr,
                SummonAiNumbers.RETURN_TELEPORT_DISTANCE_SQ, SummonAiNumbers.RETURN_TELEPORT_COOLDOWN_TICKS)) {
            return false;
        }
        if (!this.returnMovement.teleportNearLiving(carrier, SummonAiNumbers.TELEPORT_VERTICAL_OFFSET)) {
            return false;
        }

        Shincolle.debugLog("[SCMoveDiag] SummonReturn teleportRecovery summon={} carrier={} force={} distanceSqr={} stuckTicks={}",
                this.getUUID(), carrier.getUUID(), force, distanceSqr, this.returnRecovery.stuckTicks());
        this.returnRecovery.reset(this.position());
        this.returnTicks = 0;
        return true;
    }

    private void resetReturnState() {
        this.returnRecovery.clear();
        this.returnTicks = 0;
    }

    @Override
    protected Brain.Provider<EntitySummonBase> brainProvider() {
        return Brain.provider(EntitySummonBrainAi.MEMORY_TYPES, EntitySummonBrainAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return EntitySummonBrainAi.makeBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @Override
    protected void customServerAiStep() {
        if (this.level() instanceof ServerLevel serverLevel) {
            EntitySummonBrainAi.tick(serverLevel, this);
        }
        super.customServerAiStep();
    }

    ShipMovementCoordinator attackMovementCoordinator() {
        return this.attackMovement;
    }

    ShipMovementCoordinator followMovementCoordinator() {
        return this.followMovement;
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (!this.level().isClientSide) {
            returnSummonResourcesOnce(getCarrier());
        }
    }

    protected final void returnSummonResourcesOnce(@Nullable EntityShipBase carrier) {
        if (this.resourcesReturned || carrier == null) {
            return;
        }
        this.resourcesReturned = true;
        returnSummonResources(carrier);
    }

    /** Override in subclasses to return ammo/servants/resources to the carrier. */

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
        Entity entity = serverLevel.getEntity(this.targetId);
        if (entity == null || !entity.isAlive() || entity.isRemoved()) {
            return null;
        }
        return entity;
    }
}
