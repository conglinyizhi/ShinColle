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
        setModelPos(floatArrayOf(0f, 25f, 0f, 50f))
        setStateCanRide(true)
        setStateMinor(STATE_MINOR_FACTION_ID, -1)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 52)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 5)
        setStateMinor(STATE_MINOR_RARITY, 5)
        setStateGuiBtn4(false)
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
            if (this.getIsSitting()) {
                return (if (this.getStateEmotion(1) == 4) this.getBbHeight() * -0.07f else this.getBbHeight() * 0.26f).toDouble()
            }
            return (this.getBbHeight() * 0.64f).toDouble()
        }

    override fun getEquipOptions(): MutableList<EquipOption?> {
        val list: MutableList<EquipOption?> = ArrayList<EquipOption?>(super.getEquipOptions())
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
        if (this.isStateMarried() && this.isStateRingEffect() && this.getStateMinor(6) > 0) {
            if (this.getOwnerPlayer() != null && this.distanceToSqr(this.getOwnerPlayer()) < 256.0) {
                val amp = this.getStateMinor(0) / 45 + 1
                this.getOwnerPlayer().addEffect(
                    MobEffectInstance(
                        MobEffects.JUMP,
                        80 + this.getStateMinor(0), amp, false, false
                    )
                )
            }
        }
    }

    private fun spawnEngineParticles() {
        val canSpawn = !this.getIsSitting() && this.getEquipFlag(EQUIP_RIGGING) && this.riderType < 2
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
            this.setRidingState(2)
        } else if (this.riderType == 1) {
            this.setRidingState(1)
        } else {
            this.setRidingState(0)
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

    protected override fun setFaceNormal() {
        this.setFaceId(FACE_EYES_OPEN)
        val tick = this.tickCount and EMOTION_TICK_MASK_8BIT
        if (this.getStateEmotion(7) == 4 && tick > 200) {
            this.setMouthId(mapLegacyMouth(0))
        } else {
            this.setMouthId(mapLegacyMouth(3))
        }
    }

    protected override fun setFaceCry() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 128) {
            this.setFaceId(FACE_DOT_EYES_TEAR)
            this.setMouthId(mapLegacyMouth(if (tick < 64) 2 else 1))
        } else {
            this.setFaceId(FACE_CRY)
            this.setMouthId(mapLegacyMouth(2))
        }
    }

    override fun setFaceDamaged() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_9BIT)
        if (tick < 200) {
            this.setFaceId(FACE_DOT_EYES_TEAR)
            this.setMouthId(mapLegacyMouth(if (tick < 60) 2 else 1))
        } else if (tick < 400) {
            this.setFaceId(FACE_TENSION)
            this.setMouthId(mapLegacyMouth(if (tick < 250) 0 else 3))
        } else {
            this.setFaceId(FACE_SOFT)
            this.setMouthId(mapLegacyMouth(if (tick < 450) 0 else 1))
        }
    }

    override fun setFaceScorn() {
        this.setFaceId(FACE_EYES_HALF)
        this.setMouthId(mapLegacyMouth(1))
    }

    protected override fun setFaceHungry() {
        this.setFaceId(FACE_DESPAIR)
        this.setMouthId(mapLegacyMouth(2))
    }

    protected override fun setFaceAngry() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 128) {
            this.setFaceId(FACE_EYES_CLOSED)
            this.setMouthId(mapLegacyMouth(if (tick < 64) 3 else 1))
        } else {
            this.setFaceId(FACE_EYES_HALF)
            this.setMouthId(mapLegacyMouth(if (tick < 170) 1 else 3))
        }
    }

    protected override fun setFaceBored() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_9BIT)
        if (tick < 170) {
            this.setFaceId(FACE_EYES_CLOSED)
            this.setMouthId(mapLegacyMouth(if (tick < 80) 0 else 3))
        } else if (tick < 340) {
            this.setFaceId(FACE_WINK)
            this.setMouthId(mapLegacyMouth(if (tick < 250) 0 else 3))
        } else {
            this.setFaceId(FACE_EYES_OPEN)
            this.setMouthId(mapLegacyMouth(if (tick < 420) 0 else 3))
        }
    }

    protected override fun setFaceShy() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        this.setFaceId(FACE_EYES_OPEN)
        this.setMouthId(mapLegacyMouth(if (tick < 150) 3 else 2))
    }

    protected override fun setFaceHappy() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 140) {
            this.setFaceId(FACE_TENSION)
            this.setMouthId(mapLegacyMouth(if (tick < 80) 3 else 4))
        } else {
            this.setFaceId(FACE_WINK)
            this.setMouthId(mapLegacyMouth(0))
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

