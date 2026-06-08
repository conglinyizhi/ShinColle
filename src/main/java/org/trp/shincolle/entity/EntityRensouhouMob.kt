package org.trp.shincolle.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.base.EntitySummonBase
import org.trp.shincolle.init.ModEntities

/**
 * 深海连装炮酱（敌方版本）。
 * 远程攻击型召唤物，由敌对舰娘召唤。
 * 击杀后不返还资源。
 */
class EntityRensouhouMob(type: EntityType<out TamableAnimal>, level: Level) : EntitySummonBase(type, level) {

    constructor(level: Level) : this(ModEntities.RENSOUHOU_MOB.get(), level)

    override fun returnSummonResources(carrier: EntityShipBase?) {
        // Hostile version: no resources returned when killed
    }
}
