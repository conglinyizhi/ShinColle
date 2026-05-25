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
}
