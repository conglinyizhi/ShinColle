package org.trp.shincolle.entity.base

import com.mojang.serialization.Dynamic
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.DamageTypeTags
import net.minecraft.tags.FluidTags
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.Brain
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.pathfinder.PathType
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.command.ModCommands
import org.trp.shincolle.entity.base.path.ShipLegacyNavigation
import org.trp.shincolle.entity.base.path.ShipMoveControl
import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin

abstract class EntityMountBase protected constructor(type: EntityType<out PathfinderMob?>, level: Level) :
    PathfinderMob(type, level) {
    var seatPos: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
        protected set
    protected var seatPos2: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
    var shipDepth: Double = 0.0
        protected set
    var keyPressed: Int = 0
    var keyTick: Int = 0

    var isSubmarineMode: Boolean = false

    protected var host: EntityShipBase? = null

    private var lightAttackCooldown = 0
    private var heavyAttackCooldown = 0
    private val followMovement: ShipMovementCoordinator

    init {
        this.setPathfindingMalus(PathType.WATER, 0.0f)
        this.setPathfindingMalus(PathType.WATER_BORDER, 0.0f)
        this.followMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.Companion.PRIORITY_COMMAND)
        this.moveControl = ShipMoveControl(this, 30.0f)
        this.navigation = ShipLegacyNavigation(this, level)
    }

    private fun applyWaterBuoyancy() {
        val depth = this.shipDepth
        var upward = 0.0
        if (depth > MountAiNumbers.BUOY_MIN_DEPTH) {
            upward = MountAiNumbers.BUOY_COEFF * depth.pow(MountAiNumbers.BUOY_EXPONENT) - MountAiNumbers.BUOY_OFFSET
        }
        val dm = this.getDeltaMovement()
        var newY = (dm.y + upward) * MountAiNumbers.BUOY_DAMP
        newY = Mth.clamp(newY, -MountAiNumbers.BUOY_MAX_MOTION, MountAiNumbers.BUOY_MAX_MOTION)
        this.setDeltaMovement(dm.x, newY, dm.z)
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define<Optional<UUID?>?>(HOST_UUID, Optional.empty<UUID?>())
        builder.define<Int?>(STATE_EMOTION, 0)
    }

    var hostUUID: UUID?
        get() = this.entityData.get<Optional<UUID?>?>(HOST_UUID).orElse(null)
        set(uuid) {
            this.entityData.set<Optional<UUID?>?>(
                HOST_UUID,
                Optional.ofNullable<UUID?>(uuid)
            )
        }

    fun getHost(): EntityShipBase? {
        if (this.host != null && (!this.host!!.isAlive() || this.host!!.isRemoved())) {
            this.host = null
        }
        if (this.host == null) {
            for (p in this.getPassengers()) {
                if (p is EntityShipBase && p.isAlive() && !p.isRemoved()) {
                    this.host = p
                    break
                }
            }
        }
        return this.host
    }

    var stateEmotion: Int
        get() = this.entityData.get<Int?>(STATE_EMOTION)
        set(value) {
            this.entityData.set<Int?>(STATE_EMOTION, value)
        }

    override fun tick() {
        if (!this.level().isClientSide && ModCommands.isStopShipAi()) {
            if (!checkHostExistence()) return
            this.setDeltaMovement(Vec3.ZERO)
            this.setSpeed(0.0f)
            this.followMovement.stopAny()
            return
        }

        val fluidH = this.getFluidHeight(FluidTags.WATER)
        this.shipDepth = fluidH

        if (this.isSubmarineMode && fluidH <= 0.4) {
            this.isSubmarineMode = false
        }
        if (this.isSubmarineMode && fluidH <= MountAiNumbers.SUBMARINE_DISABLE_DEPTH) {
            this.isSubmarineMode = false
        }

        if (this.isInWater() && !this.isPassenger() && !this.isSubmarineMode) {
            if (this.isVehicle() || this.followMovement.isNavigationDone()) {
                applyWaterBuoyancy()
            }
        }

        super.tick()

        if (this.isOnFire()) {
            this.clearFire()
        }

        if ((this.tickCount and MountAiNumbers.AIR_SUPPLY_INTERVAL_MASK) == 0) this.setAirSupply(MountAiNumbers.STOP_SHIP_AI_AIR_SUPPLY)

        if (this.level().isClientSide) {
            updateClientLogic()
        } else {
            updateServerLogic()
        }

        handleMovement()
    }

    protected open fun updateClientLogic() {
        updateShipDepthClient()
        spawnMovingParticle()
    }

    private fun updateShipDepthClient() {
        val fluidH = this.getFluidHeight(FluidTags.WATER)
        if (fluidH > 0.0) {
            this.shipDepth = fluidH
        } else {
            this.shipDepth = 0.0
        }
    }

    private fun spawnMovingParticle() {
        if (this.shipDepth <= 0.0) return
        var motX = this.getX() - this.xo
        var motZ = this.getZ() - this.zo
        val limit = 0.25
        motX = Mth.clamp(motX, -limit, limit)
        motZ = Mth.clamp(motZ, -limit, limit)
        if (motX != 0.0 || motZ != 0.0) {
            val width = this.getBbWidth().toDouble()
            val amount = 2 + this.random.nextInt(3)
            for (i in 0..<amount) {
                val px = this.getX() + motX * 3.0 + (this.random.nextDouble() - 0.5) * width
                val py = this.getY() + 0.6 + (this.random.nextDouble() - 0.5) * width * 0.15
                val pz = this.getZ() + motZ * 3.0 + (this.random.nextDouble() - 0.5) * width
                val vx = -motX * 1.5
                val vz = -motZ * 1.5
                this.level().addParticle(ParticleTypes.CLOUD, px, py, pz, vx, 0.0, vz)
            }
        }
    }

    protected fun updateServerLogic() {
        if (!checkHostExistence()) return

        if (lightAttackCooldown > 0) --lightAttackCooldown
        if (heavyAttackCooldown > 0) --heavyAttackCooldown

        if ((this.tickCount and MountAiNumbers.SERVER_SYNC_INTERVAL_MASK) == 0) {
            syncWithHost()
        }
    }

    protected fun checkHostExistence(): Boolean {
        val uuid = this.hostUUID
        if (uuid == null) {
            this.discard()
            return false
        }

        if (this.host == null || this.host!!.isRemoved()) {
            val entity = (this.level() as ServerLevel).getEntity(uuid)
            if (entity is EntityShipBase && entity.isAlive() && !entity.isRemoved()) {
                this.host = entity
            } else {
                this.discard()
                return false
            }
        }

        if (this.host!!.getVehicle() !== this) {
            this.discard()
            return false
        }
        return true
    }

    protected fun syncWithHost() {
        if (this.host == null) return

        val hostMaxHP = this.host!!.getMaxHealth()
        this.getAttribute(Attributes.MAX_HEALTH)!!
            .setBaseValue(hostMaxHP * MountAiNumbers.HOST_MAX_HEALTH_SCALE)

        val hostSpeed = this.host!!.getAttributeValue(Attributes.MOVEMENT_SPEED)
        this.getAttribute(Attributes.MOVEMENT_SPEED)!!.setBaseValue(hostSpeed)

        val kr = this.host!!.getLegacyShipStats().getBuffedAttr(20)
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE)!!.setBaseValue(Mth.clamp(kr, 0.0f, 1.0f).toDouble())

        this.getAttribute(Attributes.FOLLOW_RANGE)!!.setBaseValue(MountAiNumbers.FOLLOW_RANGE_ATTR)
    }

    protected fun handleMovement() {
        if (this.keyTick > 0) {
            --this.keyTick
            val rider = getControllingPassenger()
            if (rider != null) {
                this.setYRot(rider.getYRot())
                this.yRotO = rider.yRotO
                this.setXRot(rider.getXRot())
                this.xRotO = rider.xRotO
                this.yBodyRot = rider.yBodyRot
                this.yBodyRotO = rider.yBodyRotO
                this.yHeadRot = rider.getYHeadRot()
                this.yHeadRotO = rider.yHeadRotO
            }
        } else if (abs(this.getX() - this.xo) > MountAiNumbers.ROTATION_EPSILON
            || abs(this.getZ() - this.zo) > MountAiNumbers.ROTATION_EPSILON
        ) {
            handleAIMovementRotation()
        } else {
            syncRotationWithHost()
        }
    }

    protected fun handleAIMovementRotation() {
        val dx = this.getX() - this.xo
        val dz = this.getZ() - this.zo
        val yaw = (Mth.atan2(dz, dx) * (180.0 / Math.PI)).toFloat() - 90.0f
        this.setYRot(yaw)
        this.yRotO = yaw
        this.yBodyRot = yaw
        this.yHeadRot = yaw
        syncHostToMount()
    }

    protected fun syncRotationWithHost() {
        val h = getHost()
        if (h != null) {
            this.setYRot(h.getYRot())
            this.yRotO = h.yRotO
            this.yBodyRot = h.yBodyRot
            this.yBodyRotO = h.yBodyRotO
            this.yHeadRot = h.getYHeadRot()
            this.yHeadRotO = h.yHeadRotO
            this.setXRot(h.getXRot())
            this.xRotO = h.xRotO
        }
    }

    protected fun syncHostToMount() {
        val h = getHost()
        if (h != null) {
            h.setYRot(this.getYRot())
            h.yRotO = this.yRotO
            h.yBodyRot = this.yBodyRot
            h.yBodyRotO = this.yBodyRotO
            h.yHeadRot = this.yHeadRot
            h.yHeadRotO = this.yHeadRotO
            h.setXRot(this.getXRot())
            h.xRotO = this.xRotO
        }
    }

    override fun travel(travelVector: Vec3) {
        if (this.host != null && (this.host!!.isOrderedToSit() || this.host!!.isInSittingPose()) && getControllingPassenger() == null) {
            this.setDeltaMovement(Vec3.ZERO)
            this.setSpeed(0.0f)
            super.travel(Vec3.ZERO)
            return
        }

        if (this.isAlive()) {
            val rider = getControllingPassenger()
            if (rider != null) {
                this.setYRot(rider.getYRot())
                this.yRotO = this.getYRot()
                val visualPitch = rider.getXRot()
                this.yBodyRot = rider.yBodyRot
                this.yHeadRot = rider.getYHeadRot()

                val strafe = rider.xxa * 0.5f
                var forward = rider.zza
                if (forward <= 0.0f) forward *= 0.25f

                this.setSpeed(this.getAttributeValue(Attributes.MOVEMENT_SPEED).toFloat())

                if (rider.getXRot() > 60.0f && forward > 0.0f) {
                    this.isSubmarineMode = true
                }

                if (this.isInWaterOrBubble() && (forward != 0f || strafe != 0f)
                    && this.tickCount % 2 == 0 && this.level() is ServerLevel
                ) {
                    var motX = this.getX() - this.xo
                    var motZ = this.getZ() - this.zo
                    val limit = 0.25
                    motX = Mth.clamp(motX, -limit, limit)
                    motZ = Mth.clamp(motZ, -limit, limit)
                    val width = this.getBbWidth().toDouble()
                    val amount = 2 + this.random.nextInt(3)
                    for (i in 0..<amount) {
                        val px = this.getX() + motX * 3.0 + (this.random.nextDouble() - 0.5) * width
                        val py = this.getY() + 0.6 + (this.random.nextDouble() - 0.5) * width * 0.15
                        val pz = this.getZ() + motZ * 3.0 + (this.random.nextDouble() - 0.5) * width
                        val vx = -motX * 1.5
                        val vz = -motZ * 1.5
                        sl.sendParticles<SimpleParticleType?>(ParticleTypes.CLOUD, px, py, pz, 0, vx, 0.0, vz, 1.0)
                    }
                }

                var travelPitch = 0.0f
                if (rider.getXRot() > 60.0f || rider.getXRot() < -60.0f) {
                    travelPitch = visualPitch
                }
                this.setXRot(travelPitch)
                this.setRot(this.getYRot(), this.getXRot())

                super.travel(Vec3(strafe.toDouble(), travelVector.y, forward.toDouble()))

                if (this.isSubmarineMode) {
                    val currentMotion = this.getDeltaMovement()

                    if (forward > 0.0f) {
                        val pitchRadians = Math.toRadians(rider.getXRot().toDouble())
                        val speed = this.getAttributeValue(Attributes.MOVEMENT_SPEED)
                        val verticalSpeed = -sin(pitchRadians) * speed

                        this.setDeltaMovement(currentMotion.x, verticalSpeed, currentMotion.z)
                    } else {
                        this.setDeltaMovement(currentMotion.x, 0.0, currentMotion.z)
                    }
                }

                this.setXRot(visualPitch)
                this.setRot(this.getYRot(), this.getXRot())
                this.calculateEntityAnimation(false)
                return
            }
        }
        super.travel(travelVector)
    }

    override fun getControllingPassenger(): LivingEntity? {
        for (p in this.getPassengers()) {
            if (p is Player) return p
        }
        return null
    }

    override fun getPassengerAttachmentPoint(passenger: Entity, dimensions: EntityDimensions, scale: Float): Vec3 {
        val pos = if (passenger is EntityShipBase) seatPos else seatPos2
        val radians = this.yBodyRot * (Math.PI / 180.0).toFloat()
        val cosR = Mth.cos(radians)
        val sinR = Mth.sin(radians)
        val rz = pos[0] * cosR + pos[2] * sinR
        val rx = pos[2] * cosR - pos[0] * sinR
        return Vec3(rx.toDouble(), pos[1].toDouble(), rz.toDouble())
    }

    override fun canAddPassenger(passenger: Entity): Boolean {
        return true
    }

    override fun shouldRiderFaceForward(player: Player): Boolean {
        return true
    }

    public override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        val heldStack = player.getItemInHand(hand)
        if (this.host != null && this.host!!.isOwnedBy(player) && ShipHostInteractionRouter.shouldForwardToHost(
                heldStack
            )
        ) {
            val hostInteractionResult = ShipHostInteractionRouter.forwardToHost(this.host, player, hand, heldStack)
            if (hostInteractionResult != InteractionResult.PASS) {
                return hostInteractionResult
            }
        }

        if (this.level().isClientSide) return InteractionResult.SUCCESS

        if (!player.isSecondaryUseActive()) {
            if (this.distanceToSqr(player) < MountAiNumbers.RIDER_INTERACT_DISTANCE_SQ) {
                player.startRiding(this, true)
                return InteractionResult.SUCCESS
            }
        }
        return super.mobInteract(player, hand)
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (this.level().isClientSide) return false

        if (this.host == null) {
            this.discard()
            return false
        }

        if (source.`is`(DamageTypeTags.IS_FIRE)
            || source.`is`(DamageTypeTags.IS_FALL)
            || source.`is`(DamageTypes.IN_WALL)
            || source.`is`(DamageTypes.STARVE)
            || source.`is`(DamageTypes.CACTUS)
            || source.`is`(DamageTypeTags.IS_DROWNING)
            || source.`is`(DamageTypeTags.NO_ANGER)
        ) {
            return false
        }

        if (source.`is`(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.discard()
            return false
        }

        val attacker = source.getEntity()
        if (attacker != null && attacker == this) {
            this.host!!.setOrderedToSit(false)
            return false
        }

        val mountArmor = this.host!!.getLegacyShipStats().getArmor() * 0.5f
        var reduced = amount * (1.0f - mountArmor + (this.random.nextFloat() * 0.5f - 0.25f))

        if (reduced > 0.0f && reduced < 1.0f) {
            reduced = 1.0f
        } else if (reduced < 0.0f) {
            reduced = 0.0f
        }

        if (reduced <= 0.0f) return false

        this.host!!.setOrderedToSit(false)

        if (this.random.nextInt(5) == 0) {
            this.host!!.applyEmotesReaction(2)
        }

        val hurtResult = super.hurt(source, reduced)
        // Retaliate: if an external entity attacked us, notify the host
        if (hurtResult && reduced > 0.0f && attacker is LivingEntity
            && attacker !== this.host && !attacker.isAlliedTo(this.host) && this.host!!.getTarget() == null
        ) {
            this.host!!.setTarget(attacker)
        }
        return hurtResult
    }

    override fun fireImmune(): Boolean {
        return true
    }

    override fun displayFireAnimation(): Boolean {
        return false
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        val uuid = this.hostUUID
        if (uuid != null) compound.putUUID("HostUUID", uuid)
        compound.putInt("StateEmotion", this.stateEmotion)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        if (compound.hasUUID("HostUUID")) this.hostUUID = compound.getUUID("HostUUID")
        this.stateEmotion = compound.getInt("StateEmotion")
    }

    override fun brainProvider(): Brain.Provider<EntityMountBase?> {
        return Brain.provider<EntityMountBase?>(EntityMountBrainAi.MEMORY_TYPES, EntityMountBrainAi.SENSOR_TYPES)
    }

    override fun makeBrain(dynamic: Dynamic<*>): Brain<*> {
        return EntityMountBrainAi.makeBrain(this, this.brainProvider().makeBrain(dynamic))
    }

    override fun customServerAiStep() {
        if (this.level() is ServerLevel) {
            EntityMountBrainAi.tick(serverLevel, this)
        }
        super.customServerAiStep()
    }

    fun followMovementCoordinator(): ShipMovementCoordinator {
        return this.followMovement
    }

    fun setSeatPos(x: Float, y: Float, z: Float) {
        this.seatPos[0] = x
        this.seatPos[1] = y
        this.seatPos[2] = z
    }

    fun setSeatPos2(x: Float, y: Float, z: Float) {
        this.seatPos2[0] = x
        this.seatPos2[1] = y
        this.seatPos2[2] = z
    }

    companion object {
        private val HOST_UUID: EntityDataAccessor<Optional<UUID?>?> = SynchedEntityData.defineId<Optional<UUID?>?>(
            EntityMountBase::class.java,
            EntityDataSerializers.OPTIONAL_UUID
        )
        private val STATE_EMOTION: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityMountBase::class.java, EntityDataSerializers.INT)

        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, MountAiNumbers.BASE_MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MountAiNumbers.BASE_MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, MountAiNumbers.FOLLOW_RANGE_ATTR)
                .add(Attributes.KNOCKBACK_RESISTANCE, MountAiNumbers.BASE_KNOCKBACK_RESISTANCE)
                .add(Attributes.STEP_HEIGHT, MountAiNumbers.BASE_STEP_HEIGHT)
                .add(Attributes.ATTACK_DAMAGE, MountAiNumbers.BASE_ATTACK_DAMAGE)
        }
    }
}
