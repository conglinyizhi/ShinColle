package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipCommandRecoveryRegressionTest {
    private static final Path POINTER_GOAL_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipPointerGoals.java");
    private static final Path POINTER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBasePointer.java");
    private static final Path GUARD_GOAL_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipGuardGoal.java");

    @Test
    void pointerMoveGoalShouldTryTeleportRecoveryBeforeClearingPositionCommand() throws IOException {
        String source = Files.readString(POINTER_GOAL_SOURCE);

        assertTrue(source.contains("private static final int POINTER_MOVE_FAIL_LIMIT = 40;"),
                "Pointer move goal should define a move failure limit");
        assertTrue(source.contains("private static final int POINTER_MOVE_STUCK_TICK_LIMIT = 120;"),
                "Pointer move goal should define a stuck timeout");
        assertTrue(source.contains("private static final int POINTER_MOVE_TELEPORT_COOLDOWN_TICKS = 100;"),
                "Pointer move goal should use a cooldown before remote teleport recovery");
        assertTrue(source.contains("this.recovery.shouldTryTeleportThrottled(force, ship.distanceToSqr(target),"),
                "Pointer move forced teleport recovery should be throttled after failed attempts");
        assertTrue(source.contains("if (tryTeleportRecovery(ship.getPointerTarget(), false)) {\n            return;\n        }"),
                "Pointer move goal should check distant teleport recovery every tick while stuck on a far command");
        assertTrue(source.contains("tryTeleportRecovery(target, true)"),
                "Pointer move goal should force one teleport recovery attempt before clearing repeated failures");
        assertTrue(source.contains("tryTeleportRecovery(ship.getPointerTarget(), true)"),
                "Pointer move goal should force one teleport recovery attempt before clearing stuck commands");
        assertTrue(source.contains("movement().teleportNearPoint(target, 0.75D)"),
                "Pointer move recovery should use the movement coordinator's safe point teleport");
        assertTrue(source.contains("ship.clearPointerTarget();\n            return;"),
                "Pointer move goal should still clear pointer position commands when recovery fails");
        assertTrue(source.contains("this.lastRawTarget = null;\n        this.recovery.clear();\n        movement().stop();"),
                "Pointer move goal should clear target memory and recovery counters when interrupted");
    }

    @Test
    void pointerEntityCommandShouldTryTeleportRecoveryBeforeClearingTarget() throws IOException {
        String source = Files.readString(POINTER_SOURCE);

        assertTrue(source.contains("private static final int POINTER_ENTITY_MOVE_FAIL_LIMIT = 40;"),
                "Pointer entity command should define a move failure limit");
        assertTrue(source.contains("private static final int POINTER_ENTITY_STUCK_TICK_LIMIT = 120;"),
                "Pointer entity command should define a stuck timeout");
        assertTrue(source.contains("private static final int POINTER_ENTITY_TELEPORT_COOLDOWN_TICKS = 100;"),
                "Pointer entity command should use a cooldown before remote teleport recovery");
        assertTrue(source.contains("this.pointerTargetEntityRecovery.shouldTryTeleportThrottled(force, this.ship.distanceToSqr(target),"),
                "Pointer entity forced teleport recovery should be throttled after failed attempts");
        assertTrue(source.contains("tryPointerTargetEntityTeleportRecovery(target, false)"),
                "Pointer entity command should try distant teleport recovery before ordinary path movement");
        assertTrue(source.contains("tryPointerTargetEntityTeleportRecovery(target, true)"),
                "Pointer entity command should force one teleport recovery attempt before clearing failures");
        assertTrue(source.contains("this.movement.teleportNearLiving(livingTarget, 0.75D)"),
                "Pointer entity recovery should use the movement coordinator's safe living teleport");
        assertTrue(source.contains("this.movement.teleportNearPoint(target.position(), 0.75D)"),
                "Pointer entity recovery should support non-living entity targets");
        assertTrue(source.contains("PointerEntity teleportRecovery"),
                "Pointer entity recovery should emit searchable debug logs");
        assertTrue(source.contains("clearPointerTargetEntity();\n                return;"),
                "Pointer entity command should still clear stale targets when recovery fails");
    }

    @Test
    void guardGoalShouldDisableGuardStateAfterRepeatedFailuresOrNoProgress() throws IOException {
        String source = Files.readString(GUARD_GOAL_SOURCE);

        assertTrue(source.contains("private static final int GUARD_MOVE_FAIL_LIMIT = 40;"),
                "Guard goal should define a move failure limit");
        assertTrue(source.contains("private static final int GUARD_STUCK_TICK_LIMIT = 120;"),
                "Guard goal should define a stuck timeout");
        assertTrue(source.contains("private static final int GUARD_TELEPORT_COOLDOWN_TICKS = 100;"),
                "Guard goal should use a cooldown before remote teleport recovery");
        assertTrue(source.contains("double distSq = ship.distanceToSqr(target);"),
                "Guard teleport recovery should include vertical distance instead of only horizontal distance");
        assertTrue(source.contains("this.recovery.shouldTryTeleportThrottled(force, distSq, GUARD_TELEPORT_DISTANCE_SQ,"),
                "Guard forced teleport recovery should be throttled after failed attempts");
        assertTrue(source.contains("tryTeleportRecovery(target, guardedEntity, distSq, false)"),
                "Guard goal should try teleport recovery for distant guard targets before disabling guard");
        assertTrue(source.contains("tryTeleportRecovery(target, guardedEntity, distSq, true)"),
                "Guard goal should force one teleport recovery attempt before disabling stuck guard targets");
        assertTrue(source.contains("movement().teleportNearPoint(target, 0.75D)"),
                "Guard block recovery should use the movement coordinator's safe point teleport");
        assertTrue(source.contains("ship.setStateFlag(EntityShipBase.STATE_FLAG_DISABLE_GUARD_POS, true);"),
                "Guard goal should disable guard mode when recovery triggers");
        assertTrue(source.contains("ship.clearGuardTarget();"),
                "Guard goal recovery should clear the stale guarded position");
    }
}
