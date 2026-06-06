package org.trp.shincolle.entity

import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModItems

class EntityDestroyerHime(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    init {
        this.modelPos = floatArrayOf(0f, 25f, 0f, 50f)
        setStateMinor(STATE_MINOR_FACTION_ID, 10)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 27)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 5)
        setStateMinor(STATE_MINOR_RARITY, 6)
        this.isStateCanRide = true
    }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_RIGGING, "gui.shincolle.equip.rigging"))
        list.add(EquipOption(EQUIP_HAT, "gui.shincolle.equip.hat"))
        list.add(EquipOption(EQUIP_CANNON, "gui.shincolle.equip.cannon"))
        list.add(EquipOption(EQUIP_BELT, "gui.shincolle.equip.belt"))
        list.add(EquipOption(EQUIP_LEG, "gui.shincolle.equip.leg"))
        list.add(EquipOption(EQUIP_HAND, "gui.shincolle.equip.hand"))
        return list
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()

        if ((this.tickCount % 128) == 0) {
            applyBuffToOwner()
        }
    }

    val passengersRidingOffset: Double
        get() {
            if (this.isInSittingPose) {
                if (this.getStateEmotion(1) == 4) {
                    return 0.0
                }
                return (this.getBbHeight() * 0.62f).toDouble()
            }
            return (this.getBbHeight() * 0.76f).toDouble()
        }

    private fun applyBuffToOwner() {
        if (this.isStateMarried && this.isStateRingEffect && this.getStateMinor(6) > 0) {
            if (this.ownerPlayer != null && this.distanceToSqr(this.ownerPlayer) < 256.0) {
                val ampSpeed = this.getStateMinor(0) / 45 + 1
                val ampHaste = this.getStateMinor(0) / 30
                this.ownerPlayer.addEffect(
                    MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED,
                        80 + this.getStateMinor(0), ampSpeed, false, false
                    )
                )
                this.ownerPlayer.addEffect(
                    MobEffectInstance(
                        MobEffects.DIG_SPEED,
                        80 + this.getStateMinor(0), ampHaste, false, false
                    )
                )
            }
        }
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
        get() = ModItems.DESTROYER_HIME_SPAWN_EGG.get()

    companion object {
        const val EQUIP_RIGGING: String = "equip_rigging"
        const val EQUIP_HAT: String = "equip_hat"
        const val EQUIP_CANNON: String = "equip_cannon"
        const val EQUIP_BELT: String = "equip_belt"
        const val EQUIP_LEG: String = "equip_leg"
        const val EQUIP_HAND: String = "equip_hand"
    }
}

