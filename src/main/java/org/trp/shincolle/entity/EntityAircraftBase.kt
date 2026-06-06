package org.trp.shincolle.entity

import com.mojang.serialization.Dynamic
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.Brain
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.FlyingMoveControl
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.Shincolle.Companion.debugLog
import org.trp.shincolle.entity.base.EntityShincolleSimpleMob
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.base.ShipMovementCoordinator
import org.trp.shincolle.entity.base.ShipMovementRecoveryState
import org.trp.shincolle.entity.projectile.EntityAbyssMissile
import org.trp.shincolle.entity.projectile.EntityAbyssMissile.MoveType
import org.trp.shincolle.init.ModParticles
import org.trp.shincolle.init.ModSounds
import org.trp.shincolle.utility.PerformanceTrace.addProjectileTime
import org.trp.shincolle.utility.PerformanceTrace.elapsed
import org.trp.shincolle.utility.PerformanceTrace.enabled
import org.trp.shincolle.utility.PerformanceTrace.logSlowProjectileTick
import org.trp.shincolle.utility.PerformanceTrace.now
import java.util.*
import java.util.function.Predicate
import kotlin.math.*

abstract class EntityAircraftBase protected constructor(type: EntityType<out TamableAnimal>, level: Level) :
    EntityShincolleSimpleMob(type, level) {
    private var carrierId: UUID? = null
    private var targetId: UUID? = null
    private var backHome = false
    var isMissionLightAircraft: Boolean = false
        private set
    var missionTick: Int = 0
        private set
    var attackDelay: Int = 0
        private set
    private var maxAttackDelay = 0
    private var numAmmoLight = 0
    private var numAmmoHeavy = 0

    private var deathAnimTick = 0
    private var isDying = false
    private var deadMotionX = 0.0
    private var deadMotionZ = 0.0

    private val randPos: DoubleArray
    private var attackRangeSq = 0f
    private val returnMovement: ShipMovementCoordinator
    private val attackMovement: ShipMovementCoordinator
    private val returnRecovery: ShipMovementRecoveryState
    private var returnHomeTicks = 0

    init {
        this.returnMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMMAND)
        this.attackMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMBAT)
        this.returnRecovery = ShipMovementRecoveryState()
        this.moveControl = FlyingMoveControl(this, 36, true)
        this.setNoGravity(true)
        this.randPos = DoubleArray(3)
    }

    override fun createNavigation(level: Level): PathNavigation {
        val navigation = FlyingPathNavigation(this, level)
        navigation.setCanOpenDoors(false)
        navigation.setCanFloat(true)
        navigation.setCanPassDoors(true)
        return navigation
    }

    fun initCarrierMission(carrier: EntityShipBase?, target: Entity?, lightAircraft: Boolean) {
        if (carrier == null) {
            return
        }
        this.carrierId = carrier.uuid
        this.targetId = if (target == null) null else target.uuid
        resumeMission()
        this.missionTick = 0
        this.isMissionLightAircraft = lightAircraft
        this.isDying = false
        this.deathAnimTick = 0

        if (lightAircraft) {
            this.numAmmoLight = AircraftAiNumbers.INITIAL_AMMO_LIGHT
            this.numAmmoHeavy = 0
        } else {
            this.numAmmoLight = 0
            this.numAmmoHeavy = AircraftAiNumbers.INITIAL_AMMO_HEAVY
        }

        val attackSpeed = carrier.legacyShipStats.reloadSpeed
        this.maxAttackDelay =
            (AircraftAiNumbers.BASE_ATTACK_SPEED_AIRCRAFT / attackSpeed).toInt() + AircraftAiNumbers.FIXED_ATTACK_DELAY_AIRCRAFT
        this.attackDelay = 0

        val range = if (lightAircraft) AircraftAiNumbers.ATTACK_RANGE_LIGHT else AircraftAiNumbers.ATTACK_RANGE_HEAVY
        this.attackRangeSq = range * range

        this.setNoGravity(true)
        this.setOwnerUUID(carrier.ownerUUID)
        this.setTame(true, false)

        if (target != null) {
            this.randPos[0] = target.x
            this.randPos[1] = target.y
            this.randPos[2] = target.z
        }
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        if (this.carrierId != null) {
            compound.putUUID("CarrierId", this.carrierId)
        }
        if (this.targetId != null) {
            compound.putUUID("TargetId", this.targetId)
        }
        compound.putBoolean("BackHome", this.backHome)
        compound.putBoolean("MissionLight", this.isMissionLightAircraft)
        compound.putInt("MissionTick", this.missionTick)
        compound.putInt("AttackDelay", this.attackDelay)
        compound.putInt("MaxAttackDelay", this.maxAttackDelay)
        compound.putInt("NumAmmoLight", this.numAmmoLight)
        compound.putInt("NumAmmoHeavy", this.numAmmoHeavy)
        compound.putFloat("AttackRangeSq", this.attackRangeSq)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        this.carrierId = if (compound.hasUUID("CarrierId")) compound.getUUID("CarrierId") else null
        this.targetId = if (compound.hasUUID("TargetId")) compound.getUUID("TargetId") else null
        this.backHome = compound.getBoolean("BackHome")
        this.isMissionLightAircraft = compound.getBoolean("MissionLight")
        this.missionTick = compound.getInt("MissionTick")
        this.attackDelay = compound.getInt("AttackDelay")
        this.maxAttackDelay = compound.getInt("MaxAttackDelay")
        this.numAmmoLight = compound.getInt("NumAmmoLight")
        this.numAmmoHeavy = compound.getInt("NumAmmoHeavy")
        this.attackRangeSq = compound.getFloat("AttackRangeSq")

        if (this.attackRangeSq <= 0.0f) {
            this.attackRangeSq =
                (if (this.isMissionLightAircraft) AircraftAiNumbers.ATTACK_RANGE_LIGHT else AircraftAiNumbers.ATTACK_RANGE_HEAVY)
            this.attackRangeSq *= this.attackRangeSq
        }
        if (this.maxAttackDelay <= 0) {
            this.maxAttackDelay =
                AircraftAiNumbers.FIXED_ATTACK_DELAY_AIRCRAFT + AircraftAiNumbers.BASE_ATTACK_SPEED_AIRCRAFT
        }
        if (this.attackDelay <= 0) {
            this.attackDelay = this.maxAttackDelay + AircraftAiNumbers.HOST_CHECK_TIMEOUT
        }
    }

    override fun aiStep() {
        super.aiStep()

        if (this.isDying) {
            if (!this.level().isClientSide) {
                tickDeathAnimation()
            }
            return
        }

        this.setNoGravity(true)
        this.fallDistance = 0.0f

        if (this.level().isClientSide) {
            applyFlyParticle()
        } else {
            updateServerLogic()
        }
        updateRotation()
    }

    private fun updateServerLogic() {
        val tracing = enabled()
        val start = if (tracing) now() else 0L
        try {
            this.missionTick++
            if (this.attackDelay > 0) {
                this.attackDelay--
            }

            val carrier = this.carrier
            if (carrier == null || !carrier.isAlive) {
                this.discard()
                return
            }

            if (this.backHome) {
                handleReturnToHome(carrier)
                return
            }

            handleInitialBoost()
            handleTargeting(carrier)
            checkMissionStatus()
        } finally {
            if (tracing) {
                val elapsed = elapsed(start)
                addProjectileTime(elapsed)
                logSlowProjectileTick(
                    this, "aircraft", elapsed,
                    ("missionTick=" + this.missionTick
                            + " backHome=" + this.backHome
                            + " light=" + this.isMissionLightAircraft
                            + " ammoLight=" + this.numAmmoLight
                            + " ammoHeavy=" + this.numAmmoHeavy
                            + " target=" + this.targetId
                            + " carrier=" + this.carrierId)
                )
            }
        }
    }

    private fun checkMissionStatus() {
        if (this.missionTick >= AircraftAiNumbers.LIFETIME_TICKS) {
            startReturnHome()
            return
        }

        if (this.isMissionLightAircraft && this.numAmmoLight <= 0) {
            startReturnHome()
            return
        }
        if (!this.isMissionLightAircraft && this.numAmmoHeavy <= 0) {
            startReturnHome()
        }
    }

    override fun travel(travelVector: Vec3) {
        if (this.isEffectiveAi() && (this.isNoGravity() || !this.isDying)) {
            this.moveRelative(this.getSpeed(), travelVector)
            this.move(MoverType.SELF, this.deltaMovement)

            this.deltaMovement = this.deltaMovement.scale(0.95)
        } else {
            super.travel(travelVector)
        }
    }

    private fun handleInitialBoost() {
        if (this.missionTick >= AircraftAiNumbers.INITIAL_BOOST_DURATION) {
            return
        }
        val target = this.missionTarget
        if (target == null) {
            return
        }
        val dx = target.x - this.x
        val dz = target.z - this.z
        val distSqrt = Mth.sqrt((dx * dx + dz * dz).toFloat()).toDouble()
        if (distSqrt > 1.0E-4) {
            this.setDeltaMovement(
                dx / distSqrt * AircraftAiNumbers.INITIAL_BOOST_SPEED,
                AircraftAiNumbers.INITIAL_BOOST_Y,
                dz / distSqrt * AircraftAiNumbers.INITIAL_BOOST_SPEED
            )
            this.hasImpulse = true
        }
    }

    private fun handleTargeting(carrier: EntityShipBase) {
        if (this.missionTick % AircraftAiNumbers.TARGETING_INTERVAL != 0) {
            return
        }
        if (this.missionTick < AircraftAiNumbers.HOST_CHECK_TIMEOUT) {
            return
        }

        val currentTarget = this.missionTarget
        val needsNewTarget = currentTarget == null || !currentTarget.isAlive || !isValidTarget(carrier, currentTarget)

        if (!needsNewTarget) {
            return
        }

        var newTarget = findNewTarget(carrier)

        if (newTarget == null) {
            val carrierTarget: Entity? = carrier.target
            if (carrierTarget != null && carrierTarget.isAlive && !isFriendlyTarget(carrier, carrierTarget)) {
                newTarget = carrierTarget
            }
        }

        if (newTarget != null) {
            this.targetId = newTarget.uuid
            resumeMission()
        } else {
            startReturnHome()
        }
    }

    private fun startReturnHome() {
        if (!this.backHome) {
            this.returnRecovery.clear()
            this.returnHomeTicks = 0
        }
        this.targetId = null
        this.backHome = true
    }

    private fun resumeMission() {
        this.backHome = false
        this.returnRecovery.clear()
        this.returnHomeTicks = 0
    }

    private fun handleReturnToHome(carrier: EntityShipBase) {
        if (!this.isAlive) return
        this.returnHomeTicks++

        val distSq = this.distanceToSqr(carrier)
        val arrivalDist = (2.0 + carrier.bbHeight).pow(2.0)

        if (distSq <= arrivalDist) {
            returnSummonResources(carrier)
            this.discard()
            return
        }

        val homePos =
            carrier.position().add(0.0, carrier.bbHeight + AircraftAiNumbers.RETURN_HOME_EXTRA_HEIGHT, 0.0)
        this.returnMovement.moveTo(homePos, AircraftAiNumbers.RETURN_HOME_SPEED)
        if (trackReturnHomeRecovery(carrier, distSq)) {
            return
        }
        if (this.returnHomeTicks > AircraftAiNumbers.RETURN_HOME_FAILSAFE_TICKS
            && this.returnRecovery.isStuckLongerThan(AircraftAiNumbers.RETURN_HOME_FAILSAFE_TICKS)
        ) {
            debugLog(
                "[SCMoveDiag] AircraftReturn failsafeDiscard aircraft={} carrier={} distanceSqr={} returnTicks={} stuckTicks={}",
                this.uuid, carrier.uuid, distSq, this.returnHomeTicks, this.returnRecovery.stuckTicks()
            )
            returnSummonResources(carrier)
            this.discard()
        }
    }

    private fun trackReturnHomeRecovery(carrier: EntityShipBase, distanceSqr: Double): Boolean {
        this.returnRecovery.trackProgress(this.position())
        val force = this.returnRecovery.isStuckLongerThan(AircraftAiNumbers.RETURN_HOME_STUCK_TICK_LIMIT)
        if (!force && (this.tickCount % AircraftAiNumbers.RETURN_HOME_CHECK_INTERVAL) != 0) {
            return false
        }
        if (!this.returnRecovery.shouldTryTeleportThrottled(
                force,
                distanceSqr,
                AircraftAiNumbers.RETURN_HOME_TELEPORT_DISTANCE_SQ,
                AircraftAiNumbers.RETURN_HOME_TELEPORT_COOLDOWN_TICKS
            )
        ) {
            return false
        }
        if (!this.returnMovement.teleportNearLiving(
                carrier,
                carrier.bbHeight + AircraftAiNumbers.RETURN_HOME_TELEPORT_EXTRA
            )
        ) {
            return false
        }

        debugLog(
            "[SCMoveDiag] AircraftReturn teleportRecovery aircraft={} carrier={} force={} distanceSqr={} stuckTicks={}",
            this.uuid, carrier.uuid, force, distanceSqr, this.returnRecovery.stuckTicks()
        )
        this.returnRecovery.reset(this.position())
        this.returnHomeTicks = 0
        return true
    }

    private fun returnSummonResources(carrier: EntityShipBase) {
        val returnLight = max(0, this.numAmmoLight - AircraftAiNumbers.AMMO_RETURN_PENALTY_LIGHT)
        val returnHeavy = max(0, this.numAmmoHeavy - AircraftAiNumbers.AMMO_RETURN_PENALTY_HEAVY)

        carrier.ammoLight += returnLight
        carrier.ammoHeavy += returnHeavy

        carrier.returnAircraftToDeck(this.isMissionLightAircraft)
    }

    fun attackWithLightAmmo(target: Entity) {
        val carrier = this.carrier
        if (carrier == null) return
        if (this.numAmmoLight > 0) {
            this.numAmmoLight--
        }
        this.attackDelay = this.maxAttackDelay
        this.playSound(ModSounds.SHIP_MACHINEGUN.get(), 1.0f, 1.0f)
        val atk = max(2.0f, carrier.legacyShipStats.firepower * 0.35f)
        target.hurt(this.damageSources().mobProjectile(this, carrier), atk)
    }

    fun attackWithHeavyAmmo(target: Entity) {
        val carrier = this.carrier
        if (carrier == null) return
        if (this.numAmmoHeavy > 0) {
            this.numAmmoHeavy--
        }
        this.attackDelay = this.maxAttackDelay
        val atk = max(4.0f, carrier.legacyShipStats.firepower * 0.55f)
        if (this.level() is ServerLevel) {
            val serverLevel = this.level() as ServerLevel
            val missileDamage = atk * 1.4f
            var targetPos = target.position().add(0.0, target.bbHeight * 0.5, 0.0)
            val distance = this.distanceTo(target).toDouble()

            if (this.random.nextFloat() <= calcMissRate(carrier, distance.toFloat())) {
                val offsetX = -5.0 + this.random.nextDouble() * 10.0
                val offsetY = this.random.nextDouble() * 5.0
                val offsetZ = -5.0 + this.random.nextDouble() * 10.0
                targetPos = targetPos.add(offsetX, offsetY, offsetZ)

                serverLevel.sendParticles<SimpleParticleType?>(
                    ModParticles.PARTICLE_TEXTS.get(),
                    this.x, this.y + 1.2, this.z,
                    1, 0.0, 0.1, 0.5, 0.0
                )
            }

            val missile =
                EntityAbyssMissile(
                    serverLevel, this, target, targetPos, missileDamage,
                    MoveType.ARC,
                    0.7f, 0.0f, 0.0f, null,
                    200, 3.5f
                )
            serverLevel.addFreshEntity(missile)
        }
    }

    fun calcMissRate(carrier: EntityShipBase, distance: Float): Float {
        val range = AircraftAiNumbers.ATTACK_RANGE_HEAVY
        val levelMod = 0.001f * carrier.level
        val miss = 0.25f + 0.25f * (distance / range) - levelMod
        return max(0.0f, min(miss, 0.5f))
    }

    fun getRandomCruisePos(reference: Entity?): Vec3 {
        val minDist =
            if (this.isMissionLightAircraft) AircraftAiNumbers.RAND_POS_MIN_LIGHT else AircraftAiNumbers.RAND_POS_MIN_HEAVY
        val randDist =
            if (this.isMissionLightAircraft) AircraftAiNumbers.RAND_POS_RAND_LIGHT else AircraftAiNumbers.RAND_POS_RAND_HEAVY

        val ref = if (reference != null) reference else this
        val level = this.level()
        val currentYaw = this.yRot

        for (i in 0..<AircraftAiNumbers.RANDOM_CRUISE_ATTEMPTS) {
            val angle = currentYaw + (i * AircraftAiNumbers.RANDOM_CRUISE_ANGLE_STEP)
            val rad = Math.toRadians(angle.toDouble())

            val dist = minDist + this.random.nextDouble() * randDist
            val newX = ref.x + cos(rad) * dist
            val newZ = ref.z + sin(rad) * dist
            val newY = (ref.y + ref.bbHeight + AircraftAiNumbers.RANDOM_CRUISE_Y_OFFSET
                    + this.random.nextDouble() * AircraftAiNumbers.RANDOM_CRUISE_Y_RANDOM)

            val targetPos = BlockPos.containing(newX, newY, newZ)
            if (level.getBlockState(targetPos).getCollisionShape(level, targetPos).isEmpty()) {
                return Vec3(newX, newY, newZ)
            }
        }

        return Vec3(ref.x, ref.y + AircraftAiNumbers.RANDOM_CRUISE_FALLBACK_Y, ref.z)
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        val result = super.hurt(source, amount)
        if (!this.level().isClientSide && result && !this.isAlive && !this.isDying) {
            this.isDying = true
            this.deathAnimTick = 0
            this.setNoGravity(false)
            val motion = this.deltaMovement
            this.deadMotionX = motion.x
            this.deadMotionZ = motion.z
            this.health = 1.0f
            // Return remaining ammo to carrier before death animation
            val carrier = this.carrier
            if (carrier != null) {
                returnSummonResources(carrier)
            }
        }
        return result
    }

    private fun tickDeathAnimation() {
        this.deathAnimTick++
        this.setNoGravity(false)

        val motion = this.deltaMovement
        this.setDeltaMovement(this.deadMotionX, motion.y - AircraftAiNumbers.DEATH_GRAVITY, this.deadMotionZ)
        this.hasImpulse = true

        if (this.level() is ServerLevel) {
            val serverLevel = this.level() as ServerLevel
            if (this.deathAnimTick % 2 == 0) {
                val range = this.bbWidth * 0.5
                for (i in 0..2) {
                    serverLevel.sendParticles<SimpleParticleType?>(
                        ParticleTypes.LARGE_SMOKE,
                        this.x - range + this.random.nextDouble() * range * 2.0,
                        this.y + this.bbHeight * 0.3 + this.random.nextDouble() * 0.3,
                        this.z - range + this.random.nextDouble() * range * 2.0,
                        1, 0.0, 0.0, 0.0, 0.02
                    )
                }
            }

            if (this.deathAnimTick >= AircraftAiNumbers.DEATH_TIME_EXPLOSION - 1) {
                for (i in 0..11) {
                    val ran1 = (this.bbWidth * (this.random.nextFloat() - 0.5f)).toDouble()
                    val ran2 = (this.bbWidth * (this.random.nextFloat() - 0.5f)).toDouble()
                    serverLevel.sendParticles<SimpleParticleType?>(
                        ParticleTypes.LAVA,
                        this.x + ran1, this.y + this.bbHeight * 0.3, this.z + ran2,
                        1, 0.0, 0.0, 0.0, 0.0
                    )
                    if ((i and 3) == 0) {
                        serverLevel.sendParticles<SimpleParticleType?>(
                            ParticleTypes.EXPLOSION,
                            this.x + ran2, this.y + this.bbHeight * 0.5, this.z + ran1,
                            1, 0.0, 0.0, 0.0, 0.0
                        )
                    }
                }
            }

            if (this.deathAnimTick >= AircraftAiNumbers.DEATH_TIME_EXPLOSION) {
                for (k in 0..19) {
                    val d2 = this.random.nextGaussian() * 0.02
                    val d0 = this.random.nextGaussian() * 0.02
                    val d1 = this.random.nextGaussian() * 0.02
                    serverLevel.sendParticles<SimpleParticleType?>(
                        ParticleTypes.POOF,
                        this.x + (this.random.nextFloat() * this.bbWidth * 2.0f) - this.bbWidth,
                        this.y + (this.random.nextFloat() * this.bbHeight),
                        this.z + (this.random.nextFloat() * this.bbWidth * 2.0f) - this.bbWidth,
                        1, d2, d0, d1, 0.05
                    )
                }
                this.discard()
            }
        }
    }

    override fun isOnFire(): Boolean {
        if (this.isDying && this.deathAnimTick > AircraftAiNumbers.DEATH_TIME_BURNING) {
            return true
        }
        return super.isOnFire()
    }

    override fun brainProvider(): Brain.Provider<EntityAircraftBase> {
        return Brain.provider<EntityAircraftBase>(AircraftBrainAi.MEMORY_TYPES, AircraftBrainAi.SENSOR_TYPES)
    }

    override fun makeBrain(dynamic: Dynamic<*>): Brain<*> {
        return AircraftBrainAi.makeBrain(this, this.brainProvider().makeBrain(dynamic))
    }

    override fun customServerAiStep() {
        if (this.level() is ServerLevel) {
            val serverLevel = this.level() as ServerLevel
            AircraftBrainAi.tick(serverLevel, this)
        }
        super.customServerAiStep()
    }

    fun attackMovementCoordinator(): ShipMovementCoordinator {
        return this.attackMovement
    }


    override fun move(type: MoverType, pos: Vec3) {
        super.move(type, pos)
        this.checkInsideBlocks()
    }

    private fun updateRotation() {
        val delta = this.deltaMovement
        if (delta.horizontalDistanceSqr() < 1.0E-5) {
            return
        }

        val horizontal = delta.horizontalDistance()
        val targetYaw = (Math.toDegrees(atan2(delta.z, delta.x)) - 90.0).toFloat()
        val targetPitch = (-Math.toDegrees(atan2(delta.y, horizontal))).toFloat()

        this.yRot = Mth.approachDegrees(this.yRot, targetYaw, 15.0f)
        this.xRot = Mth.approachDegrees(this.xRot, targetPitch, 15.0f)

        this.yBodyRot = this.yRot
        this.yHeadRot = this.yRot
    }


    override fun pushEntities() {
    }

    override fun isPushable(): Boolean {
        return false
    }

    override fun causeFallDamage(fallDistance: Float, multiplier: Float, source: DamageSource): Boolean {
        return false
    }

    override fun onClimbable(): Boolean {
        return false
    }

    protected open fun applyFlyParticle() {
    }


    private val carrier: EntityShipBase?
        get() {
            if (this.carrierId == null || this.level() !is ServerLevel) {
                return null
            }
            val entity: Entity? = (this.level() as ServerLevel).getEntity(this.carrierId)
            if (entity is EntityShipBase && entity.isAlive && !entity.isRemoved) {
                return entity
            }
            return null
        }

    val missionTarget: Entity?
        get() {
            if (this.targetId == null || this.level() !is ServerLevel) {
                return null
            }
            val entity: Entity? = (this.level() as ServerLevel).getEntity(this.targetId)
            if (entity == null || !entity.isAlive || entity.isRemoved) {
                return null
            }
            return entity
        }

    private fun findNewTarget(carrier: EntityShipBase): Entity? {
        val range =
            if (carrier.isStateAntiAir) AircraftAiNumbers.TARGETING_RANGE_AIR_ONLY else AircraftAiNumbers.TARGETING_RANGE_NORMAL
        val box = this.boundingBox.inflate(range, range, range)
        val entities = this.level().getEntities(this, box) { entity: Entity? ->
            if (entity == null || !entity.isAlive || entity === this) return@getEntities false
            entity is LivingEntity
        }

        var nearest: Entity? = null
        var nearestDistance = Double.MAX_VALUE
        for (entity in entities) {
            if (!isValidTarget(carrier, entity)) {
                continue
            }
            val dist = this.distanceToSqr(entity)
            if (dist < nearestDistance) {
                nearestDistance = dist
                nearest = entity
            }
        }
        return nearest
    }

    private fun isValidTarget(carrier: EntityShipBase, target: Entity?): Boolean {
        if (target !is LivingEntity) {
            return false
        }
        if (isFriendlyTarget(carrier, target)) {
            return false
        }
        if (target is Enemy) {
            return true
        }
        val pvpEnabled = carrier.getStateFlag(18)
        if (pvpEnabled) {
            if (target is Player || target is EntityShipBase) {
                return true
            }
        }
        if (carrier.target === target) {
            return true
        }
        val lastHurtBy = carrier.getLastHurtByMob()
        if (lastHurtBy === target) {
            return true
        }
        val owner = carrier.owner
        if (owner != null && (owner.getLastHurtByMob() === target || owner.getLastHurtMob() === target)) {
            return true
        }
        return false
    }

    private fun isFriendlyTarget(carrier: EntityShipBase, target: Entity?): Boolean {
        if (target === carrier) {
            return true
        }
        if (target is Player && target.uuid == carrier.ownerUUID) {
            return true
        }
        if (target is TamableAnimal && target.ownerUUID == carrier.ownerUUID) {
            return true
        }
        if (target is EntityShipBase && target.ownerUUID == carrier.ownerUUID) {
            return true
        }
        if (target is EntityAircraftBase) {
            val otherCarrier = target.carrier
            return otherCarrier != null && otherCarrier.ownerUUID == carrier.ownerUUID
        }
        return false
    }


    protected open val isDefaultLightAircraft: Boolean
        get() = true

    fun hasAmmoLight(): Boolean {
        return this.numAmmoLight > 0
    }

    fun hasAmmoHeavy(): Boolean {
        return this.numAmmoHeavy > 0
    }

    companion object {
        @JvmStatic
        fun createAttributes(): AttributeSupplier.Builder {
            return EntityShincolleSimpleMob.createAttributes()
                .add(Attributes.FLYING_SPEED, 0.4)
        }
    }
}
