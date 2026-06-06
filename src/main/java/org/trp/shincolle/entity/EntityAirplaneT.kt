package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.level.Level

class EntityAirplaneT(type: EntityType<out TamableAnimal>, level: Level) : EntityAircraftBase(type, level) {
    override val isDefaultLightAircraft: Boolean
        get() = false

    override fun applyFlyParticle() {
        if (this.tickCount % 2 == 0) {
            val motion = this.deltaMovement
            this.level().addParticle(
                ParticleTypes.SMOKE,
                this.x, this.y + this.bbHeight, this.z,
                -motion.x * 0.5, 0.07, -motion.z * 0.5
            )
        }
    }
}
