package org.trp.shincolle.client.particle

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.Mth
import java.util.function.Supplier
import kotlin.math.atan2
import kotlin.math.sqrt

class ParticleBeam protected constructor(
    level: ClientLevel, x: Double, y: Double, z: Double,
    private val hostId: Int, private val targetId: Int, private val baseScale: Float
) : Particle(level, x, y, z) {
    private var shotYaw = 0f
    private var shotPitch = 0f
    private var scaleOut = 0f
    private var scaleIn = 0f
    private var alphaOut = 0f
    private var alphaIn = 0f

    private var tarX = 0.0
    private var tarY = 0.0
    private var tarZ = 0.0

    init {
        this.lifetime = 30

        this.rCol = 1.0f
        this.gCol = 0.8f
        this.bCol = 0.9f

        this.hasPhysics = false
        this.setSize(0.0f, 0.0f)

        updatePositions()
    }

    private fun updatePositions() {
        val host = this.level.getEntity(this.hostId)
        val target = this.level.getEntity(this.targetId)

        if (host == null || target == null) {
            return
        }

        val dx = target.x - host.x
        val dy = (target.y + target.bbHeight * 0.5) - (host.y + host.bbHeight * 0.6)
        val dz = target.z - host.z

        val d1 = sqrt(dx * dx + dy * dy + dz * dz)
        var motX = dx
        var motY = dy
        var motZ = dz
        if (d1 > 1.0E-4) {
            motX /= d1
            motY /= d1
            motZ /= d1
        }
        val f1 = sqrt(motX * motX + motZ * motZ)
        this.shotPitch = -atan2(motY, f1).toFloat()
        this.shotYaw = -atan2(motX, motZ).toFloat()

        val posOffset = rotateXYZByYawPitch(0.0f, 0.0f, host.bbWidth * 2.0f, this.shotYaw, this.shotPitch, 1.0f)

        this.x = host.x + posOffset[0]
        this.y = host.y + host.bbHeight * 0.6 + posOffset[1]
        this.z = host.z + posOffset[2]

        this.tarX = target.x
        this.tarY = target.y + target.bbHeight * 0.5
        this.tarZ = target.z
    }

    override fun tick() {
        this.xo = this.x
        this.yo = this.y
        this.zo = this.z

        val host = this.level.getEntity(this.hostId)
        val target = this.level.getEntity(this.targetId)
        if (host == null || target == null || !host.isAlive || !target.isAlive) {
            this.remove()
            return
        }

        updatePositions()

        if (this.age > 20) {
            this.alphaIn = 1.0f + (20 - this.age) * 0.1f
            this.alphaOut = this.alphaIn * 0.25f
        } else if (this.age < 4) {
            this.alphaIn = 0.2f + this.age * 0.2f
            this.alphaOut = this.alphaIn * 0.25f
        } else {
            this.alphaIn = 1.0f
            this.alphaOut = 0.1f + this.random.nextFloat() * 0.25f
        }

        if (this.age > 20) {
            this.scaleOut = this.baseScale * (1.0f + (this.age - 20))
            this.scaleIn = this.baseScale * 0.35f * (1.0f - (this.age - 20) * 0.1f)
        } else if (this.age < 8) {
            this.scaleOut = this.baseScale * 0.3f * (this.age * 0.3f)
            this.scaleIn = this.baseScale * 0.35f * (this.age * 0.125f)
        } else {
            this.scaleOut = this.baseScale
            this.scaleIn = this.baseScale * 0.35f
        }

        this.scaleOut += this.random.nextFloat() * 0.2f - 0.05f
        this.scaleIn += this.random.nextFloat() * 0.08f - 0.04f

        if (this.age++ >= this.lifetime) {
            this.remove()
        }
    }

    private fun rotateXYZByYawPitch(x: Float, y: Float, z: Float, yaw: Float, pitch: Float, scale: Float): FloatArray {
        val cosYaw = Mth.cos(yaw)
        val sinYaw = Mth.sin(yaw)
        val cosPitch = Mth.cos(-pitch)
        val sinPitch = Mth.sin(-pitch)
        val newPos = floatArrayOf(x, y, z)
        newPos[1] = y * cosPitch + z * sinPitch
        newPos[2] = z * cosPitch - y * sinPitch
        val x2 = newPos[0]
        val z2 = newPos[2]
        newPos[0] = x2 * cosYaw - z2 * sinYaw
        newPos[2] = z2 * cosYaw + x2 * sinYaw
        newPos[0] *= scale
        newPos[1] *= scale
        newPos[2] *= scale
        return newPos
    }

    override fun render(buffer: VertexConsumer, camera: Camera, partialTicks: Float) {
        if (this.age <= 1) {
            return
        }

        val cameraPos = camera.position
        val hx = (Mth.lerp(partialTicks.toDouble(), this.xo, this.x) - cameraPos.x()).toFloat()
        val hy = (Mth.lerp(partialTicks.toDouble(), this.yo, this.y) - cameraPos.y()).toFloat()
        val hz = (Mth.lerp(partialTicks.toDouble(), this.zo, this.z) - cameraPos.z()).toFloat()

        val tx = (this.tarX - cameraPos.x()).toFloat()
        val ty = (this.tarY - cameraPos.y()).toFloat()
        val tz = (this.tarZ - cameraPos.z()).toFloat()

        val light = LightTexture.FULL_BRIGHT

        val v1 = rotateXYZByYawPitch(1.0f, -1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleOut)
        val v2 = rotateXYZByYawPitch(1.0f, 1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleOut)
        val v3 = rotateXYZByYawPitch(-1.0f, 1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleOut)
        val v4 = rotateXYZByYawPitch(-1.0f, -1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleOut)

        val v5 = rotateXYZByYawPitch(1.0f, -1.0f, 0.0f, this.shotYaw, this.shotPitch, this.scaleIn)
        val v6 = rotateXYZByYawPitch(1.0f, 1.0f, 0.0f, this.shotYaw, this.shotPitch, this.scaleIn)
        val v7 = rotateXYZByYawPitch(-1.0f, 1.0f, 0.0f, this.shotYaw, this.shotPitch, this.scaleIn)
        val v8 = rotateXYZByYawPitch(-1.0f, -1.0f, 0.0f, this.shotYaw, this.shotPitch, this.scaleIn)

        drawQuad(
            buffer, tx + v1[0], ty + v1[1], tz + v1[2], tx + v2[0], ty + v2[1], tz + v2[2],
            hx + v2[0], hy + v2[1], hz + v2[2], hx + v1[0], hy + v1[1], hz + v1[2],
            this.rCol, this.gCol, this.bCol, this.alphaOut, light
        )
        drawQuad(
            buffer, tx + v4[0], ty + v4[1], tz + v4[2], tx + v3[0], ty + v3[1], tz + v3[2],
            hx + v3[0], hy + v3[1], hz + v3[2], hx + v4[0], hy + v4[1], hz + v4[2],
            this.rCol, this.gCol, this.bCol, this.alphaOut, light
        )
        drawQuad(
            buffer, tx + v1[0], ty + v1[1], tz + v1[2], tx + v4[0], ty + v4[1], tz + v4[2],
            hx + v4[0], hy + v4[1], hz + v4[2], hx + v1[0], hy + v1[1], hz + v1[2],
            this.rCol, this.gCol, this.bCol, this.alphaOut, light
        )
        drawQuad(
            buffer, tx + v2[0], ty + v2[1], tz + v2[2], tx + v3[0], ty + v3[1], tz + v3[2],
            hx + v3[0], hy + v3[1], hz + v3[2], hx + v2[0], hy + v2[1], hz + v2[2],
            this.rCol, this.gCol, this.bCol, this.alphaOut, light
        )

        drawQuad(
            buffer, tx + v5[0], ty + v5[1], tz + v5[2], tx + v6[0], ty + v6[1], tz + v6[2],
            hx + v6[0], hy + v6[1], hz + v6[2], hx + v5[0], hy + v5[1], hz + v5[2],
            1.0f, 1.0f, 1.0f, this.alphaIn, light
        )
        drawQuad(
            buffer, tx + v8[0], ty + v8[1], tz + v8[2], tx + v7[0], ty + v7[1], tz + v7[2],
            hx + v7[0], hy + v7[1], hz + v7[2], hx + v8[0], hy + v8[1], hz + v8[2],
            1.0f, 1.0f, 1.0f, this.alphaIn, light
        )
        drawQuad(
            buffer, tx + v5[0], ty + v5[1], tz + v5[2], tx + v8[0], ty + v8[1], tz + v8[2],
            hx + v8[0], hy + v8[1], hz + v8[2], hx + v5[0], hy + v5[1], hz + v5[2],
            1.0f, 1.0f, 1.0f, this.alphaIn, light
        )
        drawQuad(
            buffer, tx + v6[0], ty + v6[1], tz + v6[2], tx + v7[0], ty + v7[1], tz + v7[2],
            hx + v7[0], hy + v7[1], hz + v7[2], hx + v6[0], hy + v6[1], hz + v6[2],
            1.0f, 1.0f, 1.0f, this.alphaIn, light
        )
    }

    private fun drawQuad(
        buffer: VertexConsumer, x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float,
        x3: Float, y3: Float, z3: Float, x4: Float, y4: Float, z4: Float,
        r: Float, g: Float, b: Float, alpha: Float, light: Int
    ) {
        buffer.addVertex(x1, y1, z1).setColor(r, g, b, alpha)
        buffer.addVertex(x2, y2, z2).setColor(r, g, b, alpha)
        buffer.addVertex(x3, y3, z3).setColor(r, g, b, alpha)
        buffer.addVertex(x4, y4, z4).setColor(r, g, b, alpha)
    }

    override fun getRenderType(): ParticleRenderType {
        return UNTEXTURED_RENDER
    }

    class Provider : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            type: SimpleParticleType, level: ClientLevel,
            x: Double, y: Double, z: Double,
            vx: Double, vy: Double, vz: Double
        ): Particle? {
            return ParticleBeam(level, x, y, z, vx.toInt(), vy.toInt(), vz.toFloat())
        }
    }

    companion object {
        private val UNTEXTURED_RENDER: ParticleRenderType = object : ParticleRenderType {
            override fun begin(tesselator: Tesselator, textureManager: TextureManager): BufferBuilder {
                RenderSystem.enableBlend()
                RenderSystem.defaultBlendFunc()
                RenderSystem.disableCull()
                RenderSystem.setShader(Supplier { GameRenderer.getPositionColorShader() })
                return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR)
            }

            override fun toString(): String {
                return "SHINCOLLE_BEAM_UNTEXTURED"
            }
        }
    }
}
