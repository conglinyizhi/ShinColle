package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipPassiveCombatRegressionTest {
    private static final Path PASSIVE_COMBAT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBasePassiveCombat.java");

    @Test
    void passiveCombatShouldStillAdvanceOnTargetsEvenWhenFollowOwnerWouldNormallyApply() throws IOException {
        String source = Files.readString(PASSIVE_COMBAT_SOURCE);

        assertTrue(source.contains("if (this.ship.hasPointerTarget() || !hasAttackMeans) {"),
                "Passive combat should only yield to explicit position commands or lack of attack means");
        assertFalse(source.contains("if (this.ship.shouldFollowOwner() || this.ship.hasPointerTarget() || !hasAttackMeans) {"),
                "Passive combat should not abandon active combat movement just because follow-owner is available");
    }
}
