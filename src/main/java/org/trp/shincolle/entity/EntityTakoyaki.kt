package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.level.Level

class EntityTakoyaki(type: EntityType<out TamableAnimal?>?, level: Level?) : EntityAircraftBase(type, level) {
    override fun isDefaultLightAircraft(): Boolean {
        return false
    }

    override fun applyFlyParticle() {
        if (this.tickCount % 2 == 0) {
            val motion = this.getDeltaMovement()
            this.level().addParticle(
                ParticleTypes.SMOKE,
                this.getX(), this.getY() + this.getBbHeight(), this.getZ(),
                -motion.x * 0.5, 0.07, -motion.z * 0.5
            )
        }
    }
}
