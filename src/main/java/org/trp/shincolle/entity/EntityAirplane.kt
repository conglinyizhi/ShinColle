package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.level.Level

class EntityAirplane(type: EntityType<out TamableAnimal>, level: Level) : EntityAircraftBase(type, level) {
    override fun applyFlyParticle() {
        val motion = this.getDeltaMovement()
        val trailX = this.getX() - motion.x * 1.5
        val trailY = this.getY() + this.getBbHeight()
        val trailZ = this.getZ() - motion.z * 1.5

        this.level().addParticle(
            ParticleTypes.SMOKE,
            trailX, trailY, trailZ,
            -motion.x * 0.5, -motion.y * 0.5, -motion.z * 0.5
        )
    }
}
