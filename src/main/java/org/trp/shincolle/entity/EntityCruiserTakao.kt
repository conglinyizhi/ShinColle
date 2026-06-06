package org.trp.shincolle.entity

import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModItems
import kotlin.math.max

class EntityCruiserTakao(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    init {
        this.modelPos = floatArrayOf(0f, 25f, 0f, 40f)
        setStateMinor(STATE_MINOR_FACTION_ID, 2)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 59)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 4)
        setStateMinor(STATE_MINOR_RARITY, 4)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeCA)
        this.isStateGuiBtn3 = false
        this.isStateGuiBtn4 = false
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        val result = super.hurt(source, amount)
        if (result && !this.level().isClientSide) {
            val attacker = source.entity
            if (attacker is LivingEntity) {
                val duration = 80 + this.getStateMinor(0)
                val amp = max(0, this.getStateMinor(0) / 80)
                attacker.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, amp, false, false))
            }
        }
        return result
    }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_RIGGING, "gui.shincolle.equip.rigging"))
        list.add(EquipOption(EQUIP_BAG, "gui.shincolle.equip.bag"))
        list.add(EquipOption(EQUIP_HAT, "gui.shincolle.equip.hat"))
        list.add(EquipOption(EQUIP_SHOES, "gui.shincolle.equip.shoes"))
        return list
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.CRUISER_TAKAO_SPAWN_EGG.get()

    companion object {
        const val EQUIP_RIGGING: String = "equip_rigging"
        const val EQUIP_BAG: String = "equip_bag"
        const val EQUIP_HAT: String = "equip_hat"
        const val EQUIP_SHOES: String = "equip_shoes"
    }
}

