package org.trp.shincolle.utility

import net.minecraft.util.Mth
import kotlin.math.atan2
import kotlin.math.sqrt

object CalcHelper {
    @JvmStatic
    fun rotateXZByAxis(z: Float, x: Float, rad: Float, scale: Float): FloatArray {
        val cosD = Mth.cos(rad)
        val sinD = Mth.sin(rad)
        val newPos = floatArrayOf(0.0f, 0.0f)
        newPos[0] = z * cosD + x * sinD
        newPos[1] = x * cosD - z * sinD
        newPos[0] = newPos[0] * scale
        newPos[1] = newPos[1] * scale
        return newPos
    }

    fun checkIntNotInArray(target: Int, host: IntArray?): Boolean {
        if (host == null) {
            return true
        }
        for (i in host) {
            if (target == i) return false
        }
        return true
    }

    @JvmStatic
    fun getLookDegree(motX: Double, motY: Double, motZ: Double, getDegree: Boolean): FloatArray {
        var motX = motX
        var motY = motY
        var motZ = motZ
        val d1 = sqrt(motX * motX + motY * motY + motZ * motZ)
        if (d1 > 1.0E-4) {
            motX /= d1
            motY /= d1
            motZ /= d1
        }
        val f1 = sqrt(motX * motX + motZ * motZ)
        val degree = FloatArray(2)
        degree[1] = -(atan2(motY, f1).toFloat())
        degree[0] = -(atan2(motX, motZ).toFloat())
        if (getDegree) {
            degree[0] = degree[0] * 57.29578f
            degree[1] = degree[1] * 57.29578f
        }
        return degree
    }

    @JvmStatic
    fun rotateXYZByYawPitch(x: Float, y: Float, z: Float, yaw: Float, pitch: Float, scale: Float): FloatArray {
        val cosYaw = Mth.cos(yaw)
        val sinYaw = Mth.sin(yaw)
        val cosPitch = Mth.cos(-pitch)
        val sinPitch = Mth.sin(-pitch)
        val newPos = floatArrayOf(x, y, z)
        newPos[1] = y * cosPitch + z * sinPitch
        newPos[2] = z * cosPitch - y * sinPitch
        val x2 = newPos[0]
        val z2 = newPos[2]
        newPos[0] = x2 * cosYaw - z2 * sinYaw
        newPos[2] = z2 * cosYaw + x2 * sinYaw
        newPos[0] = newPos[0] * scale
        newPos[1] = newPos[1] * scale
        newPos[2] = newPos[2] * scale
        return newPos
    }
}
