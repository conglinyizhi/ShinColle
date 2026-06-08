package org.trp.shincolle.event

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class ShipGuardTargetArchitectureRegressionTest {
    private val SHIP_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt")
    private val GUARD_TARGET_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/entity/base/ShipGuardTarget.kt")
    private val BRAIN_AI_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBrainAi.kt")
    private val MOUNT_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/entity/base/EntityMountBase.kt")
    private val MOUNT_BRAIN_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/entity/base/EntityMountBrainAi.kt")
    private val FORMATION_HELPER_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/utility/FormationHelper.kt")
    private val EVENT_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/event/ModEventBusEvents.kt")
    private val POINTER_SERVICE_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/server/PointerInteractionService.kt")

    @Test
    fun guardStateShouldHaveTypedFacadeOverLegacySlots() {
        val ship = Files.readString(SHIP_SOURCE)
        val guardTarget = Files.readString(GUARD_TARGET_SOURCE)

        assertTrue(guardTarget.contains("public record ShipGuardTarget")) {
            "Guard target state should have a typed facade over legacy slots"
        }
        assertTrue(ship.contains("public ShipGuardTarget getGuardTarget()")) {
            "EntityShipBase should expose guard target through the typed facade"
        }
        assertTrue(ship.contains("public void setGuardBlockTarget(BlockPos pos)")) {
            "Block guard writes should be centralized"
        }
        assertTrue(ship.contains("public void clearGuardTarget()")) {
            "Guard clearing should be centralized"
        }
    }

    @Test
    fun pointerAndWaypointCommandsShouldNotWriteGuardSlotsDirectly() {
        val eventSource = Files.readString(EVENT_SOURCE)
        val pointerService = Files.readString(POINTER_SERVICE_SOURCE)
        val shipSource = Files.readString(SHIP_SOURCE)

        assertFalse(eventSource.contains("setStateMinor(24, 1)")) {
            "Pointer waypoint command must not write the wrong legacy state slot"
        }
        assertFalse(eventSource.contains("STATE_MINOR_GUARD_X")) {
            "Pointer waypoint command should use the typed guard write API"
        }
        var hasRightClickDelegation = eventSource.contains("PointerInteractionService.handleRightClickBlock(event.getEntity(), event)")
            || eventSource.contains("PointerInteractionService.handleRightClickItem(event.getEntity(), event)")
        assertTrue(hasRightClickDelegation) {
            "Pointer waypoint command should be delegated out of the event layer"
        }
        assertTrue(pointerService.contains("ship.setGuardBlockTarget(guardPos);")) {
            "Pointer waypoint command should persist the waypoint guard target"
        }
        assertTrue(shipSource.contains("this.suspendBlockGuardTarget();")) {
            "Temporary pointer commands should suspend block guard through the typed API"
        }
        assertTrue(shipSource.contains("public void suspendBlockGuardTarget()")) {
            "Suspending block guard should be centralized in the ship API"
        }
        assertTrue(shipSource.contains("this.setGuardedPos(target.x(), target.y(), target.z(), target.dimensionId(), ShipGuardTarget.Type.NONE.legacyId());\n            this.guardMovement.stop();")) {
            "Suspending block guard should stop stale guard navigation before temporary pointer commands take over"
        }
    }

    @Test
    fun guardMovementShouldNotUsePositiveYAsValiditySentinel() {
        val guardGoal = Files.readString(BRAIN_AI_SOURCE)
        val mount = Files.readString(MOUNT_BRAIN_SOURCE)

        assertFalse(guardGoal.contains("getGuardedPos(1) > 0")) {
            "Guard AI should not reject valid modern-world guard targets by Y coordinate"
        }
        assertFalse(mount.contains("getGuardedPos(1) > 0")) {
            "Mount guard-follow AI should not reject valid modern-world guard targets by Y coordinate"
        }
        assertTrue(guardGoal.contains("guardMemory.hasBlockTarget()")) {
            "Guard AI should branch on memory-backed typed guard target kind"
        }
        assertTrue(mount.contains("guardTarget.isBlock() && guardTarget.isIn(host.level())")) {
            "Mount AI should branch on typed guard target kind"
        }
    }

    @Test
    fun formationGuardShouldUseCurrentDimension() {
        val formationHelper = Files.readString(FORMATION_HELPER_SOURCE)

        assertFalse(formationHelper.contains("setGuardedPos(x, y, z, 0, 1)")) {
            "Formation guard should not hard-code overworld dimension"
        }
        assertTrue(formationHelper.contains("ship.setGuardBlockTarget(new BlockPos(x, y, z));")) {
            "Formation guard should let the ship compute its current legacy dimension"
        }
        assertTrue(formationHelper.contains("ship.moveGuardTargetTo(new Vec3(x + 0.5D, y, z + 0.5D), 1.2D);")) {
            "Formation guard should route immediate movement through the ship-owned guard channel"
        }
        assertTrue(formationHelper.contains("Shincolle.debugLog(\"Formation summon teleportFailed")) {
            "Desk summon should log failed safe teleport recovery"
        }
        assertFalse(formationHelper.contains("ship.teleportTo(spawnX, spawnY, spawnZ)")) {
            "Desk summon should not directly teleport ships into unchecked spawn positions"
        }
    }

    @Test
    fun mountGuardTeleportShouldUseSharedSafePointHelper() {
        val mount = Files.readString(MOUNT_BRAIN_SOURCE)

        assertFalse(mount.contains("mount.teleportTo(guardPos.x, guardPos.y + 0.75D, guardPos.z)")) {
            "Mount guard teleport should not jump directly into an unchecked block position"
        }
        assertTrue(mount.contains("trackAndRecoverPoint(mount, guardPos, \"guardBlock\");")) {
            "Mount guard movement should route recovery through the shared tracker"
        }
        assertTrue(mount.contains("mount.followMovementCoordinator().teleportNearPoint(target, MountAiNumbers.TELEPORT_VERTICAL_OFFSET)")) {
            "Mount guard teleport should share the centralized movement recovery policy"
        }
    }

    @Test
    fun guardRecoveryShouldResetWhenGuardTargetIdentityChanges() {
        val guardGoal = Files.readString(BRAIN_AI_SOURCE)
        val mount = Files.readString(MOUNT_BRAIN_SOURCE)
        val ship = Files.readString(SHIP_SOURCE)

        assertTrue(guardGoal.contains("ShipGuardTarget guardTarget = guardMemory.target();")) {
            "Brain guard behavior should read the typed guard target through Brain memory"
        }
        assertTrue(guardGoal.contains("ShipGuardDecisionResolver.shouldSyncEntityDimension(unresolvedGuardState)")) {
            "Brain guard behavior should delegate entity-guard dimension drift checks to a behavior-tested resolver"
        }
        assertTrue(guardGoal.contains("target = guardMemory.guardedEntityPos();")) {
            "Brain guard behavior should branch on memory-backed typed guard target kind"
        }
        assertTrue(guardGoal.contains("target = guardMemory.blockCenter();")) {
            "Brain guard behavior should use memory-backed block guard coordinates"
        }
        assertTrue(guardGoal.contains("ship.guardMovementCoordinator()")) {
            "Brain guard behavior should reuse the ship-owned guard movement coordinator"
        }
        assertTrue(guardGoal.contains("private GuardRecoveryTargetKey lastGuardRecoveryTargetKey;")) {
            "Brain guard behavior should keep explicit guard-target identity for recovery resets"
        }
        assertTrue(guardGoal.contains("resetGuardRecoveryIfTargetChanged(ship, guardTarget, guardedEntity);")) {
            "Brain guard behavior should reset recovery when switching guard targets"
        }
        assertTrue(guardGoal.contains("private record GuardRecoveryTargetKey")) {
            "Brain guard recovery target identity should remain explicit after migration"
        }
        assertTrue(ship.contains("Entity entity = serverLevel.getEntity(this.guardedEntityId);")) {
            "Guarded-entity lookup should resolve the current guard target through the owning server level"
        }
        assertTrue(ship.contains("if (entity == null || !entity.isAlive() || entity.isRemoved()) {\n                    return null;\n                }")) {
            "Guarded-entity lookup should treat dead or removed entities as invalid guard targets"
        }
        assertTrue(ship.contains("if (entity.getUUID().equals(this.guardedEntityId) && entity.isAlive() && !entity.isRemoved()) {")) {
            "Client-side guarded-entity lookup should ignore dead or removed nearby entities"
        }

        assertTrue(mount.contains("private FollowRecoveryTargetKey lastRecoveryTargetKey;")) {
            "Mount follow should remember which host-follow mode owns the current recovery counters"
        }
        assertTrue(mount.contains("FollowRecoveryTargetKey.point(\"pointer\", pt,\n                        EntityShipBase.getLegacyDimensionId(h.level()))")) {
            "Mount pointer follow recovery should include the host dimension in the target identity"
        }
        assertTrue(mount.contains("resetRecoveryIfTargetChanged(mount, FollowRecoveryTargetKey.entity(\"owner\", owner));")) {
            "Mount owner follow should reset recovery only when the owner identity changes"
        }
        assertTrue(mount.contains("resetRecoveryIfTargetChanged(mount, FollowRecoveryTargetKey.entity(\"guardEntity\", guarded));")) {
            "Mount entity guard follow should reset recovery when the guarded entity changes"
        }
        assertTrue(mount.contains("resetRecoveryIfTargetChanged(mount, FollowRecoveryTargetKey.guardBlock(guardTarget));")) {
            "Mount block guard follow should reset recovery when the guarded block changes"
        }
        assertTrue(mount.contains("private record FollowRecoveryTargetKey")) {
            "Mount follow recovery target identity should be explicit and include the follow reason"
        }
    }
}
