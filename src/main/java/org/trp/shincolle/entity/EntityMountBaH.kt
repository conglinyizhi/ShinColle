package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EntityMountBase

class EntityMountBaH(type: EntityType<out PathfinderMob>, level: Level) : EntityMountBase(type, level) {
    init {
        this.setSeatPos(1.05f, 3.0f, 0.0f)
        this.setSeatPos2(1.2f, 0.7f, -1.3f)
    }

    override fun updateClientLogic() {
        super.updateClientLogic()
        if (this.tickCount % 4 == 0) {
            this.level().addParticle(ParticleTypes.SMOKE, this.x, this.y + 3.0, this.z, 0.0, 0.0, 0.0)
        }
    }
}
