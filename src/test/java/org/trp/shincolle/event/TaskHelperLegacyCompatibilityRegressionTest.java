package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskHelperLegacyCompatibilityRegressionTest {
    private static final Path TASK_HELPER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/utility/TaskHelper.java");
    private static final Path CONFIG_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/Config.java");

    @Test
    void taskDispatcherShouldPreserveFuelAndEnableGates() throws IOException {
        String taskHelper = Files.readString(TASK_HELPER_SOURCE);
        String config = Files.readString(CONFIG_SOURCE);

        assertTrue(taskHelper.contains("if (host.getIsSitting() || !host.isAlive() || host.isNoFuel())"),
                "Task updates should stop immediately when the ship has no fuel");
        assertTrue(taskHelper.contains("if (isTaskEnabled(0)) onUpdateCooking(host, runtime);"),
                "Cooking should honor the legacy task enable config");
        assertTrue(taskHelper.contains("if (isTaskEnabled(1)) onUpdateFishing(host, runtime);"),
                "Fishing should honor the legacy task enable config");
        assertTrue(taskHelper.contains("if (isTaskEnabled(2)) onUpdateMining(host, runtime);"),
                "Mining should honor the legacy task enable config");
        assertTrue(taskHelper.contains("if (isTaskEnabled(3)) onUpdateCrafting(host, runtime);"),
                "Crafting should honor the legacy task enable config");
        assertTrue(config.contains("public static boolean[] taskEnable = {true, true, true, true};"),
                "Config should expose legacy per-task enable switches");
    }

    @Test
    void fishingShouldRequireLegacyWaterDepthAndRewardMorale() throws IOException {
        String source = Files.readString(TASK_HELPER_SOURCE);

        assertTrue(source.contains("BlockPos waterPos = findNearbyFishingWater(host, 5, 3);"),
                "Fishing should search for the legacy 5-block radius and 3-block water depth");
        assertTrue(source.contains("private static boolean isWaterWithDepth(Level level, BlockPos pos, int depth)"),
                "Fishing should have an explicit water-depth check");
        assertTrue(source.contains("level.getFluidState(pos.below(dy)).is(FluidTags.WATER)"),
                "Fishing depth check should verify water below the surface block");
        assertTrue(source.contains(".withLuck(getFishingLuck(host, serverLevel))"),
                "Fishing loot should keep the old luck modifiers instead of using unmodified vanilla loot");
        assertTrue(source.contains("getLuckOfTheSeaLevel(host.getHeldItemMainhandSlot(), level)"),
                "Fishing luck should include Luck of the Sea from the mainhand rod");
        assertTrue(source.contains("getLuckOfTheSeaLevel(host.getHeldItemOffhandSlot(), level)"),
                "Fishing luck should include Luck of the Sea from the offhand rod");
        assertTrue(source.contains("+ getLuckLevel(host)\n                + host.getLevel() / (float) Config.shipMaxLevelNormal * 1.5F"),
                "Fishing luck should include potion luck and ship-level scaling like the old task");
        assertTrue(source.contains("host.addMorale(300);"),
                "Fishing should keep the old morale reward");
    }

    @Test
    void miningShouldAcceptLegacyWildcardsAndLuckScaling() throws IOException {
        String taskHelper = Files.readString(TASK_HELPER_SOURCE);
        String config = Files.readString(CONFIG_SOURCE);

        assertTrue(config.contains("case \"999999\", \"*\" -> 999999;"),
                "Mining config parser should preserve legacy all-world wildcard id");
        assertTrue(config.contains("case \"shincolle:abyssmetal\" -> itemMeta == 1 ? \"shincolle:abyss_polymetal\" : \"shincolle:abyss_metal\";"),
                "Mining config parser should preserve legacy AbyssMetal meta semantics");
        assertTrue(config.contains("case \"minecraft:stone\" -> switch (itemMeta)"),
                "Mining config parser should translate legacy stone metadata variants");
        assertTrue(config.contains("case \"minecraft:dye\" -> itemMeta == 4 ? \"minecraft:lapis_lazuli\" : id;"),
                "Mining config parser should translate legacy dye metadata for lapis");
        assertTrue(taskHelper.contains("entry.dimensionId() == LEGACY_GENERAL_WORLD_ID"),
                "Mining dimension matching should treat legacy 999999 as all worlds");
        assertTrue(taskHelper.contains("LEGACY_GENERAL_BIOME_ID.equals(entry.biomePath())"),
                "Mining biome matching should treat legacy -999999 as all biomes");
        assertTrue(taskHelper.contains("int luckLevel = getLuckLevel(host);"),
                "Mining output scaling should include luck potion level");
        assertTrue(taskHelper.contains("chosen.enchantFactor() * (fortuneLevel + luckLevel)"),
                "Mining enchant factor should scale with fortune plus luck like the old task");
        assertTrue(taskHelper.contains("host.addMorale(-200);"),
                "Mining should keep the old morale penalty on successful mining");
    }

    @Test
    void cookingShouldExcludeHeldSlotsAndOnlyExtractSmeltingResult() throws IOException {
        String source = Files.readString(TASK_HELPER_SOURCE);

        assertTrue(source.contains("private static final int[] HELD_ITEM_SLOTS = {HELD_MAINHAND_SLOT, HELD_OFFHAND_SLOT};"),
                "Cooking should keep the legacy main/offhand exclusion slots");
        assertTrue(source.contains("InventoryHelper.getAndRemoveItem(host.getInventory(), mainStack, canFit, checkMeta, checkNbt, checkOre, HELD_ITEM_SLOTS)"),
                "Cooking should not pull input materials from the displayed main/offhand stacks");
        assertTrue(source.contains("InventoryHelper.getAndRemoveItem(host.getInventory(), offhandStack, canFit, checkMeta, checkNbt, checkOre, HELD_ITEM_SLOTS)"),
                "Cooking should not pull fuel from the displayed main/offhand stacks");
        assertTrue(source.contains("InventoryHelper.matchTargetItem(inOutputSlot, resultStack, checkMeta, checkNbt, checkOre)"),
                "Cooking output extraction should match the expected smelting result stack");
    }

    @Test
    void craftingShouldKeepLegacySingleCompletionCost() throws IOException {
        String source = Files.readString(TASK_HELPER_SOURCE);

        assertTrue(source.contains("host.addShipExp(Config.expGainTask[3]);"),
                "Crafting should add task exp once per update like the old task");
        assertTrue(source.contains("host.setFuel(host.getFuel() - Config.consumeGrudgeTask[3]);"),
                "Crafting should consume task fuel once per update like the old task");
        assertTrue(source.contains("host.addMorale(-10);"),
                "Crafting should apply the old single morale penalty");
    }
}
