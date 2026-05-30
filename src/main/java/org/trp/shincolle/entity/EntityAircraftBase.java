package org.trp.shincolle.entity;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.entity.base.ShipMovementCoordinator;
import org.trp.shincolle.entity.base.ShipMovementRecoveryState;
import org.trp.shincolle.init.ModSounds;
import org.trp.shincolle.utility.PerformanceTrace;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class EntityAircraftBase extends org.trp.shincolle.entity.base.EntityShincolleSimpleMob {

    private UUID carrierId;
    private UUID targetId;
    private boolean backHome;
    private boolean missionLightAircraft;
    private int missionTick;
    private int attackDelay;
    private int maxAttackDelay;
    private int numAmmoLight;
    private int numAmmoHeavy;

    private int deathAnimTick;
    private boolean isDying;
    private double deadMotionX;
    private double deadMotionZ;

    private double[] randPos;
    private float attackRangeSq;
    private final ShipMovementCoordinator returnMovement;
    private final ShipMovementCoordinator attackMovement;
    private final ShipMovementRecoveryState returnRecovery;
    private int returnHomeTicks;

    public static AttributeSupplier.Builder createAttributes() {
        return org.trp.shincolle.entity.base.EntityShincolleSimpleMob.createAttributes()
                .add(Attributes.FLYING_SPEED, 0.4D);
    }

    protected EntityAircraftBase(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.returnMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMMAND);
        this.attackMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMBAT);
        this.returnRecovery = new ShipMovementRecoveryState();
        this.moveControl = new FlyingMoveControl(this, 36, true);
        this.setNoGravity(true);
        this.randPos = new double[3];
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    public void initCarrierMission(EntityShipBase carrier, Entity target, boolean lightAircraft) {
        if (carrier == null) {
            return;
        }
        this.carrierId = carrier.getUUID();
        this.targetId = target == null ? null : target.getUUID();
        resumeMission();
        this.missionTick = 0;
        this.missionLightAircraft = lightAircraft;
        this.isDying = false;
        this.deathAnimTick = 0;

        if (lightAircraft) {
            this.numAmmoLight = AircraftAiNumbers.INITIAL_AMMO_LIGHT;
            this.numAmmoHeavy = 0;
        } else {
            this.numAmmoLight = 0;
            this.numAmmoHeavy = AircraftAiNumbers.INITIAL_AMMO_HEAVY;
        }

        float attackSpeed = carrier.getLegacyShipStats().getReloadSpeed();
        this.maxAttackDelay = (int) (AircraftAiNumbers.BASE_ATTACK_SPEED_AIRCRAFT / attackSpeed) + AircraftAiNumbers.FIXED_ATTACK_DELAY_AIRCRAFT;
        this.attackDelay = 0;

        float range = lightAircraft ? AircraftAiNumbers.ATTACK_RANGE_LIGHT : AircraftAiNumbers.ATTACK_RANGE_HEAVY;
        this.attackRangeSq = range * range;

        this.setNoGravity(true);
        this.setOwnerUUID(carrier.getOwnerUUID());
        this.setTame(true, false);

        if (target != null) {
            this.randPos[0] = target.getX();
            this.randPos[1] = target.getY();
            this.randPos[2] = target.getZ();
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
        compound.putBoolean("BackHome", this.backHome);
        compound.putBoolean("MissionLight", this.missionLightAircraft);
        compound.putInt("MissionTick", this.missionTick);
        compound.putInt("AttackDelay", this.attackDelay);
        compound.putInt("MaxAttackDelay", this.maxAttackDelay);
        compound.putInt("NumAmmoLight", this.numAmmoLight);
        compound.putInt("NumAmmoHeavy", this.numAmmoHeavy);
        compound.putFloat("AttackRangeSq", this.attackRangeSq);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.carrierId = compound.hasUUID("CarrierId") ? compound.getUUID("CarrierId") : null;
        this.targetId = compound.hasUUID("TargetId") ? compound.getUUID("TargetId") : null;
        this.backHome = compound.getBoolean("BackHome");
        this.missionLightAircraft = compound.getBoolean("MissionLight");
        this.missionTick = compound.getInt("MissionTick");
        this.attackDelay = compound.getInt("AttackDelay");
        this.maxAttackDelay = compound.getInt("MaxAttackDelay");
        this.numAmmoLight = compound.getInt("NumAmmoLight");
        this.numAmmoHeavy = compound.getInt("NumAmmoHeavy");
        this.attackRangeSq = compound.getFloat("AttackRangeSq");

        if (this.attackRangeSq <= 0.0F) {
            this.attackRangeSq = (this.missionLightAircraft ? AircraftAiNumbers.ATTACK_RANGE_LIGHT : AircraftAiNumbers.ATTACK_RANGE_HEAVY);
            this.attackRangeSq *= this.attackRangeSq;
        }
        if (this.maxAttackDelay <= 0) {
            this.maxAttackDelay = AircraftAiNumbers.FIXED_ATTACK_DELAY_AIRCRAFT + AircraftAiNumbers.BASE_ATTACK_SPEED_AIRCRAFT;
        }
        if (this.attackDelay <= 0) {
            this.attackDelay = this.maxAttackDelay + AircraftAiNumbers.HOST_CHECK_TIMEOUT;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.isDying) {
            if (!this.level().isClientSide) {
                tickDeathAnimation();
            }
            return;
        }

        this.setNoGravity(true);
        this.fallDistance = 0.0F;

        if (this.level().isClientSide) {
            applyFlyParticle();
        } else {
            updateServerLogic();
        }
        updateRotation();
    }

    private void updateServerLogic() {
        boolean tracing = PerformanceTrace.enabled();
        long start = tracing ? PerformanceTrace.now() : 0L;
        try {
            this.missionTick++;
            if (this.attackDelay > 0) {
                this.attackDelay--;
            }

            EntityShipBase carrier = getCarrier();
            if (carrier == null || !carrier.isAlive()) {
                this.discard();
                return;
            }

            if (this.backHome) {
                handleReturnToHome(carrier);
                return;
            }

            handleInitialBoost();
            handleTargeting(carrier);
            checkMissionStatus();
        } finally {
            if (tracing) {
                long elapsed = PerformanceTrace.elapsed(start);
                PerformanceTrace.addProjectileTime(elapsed);
                PerformanceTrace.logSlowProjectileTick(this, "aircraft", elapsed,
                        "missionTick=" + this.missionTick
                                + " backHome=" + this.backHome
                                + " light=" + this.missionLightAircraft
                                + " ammoLight=" + this.numAmmoLight
                                + " ammoHeavy=" + this.numAmmoHeavy
                                + " target=" + this.targetId
                                + " carrier=" + this.carrierId);
            }
        }

    }

    private void checkMissionStatus() {
        if (this.missionTick >= AircraftAiNumbers.LIFETIME_TICKS) {
            startReturnHome();
            return;
        }

        if (this.missionLightAircraft && this.numAmmoLight <= 0) {
            startReturnHome();
            return;
        }
        if (!this.missionLightAircraft && this.numAmmoHeavy <= 0) {
            startReturnHome();
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && (this.isNoGravity() || !this.isDying)) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95D));
        } else {
            super.travel(travelVector);
        }
    }

    private void handleInitialBoost() {
        if (this.missionTick >= AircraftAiNumbers.INITIAL_BOOST_DURATION) {
            return;
        }
        Entity target = getMissionTarget();
        if (target == null) {
            return;
        }
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double distSqrt = Mth.sqrt((float) (dx * dx + dz * dz));
        if (distSqrt > 1.0E-4D) {
            this.setDeltaMovement(
                    dx / distSqrt * AircraftAiNumbers.INITIAL_BOOST_SPEED,
                    AircraftAiNumbers.INITIAL_BOOST_Y,
                    dz / distSqrt * AircraftAiNumbers.INITIAL_BOOST_SPEED
            );
            this.hasImpulse = true;
        }
    }

    private void handleTargeting(EntityShipBase carrier) {
        if (this.missionTick % AircraftAiNumbers.TARGETING_INTERVAL != 0) {
            return;
        }
        if (this.missionTick < AircraftAiNumbers.HOST_CHECK_TIMEOUT) {
            return;
        }

        Entity currentTarget = getMissionTarget();
        boolean needsNewTarget = currentTarget == null || !currentTarget.isAlive()
                || !isValidTarget(carrier, currentTarget);

        if (!needsNewTarget) {
            return;
        }

        Entity newTarget = findNewTarget(carrier);

        if (newTarget == null) {
            Entity carrierTarget = carrier.getTarget();
            if (carrierTarget != null && carrierTarget.isAlive() && !isFriendlyTarget(carrier, carrierTarget)) {
                newTarget = carrierTarget;
            }
        }

        if (newTarget != null) {
            this.targetId = newTarget.getUUID();
            resumeMission();
        } else {
            startReturnHome();
        }
    }

    private void startReturnHome() {
        if (!this.backHome) {
            this.returnRecovery.clear();
            this.returnHomeTicks = 0;
        }
        this.targetId = null;
        this.backHome = true;
    }

    private void resumeMission() {
        this.backHome = false;
        this.returnRecovery.clear();
        this.returnHomeTicks = 0;
    }

    private void handleReturnToHome(EntityShipBase carrier) {
        if (!this.isAlive()) return;
        this.returnHomeTicks++;

        double distSq = this.distanceToSqr(carrier);
        double arrivalDist = Math.pow(2.0D + carrier.getBbHeight(), 2.0D);
        
        if (distSq <= arrivalDist) {
            returnSummonResources(carrier);
            this.discard();
            return;
        }

        Vec3 homePos = carrier.position().add(0.0D, carrier.getBbHeight() + AircraftAiNumbers.RETURN_HOME_EXTRA_HEIGHT, 0.0D);
        this.returnMovement.moveTo(homePos, AircraftAiNumbers.RETURN_HOME_SPEED);
        if (trackReturnHomeRecovery(carrier, distSq)) {
            return;
        }
        if (this.returnHomeTicks > AircraftAiNumbers.RETURN_HOME_FAILSAFE_TICKS
                && this.returnRecovery.isStuckLongerThan(AircraftAiNumbers.RETURN_HOME_FAILSAFE_TICKS)) {
            Shincolle.debugLog("[SCMoveDiag] AircraftReturn failsafeDiscard aircraft={} carrier={} distanceSqr={} returnTicks={} stuckTicks={}",
                    this.getUUID(), carrier.getUUID(), distSq, this.returnHomeTicks, this.returnRecovery.stuckTicks());
            returnSummonResources(carrier);
            this.discard();
        }
    }

    private boolean trackReturnHomeRecovery(EntityShipBase carrier, double distanceSqr) {
        this.returnRecovery.trackProgress(this.position());
        boolean force = this.returnRecovery.isStuckLongerThan(AircraftAiNumbers.RETURN_HOME_STUCK_TICK_LIMIT);
        if (!force && (this.tickCount % AircraftAiNumbers.RETURN_HOME_CHECK_INTERVAL) != 0) {
            return false;
        }
        if (!this.returnRecovery.shouldTryTeleportThrottled(force, distanceSqr,
                AircraftAiNumbers.RETURN_HOME_TELEPORT_DISTANCE_SQ, AircraftAiNumbers.RETURN_HOME_TELEPORT_COOLDOWN_TICKS)) {
            return false;
        }
        if (!this.returnMovement.teleportNearLiving(carrier, carrier.getBbHeight() + AircraftAiNumbers.RETURN_HOME_TELEPORT_EXTRA)) {
            return false;
        }

        Shincolle.debugLog("[SCMoveDiag] AircraftReturn teleportRecovery aircraft={} carrier={} force={} distanceSqr={} stuckTicks={}",
                this.getUUID(), carrier.getUUID(), force, distanceSqr, this.returnRecovery.stuckTicks());
        this.returnRecovery.reset(this.position());
        this.returnHomeTicks = 0;
        return true;
    }

    private void returnSummonResources(EntityShipBase carrier) {
        int returnLight = Math.max(0, this.numAmmoLight - AircraftAiNumbers.AMMO_RETURN_PENALTY_LIGHT);
        int returnHeavy = Math.max(0, this.numAmmoHeavy - AircraftAiNumbers.AMMO_RETURN_PENALTY_HEAVY);

        carrier.setAmmoLight(carrier.getAmmoLight() + returnLight);
        carrier.setAmmoHeavy(carrier.getAmmoHeavy() + returnHeavy);

        carrier.returnAircraftToDeck(this.missionLightAircraft);
    }

    public void attackWithLightAmmo(Entity target) {
        EntityShipBase carrier = getCarrier();
        if (carrier == null) return;
        if (this.numAmmoLight > 0) {
            this.numAmmoLight--;
        }
        this.attackDelay = this.maxAttackDelay;
        this.playSound(ModSounds.SHIP_MACHINEGUN.get(), 1.0F, 1.0F);
        float atk = Math.max(2.0F, carrier.getLegacyShipStats().getFirepower() * 0.35F);
        target.hurt(this.damageSources().mobProjectile(this, carrier), atk);
    }

    public void attackWithHeavyAmmo(Entity target) {
        EntityShipBase carrier = getCarrier();
        if (carrier == null) return;
        if (this.numAmmoHeavy > 0) {
            this.numAmmoHeavy--;
        }
        this.attackDelay = this.maxAttackDelay;
        float atk = Math.max(4.0F, carrier.getLegacyShipStats().getFirepower() * 0.55F);
        if (this.level() instanceof ServerLevel serverLevel) {
            float missileDamage = atk * 1.4F;
            Vec3 targetPos = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
            double distance = this.distanceTo(target);
            
            if (this.random.nextFloat() <= calcMissRate(carrier, (float) distance)) {
                double offsetX = -5.0D + this.random.nextDouble() * 10.0D;
                double offsetY = this.random.nextDouble() * 5.0D;
                double offsetZ = -5.0D + this.random.nextDouble() * 10.0D;
                targetPos = targetPos.add(offsetX, offsetY, offsetZ);
                
                serverLevel.sendParticles(org.trp.shincolle.init.ModParticles.PARTICLE_TEXTS.get(),
                        this.getX(), this.getY() + 1.2D, this.getZ(),
                        1, 0.0D, 0.1D, 0.5D, 0.0D);
            }
            
            org.trp.shincolle.entity.projectile.EntityAbyssMissile missile =
                    new org.trp.shincolle.entity.projectile.EntityAbyssMissile(
                            serverLevel, this, target, targetPos, missileDamage,
                            org.trp.shincolle.entity.projectile.EntityAbyssMissile.MoveType.ARC,
                            0.7F, 0.0F, 0.0F, null,
                            200, 3.5F);
            serverLevel.addFreshEntity(missile);
        }
    }

    public float calcMissRate(EntityShipBase carrier, float distance) {
        float range = AircraftAiNumbers.ATTACK_RANGE_HEAVY;
        float levelMod = 0.001F * carrier.getLevel();
        float miss = 0.25F + 0.25F * (distance / range) - levelMod;
        return Math.max(0.0F, Math.min(miss, 0.5F));
    }

    public Vec3 getRandomCruisePos(@Nullable Entity reference) {
        double minDist = this.missionLightAircraft ? AircraftAiNumbers.RAND_POS_MIN_LIGHT : AircraftAiNumbers.RAND_POS_MIN_HEAVY;
        double randDist = this.missionLightAircraft ? AircraftAiNumbers.RAND_POS_RAND_LIGHT : AircraftAiNumbers.RAND_POS_RAND_HEAVY;

        Entity ref = reference != null ? reference : this;
        Level level = this.level();
        float currentYaw = this.getYRot();

        for (int i = 0; i < AircraftAiNumbers.RANDOM_CRUISE_ATTEMPTS; i++) {
            float angle = currentYaw + (i * AircraftAiNumbers.RANDOM_CRUISE_ANGLE_STEP);
            double rad = Math.toRadians(angle);

            double dist = minDist + this.getRandom().nextDouble() * randDist;
            double newX = ref.getX() + Math.cos(rad) * dist;
            double newZ = ref.getZ() + Math.sin(rad) * dist;
            double newY = ref.getY() + ref.getBbHeight() + AircraftAiNumbers.RANDOM_CRUISE_Y_OFFSET
                    + this.getRandom().nextDouble() * AircraftAiNumbers.RANDOM_CRUISE_Y_RANDOM;

            net.minecraft.core.BlockPos targetPos = net.minecraft.core.BlockPos.containing(newX, newY, newZ);
            if (level.getBlockState(targetPos).getCollisionShape(level, targetPos).isEmpty()) {
                return new Vec3(newX, newY, newZ);
            }
        }

        return new Vec3(ref.getX(), ref.getY() + AircraftAiNumbers.RANDOM_CRUISE_FALLBACK_Y, ref.getZ());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (!this.level().isClientSide && result && !this.isAlive() && !this.isDying) {
            this.isDying = true;
            this.deathAnimTick = 0;
            this.setNoGravity(false);
            Vec3 motion = this.getDeltaMovement();
            this.deadMotionX = motion.x;
            this.deadMotionZ = motion.z;
            this.setHealth(1.0F);
            // Return remaining ammo to carrier before death animation
            EntityShipBase carrier = getCarrier();
            if (carrier != null) {
                returnSummonResources(carrier);
            }
        }
        return result;
    }

    private void tickDeathAnimation() {
        this.deathAnimTick++;
        this.setNoGravity(false);

        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(this.deadMotionX, motion.y - AircraftAiNumbers.DEATH_GRAVITY, this.deadMotionZ);
        this.hasImpulse = true;

        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.deathAnimTick % 2 == 0) {
                double range = this.getBbWidth() * 0.5D;
                for (int i = 0; i < 3; i++) {
                    serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                            this.getX() - range + this.random.nextDouble() * range * 2.0D,
                            this.getY() + this.getBbHeight() * 0.3D + this.random.nextDouble() * 0.3D,
                            this.getZ() - range + this.random.nextDouble() * range * 2.0D,
                            1, 0.0D, 0.0D, 0.0D, 0.02D);
                }
            }

            if (this.deathAnimTick >= AircraftAiNumbers.DEATH_TIME_EXPLOSION - 1) {
                for (int i = 0; i < 12; i++) {
                    double ran1 = this.getBbWidth() * (this.random.nextFloat() - 0.5F);
                    double ran2 = this.getBbWidth() * (this.random.nextFloat() - 0.5F);
                    serverLevel.sendParticles(ParticleTypes.LAVA,
                            this.getX() + ran1, this.getY() + this.getBbHeight() * 0.3D, this.getZ() + ran2,
                            1, 0.0D, 0.0D, 0.0D, 0.0D);
                    if ((i & 3) == 0) {
                        serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                                this.getX() + ran2, this.getY() + this.getBbHeight() * 0.5D, this.getZ() + ran1,
                                1, 0.0D, 0.0D, 0.0D, 0.0D);
                    }
                }
            }

            if (this.deathAnimTick >= AircraftAiNumbers.DEATH_TIME_EXPLOSION) {
                for (int k = 0; k < 20; k++) {
                    double d2 = this.random.nextGaussian() * 0.02D;
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    serverLevel.sendParticles(ParticleTypes.POOF,
                            this.getX() + (this.random.nextFloat() * this.getBbWidth() * 2.0F) - this.getBbWidth(),
                            this.getY() + (this.random.nextFloat() * this.getBbHeight()),
                            this.getZ() + (this.random.nextFloat() * this.getBbWidth() * 2.0F) - this.getBbWidth(),
                            1, d2, d0, d1, 0.05D);
                }
                this.discard();
            }
        }
    }

    @Override
    public boolean isOnFire() {
        if (this.isDying && this.deathAnimTick > AircraftAiNumbers.DEATH_TIME_BURNING) {
            return true;
        }
        return super.isOnFire();
    }

    @Override
    protected Brain.Provider<EntityAircraftBase> brainProvider() {
        return Brain.provider(AircraftBrainAi.MEMORY_TYPES, AircraftBrainAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return AircraftBrainAi.makeBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @Override
    protected void customServerAiStep() {
        if (this.level() instanceof ServerLevel serverLevel) {
            AircraftBrainAi.tick(serverLevel, this);
        }
        super.customServerAiStep();
    }

    public ShipMovementCoordinator attackMovementCoordinator() {
        return this.attackMovement;
    }


    @Override
    public void move(MoverType type, Vec3 pos) {
        super.move(type, pos);
        this.checkInsideBlocks();
    }

    private void updateRotation() {
        Vec3 delta = this.getDeltaMovement();
        if (delta.horizontalDistanceSqr() < 1.0E-5D) {
            return;
        }
        
        double horizontal = delta.horizontalDistance();
        float targetYaw = (float) (Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0D);
        float targetPitch = (float) (-Math.toDegrees(Math.atan2(delta.y, horizontal)));

        this.setYRot(Mth.approachDegrees(this.getYRot(), targetYaw, 15.0F));
        this.setXRot(Mth.approachDegrees(this.getXRot(), targetPitch, 15.0F));
        
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
    }


    @Override
    protected void pushEntities() {
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    protected void applyFlyParticle() {
    }


    @Nullable
    private EntityShipBase getCarrier() {
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
    public Entity getMissionTarget() {
        if (this.targetId == null || !(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        return serverLevel.getEntity(this.targetId);
    }

    @Nullable
    private Entity findNewTarget(EntityShipBase carrier) {
        double range = carrier.isStateAntiAir() ? AircraftAiNumbers.TARGETING_RANGE_AIR_ONLY : AircraftAiNumbers.TARGETING_RANGE_NORMAL;
        AABB box = this.getBoundingBox().inflate(range, range, range);
        List<Entity> entities = this.level().getEntities(this, box, entity -> {
            if (entity == null || !entity.isAlive() || entity == this) return false;
            return entity instanceof LivingEntity;
        });

        Entity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Entity entity : entities) {
            if (!isValidTarget(carrier, entity)) {
                continue;
            }
            double dist = this.distanceToSqr(entity);
            if (dist < nearestDistance) {
                nearestDistance = dist;
                nearest = entity;
            }
        }
        return nearest;
    }

    private boolean isValidTarget(EntityShipBase carrier, Entity target) {
        if (!(target instanceof LivingEntity)) {
            return false;
        }
        if (isFriendlyTarget(carrier, target)) {
            return false;
        }
        if (target instanceof Enemy) {
            return true;
        }
        boolean pvpEnabled = carrier.getStateFlag(18);
        if (pvpEnabled) {
            if (target instanceof Player || target instanceof EntityShipBase) {
                return true;
            }
        }
        if (carrier.getTarget() == target) {
            return true;
        }
        LivingEntity lastHurtBy = carrier.getLastHurtByMob();
        if (lastHurtBy == target) {
            return true;
        }
        LivingEntity owner = carrier.getOwner();
        if (owner != null && (owner.getLastHurtByMob() == target || owner.getLastHurtMob() == target)) {
            return true;
        }
        return false;
    }

    private boolean isFriendlyTarget(EntityShipBase carrier, Entity target) {
        if (target == carrier) {
            return true;
        }
        if (target instanceof Player player && Objects.equals(player.getUUID(), carrier.getOwnerUUID())) {
            return true;
        }
        if (target instanceof TamableAnimal tamable && Objects.equals(tamable.getOwnerUUID(), carrier.getOwnerUUID())) {
            return true;
        }
        if (target instanceof EntityShipBase ship && Objects.equals(ship.getOwnerUUID(), carrier.getOwnerUUID())) {
            return true;
        }
        if (target instanceof EntityAircraftBase aircraft) {
            EntityShipBase otherCarrier = aircraft.getCarrier();
            return otherCarrier != null && Objects.equals(otherCarrier.getOwnerUUID(), carrier.getOwnerUUID());
        }
        return false;
    }



    public boolean isMissionLightAircraft() {
        return this.missionLightAircraft;
    }

    protected boolean isDefaultLightAircraft() {
        return true;
    }

    public int getMissionTick() {
        return this.missionTick;
    }

    public int getAttackDelay() {
        return this.attackDelay;
    }

    public boolean hasAmmoLight() {
        return this.numAmmoLight > 0;
    }

    public boolean hasAmmoHeavy() {
        return this.numAmmoHeavy > 0;
    }
}
