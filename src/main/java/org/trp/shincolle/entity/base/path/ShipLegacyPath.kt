package org.trp.shincolle.entity.base.path

import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import kotlin.math.max
import kotlin.math.min

internal class ShipLegacyPath(private val points: Array<ShipLegacyPathPoint>) {
    private var pathIndex = 0

    val currentPathLength: Int
        get() = this.points.size

    val currentPathIndex: Int
        get() = this.pathIndex

    fun setCurrentPathIndex(currentPathIndex: Int) {
        this.pathIndex = max(0, min(currentPathIndex, this.points.size))
    }

    fun incrementPathIndex() {
        setCurrentPathIndex(this.pathIndex + 1)
    }

    val isFinished: Boolean
        get() = this.pathIndex >= this.points.size

    val finalPathPoint: ShipLegacyPathPoint?
        get() = if (this.points.size > 0) this.points[this.points.size - 1] else null

    fun getPathPointFromIndex(index: Int): ShipLegacyPathPoint? {
        if (index < 0 || index >= this.points.size) {
            return null
        }

        return this.points[index]
    }

    val currentPos: Vec3?
        get() {
            if (this.isFinished) {
                return null
            }

            val point = this.points[this.pathIndex]
            return Vec3(point.x.toDouble(), point.y.toDouble(), point.z.toDouble())
        }

    fun getVectorFromIndex(entity: Entity, index: Int): Vec3? {
        if (index < 0 || index >= this.points.size) {
            return null
        }

        val point = this.points[index]
        val x = point.x + ((entity.bbWidth + 1.0f).toInt()).toDouble() * 0.5
        val y = point.y.toDouble()
        val z = point.z + ((entity.bbWidth + 1.0f).toInt()).toDouble() * 0.5
        return Vec3(x, y, z)
    }

    fun getPosition(entity: Entity): Vec3? {
        return getVectorFromIndex(entity, this.pathIndex)
    }
}
