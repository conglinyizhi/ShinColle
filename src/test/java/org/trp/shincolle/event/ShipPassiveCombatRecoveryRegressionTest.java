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
        assertTrue(source.contains("private static final int PASSIVE_TELEPORT_COOLDOWN_TICKS = 100;"),
                "Passive combat should use a cooldown before remote teleport recovery");
        assertTrue(source.contains("private static final double PASSIVE_TELEPORT_DISTANCE_SQ = 256.0D;"),
                "Passive combat should avoid teleport recovery for ordinary short chases");
        assertTrue(source.contains("if (failCount > PASSIVE_MOVE_FAIL_LIMIT) {"),
                "Passive combat should clear unreachable targets after repeated move failures");
        assertTrue(source.contains("if (this.movementRecovery.isStuckLongerThan(PASSIVE_STUCK_TICK_LIMIT)) {"),
                "Passive combat should clear targets after remaining stuck for too long");
        assertTrue(source.contains("tryPassiveCombatTeleportRecovery(target, distanceSqr, false)"),
                "Passive combat should try remote teleport recovery before ordinary path movement");
        assertTrue(source.contains("tryPassiveCombatTeleportRecovery(target, distanceSqr, true)"),
                "Passive combat should force one teleport recovery attempt before clearing unreachable targets");
        assertTrue(source.contains("this.movementRecovery.shouldTryTeleportThrottled(force, distanceSqr,"),
                "Passive combat forced teleport recovery should be throttled after failed attempts");
        assertTrue(source.contains("this.movement.teleportNearLiving(target, 0.75D)"),
                "Passive combat recovery should use the movement coordinator's safe living teleport");
        assertTrue(source.contains("PassiveCombat teleportRecovery"),
                "Passive combat recovery should emit searchable debug logs");
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
