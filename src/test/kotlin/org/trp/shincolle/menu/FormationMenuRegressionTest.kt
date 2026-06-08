package org.trp.shincolle.menu

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

import org.junit.jupiter.api.Assertions.assertTrue

class FormationMenuRegressionTest {
    private val FORMATION_MENU_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/menu/FormationMenu.kt")
    private val POINTER_ITEM_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/item/PointerItem.kt")
    private val POINTER_SERVICE_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/server/PointerInteractionService.kt")

    @Test
    fun formationMenuShouldStayBoundToPointerHostItem() {
        val menuSource = Files.readString(FORMATION_MENU_SOURCE)

        assertTrue(menuSource.contains("return player.getMainHandItem().getItem() instanceof PointerItem\n                || player.getOffhandItem().getItem() instanceof PointerItem;")) {
            "Formation menu should close once the player no longer holds a pointer item"
        }
    }

    @Test
    fun pointerEntrypointsShouldRemainTheOnlyFormationMenuHosts() {
        val itemSource = Files.readString(POINTER_ITEM_SOURCE)
        val serviceSource = Files.readString(POINTER_SERVICE_SOURCE)

        assertTrue(itemSource.contains("if (player.isShiftKeyDown() && getMode(stack) == MODE_FORMATION) {\n                player.openMenu(new net.minecraft.world.SimpleMenuProvider(")) {
            "Pointer item right click should keep opening the formation menu in formation mode"
        }
        assertTrue(serviceSource.contains("} else if (action == 4) {\n            player.openMenu(new SimpleMenuProvider(")) {
            "Pointer payload action 4 should keep opening the formation menu from the pointer service"
        }
    }
}
