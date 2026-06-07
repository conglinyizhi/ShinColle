package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipAmmoValueRegressionTest {
    private static final Path COMBAT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBaseCombat.kt");

    @Test
    void ammoItemsShouldKeepLegacyInternalValueScale() throws IOException {
        String source = Files.readString(COMBAT_SOURCE);
        assertTrue(source.contains("private static final int AMMO_LIGHT_VALUE = 30;"),
                "Light ammo should keep the legacy 30-point internal value");
        assertTrue(source.contains("private static final int AMMO_HEAVY_VALUE = 15;"),
                "Heavy ammo should keep the legacy 15-point internal value");
    }

    @Test
    void ammoContainersShouldKeepLegacyNineRoundValueScale() throws IOException {
        String source = Files.readString(COMBAT_SOURCE);
        assertTrue(source.contains("private static final int AMMO_LIGHT_CONTAINER_VALUE = 270;"),
                "Light ammo container should keep the legacy 270-point internal value");
        assertTrue(source.contains("private static final int AMMO_HEAVY_CONTAINER_VALUE = 135;"),
                "Heavy ammo container should keep the legacy 135-point internal value");
    }

    @Test
    void aircraftCostsShouldRemainCompatibleWithLegacyAmmoScale() throws IOException {
        String source = Files.readString(COMBAT_SOURCE);
        assertTrue(source.contains("private static final int AIRCRAFT_LIGHT_AMMO_COST = 6;"),
                "Aircraft light ammo cost should stay on the legacy internal ammo scale");
        assertTrue(source.contains("private static final int AIRCRAFT_HEAVY_AMMO_COST = 2;"),
                "Aircraft heavy ammo cost should stay on the legacy internal ammo scale");
    }
}
