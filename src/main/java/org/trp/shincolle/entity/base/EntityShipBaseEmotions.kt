package org.trp.shincolle.entity.base

import net.minecraft.util.Mth

internal class EntityShipBaseEmotions(private val ship: EntityShipBase) {
    private var faceTick = -1
    private var lastEmotionUpdateTick = 0
    private var headTiltTick = 0
    private var headTiltActive = false
    private var headTiltState = 0

    fun tickEmotions() {
        if ((this.ship.tickCount - this.lastEmotionUpdateTick) >= EMOTION_UPDATE_INTERVAL) {
            this.lastEmotionUpdateTick = this.ship.tickCount
            updateEmotionState()
        }
        applyEmotionState()
    }

    fun getHeadTiltAngle(ageInTicks: Float): Float {
        val cooldown = this.ship.tickCount - this.headTiltTick
        val maxAngle = Companion.headTiltMaxAngle
        var partTick = ageInTicks - ageInTicks.toInt() + cooldown

        if (cooldown > Companion.headTiltResetBaseTicks + this.ship.random.nextInt(Companion.headTiltResetRandomTicks)) {
            this.headTiltTick = this.ship.tickCount
            partTick = ageInTicks - ageInTicks.toInt()
            this.headTiltActive = this.ship.random.nextInt(10) > 4
        }

        if (this.headTiltActive) {
            if (this.headTiltState > 0) {
                return maxAngle
            }
            var f = Mth.sin(partTick * Companion.headTiltInSpeed * HEAD_TILT_HALF_PI) * maxAngle
            if (f - Companion.headTiltAngleEps < maxAngle || partTick > Companion.headTiltInMaxTicks) {
                this.headTiltState = 1
                f = maxAngle
            }
            return f
        }

        if (this.headTiltState <= 0) {
            return 0.0f
        }
        var f = (1.0f - Mth.sin(partTick * Companion.headTiltOutSpeed * HEAD_TILT_HALF_PI)) * maxAngle
        if (f + Companion.headTiltAngleEps > 0.0f || partTick > Companion.headTiltOutMaxTicks) {
            this.headTiltState = 0
            f = 0.0f
        }
        return f
    }

    fun resetFaceTick() {
        this.faceTick = -1
    }

    fun ensureFaceTick() {
        if (this.faceTick <= 0) {
            this.faceTick = this.ship.tickCount
        }
    }

    val faceElapsed: Int
        get() {
            ensureFaceTick()
            return this.ship.tickCount - this.faceTick
        }

    private fun updateEmotionState() {
        if (this.ship.isNoFuel) {
            this.ship.emotionPrimary = EntityShipBase.Companion.EMOTION_HUNGRY
            this.faceTick = -1
            return
        }

        val healthRatio = this.ship.health / this.ship.maxHealth
        if (healthRatio <= 0.25f) {
            this.ship.emotionPrimary = EntityShipBase.Companion.EMOTION_CRY
            this.faceTick = -1
            return
        }
        if (healthRatio <= 0.5f) {
            this.ship.emotionPrimary = EntityShipBase.Companion.EMOTION_ANGRY
            this.faceTick = -1
            return
        }
        if (healthRatio <= 0.75f) {
            this.ship.emotionPrimary = EntityShipBase.Companion.EMOTION_SCORN
            this.faceTick = -1
            return
        }

        val current = this.ship.emotionPrimary
        if (current == EntityShipBase.Companion.EMOTION_HUNGRY) {
            this.faceTick = -1
        } else if (current != EntityShipBase.Companion.EMOTION_NORMAL && current != EntityShipBase.Companion.EMOTION_BORED) {
            return
        }

        if (this.ship.emotionPrimary == EntityShipBase.Companion.EMOTION_NORMAL) {
            if (this.ship.random.nextInt(3) == 0) {
                this.ship.emotionPrimary = EntityShipBase.Companion.EMOTION_BORED
            }
        } else if (this.ship.random.nextInt(4) == 0) {
            this.ship.emotionPrimary = EntityShipBase.Companion.EMOTION_NORMAL
        }

        if (this.ship.emotionSecondary == EntityShipBase.Companion.EMOTION_NORMAL) {
            if (this.ship.random.nextInt(3) == 0) {
                this.ship.emotionSecondary = EntityShipBase.Companion.EMOTION_BORED
            }
        } else if (this.ship.random.nextInt(3) == 0) {
            this.ship.emotionSecondary = EntityShipBase.Companion.EMOTION_NORMAL
        }
    }

    private fun applyEmotionState() {
        val emotion = this.ship.emotionPrimary
        when (emotion) {
            EntityShipBase.Companion.EMOTION_BLINK -> applyBlink()
            EntityShipBase.Companion.EMOTION_CRY -> applyTimedEmotion(
                CRY_DURATION,
                Runnable { this.ship.setFaceCry() },
                EntityShipBase.Companion.EMOTION_NORMAL
            )

            EntityShipBase.Companion.EMOTION_SCORN -> applyTimedEmotion(
                SCORN_DURATION,
                Runnable { this.ship.setFaceScornOrDamaged() },
                EntityShipBase.Companion.EMOTION_NORMAL
            )

            EntityShipBase.Companion.EMOTION_ANGRY -> applyTimedEmotion(
                ANGRY_DURATION,
                Runnable { this.ship.setFaceAngry() },
                EntityShipBase.Companion.EMOTION_NORMAL
            )

            EntityShipBase.Companion.EMOTION_SHY -> applyTimedEmotion(
                SHY_DURATION,
                Runnable { this.ship.setFaceShy() },
                EntityShipBase.Companion.EMOTION_NORMAL
            )

            EntityShipBase.Companion.EMOTION_HAPPY -> applyTimedEmotion(
                HAPPY_DURATION,
                Runnable { this.ship.setFaceHappy() },
                EntityShipBase.Companion.EMOTION_NORMAL
            )

            EntityShipBase.Companion.EMOTION_BORED -> this.ship.setFaceBored()
            EntityShipBase.Companion.EMOTION_HUNGRY -> this.ship.setFaceHungry()
            EntityShipBase.Companion.EMOTION_NORMAL, EntityShipBase.Companion.EMOTION_DEBUG -> this.ship.setFaceNormal()
            else -> this.ship.setFaceNormal()
        }

        if (emotion == EntityShipBase.Companion.EMOTION_NORMAL && this.ship.random
                .nextInt(BLINK_RANDOM_INTERVAL) == 0
        ) {
            this.ship.emotionPrimary = EntityShipBase.Companion.EMOTION_BLINK
            this.faceTick = -1
        }
    }

    private fun applyBlink() {
        ensureFaceTick()
        val tick = this.faceElapsed
        if (tick >= BLINK_DURATION) {
            this.ship.emotionPrimary = EntityShipBase.Companion.EMOTION_NORMAL
            this.faceTick = -1
            return
        }
        this.ship.faceId = EntityShipBase.Companion.FACE_EYES_CLOSED
        this.ship.mouthId = EntityShipBase.Companion.MOUTH_FRONT_0
    }

    private fun applyTimedEmotion(maxTime: Int, action: Runnable, resetEmotion: Int) {
        ensureFaceTick()
        if (this.faceElapsed > maxTime) {
            this.ship.emotionPrimary = resetEmotion
            this.faceTick = -1
            this.ship.setFaceNormal()
            return
        }
        action.run()
    }

    companion object {
        private const val EMOTION_UPDATE_INTERVAL = 64
        private const val LOW_HEALTH_THRESHOLD = 0.35f
        private const val BLINK_DURATION = 25
        private const val BLINK_RANDOM_INTERVAL = 240

        private const val CRY_DURATION = 80
        private const val SCORN_DURATION = 45
        private const val ANGRY_DURATION = 40
        private const val SHY_DURATION = 80
        private const val HAPPY_DURATION = 60

        const val scornToggleMask: Int = 0x7FF
        const val scornToggleThreshold: Int = 1024

        const val normalMouthTickMask: Int = 0xFF
        const val normalMouthTickThreshold: Int = 160

        const val cryMask: Int = 0xFF
        const val damagedMask: Int = 0x1FF
        const val boredMask: Int = 0x1FF
        const val angryMask: Int = 0xFF
        const val shyMask: Int = 0xFF
        const val happyMask: Int = 0xFF

        private val headTiltMaxAngle = -0.27f
        private val HEAD_TILT_HALF_PI = (Math.PI / 2.0).toFloat()
        private val headTiltResetBaseTicks = 70
        private val headTiltResetRandomTicks = 5
        private val headTiltInSpeed = 0.1f
        private val headTiltOutSpeed = 0.2f
        private val headTiltInMaxTicks = 10.0f
        private val headTiltOutMaxTicks = 8.0f
        private val headTiltAngleEps = 0.03f
    }
}
