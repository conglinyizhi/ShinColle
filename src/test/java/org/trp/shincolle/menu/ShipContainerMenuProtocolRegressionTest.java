package org.trp.shincolle.menu;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipContainerMenuProtocolRegressionTest {
    private static final Path MENU_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/menu/ShipContainerMenu.kt");
    private static final Path MOD_MENUS =
            Path.of("src/main/java/org/trp/shincolle/menu/ModMenus.kt");

    @Test
    void shipMenuShouldKeepRegistryFriendlyByteBufClientFactory() throws IOException {
        String modMenus = Files.readString(MOD_MENUS);

        assertTrue(modMenus.contains("IMenuTypeExtension.create(ShipContainerMenu::new)"),
                "Ship container menu should keep the RegistryFriendlyByteBuf client factory");
    }

    @Test
    void shipMenuShouldFailFastWhenTheTargetShipIsUnavailable() throws IOException {
        String source = Files.readString(MENU_SOURCE);

        assertTrue(source.contains("public ShipContainerMenu(int containerId, Inventory playerInv, RegistryFriendlyByteBuf buf)"),
                "Ship container menu should keep the client constructor that receives RegistryFriendlyByteBuf");
        assertTrue(source.contains("this(containerId, playerInv, getEntity(playerInv, buf));"),
                "Ship container menu should keep delegating buffer decoding through getEntity");
        assertTrue(source.contains("if (ship == null || !ship.isAlive() || ship.isRemoved()) {\n            throw new IllegalStateException(\"Ship entity is not available for menu access.\");\n        }"),
                "Ship container menu should fail fast before constructing against unavailable, dead, or removed ships");
        assertTrue(source.contains("private static EntityShipBase getEntity(Inventory playerInv, RegistryFriendlyByteBuf buf) {"),
                "Ship container menu should keep a dedicated buffer -> ship resolver");
        assertTrue(source.contains("if (buf == null) {\n            throw new IllegalStateException(\"Missing ship entity data.\");\n        }"),
                "Ship container menu should keep a stable missing-buffer failure message");
        assertTrue(source.contains("int entityId = buf.readInt();"),
                "Ship container menu should decode exactly one entity id from the menu payload");
        assertTrue(source.contains("playerInv.player.level().getEntity(entityId) instanceof EntityShipBase ship\n                && ship.isAlive()\n                && !ship.isRemoved()"),
                "Ship container menu should only resolve live, non-removed ships even when they are out of fuel");
        assertTrue(source.contains("throw new IllegalStateException(\"Ship entity not found.\");"),
                "Ship container menu should keep the stable fail-fast message for missing or invalid ships");
    }

    @Test
    void shipMenuShouldStayValidForLiveShipsEvenWhenTheyRunOutOfFuel() throws IOException {
        String source = Files.readString(MENU_SOURCE);

        assertTrue(source.contains("return ship.isAlive() && !ship.isRemoved() && player.distanceToSqr(ship) < 64.0D;"),
                "Ship container menu should remain valid only for live, non-removed ships even when no-fuel drives a dead-pose animation");
    }
}
