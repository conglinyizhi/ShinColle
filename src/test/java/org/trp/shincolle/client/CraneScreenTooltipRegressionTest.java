package org.trp.shincolle.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CraneScreenTooltipRegressionTest {
    private static final Path SCREEN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/screen/CraneScreen.java");
    private static final Path EN_US_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");

    private static final List<String> REQUIRED_CRANE_KEYS = List.of(
            "gui.shincolle.crane.usemeta",
            "gui.shincolle.crane.useoredict",
            "gui.shincolle.crane.usenbt",
            "gui.shincolle.crane.red0",
            "gui.shincolle.crane.red1",
            "gui.shincolle.crane.red2",
            "gui.shincolle.crane.liquid0",
            "gui.shincolle.crane.liquid1",
            "gui.shincolle.crane.liquid2",
            "gui.shincolle.crane.energy0",
            "gui.shincolle.crane.energy1",
            "gui.shincolle.crane.energy2",
            "gui.shincolle.crane.nowait1",
            "gui.shincolle.crane.untilfull1",
            "gui.shincolle.crane.untilfull2",
            "gui.shincolle.crane.untilempty1",
            "gui.shincolle.crane.untilempty2",
            "gui.shincolle.crane.excess1",
            "gui.shincolle.crane.excess2",
            "gui.shincolle.crane.remain1",
            "gui.shincolle.crane.remain2"
    );

    @Test
    void craneScreenShouldKeepOptionToggleTooltipBranches() throws IOException {
        String screen = Files.readString(SCREEN_SOURCE);
        String enUs = Files.readString(EN_US_LANG);

        assertTrue(screen.contains("if (my > 21 && my < 34) {"),
                "Crane screen should keep the option-toggle hover row");
        assertTrue(screen.contains("if (mx > 22 && mx < 35) tooltip.add(Component.translatable(\"gui.shincolle.crane.usemeta\"));"),
                "Crane screen should keep metadata-check hover text");
        assertTrue(screen.contains("else if (mx > 36 && mx < 49) tooltip.add(Component.translatable(\"gui.shincolle.crane.useoredict\"));"),
                "Crane screen should keep ore-dictionary hover text");
        assertTrue(screen.contains("else if (mx > 50 && mx < 63) tooltip.add(Component.translatable(\"gui.shincolle.crane.usenbt\"));"),
                "Crane screen should keep NBT-check hover text");
        assertTrue(screen.contains("tooltip.add(Component.translatable(\"gui.shincolle.crane.red\" + r));"),
                "Crane screen should keep the dynamic redstone-mode hover text");

        for (String key : List.of(
                "gui.shincolle.crane.usemeta",
                "gui.shincolle.crane.useoredict",
                "gui.shincolle.crane.usenbt",
                "gui.shincolle.crane.red0",
                "gui.shincolle.crane.red1",
                "gui.shincolle.crane.red2"
        )) {
            assertTrue(enUs.contains("\"" + key + "\""),
                    () -> "Expected en_us to keep defining " + key);
        }
    }

    @Test
    void craneScreenShouldKeepLiquidAndEnergyTooltipBranches() throws IOException {
        String screen = Files.readString(SCREEN_SOURCE);
        String enUs = Files.readString(EN_US_LANG);

        assertTrue(screen.contains("} else if (my > 35 && my < 50) {"),
                "Crane screen should keep the liquid/energy hover row");
        assertTrue(screen.contains("tooltip.add(Component.translatable(\"gui.shincolle.crane.liquid\" + l));"),
                "Crane screen should keep the dynamic liquid-mode hover text");
        assertTrue(screen.contains("tooltip.add(Component.translatable(\"gui.shincolle.crane.energy\" + e));"),
                "Crane screen should keep the dynamic energy-mode hover text");

        for (String key : List.of(
                "gui.shincolle.crane.liquid0",
                "gui.shincolle.crane.liquid1",
                "gui.shincolle.crane.liquid2",
                "gui.shincolle.crane.energy0",
                "gui.shincolle.crane.energy1",
                "gui.shincolle.crane.energy2"
        )) {
            assertTrue(enUs.contains("\"" + key + "\""),
                    () -> "Expected en_us to keep defining " + key);
        }
    }

    @Test
    void craneScreenShouldKeepModeDescriptionTooltipAndFinalRenderer() throws IOException {
        String screen = Files.readString(SCREEN_SOURCE);
        String enUs = Files.readString(EN_US_LANG);

        assertTrue(screen.contains("if (mx > 22 && mx < 91 && my > 5 && my < 20) {"),
                "Crane screen should keep the main mode-description hover hitbox");
        assertTrue(screen.contains("if (m == 0) tooltip.add(Component.translatable(\"gui.shincolle.crane.nowait1\"));"),
                "Crane screen should keep no-wait hover text");
        assertTrue(screen.contains("tooltip.add(Component.translatable(\"gui.shincolle.crane.untilfull1\"));"),
                "Crane screen should keep until-full hover text");
        assertTrue(screen.contains("tooltip.add(Component.translatable(\"gui.shincolle.crane.untilempty1\"));"),
                "Crane screen should keep until-empty hover text");
        assertTrue(screen.contains("tooltip.add(Component.translatable(\"gui.shincolle.crane.excess1\"));"),
                "Crane screen should keep excess-stack hover text");
        assertTrue(screen.contains("tooltip.add(Component.translatable(\"gui.shincolle.crane.remain1\"));"),
                "Crane screen should keep remain-stack hover text");
        assertTrue(screen.contains("if (!tooltip.isEmpty()) {\n            graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);\n        }"),
                "Crane screen should keep rendering the collected tooltip lines together");

        for (String key : REQUIRED_CRANE_KEYS) {
            assertTrue(enUs.contains("\"" + key + "\""),
                    () -> "Expected en_us to keep defining " + key);
        }
    }
}
