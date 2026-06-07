package org.trp.shincolle.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DeskRadarOpenShipRegressionTest {
    private static final Path SCREEN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/screen/DeskScreen.kt");
    private static final Path NETWORK_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/network/ModNetwork.kt");
    private static final Path DESK_SERVICE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/server/DeskInteractionService.kt");

    @Test
    void radarListShouldStillOpenShipMenuWhenClickingSameShipAgain() throws IOException {
        String screenSource = Files.readString(SCREEN_SOURCE);

        assertTrue(screenSource.contains("boolean sameSelection = this.selectedShips.size() == 1 && this.selectedShips.contains(shipUuid);"),
                "Desk radar should detect clicking the already selected ship");
        assertTrue(screenSource.contains("if (entity instanceof EntityShipBase ship && ship.isAlive() && !ship.isRemoved() && ship.getOwnerUUID() != null && ship.getOwnerUUID().equals(this.minecraft.player.getUUID())) {"),
                "Desk radar list should ignore removed ships while collecting owned radar targets");
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
        assertTrue(screenSource.contains("if (menu.getDeskType() == 0) {\n            summonSelectedShipsToDesk();\n        } else if (this.selectedShips.size() == 1) {\n            openRadarSelectedShip(this.selectedShips.iterator().next());\n        }"),
                "Desk radar action button should summon on block desks but keep portable-radar single-selection ship open behavior");
        assertTrue(screenSource.contains("if (hasShiftDown() && menu.getDeskType() == 0)"),
                "Block desk radar should support multi-select for summon without changing portable radar selection behavior");
        assertTrue(!screenSource.contains("handleSummonSelectedShips();"),
                "Desk radar should not keep the non-legacy summon path");
        assertTrue(screenSource.contains("PacketDistributor.sendToServer(new C2SDeskSummonPayload(List.copyOf(this.selectedShips)));"),
                "Block desk radar should send selected ship UUIDs to the server summon payload");
    }

    @Test
    void serverShouldAuthorizeDeskRadarShipOpenByOwner() throws IOException {
        String networkSource = Files.readString(NETWORK_SOURCE);
        String deskServiceSource = Files.readString(DESK_SERVICE_SOURCE);

        assertTrue(networkSource.contains("C2SDeskOpenShipPayload.TYPE"),
                "Desk radar ship-open payload should be registered");
        assertTrue(networkSource.contains("C2SDeskSummonPayload.TYPE"),
                "Desk radar summon payload should be registered for block desks");
        assertTrue(networkSource.contains("DeskInteractionService.openOwnedShipFromDesk(player, payload.shipUuid());"),
                "Network should delegate desk ship-open authorization to the service layer");
        assertTrue(networkSource.contains("DeskInteractionService.summonOwnedShipsToDesk(player, payload.shipUuids());"),
                "Network should delegate desk summon authorization to the service layer");
        assertTrue(deskServiceSource.contains("Entity entity = serverLevel.getEntity(shipUuid);"),
                "Server should resolve the ship from the payload UUID");
        assertTrue(deskServiceSource.contains("if (entity instanceof EntityShipBase ship\n                && ship.isOwnedBy(player)\n                && ship.isAlive()\n                && !ship.isRemoved()) {\n            ship.openShipMenu(player);\n        }"),
                "Server should keep opening ship menu for owned ships that are alive and not removed, including out-of-fuel ships that need refuel");
        assertTrue(deskServiceSource.contains("if (entity instanceof EntityShipBase ship && ship.isOwnedBy(player) && ship.isAlive() && !ship.isRemoved() && !ship.isInDeadPose())"),
                "Server should only summon live, non-removed ships owned by the requesting player");
        assertTrue(deskServiceSource.contains("FormationHelper.applySummonShipsToDesk(player, deskMenu.getBlockEntity().getBlockPos(), ownedShips);"),
                "Server-side desk summon should route through the shared formation summon helper");
    }
}
