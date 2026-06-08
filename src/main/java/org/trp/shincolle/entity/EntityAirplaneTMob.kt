package org.trp.shincolle.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.level.Level
import org.trp.shincolle.init.ModEntities

/**
 * 深海大型舰载机 T（敌方版本）。
 * 继承友方 EntityAirplaneT，由敌对舰娘召唤时使用。
 */
class EntityAirplaneTMob(type: EntityType<out TamableAnimal>, level: Level) : EntityAirplaneT(type, level) {

    constructor(level: Level) : this(ModEntities.AIRPLANE_T_MOB.get(), level)

    override val isDefaultLightAircraft: Boolean
        get() = false
}
