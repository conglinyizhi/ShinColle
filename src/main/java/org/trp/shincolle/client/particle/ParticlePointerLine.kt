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
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import org.trp.shincolle.Shincolle
import java.util.function.Supplier
import kotlin.math.atan2
import kotlin.math.sqrt

class ParticlePointerLine protected constructor(
    level: ClientLevel, x: Double, y: Double, z: Double,
    vx: Double, vy: Double, vz: Double, private val particleType: Int
) : Particle(level, x, y, z) {
    private val vx: Float
    private val vy: Float
    private val vz: Float
    private val shotYaw: Float
    private val shotPitch: Float

    // Type 0 params (Untextured Tube)
    private val scaleOut = 0.05f
    private val scaleIn = 0.0125f
    private val alphaOut = 0.1f
    private val alphaIn = 0.2f

    // Type 1 params (Textured Plane)
    private val particleScale = 0.5f

    init {
        this.vx = vx.toFloat()
        this.vy = vy.toFloat()
        this.vz = vz.toFloat()

        val d1 = sqrt(vx * vx + vy * vy + vz * vz)
        var motX = vx
        var motY = vy
        var motZ = vz
        if (d1 > 1.0E-4) {
            motX /= d1
            motY /= d1
            motZ /= d1
        }
        val f1 = sqrt(motX * motX + motZ * motZ)
        this.shotPitch = -atan2(motY, f1).toFloat()
        this.shotYaw = -atan2(motX, motZ).toFloat()

        this.hasPhysics = false

        if (this.particleType == 0) {
            this.lifetime = 8
            this.rCol = 1.0f
            this.gCol = 1.0f
            this.bCol = 1.0f
        } else if (this.particleType == 2) {
            this.lifetime = 11
            this.age = 4
            this.rCol = 1.0f
            this.gCol = 0.0f
            this.bCol = 0.0f
            this.alpha = 1.0f
        } else {
            this.lifetime = 11
            this.age = 4
            this.rCol = 1.0f
            this.gCol = 0.0f
            this.bCol = 1.0f
            this.alpha = 1.0f
        }
    }

    override fun tick() {
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
        if (this.particleType == 0 && this.age <= 1) {
            return
        }

        val cameraPos = camera.getPosition()
        val hx = (Mth.lerp(partialTicks.toDouble(), this.xo, this.x) - cameraPos.x()).toFloat()
        val hy = (Mth.lerp(partialTicks.toDouble(), this.yo, this.y) - cameraPos.y()).toFloat()
        val hz = (Mth.lerp(partialTicks.toDouble(), this.zo, this.z) - cameraPos.z()).toFloat()

        val tx = hx + this.vx
        val ty = hy + this.vy
        val tz = hz + this.vz

        val light = LightTexture.FULL_BRIGHT

        if (this.particleType == 0) {
            // Untextured tube (ParticleLaserNoTexture logic)
            val v1 = rotateXYZByYawPitch(1.0f, -1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleOut)
            val v2 = rotateXYZByYawPitch(1.0f, 1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleOut)
            val v3 = rotateXYZByYawPitch(-1.0f, 1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleOut)
            val v4 = rotateXYZByYawPitch(-1.0f, -1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleOut)

            val v5 = rotateXYZByYawPitch(1.0f, -1.0f, 0.0f, this.shotYaw, this.shotPitch, this.scaleIn)
            val v6 = rotateXYZByYawPitch(1.0f, 1.0f, 0.0f, this.shotYaw, this.shotPitch, this.scaleIn)
            val v7 = rotateXYZByYawPitch(-1.0f, 1.0f, 0.0f, this.shotYaw, this.shotPitch, this.scaleIn)
            val v8 = rotateXYZByYawPitch(-1.0f, -1.0f, 0.0f, this.shotYaw, this.shotPitch, this.scaleIn)

            // Outer Tube
            drawQuad(
                buffer, tx + v1[0], ty + v1[1], tz + v1[2], tx + v2[0], ty + v2[1], tz + v2[2],
                hx + v2[0], hy + v2[1], hz + v2[2], hx + v1[0], hy + v1[1], hz + v1[2], this.alphaOut, light
            )
            drawQuad(
                buffer, tx + v4[0], ty + v4[1], tz + v4[2], tx + v3[0], ty + v3[1], tz + v3[2],
                hx + v3[0], hy + v3[1], hz + v3[2], hx + v4[0], hy + v4[1], hz + v4[2], this.alphaOut, light
            )
            drawQuad(
                buffer, tx + v1[0], ty + v1[1], tz + v1[2], tx + v4[0], ty + v4[1], tz + v4[2],
                hx + v4[0], hy + v4[1], hz + v4[2], hx + v1[0], hy + v1[1], hz + v1[2], this.alphaOut, light
            )
            drawQuad(
                buffer, tx + v2[0], ty + v2[1], tz + v2[2], tx + v3[0], ty + v3[1], tz + v3[2],
                hx + v3[0], hy + v3[1], hz + v3[2], hx + v2[0], hy + v2[1], hz + v2[2], this.alphaOut, light
            )

            // Inner Tube
            drawQuad(
                buffer, tx + v5[0], ty + v5[1], tz + v5[2], tx + v6[0], ty + v6[1], tz + v6[2],
                hx + v6[0], hy + v6[1], hz + v6[2], hx + v5[0], hy + v5[1], hz + v5[2], this.alphaIn, light
            )
            drawQuad(
                buffer, tx + v8[0], ty + v8[1], tz + v8[2], tx + v7[0], ty + v7[1], tz + v7[2],
                hx + v7[0], hy + v7[1], hz + v7[2], hx + v8[0], hy + v8[1], hz + v8[2], this.alphaIn, light
            )
            drawQuad(
                buffer, tx + v5[0], ty + v5[1], tz + v5[2], tx + v8[0], ty + v8[1], tz + v8[2],
                hx + v8[0], hy + v8[1], hz + v8[2], hx + v5[0], hy + v5[1], hz + v5[2], this.alphaIn, light
            )
            drawQuad(
                buffer, tx + v6[0], ty + v6[1], tz + v6[2], tx + v7[0], ty + v7[1], tz + v7[2],
                hx + v7[0], hy + v7[1], hz + v7[2], hx + v6[0], hy + v6[1], hz + v6[2], this.alphaIn, light
            )
        } else {
            // Textured Plane (ParticleLaser logic)
            val minU = 0.0f
            val maxU = this.random.nextInt(32).toFloat() + 32
            val minV = (this.age % 12) / 12.0f
            val maxV = minV + 0.08333333f

            val yOff = this.particleScale * 0.3f

            drawTexturedQuad(
                buffer,
                tx,
                ty,
                tz,
                tx,
                ty + yOff,
                tz,
                hx,
                hy + yOff,
                hz,
                hx,
                hy,
                hz,
                maxU,
                minV,
                minU,
                maxV,
                light
            )
            drawTexturedQuad(
                buffer,
                hx,
                hy,
                hz,
                hx,
                hy + yOff,
                hz,
                tx,
                ty + yOff,
                tz,
                tx,
                ty,
                tz,
                minU,
                maxV,
                maxU,
                minV,
                light
            )
        }
    }

    private fun drawQuad(
        buffer: VertexConsumer, x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float,
        x3: Float, y3: Float, z3: Float, x4: Float, y4: Float, z4: Float, alpha: Float, light: Int
    ) {
        buffer.addVertex(x1, y1, z1).setColor(this.rCol, this.gCol, this.bCol, alpha)
        buffer.addVertex(x2, y2, z2).setColor(this.rCol, this.gCol, this.bCol, alpha)
        buffer.addVertex(x3, y3, z3).setColor(this.rCol, this.gCol, this.bCol, alpha)
        buffer.addVertex(x4, y4, z4).setColor(this.rCol, this.gCol, this.bCol, alpha)
    }

    private fun drawTexturedQuad(
        buffer: VertexConsumer, x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float,
        x3: Float, y3: Float, z3: Float, x4: Float, y4: Float, z4: Float,
        u1: Float, v1: Float, u2: Float, v2: Float, light: Int
    ) {
        buffer.addVertex(x1, y1, z1).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setUv(u1, v2).setLight(light)
        buffer.addVertex(x2, y2, z2).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setUv(u1, v1).setLight(light)
        buffer.addVertex(x3, y3, z3).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setUv(u2, v1).setLight(light)
        buffer.addVertex(x4, y4, z4).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setUv(u2, v2).setLight(light)
    }

    override fun getRenderType(): ParticleRenderType {
        return if (this.particleType == 1) TEXTURED_RENDER else UNTEXTURED_RENDER
    }

    class Provider(private val type: Int, ignored: FloatArray?) : ParticleProvider<SimpleParticleType?> {
        override fun createParticle(
            type: SimpleParticleType, level: ClientLevel,
            x: Double, y: Double, z: Double,
            vx: Double, vy: Double, vz: Double
        ): Particle? {
            return ParticlePointerLine(level, x, y, z, vx, vy, vz, this.type)
        }
    }

    companion object {
        private val TEXTURE_LASER: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/particle/particlelaser.png")

        private val UNTEXTURED_RENDER: ParticleRenderType = object : ParticleRenderType {
            override fun begin(tesselator: Tesselator, textureManager: TextureManager): BufferBuilder {
                RenderSystem.enableBlend()
                RenderSystem.defaultBlendFunc()
                RenderSystem.disableCull()
                RenderSystem.setShader(Supplier { GameRenderer.getPositionColorShader() })
                return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR)
            }

            override fun toString(): String {
                return "SHINCOLLE_POINTER_LINE_UNTEXTURED"
            }
        }

        private val TEXTURED_RENDER: ParticleRenderType = object : ParticleRenderType {
            override fun begin(tesselator: Tesselator, textureManager: TextureManager): BufferBuilder {
                RenderSystem.setShader(Supplier { GameRenderer.getPositionColorTexLightmapShader() })
                RenderSystem.setShaderTexture(0, TEXTURE_LASER)
                RenderSystem.enableBlend()
                RenderSystem.defaultBlendFunc()
                RenderSystem.disableCull()
                return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
            }

            override fun toString(): String {
                return "SHINCOLLE_POINTER_LINE_TEXTURED"
            }
        }
    }
}
