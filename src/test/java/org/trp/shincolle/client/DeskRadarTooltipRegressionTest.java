package org.trp.shincolle.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DeskRadarTooltipRegressionTest {
    private static final Path SCREEN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/screen/DeskScreen.kt");
    private static final Path EN_US_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");

    private static final List<String> RADAR_TOOLTIP_KEYS = List.of(
            "gui.shincolle.radar.zoom.tooltip",
            "gui.shincolle.radar.clear.tooltip",
            "gui.shincolle.radar.action.recall.tooltip",
            "gui.shincolle.radar.action.open.tooltip"
    );

    @Test
    void radarHoverTextShouldKeepDedicatedTooltipBranches() throws IOException {
        String screen = Files.readString(SCREEN_SOURCE);

        assertTrue(screen.contains("private void drawRadarHoverText(GuiGraphics guiGraphics, int mx, int my, int mouseX, int mouseY) {"),
                "Desk radar should keep a dedicated hover-tooltip renderer");
        assertTrue(screen.contains("if (mx >= RADAR_ZOOM_X1 && mx <= RADAR_ZOOM_X2 && my >= RADAR_ZOOM_Y1 && my <= RADAR_ZOOM_Y2) {"),
                "Desk radar should keep the zoom-button hover hitbox");
        assertTrue(screen.contains("if (mx >= RADAR_CLEAR_X1 && mx <= RADAR_CLEAR_X2 && my >= RADAR_CLEAR_Y1 && my <= RADAR_CLEAR_Y2) {"),
                "Desk radar should keep the clear-button hover hitbox");
        assertTrue(screen.contains("if (!this.selectedShips.isEmpty()\n                && mx >= RADAR_ACTION_X1 && mx <= RADAR_ACTION_X2\n                && my >= RADAR_ACTION_Y1 && my <= RADAR_ACTION_Y2) {"),
                "Desk radar action tooltip should remain gated by a non-empty selection");
    }

    @Test
    void radarActionTooltipShouldStayBoundToDeskMode() throws IOException {
        String screen = Files.readString(SCREEN_SOURCE);
        String enUs = Files.readString(EN_US_LANG);

        assertTrue(screen.contains("String key = menu.getDeskType() == 0\n                    ? \"gui.shincolle.radar.action.recall.tooltip\"\n                    : \"gui.shincolle.radar.action.open.tooltip\";"),
                "Desk radar action tooltip should keep switching between recall and open based on desk mode");
        assertTrue(screen.contains("guiGraphics.renderTooltip(this.font, Component.translatable(key), mouseX, mouseY);"),
                "Desk radar should render the selected action tooltip key");

        for (String key : RADAR_TOOLTIP_KEYS) {
            assertTrue(enUs.contains("\"" + key + "\""),
                    () -> "Expected en_us to keep defining " + key);
        }
    }

    @Test
    void radarShipDotsShouldKeepShowingNameTooltipList() throws IOException {
        String screen = Files.readString(SCREEN_SOURCE);

        assertTrue(screen.contains("java.util.List<Component> list = new java.util.ArrayList<>();"),
                "Desk radar should keep collecting hovered ship labels into a tooltip list");
        assertTrue(screen.contains("list.add(obj.ship.getName());"),
                "Desk radar should keep showing the hovered ship display name");
        assertTrue(screen.contains("guiGraphics.renderComponentTooltip(this.font, list, mouseX, mouseY);"),
                "Desk radar should render the hovered ship-name tooltip list");
    }
}
