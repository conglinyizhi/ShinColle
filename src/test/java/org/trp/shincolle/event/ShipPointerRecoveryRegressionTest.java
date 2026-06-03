package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipPointerRecoveryRegressionTest {
    private static final Path POINTER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBasePointer.java");
    private static final Path BRAIN_AI_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBrainAi.java");
    private static final Path AI_NUMBERS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/ShipAiNumbers.java");
    private static final Path SHIP_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.java");

    @Test
    void pointerEntityCommandShouldClearAfterRepeatedMoveFailures() throws IOException {
        String brain = Files.readString(BRAIN_AI_SOURCE);
        String numbers = Files.readString(AI_NUMBERS_SOURCE);

        assertTrue(numbers.contains("static final int POINTER_ENTITY_MOVE_FAIL_LIMIT = 40;"),
                "Pointer-entity movement should define a move failure limit");
        assertTrue(brain.contains("ShipPointerEntityDecisionResolver.shouldClearAfterMoveFailures("),
                "Pointer-entity Brain movement should use the shared recovery resolver for move-failure clear thresholds");
        assertTrue(brain.contains("tryPointerEntityTeleportRecovery(ship, target, distanceSqr, true)"),
                "Pointer-entity Brain movement should try teleport recovery before clearing repeated failures");
        assertTrue(brain.contains("ship.clearPointerTargetEntity();\n                        clearPointerMoveState(ship);"),
                "Pointer-entity failure recovery should release the command through the centralized clear path");
    }

    @Test
    void pointerEntityCommandShouldClearAfterExtendedNoMovement() throws IOException {
        String brain = Files.readString(BRAIN_AI_SOURCE);
        String numbers = Files.readString(AI_NUMBERS_SOURCE);

        assertTrue(numbers.contains("static final int POINTER_ENTITY_STUCK_TICK_LIMIT = 120;"),
                "Pointer-entity movement should define a stuck timeout");
        assertTrue(brain.contains("this.pointerRecovery.trackProgress(ship.position());"),
                "Pointer-entity Brain movement should track whether the ship is making progress");
        assertTrue(brain.contains("ShipPointerEntityDecisionResolver.shouldClearAfterStuck("),
                "Pointer-entity Brain movement should use the shared recovery resolver for stuck clear thresholds");
    }

    @Test
    void pointerEntityMovementShouldLiveInBrainBehavior() throws IOException {
        String pointer = Files.readString(POINTER_SOURCE);
        String brain = Files.readString(BRAIN_AI_SOURCE);
        String ship = Files.readString(SHIP_SOURCE);

        assertTrue(brain.contains("private void tickPointerEntityMove(EntityShipBase ship, ShipBrainMemory.PointerTargetMemory pointerMemory)"),
                "Pointer-entity chase should be owned by the Brain pointer behavior");
        assertTrue(brain.contains("movement.moveTo(target, ShipAiNumbers.POINTER_ENTITY_MOVE_SPEED)"),
                "Pointer-entity Brain movement should route through the coordinator");
        assertTrue(brain.contains("this.lastPointerEntityTargetId = pointerMemory.entityTargetId();"),
                "Pointer-entity Brain movement should reset duplicate move suppression when a new target is assigned");
        assertTrue(brain.contains("tickPointerEntityAttacks(ship, target, pointerMemory);"),
                "Pointer-entity attacks should be owned by the Brain pointer behavior when movement is no longer needed");
        assertTrue(brain.contains("private int pointerEntityMeleeAttackTick;"),
                "Pointer-entity melee cadence should be kept in the Brain behavior runtime");
        assertTrue(brain.contains("private int pointerEntityLightShotTick;"),
                "Pointer-entity light attack cadence should be kept in the Brain behavior runtime");
        assertTrue(brain.contains("private int pointerEntityHeavyShotTick;"),
                "Pointer-entity heavy attack cadence should be kept in the Brain behavior runtime");
        assertTrue(ship.contains("public void clearPointerTargetEntity() {\n        this.pointer.clearPointerTargetEntity();\n        this.pointerMovement.stop();"),
                "Public pointer-entity clear should stop stale Brain-owned pointer navigation");
        assertFalse(pointer.contains("private final ShipMovementCoordinator movement;"),
                "Pointer runtime should no longer own movement coordination after Brain migration");
        assertFalse(pointer.contains("pointerTargetEntityRecovery"),
                "Pointer runtime should no longer own movement recovery after Brain migration");
        assertFalse(pointer.contains("pointerTargetEntityAttackTick"),
                "Pointer runtime should no longer own pointer-entity attack cadence after Brain migration");
        assertFalse(pointer.contains("handlePointerTargetEntityCombat"),
                "Pointer runtime should not execute pointer-entity attacks outside the Brain behavior");
    }

    @Test
    void pointerPositionCommandShouldClearEntityCommandAtPublicApiBoundary() throws IOException {
        String source = Files.readString(SHIP_SOURCE);

        assertTrue(source.contains("public void setPointerTarget(Vec3 target, long durationTicks)"),
                "Pointer position commands should pass through the public ship API");
        assertTrue(source.contains("this.pointer.clearPointerTargetEntity();\n        this.pointerMovement.stop();\n        this.pointer.setPointerTarget(target, durationTicks);"),
                "Pointer position commands should always clear any active entity command before assigning the point");
    }

    @Test
    void pointerEntityCommandShouldClearPositionCommandAtPublicApiBoundary() throws IOException {
        String source = Files.readString(POINTER_SOURCE);

        assertTrue(source.contains("void setPointerTargetEntity(Entity target, long durationTicks)"),
                "Pointer entity commands should pass through the pointer runtime API");
        assertTrue(source.contains("this.pointerTarget = null;\n        this.pointerTargetUntil = 0L;\n        this.pointerTargetEntityId = target.getUUID();"),
                "Pointer entity commands should always clear any active point command before assigning the entity");
        assertTrue(source.contains("Entity entity = serverLevel.getEntity(this.pointerTargetEntityId);"),
                "Pointer entity lookup should resolve the current target entity through the server world");
        assertTrue(source.contains("if (entity == null || !entity.isAlive() || entity.isRemoved()) {\n                return null;\n            }"),
                "Pointer entity lookup should treat dead or removed entities as invalid command targets");
        assertTrue(source.contains("if (e.getUUID().equals(this.pointerTargetEntityId) && e.isAlive() && !e.isRemoved()) {"),
                "Client-side pointer entity lookup should ignore dead or removed rendered entities");
    }
}
