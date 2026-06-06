package org.trp.shincolle.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EntityMountBase

class EntityMountSuH(type: EntityType<out PathfinderMob>, level: Level) : EntityMountBase(type, level) {
    init {
        this.setSeatPos(-0.8f, 0.6f, 0.0f)
        this.setSeatPos2(0.55f, 1.2f, 0.0f)
    }
}
