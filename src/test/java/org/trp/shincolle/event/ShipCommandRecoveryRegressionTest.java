package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipCommandRecoveryRegressionTest {
    private static final Path POINTER_GOAL_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBrainAi.java");
    private static final Path POINTER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBasePointer.java");
    private static final Path AI_NUMBERS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/ShipAiNumbers.java");

    @Test
    void pointerMoveBehaviorShouldUseCentralizedAiNumbersAndCoordinator() throws IOException {
        String source = Files.readString(POINTER_GOAL_SOURCE);
        String numbers = Files.readString(AI_NUMBERS_SOURCE);

        assertTrue(numbers.contains("static final double POINTER_MOVE_REACH_SQR = 1.0D;"),
                "Pointer move reach distance should be centralized");
        assertTrue(numbers.contains("static final double POINTER_MOVE_SPEED = 1.2D;"),
                "Pointer move speed should be centralized");
        assertTrue(numbers.contains("static final double TARGET_SWITCH_DISTANCE_SQ = 0.01D;"),
                "Pointer target-change threshold should be centralized");
        assertTrue(source.contains("movement.moveTo(target, ShipAiNumbers.POINTER_MOVE_SPEED)"),
                "Pointer move Brain behavior should route movement through the shared coordinator");
        assertTrue(source.contains("ship.distanceToSqr(target) <= ShipAiNumbers.POINTER_MOVE_REACH_SQR"),
                "Pointer move Brain behavior should use centralized reach distance");
        assertTrue(source.contains("if (this.pointerRecovery.isStuckLongerThan(ShipAiNumbers.MOVE_STUCK_TICK_LIMIT)) {"),
                "Pointer move Brain behavior should retain stuck recovery after migration");
        assertTrue(source.contains("this.pointerRecovery.shouldTryTeleportThrottled(force, ship.distanceToSqr(target),"),
                "Pointer move Brain behavior should throttle forced teleport recovery");
        assertTrue(source.contains("ship.pointerMovementCoordinator().teleportNearPoint(target, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)"),
                "Pointer move Brain behavior should use coordinator teleport recovery");
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
    void guardBehaviorShouldUseCentralizedAiNumbersAndCoordinator() throws IOException {
        String source = Files.readString(POINTER_GOAL_SOURCE);
        String numbers = Files.readString(AI_NUMBERS_SOURCE);

        assertTrue(numbers.contains("static final double GUARD_MOVE_SPEED = 1.1D;"),
                "Guard movement speed should be centralized");
        assertTrue(numbers.contains("static final double GUARD_ENTITY_STOP_DISTANCE_SQ = 9.0D;"),
                "Guard entity stop distance should be centralized");
        assertTrue(numbers.contains("static final double GUARD_BLOCK_STOP_DISTANCE_SQ = 0.5D;"),
                "Guard block stop distance should be centralized");
        assertTrue(source.contains("ship.guardMovementCoordinator()"),
                "Guard Brain behavior should route movement through the shared guard coordinator");
        assertTrue(source.contains("guardTarget.isEntity()\n                    ? ShipAiNumbers.GUARD_ENTITY_STOP_DISTANCE_SQ\n                    : ShipAiNumbers.GUARD_BLOCK_STOP_DISTANCE_SQ;"),
                "Guard Brain behavior should use centralized stop distances");
        assertTrue(source.contains("if (this.guardRecovery.isStuckLongerThan(ShipAiNumbers.MOVE_STUCK_TICK_LIMIT)) {"),
                "Guard Brain behavior should retain stuck recovery after migration");
        assertTrue(source.contains("this.guardRecovery.shouldTryTeleportThrottled(force, distSq,"),
                "Guard Brain behavior should throttle forced teleport recovery");
        assertTrue(source.contains("ship.guardMovementCoordinator().teleportNearPoint(target, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)"),
                "Guard block recovery should use coordinator teleport recovery");
    }
}
