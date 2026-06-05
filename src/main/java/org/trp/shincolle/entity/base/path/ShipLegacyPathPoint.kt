package org.trp.shincolle.entity.base.path

import net.minecraft.util.Mth
import kotlin.math.abs

internal class ShipLegacyPathPoint(val x: Int, val y: Int, val z: Int, val key: Long) {
    var heapIndex: Int = -1
    var totalPathDistance: Float = 0f
        private set
    var distanceToNext: Float = 0f
        private set
    var distanceToTarget: Float = 0f
    var previous: ShipLegacyPathPoint? = null
        private set
    var isClosed: Boolean = false
    private var distanceFromOrigin = 0f

    fun initForSearch(target: ShipLegacyPathPoint) {
        this.totalPathDistance = 0.0f
        val dist = distanceManhattan(target)
        this.distanceToNext = dist
        this.distanceToTarget = dist
        this.distanceFromOrigin = 0.0f
        this.previous = null
        this.isClosed = false
        this.heapIndex = -1
    }

    fun initPathParameters(parent: ShipLegacyPathPoint, target: ShipLegacyPathPoint, range: Float): Boolean {
        val dist = parent.distanceManhattan(this)
        val newFrom = parent.distanceFromOrigin + dist
        val newCost = parent.totalPathDistance + dist

        if (newFrom >= range || (this.isAssigned && newCost >= this.totalPathDistance)) {
            return false
        }

        this.distanceFromOrigin = newFrom
        this.previous = parent
        this.totalPathDistance = newCost
        this.distanceToNext = distanceManhattan(target)
        return true
    }

    fun distanceTo(point: ShipLegacyPathPoint): Float {
        val dx = point.x.toFloat() - this.x
        val dy = point.y.toFloat() - this.y
        val dz = point.z.toFloat() - this.z
        return Mth.sqrt(dx * dx + dy * dy + dz * dz)
    }

    fun distanceToSquared(point: ShipLegacyPathPoint): Float {
        val dx = point.x.toFloat() - this.x
        val dy = point.y.toFloat() - this.y
        val dz = point.z.toFloat() - this.z
        return dx * dx + dy * dy + dz * dz
    }

    fun distanceManhattan(point: ShipLegacyPathPoint): Float {
        return ((abs(point.x - this.x) + abs(point.y - this.y) + abs(point.z - this.z))).toFloat()
    }

    val isAssigned: Boolean
        get() = this.heapIndex >= 0
}
