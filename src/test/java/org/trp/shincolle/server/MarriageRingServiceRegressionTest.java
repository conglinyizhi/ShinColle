package org.trp.shincolle.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MarriageRingServiceRegressionTest {
    private static final Path MARRIAGE_RING_SERVICE =
            Path.of("src/main/java/org/trp/shincolle/server/MarriageRingService.java");

    @Test
    void marriageRingServiceShouldGuardNullPlayersAtInventoryAndTickEntrypoints() throws IOException {
        String source = Files.readString(MARRIAGE_RING_SERVICE);

        assertTrue(source.contains("public static void applyTickAbilities(Player player) {\n        if (player == null || !hasActiveMarriageRing(player)) {"),
                "MarriageRingService tick abilities should reject null players before evaluating ring effects");
        assertTrue(source.contains("public static float getUnderwaterBreakSpeedMultiplier(Player player) {\n        if (player == null"),
                "MarriageRingService underwater dig rules should reject null players");
        assertTrue(source.contains("public static boolean shouldCancelFireDamage(Player player, net.minecraft.world.damagesource.DamageSource source) {\n        if (player == null"),
                "MarriageRingService fire-damage immunity should reject null players");
        assertTrue(source.contains("public static float getUnderwaterFogDistanceMultiplier(Player player) {\n        if (player == null"),
                "MarriageRingService underwater fog rules should reject null players");
        assertTrue(source.contains("public static boolean hasActiveMarriageRing(Player player) {\n        if (player == null) {\n            return false;\n        }"),
                "MarriageRingService should treat null players as having no active marriage ring");
        assertTrue(source.contains("public static ItemStack findActiveMarriageRing(Player player) {\n        if (player == null) {\n            return ItemStack.EMPTY;\n        }"),
                "MarriageRingService inventory scans should reject null players before touching inventory lists");
    }
}
