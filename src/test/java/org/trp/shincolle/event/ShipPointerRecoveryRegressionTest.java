package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipPointerRecoveryRegressionTest {
    private static final Path POINTER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBasePointer.java");

    @Test
    void pointerEntityCommandShouldClearAfterRepeatedMoveFailures() throws IOException {
        String source = Files.readString(POINTER_SOURCE);

        assertTrue(source.contains("private static final int POINTER_ENTITY_MOVE_FAIL_LIMIT = 40;"),
                "Pointer-entity commands should define a move failure limit");
        assertTrue(source.contains("if (this.pointerTargetEntityMoveFailCount > POINTER_ENTITY_MOVE_FAIL_LIMIT) {"),
                "Pointer-entity commands should clear themselves after repeated move failures");
        assertTrue(source.contains("clearPointerTargetEntity();\n                        this.movement.stop();\n                        return;"),
                "Pointer-entity failure recovery should release the command and stop navigation");
    }

    @Test
    void pointerEntityCommandShouldClearAfterExtendedNoMovement() throws IOException {
        String source = Files.readString(POINTER_SOURCE);

        assertTrue(source.contains("private static final int POINTER_ENTITY_STUCK_TICK_LIMIT = 120;"),
                "Pointer-entity commands should define a stuck timeout");
        assertTrue(source.contains("trackPointerTargetEntityStuckState();"),
                "Pointer-entity commands should track whether the ship is making progress");
        assertTrue(source.contains("if (this.pointerTargetEntityStuckTicks > POINTER_ENTITY_STUCK_TICK_LIMIT) {"),
                "Pointer-entity commands should clear themselves after the ship remains stuck too long");
    }

    @Test
    void pointerEntityCommandShouldResetMovementCoordinatorWhenTargetChanges() throws IOException {
        String source = Files.readString(POINTER_SOURCE);

        assertTrue(source.contains("private final ShipMovementCoordinator movement;"),
                "Pointer-entity commands should share the ship movement coordinator");
        assertTrue(source.contains("this.movement.moveTo(target, POINTER_ENTITY_MOVE_SPEED)"),
                "Pointer-entity chase movement should route through the coordinator");
        assertTrue(source.contains("this.pointerTargetEntityLastPos = this.ship.position();\n        this.movement.reset();"),
                "Pointer-entity commands should reset duplicate move suppression when a new target is assigned");
        assertTrue(source.contains("this.pointerTargetEntityLastPos = null;\n        this.movement.reset();"),
                "Pointer-entity commands should reset movement state when the command clears");
    }
}
