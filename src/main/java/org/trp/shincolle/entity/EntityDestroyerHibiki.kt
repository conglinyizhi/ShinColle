package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModItems

class EntityDestroyerHibiki(type: EntityType<out TamableAnimal?>?, level: Level?) : EntityShipBase(type, level),
    IShipRiderType {
    private var riderType: Int

    init {
        this.modelPos = floatArrayOf(0f, 25f, 0f, 50f)
        this.isStateCanRide = true
        setStateMinor(STATE_MINOR_FACTION_ID, -1)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 52)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 5)
        setStateMinor(STATE_MINOR_RARITY, 5)
        this.isStateGuiBtn4 = false
        this.riderType = 0
    }

    override fun aiStep() {
        super.aiStep()

        checkRiderType()
        checkRidingState()

        if (this.level().isClientSide) {
            updateClientLogic()
        }

        val akatsuki = this.akatsukiRiding
        if (akatsuki != null) {
            akatsuki.syncRotateToRider()
        }
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()
        updateServerLogic()
    }

    val passengersRidingOffset: Double
        get() {
            if (this.isInSittingPose) {
                return (if (this.getStateEmotion(1) == 4) this.getBbHeight() * -0.07f else this.getBbHeight() * 0.26f).toDouble()
            }
            return (this.getBbHeight() * 0.64f).toDouble()
        }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_RIGGING, "gui.shincolle.equip.rigging"))
        list.add(EquipOption(EQUIP_TORPEDO, "gui.shincolle.equip.torpedo"))
        list.add(EquipOption(EQUIP_HAIR_FRONT_1, "gui.shincolle.equip.hair_front_1"))
        list.add(EquipOption(EQUIP_HAIR_FRONT_2, "gui.shincolle.equip.hair_front_2"))
        list.add(EquipOption(EQUIP_HAIR_FRONT_3, "gui.shincolle.equip.hair_front_3"))
        return list
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        val damaged = super.hurt(source, amount)
        if (damaged && !this.level().isClientSide) {
            val akatsuki = this.akatsukiRiding
            if (akatsuki != null) {
                akatsuki.dismountAllRider()
            }
        }
        return damaged
    }

    private fun updateServerLogic() {
        if ((this.tickCount % 32) != 0) {
            return
        }

        if ((this.tickCount % 128) == 0) {
            applyBuffToOwner()
        }
    }

    private fun updateClientLogic() {
        if ((this.tickCount % 4) == 0) {
            spawnEngineParticles()
        }
    }

    private fun applyBuffToOwner() {
        if (this.isStateMarried && this.isStateRingEffect && this.getStateMinor(6) > 0) {
            if (this.ownerPlayer != null && this.distanceToSqr(this.ownerPlayer) < 256.0) {
                val amp = this.getStateMinor(0) / 45 + 1
                this.ownerPlayer.addEffect(
                    MobEffectInstance(
                        MobEffects.JUMP,
                        80 + this.getStateMinor(0), amp, false, false
                    )
                )
            }
        }
    }

    private fun spawnEngineParticles() {
        val canSpawn = !this.isInSittingPose && this.getEquipFlag(EQUIP_RIGGING) && this.riderType < 2
        if (canSpawn) {
            val partPos = rotateXZByAxis(-0.42f, 0.0f, (this.yBodyRot % 360.0f) * Mth.DEG_TO_RAD, 1.0f)
            this.level().addParticle(
                ParticleTypes.SMOKE,
                this.getX() + partPos[1], this.getY() + 1.4, this.getZ() + partPos[0],
                0.0, 0.0, 0.0
            )
        }
    }

    private fun checkRiderType() {
        this.riderType = 0
        val akatsuki = this.akatsukiRiding
        if (akatsuki != null) {
            this.riderType = akatsuki.getRiderType()
        }
    }

    private fun checkRidingState() {
        if (this.riderType > 1) {
            this.ridingState = 2
        } else if (this.riderType == 1) {
            this.ridingState = 1
        } else {
            this.ridingState = 0
        }
    }

    private val akatsukiRiding: EntityDestroyerAkatsuki?
        get() {
            if (this.getVehicle() is EntityDestroyerAkatsuki) {
                return akatsuki
            }
            return null
        }

    override fun getRiderType(): Int {
        return this.riderType
    }

    override fun setRiderType(type: Int) {
        this.riderType = type
    }

    override fun setFaceNormal() {
        this.faceId = FACE_EYES_OPEN
        val tick = this.tickCount and EMOTION_TICK_MASK_8BIT
        if (this.getStateEmotion(7) == 4 && tick > 200) {
            this.mouthId = mapLegacyMouth(0)
        } else {
            this.mouthId = mapLegacyMouth(3)
        }
    }

    override fun setFaceCry() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 128) {
            this.faceId = FACE_DOT_EYES_TEAR
            this.mouthId = mapLegacyMouth(if (tick < 64) 2 else 1)
        } else {
            this.faceId = FACE_CRY
            this.mouthId = mapLegacyMouth(2)
        }
    }

    override fun setFaceDamaged() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_9BIT)
        if (tick < 200) {
            this.faceId = FACE_DOT_EYES_TEAR
            this.mouthId = mapLegacyMouth(if (tick < 60) 2 else 1)
        } else if (tick < 400) {
            this.faceId = FACE_TENSION
            this.mouthId = mapLegacyMouth(if (tick < 250) 0 else 3)
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
            this.mouthId = mapLegacyMouth(if (tick < 64) 3 else 1)
        } else {
            this.faceId = FACE_EYES_HALF
            this.mouthId = mapLegacyMouth(if (tick < 170) 1 else 3)
        }
    }

    override fun setFaceBored() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_9BIT)
        if (tick < 170) {
            this.faceId = FACE_EYES_CLOSED
            this.mouthId = mapLegacyMouth(if (tick < 80) 0 else 3)
        } else if (tick < 340) {
            this.faceId = FACE_WINK
            this.mouthId = mapLegacyMouth(if (tick < 250) 0 else 3)
        } else {
            this.faceId = FACE_EYES_OPEN
            this.mouthId = mapLegacyMouth(if (tick < 420) 0 else 3)
        }
    }

    override fun setFaceShy() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        this.faceId = FACE_EYES_OPEN
        this.mouthId = mapLegacyMouth(if (tick < 150) 3 else 2)
    }

    override fun setFaceHappy() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 140) {
            this.faceId = FACE_TENSION
            this.mouthId = mapLegacyMouth(if (tick < 80) 3 else 4)
        } else {
            this.faceId = FACE_WINK
            this.mouthId = mapLegacyMouth(0)
        }
    }


    override fun supportsItemPickup(): Boolean {
        return true
    }

    override fun getShipSpawnEggItem(): Item {
        return ModItems.DESTROYER_HIBIKI_SPAWN_EGG.get()
    }

    companion object {
        const val EQUIP_RIGGING: String = "equip_rigging"
        const val EQUIP_TORPEDO: String = "equip_torpedo"
        const val EQUIP_HAIR_FRONT_1: String = "equip_hair_front_1"
        const val EQUIP_HAIR_FRONT_2: String = "equip_hair_front_2"
        const val EQUIP_HAIR_FRONT_3: String = "equip_hair_front_3"
    }
}

