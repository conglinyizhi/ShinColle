package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipTaskRuntimeArchitectureRegressionTest {
    private static final Path SHIP_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.java");
    private static final Path TASK_RUNTIME_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/ShipTaskRuntime.java");
    private static final Path TASK_HELPER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/utility/TaskHelper.java");

    @Test
    void shipsShouldOwnLongLivedTaskRuntime() throws IOException {
        String ship = Files.readString(SHIP_SOURCE);
        String runtime = Files.readString(TASK_RUNTIME_SOURCE);

        assertTrue(ship.contains("private final ShipTaskRuntime taskRuntime;"),
                "Ship should own long-lived task runtime state");
        assertTrue(ship.contains("this.taskRuntime = new ShipTaskRuntime(this);"),
                "Ship should initialize task runtime once with the ship instance");
        assertTrue(ship.contains("public ShipTaskRuntime getTaskRuntime()"),
                "Task helper should be able to access the ship task runtime");
        assertTrue(runtime.contains("private final ShipMovementCoordinator movement;"),
                "Task runtime should preserve a long-lived movement coordinator");
        assertTrue(runtime.contains("public void beginTaskTick(int taskId)"),
                "Task runtime should track task transitions");
        assertTrue(runtime.contains("this.movement.reset();\n            this.lastTaskId = taskId;"),
                "Task runtime should reset movement suppression when task id changes");
        assertTrue(runtime.contains("public void clearTask()"),
                "Task runtime should reset movement state when tasks stop");
    }

    @Test
    void taskHelperShouldRouteTaskMovementThroughRuntime() throws IOException {
        String source = Files.readString(TASK_HELPER_SOURCE);

        assertTrue(source.contains("ShipTaskRuntime runtime = host.getTaskRuntime();"),
                "Task helper should use the ship-owned task runtime");
        assertTrue(source.contains("runtime.beginTaskTick(taskId);"),
                "Task helper should notify runtime about task id changes");
        assertTrue(source.contains("host.getTaskRuntime().clearTask();"),
                "Task helper should clear runtime movement state when tasks cannot run");
        assertTrue(source.contains("runtime.moveTo(new Vec3(gx + 0.5D, gy, gz + 0.5D), 1.0D);"),
                "Waypoint task movement should route through the runtime coordinator");
        assertTrue(source.contains("runtime.moveTo(new Vec3(\n                    host.getX() + host.getRandom().nextInt(9) - 4.0D"),
                "Mining random stroll should route through the runtime coordinator");
        assertFalse(source.contains("host.getNavigation().moveTo"),
                "Task helper should not issue raw ship navigation requests");
    }
}
