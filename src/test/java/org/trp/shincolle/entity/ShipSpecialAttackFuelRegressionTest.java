package org.trp.shincolle.entity;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipSpecialAttackFuelRegressionTest {
    @Test
    void specialLightAttacksShouldStillConsumeFuelAfterAmmoSpend() throws IOException {
        assertFuelAfterConsume(
                "src/main/java/org/trp/shincolle/entity/EntityBattleshipTa.java",
                "if (consumeLightAmmo(4)) {",
                "this.setFuel(this.getFuel() - org.trp.shincolle.Config.fuelConsumeActionLight);");
        assertFuelAfterConsume(
                "src/main/java/org/trp/shincolle/entity/EntityDestroyerShimakaze.java",
                "if (!consumeLightAmmo(4)) {",
                "this.setFuel(this.getFuel() - org.trp.shincolle.Config.fuelConsumeActionLight);");
    }

    @Test
    void specialHeavyAttacksShouldStillConsumeFuelAfterAmmoSpend() throws IOException {
        Map<String, String> files = Map.of(
                "src/main/java/org/trp/shincolle/entity/EntityBattleshipRu.java", "this.setFuel(this.getFuel() - org.trp.shincolle.Config.fuelConsumeActionHeavy);",
                "src/main/java/org/trp/shincolle/entity/EntityDestroyerShimakaze.java", "this.setFuel(this.getFuel() - org.trp.shincolle.Config.fuelConsumeActionHeavy);",
                "src/main/java/org/trp/shincolle/entity/EntitySubmHime.java", "this.setFuel(this.getFuel() - org.trp.shincolle.Config.fuelConsumeActionHeavy);",
                "src/main/java/org/trp/shincolle/entity/EntitySubmYo.java", "this.setFuel(this.getFuel() - org.trp.shincolle.Config.fuelConsumeActionHeavy);",
                "src/main/java/org/trp/shincolle/entity/EntitySubmU511.java", "this.setFuel(this.getFuel() - org.trp.shincolle.Config.fuelConsumeActionHeavy);",
                "src/main/java/org/trp/shincolle/entity/EntitySubmRo500.java", "this.setFuel(this.getFuel() - org.trp.shincolle.Config.fuelConsumeActionHeavy);",
                "src/main/java/org/trp/shincolle/entity/EntityIsolatedHime.java", "this.setFuel(this.getFuel() - org.trp.shincolle.Config.fuelConsumeActionHeavy);");

        for (String file : files.keySet()) {
            assertFuelAfterConsume(file, "if (!consumeHeavyAmmo(1)) {", files.get(file));
        }
    }

    private static void assertFuelAfterConsume(String file, String consumeMarker, String fuelMarker) throws IOException {
        String source = Files.readString(Path.of(file));
        int consumeIndex = source.indexOf(consumeMarker);
        int fuelIndex = source.indexOf(fuelMarker, consumeIndex);
        int attackTickIndex = source.indexOf("this.setAttackTick(", consumeIndex);

        assertTrue(consumeIndex >= 0, file + " should still spend ammo");
        assertTrue(fuelIndex >= 0, file + " should still spend fuel after ammo consumption");
        assertTrue(attackTickIndex >= 0, file + " should still trigger attack tick state");
        assertTrue(consumeIndex < fuelIndex && fuelIndex < attackTickIndex,
                file + " should deduct fuel after ammo spend and before attack state side effects");
    }
}
