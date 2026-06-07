package org.trp.shincolle.entity;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipFriendlyFirePrecheckRegressionTest {
    private static final Path SHIP_COMBAT =
            Path.of("src/main/java/org/trp/shincolle/entity/base/EntityShipBaseCombat.kt");

    @Test
    void lightAndHeavyAttacksShouldRejectSameOwnerTargetsBeforeSpendingAmmo() throws IOException {
        String source = Files.readString(SHIP_COMBAT);

        assertTrue(source.contains("""
    void performLightAttack(Entity target) {
        if (!canUseLightAmmo()) {
            return;
        }
        if (!(this.ship.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (target == null || !target.isAlive()) {
            return;
        }
        if (isSameOwner(target)) {
            return;
        }
        if (!consumeLightAmmo(1)) {
            return;
        }
"""), "Light attack should reject same-owner targets before consuming ammo");
        assertTrue(source.contains("""
    boolean performHeavyAttack(Entity target) {
        if (!canUseHeavyAmmo()) {
            return false;
        }
        if (!(this.ship.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (isSameOwner(target)) {
            return false;
        }
        if (Config.enableFiringLineCheck && !hasClearFiringLine(target)) {
            return false;
        }
        if (!consumeHeavyAmmo(1)) {
            return false;
        }
"""), "Heavy attack should reject same-owner targets before spending ammo");
    }

    @Test
    void sameOwnerCheckShouldCoverPlayersShipsMountsAndAircraft() throws IOException {
        String source = Files.readString(SHIP_COMBAT);

        assertTrue(source.contains("if (target instanceof net.minecraft.world.entity.player.Player player) {"),
                "Same-owner check should recognize the owning player");
        assertTrue(source.contains("if (target instanceof EntityShipBase shipTarget) {"),
                "Same-owner check should recognize other ships with the same owner");
        assertTrue(source.contains("if (target instanceof TamableAnimal t) {"),
                "Same-owner check should still recognize generic tameables");
        assertTrue(source.contains("if (target instanceof EntityMountBase mount) {"),
                "Same-owner check should recognize mounted entities linked to the same owner");
        assertTrue(source.contains("if (target instanceof EntityAircraftBase aircraft) {"),
                "Same-owner check should recognize aircraft launched by the same owner");
    }
}
