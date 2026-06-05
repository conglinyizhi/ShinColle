package org.trp.shincolle.client.renderer

import net.minecraft.client.model.EntityModel
import net.minecraft.world.entity.LivingEntity
import org.trp.shincolle.client.model.ShipModelBaseAdv
import org.trp.shincolle.entity.EntityRensouhou
import org.trp.shincolle.entity.EntityRensouhouS
import org.trp.shincolle.entity.base.EntityShincolleSimpleMob
import org.trp.shincolle.entity.base.EntityShipBase

object LegacyScale {
    private const val DEFAULT = 0.34f

    fun getScale(entity: LivingEntity?, model: EntityModel<*>?): Float {
        if (entity is EntityShipBase && model is ShipModelBaseAdv<*>) {
            return model.getLegacyScale(entity)
        }
        if (entity is EntityShincolleSimpleMob) {
            var base = 0.34f
            if (entity is EntityRensouhou || entity is EntityRensouhouS) {
                base = 0.4f
            }
            return base * (entity.scaleLevel + 1)
        }
        return DEFAULT
    }
}
