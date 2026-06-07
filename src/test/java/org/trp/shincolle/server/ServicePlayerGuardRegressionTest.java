package org.trp.shincolle.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicePlayerGuardRegressionTest {
    private static final Path WAYPOINT_SERVICE =
            Path.of("src/main/java/org/trp/shincolle/server/WaypointService.kt");
    private static final Path FORMATION_SERVICE =
            Path.of("src/main/java/org/trp/shincolle/server/FormationService.kt");
    private static final Path POINTER_SERVICE =
            Path.of("src/main/java/org/trp/shincolle/server/PointerInteractionService.kt");

    @Test
    void waypointServiceShouldKeepExplicitPayloadDispatchBranches() throws IOException {
        String source = Files.readString(WAYPOINT_SERVICE);

        assertTrue(source.contains("public static void handleAction(Player player, int action, BlockPos pos1, BlockPos pos2) {"),
                "WaypointService should keep a dedicated payload-facing handleAction entrypoint");
        assertTrue(source.contains("if (action == 0) {\n            pairWaypointToWaypoint(player, pos1, pos2);\n        } else if (action == 1) {\n            pairWaypointToContainer(player, pos1, pos2);\n        } else if (action == 2) {\n            autoPair(player, pos1, pos2);\n        }"),
                "WaypointService should ignore unknown action ids instead of dispatching unexpected pairing logic");
    }

    @Test
    void formationServiceShouldOnlySyncAfterRealStateChanges() throws IOException {
        String source = Files.readString(FORMATION_SERVICE);

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
        assertTrue(source.contains("if (entity instanceof EntityShipBase ship && ship.isOwnedBy(player) && ship.isAlive() && !ship.isRemoved()) {\n            action.accept(ship);\n        }"),
                "FormationService should only hand UUID-resolved ships to callbacks when they are still owned by the player, alive, and not removed");
    }

    @Test
    void playerStateServiceShouldRejectNoopFormationMutations() throws IOException {
        String source = Files.readString(Path.of("src/main/java/org/trp/shincolle/server/PlayerStateService.kt"));

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
    void pointerPayloadServiceShouldOnlySyncRealGroupedSelectionChanges() throws IOException {
        String source = Files.readString(POINTER_SERVICE);

        assertTrue(source.contains("boolean shouldSync = false;"),
                "PointerInteractionService grouped selection should track whether formation operations actually changed state");
        assertTrue(source.contains("if (shouldSync) {\n                sendAdmiralStateIfServerPlayer(player);\n            }"),
                "PointerInteractionService should only sync admiral state after real grouped formation changes");
        assertTrue(source.contains("if (!(entity instanceof EntityShipBase ship) || !ship.isOwnedBy(player) || !ship.isAlive() || ship.isRemoved()) {\n                continue;\n            }"),
                "PointerInteractionService formation target assignment should skip UUID-resolved ships that are no longer owned by the player, alive, or still present");
        assertTrue(source.contains("if (target == null || !target.isAlive() || target.isRemoved()) {\n                    continue;\n                }"),
                "PointerInteractionService formation target assignment should skip UUID-resolved targets that are already dead or removed");
    }
}
