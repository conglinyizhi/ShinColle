package org.trp.shincolle.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DeskInteractionServiceRegressionTest {
    private static final Path DESK_INTERACTION_SERVICE =
            Path.of("src/main/java/org/trp/shincolle/server/DeskInteractionService.java");

    @Test
    void deskPayloadFacingActionsShouldGuardNullPlayers() throws IOException {
        String source = Files.readString(DESK_INTERACTION_SERVICE);

        assertTrue(source.contains("public static void updateBookState(Player player, int chapter, int page) {\n        if (player == null) {\n            return;\n        }"),
                "DeskInteractionService should ignore null players before updating desk book state");
        assertTrue(source.contains("public static void updateDeskGui(Player player, int guiFunc, int radarZoom) {\n        if (player == null) {\n            return;\n        }"),
                "DeskInteractionService should ignore null players before reading desk GUI state");
        assertTrue(source.contains("public static void openOwnedShipFromDesk(Player player, UUID shipUuid) {\n        if (player == null) {\n            return;\n        }"),
                "DeskInteractionService should ignore null players before resolving desk-selected ships");
        assertTrue(source.contains("if (player == null\n                || !(player.level() instanceof ServerLevel serverLevel)"),
                "DeskInteractionService summonOwnedShipsToDesk should ignore null players before accessing level data");
    }
}
