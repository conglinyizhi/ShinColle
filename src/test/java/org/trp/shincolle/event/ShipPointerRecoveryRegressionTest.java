package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipPointerRecoveryRegressionTest {
    private static final Path POINTER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBasePointer.java");
    private static final Path SHIP_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.java");

    @Test
    void pointerEntityCommandShouldClearAfterRepeatedMoveFailures() throws IOException {
        String source = Files.readString(POINTER_SOURCE);

        assertTrue(source.contains("private static final int POINTER_ENTITY_MOVE_FAIL_LIMIT = 40;"),
                "Pointer-entity commands should define a move failure limit");
        assertTrue(source.contains("if (failCount > POINTER_ENTITY_MOVE_FAIL_LIMIT) {"),
                "Pointer-entity commands should clear themselves after repeated move failures");
        assertTrue(source.contains("tryPointerTargetEntityTeleportRecovery(target, true)"),
                "Pointer-entity commands should try teleport recovery before clearing repeated failures");
        assertTrue(source.contains("clearPointerTargetEntity();\n                        return;"),
                "Pointer-entity failure recovery should release the command through the centralized clear path");
    }

    @Test
    void pointerEntityCommandShouldClearAfterExtendedNoMovement() throws IOException {
        String source = Files.readString(POINTER_SOURCE);

        assertTrue(source.contains("private static final int POINTER_ENTITY_STUCK_TICK_LIMIT = 120;"),
                "Pointer-entity commands should define a stuck timeout");
        assertTrue(source.contains("this.pointerTargetEntityRecovery.trackProgress(this.ship.position());"),
                "Pointer-entity commands should track whether the ship is making progress");
        assertTrue(source.contains("if (this.pointerTargetEntityRecovery.isStuckLongerThan(POINTER_ENTITY_STUCK_TICK_LIMIT)) {"),
                "Pointer-entity commands should clear themselves after the ship remains stuck too long");
    }

    @Test
    void pointerEntityCommandShouldResetMovementCoordinatorWhenTargetChanges() throws IOException {
        String source = Files.readString(POINTER_SOURCE);

        assertTrue(source.contains("private final ShipMovementCoordinator movement;"),
                "Pointer-entity commands should share the ship movement coordinator");
        assertTrue(source.contains("this.movement.moveTo(target, POINTER_ENTITY_MOVE_SPEED)"),
                "Pointer-entity chase movement should route through the coordinator");
        assertTrue(source.contains("this.pointerTargetEntityRecovery.reset(this.ship.position());\n        this.movement.reset();"),
                "Pointer-entity commands should reset duplicate move suppression when a new target is assigned");
        assertTrue(source.contains("this.pointerTargetEntityRecovery.clear();\n        this.movement.stop();"),
                "Pointer-entity commands should stop stale movement when the command clears");
    }

    @Test
    void pointerPositionCommandShouldClearEntityCommandAtPublicApiBoundary() throws IOException {
        String source = Files.readString(SHIP_SOURCE);

        assertTrue(source.contains("public void setPointerTarget(Vec3 target, long durationTicks)"),
                "Pointer position commands should pass through the public ship API");
        assertTrue(source.contains("this.pointer.clearPointerTargetEntity();\n        this.pointer.setPointerTarget(target, durationTicks);"),
                "Pointer position commands should always clear any active entity command before assigning the point");
    }

    @Test
    void pointerEntityCommandShouldClearPositionCommandAtPublicApiBoundary() throws IOException {
        String source = Files.readString(POINTER_SOURCE);

        assertTrue(source.contains("void setPointerTargetEntity(Entity target, long durationTicks)"),
                "Pointer entity commands should pass through the pointer runtime API");
        assertTrue(source.contains("this.pointerTarget = null;\n        this.pointerTargetUntil = 0L;\n        this.pointerTargetEntityId = target.getUUID();"),
                "Pointer entity commands should always clear any active point command before assigning the entity");
    }
}
