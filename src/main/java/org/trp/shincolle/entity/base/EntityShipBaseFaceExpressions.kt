package org.trp.shincolle.entity.base

internal class EntityShipBaseFaceExpressions(
    private val ship: EntityShipBase,
    private val emotions: EntityShipBaseEmotions
) {
    fun setFaceNormal() {
        this.ship.faceId = EntityShipBase.Companion.FACE_EYES_OPEN
        if (this.ship.emotionSecondary == EntityShipBase.Companion.EMOTION_BORED
            && (this.ship.tickCount and EntityShipBaseEmotions.normalMouthTickMask) > EntityShipBaseEmotions.normalMouthTickThreshold
        ) {
            this.ship.mouthId = this.ship.resolveMouthId(EntityShipBase.Companion.MOUTH_FLIP_0)
        } else {
            this.ship.mouthId = EntityShipBase.Companion.MOUTH_FRONT_0
        }
    }

    fun setFaceCry() {
        val tick = this.ship.faceElapsed and EntityShipBaseEmotions.cryMask
        val steps: Array<FaceStep> = arrayOf<FaceStep>(
            FaceStep(64, EntityShipBase.Companion.FACE_DOT_EYES_TEAR, EntityShipBase.Companion.MOUTH_FLIP_2),
            FaceStep(128, EntityShipBase.Companion.FACE_DOT_EYES_TEAR, EntityShipBase.Companion.MOUTH_FRONT_2),
            FaceStep(256, EntityShipBase.Companion.FACE_CRY, EntityShipBase.Companion.MOUTH_FRONT_2)
        )
        applyFaceTimeline(steps, EntityShipBase.Companion.FACE_CRY, EntityShipBase.Companion.MOUTH_FRONT_2, tick)
    }

    fun setFaceScornOrDamaged() {
        if ((this.ship.tickCount and EntityShipBaseEmotions.scornToggleMask) > EntityShipBaseEmotions.scornToggleThreshold) {
            setFaceDamaged()
        } else {
            setFaceScorn()
        }
    }

    fun setFaceScorn() {
        this.ship.faceId = EntityShipBase.Companion.FACE_EYES_HALF
        this.ship.mouthId = EntityShipBase.Companion.MOUTH_FRONT_1
    }

    fun setFaceDamaged() {
        val tick = this.ship.faceElapsed and EntityShipBaseEmotions.damagedMask
        val steps: Array<FaceStep> = arrayOf<FaceStep>(
            FaceStep(60, EntityShipBase.Companion.FACE_DOT_EYES_TEAR, EntityShipBase.Companion.MOUTH_FLIP_2),
            FaceStep(200, EntityShipBase.Companion.FACE_DOT_EYES_TEAR, EntityShipBase.Companion.MOUTH_FRONT_2),
            FaceStep(250, EntityShipBase.Companion.FACE_TENSION, EntityShipBase.Companion.MOUTH_FRONT_0),
            FaceStep(400, EntityShipBase.Companion.FACE_TENSION, EntityShipBase.Companion.MOUTH_FLIP_1),
            FaceStep(450, EntityShipBase.Companion.FACE_SOFT, EntityShipBase.Companion.MOUTH_FRONT_0)
        )
        applyFaceTimeline(steps, EntityShipBase.Companion.FACE_SOFT, EntityShipBase.Companion.MOUTH_FRONT_1, tick)
    }

    fun setFaceHungry() {
        this.ship.faceId = EntityShipBase.Companion.FACE_DESPAIR
        this.ship.mouthId = EntityShipBase.Companion.MOUTH_FRONT_2
    }

    fun setFaceAngry() {
        val tick = this.ship.faceElapsed and EntityShipBaseEmotions.angryMask
        val steps: Array<FaceStep> = arrayOf<FaceStep>(
            FaceStep(64, EntityShipBase.Companion.FACE_EYES_CLOSED, EntityShipBase.Companion.MOUTH_FRONT_0),
            FaceStep(128, EntityShipBase.Companion.FACE_EYES_CLOSED, EntityShipBase.Companion.MOUTH_FRONT_1),
            FaceStep(170, EntityShipBase.Companion.FACE_EYES_HALF, EntityShipBase.Companion.MOUTH_FRONT_1)
        )
        applyFaceTimeline(steps, EntityShipBase.Companion.FACE_EYES_HALF, EntityShipBase.Companion.MOUTH_FRONT_2, tick)
    }

    fun setFaceBored() {
        val tick = this.ship.faceElapsed and EntityShipBaseEmotions.boredMask
        val steps: Array<FaceStep> = arrayOf<FaceStep>(
            FaceStep(80, EntityShipBase.Companion.FACE_DOT_EYES, EntityShipBase.Companion.MOUTH_FRONT_0),
            FaceStep(170, EntityShipBase.Companion.FACE_DOT_EYES, EntityShipBase.Companion.MOUTH_FLIP_1),
            FaceStep(340, EntityShipBase.Companion.FACE_WINK, EntityShipBase.Companion.MOUTH_FRONT_0)
        )
        applyFaceTimeline(steps, EntityShipBase.Companion.FACE_EYES_OPEN, EntityShipBase.Companion.MOUTH_FRONT_0, tick)
    }

    fun setFaceShy() {
        val tick = this.ship.faceElapsed and EntityShipBaseEmotions.shyMask
        val steps: Array<FaceStep> = arrayOf<FaceStep>(
            FaceStep(80, EntityShipBase.Companion.FACE_EYES_OPEN, EntityShipBase.Companion.MOUTH_FLIP_0),
            FaceStep(140, EntityShipBase.Companion.FACE_EYES_OPEN, EntityShipBase.Companion.MOUTH_FRONT_2)
        )
        applyFaceTimeline(steps, EntityShipBase.Companion.FACE_WINK, EntityShipBase.Companion.MOUTH_FRONT_0, tick)
    }

    fun setFaceHappy() {
        val tick = this.ship.faceElapsed and EntityShipBaseEmotions.happyMask
        val steps: Array<FaceStep> = arrayOf<FaceStep>(
            FaceStep(80, EntityShipBase.Companion.FACE_TENSION, EntityShipBase.Companion.MOUTH_FRONT_0),
            FaceStep(140, EntityShipBase.Companion.FACE_TENSION, EntityShipBase.Companion.MOUTH_FLIP_1)
        )
        applyFaceTimeline(steps, EntityShipBase.Companion.FACE_WINK, EntityShipBase.Companion.MOUTH_FLIP_1, tick)
    }

    private fun applyFaceTimeline(steps: Array<FaceStep>, fallbackFaceId: Int, fallbackMouthId: Int, tick: Int) {
        for (step in steps) {
            if (tick < step.untilTick) {
                this.ship.faceId = step.faceId
                this.ship.mouthId = this.ship.resolveMouthId(step.mouthId)
                return
            }
        }
        this.ship.faceId = fallbackFaceId
        this.ship.mouthId = this.ship.resolveMouthId(fallbackMouthId)
    }

    @JvmRecord
    private data class FaceStep(val untilTick: Int, val faceId: Int, val mouthId: Int)
}
