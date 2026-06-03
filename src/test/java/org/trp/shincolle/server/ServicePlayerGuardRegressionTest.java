package org.trp.shincolle.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicePlayerGuardRegressionTest {
    private static final Path WAYPOINT_SERVICE =
            Path.of("src/main/java/org/trp/shincolle/server/WaypointService.java");
    private static final Path FORMATION_SERVICE =
            Path.of("src/main/java/org/trp/shincolle/server/FormationService.java");
    private static final Path POINTER_SERVICE =
            Path.of("src/main/java/org/trp/shincolle/server/PointerInteractionService.java");

    @Test
    void waypointServiceShouldIgnoreNullPlayersBeforeAccessingLevel() throws IOException {
        String source = Files.readString(WAYPOINT_SERVICE);

        assertTrue(source.contains("public static void handleAction(Player player, int action, BlockPos pos1, BlockPos pos2) {"),
                "WaypointService should keep a dedicated payload-facing handleAction entrypoint");
        assertTrue(source.contains("if (player == null) {\n            return;\n        }\n        if (player.level() == null) {"),
                "WaypointService should return early when payload handling reaches it without a player");
        assertTrue(source.contains("if (pos1 == null || pos2 == null) {\n            return;\n        }"),
                "WaypointService should reject malformed payloads that omit either waypoint position before pairing logic");
        assertTrue(source.contains("if (action == 0) {\n            pairWaypointToWaypoint(player, pos1, pos2);\n        } else if (action == 1) {\n            pairWaypointToContainer(player, pos1, pos2);\n        } else if (action == 2) {\n            autoPair(player, pos1, pos2);\n        }"),
                "WaypointService should ignore unknown action ids instead of dispatching unexpected pairing logic");
    }

    @Test
    void formationServiceShouldGuardNullPlayersAtPayloadFacingEntrypoints() throws IOException {
        String source = Files.readString(FORMATION_SERVICE);

        assertTrue(source.contains("public static void syncNearbyShipsForCurrentTeam(Player player, boolean clearDeselectedTargets) {"),
                "FormationService should keep the nearby-ship sync entrypoint");
        assertTrue(source.contains("public static void handleFormationAction(Player player, int action, int param1, int param2,"),
                "FormationService should keep the payload-facing formation action entrypoint");
        assertTrue(source.contains("public static void handlePointerRosterToggle(Player player, UUID targetUuid) {"),
                "FormationService should keep the pointer roster toggle entrypoint");

        assertTrue(source.contains("public static void syncNearbyShipsForCurrentTeam(Player player, boolean clearDeselectedTargets) {\n        if (player == null) {\n            return;\n        }"),
                "FormationService syncNearbyShipsForCurrentTeam should ignore null players");
        assertTrue(source.contains("public static void handleFormationAction(Player player, int action, int param1, int param2,\n                                             String paramString, Optional<UUID> paramUuid) {\n        if (player == null) {\n            return;\n        }"),
                "FormationService handleFormationAction should ignore null players");
        assertTrue(source.contains("boolean shouldSync = false;"),
                "FormationService should track whether an incoming action actually changed admiral state");
        assertTrue(source.contains("case 1 -> shouldSync = PlayerStateService.setCurrentTeamFormation(player, param1);"),
                "FormationService should only sync formation changes when the formation id actually changes");
        assertTrue(source.contains("case 2 -> shouldSync = setCurrentTeamSlotSelected(player, data, param1, param2 != 0);"),
                "FormationService should only sync slot selection changes when the selection state actually changes");
        assertTrue(source.contains("case 3 -> shouldSync = removeCurrentTeamSlot(player, data, param1);"),
                "FormationService should only sync slot removals when a ship was actually removed");
        assertTrue(source.contains("case 4 -> shouldSync = PlayerStateService.setCurrentTeamName(player, paramString);"),
                "FormationService should only sync team renames when the stored name actually changes");
        assertTrue(source.contains("case 5 -> shouldSync = paramUuid.map(uuid -> replaceCurrentTeamSlot(player, param1, uuid)).orElse(false);"),
                "FormationService should only sync slot replacement when a valid uuid produced a real slot change");
        assertTrue(source.contains("case 6 -> shouldSync = swapCurrentTeamSlots(player, data, param1, param2);"),
                "FormationService should only sync slot swaps when the swap request is valid");
        assertTrue(source.contains("case 7 -> shouldSync = importNearbySelectedShips(player, data);"),
                "FormationService should only sync nearby import when selected ships were actually imported");
        assertTrue(source.contains("case 8 -> openCurrentTeamSlotShipMenu(player, data, param1);"),
                "FormationService should keep menu-open actions side-effect only and avoid admiral sync");
        assertTrue(source.contains("if (shouldSync && player instanceof ServerPlayer serverPlayer) {\n            PlayerStateService.sendAdmiralState(serverPlayer);\n        }"),
                "FormationService should only send admiral sync after real state changes");
        assertTrue(source.contains("public static void handlePointerRosterToggle(Player player, UUID targetUuid) {\n        if (player == null || targetUuid == null) {\n            return;\n        }"),
                "FormationService handlePointerRosterToggle should ignore null players before attachment access");
        assertTrue(source.contains("boolean shouldSync = false;"),
                "FormationService pointer roster toggle should track whether the roster action actually changed admiral state");
        assertTrue(source.contains("if (shouldSync && player instanceof ServerPlayer serverPlayer) {\n            PlayerStateService.sendAdmiralState(serverPlayer);\n        }"),
                "FormationService pointer roster toggle should only sync after a real roster change");
        assertTrue(source.contains("if (entity instanceof EntityShipBase ship && ship.isOwnedBy(player) && ship.isAlive()) {\n            action.accept(ship);\n        }"),
                "FormationService should only hand UUID-resolved ships to callbacks when they are still owned by the player and alive");
    }

    @Test
    void playerStateServiceShouldRejectNoopFormationMutations() throws IOException {
        String source = Files.readString(Path.of("src/main/java/org/trp/shincolle/server/PlayerStateService.java"));

        assertTrue(source.contains("if (player == null) {\n            return 0;\n        }\n        if (player instanceof ServerPlayer serverPlayer) {"),
                "PlayerStateService should treat null players as zero married ships before server-side reconciliation");
        assertTrue(source.contains("if (player == null) {\n            return false;\n        }\n        return admiralData(player).isRingFlightActive();"),
                "PlayerStateService should treat null players as ring-flight inactive");
        assertTrue(source.contains("if (player == null) {\n            return;\n        }\n        admiralData(player).setRingFlightActive(active);"),
                "PlayerStateService should ignore null players when toggling ring flight state");
        assertTrue(source.contains("if (player == null || delta == 0) {\n            return;\n        }"),
                "PlayerStateService should ignore null players before adjusting married ship counters");
        assertTrue(source.contains("if (player == null) {\n            return 0;\n        }\n        return admiralData(player).getCurrentTeamID();"),
                "PlayerStateService should treat null players as team zero when callers ask for the current team id");
        assertTrue(source.contains("if (player == null || teamId < 0 || teamId >= AdmiralData.TEAM_COUNT) {\n            return false;\n        }"),
                "PlayerStateService should reject null players before changing current team");
        assertTrue(source.contains("if (player == null || formationId < 0) {\n            return false;\n        }"),
                "PlayerStateService should reject null players before changing formation ids");
        assertTrue(source.contains("if (player == null || slotId < 0 || slotId >= AdmiralData.SLOT_COUNT) {\n            return false;\n        }"),
                "PlayerStateService should reject null players before mutating slot selection");
        assertTrue(source.contains("if (player == null) {\n            return false;\n        }\n        AdmiralData data = admiralData(player);"),
                "PlayerStateService should reject null players before mutating team names");
        assertTrue(source.contains("if (player == null\n                || slot1 < 0 || slot1 >= AdmiralData.SLOT_COUNT"),
                "PlayerStateService should reject null players before swapping team slots");
        assertTrue(source.contains("if (player == null || shipUuid == null) {\n            return -1;\n        }"),
                "PlayerStateService should reject null players before assigning ships to the current team");
        assertTrue(source.contains("if (player == null || shipUuid == null) {\n            return false;\n        }\n        return admiralData(player).removeShip(shipUuid);"),
                "PlayerStateService should reject null players before removing ships from teams");
        assertTrue(source.contains("if (player == null || slotId < 0 || slotId >= AdmiralData.SLOT_COUNT || shipUuid == null) {\n            return null;\n        }"),
                "PlayerStateService should reject null players before replacing team slots");
        assertTrue(source.contains("if (data.getCurrentTeamID() == teamId) {\n            return false;\n        }"),
                "PlayerStateService should reject no-op current team switches");
        assertTrue(source.contains("if (data.getFormationID(data.getCurrentTeamID()) == formationId) {\n            return false;\n        }"),
                "PlayerStateService should reject no-op formation changes");
        assertTrue(source.contains("if (data.isSelected(teamId, slotId) == selected) {\n            return false;\n        }"),
                "PlayerStateService should reject no-op slot selection updates");
        assertTrue(source.contains("if (currentName.equals(nextName)) {\n            return false;\n        }"),
                "PlayerStateService should reject no-op team renames after normalization");
        assertTrue(source.contains("if (shipUuid.equals(replacedUuid) && data.isSelected(teamId, slotId)) {\n            return null;\n        }"),
                "PlayerStateService should reject no-op slot replacement when the same selected ship is already present");
    }

    @Test
    void pointerPayloadServiceShouldGuardNullPlayersAndClientSideCalls() throws IOException {
        String source = Files.readString(POINTER_SERVICE);

        assertTrue(source.contains("public static void handlePayloadAction(Player player, ItemStack pointerStack, int action,"),
                "PointerInteractionService should keep a dedicated payload-facing entrypoint");
        assertTrue(source.contains("if (player == null || player.level().isClientSide) {\n            return;\n        }\n        if (!(pointerStack.getItem() instanceof PointerItem pointerItem)) {"),
                "PointerInteractionService handlePayloadAction should reject null players and client-side calls before pointer item logic");
        assertTrue(source.contains("boolean shouldSync = false;"),
                "PointerInteractionService grouped selection should track whether formation operations actually changed state");
        assertTrue(source.contains("if (shouldSync) {\n                sendAdmiralStateIfServerPlayer(player);\n            }"),
                "PointerInteractionService should only sync admiral state after real grouped formation changes");
        assertTrue(source.contains("if (PlayerStateService.removeShipFromTeams(player, ship.getUUID())) {\n                        FormationService.clearFormationState(ship);\n                        shouldSync = true;\n                    }"),
                "PointerInteractionService should only sync grouped removal when a ship was actually removed from teams");
        assertTrue(source.contains("if (PlayerStateService.setCurrentTeamSlotSelected(player, existingSlot, nextState)) {\n                        ship.setPointerSelected(nextState);\n                        shouldSync = true;\n                    }"),
                "PointerInteractionService should only sync grouped roster toggles when selection state actually changes");
        assertTrue(source.contains("public static EntityHitResult getLookTargetResult(Player player) {\n        if (player == null) {\n            return null;\n        }"),
                "PointerInteractionService should reject null players before ray-picking entity targets");
        assertTrue(source.contains("private static void applyPointerModeSelectionState(Player player, int nextMode) {\n        if (player == null) {\n            return;\n        }"),
                "PointerInteractionService should reject null players before reconciling pointer mode selection state");
        assertTrue(source.contains("private static void clearOwnedPointerSelection(Player player, EntityShipBase keepSelected, double radius) {\n        if (player == null) {\n            return;\n        }"),
                "PointerInteractionService should reject null players before clearing owned pointer selection");
        assertTrue(source.contains("private static void openOwnedShipMenu(Player player, UUID shipUuid) {\n        if (player == null) {\n            return;\n        }"),
                "PointerInteractionService should reject null players before opening owned ship menus");
        assertTrue(source.contains("if (!(entity instanceof EntityShipBase ship) || !ship.isOwnedBy(player) || !ship.isAlive()) {\n                continue;\n            }"),
                "PointerInteractionService formation target assignment should skip UUID-resolved ships that are no longer owned by the player or alive");
    }
}
