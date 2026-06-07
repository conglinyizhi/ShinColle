package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskHelperLegacyCompatibilityRegressionTest {
    private static final Path TASK_HELPER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/utility/TaskHelper.kt");
    private static final Path CONFIG_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/Config.kt");

    @Test
    void taskDispatcherShouldPreserveFuelAndEnableGates() throws IOException {
        String taskHelper = Files.readString(TASK_HELPER_SOURCE);
        String config = Files.readString(CONFIG_SOURCE);

        assertTrue(taskHelper.contains("if (host.getIsSitting() || !host.isAlive() || host.isNoFuel())"),
                "Task updates should stop immediately when the ship has no fuel");
        assertTrue(taskHelper.contains("if (isTaskEnabled(0)) {\n                        onUpdateCooking(host, runtime);"),
                "Cooking should honor the legacy task enable config");
        assertTrue(taskHelper.contains("if (isTaskEnabled(1)) {\n                        onUpdateFishing(host, runtime);"),
                "Fishing should honor the legacy task enable config");
        assertTrue(taskHelper.contains("if (isTaskEnabled(2)) {\n                        onUpdateMining(host, runtime);"),
                "Mining should honor the legacy task enable config");
        assertTrue(taskHelper.contains("if (isTaskEnabled(3)) {\n                        onUpdateCrafting(host, runtime);"),
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
        assertTrue(!source.contains("level.getBlockState(pos.above()).isAir()"),
                "Fishing should not require an air block above the water, matching the legacy liquid search");
        assertTrue(source.contains(".withLuck(getFishingLuck(host, serverLevel))"),
                "Fishing loot should keep the old luck modifiers instead of using unmodified vanilla loot");
        assertTrue(source.contains("getLuckOfTheSeaLevel(host.getHeldItemMainhandSlot(), level)"),
                "Fishing luck should include Luck of the Sea from the mainhand rod");
        assertTrue(source.contains("getLuckOfTheSeaLevel(host.getHeldItemOffhandSlot(), level)"),
                "Fishing luck should include Luck of the Sea from the offhand rod");
        assertTrue(source.contains("+ getLuckLevel(host)\n                + host.getLevel() / (float) Config.shipMaxLevelNormal * 1.5F"),
                "Fishing luck should include potion luck and ship-level scaling like the old task");
        assertTrue(source.contains("host.getFishHook().tickCount > Config.tickFishingMin + Config.tickFishingMax)"),
                "Fishing hook timeout should clear at the legacy max wait threshold");
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
        assertTrue(config.contains("Item item = resolveConfigItem(parts[2], itemMeta, Items.AIR);"),
                "Mining config parser should reject invalid item ids instead of silently falling back to cobblestone");
        assertTrue(config.contains("if (item == Items.AIR) continue;"),
                "Mining config parser should skip unresolved mining entries");
        assertTrue(taskHelper.contains("entry.dimensionId() == LEGACY_GENERAL_WORLD_ID"),
                "Mining dimension matching should treat legacy 999999 as all worlds");
        assertTrue(config.contains("int biomeId = parseBiomeId(parts[1]);"),
                "Mining config parser should preserve legacy numeric biome ids for old config entries");
        assertTrue(config.contains("private static int parseBiomeId(String value) {"),
                "Mining config parser should normalize legacy biome wildcards and numeric biome ids");
        assertTrue(taskHelper.contains("if (!matchesBiome(entry, biomeId, biomePath)) continue;"),
                "Mining drop matching should evaluate both legacy biome ids and modern biome paths");
        assertTrue(taskHelper.contains("entry.biomeId() == biomeId"),
                "Mining biome matching should still accept legacy numeric biome ids");
        assertTrue(taskHelper.contains("LEGACY_GENERAL_BIOME_ID.equals(entry.biomePath())"),
                "Mining biome matching should treat legacy -999999 as all biomes");
        assertTrue(taskHelper.contains("if (pickaxe.getItem() instanceof TieredItem tieredItem) {"),
                "Mining tool level checks should derive from generic tool tiers instead of only hard-coded vanilla pickaxes");
        assertTrue(taskHelper.contains("Tier tier = tieredItem.getTier();"),
                "Mining tool levels should normalize the current tool tier before matching legacy harvest levels");
        assertTrue(taskHelper.contains("if (tier == Tiers.DIAMOND || tier == Tiers.NETHERITE) {"),
                "Mining tool levels should preserve the old diamond-tier harvest semantics across modern tiered pickaxes");
        assertTrue(taskHelper.contains("level.getBlockState(mutPos).is(BlockTags.MINEABLE_WITH_PICKAXE)"),
                "Mining should count the broader legacy-equivalent set of pickaxe-mineable stone blocks around the ship");
        assertTrue(taskHelper.contains("int luckLevel = getLuckLevel(host);"),
                "Mining output scaling should include luck potion level");
        assertTrue(taskHelper.contains("chosen.enchantFactor() * (fortuneLevel + luckLevel)"),
                "Mining enchant factor should scale with fortune plus luck like the old task");
        assertTrue(taskHelper.contains("createMiningResultStack(chosen, finalAmount, host)"),
                "Mining output creation should preserve legacy metadata handling instead of dropping raw base items");
        assertTrue(taskHelper.contains("if (item instanceof LegacyEquipItem legacyEquipItem) {"),
                "Mining output should recreate legacy equipment subtypes from config metadata");
        assertTrue(taskHelper.contains("if (item instanceof CombatRationItem combatRationItem) {"),
                "Mining output should recreate combat ration subtypes from legacy metadata");
        assertTrue(taskHelper.contains("if (item instanceof ShipTankItem shipTankItem) {"),
                "Mining output should recreate ship tank subtypes from legacy metadata");
        assertTrue(taskHelper.contains("if (item instanceof GrudgeItem grudgeItem) {"),
                "Mining output should recreate grudge subtypes from legacy metadata");
        assertTrue(taskHelper.contains("if (item instanceof AbyssNuggetItem abyssNuggetItem) {"),
                "Mining output should recreate abyss nugget subtypes from legacy metadata");
        assertTrue(taskHelper.contains("int variant = itemMeta <= 0\n                ? host.getRandom().nextInt(variantCount)\n                : Math.min(itemMeta, variantCount - 1);"),
                "Mining output should preserve the old meta<=0 random subtype semantics for legacy variant items");
        assertTrue(taskHelper.contains("return new MiningDropResult(ItemStack.EMPTY, candidates, totalWeight, true);"),
                "Mining should not force a cobblestone fallback when no legacy drop entry matches");
        assertTrue(taskHelper.contains("level.addFreshEntity(new ItemEntity(level, host.getX(), host.getY(), host.getZ(), remainder));"),
                "Mining overflow should drop beside the ship when its inventory is full");
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
        assertTrue(source.contains("ItemStack remainderSim = ItemHandlerHelper.insertItemStacked(host.getInventory(), inOutputSlot.copy(), true);"),
                "Cooking should only extract furnace output that can actually fit into the ship inventory");
        assertTrue(source.contains("int movable = inOutputSlot.getCount() - remainderSim.getCount();"),
                "Cooking should preserve the unmovable portion in the furnace output slot");
        assertTrue(source.contains("if (!(targetBE instanceof net.minecraft.world.WorldlyContainer)\n                        || fallbackHandler == null\n                        || fallbackHandler.getSlots() != 3) {"),
                "Cooking fallback should stay limited to furnace-like sided containers instead of any generic container");
        assertTrue(source.contains("canFit += simulateInsertAcrossHandler(handler, mainStack);"),
                "Cooking input capacity checks should respect every accessible slot on a sided handler");
        assertTrue(source.contains("canFit += simulateInsertAcrossHandler(handler, offhandStack);"),
                "Cooking fuel capacity checks should respect every accessible slot on a sided handler");
        assertTrue(source.contains("remaining = insertAcrossHandler(handler, remaining, false);"),
                "Cooking inserts should keep trying every accessible sided slot instead of only slot 0");
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
        assertTrue(source.contains("level.addFreshEntity(new ItemEntity(level, chestPos.getX() + 0.5, chestPos.getY() + 1.0, chestPos.getZ() + 0.5, finalResult));"),
                "Crafting result overflow should drop beside the paired container like the old task");
        assertTrue(source.contains("level.addFreshEntity(new ItemEntity(level, chestPos.getX() + 0.5, chestPos.getY() + 1.0, chestPos.getZ() + 0.5, remaining));"),
                "Crafting remainder overflow should drop beside the paired container like the old task");
    }
}
