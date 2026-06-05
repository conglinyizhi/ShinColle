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

class EntityCruiserTenryuu(type: EntityType<out TamableAnimal?>?, level: Level?) : EntityShipBase(type, level) {
    init {
        setModelPos(floatArrayOf(0f, 22f, 0f, 42f))
        setStateMinor(STATE_MINOR_FACTION_ID, 1)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 56)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 4)
        setStateMinor(STATE_MINOR_RARITY, 5)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeCL)
        setStateGuiBtn3(false)
        setStateGuiBtn4(false)
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()

        if ((this.tickCount % 128) == 0) {
            updateServerLogic()
        }
    }

    private fun updateServerLogic() {
        if (!this.level().isDay() && this.isStateMarried() && this.isStateRingEffect() && this.getStateMinor(6) > 0) {
            if (this.getOwnerPlayer() != null && this.distanceToSqr(this.getOwnerPlayer()) < 256.0) {
                this.getOwnerPlayer().addEffect(
                    MobEffectInstance(
                        MobEffects.NIGHT_VISION,
                        Config.SHIP_BUFF_DURATION.get(),
                        0,
                        false,
                        false
                    )
                )
            }
        }
    }

    override fun getEquipOptions(): MutableList<EquipOption?> {
        val list: MutableList<EquipOption?> = ArrayList<EquipOption?>(super.getEquipOptions())
        list.add(EquipOption(EQUIP_RIGGING, "gui.shincolle.equip.rigging"))
        list.add(EquipOption(EQUIP_EARS, "gui.shincolle.equip.ears"))
        list.add(EquipOption(EQUIP_SIDE, "gui.shincolle.equip.side"))
        list.add(EquipOption(EQUIP_MASK, "gui.shincolle.equip.mask"))
        list.add(EquipOption(EQUIP_SHOES, "gui.shincolle.equip.shoes"))
        return list
    }

    override fun getShipSpawnEggItem(): Item {
        return ModItems.CRUISER_TENRYUU_SPAWN_EGG.get()
    }

    companion object {
        const val EQUIP_RIGGING: String = "equip_rigging"
        const val EQUIP_EARS: String = "equip_ears"
        const val EQUIP_SIDE: String = "equip_side"
        const val EQUIP_MASK: String = "equip_mask"
        const val EQUIP_SHOES: String = "equip_shoes"
    }
}

