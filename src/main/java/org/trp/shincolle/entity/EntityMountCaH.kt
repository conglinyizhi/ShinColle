package org.trp.shincolle.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EntityMountBase

class EntityMountCaH(type: EntityType<out PathfinderMob?>?, level: Level?) : EntityMountBase(type, level) {
    init {
        this.setSeatPos(0.0f, 1.62f, 0.0f)
        this.setSeatPos2(0.14f, -0.39f, 0.0f)
    }
}
