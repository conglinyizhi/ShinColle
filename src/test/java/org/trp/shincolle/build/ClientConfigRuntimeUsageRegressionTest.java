package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientConfigRuntimeUsageRegressionTest {
    private static final Path SHIP_BASE_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt");
    private static final Path SHIP_COMBAT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBaseCombat.kt");
    private static final Path BOOK_RENDERER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/renderer/BookRenderer.kt");

    @Test
    void clientConfigTogglesShouldRemainConnectedToRuntimeConsumers() throws IOException {
        String shipBase = Files.readString(SHIP_BASE_SOURCE);
        String shipCombat = Files.readString(SHIP_COMBAT_SOURCE);
        String bookRenderer = Files.readString(BOOK_RENDERER_SOURCE);

        assertTrue(shipBase.contains("return Math.max(0.0F, Config.volumeShip);"),
                "Ship ambient/general voice volume should keep using Config.volumeShip");
        assertTrue(shipBase.contains("Math.max(0.0F, Config.volumeShip),"),
                "Ship voice playback should keep clamping against Config.volumeShip");

        assertTrue(shipCombat.contains("Math.max(0.0F, org.trp.shincolle.Config.volumeAttack)"),
                "Ship attack sound playback should keep using Config.volumeAttack");

        assertTrue(bookRenderer.contains("return org.trp.shincolle.Config.useMiSansFont")
                        && bookRenderer.contains("&& org.trp.shincolle.Config.miSansOnlyForLegacyLogs;"),
                "Legacy book renderer should keep gating MiSans through both client font toggles");
        assertTrue(bookRenderer.contains("withFont(FONT_MISANS)"),
                "Legacy book renderer should keep applying the MiSans font override when enabled");
    }
}
