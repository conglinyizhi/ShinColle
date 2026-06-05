package org.trp.shincolle.entity.base.path

import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import kotlin.math.max
import kotlin.math.min

internal class ShipLegacyPath(private val points: Array<ShipLegacyPathPoint>) {
    private var currentPathIndex = 0

    val currentPathLength: Int
        get() = this.points.size

    fun getCurrentPathIndex(): Int {
        return this.currentPathIndex
    }

    fun setCurrentPathIndex(currentPathIndex: Int) {
        this.currentPathIndex = max(0, min(currentPathIndex, this.points.size))
    }

    fun incrementPathIndex() {
        setCurrentPathIndex(this.currentPathIndex + 1)
    }

    val isFinished: Boolean
        get() = this.currentPathIndex >= this.points.size

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

            val point = this.points[this.currentPathIndex]
            return Vec3(point.getX().toDouble(), point.getY().toDouble(), point.getZ().toDouble())
        }

    fun getVectorFromIndex(entity: Entity, index: Int): Vec3? {
        if (index < 0 || index >= this.points.size) {
            return null
        }

        val point = this.points[index]
        val x = point.getX() + ((entity.getBbWidth() + 1.0f).toInt()).toDouble() * 0.5
        val y = point.getY().toDouble()
        val z = point.getZ() + ((entity.getBbWidth() + 1.0f).toInt()).toDouble() * 0.5
        return Vec3(x, y, z)
    }

    fun getPosition(entity: Entity): Vec3? {
        return getVectorFromIndex(entity, this.currentPathIndex)
    }
}
