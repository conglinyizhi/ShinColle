package org.trp.shincolle.client

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.nio.file.Files
import java.nio.file.Path

class VolCoreScreenRegressionTest {
    private val SCREEN_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/client/screen/VolCoreScreen.kt")

    @Test
    fun volCoreScreenShouldKeepPowerBarAndActiveButtonVisuals() {
        val screen = Files.readString(SCREEN_SOURCE)

        assertTrue(screen.contains("if (menu.isBtnActive()) {")) {
            "VolCore screen should keep the active-button visual state"
        }
        assertTrue(screen.contains("graphics.blit(GUI_TEXTURE, leftPos + 7, topPos + 6, Sprites.VOLCORE_BTN_ACTIVE_U, Sprites.VOLCORE_BTN_ACTIVE_V, Sprites.VOLCORE_BTN_W, Sprites.VOLCORE_BTN_H);")) {
            "VolCore screen should keep drawing the active-button overlay"
        }
        assertTrue(screen.contains("int power = menu.getRemainedPower();")) {
            "VolCore screen should keep reading the remaining power value"
        }
        assertTrue(screen.contains("int scaleBar = (int) (power * 31.0 / 9600.0);")) {
            "VolCore screen should keep scaling the power bar against the legacy 9600-capacity model"
        }
        assertTrue(screen.contains("graphics.blit(GUI_TEXTURE, leftPos + 38, topPos + 59 - scaleBar, 0, 197 - scaleBar, 12, scaleBar);")) {
            "VolCore screen should keep drawing the scaled power bar"
        }
    }

    @Test
    fun volCoreScreenShouldKeepHoverPowerTooltip() {
        val screen = Files.readString(SCREEN_SOURCE)

        assertTrue(screen.contains("if (isHovering(36, 27, 16, 34, mouseX, mouseY)) {")) {
            "VolCore screen should keep the power-bar hover hitbox"
        }
        assertTrue(screen.contains("List<Component> tooltip = new ArrayList<>();")) {
            "VolCore screen should keep building a tooltip list for the power readout"
        }
        assertTrue(screen.contains("tooltip.add(Component.literal(String.valueOf(menu.getRemainedPower())));")) {
            "VolCore screen should keep exposing the raw remaining power in the tooltip"
        }
        assertTrue(screen.contains("graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);")) {
            "VolCore screen should keep rendering the power tooltip list"
        }
    }

    @Test
    fun volCoreScreenShouldKeepTopLeftToggleButtonAction() {
        val screen = Files.readString(SCREEN_SOURCE)

        assertTrue(screen.contains("if (xClick >= 7 && xClick <= 20 && yClick >= 6 && yClick <= 19) {")) {
            "VolCore screen should keep the top-left button click hitbox"
        }
        assertTrue(screen.contains("if (this.minecraft != null && this.minecraft.gameMode != null) {")) {
            "VolCore screen should keep guarding button clicks behind a valid game mode"
        }
        assertTrue(screen.contains("this.minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);")) {
            "VolCore screen should keep routing the toggle button to menu button 0"
        }
        assertTrue(screen.contains("return true;")) {
            "VolCore screen should consume the click after sending the toggle action"
        }
    }
}
