package org.trp.shincolle.entity;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipAircraftLaunchResourceRegressionTest {
    private static final Path SHIP_COMBAT =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBaseCombat.kt");

    @Test
    void aircraftAttackShouldNotSpendResourcesBeforeSpawnSucceeds() throws IOException {
        String source = Files.readString(SHIP_COMBAT);

        assertTrue(source.contains("""
    private boolean performLightAircraftAttack(Entity target) {
        if (!canUseLightAircraft()) {
            return false;
        }
        return spawnAircraft(target, true);
    }
"""), "Light aircraft attack should delegate resource spending to spawn success handling");
        assertTrue(source.contains("""
    private boolean performHeavyAircraftAttack(Entity target) {
        if (!canUseHeavyAircraft()) {
            return false;
        }
        return spawnAircraft(target, false);
    }
"""), "Heavy aircraft attack should delegate resource spending to spawn success handling");
        assertTrue(source.contains("""
        if (lightAircraft) {
            if (!consumeLightAmmo(AIRCRAFT_LIGHT_AMMO_COST)) {
                return false;
            }
            this.ship.setNumAircraftLight(Math.max(0, this.ship.getNumAircraftLight() - 1));
            this.ship.setFuel(this.ship.getFuel() - org.trp.shincolle.Config.fuelConsumeActionLightAircraft);
        } else {
            if (!consumeHeavyAmmo(AIRCRAFT_HEAVY_AMMO_COST)) {
                return false;
            }
            this.ship.setNumAircraftHeavy(Math.max(0, this.ship.getNumAircraftHeavy() - 1));
            this.ship.setFuel(this.ship.getFuel() - org.trp.shincolle.Config.fuelConsumeActionHeavyAircraft);
        }
"""), "Aircraft spawn should spend ammo, deck count, and fuel only after the entity type is created successfully");
        assertFalse(source.contains("""
        if (!consumeLightAmmo(AIRCRAFT_LIGHT_AMMO_COST)) {
            return false;
        }
        this.ship.setNumAircraftLight(Math.max(0, this.ship.getNumAircraftLight() - 1));
        this.ship.setFuel(this.ship.getFuel() - org.trp.shincolle.Config.fuelConsumeActionLightAircraft);
        return spawnAircraft(target, true);
"""), "Light aircraft launch should not deduct resources before spawnAircraft returns");
        assertFalse(source.contains("""
        if (!consumeHeavyAmmo(AIRCRAFT_HEAVY_AMMO_COST)) {
            return false;
        }
        this.ship.setNumAircraftHeavy(Math.max(0, this.ship.getNumAircraftHeavy() - 1));
        this.ship.setFuel(this.ship.getFuel() - org.trp.shincolle.Config.fuelConsumeActionHeavyAircraft);
        return spawnAircraft(target, false);
"""), "Heavy aircraft launch should not deduct resources before spawnAircraft returns");
    }
}
