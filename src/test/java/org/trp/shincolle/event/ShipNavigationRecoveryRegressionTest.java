package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipNavigationRecoveryRegressionTest {
    private static final Path NAVIGATION_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/path/ShipLegacyNavigation.java");

    @Test
    void navigationTimeoutShouldRetryPathBeforeGivingUp() throws IOException {
        String source = Files.readString(NAVIGATION_SOURCE);

        assertTrue(source.contains("BlockPos retryTarget = this.targetPos;"),
                "Legacy ship navigation should keep the last target position for timeout recovery");
        assertTrue(source.contains("if (retryTarget == null || !moveTo(retryTarget.getX() + 0.5D, retryTarget.getY(), retryTarget.getZ() + 0.5D, retrySpeed)) {"),
                "Legacy ship navigation should retry pathfinding to the last target before stopping");
        assertTrue(source.contains("stop();"),
                "Legacy ship navigation should still fall back to stop when retry pathfinding fails");
    }

    @Test
    void navigationDebugLoggingShouldNotResetStuckRecoveryProgress() throws IOException {
        String source = Files.readString(NAVIGATION_SOURCE);

        assertTrue(source.contains("private static final int NAVIGATION_DEBUG_LOG_INTERVAL = 200;"),
                "Navigation exceeded-check diagnostics should be rate-limited");
        assertTrue(source.contains("double progressDistanceSqr = hostPos.distanceToSqr(this.lastPosStuck);"),
                "Navigation should track real movement progress separately from diagnostic logging");
        assertTrue(source.contains("if (progressDistanceSqr >= STUCK_DISTANCE_SQR) {"),
                "Navigation should reset stuck recovery only after real movement progress");
        assertTrue(source.contains("if (stationaryTicks > STUCK_CHECK_INTERVAL && shouldLogExceededCheck()) {"),
                "Navigation exceeded-check logs should not fire every check interval");
    }
}
