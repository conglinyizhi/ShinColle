package org.trp.shincolle.entity.projectile

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.entity.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.entity.EntityAircraftBase
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.init.ModParticles
import org.trp.shincolle.utility.PerformanceTrace.addProjectileTime
import org.trp.shincolle.utility.PerformanceTrace.elapsed
import org.trp.shincolle.utility.PerformanceTrace.enabled
import org.trp.shincolle.utility.PerformanceTrace.logSlowProjectileTick
import org.trp.shincolle.utility.PerformanceTrace.now
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import kotlin.math.sqrt

class EntityProjectileBeam(type: EntityType<out EntityProjectileBeam?>, level: Level) : Entity(type, level) {
    private var age = 0
    private var accX = 0.0
    private var accY = 0.0
    private var accZ = 0.0
    private val damagedTargets: MutableSet<UUID?> = HashSet<UUID?>()

    init {
        this.noPhysics = true
        this.setNoGravity(true)
    }

    constructor(level: Level) : this(ModEntities.PROJECTILE_BEAM.get(), level)

    fun initAttrs(owner: Entity, type: Int, ax: Float, ay: Float, az: Float, damage: Float) {
        this.setOwner(owner)
        this.entityData.set<Int?>(OWNER_ID, owner.id)
        this.beamType = type
        this.damage = damage

        val speed: Float
        if (type == TYPE_SHORT) {
            this.setPos(owner.x, owner.y + owner.bbHeight * 0.75, owner.z)
            this.life = LIFE_SHORT
            speed = SPEED_SHORT
        } else {
            this.setPos(owner.x + ax, owner.y + owner.bbHeight * 0.5, owner.z + az)
            this.life = LIFE_LONG
            speed = SPEED_LONG
        }

        this.accX = (ax * speed).toDouble()
        this.accY = (ay * speed).toDouble()
        this.accZ = (az * speed).toDouble()
        this.setDeltaMovement(this.accX, this.accY, this.accZ)
        this.updateRotationFromMovement(this.deltaMovement)
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define<Optional<UUID>>(OWNER_UUID, Optional.empty())
        builder.define<Int?>(OWNER_ID, -1)
        builder.define<Float?>(DAMAGE, 6.0f)
        builder.define<Int?>(LIFE, LIFE_LONG)
        builder.define<Int?>(TYPE, 0)
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        if (tag.hasUUID("Owner")) {
            this.entityData.set<Optional<UUID>>(OWNER_UUID, Optional.of(tag.getUUID("Owner")))
        }
        if (tag.contains("OwnerId")) {
            this.entityData.set<Int?>(OWNER_ID, tag.getInt("OwnerId"))
        }
        this.entityData.set<Float?>(DAMAGE, tag.getFloat("Damage"))
        this.entityData.set<Int?>(LIFE, tag.getInt("Life"))
        this.entityData.set<Int?>(TYPE, tag.getInt("Type"))
        this.age = tag.getInt("Age")
        this.accX = tag.getDouble("AccX")
        this.accY = tag.getDouble("AccY")
        this.accZ = tag.getDouble("AccZ")
        this.setDeltaMovement(this.accX, this.accY, this.accZ)
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        this.ownerUuid.ifPresent(Consumer { uuid: UUID? -> tag.putUUID("Owner", uuid) })
        tag.putInt("OwnerId", this.entityData.get<Int?>(OWNER_ID))
        tag.putFloat("Damage", this.damage)
        tag.putInt("Life", this.life)
        tag.putInt("Type", this.beamType)
        tag.putInt("Age", this.age)
        tag.putDouble("AccX", this.accX)
        tag.putDouble("AccY", this.accY)
        tag.putDouble("AccZ", this.accZ)
    }

    override fun tick() {
        super.tick()
        if (this.level().isClientSide) {
            val delta = this.deltaMovement
            this.setPos(this.x + delta.x, this.y + delta.y, this.z + delta.z)

            this.age++
            if (this.age > this.life) {
                this.discard()
                return
            }

            if (this.age == 1) {
                val ownerId = this.entityData.get<Int?>(OWNER_ID)
                if (ownerId != -1) {
                    this.level().addParticle(
                        ModParticles.PARTICLE_BEAM.get(),
                        this.x, this.y, this.z,
                        ownerId.toDouble(), this.id.toDouble(), 2.0
                    )
                    this.level().addParticle(
                        ModParticles.PARTICLE_CUBE.get(),
                        this.x, this.y, this.z,
                        2.5, ownerId.toDouble(), 1.0
                    )
                }
            }

            val lifeLeft = (this.life - this.age).toDouble()
            if (lifeLeft > 0) {
                for (i in 0..3) {
                    this.level().addParticle(
                        ModParticles.PARTICLE_LIGHTNING.get(),
                        this.x, this.y, this.z,
                        lifeLeft, this.id.toDouble(), 6.0
                    )
                }
            }
            return
        }

        val tracing = enabled()
        val start = if (tracing) now() else 0L
        try {
            this.age++
            if (this.age > this.life) {
                this.discard()
                return
            }
            val owner = this.ownerEntity
            if (owner == null || !owner.isAlive) {
                this.discard()
                return
            }

            val delta = Vec3(this.accX, this.accY, this.accZ)
            this.deltaMovement = delta
            move(MoverType.SELF, delta)
            updateRotationFromMovement(delta)

            for (target in this.level().getEntities(
                this,
                this.boundingBox.inflate(1.5),
                Predicate { target: Entity? -> this.canHitEntity(target!!) })) {
                if (this.damagedTargets.add(target.uuid)) {
                    onImpact(target, owner)
                }
            }
        } finally {
            if (tracing) {
                val elapsed = elapsed(start)
                addProjectileTime(elapsed)
                logSlowProjectileTick(
                    this, "beam", elapsed,
                    ("age=" + this.age
                            + " life=" + this.life
                            + " type=" + this.beamType
                            + " damagedTargets=" + this.damagedTargets.size)
                )
            }
        }
    }

    private fun canHitEntity(target: Entity): Boolean {
        if (!target.isAlive || !target.isPickable()) {
            return false
        }
        val owner = this.ownerEntity
        if (owner == null || target === owner) {
            return false
        }
        if (isSameOwner(target)) {
            return false
        }
        return !owner.isAlliedTo(target)
    }

    private fun isSameOwner(target: Entity?): Boolean {
        val owner = this.ownerEntity
        val ownerId = resolveOwnerUuid(owner)
        if (ownerId == null) return false

        return ownerId == resolveOwnerUuid(target)
    }

    private fun resolveOwnerUuid(entity: Entity?): UUID? {
        if (entity is Player) {
            return entity.uuid
        }
        if (entity is EntityShipBase) {
            return entity.ownerUUID
        }
        if (entity is TamableAnimal) {
            return entity.ownerUUID
        }
        if (entity is EntityMountBase) {
            val host = entity.host
            if (host != null) {
                return host.ownerUUID
            }
            return entity.hostUUID
        }
        if (entity is EntityAircraftBase) {
            return entity.ownerUUID
        }
        return null
    }

    private fun onImpact(target: Entity, owner: Entity?) {
        val source = if (owner is LivingEntity)
            this.damageSources().mobAttack(owner)
        else
            this.damageSources().generic()
        target.hurt(source, this.damage)
    }

    private fun updateRotationFromMovement(delta: Vec3) {
        val dx = delta.x
        val dz = delta.z
        val dy = delta.y
        val yaw = (Mth.atan2(dz, dx) * (180.0f / Math.PI)).toFloat() - 90.0f
        val pitch = (-(Mth.atan2(dy, sqrt(dx * dx + dz * dz)) * (180.0f / Math.PI))).toFloat()
        this.yRot = yaw
        this.xRot = pitch
        this.yRotO = this.yRot
        this.xRotO = this.xRot
    }

    fun setOwner(owner: Entity) {
        this.entityData.set<Optional<UUID>>(OWNER_UUID, Optional.of(owner.uuid))
    }

    val ownerEntity: Entity?
        get() {
            val ownerUuid = this.ownerUuid
            if (ownerUuid.isEmpty() || this.level() !is ServerLevel) {
                return null
            }
            val serverLevel = this.level() as ServerLevel
            val entity: Entity? = serverLevel.getEntity(ownerUuid.get())
            if (entity == null || !entity.isAlive || entity.isRemoved) {
                return null
            }
            return entity
        }

    val ownerUuid: Optional<UUID>
        get() = this.entityData.get<Optional<UUID>>(OWNER_UUID)

    var damage: Float
        get() = this.entityData.get<Float?>(DAMAGE)
        set(damage) {
            this.entityData.set<Float?>(DAMAGE, damage)
        }

    var life: Int
        get() = this.entityData.get<Int?>(LIFE)
        set(life) {
            this.entityData.set<Int?>(LIFE, life)
        }

    var beamType: Int
        get() = this.entityData.get<Int?>(TYPE)
        set(type) {
            this.entityData.set<Int?>(TYPE, type)
        }

    override fun isPickable(): Boolean {
        return false
    }

    override fun isPushable(): Boolean {
        return false
    }

    override fun isNoGravity(): Boolean {
        return true
    }

    companion object {
        private val OWNER_UUID: EntityDataAccessor<Optional<UUID>> = SynchedEntityData.defineId<Optional<UUID>>(
            EntityProjectileBeam::class.java,
            EntityDataSerializers.OPTIONAL_UUID
        )
        private val OWNER_ID: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityProjectileBeam::class.java, EntityDataSerializers.INT)
        private val DAMAGE: EntityDataAccessor<Float?> =
            SynchedEntityData.defineId<Float?>(EntityProjectileBeam::class.java, EntityDataSerializers.FLOAT)
        private val LIFE: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityProjectileBeam::class.java, EntityDataSerializers.INT)
        private val TYPE: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityProjectileBeam::class.java, EntityDataSerializers.INT)

        private const val TYPE_SHORT = 1
        private const val LIFE_SHORT = 8
        private const val LIFE_LONG = 31
        private const val SPEED_SHORT = 3.0f
        private const val SPEED_LONG = 4.0f
    }
}
