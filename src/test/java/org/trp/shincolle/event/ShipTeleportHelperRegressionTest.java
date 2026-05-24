package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipTeleportHelperRegressionTest {
    private static final Path TELEPORT_HELPER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/utility/ShipTeleportHelper.java");

    @Test
    void teleportNearLivingShouldKeepShipsOutsideThreeBlockRadius() throws IOException {
        String source = Files.readString(TELEPORT_HELPER_SOURCE);

        assertTrue(source.contains("private static final double MIN_PLAYER_DISTANCE_SQ = 9.0D;"),
                "Teleport helper should keep ships at least three blocks away from the player");
        assertTrue(source.contains("if (horizontalDistSq < MIN_PLAYER_DISTANCE_SQ) {"),
                "Teleport helper should reject candidate positions that are too close to the player");
    }

    @Test
    void teleportNearLivingShouldRejectPositionsInFrontOfFacingDirection() throws IOException {
        String source = Files.readString(TELEPORT_HELPER_SOURCE);

        assertTrue(source.contains("if (rejectFront) {"),
                "Teleport helper should have a front-facing rejection branch");
        assertTrue(source.contains("double dot = dx * facing.x + dz * facing.z;"),
                "Teleport helper should project candidate positions onto the player's facing vector");
        assertTrue(source.contains("if (dot > 0.0D) {"),
                "Teleport helper should reject candidate positions in front of the player");
    }
}
