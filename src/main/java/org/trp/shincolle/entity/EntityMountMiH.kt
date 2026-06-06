package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EntityMountBase
import kotlin.math.cos
import kotlin.math.sin

class EntityMountMiH(type: EntityType<out PathfinderMob>, level: Level) : EntityMountBase(type, level) {
    init {
        this.setSeatPos(0.0f, 2.7f, 0.0f)
        this.setSeatPos2(-0.3f, 0.5f, 0.0f)
    }

    override fun updateClientLogic() {
        super.updateClientLogic()
        if (this.tickCount % 8 == 0) {
            val radians = this.getYRot() * (Math.PI / 180.0).toFloat()
            val cosR = cos(radians.toDouble()).toFloat()
            val sinR = sin(radians.toDouble()).toFloat()
            val px = this.getX() + (-0.25 * cosR - 1.2 * sinR)
            val pz = this.getZ() + (1.2 * cosR - 0.25 * sinR)
            this.level().addParticle(ParticleTypes.DRIPPING_LAVA, px, this.getY() + 0.85, pz, 0.0, 0.0, 0.0)
        }
    }
}
