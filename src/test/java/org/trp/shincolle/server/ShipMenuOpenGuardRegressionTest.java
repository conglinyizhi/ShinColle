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
        assertTrue(shipSource.contains("if (!(player instanceof ServerPlayer serverPlayer)\n                || !this.isAlive()) {\n            return;\n        }"),
                "EntityShipBase menu-open should reject non-server players and actually dead ship states");
        assertTrue(shipSource.contains("if (this.level() != serverPlayer.level()) {\n            return;\n        }"),
                "EntityShipBase menu-open should reject cross-level menu open attempts");
        assertTrue(shipSource.contains("if (!this.isOwnedBy(player)) {\n            return;\n        }"),
                "EntityShipBase menu-open should reject non-owner menu open attempts even if called directly");
        assertTrue(shipSource.contains("(serverPlayer).openMenu(provider, buffer -> buffer.writeInt(this.getId()));"),
                "EntityShipBase menu-open should still route through the server menu provider when authorized");
    }

    @Test
    void shipMenuOpenServicesShouldForwardOwnedLiveShipsEvenWhenOutOfFuel() throws IOException {
        String deskServiceSource = Files.readString(DESK_SERVICE_SOURCE);
        String pointerServiceSource = Files.readString(POINTER_SERVICE_SOURCE);

        assertTrue(deskServiceSource.contains("if (entity instanceof EntityShipBase ship\n                && ship.isOwnedBy(player)\n                && ship.isAlive()) {\n            ship.openShipMenu(player);\n        }"),
                "Desk ship-open service should keep forwarding owned ships that are alive, including out-of-fuel ships");
        assertTrue(pointerServiceSource.contains("if (entity instanceof EntityShipBase ship\n                && ship.isOwnedBy(player)\n                && ship.isAlive()) {\n            ship.openShipMenu(player);\n        }"),
                "Pointer ship-open service should keep forwarding owned ships that are alive, including out-of-fuel ships");
    }

    @Test
    void ownedShipMobInteractShouldKeepSneakOpenAndDefaultRightClickStateToggle() throws IOException {
        String shipSource = Files.readString(SHIP_SOURCE);

        assertTrue(shipSource.contains("if (player.isShiftKeyDown()) {\n                this.openShipMenu(player);\n                this.resetInteractionEmotionState();\n                this.focusOnPlayer(player);\n                return InteractionResult.sidedSuccess(this.level().isClientSide);\n            }"),
                "Owned ship interaction should keep opening the ship menu on sneak right-click");
        assertTrue(shipSource.contains("boolean isSitting = !this.isOrderedToSit();\n            this.setOrderedToSit(isSitting);\n            this.setInSittingPose(isSitting);"),
                "Owned ship interaction should keep using default right-click to toggle standby and follow state");
        assertTrue(shipSource.contains("if (!isSitting && this.hasBlockGuardTarget()) {\n                this.clearGuardTarget();\n            }"),
                "Leaving standby through default right-click should still clear stale block-guard targets");
    }
}
