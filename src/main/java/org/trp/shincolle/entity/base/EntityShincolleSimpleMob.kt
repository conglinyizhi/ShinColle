package org.trp.shincolle.entity.base

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import kotlin.math.max

open class EntityShincolleSimpleMob protected constructor(type: EntityType<out TamableAnimal?>, level: Level) :
    TamableAnimal(type, level) {
    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define<Int?>(SCALE_LEVEL, 0)
        builder.define<Int?>(ATTACK_TICK, 0)
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putInt("ScaleLevel", this.scaleLevel)
        compound.putInt("AttackTick", this.attackTick)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        this.scaleLevel = compound.getInt("ScaleLevel")
        this.attackTick = compound.getInt("AttackTick")
    }

    var scaleLevel: Int
        get() = this.entityData.get<Int?>(SCALE_LEVEL)
        set(level) {
            this.entityData.set<Int?>(SCALE_LEVEL, Mth.clamp(level, 0, 3))
        }

    var attackTick: Int
        get() = this.entityData.get<Int?>(ATTACK_TICK)
        set(tick) {
            this.entityData.set<Int?>(ATTACK_TICK, max(0, tick))
        }

    override fun getBreedOffspring(level: ServerLevel, otherParent: AgeableMob): AgeableMob? {
        return null
    }

    override fun isFood(stack: ItemStack): Boolean {
        return false
    }

    companion object {
        protected val SCALE_LEVEL: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShincolleSimpleMob::class.java, EntityDataSerializers.INT)
        protected val ATTACK_TICK: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShincolleSimpleMob::class.java, EntityDataSerializers.INT)

        fun createAttributes(): AttributeSupplier.Builder {
            return createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.STEP_HEIGHT, 1.0)
        }
    }
}
