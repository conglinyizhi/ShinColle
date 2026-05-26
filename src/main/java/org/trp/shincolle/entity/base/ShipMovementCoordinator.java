package org.trp.shincolle.entity.base;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.trp.shincolle.Config;
import org.trp.shincolle.utility.ShipTeleportHelper;

public final class ShipMovementCoordinator {
    private static final double SAME_MOVE_TARGET_SQR = 0.25D;
    private static final int SAME_MOVE_REFRESH_INTERVAL_TICKS = 20;

    private final PathfinderMob mob;
    private Vec3 lastMoveTarget;
    private int lastMoveTick = Integer.MIN_VALUE;

    public ShipMovementCoordinator(PathfinderMob mob) {
        this.mob = mob;
    }

    public void reset() {
        this.lastMoveTarget = null;
        this.lastMoveTick = Integer.MIN_VALUE;
    }

    public void stop() {
        reset();
        mob.getNavigation().stop();
    }

    public boolean moveTo(Vec3 target, double speed) {
        if (shouldSuppressSameTargetMove(target)) {
            return true;
        }

        return recordMoveRequest(target, mob.getNavigation().moveTo(target.x, target.y, target.z, speed));
    }

    public boolean moveTo(Entity target, double speed) {
        Vec3 targetPos = target.position();
        if (shouldSuppressSameTargetMove(targetPos)) {
            return true;
        }

        return recordMoveRequest(targetPos, mob.getNavigation().moveTo(target, speed));
    }

    private boolean shouldSuppressSameTargetMove(Vec3 target) {
        return !mob.getNavigation().isDone()
                && this.lastMoveTarget != null
                && this.lastMoveTarget.distanceToSqr(target) < SAME_MOVE_TARGET_SQR
                && this.mob.tickCount - this.lastMoveTick < SAME_MOVE_REFRESH_INTERVAL_TICKS;
    }

    private boolean recordMoveRequest(Vec3 target, boolean moved) {
        if (!moved) {
            stop();
            return false;
        }

        this.lastMoveTarget = target;
        this.lastMoveTick = this.mob.tickCount;
        return true;
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

        stop();
        return ShipTeleportHelper.teleportNearLiving(mob, anchor, verticalOffset);
    }

    public boolean teleportNearPoint(Vec3 anchor, double verticalOffset) {
        if (!Config.canTeleport || !isAnchorChunkLoaded(anchor)) {
            return false;
        }

        stop();
        return ShipTeleportHelper.teleportNearPoint(mob, anchor, verticalOffset);
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
