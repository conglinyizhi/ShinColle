package org.trp.shincolle.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.init.ModItems

class EntityHarbourHime(type: EntityType<out TamableAnimal?>?, level: Level?) : EntityShipBase(type, level) {
    init {
        setModelPos(floatArrayOf(-6f, 30f, 0f, 40f))
        setStateMinor(STATE_MINOR_FACTION_ID, 10)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 28)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 2)
        setStateMinor(STATE_MINOR_RARITY, 1)
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

        val range = this.getBoundingBox().inflate(12.0, 8.0, 12.0)
        val ships = this.level().getEntitiesOfClass<EntityShipBase?>(EntityShipBase::class.java, range)
        for (ship in ships) {
            if (ship === this || ship.getHealth() >= ship.getMaxHealth()) {
                continue
            }
            if (ship.getOwnerUUID() != this.getOwnerUUID()) {
                continue
            }
            ship.heal(baseHeal * 0.5f)
        }
    }

    override fun getShipSpawnEggItem(): Item {
        return ModItems.HARBOUR_HIME_SPAWN_EGG.get()
    }

    override fun hasShipMounts(): Boolean {
        return true
    }

    override fun summonMountEntity(): EntityMountBase {
        return EntityMountHbH(ModEntities.MOUNT_HB_H.get(), this.level())
    }
}

