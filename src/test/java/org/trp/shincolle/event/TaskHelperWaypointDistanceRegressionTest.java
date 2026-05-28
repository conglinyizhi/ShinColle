package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskHelperWaypointDistanceRegressionTest {
    private static final Path TASK_HELPER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/utility/TaskHelper.java");

    @Test
    void cookingAndCraftingShouldKeepLegacyChestDistanceGate() throws IOException {
        String source = Files.readString(TASK_HELPER_SOURCE);

        assertTrue(source.contains("if (host.distanceToSqr(chestPos.getX(), chestPos.getY(), chestPos.getZ()) > 25.0D)"),
                "Cooking and crafting should preserve the legacy paired-container distance gate");
        assertTrue(source.contains("runtime.moveToTaskPoint(new Vec3(gx + 0.5D, gy, gz + 0.5D), 1.0D);"),
                "When the paired container is too far away, tasks should still move back toward the waypoint");
    }
}
