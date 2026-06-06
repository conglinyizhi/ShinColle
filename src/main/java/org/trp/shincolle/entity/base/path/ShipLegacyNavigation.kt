package org.trp.shincolle.entity.base.path

import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.Direction
import net.minecraft.tags.BlockTags
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.LadderBlock
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.Config
import org.trp.shincolle.Shincolle.Companion.diagnosticLog
import org.trp.shincolle.entity.base.EntityShipBase
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class ShipLegacyNavigation(mob: Mob, level: Level) : GroundPathNavigation(mob, level) {
    // --- Core navigation state ---
    private val pathFinder: ShipLegacyPathFinder
    private val maxDistanceToWaypoint: Float
    private val hostCeilWidth: Int
    private val hostCeilHeight: Int
    private val hostCeilDepth: Int
    private var currentPath: ShipLegacyPath? = null
    private var speedModifier = 0.0
    private var targetPos: BlockPos? = null

    // --- Time keeping ---
    private var totalTicks = 0

    // --- Stuck detection state ---
    private var ticksAtLastPos = 0
    private var lastPosCheck: Vec3 = Vec3.ZERO
    private var lastPosStuck: Vec3 = Vec3.ZERO

    // --- Path timeout state ---
    private var timeoutCachedNode: Long = 0
    private var timeoutTimer: Long = 0
    private var timeoutLimit = 0.0

    // --- Log throttling state ---
    private var loggedTargetPos: BlockPos? = null
    private var lastExceededLogTarget: BlockPos? = null
    private var lastStuckApplyLogTarget: BlockPos? = null
    private var loggedPathFailure = false
    private var loggedPathLength = -1
    private var lastSetPathLogTick = Int.MIN_VALUE
    private var lastExceededLogTick = Int.MIN_VALUE
    private var lastStuckApplyLogTick = Int.MIN_VALUE

    // --- Transient navigation flags ---
    private var preserveCurrentPathOnNextFailure = false
    private var inertiaTicks = 0

    // ========================================================================
    // Construction
    // ========================================================================
    init {
        this.pathFinder = ShipLegacyPathFinder(level, false)
        this.hostCeilWidth = Mth.floor(mob.bbWidth + 1.0f)
        this.hostCeilHeight = Mth.floor(mob.bbHeight + 1.0f)
        this.hostCeilDepth = this.hostCeilWidth
        this.maxDistanceToWaypoint =
            if (mob.bbWidth > 0.75f) mob.bbWidth * 0.5f else 0.75f - mob.bbWidth * 0.5f
    }

    // ========================================================================
    // Public API — move / stop / query
    // ========================================================================
    override fun moveTo(x: Double, y: Double, z: Double, speed: Double): Boolean {
        if (!canStartMove()) return false
        val pos = BlockPos.containing(x, y, z)
        val previousTarget = this.targetPos
        val path = getPathToPos(pos)
        return setPath(path, speed, isSameNavigationTarget(previousTarget, pos), pos)
    }

    override fun moveTo(entity: Entity, speed: Double): Boolean {
        if (!canStartMove()) return false
        val previousTarget = this.targetPos
        val nextTarget = entity.blockPosition()
        val path = getPathToEntity(entity)
        return setPath(path, speed, isSameNavigationTarget(previousTarget, nextTarget), nextTarget)
    }

    override fun stop() {
        this.preserveCurrentPathOnNextFailure = false
        this.currentPath = null
        this.path = null
    }

    override fun isDone(): Boolean {
        return noPath()
    }

    fun noPath(): Boolean {
        return this.currentPath == null || this.currentPath!!.isFinished
    }

    fun preserveCurrentPathOnNextFailure() {
        this.preserveCurrentPathOnNextFailure = true
    }

    // ========================================================================
    // Tick — main loop
    // ========================================================================
    override fun tick() {
        this.totalTicks++

        if (noPath()) {
            tickInertia()
            return
        }
        this.inertiaTicks = 0

        if (this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED) <= 1.0E-4) {
            stop()
            return
        }

        if (canNavigate()) {
            pathFollow()
        }

        if (noPath()) {
            return
        }

        val target = this.currentPath!!.getPosition(this.mob)
        if (target == null) {
            stop()
            return
        }

        val wantedY = resolveTargetY(target)
        this.mob.moveControl.setWantedPosition(target.x, wantedY, target.z, this.speedModifier)
        logNavigationTick(target, wantedY)
    }

    // ========================================================================
    // Path following
    // ========================================================================
    private fun pathFollow() {
        val hostPos = this.entityPosition
        val nextPos = this.currentPath!!.currentPos
        if (nextPos == null) {
            stop()
            return
        }
        val centerX = nextPos.x + 0.5
        val centerZ = nextPos.z + 0.5
        val reach = this.maxDistanceToWaypoint + min(1.25, this.speedModifier * 0.35)
        val dx = this.mob.x - centerX
        val dz = this.mob.z - centerZ

        if (dx * dx + dz * dz <= reach * reach) {
            this.currentPath!!.incrementPathIndex()
        }
        checkForStuck(hostPos)
    }

    // ========================================================================
    // Navigation guards
    // ========================================================================
    private fun canStartMove(): Boolean {
        if (this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED) <= 1.0E-4) {
            this.preserveCurrentPathOnNextFailure = false
            return false
        }
        if (frozenByGui()) return false
        if (!canNavigate()) {
            this.preserveCurrentPathOnNextFailure = false
            return false
        }
        return true
    }

    private fun canNavigate(): Boolean {
        return !this.mob.isPassenger
                && (this.mob.onGround() || this.isInLiquid || this.mob.isNoGravity
                || this.mob.fallDistance < 2.0f)
    }

    private fun frozenByGui(): Boolean {
        if (!Config.SHIP_FREEZE_WHEN_GUI_OPEN.get()) return false
        val ship = this.mob as? EntityShipBase ?: return false
        return ship.isGuiOpen
    }

    // ========================================================================
    // Inertia — brief coast after path ends
    // ========================================================================
    private fun tickInertia() {
        if (this.targetPos != null && this.inertiaTicks < INERTIA_TICKS) {
            this.inertiaTicks++
            val lastTarget = Vec3.atCenterOf(this.targetPos)
            this.mob.moveControl.setWantedPosition(
                lastTarget.x, lastTarget.y, lastTarget.z, this.speedModifier
            )
        }
    }

    // ========================================================================
    // Stuck detection & recovery
    // ========================================================================
    private fun checkForStuck(hostPos: Vec3) {
        val progressDistanceSqr = hostPos.distanceToSqr(this.lastPosStuck)
        if (progressDistanceSqr >= STUCK_DISTANCE_SQR) {
            this.lastPosStuck = hostPos
            this.ticksAtLastPos = this.totalTicks
            checkPathTimeout(hostPos)
            return
        }

        val stationaryTicks = this.totalTicks - this.ticksAtLastPos

        if (stationaryTicks > STUCK_CHECK_INTERVAL && shouldLogExceededCheck()) {
            logExceededCheck(hostPos)
        }

        if (stationaryTicks > STUCK_MAX_TICKS && this.currentPath != null) {
            logStuckApply(hostPos)
            applyUnstuckMotion(hostPos)
            this.lastPosStuck = hostPos
            this.ticksAtLastPos = this.totalTicks
            resetPathTimeoutState()
        }

        checkPathTimeout(hostPos)
    }

    private fun applyUnstuckMotion(hostPos: Vec3) {
        val targetPos = this.currentPath!!.getPosition(this.mob)
        if (targetPos == null) return

        val dx = targetPos.x - hostPos.x
        val dz = targetPos.z - hostPos.z
        val lengthSq = dx * dx + dz * dz
        if (lengthSq < 1.0E-6) return

        val length = sqrt(lengthSq)
        val motion = this.mob.deltaMovement
        this.mob.deltaMovement = Vec3(
            dx / length * UNSTUCK_DIRECTION_FACTOR * this.speedModifier,
            motion.y,
            dz / length * UNSTUCK_DIRECTION_FACTOR * this.speedModifier
        )

        if (this.mob.random.nextBoolean()) {
            this.mob.jumpControl.jump()
            val bonus: Float = this.mob.speed * JUMP_SPRINT_FACTOR
            this.mob.deltaMovement = Vec3(
                this.mob.deltaMovement.x + dx / length * bonus,
                this.mob.deltaMovement.y,
                this.mob.deltaMovement.z + dz / length * bonus
            )
        }
    }

    // ========================================================================
    // Path timeout — retry when stuck on same node too long
    // ========================================================================
    private fun checkPathTimeout(hostPos: Vec3) {
        if (this.currentPath == null || this.currentPath!!.isFinished) {
            this.timeoutTimer = 0L
            return
        }

        if (hostPos.distanceToSqr(this.lastPosCheck) < POSITION_TIMEOUT_MOVED_SQR) {
            if (this.timeoutCachedNode == this.currentPath!!.currentPathIndex.toLong()) {
                this.timeoutTimer++
                if (this.timeoutLimit <= 0.0) {
                    val pathPos = this.currentPath!!.getPosition(this.mob)
                    if (pathPos != null) {
                        val dist = hostPos.distanceTo(pathPos)
                        this.timeoutLimit =
                            ShipLegacyNavigationPolicy.calculateTimeoutLimit(dist, this.mob.speed.toDouble())
                    }
                }
                if (ShipLegacyNavigationPolicy.shouldRetryTimedOutPath(this.timeoutTimer, this.timeoutLimit)) {
                    val retryPath = recalculatePathToCurrentTarget()
                    if (!setPath(retryPath, this.speedModifier, true, this.targetPos)) {
                        stop()
                    }
                    resetPathTimeoutState()
                }
            } else {
                resetPathTimeoutStateTo(this.currentPath!!.currentPathIndex.toLong())
            }
        } else {
            resetPathTimeoutStateTo(this.currentPath!!.currentPathIndex.toLong())
        }
        this.lastPosCheck = hostPos
    }

    // ========================================================================
    // Path resolution
    // ========================================================================
    private fun getPathToEntity(target: Entity): ShipLegacyPath? {
        if (this.currentPath != null && !this.currentPath!!.isFinished) {
            val finalPoint = this.currentPath!!.finalPathPoint
            if (finalPoint != null && finalPoint.x == target.blockPosition()
                    .x && finalPoint.y == target.blockPosition()
                    .y && finalPoint.z == target.blockPosition().z
            ) {
                return this.currentPath
            }
        }
        val range = this.pathSearchRange
        return this.pathFinder.findPath(this.mob, target, range)
    }

    private fun getPathToPos(pos: BlockPos): ShipLegacyPath? {
        if (this.currentPath != null && !this.currentPath!!.isFinished && pos == this.targetPos) {
            return this.currentPath
        }
        val range = this.pathSearchRange
        return this.pathFinder.findPath(this.mob, pos.x, pos.y, pos.z, range)
    }

    private fun recalculatePathToCurrentTarget(): ShipLegacyPath? {
        if (this.targetPos == null) return null
        val range = this.pathSearchRange
        return this.pathFinder.findPath(
            this.mob,
            this.targetPos!!.x,
            this.targetPos!!.y,
            this.targetPos!!.z,
            range
        )
    }

    private val pathSearchRange: Float
        get() {
            val follow =
                this.mob.getAttributeValue(Attributes.FOLLOW_RANGE)
            return max(48.0, follow).toFloat()
        }

    // ========================================================================
    // Path assignment
    // ========================================================================
    private fun setPath(
        path: ShipLegacyPath?,
        speed: Double,
        sameNavigationTarget: Boolean,
        nextTarget: BlockPos?
    ): Boolean {
        val hadActivePath = this.currentPath != null && !this.currentPath!!.isFinished

        if (path == null) {
            if (shouldLogSetPath(-1, true, nextTarget)) {
                logSetPath(nextTarget, true, -1)
            }
            if (!this.preserveCurrentPathOnNextFailure) {
                this.currentPath = null
                this.path = null
                this.targetPos = nextTarget
            }
            this.preserveCurrentPathOnNextFailure = false
            return false
        }

        this.preserveCurrentPathOnNextFailure = false
        this.targetPos = nextTarget
        if (path == this.currentPath) {
            this.speedModifier = speed
            return true
        }

        this.currentPath = path
        this.path = null
        this.speedModifier = speed

        val hostPos = this.entityPosition
        if (ShipLegacyNavigationPolicy.shouldResetStuckProgress(hadActivePath, sameNavigationTarget)) {
            resetStuckProgressState(hostPos)
        }
        resetPathTimeoutState()
        logSetPath(nextTarget, false, path.currentPathLength)
        return true
    }

    private val entityPosition: Vec3
        // ========================================================================
        get() = Vec3(this.mob.x, this.pathableYPos.toDouble(), this.mob.z)

    private val pathableYPos: Int
        get() {
            if (this.mob.isInWater || this.mob.isInLava) {
                val pos = this.mob.blockPosition().mutable()
                var y = pos.y
                var scan = 0
                while (scan++ < 16 && y < this.level.maxBuildHeight && !this.level.getFluidState(pos).isEmpty) {
                    y++
                    pos.setY(y)
                }
                return y
            }
            return Mth.floor(this.mob.y + 0.5)
        }

    private fun resolveTargetY(target: Vec3): Double {
        if (this.isInLiquid) {
            return Mth.lerp(0.4, this.mob.y, getLiquidHoverY(target))
        }
        val pos = BlockPos.containing(target.x, target.y - 0.5, target.z)
        val state = this.level.getBlockState(pos)
        val maxY = state.getCollisionShape(this.level, pos).max(Direction.Axis.Y)
        if (!java.lang.Double.isInfinite(maxY) && !java.lang.Double.isNaN(maxY)) {
            return pos.y + maxY + 0.1
        }
        return target.y
    }

    private fun getLiquidHoverY(target: Vec3): Double {
        var pos = BlockPos.containing(target.x, target.y, target.z)
        var fluid = this.level.getFluidState(pos)
        if (fluid.isEmpty) {
            val below = pos.below()
            fluid = this.level.getFluidState(below)
            if (fluid.isEmpty) return target.y
            pos = below
        }
        return pos.y + fluid.getHeight(this.level, pos) - LIQUID_HOVER_OFFSET
    }

    private val isInLiquid: Boolean
        get() = this.mob.isInWaterOrBubble || this.mob.isInLava

    // ========================================================================
    // Direct path check
    // ========================================================================
    private fun isDirectPathBetweenPoints(from: Vec3, to: Vec3, sizeX: Int, sizeY: Int, sizeZ: Int): Boolean {
        var x1 = Mth.floor(from.x)
        var y1 = from.y.toInt()
        var z1 = Mth.floor(from.z)
        var dx = to.x - from.x
        var dy = to.y - from.y
        var dz = to.z - from.z
        val offsetSq = dx * dx + dy * dy + dz * dz
        if (offsetSq < 1.0E-8) return false

        val invDist = 1.0 / sqrt(offsetSq)
        dx *= invDist
        dy *= invDist
        dz *= invDist

        if (!this.isSafeToStandAt(x1, y1, z1, sizeX + 2, sizeY + 1, sizeZ + 2, from, dx, dz)) return false

        val unitX = 1.0 / abs(dx)
        val unitY = 1.0 / abs(dy)
        val unitZ = 1.0 / abs(dz)
        var proX = x1 - from.x
        var proY = y1 - from.y
        var proZ = z1 - from.z
        if (dx >= 0.0) proX += 1.0
        if (dy >= 0.0) proY += 1.0
        if (dz >= 0.0) proZ += 1.0
        proX /= dx
        proY /= dy
        proZ /= dz
        val dirX = if (dx < 0.0) -1 else 1
        val dirY = if (dy < 0.0) -1 else 1
        val dirZ = if (dz < 0.0) -1 else 1
        val x2 = Mth.floor(to.x)
        val y2 = Mth.floor(to.y)
        val z2 = Mth.floor(to.z)
        var xIntOffset = x2 - x1
        var yIntOffset = y2 - y1
        var zIntOffset = z2 - z1

        while (xIntOffset * dirX > 0 || yIntOffset * dirY > 0 || zIntOffset * dirZ > 0) {
            if (proX < proY && proX < proZ) {
                proX += unitX
                x1 += dirX
                xIntOffset = x2 - x1
            } else if (proY < proX && proY < proZ) {
                proY += unitY
                y1 += dirY
                yIntOffset = y2 - y1
            } else {
                proZ += unitZ
                z1 += dirZ
                zIntOffset = z2 - z1
            }
            if (!this.isSafeToStandAt(x1, y1, z1, sizeX, sizeY, sizeZ, from, dx, dz)) return false
        }
        return true
    }

    private fun isSafeToStandAt(
        xOffset: Int,
        yOffset: Int,
        zOffset: Int,
        xSize: Int,
        ySize: Int,
        zSize: Int,
        orgPos: Vec3,
        vecX: Double,
        vecZ: Double
    ): Boolean {
        val xSize2 = xOffset - xSize / 2
        val zSize2 = zOffset - zSize / 2
        if (!this.isPositionClear(xSize2, yOffset, zSize2, xSize, ySize, zSize, orgPos, vecX, vecZ)) return false
        if (this.isInLiquid) return true

        val pos = MutableBlockPos()
        for (x1 in xSize2..<xSize2 + xSize) {
            for (z1 in zSize2..<zSize2 + zSize) {
                val x2 = x1 + 0.5 - orgPos.x
                val z2 = z1 + 0.5 - orgPos.z
                if (x2 * vecX + z2 * vecZ < 0.0) continue
                pos.set(x1, yOffset - 1, z1)
                if (this.level.getBlockState(pos).isAir) return false
            }
        }
        return true
    }

    private fun isPositionClear(
        x: Int,
        y: Int,
        z: Int,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        from: Vec3,
        dirX: Double,
        dirZ: Double
    ): Boolean {
        val pos = MutableBlockPos()
        for (ix in x..<x + sizeX) {
            for (iy in y..<y + sizeY) {
                for (iz in z..<z + sizeZ) {
                    val deltaX = ix + 0.5 - from.x
                    val deltaZ = iz + 0.5 - from.z
                    if (deltaX * dirX + deltaZ * dirZ < 0.0) continue
                    pos.set(ix, iy, iz)
                    val state = this.level.getBlockState(pos)
                    if (state.block is StairBlock
                        || state.block is LadderBlock
                        || state.`is`(BlockTags.CLIMBABLE)
                    ) return false
                    if (!state.getCollisionShape(this.level, pos).isEmpty) return false
                }
            }
        }
        return true
    }

    // ========================================================================
    // Policy delegates
    // ========================================================================
    private fun isSameNavigationTarget(previousTarget: BlockPos?, nextTarget: BlockPos?): Boolean {
        return ShipLegacyNavigationPolicy.isSameNavigationTarget(policyTarget(previousTarget), policyTarget(nextTarget))
    }

    private fun shouldLogSetPath(pathLength: Int, failure: Boolean, logTarget: BlockPos?): Boolean {
        return ShipLegacyNavigationPolicy.shouldLogSetPath(
            this.loggedPathFailure, failure,
            policyTarget(this.loggedTargetPos), policyTarget(logTarget),
            this.totalTicks, this.lastSetPathLogTick
        )
    }

    private fun shouldLogExceededCheck(): Boolean {
        return ShipLegacyNavigationPolicy.shouldLogNavigationEvent(
            policyTarget(this.lastExceededLogTarget), policyTarget(this.targetPos),
            this.totalTicks, this.lastExceededLogTick
        )
    }

    private fun shouldLogStuckApply(): Boolean {
        return ShipLegacyNavigationPolicy.shouldLogNavigationEvent(
            policyTarget(this.lastStuckApplyLogTarget), policyTarget(this.targetPos),
            this.totalTicks, this.lastStuckApplyLogTick
        )
    }

    // ========================================================================
    // State transitions
    // ========================================================================
    private fun resetStuckProgressState(hostPos: Vec3) {
        this.ticksAtLastPos = this.totalTicks
        this.lastPosCheck = hostPos
        this.lastPosStuck = hostPos
    }

    private fun resetPathTimeoutState() {
        this.timeoutCachedNode = 0L
        this.timeoutTimer = 0L
        this.timeoutLimit = 0.0
    }

    private fun resetPathTimeoutStateTo(nodeIndex: Long) {
        this.timeoutCachedNode = nodeIndex
        this.timeoutTimer = 0L
        this.timeoutLimit = 0.0
    }

    // ========================================================================
    // Logging
    // ========================================================================
    private fun logNavigationTick(target: Vec3, wantedY: Double) {
        if (this.totalTicks % 20 != 0) return
        val pos = this.mob.position()
        val distToTarget = pos.distanceTo(Vec3(target.x, wantedY, target.z))
        val speed = this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)
        diagnosticLog(
            "[SCNaviTick] mob={} speedMod={} movSpeedAttr={} distToNode={} pathIdx={}/{}",
            this.mob.uuid, this.speedModifier, speed, distToTarget,
            this.currentPath!!.currentPathIndex, this.currentPath!!.currentPathLength
        )
    }

    private fun logSetPath(nextTarget: BlockPos?, failure: Boolean, pathLength: Int) {
        if (failure) {
            diagnosticLog("[SCNavDiag] setPath failed mob={} targetPos={}", this.mob.uuid, nextTarget)
        } else {
            diagnosticLog(
                "[SCNavDiag] setPath success mob={} targetPos={} speed={} pathLength={}",
                this.mob.uuid, nextTarget, this.speedModifier, pathLength
            )
        }
        this.loggedTargetPos = nextTarget
        this.loggedPathFailure = failure
        this.loggedPathLength = pathLength
        this.lastSetPathLogTick = this.totalTicks
    }

    private fun logExceededCheck(hostPos: Vec3?) {
        diagnosticLog(
            "[SCNavDiag] exceededCheck mob={} pos={} targetPos={}",
            this.mob.uuid, hostPos, this.targetPos
        )
        this.lastExceededLogTarget = this.targetPos
        this.lastExceededLogTick = this.totalTicks
    }

    private fun logStuckApply(hostPos: Vec3?) {
        if (!shouldLogStuckApply()) return
        diagnosticLog(
            "[SCNavDiag] stuckApply mob={} pos={} targetPos={}",
            this.mob.uuid, hostPos, this.targetPos
        )
        this.lastStuckApplyLogTarget = this.targetPos
        this.lastStuckApplyLogTick = this.totalTicks
    }

    companion object {
        // --- Constants ---
        private const val STUCK_CHECK_INTERVAL = 32
        private const val STUCK_MAX_TICKS = 100
        private const val STUCK_DISTANCE_SQR = 1.0
        private const val POSITION_TIMEOUT_MOVED_SQR = 0.25
        private const val JUMP_SPRINT_FACTOR = 0.35f
        private const val UNSTUCK_DIRECTION_FACTOR = 0.5f
        private const val LIQUID_HOVER_OFFSET = 0.08
        private const val INERTIA_TICKS = 10

        private fun policyTarget(target: BlockPos?): ShipLegacyNavigationPolicy.Target? {
            if (target == null) return null
            return ShipLegacyNavigationPolicy.Target(target.x, target.y, target.z)
        }
    }
}
