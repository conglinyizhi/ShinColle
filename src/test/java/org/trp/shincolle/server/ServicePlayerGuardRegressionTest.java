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
