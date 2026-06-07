package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipCommandRecoveryRegressionTest {
    private static final Path POINTER_GOAL_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBrainAi.kt");
    private static final Path POINTER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBasePointer.kt");
    private static final Path AI_NUMBERS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/ShipAiNumbers.kt");

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
                "Pointer move Brain behavior should use the tested resolver for centralized reach distance");
        assertTrue(source.contains("this.pointerRecovery.isStuckLongerThan(ShipAiNumbers.MOVE_STUCK_TICK_LIMIT)"),
                "Pointer move Brain behavior should retain stuck recovery through the tested point-command resolver");
        assertTrue(source.contains("ShipBrainRecoverySupport.shouldTryTeleportRecovery(this.pointerRecovery, recoveryState,"),
                "Pointer move Brain behavior should route point-command teleport gating through the shared Brain helper");
        assertTrue(source.contains("ship.pointerMovementCoordinator().teleportNearPoint(target, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)"),
                "Pointer move Brain behavior should use coordinator teleport recovery");
    }

    @Test
    void pointerEntityCommandShouldTryTeleportRecoveryBeforeClearingTarget() throws IOException {
        String brain = Files.readString(POINTER_GOAL_SOURCE);
        String pointer = Files.readString(POINTER_SOURCE);
        String numbers = Files.readString(AI_NUMBERS_SOURCE);

        assertTrue(numbers.contains("static final int POINTER_ENTITY_MOVE_FAIL_LIMIT = 40;"),
                "Pointer entity command should define a move failure limit");
        assertTrue(numbers.contains("static final int POINTER_ENTITY_STUCK_TICK_LIMIT = 120;"),
                "Pointer entity command should define a stuck timeout");
        assertTrue(numbers.contains("static final int POINTER_ENTITY_TELEPORT_COOLDOWN_TICKS = 100;"),
                "Pointer entity command should use a cooldown before remote teleport recovery");
        assertTrue(brain.contains("ShipBrainRecoverySupport.shouldTryTeleportRecovery(this.pointerRecovery, recoveryState,"),
                "Pointer entity recovery should route teleport-attempt gating through the shared Brain helper");
        assertTrue(brain.contains("ShipBrainRecoverySupport.recordMoveFailureAndSync(ship, this.pointerRecovery,"),
                "Pointer entity recovery should reuse the shared move-failure bookkeeping helper");
        assertTrue(brain.contains("tryPointerEntityTeleportRecovery(ship, target, distanceSqr, false)"),
                "Pointer entity command should try distant teleport recovery before ordinary path movement");
        assertTrue(brain.contains("tryPointerEntityTeleportRecovery(ship, target, distanceSqr, true)"),
                "Pointer entity command should force one teleport recovery attempt before clearing failures");
        assertTrue(brain.contains("movement.teleportNearLiving(livingTarget, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)"),
                "Pointer entity recovery should use the movement coordinator's safe living teleport");
        assertTrue(brain.contains("movement.teleportNearPoint(target.position(), ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)"),
                "Pointer entity recovery should support non-living entity targets");
        assertTrue(brain.contains("PointerEntity teleportRecovery"),
                "Pointer entity recovery should emit searchable debug logs");
        assertTrue(brain.contains("ship.clearPointerTargetEntity();\n                clearPointerMoveState(ship);"),
                "Pointer entity command should still clear stale targets when recovery fails");
        assertFalse(pointer.contains("tryPointerTargetEntityTeleportRecovery"),
                "Pointer runtime should not retain movement recovery after Brain migration");
        assertFalse(pointer.contains("shouldMoveTowardPointerTargetEntity"),
                "Pointer runtime should not retain chase decisions after Brain memory migration");
        assertTrue(brain.contains("if (!pointerMemory.entityShouldChase()) {"),
                "Pointer entity Brain behavior should consume memory-backed chase decisions");
        assertTrue(brain.contains("tickPointerEntityAttacks(ship, target, pointerMemory);"),
                "Pointer entity Brain behavior should handle attacks after chase completes");
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
        assertTrue(source.contains("ShipGuardDecisionResolver.stopDistanceSqr(guardState)"),
                "Guard Brain behavior should use resolver-owned stop distances");
        assertTrue(source.contains("ShipGuardDecisionResolver.shouldClearAfterStuck(this.guardRecovery)"),
                "Guard Brain behavior should retain shared recovery resolver stuck thresholds after migration");
        assertTrue(source.contains("ShipBrainRecoverySupport.shouldTryTeleportRecovery(this.guardRecovery, recoveryState,"),
                "Guard Brain behavior should route teleport-attempt gating through the shared Brain helper");
        assertTrue(source.contains("ShipBrainRecoverySupport.recordMoveFailureAndSync(ship, this.guardRecovery,"),
                "Guard Brain behavior should reuse the shared move-failure bookkeeping helper");
        assertTrue(source.contains("ship.guardMovementCoordinator().teleportNearPoint(target, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)"),
                "Guard block recovery should use coordinator teleport recovery");
    }
}
