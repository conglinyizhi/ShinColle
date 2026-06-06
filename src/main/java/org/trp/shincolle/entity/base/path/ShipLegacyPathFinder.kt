package org.trp.shincolle.entity.base.path

import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.*

internal class ShipLegacyPathFinder(private val level: LevelReader, private val canEntityFly: Boolean) {
    private enum class LegacyPathType {
        OPEN,
        FLUID,
        OPENABLE,
        FENCE,
        BLOCKED
    }

    private val openSet = ShipLegacyPathHeap()
    private val pointMap: MutableMap<Long, ShipLegacyPathPoint> = HashMap()
    private val pathOptions: Array<ShipLegacyPathPoint?> = arrayOfNulls(MAX_PATH_OPTIONS)

    fun findPath(host: Entity, target: Entity, range: Float): ShipLegacyPath? {
        return findPath(host, target.x, target.y, target.z, range)
    }

    fun findPath(host: Entity, x: Int, y: Int, z: Int, range: Float): ShipLegacyPath? {
        return findPath(host, x.toDouble() + 0.5, y.toDouble(), z.toDouble() + 0.5, range)
    }

    fun findPath(host: Entity, tx: Double, ty: Double, tz: Double, range: Float): ShipLegacyPath? {
        this.openSet.clearPath()
        this.pointMap.clear()

        var startY: Int
        val hostPos = host.blockPosition()

        if (isInLiquid(host)) {
            startY = hostPos.y

            val cursor = MutableBlockPos(hostPos.x, startY, hostPos.z)

            while (startY < this.level.maxBuildHeight
                && !this.level.getFluidState(cursor).isEmpty
            ) {
                startY++
                cursor.setY(startY)
            }
        } else {
            startY = Mth.floor(host.boundingBox.minY + 0.5)
        }

        val start = openPoint(
            Mth.floor(host.boundingBox.minX),
            startY,
            Mth.floor(host.boundingBox.minZ)
        )
        val end = openPoint(Mth.floor(tx), Mth.floor(ty), Mth.floor(tz))

        val sizeX = Mth.floor(host.bbWidth + 1.0f)
        val sizeY = Mth.floor(host.bbHeight + 1.0f)
        val sizeZ = Mth.floor(host.bbWidth + 1.0f)

        return addToPath(host, start, end, range, sizeX, sizeY, sizeZ)
    }

    private fun addToPath(
        host: Entity,
        start: ShipLegacyPathPoint,
        end: ShipLegacyPathPoint,
        range: Float,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int
    ): ShipLegacyPath? {
        start.initForSearch(end)
        this.openSet.clearPath()
        this.openSet.addPoint(start)

        var nearest = start
        var loops = 0

        while (!this.openSet.isPathEmpty && loops++ < MAX_SEARCH_ITERS) {
            val current = this.openSet.dequeue()

            if (current.key == end.key) {
                return createEntityPath(start, end)
            }

            if (current.distanceToSquared(end) < nearest.distanceToSquared(end)) {
                nearest = current
            }

            current.isClosed = true
            val optionCount = findPathOptions(host, current, end, range, sizeX, sizeY, sizeZ)

            for (i in 0..<optionCount) {
                val next = this.pathOptions[i]!!

                if (next.initPathParameters(current, end, range)) {
                    val score = next.totalPathDistance + next.distanceToNext

                    if (next.isAssigned) {
                        this.openSet.changeDistance(next, score)
                    } else {
                        next.distanceToTarget = score
                        this.openSet.addPoint(next)
                    }
                }
            }
        }

        return if (nearest == start) null else createEntityPath(start, nearest)
    }

    private fun findPathOptions(
        host: Entity,
        current: ShipLegacyPathPoint,
        target: ShipLegacyPathPoint,
        maxRange: Float,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int
    ): Int {
        var yOffset = 0
        val above = BlockPos(current.x, current.y + 1, current.z)

        val aboveType = getPathType(above.x, above.y, above.z, sizeX, sizeY, sizeZ)
        if (aboveType == LegacyPathType.OPEN || aboveType == LegacyPathType.FLUID || aboveType == LegacyPathType.OPENABLE) {
            yOffset = 1
        }

        var found = 0
        val candidates = arrayOf<ShipLegacyPathPoint?>(
            getSafePoint(host, current.x, current.y, current.z + 1, yOffset, sizeX, sizeY, sizeZ),
            getSafePoint(host, current.x - 1, current.y, current.z, yOffset, sizeX, sizeY, sizeZ),
            getSafePoint(host, current.x + 1, current.y, current.z, yOffset, sizeX, sizeY, sizeZ),
            getSafePoint(host, current.x, current.y, current.z - 1, yOffset, sizeX, sizeY, sizeZ),
            getSafePoint(host, current.x, current.y + 1, current.z, 0, sizeX, sizeY, sizeZ),
            getSafePoint(host, current.x, current.y - 1, current.z, 0, sizeX, sizeY, sizeZ)
        )

        for (candidate in candidates) {
            if (candidate != null && !candidate.isClosed && candidate.distanceTo(target) < maxRange) {
                this.pathOptions[found++] = candidate
            }
        }

        return found
    }

    private fun getSafePoint(
        host: Entity,
        x: Int,
        y: Int,
        z: Int,
        yOffset: Int,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int
    ): ShipLegacyPathPoint? {
        var result: ShipLegacyPathPoint? = null

        val type = getPathType(x, y, z, sizeX, sizeY, sizeZ)

        if (type == LegacyPathType.OPEN || type == LegacyPathType.OPENABLE || type == LegacyPathType.FLUID) {
            result = openPoint(x, y, z)
        }

        if (result == null && yOffset > 0 && type != LegacyPathType.FENCE) {
            result = getSafePoint(host, x, y + yOffset, z, 0, sizeX, sizeY, sizeZ)
        }

        if (result == null && type == LegacyPathType.BLOCKED) {
            return null
        }

        if (result != null && !this.canEntityFly && !isInLiquid(host)) {
            var scanY = result.y
            var scanCount = 0

            while (scanY > this.level.minBuildHeight) {
                val below = getPathType(x, scanY - 1, z, sizeX, sizeY, sizeZ)

                if (below == LegacyPathType.FLUID) {
                    return openPoint(x, scanY - 1, z)
                }

                if (below != LegacyPathType.OPEN) {
                    break
                }

                if (scanCount++ > MAX_DESCEND_SCAN) {
                    return null
                }

                scanY--
                result = openPoint(x, scanY, z)
            }
        }

        return result
    }

    private fun getPathType(x: Int, y: Int, z: Int, sizeX: Int, sizeY: Int, sizeZ: Int): LegacyPathType {
        var sawOpen = false
        var sawFluid = false
        var sawOpenable = false

        val pos = MutableBlockPos()

        for (ix in x..<x + sizeX) {
            for (iy in y..<y + sizeY) {
                for (iz in z..<z + sizeZ) {
                    pos.set(ix, iy, iz)
                    val state = this.level.getBlockState(pos)

                    if (state.isAir) {
                        sawOpen = true
                        continue
                    }

                    if (state.block is StairBlock
                        || state.block is LadderBlock
                        || state.`is`(BlockTags.CLIMBABLE)
                    ) {
                        sawOpenable = true
                        continue
                    }

                    if (state.block is FenceBlock || state.block is WallBlock) {
                        return LegacyPathType.FENCE
                    }

                    if (state.block is DoorBlock || state.block is FenceGateBlock) {
                        if (state.getCollisionShape(this.level, pos).isEmpty) {
                            sawOpen = true
                        } else {
                            sawOpenable = true
                        }
                        continue
                    }

                    if (!state.fluidState.isEmpty) {
                        sawFluid = true
                        continue
                    }

                    if (!state.getCollisionShape(this.level, pos).isEmpty) {
                        return LegacyPathType.BLOCKED
                    }

                    sawOpen = true
                }
            }
        }

        if (sawFluid) {
            return LegacyPathType.FLUID
        }

        if (sawOpenable) {
            return LegacyPathType.OPENABLE
        }

        return if (sawOpen) LegacyPathType.OPEN else LegacyPathType.BLOCKED
    }

    private fun openPoint(x: Int, y: Int, z: Int): ShipLegacyPathPoint {
        val key = BlockPos.asLong(x, y, z)
        var point = this.pointMap[key]

        if (point == null) {
            point = ShipLegacyPathPoint(x, y, z, key)
            this.pointMap[key] = point
        }

        return point
    }

    private fun createEntityPath(start: ShipLegacyPathPoint?, end: ShipLegacyPathPoint): ShipLegacyPath {
        val path: MutableList<ShipLegacyPathPoint> = ArrayList()
        var node = end

        path.add(end)

        while (node.previous != null && node != start) {
            node = node.previous!!
            path.add(0, node)
        }

        return ShipLegacyPath(path.toTypedArray())
    }

    companion object {
        private const val MAX_DESCEND_SCAN = 64
        private const val MAX_SEARCH_ITERS = 4096
        private const val MAX_PATH_OPTIONS = 32

        private fun isInLiquid(entity: Entity): Boolean {
            return entity.isInWaterOrBubble || entity.isInLava
        }
    }
}
