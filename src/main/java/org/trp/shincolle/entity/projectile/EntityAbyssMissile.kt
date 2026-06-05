package org.trp.shincolle.entity.projectile

import net.minecraft.core.Holder
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn
import org.trp.shincolle.entity.EntityAircraftBase
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.init.ModSounds
import org.trp.shincolle.utility.PerformanceTrace.addProjectileTime
import org.trp.shincolle.utility.PerformanceTrace.elapsed
import org.trp.shincolle.utility.PerformanceTrace.enabled
import org.trp.shincolle.utility.PerformanceTrace.logSlowProjectileTick
import org.trp.shincolle.utility.PerformanceTrace.now
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sqrt

class EntityAbyssMissile(type: EntityType<EntityAbyssMissile?>, level: Level) : Entity(type, level),
    IEntityWithComplexSpawn {
    enum class MoveType {
        DIRECT,
        ARC,
        TORPEDO,
        ARC_HOMING,
        PRESET_VELOCITY;

        companion object {
            fun fromId(id: Int): MoveType {
                if (id < 0 || id >= MoveType.entries.toTypedArray().length) {
                    return MoveType.DIRECT
                }
                return MoveType.entries[id]
            }
        }
    }

    private var age = 0
    private var moveType = MoveType.DIRECT
    private var velX = 0.0
    private var velY = 0.0
    private var velZ = 0.0
    private var accY1 = 0.0
    private var accY2 = 0.0
    private var arcTick = 0
    private var arcSwitchTick = 0
    private var torpedoStarted = false
    private var torpedoDelay = 0
    private var targetPos: Vec3? = null
    private var clusterMain = false
    private var clusterSub = false
    private var blackHole = false
    private val impactEffects: MutableList<ImpactEffectData> = ArrayList<ImpactEffectData>()

    init {
        this.noPhysics = true
    }

    constructor(
        level: Level,
        owner: Entity?,
        target: Entity?,
        damage: Float,
        speed: Float,
        life: Int,
        explosionRadius: Float
    ) : this(ModEntities.ABYSS_MISSILE.get(), level) {
        if (owner != null) {
            this.setOwner(owner)
            this.setPos(owner.getX(), owner.getY() + owner.getBbHeight() * 0.6, owner.getZ())
        }
        if (target != null) {
            this.setTarget(target)
            this.targetPos = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0)
        }
        this.damage = damage
        this.speed = speed
        this.life = life
        this.explosionRadius = explosionRadius
        initializeMovement(MoveType.DIRECT, speed, 1.04f, 1.04f, null)
    }

    constructor(
        level: Level, owner: Entity?, target: Entity?, damage: Float, moveType: MoveType?,
        vel0: Float, accY1: Float, accY2: Float, presetVelocity: Vec3?,
        life: Int, explosionRadius: Float
    ) : this(ModEntities.ABYSS_MISSILE.get(), level) {
        if (owner != null) {
            this.setOwner(owner)
            this.setPos(owner.getX(), owner.getY() + owner.getBbHeight() * 0.6, owner.getZ())
        }
        if (target != null) {
            this.setTarget(target)
            this.targetPos = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0)
        }
        this.damage = damage
        this.speed = vel0
        this.life = life
        this.explosionRadius = explosionRadius
        initializeMovement(moveType, vel0, accY1, accY2, presetVelocity)
    }

    constructor(
        level: Level, owner: Entity?, target: Entity?, targetPos: Vec3?, damage: Float, moveType: MoveType?,
        vel0: Float, accY1: Float, accY2: Float, presetVelocity: Vec3?,
        life: Int, explosionRadius: Float
    ) : this(ModEntities.ABYSS_MISSILE.get(), level) {
        if (owner != null) {
            this.setOwner(owner)
            this.setPos(owner.getX(), owner.getY() + owner.getBbHeight() * 0.6, owner.getZ())
        }
        if (target != null) {
            this.setTarget(target)
        }
        this.targetPos = targetPos
        this.damage = damage
        this.speed = vel0
        this.life = life
        this.explosionRadius = explosionRadius
        initializeMovement(moveType, vel0, accY1, accY2, presetVelocity)
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define<Optional<UUID?>?>(OWNER_UUID, Optional.empty<UUID?>())
        builder.define<Optional<UUID?>?>(TARGET_UUID, Optional.empty<UUID?>())
        builder.define<Float?>(DAMAGE, 6.0f)
        builder.define<Float?>(SPEED, 0.7f)
        builder.define<Int?>(LIFE, 200)
        builder.define<Float?>(EXPLOSION_RADIUS, 3.5f)
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        if (tag.hasUUID("Owner")) {
            this.entityData.set<Optional<UUID?>?>(OWNER_UUID, Optional.of<UUID?>(tag.getUUID("Owner")))
        }
        if (tag.hasUUID("Target")) {
            this.entityData.set<Optional<UUID?>?>(TARGET_UUID, Optional.of<UUID?>(tag.getUUID("Target")))
        }
        this.entityData.set<Float?>(DAMAGE, tag.getFloat("Damage"))
        this.entityData.set<Float?>(SPEED, tag.getFloat("Speed"))
        this.entityData.set<Int?>(LIFE, tag.getInt("Life"))
        this.entityData.set<Float?>(EXPLOSION_RADIUS, tag.getFloat("ExplosionRadius"))
        this.moveType = MoveType.fromId(tag.getInt("MoveType"))
        this.velX = tag.getDouble("VelX")
        this.velY = tag.getDouble("VelY")
        this.velZ = tag.getDouble("VelZ")
        this.accY1 = tag.getDouble("AccY1")
        this.accY2 = tag.getDouble("AccY2")
        this.arcTick = tag.getInt("ArcTick")
        this.arcSwitchTick = tag.getInt("ArcSwitch")
        this.torpedoStarted = tag.getBoolean("TorpedoStarted")
        this.torpedoDelay = tag.getInt("TorpedoDelay")
        this.clusterMain = tag.getBoolean("ClusterMain")
        this.clusterSub = tag.getBoolean("ClusterSub")
        this.blackHole = tag.getBoolean("BlackHole")
        this.impactEffects.clear()
        if (tag.contains(TAG_IMPACT_EFFECTS, Tag.TAG_LIST.toInt())) {
            val listTag = tag.getList(TAG_IMPACT_EFFECTS, Tag.TAG_COMPOUND.toInt())
            for (i in listTag.indices) {
                val effectData = ImpactEffectData.Companion.fromTag(listTag.getCompound(i))
                if (effectData != null) {
                    this.impactEffects.add(effectData)
                }
            }
        }
        if (tag.contains("TargetX")) {
            this.targetPos = Vec3(tag.getDouble("TargetX"), tag.getDouble("TargetY"), tag.getDouble("TargetZ"))
        }
        this.setDeltaMovement(this.velX, this.velY, this.velZ)
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        this.ownerUuid.ifPresent(Consumer { uuid: UUID? -> tag.putUUID("Owner", uuid) })
        this.targetUuid.ifPresent(Consumer { uuid: UUID? -> tag.putUUID("Target", uuid) })
        tag.putFloat("Damage", this.damage)
        tag.putFloat("Speed", this.speed)
        tag.putInt("Life", this.life)
        tag.putFloat("ExplosionRadius", this.explosionRadius)
        tag.putInt("MoveType", this.moveType.ordinal)
        tag.putDouble("VelX", this.velX)
        tag.putDouble("VelY", this.velY)
        tag.putDouble("VelZ", this.velZ)
        tag.putDouble("AccY1", this.accY1)
        tag.putDouble("AccY2", this.accY2)
        tag.putInt("ArcTick", this.arcTick)
        tag.putInt("ArcSwitch", this.arcSwitchTick)
        tag.putBoolean("TorpedoStarted", this.torpedoStarted)
        tag.putInt("TorpedoDelay", this.torpedoDelay)
        tag.putBoolean("ClusterMain", this.clusterMain)
        tag.putBoolean("ClusterSub", this.clusterSub)
        tag.putBoolean("BlackHole", this.blackHole)
        if (!this.impactEffects.isEmpty()) {
            val listTag = ListTag()
            for (effectData in this.impactEffects) {
                listTag.add(effectData.toTag())
            }
            tag.put(TAG_IMPACT_EFFECTS, listTag)
        }
        if (this.targetPos != null) {
            tag.putDouble("TargetX", this.targetPos!!.x)
            tag.putDouble("TargetY", this.targetPos!!.y)
            tag.putDouble("TargetZ", this.targetPos!!.z)
        }
    }

    override fun tick() {
        super.tick()
        val tracing = enabled() && !this.level().isClientSide
        val startNanos = if (tracing) now() else 0L
        try {
            if (!this.level().isClientSide) {
                this.age++
                if (this.age > this.life) {
                    onImpact(null)
                    return
                }
                tickClusterSplit()
            }

            updateVelocityByMoveType()
            val delta = Vec3(this.velX, this.velY, this.velZ)
            this.setDeltaMovement(delta)
            val start = this.position()
            var end = start.add(delta)

            val blockHit =
                this.level().clip(ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this))
            if (blockHit.getType() != HitResult.Type.MISS) {
                end = blockHit.getLocation()
            }

            val entityHit = ProjectileUtil.getEntityHitResult(
                this.level(),
                this,
                start,
                end,
                this.getBoundingBox().expandTowards(delta).inflate(1.0),
                Predicate { entity: Entity? -> this.canHitEntity(entity!!) })
            if (entityHit != null) {
                onImpact(entityHit.getEntity())
                return
            }

            if (blockHit.getType() == HitResult.Type.BLOCK) {
                onImpact(null)
                return
            }

            move(MoverType.SELF, delta)
            updateRotationFromMovement(delta)
        } finally {
            if (tracing) {
                val elapsed = elapsed(startNanos)
                addProjectileTime(elapsed)
                logSlowProjectileTick(
                    this, "abyss_missile", elapsed,
                    ("age=" + this.age
                            + " life=" + this.life
                            + " moveType=" + this.moveType
                            + " clusterMain=" + this.clusterMain
                            + " clusterSub=" + this.clusterSub
                            + " blackHole=" + this.blackHole
                            + " effects=" + this.impactEffects.size)
                )
            }
        }
    }

    override fun writeSpawnData(buffer: RegistryFriendlyByteBuf) {
        buffer.writeEnum(this.moveType)
        buffer.writeDouble(this.velX)
        buffer.writeDouble(this.velY)
        buffer.writeDouble(this.velZ)
        buffer.writeDouble(this.accY1)
        buffer.writeDouble(this.accY2)
        buffer.writeInt(this.arcSwitchTick)
        buffer.writeBoolean(this.clusterMain)
        buffer.writeBoolean(this.clusterSub)
        buffer.writeBoolean(this.blackHole)
        buffer.writeInt(this.impactEffects.size)
        for (effectData in this.impactEffects) {
            effectData.write(buffer)
        }

        buffer.writeBoolean(this.targetPos != null)
        if (this.targetPos != null) {
            buffer.writeDouble(this.targetPos!!.x)
            buffer.writeDouble(this.targetPos!!.y)
            buffer.writeDouble(this.targetPos!!.z)
        }
    }

    override fun readSpawnData(buffer: RegistryFriendlyByteBuf) {
        this.moveType = buffer.readEnum<MoveType>(MoveType::class.java)
        this.velX = buffer.readDouble()
        this.velY = buffer.readDouble()
        this.velZ = buffer.readDouble()
        this.accY1 = buffer.readDouble()
        this.accY2 = buffer.readDouble()
        this.arcSwitchTick = buffer.readInt()
        this.clusterMain = buffer.readBoolean()
        this.clusterSub = buffer.readBoolean()
        this.blackHole = buffer.readBoolean()
        this.impactEffects.clear()
        val impactEffectCount = buffer.readInt()
        for (i in 0..<impactEffectCount) {
            val effectData = ImpactEffectData.Companion.read(buffer)
            if (effectData != null) {
                this.impactEffects.add(effectData)
            }
        }

        if (buffer.readBoolean()) {
            this.targetPos = Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())
        }

        this.setDeltaMovement(this.velX, this.velY, this.velZ)
    }

    private fun updateHomingMovement() {
        val target = this.targetEntity

        val aim = getAimVector(target)
        if (aim.lengthSqr() < 1.0E-6) {
            return
        }
        val desired = aim.scale(this.speed.toDouble())
        val current = Vec3(this.velX, this.velY, this.velZ)
        val blended = current.scale(0.8).add(desired.scale(0.2))
        this.velX = blended.x
        this.velY = blended.y
        this.velZ = blended.z
    }

    private fun updateRotationFromMovement(delta: Vec3) {
        if (delta.lengthSqr() < 1.0E-5) {
            return
        }

        val d0 = delta.horizontalDistance()

        var yaw = (Mth.atan2(delta.x, delta.z) * (180.0f / Math.PI)).toFloat()

        if (delta.x > 0) {
            yaw -= 180.0f
        } else {
            yaw += 180.0f
        }

        var pitch = (Mth.atan2(delta.y, d0) * (180.0f / Math.PI)).toFloat()

        if (this.moveType == MoveType.TORPEDO && !this.torpedoStarted) {
            pitch = 0.0f
        }

        if (this.tickCount <= 2 || this.age <= 2) {
            this.setYRot(yaw)
            this.setXRot(pitch)
            this.yRotO = yaw
            this.xRotO = pitch
        } else {
            this.setYRot(Mth.rotLerp(1.0f, this.yRotO, yaw))
            this.setXRot(Mth.rotLerp(1.0f, this.xRotO, pitch))
            this.yRotO = this.getYRot()
            this.xRotO = this.getXRot()
        }
    }

    private fun initializeMovement(
        moveType: MoveType?,
        vel0: Float,
        accY1: Float,
        accY2: Float,
        presetVelocity: Vec3?
    ) {
        this.moveType = if (moveType == null) MoveType.DIRECT else moveType
        this.accY1 = accY1.toDouble()
        this.accY2 = accY2.toDouble()

        val targetVector = resolveTargetVector()
        if (this.moveType == MoveType.PRESET_VELOCITY && presetVelocity != null) {
            this.velX = presetVelocity.x
            this.velY = presetVelocity.y
            this.velZ = presetVelocity.z
            return
        }

        if (targetVector == null) {
            val fallback = this.getLookAngle().scale(vel0.toDouble())
            this.velX = fallback.x
            this.velY = fallback.y
            this.velZ = fallback.z
            return
        }

        when (this.moveType) {
            MoveType.DIRECT -> setDirectMovement(targetVector, vel0)
            MoveType.ARC -> initializeArcMovement(targetVector, vel0)
            MoveType.TORPEDO -> initializeTorpedoMovement(targetVector, vel0)
            MoveType.ARC_HOMING -> {
                setDirectMovement(targetVector, vel0)
                this.accY1 = -abs(this.accY1) * 0.035
                this.accY2 = -abs(this.accY2) * 0.035
            }

            MoveType.PRESET_VELOCITY -> setDirectMovement(targetVector, vel0)
        }
    }

    private fun resolveTargetVector(): Vec3? {
        var targetVector: Vec3? = null
        if (this.targetPos != null) {
            targetVector = this.targetPos!!.subtract(this.position())
        } else {
            val target = this.targetEntity
            if (target != null) {
                targetVector = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0).subtract(this.position())
            }
        }
        return targetVector
    }

    private fun setDirectMovement(targetVector: Vec3, velocity: Float) {
        val dir = targetVector.normalize()
        this.velX = dir.x * velocity
        this.velY = dir.y * velocity
        this.velZ = dir.z * velocity
    }

    private fun initializeArcMovement(targetVector: Vec3, initialVelocity: Float) {
        val to = targetVector
        val dx = to.x
        val dz = to.z
        val dxz = sqrt(dx * dx + dz * dz)
        if (dxz <= MIN_DIST_FOR_ARC) {
            setDirectMovement(to, initialVelocity)
            this.moveType = MoveType.DIRECT
            return
        }
        val t = dxz / initialVelocity
        val addHeight: Double = to.length() * ARC_FACTOR_DEFAULT
        val dy = abs(to.y)

        val nx = dx / dxz
        val nz = dz / dxz

        this.velX = nx * initialVelocity
        this.velZ = nz * initialVelocity

        val t0: Double
        val t1: Double
        if (to.y < 1.0) {
            val hy = sqrt(addHeight / (addHeight + dy))
            t0 = floor(t / (1.0 + hy))
            t1 = floor(t * hy / (1.0 + hy))
            this.velY = 2.0 * (addHeight + dy) / t0
            this.accY1 = -this.velY / t0
            this.accY2 = -2.0 * addHeight / (t1 * t1)
        } else {
            val hy = sqrt(addHeight / (addHeight + dy))
            t0 = floor(t * hy / (1.0 + hy))
            t1 = floor(t / (1.0 + hy))
            this.accY1 = -2.0 * addHeight / (t0 * t0)
            this.velY = -this.accY1 * t0
            this.accY2 = -2.0 * (addHeight + dy) / (t1 * t1)
        }
        if (abs(this.accY1) > ARC_ACCEL_LIMIT || abs(this.accY2) > ARC_ACCEL_LIMIT) {
            setDirectMovement(to, initialVelocity)
            this.moveType = MoveType.DIRECT
            return
        }
        this.arcTick = 0
        this.arcSwitchTick = max(1.0, t0).toInt()
    }

    private fun initializeTorpedoMovement(targetVector: Vec3, initialVelocity: Float) {
        val dir = targetVector.normalize()
        this.velX = dir.x * initialVelocity * 0.6
        this.velY = 0.1
        this.velZ = dir.z * initialVelocity * 0.6
        this.accY1 = -abs(this.accY1) * 0.035
        this.torpedoStarted = false
        this.torpedoDelay = TORPEDO_START_DELAY
    }

    private fun updateVelocityByMoveType() {
        if (this.clusterSub) {
            this.velX *= 0.95
            this.velY += this.accY1
            this.velZ *= 0.95
            return
        }

        when (this.moveType) {
            MoveType.DIRECT -> {
            }

            MoveType.ARC -> {
                if (this.arcTick <= this.arcSwitchTick) {
                    this.velY += this.accY1
                } else {
                    this.velY += this.accY2
                }
                this.arcTick++
            }

            MoveType.ARC_HOMING -> {
                updateHomingMovement()
                this.velY += this.accY1
            }

            MoveType.TORPEDO -> updateTorpedoMovement()
            MoveType.PRESET_VELOCITY -> {
            }
        }
    }

    private fun updateTorpedoMovement() {
        if (!this.torpedoStarted) {
            this.velX *= TORPEDO_VEL_MULTIPLIER
            this.velZ *= TORPEDO_VEL_MULTIPLIER
            this.velY += this.accY1
            if (this.isInWater()) {
                this.torpedoStarted = true
                this.torpedoDelay = TORPEDO_START_DELAY
            }
            return
        }

        if (this.torpedoDelay > 0) {
            this.torpedoDelay--
            return
        }

        val accel = if (this.accY2 != 0.0) this.accY2 else TORPEDO_ACCEL_MULTIPLIER
        this.velX *= accel
        this.velY *= accel
        this.velZ *= accel
    }

    private fun tickClusterSplit() {
        if (!this.clusterMain || this.level().isClientSide) {
            return
        }

        if (this.age <= CLUSTER_SPLIT_START || this.age >= CLUSTER_SPLIT_END || (this.age % CLUSTER_SPLIT_INTERVAL) != 0) {
            return
        }

        val serverLevel = this.level() as ServerLevel
        val sourceVelocity = Vec3(this.velX, this.velY, this.velZ)
        val spawnPos = this.position().add(0.0, -0.65 - abs(this.velY), 0.0)

        val sub = EntityAbyssMissile(
            serverLevel,
            this.ownerEntity,
            this.targetEntity, spawnPos,
            this.damage * CLUSTER_SUB_DAMAGE_SCALE, MoveType.PRESET_VELOCITY,
            CLUSTER_SUB_SPEED, CLUSTER_SUB_VERTICAL_ACCEL, CLUSTER_SUB_VERTICAL_ACCEL,
            sourceVelocity, CLUSTER_SUB_LIFE, CLUSTER_SUB_EXPLOSION_RADIUS
        )
        sub.markClusterSub()
        serverLevel.addFreshEntity(sub)
    }

    private fun canHitEntity(entity: Entity): Boolean {
        val owner = this.ownerEntity
        return entity.isPickable() && entity.isAlive && entity !== owner
    }

    private fun onImpact(hit: Entity?) {
        if (this.level().isClientSide) {
            this.discard()
            return
        }
        val serverLevel = this.level() as ServerLevel
        spawnImpactParticles(serverLevel)
        this.playSound(
            ModSounds.SHIP_EXPLODE.get(), 0.7f,
            this.getRandom().nextFloat() * 0.12f + 0.98f
        )
        if (this.blackHole) {
            applyBlackHoleEffect(serverLevel)
        }
        applyExplosionDamage(serverLevel, hit)
        this.discard()
    }

    private fun spawnImpactParticles(serverLevel: ServerLevel) {
        val posX = this.getX()
        val posY = this.getY()
        val posZ = this.getZ()
        serverLevel.sendParticles<SimpleParticleType?>(
            ParticleTypes.EXPLOSION_EMITTER, posX, posY + 1.0, posZ,
            1, 0.0, 0.0, 0.0, 0.0
        )
        for (i in 0..23) {
            val ran1 = ((this.random.nextFloat() * 6.0f) - 3.0f).toDouble()
            val ran2 = ((this.random.nextFloat() * 6.0f) - 3.0f).toDouble()
            serverLevel.sendParticles<SimpleParticleType?>(
                ParticleTypes.LAVA,
                posX + ran1, posY + 1.0, posZ + ran2,
                1, 0.0, 0.0, 0.0, 0.0
            )
        }
    }

    private fun applyExplosionDamage(serverLevel: ServerLevel, directHit: Entity?) {
        val radius = this.explosionRadius
        val damage = this.damage
        val owner = this.ownerEntity
        val source = if (owner is LivingEntity)
            this.damageSources().mobAttack(owner)
        else
            this.damageSources().generic()

        val targets = serverLevel.getEntities(
            this, this.getBoundingBox().inflate(radius.toDouble()),
            Predicate { entity: Entity? ->
                entity!!.isAlive && entity.isPickable() && (entity !is EntityAbyssMissile) && !isFriendlyTarget(
                    owner,
                    entity
                )
            })
        for (entity in targets) {
            entity.hurt(source, damage)
            applyImpactEffects(entity)
        }
        if (directHit != null && directHit.isAlive && !isFriendlyTarget(owner, directHit)) {
            if (!targets.contains(directHit)) {
                directHit.hurt(source, damage)
            }
            applyImpactEffects(directHit)
        }
    }

    private fun applyImpactEffects(entity: Entity?) {
        if (this.impactEffects.isEmpty() || entity !is LivingEntity) {
            return
        }

        for (effectData in this.impactEffects) {
            effectData.apply(this.random, entity)
        }
    }

    private fun isFriendlyTarget(owner: Entity?, target: Entity?): Boolean {
        if (owner === target) return true

        val ownerId = resolveOwnerUuid(owner)
        if (ownerId == null) return false

        val targetId = resolveOwnerUuid(target)
        return ownerId == targetId
    }

    private fun resolveOwnerUuid(entity: Entity?): UUID? {
        if (entity is Player) {
            return entity.getUUID()
        }
        if (entity is EntityShipBase) {
            return entity.getOwnerUUID()
        }
        if (entity is TamableAnimal) {
            return entity.getOwnerUUID()
        }
        if (entity is EntityMountBase) {
            val host = entity.getHost()
            if (host != null) {
                return host.getOwnerUUID()
            }
            return entity.getHostUUID()
        }
        if (entity is EntityAircraftBase) {
            return entity.getOwnerUUID()
        }
        return null
    }

    private fun getAimVector(target: Entity?): Vec3 {
        val from = this.position()
        val to: Vec3?

        if (target != null && target.isAlive) {
            to = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0)
            this.targetPos = to
        } else if (this.targetPos != null) {
            to = this.targetPos
        } else {
            return Vec3.ZERO
        }

        val dir = to!!.subtract(from)
        if (dir.lengthSqr() < 1.0E-6) {
            return Vec3.ZERO
        }
        return dir.normalize()
    }

    fun setOwner(owner: Entity) {
        this.entityData.set<Optional<UUID?>?>(OWNER_UUID, Optional.of<UUID?>(owner.getUUID()))
    }

    val ownerEntity: Entity?
        get() {
            val ownerUuid = this.ownerUuid
            if (ownerUuid.isEmpty() || this.level() !is ServerLevel) {
                return null
            }
            val entity: Entity? = serverLevel.getEntity(ownerUuid.get())
            if (entity == null || !entity.isAlive || entity.isRemoved) {
                return null
            }
            return entity
        }

    val ownerUuid: Optional<UUID?>
        get() = this.entityData.get<Optional<UUID?>>(OWNER_UUID)

    fun setTarget(target: Entity) {
        this.entityData.set<Optional<UUID?>?>(TARGET_UUID, Optional.of<UUID?>(target.getUUID()))
    }

    val targetEntity: Entity?
        get() {
            val targetUuid = this.targetUuid
            if (targetUuid.isEmpty() || this.level() !is ServerLevel) {
                return null
            }
            val entity: Entity? = serverLevel.getEntity(targetUuid.get())
            if (entity == null || !entity.isAlive || entity.isRemoved) {
                return null
            }
            return entity
        }

    val targetUuid: Optional<UUID?>
        get() = this.entityData.get<Optional<UUID?>>(TARGET_UUID)

    var damage: Float
        get() = this.entityData.get<Float?>(DAMAGE)
        set(damage) {
            this.entityData.set<Float?>(DAMAGE, damage)
        }

    var speed: Float
        get() = this.entityData.get<Float?>(SPEED)
        set(speed) {
            this.entityData.set<Float?>(SPEED, speed)
        }

    var life: Int
        get() = this.entityData.get<Int?>(LIFE)
        set(life) {
            this.entityData.set<Int?>(LIFE, life)
        }

    var explosionRadius: Float
        get() = this.entityData.get<Float?>(EXPLOSION_RADIUS)
        set(radius) {
            this.entityData.set<Float?>(EXPLOSION_RADIUS, radius)
        }

    fun markClusterMain() {
        this.clusterMain = true
        this.clusterSub = false
    }

    fun markClusterSub() {
        this.clusterMain = false
        this.clusterSub = true
    }

    fun markBlackHole() {
        this.blackHole = true
    }

    fun addImpactEffect(effect: Holder<MobEffect?>?, amplifier: Int, duration: Int, chance: Int) {
        if (effect == null || duration <= 0 || chance <= 0) {
            return
        }
        this.impactEffects.add(ImpactEffectData(effect, amplifier, duration, chance))
    }

    private fun applyBlackHoleEffect(serverLevel: ServerLevel) {
        val center = this.position()
        val owner = this.ownerEntity
        val targets = serverLevel.getEntitiesOfClass<LivingEntity?>(
            LivingEntity::class.java,
            this.getBoundingBox().inflate(BLACK_HOLE_PULL_RADIUS),
            Predicate { entity: LivingEntity? -> entity!!.isAlive && !isFriendlyTarget(owner, entity) })

        for (target in targets) {
            val pull = center.subtract(target.position()).normalize().scale(BLACK_HOLE_PULL_STRENGTH)
            target.setDeltaMovement(target.getDeltaMovement().add(pull.x, pull.y * 0.35, pull.z))
            target.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1, false, true))
            target.addEffect(MobEffectInstance(MobEffects.LEVITATION, 20, 0, false, true))
        }
    }

    override fun isPickable(): Boolean {
        return true
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (this.level().isClientSide || this.isRemoved) {
            return false
        }
        onImpact(null)
        return true
    }

    override fun positionRider(passenger: Entity, moveFunction: MoveFunction) {
        moveFunction.accept(passenger, this.getX(), this.getY(), this.getZ())
    }

    override fun isNoGravity(): Boolean {
        return true
    }

    @JvmRecord
    private data class ImpactEffectData(
        val effect: Holder<MobEffect?>?,
        val amplifier: Int,
        val duration: Int,
        val chance: Int
    ) {
        fun toTag(): CompoundTag {
            val tag = CompoundTag()
            tag.putString(TAG_EFFECT_ID, BuiltInRegistries.MOB_EFFECT.getKey(this.effect!!.value()).toString())
            tag.putInt(TAG_EFFECT_LEVEL, this.amplifier)
            tag.putInt(TAG_EFFECT_DURATION, this.duration)
            tag.putInt(TAG_EFFECT_CHANCE, this.chance)
            return tag
        }

        fun write(buffer: RegistryFriendlyByteBuf) {
            buffer.writeResourceLocation(BuiltInRegistries.MOB_EFFECT.getKey(this.effect!!.value()))
            buffer.writeVarInt(this.amplifier)
            buffer.writeVarInt(this.duration)
            buffer.writeVarInt(this.chance)
        }

        fun apply(random: RandomSource, target: LivingEntity) {
            if (random.nextInt(100) >= this.chance) {
                return
            }
            target.addEffect(MobEffectInstance(this.effect, this.duration, this.amplifier, false, true))
        }

        companion object {
            private fun fromTag(tag: CompoundTag): ImpactEffectData? {
                val effectId = ResourceLocation.tryParse(tag.getString(TAG_EFFECT_ID))
                val effect = if (effectId == null) null else BuiltInRegistries.MOB_EFFECT.get(effectId)
                if (effect == null) {
                    return null
                }
                return ImpactEffectData(
                    Holder.direct<MobEffect?>(effect),
                    tag.getInt(TAG_EFFECT_LEVEL),
                    tag.getInt(TAG_EFFECT_DURATION),
                    tag.getInt(TAG_EFFECT_CHANCE)
                )
            }

            private fun read(buffer: RegistryFriendlyByteBuf): ImpactEffectData? {
                val effect = BuiltInRegistries.MOB_EFFECT.get(buffer.readResourceLocation())
                val amplifier = buffer.readVarInt()
                val duration = buffer.readVarInt()
                val chance = buffer.readVarInt()
                if (effect == null) {
                    return null
                }
                return ImpactEffectData(Holder.direct<MobEffect?>(effect), amplifier, duration, chance)
            }
        }
    }

    companion object {
        private const val MIN_DIST_FOR_ARC = 4.0
        private const val ARC_ACCEL_LIMIT = 0.15
        private const val TORPEDO_VEL_MULTIPLIER = 0.85
        private const val TORPEDO_ACCEL_MULTIPLIER = 1.05
        private const val TORPEDO_START_DELAY = 3
        private const val ARC_FACTOR_DEFAULT = 0.35f
        private const val CLUSTER_SPLIT_START = 6
        private const val CLUSTER_SPLIT_END = 40
        private const val CLUSTER_SPLIT_INTERVAL = 8
        private const val CLUSTER_SUB_LIFE = 140
        private const val CLUSTER_SUB_DAMAGE_SCALE = 0.5f
        private const val CLUSTER_SUB_EXPLOSION_RADIUS = 1.8f
        private const val CLUSTER_SUB_SPEED = 0.5f
        private val CLUSTER_SUB_VERTICAL_ACCEL = -0.06f
        private const val BLACK_HOLE_PULL_RADIUS = 7.5
        private const val BLACK_HOLE_PULL_STRENGTH = 0.12
        private const val TAG_IMPACT_EFFECTS = "ImpactEffects"
        private const val TAG_EFFECT_ID = "EffectId"
        private const val TAG_EFFECT_LEVEL = "Amplifier"
        private const val TAG_EFFECT_DURATION = "Duration"
        private const val TAG_EFFECT_CHANCE = "Chance"

        private val OWNER_UUID: EntityDataAccessor<Optional<UUID?>?> = SynchedEntityData.defineId<Optional<UUID?>?>(
            EntityAbyssMissile::class.java,
            EntityDataSerializers.OPTIONAL_UUID
        )
        private val TARGET_UUID: EntityDataAccessor<Optional<UUID?>?> = SynchedEntityData.defineId<Optional<UUID?>?>(
            EntityAbyssMissile::class.java,
            EntityDataSerializers.OPTIONAL_UUID
        )
        private val DAMAGE: EntityDataAccessor<Float?> =
            SynchedEntityData.defineId<Float?>(EntityAbyssMissile::class.java, EntityDataSerializers.FLOAT)
        private val SPEED: EntityDataAccessor<Float?> =
            SynchedEntityData.defineId<Float?>(EntityAbyssMissile::class.java, EntityDataSerializers.FLOAT)
        private val LIFE: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityAbyssMissile::class.java, EntityDataSerializers.INT)
        private val EXPLOSION_RADIUS: EntityDataAccessor<Float?> =
            SynchedEntityData.defineId<Float?>(EntityAbyssMissile::class.java, EntityDataSerializers.FLOAT)
    }
}
