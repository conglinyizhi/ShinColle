package org.trp.shincolle.entity.base

import net.minecraft.core.BlockPos
import java.lang.Float
import kotlin.Array
import kotlin.Boolean
import kotlin.BooleanArray
import kotlin.ByteArray
import kotlin.FloatArray
import kotlin.Int
import kotlin.IntArray
import kotlin.arrayOf
import kotlin.arrayOfNulls
import kotlin.booleanArrayOf
import kotlin.byteArrayOf
import kotlin.floatArrayOf
import kotlin.intArrayOf
import kotlin.math.min

internal class EntityShipLegacyState {
    val stateMinor: IntArray
    val stateTimer: IntArray
    val stateFlag: BooleanArray
    val updateFlag: BooleanArray
    val bodyHeightStand: ByteArray
    val bodyHeightSit: ByteArray
    val modelPos: FloatArray
    var waypoints: Array<BlockPos?>?

    init {
        this.stateMinor = LEGACY_STATE_MINOR_DEFAULTS.copyOf(LEGACY_STATE_MINOR_SIZE)
        this.stateTimer = IntArray(LEGACY_STATE_TIMER_SIZE)
        this.stateFlag = LEGACY_STATE_FLAG_DEFAULTS.copyOf(LEGACY_STATE_FLAG_SIZE)
        this.updateFlag = BooleanArray(LEGACY_UPDATE_FLAG_SIZE)
        this.bodyHeightStand = LEGACY_BODY_HEIGHT_STAND_DEFAULTS.copyOf(LEGACY_BODY_HEIGHT_STAND_DEFAULTS.size)
        this.bodyHeightSit = LEGACY_BODY_HEIGHT_SIT_DEFAULTS.copyOf(LEGACY_BODY_HEIGHT_SIT_DEFAULTS.size)
        this.modelPos = LEGACY_MODEL_POS_DEFAULTS.copyOf(LEGACY_MODEL_POS_DEFAULTS.size)
        this.waypoints = arrayOf<BlockPos?>(BlockPos.ZERO)
    }

    fun getInt(data: IntArray, index: Int): Int {
        if (index < 0 || index >= data.size) {
            return 0
        }
        return data[index]
    }

    fun setInt(data: IntArray, index: Int, value: Int) {
        if (index < 0 || index >= data.size) {
            return
        }
        data[index] = value
    }

    fun getBoolean(data: BooleanArray, index: Int): Boolean {
        if (index < 0 || index >= data.size) {
            return false
        }
        return data[index]
    }

    fun setBoolean(data: BooleanArray, index: Int, value: Boolean) {
        if (index < 0 || index >= data.size) {
            return
        }
        data[index] = value
    }

    fun applyIntArray(target: IntArray?, source: IntArray?) {
        if (source == null || target == null) {
            return
        }
        val length = min(target.size, source.size)
        System.arraycopy(source, 0, target, 0, length)
    }

    fun applyByteArray(target: BooleanArray?, source: ByteArray?) {
        if (source == null || target == null) {
            return
        }
        val length = min(target.size, source.size)
        for (i in 0..<length) {
            target[i] = source[i].toInt() != 0
        }
    }

    fun applyByteArray(target: ByteArray?, source: ByteArray?) {
        if (source == null || target == null) {
            return
        }
        val length = min(target.size, source.size)
        System.arraycopy(source, 0, target, 0, length)
    }

    fun toByteArray(source: BooleanArray): ByteArray {
        val data = ByteArray(source.size)
        for (i in source.indices) {
            data[i] = if (source[i]) 1.toByte() else 0.toByte()
        }
        return data
    }

    val modelPosBits: IntArray
        get() {
            val bits = IntArray(this.modelPos.size)
            for (i in this.modelPos.indices) {
                bits[i] = Float.floatToIntBits(this.modelPos[i])
            }
            return bits
        }

    fun applyModelPos(pos: FloatArray?) {
        if (pos == null) {
            return
        }
        val length = min(this.modelPos.size, pos.size)
        System.arraycopy(pos, 0, this.modelPos, 0, length)
    }

    fun applyModelPosBits(bits: IntArray?) {
        if (bits == null) {
            return
        }
        val length = min(this.modelPos.size, bits.size)
        for (i in 0..<length) {
            this.modelPos[i] = Float.intBitsToFloat(bits[i])
        }
    }

    val waypointBits: IntArray
        get() {
            if (this.waypoints == null || this.waypoints!!.size == 0) {
                return IntArray(0)
            }
            val data = IntArray(this.waypoints!!.size * 3)
            for (i in this.waypoints.indices) {
                val pos = this.waypoints!![i]
                val base = i * 3
                data[base] = pos.getX()
                data[base + 1] = pos.getY()
                data[base + 2] = pos.getZ()
            }
            return data
        }

    fun applyWaypoints(points: Array<BlockPos?>?) {
        if (points == null || points.size == 0) {
            this.waypoints = arrayOf<BlockPos>(BlockPos.ZERO)
            return
        }
        this.waypoints = points.copyOf<BlockPos?>(points.size)
    }

    fun applyWaypointBits(data: IntArray?) {
        if (data == null || data.size < 3) {
            return
        }
        val count = data.size / 3
        val points: Array<BlockPos> = arrayOfNulls<BlockPos>(count)
        for (i in 0..<count) {
            val base = i * 3
            points[i] = BlockPos(data[base], data[base + 1], data[base + 2])
        }
        this.waypoints = points
    }

    companion object {
        private const val LEGACY_STATE_MINOR_SIZE = 46
        private const val LEGACY_STATE_TIMER_SIZE = 21
        private const val LEGACY_STATE_FLAG_SIZE = 28
        private const val LEGACY_UPDATE_FLAG_SIZE = 8
        private val LEGACY_STATE_MINOR_DEFAULTS = intArrayOf(
            1,
            0,
            0,
            40,
            0,
            0,
            0,
            0,
            0,
            3,
            5,
            8,
            35,
            1,
            -1,
            -1,
            -1,
            0,
            -1,
            0,
            0,
            -1,
            -1,
            -1,
            0,
            0,
            0,
            0,
            0,
            0,
            60,
            0,
            10,
            0,
            0,
            -1,
            0,
            0,
            0,
            0,
            -1,
            -1,
            -1,
            0,
            0,
            -1
        )
        private val LEGACY_STATE_FLAG_DEFAULTS = booleanArrayOf(
            false,
            false,
            false,
            false,
            true,
            true,
            true,
            true,
            false,
            true,
            true,
            false,
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            false,
            false,
            false,
            true,
            true,
            false,
            true,
            false,
            false
        )
        private val LEGACY_BODY_HEIGHT_STAND_DEFAULTS = byteArrayOf(92, 78, 73, 58, 47, 37)
        private val LEGACY_BODY_HEIGHT_SIT_DEFAULTS = byteArrayOf(64, 49, 44, 29, 23, 12)
        private val LEGACY_MODEL_POS_DEFAULTS = floatArrayOf(0.0f, 0.0f, 0.0f, 50.0f)
    }
}
