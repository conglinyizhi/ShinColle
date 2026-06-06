package org.trp.shincolle.entity.base

import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.Config
import org.trp.shincolle.Shincolle.Companion.debugLog
import org.trp.shincolle.Shincolle.Companion.diagnosticLog
import org.trp.shincolle.entity.base.path.ShipLegacyNavigation
import org.trp.shincolle.utility.ShipTeleportHelper.teleportNearLiving
import org.trp.shincolle.utility.ShipTeleportHelper.teleportNearPoint
import java.util.*

class ShipMovementCoordinator @JvmOverloads constructor(
    private val mob: PathfinderMob,
    private val priority: Int = PRIORITY_NORMAL
) {
    private val ownerToken = Any()
    private var lastMoveTarget: Vec3? = null
    private var lastMoveTick = Int.MIN_VALUE

    fun reset() {
        this.lastMoveTarget = null
        this.lastMoveTick = Int.MIN_VALUE
    }

    fun stop() {
        reset()
        if (!ownsNavigation()) {
            return
        }
        debugLog(
            "[SCMoveDiag] MovementCoordinator stop mob={} priority={} owner={}",
            this.mob.uuid, this.priority, this.ownerToken.hashCode()
        )
        clearAnyNavigationOwner()
        mob.navigation.stop()
    }

    fun stopAny() {
        reset()
        debugLog(
            "[SCMoveDiag] MovementCoordinator stopAny mob={} priority={} owner={}",
            this.mob.uuid, this.priority, this.ownerToken.hashCode()
        )
        clearAnyNavigationOwner()
        mob.navigation.stop()
    }

    fun moveTo(target: Vec3, speed: Double): Boolean {
        if (shouldSuppressSameTargetMove(target, SAME_POINT_MOVE_TARGET_SQR)) {
            debugLog(
                "[SCMoveDiag] MovementCoordinator suppressPoint mob={} priority={} target={} speed={}",
                this.mob.uuid, this.priority, target, speed
            )
            return true
        }
        if (shouldYieldToHigherPriorityOwner()) {
            debugLog(
                "[SCMoveDiag] MovementCoordinator yieldPoint mob={} priority={} target={} speed={}",
                this.mob.uuid, this.priority, target, speed
            )
            return true
        }

        preserveForeignNavigationOnMoveFailure()
        return recordMoveRequest(target, mob.navigation.moveTo(target.x, target.y, target.z, speed))
    }

    fun moveTo(target: Entity, speed: Double): Boolean {
        val targetPos = target.position()
        if (shouldSuppressSameTargetMove(targetPos, SAME_ENTITY_MOVE_TARGET_SQR)) {
            debugLog(
                "[SCMoveDiag] MovementCoordinator suppressEntity mob={} priority={} target={} speed={}",
                this.mob.uuid, this.priority, target.uuid, speed
            )
            return true
        }
        if (shouldYieldToHigherPriorityOwner()) {
            debugLog(
                "[SCMoveDiag] MovementCoordinator yieldEntity mob={} priority={} target={} speed={}",
                this.mob.uuid, this.priority, target.uuid, speed
            )
            return true
        }

        preserveForeignNavigationOnMoveFailure()
        return recordMoveRequest(targetPos, mob.navigation.moveTo(target, speed))
    }

    val isNavigationDone: Boolean
        get() = mob.navigation.isDone()

    private fun shouldSuppressSameTargetMove(target: Vec3, sameTargetSqr: Double): Boolean {
        return !mob.navigation.isDone() && ownsNavigation()
                && this.lastMoveTarget != null && this.lastMoveTarget!!.distanceToSqr(target) < sameTargetSqr && this.mob.tickCount - this.lastMoveTick < SAME_MOVE_REFRESH_INTERVAL_TICKS
    }

    private fun recordMoveRequest(target: Vec3?, moved: Boolean): Boolean {
        if (!moved) {
            diagnosticLog(
                "[SCMoveDiag] moveFailed mob={} priority={} target={}",
                this.mob.uuid, this.priority, target
            )
            stopAfterFailedMove()
            return false
        }

        claimNavigation()
        this.lastMoveTarget = target
        this.lastMoveTick = this.mob.tickCount
        diagnosticLog(
            "[SCMoveDiag] moveOk mob={} priority={} target={} tick={}",
            this.mob.uuid, this.priority, target, this.lastMoveTick
        )
        return true
    }

    private fun stopAfterFailedMove() {
        val owner = activeNavigationOwner()
        if (owner == null || owner.token === this.ownerToken || owner.priority <= this.priority) {
            stopAny()
            return
        }

        reset()
        if (mob.navigation.isDone()) {
            clearAnyNavigationOwner()
        }
    }

    private fun preserveForeignNavigationOnMoveFailure() {
        if (!hasForeignHigherPriorityNavigationOwner()) {
            return
        }
        if (mob.navigation is ShipLegacyNavigation) {
            (mob.navigation as ShipLegacyNavigation).preserveCurrentPathOnNextFailure()
        }
    }

    private fun shouldYieldToHigherPriorityOwner(): Boolean {
        return hasForeignHigherPriorityNavigationOwner()
    }

    fun teleportNearLiving(anchor: LivingEntity, verticalOffset: Double): Boolean {
        if (!Config.canTeleport || !isAnchorChunkLoaded(anchor.position())) {
            return false
        }

        return teleportNearLivingIgnoringConfig(anchor, verticalOffset)
    }

    fun teleportNearLivingIgnoringConfig(anchor: LivingEntity, verticalOffset: Double): Boolean {
        if (!isAnchorChunkLoaded(anchor.position())) {
            return false
        }

        stopAny()
        return teleportNearLiving(mob, anchor, verticalOffset)
    }

    fun teleportNearPoint(anchor: Vec3, verticalOffset: Double): Boolean {
        if (!Config.canTeleport || !isAnchorChunkLoaded(anchor)) {
            return false
        }

        stopAny()
        return teleportNearPoint(mob, anchor, verticalOffset)
    }

    private fun ownsNavigation(): Boolean {
        val owner = activeNavigationOwner()
        return owner != null && owner.token === this.ownerToken
    }

    private fun hasNavigationOwner(): Boolean {
        return activeNavigationOwner() != null
    }

    private fun hasForeignHigherPriorityNavigationOwner(): Boolean {
        if (mob.navigation.isDone()) {
            return false
        }
        val owner = activeNavigationOwner()
        return owner != null && owner.token !== this.ownerToken && owner.priority > this.priority
    }

    private fun activeNavigationOwner(): NavigationOwner? {
        synchronized(ACTIVE_NAVIGATION_OWNERS) {
            return ACTIVE_NAVIGATION_OWNERS.get(this.mob)
        }
    }

    private fun claimNavigation() {
        synchronized(ACTIVE_NAVIGATION_OWNERS) {
            ACTIVE_NAVIGATION_OWNERS.put(this.mob, NavigationOwner(this.ownerToken, this.priority))
        }
    }

    private fun clearAnyNavigationOwner() {
        synchronized(ACTIVE_NAVIGATION_OWNERS) {
            ACTIVE_NAVIGATION_OWNERS.remove(this.mob)
        }
    }

    @JvmRecord
    private data class NavigationOwner(val token: Any?, val priority: Int)

    private fun isAnchorChunkLoaded(anchor: Vec3): Boolean {
        if (mob.level() !is ServerLevel) {
            return false
        }

        val cx = Mth.floor(anchor.x) shr 4
        val cz = Mth.floor(anchor.z) shr 4
        return (this.mob.level() as ServerLevel).hasChunk(cx, cz)
    }

    companion object {
        const val PRIORITY_BACKGROUND: Int = 0
        const val PRIORITY_NORMAL: Int = 10
        const val PRIORITY_TASK: Int = 15
        const val PRIORITY_COMBAT: Int = 20
        const val PRIORITY_FOLLOW: Int = 25
        const val PRIORITY_COMMAND: Int = 30
        const val PRIORITY_EMERGENCY: Int = 40

        private const val SAME_POINT_MOVE_TARGET_SQR = 0.25
        private const val SAME_ENTITY_MOVE_TARGET_SQR = 2.25
        private const val SAME_MOVE_REFRESH_INTERVAL_TICKS = 20
        private val ACTIVE_NAVIGATION_OWNERS: MutableMap<PathfinderMob?, NavigationOwner?> =
            Collections.synchronizedMap<PathfinderMob?, NavigationOwner?>(WeakHashMap<PathfinderMob?, NavigationOwner?>())
    }
}
