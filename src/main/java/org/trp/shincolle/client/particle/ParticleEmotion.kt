package org.trp.shincolle.client.particle

import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.particle.TextureSheetParticle
import net.minecraft.client.renderer.LightTexture
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player
import org.joml.Vector3f
import org.trp.shincolle.entity.base.EmotionParticleType.Companion.fromId
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class ParticleEmotion protected constructor(
    level: ClientLevel, x: Double, y: Double, z: Double, height: Float, hostEntityId: Int,
    emotionId: Int, sprites: SpriteSet
) : TextureSheetParticle(level, x, y, z) {
    private val sprites: SpriteSet?
    private val emotionId: Int
    private val hostEntityId: Int
    private val addHeight: Float
    private val entType: Float

    private var playTimes = 0
    private var fadeTick: Int
    private var fadeState: Int
    private var stayTick: Int
    private var stayTickCount: Int
    private var frameSize: Int
    private var frameIndex: Int
    private var frameMax: Int
    private var playSpeed: Float
    private var playSpeedCount: Float
    private var iconU = 0f
    private var iconV = 0f
    private var addX = 0.0
    private var addY = 0.0
    private var addZ = 0.0

    init {
        this.sprites = sprites
        this.emotionId = emotionId
        this.hostEntityId = hostEntityId
        this.addHeight = height
        this.entType = 1.0f
        this.playSpeed = 1.0f
        this.playSpeedCount = 0.0f
        this.stayTick = 10
        this.stayTickCount = 0
        this.fadeTick = 0
        this.fadeState = 0
        this.frameSize = 1
        this.frameIndex = -1
        this.frameMax = 15
        this.quadSize = this.random.nextFloat() * BASE_SCALE_RANGE + BASE_SCALE_MIN
        this.alpha = 0.0f
        this.hasPhysics = false
        this.lifetime = LIFETIME_FALLBACK
        configureForType(emotionId)
        applyIconCoordinates(emotionId)
        this.setSpriteFromAge(sprites)
        calcParticlePosition()
    }

    override fun tick() {
        this.xo = this.x
        this.yo = this.y
        this.zo = this.z
        updateHostPosition()

        when (this.fadeState) {
            0 -> {
                this.fadeTick++
                this.alpha = this.fadeTick * (1.0f / FADE_TICK_MAX)
                if (this.fadeTick > FADE_TICK_MAX) {
                    this.fadeState = 1
                }
            }

            1 -> {
                this.playSpeedCount += this.playSpeed
                this.frameIndex = this.frameSize * this.playSpeedCount.toInt()
                this.alpha = 1.0f
            }

            2 -> {
                this.fadeTick--
                this.alpha = this.fadeTick * (1.0f / FADE_TICK_MAX)
                if (this.fadeTick < 1) {
                    this.remove()
                    return
                }
            }

            else -> {
                this.remove()
                return
            }
        }

        val clampedFrame = min(this.frameIndex, this.frameMax)
        if (clampedFrame >= this.frameMax) {
            this.frameIndex = this.frameMax
            if (this.stayTickCount > this.stayTick) {
                this.frameIndex = this.frameMax + 1
                this.stayTickCount = 0
            } else {
                this.stayTickCount++
            }
        }

        if (this.frameIndex > this.frameMax) {
            if (--this.playTimes <= 0) {
                this.fadeState = 2
            } else {
                this.frameIndex = 0
                this.playSpeedCount = 0.0f
            }
        }
    }

    override fun render(buffer: VertexConsumer, camera: Camera, partialTicks: Float) {
        if (this.frameIndex < 0) {
            return
        }
        val sprite = this.sprite
        val u0 = Mth.lerp(this.iconU, sprite.getU0(), sprite.getU1())
        val u1 = Mth.lerp(this.iconU + ICON_SIZE, sprite.getU0(), sprite.getU1())
        val frameV: Float = this.iconV + (this.frameIndex * ICON_SIZE)
        val v0 = Mth.lerp(frameV, sprite.getV0(), sprite.getV1())
        val v1 = Mth.lerp(frameV + (ICON_SIZE * this.frameSize), sprite.getV0(), sprite.getV1())

        val cameraPos = camera.getPosition()
        val px = (Mth.lerp(partialTicks.toDouble(), this.xo, this.x) - cameraPos.x()).toFloat()
        val py = (Mth.lerp(partialTicks.toDouble(), this.yo, this.y) - cameraPos.y()).toFloat()
        val pz = (Mth.lerp(partialTicks.toDouble(), this.zo, this.z) - cameraPos.z()).toFloat()

        val quadSize = this.getQuadSize(partialTicks)
        val yStretch = this.frameSize.toFloat()
        val light = this.getLightColor(partialTicks)

        val rotation = camera.rotation()
        val corners: Array<Vector3f> = arrayOf<Vector3f>(
            Vector3f(-1.0f, -yStretch, 0.0f),
            Vector3f(-1.0f, yStretch, 0.0f),
            Vector3f(1.0f, yStretch, 0.0f),
            Vector3f(1.0f, -yStretch, 0.0f)
        )

        for (corner in corners) {
            corner!!.rotate(rotation)
            corner.mul(quadSize)
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

    private fun updateHostPosition() {
        if (this.hostEntityId < 0) {
            return
        }
        val host = this.level.getEntity(this.hostEntityId)
        if (host == null || !host.isAlive) {
            this.remove()
            return
        }
        this.x = host.getX() + this.addX
        this.y = host.getY() + this.addY
        this.z = host.getZ() + this.addZ
    }

    private fun configureForType(type: Int) {
        when (type) {
            1 -> {
                this.frameMax = 7
                this.playTimes = 4
                this.stayTick = 0
            }

            2 -> {
                this.frameMax = 7
                this.playTimes = 3
                this.alpha = 1.0f
                this.fadeState = 1
                this.fadeTick = 5
                this.stayTick = 0
            }

            3 -> {
                this.frameMax = 7
                this.playTimes = 1
                this.fadeTick = 3
            }

            4 -> {
                this.frameMax = 7
                this.playTimes = 1
                this.fadeTick = 3
                this.stayTick = 20
            }

            5 -> {
                this.frameMax = 7
                this.playTimes = 1
                this.stayTick = 20
                this.playSpeed = 0.5f
            }

            6 -> {
                this.frameMax = 7
                this.playTimes = 1
                this.fadeTick = 3
            }

            7 -> {
                this.frameMax = 15
                this.playTimes = 1
                this.alpha = 1.0f
                this.fadeState = 1
                this.fadeTick = 3
                this.stayTick = 3
                this.playSpeed = 0.7f
            }

            8 -> {
                this.frameMax = 7
                this.playTimes = 3
                this.fadeTick = 3
                this.stayTick = 0
                this.playSpeed = 0.5f
            }

            9 -> {
                this.frameMax = 7
                this.playTimes = 2
                this.fadeTick = 3
                this.stayTick = 1
                this.playSpeed = 0.5f
            }

            10 -> {
                this.frameMax = 7
                this.playTimes = 4
                this.alpha = 1.0f
                this.fadeState = 1
                this.fadeTick = 3
                this.stayTick = 1
            }

            11 -> {
                this.frameMax = 7
                this.playTimes = 2
                this.alpha = 1.0f
                this.fadeState = 1
                this.fadeTick = 3
                this.stayTick = 0
                this.playSpeed = 0.75f
            }

            12 -> {
                this.frameMax = 14
                this.playTimes = 1
                this.alpha = 1.0f
                this.fadeState = 1
                this.fadeTick = 3
                this.stayTick = 20
                this.playSpeed = 0.75f
                this.frameSize = 2
            }

            13 -> {
                this.frameMax = 7
                this.playTimes = 2
                this.alpha = 1.0f
                this.fadeState = 1
                this.fadeTick = 3
                this.stayTick = 0
                this.playSpeed = 0.75f
            }

            14 -> {
                this.frameMax = 7
                this.playTimes = 2
                this.fadeTick = 3
                this.stayTick = 0
            }

            15 -> {
                this.frameMax = 7
                this.playTimes = 1
                this.fadeTick = 3
                this.stayTick = 15
                this.playSpeed = 0.7f
            }

            16 -> {
                this.frameMax = 7
                this.playTimes = 3
                this.alpha = 1.0f
                this.fadeState = 1
                this.fadeTick = 3
                this.stayTick = 0
            }

            17 -> {
                this.frameMax = 15
                this.playTimes = 1
                this.fadeTick = 3
                this.playSpeed = 0.5f
            }

            18 -> {
                this.frameMax = 7
                this.playTimes = 1
                this.stayTick = 0
                this.playSpeed = 0.4f
            }

            19 -> {
                this.frameMax = 7
                this.playTimes = 3
                this.alpha = 1.0f
                this.fadeState = 1
                this.fadeTick = 3
                this.stayTick = 0
                this.playSpeed = 0.75f
            }

            20 -> {
                this.frameMax = 7
                this.playTimes = 1
                this.fadeTick = 3
                this.stayTick = 20
                this.playSpeed = 0.5f
            }

            21, 22, 23, 24, 25, 26, 27, 28 -> {
                this.frameMax = 0
                this.playTimes = 1
                this.stayTick = 40
            }

            29 -> {
                this.frameMax = 7
                this.playTimes = 1
                this.fadeTick = 3
                this.playSpeed = 0.35f
                this.stayTick = 20
            }

            30 -> {
                this.frameMax = 7
                this.playTimes = 1
                this.fadeTick = 3
                this.playSpeed = 0.75f
                this.stayTick = 3
            }

            31 -> {
                this.frameMax = 3
                this.quadSize += 0.2f
                this.playTimes = 1
                this.fadeTick = 3
                this.playSpeed = 0.75f
                this.stayTick = 30
            }

            32 -> {
                this.frameMax = 5
                this.playTimes = 4
                this.playSpeed = 0.75f
                this.stayTick = 0
            }

            33 -> {
                this.frameMax = 4
                this.playTimes = 1
                this.playSpeed = 0.25f
                this.stayTick = 30
            }

            34 -> {
                this.frameMax = 0
                this.quadSize += 0.3f
                this.playTimes = 1
                this.stayTick = 50
            }

            else -> {
                this.frameMax = 15
                this.playTimes = 1
            }
        }
    }

    private fun applyIconCoordinates(type: Int) {
        val resolved = fromId(type)
        this.iconU = resolved!!.iconColumn * ICON_SIZE
        this.iconV = resolved.iconRow * ICON_SIZE
    }

    private fun calcParticlePosition() {
        val player: Player? = Minecraft.getInstance().player
        var angle = 0.0f
        if (player != null) {
            angle = player.getYRot() * Math.PI.toFloat() / 180.0f
        }

        var baseX = 0.0
        var baseY = 0.0
        var baseZ = 0.0

        if (this.entType == 1.0f) {
            var frontDist = 0.7f
            var leftDist = -0.2f
            when (this.emotionId) {
                12 -> {
                    leftDist = 0.0f
                    baseY += 0.6
                }

                15 -> {
                    frontDist = 1.5f
                    leftDist = -0.7f
                }

                19 -> {
                    frontDist = 1.4f
                    leftDist = -1.1f
                }

                34 -> {
                    frontDist = -0.2f
                    leftDist = 0.0f
                    baseY -= 0.2
                }

                else -> {}
            }
            val rotated = rotateXZ(frontDist, leftDist, angle)
            baseX += rotated[1]
            baseY -= 0.2
            baseZ += rotated[0]
        } else {
            val rotated = rotateXZ(0.0f, -0.2f, angle)
            baseX += rotated[1]
            baseY += 0.5
            baseZ += rotated[0]
        }

        var addX2 = 0.0
        var addY2 = 0.0
        var addZ2 = 0.0
        if (this.addHeight > 2.0f) {
            this.quadSize += 1.0f
            addX2 = 1.2
            addY2 = 1.5
            addZ2 = 0.5
        }

        val rotated: DoubleArray?
        when (this.emotionId) {
            2 -> {
                rotated = rotateXZ(
                    -0.2f - addZ2.toFloat(), this.random.nextFloat() * 0.3f - 1.0f - addX2.toFloat(),
                    angle
                )
                baseX += rotated[1]
                baseY = baseY + this.random.nextDouble() * this.addHeight * 0.2 + this.addHeight * 1.8 + addY2
                baseZ += rotated[0]
            }

            15 -> {
                rotated = rotateXZ(
                    this.random.nextFloat() * 0.1f - 0.7f - addX2.toFloat(),
                    this.random.nextFloat() * 0.1f + 0.2f + addZ2.toFloat(), angle
                )
                baseX += rotated[1]
                baseY = baseY + this.random.nextDouble() * this.addHeight * 0.2 + this.addHeight * 1.6 + addY2
                baseZ += rotated[0]
            }

            34 -> {
                rotated = rotateXZ(0.15f, 0.0f, angle)
                baseX += rotated[1]
                baseY = baseY + this.random.nextDouble() * this.addHeight * 0.15 + this.addHeight * 1.9 + addY2
                baseZ += rotated[0]
            }

            else -> {
                rotated = rotateXZ(
                    0.5f - addZ2.toFloat(), this.random.nextFloat() * 0.3f + 0.7f + addX2.toFloat(),
                    angle
                )
                baseX += rotated[1]
                baseY = baseY + this.random.nextDouble() * this.addHeight * 0.5 + this.addHeight * 1.5 + addY2
                baseZ += rotated[0]
            }
        }

        this.addX = baseX
        this.addY = baseY
        this.addZ = baseZ
        this.x += this.addX
        this.y += this.addY
        this.z += this.addZ
    }

    private fun rotateXZ(x: Float, z: Float, angle: Float): DoubleArray {
        val cos = cos(angle.toDouble())
        val sin = sin(angle.toDouble())
        val rx = x * cos - z * sin
        val rz = x * sin + z * cos
        return doubleArrayOf(rx, rz)
    }

    class Provider(private val sprites: SpriteSet) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            type: SimpleParticleType, level: ClientLevel,
            x: Double, y: Double, z: Double, xSpeed: Double, ySpeed: Double,
            zSpeed: Double
        ): ParticleEmotion? {
            val height = xSpeed.toFloat()
            val hostEntityId = if (ySpeed >= 0.0) Math.round(ySpeed).toInt() else -1
            val emotionId = Math.round(zSpeed).toInt()
            return ParticleEmotion(level, x, y, z, height, hostEntityId, emotionId, this.sprites)
        }
    }

    companion object {
        private const val BASE_SCALE_MIN = 0.275f
        private const val BASE_SCALE_RANGE = 0.05f
        private val ICON_SIZE = 1.0f / 16.0f
        private const val FADE_TICK_MAX = 5
        private const val LIFETIME_FALLBACK = 2000
    }
}
