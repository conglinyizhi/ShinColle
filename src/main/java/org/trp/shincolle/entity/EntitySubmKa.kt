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

class EntitySubmKa(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    init {
        this.modelPos = floatArrayOf(0f, 25f, 0f, 45f)
        setStateMinor(STATE_MINOR_FACTION_ID, 8)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 17)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 6)
        setStateMinor(STATE_MINOR_RARITY, 4)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeSS)
        this.isStateGuiBtn3 = false
        this.isStateGuiBtn4 = false
        this.isStateCanRide = true
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()

        if ((this.tickCount % 128) == 0) {
            updateServerLogic()
        }
    }

    private fun updateServerLogic() {
        if (this.isStateRingEffect) {
            val duration = 40 + this.level
            this.addEffect(MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false))
            val owner = this.ownerPlayer

            if (this.isStateMarried && owner != null && this.distanceToSqr(owner) < 256.0) {
                owner.addEffect(MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false))
            }
        }
    }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_BASE, "gui.shincolle.equip.base"))
        list.add(EquipOption(EQUIP_HEAD_BASE, "gui.shincolle.equip.head_base"))
        list.add(EquipOption(EQUIP_NORMAL_BODY, "gui.shincolle.equip.normal_body"))
        list.add(EquipOption(EQUIP_TORPEDO, "gui.shincolle.equip.torpedo"))
        return list
    }

    override fun supportsItemPickup(): Boolean {
        return true
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.SUBM_KA_SPAWN_EGG.get()

    override val isSubmarine: Boolean
        get() = true

    companion object {
        const val EQUIP_BASE: String = "equip_base"
        const val EQUIP_HEAD_BASE: String = "equip_head_base"
        const val EQUIP_NORMAL_BODY: String = "equip_normal_body"
        const val EQUIP_TORPEDO: String = "equip_torpedo"
    }
}

