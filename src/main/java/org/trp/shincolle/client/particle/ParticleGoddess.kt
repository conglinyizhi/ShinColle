package org.trp.shincolle.client.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.SimpleParticleType
import kotlin.math.max

class ParticleGoddess protected constructor(
    level: ClientLevel, x: Double, y: Double, z: Double,
    beamFad: Double, beamRiseSpeed: Double, beamHeight: Double
) : ParticleHealSparkle(level, x, y, z, beamFad, beamRiseSpeed, beamHeight) {
    init {
        this.lifetime = 120
        this.maxBeamAge = 40

        val setting = getParticleSetting(level)
        val numBeam = max(1, (3 - setting) * 30)
        this.beams = Array<FloatArray>(numBeam) { FloatArray(8) }
        for (i in 0..<numBeam) {
            this.beams[i][7] = this.maxBeamAge.toFloat()
        }
        this.beamCurrent = 0
    }

    override fun spawnBeam() {
        val randFactor = this.random.nextFloat() * 0.4f - 0.2f
        val red = 1.0f
        val green = 0.9f + randFactor
        val blue = 0.4f + randFactor

        this.beams[this.beamCurrent][0] = (this.random.nextFloat() * 2.5f - 1.25f) * this.beamFad
        this.beams[this.beamCurrent][1] = this.beamHeight + (this.random.nextFloat() * 2.5f - 1.25f) * this.beamFad
        this.beams[this.beamCurrent][2] = (this.random.nextFloat() * 2.5f - 1.25f) * this.beamFad
        this.beams[this.beamCurrent][3] = red
        this.beams[this.beamCurrent][4] = green
        this.beams[this.beamCurrent][5] = blue
        this.beams[this.beamCurrent][6] = 1.0f
        this.beams[this.beamCurrent][7] = 0.0f

        this.beamCurrent = (this.beamCurrent + 1) % this.beams.size
    }

    class Provider(sprites: SpriteSet?) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            type: SimpleParticleType, level: ClientLevel, x: Double, y: Double,
            z: Double, xSpeed: Double, ySpeed: Double, zSpeed: Double
        ): ParticleGoddess? {
            return ParticleGoddess(level, x, y, z, xSpeed, ySpeed, zSpeed)
        }
    }
}
