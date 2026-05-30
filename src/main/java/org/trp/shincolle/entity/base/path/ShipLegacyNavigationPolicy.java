package org.trp.shincolle.entity.base.path;

final class ShipLegacyNavigationPolicy {
    static final int NAVIGATION_DEBUG_LOG_INTERVAL = 200;
    static final int NAVIGATION_SET_PATH_LOG_INTERVAL = 100;
    static final double SAME_NAVIGATION_TARGET_SQR = 9.0D;

    private ShipLegacyNavigationPolicy() {
    }

    static boolean isSameNavigationTarget(Target previousTarget, Target nextTarget) {
        return previousTarget != null
                && nextTarget != null
                && previousTarget.distanceToSqr(nextTarget) <= SAME_NAVIGATION_TARGET_SQR;
    }

    static boolean shouldResetStuckProgress(boolean hadActivePath, boolean sameNavigationTarget) {
        return !hadActivePath || !sameNavigationTarget;
    }

    static boolean shouldLogSetPath(boolean loggedPathFailure, boolean failure, Target loggedTarget,
                                    Target logTarget, int totalTicks, int lastSetPathLogTick) {
        if (loggedPathFailure != failure) {
            return true;
        }
        if (!isSameNavigationTarget(loggedTarget, logTarget)) {
            return true;
        }
        return totalTicks - lastSetPathLogTick >= NAVIGATION_SET_PATH_LOG_INTERVAL;
    }

    static boolean shouldLogNavigationEvent(Target lastLogTarget, Target currentTarget, int totalTicks, int lastLogTick) {
        return !isSameNavigationTarget(lastLogTarget, currentTarget)
                || lastLogTick == Integer.MIN_VALUE
                || totalTicks - lastLogTick >= NAVIGATION_DEBUG_LOG_INTERVAL;
    }

    static double calculateTimeoutLimit(double distance, double speed) {
        return distance / Math.max(0.01D, speed) * 60.0D;
    }

    static boolean shouldRetryTimedOutPath(long timeoutTimer, double timeoutLimit) {
        return timeoutLimit > 0.0D && timeoutTimer > timeoutLimit * 2.0D;
    }

    record Target(int x, int y, int z) {
        private double distanceToSqr(Target other) {
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            double dz = this.z - other.z;
            return dx * dx + dy * dy + dz * dz;
        }
    }
}
