package org.trp.shincolle.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.trp.shincolle.Config;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.init.ModEntities;

public class EntityShipFishingHook extends Entity {
    private static final EntityDataAccessor<Integer> HOST_ID = SynchedEntityData.defineId(EntityShipFishingHook.class, EntityDataSerializers.INT);
    private EntityShipBase host;

    public EntityShipFishingHook(EntityType<?> type, Level level) {
        super(type, level);
    }

    public EntityShipBase getHost() {
        if (this.host != null && (!this.host.isAlive() || this.host.isRemoved())) {
            this.host = null;
        }
        return this.host;
    }

    public EntityShipFishingHook(Level level, EntityShipBase host) {
        this(ModEntities.SHIP_FISHING_HOOK.get(), level);
        this.host = host;
        this.entityData.set(HOST_ID, host.getId());
        this.setPos(host.getX(), host.getY() + host.getEyeHeight(), host.getZ());
        host.setFishHook(this);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(HOST_ID, -1);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide && this.host == null) {
            int id = this.entityData.get(HOST_ID);
            if (id != -1) {
                Entity e = this.level().getEntity(id);
                if (e instanceof EntityShipBase ship && ship.isAlive() && !ship.isRemoved()) {
                    this.host = ship;
                    ship.setFishHook(this);
                }
            }
        }

        if (this.tickCount > Config.tickFishingMin + Config.tickFishingMax) {
            this.discard();
        }
        if (!this.level().isClientSide) {
            if (this.host == null || !this.host.isAlive()) {
                this.discard();
                return;
            }
            var rod = this.host.getHeldItemMainhandSlot();
            if (rod.isEmpty() || rod.getItem() != Items.FISHING_ROD) {
                rod = this.host.getHeldItemOffhandSlot();
            }
            if (rod.isEmpty() || rod.getItem() != Items.FISHING_ROD || this.distanceToSqr(this.host) > 1024.0) {
                this.discard();
                return;
            }
            if (this.tickCount == 4) {
                this.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
            }
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 0.9, 0.9));
            if (this.tickCount == 4 || ((this.tickCount & 0x3F) == 0 && this.random.nextFloat() < 0.35f)) {
                double x = this.getX();
                double y = this.getY() - 0.1D;
                double z = this.getZ();
                for (int i = 0; i < 14; i++) {
                    double ranY = (this.random.nextFloat() - 0.5F) * 0.25F;
                    double ranX = (this.random.nextFloat() - 0.5F) * 1.5F;
                    double ranZ = (this.random.nextFloat() - 0.5F) * 1.5F;
                    this.level().addParticle(ParticleTypes.BUBBLE, x + ranX, y + ranY, z + ranZ, 0.0D, 0.0D, 0.0D);
                    this.level().addParticle(ParticleTypes.FISHING, x + ranX, y + ranY, z + ranZ, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (this.host != null) {
            this.host.setFishHook(null);
        }
    }
}
