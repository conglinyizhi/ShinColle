package org.trp.shincolle.event

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class ShipTaskRuntimeArchitectureRegressionTest {
    private val SHIP_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt")
    private val TASK_RUNTIME_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/entity/base/ShipTaskRuntime.kt")
    private val TASK_HELPER_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/utility/TaskHelper.kt")

    @Test
    fun shipsShouldOwnLongLivedTaskRuntime() {
        val ship: String = Files.readString(SHIP_SOURCE)
        val runtime: String = Files.readString(TASK_RUNTIME_SOURCE)

        assertTrue(ship.contains("private final ShipTaskRuntime taskRuntime;")) {
            "Ship should own long-lived task runtime state"
        }
        assertTrue(ship.contains("this.taskRuntime = new ShipTaskRuntime(this);")) {
            "Ship should initialize task runtime once with the ship instance"
        }
        assertTrue(ship.contains("public ShipTaskRuntime getTaskRuntime()")) {
            "Task helper should be able to access the ship task runtime"
        }
        assertTrue(runtime.contains("private final ShipMovementCoordinator movement;")) {
            "Task runtime should preserve a long-lived movement coordinator"
        }
        assertTrue(runtime.contains("private final ShipMovementRecoveryState recovery;")) {
            "Task runtime should use shared movement recovery for fixed task targets"
        }
        assertTrue(runtime.contains("public boolean moveToTaskPoint(Vec3 target, double speed)")) {
            "Task runtime should expose recoverable movement for fixed waypoint tasks"
        }
        assertTrue(runtime.contains("this.recovery.shouldTryTeleportThrottled(force, distanceSqr,")) {
            "Task runtime should use throttled teleport recovery for fixed task targets"
        }
        assertTrue(runtime.contains("this.movement.teleportNearPoint(target, 0.75D)")) {
            "Task runtime should route task teleport recovery through the coordinator"
        }
        assertTrue(runtime.contains("TaskMove teleportRecovery")) {
            "Task runtime should emit searchable recovery diagnostics"
        }
        assertTrue(runtime.contains("this.recovery.shouldLogMoveFailure(this.ship.tickCount, TASK_MOVE_FAIL_LOG_INTERVAL)")) {
            "Task runtime should rate-limit repeated move-failure diagnostics through shared recovery state"
        }
        assertFalse(runtime.contains("lastMoveFailLogTick")) {
            "Task runtime should not duplicate move-failure diagnostic throttling state"
        }
        assertTrue(runtime.contains("public void beginTaskTick(int taskId)")) {
            "Task runtime should track task transitions"
        }
        assertTrue(runtime.contains("this.movement.reset();\n            this.recovery.reset(this.ship.position());")) {
            "Task runtime should reset movement suppression when task id changes"
        }
        assertTrue(runtime.contains("public void clearTask()")) {
            "Task runtime should reset movement state when tasks stop"
        }
        assertTrue(runtime.contains("public void clearTask() {\n        if (this.lastTaskId != NO_TASK) {\n            this.movement.stop();")) {
            "Task runtime should stop stale navigation when tasks stop"
        }
    }

    @Test
    fun taskHelperShouldRouteTaskMovementThroughRuntime() {
        val source: String = Files.readString(TASK_HELPER_SOURCE)

        assertTrue(source.contains("ShipTaskRuntime runtime = host.getTaskRuntime();")) {
            "Task helper should use the ship-owned task runtime"
        }
        assertTrue(source.contains("runtime.beginTaskTick(taskId);")) {
            "Task helper should notify runtime about task id changes"
        }
        assertTrue(source.contains("host.getTaskRuntime().clearTask();")) {
            "Task helper should clear runtime movement state when tasks cannot run"
        }
        assertTrue(source.contains("} else {\n                        runtime.clearTask();\n                    }")) {
            "Task helper should clear stale runtime state when a task is disabled by config"
        }
        assertTrue(source.contains("runtime.moveToTaskPoint(new Vec3(gx + 0.5D, gy, gz + 0.5D), 1.0D);")) {
            "Fixed waypoint task movement should route through the recoverable runtime path"
        }
        assertTrue(source.contains("runtime.moveTo(new Vec3(\n                    host.getX() + host.getRandom().nextInt(9) - 4.0D")) {
            "Mining random stroll should route through ordinary runtime movement instead of teleport recovery"
        }
        assertFalse(source.contains("host.getNavigation().moveTo")) {
            "Task helper should not issue raw ship navigation requests"
        }
    }

    @Test
    fun taskHelperShouldClearRuntimeWhenTaskPrerequisitesBecomeInvalid() {
        val source: String = Files.readString(TASK_HELPER_SOURCE)

        assertTrue(source.contains("private static void invalidateTask(ShipTaskRuntime runtime)")) {
            "Task helper should centralize task invalidation for long-lived runtime state"
        }
        assertTrue(source.contains("if (mainStack.isEmpty()) {\n            invalidateTask(runtime);")) {
            "Cooking should clear stale runtime state when the held input stack is no longer present"
        }
        assertTrue(source.contains("if (resultStack.isEmpty()) {\n            invalidateTask(runtime);")) {
            "Cooking should clear stale runtime state when the held item no longer has a smelting recipe"
        }
        assertTrue(source.contains("if (!isWaypointGuardContext(guardTarget, level)) {\n            invalidateTask(runtime);")) {
            "Waypoint-bound tasks should clear stale runtime state when the guard context becomes invalid"
        }
        assertTrue(source.contains("if (waterPos == null) {\n            invalidateTask(runtime);")) {
            "Fishing should clear stale runtime state when no valid fishing water remains"
        }
        assertTrue(source.contains("if (pickaxe.isEmpty() || !pickaxe.is(net.minecraft.tags.ItemTags.PICKAXES)) {\n            invalidateTask(runtime);")) {
            "Mining should clear stale runtime state when the held tool is no longer a pickaxe"
        }
        assertTrue(source.contains("if (recipePaper.isEmpty() || !recipePaper.is(ModItems.RECIPE_PAPER.get())) {\n            invalidateTask(runtime);")) {
            "Crafting should clear stale runtime state when the held recipe paper is removed"
        }
        assertTrue(source.contains("if (!RecipePaperData.hasAnyRecipeIngredient(recipeGrid)) {\n            invalidateTask(runtime);")) {
            "Crafting should clear stale runtime state when the stored recipe becomes empty"
        }
    }
}
