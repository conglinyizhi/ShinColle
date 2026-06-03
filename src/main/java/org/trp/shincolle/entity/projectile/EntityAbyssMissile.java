package org.trp.shincolle.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.trp.shincolle.entity.EntityAircraftBase;
import org.trp.shincolle.entity.base.EntityMountBase;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.init.ModEntities;
import org.trp.shincolle.init.ModSounds;
import org.trp.shincolle.utility.PerformanceTrace;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntityAbyssMissile extends Entity implements IEntityWithComplexSpawn {
    private static final double MIN_DIST_FOR_ARC = 4.0D;
    private static final double ARC_ACCEL_LIMIT = 0.15D;
    private static final double TORPEDO_VEL_MULTIPLIER = 0.85D;
    private static final double TORPEDO_ACCEL_MULTIPLIER = 1.05D;
    private static final int TORPEDO_START_DELAY = 3;
    private static final float ARC_FACTOR_DEFAULT = 0.35F;
    private static final int CLUSTER_SPLIT_START = 6;
    private static final int CLUSTER_SPLIT_END = 40;
    private static final int CLUSTER_SPLIT_INTERVAL = 8;
    private static final int CLUSTER_SUB_LIFE = 140;
    private static final float CLUSTER_SUB_DAMAGE_SCALE = 0.5F;
    private static final float CLUSTER_SUB_EXPLOSION_RADIUS = 1.8F;
    private static final float CLUSTER_SUB_SPEED = 0.5F;
    private static final float CLUSTER_SUB_VERTICAL_ACCEL = -0.06F;
    private static final double BLACK_HOLE_PULL_RADIUS = 7.5D;
    private static final double BLACK_HOLE_PULL_STRENGTH = 0.12D;
    private static final String TAG_IMPACT_EFFECTS = "ImpactEffects";
    private static final String TAG_EFFECT_ID = "EffectId";
    private static final String TAG_EFFECT_LEVEL = "Amplifier";
    private static final String TAG_EFFECT_DURATION = "Duration";
    private static final String TAG_EFFECT_CHANCE = "Chance";

    public enum MoveType {
        DIRECT,
        ARC,
        TORPEDO,
        ARC_HOMING,
        PRESET_VELOCITY;

        public static MoveType fromId(int id) {
            if (id < 0 || id >= values().length) {
                return DIRECT;
            }
            return values()[id];
        }
    }

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(EntityAbyssMissile.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Optional<UUID>> TARGET_UUID = SynchedEntityData.defineId(EntityAbyssMissile.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(EntityAbyssMissile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(EntityAbyssMissile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> LIFE = SynchedEntityData.defineId(EntityAbyssMissile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> EXPLOSION_RADIUS = SynchedEntityData.defineId(EntityAbyssMissile.class, EntityDataSerializers.FLOAT);

    private int age;
    private MoveType moveType = MoveType.DIRECT;
    private double velX;
    private double velY;
    private double velZ;
    private double accY1;
    private double accY2;
    private int arcTick;
    private int arcSwitchTick;
    private boolean torpedoStarted;
    private int torpedoDelay;
    private Vec3 targetPos;
    private boolean clusterMain;
    private boolean clusterSub;
    private boolean blackHole;
    private final List<ImpactEffectData> impactEffects = new java.util.ArrayList<>();

    public EntityAbyssMissile(EntityType<EntityAbyssMissile> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public EntityAbyssMissile(Level level, Entity owner, Entity target, float damage, float speed, int life, float explosionRadius) {
        this(ModEntities.ABYSS_MISSILE.get(), level);
        if (owner != null) {
            this.setOwner(owner);
            this.setPos(owner.getX(), owner.getY() + owner.getBbHeight() * 0.6D, owner.getZ());
        }
        if (target != null) {
            this.setTarget(target);
            this.targetPos = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
        }
        this.setDamage(damage);
        this.setSpeed(speed);
        this.setLife(life);
        this.setExplosionRadius(explosionRadius);
        initializeMovement(MoveType.DIRECT, speed, 1.04F, 1.04F, null);
    }

    public EntityAbyssMissile(Level level, Entity owner, Entity target, float damage, MoveType moveType,
                              float vel0, float accY1, float accY2, Vec3 presetVelocity,
                              int life, float explosionRadius) {
        this(ModEntities.ABYSS_MISSILE.get(), level);
        if (owner != null) {
            this.setOwner(owner);
            this.setPos(owner.getX(), owner.getY() + owner.getBbHeight() * 0.6D, owner.getZ());
        }
        if (target != null) {
            this.setTarget(target);
            this.targetPos = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
        }
        this.setDamage(damage);
        this.setSpeed(vel0);
        this.setLife(life);
        this.setExplosionRadius(explosionRadius);
        initializeMovement(moveType, vel0, accY1, accY2, presetVelocity);
    }

    public EntityAbyssMissile(Level level, Entity owner, Entity target, Vec3 targetPos, float damage, MoveType moveType,
                              float vel0, float accY1, float accY2, Vec3 presetVelocity,
                              int life, float explosionRadius) {
        this(ModEntities.ABYSS_MISSILE.get(), level);
        if (owner != null) {
            this.setOwner(owner);
            this.setPos(owner.getX(), owner.getY() + owner.getBbHeight() * 0.6D, owner.getZ());
        }
        if (target != null) {
            this.setTarget(target);
        }
        this.targetPos = targetPos;
        this.setDamage(damage);
        this.setSpeed(vel0);
        this.setLife(life);
        this.setExplosionRadius(explosionRadius);
        initializeMovement(moveType, vel0, accY1, accY2, presetVelocity);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER_UUID, Optional.empty());
        builder.define(TARGET_UUID, Optional.empty());
        builder.define(DAMAGE, 6.0F);
        builder.define(SPEED, 0.7F);
        builder.define(LIFE, 200);
        builder.define(EXPLOSION_RADIUS, 3.5F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            this.entityData.set(OWNER_UUID, Optional.of(tag.getUUID("Owner")));
        }
        if (tag.hasUUID("Target")) {
            this.entityData.set(TARGET_UUID, Optional.of(tag.getUUID("Target")));
        }
        this.entityData.set(DAMAGE, tag.getFloat("Damage"));
        this.entityData.set(SPEED, tag.getFloat("Speed"));
        this.entityData.set(LIFE, tag.getInt("Life"));
        this.entityData.set(EXPLOSION_RADIUS, tag.getFloat("ExplosionRadius"));
        this.moveType = MoveType.fromId(tag.getInt("MoveType"));
        this.velX = tag.getDouble("VelX");
        this.velY = tag.getDouble("VelY");
        this.velZ = tag.getDouble("VelZ");
        this.accY1 = tag.getDouble("AccY1");
        this.accY2 = tag.getDouble("AccY2");
        this.arcTick = tag.getInt("ArcTick");
        this.arcSwitchTick = tag.getInt("ArcSwitch");
        this.torpedoStarted = tag.getBoolean("TorpedoStarted");
        this.torpedoDelay = tag.getInt("TorpedoDelay");
        this.clusterMain = tag.getBoolean("ClusterMain");
        this.clusterSub = tag.getBoolean("ClusterSub");
        this.blackHole = tag.getBoolean("BlackHole");
        this.impactEffects.clear();
        if (tag.contains(TAG_IMPACT_EFFECTS, Tag.TAG_LIST)) {
            ListTag listTag = tag.getList(TAG_IMPACT_EFFECTS, Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                ImpactEffectData effectData = ImpactEffectData.fromTag(listTag.getCompound(i));
                if (effectData != null) {
                    this.impactEffects.add(effectData);
                }
            }
        }
        if (tag.contains("TargetX")) {
            this.targetPos = new Vec3(tag.getDouble("TargetX"), tag.getDouble("TargetY"), tag.getDouble("TargetZ"));
        }
        this.setDeltaMovement(this.velX, this.velY, this.velZ);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        getOwnerUuid().ifPresent(uuid -> tag.putUUID("Owner", uuid));
        getTargetUuid().ifPresent(uuid -> tag.putUUID("Target", uuid));
        tag.putFloat("Damage", getDamage());
        tag.putFloat("Speed", getSpeed());
        tag.putInt("Life", getLife());
        tag.putFloat("ExplosionRadius", getExplosionRadius());
        tag.putInt("MoveType", this.moveType.ordinal());
        tag.putDouble("VelX", this.velX);
        tag.putDouble("VelY", this.velY);
        tag.putDouble("VelZ", this.velZ);
        tag.putDouble("AccY1", this.accY1);
        tag.putDouble("AccY2", this.accY2);
        tag.putInt("ArcTick", this.arcTick);
        tag.putInt("ArcSwitch", this.arcSwitchTick);
        tag.putBoolean("TorpedoStarted", this.torpedoStarted);
        tag.putInt("TorpedoDelay", this.torpedoDelay);
        tag.putBoolean("ClusterMain", this.clusterMain);
        tag.putBoolean("ClusterSub", this.clusterSub);
        tag.putBoolean("BlackHole", this.blackHole);
        if (!this.impactEffects.isEmpty()) {
            ListTag listTag = new ListTag();
            for (ImpactEffectData effectData : this.impactEffects) {
                listTag.add(effectData.toTag());
            }
            tag.put(TAG_IMPACT_EFFECTS, listTag);
        }
        if (this.targetPos != null) {
            tag.putDouble("TargetX", this.targetPos.x);
            tag.putDouble("TargetY", this.targetPos.y);
            tag.putDouble("TargetZ", this.targetPos.z);
        }
    }

    @Override
    public void tick() {
        super.tick();
        boolean tracing = PerformanceTrace.enabled() && !this.level().isClientSide;
        long startNanos = tracing ? PerformanceTrace.now() : 0L;
        try {
            if (!this.level().isClientSide) {
                this.age++;
                if (this.age > getLife()) {
                    onImpact(null);
                    return;
                }
                tickClusterSplit();
            }

            updateVelocityByMoveType();
            Vec3 delta = new Vec3(this.velX, this.velY, this.velZ);
            this.setDeltaMovement(delta);
            Vec3 start = this.position();
            Vec3 end = start.add(delta);

            BlockHitResult blockHit = this.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (blockHit.getType() != HitResult.Type.MISS) {
                end = blockHit.getLocation();
            }

            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(this.level(), this, start, end,
                    this.getBoundingBox().expandTowards(delta).inflate(1.0D), this::canHitEntity);
            if (entityHit != null) {
                onImpact(entityHit.getEntity());
                return;
            }

            if (blockHit.getType() == HitResult.Type.BLOCK) {
                onImpact(null);
                return;
            }

            move(MoverType.SELF, delta);
            updateRotationFromMovement(delta);
        } finally {
            if (tracing) {
                long elapsed = PerformanceTrace.elapsed(startNanos);
                PerformanceTrace.addProjectileTime(elapsed);
                PerformanceTrace.logSlowProjectileTick(this, "abyss_missile", elapsed,
                        "age=" + this.age
                                + " life=" + getLife()
                                + " moveType=" + this.moveType
                                + " clusterMain=" + this.clusterMain
                                + " clusterSub=" + this.clusterSub
                                + " blackHole=" + this.blackHole
                                + " effects=" + this.impactEffects.size());
            }
        }
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeEnum(this.moveType);
        buffer.writeDouble(this.velX);
        buffer.writeDouble(this.velY);
        buffer.writeDouble(this.velZ);
        buffer.writeDouble(this.accY1);
        buffer.writeDouble(this.accY2);
        buffer.writeInt(this.arcSwitchTick);
        buffer.writeBoolean(this.clusterMain);
        buffer.writeBoolean(this.clusterSub);
        buffer.writeBoolean(this.blackHole);
        buffer.writeInt(this.impactEffects.size());
        for (ImpactEffectData effectData : this.impactEffects) {
            effectData.write(buffer);
        }

        buffer.writeBoolean(this.targetPos != null);
        if (this.targetPos != null) {
            buffer.writeDouble(this.targetPos.x);
            buffer.writeDouble(this.targetPos.y);
            buffer.writeDouble(this.targetPos.z);
        }
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        this.moveType = buffer.readEnum(MoveType.class);
        this.velX = buffer.readDouble();
        this.velY = buffer.readDouble();
        this.velZ = buffer.readDouble();
        this.accY1 = buffer.readDouble();
        this.accY2 = buffer.readDouble();
        this.arcSwitchTick = buffer.readInt();
        this.clusterMain = buffer.readBoolean();
        this.clusterSub = buffer.readBoolean();
        this.blackHole = buffer.readBoolean();
        this.impactEffects.clear();
        int impactEffectCount = buffer.readInt();
        for (int i = 0; i < impactEffectCount; i++) {
            ImpactEffectData effectData = ImpactEffectData.read(buffer);
            if (effectData != null) {
                this.impactEffects.add(effectData);
            }
        }

        if (buffer.readBoolean()) {
            this.targetPos = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }

        this.setDeltaMovement(this.velX, this.velY, this.velZ);
    }

    private void updateHomingMovement() {
        Entity target = getTargetEntity();

        Vec3 aim = getAimVector(target);
        if (aim.lengthSqr() < 1.0E-6D) {
            return;
        }
        Vec3 desired = aim.scale(getSpeed());
        Vec3 current = new Vec3(this.velX, this.velY, this.velZ);
        Vec3 blended = current.scale(0.8D).add(desired.scale(0.2D));
        this.velX = blended.x;
        this.velY = blended.y;
        this.velZ = blended.z;
    }

    private void updateRotationFromMovement(Vec3 delta) {
        if (delta.lengthSqr() < 1.0E-5D) {
            return;
        }

        double d0 = delta.horizontalDistance();
        
        float yaw = (float) (Mth.atan2(delta.x, delta.z) * (180.0F / Math.PI));
        
        if (delta.x > 0) {
            yaw -= 180.0F;
        } else {
            yaw += 180.0F;
        }

        float pitch = (float) (Mth.atan2(delta.y, d0) * (180.0F / Math.PI));

        if (this.moveType == MoveType.TORPEDO && !this.torpedoStarted) {
            pitch = 0.0F;
        }

        if (this.tickCount <= 2 || this.age <= 2) {
            this.setYRot(yaw);
            this.setXRot(pitch);
            this.yRotO = yaw;
            this.xRotO = pitch;
        } else {
            this.setYRot(Mth.rotLerp(1.0F, this.yRotO, yaw));
            this.setXRot(Mth.rotLerp(1.0F, this.xRotO, pitch));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
    }

    private void initializeMovement(MoveType moveType, float vel0, float accY1, float accY2, Vec3 presetVelocity) {
        this.moveType = moveType == null ? MoveType.DIRECT : moveType;
        this.accY1 = accY1;
        this.accY2 = accY2;

        Vec3 targetVector = resolveTargetVector();
        if (this.moveType == MoveType.PRESET_VELOCITY && presetVelocity != null) {
            this.velX = presetVelocity.x;
            this.velY = presetVelocity.y;
            this.velZ = presetVelocity.z;
            return;
        }

        if (targetVector == null) {
            Vec3 fallback = this.getLookAngle().scale(vel0);
            this.velX = fallback.x;
            this.velY = fallback.y;
            this.velZ = fallback.z;
            return;
        }

        switch (this.moveType) {
            case DIRECT -> setDirectMovement(targetVector, vel0);
            case ARC -> initializeArcMovement(targetVector, vel0);
            case TORPEDO -> initializeTorpedoMovement(targetVector, vel0);
            case ARC_HOMING -> {
                setDirectMovement(targetVector, vel0);
                this.accY1 = -Math.abs(this.accY1) * 0.035D;
                this.accY2 = -Math.abs(this.accY2) * 0.035D;
            }
            case PRESET_VELOCITY -> setDirectMovement(targetVector, vel0);
        }
    }

    private Vec3 resolveTargetVector() {
        Vec3 targetVector = null;
        if (this.targetPos != null) {
            targetVector = this.targetPos.subtract(this.position());
        } else {
            Entity target = getTargetEntity();
            if (target != null) {
                targetVector = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D).subtract(this.position());
            }
        }
        return targetVector;
    }

    private void setDirectMovement(Vec3 targetVector, float velocity) {
        Vec3 dir = targetVector.normalize();
        this.velX = dir.x * velocity;
        this.velY = dir.y * velocity;
        this.velZ = dir.z * velocity;
    }

    private void initializeArcMovement(Vec3 targetVector, float initialVelocity) {
        Vec3 to = targetVector;
        double dx = to.x;
        double dz = to.z;
        double dxz = Math.sqrt(dx * dx + dz * dz);
        if (dxz <= MIN_DIST_FOR_ARC) {
            setDirectMovement(to, initialVelocity);
            this.moveType = MoveType.DIRECT;
            return;
        }
        double t = dxz / initialVelocity;
        double addHeight = to.length() * ARC_FACTOR_DEFAULT;
        double dy = Math.abs(to.y);

        double nx = dx / dxz;
        double nz = dz / dxz;

        this.velX = nx * initialVelocity;
        this.velZ = nz * initialVelocity;

        double t0;
        double t1;
        if (to.y < 1.0) {
            double hy = Math.sqrt(addHeight / (addHeight + dy));
            t0 = Math.floor(t / (1.0 + hy));
            t1 = Math.floor(t * hy / (1.0 + hy));
            this.velY = 2.0 * (addHeight + dy) / t0;
            this.accY1 = -this.velY / t0;
            this.accY2 = -2.0 * addHeight / (t1 * t1);
        } else {
            double hy = Math.sqrt(addHeight / (addHeight + dy));
            t0 = Math.floor(t * hy / (1.0 + hy));
            t1 = Math.floor(t / (1.0 + hy));
            this.accY1 = -2.0 * addHeight / (t0 * t0);
            this.velY = -this.accY1 * t0;
            this.accY2 = -2.0 * (addHeight + dy) / (t1 * t1);
        }
        if (Math.abs(this.accY1) > ARC_ACCEL_LIMIT || Math.abs(this.accY2) > ARC_ACCEL_LIMIT) {
            setDirectMovement(to, initialVelocity);
            this.moveType = MoveType.DIRECT;
            return;
        }
        this.arcTick = 0;
        this.arcSwitchTick = (int) Math.max(1, t0);
    }

    private void initializeTorpedoMovement(Vec3 targetVector, float initialVelocity) {
        Vec3 dir = targetVector.normalize();
        this.velX = dir.x * initialVelocity * 0.6D;
        this.velY = 0.1D;
        this.velZ = dir.z * initialVelocity * 0.6D;
        this.accY1 = -Math.abs(this.accY1) * 0.035D;
        this.torpedoStarted = false;
        this.torpedoDelay = TORPEDO_START_DELAY;
    }

    private void updateVelocityByMoveType() {
        if (this.clusterSub) {
            this.velX *= 0.95D;
            this.velY += this.accY1;
            this.velZ *= 0.95D;
            return;
        }

        switch (this.moveType) {
            case DIRECT -> {

            }
            case ARC -> {
                if (this.arcTick <= this.arcSwitchTick) {
                    this.velY += this.accY1;
                } else {
                    this.velY += this.accY2;
                }
                this.arcTick++;
            }
            case ARC_HOMING -> {
                updateHomingMovement();
                this.velY += this.accY1;
            }
            case TORPEDO -> updateTorpedoMovement();
            case PRESET_VELOCITY -> {

            }
        }
    }

    private void updateTorpedoMovement() {
        if (!this.torpedoStarted) {
            this.velX *= TORPEDO_VEL_MULTIPLIER;
            this.velZ *= TORPEDO_VEL_MULTIPLIER;
            this.velY += this.accY1;
            if (this.isInWater()) {
                this.torpedoStarted = true;
                this.torpedoDelay = TORPEDO_START_DELAY;
            }
            return;
        }

        if (this.torpedoDelay > 0) {
            this.torpedoDelay--;
            return;
        }

        double accel = this.accY2 != 0.0 ? this.accY2 : TORPEDO_ACCEL_MULTIPLIER;
        this.velX *= accel;
        this.velY *= accel;
        this.velZ *= accel;
    }

    private void tickClusterSplit() {
        if (!this.clusterMain || this.level().isClientSide) {
            return;
        }

        if (this.age <= CLUSTER_SPLIT_START || this.age >= CLUSTER_SPLIT_END || (this.age % CLUSTER_SPLIT_INTERVAL) != 0) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) this.level();
        Vec3 sourceVelocity = new Vec3(this.velX, this.velY, this.velZ);
        Vec3 spawnPos = this.position().add(0.0D, -0.65D - Math.abs(this.velY), 0.0D);

        EntityAbyssMissile sub = new EntityAbyssMissile(serverLevel, getOwnerEntity(), getTargetEntity(), spawnPos,
                getDamage() * CLUSTER_SUB_DAMAGE_SCALE, MoveType.PRESET_VELOCITY,
                CLUSTER_SUB_SPEED, CLUSTER_SUB_VERTICAL_ACCEL, CLUSTER_SUB_VERTICAL_ACCEL,
                sourceVelocity, CLUSTER_SUB_LIFE, CLUSTER_SUB_EXPLOSION_RADIUS);
        sub.markClusterSub();
        serverLevel.addFreshEntity(sub);
    }

    private boolean canHitEntity(Entity entity) {
        Entity owner = getOwnerEntity();
        return entity.isPickable() && entity.isAlive() && entity != owner;
    }

    private void onImpact(Entity hit) {
        if (this.level().isClientSide) {
            this.discard();
            return;
        }
        ServerLevel serverLevel = (ServerLevel) this.level();
        spawnImpactParticles(serverLevel);
        this.playSound(ModSounds.SHIP_EXPLODE.get(), 0.7F,
                this.getRandom().nextFloat() * 0.12F + 0.98F);
        if (this.blackHole) {
            applyBlackHoleEffect(serverLevel);
        }
        applyExplosionDamage(serverLevel, hit);
        this.discard();
    }

    private void spawnImpactParticles(ServerLevel serverLevel) {
        double posX = this.getX();
        double posY = this.getY();
        double posZ = this.getZ();
        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, posX, posY + 1.0D, posZ,
                1, 0.0D, 0.0D, 0.0D, 0.0D);
        for (int i = 0; i < 24; ++i) {
            double ran1 = (this.random.nextFloat() * 6.0F) - 3.0F;
            double ran2 = (this.random.nextFloat() * 6.0F) - 3.0F;
            serverLevel.sendParticles(ParticleTypes.LAVA,
                    posX + ran1, posY + 1.0D, posZ + ran2,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private void applyExplosionDamage(ServerLevel serverLevel, Entity directHit) {
        float radius = getExplosionRadius();
        float damage = getDamage();
        Entity owner = getOwnerEntity();
        DamageSource source = owner instanceof net.minecraft.world.entity.LivingEntity livingOwner
                ? this.damageSources().mobAttack(livingOwner)
                : this.damageSources().generic();

        List<Entity> targets = serverLevel.getEntities(this, this.getBoundingBox().inflate(radius),
                entity -> entity.isAlive() && entity.isPickable() && !(entity instanceof EntityAbyssMissile) && !isFriendlyTarget(owner, entity));
        for (Entity entity : targets) {
            entity.hurt(source, damage);
            applyImpactEffects(entity);
        }
        if (directHit != null && directHit.isAlive() && !isFriendlyTarget(owner, directHit)) {
            if (!targets.contains(directHit)) {
                directHit.hurt(source, damage);
            }
            applyImpactEffects(directHit);
        }
    }

    private void applyImpactEffects(Entity entity) {
        if (this.impactEffects.isEmpty() || !(entity instanceof LivingEntity living)) {
            return;
        }

        for (ImpactEffectData effectData : this.impactEffects) {
            effectData.apply(this.random, living);
        }
    }

    private boolean isFriendlyTarget(Entity owner, Entity target) {
        if (owner == target) return true;

        UUID ownerId = resolveOwnerUuid(owner);
        if (ownerId == null) return false;

        UUID targetId = resolveOwnerUuid(target);
        return ownerId.equals(targetId);
    }

    private UUID resolveOwnerUuid(Entity entity) {
        if (entity instanceof Player player) {
            return player.getUUID();
        }
        if (entity instanceof EntityShipBase ship) {
            return ship.getOwnerUUID();
        }
        if (entity instanceof TamableAnimal tamable) {
            return tamable.getOwnerUUID();
        }
        if (entity instanceof EntityMountBase mount) {
            EntityShipBase host = mount.getHost();
            if (host != null) {
                return host.getOwnerUUID();
            }
            return mount.getHostUUID();
        }
        if (entity instanceof EntityAircraftBase aircraft) {
            return aircraft.getOwnerUUID();
        }
        return null;
    }

    private Vec3 getAimVector(Entity target) {
        Vec3 from = this.position();
        Vec3 to;

        if (target != null && target.isAlive()) {
            to = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
            this.targetPos = to;
        } else if (this.targetPos != null) {
            to = this.targetPos;
        } else {
            return Vec3.ZERO;
        }

        Vec3 dir = to.subtract(from);
        if (dir.lengthSqr() < 1.0E-6D) {
            return Vec3.ZERO;
        }
        return dir.normalize();
    }

    public void setOwner(Entity owner) {
        this.entityData.set(OWNER_UUID, Optional.of(owner.getUUID()));
    }

    public Entity getOwnerEntity() {
        Optional<UUID> ownerUuid = getOwnerUuid();
        if (ownerUuid.isEmpty() || !(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        return serverLevel.getEntity(ownerUuid.get());
    }

    public Optional<UUID> getOwnerUuid() {
        return this.entityData.get(OWNER_UUID);
    }

    public void setTarget(Entity target) {
        this.entityData.set(TARGET_UUID, Optional.of(target.getUUID()));
    }

    public Entity getTargetEntity() {
        Optional<UUID> targetUuid = getTargetUuid();
        if (targetUuid.isEmpty() || !(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        return serverLevel.getEntity(targetUuid.get());
    }

    public Optional<UUID> getTargetUuid() {
        return this.entityData.get(TARGET_UUID);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setSpeed(float speed) {
        this.entityData.set(SPEED, speed);
    }

    public float getSpeed() {
        return this.entityData.get(SPEED);
    }

    public void setLife(int life) {
        this.entityData.set(LIFE, life);
    }

    public int getLife() {
        return this.entityData.get(LIFE);
    }

    public void setExplosionRadius(float radius) {
        this.entityData.set(EXPLOSION_RADIUS, radius);
    }

    public float getExplosionRadius() {
        return this.entityData.get(EXPLOSION_RADIUS);
    }

    public void markClusterMain() {
        this.clusterMain = true;
        this.clusterSub = false;
    }

    public void markClusterSub() {
        this.clusterMain = false;
        this.clusterSub = true;
    }

    public void markBlackHole() {
        this.blackHole = true;
    }

    public void addImpactEffect(Holder<net.minecraft.world.effect.MobEffect> effect, int amplifier, int duration, int chance) {
        if (effect == null || duration <= 0 || chance <= 0) {
            return;
        }
        this.impactEffects.add(new ImpactEffectData(effect, amplifier, duration, chance));
    }

    private void applyBlackHoleEffect(ServerLevel serverLevel) {
        Vec3 center = this.position();
        Entity owner = getOwnerEntity();
        List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(BLACK_HOLE_PULL_RADIUS),
                entity -> entity.isAlive() && !isFriendlyTarget(owner, entity));

        for (LivingEntity target : targets) {
            Vec3 pull = center.subtract(target.position()).normalize().scale(BLACK_HOLE_PULL_STRENGTH);
            target.setDeltaMovement(target.getDeltaMovement().add(pull.x, pull.y * 0.35D, pull.z));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1, false, true));
            target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20, 0, false, true));
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide|| this.isRemoved()) {
            return false;
        }
        onImpact(null);
        return true;
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunction) {
        moveFunction.accept(passenger, this.getX(), this.getY(), this.getZ());
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    private record ImpactEffectData(Holder<net.minecraft.world.effect.MobEffect> effect, int amplifier, int duration, int chance) {
        private CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();
            tag.putString(TAG_EFFECT_ID, BuiltInRegistries.MOB_EFFECT.getKey(this.effect.value()).toString());
            tag.putInt(TAG_EFFECT_LEVEL, this.amplifier);
            tag.putInt(TAG_EFFECT_DURATION, this.duration);
            tag.putInt(TAG_EFFECT_CHANCE, this.chance);
            return tag;
        }

        private static ImpactEffectData fromTag(CompoundTag tag) {
            net.minecraft.resources.ResourceLocation effectId = net.minecraft.resources.ResourceLocation.tryParse(tag.getString(TAG_EFFECT_ID));
            net.minecraft.world.effect.MobEffect effect = effectId == null ? null : BuiltInRegistries.MOB_EFFECT.get(effectId);
            if (effect == null) {
                return null;
            }
            return new ImpactEffectData(
                    Holder.direct(effect),
                    tag.getInt(TAG_EFFECT_LEVEL),
                    tag.getInt(TAG_EFFECT_DURATION),
                    tag.getInt(TAG_EFFECT_CHANCE)
            );
        }

        private void write(RegistryFriendlyByteBuf buffer) {
            buffer.writeResourceLocation(BuiltInRegistries.MOB_EFFECT.getKey(this.effect.value()));
            buffer.writeVarInt(this.amplifier);
            buffer.writeVarInt(this.duration);
            buffer.writeVarInt(this.chance);
        }

        private static ImpactEffectData read(RegistryFriendlyByteBuf buffer) {
            net.minecraft.world.effect.MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(buffer.readResourceLocation());
            int amplifier = buffer.readVarInt();
            int duration = buffer.readVarInt();
            int chance = buffer.readVarInt();
            if (effect == null) {
                return null;
            }
            return new ImpactEffectData(Holder.direct(effect), amplifier, duration, chance);
        }

        private void apply(net.minecraft.util.RandomSource random, LivingEntity target) {
            if (random.nextInt(100) >= this.chance) {
                return;
            }
            target.addEffect(new MobEffectInstance(this.effect, this.duration, this.amplifier, false, true));
        }
    }
}
