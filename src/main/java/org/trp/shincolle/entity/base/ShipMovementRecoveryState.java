package org.trp.shincolle.entity.base;

import net.minecraft.world.phys.Vec3;

final class ShipMovementRecoveryState {
    private static final double PROGRESS_DISTANCE_SQR = 0.04D;

    private int moveFailCount;
    private int stuckTicks;
    private int teleportCooldown;
    private Vec3 lastProgressPos;

    void reset(Vec3 currentPos) {
        this.moveFailCount = 0;
        this.stuckTicks = 0;
        this.teleportCooldown = 0;
        this.lastProgressPos = currentPos;
    }

    void clear() {
        this.moveFailCount = 0;
        this.stuckTicks = 0;
        this.teleportCooldown = 0;
        this.lastProgressPos = null;
    }

    void trackProgress(Vec3 currentPos) {
        if (this.lastProgressPos == null) {
            this.lastProgressPos = currentPos;
            this.stuckTicks = 0;
            return;
        }

        if (currentPos.distanceToSqr(this.lastProgressPos) < PROGRESS_DISTANCE_SQR) {
            this.stuckTicks++;
        } else {
            this.stuckTicks = 0;
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

    int stuckTicks() {
        return this.stuckTicks;
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
}
