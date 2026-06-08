package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.init.ModEntities

/**
 * 静态特效投射物，当前实现为黑洞效果。
 * 持续吸引附近实体向中心移动。
 */
class EntityProjectileStatic(type: EntityType<out EntityProjectileStatic?>, level: Level) : Entity(type, level) {

    private var age = 0

    init {
        this.noPhysics = true
        this.setNoGravity(true)
    }

    constructor(level: Level) : this(ModEntities.PROJECTILE_STATIC.get(), level)

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define<Int?>(DATA_LIFE_LENGTH, 100)
        builder.define<Float?>(DATA_PULL_STRENGTH, 0.08f)
        builder.define<Float?>(DATA_RANGE, 6.0f)
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putInt("LifeLength", this.lifeLength)
        compound.putFloat("PullStrength", this.pullStrength)
        compound.putFloat("Range", this.range)
        compound.putInt("Age", this.age)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        this.lifeLength = compound.getInt("LifeLength")
        this.pullStrength = compound.getFloat("PullStrength")
        this.range = compound.getFloat("Range")
        this.age = compound.getInt("Age")
    }

    override fun tick() {
        super.tick()

        if (this.level().isClientSide) {
            // Client: spawn particles
            if (this.tickCount == 1) {
                for (i in 0..7) {
                    this.level().addParticle(
                        ParticleTypes.END_ROD,
                        this.x, this.y, this.z,
                        (this.random.nextFloat() - 0.5) * 0.3,
                        (this.random.nextFloat() - 0.5) * 0.3,
                        (this.random.nextFloat() - 0.5) * 0.3
                    )
                }
            }
            if (this.tickCount % 4 == 0) {
                this.level().addParticle(
                    ParticleTypes.PORTAL,
                    this.x, this.y, this.z,
                    (this.random.nextFloat() - 0.5) * 0.1,
                    (this.random.nextFloat() - 0.5) * 0.1,
                    (this.random.nextFloat() - 0.5) * 0.1
                )
            }
        } else {
            // Server: pull entities
            this.age++
            if (this.age >= this.lifeLength) {
                this.discard()
                return
            }

            if (this.age % 4 == 0) {
                pullEntities()
            }
        }
    }

    private fun pullEntities() {
        val level = this.level()
        if (level !is ServerLevel) return

        val range = this.range
        val box = this.boundingBox.inflate(range.toDouble())
        val entities = level.getEntitiesOfClass(LivingEntity::class.java, box) { it != this }

        for (entity in entities) {
            val distSqr = entity.distanceToSqr(this)
            if (distSqr > 1.0) {
                val dir = Vec3(
                    this.x - entity.x,
                    this.y - entity.y + entity.bbHeight * 0.5,
                    this.z - entity.z
                ).normalize()
                val strength = this.pullStrength * (1.0 - distSqr / ((range + 1) * (range + 1)))
                entity.deltaMovement = entity.deltaMovement.add(
                    dir.x * strength,
                    dir.y * strength * 0.3,
                    dir.z * strength
                )
                entity.hasImpulse = true
            }
        }
    }

    var lifeLength: Int
        get() = this.entityData.get<Int?>(DATA_LIFE_LENGTH) ?: 100
        set(value) {
            this.entityData.set<Int?>(DATA_LIFE_LENGTH, value)
        }

    var pullStrength: Float
        get() = this.entityData.get<Float?>(DATA_PULL_STRENGTH) ?: 0.08f
        set(value) {
            this.entityData.set<Float?>(DATA_PULL_STRENGTH, value)
        }

    var range: Float
        get() = this.entityData.get<Float?>(DATA_RANGE) ?: 6.0f
        set(value) {
            this.entityData.set<Float?>(DATA_RANGE, value)
        }

    companion object {
        private val DATA_LIFE_LENGTH: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityProjectileStatic::class.java, EntityDataSerializers.INT)
        private val DATA_PULL_STRENGTH: EntityDataAccessor<Float?> =
            SynchedEntityData.defineId<Float?>(EntityProjectileStatic::class.java, EntityDataSerializers.FLOAT)
        private val DATA_RANGE: EntityDataAccessor<Float?> =
            SynchedEntityData.defineId<Float?>(EntityProjectileStatic::class.java, EntityDataSerializers.FLOAT)
    }
}
