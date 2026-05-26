package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipPassiveCombatRecoveryRegressionTest {
    private static final Path PASSIVE_COMBAT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBasePassiveCombat.java");

    @Test
    void passiveCombatShouldDropUnreachableTargetsAfterRepeatedFailuresOrNoProgress() throws IOException {
        String source = Files.readString(PASSIVE_COMBAT_SOURCE);

        assertTrue(source.contains("private static final int PASSIVE_MOVE_FAIL_LIMIT = 40;"),
                "Passive combat should define a move failure limit");
        assertTrue(source.contains("private static final int PASSIVE_STUCK_TICK_LIMIT = 120;"),
                "Passive combat should define a stuck timeout");
        assertTrue(source.contains("if (failCount > PASSIVE_MOVE_FAIL_LIMIT) {"),
                "Passive combat should clear unreachable targets after repeated move failures");
        assertTrue(source.contains("if (this.movementRecovery.isStuckLongerThan(PASSIVE_STUCK_TICK_LIMIT)) {"),
                "Passive combat should clear targets after remaining stuck for too long");
        assertTrue(source.contains("this.movementRecovery.trackProgress(this.ship.position());"),
                "Passive combat should track whether the ship is making chase progress");
        assertTrue(source.contains("private final ShipMovementRecoveryState movementRecovery = new ShipMovementRecoveryState();"),
                "Passive combat should use the shared movement recovery state");
        assertTrue(source.contains("private final ShipMovementCoordinator movement;"),
                "Passive combat should use the shared movement coordinator");
        assertTrue(source.contains("this.movement.moveTo(target, getPassiveMoveSpeed())"),
                "Passive combat chase movement should route through the movement coordinator");
    }
}
