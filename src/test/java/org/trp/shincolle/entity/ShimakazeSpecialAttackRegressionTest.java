package org.trp.shincolle.entity;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShimakazeSpecialAttackRegressionTest {
    private static final Path SHIMAKAZE =
            Path.of("src/main/java/org/trp/shincolle/entity/EntityDestroyerShimakaze.java");

    @Test
    void specialLightAttackShouldRejectSameOwnerTargetsBeforeAmmoSpend() throws IOException {
        String source = Files.readString(SHIMAKAZE);
        int aliveCheck = source.indexOf("if (target == null || !target.isAlive()) {");
        int sameOwnerCheck = source.indexOf("if (isSameOwnerAttackTarget(target)) {");
        int consumeIndex = source.indexOf("if (!consumeLightAmmo(4)) {");

        assertTrue(aliveCheck >= 0, "Shimakaze light special attack should still validate target liveness");
        assertTrue(sameOwnerCheck >= 0, "Shimakaze light special attack should reject same-owner targets");
        assertTrue(consumeIndex >= 0, "Shimakaze light special attack should still spend light ammo when allowed");
        assertTrue(aliveCheck < sameOwnerCheck && sameOwnerCheck < consumeIndex,
                "Shimakaze light special attack should reject dead or same-owner targets before spending ammo");
    }

    @Test
    void specialHeavyAttackShouldRejectSameOwnerTargetsBeforeLaunchingTorpedoes() throws IOException {
        String source = Files.readString(SHIMAKAZE);
        int nullCheck = source.indexOf("if (target == null) {");
        int aliveCheck = source.indexOf("if (!target.isAlive()) {");
        int sameOwnerCheck = source.indexOf("if (isSameOwnerAttackTarget(target)) {", source.indexOf("public boolean attackEntityWithHeavyAmmo(Entity target)"));
        int launchIndex = source.indexOf("return launchTorpedoSalvo(target.blockPosition(), target);");

        assertTrue(nullCheck >= 0, "Shimakaze heavy special attack should still validate null targets");
        assertTrue(aliveCheck >= 0, "Shimakaze heavy special attack should still validate target liveness");
        assertTrue(sameOwnerCheck >= 0, "Shimakaze heavy special attack should reject same-owner targets");
        assertTrue(launchIndex >= 0, "Shimakaze heavy special attack should still launch torpedoes for valid targets");
        assertTrue(nullCheck < aliveCheck && aliveCheck < sameOwnerCheck && sameOwnerCheck < launchIndex,
                "Shimakaze heavy special attack should reject invalid or same-owner targets before launching torpedoes");
    }
}
