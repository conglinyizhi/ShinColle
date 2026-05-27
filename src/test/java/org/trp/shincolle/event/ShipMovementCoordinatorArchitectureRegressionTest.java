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
    private static final Path COMMANDS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/command/ModCommands.java");
    private static final Path SHIP_BASE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.java");
    private static final Path MOVEMENT_RECOVERY_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/ShipMovementRecoveryState.java");
    private static final Path AIRCRAFT_ATTACK_GOAL_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/GoalShipAircraftAttack.java");
    private static final Path AIRCRAFT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/EntityAircraftBase.java");
    private static final Path SUMMON_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntitySummonBase.java");
    private static final Path CA_HIME_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/EntityCAHime.java");
    private static final Path HEAVY_CRUISER_NE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/EntityHeavyCruiserNe.java");
    private static final Path BATTLESHIP_RE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/EntityBattleshipRe.java");
    private static final Path DESTROYER_INAZUMA_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/EntityDestroyerInazuma.java");
    private static final Path BATTLESHIP_NAGATO_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/EntityBattleshipNagato.java");

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
        assertTrue(source.contains("private static final Map<PathfinderMob, NavigationOwner> ACTIVE_NAVIGATION_OWNERS"),
                "Coordinator should track which movement channel currently owns the shared navigation");
        assertTrue(source.contains("private final Object ownerToken = new Object();"),
                "Each coordinator instance should have an ownership token for guarded stops");
        assertTrue(source.contains("public static final int PRIORITY_BACKGROUND = 0;"),
                "Coordinator should expose explicit movement priorities instead of relying on tick order");
        assertTrue(source.contains("public static final int PRIORITY_EMERGENCY = 40;"),
                "Emergency movement should have an explicit priority above command and combat movement");
        assertTrue(source.contains("private final int priority;"),
                "Each coordinator should carry the priority of its movement channel");
        assertTrue(source.contains("private record NavigationOwner(Object token, int priority)"),
                "Navigation ownership should remember both owner identity and movement priority");
        assertTrue(source.contains("private boolean shouldYieldToHigherPriorityOwner()"),
                "Lower-priority movement should yield before recalculating paths");
        assertTrue(source.contains("owner != null && owner.token() != this.ownerToken && owner.priority() > this.priority"),
                "Only foreign higher-priority owners should block a movement request");
        assertTrue(source.contains("public void stopAny()"),
                "Coordinator should keep an explicit force-stop escape hatch for lifecycle and admin resets");
        assertTrue(source.contains("if (!ownsNavigation()) {\n            return;\n        }"),
                "Ordinary stop should not clear navigation that has already been claimed by another movement channel");
        assertTrue(source.contains("&& ownsNavigation()\n                && this.lastMoveTarget != null"),
                "Duplicate-move suppression should only apply while this coordinator still owns navigation");
        assertTrue(source.contains("claimNavigation();"),
                "Successful move requests should claim the shared navigation owner");
        assertTrue(source.contains("private void stopAfterFailedMove()"),
                "Failed move requests should centralize owner-aware cleanup");
        assertTrue(source.contains("preserveForeignNavigationOnMoveFailure();"),
                "A foreign failed move should not erase another channel's active path before ownership checks run");
        assertTrue(source.contains("private static final double SAME_POINT_MOVE_TARGET_SQR = 0.25D;"),
                "Coordinator should suppress duplicate point move requests to the same target");
        assertTrue(source.contains("private static final double SAME_ENTITY_MOVE_TARGET_SQR = 2.25D;"),
                "Coordinator should tolerate small entity target drift without constantly recalculating paths");
        assertTrue(source.contains("private static final int SAME_MOVE_REFRESH_INTERVAL_TICKS = 20;"),
                "Coordinator should periodically refresh same-target move requests");
        assertTrue(source.contains("private int lastMoveTick = Integer.MIN_VALUE;"),
                "Coordinator should track when the current suppressed target was last submitted");
        assertTrue(source.contains("private boolean shouldSuppressSameTargetMove(Vec3 target, double sameTargetSqr)"),
                "Repeated move suppression should be centralized for point and entity targets");
        assertTrue(source.contains("shouldSuppressSameTargetMove(targetPos, SAME_ENTITY_MOVE_TARGET_SQR)"),
                "Entity movement should use the wider drift tolerance for moving targets");
        assertTrue(source.contains("shouldSuppressSameTargetMove(target, SAME_POINT_MOVE_TARGET_SQR)"),
                "Point movement should keep the tighter tolerance for fixed commands");
        assertTrue(source.contains("this.mob.tickCount - this.lastMoveTick < SAME_MOVE_REFRESH_INTERVAL_TICKS"),
                "Repeated move suppression should not hide stale navigation indefinitely");
        assertTrue(source.contains("private boolean recordMoveRequest(Vec3 target, boolean moved)"),
                "Coordinator should centralize successful move bookkeeping");
        assertTrue(source.contains("if (!moved) {\n            stopAfterFailedMove();\n            return false;\n        }"),
                "Failed move requests should stop only stale owned navigation and not poison later duplicate-move suppression");
        assertTrue(source.contains("return recordMoveRequest(target, mob.getNavigation().moveTo(target.x, target.y, target.z, speed));"),
                "Point movement should only record duplicate suppression state after navigation accepts the move");
        assertTrue(source.contains("return recordMoveRequest(targetPos, mob.getNavigation().moveTo(target, speed));"),
                "Entity movement should only record duplicate suppression state after navigation accepts the move");
        assertTrue(source.contains("boolean teleportNearLiving(LivingEntity anchor, double verticalOffset)"),
                "Coordinator should expose living-anchor teleport recovery");
        assertTrue(source.contains("public boolean teleportNearLivingIgnoringConfig(LivingEntity anchor, double verticalOffset)"),
                "Coordinator should expose admin recovery teleport without the gameplay teleport toggle");
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
        assertTrue(follow.contains("private final ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();"),
                "Follow-owner goal should use the shared recovery state");
        assertTrue(follow.contains("this.movement.moveTo(moveTarget, this.speed);"),
                "Follow-owner goal should route move requests through the coordinator");
        assertTrue(follow.contains("this.movement.teleportNearLiving(owner, 0.75D)"),
                "Follow-owner teleport should route through the coordinator");
        assertTrue(follow.contains("this.recovery.shouldTryTeleportThrottled(force, distSq, TP_DIST_SQ, TP_COOLDOWN)"),
                "Follow-owner teleport recovery should share throttled recovery policy");
        assertTrue(follow.contains("FollowOwner teleportRecovery"),
                "Follow-owner teleport recovery should emit searchable debug logs");
        assertFalse(follow.contains("checkTP_T"),
                "Follow-owner goal should not keep a separate blind teleport timer");
        assertFalse(follow.contains("checkTP_D"),
                "Follow-owner goal should not keep a separate distance teleport timer");
        assertFalse(follow.contains("ShipTeleportHelper.teleportNearLiving"),
                "Follow-owner goal should not call teleport helper directly");

        assertTrue(guard.contains("private ShipMovementCoordinator movement()"),
                "Guard goal should lazily access the shared movement coordinator after entity construction");
        assertTrue(guard.contains("return ship.guardMovementCoordinator();"),
                "Guard goal should reuse the ship-owned guard movement channel so public guard cleanup can stop it");
        assertFalse(guard.contains("this.movement = ship.guardMovementCoordinator();"),
                "Guard goal must not read ship-owned movement fields during Mob.registerGoals construction");
        assertTrue(guard.contains("movement().moveTo(target, speed)"),
                "Guard goal should route move requests through the coordinator");
        assertTrue(guard.contains("movement().teleportNearPoint(target, 0.75D)"),
                "Guard fixed-point teleport should route through the coordinator");
        assertFalse(guard.contains("ShipTeleportHelper.teleportNearPoint"),
                "Guard goal should not call teleport helper directly");
    }

    @Test
    void pointerAndPassiveCombatShouldUseMovementCoordinator() throws IOException {
        String pointer = Files.readString(POINTER_GOAL_SOURCE);
        String pointerEntity = Files.readString(POINTER_SOURCE);
        String passiveCombat = Files.readString(PASSIVE_COMBAT_SOURCE);

        assertTrue(pointer.contains("private ShipMovementCoordinator movement()"),
                "Pointer move goal should lazily access the shared movement coordinator after entity construction");
        assertTrue(pointer.contains("return ship.pointerMovementCoordinator();"),
                "Pointer move goal should reuse the ship-owned pointer movement channel so public pointer cleanup can stop it");
        assertFalse(pointer.contains("this.movement = ship.pointerMovementCoordinator();"),
                "Pointer move goal must not read ship-owned movement fields during Mob.registerGoals construction");
        assertTrue(pointer.contains("movement().moveTo(target, this.speed)"),
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
        assertTrue(passiveCombat.contains("this.movement = new ShipMovementCoordinator(ship, ShipMovementCoordinator.PRIORITY_COMBAT);"),
                "Passive combat movement should outrank ordinary follow and pickup movement");
        assertTrue(passiveCombat.contains("this.movement.moveTo(target, getPassiveMoveSpeed())"),
                "Passive combat should route chase movement through the coordinator");
        assertTrue(passiveCombat.contains("this.movement.teleportNearLiving(target, 0.75D)"),
                "Passive combat recovery should route teleport through the coordinator");
        assertTrue(passiveCombat.contains("PassiveCombat teleportRecovery"),
                "Passive combat recovery should emit searchable debug logs");
        assertFalse(passiveCombat.contains("this.ship.getNavigation().moveTo(target, getPassiveMoveSpeed())"),
                "Passive combat should not issue raw navigation requests");
        assertFalse(passiveCombat.contains("ShipTeleportHelper.teleportNearLiving"),
                "Passive combat should not bypass the coordinator for teleport recovery");
    }

    @Test
    void mountFollowShouldUseMovementCoordinatorInsteadOfDuplicatingPolicy() throws IOException {
        String mount = Files.readString(MOUNT_SOURCE);

        assertTrue(mount.contains("private final ShipMovementCoordinator movement;"),
                "Mount follow should share the same movement policy as ships");
        assertTrue(mount.contains("this.movement = new ShipMovementCoordinator(mount, ShipMovementCoordinator.PRIORITY_COMMAND);"),
                "Mount follow should create a command-priority coordinator for the mount mob");
        assertTrue(mount.contains("private final ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();"),
                "Mount follow should share the movement recovery state");
        assertTrue(mount.contains("movement.moveTo(owner, 1.0D);"),
                "Mount owner follow should route movement through the coordinator");
        assertTrue(mount.contains("trackAndRecoverLiving(owner, \"owner\");"),
                "Mount owner follow should route teleport recovery through the shared tracker");
        assertTrue(mount.contains("movement.teleportNearLiving(target, 0.75D)"),
                "Mount owner teleport recovery should route through the coordinator");
        assertTrue(mount.contains("trackAndRecoverPoint(guardPos, \"guardBlock\");"),
                "Mount guard follow should route teleport recovery through the shared tracker");
        assertTrue(mount.contains("movement.teleportNearPoint(target, 0.75D)"),
                "Mount guard teleport recovery should route through the coordinator");
        assertTrue(mount.contains("MountFollow teleportRecovery"),
                "Mount follow recovery should emit searchable debug logs");
        assertFalse(mount.contains("lastMoveTarget"),
                "Mount follow should not duplicate repeated move suppression state");
        assertFalse(mount.contains("ShipTeleportHelper.teleportNear"),
                "Mount follow should not bypass the coordinator for teleport recovery");
    }

    @Test
    void formationCommandsShouldUseMovementCoordinatorForImmediateShipMovement() throws IOException {
        String formation = Files.readString(FORMATION_HELPER_SOURCE);

        assertTrue(formation.contains("import org.trp.shincolle.entity.base.ShipMovementCoordinator;"),
                "Formation commands should use the shared movement coordinator for teleport recovery");
        assertTrue(formation.contains("ship.moveGuardTargetTo(new Vec3(x + 0.5D, y, z + 0.5D), 1.2D);"),
                "Block guard formation commands should route immediate movement through the ship-owned guard channel");
        assertTrue(formation.contains("ship.setGuardedEntity(null);"),
                "Guard toggle commands should clear entity guard through the ship API");
        assertFalse(formation.contains("ship.setGuardedEntity(null);\n            ship.setStateFlag(EntityShipBase.STATE_FLAG_DISABLE_GUARD_POS, false);\n            new ShipMovementCoordinator(ship).stop();"),
                "Guard toggle commands should not duplicate guard navigation cleanup outside the ship API");
        assertTrue(formation.contains("ship.moveGuardTargetTo(guarded, 1.2D);"),
                "Entity guard formation commands should route immediate movement through the ship-owned guard channel");
        assertFalse(formation.contains("ship.teleportTo(spawnX, spawnY, spawnZ)"),
                "Formation commands should not directly teleport ships into unchecked positions");
        assertFalse(formation.contains("ship.getNavigation().moveTo"),
                "Formation commands should not issue raw ship navigation requests");
        assertFalse(formation.contains("ship.getNavigation().stop"),
                "Formation commands should not stop ship navigation directly");
    }

    @Test
    void maintenanceCommandsShouldUseMovementCoordinatorForMovementResets() throws IOException {
        String commands = Files.readString(COMMANDS_SOURCE);

        assertTrue(commands.contains("import org.trp.shincolle.entity.base.ShipMovementCoordinator;"),
                "Maintenance commands should use the shared movement coordinator");
        assertTrue(commands.contains("ShipMovementCoordinator movement = new ShipMovementCoordinator(ship);"),
                "Single ship recall should create a coordinator for teleport recovery");
        assertTrue(commands.contains("movement.teleportNearLivingIgnoringConfig(player, 0.5D)"),
                "Single ship recall should route teleport recovery through the coordinator");
        assertTrue(commands.contains("new ShipMovementCoordinator(ship).teleportNearLivingIgnoringConfig(player, 0.5D)"),
                "Selected ship teleport should route teleport recovery through the coordinator");
        assertTrue(commands.contains("new ShipMovementCoordinator(ship).stopAny();"),
                "Owner maintenance commands should explicitly force-stop navigation through the coordinator");
        assertFalse(commands.contains("ShipTeleportHelper.teleportNearLiving"),
                "Maintenance commands should not call teleport helper directly");
        assertFalse(commands.contains("ship.getNavigation().stop()"),
                "Maintenance commands should not stop ship navigation directly");
    }

    @Test
    void shipBaseCoreMovementShouldUseMovementCoordinator() throws IOException {
        String shipBase = Files.readString(SHIP_BASE_SOURCE);

        assertTrue(shipBase.contains("private final ShipMovementCoordinator lifecycleMovement;"),
                "Ship base should keep a coordinator for lifecycle movement stops");
        assertTrue(shipBase.contains("private final ShipMovementCoordinator retreatMovement;"),
                "Ship base should keep a coordinator for retreat movement");
        assertTrue(shipBase.contains("private final ShipMovementCoordinator pickupMovement;"),
                "Ship base should keep a coordinator for pickup movement");
        assertTrue(shipBase.contains("private final ShipMovementCoordinator guardMovement;"),
                "Ship base should keep a coordinator for guard and waypoint movement");
        assertTrue(shipBase.contains("private final ShipMovementCoordinator pointerMovement;"),
                "Ship base should keep a coordinator for pointer command cleanup");
        assertTrue(shipBase.contains("ShipMovementCoordinator pointerMovementCoordinator()"),
                "Ship goals should reuse the ship-owned pointer movement channel");
        assertTrue(shipBase.contains("ShipMovementCoordinator guardMovementCoordinator()"),
                "Ship goals should reuse the ship-owned guard movement channel");
        assertTrue(shipBase.contains("public boolean moveGuardTargetTo(Vec3 target, double speed)"),
                "External guard commands should not create long-lived temporary guard movement owners");
        assertTrue(shipBase.contains("public boolean moveGuardTargetTo(Entity target, double speed)"),
                "External entity-guard commands should reuse the ship-owned guard movement channel");
        assertTrue(shipBase.contains("this.lifecycleMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_EMERGENCY);"),
                "Ship base should create the lifecycle coordinator once per entity");
        assertTrue(shipBase.contains("this.retreatMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_EMERGENCY);"),
                "Low-health retreat should outrank command, combat, follow, and pickup movement");
        assertTrue(shipBase.contains("this.pickupMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_BACKGROUND);"),
                "Auto pickup should stay below player commands, combat, and follow movement");
        assertTrue(shipBase.contains("this.guardMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMMAND);"),
                "Guard movement should be treated as a player command channel");
        assertTrue(shipBase.contains("this.pointerMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMMAND);"),
                "Pointer movement should be treated as a player command channel");
        assertTrue(shipBase.contains("this.retreatMovement.moveTo(owner, 1.25D);"),
                "Low-health retreat should route movement through the coordinator");
        assertTrue(shipBase.contains("if (this.getIsSitting() || this.isInDeadPose()) {\n            this.lifecycleMovement.stopAny();"),
                "Sitting or death-pose transitions should force-stop any active navigation");
        assertTrue(shipBase.contains("} else {\n                this.retreatMovement.stop();\n                if (this.hasPointerTargetEntity())"),
                "Leaving low-health retreat should stop stale retreat navigation before other movement channels take over");
        assertTrue(shipBase.contains("if (retreatingForLowHealth) {\n                this.pickupMovement.stop();\n            } else {\n                tickAutoPickupItems();\n            }"),
                "Low-health retreat should not be overwritten by auto-pickup movement in the same tick");
        assertTrue(shipBase.contains("this.pickupMovement.moveTo(target, 1.0D);"),
                "Auto pickup should route movement through the coordinator");
        assertTrue(shipBase.contains("if (this.hasPointerTarget() || this.hasPointerTargetEntity() || this.getTarget() != null) {\n            this.pickupMovement.stop();"),
                "Auto pickup should clear stale pickup paths when player commands or combat take priority");
        assertTrue(shipBase.contains("this.guardMovement.moveTo(new Vec3(pos.getX() + 0.5D, pos.getY() - 2.0D, pos.getZ() + 0.5D), 1.0D);"),
                "Crane guard approach should route movement through the coordinator");
        assertTrue(shipBase.contains("this.guardMovement.moveTo(new Vec3(targetPos.getX() + 0.5D, targetPos.getY(), targetPos.getZ() + 0.5D), 1.0D);"),
                "Waypoint target switch should route movement through the coordinator");
        assertTrue(shipBase.contains("this.guardMovement.moveTo(new Vec3(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D), 1.0D);"),
                "Waypoint approach should route movement through the coordinator");
        assertTrue(shipBase.contains("private void clearWaypointMoveRuntimeState()"),
                "Ship base should centralize waypoint/crane runtime cleanup");
        assertTrue(shipBase.contains("this.setStateMinor(43, 0);\n        this.setStateTimer(4, 0);\n        this.guardMovement.stop();"),
                "Waypoint/crane cleanup should clear stale runtime state and navigation");
        assertTrue(shipBase.contains("if (this.getStateFlag(11) || this.isOrderedToSit() || this.isLeashed() || this.isVehicle()) {\n            this.clearWaypointMoveRuntimeState();\n            return;\n        }"),
                "Waypoint movement should clear stale runtime state while temporarily unable to move");
        assertTrue(shipBase.contains("if (!guardTarget.isBlock()) {\n            this.clearWaypointMoveRuntimeState();\n            return;\n        }"),
                "Waypoint movement should clear stale runtime state when no block guard target remains");
        assertTrue(shipBase.contains("public void clearPointerTarget() {\n        this.pointer.clearPointerTarget();\n        this.pointerMovement.stop();"),
                "Clearing pointer position commands should stop stale pointer navigation at the public API boundary");
        assertTrue(shipBase.contains("public void clearGuardTarget()"),
                "Ship base should centralize guard target clearing");
        assertTrue(shipBase.contains("this.guardMovement.stop();"),
                "Clearing guard targets should stop stale guard navigation at the public API boundary");
        assertFalse(shipBase.contains("this.getNavigation().moveTo"),
                "Ship base should not issue raw navigation move requests");
        assertFalse(shipBase.contains("this.getNavigation().stop()"),
                "Ship base should not stop navigation directly");
    }

    @Test
    void movementRecoveryStateShouldCentralizeFailureAndStuckCounters() throws IOException {
        String recovery = Files.readString(MOVEMENT_RECOVERY_SOURCE);
        String follow = Files.readString(FOLLOW_GOAL_SOURCE);
        String pointerGoal = Files.readString(POINTER_GOAL_SOURCE);
        String pointerEntity = Files.readString(POINTER_SOURCE);
        String guard = Files.readString(GUARD_GOAL_SOURCE);
        String passiveCombat = Files.readString(PASSIVE_COMBAT_SOURCE);

        assertTrue(recovery.contains("public final class ShipMovementRecoveryState"),
                "Movement recovery counters should live in a small shared runtime object reusable by special ship entities");
        assertTrue(recovery.contains("private static final double PROGRESS_DISTANCE_SQR = 0.04D;"),
                "No-progress detection threshold should be centralized");
        assertTrue(recovery.contains("boolean shouldTryTeleport(boolean force, double distanceSqr, double teleportDistanceSqr, int cooldownTicks)"),
                "Teleport cooldown policy should be centralized");
        assertTrue(recovery.contains("boolean shouldTryTeleportThrottled(boolean force, double distanceSqr, double teleportDistanceSqr, int cooldownTicks)"),
                "Forced teleport recovery should also have an opt-in shared throttle");
        assertTrue(recovery.contains("public int stuckTicks()"),
                "Special ship entities should be able to read shared stuck state without duplicating counters");
        assertTrue(recovery.contains("public boolean isStuckLongerThan(int stuckTickLimit)"),
                "Stuck timeout comparison should be centralized in the recovery state");
        assertTrue(recovery.contains("boolean shouldLogMoveFailure(int currentTick, int intervalTicks)"),
                "Move-failure diagnostic throttling should be centralized in the recovery state");
        assertTrue(recovery.contains("private int lastMoveFailLogTick = Integer.MIN_VALUE;"),
                "Move-failure diagnostic throttling should not be duplicated by callers");
        assertTrue(recovery.contains("this.forcedTeleportCooldown = 0;\n            this.lastProgressPos = currentPos;"),
                "Actual movement progress should clear stale forced teleport throttling");
        assertTrue(follow.contains("private final ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();"),
                "Follow-owner movement should use the shared recovery state");
        assertTrue(pointerGoal.contains("private final ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();"),
                "Pointer position movement should use the shared recovery state");
        assertTrue(guard.contains("private final ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();"),
                "Guard movement should use the shared recovery state");
        assertTrue(pointerEntity.contains("private final ShipMovementRecoveryState pointerTargetEntityRecovery = new ShipMovementRecoveryState();"),
                "Pointer entity movement should use the shared recovery state");
        assertTrue(passiveCombat.contains("private final ShipMovementRecoveryState movementRecovery = new ShipMovementRecoveryState();"),
                "Passive combat movement should use the shared recovery state");
        assertFalse(pointerGoal.contains("private Vec3 lastProgressPos;"),
                "Pointer position movement should not duplicate progress tracking fields");
        assertFalse(guard.contains("private Vec3 lastProgressPos;"),
                "Guard movement should not duplicate progress tracking fields");
        assertFalse(pointerEntity.contains("pointerTargetEntityLastPos"),
                "Pointer entity movement should not duplicate progress tracking fields");
        assertFalse(passiveCombat.contains("passiveLastProgressPos"),
                "Passive combat movement should not duplicate progress tracking fields");
        assertFalse(pointerGoal.contains("stuckTicks() >"),
                "Pointer movement should not duplicate stuck-timeout comparison");
        assertFalse(guard.contains("stuckTicks() >"),
                "Guard movement should not duplicate stuck-timeout comparison");
        assertFalse(pointerEntity.contains("stuckTicks() >"),
                "Pointer entity movement should not duplicate stuck-timeout comparison");
        assertFalse(passiveCombat.contains("stuckTicks() >"),
                "Passive combat movement should not duplicate stuck-timeout comparison");
        assertTrue(pointerGoal.contains("this.recovery.shouldLogMoveFailure(ship.tickCount, POINTER_MOVE_FAIL_LOG_INTERVAL)"),
                "Pointer movement should rate-limit repeated move-failure diagnostics through recovery state");
        assertTrue(guard.contains("this.recovery.shouldLogMoveFailure(ship.tickCount, GUARD_MOVE_FAIL_LOG_INTERVAL)"),
                "Guard movement should rate-limit repeated move-failure diagnostics through recovery state");
        assertTrue(pointerEntity.contains("this.pointerTargetEntityRecovery.shouldLogMoveFailure(this.ship.tickCount,"),
                "Pointer entity movement should rate-limit repeated move-failure diagnostics through recovery state");
        assertTrue(passiveCombat.contains("this.movementRecovery.shouldLogMoveFailure(this.ship.tickCount, PASSIVE_MOVE_FAIL_LOG_INTERVAL)"),
                "Passive combat movement should rate-limit repeated move-failure diagnostics through recovery state");
    }

    @Test
    void mountFollowShouldThrottleForcedTeleportRecovery() throws IOException {
        String mount = Files.readString(MOUNT_SOURCE);

        assertTrue(mount.contains("recovery.shouldTryTeleportThrottled(force, distSq, TP_DIST_SQ, TP_COOLDOWN)"),
                "Mount follow should not retry failed forced teleport recovery every tick");
        assertFalse(mount.contains("if (!force && !recovery.shouldTryTeleport(false"),
                "Mount follow should not bypass the shared throttle once stuck recovery is forced");
    }

    @Test
    void temporaryCombatEntitiesShouldUseMovementCoordinator() throws IOException {
        String aircraftGoal = Files.readString(AIRCRAFT_ATTACK_GOAL_SOURCE);
        String aircraft = Files.readString(AIRCRAFT_SOURCE);
        String summon = Files.readString(SUMMON_SOURCE);

        assertTrue(aircraft.contains("private final ShipMovementCoordinator returnMovement;"),
                "Aircraft return-home movement should use the shared movement coordinator");
        assertTrue(aircraft.contains("private final ShipMovementRecoveryState returnRecovery;"),
                "Aircraft return-home movement should use shared recovery state");
        assertTrue(aircraft.contains("private int returnHomeTicks;"),
                "Aircraft return-home should track failsafe time after recovery starts");
        assertTrue(aircraft.contains("private void startReturnHome()"),
                "Aircraft return-home state changes should be centralized");
        assertTrue(aircraft.contains("private void resumeMission()"),
                "Aircraft mission resume should clear stale return-home state");
        assertTrue(aircraft.contains("this.returnRecovery.clear();\n            this.returnHomeTicks = 0;"),
                "Aircraft return-home state transitions should clear stale recovery counters");
        assertTrue(aircraft.contains("this.returnMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMMAND);"),
                "Aircraft should create a high-priority coordinator for return-home movement");
        assertTrue(aircraft.contains("this.returnRecovery = new ShipMovementRecoveryState();"),
                "Aircraft should create recovery state for return-home movement");
        assertTrue(aircraft.contains("this.returnMovement.moveTo(homePos, 0.5D);"),
                "Aircraft return-home movement should route through the coordinator");
        assertTrue(aircraft.contains("if (trackReturnHomeRecovery(carrier, distSq))"),
                "Aircraft return-home movement should try recovery before giving up");
        assertTrue(aircraft.contains("this.returnMovement.teleportNearLiving(carrier, carrier.getBbHeight() + 0.75D)"),
                "Aircraft return-home recovery should safely teleport near the carrier");
        assertTrue(aircraft.contains("AircraftReturn teleportRecovery"),
                "Aircraft return-home recovery should emit searchable debug logs");
        assertTrue(aircraft.contains("AircraftReturn failsafeDiscard"),
                "Aircraft return-home should eventually release resources if recovery cannot reach the carrier");
        assertTrue(aircraft.contains("this.returnHomeTicks > RETURN_HOME_FAILSAFE_TICKS\n                && this.returnRecovery.isStuckLongerThan(RETURN_HOME_FAILSAFE_TICKS)"),
                "Aircraft return-home failsafe should require sustained no-progress, not only elapsed return time");
        assertTrue(aircraft.contains("returnSummonResources(carrier);\n            this.discard();"),
                "Aircraft return-home failsafe should return resources before discarding");
        assertFalse(aircraft.contains("RETURN_MAX_DISTANCE_SQR"),
                "Aircraft return-home should not discard solely because it is far from the carrier");
        assertFalse(aircraft.contains("this.getNavigation().moveTo(homePos.x, homePos.y, homePos.z"),
                "Aircraft return-home movement should not issue raw navigation requests");

        assertTrue(aircraftGoal.contains("private final ShipMovementCoordinator movement;"),
                "Aircraft attack goal should use the shared movement coordinator");
        assertTrue(aircraftGoal.contains("this.movement = new ShipMovementCoordinator(host, ShipMovementCoordinator.PRIORITY_COMBAT);"),
                "Aircraft attack goal should create a coordinator for its host aircraft");
        assertTrue(aircraftGoal.contains("this.movement.moveTo(this.randPos, speed);"),
                "Aircraft attack movement should route through the coordinator");
        assertTrue(aircraftGoal.contains("private boolean canAttackMissionTarget(Entity targetEntity)"),
                "Aircraft attack goal should centralize mission target viability checks");
        assertTrue(aircraftGoal.contains("this.target = null;\n        this.randPos = null;\n        this.movement.stop();"),
                "Aircraft attack stop should clear attack navigation instead of starting unmanaged cruise movement");
        assertFalse(aircraftGoal.contains("return this.canUse() || (this.target != null && this.target.isAlive() && !this.host.getNavigation().isDone());"),
                "Aircraft attack goal should not continue just because stale navigation is still running");
        assertFalse(aircraftGoal.contains("this.movement.moveTo(this.randPos, 1.0D);"),
                "Aircraft attack stop should not start a new random cruise path outside the goal lifecycle");
        assertFalse(aircraftGoal.contains("host.getNavigation().moveTo"),
                "Aircraft attack goal should not issue raw navigation requests");

        assertTrue(summon.contains("private final ShipMovementCoordinator movement;"),
                "Summon goals should use the shared movement coordinator");
        assertTrue(summon.contains("private final ShipMovementCoordinator returnMovement;"),
                "Summon return-to-carrier movement should use a shared movement coordinator");
        assertTrue(summon.contains("private final ShipMovementRecoveryState returnRecovery;"),
                "Summon return-to-carrier movement should use shared recovery state");
        assertTrue(summon.contains("private int returnTicks;"),
                "Summon return-to-carrier should track failsafe time after recovery starts");
        assertTrue(summon.contains("private void resetReturnState()"),
                "Summon return-to-carrier state resets should be centralized");
        assertTrue(summon.contains("this.returnRecovery.clear();\n        this.returnTicks = 0;"),
                "Summon target reacquisition should clear stale return-to-carrier state");
        assertTrue(summon.contains("this.movement.moveTo(target, 1.2D);"),
                "Summon attack chase should route through the coordinator");
        assertTrue(summon.contains("this.movement.stop();"),
                "Summon attack stop should route through the coordinator");
        assertTrue(summon.contains("this.movement.moveTo(carrier, this.speed);"),
                "Summon carrier follow should route through the coordinator");
        assertTrue(summon.contains("this.returnMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMMAND);"),
                "Summon return-to-carrier should outrank ordinary summon follow movement");
        assertTrue(summon.contains("this.movement = new ShipMovementCoordinator(mob, ShipMovementCoordinator.PRIORITY_COMBAT);"),
                "Summon attack chase should outrank ordinary summon follow movement");
        assertTrue(summon.contains("this.movement = new ShipMovementCoordinator(mob, ShipMovementCoordinator.PRIORITY_NORMAL);"),
                "Summon carrier follow should stay at normal movement priority");
        assertTrue(summon.contains("this.returnMovement.moveTo(carrier, 1.2D);"),
                "Summon return-to-carrier movement should route through the coordinator");
        assertTrue(summon.contains("if (trackReturnRecovery(carrier, distSq))"),
                "Summon return-to-carrier movement should try recovery before giving up");
        assertTrue(summon.contains("this.returnMovement.teleportNearLiving(carrier, 0.75D)"),
                "Summon return-to-carrier recovery should safely teleport near the carrier");
        assertTrue(summon.contains("SummonReturn teleportRecovery"),
                "Summon return-to-carrier recovery should emit searchable debug logs");
        assertTrue(summon.contains("SummonReturn failsafeDiscard"),
                "Summon return-to-carrier should eventually release resources if recovery cannot reach the carrier");
        assertTrue(summon.contains("this.returnTicks > RETURN_FAILSAFE_TICKS\n                    && this.returnRecovery.isStuckLongerThan(RETURN_FAILSAFE_TICKS)"),
                "Summon return-to-carrier failsafe should require sustained no-progress, not only elapsed return time");
        assertTrue(summon.contains("returnSummonResourcesOnce(carrier);\n                this.discard();"),
                "Summon return-to-carrier failsafe should return resources before discarding");
        assertFalse(summon.contains("this.distanceToSqr(carrier) > 1024.0D"),
                "Summon return-to-carrier should not discard solely because it is far from the carrier");
        assertFalse(summon.contains("mob.getNavigation().moveTo"),
                "Summon goals should not issue raw navigation requests");
        assertFalse(summon.contains("mob.getNavigation().stop"),
                "Summon goals should not stop navigation directly");
        assertFalse(summon.contains("this.getNavigation().moveTo(carrier, 1.2D)"),
                "Summon return-to-carrier movement should not issue raw navigation requests");
    }

    @Test
    void specialPushAttackShipsShouldUseMovementCoordinator() throws IOException {
        assertPushAttackUsesMovementCoordinator(Files.readString(CA_HIME_SOURCE), "CA Hime");
        assertPushAttackUsesMovementCoordinator(Files.readString(HEAVY_CRUISER_NE_SOURCE), "Heavy Cruiser Ne");
        assertPushAttackUsesMovementCoordinator(Files.readString(BATTLESHIP_RE_SOURCE), "Battleship Re");
    }

    private void assertPushAttackUsesMovementCoordinator(String source, String shipName) {
        assertTrue(source.contains("private final ShipMovementCoordinator pushMovement;"),
                shipName + " push attack should use the shared movement coordinator");
        assertTrue(source.contains("this.pushMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMBAT);"),
                shipName + " should create a combat-priority coordinator for push attack chase movement");
        assertTrue(source.contains("!this.pushMovement.moveTo(this.targetPush, 1.0D)"),
                shipName + " push attack chase should route failures through the coordinator");
        assertTrue(source.contains("this.pushMovement.reset();"),
                shipName + " should reset duplicate-move state when a new push target is selected");
        assertTrue(source.contains("this.pushMovement.stop();"),
                shipName + " should stop stale navigation when push attack ends");
        assertTrue(source.contains("!this.isPushing)"),
                shipName + " should not replace an active push target with another target");
        assertFalse(source.contains("this.getNavigation().moveTo(this.targetPush"),
                shipName + " push attack chase should not issue raw navigation requests");
    }

    @Test
    void specialShipEventsShouldUseMovementCoordinator() throws IOException {
        String inazuma = Files.readString(DESTROYER_INAZUMA_SOURCE);
        String nagato = Files.readString(BATTLESHIP_NAGATO_SOURCE);

        assertTrue(inazuma.contains("private final ShipMovementCoordinator raidenMovement;"),
                "Raiden gattai follow should use the shared movement coordinator");
        assertTrue(inazuma.contains("private final ShipMovementRecoveryState raidenRecovery;"),
                "Raiden gattai follow should use the shared movement recovery state");
        assertTrue(inazuma.contains("this.raidenMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMMAND);"),
                "Raiden gattai should create a command-priority coordinator for follow movement");
        assertTrue(inazuma.contains("this.raidenRecovery = new ShipMovementRecoveryState();"),
                "Raiden gattai should create a shared recovery state");
        assertTrue(inazuma.contains("this.raidenMovement.stop();"),
                "Raiden gattai close-range stop should route through the coordinator");
        assertTrue(inazuma.contains("this.raidenMovement.moveTo(owner, 1.0D);"),
                "Raiden gattai follow should route movement through the coordinator");
        assertTrue(inazuma.contains("trackRaidenFollowRecovery(owner, distanceSqr);"),
                "Raiden gattai should try recovery even when it is too far for ordinary navigation");
        assertTrue(inazuma.contains("this.raidenRecovery.shouldTryTeleportThrottled(force, distanceSqr,"),
                "Raiden gattai recovery should share throttled teleport policy");
        assertTrue(inazuma.contains("this.raidenMovement.teleportNearLiving(owner, 0.75D)"),
                "Raiden gattai recovery should route teleport through the coordinator");
        assertTrue(inazuma.contains("RaidenFollow teleportRecovery"),
                "Raiden gattai recovery should emit searchable debug logs");
        assertTrue(inazuma.contains("this.raidenMovement.reset();"),
                "Raiden gattai should reset duplicate-move state when it ends");
        assertTrue(inazuma.contains("this.raidenRecovery.clear();"),
                "Raiden gattai should clear recovery state when it ends");
        assertFalse(inazuma.contains("this.getNavigation().moveTo(owner, 1.0D)"),
                "Raiden gattai follow should not issue raw navigation requests");

        assertTrue(nagato.contains("private final ShipMovementCoordinator eventMovement;"),
                "Nagato special event movement should use the shared movement coordinator");
        assertTrue(nagato.contains("this.eventMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMMAND);"),
                "Nagato should create a command-priority coordinator for special event movement");
        assertTrue(nagato.contains("private LivingEntity loveEventMoveTarget;"),
                "Nagato special event movement should have an explicit target lifecycle");
        assertTrue(nagato.contains("private int loveEventMoveTicks;"),
                "Nagato special event movement should have a bounded lifetime");
        assertTrue(nagato.contains("tickLoveEventMovement();"),
                "Nagato should tick special event movement every server tick");
        assertTrue(nagato.contains("this.eventMovement.reset();\n        if (!this.eventMovement.moveTo(target, 1.0D))"),
                "Nagato special event movement should reset duplicate state before starting");
        assertTrue(nagato.contains("!this.eventMovement.moveTo(target, 1.0D)"),
                "Nagato special event movement should route through the coordinator");
        assertTrue(nagato.contains("private void stopLoveEventMovement()"),
                "Nagato special event movement should centralize cleanup");
        assertTrue(nagato.contains("this.eventMovement.stop();"),
                "Nagato special event movement should stop stale navigation when it ends");
        assertTrue(nagato.contains("this.loveEventMoveTicks <= LOVE_EVENT_MOVE_MAX_TICKS"),
                "Nagato special event movement should not run indefinitely");
        assertFalse(nagato.contains("this.getNavigation().moveTo(target, 1.0D)"),
                "Nagato special event movement should not issue raw navigation requests");
    }
}
