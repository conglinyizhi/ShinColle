package org.trp.shincolle.entity.base

import com.mojang.serialization.Dynamic
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.ai.Brain
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.level.Level
import org.trp.shincolle.Shincolle.Companion.debugLog
import org.trp.shincolle.init.ModSounds
import java.util.*
import kotlin.math.max

abstract class EntitySummonBase protected constructor(type: EntityType<out TamableAnimal?>?, level: Level?) :
    EntityShincolleSimpleMob(type, level) {
    protected var carrierId: UUID? = null
    protected var targetId: UUID? = null
    protected var missionTick: Int = 0
    protected var numAmmoLight: Int
    protected var numAmmoHeavy: Int
    var attackRangeSq: Float
        protected set
    protected var resourcesReturned: Boolean
    private val returnMovement: ShipMovementCoordinator
    private val attackMovement: ShipMovementCoordinator
    private val followMovement: ShipMovementCoordinator
    private val returnRecovery: ShipMovementRecoveryState
    private var returnTicks = 0

    init {
        this.returnMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.Companion.PRIORITY_COMMAND)
        this.attackMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.Companion.PRIORITY_COMBAT)
        this.followMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.Companion.PRIORITY_NORMAL)
        this.returnRecovery = ShipMovementRecoveryState()
        this.numAmmoLight = SummonAiNumbers.INITIAL_LIGHT_AMMO
        this.numAmmoHeavy = 0
        this.attackRangeSq = SummonAiNumbers.DEFAULT_ATTACK_RANGE_SQ
        this.resourcesReturned = false
    }

    fun performAttack(target: LivingEntity) {
        if (this.level() !is ServerLevel) {
            return
        }
        val carrier = this.carrier
        var damage = SummonAiNumbers.ATTACK_DAMAGE_DEFAULT
        if (carrier != null) {
            damage = max(
                SummonAiNumbers.DAMAGE_MIN,
                carrier.getLegacyShipStats().getFirepower() * SummonAiNumbers.ATTACK_DAMAGE_CARRIER_FACTOR
            )
        }

        target.hurt(this.damageSources().mobAttack(this), damage)

        serverLevel.sendParticles<SimpleParticleType?>(
            ParticleTypes.CRIT,
            target.getX(),
            target.getY() + target.getBbHeight() * 0.5,
            target.getZ(),
            SummonAiNumbers.CRIT_PARTICLE_COUNT,
            SummonAiNumbers.CRIT_PARTICLE_OFFSET,
            SummonAiNumbers.CRIT_PARTICLE_OFFSET,
            SummonAiNumbers.CRIT_PARTICLE_OFFSET,
            SummonAiNumbers.CRIT_PARTICLE_SPEED
        )
        this.playSound(
            ModSounds.SHIP_FIRELIGHT.get(),
            SummonAiNumbers.ATTACK_SOUND_VOLUME,
            SummonAiNumbers.ATTACK_SOUND_PITCH
        )
    }

    fun initSummon(carrier: EntityShipBase?, target: Entity?, scaleLevel: Int) {
        if (carrier == null) {
            return
        }
        this.carrierId = carrier.getUUID()
        this.targetId = if (target == null) null else target.getUUID()
        this.missionTick = 0
        this.resourcesReturned = false
        resetReturnState()
        this.setScaleLevel(scaleLevel)

        val offsetX =
            (this.random.nextDouble() * SummonAiNumbers.INIT_SUMMON_OFFSET_RANGE - SummonAiNumbers.INIT_SUMMON_OFFSET_CENTER)
        val offsetZ =
            (this.random.nextDouble() * SummonAiNumbers.INIT_SUMMON_OFFSET_RANGE - SummonAiNumbers.INIT_SUMMON_OFFSET_CENTER)
        this.moveTo(this.getX() + offsetX, this.getY(), this.getZ() + offsetZ, this.getYRot(), this.getXRot())

        this.setOwnerUUID(carrier.getOwnerUUID())
        this.setTame(true, false)

        val maxHealth = SummonAiNumbers.HEALTH_BASE + carrier.getLegacyShipStats()
            .getMaxHealth() * SummonAiNumbers.HEALTH_SCALE_FACTOR
        this.getAttribute(Attributes.MAX_HEALTH)!!.setBaseValue(maxHealth.toDouble())
        this.setHealth(maxHealth)

        val speed = SummonAiNumbers.SPEED_BASE + carrier.getLegacyShipStats()
            .getMoveSpeed() * SummonAiNumbers.SPEED_SCALE_FACTOR
        this.getAttribute(Attributes.MOVEMENT_SPEED)!!.setBaseValue(speed.toDouble())

        val damage = max(
            SummonAiNumbers.DAMAGE_MIN,
            carrier.getLegacyShipStats().getFirepower() * SummonAiNumbers.DAMAGE_SCALE_FACTOR
        )
        this.getAttribute(Attributes.ATTACK_DAMAGE)!!.setBaseValue(damage.toDouble())
        this.getAttribute(Attributes.FOLLOW_RANGE)!!.setBaseValue(SummonAiNumbers.FOLLOW_RANGE_ATTR)

        this.attackRangeSq = SummonAiNumbers.DEFAULT_ATTACK_RANGE_SQ * SummonAiNumbers.DEFAULT_ATTACK_RANGE_SQ

        if (target is LivingEntity) {
            this.setTarget(target)
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
        compound.putInt("MissionTick", this.missionTick)
        compound.putInt("NumAmmoLight", this.numAmmoLight)
        compound.putInt("NumAmmoHeavy", this.numAmmoHeavy)
        compound.putFloat("AttackRangeSq", this.attackRangeSq)
        compound.putBoolean("ResourcesReturned", this.resourcesReturned)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        this.carrierId = if (compound.hasUUID("CarrierId")) compound.getUUID("CarrierId") else null
        this.targetId = if (compound.hasUUID("TargetId")) compound.getUUID("TargetId") else null
        this.missionTick = compound.getInt("MissionTick")
        this.numAmmoLight = compound.getInt("NumAmmoLight")
        this.numAmmoHeavy = compound.getInt("NumAmmoHeavy")
        this.attackRangeSq = compound.getFloat("AttackRangeSq")
        this.resourcesReturned = compound.getBoolean("ResourcesReturned")
    }

    override fun aiStep() {
        super.aiStep()
        if (!this.level().isClientSide) {
            updateServerLogic()
        }
    }

    protected fun updateServerLogic() {
        this.missionTick++

        val carrier = this.carrier
        if (carrier == null) {
            // Carrier no longer exists in the world; cannot return resources
            this.discard()
            return
        }
        if (!carrier.isAlive()) {
            // Carrier is dead but still exists; return resources before discarding
            returnSummonResourcesOnce(carrier)
            this.discard()
            return
        }

        if (checkReturnToCarrier(carrier)) {
            handleReturnToCarrier(carrier)
            return
        }

        if (this.getTarget() == null || !this.getTarget()!!.isAlive()) {
            val currentTarget = this.missionTarget
            if (currentTarget is LivingEntity && currentTarget.isAlive()) {
                this.setTarget(currentTarget)
                resetReturnState()
            } else {
                val carrierTarget: Entity? = carrier.getTarget()
                if (carrierTarget is LivingEntity && carrierTarget.isAlive()) {
                    this.setTarget(carrierTarget)
                    this.targetId = carrierTarget.getUUID()
                    resetReturnState()
                } else {
                    this.setTarget(null)
                    handleReturnToCarrier(carrier)
                    return
                }
            }
        }
    }

    protected fun checkReturnToCarrier(carrier: EntityShipBase?): Boolean {
        if (this.missionTick >= SummonAiNumbers.LIFETIME_TICKS) {
            return true
        }
        if (this.numAmmoLight <= 0 && this.numAmmoHeavy <= 0) {
            return true
        }
        return false
    }

    protected fun handleReturnToCarrier(carrier: EntityShipBase) {
        this.returnTicks++
        val distSq = this.distanceToSqr(carrier)
        if (distSq <= SummonAiNumbers.RETURN_REACH_DISTANCE_SQ && this.missionTick > SummonAiNumbers.RETURN_MIN_MISSION_TICKS) {
            returnSummonResourcesOnce(carrier)
            this.discard()
            resetReturnState()
        } else {
            this.returnMovement.moveTo(carrier, SummonAiNumbers.RETURN_MOVE_SPEED)
            if (trackReturnRecovery(carrier, distSq)) {
                return
            }
            if (this.returnTicks > SummonAiNumbers.RETURN_FAILSAFE_TICKS
                && this.returnRecovery.isStuckLongerThan(SummonAiNumbers.RETURN_FAILSAFE_TICKS)
            ) {
                debugLog(
                    "[SCMoveDiag] SummonReturn failsafeDiscard summon={} carrier={} distanceSqr={} returnTicks={} stuckTicks={}",
                    this.getUUID(), carrier.getUUID(), distSq, this.returnTicks, this.returnRecovery.stuckTicks()
                )
                returnSummonResourcesOnce(carrier)
                this.discard()
                resetReturnState()
            }
        }
    }

    private fun trackReturnRecovery(carrier: EntityShipBase, distanceSqr: Double): Boolean {
        this.returnRecovery.trackProgress(this.position())
        val force = this.returnRecovery.isStuckLongerThan(SummonAiNumbers.RETURN_STUCK_TICK_LIMIT)
        if (!force && (this.tickCount % SummonAiNumbers.RETURN_RECOVERY_POLL_INTERVAL) != 0) {
            return false
        }
        if (!this.returnRecovery.shouldTryTeleportThrottled(
                force, distanceSqr,
                SummonAiNumbers.RETURN_TELEPORT_DISTANCE_SQ, SummonAiNumbers.RETURN_TELEPORT_COOLDOWN_TICKS
            )
        ) {
            return false
        }
        if (!this.returnMovement.teleportNearLiving(carrier, SummonAiNumbers.TELEPORT_VERTICAL_OFFSET)) {
            return false
        }

        debugLog(
            "[SCMoveDiag] SummonReturn teleportRecovery summon={} carrier={} force={} distanceSqr={} stuckTicks={}",
            this.getUUID(), carrier.getUUID(), force, distanceSqr, this.returnRecovery.stuckTicks()
        )
        this.returnRecovery.reset(this.position())
        this.returnTicks = 0
        return true
    }

    private fun resetReturnState() {
        this.returnRecovery.clear()
        this.returnTicks = 0
    }

    override fun brainProvider(): Brain.Provider<EntitySummonBase?> {
        return Brain.provider<EntitySummonBase?>(EntitySummonBrainAi.MEMORY_TYPES, EntitySummonBrainAi.SENSOR_TYPES)
    }

    override fun makeBrain(dynamic: Dynamic<*>): Brain<*> {
        return EntitySummonBrainAi.makeBrain(this, this.brainProvider().makeBrain(dynamic))
    }

    override fun customServerAiStep() {
        if (this.level() is ServerLevel) {
            EntitySummonBrainAi.tick(serverLevel, this)
        }
        super.customServerAiStep()
    }

    fun attackMovementCoordinator(): ShipMovementCoordinator {
        return this.attackMovement
    }

    fun followMovementCoordinator(): ShipMovementCoordinator {
        return this.followMovement
    }

    override fun die(damageSource: DamageSource) {
        super.die(damageSource)
        if (!this.level().isClientSide) {
            returnSummonResourcesOnce(this.carrier)
        }
    }

    protected fun returnSummonResourcesOnce(carrier: EntityShipBase?) {
        if (this.resourcesReturned || carrier == null) {
            return
        }
        this.resourcesReturned = true
        returnSummonResources(carrier)
    }

    /** Override in subclasses to return ammo/servants/resources to the carrier.  */
    protected open fun returnSummonResources(carrier: EntityShipBase?) {
    }

    val carrier: EntityShipBase?
        get() {
            if (this.carrierId == null || this.level() !is ServerLevel) {
                return null
            }
            val entity: Entity? = serverLevel.getEntity(this.carrierId)
            if (entity is EntityShipBase && entity.isAlive() && !entity.isRemoved()) {
                return entity
            }
            return null
        }

    protected val missionTarget: Entity?
        get() {
            if (this.targetId == null || this.level() !is ServerLevel) {
                return null
            }
            val entity: Entity? = serverLevel.getEntity(this.targetId)
            if (entity == null || !entity.isAlive() || entity.isRemoved()) {
                return null
            }
            return entity
        }
}
