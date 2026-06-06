package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EntityMountBase
import kotlin.math.cos
import kotlin.math.sin

class EntityMountIsH(type: EntityType<out PathfinderMob>, level: Level) : EntityMountBase(type, level) {
    init {
        this.setSeatPos(-0.32f, 1.0f, 0.0f)
        this.setSeatPos2(0.61f, 0.28f, 0.0f)
    }

    override fun updateClientLogic() {
        super.updateClientLogic()
        if (this.tickCount % 8 == 0) {
            val radians = this.yRot * (Math.PI / 180.0).toFloat()
            val cosR = cos(radians.toDouble()).toFloat()
            val sinR = sin(radians.toDouble()).toFloat()
            val px = this.x + (-0.15 * cosR - 0.65 * sinR)
            val pz = this.z + (0.65 * cosR - 0.15 * sinR)
            this.level().addParticle(ParticleTypes.DRIPPING_WATER, px, this.y + 0.7, pz, 0.0, 0.0, 0.0)
        }
    }
}
