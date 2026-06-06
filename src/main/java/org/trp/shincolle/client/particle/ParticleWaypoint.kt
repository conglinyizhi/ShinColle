package org.trp.shincolle.client.particle

import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.client.renderer.LightTexture
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.Mth
import kotlin.math.max

class ParticleWaypoint protected constructor(
    level: ClientLevel, x: Double, y: Double, z: Double,
    markerHeight: Double, private val sprites: SpriteSet
) : TextureSheetParticle(level, x, y, z) {
    private val markerHeight: Float

    init {
        this.lifetime = 31
        this.quadSize = (if (markerHeight > 0) markerHeight else 0.2).toFloat()
        this.markerHeight = 1.5f
        this.rCol = 1.0f
        this.gCol = 0.0f
        this.bCol = 0.0f
        this.alpha = 0.9f
        this.hasPhysics = false
        this.gravity = 0.0f
        this.setSpriteFromAge(sprites)
    }

    override fun tick() {
        this.xo = this.x
        this.yo = this.y
        this.zo = this.z

        this.y += this.age * 0.002
        this.alpha = max(0.0f, 0.9f - this.age * 0.027f)

        this.setSpriteFromAge(this.sprites)

        if (this.age++ >= this.lifetime) {
            this.remove()
        }
    }

    override fun render(buffer: VertexConsumer, camera: Camera, partialTick: Float) {
        val sprite = this.sprite
        val u0 = sprite.getU0()
        val u1 = sprite.getU1()
        val v0 = Mth.lerp(0.5f, sprite.getV0(), sprite.getV1())
        val v1 = sprite.getV1()

        val cameraPos = camera.getPosition()
        val bx = (Mth.lerp(partialTick.toDouble(), this.xo, this.x) - cameraPos.x()).toFloat()
        val by: Float = (Mth.lerp(partialTick.toDouble(), this.yo, this.y) - cameraPos.y()).toFloat() + BASE_Y_OFFSET
        val bz = (Mth.lerp(partialTick.toDouble(), this.zo, this.z) - cameraPos.z()).toFloat()

        val halfScale: Float = this.getQuadSize(partialTick) * BASE_SCALE
        val light = getLightColor(partialTick)

        emitVertex(buffer, bx + halfScale, by, bz + halfScale, u1, v1, light)
        emitVertex(buffer, bx + halfScale, by, bz - halfScale, u1, v0, light)
        emitVertex(buffer, bx - halfScale, by, bz - halfScale, u0, v0, light)
        emitVertex(buffer, bx - halfScale, by, bz + halfScale, u0, v1, light)

        emitVertex(buffer, bx + halfScale, by, bz - halfScale, u1, v1, light)
        emitVertex(buffer, bx + halfScale, by, bz + halfScale, u1, v0, light)
        emitVertex(buffer, bx - halfScale, by, bz + halfScale, u0, v0, light)
        emitVertex(buffer, bx - halfScale, by, bz - halfScale, u0, v1, light)
    }

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    }

    override fun getLightColor(partialTick: Float): Int {
        return LightTexture.FULL_BRIGHT
    }

    private fun emitVertex(buffer: VertexConsumer, x: Float, y: Float, z: Float, u: Float, v: Float, light: Int) {
        buffer.addVertex(x, y, z)
            .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            .setUv(u, v)
            .setLight(light)
    }

    class Provider(private val sprites: SpriteSet) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            type: SimpleParticleType, level: ClientLevel,
            x: Double, y: Double, z: Double,
            xSpeed: Double, ySpeed: Double, zSpeed: Double
        ): Particle? {
            return ParticleWaypoint(level, x, y, z, xSpeed, this.sprites)
        }
    }

    companion object {
        private const val BASE_Y_OFFSET = 0.26f
        private const val BASE_SCALE = 3.0f
    }
}
