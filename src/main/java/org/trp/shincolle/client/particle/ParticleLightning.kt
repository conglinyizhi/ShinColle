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
import org.joml.Vector3f
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.utility.CalcHelper.rotateXZByAxis
import java.util.function.Supplier

class ParticleLightning protected constructor(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    scale: Double,
    hostEntityId: Int,
    particleType: Int
) : Particle(level, x, y, z) {
    private val hostEntityId: Int
    private val particleType: Int
    private var numStem = 4
    private var scaleX = 0.1f
    private var scaleY = 0.12f
    private var scaleZ = 0.1f
    private var stemWidth = 0.01f
    private var prevShape: Array<DoubleArray> = arrayOf()

    init {
        var scale = scale
        this.setSize(0.0f, 0.0f)
        this.hostEntityId = hostEntityId
        this.particleType = particleType

        this.hasPhysics = false
        this.xd = 0.0
        this.yd = 0.0
        this.zd = 0.0
        this.scale(scale.toFloat())

        val host = level.getEntity(hostEntityId)
        val hostWidth = if (host != null) host.getBbWidth() else 1.0f
        val hostHeight = if (host != null) host.getBbHeight() else 2.0f

        if (scale <= 0.0) {
            scale = 1.0
        }

        when (this.particleType) {
            1 -> {
                this.rCol = 1.0f
                this.gCol = 0.5f
                this.bCol = 0.7f
                this.alpha = 1.0f
                this.numStem = 4
                this.scaleX = 0.5f + hostWidth * 0.5f
                this.scaleY = 0.5f + hostWidth * 0.5f
                this.scaleZ = 0.5f + hostWidth * 0.5f
                this.stemWidth = 0.01f * hostWidth
                this.lifetime = 40
            }

            2 -> {
                this.rCol = 1.0f
                this.gCol = 0.5f
                this.bCol = 0.7f
                this.alpha = 1.0f
                this.numStem = 12
                this.scaleX = 0.25f
                this.scaleY = 0.25f
                this.scaleZ = 0.25f
                this.stemWidth = 0.005f
                this.lifetime = 40
            }

            3 -> {
                this.rCol = 1.0f
                this.gCol = 0.5f
                this.bCol = 0.7f
                this.alpha = 1.0f
                this.numStem = 4
                this.scaleX = 1.0f
                this.scaleY = 1.0f
                this.scaleZ = 1.0f
                this.stemWidth = 0.025f
                this.lifetime = 40
            }

            4 -> {
                this.rCol = 0.0f
                this.gCol = 0.7f
                this.bCol = 1.0f
                this.alpha = 1.0f
                this.numStem = 12
                this.scaleX = 0.75f
                this.scaleY = 0.75f
                this.scaleZ = 0.75f
                this.stemWidth = 0.008f
                this.lifetime = 40
            }

            5 -> {
                this.rCol = 0.0f
                this.gCol = 0.0f
                this.bCol = 0.0f
                this.alpha = 0.0f
                this.numStem = 4
                this.scaleX = scale.toFloat()
                this.scaleY = scale.toFloat()
                this.scaleZ = scale.toFloat()
                this.stemWidth = 0.1f
                this.lifetime = 40
            }

            6 -> {
                this.rCol = 1.0f
                this.gCol = 0.5f
                this.bCol = 0.7f
                this.alpha = 1.0f
                this.numStem = 8
                this.scaleX = 1.75f
                this.scaleY = 1.75f
                this.scaleZ = 1.75f
                this.stemWidth = 0.006f
                this.lifetime = scale.toInt()
            }

            else -> {
                this.rCol = 1.0f
                this.gCol = 0.4f + this.random.nextFloat() * 0.3f
                this.bCol = 0.4f + this.random.nextFloat() * 0.3f
                this.alpha = 1.0f
                this.numStem = 4
                this.scaleX = 0.1f
                this.scaleY = 0.12f
                this.scaleZ = 0.1f
                this.stemWidth = 0.01f
                this.lifetime = 20
            }
        }

        if (this.particleType != 0) {
            this.prevShape = Array<DoubleArray>(this.numStem) { DoubleArray(6) }
            updateShape()
        }

        if (host != null) {
            if (this.particleType == 3) {
                updateYamatoPosition()
            } else if (this.particleType == 1 || this.particleType == 4 || this.particleType == 5) {
                val sc = if (this.particleType == 1) scale.toFloat() else 0.25f
                this.setPos(
                    host.getX() + (this.random.nextFloat() * sc * 2.0f) - sc,
                    host.getY() + hostHeight * 0.5 + (this.random.nextFloat() * sc * 2.0f) - sc,
                    host.getZ() + (this.random.nextFloat() * sc * 2.0f) - sc
                )
            } else if (this.particleType == 2) {
                val yaw = if (host is LivingEntity) host.yBodyRot else host.getYRot()
                val partPos = rotateXZByAxis(1.0f, 0.0f, -yaw * Mth.DEG_TO_RAD)
                this.setPos(
                    host.getX() + partPos[0],
                    host.getY() + hostHeight * 0.8,
                    host.getZ() + partPos[1]
                )
            } else if (this.particleType == 6) {
                this.setPos(
                    host.getX() + (this.random.nextFloat() * 2.0f) - 1.0f,
                    host.getY() + hostHeight * 0.5 + (this.random.nextFloat() * 2.0f) - 1.0f,
                    host.getZ() + (this.random.nextFloat() * 2.0f) - 1.0f
                )
            } else {
                updatePosition(true)
            }
        }

        this.xo = this.x
        this.yo = this.y
        this.zo = this.z
    }

    override fun tick() {
        this.xo = this.x
        this.yo = this.y
        this.zo = this.z

        if (this.age++ > this.lifetime) {
            this.remove()
            return
        }

        if (this.particleType == 3) {
            updateYamatoPosition()
        } else if (this.particleType == 0) {
            updatePosition(false)
        }

        if (this.particleType != 0 && this.age % 2 == 0) {
            updateShape()
        }

        if (this.particleType == 4) {
            if (this.lifetime - this.age < 6) {
                this.alpha = (this.lifetime - this.age) * 0.15f + 0.2f
            }
            this.gCol = 0.6f + this.random.nextFloat() * 0.6f
            this.rCol = this.gCol - 0.3f
        } else if (this.particleType == 5) {
            this.alpha = if (this.lifetime - this.age < 10) (this.lifetime - this.age) * 0.015f + 0.018f else 0.35f
            this.gCol = 0.0f + this.random.nextFloat() * 0.1f
            this.rCol = this.gCol + this.random.nextFloat() * 0.15f
            this.bCol = this.rCol + this.random.nextFloat() * 0.15f
        } else if (this.particleType != 0) {
            if (this.lifetime - this.age < 6) {
                this.alpha = (this.lifetime - this.age) * 0.15f + 0.2f
            }
            this.gCol = 0.4f + this.random.nextFloat() * 0.75f
            this.bCol = 0.1f + this.gCol
        }
    }

    private fun updateShape() {
        for (i in 0..<this.numStem) {
            val offx = (this.random.nextFloat() - 0.5f) * this.scaleX
            val offy = (this.random.nextFloat() - 0.5f) * this.scaleY
            val offz = (this.random.nextFloat() - 0.5f) * this.scaleZ
            if (i == 0) {
                this.prevShape[i]!![0] = offx.toDouble()
                this.prevShape[i]!![1] = offy.toDouble()
                this.prevShape[i]!![2] = offz.toDouble()
                this.prevShape[i]!![3] = this.prevShape[i]!![0]
                this.prevShape[i]!![4] = this.prevShape[i]!![1]
                this.prevShape[i]!![5] = this.prevShape[i]!![2]
                continue
            }
            if (i == this.numStem - 1) {
                this.prevShape[i]!![0] = this.prevShape[i - 1]!![0] + offx
                this.prevShape[i]!![1] = this.prevShape[i - 1]!![1] + offy
                this.prevShape[i]!![2] = this.prevShape[i - 1]!![2] + offz
                this.prevShape[i]!![3] = this.prevShape[i]!![0]
                this.prevShape[i]!![4] = this.prevShape[i]!![1]
                this.prevShape[i]!![5] = this.prevShape[i]!![2]
                continue
            }
            this.prevShape[i]!![0] = this.prevShape[i - 1]!![0] + offx
            this.prevShape[i]!![1] = this.prevShape[i - 1]!![1] + offy
            this.prevShape[i]!![2] = this.prevShape[i - 1]!![2] + offz
            this.prevShape[i]!![3] = this.prevShape[i - 1]!![3] + offx + this.stemWidth
            this.prevShape[i]!![4] = this.prevShape[i - 1]!![4] + offy + this.stemWidth
            this.prevShape[i]!![5] = this.prevShape[i - 1]!![5] + offz + this.stemWidth
        }
    }

    private fun updateYamatoPosition() {
        val host = this.level.getEntity(this.hostEntityId)
        if (host == null || !host.isAlive) {
            this.remove()
            return
        }
        val yaw = if (host is LivingEntity) host.yBodyRot else host.getYRot()
        val posOffset = rotateXZByAxis(host.getBbWidth() * 2.0f, 0.0f, yaw * Mth.DEG_TO_RAD, 1.0f)
        this.setPos(
            host.getX() + posOffset[1],
            host.getY() + host.getBbHeight() * 0.6,
            host.getZ() + posOffset[0]
        )
    }

    private fun updatePosition(initial: Boolean) {
        val host = this.level.getEntity(this.hostEntityId)
        if (host == null || !host.isAlive) {
            if (this.age > 2) this.remove()
            return
        }

        val randx = this.random.nextFloat() + 0.1f
        val yaw = if (host is LivingEntity) host.yBodyRot else host.getYRot()
        val newPos = rotateXZByAxis(0.8f + this.random.nextFloat() * 0.2f, randx, -yaw * Mth.DEG_TO_RAD)

        this.x = host.getX() + newPos[0]
        this.y = host.getY() + (if (initial) 1.53 else 1.76) + randx * 0.25
        this.z = host.getZ() + newPos[1]

        if (host is EntityMountBase) {
            if (host.shipDepth > 0.0) {
                this.y -= 0.08
            }
            if (host.getHost() != null && host.getHost()!!.isOrderedToSit()) {
                this.y -= 0.23
            }
        }
    }

    private fun rotateXZByAxis(z: Float, x: Float, angle: Float): FloatArray {
        val cos = Mth.cos(angle)
        val sin = Mth.sin(angle)
        return floatArrayOf(
            z * cos + x * sin,
            x * cos - z * sin
        )
    }

    override fun render(buffer: VertexConsumer, camera: Camera, partialTicks: Float) {
        val cameraPos = camera.getPosition()
        val px = (Mth.lerp(partialTicks.toDouble(), this.xo, this.x) - cameraPos.x()).toFloat()
        val py = (Mth.lerp(partialTicks.toDouble(), this.yo, this.y) - cameraPos.y()).toFloat()
        val pz = (Mth.lerp(partialTicks.toDouble(), this.zo, this.z) - cameraPos.z()).toFloat()

        val rotation = camera.rotation()
        val right = Vector3f(1.0f, 0.0f, 0.0f).rotate(rotation)
        right.y = 0f
        right.normalize()

        if (this.particleType == 0) {
            val cosPitch = Mth.cos(camera.getXRot() * Mth.DEG_TO_RAD)

            for (i in this.numStem - 1 downTo 0) {
                val offx = (this.random.nextFloat() - 0.5f) * 0.1f * (i + 1)
                val offz = (this.random.nextFloat() - 0.5f) * 0.1f * (i + 1)

                val yOffset = if (i == 0) (cosPitch * this.scaleY) else (cosPitch * this.scaleY - i * this.scaleY)
                val currentY = py + yOffset

                val v1x = px + offx + right.x() * this.stemWidth
                val v1z = pz + offz + right.z() * this.stemWidth
                val v2x = px + offx - right.x() * this.stemWidth
                val v2z = pz + offz - right.z() * this.stemWidth

                buffer.addVertex(v1x, currentY, v1z).setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                buffer.addVertex(v2x, currentY, v2z).setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            }
        } else {
            // First pass: front to back
            for (i in this.numStem - 1 downTo 0) {
                val v1x = px + this.prevShape[i]!![0].toFloat()
                val v1y = py + this.prevShape[i]!![1].toFloat()
                val v1z = pz + this.prevShape[i]!![2].toFloat()
                val v2x = px + this.prevShape[i]!![3].toFloat()
                val v2y = py + this.prevShape[i]!![4].toFloat()
                val v2z = pz + this.prevShape[i]!![5].toFloat()

                buffer.addVertex(v1x, v1y, v1z).setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                buffer.addVertex(v2x, v2y, v2z).setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            }
            // Second pass: back to front (ensures visibility from all angles in TRIANGLE_STRIP mode)
            for (i in this.numStem - 1 downTo 0) {
                val v1x = px + this.prevShape[i]!![3].toFloat()
                val v1y = py + this.prevShape[i]!![4].toFloat()
                val v1z = pz + this.prevShape[i]!![5].toFloat()
                val v2x = px + this.prevShape[i]!![0].toFloat()
                val v2y = py + this.prevShape[i]!![1].toFloat()
                val v2z = pz + this.prevShape[i]!![2].toFloat()

                buffer.addVertex(v1x, v1y, v1z).setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                buffer.addVertex(v2x, v2y, v2z).setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            }
        }
    }

    override fun getRenderType(): ParticleRenderType {
        return LIGHTNING_RENDER
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
            return ParticleLightning(
                level,
                x,
                y,
                z,
                scale,
                Math.round(hostEntityId).toInt(),
                Math.round(particleType).toInt()
            )
        }
    }

    companion object {
        private val LIGHTNING_RENDER: ParticleRenderType = object : ParticleRenderType {
            override fun begin(tesselator: Tesselator, textureManager: TextureManager): BufferBuilder {
                RenderSystem.enableBlend()
                RenderSystem.defaultBlendFunc()
                RenderSystem.disableCull()
                RenderSystem.setShader(Supplier { GameRenderer.getPositionColorShader() })
                return tesselator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR)
            }

            override fun toString(): String {
                return "SHINCOLLE_LIGHTNING"
            }
        }
    }
}