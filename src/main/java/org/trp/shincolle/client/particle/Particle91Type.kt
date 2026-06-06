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
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import org.trp.shincolle.Shincolle
import java.util.function.Supplier

class Particle91Type protected constructor(level: ClientLevel, x: Double, y: Double, z: Double, scale: Double) :
    Particle(level, x, y, z) {
    private val fadeTime = 16
    private val middTime = 60
    private val totalTime = 2 * this.fadeTime + this.middTime
    private val fadeCoef = 1.0f / this.fadeTime
    private val sizeScale: Float
    private var alphaVal = 0f

    init {
        this.setSize(0.0f, 0.0f)
        this.xo = x
        this.yo = y + this.random.nextDouble() * 4.0
        this.zo = z
        this.x = this.xo
        this.y = this.yo
        this.z = this.zo
        this.hasPhysics = false
        this.sizeScale = scale.toFloat()
        this.lifetime = 136
    }

    override fun tick() {
        this.xo = this.x
        this.yo = this.y
        this.zo = this.z
        if (this.age++ > this.lifetime) {
            this.remove()
        }
    }

    override fun render(buffer: VertexConsumer, camera: Camera, partialTicks: Float) {
        val cameraPos = camera.getPosition()
        val f11 = (Mth.lerp(partialTicks.toDouble(), this.xo, this.x) - cameraPos.x()).toFloat()
        val f12 = (Mth.lerp(partialTicks.toDouble(), this.yo, this.y) - cameraPos.y()).toFloat()
        val f13 = (Mth.lerp(partialTicks.toDouble(), this.zo, this.z) - cameraPos.z()).toFloat()

        val yawRad = camera.getYRot() * Mth.DEG_TO_RAD
        val pitchRad = camera.getXRot() * Mth.DEG_TO_RAD
        val cosYaw = Mth.cos(yawRad)
        val sinYaw = Mth.sin(yawRad)
        val cosPitch = Mth.cos(pitchRad)

        val light = LightTexture.FULL_BRIGHT

        for (i in 0..5) {
            var partAge = this.age - i * 8
            if (partAge <= -1 || partAge >= this.totalTime) {
                continue
            }

            val minu = 0.16666667f * i
            val maxu = 0.16666667f * (i + 1)
            val scale: Float
            val px = f11 - (i - 2.5f) * this.sizeScale * 2.0f * cosYaw
            val py = f12
            val pz = f13 - (i - 2.5f) * this.sizeScale * 2.0f * sinYaw

            if (partAge < this.fadeTime) {
                scale = this.sizeScale * (3.0f - 2.0f * this.fadeCoef * partAge)
                this.alphaVal = this.fadeCoef * partAge
            } else if (partAge >= this.fadeTime + this.middTime) {
                partAge -= this.fadeTime + this.middTime
                scale = this.sizeScale * (1.0f + 2.0f * this.fadeCoef * partAge)
                this.alphaVal = 1.0f - this.fadeCoef * partAge
            } else {
                scale = this.sizeScale
                this.alphaVal = 1.0f
            }

            addQuad(buffer, scale, px, py, pz, cosYaw, cosPitch, sinYaw, minu, maxu, 0.0f, 1.0f, light)
        }
    }

    private fun addQuad(
        buffer: VertexConsumer, scale: Float, x: Float, y: Float, z: Float,
        offx: Float, offy: Float, offz: Float, minu: Float, maxu: Float, minv: Float, maxv: Float, light: Int
    ) {
        val offsetX = offx * scale
        val offsetY = offy * scale
        val offsetZ = offz * scale
        buffer.addVertex(x - offsetX, y - offsetY, z - offsetZ).setColor(1.0f, 1.0f, 1.0f, this.alphaVal)
            .setUv(maxu, maxv).setLight(light)
        buffer.addVertex(x - offsetX, y + offsetY, z - offsetZ).setColor(1.0f, 1.0f, 1.0f, this.alphaVal)
            .setUv(maxu, minv).setLight(light)
        buffer.addVertex(x + offsetX, y + offsetY, z + offsetZ).setColor(1.0f, 1.0f, 1.0f, this.alphaVal)
            .setUv(minu, minv).setLight(light)
        buffer.addVertex(x + offsetX, y - offsetY, z + offsetZ).setColor(1.0f, 1.0f, 1.0f, this.alphaVal)
            .setUv(minu, maxv).setLight(light)
    }

    override fun getRenderType(): ParticleRenderType {
        return TEXTURED_RENDER
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
            return Particle91Type(level, x, y, z, scale)
        }
    }

    companion object {
        private val TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(Shincolle.MODID, "textures/particle/particle91type.png")

        private val TEXTURED_RENDER: ParticleRenderType = object : ParticleRenderType {
            override fun begin(tesselator: Tesselator, textureManager: TextureManager): BufferBuilder {
                RenderSystem.setShader(Supplier { GameRenderer.getPositionColorTexLightmapShader() })
                RenderSystem.setShaderTexture(0, TEXTURE)
                RenderSystem.enableBlend()
                RenderSystem.defaultBlendFunc()
                RenderSystem.disableCull()
                return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)
            }

            override fun toString(): String {
                return "SHINCOLLE_91TYPE"
            }
        }
    }
}
