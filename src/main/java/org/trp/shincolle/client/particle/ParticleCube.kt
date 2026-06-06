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
import net.minecraft.world.entity.LivingEntity
import org.trp.shincolle.utility.CalcHelper.getLookDegree
import org.trp.shincolle.utility.CalcHelper.rotateXYZByYawPitch
import org.trp.shincolle.utility.CalcHelper.rotateXZByAxis
import java.util.function.Supplier

class ParticleCube(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    scale: Float,
    type: Int,
    hostEntityId: Int,
    par1: Double,
    par2: Double,
    par3: Double
) : Particle(level, x, y, z) {
    private val particleType: Int
    private var quadSize: Float
    private var shotYaw = 0f
    private var shotPitch = 0f
    private var scaleOut = 0f
    private var scaleIn = 0f
    private var alphaOut = 0f
    private var alphaIn = 0f
    private val par1: Double
    private val par2: Double
    private val par3: Double
    private val hostEntityId: Int

    init {
        this.setSize(0.0f, 0.0f)
        this.hostEntityId = hostEntityId
        this.xd = 0.0
        this.yd = 0.0
        this.zd = 0.0
        this.quadSize = scale
        this.particleType = type
        this.par1 = par1
        this.par2 = par2
        this.par3 = par3
        this.hasPhysics = false

        if (type == 1) {
            this.lifetime = 30
            this.rCol = 1.0f
            this.gCol = 0.8f
            this.bCol = 0.9f
        } else {
            this.quadSize = par1.toFloat()
            this.lifetime = 40
            this.rCol = 1.0f
            this.gCol = 0.8f
            this.bCol = 0.9f
        }
    }

    override fun tick() {
        this.xo = this.x
        this.yo = this.y
        this.zo = this.z

        if (this.age++ > this.lifetime) {
            this.remove()
            return
        }

        val host = this.level.getEntity(this.hostEntityId)
        if (host == null || !host.isAlive) {
            this.remove()
            return
        }

        if (this.particleType == 1) {
            val look = host.getViewVector(1.0f)
            val lookDeg = getLookDegree(look.x, look.y, look.z, false)
            val posOffset = rotateXYZByYawPitch(0.0f, 0.0f, host.getBbWidth() * 2.0f, lookDeg[0], lookDeg[1], 1.0f)
            this.setPos(host.getX() + posOffset[0], host.getY() + host.getBbHeight() * 0.6, host.getZ() + posOffset[2])
            this.shotYaw = lookDeg[0]
            this.shotPitch = lookDeg[1]
            this.alphaIn =
                if (this.age > 20) 1.0f + (20 - this.age) * 0.1f else (if (this.age < 4) 0.2f + this.age * 0.2f else 0.95f)
            this.alphaOut = 0.0f
            if (this.age > 20) {
                this.scaleOut = this.quadSize * (1.0f + (this.age - 20))
                this.scaleIn = this.quadSize * 0.4f * (1.0f - (this.age - 20) * 0.1f)
            } else if (this.age < 8) {
                this.scaleOut = this.quadSize * 0.3f * (this.age * 0.3f)
                this.scaleIn = this.quadSize * 0.4f * (this.age * 0.125f)
            } else {
                this.scaleOut = this.quadSize
                this.scaleIn = this.quadSize * 0.4f
            }
            this.scaleOut += this.random.nextFloat() * 0.04f - 0.01f
            this.scaleIn += this.random.nextFloat() * 0.04f - 0.005f
        } else {
            val yaw = if (host is LivingEntity) host.yBodyRot else host.getYRot()
            val pitch = host.getXRot()
            val posOffset = rotateXZByAxis(host.getBbWidth() * 2.0f, 0.0f, yaw * Mth.DEG_TO_RAD, 1.0f)
            this.setPos(host.getX() + posOffset[1], host.getY() + host.getBbHeight() * 0.6, host.getZ() + posOffset[0])
            this.shotYaw = yaw * Mth.DEG_TO_RAD
            this.shotPitch = pitch * Mth.DEG_TO_RAD
            this.alphaIn =
                if (this.age < 32) this.random.nextFloat() * 0.5f + 0.75f else (this.lifetime - this.age) * 0.1f + 0.2f
            this.alphaOut = this.alphaIn * 0.25f
            this.scaleOut = this.quadSize * this.age * ((Mth.cos(this.age.toFloat()) + 1.0f) * 0.005f + 0.015f)
            this.scaleIn = this.scaleOut * 0.75f
            this.scaleOut += this.random.nextFloat() * 0.04f - 0.01f
            this.scaleIn += this.random.nextFloat() * 0.04f - 0.005f
        }
    }

    override fun render(buffer: VertexConsumer, camera: Camera, partialTicks: Float) {
        if (this.age <= 1) {
            return
        }

        val v1 = rotateXYZByYawPitch(-1.0f, -1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleOut)
        val v2 = rotateXYZByYawPitch(-1.0f, 1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleOut)
        val v3 = rotateXYZByYawPitch(1.0f, 1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleOut)
        val v4 = rotateXYZByYawPitch(1.0f, -1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleOut)
        val v5 = rotateXYZByYawPitch(-1.0f, -1.0f, 1.0f, this.shotYaw, this.shotPitch, this.scaleOut)
        val v6 = rotateXYZByYawPitch(-1.0f, 1.0f, 1.0f, this.shotYaw, this.shotPitch, this.scaleOut)
        val v7 = rotateXYZByYawPitch(1.0f, 1.0f, 1.0f, this.shotYaw, this.shotPitch, this.scaleOut)
        val v8 = rotateXYZByYawPitch(1.0f, -1.0f, 1.0f, this.shotYaw, this.shotPitch, this.scaleOut)

        val t1 = rotateXYZByYawPitch(-1.0f, -1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleIn)
        val t2 = rotateXYZByYawPitch(-1.0f, 1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleIn)
        val t3 = rotateXYZByYawPitch(1.0f, 1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleIn)
        val t4 = rotateXYZByYawPitch(1.0f, -1.0f, -1.0f, this.shotYaw, this.shotPitch, this.scaleIn)
        val t5 = rotateXYZByYawPitch(-1.0f, -1.0f, 1.0f, this.shotYaw, this.shotPitch, this.scaleIn)
        val t6 = rotateXYZByYawPitch(-1.0f, 1.0f, 1.0f, this.shotYaw, this.shotPitch, this.scaleIn)
        val t7 = rotateXYZByYawPitch(1.0f, 1.0f, 1.0f, this.shotYaw, this.shotPitch, this.scaleIn)
        val t8 = rotateXYZByYawPitch(1.0f, -1.0f, 1.0f, this.shotYaw, this.shotPitch, this.scaleIn)

        val cameraPos = camera.getPosition()
        val hx = Mth.lerp(partialTicks.toDouble(), this.xo, this.x) - cameraPos.x()
        val hy = Mth.lerp(partialTicks.toDouble(), this.yo, this.y) - cameraPos.y()
        val hz = Mth.lerp(partialTicks.toDouble(), this.zo, this.z) - cameraPos.z()

        val vt = Array<DoubleArray?>(8) { DoubleArray(3) }
        val vt2 = Array<DoubleArray?>(8) { DoubleArray(3) }

        vt[0]!![0] = hx + v1[0]
        vt[0]!![1] = hy + v1[1]
        vt[0]!![2] = hz + v1[2]
        vt[1]!![0] = hx + v2[0]
        vt[1]!![1] = hy + v2[1]
        vt[1]!![2] = hz + v2[2]
        vt[2]!![0] = hx + v3[0]
        vt[2]!![1] = hy + v3[1]
        vt[2]!![2] = hz + v3[2]
        vt[3]!![0] = hx + v4[0]
        vt[3]!![1] = hy + v4[1]
        vt[3]!![2] = hz + v4[2]
        vt[4]!![0] = hx + v5[0]
        vt[4]!![1] = hy + v5[1]
        vt[4]!![2] = hz + v5[2]
        vt[5]!![0] = hx + v6[0]
        vt[5]!![1] = hy + v6[1]
        vt[5]!![2] = hz + v6[2]
        vt[6]!![0] = hx + v7[0]
        vt[6]!![1] = hy + v7[1]
        vt[6]!![2] = hz + v7[2]
        vt[7]!![0] = hx + v8[0]
        vt[7]!![1] = hy + v8[1]
        vt[7]!![2] = hz + v8[2]

        vt2[0]!![0] = hx + t1[0]
        vt2[0]!![1] = hy + t1[1]
        vt2[0]!![2] = hz + t1[2]
        vt2[1]!![0] = hx + t2[0]
        vt2[1]!![1] = hy + t2[1]
        vt2[1]!![2] = hz + t2[2]
        vt2[2]!![0] = hx + t3[0]
        vt2[2]!![1] = hy + t3[1]
        vt2[2]!![2] = hz + t3[2]
        vt2[3]!![0] = hx + t4[0]
        vt2[3]!![1] = hy + t4[1]
        vt2[3]!![2] = hz + t4[2]
        vt2[4]!![0] = hx + t5[0]
        vt2[4]!![1] = hy + t5[1]
        vt2[4]!![2] = hz + t5[2]
        vt2[5]!![0] = hx + t6[0]
        vt2[5]!![1] = hy + t6[1]
        vt2[5]!![2] = hz + t6[2]
        vt2[6]!![0] = hx + t7[0]
        vt2[6]!![1] = hy + t7[1]
        vt2[6]!![2] = hz + t7[2]
        vt2[7]!![0] = hx + t8[0]
        vt2[7]!![1] = hy + t8[1]
        vt2[7]!![2] = hz + t8[2]

        buffer.addVertex(vt2[7]!![0].toFloat(), vt2[7]!![1].toFloat(), vt2[7]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[6]!![0].toFloat(), vt2[6]!![1].toFloat(), vt2[6]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[5]!![0].toFloat(), vt2[5]!![1].toFloat(), vt2[5]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[4]!![0].toFloat(), vt2[4]!![1].toFloat(), vt2[4]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)

        buffer.addVertex(vt2[3]!![0].toFloat(), vt2[3]!![1].toFloat(), vt2[3]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[2]!![0].toFloat(), vt2[2]!![1].toFloat(), vt2[2]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[6]!![0].toFloat(), vt2[6]!![1].toFloat(), vt2[6]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[7]!![0].toFloat(), vt2[7]!![1].toFloat(), vt2[7]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)

        buffer.addVertex(vt2[0]!![0].toFloat(), vt2[0]!![1].toFloat(), vt2[0]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[1]!![0].toFloat(), vt2[1]!![1].toFloat(), vt2[1]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[2]!![0].toFloat(), vt2[2]!![1].toFloat(), vt2[2]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[3]!![0].toFloat(), vt2[3]!![1].toFloat(), vt2[3]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)

        buffer.addVertex(vt2[4]!![0].toFloat(), vt2[4]!![1].toFloat(), vt2[4]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[5]!![0].toFloat(), vt2[5]!![1].toFloat(), vt2[5]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[1]!![0].toFloat(), vt2[1]!![1].toFloat(), vt2[1]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[0]!![0].toFloat(), vt2[0]!![1].toFloat(), vt2[0]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)

        buffer.addVertex(vt2[2]!![0].toFloat(), vt2[2]!![1].toFloat(), vt2[2]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[1]!![0].toFloat(), vt2[1]!![1].toFloat(), vt2[1]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[5]!![0].toFloat(), vt2[5]!![1].toFloat(), vt2[5]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[6]!![0].toFloat(), vt2[6]!![1].toFloat(), vt2[6]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)

        buffer.addVertex(vt2[3]!![0].toFloat(), vt2[3]!![1].toFloat(), vt2[3]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[7]!![0].toFloat(), vt2[7]!![1].toFloat(), vt2[7]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[4]!![0].toFloat(), vt2[4]!![1].toFloat(), vt2[4]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)
        buffer.addVertex(vt2[0]!![0].toFloat(), vt2[0]!![1].toFloat(), vt2[0]!![2].toFloat())
            .setColor(1.0f, 1.0f, 1.0f, this.alphaIn)

        buffer.addVertex(vt[7]!![0].toFloat(), vt[7]!![1].toFloat(), vt[7]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[6]!![0].toFloat(), vt[6]!![1].toFloat(), vt[6]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[5]!![0].toFloat(), vt[5]!![1].toFloat(), vt[5]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[4]!![0].toFloat(), vt[4]!![1].toFloat(), vt[4]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)

        buffer.addVertex(vt[3]!![0].toFloat(), vt[3]!![1].toFloat(), vt[3]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[2]!![0].toFloat(), vt[2]!![1].toFloat(), vt[2]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[6]!![0].toFloat(), vt[6]!![1].toFloat(), vt[6]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[7]!![0].toFloat(), vt[7]!![1].toFloat(), vt[7]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)

        buffer.addVertex(vt[0]!![0].toFloat(), vt[0]!![1].toFloat(), vt[0]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[1]!![0].toFloat(), vt[1]!![1].toFloat(), vt[1]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[2]!![0].toFloat(), vt[2]!![1].toFloat(), vt[2]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[3]!![0].toFloat(), vt[3]!![1].toFloat(), vt[3]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)

        buffer.addVertex(vt[4]!![0].toFloat(), vt[4]!![1].toFloat(), vt[4]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[5]!![0].toFloat(), vt[5]!![1].toFloat(), vt[5]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[1]!![0].toFloat(), vt[1]!![1].toFloat(), vt[1]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[0]!![0].toFloat(), vt[0]!![1].toFloat(), vt[0]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)

        buffer.addVertex(vt[2]!![0].toFloat(), vt[2]!![1].toFloat(), vt[2]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[1]!![0].toFloat(), vt[1]!![1].toFloat(), vt[1]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[5]!![0].toFloat(), vt[5]!![1].toFloat(), vt[5]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[6]!![0].toFloat(), vt[6]!![1].toFloat(), vt[6]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)

        buffer.addVertex(vt[3]!![0].toFloat(), vt[3]!![1].toFloat(), vt[3]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[7]!![0].toFloat(), vt[7]!![1].toFloat(), vt[7]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[4]!![0].toFloat(), vt[4]!![1].toFloat(), vt[4]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
        buffer.addVertex(vt[0]!![0].toFloat(), vt[0]!![1].toFloat(), vt[0]!![2].toFloat())
            .setColor(this.rCol, this.gCol, this.bCol, this.alphaOut)
    }

    override fun getRenderType(): ParticleRenderType {
        return UNTEXTURED_RENDER
    }

    override fun getLightColor(partialTicks: Float): Int {
        return LightTexture.FULL_BRIGHT
    }

    class Provider(sprites: SpriteSet?) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            type: SimpleParticleType,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            scale: Double,
            hostEntityId: Double,
            particleType: Double
        ): Particle? {
            return ParticleCube(
                level,
                x,
                y,
                z,
                scale.toFloat(),
                Math.round(particleType).toInt(),
                Math.round(hostEntityId).toInt(),
                1.0,
                0.0,
                0.0
            )
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
                return "SHINCOLLE_CUBE"
            }
        }
    }
}
