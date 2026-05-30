package org.trp.shincolle.entity.base;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.Config;
import org.trp.shincolle.entity.base.path.ShipLegacyNavigation;
import org.trp.shincolle.utility.ShipTeleportHelper;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class ShipMovementCoordinator {
    public static final int PRIORITY_BACKGROUND = 0;
    public static final int PRIORITY_NORMAL = 10;
    public static final int PRIORITY_TASK = 15;
    public static final int PRIORITY_COMBAT = 20;
    public static final int PRIORITY_FOLLOW = 25;
    public static final int PRIORITY_COMMAND = 30;
    public static final int PRIORITY_EMERGENCY = 40;

    private static final double SAME_POINT_MOVE_TARGET_SQR = 0.25D;
    private static final double SAME_ENTITY_MOVE_TARGET_SQR = 2.25D;
    private static final int SAME_MOVE_REFRESH_INTERVAL_TICKS = 20;
    private static final Map<PathfinderMob, NavigationOwner> ACTIVE_NAVIGATION_OWNERS =
            Collections.synchronizedMap(new WeakHashMap<>());

    private final PathfinderMob mob;
    private final Object ownerToken = new Object();
    private final int priority;
    private Vec3 lastMoveTarget;
    private int lastMoveTick = Integer.MIN_VALUE;

    public ShipMovementCoordinator(PathfinderMob mob) {
        this(mob, PRIORITY_NORMAL);
    }

    public ShipMovementCoordinator(PathfinderMob mob, int priority) {
        this.mob = mob;
        this.priority = priority;
    }

    public void reset() {
        this.lastMoveTarget = null;
        this.lastMoveTick = Integer.MIN_VALUE;
    }

    public void stop() {
        reset();
        if (!ownsNavigation()) {
            return;
        }
        Shincolle.debugLog("[SCMoveDiag] MovementCoordinator stop mob={} priority={} owner={}",
                this.mob.getUUID(), this.priority, this.ownerToken.hashCode());
        clearAnyNavigationOwner();
        mob.getNavigation().stop();
    }

    public void stopAny() {
        reset();
        Shincolle.debugLog("[SCMoveDiag] MovementCoordinator stopAny mob={} priority={} owner={}",
                this.mob.getUUID(), this.priority, this.ownerToken.hashCode());
        clearAnyNavigationOwner();
        mob.getNavigation().stop();
    }

    public boolean moveTo(Vec3 target, double speed) {
        if (shouldSuppressSameTargetMove(target, SAME_POINT_MOVE_TARGET_SQR)) {
            Shincolle.debugLog("[SCMoveDiag] MovementCoordinator suppressPoint mob={} priority={} target={} speed={}",
                    this.mob.getUUID(), this.priority, target, speed);
            return true;
        }
        if (shouldYieldToHigherPriorityOwner()) {
            Shincolle.debugLog("[SCMoveDiag] MovementCoordinator yieldPoint mob={} priority={} target={} speed={}",
                    this.mob.getUUID(), this.priority, target, speed);
            return true;
        }

        preserveForeignNavigationOnMoveFailure();
        return recordMoveRequest(target, mob.getNavigation().moveTo(target.x, target.y, target.z, speed));
    }

    public boolean moveTo(Entity target, double speed) {
        Vec3 targetPos = target.position();
        if (shouldSuppressSameTargetMove(targetPos, SAME_ENTITY_MOVE_TARGET_SQR)) {
            Shincolle.debugLog("[SCMoveDiag] MovementCoordinator suppressEntity mob={} priority={} target={} speed={}",
                    this.mob.getUUID(), this.priority, target.getUUID(), speed);
            return true;
        }
        if (shouldYieldToHigherPriorityOwner()) {
            Shincolle.debugLog("[SCMoveDiag] MovementCoordinator yieldEntity mob={} priority={} target={} speed={}",
                    this.mob.getUUID(), this.priority, target.getUUID(), speed);
            return true;
        }

        preserveForeignNavigationOnMoveFailure();
        return recordMoveRequest(targetPos, mob.getNavigation().moveTo(target, speed));
    }

    public boolean isNavigationDone() {
        return mob.getNavigation().isDone();
    }

    private boolean shouldSuppressSameTargetMove(Vec3 target, double sameTargetSqr) {
        return !mob.getNavigation().isDone()
                && ownsNavigation()
                && this.lastMoveTarget != null
                && this.lastMoveTarget.distanceToSqr(target) < sameTargetSqr
                && this.mob.tickCount - this.lastMoveTick < SAME_MOVE_REFRESH_INTERVAL_TICKS;
    }

    private boolean recordMoveRequest(Vec3 target, boolean moved) {
        if (!moved) {
            Shincolle.diagnosticLog("[SCMoveDiag] moveFailed mob={} priority={} target={}",
                    this.mob.getUUID(), this.priority, target);
            stopAfterFailedMove();
            return false;
        }

        claimNavigation();
        this.lastMoveTarget = target;
        this.lastMoveTick = this.mob.tickCount;
        Shincolle.diagnosticLog("[SCMoveDiag] moveOk mob={} priority={} target={} tick={}",
                this.mob.getUUID(), this.priority, target, this.lastMoveTick);
        return true;
    }

    private void stopAfterFailedMove() {
        NavigationOwner owner = activeNavigationOwner();
        if (owner == null || owner.token() == this.ownerToken || owner.priority() <= this.priority) {
            stopAny();
            return;
        }

        reset();
        if (mob.getNavigation().isDone()) {
            clearAnyNavigationOwner();
        }
    }

    private void preserveForeignNavigationOnMoveFailure() {
        if (!hasForeignHigherPriorityNavigationOwner()) {
            return;
        }
        if (mob.getNavigation() instanceof ShipLegacyNavigation navigation) {
            navigation.preserveCurrentPathOnNextFailure();
        }
    }

    private boolean shouldYieldToHigherPriorityOwner() {
        return hasForeignHigherPriorityNavigationOwner();
    }

    public boolean teleportNearLiving(LivingEntity anchor, double verticalOffset) {
        if (!Config.canTeleport || !isAnchorChunkLoaded(anchor.position())) {
            return false;
        }

        return teleportNearLivingIgnoringConfig(anchor, verticalOffset);
    }

    public boolean teleportNearLivingIgnoringConfig(LivingEntity anchor, double verticalOffset) {
        if (!isAnchorChunkLoaded(anchor.position())) {
            return false;
        }

        stopAny();
        return ShipTeleportHelper.teleportNearLiving(mob, anchor, verticalOffset);
    }

    public boolean teleportNearPoint(Vec3 anchor, double verticalOffset) {
        if (!Config.canTeleport || !isAnchorChunkLoaded(anchor)) {
            return false;
        }

        stopAny();
        return ShipTeleportHelper.teleportNearPoint(mob, anchor, verticalOffset);
    }

    private boolean ownsNavigation() {
        NavigationOwner owner = activeNavigationOwner();
        return owner != null && owner.token() == this.ownerToken;
    }

    private boolean hasNavigationOwner() {
        return activeNavigationOwner() != null;
    }

    private boolean hasForeignHigherPriorityNavigationOwner() {
        if (mob.getNavigation().isDone()) {
            return false;
        }
        NavigationOwner owner = activeNavigationOwner();
        return owner != null && owner.token() != this.ownerToken && owner.priority() > this.priority;
    }

    private NavigationOwner activeNavigationOwner() {
        synchronized (ACTIVE_NAVIGATION_OWNERS) {
            return ACTIVE_NAVIGATION_OWNERS.get(this.mob);
        }
    }

    private void claimNavigation() {
        synchronized (ACTIVE_NAVIGATION_OWNERS) {
            ACTIVE_NAVIGATION_OWNERS.put(this.mob, new NavigationOwner(this.ownerToken, this.priority));
        }
    }

    private void clearAnyNavigationOwner() {
        synchronized (ACTIVE_NAVIGATION_OWNERS) {
            ACTIVE_NAVIGATION_OWNERS.remove(this.mob);
        }
    }

    private record NavigationOwner(Object token, int priority) {
    }

    private boolean isAnchorChunkLoaded(Vec3 anchor) {
        if (!(mob.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return false;
        }

        int cx = Mth.floor(anchor.x) >> 4;
        int cz = Mth.floor(anchor.z) >> 4;
        return serverLevel.hasChunk(cx, cz);
    }
}
