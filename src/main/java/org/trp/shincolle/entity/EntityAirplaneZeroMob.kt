package org.trp.shincolle.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.level.Level
import org.trp.shincolle.init.ModEntities

/**
 * 深海零式舰载机（敌方版本）。
 * 继承友方 EntityAirplaneZero，由敌对舰娘召唤时使用。
 */
class EntityAirplaneZeroMob(type: EntityType<out TamableAnimal>, level: Level) : EntityAirplaneZero(type, level) {

    constructor(level: Level) : this(ModEntities.AIRPLANE_ZERO_MOB.get(), level)
}
