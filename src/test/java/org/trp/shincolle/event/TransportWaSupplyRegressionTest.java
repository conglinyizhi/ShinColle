package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TransportWaSupplyRegressionTest {
    private static final Path WA_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/EntityTransportWa.kt");

    @Test
    void waShouldKeepLegacyAmmoThresholdsForSelfSupplyAndAllySupply() throws IOException {
        String source = Files.readString(WA_SOURCE);
        assertTrue(source.contains("if (this.getAmmoLight() <= 540) {"),
                "Transport Wa should restock light ammo at the legacy 540 threshold");
        assertTrue(source.contains("if (this.getAmmoHeavy() <= 270) {"),
                "Transport Wa should restock heavy ammo at the legacy 270 threshold");
        assertTrue(source.contains("if (this.getAmmoLight() >= 540 && ship.getAmmoLight() < 270) {"),
                "Transport Wa should only supply allies when it has enough light ammo reserve");
        assertTrue(source.contains("if (this.getAmmoHeavy() >= 270 && ship.getAmmoHeavy() < 135) {"),
                "Transport Wa should only supply allies when it has enough heavy ammo reserve");
    }

    @Test
    void waShouldUseLegacyAmmoBundleValuesWhenConsumingInventorySupplies() throws IOException {
        String source = Files.readString(WA_SOURCE);
        assertTrue(source.contains("addAmmoLight(30);"),
                "Single light ammo items should refill 30 internal ammo for Transport Wa");
        assertTrue(source.contains("addAmmoLight(270);"),
                "Light ammo containers should refill 270 internal ammo for Transport Wa");
        assertTrue(source.contains("addAmmoHeavy(15);"),
                "Single heavy ammo items should refill 15 internal ammo for Transport Wa");
        assertTrue(source.contains("addAmmoHeavy(135);"),
                "Heavy ammo containers should refill 135 internal ammo for Transport Wa");
    }

    @Test
    void waShouldApplyReceiverAmmoAndGrudgeModifiersWhenSupplyingAllies() throws IOException {
        String source = Files.readString(WA_SOURCE);
        assertTrue(source.contains("5400.0F * ship.getLegacyShipStats().getBuffedAttr(17)"),
                "Grudge supply should scale with the receiver's grudge modifier like the legacy branch");
        assertTrue(source.contains("540.0F * ship.getLegacyShipStats().getBuffedAttr(18)"),
                "Light ammo supply should scale with the receiver's ammo modifier like the legacy branch");
        assertTrue(source.contains("270.0F * ship.getLegacyShipStats().getBuffedAttr(18)"),
                "Heavy ammo supply should scale with the receiver's ammo modifier like the legacy branch");
    }
}
