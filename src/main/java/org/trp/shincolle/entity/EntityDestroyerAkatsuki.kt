package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModItems
import kotlin.math.max

class EntityDestroyerAkatsuki(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level),
    IShipRiderType {
    override var riderType: Int = RIDER_TYPE_NONE
    private var akatsukiGattaiExpireTick = 0L
    private var akatsukiGattaiCooldownUntilTick = 0L

    init {
        setStateMinor(STATE_MINOR_FACTION_ID, -1)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 51)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 5)
        setStateMinor(STATE_MINOR_RARITY, 5)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeDD)
        this.modelPos = floatArrayOf(0.0f, 25.0f, 0.0f, 50.0f)
        setStateFlag(STATE_FLAG_15, false)
        setStateFlag(STATE_FLAG_16, false)
        this.isStateCanRide = true
    }

    override fun getBreedOffspring(level: ServerLevel, otherParent: AgeableMob): AgeableMob? {
        return null
    }

    override fun isFood(stack: ItemStack): Boolean {
        return false
    }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_RIGGING, "gui.shincolle.equip.rigging"))
        list.add(EquipOption(EQUIP_ANCHOR, "gui.shincolle.equip.anchor"))
        list.add(EquipOption(EQUIP_HAT, "gui.shincolle.equip.hat"))
        list.add(EquipOption(EQUIP_HAND_CANNON, "gui.shincolle.equip.cannon"))
        list.add(EquipOption(EQUIP_ARM_TORPEDO, "gui.shincolle.equip.torpedo"))
        list.add(EquipOption(EQUIP_SHOULDER_SEARCHLIGHT, "gui.shincolle.equip.shoulder_searchlight"))
        return list
    }

    override fun aiStep() {
        super.aiStep()

        checkRiderType()

        if (this.level().isClientSide) {
            updateClientEffects()
        }

        if (!this.getPassengers().isEmpty()) {
            syncRotateToRider()
        }
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()
        updateServerLogic()
        updateGattaiDurationAndCooldown()
    }

    override fun positionRider(passenger: Entity, moveFunction: MoveFunction) {
        if (!this.hasPassenger(passenger)) {
            return
        }

        val baseOffset = if (this.isInSittingPose) 0.26 else 0.68
        val yOffsetEmotion = if (this.getStateEmotion(1) == 4 || this.isInSittingPose) 0.0 else 0.1

        if (passenger is EntityDestroyerHibiki) {
            passenger.setStateEmotion(1, this.getStateEmotion(1), false)
            val partPos = rotateXZByAxis(-0.2f, 0.0f, (this.yBodyRot % 360.0f) * Mth.DEG_TO_RAD, 1.0f)
            moveFunction.accept(
                passenger,
                this.getX() + partPos[1],
                this.getY() + baseOffset + yOffsetEmotion * 2.5 - 0.4f,
                this.getZ() + partPos[0]
            )
            return
        }
        if (passenger is EntityDestroyerInazuma) {
            passenger.setStateEmotion(1, this.getStateEmotion(1), false)
            val partPos = rotateXZByAxis(-0.48f, 0.0f, (this.yBodyRot % 360.0f) * Mth.DEG_TO_RAD, 1.0f)
            moveFunction.accept(
                passenger,
                this.getX() + partPos[1],
                this.getY() + baseOffset + yOffsetEmotion * 4.5 - 0.05f,
                this.getZ() + partPos[0]
            )
            return
        }
        if (passenger is EntityDestroyerIkazuchi) {
            passenger.setStateEmotion(1, this.getStateEmotion(1), false)
            val partPos = rotateXZByAxis(-0.68f, 0.0f, (this.yBodyRot % 360.0f) * Mth.DEG_TO_RAD, 1.0f)
            moveFunction.accept(
                passenger,
                this.getX() + partPos[1],
                this.getY() + baseOffset + yOffsetEmotion * 6 + 0.4f,
                this.getZ() + partPos[0]
            )
            return
        }

        super.positionRider(passenger, moveFunction)
    }

    val passengersRidingOffset: Double
        get() {
            if (this.isInSittingPose) {
                return (if (this.getStateEmotion(1) == 4) this.getBbHeight() * -0.07f else this.getBbHeight() * 0.26f).toDouble()
            }
            return (this.getBbHeight() * 0.64f).toDouble()
        }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        val damaged = super.hurt(source, amount)
        if (damaged && !this.level().isClientSide) {
            dismountAllRider()
        }
        return damaged
    }

    private fun updateClientEffects() {
        if ((this.tickCount % 4) == 0) {
            if (!this.isInSittingPose && this.getEquipFlag(EQUIP_RIGGING)
                && this.riderType < 1
            ) {
                val addZ = if (this.isPassenger()) -0.2f else 0.0f
                val partPos = rotateXZByAxis(-0.42f + addZ, 0.0f, (this.yBodyRot % 360.0f) * Mth.DEG_TO_RAD, 1.0f)
                this.level().addParticle(
                    ParticleTypes.SMOKE,
                    this.getX() + partPos[1], this.getY() + 1.4, this.getZ() + partPos[0],
                    0.0, 0.0, 0.0
                )
            }
        }
    }

    private fun updateServerLogic() {
        if ((this.tickCount % 32) != 0) {
            return
        }

        if (this.riderType == 2 || this.riderType == 4 || this.riderType == 5 || this.riderType == 6) {
            dismountAllRider()
        }
        if (this.riderType > 0) {
            addMoraleToRider()
            if (this.morale < 7650) {
                this.addMorale(100)
            }
        }
        if ((this.tickCount % 128) == 0) {
            applyPlayerBuff()
            tryGattai()
        }
    }

    private fun applyPlayerBuff() {
        if (this.isStateMarried && this.isStateRingEffect && this.getStateMinor(6) > 0) {
            val owner = this.ownerPlayer
            if (owner != null && this.distanceToSqr(owner) < 256.0) {
                val amp = this.getStateMinor(0) / 30
                owner.addEffect(
                    MobEffectInstance(MobEffects.DIG_SPEED, Config.SHIP_BUFF_DURATION.get(), amp, false, false)
                )
            }
        }
    }

    private fun checkRiderType() {
        this.riderType = RIDER_TYPE_NONE
        var hasHibiki = false
        for (rider in this.getPassengers()) {
            if (rider is EntityDestroyerHibiki) {
                this.riderType = this.riderType or RIDER_TYPE_HIBIKI
                hasHibiki = true
            } else if (rider is EntityDestroyerInazuma) {
                this.riderType = this.riderType or RIDER_TYPE_INAZUMA
            } else if (rider is EntityDestroyerIkazuchi) {
                this.riderType = this.riderType or RIDER_TYPE_IKAZUCHI
            }
        }
        this.ridingState = if (hasHibiki) 1 else 0
    }

    private fun addMoraleToRider() {
        for (rider in this.getPassengers()) {
            if (rider is EntityShipBase && rider.morale < 7650) {
                rider.addMorale(100)
            }
            if (rider is IShipRiderType) {
                rider.riderType = this.riderType
            }
        }
    }

    private fun isGattaiCandidate(ship: EntityShipBase?): Boolean {
        if (ship == null) {
            return false
        }
        if (this.formationTeam == -1 || ship.formationTeam != this.formationTeam) {
            return false
        }
        if (!ship.isAlive) {
            return false
        }
        if (this.ownerUUID != ship.ownerUUID) {
            return false
        }
        if (ship.isInSittingPose) {
            return false
        }
        if (ship.isStateNoEquip) {
            return false
        }
        if (ship.getStateMinor(43) > 0) {
            return false
        }
        if (ship.getStateMinor(26) != 0 && ship.getStateMinor(26) != 1) {
            return false
        }
        if (ship.isPassenger()) {
            val vehicle = ship.getVehicle()
            if (vehicle === this) {
                return true
            }
            if (ship is EntityDestroyerIkazuchi && vehicle is EntityDestroyerInazuma) {
                return true
            }
            return false
        }
        return true
    }

    private fun tryGattai() {
        if (this.getStateMinor(43) > 0) {
            dismountAllRider()
            this.stopRiding()
            return
        }
        if (this.isGattaiCooldownActive) {
            return
        }
        if (this.getHealth() <= this.getMaxHealth() * 0.5f) {
            return
        }
        if (this.isInSittingPose || this.isStateNoEquip || this.riderType == RIDER_TYPE_ALL) {
            return
        }

        val ships = this.level().getEntitiesOfClass<EntityShipBase?>(
            EntityShipBase::class.java,
            this.getBoundingBox().inflate(6.0, 5.0, 6.0)
        )

        var hibiki: EntityDestroyerHibiki? = null
        var inazuma: EntityDestroyerInazuma? = null
        var ikazuchi: EntityDestroyerIkazuchi? = null

        for (ship in ships) {
            if (ship is EntityDestroyerHibiki && isGattaiCandidate(ship)) {
                hibiki = ship
            } else if (ship is EntityDestroyerInazuma && isGattaiCandidate(ship)) {
                inazuma = ship
            } else if (ship is EntityDestroyerIkazuchi && isGattaiCandidate(ship)) {
                ikazuchi = ship
            }
        }

        if (this.riderType == RIDER_TYPE_NONE) {
            if (hibiki != null) {
                hibiki.startRiding(this, true)
                if (inazuma != null) {
                    inazuma.startRiding(this, true)
                    if (ikazuchi != null) {
                        ikazuchi.startRiding(this, true)
                    }
                }
            }
        } else if (this.riderType == RIDER_TYPE_HIBIKI) {
            if (inazuma != null) {
                inazuma.startRiding(this, true)
                if (ikazuchi != null) {
                    ikazuchi.startRiding(this, true)
                }
            }
        } else if (this.riderType == (RIDER_TYPE_HIBIKI or RIDER_TYPE_INAZUMA)) {
            if (ikazuchi != null) {
                ikazuchi.startRiding(this, true)
            }
        }
    }

    fun syncRotateToRider() {
        for (rider in this.getPassengers()) {
            if (rider is LivingEntity) {
                rider.yBodyRot = this.yBodyRot
                rider.yBodyRotO = this.yBodyRotO
                rider.yHeadRot = this.yBodyRot
                rider.yHeadRotO = this.yBodyRotO
                rider.setYRot(this.yBodyRot)
                rider.yRotO = this.yBodyRotO
            }
        }
    }

    fun dismountAllRider() {
        val wasFullyCombined = (this.riderType == RIDER_TYPE_ALL)
        this.riderType = RIDER_TYPE_NONE
        this.ridingState = 0
        for (rider in this.getPassengers()) {
            if (rider is IShipRiderType) {
                rider.riderType = RIDER_TYPE_NONE
            }
            if (rider is EntityShipBase) {
                rider.ridingState = 0
            }
        }
        this.ejectPassengers()
        if (wasFullyCombined) {
            startGattaiCooldown()
        }
    }

    private fun updateGattaiDurationAndCooldown() {
        if (this.level().isClientSide) {
            return
        }

        val isFullyCombined = (this.riderType == RIDER_TYPE_ALL)

        if (isFullyCombined) {
            if (this.akatsukiGattaiExpireTick == 0L) {
                this.akatsukiGattaiExpireTick = this.level().getGameTime() + AKATSUKI_GATTAI_DURATION_TICKS
            }
            if (this.isInSittingPose || this.isInDeadPose || this.getHealth() <= this.getMaxHealth() * 0.5f || this.isGattaiDurationExpired) {
                dismountAllRider()
            }
        } else {
            this.akatsukiGattaiExpireTick = 0L
            if (this.riderType > 0 && this.riderType != RIDER_TYPE_HIBIKI && this.riderType != (RIDER_TYPE_HIBIKI or RIDER_TYPE_INAZUMA)) {
                dismountAllRider()
            }
        }

        if (this.akatsukiGattaiCooldownUntilTick > 0L && this.level()
                .getGameTime() >= this.akatsukiGattaiCooldownUntilTick
        ) {
            this.akatsukiGattaiCooldownUntilTick = 0L
        }
    }

    private val isGattaiDurationExpired: Boolean
        get() = this.akatsukiGattaiExpireTick > 0L && this.level().getGameTime() >= this.akatsukiGattaiExpireTick

    val isGattaiCooldownActive: Boolean
        get() = this.akatsukiGattaiCooldownUntilTick > this.level().getGameTime()

    fun startGattaiCooldown() {
        this.akatsukiGattaiExpireTick = 0L
        this.akatsukiGattaiCooldownUntilTick = max(
            this.akatsukiGattaiCooldownUntilTick,
            this.level().getGameTime() + AKATSUKI_GATTAI_COOLDOWN_TICKS
        )
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putLong("AkatsukiGattaiExpireTick", this.akatsukiGattaiExpireTick)
        compound.putLong("AkatsukiGattaiCooldownUntilTick", this.akatsukiGattaiCooldownUntilTick)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        this.akatsukiGattaiExpireTick = compound.getLong("AkatsukiGattaiExpireTick")
        this.akatsukiGattaiCooldownUntilTick = compound.getLong("AkatsukiGattaiCooldownUntilTick")
    }



    override fun setFaceNormal() {
        this.faceId = FACE_EYES_OPEN
        val tick = this.tickCount and EMOTION_TICK_MASK_8BIT
        if (this.getStateEmotion(7) == 4 && tick > 160) {
            this.mouthId = mapLegacyMouth(3)
        } else {
            this.mouthId = mapLegacyMouth(0)
        }
    }

    override fun setFaceCry() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 128) {
            this.faceId = FACE_DOT_EYES_TEAR
            this.mouthId = mapLegacyMouth(if (tick < 64) 5 else 2)
        } else {
            this.faceId = FACE_CRY
            this.mouthId = mapLegacyMouth(2)
        }
    }

    override fun setFaceDamaged() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_9BIT)
        if (tick < 200) {
            this.faceId = FACE_DOT_EYES_TEAR
            this.mouthId = mapLegacyMouth(if (tick < 60) 5 else 2)
        } else if (tick < 400) {
            this.faceId = FACE_TENSION
            this.mouthId = mapLegacyMouth(if (tick < 250) 0 else 4)
        } else {
            this.faceId = FACE_SOFT
            this.mouthId = mapLegacyMouth(if (tick < 450) 0 else 1)
        }
    }

    override fun setFaceScorn() {
        this.faceId = FACE_EYES_HALF
        this.mouthId = mapLegacyMouth(1)
    }

    override fun setFaceHungry() {
        this.faceId = FACE_DESPAIR
        this.mouthId = mapLegacyMouth(2)
    }

    override fun setFaceAngry() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 128) {
            this.faceId = FACE_EYES_CLOSED
            this.mouthId = mapLegacyMouth(if (tick < 64) 0 else 1)
        } else {
            this.faceId = FACE_EYES_HALF
            this.mouthId = mapLegacyMouth(if (tick < 170) 1 else 2)
        }
    }

    override fun setFaceBored() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_9BIT)
        if (tick < 170) {
            this.faceId = FACE_DOT_EYES
            this.mouthId = mapLegacyMouth(if (tick < 80) 0 else 4)
        } else if (tick < 340) {
            this.faceId = FACE_WINK
            this.mouthId = mapLegacyMouth(0)
        } else {
            this.faceId = FACE_EYES_OPEN
            this.mouthId = mapLegacyMouth(0)
        }
    }

    override fun setFaceShy() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 140) {
            this.faceId = FACE_EYES_OPEN
            this.mouthId = mapLegacyMouth(if (tick < 80) 3 else 2)
        } else {
            this.faceId = FACE_WINK
            this.mouthId = mapLegacyMouth(0)
        }
    }

    override fun setFaceHappy() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 140) {
            this.faceId = FACE_TENSION
            this.mouthId = mapLegacyMouth(if (tick < 80) 0 else 4)
        } else {
            this.faceId = FACE_WINK
            this.mouthId = mapLegacyMouth(4)
        }
    }


    override fun supportsItemPickup(): Boolean {
        return true
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.DESTROYER_AKATSUKI_SPAWN_EGG.get()

    companion object {
        const val EQUIP_RIGGING: String = "equip_rigging"
        const val EQUIP_ANCHOR: String = "equip_anchor"
        const val EQUIP_HAT: String = "equip_hat"
        const val EQUIP_HAND_CANNON: String = "equip_hand_cannon"
        const val EQUIP_ARM_TORPEDO: String = "equip_arm_torpedo"
        const val EQUIP_SHOULDER_SEARCHLIGHT: String = "equip_shoulder_searchlight"

        private const val STATE_FLAG_15 = 15
        private const val STATE_FLAG_16 = 16
        private const val RIDER_TYPE_NONE = 0
        private const val RIDER_TYPE_HIBIKI = 1
        private const val RIDER_TYPE_INAZUMA = 2
        private const val RIDER_TYPE_IKAZUCHI = 4
        private const val RIDER_TYPE_ALL = 7

        private val AKATSUKI_GATTAI_DURATION_TICKS = 20L * 60L
        private val AKATSUKI_GATTAI_COOLDOWN_TICKS = 20L * 120L

        fun createAttributes(): AttributeSupplier.Builder {
            return createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 160.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.FOLLOW_RANGE, 36.0)
                .add(Attributes.STEP_HEIGHT, 1.0)
        }
    }
}
