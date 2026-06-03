package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipMovementCoordinatorArchitectureRegressionTest {
    private static final Path COORDINATOR_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/ShipMovementCoordinator.java");
    private static final Path BRAIN_AI_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBrainAi.java");
    private static final Path SHIP_BRAIN_ACTIVITY_RESOLVER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/ShipBrainActivityResolver.java");
    private static final Path SHIP_BRAIN_RECOVERY_SUPPORT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/ShipBrainRecoverySupport.java");
    private static final Path SHIP_BRAIN_RECOVERY_SUPPORT_TEST_SOURCE =
            Path.of("src/test/java/org/trp/shincolle/entity/base/ShipBrainRecoverySupportTest.java");
    private static final Path SHIP_POINTER_POINT_DECISION_RESOLVER_TEST_SOURCE =
            Path.of("src/test/java/org/trp/shincolle/entity/base/ShipPointerPointDecisionResolverTest.java");
    private static final Path SHIP_FOLLOW_DECISION_RESOLVER_TEST_SOURCE =
            Path.of("src/test/java/org/trp/shincolle/entity/base/ShipFollowDecisionResolverTest.java");
    private static final Path AI_NUMBERS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/ShipAiNumbers.java");
    private static final Path POINTER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBasePointer.java");
    private static final Path PASSIVE_COMBAT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBasePassiveCombat.java");
    private static final Path MOUNT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityMountBase.java");
    private static final Path MOUNT_BRAIN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityMountBrainAi.java");
    private static final Path MOUNT_NUMBERS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/MountAiNumbers.java");
    private static final Path FORMATION_HELPER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/utility/FormationHelper.java");
    private static final Path COMMANDS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/command/ModCommands.java");
    private static final Path SHIP_BASE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.java");
    private static final Path MOVEMENT_RECOVERY_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/ShipMovementRecoveryState.java");
    private static final Path AIRCRAFT_BRAIN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/AircraftBrainAi.java");
    private static final Path AIRCRAFT_NUMBERS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/AircraftAiNumbers.java");
    private static final Path AIRCRAFT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/EntityAircraftBase.java");
    private static final Path SUMMON_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntitySummonBase.java");
    private static final Path SUMMON_BRAIN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntitySummonBrainAi.java");
    private static final Path SUMMON_NUMBERS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/SummonAiNumbers.java");
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
    private static final Path SHINCOLLE_MAIN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle");

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
        assertTrue(source.contains("public boolean isNavigationDone()"),
                "Coordinator should expose read-only navigation state so Brain behaviors do not query navigation directly");
        assertTrue(source.contains("if (!moved) {\n            Shincolle.diagnosticLog(\"[SCMoveDiag] moveFailed mob={} priority={} target={}\",")
                        && source.contains("stopAfterFailedMove();\n            return false;\n        }"),
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
    void movementCoordinatorShouldBeOnlyDirectNavigationEntryPoint() throws IOException {
        assertNoRawNavigationOutsideCoordinator("getNavigation().moveTo");
        assertNoRawNavigationOutsideCoordinator("getNavigation().stop");
        assertNoRawNavigationOutsideCoordinator("getNavigation().createPath");
        assertNoRawNavigationOutsideCoordinator("getNavigation().isDone");
        assertNoRawNavigationOutsideCoordinator("navigation.moveTo");
        assertNoRawNavigationOutsideCoordinator("navigation.stop");
        assertNoRawNavigationOutsideCoordinator("navigation.createPath");
    }

    @Test
    void shipBrainAiShouldUseMovementCoordinator() throws IOException {
        String brain = Files.readString(BRAIN_AI_SOURCE);
        String activityResolver = Files.readString(SHIP_BRAIN_ACTIVITY_RESOLVER_SOURCE);
        String recoverySupport = Files.readString(SHIP_BRAIN_RECOVERY_SUPPORT_SOURCE);
        String recoverySupportTest = Files.readString(SHIP_BRAIN_RECOVERY_SUPPORT_TEST_SOURCE);
        String pointerPointResolverTest = Files.readString(SHIP_POINTER_POINT_DECISION_RESOLVER_TEST_SOURCE);
        String followResolverTest = Files.readString(SHIP_FOLLOW_DECISION_RESOLVER_TEST_SOURCE);
        String numbers = Files.readString(AI_NUMBERS_SOURCE);
        String ship = Files.readString(SHIP_BASE_SOURCE);

        assertTrue(ship.contains("private final ShipMovementCoordinator followOwnerMovement;"),
                "Ship should own a dedicated follow-owner movement channel for Brain behaviors");
        assertTrue(ship.contains("this.followOwnerMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_FOLLOW);"),
                "Follow-owner Brain movement should use an explicit shared coordinator");
        assertTrue(ship.contains("ShipMovementCoordinator followOwnerMovementCoordinator()"),
                "Ship should expose the follow-owner movement channel to Brain behaviors");
        assertTrue(ship.contains("private final ShipMovementCoordinator combatMovement;"),
                "Ship should own a dedicated passive-combat movement channel for Brain behaviors");
        assertTrue(ship.contains("this.combatMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMBAT);"),
                "Passive-combat Brain movement should use an explicit combat-priority coordinator");
        assertTrue(ship.contains("ShipMovementCoordinator combatMovementCoordinator()"),
                "Ship should expose the combat movement channel to Brain behaviors");

        assertTrue(brain.contains("ShipBrainActivityResolver.Mode.COMMAND, Activity.WORK"),
                "Ship Brain should expose a command activity for pointer commands instead of hiding command movement in CORE");
        assertTrue(brain.contains("ShipBrainActivityResolver.Mode.GUARD, Activity.MEET"),
                "Ship Brain should expose a guard activity for guard movement");
        assertTrue(brain.contains("ShipBrainActivityResolver.Mode.FOLLOW, Activity.PLAY"),
                "Ship Brain should expose a follow activity for owner following");
        assertTrue(brain.contains("ShipBrainActivityResolver.Mode.COMBAT, Activity.FIGHT"),
                "Ship Brain should expose a combat activity for target state");
        assertTrue(activityResolver.contains("static List<Mode> resolveActiveModes(State state, boolean following)"),
                "Ship Brain should choose explicit activities through a behavior-testable resolver");
        assertTrue(brain.contains("ShipBrainActivityResolver.resolveActiveModes(state, following)"),
                "Ship Brain tick should delegate activity choice to the tested resolver");
        assertTrue(brain.contains("brain.isActive(activityFor(ShipBrainActivityResolver.Mode.FOLLOW))"),
                "Ship Brain tick should switch activities through the explicit resolver");
        assertTrue(brain.contains("new ShipPointerMoveBehavior()"),
                "Pointer movement should be split into a dedicated Brain behavior");
        assertTrue(brain.contains("ShipPointerEntityDecisionResolver.aimDelayTicks(ship.getLevel())"),
                "Pointer-entity attack cadence should use the behavior-tested resolver");
        assertTrue(brain.contains("ShipPointerEntityDecisionResolver.shouldFireLightAttack(attackState)"),
                "Pointer-entity light attack cadence should use the behavior-tested resolver");
        assertTrue(brain.contains("ShipPointerEntityDecisionResolver.shouldFireHeavyAttack(attackState)"),
                "Pointer-entity heavy attack cadence should use the behavior-tested resolver");
        assertTrue(brain.contains("ShipPointerEntityDecisionResolver.shouldFireMeleeAttack(attackState)"),
                "Pointer-entity melee cadence should use the behavior-tested resolver");
        assertTrue(brain.contains("new ShipGuardMoveBehavior()"),
                "Guard movement should be split into a dedicated Brain behavior");
        assertTrue(brain.contains("new ShipFollowOwnerBehavior()"),
                "Follow-owner movement should be split into a dedicated Brain behavior");
        assertTrue(brain.contains("new ShipCombatMemoryBehavior()"),
                "Combat activity should have a Brain behavior that keeps target memory current");
        assertTrue(brain.contains("new ShipPassiveCombatTargetingBehavior()"),
                "Passive combat target acquisition should be split into a core Brain behavior");
        assertTrue(brain.contains("ship.tickPassiveCombatTargetingBrain();"),
                "Passive combat target acquisition should be driven by Brain rather than entity tick support logic");
        assertTrue(brain.contains("ship.tickPassiveCombatActionsBrain(state);"),
                "Passive combat chase and attacks should be driven by the combat Brain behavior");
        assertFalse(brain.contains("class ShipMovementBehavior"),
                "Ship Brain should not regress to a single large movement behavior");
        assertTrue(brain.contains("brain.setMemory(MemoryModuleType.ATTACK_TARGET, target);"),
                "Ship Brain should populate attack target memory when combat target exists");
        assertTrue(brain.contains("brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);"),
                "Ship Brain should clear attack target memory when combat target is gone");
        assertTrue(brain.contains("BehaviorUtils.setWalkAndLookTargetMemories(ship, BlockPos.containing(target), (float) speed, closeEnoughDist);"),
                "Ship Brain movement behaviors should mirror point movement into WALK_TARGET and LOOK_TARGET memory");
        assertTrue(brain.contains("BehaviorUtils.setWalkAndLookTargetMemories(ship, livingEntity, (float) speed, closeEnoughDist);"),
                "Ship Brain movement behaviors should mirror entity movement into WALK_TARGET and LOOK_TARGET memory");
        assertTrue(brain.contains("BehaviorUtils.lookAtEntity(ship, target);"),
                "Ship Brain should use LOOK_TARGET memory for entity look state");
        assertTrue(recoverySupport.contains("brain.eraseMemory(MemoryModuleType.WALK_TARGET);"),
                "Ship Brain should clear walk memory when movement behaviors stop");
        assertTrue(recoverySupport.contains("brain.eraseMemory(MemoryModuleType.LOOK_TARGET);"),
                "Ship Brain should clear look memory when movement behaviors stop");
        assertTrue(recoverySupport.contains("brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);"),
                "Ship Brain should clear walk failure memory with stale movement targets");
        assertTrue(recoverySupport.contains("final class ShipBrainRecoverySupport"),
                "Ship Brain should extract shared recovery helpers into a dedicated support class");
        assertTrue(recoverySupport.contains("static void clearWalkAndLookMemory(EntityShipBase ship)"),
                "Recovery support should own shared walk/look memory cleanup");
        assertTrue(recoverySupport.contains("static void clearMovementRuntime(EntityShipBase ship,"),
                "Recovery support should own shared movement teardown");
        assertTrue(recoverySupport.contains("static void resetMovementRuntime(EntityShipBase ship,"),
                "Recovery support should own shared recovery reset-and-stop flow");
        assertTrue(recoverySupport.contains("static boolean shouldTryTeleportRecovery(ShipMovementRecoveryState recovery,"),
                "Recovery support should own shared teleport-attempt gating");
        assertTrue(recoverySupport.contains("static int recordMoveFailureAndSync(EntityShipBase ship,"),
                "Recovery support should own shared move-failure counting and sync");
        assertTrue(recoverySupport.contains("static void clearMoveFailuresAndSync(EntityShipBase ship,"),
                "Recovery support should own shared move-failure reset and sync");
        assertTrue(recoverySupportTest.contains("ShipBrainRecoverySupport.shouldTryTeleportRecovery("),
                "Shared recovery support should have a behavior-level test for teleport gating");
        assertTrue(pointerPointResolverTest.contains("ShipPointerPointDecisionResolver.shouldResetForNewTarget("),
                "Pointer point-command resolver should have a behavior-level test for target reset decisions");
        assertTrue(followResolverTest.contains("ShipFollowDecisionResolver.shouldTryTeleport("),
                "Follow resolver should have a behavior-level test for teleport gating");

        assertTrue(brain.contains("movement.moveTo(target, ShipAiNumbers.POINTER_MOVE_SPEED)"),
                "Pointer Brain behavior should route move requests through the pointer coordinator");
        assertTrue(brain.contains("this.pointerRecovery.isStuckLongerThan(ShipAiNumbers.MOVE_STUCK_TICK_LIMIT)"),
                "Pointer point-command behavior should delegate target-switch decisions to a tested resolver");
        assertTrue(brain.contains("ship.distanceToSqr(target) <= ShipAiNumbers.POINTER_MOVE_REACH_SQR"),
                "Pointer point-command behavior should delegate reach decisions to a tested resolver");
        assertTrue(brain.contains("if (failCount > ShipAiNumbers.MOVE_FAIL_LIMIT)"),
                "Pointer point-command behavior should delegate stuck-clear decisions to a tested resolver");
        assertTrue(brain.contains("ShipBrainRecoverySupport.clearMovementRuntime(ship, this.pointerRecovery, ModMemoryModules.SHIP_POINTER_RECOVERY.get(),"),
                "Pointer Brain behavior should reuse the shared movement teardown helper");
        assertTrue(brain.contains("ship.guardMovementCoordinator()"),
                "Guard Brain behavior should reuse the ship-owned guard movement channel");
        assertTrue(brain.contains("ShipGuardDecisionResolver.stopDistanceSqr(guardState)"),
                "Guard Brain stop distance should use the behavior-tested resolver");
        assertTrue(brain.contains("ShipGuardDecisionResolver.shouldMove(guardState)"),
                "Guard Brain movement continuation should use the behavior-tested resolver");
        assertTrue(brain.contains("ShipGuardDecisionResolver.shouldLookAtOwnerOrPlayer("),
                "Guard Brain owner/player look fallback should use the behavior-tested resolver");
        assertTrue(brain.contains("ShipBrainRecoverySupport.clearMovementRuntime(ship, this.guardRecovery, ModMemoryModules.SHIP_GUARD_RECOVERY.get(),"),
                "Guard Brain behavior should reuse the shared movement teardown helper");
        assertTrue(brain.contains("ShipBrainRecoverySupport.resetMovementRuntime(ship, this.guardRecovery, ModMemoryModules.SHIP_GUARD_RECOVERY.get(),"),
                "Guard Brain behavior should reuse the shared reset-and-stop helper when guard movement idles");
        assertTrue(brain.contains("ShipBrainRecoverySupport.shouldTryTeleportRecovery(this.guardRecovery, recoveryState,"),
                "Guard Brain behavior should reuse the shared teleport gating helper");
        assertTrue(brain.contains("ShipBrainRecoverySupport.recordMoveFailureAndSync(ship, this.guardRecovery,"),
                "Guard Brain behavior should reuse the shared move-failure bookkeeping helper");
        assertTrue(brain.contains("ShipGuardDecisionResolver.shouldClearAfterStuck(this.guardRecovery)"),
                "Guard Brain stuck disable threshold should use the shared recovery resolver");
        assertTrue(brain.contains("ShipGuardDecisionResolver.shouldClearAfterMoveFailures(failCount)"),
                "Guard Brain move-failure disable threshold should use the shared recovery resolver");
        assertTrue(brain.contains("movement.moveTo(moveTarget, ShipAiNumbers.FOLLOW_OWNER_SPEED);"),
                "Follow-owner Brain behavior should route movement through the shared coordinator");
        assertTrue(brain.contains("ShipBrainRecoverySupport.clearMovementRuntime(ship, this.followRecovery, ModMemoryModules.SHIP_FOLLOW_RECOVERY.get(),"),
                "Follow-owner Brain behavior should reuse the shared movement teardown helper");
        assertTrue(brain.contains("this.followRecovery.shouldTryTeleportThrottled(force, distSq,"),
                "Follow-owner Brain behavior should delegate teleport gating to a tested resolver");
        assertTrue(brain.contains("ship.idleMovementCoordinator().moveTo(target, ShipAiNumbers.RANDOM_STROLL_SPEED);"),
                "Idle Brain stroll should reuse the ship-owned idle movement coordinator");
        assertTrue(brain.contains("ShipMovementCoordinator movement = ship.combatMovementCoordinator();"),
                "Passive combat Brain behavior should reuse the ship-owned combat movement channel");
        assertTrue(brain.contains("movement.moveTo(target, state.moveSpeed())"),
                "Passive combat Brain behavior should route chase movement through the combat coordinator");
        assertTrue(brain.contains("ShipBrainRecoverySupport.clearMovementRuntime(ship, this.combatRecovery, ModMemoryModules.SHIP_COMBAT_RECOVERY.get(),"),
                "Passive combat Brain behavior should reuse the shared movement teardown helper");
        assertTrue(brain.contains("ShipBrainRecoverySupport.shouldTryTeleportRecovery(this.combatRecovery, recoveryState,"),
                "Passive combat Brain behavior should reuse the shared teleport gating helper");
        assertTrue(brain.contains("ShipBrainRecoverySupport.recordMoveFailureAndSync(ship, this.combatRecovery,"),
                "Passive combat Brain behavior should reuse the shared move-failure bookkeeping helper");
        assertFalse(brain.contains("getNavigation().moveTo"),
                "Ship Brain behaviors should not issue raw navigation requests");
        assertFalse(brain.contains("ShipTeleportHelper"),
                "Ship Brain behaviors should not bypass movement coordination via teleport helper");

        assertTrue(numbers.contains("static final double POINTER_MOVE_SPEED = 1.2D;"),
                "Brain AI movement speeds should be centralized in ShipAiNumbers");
        assertTrue(numbers.contains("static final double POINTER_ENTITY_MOVE_SPEED = 1.1D;"),
                "Pointer-entity Brain movement speed should be centralized in ShipAiNumbers");
        assertTrue(numbers.contains("static final double GUARD_MOVE_SPEED = 1.1D;"),
                "Guard Brain movement speed should be centralized in ShipAiNumbers");
        assertTrue(numbers.contains("static final double FOLLOW_OWNER_SPEED = 1.2D;"),
                "Follow-owner Brain movement speed should be centralized in ShipAiNumbers");
    }

    @Test
    void pointerAndPassiveCombatShouldUseMovementCoordinator() throws IOException {
        String pointer = Files.readString(BRAIN_AI_SOURCE);
        String pointerEntity = Files.readString(POINTER_SOURCE);
        String passiveCombat = Files.readString(PASSIVE_COMBAT_SOURCE);

        assertTrue(pointer.contains("movement.moveTo(target, ShipAiNumbers.POINTER_MOVE_SPEED)"),
                "Pointer Brain behavior should route move requests through the coordinator");
        assertTrue(pointer.contains("movement.moveTo(target, ShipAiNumbers.POINTER_ENTITY_MOVE_SPEED)"),
                "Pointer-entity Brain behavior should route move requests through the coordinator");
        assertTrue(pointer.contains("movement.moveTo(target, state.moveSpeed())"),
                "Passive combat Brain behavior should route chase movement through the coordinator");
        assertTrue(pointer.contains("ship.combatMovementCoordinator().teleportNearLiving(target, ShipAiNumbers.TELEPORT_VERTICAL_OFFSET)"),
                "Passive combat Brain recovery should route teleport through the coordinator");
        assertTrue(pointer.contains("PassiveCombat teleportRecovery"),
                "Passive combat Brain recovery should emit searchable debug logs");
        assertFalse(pointer.contains("ship.getNavigation().moveTo(target.x, target.y, target.z"),
                "Pointer Brain behavior should not issue raw navigation requests");

        assertFalse(pointerEntity.contains("private final ShipMovementCoordinator movement;"),
                "Pointer runtime should not own movement coordination once entity chase is Brain-owned");
        assertFalse(pointerEntity.contains("this.movement.moveTo(target"),
                "Pointer runtime should not route chase movement outside the Brain behavior");
        assertFalse(pointerEntity.contains("this.ship.getNavigation().moveTo(target, POINTER_ENTITY_MOVE_SPEED)"),
                "Pointer entity chase should not issue raw navigation requests");
        assertFalse(pointerEntity.contains("this.ship.getNavigation().stop();"),
                "Pointer entity chase should not stop navigation outside the coordinator");

        assertFalse(passiveCombat.contains("private final ShipMovementCoordinator movement;"),
                "Passive combat support should not own movement coordination once chase is Brain-owned");
        assertFalse(passiveCombat.contains("this.movement.moveTo(target"),
                "Passive combat support should not route chase movement outside the Brain behavior");
        assertFalse(passiveCombat.contains("this.movement.teleportNearLiving(target"),
                "Passive combat support should not route teleport recovery outside the Brain behavior");
        assertFalse(passiveCombat.contains("PassiveCombat teleportRecovery"),
                "Passive combat support should not own movement recovery diagnostics once chase is Brain-owned");
        assertFalse(passiveCombat.contains("this.ship.getNavigation().moveTo(target, getPassiveMoveSpeed())"),
                "Passive combat should not issue raw navigation requests");
        assertFalse(passiveCombat.contains("ShipTeleportHelper.teleportNearLiving"),
                "Passive combat should not bypass the coordinator for teleport recovery");
    }

    @Test
    void mountFollowShouldUseMovementCoordinatorInsteadOfDuplicatingPolicy() throws IOException {
        String mount = Files.readString(MOUNT_SOURCE);
        String mountBrain = Files.readString(MOUNT_BRAIN_SOURCE);
        String mountNumbers = Files.readString(MOUNT_NUMBERS_SOURCE);

        assertTrue(mount.contains("private final ShipMovementCoordinator followMovement;"),
                "Mount should own a shared follow movement coordinator after Brain migration");
        assertTrue(mount.contains("this.followMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMMAND);"),
                "Mount follow should create a command-priority coordinator for the mount mob");
        assertTrue(mount.contains("protected Brain.Provider<EntityMountBase> brainProvider()"),
                "Mount should expose Brain provider after migration");
        assertTrue(mount.contains("return Brain.provider(EntityMountBrainAi.MEMORY_TYPES, EntityMountBrainAi.SENSOR_TYPES);"),
                "Mount Brain provider should route through mount Brain AI helper");
        assertTrue(mount.contains("return EntityMountBrainAi.makeBrain(this, this.brainProvider().makeBrain(dynamic));"),
                "Mount Brain construction should route through mount Brain AI helper");
        assertTrue(mount.contains("this.followMovement.stopAny();"),
                "Mount global AI stop should route navigation cleanup through the movement coordinator");
        assertFalse(mount.contains("this.goalSelector.addGoal"),
                "Mount should no longer register ship-specific GoalSelector behaviors");
        assertFalse(mount.contains("this.getNavigation().stop();"),
                "Mount should not stop navigation outside the movement coordinator");

        assertTrue(mountBrain.contains("private final ShipMovementRecoveryState recovery = new ShipMovementRecoveryState();"),
                "Mount Brain follow should keep explicit recovery state");
        assertTrue(mountBrain.contains("mount.followMovementCoordinator().moveTo(owner, MountAiNumbers.FOLLOW_MOVE_SPEED);"),
                "Mount owner follow should route movement through the coordinator");
        assertTrue(mountBrain.contains("trackAndRecoverPoint(mount, guardPos, \"guardBlock\");"),
                "Mount guard follow should route teleport recovery through the shared tracker");
        assertTrue(mountBrain.contains("MountBrainDecisionResolver.shouldFollowHost(decisionState(mount, host))"),
                "Mount Brain should delegate follow eligibility to a behavior-tested resolver");
        assertTrue(mountBrain.contains("MountBrainDecisionResolver.shouldFireLight(state)"),
                "Mount Brain should delegate light-attack fire eligibility to a behavior-tested resolver");
        assertTrue(mountBrain.contains("MountBrainDecisionResolver.shouldFireHeavy(state)"),
                "Mount Brain should delegate heavy-attack fire eligibility to a behavior-tested resolver");
        assertTrue(mountBrain.contains("MountBrainDecisionResolver.shouldRandomStroll(decisionState(mount, h))"),
                "Mount random stroll should use the resolver that prevents racing follow/guard/pointer movement");
        assertTrue(mountBrain.contains("mount.followMovementCoordinator().teleportNearLiving(target, MountAiNumbers.TELEPORT_VERTICAL_OFFSET)"),
                "Mount owner teleport recovery should route through the coordinator");
        assertTrue(mountBrain.contains("mount.followMovementCoordinator().teleportNearPoint(target, MountAiNumbers.TELEPORT_VERTICAL_OFFSET)"),
                "Mount guard teleport recovery should route through the coordinator");
        assertTrue(mountBrain.contains("syncAttackTargetMemory(mount, brain);"),
                "Mount Brain should mirror its combat target into ATTACK_TARGET memory each tick");
        assertTrue(mountBrain.contains("BehaviorUtils.setWalkAndLookTargetMemories(mount, BlockPos.containing(target), (float) speed, closeEnoughDist);"),
                "Mount point movement should mirror coordinator targets into WALK_TARGET and LOOK_TARGET memory");
        assertTrue(mountBrain.contains("BehaviorUtils.setWalkAndLookTargetMemories(mount, livingTarget, (float) speed, closeEnoughDist);"),
                "Mount entity movement should mirror coordinator targets into WALK_TARGET and LOOK_TARGET memory");
        assertTrue(mountBrain.contains("BehaviorUtils.lookAtEntity(mount, target);"),
                "Mount look behavior should publish LOOK_TARGET memory");
        assertTrue(mountBrain.contains("brain.eraseMemory(MemoryModuleType.WALK_TARGET);"),
                "Mount Brain should clear stale walk memory when movement behavior stops");
        assertTrue(mountBrain.contains("brain.eraseMemory(MemoryModuleType.LOOK_TARGET);"),
                "Mount Brain should clear stale look memory when movement behavior stops");
        assertTrue(mountBrain.contains("brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);"),
                "Mount Brain should clear stale path-failure memory when movement behavior stops");
        assertTrue(mountBrain.contains("MountFollow teleportRecovery"),
                "Mount follow recovery should emit searchable debug logs");
        assertFalse(mountBrain.contains("ShipTeleportHelper.teleportNear"),
                "Mount follow should not bypass the coordinator for teleport recovery");
        assertTrue(mountNumbers.contains("static final double FOLLOW_MOVE_SPEED = 1.0D;"),
                "Mount follow speed should be centralized");
        assertTrue(mountNumbers.contains("static final int FOLLOW_TELEPORT_COOLDOWN_TICKS = 100;"),
                "Mount follow teleport cooldown should be centralized");
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
        String brain = Files.readString(BRAIN_AI_SOURCE);
        String pointerEntity = Files.readString(POINTER_SOURCE);
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
        assertTrue(brain.contains("private final ShipMovementRecoveryState pointerRecovery = new ShipMovementRecoveryState();"),
                "Pointer Brain movement should keep explicit recovery state after migration");
        assertTrue(brain.contains("private final ShipMovementRecoveryState guardRecovery = new ShipMovementRecoveryState();"),
                "Guard Brain movement should keep explicit recovery state after migration");
        assertTrue(brain.contains("private final ShipMovementRecoveryState followRecovery = new ShipMovementRecoveryState();"),
                "Follow-owner Brain movement should keep explicit recovery state after migration");
        assertTrue(brain.contains("private final ShipMovementRecoveryState pointerRecovery = new ShipMovementRecoveryState();"),
                "Pointer entity Brain movement should reuse the shared pointer recovery state");
        assertTrue(brain.contains("private final ShipMovementRecoveryState combatRecovery = new ShipMovementRecoveryState();"),
                "Passive combat Brain movement should use the shared recovery state");
        assertFalse(pointerEntity.contains("pointerTargetEntityLastPos"),
                "Pointer entity movement should not duplicate progress tracking fields");
        assertFalse(passiveCombat.contains("passiveLastProgressPos"),
                "Passive combat movement should not duplicate progress tracking fields");
        assertTrue(brain.contains("this.pointerRecovery.shouldLogMoveFailure(ship.tickCount, ShipAiNumbers.MOVE_FAIL_LOG_INTERVAL)"),
                "Pointer Brain movement should rate-limit repeated move-failure diagnostics through recovery state");
        assertTrue(brain.contains("this.guardRecovery.shouldLogMoveFailure(ship.tickCount, ShipAiNumbers.MOVE_FAIL_LOG_INTERVAL)"),
                "Guard Brain movement should rate-limit repeated move-failure diagnostics through recovery state");
        assertFalse(pointerEntity.contains("stuckTicks() >"),
                "Pointer entity movement should not duplicate stuck-timeout comparison");
        assertFalse(passiveCombat.contains("stuckTicks() >"),
                "Passive combat movement should not duplicate stuck-timeout comparison");
        assertTrue(brain.contains("this.pointerRecovery.shouldLogMoveFailure(ship.tickCount,\n                            ShipAiNumbers.POINTER_ENTITY_MOVE_FAIL_LOG_INTERVAL)"),
                "Pointer entity Brain movement should rate-limit repeated move-failure diagnostics through recovery state");
        assertTrue(brain.contains("this.combatRecovery.shouldLogMoveFailure(ship.tickCount,\n                            ShipAiNumbers.PASSIVE_COMBAT_MOVE_FAIL_LOG_INTERVAL)"),
                "Passive combat movement should rate-limit repeated move-failure diagnostics through recovery state");
        assertFalse(passiveCombat.contains("ShipMovementRecoveryState"),
                "Passive combat support should not own movement recovery state once chase is Brain-owned");
    }

    @Test
    void mountFollowShouldThrottleForcedTeleportRecovery() throws IOException {
        String mount = Files.readString(MOUNT_BRAIN_SOURCE);

        assertTrue(mount.contains("this.recovery.shouldTryTeleportThrottled(force, distSq,"),
                "Mount follow should not retry failed forced teleport recovery every tick");
        assertFalse(mount.contains("if (!force && !recovery.shouldTryTeleport(false"),
                "Mount follow should not bypass the shared throttle once stuck recovery is forced");
    }

    @Test
    void temporaryCombatEntitiesShouldUseMovementCoordinator() throws IOException {
        String aircraft = Files.readString(AIRCRAFT_SOURCE);
        String aircraftBrain = Files.readString(AIRCRAFT_BRAIN_SOURCE);
        String aircraftNumbers = Files.readString(AIRCRAFT_NUMBERS_SOURCE);
        String summon = Files.readString(SUMMON_SOURCE);
        String summonBrain = Files.readString(SUMMON_BRAIN_SOURCE);
        String summonNumbers = Files.readString(SUMMON_NUMBERS_SOURCE);

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
        assertTrue(aircraft.contains("private final ShipMovementCoordinator attackMovement;"),
                "Aircraft attack movement should use a ship-owned coordinator after Brain migration");
        assertTrue(aircraft.contains("this.returnRecovery = new ShipMovementRecoveryState();"),
                "Aircraft should create recovery state for return-home movement");
        assertTrue(aircraft.contains("this.returnMovement.moveTo(homePos, AircraftAiNumbers.RETURN_HOME_SPEED);"),
                "Aircraft return-home movement should route through the coordinator");
        assertTrue(aircraft.contains("if (trackReturnHomeRecovery(carrier, distSq))"),
                "Aircraft return-home movement should try recovery before giving up");
        assertTrue(aircraft.contains("this.returnMovement.teleportNearLiving(carrier, carrier.getBbHeight() + AircraftAiNumbers.RETURN_HOME_TELEPORT_EXTRA)"),
                "Aircraft return-home recovery should safely teleport near the carrier");
        assertTrue(aircraft.contains("AircraftReturn teleportRecovery"),
                "Aircraft return-home recovery should emit searchable debug logs");
        assertTrue(aircraft.contains("AircraftReturn failsafeDiscard"),
                "Aircraft return-home should eventually release resources if recovery cannot reach the carrier");
        assertTrue(aircraft.contains("this.returnHomeTicks > AircraftAiNumbers.RETURN_HOME_FAILSAFE_TICKS\n                && this.returnRecovery.isStuckLongerThan(AircraftAiNumbers.RETURN_HOME_FAILSAFE_TICKS)"),
                "Aircraft return-home failsafe should require sustained no-progress, not only elapsed return time");
        assertTrue(aircraft.contains("returnSummonResources(carrier);\n            this.discard();"),
                "Aircraft return-home failsafe should return resources before discarding");
        assertTrue(aircraft.contains("protected Brain.Provider<EntityAircraftBase> brainProvider()"),
                "Aircraft should expose Brain provider after migration");
        assertTrue(aircraft.contains("return Brain.provider(AircraftBrainAi.MEMORY_TYPES, AircraftBrainAi.SENSOR_TYPES);"),
                "Aircraft Brain provider should route through aircraft Brain AI helper");
        assertTrue(aircraft.contains("return AircraftBrainAi.makeBrain(this, this.brainProvider().makeBrain(dynamic));"),
                "Aircraft Brain construction should route through aircraft Brain AI helper");
        assertTrue(aircraft.contains("AircraftBrainAi.tick(serverLevel, this);"),
                "Aircraft server AI step should tick aircraft Brain AI helper");
        assertTrue(aircraft.contains("public ShipMovementCoordinator attackMovementCoordinator()"),
                "Aircraft should expose the attack movement coordinator to Brain behaviors");
        assertTrue(aircraft.contains("if (entity instanceof EntityShipBase ship && ship.isAlive() && !ship.isRemoved()) {"),
                "Aircraft carrier lookup should reject dead or removed carrier entities");
        assertTrue(aircraft.contains("Entity entity = serverLevel.getEntity(this.targetId);"),
                "Aircraft mission-target lookup should resolve the current target entity through the server world");
        assertTrue(aircraft.contains("if (entity == null || !entity.isAlive() || entity.isRemoved()) {\n            return null;\n        }"),
                "Aircraft mission-target lookup should treat dead or removed entities as invalid mission targets");
        assertFalse(aircraft.contains("RETURN_MAX_DISTANCE_SQR"),
                "Aircraft return-home should not discard solely because it is far from the carrier");
        assertFalse(aircraft.contains("this.getNavigation().moveTo(homePos.x, homePos.y, homePos.z"),
                "Aircraft return-home movement should not issue raw navigation requests");
        assertFalse(aircraft.contains("this.goalSelector.addGoal"),
                "Aircraft should no longer register combat behaviors through GoalSelector");

        assertTrue(aircraftBrain.contains("brain.addActivity(Activity.CORE, ImmutableList.of("),
                "Aircraft should build its combat loop through Brain activities");
        assertTrue(aircraftBrain.contains("new AircraftAttackBehavior()"),
                "Aircraft Brain should own the dedicated attack behavior");
        assertTrue(aircraftBrain.contains("host.attackMovementCoordinator().reset();"),
                "Aircraft attack behavior should reset the shared movement coordinator at start");
        assertTrue(aircraftBrain.contains("host.attackMovementCoordinator().moveTo(this.randPos, speed);"),
                "Aircraft attack movement should route through the coordinator");
        assertTrue(aircraftBrain.contains("AircraftBrainDecisionResolver.shouldStartAttack(decisionState(host, targetEntity))"),
                "Aircraft attack behavior should delegate start eligibility to a behavior-tested resolver");
        assertTrue(aircraftBrain.contains("AircraftBrainDecisionResolver.canAttackMissionTarget(decisionState(host, targetEntity))"),
                "Aircraft attack behavior should centralize mission target viability checks in the resolver");
        assertTrue(aircraftBrain.contains("AircraftBrainDecisionResolver.attackMoveSpeed(state)"),
                "Aircraft attack behavior should delegate chase speed selection to a behavior-tested resolver");
        assertTrue(aircraftBrain.contains("AircraftBrainDecisionResolver.shouldFire(state)"),
                "Aircraft attack behavior should delegate fire eligibility to a behavior-tested resolver");
        assertTrue(aircraftBrain.contains("this.target = null;\n            this.randPos = null;"),
                "Aircraft attack stop should clear cached target state");
        assertTrue(aircraftBrain.contains("host.attackMovementCoordinator().stop();"),
                "Aircraft attack stop should clear managed navigation instead of starting unmanaged cruise movement");
        assertTrue(aircraftBrain.contains("syncAttackTargetMemory(aircraft);"),
                "Aircraft Brain should mirror mission targets into ATTACK_TARGET memory each tick");
        assertTrue(aircraftBrain.contains("BehaviorUtils.setWalkAndLookTargetMemories(aircraft, BlockPos.containing(target), (float) speed, closeEnoughDist);"),
                "Aircraft attack cruise movement should mirror coordinator targets into WALK_TARGET and LOOK_TARGET memory");
        assertTrue(aircraftBrain.contains("BehaviorUtils.lookAtEntity(aircraft, livingTarget);"),
                "Aircraft attack behavior should publish LOOK_TARGET memory for living mission targets");
        assertTrue(aircraftBrain.contains("brain.eraseMemory(MemoryModuleType.WALK_TARGET);"),
                "Aircraft Brain should clear stale walk memory when attack behavior stops");
        assertTrue(aircraftBrain.contains("brain.eraseMemory(MemoryModuleType.LOOK_TARGET);"),
                "Aircraft Brain should clear stale look memory when attack behavior stops");
        assertTrue(aircraftBrain.contains("brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);"),
                "Aircraft Brain should clear stale path-failure memory when attack behavior stops");
        assertFalse(aircraftBrain.contains("host.getNavigation().moveTo"),
                "Aircraft attack behavior should not issue raw navigation requests");
        assertTrue(aircraftNumbers.contains("static final int ATTACK_ACTIVATION_TICKS = 20;"),
                "Aircraft attack activation timing should be centralized");
        assertTrue(aircraftNumbers.contains("static final double ATTACK_SPEED_FAST = 0.6D;"),
                "Aircraft attack movement speed should be centralized");
        assertTrue(aircraftNumbers.contains("static final double RETURN_HOME_SPEED = 0.5D;"),
                "Aircraft return-home movement speed should be centralized");

        assertTrue(summon.contains("private final ShipMovementCoordinator returnMovement;"),
                "Summon return-to-carrier movement should use a shared movement coordinator");
        assertTrue(summon.contains("private final ShipMovementCoordinator attackMovement;"),
                "Summon attack movement should use a ship-owned coordinator after Brain migration");
        assertTrue(summon.contains("private final ShipMovementCoordinator followMovement;"),
                "Summon follow movement should use a ship-owned coordinator after Brain migration");
        assertTrue(summon.contains("private final ShipMovementRecoveryState returnRecovery;"),
                "Summon return-to-carrier movement should use shared recovery state");
        assertTrue(summon.contains("private int returnTicks;"),
                "Summon return-to-carrier should track failsafe time after recovery starts");
        assertTrue(summon.contains("private void resetReturnState()"),
                "Summon return-to-carrier state resets should be centralized");
        assertTrue(summon.contains("this.returnRecovery.clear();\n        this.returnTicks = 0;"),
                "Summon target reacquisition should clear stale return-to-carrier state");
        assertTrue(summon.contains("this.returnMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMMAND);"),
                "Summon return-to-carrier should outrank ordinary summon follow movement");
        assertTrue(summon.contains("this.attackMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_COMBAT);"),
                "Summon attack chase should outrank ordinary summon follow movement");
        assertTrue(summon.contains("this.followMovement = new ShipMovementCoordinator(this, ShipMovementCoordinator.PRIORITY_NORMAL);"),
                "Summon carrier follow should stay at normal movement priority");
        assertTrue(summon.contains("this.returnMovement.moveTo(carrier, SummonAiNumbers.RETURN_MOVE_SPEED);"),
                "Summon return-to-carrier movement should route through the coordinator");
        assertTrue(summon.contains("if (trackReturnRecovery(carrier, distSq))"),
                "Summon return-to-carrier movement should try recovery before giving up");
        assertTrue(summon.contains("this.returnMovement.teleportNearLiving(carrier, SummonAiNumbers.TELEPORT_VERTICAL_OFFSET)"),
                "Summon return-to-carrier recovery should safely teleport near the carrier");
        assertTrue(summon.contains("SummonReturn teleportRecovery"),
                "Summon return-to-carrier recovery should emit searchable debug logs");
        assertTrue(summon.contains("SummonReturn failsafeDiscard"),
                "Summon return-to-carrier should eventually release resources if recovery cannot reach the carrier");
        assertTrue(summon.contains("this.returnTicks > SummonAiNumbers.RETURN_FAILSAFE_TICKS\n                    && this.returnRecovery.isStuckLongerThan(SummonAiNumbers.RETURN_FAILSAFE_TICKS)"),
                "Summon return-to-carrier failsafe should require sustained no-progress, not only elapsed return time");
        assertTrue(summon.contains("returnSummonResourcesOnce(carrier);\n                this.discard();"),
                "Summon return-to-carrier failsafe should return resources before discarding");
        assertTrue(summon.contains("protected Brain.Provider<EntitySummonBase> brainProvider()"),
                "Summon entities should expose Brain providers after migration");
        assertTrue(summon.contains("return Brain.provider(EntitySummonBrainAi.MEMORY_TYPES, EntitySummonBrainAi.SENSOR_TYPES);"),
                "Summon Brain provider should route through the summon Brain AI helper");
        assertTrue(summon.contains("return EntitySummonBrainAi.makeBrain(this, this.brainProvider().makeBrain(dynamic));"),
                "Summon Brain construction should route through the summon Brain AI helper");
        assertTrue(summon.contains("if (entity instanceof EntityShipBase ship && ship.isAlive() && !ship.isRemoved()) {"),
                "Summon carrier lookup should reject dead or removed carrier entities");
        assertTrue(summon.contains("Entity entity = serverLevel.getEntity(this.targetId);"),
                "Summon mission-target lookup should resolve the current target entity through the server world");
        assertTrue(summon.contains("if (entity == null || !entity.isAlive() || entity.isRemoved()) {\n            return null;\n        }"),
                "Summon mission-target lookup should treat dead or removed entities as invalid mission targets");
        assertFalse(summon.contains("this.goalSelector.addGoal"),
                "Summon entity should no longer register summon behaviors through GoalSelector");
        assertFalse(summon.contains("this.distanceToSqr(carrier) > 1024.0D"),
                "Summon return-to-carrier should not discard solely because it is far from the carrier");
        assertFalse(summon.contains("mob.getNavigation().moveTo"),
                "Summon goals should not issue raw navigation requests");
        assertFalse(summon.contains("mob.getNavigation().stop"),
                "Summon goals should not stop navigation directly");
        assertFalse(summon.contains("this.getNavigation().moveTo(carrier, 1.2D)"),
                "Summon return-to-carrier movement should not issue raw navigation requests");

        assertTrue(summonBrain.contains("summon.attackMovementCoordinator().moveTo(target, SummonAiNumbers.ATTACK_MOVE_SPEED);"),
                "Summon attack Brain behavior should route chase movement through the coordinator");
        assertTrue(summonBrain.contains("summon.followMovementCoordinator().moveTo(carrier, SummonAiNumbers.FOLLOW_CARRIER_SPEED);"),
                "Summon follow Brain behavior should route carrier follow through the coordinator");
        assertTrue(summonBrain.contains("summon.followMovementCoordinator().moveTo(target, SummonAiNumbers.RANDOM_STROLL_SPEED);"),
                "Summon idle Brain behavior should route stroll movement through the coordinator");
        assertTrue(summonBrain.contains("SummonBrainDecisionResolver.shouldFollowCarrier(decisionState(summon, carrier))"),
                "Summon Brain should delegate carrier-follow eligibility to a behavior-tested resolver");
        assertTrue(summonBrain.contains("SummonBrainDecisionResolver.shouldChaseAttackTarget(state)"),
                "Summon attack chase should delegate range boundary decisions to a behavior-tested resolver");
        assertTrue(summonBrain.contains("SummonBrainDecisionResolver.shouldPerformAttack(state)"),
                "Summon attack execution should delegate cooldown and range checks to a behavior-tested resolver");
        assertTrue(summonBrain.contains("SummonBrainDecisionResolver.shouldRandomStroll(decisionState(summon, summon.getCarrier()))"),
                "Summon random stroll should use the resolver that prevents racing carrier-follow movement");
        assertTrue(summonBrain.contains("syncAttackTargetMemory(summon, brain);"),
                "Summon Brain should mirror its combat target into ATTACK_TARGET memory each tick");
        assertTrue(summonBrain.contains("BehaviorUtils.setWalkAndLookTargetMemories(summon, BlockPos.containing(target), (float) speed, closeEnoughDist);"),
                "Summon point movement should mirror coordinator targets into WALK_TARGET and LOOK_TARGET memory");
        assertTrue(summonBrain.contains("BehaviorUtils.setWalkAndLookTargetMemories(summon, target, (float) speed, closeEnoughDist);"),
                "Summon entity movement should mirror coordinator targets into WALK_TARGET and LOOK_TARGET memory");
        assertTrue(summonBrain.contains("BehaviorUtils.lookAtEntity(summon, target);"),
                "Summon look behavior should publish LOOK_TARGET memory");
        assertTrue(summonBrain.contains("brain.eraseMemory(MemoryModuleType.WALK_TARGET);"),
                "Summon Brain should clear stale walk memory when movement behavior stops");
        assertTrue(summonBrain.contains("brain.eraseMemory(MemoryModuleType.LOOK_TARGET);"),
                "Summon Brain should clear stale look memory when movement behavior stops");
        assertTrue(summonBrain.contains("brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);"),
                "Summon Brain should clear stale path-failure memory when movement behavior stops");
        assertFalse(summonBrain.contains("private final ShipMovementCoordinator movement;"),
                "Summon Brain attack behavior should not keep dead coordinator fields outside the entity-owned channels");
        assertFalse(summonBrain.contains("new net.minecraft.world.entity.ai.goal"),
                "Summon Brain helper should not embed goal-based AI implementations");

        assertTrue(summonNumbers.contains("static final double ATTACK_MOVE_SPEED = 1.2D;"),
                "Summon attack move speed should be centralized");
        assertTrue(summonNumbers.contains("static final double FOLLOW_CARRIER_SPEED = 1.2D;"),
                "Summon follow move speed should be centralized");
        assertTrue(summonNumbers.contains("static final int ATTACK_DELAY_TICKS = 20;"),
                "Summon attack cooldown should be centralized");
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

    private static void assertNoRawNavigationOutsideCoordinator(String forbidden) throws IOException {
        try (Stream<Path> files = Files.walk(SHINCOLLE_MAIN_SOURCE)) {
            String offenders = files
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> !path.equals(COORDINATOR_SOURCE))
                    .filter(path -> contains(path, forbidden))
                    .map(Path::toString)
                    .sorted()
                    .reduce("", (left, right) -> left + "\n" + right);

            assertTrue(offenders.isEmpty(),
                    "Only ShipMovementCoordinator should directly use " + forbidden + "; offenders:" + offenders);
        }
    }

    private static boolean contains(Path path, String text) {
        try {
            return Files.readString(path).contains(text);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to inspect " + path, e);
        }
    }
}
