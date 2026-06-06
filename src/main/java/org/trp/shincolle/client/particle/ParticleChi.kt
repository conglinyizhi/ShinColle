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
import org.trp.shincolle.entity.base.EntityShipBase
import java.util.function.Supplier

class ParticleChi protected constructor(level: ClientLevel, scale: Double, hostEntityId: Int, particleType: Int) :
    Particle(level, 0.0, 0.0, 0.0) {
    private val hostEntityId: Int
    private val particleType: Int
    private val sizeScale: Float
    private var radChi = 0f

    init {
        this.setSize(0.0f, 0.0f)
        this.hostEntityId = hostEntityId
        this.particleType = particleType
        this.hasPhysics = false
        this.xd = 0.0
        this.yd = 0.0
        this.zd = 0.0
        this.sizeScale = scale.toFloat()

        val host = this.level.getEntity(this.hostEntityId)
        if (host != null) {
            this.setPos(host.getX(), host.getY() + host.getBbHeight() * 0.55, host.getZ())
        }
        this.xo = this.x
        this.yo = this.y
        this.zo = this.z

        if (particleType == 1) {
            this.rCol = 1.0f
            this.gCol = 1.0f
            this.bCol = 1.0f
            this.alpha = 1.0f
            this.lifetime = 40
            this.radChi = scale.toFloat() * 12.0f
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

        if (host is EntityShipBase) {
            val phase = host.getStateEmotion(5)
            if (phase == 0 || phase == 2) {
                this.remove()
                return
            }
        }

        val angle = (2.0f * Math.PI.toFloat() / this.lifetime) * this.age
        val cos = Mth.cos(angle)
        val sin = Mth.sin(angle)
        val offsetX = this.radChi * cos
        val offsetZ = this.radChi * sin

        this.setPos(host.getX() + offsetX, host.getY() + host.getBbHeight() * 0.55, host.getZ() + offsetZ)
    }

    override fun render(buffer: VertexConsumer, camera: Camera, partialTicks: Float) {
        if (this.age <= 1) {
            return
        }

        val cameraPos = camera.getPosition()
        val px = (Mth.lerp(partialTicks.toDouble(), this.xo, this.x) - cameraPos.x()).toFloat()
        val py = (Mth.lerp(partialTicks.toDouble(), this.yo, this.y) - cameraPos.y()).toFloat()
        val pz = (Mth.lerp(partialTicks.toDouble(), this.zo, this.z) - cameraPos.z()).toFloat()

        val size = this.sizeScale

        drawCross(buffer, px, py, pz, size, this.rCol, this.gCol, this.bCol, this.alpha)

        val alpha2 = this.alpha * 0.5f
        drawCross(buffer, px, py, pz, size * 1.3f, this.rCol, this.gCol, this.bCol, alpha2)
    }

    private fun drawCross(
        buffer: VertexConsumer,
        x: Float,
        y: Float,
        z: Float,
        s: Float,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ) {
        buffer.addVertex(x, y, z + s).setColor(r, g, b, a)
        buffer.addVertex(x, y + s, z).setColor(r, g, b, a)
        buffer.addVertex(x + s, y, z).setColor(r, g, b, a)
        buffer.addVertex(x, y - s, z).setColor(r, g, b, a)

        buffer.addVertex(x + s, y, z).setColor(r, g, b, a)
        buffer.addVertex(x, y + s, z).setColor(r, g, b, a)
        buffer.addVertex(x, y, z - s).setColor(r, g, b, a)
        buffer.addVertex(x, y - s, z).setColor(r, g, b, a)

        buffer.addVertex(x, y, z - s).setColor(r, g, b, a)
        buffer.addVertex(x, y + s, z).setColor(r, g, b, a)
        buffer.addVertex(x - s, y, z).setColor(r, g, b, a)
        buffer.addVertex(x, y - s, z).setColor(r, g, b, a)

        buffer.addVertex(x - s, y, z).setColor(r, g, b, a)
        buffer.addVertex(x, y + s, z).setColor(r, g, b, a)
        buffer.addVertex(x, y, z + s).setColor(r, g, b, a)
        buffer.addVertex(x, y - s, z).setColor(r, g, b, a)
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
            return ParticleChi(level, scale, Math.round(hostEntityId).toInt(), Math.round(particleType).toInt())
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
                return "SHINCOLLE_CHI"
            }
        }
    }
}
