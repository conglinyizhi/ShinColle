package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipPassiveCombatRecoveryRegressionTest {
    private static final Path PASSIVE_COMBAT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBasePassiveCombat.java");
    private static final Path BRAIN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBrainAi.java");
    private static final Path AI_NUMBERS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/ShipAiNumbers.java");

    @Test
    void passiveCombatShouldDropUnreachableTargetsAfterRepeatedFailuresOrNoProgress() throws IOException {
        String passiveCombat = Files.readString(PASSIVE_COMBAT_SOURCE);
        String brain = Files.readString(BRAIN_SOURCE);
        String numbers = Files.readString(AI_NUMBERS_SOURCE);

        assertTrue(numbers.contains("static final int PASSIVE_COMBAT_MOVE_FAIL_LIMIT = 40;"),
                "Passive combat should define a move failure limit");
        assertTrue(numbers.contains("static final int PASSIVE_COMBAT_STUCK_TICK_LIMIT = 120;"),
                "Passive combat should define a stuck timeout");
        assertTrue(numbers.contains("static final int PASSIVE_COMBAT_TELEPORT_COOLDOWN_TICKS = 100;"),
                "Passive combat should use a cooldown before remote teleport recovery");
        assertTrue(numbers.contains("static final double PASSIVE_COMBAT_TELEPORT_DISTANCE_SQ = 256.0D;"),
                "Passive combat should avoid teleport recovery for ordinary short chases");
        assertTrue(brain.contains("ShipCombatDecisionResolver.shouldClearAfterMoveFailures("),
                "Passive combat should use the shared recovery resolver for move-failure clear thresholds");
        assertTrue(brain.contains("ShipCombatDecisionResolver.shouldClearAfterStuck("),
                "Passive combat should use the shared recovery resolver for stuck clear thresholds");
        assertTrue(brain.contains("tryPassiveCombatTeleportRecovery(ship, target, state.distanceSqr(), false)"),
                "Passive combat should try remote teleport recovery before ordinary path movement");
        assertTrue(brain.contains("tryPassiveCombatTeleportRecovery(ship, target, state.distanceSqr(), true)"),
                "Passive combat should force one teleport recovery attempt before clearing unreachable targets");
        assertTrue(brain.contains("ShipBrainRecoverySupport.shouldTryTeleportRecovery(this.combatRecovery, recoveryState,"),
                "Passive combat recovery should route teleport-attempt gating through the shared Brain helper");
        assertTrue(brain.contains("ship.combatMovementCoordinator().teleportNearLiving(target, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)"),
                "Passive combat recovery should use the movement coordinator's safe living teleport");
        assertTrue(brain.contains("PassiveCombat teleportRecovery"),
                "Passive combat recovery should emit searchable debug logs");
        assertTrue(brain.contains("this.combatRecovery.trackProgress(ship.position());"),
                "Passive combat should track whether the ship is making chase progress");
        assertTrue(brain.contains("private final ShipMovementRecoveryState combatRecovery = new ShipMovementRecoveryState();"),
                "Passive combat should use the shared movement recovery state");
        assertTrue(brain.contains("ShipMovementCoordinator movement = ship.combatMovementCoordinator();"),
                "Passive combat Brain behavior should use the ship-owned combat movement coordinator");
        assertTrue(brain.contains("movement.moveTo(target, state.moveSpeed())"),
                "Passive combat chase movement should route through the movement coordinator");
        assertFalse(passiveCombat.contains("ShipMovementCoordinator"),
                "Passive combat entity support should not own movement coordination after Brain migration");
        assertFalse(passiveCombat.contains("ShipMovementRecoveryState"),
                "Passive combat entity support should not own movement recovery after Brain migration");
    }
}
