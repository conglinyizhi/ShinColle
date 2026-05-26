package org.trp.shincolle.entity.base;

import net.minecraft.world.phys.Vec3;

public final class ShipMovementRecoveryState {
    private static final double PROGRESS_DISTANCE_SQR = 0.04D;

    private int moveFailCount;
    private int stuckTicks;
    private int teleportCooldown;
    private int forcedTeleportCooldown;
    private Vec3 lastProgressPos;

    public void reset(Vec3 currentPos) {
        this.moveFailCount = 0;
        this.stuckTicks = 0;
        this.teleportCooldown = 0;
        this.forcedTeleportCooldown = 0;
        this.lastProgressPos = currentPos;
    }

    public void clear() {
        this.moveFailCount = 0;
        this.stuckTicks = 0;
        this.teleportCooldown = 0;
        this.forcedTeleportCooldown = 0;
        this.lastProgressPos = null;
    }

    public void trackProgress(Vec3 currentPos) {
        if (this.lastProgressPos == null) {
            this.lastProgressPos = currentPos;
            this.stuckTicks = 0;
            return;
        }

        if (currentPos.distanceToSqr(this.lastProgressPos) < PROGRESS_DISTANCE_SQR) {
            this.stuckTicks++;
        } else {
            this.stuckTicks = 0;
            this.forcedTeleportCooldown = 0;
            this.lastProgressPos = currentPos;
        }
    }

    int recordMoveFailure() {
        return ++this.moveFailCount;
    }

    void clearMoveFailures() {
        this.moveFailCount = 0;
    }

    void clearTeleportCooldown() {
        this.teleportCooldown = 0;
    }

    int moveFailCount() {
        return this.moveFailCount;
    }

    public int stuckTicks() {
        return this.stuckTicks;
    }

    public boolean isStuckLongerThan(int stuckTickLimit) {
        return this.stuckTicks > stuckTickLimit;
    }

    boolean shouldTryTeleport(boolean force, double distanceSqr, double teleportDistanceSqr, int cooldownTicks) {
        if (!force && distanceSqr <= teleportDistanceSqr) {
            return false;
        }
        if (!force && this.teleportCooldown++ < cooldownTicks) {
            return false;
        }

        this.teleportCooldown = 0;
        return true;
    }

    public boolean shouldTryTeleportThrottled(boolean force, double distanceSqr, double teleportDistanceSqr, int cooldownTicks) {
        if (!force) {
            return shouldTryTeleport(false, distanceSqr, teleportDistanceSqr, cooldownTicks);
        }
        if (this.forcedTeleportCooldown > 0) {
            this.forcedTeleportCooldown--;
            return false;
        }

        this.forcedTeleportCooldown = cooldownTicks;
        this.teleportCooldown = 0;
        return true;
    }
}
