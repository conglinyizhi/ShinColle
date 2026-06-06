package org.trp.shincolle.client.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.core.particles.SimpleParticleType
import kotlin.math.sqrt

class ParticleSpray protected constructor(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    vx: Double,
    vy: Double,
    vz: Double,
    private val sprites: SpriteSet
) : TextureSheetParticle(level, x, y, z, vx, vy, vz) {
    private val maxQuadSize: Float

    init {
        val speedSq = vx * vx + vy * vy + vz * vz
        val speedLimit = 0.3
        val speed = sqrt(speedSq)
        if (speedSq > speedLimit * speedLimit) {
            this.xd = (vx / speed) * speedLimit
            this.yd = (vy / speed) * speedLimit
            this.zd = (vz / speed) * speedLimit
        } else {
            this.xd = vx
            this.yd = vy
            this.zd = vz
        }

        this.rCol = 1.0f
        this.gCol = 1.0f
        this.bCol = 1.0f
        this.alpha = 0.5f

        this.lifetime = 50

        this.hasPhysics = true
        this.gravity = 0.0f
        this.friction = 0.96f

        if (speed > 0.25) {
            this.maxQuadSize = 1.5f
        } else {
            this.maxQuadSize = 0.15f
        }
        this.quadSize = 0.0f

        this.setSpriteFromAge(sprites)
    }

    override fun tick() {
        super.tick()
        if (!this.removed) {
            this.setSpriteFromAge(this.sprites)
            val ageRatio = this.age.toFloat() / this.lifetime.toFloat()
            this.quadSize = this.maxQuadSize * ageRatio
        }
    }

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    }

    class Provider(private val sprites: SpriteSet) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            type: SimpleParticleType, level: ClientLevel, x: Double, y: Double, z: Double,
            xSpeed: Double, ySpeed: Double, zSpeed: Double
        ): Particle? {
            return ParticleSpray(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites)
        }
    }
}
