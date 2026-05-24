package org.trp.shincolle.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DeskRadarOpenShipRegressionTest {
    private static final Path SCREEN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/screen/DeskScreen.java");
    private static final Path NETWORK_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/network/ModNetwork.java");

    @Test
    void radarListShouldStillOpenShipMenuWhenClickingSameShipAgain() throws IOException {
        String screenSource = Files.readString(SCREEN_SOURCE);

        assertTrue(screenSource.contains("boolean sameSelection = this.selectedShips.size() == 1 && this.selectedShips.contains(shipUuid);"),
                "Desk radar should detect clicking the already selected ship");
        assertTrue(screenSource.contains("if (sameSelection) {\n                                openRadarSelectedShip(shipUuid);\n                                return true;\n                            }"),
                "Desk radar should reopen the selected ship menu on repeated click");
        assertTrue(screenSource.contains("PacketDistributor.sendToServer(new C2SDeskOpenShipPayload(shipUuid));"),
                "Desk radar should send a dedicated payload to open ship GUI");
    }

    @Test
    void radarActionButtonShouldKeepLegacySingleSelectionOpenBehavior() throws IOException {
        String screenSource = Files.readString(SCREEN_SOURCE);

        assertTrue(screenSource.contains("private void handleRadarActionButton() {"),
                "Desk radar should keep a dedicated action-button handler");
        assertTrue(screenSource.contains("if (this.selectedShips.size() == 1) {\n            openRadarSelectedShip(this.selectedShips.iterator().next());\n        }"),
                "Desk radar action button should open ship GUI for a single selection");
        assertTrue(!screenSource.contains("handleSummonSelectedShips();"),
                "Desk radar should not keep the non-legacy summon path");
        assertTrue(!screenSource.contains("hasShiftDown()"),
                "Desk radar should keep legacy single-selection behavior instead of multi-select");
    }

    @Test
    void serverShouldAuthorizeDeskRadarShipOpenByOwner() throws IOException {
        String networkSource = Files.readString(NETWORK_SOURCE);

        assertTrue(networkSource.contains("C2SDeskOpenShipPayload.TYPE"),
                "Desk radar ship-open payload should be registered");
        assertTrue(!networkSource.contains("C2SDeskSummonPayload.TYPE"),
                "Desk radar should not keep the non-legacy summon payload registered");
        assertTrue(networkSource.contains("Entity entity = serverLevel.getEntity(payload.shipUuid());"),
                "Server should resolve the ship from the payload UUID");
        assertTrue(networkSource.contains("if (entity instanceof EntityShipBase ship && ship.isOwnedBy(player)) {\n                ship.openShipMenu(player);\n            }"),
                "Server should only open ship menu for ships owned by the requesting player");
    }
}
