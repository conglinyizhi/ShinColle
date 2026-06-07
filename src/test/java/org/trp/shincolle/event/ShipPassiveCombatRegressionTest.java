package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipPassiveCombatRegressionTest {
    private static final Path PASSIVE_COMBAT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBasePassiveCombat.kt");
    private static final Path SHIP_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt");
    private static final Path SHIP_BRAIN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBrainAi.kt");

    @Test
    void passiveCombatShouldStillAdvanceOnTargetsEvenWhenFollowOwnerWouldNormallyApply() throws IOException {
        String source = Files.readString(PASSIVE_COMBAT_SOURCE);
        String brain = Files.readString(SHIP_BRAIN_SOURCE);

        assertTrue(source.contains("needsMovement && !this.ship.hasPointerTarget()"),
                "Passive combat should only advance on targets when the ship has attack means (ammo or melee)");
        assertTrue(brain.contains("if (state.shouldChase()) {"),
                "Passive combat Brain behavior should chase only when the passive combat state allows it");
        assertFalse(source.contains("this.ship.shouldFollowOwner() || this.ship.hasPointerTarget() || !hasAttackMeans"),
                "Passive combat should not abandon active combat movement just because follow-owner is available");
    }

    @Test
    void passiveCombatShouldBeDrivenByBrainBehaviors() throws IOException {
        String ship = Files.readString(SHIP_SOURCE);
        String supportTick = ship.substring(ship.indexOf("protected void tickAliveLogic()"),
                ship.indexOf("        if (this.isAlive() && (this.tickCount & 7) == 0)"));
        String brain = Files.readString(SHIP_BRAIN_SOURCE);

        assertTrue(brain.contains("private static final class ShipPassiveCombatTargetingBehavior extends Behavior<EntityShipBase>"),
                "Passive target acquisition should live in a Brain behavior");
        assertTrue(brain.contains("ship.tickPassiveCombatTargetingBrain();"),
                "Passive target acquisition should be invoked by Brain");
        assertTrue(brain.contains("ship.tickPassiveCombatActionsBrain(state);"),
                "Passive combat actions should be invoked by Brain combat behavior");
        assertTrue(brain.contains("ShipBrainMemory.PassiveCombatStateMemory state = ship.updatePassiveCombatStateBrain();"),
                "Passive combat decisions should be snapshotted into Brain memory before movement and attacks");
        assertFalse(supportTick.contains("this.passiveCombat.tickTargeting();"),
                "Entity support tick should not directly run passive target acquisition");
        assertFalse(supportTick.contains("this.passiveCombat.tickActions();"),
                "Entity support tick should not directly run passive combat actions");
    }
}
