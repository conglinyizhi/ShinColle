package org.trp.shincolle.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipInventoryTooltipRegressionTest {
    private static final Path SCREEN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/screen/ShipInventoryScreen.java");
    private static final Path EN_US_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");

    private static final List<String> PAGE6_TOOLTIP_KEYS = List.of(
            "gui.shincolle.showhelditem",
            "gui.shincolle.equip.mount"
    );

    private static final List<String> PAGE7_TOOLTIP_KEYS = List.of(
            "gui.shincolle.ai.cooking",
            "gui.shincolle.ai.fishing",
            "gui.shincolle.ai.mining",
            "gui.shincolle.ai.crafting",
            "gui.shincolle.crane.usemeta",
            "gui.shincolle.crane.useoredict",
            "gui.shincolle.crane.usenbt"
    );

    private static final List<String> PAGE8_TOOLTIP_KEYS = List.of(
            "gui.shincolle.ai.inputside",
            "gui.shincolle.ai.outputside",
            "gui.shincolle.ai.fuelside"
    );

    @Test
    void shipInventoryScreenShouldKeepHoverTooltipEntrypointsForSettingsPages() throws IOException {
        String screen = Files.readString(SCREEN_SOURCE);

        assertTrue(screen.contains("renderLegacyHoverTooltips(guiGraphics, mouseX, mouseY);"),
                "Ship inventory render pass should keep the legacy hover tooltip bridge");
        assertTrue(screen.contains("if (this.activeSettingsTab == SETTINGS_TAB_7) { renderAIPage7Tooltips(g, mx, my); return; }"),
                "Settings page 7 should keep its hover tooltip dispatcher");
        assertTrue(screen.contains("if (this.activeSettingsTab == SETTINGS_TAB_8) { renderAIPage8Tooltips(g, mx, my); return; }"),
                "Settings page 8 should keep its hover tooltip dispatcher");
        assertTrue(screen.contains("renderFixedToggleTooltips(g, mx, my);"),
                "Fixed toggle tooltips should still be rendered for the appearance page");
    }

    @Test
    void shipInventoryScreenShouldKeepAppearanceToggleHoverTooltips() throws IOException {
        String screen = Files.readString(SCREEN_SOURCE);
        String enUs = Files.readString(EN_US_LANG);

        assertTrue(screen.contains("if (this.activeSettingsTab == SETTINGS_TAB_6) {"),
                "Settings page 6 should keep its fixed toggle tooltip block");
        assertTrue(screen.contains("TooltipBuilder.of(\"gui.shincolle.showhelditem\").renderIfNotEmpty(g, this.font, mx, my);"),
                "Show-held-item toggle should keep its hover tooltip");
        assertTrue(screen.contains("TooltipBuilder.of(\"gui.shincolle.equip.mount\").renderIfNotEmpty(g, this.font, mx, my);"),
                "Mount toggle should keep its hover tooltip");
        assertTrue(screen.contains("g.renderComponentTooltip(this.font, List.of(this.menu.getEquipOptionLabel(i)), mx, my);"),
                "Appearance option buttons should keep showing the localized option label on hover");

        for (String key : PAGE6_TOOLTIP_KEYS) {
            assertTrue(enUs.contains("\"" + key + "\""),
                    () -> "Expected en_us to keep defining " + key);
        }
    }

    @Test
    void shipInventoryScreenShouldKeepAutomationTooltipTranslations() throws IOException {
        String screen = Files.readString(SCREEN_SOURCE);
        String enUs = Files.readString(EN_US_LANG);

        assertTrue(screen.contains("String key = switch (idx) { case 1 -> \"gui.shincolle.ai.cooking\"; case 2 -> \"gui.shincolle.ai.fishing\"; case 3 -> \"gui.shincolle.ai.mining\"; case 4 -> \"gui.shincolle.ai.crafting\"; default -> null; };"),
                "Settings page 7 should keep mapping task buttons to their localized hover tooltips");
        assertTrue(screen.contains("if (key != null) TooltipBuilder.of(key).renderIfNotEmpty(g, this.font, mx, my);"),
                "Settings page 7 should keep rendering the selected task tooltip key");

        for (String key : PAGE7_TOOLTIP_KEYS) {
            assertTrue(enUs.contains("\"" + key + "\""),
                    () -> "Expected en_us to keep defining " + key);
        }

        for (String key : List.of(
                "gui.shincolle.crane.usemeta",
                "gui.shincolle.crane.useoredict",
                "gui.shincolle.crane.usenbt",
                "gui.shincolle.ai.inputside",
                "gui.shincolle.ai.outputside",
                "gui.shincolle.ai.fuelside"
        )) {
            assertTrue(screen.contains("TooltipBuilder.of(\"" + key + "\")"),
                    () -> "Expected ShipInventoryScreen to keep using tooltip key " + key);
            assertTrue(enUs.contains("\"" + key + "\""),
                    () -> "Expected en_us to keep defining " + key);
        }

        for (String key : PAGE8_TOOLTIP_KEYS) {
            assertTrue(enUs.contains("\"" + key + "\""),
                    () -> "Expected en_us to keep defining " + key);
        }
    }
}
