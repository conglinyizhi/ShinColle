package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrainMemorySensorArchitectureRegressionTest {
    private static final List<Path> BRAIN_HELPERS = List.of(
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBrainAi.java"),
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityMountBrainAi.java"),
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntitySummonBrainAi.java"),
            Path.of("src/main/java/org/trp/shincolle/entity/AircraftBrainAi.java")
    );
    private static final Path SHIP_BRAIN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBrainAi.java");
    private static final Path SHIP_BRAIN_MEMORY_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/ShipBrainMemory.java");
    private static final Path MEMORY_MODULES_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModMemoryModules.java");
    private static final Path MOD_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/Shincolle.java");

    @Test
    void brainHelpersShouldNotDeclareUnconsumedSensors() throws IOException {
        for (Path helper : BRAIN_HELPERS) {
            String source = Files.readString(helper);

            assertTrue(source.contains("SENSOR_TYPES =\n            ImmutableList.of();"),
                    helper + " should not run vanilla sensors until behavior code consumes their memories");
            assertFalse(source.contains("SensorType.NEAREST_LIVING_ENTITIES"),
                    helper + " should not declare nearest-living sensor without consuming nearest-living memory");
            assertFalse(source.contains("SensorType.NEAREST_PLAYERS"),
                    helper + " should not declare nearest-player sensor without consuming nearest-player memory");
        }
    }

    @Test
    void brainHelpersShouldOnlyDeclareMovementMemoriesTheyActuallyMirror() throws IOException {
        for (Path helper : BRAIN_HELPERS) {
            String source = Files.readString(helper);

            assertTrue(source.contains("MemoryModuleType.WALK_TARGET"),
                    helper + " should declare WALK_TARGET because coordinator targets are mirrored into Brain memory");
            assertTrue(source.contains("MemoryModuleType.LOOK_TARGET"),
                    helper + " should declare LOOK_TARGET because look targets are mirrored into Brain memory");
            assertTrue(source.contains("MemoryModuleType.ATTACK_TARGET"),
                    helper + " should declare ATTACK_TARGET because current combat targets are mirrored into Brain memory");
            assertTrue(source.contains("MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE"),
                    helper + " should declare CANT_REACH_WALK_TARGET_SINCE because stale path-failure memory is cleared with movement targets");
            assertFalse(source.contains("MemoryModuleType.PATH"),
                    helper + " should not declare PATH while vanilla MoveToTargetSink is not the navigation owner");
        }
    }

    @Test
    void shipBrainShouldMirrorLegacyStateIntoCustomMemories() throws IOException {
        String brain = Files.readString(SHIP_BRAIN_SOURCE);
        String memory = Files.readString(SHIP_BRAIN_MEMORY_SOURCE);
        String modules = Files.readString(MEMORY_MODULES_SOURCE);
        String mod = Files.readString(MOD_SOURCE);

        assertTrue(modules.contains("DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, Shincolle.MODID)"),
                "Custom ship Brain memories should be registered in the vanilla memory-module registry");
        assertTrue(modules.contains("SHIP_POINTER_TARGET"));
        assertTrue(modules.contains("SHIP_GUARD_TARGET"));
        assertTrue(modules.contains("SHIP_FOLLOW_STATE"));
        assertTrue(modules.contains("SHIP_PASSIVE_COMBAT_STATE"));
        assertTrue(modules.contains("SHIP_POINTER_RECOVERY"));
        assertTrue(modules.contains("SHIP_GUARD_RECOVERY"));
        assertTrue(modules.contains("SHIP_FOLLOW_RECOVERY"));
        assertTrue(modules.contains("SHIP_COMBAT_RECOVERY"));
        assertTrue(mod.contains("ModMemoryModules.MEMORY_MODULE_TYPES.register(modEventBus);"),
                "Memory modules must be attached to the mod event bus");

        assertTrue(brain.contains("ModMemoryModules.SHIP_POINTER_TARGET.get()"),
                "Ship Brain provider should declare pointer-target memory");
        assertTrue(brain.contains("ModMemoryModules.SHIP_GUARD_TARGET.get()"),
                "Ship Brain provider should declare guard-target memory");
        assertTrue(brain.contains("ModMemoryModules.SHIP_FOLLOW_STATE.get()"),
                "Ship Brain provider should declare follow-state memory");
        assertTrue(brain.contains("ModMemoryModules.SHIP_PASSIVE_COMBAT_STATE.get()"),
                "Ship Brain provider should declare passive-combat state memory");
        assertTrue(brain.contains("syncShipStateMemory(ship, brain);"),
                "Ship Brain tick should mirror legacy state into custom memories before activity selection");
        assertTrue(brain.contains("activityState(ship, brain)"),
                "Ship Brain activity selection should use the memory-backed state path");
        assertTrue(brain.contains("pointerTargetMemory(brain)"));
        assertTrue(brain.contains("guardTargetMemory(brain)"));
        assertTrue(brain.contains("followStateMemory(brain)"));
        assertTrue(brain.contains("ShipBrainRecoverySupport.syncRecoveryMemory(ship, ModMemoryModules.SHIP_POINTER_RECOVERY.get()"));
        assertTrue(brain.contains("ShipBrainRecoverySupport.syncRecoveryMemory(ship, ModMemoryModules.SHIP_GUARD_RECOVERY.get()"));
        assertTrue(brain.contains("ShipBrainRecoverySupport.syncRecoveryMemory(ship, ModMemoryModules.SHIP_FOLLOW_RECOVERY.get()"));
        assertTrue(brain.contains("ShipBrainRecoverySupport.syncRecoveryMemory(ship, ModMemoryModules.SHIP_COMBAT_RECOVERY.get()"));
        assertTrue(brain.contains("pointerTargetMemory(ship).hasAnyTarget()"),
                "Pointer movement should start from memory-backed point or entity target state");
        assertTrue(brain.contains("pointerMemory.hasAnyTarget()"),
                "Ship Brain activity selection should treat pointer entity commands as command activity");
        assertTrue(brain.contains("tickPointerEntityMove(ship, pointerMemory);"),
                "Pointer entity chase movement should be consumed by the Brain pointer behavior");
        assertTrue(brain.contains("Vec3 rawTarget = pointerMemory.rawPointTarget();"));
        assertTrue(brain.contains("Vec3 target = pointerMemory.adjustedPointTarget();"));
        assertTrue(brain.contains("ShipBrainMemory.GuardTargetMemory guardMemory = guardTargetMemory(ship);"));
        assertTrue(brain.contains("target = guardMemory.guardedEntityPos();"));
        assertTrue(brain.contains("target = guardMemory.blockCenter();"));
        assertTrue(brain.contains("pointerMemory.entityTargetAlive() && pointerMemory.entityTargetPos() != null"),
                "Look behavior should use pointer entity memory before falling back to entity fields");
        assertTrue(brain.contains("!pointerTargetMemory(ship).hasAnyTarget()"));
        assertTrue(brain.contains("!followStateMemory(ship).shouldFollow()"));
        assertTrue(brain.contains("!guardTargetMemory(ship).target().isActive()"));

        assertTrue(memory.contains("record PointerTargetMemory"));
        assertTrue(memory.contains("record GuardTargetMemory"));
        assertTrue(memory.contains("record FollowStateMemory"));
        assertTrue(memory.contains("record PassiveCombatStateMemory"));
        assertTrue(memory.contains("record RecoveryStateMemory"));
        assertTrue(memory.contains("public boolean hasAnyTarget()"),
                "Pointer memory should expose a single decision-friendly target flag");
        assertTrue(memory.contains("public boolean hasAdjustedPointTarget()"),
                "Pointer memory should expose adjusted point-target readiness for Brain movement");
        assertTrue(memory.contains("boolean hasEntityTargetCommand"),
                "Pointer memory should snapshot entity-command presence so Brain can clear stale entity commands");
        assertTrue(memory.contains("boolean entityShouldChase"),
                "Pointer memory should expose entity chase decisions to the Brain pointer behavior");
        assertTrue(memory.contains("double entityAttackRangeSqr"),
                "Pointer memory should expose entity attack range to the Brain pointer behavior");
        assertTrue(memory.contains("ShipPointerEntityDecisionResolver.resolve("),
                "Pointer memory should use the behavior-tested resolver for entity chase decisions");
        assertTrue(brain.contains("if (!pointerMemory.entityShouldChase()) {"),
                "Pointer entity movement should consume memory-backed chase decisions");
        assertTrue(brain.contains("tickPointerEntityAttacks(ship, target, pointerMemory);"),
                "Pointer entity attacks should be driven by the Brain pointer behavior");
        assertTrue(memory.contains("Vec3 blockCenter"));
        assertTrue(memory.contains("public boolean hasBlockTarget()"));
        assertTrue(memory.contains("public boolean hasLiveEntityTarget()"));
        assertTrue(memory.contains("boolean shouldChase"),
                "Passive combat memory should expose Brain-owned chase decisions");
        assertTrue(memory.contains("double moveSpeed"),
                "Passive combat memory should expose Brain-owned chase speed");
        assertTrue(brain.contains("ShipBrainMemory.PassiveCombatStateMemory state = ship.updatePassiveCombatStateBrain();"),
                "Combat behavior should snapshot passive combat state before movement and attacks");
        assertTrue(brain.contains("ship.getBrain().setMemory(ModMemoryModules.SHIP_PASSIVE_COMBAT_STATE.get(), state);"),
                "Combat behavior should mirror passive combat state into Brain memory");
        assertTrue(brain.contains("state = passiveCombatStateMemory(ship);"),
                "Combat behavior should consume passive combat decisions through Brain memory after writing them");
        assertTrue(brain.contains("if (state.shouldChase()) {"),
                "Combat behavior should consume passive combat memory for chase decisions");
    }

    @Test
    void shipFollowBehaviorShouldConsumeBrainMemorySnapshots() throws IOException {
        String brain = Files.readString(SHIP_BRAIN_SOURCE);
        String memory = Files.readString(SHIP_BRAIN_MEMORY_SOURCE);

        assertTrue(memory.contains("Vec3 ownerPos"),
                "Follow memory should snapshot owner position for movement/look decisions");
        assertTrue(memory.contains("double ownerEyeY"),
                "Follow memory should snapshot owner eye height for look decisions");
        assertTrue(memory.contains("int ownerDimensionId"),
                "Follow memory should snapshot owner dimension for same-dimension movement checks");
        assertTrue(memory.contains("owner != null ? owner.position() : null"));
        assertTrue(memory.contains("owner != null ? owner.getEyeY() : 0.0D"));
        assertTrue(memory.contains("owner != null ? EntityShipBase.getLegacyDimensionId(owner.level()) : 0"));

        assertTrue(brain.contains("hasSameDimensionOwnerPosition(level, followMemory)"),
                "Follow behavior should validate owner position snapshots before moving");
        assertTrue(brain.contains("Vec3 ownerPos = followMemory.ownerPos();"));
        assertTrue(brain.contains("Vec3 moveTarget = ownerPos;"),
                "Default follow target should come from Brain memory instead of reading owner.position() directly");
        assertTrue(brain.contains("followMemory.ownerHasCombatRation()"),
                "Follow behavior should use the ration flag mirrored into Brain memory");
        assertTrue(brain.contains("ShipBrainActivityResolver.shouldContinueFollow(followResolverState(level, ship, followMemory))"),
                "Follow continuation should route through the shared resolver using memory-backed follow state");
        assertTrue(brain.contains("ship.getLookControl().setLookAt(target.x, followMemory.ownerEyeY(), target.z"),
                "Owner look fallback should use memory-backed owner coordinates");
        assertFalse(brain.contains("double distSq = ship.distanceToSqr(owner);"),
                "Follow behavior should not recompute owner distance from entity state");
        assertFalse(brain.contains("Vec3 moveTarget = owner.position();"),
                "Follow behavior should not use owner.position() as the default movement target");
        assertFalse(brain.contains("owner instanceof Player player && ship.playerHasCombatRation(player)"),
                "Follow behavior should not re-read combat ration state once it is mirrored into Brain memory");
    }
}
