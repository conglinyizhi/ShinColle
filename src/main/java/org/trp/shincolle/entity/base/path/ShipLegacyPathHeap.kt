package org.trp.shincolle.entity.base.path

internal class ShipLegacyPathHeap {
    private var points: Array<ShipLegacyPathPoint?> = arrayOfNulls(1024)
    private var count = 0

    fun clearPath() {
        for (i in 0..<this.count) {
            val point = this.points[i]
            if (point != null) {
                point.heapIndex = -1
                this.points[i] = null
            }
        }
        this.count = 0
    }

    val isPathEmpty: Boolean
        get() = this.count == 0

    fun addPoint(point: ShipLegacyPathPoint): ShipLegacyPathPoint {
        check(!point.isAssigned) { "Point already assigned to heap" }

        if (this.count == this.points.size) {
            this.points = this.points.copyOf(this.points.size * 2)
        }

        this.points[this.count] = point
        point.heapIndex = this.count
        this.sortBack(this.count++)
        return point
    }

    fun dequeue(): ShipLegacyPathPoint {
        check(this.count != 0) { "Cannot dequeue from an empty path heap" }

        val result = this.points[0]!!
        this.points[0] = this.points[--this.count]
        this.points[this.count] = null

        if (this.count > 0) {
            this.points[0]!!.heapIndex = 0
            this.sortForward(0)
        }

        result.heapIndex = -1
        return result
    }

    fun changeDistance(point: ShipLegacyPathPoint, distance: Float) {
        val heapIndex = point.heapIndex
        check(!(heapIndex < 0 || heapIndex >= this.count || this.points[heapIndex] != point)) { "Point is not assigned to this heap" }

        val prev = point.distanceToTarget
        point.distanceToTarget = distance

        if (distance < prev) {
            this.sortBack(heapIndex)
        } else {
            this.sortForward(heapIndex)
        }
    }

    private fun sortBack(index: Int) {
        var index = index
        val current = this.points[index]!!

        while (index > 0) {
            val parentIndex = (index - 1) shr 1
            val parent = this.points[parentIndex]!!

            if (current.distanceToTarget >= parent.distanceToTarget) {
                break
            }

            this.points[index] = parent
            parent.heapIndex = index
            index = parentIndex
        }

        this.points[index] = current
        current.heapIndex = index
    }

    private fun sortForward(index: Int) {
        var index = index
        val current = this.points[index]!!
        val currentDist = current.distanceToTarget

        while (true) {
            val left = 1 + (index shl 1)
            val right = left + 1

            if (left >= this.count) {
                break
            }

            val leftPoint = this.points[left]!!
            val leftDist = leftPoint.distanceToTarget
            var rightDist = Float.POSITIVE_INFINITY
            var rightPoint: ShipLegacyPathPoint? = null

            if (right < this.count) {
                rightPoint = this.points[right]
                rightDist = rightPoint!!.distanceToTarget
            }

            if (leftDist < rightDist) {
                if (leftDist >= currentDist) {
                    break
                }

                this.points[index] = leftPoint
                leftPoint.heapIndex = index
                index = left
            } else {
                if (rightDist >= currentDist) {
                    break
                }

                this.points[index] = rightPoint!!
                rightPoint.heapIndex = index
                index = right
            }
        }

        this.points[index] = current
        current.heapIndex = index
    }
}
