package org.trp.shincolle.client.particle

import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.particle.TextureSheetParticle
import net.minecraft.client.renderer.LightTexture
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.Mth
import org.joml.Vector3f
import kotlin.math.max

class ParticleTexts protected constructor(
    level: ClientLevel, x: Double, y: Double, z: Double, textType: Int, riseSpeed: Double,
    spread: Double, private val sprites: SpriteSet
) : TextureSheetParticle(level, x, y, z) {
    private val textType: Int

    init {
        this.textType = Mth.clamp(textType, 0, TYPE_COUNT - 1)

        this.lifetime = BASE_LIFETIME + this.random.nextInt(6)
        this.quadSize = BASE_SCALE + this.random.nextFloat() * 0.06f
        this.alpha = 0.0f
        this.friction = 0.93f
        this.gravity = 0.0f
        this.hasPhysics = false

        val spreadScale = max(0.0, spread)
        this.x += (this.random.nextDouble() - 0.5) * spreadScale
        this.y += this.random.nextDouble() * 0.35 + 0.15
        this.z += (this.random.nextDouble() - 0.5) * spreadScale

        this.xd = (this.random.nextDouble() - 0.5) * 0.02
        this.yd = max(0.02, riseSpeed)
        this.zd = (this.random.nextDouble() - 0.5) * 0.02

        this.setSpriteFromAge(this.sprites)
    }

    override fun tick() {
        super.tick()
        if (this.removed) {
            return
        }

        this.setSpriteFromAge(this.sprites)
        val progress = if (this.lifetime <= 0) 1.0f else this.age.toFloat() / this.lifetime.toFloat()
        if (progress < 0.2f) {
            this.alpha = progress / 0.2f
        } else if (progress > 0.8f) {
            this.alpha = (1.0f - progress) / 0.2f
        } else {
            this.alpha = 1.0f
        }
        this.alpha = Mth.clamp(this.alpha, 0.0f, 1.0f)
    }

    override fun render(buffer: VertexConsumer, camera: Camera, partialTicks: Float) {
        val sprite = this.sprite
        val u0 = sprite.getU0()
        val u1 = sprite.getU1()

        val rowMin: Float = this.textType * TYPE_ROW_HEIGHT
        val rowMax: Float = rowMin + TYPE_ROW_HEIGHT
        val v0 = Mth.lerp(rowMin, sprite.getV0(), sprite.getV1())
        val v1 = Mth.lerp(rowMax, sprite.getV0(), sprite.getV1())

        val cameraPos = camera.getPosition()
        val px = (Mth.lerp(partialTicks.toDouble(), this.xo, this.x) - cameraPos.x()).toFloat()
        val py = (Mth.lerp(partialTicks.toDouble(), this.yo, this.y) - cameraPos.y()).toFloat()
        val pz = (Mth.lerp(partialTicks.toDouble(), this.zo, this.z) - cameraPos.z()).toFloat()

        val scale = this.getQuadSize(partialTicks)
        val light = this.getLightColor(partialTicks)

        val rotation = camera.rotation()
        val corners: Array<Vector3f> = arrayOf<Vector3f>(
            Vector3f(-1.0f, -TEXT_ASPECT_Y, 0.0f),
            Vector3f(-1.0f, TEXT_ASPECT_Y, 0.0f),
            Vector3f(1.0f, TEXT_ASPECT_Y, 0.0f),
            Vector3f(1.0f, -TEXT_ASPECT_Y, 0.0f)
        )

        for (corner in corners) {
            corner!!.rotate(rotation)
            corner.mul(scale)
            corner.add(px, py, pz)
        }

        emitVertex(buffer, corners[0]!!, u1, v1, light)
        emitVertex(buffer, corners[1]!!, u1, v0, light)
        emitVertex(buffer, corners[2]!!, u0, v0, light)
        emitVertex(buffer, corners[3]!!, u0, v1, light)

        emitVertex(buffer, corners[3]!!, u1, v1, light)
        emitVertex(buffer, corners[2]!!, u1, v0, light)
        emitVertex(buffer, corners[1]!!, u0, v0, light)
        emitVertex(buffer, corners[0]!!, u0, v1, light)
    }

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    }

    override fun getLightColor(partialTicks: Float): Int {
        return LightTexture.FULL_BRIGHT
    }

    private fun emitVertex(buffer: VertexConsumer, pos: Vector3f, u: Float, v: Float, light: Int) {
        buffer.addVertex(pos.x(), pos.y(), pos.z())
            .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            .setUv(u, v)
            .setLight(light)
    }

    class Provider(private val sprites: SpriteSet) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            type: SimpleParticleType, level: ClientLevel, x: Double, y: Double, z: Double,
            xSpeed: Double, ySpeed: Double, zSpeed: Double
        ): ParticleTexts? {
            val textType = Math.round(xSpeed).toInt()
            val rise = if (ySpeed > 0.0) ySpeed else 0.045
            val spread = if (zSpeed > 0.0) zSpeed else 0.85
            return ParticleTexts(level, x, y, z, textType, rise, spread, this.sprites)
        }
    }

    companion object {
        private const val TYPE_COUNT = 5
        private val TYPE_ROW_HEIGHT: Float = 1.0f / TYPE_COUNT
        private const val BASE_SCALE = 0.34f
        private const val TEXT_ASPECT_Y = 0.22f
        private const val BASE_LIFETIME = 26
    }
}
