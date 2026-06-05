package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import org.trp.shincolle.Shincolle.Companion.debugLog
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.base.ShipMovementCoordinator
import org.trp.shincolle.entity.base.ShipMovementRecoveryState
import org.trp.shincolle.init.ModItems
import kotlin.math.max

class EntityDestroyerInazuma(type: EntityType<out TamableAnimal?>?, level: Level?) : EntityShipBase(type, level),
    IShipRiderType {
    private var riderType: Int
    var isRaiden: Boolean
    private var raidenGattaiExpireTick: Long
    private var raidenGattaiCooldownUntilTick: Long
    private val raidenMovement: ShipMovementCoordinator
    private val raidenRecovery: ShipMovementRecoveryState

    init {
        this.raidenMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMMAND)
        this.raidenRecovery = ShipMovementRecoveryState()
        this.modelPos = floatArrayOf(0f, 25f, 0f, 50f)
        setStateMinor(STATE_MINOR_FACTION_ID, -1)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 54)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 5)
        setStateMinor(STATE_MINOR_RARITY, 1)
        this.isStateGuiBtn3 = false
        this.isStateGuiBtn4 = false
        this.isStateCanRide = true
        this.riderType = 0
        this.isRaiden = false
        this.raidenGattaiExpireTick = 0L
        this.raidenGattaiCooldownUntilTick = 0L
    }

    override fun aiStep() {
        super.aiStep()

        updateState()

        if (this.level().isClientSide) {
            updateClientLogic()
        }

        updateRiderRotation()
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()
        updateServerLogic()
        applyRaidenFollowOwner()
    }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_RIGGING, "gui.shincolle.equip.rigging"))
        return list
    }

    override fun positionRider(passenger: Entity, moveFunction: MoveFunction) {
        if (!this.hasPassenger(passenger)) {
            return
        }

        if (passenger is EntityDestroyerIkazuchi) {
            val yOffsetEmotion = if (this.getStateEmotion(1) == 4) -0.65 else -0.45
            val baseOffset = if (this.isInSittingPose) 0.26 else 0.68
            val partPos = rotateXZByAxis(-0.2f, 0.0f, (this.yBodyRot % 360.0f) * Mth.DEG_TO_RAD, 1.0f)
            moveFunction.accept(
                passenger,
                this.getX() + partPos[1],
                this.getY() + baseOffset + yOffsetEmotion + 0.375,
                this.getZ() + partPos[0]
            )
            return
        }

        super.positionRider(passenger, moveFunction)
    }

    val passengersRidingOffset: Double
        get() {
            if (this.isInSittingPose) {
                return (if (this.getStateEmotion(1) == 4) this.getBbHeight() * 0.23f else this.getBbHeight() * 0.44f).toDouble()
            }
            return (this.getBbHeight() * 0.64f).toDouble()
        }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        val damaged = super.hurt(source, amount)
        if (damaged && !this.level().isClientSide) {
            if (this.getVehicle() is EntityDestroyerAkatsuki) {
                akatsuki.dismountAllRider()
            }
            if (this.isRaiden) {
                dismountRaiden()
            }
        }
        return damaged
    }

    override fun updateFuelState(nofuel: Boolean) {
        if (nofuel) {
            if (this.getVehicle() is EntityDestroyerAkatsuki) {
                akatsuki.dismountAllRider()
                this.stopRiding()
            }
            if (this.isRaiden) {
                dismountRaiden()
            }
        }
        super.updateFuelState(nofuel)
    }

    private fun updateClientLogic() {
        if ((this.tickCount % 4) == 0 && !this.isInSittingPose && !this.isInDeadPose && this.getEquipFlag(EQUIP_RIGGING)
            && this.riderType < 4 && this.getPassengers().stream()
                .noneMatch { o: Entity? -> EntityDestroyerIkazuchi::class.java.isInstance(o) }
        ) {
            val partPos = rotateXZByAxis(-0.42f, 0.0f, (this.yBodyRot % 360.0f) * Mth.DEG_TO_RAD, 1.0f)
            this.level().addParticle(
                ParticleTypes.SMOKE,
                this.getX() + partPos[1], this.getY() + 1.4, this.getZ() + partPos[0],
                0.0, 0.0, 0.0
            )
        }
    }

    private fun updateServerLogic() {
        if ((this.tickCount % 32) != 0) {
            return
        }

        if (!this.isRaiden) {
            this.raidenGattaiExpireTick = 0L
        }
        if (this.raidenGattaiCooldownUntilTick > 0L && this.level()
                .getGameTime() >= this.raidenGattaiCooldownUntilTick
        ) {
            this.raidenGattaiCooldownUntilTick = 0L
        }
        if (this.isRaiden && (this.isInSittingPose || this.isInDeadPose || this.getHealth() <= this.getMaxHealth() * 0.5f || this.isRaidenGattaiDurationExpired)) {
            dismountRaiden()
        }
        if (this.isRaiden && this.getPassengers().stream()
                .noneMatch { o: Entity? -> EntityDestroyerIkazuchi::class.java.isInstance(o) }
        ) {
            this.isRaiden = false
        }
        if (this.riderType == 0 && this.isRaiden && this.getMorale() < 7650) {
            this.addMorale(100)
        }
        if ((this.tickCount % 128) == 0) {
            applyBuffToPlayer()
            tryRaidenGattai()
        }
    }

    private fun updateState() {
        checkRiderType()
        checkIsRaiden()
        checkRidingState()
    }

    private fun applyBuffToPlayer() {
        if (this.isStateMarried && this.isStateRingEffect && this.getStateMinor(6) > 0) {
            if (this.ownerPlayer != null && this.distanceToSqr(this.ownerPlayer) < 256.0) {
                val amp = this.getStateMinor(0) / 45
                this.ownerPlayer.addEffect(
                    MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED,
                        80 + this.getStateMinor(0), amp, false, false
                    )
                )
            }
        }
    }

    private fun updateRiderRotation() {
        if (this.getVehicle() is EntityDestroyerAkatsuki) {
            akatsuki.syncRotateToRider()
        } else if (this.isRaiden) {
            for (rider in this.getPassengers()) {
                if (rider is LivingEntity && rider is EntityDestroyerIkazuchi) {
                    rider.yBodyRot = this.yBodyRot
                    rider.yBodyRotO = this.yBodyRotO
                    rider.yHeadRot = this.yBodyRot
                    rider.yHeadRotO = this.yBodyRotO
                    rider.setYRot(this.yBodyRot)
                    rider.yRotO = this.yBodyRotO
                }
            }
        }
    }

    private fun applyRaidenFollowOwner() {
        if (!this.isRaiden || this.isInSittingPose || this.isPassenger() || this.isInDeadPose) {
            return
        }

        val owner = this.getOwner()
        if (owner == null) {
            return
        }

        val minDist = 2.0
        val maxDist = 10.0
        val distanceSqr = this.distanceToSqr(owner)
        if (distanceSqr <= minDist * minDist) {
            this.raidenMovement.stop()
            this.raidenRecovery.reset(this.position())
            return
        }

        this.getLookControl().setLookAt(owner, 30.0f, 30.0f)
        if (distanceSqr < (maxDist * maxDist) * 256.0) {
            this.raidenMovement.moveTo(owner, 1.0)
        }
        trackRaidenFollowRecovery(owner, distanceSqr)
    }

    private fun trackRaidenFollowRecovery(owner: LivingEntity, distanceSqr: Double) {
        this.raidenRecovery.trackProgress(this.position())
        val force = this.raidenRecovery.isStuckLongerThan(RAIDEN_FOLLOW_STUCK_TICK_LIMIT)
        if (!this.raidenRecovery.shouldTryTeleportThrottled(
                force, distanceSqr,
                RAIDEN_FOLLOW_TELEPORT_DISTANCE_SQ, RAIDEN_FOLLOW_TELEPORT_COOLDOWN_TICKS
            )
        ) {
            return
        }
        if (!this.raidenMovement.teleportNearLiving(owner, 0.75)) {
            return
        }
        debugLog(
            "[SCMoveDiag] RaidenFollow teleportRecovery ship={} owner={} force={} distSq={} stuckTicks={}",
            this.getUUID(), owner.getUUID(), force, distanceSqr, this.raidenRecovery.stuckTicks()
        )
        this.raidenRecovery.reset(this.position())
    }

    private fun tryRaidenGattai() {
        if (!canAttemptGattai()) {
            return
        }

        val list = this.level().getEntitiesOfClass<EntityDestroyerIkazuchi?>(
            EntityDestroyerIkazuchi::class.java,
            this.getBoundingBox().inflate(4.0, 4.0, 4.0)
        )
        for (ikazuchi in list) {
            if (canGattaiWith(ikazuchi)) {
                ikazuchi!!.startRiding(this, true)
                beginRaidenGattai(ikazuchi)
                break
            }
        }
    }

    private fun canAttemptGattai(): Boolean {
        if (this.getStateMinor(43) > 0) {
            dismountRaiden()
            this.stopRiding()
            return false
        }
        if (this.isRaidenGattaiCooldownActive) {
            return false
        }
        if (this.isInSittingPose || this.isStateNoEquip || this.riderType > 0 || this.isRaiden || this.isPassenger()) {
            return false
        }
        return this.getHealth() > this.getMaxHealth() * 0.5f
    }

    private fun beginRaidenGattai(ikazuchi: EntityDestroyerIkazuchi) {
        this.isRaiden = true
        this.raidenRecovery.reset(this.position())
        ikazuchi.setRaiden(true)

        val expireTick: Long = this.level().getGameTime() + RAIDEN_GATTAI_DURATION_TICKS
        this.setRaidenGattaiExpireTick(expireTick)
        ikazuchi.setRaidenGattaiExpireTick(expireTick)
    }

    private fun canGattaiWith(ikazuchi: EntityDestroyerIkazuchi?): Boolean {
        if (ikazuchi == null || !ikazuchi.isAlive) {
            return false
        }
        if (this.getOwnerUUID() != ikazuchi.getOwnerUUID()) {
            return false
        }
        return ikazuchi.getRiderType() == 0 && !ikazuchi.isRaiden() && !ikazuchi.isStateNoEquip && ikazuchi.getStateMinor(
            43
        ) == 0 && !ikazuchi.isRaidenGattaiCooldownActive()
    }

    private fun checkRiderType() {
        this.riderType = 0
        if (this.getVehicle() is EntityDestroyerAkatsuki) {
            this.riderType = akatsuki.getRiderType()
        }
    }

    private fun checkRidingState() {
        if (this.riderType == 7) {
            this.ridingState = 3
        } else if (this.isRaiden) {
            this.ridingState = 2
        } else if (this.riderType == 3) {
            this.ridingState = 1
        } else {
            this.ridingState = 0
        }
    }

    private fun checkIsRaiden() {
        this.isRaiden =
            this.getPassengers().stream().anyMatch { o: Entity? -> EntityDestroyerIkazuchi::class.java.isInstance(o) }
    }

    private fun dismountRaiden() {
        var hadRaiden = this.isRaiden
        this.raidenMovement.reset()
        this.raidenRecovery.clear()
        for (rider in this.getPassengers()) {
            if (rider is EntityDestroyerIkazuchi) {
                hadRaiden = true
                rider.setRaiden(false)
                rider.startRaidenGattaiCooldown()
                rider.stopRiding()
                placeIkazuchiAfterRaidenDismount(rider)
            }
        }
        if (hadRaiden) {
            this.startRaidenGattaiCooldown()
        }
        this.isRaiden = false
    }

    fun placeIkazuchiAfterRaidenDismount(ikazuchi: EntityDestroyerIkazuchi?) {
        if (ikazuchi == null) {
            return
        }
        val dismountOffset = rotateXZByAxis(0.0f, 1.1f, (this.yBodyRot % 360.0f) * Mth.DEG_TO_RAD, 1.0f)
        ikazuchi.moveTo(
            this.getX() + dismountOffset[1],
            this.getY() + 0.1,
            this.getZ() + dismountOffset[0],
            ikazuchi.getYRot(),
            ikazuchi.getXRot()
        )
    }

    val isRaidenGattaiCooldownActive: Boolean
        get() = this.raidenGattaiCooldownUntilTick > this.level().getGameTime()

    fun setRaidenGattaiExpireTick(expireTick: Long) {
        this.raidenGattaiExpireTick = expireTick
    }

    fun startRaidenGattaiCooldown() {
        this.raidenGattaiExpireTick = 0L
        this.raidenGattaiCooldownUntilTick = max(
            this.raidenGattaiCooldownUntilTick,
            this.level().getGameTime() + RAIDEN_GATTAI_COOLDOWN_TICKS
        )
    }

    private val isRaidenGattaiDurationExpired: Boolean
        get() = this.raidenGattaiExpireTick > 0L && this.level().getGameTime() >= this.raidenGattaiExpireTick

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putLong("RaidenGattaiExpireTick", this.raidenGattaiExpireTick)
        compound.putLong("RaidenGattaiCooldownUntilTick", this.raidenGattaiCooldownUntilTick)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        this.raidenGattaiExpireTick = compound.getLong("RaidenGattaiExpireTick")
        this.raidenGattaiCooldownUntilTick = compound.getLong("RaidenGattaiCooldownUntilTick")
    }

    override fun getRiderType(): Int {
        return this.riderType
    }

    override fun setRiderType(type: Int) {
        this.riderType = type
    }

    protected override fun setFaceNormal() {
        this.faceId = FACE_EYES_OPEN
        val tick = this.tickCount and EMOTION_TICK_MASK_8BIT
        if (this.getStateEmotion(7) == 4 && tick > 160) {
            this.mouthId = mapLegacyMouth(3)
        } else {
            this.mouthId = mapLegacyMouth(0)
        }
    }

    protected override fun setFaceCry() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 128) {
            this.faceId = FACE_DOT_EYES_TEAR
            this.mouthId = mapLegacyMouth(if (tick < 64 5 else 2))
        } else {
            this.faceId = FACE_CRY
            this.mouthId = mapLegacyMouth(2)
        }
    }

    override fun setFaceDamaged() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_9BIT)
        if (tick < 200) {
            this.faceId = FACE_DOT_EYES_TEAR
            this.mouthId = mapLegacyMouth(if (tick < 60 5 else 2))
        } else if (tick < 400) {
            this.faceId = FACE_TENSION
            this.mouthId = mapLegacyMouth(if (tick < 250 0 else 4))
        } else {
            this.faceId = FACE_SOFT
            this.mouthId = mapLegacyMouth(if (tick < 450 0 else 1))
        }
    }

    override fun setFaceScorn() {
        this.faceId = FACE_EYES_HALF
        this.mouthId = mapLegacyMouth(1)
    }

    protected override fun setFaceHungry() {
        this.faceId = FACE_DESPAIR
        this.mouthId = mapLegacyMouth(2)
    }

    protected override fun setFaceAngry() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 128) {
            this.faceId = FACE_EYES_CLOSED
            this.mouthId = mapLegacyMouth(if (tick < 64 0 else 1))
        } else {
            this.faceId = FACE_EYES_HALF
            this.mouthId = mapLegacyMouth(if (tick < 170 1 else 2))
        }
    }

    protected override fun setFaceBored() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_9BIT)
        if (tick < 170) {
            this.faceId = FACE_DOT_EYES
            this.mouthId = mapLegacyMouth(if (tick < 80 0 else 4))
        } else if (tick < 340) {
            this.faceId = FACE_WINK
            this.mouthId = mapLegacyMouth(0)
        } else {
            this.faceId = FACE_EYES_OPEN
            this.mouthId = mapLegacyMouth(0)
        }
    }

    protected override fun setFaceShy() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 140) {
            this.faceId = FACE_EYES_OPEN
            this.mouthId = mapLegacyMouth(if (tick < 80 3 else 2))
        } else {
            this.faceId = FACE_WINK
            this.mouthId = mapLegacyMouth(0)
        }
    }

    protected override fun setFaceHappy() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 140) {
            this.faceId = FACE_TENSION
            this.mouthId = mapLegacyMouth(if (tick < 80 0 else 4))
        } else {
            this.faceId = FACE_WINK
            this.mouthId = mapLegacyMouth(4)
        }
    }


    override fun supportsItemPickup(): Boolean {
        return true
    }

    override fun getShipSpawnEggItem(): Item {
        return ModItems.DESTROYER_INAZUMA_SPAWN_EGG.get()
    }

    companion object {
        const val EQUIP_RIGGING: String = "equip_rigging"
        private val RAIDEN_GATTAI_DURATION_TICKS = 20L * 45L
        private val RAIDEN_GATTAI_COOLDOWN_TICKS = 20L * 20L
        private const val RAIDEN_FOLLOW_TELEPORT_COOLDOWN_TICKS = 100
        private const val RAIDEN_FOLLOW_STUCK_TICK_LIMIT = 120
        private const val RAIDEN_FOLLOW_TELEPORT_DISTANCE_SQ = 256.0
    }
}
