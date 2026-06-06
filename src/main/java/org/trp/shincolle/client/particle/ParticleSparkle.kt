package org.trp.shincolle.client.particle

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import org.joml.Vector3f
import java.util.function.Supplier
import kotlin.math.max
import kotlin.math.min

class ParticleSparkle protected constructor(
    level: ClientLevel, x: Double, y: Double, z: Double,
    type: Double, beamFad: Double, beamRiseSpeed: Double, sprites: SpriteSet?
) : Particle(level, x, y, z) {
    protected var maxBeamAge: Int
    protected val beamFad: Float
    protected val motionY: Float
    protected val beamHeight: Float = 0.4f
    protected val particleType: Int
    protected var beams: Array<FloatArray>
    protected var beamCurrent: Int
    protected val quadSize: Float

    init {
        this.particleType = type.toInt()
        this.beamFad = max(0.0, beamFad).toFloat()
        this.motionY = beamRiseSpeed.toFloat()
        this.lifetime = 20
        this.maxBeamAge = 20
        this.hasPhysics = false
        this.quadSize = 0.075f


        this.setSize(0.5f, 0.5f)
        this.setBoundingBox(AABB(x - 2.0, y - 1.0, z - 2.0, x + 2.0, y + 3.0, z + 2.0))

        val setting = getParticleSetting(level)
        val numBeam = max(1, (3 - setting) * 15)
        this.beams = Array<FloatArray?>(numBeam) { FloatArray(8) }
        for (i in 0..<numBeam) {
            this.beams[i][7] = this.maxBeamAge.toFloat()
        }
        this.beamCurrent = 0
    }

    override fun tick() {
        this.xo = this.x
        this.yo = this.y
        this.zo = this.z

        if (this.age++ > this.lifetime) {
            this.remove()
            return
        }

        val setting = getParticleSetting(this.level)
        val spawnCount = 4 - setting
        for (i in 0..<spawnCount) {
            spawnBeam()
        }

        for (beam in this.beams) {
            beam[1] += this.motionY
            beam[7] += 1.0f
            beam[6] = min(1.0f, this.random.nextFloat() + 0.1f)
        }
    }

    override fun render(buffer: VertexConsumer, camera: Camera, partialTicks: Float) {
        val cameraPos = camera.getPosition()
        val baseX = (Mth.lerp(partialTicks.toDouble(), this.xo, this.x) - cameraPos.x()).toFloat()
        val baseY = (Mth.lerp(partialTicks.toDouble(), this.yo, this.y) - cameraPos.y()).toFloat()
        val baseZ = (Mth.lerp(partialTicks.toDouble(), this.zo, this.z) - cameraPos.z()).toFloat()

        val rotation = camera.rotation()

        for (beam in this.beams) {
            val beamAge = beam[7]
            if (beamAge >= this.maxBeamAge) {
                continue
            }

            val size = (this.maxBeamAge - beamAge) / this.maxBeamAge.toFloat() * this.quadSize
            if (size <= 0.0f) {
                continue
            }

            val px = baseX + beam[0]
            val py = baseY + beam[1]
            val pz = baseZ + beam[2]

            val corners: Array<Vector3f> = arrayOf<Vector3f>(
                Vector3f(-1.0f, -1.0f, 0.0f),
                Vector3f(-1.0f, 1.0f, 0.0f),
                Vector3f(1.0f, 1.0f, 0.0f),
                Vector3f(1.0f, -1.0f, 0.0f)
            )

            for (corner in corners) {
                corner!!.rotate(rotation)
                corner.mul(size)
                corner.add(px, py, pz)
            }

            val r = beam[3]
            val g = beam[4]
            val b = beam[5]
            val a = beam[6]

            buffer.addVertex(corners[0]!!.x(), corners[0]!!.y(), corners[0]!!.z()).setColor(r, g, b, a)
            buffer.addVertex(corners[1]!!.x(), corners[1]!!.y(), corners[1]!!.z()).setColor(r, g, b, a)
            buffer.addVertex(corners[2]!!.x(), corners[2]!!.y(), corners[2]!!.z()).setColor(r, g, b, a)
            buffer.addVertex(corners[3]!!.x(), corners[3]!!.y(), corners[3]!!.z()).setColor(r, g, b, a)
        }
    }

    override fun getRenderType(): ParticleRenderType {
        return UNTEXTURED_RENDER
    }

    protected fun spawnBeam() {
        val color = this.beamColor

        this.beams[this.beamCurrent][0] = (this.random.nextFloat() * 2.0f - 1.0f) * this.beamFad
        this.beams[this.beamCurrent][1] = this.beamHeight + (this.random.nextFloat() * 2.0f - 1.0f) * this.beamFad
        this.beams[this.beamCurrent][2] = (this.random.nextFloat() * 2.0f - 1.0f) * this.beamFad
        this.beams[this.beamCurrent][3] = color[0]
        this.beams[this.beamCurrent][4] = color[1]
        this.beams[this.beamCurrent][5] = color[2]
        this.beams[this.beamCurrent][6] = 1.0f
        this.beams[this.beamCurrent][7] = 0.0f

        this.beamCurrent = (this.beamCurrent + 1) % this.beams.size
    }

    private val beamColor: FloatArray
        get() {
            var red = 1.0f
            var green = 1.0f
            var blue = 1.0f
            val randFactor = this.random.nextFloat() * 1.2f - 0.5f
            when (this.particleType) {
                0 -> red += randFactor
                2 -> green += randFactor
                3 -> {
                    red = 1.0f
                    green = 1.0f
                    blue = 1.0f + (this.random.nextFloat() * 1.2f - 0.5f)
                }

                4 -> {
                    red += randFactor
                    green += this.random.nextFloat() * 1.2f - 0.5f
                }

                5 -> {
                    red += randFactor
                    blue += this.random.nextFloat() * 1.2f - 0.5f
                }

                6 -> {
                    green += randFactor
                    blue += this.random.nextFloat() * 1.2f - 0.5f
                }

                7 -> {
                    red += randFactor
                    green += this.random.nextFloat() * 1.2f - 0.5f
                    blue += this.random.nextFloat() * 1.2f - 0.5f
                }

                8 -> {
                    red += randFactor
                    green = 0.001f
                    blue = 0.001f
                }

                9 -> {
                    red = 0.001f
                    green += randFactor
                    blue = 0.001f
                }

                10 -> {
                    red = 0.001f
                    green = 0.001f
                    blue += randFactor
                }

                else -> {}
            }
            return floatArrayOf(red, green, blue)
        }

    override fun getLightColor(partialTicks: Float): Int {
        return LightTexture.FULL_BRIGHT
    }

    protected fun getParticleSetting(level: Level?): Int {
        if (Minecraft.getInstance().level !== level) {
            return 0
        }
        return Minecraft.getInstance().options.particles().get().getId()
    }

    class Provider(private val sprites: SpriteSet?) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            type: SimpleParticleType, level: ClientLevel, x: Double, y: Double,
            z: Double, xSpeed: Double, ySpeed: Double, zSpeed: Double
        ): Particle? {
            return ParticleSparkle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites)
        }
    }

    companion object {
        private val UNTEXTURED_RENDER: ParticleRenderType = object : ParticleRenderType {
            override fun begin(tesselator: Tesselator, tm: TextureManager): BufferBuilder {
                RenderSystem.enableBlend()
                RenderSystem.defaultBlendFunc()
                RenderSystem.disableCull()
                RenderSystem.setShader(Supplier { GameRenderer.getPositionColorShader() })
                return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR)
            }
        }
    }
}