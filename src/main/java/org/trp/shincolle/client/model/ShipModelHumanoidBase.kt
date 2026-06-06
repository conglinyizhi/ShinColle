package org.trp.shincolle.client.model

import net.minecraft.client.model.geom.ModelPart
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import kotlin.math.cos

abstract class ShipModelHumanoidBase<T : EntityShipBase> : ShipModelBaseAdv<T>() {
    protected class PoseContext(
        @JvmField val angleX: Float, @JvmField val angleAdd1: Float, @JvmField val angleAdd2: Float,
        @JvmField val legAddLeft: Float, @JvmField val legAddRight: Float, @JvmField val isSitting: Boolean
    )

    protected fun computePoseContext(
        entity: T?,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        legBaseXRotOffset: Float
    ): PoseContext {
        val angleX = cos((ageInTicks * 0.08f + limbSwing * 0.25f).toDouble()).toFloat()
        val angleAdd1 = cos((limbSwing * 0.7f).toDouble()).toFloat() * limbSwingAmount
        val angleAdd2 = cos((limbSwing * 0.7f + Math.PI.toFloat()).toDouble()).toFloat() * limbSwingAmount
        val addk1 = angleAdd1 - legBaseXRotOffset
        val addk2 = angleAdd2 - legBaseXRotOffset
        val isSitting = entity != null && entity.isInSittingPose && (entity.getVehicle() !is EntityMountBase)

        return PoseContext(angleX, angleAdd1, angleAdd2, addk1, addk2, isSitting)
    }

    protected fun applyFaceAndMouth(entity: EntityShipBase?) {
        if (entity == null) {
            return
        }
        setFace(entity.faceId)
        setMouth(entity.mouthId)
    }

    protected fun applyHeadRotation(
        head: ModelPart?,
        entity: T?,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        if (head == null) {
            return
        }
        val headRadY = netHeadYaw * (Math.PI.toFloat() / 180f)
        val headRadX = headPitch * (Math.PI.toFloat() / 180f)

        head.yRot = headRadY
        head.xRot = headRadX
        head.zRot = if (entity != null) entity.getHeadTiltAngle(ageInTicks) else 0.0f
    }

    protected fun applySittingPose(
        head: ModelPart?, armLeft: ModelPart?, armRight: ModelPart?,
        legLeft: ModelPart?, legRight: ModelPart?,
        headYawScale: Float, armZRotDelta: Float,
        legYRot: Float, legXRot: Float
    ) {
        if (head != null) {
            head.yRot *= headYawScale
        }

        if (armLeft != null) {
            armLeft.zRot -= armZRotDelta
        }
        if (armRight != null) {
            armRight.zRot += armZRotDelta
        }

        if (legLeft != null) {
            legLeft.yRot = -legYRot
            legLeft.xRot = legXRot
        }
        if (legRight != null) {
            legRight.yRot = legYRot
            legRight.xRot = legXRot
        }
    }

    protected fun applySittingOrLegPose(
        ctx: PoseContext, legLeft: ModelPart?, legRight: ModelPart?,
        headYawScale: Float, armZRotDelta: Float, legYRot: Float, legXRot: Float,
        head: ModelPart?, armLeft: ModelPart?, armRight: ModelPart?
    ) {
        if (ctx.isSitting) {
            applySittingPose(head, armLeft, armRight, legLeft, legRight, headYawScale, armZRotDelta, legYRot, legXRot)
        } else {
            if (legLeft != null) {
                legLeft.xRot = ctx.legAddLeft
            }
            if (legRight != null) {
                legRight.xRot = ctx.legAddRight
            }
        }
    }
}
