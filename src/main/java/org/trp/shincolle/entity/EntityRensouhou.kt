package org.trp.shincolle.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.base.EntitySummonBase
import kotlin.math.max
import kotlin.math.min

class EntityRensouhou(type: EntityType<out TamableAnimal?>?, level: Level?) : EntitySummonBase(type, level) {
    override fun returnSummonResources(carrier: EntityShipBase) {
        if (carrier is IShipSummonAttack) {
            carrier.setNumServant(min(MAX_RENSOUHOU, carrier.getNumServant() + 1))
        }

        val returnLight = max(0, this.numAmmoLight - AMMO_RETURN_PENALTY_LIGHT)
        if (returnLight > 0) {
            carrier.setAmmoLight(carrier.getAmmoLight() + returnLight)
        }
    }

    companion object {
        private const val AMMO_RETURN_PENALTY_LIGHT = 2
        private const val MAX_RENSOUHOU = 6
    }
}
