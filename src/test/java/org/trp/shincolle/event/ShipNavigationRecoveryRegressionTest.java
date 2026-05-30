package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipNavigationRecoveryRegressionTest {
    private static final Path NAVIGATION_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/path/ShipLegacyNavigation.java");
    private static final Path NAVIGATION_POLICY_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/path/ShipLegacyNavigationPolicy.java");
    private static final Path LEGACY_PATH_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/path/ShipLegacyPath.java");

    @Test
    void navigationTimeoutShouldRetryPathBeforeGivingUp() throws IOException {
        String source = Files.readString(NAVIGATION_SOURCE);

        assertTrue(source.contains("private ShipLegacyPath recalculatePathToCurrentTarget()"),
                "Legacy ship navigation should have an explicit forced path recalculation path");
        assertTrue(source.contains("ShipLegacyPath retryPath = recalculatePathToCurrentTarget();"),
                "Legacy ship navigation should keep the last target position for timeout recovery");
        assertTrue(source.contains("if (!setPath(retryPath, this.speedModifier, true, this.targetPos)) {"),
                "Legacy ship navigation should retry pathfinding to the last target before stopping");
        assertTrue(source.contains("stop();"),
                "Legacy ship navigation should still fall back to stop when retry pathfinding fails");
    }

    @Test
    void navigationDebugLoggingShouldNotResetStuckRecoveryProgress() throws IOException {
        String source = Files.readString(NAVIGATION_SOURCE);
        String policy = Files.readString(NAVIGATION_POLICY_SOURCE);

        assertTrue(policy.contains("static final int NAVIGATION_DEBUG_LOG_INTERVAL = 200;"),
                "Navigation exceeded-check diagnostics should be rate-limited");
        assertTrue(policy.contains("static final int NAVIGATION_SET_PATH_LOG_INTERVAL = 100;"),
                "Repeated set-path diagnostics should be rate-limited separately from stuck diagnostics");
        assertTrue(policy.contains("static final double SAME_NAVIGATION_TARGET_SQR = 9.0D;"),
                "Navigation should preserve stuck state only for nearby target recalculations");
        assertTrue(source.contains("double progressDistanceSqr = hostPos.distanceToSqr(this.lastPosStuck);"),
                "Navigation should track real movement progress separately from diagnostic logging");
        assertTrue(source.contains("if (progressDistanceSqr >= STUCK_DISTANCE_SQR) {"),
                "Navigation should reset stuck recovery only after real movement progress");
        assertTrue(source.contains("if (path == this.currentPath) {\n            this.speedModifier = speed;\n            return true;\n        }"),
                "Reapplying the same unfinished path should not reset stuck recovery progress");
        assertTrue(source.contains("boolean hadActivePath = this.currentPath != null && !this.currentPath.isFinished();"),
                "Navigation should distinguish fresh path starts from active path recalculations");
        assertTrue(source.contains("private boolean setPath(ShipLegacyPath path, double speed, boolean sameNavigationTarget, BlockPos nextTarget)"),
                "Navigation setPath should know whether a recalculation still targets the same destination");
        assertTrue(source.contains("if (ShipLegacyNavigationPolicy.shouldResetStuckProgress(hadActivePath, sameNavigationTarget)) {\n            resetStuckProgressState(hostPos);\n        }"),
                "Fresh paths and real target changes should initialize stuck progress state");
        assertTrue(source.contains("private boolean isSameNavigationTarget(BlockPos previousTarget, BlockPos nextTarget)"),
                "Navigation should centralize target-change tolerance");
        assertTrue(policy.contains("previousTarget.distanceToSqr(nextTarget) <= SAME_NAVIGATION_TARGET_SQR"),
                "Navigation should treat nearby entity drift as the same target for stuck recovery");
        assertTrue(source.contains("ShipLegacyNavigationPolicy.shouldLogSetPath("),
                "Set-path diagnostics should throttle against the attempted target without mutating active navigation first");
        assertTrue(source.contains("ShipLegacyNavigationPolicy.shouldLogNavigationEvent(")
                        && source.contains("policyTarget(this.lastExceededLogTarget)")
                        && source.contains("policyTarget(this.targetPos)")
                        && source.contains("this.lastExceededLogTick"),
                "Exceeded-check diagnostics should share the same target drift tolerance");
        assertTrue(source.contains("ShipLegacyNavigationPolicy.shouldLogNavigationEvent(")
                        && source.contains("policyTarget(this.lastStuckApplyLogTarget)")
                        && source.contains("policyTarget(this.targetPos)")
                        && source.contains("this.lastStuckApplyLogTick"),
                "Unstuck diagnostics should share the same target drift tolerance");
        assertTrue(source.contains("setPath(retryPath, this.speedModifier, true, this.targetPos)"),
                "Internal timeout retries should preserve stuck progress for the same target");
        assertTrue(source.contains("private void resetStuckProgressState(Vec3 hostPos)"),
                "Stuck progress initialization should be centralized");
        assertTrue(source.contains("resetPathTimeoutState();"),
                "Per-path node timeout state should be reset independently from stuck progress state");
        assertTrue(source.contains("private void resetPathTimeoutState()"),
                "Navigation should centralize per-path timeout reset state");
        assertTrue(!source.contains("this.lastExceededLogTarget = null;\n        this.lastExceededLogTick = Integer.MIN_VALUE;"),
                "Active path recalculations should not clear exceeded-check log throttling");
        assertTrue(!source.contains("this.lastStuckApplyLogTarget = null;\n        this.lastStuckApplyLogTick = Integer.MIN_VALUE;"),
                "Active path recalculations should not clear stuck-apply log throttling");
        assertTrue(source.contains("if (target == null) {\n            stop();\n            return;\n        }"),
                "Navigation should stop safely if a path no longer exposes a valid current target");
        assertTrue(source.contains("if (nextPos == null) {\n            stop();\n            return;\n        }"),
                "Path following should stop safely if the current path index is already finished");
        assertTrue(source.contains("if (this.targetPos != null) {"),
                "Path following should keep last known direction when no active path");
        assertTrue(source.contains("this.mob.getMoveControl().setWantedPosition("),
                "Path following should feed last target to MoveControl for inertia");
        assertTrue(source.contains("if (stationaryTicks > STUCK_CHECK_INTERVAL && shouldLogExceededCheck()) {"),
                "Navigation exceeded-check logs should not fire every check interval");
        assertTrue(source.contains("if (shouldLogStuckApply()) {"),
                "Navigation unstuck motion diagnostics should be rate-limited");
        assertTrue(source.contains("private boolean shouldLogSetPath(int pathLength, boolean failure, BlockPos logTarget)"),
                "Navigation set-path logs should use a shared throttle helper");
        assertTrue(policy.contains("return totalTicks - lastSetPathLogTick >= NAVIGATION_SET_PATH_LOG_INTERVAL;"),
                "Repeated set-path logs for the same target should wait for the configured interval");
        assertTrue(source.contains("private boolean preserveCurrentPathOnNextFailure;"),
                "Navigation should expose a one-shot guard for foreign failed move requests");
        assertTrue(source.contains("public void preserveCurrentPathOnNextFailure()"),
                "Movement coordinator should be able to protect an active path before a competing path request");
        assertTrue(source.contains("if (!this.preserveCurrentPathOnNextFailure) {\n                this.currentPath = null;\n                this.path = null;\n                this.targetPos = nextTarget;\n            }"),
                "A protected pathfinding failure should not erase the active path owned by another movement channel");
        assertTrue(source.contains("this.targetPos = nextTarget;"),
                "Navigation should switch its current target only after an accepted path or an unprotected failure");
        assertTrue(source.contains("Shincolle.diagnosticLog(\"[SCNavDiag] setPath failed mob={} targetPos={}\", this.mob.getUUID(), nextTarget);"),
                "Failed path logs should report the attempted target without mutating the active navigation target first");
        assertTrue(source.contains("this.preserveCurrentPathOnNextFailure = false;"),
                "The path-preservation guard must be one-shot and clear after success, failure, or stop");
        assertTrue(source.contains("if (!canNavigate()) {\n            this.preserveCurrentPathOnNextFailure = false;\n            return false;\n        }"),
                "Path-preservation should not leak when navigation rejects a move before pathfinding starts");
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
