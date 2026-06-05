package org.trp.shincolle.entity

import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.init.ModItems
import kotlin.math.max

class EntityMidwayHime(type: EntityType<out TamableAnimal?>?, level: Level?) : EntityShipBase(type, level) {
    init {
        setModelPos(floatArrayOf(-6f, 30f, 0f, 40f))
        setStateMinor(STATE_MINOR_FACTION_ID, 10)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 30)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 2)
        setStateMinor(STATE_MINOR_RARITY, 2)
        setStateCanRide(true)
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()

        if ((this.tickCount % 128) == 0) {
            updateServerLogic()
        }
    }

    private fun updateServerLogic() {
        if (!(this.isStateMarried() && this.isStateRingEffect())) {
            return
        }

        val baseHeal = 1.0f + this.getStateMinor(0) * 0.01f
        if (this.getHealth() < this.getMaxHealth()) {
            this.heal(baseHeal)
        }

        val duration = 100 + this.getStateMinor(0)
        val amp = max(0, this.getStateMinor(0) / 80)
        val range = this.getBoundingBox().inflate(14.0, 8.0, 14.0)
        val ships = this.level().getEntitiesOfClass<EntityShipBase?>(EntityShipBase::class.java, range)
        for (ship in ships) {
            if (ship === this) {
                continue
            }
            if (ship.getOwnerUUID() != this.getOwnerUUID()) {
                continue
            }
            ship.addEffect(MobEffectInstance(MobEffects.ABSORPTION, duration, amp, false, false))
        }
        if (this.getOwnerPlayer() != null && this.distanceToSqr(this.getOwnerPlayer()) < 256.0) {
            this.getOwnerPlayer().addEffect(MobEffectInstance(MobEffects.ABSORPTION, duration, amp, false, false))
        }
    }

    override fun getEquipOptions(): MutableList<EquipOption?> {
        val list: MutableList<EquipOption?> = ArrayList<EquipOption?>(super.getEquipOptions())
        list.add(EquipOption(EQUIP_RIGGING, "gui.shincolle.equip.rigging"))
        list.add(EquipOption(EQUIP_COLLAR, "gui.shincolle.equip.collar"))
        return list
    }

    override fun getShipSpawnEggItem(): Item {
        return ModItems.MIDWAY_HIME_SPAWN_EGG.get()
    }

    override fun hasShipMounts(): Boolean {
        return true
    }

    override fun summonMountEntity(): EntityMountBase {
        return EntityMountMiH(ModEntities.MOUNT_MI_H.get(), this.level())
    }

    companion object {
        const val EQUIP_RIGGING: String = "equip_rigging"
        const val EQUIP_COLLAR: String = "equip_collar"
    }
}

