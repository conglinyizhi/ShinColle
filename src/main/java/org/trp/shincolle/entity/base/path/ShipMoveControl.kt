package org.trp.shincolle.entity.base.path

import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.MoveControl
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

class ShipMoveControl(host: Mob, private val maxTurn: Float) : MoveControl(host) {
    override fun tick() {
        this.mob.zza = 0.0f

        if (this.operation != Operation.MOVE_TO) {
            applyIdleLiquidStabilization()
            return
        }

        this.operation = Operation.WAIT

        val dx = this.wantedX - this.mob.x
        val dy = this.wantedY - this.mob.y
        val dz = this.wantedZ - this.mob.z
        val horizontalSq = dx * dx + dz * dz
        val distSq = horizontalSq + dy * dy

        if (distSq < ARRIVAL_STOP_DISTANCE_SQR) {
            this.mob.setSpeed(0.0f)
            this.mob.zza = 0.0f

            val motion = this.mob.deltaMovement

            if (this.isInLiquid) {
                val motionY = Mth.clamp(motion.y * 0.45 + computeFluidSurfaceCorrection(0.08), -0.04, 0.04)
                this.mob.setDeltaMovement(0.0, motionY, 0.0)
            } else {
                this.mob.setDeltaMovement(motion.x * 0.05, motion.y, motion.z * 0.05)
            }

            return
        }

        val targetYaw = (Mth.atan2(dz, dx) * (180.0 / Math.PI)).toFloat() - 90.0f
        this.mob.yRot = this.rotlerp(this.mob.yRot, targetYaw, this.maxTurn)
        this.mob.yBodyRot = this.mob.yRot
        this.mob.yHeadRot = this.mob.yRot

        val baseMoveSpeed = (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)).toFloat()
        val dist = sqrt(distSq)
        var distanceScale = 1.0f

        if (distSq < ARRIVAL_SLOWDOWN_DISTANCE_SQR) {
            distanceScale = Mth.clamp((dist / 1.5).toFloat(), 0.2f, 1.0f)
        }

        var moveSpeed = baseMoveSpeed * distanceScale

        if (this.isInLiquid) {
            val motion = this.mob.deltaMovement
            var verticalBoost = 0.0

            if (dy > 1.5) {
                verticalBoost = moveSpeed * 0.15
                moveSpeed *= 0.5f
            } else if (dy > 0.25) {
                verticalBoost = moveSpeed * 0.08
                moveSpeed *= 0.75f
            } else if (dy > -0.5 && dy < 0.0) {
                verticalBoost = moveSpeed * 0.1
                moveSpeed *= 0.5f
            } else if (dy < -1.0) {
                verticalBoost = -moveSpeed * 0.35
                moveSpeed *= 0.82f
            }

            var motionY = motion.y * 0.72 + verticalBoost + computeFluidSurfaceCorrection(0.06)

            if (this.mob.horizontalCollision && dy > -0.2) {
                motionY = max(motionY, 0.14)
            }

            val horizontal = sqrt(horizontalSq)
            var targetVx = 0.0
            var targetVz = 0.0

            if (horizontal > 1.0E-5) {
                val cruise = (moveSpeed * LIQUID_CRUISE_SPEED_FACTOR).toDouble()
                val invHorizontal = 1.0 / horizontal
                targetVx = dx * invHorizontal * cruise
                targetVz = dz * invHorizontal * cruise
            }

            val steerStrength: Double = if (distSq < ARRIVAL_SLOWDOWN_DISTANCE_SQR)
                LIQUID_NEAR_STOP_STEER_STRENGTH
            else
                LIQUID_STEER_STRENGTH
            var newVx = Mth.lerp(steerStrength, motion.x * LIQUID_INERTIA_DAMPING, targetVx)
            var newVz = Mth.lerp(steerStrength, motion.z * LIQUID_INERTIA_DAMPING, targetVz)

            if (abs(targetVx) < LIQUID_STOP_EPSILON && abs(newVx) < LIQUID_STOP_EPSILON) {
                newVx = 0.0
            }

            if (abs(targetVz) < LIQUID_STOP_EPSILON && abs(newVz) < LIQUID_STOP_EPSILON) {
                newVz = 0.0
            }

            this.mob.setDeltaMovement(
                newVx,
                Mth.clamp(motionY, -0.09, 0.14),
                newVz
            )
            this.mob.setSpeed(moveSpeed)
            return
        }

        val needsStepJump = dy > this.mob.maxUpStep() && horizontalSq < 4.0
        val blockedOnLedge = this.mob.horizontalCollision && dy > STEP_ASSIST_MIN_DY && horizontalSq < 3.0

        if (needsStepJump || blockedOnLedge) {
            this.mob.jumpControl.jump()
        }

        this.mob.setSpeed(moveSpeed)
        this.mob.zza = 1.0f

        val motion = this.mob.deltaMovement
        this.mob.setDeltaMovement(motion.x * 0.95, motion.y, motion.z * 0.95)
    }

    private fun applyIdleLiquidStabilization() {
        if (!this.isInLiquid) {
            return
        }

        val motion = this.mob.deltaMovement
        val motionY = Mth.clamp(motion.y * 0.55 + computeFluidSurfaceCorrection(0.08), -0.05, 0.05)

        if (this.mob.isVehicle() && this.mob.controllingPassenger != null) {
            this.mob.setDeltaMovement(motion.x, motionY, motion.z)
            return
        }

        var motionX = motion.x * 0.3
        var motionZ = motion.z * 0.3

        if (abs(motionX) < LIQUID_STOP_EPSILON) {
            motionX = 0.0
        }

        if (abs(motionZ) < LIQUID_STOP_EPSILON) {
            motionZ = 0.0
        }

        this.mob.setDeltaMovement(motionX, motionY, motionZ)
    }

    private fun computeFluidSurfaceCorrection(strength: Double): Double {
        val surfaceY = this.fluidSurfaceY
        if (java.lang.Double.isNaN(surfaceY)) {
            return 0.0
        }

        val targetY: Double = surfaceY - LIQUID_HOVER_OFFSET
        return Mth.clamp((targetY - this.mob.y) * strength, -0.03, 0.03)
    }

    private val fluidSurfaceY: Double
        get() {
            val level = this.mob.level()
            var pos = BlockPos.containing(this.mob.x, this.mob.y, this.mob.z)
            var fluid = level.getFluidState(pos)

            if (fluid.isEmpty()) {
                val below = pos.below()
                fluid = level.getFluidState(below)

                if (fluid.isEmpty()) {
                    return Double.NaN
                }

                pos = below
            }

            return (pos.y + fluid.getHeight(level, pos)).toDouble()
        }

    private val isInLiquid: Boolean
        get() = this.mob.isInWaterOrBubble() || this.mob.isInLava()

    companion object {
        private val ARRIVAL_STOP_DISTANCE_SQR = 0.2 * 0.2
        private const val ARRIVAL_SLOWDOWN_DISTANCE_SQR = 2.25
        private val STEP_ASSIST_MIN_DY = -0.15
        private const val LIQUID_HOVER_OFFSET = 0.08
        private const val LIQUID_CRUISE_SPEED_FACTOR = 1.35f
        private const val LIQUID_STEER_STRENGTH = 0.9
        private const val LIQUID_NEAR_STOP_STEER_STRENGTH = 0.78
        private const val LIQUID_INERTIA_DAMPING = 0.35
        private const val LIQUID_STOP_EPSILON = 0.003
    }
}
