package org.trp.shincolle.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipMenuOpenGuardRegressionTest {
    private static final Path SHIP_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.java");
    private static final Path DESK_SERVICE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/server/DeskInteractionService.java");
    private static final Path POINTER_SERVICE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/server/PointerInteractionService.java");

    @Test
    void shipMenuOpenShouldRejectInvalidServerSideShipStates() throws IOException {
        String shipSource = Files.readString(SHIP_SOURCE);

        assertTrue(shipSource.contains("public void openShipMenu(Player player) {"),
                "EntityShipBase should keep a dedicated menu-open entrypoint");
        assertTrue(shipSource.contains("if (!(player instanceof ServerPlayer serverPlayer)\n                || !this.isAlive()\n                || this.isInDeadPose()) {\n            return;\n        }"),
                "EntityShipBase menu-open should reject non-server players and dead ship states");
        assertTrue(shipSource.contains("if (this.level() != serverPlayer.level()) {\n            return;\n        }"),
                "EntityShipBase menu-open should reject cross-level menu open attempts");
        assertTrue(shipSource.contains("if (!this.isOwnedBy(player)) {\n            return;\n        }"),
                "EntityShipBase menu-open should reject non-owner menu open attempts even if called directly");
        assertTrue(shipSource.contains("(serverPlayer).openMenu(provider, buffer -> buffer.writeInt(this.getId()));"),
                "EntityShipBase menu-open should still route through the server menu provider when authorized");
    }

    @Test
    void shipMenuOpenServicesShouldOnlyForwardOwnedLiveShips() throws IOException {
        String deskServiceSource = Files.readString(DESK_SERVICE_SOURCE);
        String pointerServiceSource = Files.readString(POINTER_SERVICE_SOURCE);

        assertTrue(deskServiceSource.contains("if (entity instanceof EntityShipBase ship\n                && ship.isOwnedBy(player)\n                && ship.isAlive()\n                && !ship.isInDeadPose()) {\n            ship.openShipMenu(player);\n        }"),
                "Desk ship-open service should only forward owned ships that are alive and not in dead pose");
        assertTrue(pointerServiceSource.contains("if (entity instanceof EntityShipBase ship\n                && ship.isOwnedBy(player)\n                && ship.isAlive()\n                && !ship.isInDeadPose()) {\n            ship.openShipMenu(player);\n        }"),
                "Pointer ship-open service should only forward owned ships that are alive and not in dead pose");
    }
}
