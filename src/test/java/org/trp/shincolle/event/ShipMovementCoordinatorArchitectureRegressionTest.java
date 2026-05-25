package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipMovementCoordinatorArchitectureRegressionTest {
    private static final Path COORDINATOR_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/ShipMovementCoordinator.java");
    private static final Path FOLLOW_GOAL_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipFollowOwnerGoal.java");
    private static final Path GUARD_GOAL_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipGuardGoal.java");
    private static final Path POINTER_GOAL_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipPointerGoals.java");
    private static final Path POINTER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBasePointer.java");
    private static final Path PASSIVE_COMBAT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBasePassiveCombat.java");
    private static final Path MOUNT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityMountBase.java");
    private static final Path FORMATION_HELPER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/utility/FormationHelper.java");

    @Test
    void movementCoordinatorShouldOwnRepeatedMoveSuppressionAndTeleportSafety() throws IOException {
        String source = Files.readString(COORDINATOR_SOURCE);

        assertTrue(source.contains("final class ShipMovementCoordinator"),
                "Ship movement should have a small coordinator instead of scattering movement policy across goals");
        assertTrue(source.contains("public final class ShipMovementCoordinator"),
                "Coordinator should be available to command and utility orchestration code");
        assertTrue(source.contains("public ShipMovementCoordinator(PathfinderMob mob)"),
                "Coordinator construction should be available outside the entity package");
        assertTrue(source.contains("private final PathfinderMob mob;"),
                "Coordinator should be reusable by ship-attached mobs such as mounts");
        assertTrue(source.contains("private static final double SAME_MOVE_TARGET_SQR = 0.25D;"),
                "Coordinator should suppress duplicate move requests to the same target");
        assertTrue(source.contains("boolean teleportNearLiving(LivingEntity anchor, double verticalOffset)"),
                "Coordinator should expose living-anchor teleport recovery");
        assertTrue(source.contains("boolean teleportNearPoint(Vec3 anchor, double verticalOffset)"),
                "Coordinator should expose fixed-point teleport recovery");
        assertTrue(source.contains("serverLevel.hasChunk(cx, cz)"),
                "Coordinator should centralize loaded-chunk checks before teleporting");
    }

    @Test
    void followAndGuardGoalsShouldUseMovementCoordinator() throws IOException {
        String follow = Files.readString(FOLLOW_GOAL_SOURCE);
        String guard = Files.readString(GUARD_GOAL_SOURCE);

        assertTrue(follow.contains("private final ShipMovementCoordinator movement;"),
                "Follow-owner goal should use the shared movement coordinator");
        assertTrue(follow.contains("this.movement.moveTo(moveTarget, this.speed);"),
                "Follow-owner goal should route move requests through the coordinator");
        assertTrue(follow.contains("this.movement.teleportNearLiving(owner, 0.75D)"),
                "Follow-owner teleport should route through the coordinator");
        assertFalse(follow.contains("ShipTeleportHelper.teleportNearLiving"),
                "Follow-owner goal should not call teleport helper directly");

        assertTrue(guard.contains("private final ShipMovementCoordinator movement;"),
                "Guard goal should use the shared movement coordinator");
        assertTrue(guard.contains("this.movement.moveTo(target, speed)"),
                "Guard goal should route move requests through the coordinator");
        assertTrue(guard.contains("this.movement.teleportNearPoint(target, 0.75D)"),
                "Guard fixed-point teleport should route through the coordinator");
        assertFalse(guard.contains("ShipTeleportHelper.teleportNearPoint"),
                "Guard goal should not call teleport helper directly");
    }

    @Test
    void pointerAndPassiveCombatShouldUseMovementCoordinator() throws IOException {
        String pointer = Files.readString(POINTER_GOAL_SOURCE);
        String pointerEntity = Files.readString(POINTER_SOURCE);
        String passiveCombat = Files.readString(PASSIVE_COMBAT_SOURCE);

        assertTrue(pointer.contains("private final ShipMovementCoordinator movement;"),
                "Pointer move goal should use the shared movement coordinator");
        assertTrue(pointer.contains("this.movement.moveTo(target, this.speed)"),
                "Pointer move goal should route move requests through the coordinator");
        assertFalse(pointer.contains("ship.getNavigation().moveTo(target.x, target.y, target.z"),
                "Pointer move goal should not issue raw navigation requests");

        assertTrue(pointerEntity.contains("private final ShipMovementCoordinator movement;"),
                "Pointer entity chase should use the shared movement coordinator");
        assertTrue(pointerEntity.contains("this.movement.moveTo(target, POINTER_ENTITY_MOVE_SPEED)"),
                "Pointer entity chase should route move requests through the coordinator");
        assertFalse(pointerEntity.contains("this.ship.getNavigation().moveTo(target, POINTER_ENTITY_MOVE_SPEED)"),
                "Pointer entity chase should not issue raw navigation requests");
        assertFalse(pointerEntity.contains("this.ship.getNavigation().stop();"),
                "Pointer entity chase should not stop navigation outside the coordinator");

        assertTrue(passiveCombat.contains("private final ShipMovementCoordinator movement;"),
                "Passive combat should use the shared movement coordinator");
        assertTrue(passiveCombat.contains("this.movement.moveTo(target, getPassiveMoveSpeed())"),
                "Passive combat should route chase movement through the coordinator");
        assertFalse(passiveCombat.contains("this.ship.getNavigation().moveTo(target, getPassiveMoveSpeed())"),
                "Passive combat should not issue raw navigation requests");
    }

    @Test
    void mountFollowShouldUseMovementCoordinatorInsteadOfDuplicatingPolicy() throws IOException {
        String mount = Files.readString(MOUNT_SOURCE);

        assertTrue(mount.contains("private final ShipMovementCoordinator movement;"),
                "Mount follow should share the same movement policy as ships");
        assertTrue(mount.contains("this.movement = new ShipMovementCoordinator(mount);"),
                "Mount follow should create a coordinator for the mount mob");
        assertTrue(mount.contains("movement.moveTo(owner, 1.0D);"),
                "Mount owner follow should route movement through the coordinator");
        assertTrue(mount.contains("movement.teleportNearLiving(owner, 0.75D);"),
                "Mount owner teleport recovery should route through the coordinator");
        assertTrue(mount.contains("movement.teleportNearPoint(guardPos, 0.75D);"),
                "Mount guard teleport recovery should route through the coordinator");
        assertFalse(mount.contains("lastMoveTarget"),
                "Mount follow should not duplicate repeated move suppression state");
        assertFalse(mount.contains("ShipTeleportHelper.teleportNear"),
                "Mount follow should not bypass the coordinator for teleport recovery");
    }

    @Test
    void formationCommandsShouldUseMovementCoordinatorForImmediateShipMovement() throws IOException {
        String formation = Files.readString(FORMATION_HELPER_SOURCE);

        assertTrue(formation.contains("import org.trp.shincolle.entity.base.ShipMovementCoordinator;"),
                "Formation commands should use the shared movement coordinator");
        assertTrue(formation.contains("new ShipMovementCoordinator(ship).moveTo(new Vec3(x + 0.5D, y, z + 0.5D), 1.2D);"),
                "Block guard formation commands should route immediate movement through the coordinator");
        assertTrue(formation.contains("new ShipMovementCoordinator(ship).stop();"),
                "Guard toggle commands should route navigation stops through the coordinator");
        assertTrue(formation.contains("new ShipMovementCoordinator(ship).moveTo(guarded, 1.2D);"),
                "Entity guard formation commands should route immediate movement through the coordinator");
        assertFalse(formation.contains("ship.getNavigation().moveTo"),
                "Formation commands should not issue raw ship navigation requests");
        assertFalse(formation.contains("ship.getNavigation().stop"),
                "Formation commands should not stop ship navigation directly");
    }
}
