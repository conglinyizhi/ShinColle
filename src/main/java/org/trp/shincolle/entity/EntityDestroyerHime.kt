package org.trp.shincolle.entity

import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModItems

class EntityDestroyerHime(type: EntityType<out TamableAnimal?>?, level: Level?) : EntityShipBase(type, level) {
    init {
        setModelPos(floatArrayOf(0f, 25f, 0f, 50f))
        setStateMinor(STATE_MINOR_FACTION_ID, 10)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 27)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 5)
        setStateMinor(STATE_MINOR_RARITY, 6)
        setStateCanRide(true)
    }

    override fun getEquipOptions(): MutableList<EquipOption?> {
        val list: MutableList<EquipOption?> = ArrayList<EquipOption?>(super.getEquipOptions())
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
            if (this.getIsSitting()) {
                if (this.getStateEmotion(1) == 4) {
                    return 0.0
                }
                return (this.getBbHeight() * 0.62f).toDouble()
            }
            return (this.getBbHeight() * 0.76f).toDouble()
        }

    private fun applyBuffToOwner() {
        if (this.isStateMarried() && this.isStateRingEffect() && this.getStateMinor(6) > 0) {
            if (this.getOwnerPlayer() != null && this.distanceToSqr(this.getOwnerPlayer()) < 256.0) {
                val ampSpeed = this.getStateMinor(0) / 45 + 1
                val ampHaste = this.getStateMinor(0) / 30
                this.getOwnerPlayer().addEffect(
                    MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED,
                        80 + this.getStateMinor(0), ampSpeed, false, false
                    )
                )
                this.getOwnerPlayer().addEffect(
                    MobEffectInstance(
                        MobEffects.DIG_SPEED,
                        80 + this.getStateMinor(0), ampHaste, false, false
                    )
                )
            }
        }
    }

    protected override fun setFaceNormal() {
        this.setFaceId(FACE_EYES_OPEN)
        val tick = this.tickCount and EMOTION_TICK_MASK_8BIT
        if (this.getStateEmotion(7) == 4 && tick > 160) {
            this.setMouthId(mapLegacyMouth(3))
        } else {
            this.setMouthId(mapLegacyMouth(0))
        }
    }

    protected override fun setFaceCry() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 128) {
            this.setFaceId(FACE_DOT_EYES_TEAR)
            this.setMouthId(mapLegacyMouth(if (tick < 64) 5 else 2))
        } else {
            this.setFaceId(FACE_CRY)
            this.setMouthId(mapLegacyMouth(2))
        }
    }

    override fun setFaceDamaged() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_9BIT)
        if (tick < 200) {
            this.setFaceId(FACE_DOT_EYES_TEAR)
            this.setMouthId(mapLegacyMouth(if (tick < 60) 5 else 2))
        } else if (tick < 400) {
            this.setFaceId(FACE_TENSION)
            this.setMouthId(mapLegacyMouth(if (tick < 250) 0 else 4))
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
            this.setMouthId(mapLegacyMouth(if (tick < 64) 0 else 1))
        } else {
            this.setFaceId(FACE_EYES_HALF)
            this.setMouthId(mapLegacyMouth(if (tick < 170) 1 else 2))
        }
    }

    protected override fun setFaceBored() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_9BIT)
        if (tick < 170) {
            this.setFaceId(FACE_DOT_EYES)
            this.setMouthId(mapLegacyMouth(if (tick < 80) 0 else 4))
        } else if (tick < 340) {
            this.setFaceId(FACE_WINK)
            this.setMouthId(mapLegacyMouth(0))
        } else {
            this.setFaceId(FACE_EYES_OPEN)
            this.setMouthId(mapLegacyMouth(0))
        }
    }

    protected override fun setFaceShy() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 140) {
            this.setFaceId(FACE_EYES_OPEN)
            this.setMouthId(mapLegacyMouth(if (tick < 80) 3 else 2))
        } else {
            this.setFaceId(FACE_WINK)
            this.setMouthId(mapLegacyMouth(0))
        }
    }

    protected override fun setFaceHappy() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 140) {
            this.setFaceId(FACE_TENSION)
            this.setMouthId(mapLegacyMouth(if (tick < 80) 0 else 4))
        } else {
            this.setFaceId(FACE_WINK)
            this.setMouthId(mapLegacyMouth(4))
        }
    }


    override fun supportsItemPickup(): Boolean {
        return true
    }

    override fun getShipSpawnEggItem(): Item {
        return ModItems.DESTROYER_HIME_SPAWN_EGG.get()
    }

    companion object {
        const val EQUIP_RIGGING: String = "equip_rigging"
        const val EQUIP_HAT: String = "equip_hat"
        const val EQUIP_CANNON: String = "equip_cannon"
        const val EQUIP_BELT: String = "equip_belt"
        const val EQUIP_LEG: String = "equip_leg"
        const val EQUIP_HAND: String = "equip_hand"
    }
}

