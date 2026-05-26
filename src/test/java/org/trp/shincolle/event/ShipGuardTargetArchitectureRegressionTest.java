package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipGuardTargetArchitectureRegressionTest {
    private static final Path SHIP_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.java");
    private static final Path GUARD_TARGET_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/ShipGuardTarget.java");
    private static final Path GUARD_GOAL_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipGuardGoal.java");
    private static final Path MOUNT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityMountBase.java");
    private static final Path FORMATION_HELPER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/utility/FormationHelper.java");
    private static final Path EVENT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/event/ModEventBusEvents.java");

    @Test
    void guardStateShouldHaveTypedFacadeOverLegacySlots() throws IOException {
        String ship = Files.readString(SHIP_SOURCE);
        String guardTarget = Files.readString(GUARD_TARGET_SOURCE);

        assertTrue(guardTarget.contains("public record ShipGuardTarget"),
                "Guard target state should have a typed facade over legacy slots");
        assertTrue(ship.contains("public ShipGuardTarget getGuardTarget()"),
                "EntityShipBase should expose guard target through the typed facade");
        assertTrue(ship.contains("public void setGuardBlockTarget(BlockPos pos)"),
                "Block guard writes should be centralized");
        assertTrue(ship.contains("public void clearGuardTarget()"),
                "Guard clearing should be centralized");
    }

    @Test
    void pointerAndWaypointCommandsShouldNotWriteGuardSlotsDirectly() throws IOException {
        String eventSource = Files.readString(EVENT_SOURCE);
        String shipSource = Files.readString(SHIP_SOURCE);

        assertFalse(eventSource.contains("setStateMinor(24, 1)"),
                "Pointer waypoint command must not write the wrong legacy state slot");
        assertFalse(eventSource.contains("STATE_MINOR_GUARD_X"),
                "Pointer waypoint command should use the typed guard write API");
        assertTrue(eventSource.contains("ship.setGuardBlockTarget(guardPos);"),
                "Pointer waypoint command should persist the waypoint guard target");
        assertTrue(shipSource.contains("this.suspendBlockGuardTarget();"),
                "Temporary pointer commands should suspend block guard through the typed API");
    }

    @Test
    void guardMovementShouldNotUsePositiveYAsValiditySentinel() throws IOException {
        String guardGoal = Files.readString(GUARD_GOAL_SOURCE);
        String mount = Files.readString(MOUNT_SOURCE);

        assertFalse(guardGoal.contains("getGuardedPos(1) > 0"),
                "Guard AI should not reject valid modern-world guard targets by Y coordinate");
        assertFalse(mount.contains("getGuardedPos(1) > 0"),
                "Mount guard-follow AI should not reject valid modern-world guard targets by Y coordinate");
        assertTrue(guardGoal.contains("guardTarget.isBlock()"),
                "Guard AI should branch on typed guard target kind");
        assertTrue(mount.contains("guardTarget.isBlock()"),
                "Mount AI should branch on typed guard target kind");
    }

    @Test
    void formationGuardShouldUseCurrentDimension() throws IOException {
        String formationHelper = Files.readString(FORMATION_HELPER_SOURCE);

        assertFalse(formationHelper.contains("setGuardedPos(x, y, z, 0, 1)"),
                "Formation guard should not hard-code overworld dimension");
        assertTrue(formationHelper.contains("ship.setGuardBlockTarget(new BlockPos(x, y, z));"),
                "Formation guard should let the ship compute its current legacy dimension");
        assertTrue(formationHelper.contains("new ShipMovementCoordinator(ship).moveTo(new Vec3(x + 0.5D, y, z + 0.5D), 1.2D);"),
                "Formation guard should route immediate movement through the shared coordinator");
        assertTrue(formationHelper.contains("Shincolle.debugLog(\"Formation summon teleportFailed"),
                "Desk summon should log failed safe teleport recovery");
        assertFalse(formationHelper.contains("ship.teleportTo(spawnX, spawnY, spawnZ)"),
                "Desk summon should not directly teleport ships into unchecked spawn positions");
    }

    @Test
    void mountGuardTeleportShouldUseSharedSafePointHelper() throws IOException {
        String mount = Files.readString(MOUNT_SOURCE);

        assertFalse(mount.contains("mount.teleportTo(guardPos.x, guardPos.y + 0.75D, guardPos.z)"),
                "Mount guard teleport should not jump directly into an unchecked block position");
        assertTrue(mount.contains("trackAndRecoverPoint(guardPos, \"guardBlock\");"),
                "Mount guard movement should route recovery through the shared tracker");
        assertTrue(mount.contains("movement.teleportNearPoint(target, 0.75D)"),
                "Mount guard teleport should share the centralized movement recovery policy");
    }
}
