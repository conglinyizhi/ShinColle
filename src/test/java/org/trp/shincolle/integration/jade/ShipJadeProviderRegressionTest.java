package org.trp.shincolle.integration.jade;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipJadeProviderRegressionTest {
    private static final Path SHIP_JADE_PROVIDER =
            Path.of("src/main/java/org/trp/shincolle/integration/jade/ShipJadeProvider.java");
    private static final List<Path> MAINTAINED_LANGS = List.of(
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
    );

    @Test
    void shipJadeProviderShouldExposeRunningStateTooltip() throws IOException {
        String source = Files.readString(SHIP_JADE_PROVIDER);

        assertTrue(source.contains("tooltip.add(Component.translatable(\"tooltip.shincolle.jade.ship.status\", runningState(ship)));"),
                "Jade ship tooltip should render the ship running state");
        assertTrue(source.contains("private static Component runningState(EntityShipBase ship) {"),
                "Ship Jade provider should keep a dedicated running-state resolver");
        assertTrue(source.contains("int taskId = ship.getStateMinor(ShipContainerMenu.STATE_MINOR_TASK_ID);"),
                "Ship Jade running-state resolver should read the active ship task");
        assertTrue(source.contains("if (ship.hasBlockGuardTarget()) {"),
                "Ship Jade running-state resolver should detect block guard state");
        assertTrue(source.contains("if (ship.hasPointerTargetEntity()) {"),
                "Ship Jade running-state resolver should detect pointer attack state");
        assertTrue(source.contains("if (ship.hasPointerTarget()) {"),
                "Ship Jade running-state resolver should detect pointer move state");
        assertTrue(source.contains("if (ship.shouldFollowOwner()) {"),
                "Ship Jade running-state resolver should detect owner-follow state");
        assertTrue(source.contains("return Component.translatable(switch (ship.explainFollowBlockReason()) {"),
                "Ship Jade running-state resolver should reuse the existing follow-block reason boundary");
    }

    @Test
    void shipJadeProviderShouldKeepLocalizedRunningStateKeys() throws IOException {
        for (Path lang : MAINTAINED_LANGS) {
            String content = Files.readString(lang);
            for (String key : List.of(
                    "tooltip.shincolle.jade.ship.status",
                    "tooltip.shincolle.jade.ship.status.idle",
                    "tooltip.shincolle.jade.ship.status.standby",
                    "tooltip.shincolle.jade.ship.status.follow",
                    "tooltip.shincolle.jade.ship.status.guard",
                    "tooltip.shincolle.jade.ship.status.pointer_move",
                    "tooltip.shincolle.jade.ship.status.pointer_attack",
                    "tooltip.shincolle.jade.ship.status.no_fuel"
            )) {
                assertTrue(content.contains("\"" + key + "\""),
                        () -> "Expected " + lang.getFileName() + " to define " + key);
            }
        }
    }
}
