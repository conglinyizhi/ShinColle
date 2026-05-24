package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipTeleportHelperRegressionTest {
    private static final Path HELPER =
            Path.of("src/main/java/org/trp/shincolle/utility/ShipTeleportHelper.java");

    @Test
    void teleportShouldKeepShipsOutsideThreeBlockRadius() throws IOException {
        String source = Files.readString(HELPER);
        assertTrue(source.contains("private static final double MIN_PLAYER_DISTANCE_SQ = 9.0D;"),
                "Teleport helper should keep ships at least three blocks away from the player");
        assertTrue(source.contains("if (horizontalDistSq < MIN_PLAYER_DISTANCE_SQ) {"),
                "Teleport helper should reject candidate positions inside the protected radius");
    }

    @Test
    void teleportShouldRejectPositionsInFrontOfPlayerFacing() throws IOException {
        String source = Files.readString(HELPER);
        assertTrue(source.contains("double dot = dx * facing.x + dz * facing.z;"),
                "Teleport helper should evaluate whether a candidate is in front of the player");
        assertTrue(source.contains("if (dot > 0.0D) {"),
                "Teleport helper should reject positions in front of the player's facing direction");
    }

    @Test
    void preferredTeleportOffsetsShouldBiasToRearFlanks() throws IOException {
        String source = Files.readString(HELPER);
        assertTrue(source.contains("private static final int[][] PREFERRED_OFFSETS = {"),
                "Teleport helper should keep an explicit preferred offset list");
        assertTrue(source.contains("{-4, 0}, {-4, -2}, {-4, 2}"),
                "Preferred teleport offsets should start behind the player rather than at the sides or front");
    }
}
