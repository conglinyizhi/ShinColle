package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipNavigationRecoveryRegressionTest {
    private static final Path NAVIGATION_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/path/ShipLegacyNavigation.java");
    private static final Path LEGACY_PATH_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/path/ShipLegacyPath.java");

    @Test
    void navigationTimeoutShouldRetryPathBeforeGivingUp() throws IOException {
        String source = Files.readString(NAVIGATION_SOURCE);

        assertTrue(source.contains("private ShipLegacyPath recalculatePathToCurrentTarget()"),
                "Legacy ship navigation should have an explicit forced path recalculation path");
        assertTrue(source.contains("ShipLegacyPath retryPath = recalculatePathToCurrentTarget();"),
                "Legacy ship navigation should keep the last target position for timeout recovery");
        assertTrue(source.contains("if (!setPath(retryPath, this.speedModifier)) {"),
                "Legacy ship navigation should retry pathfinding to the last target before stopping");
        assertTrue(source.contains("stop();"),
                "Legacy ship navigation should still fall back to stop when retry pathfinding fails");
    }

    @Test
    void navigationDebugLoggingShouldNotResetStuckRecoveryProgress() throws IOException {
        String source = Files.readString(NAVIGATION_SOURCE);

        assertTrue(source.contains("private static final int NAVIGATION_DEBUG_LOG_INTERVAL = 200;"),
                "Navigation exceeded-check diagnostics should be rate-limited");
        assertTrue(source.contains("private static final int NAVIGATION_SET_PATH_LOG_INTERVAL = 100;"),
                "Repeated set-path diagnostics should be rate-limited separately from stuck diagnostics");
        assertTrue(source.contains("double progressDistanceSqr = hostPos.distanceToSqr(this.lastPosStuck);"),
                "Navigation should track real movement progress separately from diagnostic logging");
        assertTrue(source.contains("if (progressDistanceSqr >= STUCK_DISTANCE_SQR) {"),
                "Navigation should reset stuck recovery only after real movement progress");
        assertTrue(source.contains("if (path == this.currentPath) {\n            this.speedModifier = speed;\n            return true;\n        }"),
                "Reapplying the same unfinished path should not reset stuck recovery progress");
        assertTrue(source.contains("if (target == null) {\n            stop();\n            return;\n        }"),
                "Navigation should stop safely if a path no longer exposes a valid current target");
        assertTrue(source.contains("if (nextPos == null) {\n            stop();\n            return;\n        }"),
                "Path following should stop safely if the current path index is already finished");
        assertTrue(source.contains("ShipLegacyPathPoint point = this.currentPath.getPathPointFromIndex(i);"),
                "Path following should read path points through a null-safe accessor");
        assertTrue(source.contains("if (point == null || point.getY() != Mth.floor(hostPos.y)) {"),
                "Path following should stop scanning when a path point is missing");
        assertTrue(source.contains("if (stationaryTicks > STUCK_CHECK_INTERVAL && shouldLogExceededCheck()) {"),
                "Navigation exceeded-check logs should not fire every check interval");
        assertTrue(source.contains("if (shouldLogStuckApply()) {"),
                "Navigation unstuck motion diagnostics should be rate-limited");
        assertTrue(source.contains("private boolean shouldLogSetPath(int pathLength, boolean failure)"),
                "Navigation set-path logs should use a shared throttle helper");
        assertTrue(source.contains("return this.totalTicks - this.lastSetPathLogTick >= NAVIGATION_SET_PATH_LOG_INTERVAL;"),
                "Repeated set-path logs for the same target should wait for the configured interval");
    }

    @Test
    void legacyPathShouldExposeInvalidCurrentTargetAsMissing() throws IOException {
        String source = Files.readString(LEGACY_PATH_SOURCE);

        assertTrue(source.contains("this.currentPathIndex = Math.max(0, Math.min(currentPathIndex, this.points.length));"),
                "Legacy path indexes should stay within the valid path/sentinel range");
        assertTrue(source.contains("return null;"),
                "Finished or invalid path positions should expose no target instead of Vec3.ZERO");
        assertTrue(!source.contains("return Vec3.ZERO;"),
                "Finished paths should not masquerade as a real target at the world origin");
        assertTrue(source.contains("if (index < 0 || index >= this.points.length)"),
                "Path vector lookup should guard invalid indexes before touching path points");
        assertTrue(source.contains("ShipLegacyPathPoint getPathPointFromIndex(int index)"),
                "Path point lookup should be centralized behind an accessor");
    }
}
