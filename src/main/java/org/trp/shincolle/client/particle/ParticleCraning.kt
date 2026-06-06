package org.trp.shincolle.client.particle

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import net.minecraft.client.Camera
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
import net.minecraft.world.phys.AABB
import java.util.function.Supplier
import kotlin.math.max
import kotlin.math.min

class ParticleCraning(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    lengthMax: Double,
    scale: Double,
    sprites: SpriteSet?
) : Particle(level, x, y, z) {
    private val lenMax: Float
    private var len: Float
    private val vt1: Array<DoubleArray?>
    private val vt2: Array<DoubleArray?>
    private val quadSize: Float

    init {
        this.setSize(0.5f, 0.5f)
        this.xd = 0.0
        this.yd = 0.0
        this.zd = 0.0
        this.lenMax = lengthMax.toFloat()
        this.quadSize = scale.toFloat()
        this.vt1 = Array<DoubleArray?>(8) { DoubleArray(3) }
        this.vt2 = Array<DoubleArray?>(8) { DoubleArray(3) }
        this.hasPhysics = false
        this.lifetime = 127
        this.rCol = 0.0f
        this.gCol = 0.0f
        this.bCol = 0.0f
        this.len = 0.0f
    }

    override fun render(buffer: VertexConsumer, camera: Camera, partialTick: Float) {
        val cameraPos = camera.getPosition()

        val sizeHead = this.quadSize
        val sizeChain = this.quadSize * 0.25f
        val pos1 = floatArrayOf(sizeHead * 0.75f, -sizeHead, -sizeHead)
        val pos2 = floatArrayOf(sizeHead * 0.75f, sizeHead, -sizeHead)
        val pos3 = floatArrayOf(-sizeHead * 0.75f, sizeHead, -sizeHead)
        val pos4 = floatArrayOf(-sizeHead * 0.75f, -sizeHead, -sizeHead)
        val pos5 = floatArrayOf(sizeChain, -sizeChain * 1.5f, -sizeChain)
        val pos6 = floatArrayOf(sizeChain, sizeChain * 1.5f, -sizeChain)
        val pos7 = floatArrayOf(-sizeChain, sizeChain * 1.5f, -sizeChain)
        val pos8 = floatArrayOf(-sizeChain, -sizeChain * 1.5f, -sizeChain)

        val hx = Mth.lerp(partialTick.toDouble(), this.xo, this.x) - cameraPos.x()
        val hy = Mth.lerp(partialTick.toDouble(), this.yo, this.y) - cameraPos.y() - this.len + this.quadSize * 5.0
        val hz = Mth.lerp(partialTick.toDouble(), this.zo, this.z) - cameraPos.z() + this.quadSize * 0.5
        val hz_chain = hz - this.quadSize * 0.47

        val z1 = this.quadSize * 0.8
        val z2 = this.quadSize * 0.25
        val y1 = this.quadSize.toDouble()

        var clen = 0.0f
        while (clen < this.len) {
            val ny = hy + clen

            this.vt2[0]!![0] = hx + pos5[0]
            this.vt2[0]!![1] = (ny + pos5[1]) + y1
            this.vt2[0]!![2] = hz_chain + pos5[2]
            this.vt2[1]!![0] = hx + pos6[0]
            this.vt2[1]!![1] = (ny + pos6[1]) + y1
            this.vt2[1]!![2] = hz_chain + pos6[2]
            this.vt2[2]!![0] = hx + pos7[0]
            this.vt2[2]!![1] = (ny + pos7[1]) + y1
            this.vt2[2]!![2] = hz_chain + pos7[2]
            this.vt2[3]!![0] = hx + pos8[0]
            this.vt2[3]!![1] = (ny + pos8[1]) + y1
            this.vt2[3]!![2] = hz_chain + pos8[2]

            this.vt2[4]!![0] = hx + pos5[0]
            this.vt2[4]!![1] = (ny + pos5[1]) + y1
            this.vt2[4]!![2] = hz_chain + pos5[2] + z2
            this.vt2[5]!![0] = hx + pos6[0]
            this.vt2[5]!![1] = (ny + pos6[1]) + y1
            this.vt2[5]!![2] = hz_chain + pos6[2] + z2
            this.vt2[6]!![0] = hx + pos7[0]
            this.vt2[6]!![1] = (ny + pos7[1]) + y1
            this.vt2[6]!![2] = hz_chain + pos7[2] + z2
            this.vt2[7]!![0] = hx + pos8[0]
            this.vt2[7]!![1] = (ny + pos8[1]) + y1
            this.vt2[7]!![2] = hz_chain + pos8[2] + z2

            renderQuads(buffer, vt2)
            clen += this.quadSize
        }

        this.vt1[0]!![0] = hx + pos1[0]
        this.vt1[0]!![1] = hy + pos1[1]
        this.vt1[0]!![2] = hz + pos1[2]
        this.vt1[1]!![0] = hx + pos2[0]
        this.vt1[1]!![1] = hy + pos2[1]
        this.vt1[1]!![2] = hz + pos2[2]
        this.vt1[2]!![0] = hx + pos3[0]
        this.vt1[2]!![1] = hy + pos3[1]
        this.vt1[2]!![2] = hz + pos3[2]
        this.vt1[3]!![0] = hx + pos4[0]
        this.vt1[3]!![1] = hy + pos4[1]
        this.vt1[3]!![2] = hz + pos4[2]

        this.vt1[4]!![0] = hx + pos1[0]
        this.vt1[4]!![1] = hy + pos1[1]
        this.vt1[4]!![2] = hz + pos1[2] + z1
        this.vt1[5]!![0] = hx + pos2[0]
        this.vt1[5]!![1] = hy + pos2[1]
        this.vt1[5]!![2] = hz + pos2[2] + z1
        this.vt1[6]!![0] = hx + pos3[0]
        this.vt1[6]!![1] = hy + pos3[1]
        this.vt1[6]!![2] = hz + pos3[2] + z1
        this.vt1[7]!![0] = hx + pos4[0]
        this.vt1[7]!![1] = hy + pos4[1]
        this.vt1[7]!![2] = hz + pos4[2] + z1

        renderQuads(buffer, vt1)
    }


    private fun renderQuads(buffer: VertexConsumer, vt: Array<DoubleArray?>) {
        emitVertex(buffer, vt[3]!!)
        emitVertex(buffer, vt[2]!!)
        emitVertex(buffer, vt[1]!!)
        emitVertex(buffer, vt[0]!!)

        emitVertex(buffer, vt[0]!!)
        emitVertex(buffer, vt[1]!!)
        emitVertex(buffer, vt[5]!!)
        emitVertex(buffer, vt[4]!!)

        emitVertex(buffer, vt[4]!!)
        emitVertex(buffer, vt[5]!!)
        emitVertex(buffer, vt[6]!!)
        emitVertex(buffer, vt[7]!!)

        emitVertex(buffer, vt[7]!!)
        emitVertex(buffer, vt[6]!!)
        emitVertex(buffer, vt[2]!!)
        emitVertex(buffer, vt[3]!!)

        emitVertex(buffer, vt[1]!!)
        emitVertex(buffer, vt[2]!!)
        emitVertex(buffer, vt[6]!!)
        emitVertex(buffer, vt[5]!!)

        emitVertex(buffer, vt[3]!!)
        emitVertex(buffer, vt[0]!!)
        emitVertex(buffer, vt[4]!!)
        emitVertex(buffer, vt[7]!!)
    }

    private fun emitVertex(buffer: VertexConsumer, pos: DoubleArray) {
        buffer.addVertex(pos[0].toFloat(), pos[1].toFloat(), pos[2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, 1.0f)
    }

    override fun getLightColor(partialTick: Float): Int {
        return LightTexture.pack(3, 3)
    }

    override fun getRenderType(): ParticleRenderType {
        return UNTEXTURED_RENDER
    }

    override fun tick() {
        this.xo = this.x
        this.yo = this.y
        this.zo = this.z

        val half = this.lifetime * 0.45f
        val half2 = this.lifetime - half
        if (this.age <= half) {
            this.len = this.age / half * this.lenMax
        } else if (this.age <= half2) {
            this.len = this.lenMax
        } else {
            this.len = (this.lifetime - this.age) / half * this.lenMax
        }

        val minY = min(this.y, this.y - this.len)
        val maxY = max(this.y, this.y - this.len) + (this.quadSize * 6.0)
        this.setBoundingBox(AABB(this.x - 1.0, minY, this.z - 1.0, this.x + 1.0, maxY, this.z + 1.0))

        if (this.age++ >= this.lifetime) {
            this.remove()
        }
    }

    class Provider(private val sprites: SpriteSet?) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            type: SimpleParticleType, level: ClientLevel, x: Double, y: Double, z: Double,
            xSpeed: Double, ySpeed: Double, zSpeed: Double
        ): Particle? {
            val lenMax = xSpeed
            val scale = if (ySpeed > 0) ySpeed else 0.25
            return ParticleCraning(level, x, y, z, lenMax, scale, this.sprites)
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