package org.trp.shincolle.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.init.ModParticles
import kotlin.math.cos
import kotlin.math.sin

class EntityMountAfH(type: EntityType<out PathfinderMob?>?, level: Level?) : EntityMountBase(type, level) {
    init {
        this.setSeatPos(0.59f, -0.25f, 0.0f)
        this.setSeatPos2(-0.85f, 1.0f, -1.12f)
    }

    override fun updateClientLogic() {
        super.updateClientLogic()
        if (this.tickCount % 8 == 0) {
            val radians = this.yBodyRot * (Math.PI / 180.0).toFloat()
            val cosR = cos(radians.toDouble()).toFloat()
            val sinR = sin(radians.toDouble()).toFloat()

            val px1 = this.getX() - cosR
            val pz1 = this.getZ() - sinR
            val px2 = this.getX() - 1.8 * cosR
            val pz2 = this.getZ() - 1.8 * sinR

            this.level().addParticle(ModParticles.PARTICLE_SPRAY_RED.get(), px1, this.getY() + 0.9, pz1, 0.0, 0.1, 0.0)
            this.level().addParticle(ModParticles.PARTICLE_SPRAY_RED.get(), px2, this.getY() + 0.9, pz2, 0.0, 0.1, 0.0)
        }
    }
}
