package org.trp.shincolle.entity

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

import org.junit.jupiter.api.Assertions.assertTrue

class ShipSpecialAttackFriendlyFireRegressionTest {
    private val SHIP_BASE: Path =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBase.kt")
    private val SHIP_COMBAT: Path =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBaseCombat.kt")

    @Test
    fun shipBaseShouldExposeSharedSameOwnerAttackHelper() {
        val shipBase = Files.readString(SHIP_BASE)
        val shipCombat = Files.readString(SHIP_COMBAT)

        assertTrue(shipBase.contains("""
    protected boolean isSameOwnerAttackTarget(Entity target) {
        return this.combat.isSameOwnerTarget(target);
    }
""")) { "Ship base should expose the shared same-owner attack helper for specialized overrides" }
        assertTrue(shipCombat.contains("""
    boolean isSameOwnerTarget(Entity target) {
        return isSameOwner(target);
    }
""")) { "Combat helper should expose same-owner checks for specialized attack overrides" }
    }

    @Test
    fun specializedAttackOverridesShouldRejectSameOwnerTargetsBeforeAmmoSpend() {
        assertGuardBeforeConsume("src/main/java/org/trp/shincolle/entity/EntityBattleshipRe.java",
                "if (!consumeLightAmmo(1)) {")
        assertGuardBeforeConsume("src/main/java/org/trp/shincolle/entity/EntityBattleshipTa.java",
                "if (this.numRensouhou > 0 && this.getRandom().nextInt(3) == 0) {")
        for (file in listOf(
                "src/main/java/org/trp/shincolle/entity/EntityBattleshipRu.java",
                "src/main/java/org/trp/shincolle/entity/EntityBattleshipNagato.java",
                "src/main/java/org/trp/shincolle/entity/EntityBattleshipYamato.java",
                "src/main/java/org/trp/shincolle/entity/EntityIsolatedHime.java",
                "src/main/java/org/trp/shincolle/entity/EntitySubmHime.java",
                "src/main/java/org/trp/shincolle/entity/EntitySubmRo500.java",
                "src/main/java/org/trp/shincolle/entity/EntitySubmU511.java",
                "src/main/java/org/trp/shincolle/entity/EntitySubmYo.kt")) {
            assertGuardBeforeConsume(file, "if (!consumeHeavyAmmo(1)) {")
        }
    }

    private fun assertGuardBeforeConsume(file: String, consumeMarker: String) {
        val source = Files.readString(Path.of(file))
        val guardIndex = source.indexOf("if (isSameOwnerAttackTarget(target)) {")
        val consumeIndex = source.indexOf(consumeMarker)

        assertTrue(guardIndex >= 0) { file + " should guard against same-owner targets" }
        assertTrue(consumeIndex >= 0) { file + " should still contain its attack resource spending path" }
        assertTrue(guardIndex < consumeIndex) {
            file + " should reject same-owner targets before spending attack resources"
        }
    }
}
