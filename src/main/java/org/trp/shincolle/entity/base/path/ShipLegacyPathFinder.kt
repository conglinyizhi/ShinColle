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
    private val pointMap: MutableMap<Long?, ShipLegacyPathPoint?> = HashMap<Long?, ShipLegacyPathPoint?>()
    private val pathOptions: Array<ShipLegacyPathPoint> = arrayOfNulls<ShipLegacyPathPoint>(MAX_PATH_OPTIONS)

    fun findPath(host: Entity, target: Entity, range: Float): ShipLegacyPath? {
        return findPath(host, target.getX(), target.getY(), target.getZ(), range)
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
            startY = hostPos.getY()

            val cursor = MutableBlockPos(hostPos.getX(), startY, hostPos.getZ())

            while (startY < this.level.getMaxBuildHeight()
                && !this.level.getFluidState(cursor).isEmpty()
            ) {
                startY++
                cursor.setY(startY)
            }
        } else {
            startY = Mth.floor(host.getBoundingBox().minY + 0.5)
        }

        val start = openPoint(
            Mth.floor(host.getBoundingBox().minX),
            startY,
            Mth.floor(host.getBoundingBox().minZ)
        )
        val end = openPoint(Mth.floor(tx), Mth.floor(ty), Mth.floor(tz))

        val sizeX = Mth.floor(host.getBbWidth() + 1.0f)
        val sizeY = Mth.floor(host.getBbHeight() + 1.0f)
        val sizeZ = Mth.floor(host.getBbWidth() + 1.0f)

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

        while (!this.openSet.isPathEmpty() && loops++ < MAX_SEARCH_ITERS) {
            val current = this.openSet.dequeue()

            if (current.getKey() == end.getKey()) {
                return createEntityPath(start, end)
            }

            if (current.distanceToSquared(end) < nearest.distanceToSquared(end)) {
                nearest = current
            }

            current.setClosed(true)
            val optionCount = findPathOptions(host, current, end, range, sizeX, sizeY, sizeZ)

            for (i in 0..<optionCount) {
                val next = this.pathOptions[i]

                if (next.initPathParameters(current, end, range)) {
                    val score = next.getTotalPathDistance() + next.getDistanceToNext()

                    if (next.isAssigned()) {
                        this.openSet.changeDistance(next, score)
                    } else {
                        next.setDistanceToTarget(score)
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
        val above = BlockPos(current.getX(), current.getY() + 1, current.getZ())

        val aboveType = getPathType(above.getX(), above.getY(), above.getZ(), sizeX, sizeY, sizeZ)
        if (aboveType == LegacyPathType.OPEN || aboveType == LegacyPathType.FLUID || aboveType == LegacyPathType.OPENABLE) {
            yOffset = 1
        }

        var found = 0
        val candidates = arrayOf<ShipLegacyPathPoint?>(
            getSafePoint(host, current.getX(), current.getY(), current.getZ() + 1, yOffset, sizeX, sizeY, sizeZ),
            getSafePoint(host, current.getX() - 1, current.getY(), current.getZ(), yOffset, sizeX, sizeY, sizeZ),
            getSafePoint(host, current.getX() + 1, current.getY(), current.getZ(), yOffset, sizeX, sizeY, sizeZ),
            getSafePoint(host, current.getX(), current.getY(), current.getZ() - 1, yOffset, sizeX, sizeY, sizeZ),
            getSafePoint(host, current.getX(), current.getY() + 1, current.getZ(), 0, sizeX, sizeY, sizeZ),
            getSafePoint(host, current.getX(), current.getY() - 1, current.getZ(), 0, sizeX, sizeY, sizeZ)
        )

        for (candidate in candidates) {
            if (candidate != null && !candidate.isClosed() && candidate.distanceTo(target) < maxRange) {
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
            var scanY = result.getY()
            var scanCount = 0

            while (scanY > this.level.getMinBuildHeight()) {
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

                    if (state.isAir()) {
                        sawOpen = true
                        continue
                    }

                    if (state.getBlock() is StairBlock
                        || state.getBlock() is LadderBlock
                        || state.`is`(BlockTags.CLIMBABLE)
                    ) {
                        sawOpenable = true
                        continue
                    }

                    if (state.getBlock() is FenceBlock || state.getBlock() is WallBlock) {
                        return LegacyPathType.FENCE
                    }

                    if (state.getBlock() is DoorBlock || state.getBlock() is FenceGateBlock) {
                        if (state.getCollisionShape(this.level, pos).isEmpty()) {
                            sawOpen = true
                        } else {
                            sawOpenable = true
                        }
                        continue
                    }

                    if (!state.getFluidState().isEmpty()) {
                        sawFluid = true
                        continue
                    }

                    if (!state.getCollisionShape(this.level, pos).isEmpty()) {
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
        var point = this.pointMap.get(key)

        if (point == null) {
            point = ShipLegacyPathPoint(x, y, z, key)
            this.pointMap.put(key, point)
        }

        return point
    }

    private fun createEntityPath(start: ShipLegacyPathPoint?, end: ShipLegacyPathPoint): ShipLegacyPath {
        val path: MutableList<ShipLegacyPathPoint?> = ArrayList<ShipLegacyPathPoint?>()
        var node = end

        path.add(end)

        while (node.getPrevious() != null && node != start) {
            node = node.getPrevious()
            path.add(0, node)
        }

        return ShipLegacyPath(path.toTypedArray<ShipLegacyPathPoint?>())
    }

    companion object {
        private const val MAX_DESCEND_SCAN = 64
        private const val MAX_SEARCH_ITERS = 4096
        private const val MAX_PATH_OPTIONS = 32

        private fun isInLiquid(entity: Entity): Boolean {
            return entity.isInWaterOrBubble() || entity.isInLava()
        }
    }
}
