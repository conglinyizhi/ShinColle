package org.trp.shincolle.entity;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipSpecialAttackFriendlyFireRegressionTest {
    private static final Path SHIP_BASE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.java");
    private static final Path SHIP_COMBAT =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBaseCombat.java");

    @Test
    void shipBaseShouldExposeSharedSameOwnerAttackHelper() throws IOException {
        String shipBase = Files.readString(SHIP_BASE);
        String shipCombat = Files.readString(SHIP_COMBAT);

        assertTrue(shipBase.contains("""
    protected boolean isSameOwnerAttackTarget(Entity target) {
        return this.combat.isSameOwnerTarget(target);
    }
"""), "Ship base should expose the shared same-owner attack helper for specialized overrides");
        assertTrue(shipCombat.contains("""
    boolean isSameOwnerTarget(Entity target) {
        return isSameOwner(target);
    }
"""), "Combat helper should expose same-owner checks for specialized attack overrides");
    }

    @Test
    void specializedAttackOverridesShouldRejectSameOwnerTargetsBeforeAmmoSpend() throws IOException {
        assertGuardBeforeConsume("src/main/java/org/trp/shincolle/entity/EntityBattleshipRe.java",
                "if (!consumeLightAmmo(1)) {");
        assertGuardBeforeConsume("src/main/java/org/trp/shincolle/entity/EntityBattleshipTa.java",
                "if (this.numRensouhou > 0 && this.getRandom().nextInt(3) == 0) {");
        for (String file : List.of(
                "src/main/java/org/trp/shincolle/entity/EntityBattleshipRu.java",
                "src/main/java/org/trp/shincolle/entity/EntityBattleshipNagato.java",
                "src/main/java/org/trp/shincolle/entity/EntityBattleshipYamato.java",
                "src/main/java/org/trp/shincolle/entity/EntityIsolatedHime.java",
                "src/main/java/org/trp/shincolle/entity/EntitySubmHime.java",
                "src/main/java/org/trp/shincolle/entity/EntitySubmRo500.java",
                "src/main/java/org/trp/shincolle/entity/EntitySubmU511.java",
                "src/main/java/org/trp/shincolle/entity/EntitySubmYo.java")) {
            assertGuardBeforeConsume(file, "if (!consumeHeavyAmmo(1)) {");
        }
    }

    private static void assertGuardBeforeConsume(String file, String consumeMarker) throws IOException {
        String source = Files.readString(Path.of(file));
        int guardIndex = source.indexOf("if (isSameOwnerAttackTarget(target)) {");
        int consumeIndex = source.indexOf(consumeMarker);

        assertTrue(guardIndex >= 0, file + " should guard against same-owner targets");
        assertTrue(consumeIndex >= 0, file + " should still contain its attack resource spending path");
        assertTrue(guardIndex < consumeIndex,
                file + " should reject same-owner targets before spending attack resources");
    }
}
