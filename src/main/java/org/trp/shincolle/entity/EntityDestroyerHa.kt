package org.trp.shincolle.entity

import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModItems

class EntityDestroyerHa(type: EntityType<out TamableAnimal?>?, level: Level?) : EntityShipBase(type, level) {
    init {
        setModelPos(floatArrayOf(0f, 0f, 0f, 25f))
        setStateMinor(STATE_MINOR_FACTION_ID, -1)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 2)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 5)
        setStateMinor(STATE_MINOR_RARITY, 1)
        setStateGuiBtn3(false)
        setStateGuiBtn4(false)
        setStateCanRide(true)
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
                return (this.getBbHeight() * 0.27f).toDouble()
            }
            return (this.getBbHeight() * 0.7f).toDouble()
        }

    override fun getEquipOptions(): MutableList<EquipOption?> {
        val list: MutableList<EquipOption?> = ArrayList<EquipOption?>(super.getEquipOptions())
        list.add(EquipOption(EQUIP_HEAD_ORNAMENT, "gui.shincolle.equip.head_ornament"))
        return list
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

    protected override fun setFaceNormal() {
        setSimpleFace(FACE_EYES_OPEN)
    }

    protected override fun setFaceCry() {
        setSimpleFace(FACE_EYES_HALF)
    }

    override fun setFaceDamaged() {
        setSimpleFace(FACE_EYES_HALF)
    }

    override fun setFaceScorn() {
        setSimpleFace(FACE_EYES_HALF)
    }

    protected override fun setFaceHungry() {
        setSimpleFace(FACE_EYES_HALF)
    }

    protected override fun setFaceAngry() {
        setSimpleFace(FACE_EYES_OPEN)
    }

    protected override fun setFaceBored() {
        setSimpleFace(FACE_EYES_CLOSED)
    }

    protected override fun setFaceShy() {
        setSimpleFace(FACE_EYES_OPEN)
    }

    protected override fun setFaceHappy() {
        setSimpleFace(FACE_EYES_OPEN)
    }

    private fun setSimpleFace(faceId: Int) {
        this.setFaceId(faceId)
        this.setMouthId(MOUTH_FRONT_0)
    }

    override fun supportsItemPickup(): Boolean {
        return true
    }

    override fun getShipSpawnEggItem(): Item {
        return ModItems.DESTROYER_HA_SPAWN_EGG.get()
    }

    companion object {
        const val EQUIP_HEAD_ORNAMENT: String = "equip_head_ornament"
    }
}

