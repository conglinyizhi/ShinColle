package org.trp.shincolle.entity.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class EntityShincolleSimpleMob extends TamableAnimal {

    protected static final EntityDataAccessor<Integer> SCALE_LEVEL = SynchedEntityData.defineId(EntityShincolleSimpleMob.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntityShincolleSimpleMob.class, EntityDataSerializers.INT);

    protected EntityShincolleSimpleMob(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.STEP_HEIGHT, 1.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SCALE_LEVEL, 0);
        builder.define(ATTACK_TICK, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ScaleLevel", this.getScaleLevel());
        compound.putInt("AttackTick", this.getAttackTick());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setScaleLevel(compound.getInt("ScaleLevel"));
        this.setAttackTick(compound.getInt("AttackTick"));
    }

    public int getScaleLevel() {
        return this.entityData.get(SCALE_LEVEL);
    }

    public void setScaleLevel(int level) {
        this.entityData.set(SCALE_LEVEL, Mth.clamp(level, 0, 3));
    }

    public int getAttackTick() {
        return this.entityData.get(ATTACK_TICK);
    }

    public void setAttackTick(int tick) {
        this.entityData.set(ATTACK_TICK, Math.max(0, tick));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }
}
