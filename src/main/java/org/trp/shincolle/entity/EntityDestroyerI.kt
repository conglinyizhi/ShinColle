package org.trp.shincolle.entity

import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModItems

class EntityDestroyerI(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    init {
        this.modelPos = floatArrayOf(0f, 0f, 0f, 25f)
        setStateMinor(STATE_MINOR_FACTION_ID, -1)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 0)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 5)
        setStateMinor(STATE_MINOR_RARITY, 1)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeDD)
        this.isStateGuiBtn3 = false
        this.isStateGuiBtn4 = false
        this.isStateCanRide = true
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
                return (this.getBbHeight() * 0.51f).toDouble()
            }
            return (this.getBbHeight() * 0.6f).toDouble()
        }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_HEAD_ORNAMENT, "gui.shincolle.equip.head_ornament"))
        return list
    }

    private fun applyBuffToOwner() {
        if (this.isStateMarried && this.isStateRingEffect && this.getStateMinor(6) > 0) {
            val owner = this.ownerPlayer
            if (owner != null && this.distanceToSqr(owner) < 256.0) {
                val amp = this.getStateMinor(0) / 45
                owner.addEffect(
                    MobEffectInstance(MobEffects.MOVEMENT_SPEED, Config.SHIP_BUFF_DURATION.get(), amp, false, false)
                )
            }
        }
    }

    override fun setFaceNormal() {
        setSimpleFace(FACE_EYES_OPEN)
    }

    override fun setFaceCry() {
        setSimpleFace(FACE_EYES_HALF)
    }

    override fun setFaceDamaged() {
        setSimpleFace(FACE_EYES_HALF)
    }

    override fun setFaceScorn() {
        setSimpleFace(FACE_EYES_HALF)
    }

    override fun setFaceHungry() {
        setSimpleFace(FACE_EYES_HALF)
    }

    override fun setFaceAngry() {
        setSimpleFace(FACE_EYES_OPEN)
    }

    override fun setFaceBored() {
        setSimpleFace(FACE_EYES_CLOSED)
    }

    override fun setFaceShy() {
        setSimpleFace(FACE_EYES_OPEN)
    }

    override fun setFaceHappy() {
        setSimpleFace(FACE_EYES_OPEN)
    }

    private fun setSimpleFace(faceId: Int) {
        this.faceId = faceId
        this.mouthId = MOUTH_FRONT_0
    }

    override fun supportsItemPickup(): Boolean {
        return true
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.DESTROYER_I_SPAWN_EGG.get()

    companion object {
        const val EQUIP_HEAD_ORNAMENT: String = "equip_head_ornament"
    }
}

