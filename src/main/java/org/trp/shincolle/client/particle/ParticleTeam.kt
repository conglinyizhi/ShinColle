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
import java.util.*

class ParticleTeam protected constructor(
    level: ClientLevel, x: Double, y: Double, z: Double, markerHeight: Double, private val sprites: SpriteSet,
    renderStyle: RenderStyle?, private val followEntityId: Int, followKind: FollowKind?
) : TextureSheetParticle(level, x, y, z) {
    private var markerHeight: Float
    val renderStyle: RenderStyle
    val followKind: FollowKind

    init {
        this.followKind = if (followKind == null) FollowKind.NONE else followKind
        this.lifetime = if (this.followEntityId >= 0) FOLLOW_LIFETIME else DEFAULT_LIFETIME
        this.quadSize = DEFAULT_SCALE
        this.alpha = 1.0f
        this.hasPhysics = false
        this.markerHeight = if (markerHeight > 0.0) markerHeight.toFloat() else DEFAULT_HEIGHT
        this.renderStyle = if (renderStyle == null) RenderStyle.DEFAULT_GREEN else renderStyle
        this.rCol = this.renderStyle.colorR
        this.gCol = this.renderStyle.colorG
        this.bCol = this.renderStyle.colorB
        this.setSpriteFromAge(sprites)
        registerFollowParticle()
    }

    override fun tick() {
        super.tick()
        if (this.removed) {
            return
        }
        if (this.followEntityId >= 0) {
            val followEntity = this.level.getEntity(this.followEntityId)
            if (followEntity == null || !followEntity.isAlive) {
                this.remove()
                return
            }
            val pos = followEntity.position()
            this.x = Mth.lerp(FOLLOW_SMOOTH_FACTOR.toDouble(), this.x, pos.x)
            this.y = Mth.lerp(FOLLOW_SMOOTH_FACTOR.toDouble(), this.y, pos.y)
            this.z = Mth.lerp(FOLLOW_SMOOTH_FACTOR.toDouble(), this.z, pos.z)
            this.markerHeight = if (followEntity.getBbHeight() > 0.0f) followEntity.getBbHeight() else DEFAULT_HEIGHT
        }
        this.setSpriteFromAge(this.sprites)
    }

    override fun render(buffer: VertexConsumer, camera: Camera, partialTicks: Float) {
        val sprite = this.sprite
        val u0 = sprite.getU0()
        val u1 = sprite.getU1()
        val v0 = sprite.getV0()
        val v1 = sprite.getV1()
        val vMid = Mth.lerp(0.5f, v0, v1)

        val cameraPos = camera.getPosition()
        val x = (Mth.lerp(partialTicks.toDouble(), this.xo, this.x) - cameraPos.x()).toFloat()
        val y = (Mth.lerp(partialTicks.toDouble(), this.yo, this.y) - cameraPos.y()).toFloat()
        val z = (Mth.lerp(partialTicks.toDouble(), this.zo, this.z) - cameraPos.z()).toFloat()

        val quadSize = this.getQuadSize(partialTicks)
        val topY = y + this.markerHeight + this.renderStyle.topOffset
        val baseY = y + this.renderStyle.baseOffset
        val light = this.getLightColor(partialTicks)

        if (this.renderStyle.topAlpha > 0.0f) {
            renderBillboard(
                buffer, camera, x, topY, z, quadSize, u0, u1, v0, vMid, light,
                this.renderStyle.topAlpha
            )
        }
        if (this.renderStyle.baseAlpha > 0.0f) {
            renderBase(
                buffer, x, baseY, z, quadSize * this.renderStyle.baseScaleMultiplier, u0, u1, vMid, v1, light,
                this.renderStyle.baseAlpha
            )
        }
    }

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    }

    override fun getLightColor(partialTicks: Float): Int {
        return LightTexture.FULL_BRIGHT
    }

    private fun renderBillboard(
        buffer: VertexConsumer, camera: Camera, x: Float, y: Float, z: Float, scale: Float,
        u0: Float, u1: Float, v0: Float, v1: Float, light: Int, alpha: Float
    ) {
        val rotation = camera.rotation()
        val corners: Array<Vector3f> = arrayOf<Vector3f>(
            Vector3f(-1.0f, -1.0f, 0.0f),
            Vector3f(-1.0f, 1.0f, 0.0f),
            Vector3f(1.0f, 1.0f, 0.0f),
            Vector3f(1.0f, -1.0f, 0.0f)
        )

        for (corner in corners) {
            corner!!.rotate(rotation)
            corner.mul(scale)
            corner.add(x, y, z)
        }

        emitVertex(buffer, corners[0]!!, u1, v1, light, alpha)
        emitVertex(buffer, corners[1]!!, u1, v0, light, alpha)
        emitVertex(buffer, corners[2]!!, u0, v0, light, alpha)
        emitVertex(buffer, corners[3]!!, u0, v1, light, alpha)

        emitVertex(buffer, corners[3]!!, u1, v1, light, alpha)
        emitVertex(buffer, corners[2]!!, u1, v0, light, alpha)
        emitVertex(buffer, corners[1]!!, u0, v0, light, alpha)
        emitVertex(buffer, corners[0]!!, u0, v1, light, alpha)
    }

    private fun renderBase(
        buffer: VertexConsumer, x: Float, y: Float, z: Float, halfScale: Float,
        u0: Float, u1: Float, v0: Float, v1: Float, light: Int, alpha: Float
    ) {
        emitVertex(buffer, x + halfScale, y, z + halfScale, u1, v1, light, alpha)
        emitVertex(buffer, x + halfScale, y, z - halfScale, u1, v0, light, alpha)
        emitVertex(buffer, x - halfScale, y, z - halfScale, u0, v0, light, alpha)
        emitVertex(buffer, x - halfScale, y, z + halfScale, u0, v1, light, alpha)

        emitVertex(buffer, x + halfScale, y, z - halfScale, u1, v1, light, alpha)
        emitVertex(buffer, x + halfScale, y, z + halfScale, u1, v0, light, alpha)
        emitVertex(buffer, x - halfScale, y, z + halfScale, u0, v0, light, alpha)
        emitVertex(buffer, x - halfScale, y, z - halfScale, u0, v1, light, alpha)
    }

    private fun emitVertex(buffer: VertexConsumer, pos: Vector3f, u: Float, v: Float, light: Int, alpha: Float) {
        emitVertex(buffer, pos.x(), pos.y(), pos.z(), u, v, light, alpha)
    }

    private fun emitVertex(
        buffer: VertexConsumer,
        x: Float,
        y: Float,
        z: Float,
        u: Float,
        v: Float,
        light: Int,
        alpha: Float
    ) {
        buffer.addVertex(x, y, z)
            .setColor(this.rCol, this.gCol, this.bCol, alpha)
            .setUv(u, v)
            .setLight(light)
    }

    class Provider : ParticleProvider<SimpleParticleType> {
        private val sprites: SpriteSet
        private val renderStyle: RenderStyle

        constructor(sprites: SpriteSet) {
            this.sprites = sprites
            this.renderStyle = RenderStyle.DEFAULT_GREEN
        }

        constructor(sprites: SpriteSet, renderStyle: RenderStyle?) {
            this.sprites = sprites
            this.renderStyle = if (renderStyle == null) RenderStyle.DEFAULT_GREEN else renderStyle
        }

        override fun createParticle(
            type: SimpleParticleType, level: ClientLevel, x: Double, y: Double, z: Double,
            xSpeed: Double, ySpeed: Double, zSpeed: Double
        ): ParticleTeam? {
            val followEntityId = if (ySpeed >= 0.0) Math.round(ySpeed).toInt() else -1
            val kindId = Math.round(zSpeed).toInt()
            val followKind = FollowKind.fromMarkerId(kindId)
            return ParticleTeam(
                level, x, y, z, xSpeed, this.sprites, this.renderStyle, followEntityId,
                followKind
            )
        }
    }

    val isAliveParticle: Boolean
        get() = !this.removed

    private fun registerFollowParticle() {
        if (this.followEntityId < 0 || this.followKind == FollowKind.NONE) {
            return
        }
        val particles: MutableMap<Int?, ParticleTeam?> = FOLLOW_PARTICLES.get(this.followKind)!!
        val existing = particles.put(this.followEntityId, this)
        if (existing != null && existing !== this) {
            existing.remove()
        }
    }

    override fun remove() {
        super.remove()
        if (this.followEntityId < 0 || this.followKind == FollowKind.NONE) {
            return
        }
        val particles: MutableMap<Int?, ParticleTeam?> = FOLLOW_PARTICLES.get(this.followKind)!!
        val existing = particles.get(this.followEntityId)
        if (existing === this) {
            particles.remove(this.followEntityId)
        }
    }

    enum class FollowKind(val markerId: Int) {
        NONE(0),
        SHIP_MARKER(1),
        TARGET_ENTITY(2);

        companion object {
            fun fromMarkerId(markerId: Int): FollowKind {
                for (kind in FollowKind.entries) {
                    if (kind.markerId == markerId) {
                        return kind
                    }
                }
                return FollowKind.NONE
            }
        }
    }

    class RenderStyle(
        colorR: Float, colorG: Float, colorB: Float,
        val topOffset: Float, val baseOffset: Float,
        topAlpha: Float, baseAlpha: Float,
        val baseScaleMultiplier: Float
    ) {
        val colorR: Float
        val colorG: Float
        val colorB: Float
        val topAlpha: Float
        val baseAlpha: Float

        init {
            this.colorR = Mth.clamp(colorR, 0.0f, 1.0f)
            this.colorG = Mth.clamp(colorG, 0.0f, 1.0f)
            this.colorB = Mth.clamp(colorB, 0.0f, 1.0f)
            this.topAlpha = Mth.clamp(topAlpha, 0.0f, 1.0f)
            this.baseAlpha = Mth.clamp(baseAlpha, 0.0f, 1.0f)
        }

        companion object {
            val DEFAULT_GREEN: RenderStyle = RenderStyle(
                0.0f, 1.0f, 0.0f,
                TOP_OFFSET, BASE_OFFSET,
                0.95f, 0.45f,
                BASE_SCALE_MULTIPLIER
            )

            val DEFAULT_BLUE: RenderStyle = RenderStyle(
                0.2f, 0.6f, 1.0f,
                TOP_OFFSET, BASE_OFFSET,
                0.95f, 0.45f,
                BASE_SCALE_MULTIPLIER
            )

            val SELECTED_RED: RenderStyle = RenderStyle(
                1.0f, 0.2f, 0.2f,
                TOP_OFFSET, BASE_OFFSET,
                0.95f, 0.45f,
                BASE_SCALE_MULTIPLIER
            )

            val SELECTED_YELLOW: RenderStyle = RenderStyle(
                1.0f, 1.0f, 0.2f,
                TOP_OFFSET, BASE_OFFSET,
                0.95f, 0.45f,
                BASE_SCALE_MULTIPLIER
            )

            val TARGET_WHITE: RenderStyle = RenderStyle(
                1.0f, 1.0f, 1.0f,
                TOP_OFFSET, BASE_OFFSET,
                0.95f, 0.45f,
                BASE_SCALE_MULTIPLIER
            )

            val TARGET_RED: RenderStyle = RenderStyle(
                1.0f, 0.2f, 0.2f,
                TOP_OFFSET, BASE_OFFSET,
                0.95f, 0.45f,
                BASE_SCALE_MULTIPLIER
            )
        }
    }

    companion object {
        private const val DEFAULT_LIFETIME = 30
        private val FOLLOW_LIFETIME = 20 * 60 * 60
        private const val FOLLOW_SMOOTH_FACTOR = 0.35f
        private const val DEFAULT_SCALE = 0.35f
        private const val BASE_SCALE_MULTIPLIER = 3.0f
        private const val DEFAULT_HEIGHT = 1.5f
        private const val TOP_OFFSET = 1.6f
        private const val BASE_OFFSET = 0.26f

        private val FOLLOW_PARTICLES = EnumMap<FollowKind?, MutableMap<Int?, ParticleTeam?>>(
            FollowKind::class.java
        )

        init {
            for (kind in FollowKind.entries) {
                FOLLOW_PARTICLES.put(kind, HashMap<Int?, ParticleTeam?>())
            }
        }

        fun getFollowParticle(kind: FollowKind?, entityId: Int): ParticleTeam? {
            if (kind == null || kind == FollowKind.NONE) {
                return null
            }
            return FOLLOW_PARTICLES.get(kind)!!.get(entityId)
        }

        fun removeFollowParticle(kind: FollowKind?, entityId: Int) {
            if (kind == null || kind == FollowKind.NONE) {
                return
            }
            val particle: ParticleTeam? = FOLLOW_PARTICLES.get(kind)!!.remove(entityId)
            if (particle != null) {
                particle.remove()
            }
        }

        fun clearFollowParticles(kind: FollowKind?, keepEntityIds: MutableSet<Int?>?) {
            if (kind == null || kind == FollowKind.NONE) {
                return
            }
            val particles: MutableMap<Int?, ParticleTeam?> = FOLLOW_PARTICLES.get(kind)!!
            if (keepEntityIds == null || keepEntityIds.isEmpty()) {
                val snapshot: MutableList<ParticleTeam> = ArrayList<ParticleTeam>(particles.values)
                particles.clear()
                for (particle in snapshot) {
                    particle.remove()
                }
                return
            }
            val snapshot: MutableList<MutableMap.MutableEntry<Int?, ParticleTeam?>> =
                ArrayList<MutableMap.MutableEntry<Int?, ParticleTeam?>>(particles.entries)
            for (entry in snapshot) {
                if (keepEntityIds.contains(entry.key)) {
                    continue
                }
                particles.remove(entry.key)
                entry.value!!.remove()
            }
        }

        fun clearAllFollowParticles() {
            for (kind in FollowKind.entries) {
                if (kind == FollowKind.NONE) {
                    continue
                }
                clearFollowParticles(kind, null)
            }
        }
    }
}
