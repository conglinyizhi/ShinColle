package org.trp.shincolle.menu;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FormationMenuRegressionTest {
    private static final Path FORMATION_MENU_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/menu/FormationMenu.kt");
    private static final Path POINTER_ITEM_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/item/PointerItem.kt");
    private static final Path POINTER_SERVICE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/server/PointerInteractionService.kt");

    @Test
    void formationMenuShouldStayBoundToPointerHostItem() throws IOException {
        String menuSource = Files.readString(FORMATION_MENU_SOURCE);

        assertTrue(menuSource.contains("return player.getMainHandItem().getItem() instanceof PointerItem\n                || player.getOffhandItem().getItem() instanceof PointerItem;"),
                "Formation menu should close once the player no longer holds a pointer item");
    }

    @Test
    void pointerEntrypointsShouldRemainTheOnlyFormationMenuHosts() throws IOException {
        String itemSource = Files.readString(POINTER_ITEM_SOURCE);
        String serviceSource = Files.readString(POINTER_SERVICE_SOURCE);

        assertTrue(itemSource.contains("if (player.isShiftKeyDown() && getMode(stack) == MODE_FORMATION) {\n                player.openMenu(new net.minecraft.world.SimpleMenuProvider("),
                "Pointer item right click should keep opening the formation menu in formation mode");
        assertTrue(serviceSource.contains("} else if (action == 4) {\n            player.openMenu(new SimpleMenuProvider("),
                "Pointer payload action 4 should keep opening the formation menu from the pointer service");
    }
}
