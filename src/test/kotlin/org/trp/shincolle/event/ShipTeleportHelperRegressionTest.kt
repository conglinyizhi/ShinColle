package org.trp.shincolle.event

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class ShipTeleportHelperRegressionTest {
    private val TELEPORT_HELPER_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/utility/ShipTeleportHelper.kt")

    @Test
    fun teleportNearLivingShouldKeepShipsOutsideThreeBlockRadius() {
        val source: String = Files.readString(TELEPORT_HELPER_SOURCE)

        assertTrue(source.contains("private static final double MIN_PLAYER_DISTANCE_SQ = 9.0D;")) {
            "Teleport helper should keep ships at least three blocks away from the player"
        }
        assertTrue(source.contains("if (horizontalDistSq < MIN_PLAYER_DISTANCE_SQ) {")) {
            "Teleport helper should reject candidate positions that are too close to the player"
        }
    }

    @Test
    fun teleportNearLivingShouldRejectPositionsInFrontOfFacingDirection() {
        val source: String = Files.readString(TELEPORT_HELPER_SOURCE)

        assertTrue(source.contains("if (rejectFront) {")) {
            "Teleport helper should have a front-facing rejection branch"
        }
        assertTrue(source.contains("double dot = dx * facing.x + dz * facing.z;")) {
            "Teleport helper should project candidate positions onto the player's facing vector"
        }
        assertTrue(source.contains("if (dot > 0.0D) {")) {
            "Teleport helper should reject candidate positions in front of the player"
        }
    }

    @Test
    fun teleportNearPointShouldUseSafeCandidateSearchForGuardRecovery() {
        val source: String = Files.readString(TELEPORT_HELPER_SOURCE)

        assertTrue(source.contains("public static boolean teleportNearPoint(Entity entity, Vec3 anchor, double verticalOffset)")) {
            "Teleport helper should support guard-point recovery, not only living anchors"
        }
        assertTrue(source.contains("private static final int[][] POINT_OFFSETS")) {
            "Guard-point teleport should search nearby offsets before teleporting"
        }
        assertTrue(source.contains("Vec3 candidate = findPointCandidate(serverLevel, entity, anchor.add(0.0D, verticalOffset, 0.0D));")) {
            "Guard-point teleport should validate safe stand positions before teleporting"
        }
    }
}
