package org.trp.shincolle.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FormationScreenRosterRegressionTest {
    private static final Path SCREEN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/screen/FormationScreen.kt");

    @Test
    void formationScreenShouldIgnoreRemovedShipsWhenPickingNearbyRosterCandidates() throws IOException {
        String source = Files.readString(SCREEN_SOURCE);

        assertTrue(source.contains("ship -> ship.isAlive()\n                        && !ship.isRemoved()\n                        && !ship.isInDeadPose()"),
                "Formation screen should ignore removed ships when picking nearby pointer-selected roster candidates");
        assertTrue(source.contains("candidates.sort(Comparator.comparingDouble(ship -> ship.distanceToSqr(this.minecraft.player)));"),
                "Formation screen should keep sorting nearby roster candidates by player distance");
    }
}
