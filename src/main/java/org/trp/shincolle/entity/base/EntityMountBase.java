package org.trp.shincolle.entity.base;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.command.ModCommands;
import org.trp.shincolle.entity.base.path.ShipLegacyNavigation;
import org.trp.shincolle.entity.base.path.ShipMoveControl;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class EntityMountBase extends PathfinderMob {

    private static final EntityDataAccessor<Optional<UUID>> HOST_UUID =
            SynchedEntityData.defineId(EntityMountBase.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> STATE_EMOTION =
            SynchedEntityData.defineId(EntityMountBase.class, EntityDataSerializers.INT);

    protected float[] seatPos  = {0.0f, 0.0f, 0.0f};
    protected float[] seatPos2 = {0.0f, 0.0f, 0.0f};
    protected double shipDepth = 0.0;
    public int keyPressed = 0;
    public int keyTick    = 0;

    public boolean isSubmarineMode = false;

    @Nullable
    protected EntityShipBase host;

    private int lightAttackCooldown = 0;
    private int heavyAttackCooldown = 0;
    private final ShipMovementCoordinator followMovement;

    protected EntityMountBase(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.PathType.WATER, 0.0F);
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.PathType.WATER_BORDER, 0.0F);
        this.followMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMMAND);
        this.moveControl = new ShipMoveControl(this, 30.0F);
        this.navigation = new ShipLegacyNavigation(this, level);
    }

    private void applyWaterBuoyancy() {
        double depth = this.shipDepth;
        double upward = 0.0;
        if (depth > MountAiNumbers.BUOY_MIN_DEPTH) {
            upward = MountAiNumbers.BUOY_COEFF * Math.pow(depth, MountAiNumbers.BUOY_EXPONENT) - MountAiNumbers.BUOY_OFFSET;
        }
        Vec3 dm = this.getDeltaMovement();
        double newY = (dm.y + upward) * MountAiNumbers.BUOY_DAMP;
        newY = Mth.clamp(newY, -MountAiNumbers.BUOY_MAX_MOTION, MountAiNumbers.BUOY_MAX_MOTION);
        this.setDeltaMovement(dm.x, newY, dm.z);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MountAiNumbers.BASE_MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MountAiNumbers.BASE_MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, MountAiNumbers.FOLLOW_RANGE_ATTR)
                .add(Attributes.KNOCKBACK_RESISTANCE, MountAiNumbers.BASE_KNOCKBACK_RESISTANCE)
                .add(Attributes.STEP_HEIGHT, MountAiNumbers.BASE_STEP_HEIGHT)
                .add(Attributes.ATTACK_DAMAGE, MountAiNumbers.BASE_ATTACK_DAMAGE);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HOST_UUID,     Optional.empty());
        builder.define(STATE_EMOTION, 0);
    }

    public void setHostUUID(@Nullable UUID uuid) {
        this.entityData.set(HOST_UUID, Optional.ofNullable(uuid));
    }

    @Nullable public UUID getHostUUID() {
        return this.entityData.get(HOST_UUID).orElse(null);
    }

    @Nullable public EntityShipBase getHost() {
        if (this.host != null && (!this.host.isAlive() || this.host.isRemoved())) {
            this.host = null;
        }
        if (this.host == null) {
            for (Entity p : this.getPassengers()) {
                if (p instanceof EntityShipBase ship && ship.isAlive() && !ship.isRemoved()) {
                    this.host = ship;
                    break;
                }
            }
        }
        return this.host;
    }

    public void setStateEmotion(int value) { this.entityData.set(STATE_EMOTION, value); }
    public int  getStateEmotion()          { return this.entityData.get(STATE_EMOTION); }
    public double getShipDepth()           { return this.shipDepth; }

    @Override
    public void tick() {
        if (!this.level().isClientSide && ModCommands.isStopShipAi()) {
            if (!checkHostExistence()) return;
            this.setDeltaMovement(Vec3.ZERO);
            this.setSpeed(0.0F);
            this.followMovement.stopAny();
            return;
        }

        double fluidH = this.getFluidHeight(net.minecraft.tags.FluidTags.WATER);
        this.shipDepth = fluidH;

        if (this.isSubmarineMode && fluidH <= 0.4) {
            this.isSubmarineMode = false;
        }
        if (this.isSubmarineMode && fluidH <= MountAiNumbers.SUBMARINE_DISABLE_DEPTH) {
            this.isSubmarineMode = false;
        }

        if (this.isInWater() && !this.isPassenger() && !this.isSubmarineMode) {
            if (this.isVehicle() || this.followMovement.isNavigationDone()) {
                applyWaterBuoyancy();
            }
        }

        super.tick();

        if (this.isOnFire()) {
            this.clearFire();
        }

        if ((this.tickCount & MountAiNumbers.AIR_SUPPLY_INTERVAL_MASK) == 0) this.setAirSupply(MountAiNumbers.STOP_SHIP_AI_AIR_SUPPLY);

        if (this.level().isClientSide) {
            updateClientLogic();
        } else {
            updateServerLogic();
        }

        handleMovement();
    }

    protected void updateClientLogic() {
        updateShipDepthClient();
        spawnMovingParticle();
    }

    private void updateShipDepthClient() {
        double fluidH = this.getFluidHeight(net.minecraft.tags.FluidTags.WATER);
        if (fluidH > 0.0) {
            this.shipDepth = fluidH;
        } else {
            this.shipDepth = 0.0;
        }
    }

    private void spawnMovingParticle() {
        if (this.shipDepth <= 0.0) return;
        double motX = this.getX() - this.xo;
        double motZ = this.getZ() - this.zo;
        double limit = 0.25;
        motX = Mth.clamp(motX, -limit, limit);
        motZ = Mth.clamp(motZ, -limit, limit);
        if (motX != 0.0 || motZ != 0.0) {
            double width = this.getBbWidth();
            int amount = 2 + this.random.nextInt(3);
            for (int i = 0; i < amount; i++) {
                double px = this.getX() + motX * 3.0 + (this.random.nextDouble() - 0.5) * width;
                double py = this.getY() + 0.6 + (this.random.nextDouble() - 0.5) * width * 0.15;
                double pz = this.getZ() + motZ * 3.0 + (this.random.nextDouble() - 0.5) * width;
                double vx = -motX * 1.5;
                double vz = -motZ * 1.5;
                this.level().addParticle(ParticleTypes.CLOUD, px, py, pz, vx, 0.0, vz);
            }
        }
    }

    protected void updateServerLogic() {
        if (!checkHostExistence()) return;

        if (lightAttackCooldown > 0) --lightAttackCooldown;
        if (heavyAttackCooldown > 0) --heavyAttackCooldown;

        if ((this.tickCount & MountAiNumbers.SERVER_SYNC_INTERVAL_MASK) == 0) {
            syncWithHost();
        }
    }

    protected boolean checkHostExistence() {
        UUID uuid = getHostUUID();
        if (uuid == null) { this.discard(); return false; }

        if (this.host == null || this.host.isRemoved()) {
            Entity entity = ((ServerLevel) this.level()).getEntity(uuid);
            if (entity instanceof EntityShipBase ship && ship.isAlive() && !ship.isRemoved()) {
                this.host = ship;
            } else {
                this.discard();
                return false;
            }
        }

        if (this.host.getVehicle() != this) {
            this.discard();
            return false;
        }
        return true;
    }

    protected void syncWithHost() {
        if (this.host == null) return;

        float hostMaxHP = this.host.getMaxHealth();
        this.getAttribute(Attributes.MAX_HEALTH)
                .setBaseValue(hostMaxHP * MountAiNumbers.HOST_MAX_HEALTH_SCALE);

        double hostSpeed = this.host.getAttributeValue(Attributes.MOVEMENT_SPEED);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(hostSpeed);

        float kr = this.host.getLegacyShipStats().getBuffedAttr(20);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(Mth.clamp(kr, 0.0f, 1.0f));

        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(MountAiNumbers.FOLLOW_RANGE_ATTR);
    }

    protected void handleMovement() {
        if (this.keyTick > 0) {
            --this.keyTick;
            LivingEntity rider = getControllingPassenger();
            if (rider != null) {
                this.setYRot(rider.getYRot());
                this.yRotO = rider.yRotO;
                this.setXRot(rider.getXRot());
                this.xRotO = rider.xRotO;
                this.yBodyRot = rider.yBodyRot;
                this.yBodyRotO = rider.yBodyRotO;
                this.yHeadRot = rider.getYHeadRot();
                this.yHeadRotO = rider.yHeadRotO;
            }
        } else if (Math.abs(this.getX() - this.xo) > MountAiNumbers.ROTATION_EPSILON
                || Math.abs(this.getZ() - this.zo) > MountAiNumbers.ROTATION_EPSILON) {
            handleAIMovementRotation();
        } else {
            syncRotationWithHost();
        }
    }

    protected void handleAIMovementRotation() {
        double dx = this.getX() - this.xo;
        double dz = this.getZ() - this.zo;
        float yaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
        this.setYRot(yaw);
        this.yRotO = yaw;
        this.yBodyRot = yaw;
        this.yHeadRot = yaw;
        syncHostToMount();
    }

    protected void syncRotationWithHost() {
        EntityShipBase h = getHost();
        if (h != null) {
            this.setYRot(h.getYRot());
            this.yRotO = h.yRotO;
            this.yBodyRot = h.yBodyRot;
            this.yBodyRotO = h.yBodyRotO;
            this.yHeadRot = h.getYHeadRot();
            this.yHeadRotO = h.yHeadRotO;
            this.setXRot(h.getXRot());
            this.xRotO = h.xRotO;
        }
    }

    protected void syncHostToMount() {
        EntityShipBase h = getHost();
        if (h != null) {
            h.setYRot(this.getYRot());
            h.yRotO = this.yRotO;
            h.yBodyRot = this.yBodyRot;
            h.yBodyRotO = this.yBodyRotO;
            h.yHeadRot = this.yHeadRot;
            h.yHeadRotO = this.yHeadRotO;
            h.setXRot(this.getXRot());
            h.xRotO = this.xRotO;
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.host != null && (this.host.isOrderedToSit() || this.host.isInSittingPose()) && getControllingPassenger() == null) {
            this.setDeltaMovement(Vec3.ZERO);
            this.setSpeed(0.0F);
            super.travel(Vec3.ZERO);
            return;
        }

        if (this.isAlive()) {
            LivingEntity rider = getControllingPassenger();
            if (rider != null) {
                this.setYRot(rider.getYRot());
                this.yRotO = this.getYRot();
                float visualPitch = rider.getXRot();
                this.yBodyRot = rider.yBodyRot;
                this.yHeadRot = rider.getYHeadRot();

                float strafe  = rider.xxa * 0.5F;
                float forward = rider.zza;
                if (forward <= 0.0F) forward *= 0.25F;

                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));

                if (rider.getXRot() > 60.0F && forward > 0.0F) {
                    this.isSubmarineMode = true;
                }

                if (this.isInWaterOrBubble() && (forward != 0 || strafe != 0)
                        && this.tickCount % 2 == 0
                        && this.level() instanceof ServerLevel sl) {
                    double motX = this.getX() - this.xo;
                    double motZ = this.getZ() - this.zo;
                    double limit = 0.25;
                    motX = Mth.clamp(motX, -limit, limit);
                    motZ = Mth.clamp(motZ, -limit, limit);
                    double width = this.getBbWidth();
                    int amount = 2 + this.random.nextInt(3);
                    for (int i = 0; i < amount; i++) {
                        double px = this.getX() + motX * 3.0 + (this.random.nextDouble() - 0.5) * width;
                        double py = this.getY() + 0.6 + (this.random.nextDouble() - 0.5) * width * 0.15;
                        double pz = this.getZ() + motZ * 3.0 + (this.random.nextDouble() - 0.5) * width;
                        double vx = -motX * 1.5;
                        double vz = -motZ * 1.5;
                        sl.sendParticles(ParticleTypes.CLOUD, px, py, pz, 0, vx, 0.0, vz, 1.0);
                    }
                }

                float travelPitch = 0.0F;
                if (rider.getXRot() > 60.0F || rider.getXRot() < -60.0F) {
                    travelPitch = visualPitch;
                }
                this.setXRot(travelPitch);
                this.setRot(this.getYRot(), this.getXRot());

                super.travel(new Vec3(strafe, travelVector.y, forward));

                if (this.isSubmarineMode) {
                    Vec3 currentMotion = this.getDeltaMovement();
                    
                    if (forward > 0.0F) {
                        double pitchRadians = Math.toRadians(rider.getXRot());
                        double speed = this.getAttributeValue(Attributes.MOVEMENT_SPEED);
                        double verticalSpeed = -Math.sin(pitchRadians) * speed;
                        
                        this.setDeltaMovement(currentMotion.x, verticalSpeed, currentMotion.z);
                    } else {
                        this.setDeltaMovement(currentMotion.x, 0.0, currentMotion.z);
                    }
                }
                
                this.setXRot(visualPitch);
                this.setRot(this.getYRot(), this.getXRot());
                this.calculateEntityAnimation(false);
                return;
            }
        }
        super.travel(travelVector);
    }

    @Nullable @Override
    public LivingEntity getControllingPassenger() {
        for (Entity p : this.getPassengers()) {
            if (p instanceof Player player) return player;
        }
        return null;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) {
        float[] pos = (passenger instanceof EntityShipBase) ? seatPos : seatPos2;
        float radians = this.yBodyRot * (float)(Math.PI / 180.0);
        float cosR = Mth.cos(radians);
        float sinR = Mth.sin(radians);
        float rz = pos[0] * cosR + pos[2] * sinR;
        float rx = pos[2] * cosR - pos[0] * sinR;
        return new Vec3(rx, pos[1], rz);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return true;
    }

    @Override
    public boolean shouldRiderFaceForward(Player player) { return true; }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (this.host != null && this.host.isOwnedBy(player) && ShipHostInteractionRouter.shouldForwardToHost(heldStack)) {
            InteractionResult hostInteractionResult = ShipHostInteractionRouter.forwardToHost(this.host, player, hand, heldStack);
            if (hostInteractionResult != InteractionResult.PASS) {
                return hostInteractionResult;
            }
        }

        if (this.level().isClientSide) return InteractionResult.SUCCESS;

        if (!player.isSecondaryUseActive()) {
            if (this.distanceToSqr(player) < MountAiNumbers.RIDER_INTERACT_DISTANCE_SQ) {
                player.startRiding(this, true);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide) return false;

        if (this.host == null) { this.discard(); return false; }

        if (source.is(DamageTypeTags.IS_FIRE)
                || source.is(DamageTypeTags.IS_FALL)
                || source.is(DamageTypes.IN_WALL)
                || source.is(DamageTypes.STARVE)
                || source.is(DamageTypes.CACTUS)
                || source.is(DamageTypeTags.IS_DROWNING)
                || source.is(DamageTypeTags.NO_ANGER)) {
            return false;
        }

        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.discard();
            return false;
        }

        Entity attacker = source.getEntity();
        if (attacker != null && attacker.equals(this)) {
            this.host.setOrderedToSit(false);
            return false;
        }

        float mountArmor = this.host.getLegacyShipStats().getArmor() * 0.5F;
        float reduced = amount * (1.0F - mountArmor + (this.random.nextFloat() * 0.5F - 0.25F));

        if (reduced > 0.0F && reduced < 1.0F) {
            reduced = 1.0F;
        } else if (reduced < 0.0F) {
            reduced = 0.0F;
        }

        if (reduced <= 0.0f) return false;

        this.host.setOrderedToSit(false);

        if (this.random.nextInt(5) == 0) {
            this.host.applyEmotesReaction(2);
        }

        boolean hurtResult = super.hurt(source, reduced);
        // Retaliate: if an external entity attacked us, notify the host
        if (hurtResult && reduced > 0.0F && attacker instanceof LivingEntity livingAttacker
                && livingAttacker != this.host && !livingAttacker.isAlliedTo(this.host)
                && this.host.getTarget() == null) {
            this.host.setTarget(livingAttacker);
        }
        return hurtResult;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        UUID uuid = getHostUUID();
        if (uuid != null) compound.putUUID("HostUUID", uuid);
        compound.putInt("StateEmotion", getStateEmotion());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("HostUUID")) setHostUUID(compound.getUUID("HostUUID"));
        setStateEmotion(compound.getInt("StateEmotion"));
    }

    @Override
    protected Brain.Provider<EntityMountBase> brainProvider() {
        return Brain.provider(EntityMountBrainAi.MEMORY_TYPES, EntityMountBrainAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return EntityMountBrainAi.makeBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @Override
    protected void customServerAiStep() {
        if (this.level() instanceof ServerLevel serverLevel) {
            EntityMountBrainAi.tick(serverLevel, this);
        }
        super.customServerAiStep();
    }

    ShipMovementCoordinator followMovementCoordinator() {
        return this.followMovement;
    }

    public float[] getSeatPos() { return this.seatPos; }

    public void setSeatPos(float x, float y, float z) {
        this.seatPos[0] = x; this.seatPos[1] = y; this.seatPos[2] = z;
    }

    public void setSeatPos2(float x, float y, float z) {
        this.seatPos2[0] = x; this.seatPos2[1] = y; this.seatPos2[2] = z;
    }
}
