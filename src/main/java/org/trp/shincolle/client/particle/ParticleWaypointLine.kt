package org.trp.shincolle.client.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.client.renderer.LightTexture
import net.minecraft.core.particles.SimpleParticleType
import kotlin.math.min

class ParticleWaypointLine protected constructor(
    level: ClientLevel, x: Double, y: Double, z: Double,
    vx: Double, vy: Double, vz: Double,
    private val lineType: Int, private val sprites: SpriteSet
) : TextureSheetParticle(level, x, y, z) {
    private val pScale: Float

    init {
        this.xd = vx
        this.yd = vy
        this.zd = vz

        if (lineType == 0) {
            this.rCol = 1.0f
            this.gCol = 0.0f
            this.bCol = 0.0f
        } else {
            this.rCol = 0.5f
            this.gCol = 0.0f
            this.bCol = 0.5f
        }
        this.alpha = 0.5f

        this.quadSize = this.quadSize * 3.0f
        this.pScale = this.quadSize
        this.lifetime = 100

        this.hasPhysics = false
        this.gravity = 0.0f
        this.friction = 1.0f

        this.setSpriteFromAge(sprites)
    }

    override fun tick() {
        this.xo = this.x
        this.yo = this.y
        this.zo = this.z

        this.setSpriteFromAge(this.sprites)

        this.x += this.xd
        this.y += this.yd
        this.z += this.zd

        val ageRatio = this.age.toFloat() / this.lifetime
        val scaleFactor = min(1.0f, ageRatio * 32.0f)
        this.quadSize = this.pScale * scaleFactor

        if (this.age++ >= this.lifetime) {
            this.remove()
        }
    }

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    }

    override fun getLightColor(partialTick: Float): Int {
        return LightTexture.FULL_BRIGHT
    }

    class ProviderRed(private val sprites: SpriteSet) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            type: SimpleParticleType, level: ClientLevel,
            x: Double, y: Double, z: Double,
            xSpeed: Double, ySpeed: Double, zSpeed: Double
        ): Particle? {
            return ParticleWaypointLine(level, x, y, z, xSpeed, ySpeed, zSpeed, 0, this.sprites)
        }
    }

    class ProviderPurple(private val sprites: SpriteSet) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            type: SimpleParticleType, level: ClientLevel,
            x: Double, y: Double, z: Double,
            xSpeed: Double, ySpeed: Double, zSpeed: Double
        ): Particle? {
            return ParticleWaypointLine(level, x, y, z, xSpeed, ySpeed, zSpeed, 1, this.sprites)
        }
    }
}
