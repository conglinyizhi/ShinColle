package org.trp.shincolle.entity

import org.trp.shincolle.Config
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.utility.CalcHelper
import kotlin.math.max

class EntityDestroyerIkazuchi(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level),
    IShipRiderType {
    override var riderType: Int
    var isRaiden: Boolean
    private var raidenGattaiExpireTick: Long
    private var raidenGattaiCooldownUntilTick: Long

    init {
        this.modelPos = floatArrayOf(0f, 25f, 0f, 50f)
        setStateMinor(STATE_MINOR_FACTION_ID, -1)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 53)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 5)
        setStateMinor(STATE_MINOR_RARITY, 2)
        this.isStateGuiBtn3 = false
        this.isStateGuiBtn4 = false
        this.isStateCanRide = true
        this.riderType = 0
        this.isRaiden = false
        this.raidenGattaiExpireTick = 0L
        this.raidenGattaiCooldownUntilTick = 0L
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
    }

    override fun getBreedOffspring(level: ServerLevel, otherParent: AgeableMob): AgeableMob? {
        return null
    }

    override fun isFood(stack: ItemStack): Boolean {
        return false
    }

    override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        // mobInteract
        val vehicle = this.getVehicle()
        if (this.isRaiden && vehicle is EntityShipBase) {
            return vehicle.mobInteract(player, hand)
        }
        return super.mobInteract(player, hand)
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
    }

    val passengersRidingOffset: Double
        get() {
            if (this.isInSittingPose) {
                return (this.bbHeight * 0.23f).toDouble()
            }
            return (this.bbHeight * 0.64f).toDouble()
        }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        val damaged = super.hurt(source, amount)
        if (damaged && !this.level().isClientSide) {
            val akatsuki = this.getVehicle() as? EntityDestroyerAkatsuki
            if (akatsuki != null) {
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
            val akatsuki = this.getVehicle() as? EntityDestroyerAkatsuki
            if (akatsuki != null) {
                akatsuki.dismountAllRider()
            }
            if (this.isRaiden) {
                dismountRaiden()
            }
        }
        super.updateFuelState(nofuel)
    }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_RIGGING, "gui.shincolle.equip.rigging"))
        list.add(EquipOption(EQUIP_ANCHOR, "gui.shincolle.equip.anchor"))
        return list
    }

    override fun supportsItemPickup(): Boolean {
        return true
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.DESTROYER_IKAZUCHI_SPAWN_EGG.get()

    override fun migrateLegacyStateFlags(stateFlags: Int) {
    }

    private fun updateClientLogic() {
        if ((this.tickCount % 4) == 0 && !this.isInSittingPose && !this.isInDeadPose && this.getEquipFlag(EQUIP_RIGGING)) {
            val partPos = CalcHelper.rotateXZByAxis(-0.42f, 0.0f, (this.yBodyRot % 360.0f) * Mth.DEG_TO_RAD, 1.0f)
            this.level().addParticle(
                ParticleTypes.SMOKE,
                this.x + partPos[1], this.y + 1.4, this.z + partPos[0],
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
        if (this.isRaiden && this.getVehicle() !is EntityDestroyerInazuma) {
            this.isRaiden = false
        }
        if (this.isRaiden && (this.isInSittingPose || this.isInDeadPose || this.isRaidenGattaiDurationExpired)) {
            dismountRaiden()
        }
        if (this.riderType == 0 && this.isRaiden && this.morale < 7650) {
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
            val ownerPlayer = this.ownerPlayer
            if (ownerPlayer != null && this.distanceToSqr(ownerPlayer) < 256.0) {
                val amp = this.getStateMinor(0) / 50
                ownerPlayer.addEffect(
                    MobEffectInstance(
                        MobEffects.DAMAGE_BOOST,
                        Config.SHIP_BUFF_DURATION.get(), amp, false, false
                    )
                )
            }
        }
    }

    private fun updateRiderRotation() {
        val akatsuki = this.getVehicle() as? EntityDestroyerAkatsuki
            if (akatsuki != null) {
                akatsuki.syncRotateToRider()
        } else if (this.getVehicle() is EntityDestroyerInazuma) {
                val inazuma = this.getVehicle() as EntityDestroyerInazuma
                this.yBodyRot = inazuma.yBodyRot
            this.yBodyRotO = inazuma.yBodyRotO
            this.yHeadRot = inazuma.yBodyRot
            this.yHeadRotO = inazuma.yBodyRotO
            this.yRot = inazuma.yBodyRot
            this.yRotO = inazuma.yBodyRotO
        }
    }

    private fun tryRaidenGattai() {
        if (!canAttemptGattai()) {
            return
        }

        val list = this.level().getEntitiesOfClass<EntityDestroyerInazuma?>(
            EntityDestroyerInazuma::class.java,
            this.boundingBox.inflate(4.0, 4.0, 4.0)
        )
        for (inazuma in list) {
            if (canGattaiWith(inazuma)) {
                this.startRiding(inazuma, true)
                beginRaidenGattai(inazuma!!)
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
        return !this.isInSittingPose && !this.isPassenger() && this.getEquipFlag(EQUIP_RIGGING)
                && this.riderType <= 0 && !this.isRaiden && this.health > this.maxHealth * 0.5f
    }

    private fun beginRaidenGattai(inazuma: EntityDestroyerInazuma) {
        this.isRaiden = true
        inazuma.isRaiden = true

        val expireTick: Long = this.level().getGameTime() + RAIDEN_GATTAI_DURATION_TICKS
        this.setRaidenGattaiExpireTick(expireTick)
        inazuma.setRaidenGattaiExpireTick(expireTick)
    }

    private fun canGattaiWith(partner: EntityDestroyerInazuma?): Boolean {
        if (partner == null || !partner.isAlive) {
            return false
        }
        if (this.ownerUUID != partner.ownerUUID) {
            return false
        }
        return partner.riderType == 0 && !partner.isRaiden && !partner.isStateNoEquip && partner.getStateMinor(
            43
        ) == 0 && !partner.isRaidenGattaiCooldownActive
    }

    private fun checkRiderType() {
        this.riderType = 0
        val akatsuki = this.getVehicle() as? EntityDestroyerAkatsuki
            if (akatsuki != null) {
                this.riderType = akatsuki.riderType
        }
    }

    private fun checkIsRaiden() {
        this.isRaiden = this.getVehicle() is EntityDestroyerInazuma
    }

    private fun checkRidingState() {
        if (this.riderType == 7 || this.isRaiden) {
            this.ridingState = 2
        } else {
            this.ridingState = 0
        }
    }

    private fun dismountRaiden() {
        val inazuma = this.getVehicle() as? EntityDestroyerInazuma
            if (inazuma != null) {
                this.startRaidenGattaiCooldown()
                inazuma.startRaidenGattaiCooldown()
            this.isRaiden = false
            inazuma.isRaiden = false
            this.stopRiding()
            inazuma.placeIkazuchiAfterRaidenDismount(this)
        }
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


    override fun setFaceNormal() {
        this.faceId = FACE_EYES_OPEN
        val tick = this.tickCount and EMOTION_TICK_MASK_8BIT
        if (this.emotionSecondary == EMOTION_BORED && tick > 160) {
            this.mouthId = mapLegacyMouth(4)
        } else {
            this.mouthId = mapLegacyMouth(0)
        }
    }

    override fun setFaceCry() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 128) {
            this.faceId = FACE_DOT_EYES_TEAR
            this.mouthId = mapLegacyMouth(if (tick < 64) 2 else 5)
        } else {
            this.faceId = FACE_CRY
            this.mouthId = mapLegacyMouth(if (tick < 190) 2 else 5)
        }
    }

    override fun setFaceDamaged() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_9BIT)
        if (tick < 200) {
            this.faceId = FACE_DOT_EYES_TEAR
            this.mouthId = mapLegacyMouth(if (tick < 60) 4 else 5)
        } else if (tick < 400) {
            this.faceId = FACE_TENSION
            this.mouthId = mapLegacyMouth(if (tick < 250) 4 else 5)
        } else {
            this.faceId = FACE_SOFT
            this.mouthId = mapLegacyMouth(if (tick < 450) 4 else 5)
        }
    }

    override fun setFaceScorn() {
        this.faceId = FACE_EYES_HALF
        this.mouthId = mapLegacyMouth(1)
    }

    override fun setFaceHungry() {
        this.faceId = FACE_DESPAIR
        this.mouthId = mapLegacyMouth(5)
    }

    override fun setFaceAngry() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 128) {
            this.faceId = FACE_EYES_CLOSED
            this.mouthId = mapLegacyMouth(if (tick < 64) 0 else 4)
        } else {
            this.faceId = FACE_EYES_HALF
            this.mouthId = mapLegacyMouth(if (tick < 170) 1 else 4)
        }
    }

    override fun setFaceBored() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_9BIT)
        if (tick < 170) {
            this.faceId = FACE_EYES_CLOSED
            this.mouthId = mapLegacyMouth(if (tick < 80) 0 else 4)
        } else if (tick < 340) {
            this.faceId = FACE_WINK
            this.mouthId = mapLegacyMouth(if (tick < 250) 0 else 4)
        } else {
            this.faceId = FACE_EYES_OPEN
            this.mouthId = mapLegacyMouth(if (tick < 420) 5 else 4)
        }
    }

    override fun setFaceShy() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        this.faceId = FACE_EYES_OPEN
        this.mouthId = mapLegacyMouth(if (tick < 150) 2 else 4)
    }

    override fun setFaceHappy() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 140) {
            this.faceId = FACE_TENSION
            this.mouthId = mapLegacyMouth(if (tick < 80) 4 else 5)
        } else {
            this.faceId = FACE_WINK
            this.mouthId = mapLegacyMouth(4)
        }
    }

    companion object {
        const val EQUIP_RIGGING: String = "equip_rigging"
        const val EQUIP_ANCHOR: String = "equip_anchor"
        private val RAIDEN_GATTAI_DURATION_TICKS = 20L * 45L
        private val RAIDEN_GATTAI_COOLDOWN_TICKS = 20L * 20L

        @JvmStatic
        fun createAttributes(): AttributeSupplier.Builder {
            return createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 120.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.FOLLOW_RANGE, 36.0)
                .add(Attributes.STEP_HEIGHT, 1.0)
        }
    }
}
