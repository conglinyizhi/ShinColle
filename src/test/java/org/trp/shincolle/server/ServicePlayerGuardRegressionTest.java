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
        assertTrue(source.contains("boolean handled = true;"),
                "FormationService should track whether an incoming action id was actually recognized");
        assertTrue(source.contains("default -> handled = false;"),
                "FormationService should treat unknown action ids as ignored instead of syncing state");
        assertTrue(source.contains("if (handled && player instanceof ServerPlayer serverPlayer) {\n            PlayerStateService.sendAdmiralState(serverPlayer);\n        }"),
                "FormationService should only send admiral sync after recognized actions");
        assertTrue(source.contains("public static void handlePointerRosterToggle(Player player, UUID targetUuid) {\n        if (player == null || targetUuid == null) {\n            return;\n        }"),
                "FormationService handlePointerRosterToggle should ignore null players before attachment access");
    }

    @Test
    void pointerPayloadServiceShouldGuardNullPlayersAndClientSideCalls() throws IOException {
        String source = Files.readString(POINTER_SERVICE);

        assertTrue(source.contains("public static void handlePayloadAction(Player player, ItemStack pointerStack, int action,"),
                "PointerInteractionService should keep a dedicated payload-facing entrypoint");
        assertTrue(source.contains("if (player == null || player.level().isClientSide) {\n            return;\n        }\n        if (!(pointerStack.getItem() instanceof PointerItem pointerItem)) {"),
                "PointerInteractionService handlePayloadAction should reject null players and client-side calls before pointer item logic");
    }
}
