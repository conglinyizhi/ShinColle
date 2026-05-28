package org.trp.shincolle.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.trp.shincolle.Config;
import org.trp.shincolle.block.entity.WayPointBlockEntity;
import org.trp.shincolle.entity.EntityShipFishingHook;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.entity.base.ShipGuardTarget;
import org.trp.shincolle.entity.base.ShipTaskRuntime;
import org.trp.shincolle.init.ModItems;
import org.trp.shincolle.inventory.ShipInventoryHandler;
import org.trp.shincolle.menu.ShipContainerMenu;

import java.util.ArrayList;
import java.util.List;

public class TaskHelper {
    private static final int CRAFTING_WORK_START_SLOT = 12;
    private static final int CRAFTING_WORK_SLOT_COUNT = 9;
    private static final int HELD_MAINHAND_SLOT = 22;
    private static final int HELD_OFFHAND_SLOT = 23;
    private static final int[] HELD_ITEM_SLOTS = {HELD_MAINHAND_SLOT, HELD_OFFHAND_SLOT};
    private static final int LEGACY_GENERAL_WORLD_ID = 999999;
    private static final String LEGACY_GENERAL_BIOME_ID = "-999999";
    private static final String[] TASK_NAMES = {"none", "cooking", "fishing", "mining", "crafting"};
    
    private TaskHelper() {}

    public static void onUpdateTask(EntityShipBase host) {
        if (host.getIsSitting() || !host.isAlive() || host.isNoFuel()) {
            host.getTaskRuntime().clearTask();
            return;
        }
        int taskId = host.getStateMinor(ShipContainerMenu.STATE_MINOR_TASK_ID);
        ShipTaskRuntime runtime = host.getTaskRuntime();
        boolean tracing = PerformanceTrace.enabled();
        long start = tracing ? PerformanceTrace.now() : 0L;
        try {
            runtime.beginTaskTick(taskId);
            switch (taskId) {
                case 1:
                    if (isTaskEnabled(0)) onUpdateCooking(host, runtime);
                    break;
                case 2:
                    if (isTaskEnabled(1)) onUpdateFishing(host, runtime);
                    break;
                case 3:
                    if (isTaskEnabled(2)) onUpdateMining(host, runtime);
                    break;
                case 4:
                    if (isTaskEnabled(3)) onUpdateCrafting(host, runtime);
                    break;
                default:
                    runtime.clearTask();
                    break;
            }
        } finally {
            if (tracing) {
                long elapsed = PerformanceTrace.elapsed(start);
                PerformanceTrace.addTaskTime(elapsed);
                PerformanceTrace.logSlowTaskTick(host, taskName(taskId), taskId, elapsed);
            }
        }
    }

    private static boolean isTaskEnabled(int index) {
        return index >= 0 && index < Config.taskEnable.length && Config.taskEnable[index];
    }

    private static String taskName(int taskId) {
        return taskId >= 0 && taskId < TASK_NAMES.length ? TASK_NAMES[taskId] : "unknown";
    }

    public static void onUpdateCooking(EntityShipBase host) {
        if (host == null) return;
        onUpdateCooking(host, host.getTaskRuntime());
    }

    private static void onUpdateCooking(EntityShipBase host, ShipTaskRuntime runtime) {
        if (host == null || host.level().isClientSide) return;
        ItemStack mainStack = host.getHeldItemMainhandSlot();
        ItemStack offhandStack = host.getHeldItemOffhandSlot();
        if (mainStack.isEmpty()) return;

        Level level = host.level();
        ItemStack resultStack = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(mainStack), level)
                .map(recipe -> recipe.value().assemble(new SingleRecipeInput(mainStack), level.registryAccess()))
                .orElse(ItemStack.EMPTY);
        if (resultStack.isEmpty()) return;

        ShipGuardTarget guardTarget = host.getGuardTarget();
        if (!isWaypointGuardContext(guardTarget, level)) return;
        int gx = guardTarget.x();
        int gy = guardTarget.y();
        int gz = guardTarget.z();

        BlockPos wpPos = new BlockPos(gx, gy, gz);
        if (host.distanceToSqr(gx + 0.5, gy, gz + 0.5) > 25.0D) {
            runtime.moveToTaskPoint(new Vec3(gx + 0.5D, gy, gz + 0.5D), 1.0D);
            return;
        }

        if (level.getBlockEntity(wpPos) instanceof WayPointBlockEntity wpbe) {
            BlockPos chestPos = wpbe.getChestPos();
            if (chestPos.getY() <= 0) return;

            net.minecraft.world.level.block.entity.BlockEntity targetBE = level.getBlockEntity(chestPos);
            if (targetBE == null) return;
            int taskSide = host.getStateMinor(ShipContainerMenu.STATE_MINOR_TASK_SIDE);
            boolean checkMeta = (taskSide & (1 << 18)) != 0;
            boolean checkOre = (taskSide & (1 << 19)) != 0;
            boolean checkNbt = (taskSide & (1 << 20)) != 0;
            List<IItemHandler> inHandlers = InventoryHelper.getHandlersFromSide(level, chestPos, taskSide, 0);
            List<IItemHandler> outHandlers = InventoryHelper.getHandlersFromSide(level, chestPos, taskSide, 1);
            List<IItemHandler> fuelHandlers = InventoryHelper.getHandlersFromSide(level, chestPos, taskSide, 2);

            if (inHandlers.isEmpty() && fuelHandlers.isEmpty() && outHandlers.isEmpty()) {
                IItemHandler fallbackHandler;
                if (targetBE instanceof net.minecraft.world.Container container) {
                    fallbackHandler = new net.neoforged.neoforge.items.wrapper.InvWrapper(container);
                } else {
                    fallbackHandler = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, chestPos, null);
                }
                if (fallbackHandler == null || fallbackHandler.getSlots() < 3) return;
                inHandlers = List.of(singleSlotView(fallbackHandler, 0));
                fuelHandlers = List.of(singleSlotView(fallbackHandler, 1));
                outHandlers = List.of(singleSlotView(fallbackHandler, 2));
            }

            boolean swing = false;
            
            if (!mainStack.isEmpty()) {
                int canFit = 0;
                for (IItemHandler handler : inHandlers) {
                    ItemStack remainderSim = handler.insertItem(0, mainStack, true);
                    canFit += mainStack.getCount() - remainderSim.getCount();
                }

                if (canFit > 0) {
                    ItemStack material = InventoryHelper.getAndRemoveItem(host.getInventory(), mainStack, canFit, checkMeta, checkNbt, checkOre, HELD_ITEM_SLOTS);
                    if (!material.isEmpty()) {
                        ItemStack remaining = material;
                        for (IItemHandler handler : inHandlers) {
                            if (remaining.isEmpty()) break;
                            remaining = handler.insertItem(0, remaining, false);
                        }
                        if (remaining.getCount() < material.getCount()) {
                            swing = true;
                        }
                        if (!remaining.isEmpty()) {
                            InventoryHelper.moveItemstackToInv(host.getInventory(), remaining, null);
                        }
                    }
                }
            }

            offhandStack = host.getHeldItemOffhandSlot();
            if (!offhandStack.isEmpty() && !fuelHandlers.isEmpty()) {
                int canFit = 0;
                for (IItemHandler handler : fuelHandlers) {
                    ItemStack remainderSim = handler.insertItem(0, offhandStack, true);
                    canFit += offhandStack.getCount() - remainderSim.getCount();
                }

                if (canFit > 0) {
                    ItemStack fuel = InventoryHelper.getAndRemoveItem(host.getInventory(), offhandStack, canFit, checkMeta, checkNbt, checkOre, HELD_ITEM_SLOTS);
                    if (!fuel.isEmpty()) {
                        ItemStack remaining = fuel;
                        for (IItemHandler handler : fuelHandlers) {
                            if (remaining.isEmpty()) break;
                            remaining = handler.insertItem(0, remaining, false);
                        }
                        if (remaining.getCount() < fuel.getCount()) {
                            swing = true;
                        }
                        if (!remaining.isEmpty()) InventoryHelper.moveItemstackToInv(host.getInventory(), remaining, null);
                    }
                }
            }

            boolean tookOutput = false;
            for (IItemHandler handler : outHandlers) {
                for (int slot = 0; slot < handler.getSlots(); slot++) {
                    ItemStack inOutputSlot = handler.getStackInSlot(slot);
                    if (inOutputSlot.isEmpty()) {
                        continue;
                    }
                    if (InventoryHelper.matchTargetItem(inOutputSlot, resultStack, checkMeta, checkNbt, checkOre)) {
                        ItemStack taken = handler.extractItem(slot, 64, false);
                        if (!taken.isEmpty()) {
                            swing = true;
                            tookOutput = true;
                            InventoryHelper.moveItemstackToInv(host.getInventory(), taken, null);

                            host.addShipExp(Config.expGainTask[0]);
                            host.setFuel(host.getFuel() - Config.consumeGrudgeTask[0]);
                            host.addMorale(100);

                            float failChance = (Config.shipMaxLevelNormal - host.getLevel()) / (float) Config.shipMaxLevelNormal * 0.2F + 0.05F;
                            if (host.getRandom().nextFloat() < failChance) {
                                ItemEntity entity = new ItemEntity(level, chestPos.getX() + 0.5, chestPos.getY() + 1.0, chestPos.getZ() + 0.5, new ItemStack(Items.CHARCOAL));
                                level.addFreshEntity(entity);
                                host.applyParticleEmotion(6);
                            }
                            break;
                        }
                    }
                }
                if (tookOutput) {
                    break;
                }
            }

            if (swing) {
                host.startCustomSwing();
                if (host.getRandom().nextInt(5) == 0) {
                    host.applyParticleEmotion(host.getRandom().nextInt(5));
                }
            }
        }
    }

    public static void onUpdateFishing(EntityShipBase host) {
        if (host == null) return;
        onUpdateFishing(host, host.getTaskRuntime());
    }

    private static void onUpdateFishing(EntityShipBase host, ShipTaskRuntime runtime) {
        if (host == null) return;
        Level level = host.level();
        ItemStack rod = host.getHeldItemMainhandSlot();
        if (rod.isEmpty() || rod.getItem() != Items.FISHING_ROD) return;

        ShipGuardTarget guardTarget = host.getGuardTarget();
        if (!isWaypointGuardContext(guardTarget, level)) return;
        int gx = guardTarget.x();
        int gy = guardTarget.y();
        int gz = guardTarget.z();

        if (host.distanceToSqr(gx + 0.5, gy, gz + 0.5) > 10.0D) {
            runtime.moveToTaskPoint(new Vec3(gx + 0.5D, gy, gz + 0.5D), 1.0D);
            return;
        }

        if (Math.abs(host.getDeltaMovement().x) > 0.1D || Math.abs(host.getDeltaMovement().z) > 0.1D || host.getDeltaMovement().y > 0.1D) return;


        long stageStart = PerformanceTrace.enabled() ? PerformanceTrace.now() : 0L;
        BlockPos waterPos = findNearbyFishingWater(host, 5, 3);
        if (PerformanceTrace.enabled()) {
            PerformanceTrace.logTaskStage(host, "fishing", "findWater", PerformanceTrace.elapsed(stageStart),
                    "found=" + (waterPos != null) + " radius=5 depth=3");
        }
        if (waterPos == null) return;

        if (host.getFishHook() == null || host.getFishHook().isRemoved()) {
            host.startCustomSwing();
            if (!level.isClientSide) {
                EntityShipFishingHook hook = new EntityShipFishingHook(level, host);
                hook.setPos(waterPos.getX() + 0.1D + host.getRandom().nextDouble() * 0.8D,
                        waterPos.getY() + 1.0D,
                        waterPos.getZ() + 0.1D + host.getRandom().nextDouble() * 0.8D);
                level.addFreshEntity(hook);
                host.applyParticleEmotion(host.getRandom().nextInt(4) + 1);
            }
            return;
        }

        if (level instanceof ServerLevel serverLevel) {
            if (host.getFishHook().tickCount > Config.tickFishingMin + host.getRandom().nextInt(Config.tickFishingMax)) {
                host.startCustomSwing();
                stageStart = PerformanceTrace.enabled() ? PerformanceTrace.now() : 0L;
                LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(net.minecraft.world.level.storage.loot.BuiltInLootTables.FISHING);
                LootParams params = (new LootParams.Builder(serverLevel))
                        .withParameter(LootContextParams.ORIGIN, host.position())
                        .withParameter(LootContextParams.TOOL, rod)
                        .withParameter(LootContextParams.THIS_ENTITY, host)
                        .withLuck(getFishingLuck(host, serverLevel))
                        .create(LootContextParamSets.FISHING);
                
                List<ItemStack> items = lootTable.getRandomItems(params);
                if (PerformanceTrace.enabled()) {
                    PerformanceTrace.logTaskStage(host, "fishing", "lootRoll", PerformanceTrace.elapsed(stageStart),
                            "items=" + items.size() + " hookTicks=" + host.getFishHook().tickCount);
                }
                for (ItemStack stack : items) {
                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(host.getInventory(), stack, false);
                    if (!remainder.isEmpty()) {
                        ItemEntity entity = new ItemEntity(level, host.getX(), host.getY(), host.getZ(), remainder);
                        level.addFreshEntity(entity);
                    }
                }
                
                host.getFishHook().discard();
                host.addShipExp(Config.expGainTask[1]);
                host.setFuel(host.getFuel() - Config.consumeGrudgeTask[1]);
                host.addMorale(300);
                host.applyParticleEmotion(host.getRandom().nextInt(5));
            } else if (host.getFishHook().tickCount > Config.tickFishingMin + Config.tickFishingMax + 20) {
                host.getFishHook().discard();
            }
        }
    }

    public static void onUpdateMining(EntityShipBase host) {
        if (host == null) return;
        onUpdateMining(host, host.getTaskRuntime());
    }

    private static void onUpdateMining(EntityShipBase host, ShipTaskRuntime runtime) {
        if (host == null) return;
        Level level = host.level();
        ItemStack pickaxe = host.getHeldItemMainhandSlot();
        if (pickaxe.isEmpty() || !pickaxe.is(net.minecraft.tags.ItemTags.PICKAXES)) return;

        if (Math.abs(host.getDeltaMovement().x) > 0.1D || Math.abs(host.getDeltaMovement().z) > 0.1D || host.getDeltaMovement().y > 0.1D) return;

        if ((host.tickCount & 63) == 0) {
            runtime.moveTo(new Vec3(
                    host.getX() + host.getRandom().nextInt(9) - 4.0D,
                    host.getY() + host.getRandom().nextInt(5) - 2.0D,
                    host.getZ() + host.getRandom().nextInt(9) - 4.0D
            ), 1.0D);
            return;
        }

        if (host.getRandom().nextInt(5) > 2) {
            host.startCustomSwing();
            if (!level.isClientSide && host.getRandom().nextInt(10) > 8) {
                host.applyParticleEmotion(host.getRandom().nextInt(5));
            }
        }

        if (!level.isClientSide && (host.tickCount & 31) == 0 && host.tickCount - host.getStateTimer(15) > Config.tickMiningMin + host.getRandom().nextInt(Config.tickMiningMax)) {
            int xl = (int) host.getX();
            int yl = (int) host.getY();
            int zl = (int) host.getZ();
            int stoneCount = 0;
            boolean canMine = false;
            BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos();
            long scanStart = PerformanceTrace.enabled() ? PerformanceTrace.now() : 0L;
            for (int dy = -3; dy < 5 && !canMine; ++dy) {
                for (int dx = -3; dx < 4 && !canMine; ++dx) {
                    for (int dz = -3; dz < 4; ++dz) {
                        mutPos.set(xl + dx, yl + dy, zl + dz);
                        if (level.getBlockState(mutPos).is(BlockTags.BASE_STONE_OVERWORLD) || level.getBlockState(mutPos).is(BlockTags.BASE_STONE_NETHER)) {
                            if (++stoneCount > 120) {
                                canMine = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (PerformanceTrace.enabled()) {
                PerformanceTrace.logTaskStage(host, "mining", "scanStone", PerformanceTrace.elapsed(scanStart),
                        "stoneCount=" + stoneCount + " canMine=" + canMine + " center=" + xl + "," + yl + "," + zl);
            }
            if (canMine) {
                long rollStart = PerformanceTrace.enabled() ? PerformanceTrace.now() : 0L;
                MiningDropResult drop = rollMiningDrop(host);
                if (PerformanceTrace.enabled()) {
                    PerformanceTrace.logTaskStage(host, "mining", "rollDrop", PerformanceTrace.elapsed(rollStart),
                            "candidates=" + drop.candidates + " totalWeight=" + drop.totalWeight
                                    + " fallback=" + drop.fallback + " result=" + drop.stack);
                }
                ItemStack result = drop.stack;
                ItemHandlerHelper.insertItemStacked(host.getInventory(), result, false);
                
                host.addShipExp(Config.expGainTask[2]);
                host.setFuel(host.getFuel() - Config.consumeGrudgeTask[2]);
                host.addMorale(-200);
                host.applyParticleEmotion(host.getRandom().nextInt(5));
                host.startCustomSwing();
                host.setStateTimer(15, host.tickCount);
            }
        }
    }

    private static MiningDropResult rollMiningDrop(EntityShipBase host) {
        if (Config.miningEntries.isEmpty()) {
            return MiningDropResult.createFallback();
        }

        Level level = host.level();
        int shipLevel = host.getLevel();
        int y = host.blockPosition().getY();
        int toolLevel = getToolLevel(host.getHeldItemMainhandSlot());
        String biomePath = level.getBiome(host.blockPosition())
                .unwrapKey()
                .map(key -> key.location().toString())
                .orElse("*");
        int dimensionId = getLegacyDimensionId(level);

        List<Config.MiningEntry> candidates = new ArrayList<>();
        int totalWeight = 0;
        for (Config.MiningEntry entry : Config.miningEntries) {
            if (!matchesDimension(entry, dimensionId, level)) continue;
            if (!matchesBiome(entry, biomePath)) continue;
            if (shipLevel < entry.minShipLevel()) continue;
            if (y > entry.maxY()) continue;
            if (toolLevel < entry.minToolLevel()) continue;
            candidates.add(entry);
            totalWeight += entry.weight();
        }

        if (candidates.isEmpty() || totalWeight <= 0) {
            return MiningDropResult.createFallback(candidates.size(), totalWeight);
        }

        int roll = host.getRandom().nextInt(totalWeight);
        Config.MiningEntry chosen = candidates.get(0);
        for (Config.MiningEntry entry : candidates) {
            roll -= entry.weight();
            if (roll < 0) {
                chosen = entry;
                break;
            }
        }

        int amount = chosen.min();
        if (chosen.max() > chosen.min()) {
            amount += host.getRandom().nextInt(chosen.max() - chosen.min() + 1);
        }

        int fortuneLevel = getFortuneLevel(host.getHeldItemMainhandSlot(), level);
        int luckLevel = getLuckLevel(host);
        float scaledAmount = amount * (1.0F + chosen.enchantFactor() * (fortuneLevel + luckLevel));
        int finalAmount = Math.max(1, Math.round(scaledAmount));
        return new MiningDropResult(new ItemStack(chosen.item(), finalAmount), candidates.size(), totalWeight, false);
    }

    private record MiningDropResult(ItemStack stack, int candidates, int totalWeight, boolean fallback) {
        private static MiningDropResult createFallback() {
            return createFallback(0, 0);
        }

        private static MiningDropResult createFallback(int candidates, int totalWeight) {
            return new MiningDropResult(new ItemStack(Items.COBBLESTONE), candidates, totalWeight, true);
        }
    }

    private static boolean matchesDimension(Config.MiningEntry entry, int dimensionId, Level level) {
        if ("*".equals(entry.dimensionPath()) || entry.dimensionId() == LEGACY_GENERAL_WORLD_ID) return true;
        if (entry.dimensionId() != Integer.MIN_VALUE) {
            return entry.dimensionId() == dimensionId;
        }
        return level.dimension().location().toString().equals(entry.dimensionPath());
    }

    private static boolean matchesBiome(Config.MiningEntry entry, String biomePath) {
        return "*".equals(entry.biomePath())
                || LEGACY_GENERAL_BIOME_ID.equals(entry.biomePath())
                || biomePath.equals(entry.biomePath());
    }

    private static int getLegacyDimensionId(Level level) {
        String key = level.dimension().location().toString();
        return switch (key) {
            case "minecraft:overworld" -> 0;
            case "minecraft:the_nether" -> -1;
            case "minecraft:the_end" -> 1;
            default -> Integer.MIN_VALUE;
        };
    }

    private static int getToolLevel(ItemStack pickaxe) {
        if (pickaxe.isEmpty()) return 0;
        if (pickaxe.is(Items.NETHERITE_PICKAXE) || pickaxe.is(Items.DIAMOND_PICKAXE)) return 3;
        if (pickaxe.is(Items.IRON_PICKAXE)) return 2;
        if (pickaxe.is(Items.STONE_PICKAXE)) return 1;
        return 0;
    }

    private static int getFortuneLevel(ItemStack pickaxe, Level level) {
        if (pickaxe.isEmpty()) {
            return 0;
        }

        Holder<Enchantment> fortune = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.FORTUNE);
        return pickaxe.getEnchantmentLevel(fortune);
    }

    private static int getLuckLevel(EntityShipBase host) {
        MobEffectInstance effect = host.getEffect(MobEffects.LUCK);
        return effect == null ? 0 : effect.getAmplifier() + 1;
    }

    private static float getFishingLuck(EntityShipBase host, Level level) {
        return Math.max(
                getLuckOfTheSeaLevel(host.getHeldItemMainhandSlot(), level),
                getLuckOfTheSeaLevel(host.getHeldItemOffhandSlot(), level))
                + getLuckLevel(host)
                + host.getLevel() / (float) Config.shipMaxLevelNormal * 1.5F;
    }

    private static int getLuckOfTheSeaLevel(ItemStack stack, Level level) {
        if (stack.isEmpty()) {
            return 0;
        }

        Holder<Enchantment> luckOfTheSea = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.LUCK_OF_THE_SEA);
        return stack.getEnchantmentLevel(luckOfTheSea);
    }

    private static BlockPos findNearbyFishingWater(EntityShipBase host, int radius, int depth) {
        Level level = host.level();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dy = 1; dy > -3; dy--) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx == 0 && dz == 0) {
                        continue;
                    }
                    pos.set(host.getX() + dx, host.getY() + dy, host.getZ() + dz);
                    if (isWaterWithDepth(level, pos, depth) && level.getBlockState(pos.above()).isAir()) {
                        return pos.immutable();
                    }
                }
            }
        }
        return null;
    }

    private static boolean isWaterWithDepth(Level level, BlockPos pos, int depth) {
        if (!level.getFluidState(pos).is(FluidTags.WATER)) {
            return false;
        }
        for (int dy = 1; dy < depth; dy++) {
            if (!level.getFluidState(pos.below(dy)).is(FluidTags.WATER)) {
                return false;
            }
        }
        return true;
    }

    public static void onUpdateCrafting(EntityShipBase host) {
        if (host == null) return;
        onUpdateCrafting(host, host.getTaskRuntime());
    }

    private static void onUpdateCrafting(EntityShipBase host, ShipTaskRuntime runtime) {
        if (host == null || host.level().isClientSide) return;

        
        ShipInventoryHandler inv = host.getInventory();
        ItemStack recipePaper = host.getHeldItemMainhandSlot();
        if (recipePaper.isEmpty() || !recipePaper.is(ModItems.RECIPE_PAPER.get())) return;

        Level level = host.level();
        ItemStack[] recipeGrid = RecipePaperData.loadRecipeGrid(recipePaper, level.registryAccess());
        List<ItemStack> recipeSlots = new ArrayList<>(9);
        List<ItemStack> materials = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = recipeGrid[i];
            recipeSlots.add(stack);
            if (!stack.isEmpty()) {
                materials.add(stack);
            }
        }

        if (!RecipePaperData.hasAnyRecipeIngredient(recipeGrid)) return;

        CraftingInput recipeInput = CraftingInput.of(3, 3, recipeSlots);
        var recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, recipeInput, level);
        if (recipe.isEmpty()) return;

        ItemStack resultTemplate = recipe.get().value().assemble(recipeInput, level.registryAccess());
        if (resultTemplate.isEmpty()) return;

        
        List<ItemStack> uniqueMaterials = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        int taskSide = host.getStateMinor(ShipContainerMenu.STATE_MINOR_TASK_SIDE);
        boolean checkMeta = (taskSide & (1 << 18)) != 0;
        boolean checkOre = (taskSide & (1 << 19)) != 0;
        boolean checkNbt = (taskSide & (1 << 20)) != 0;

        for (ItemStack m : materials) {
            boolean found = false;
            for (int i = 0; i < uniqueMaterials.size(); i++) {
                if (InventoryHelper.matchTargetItem(uniqueMaterials.get(i), m, checkMeta, checkNbt, checkOre)) {
                    counts.set(i, counts.get(i) + 1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                uniqueMaterials.add(m.copy());
                counts.add(1);
            }
        }

        ShipGuardTarget guardTarget = host.getGuardTarget();
        if (!isWaypointGuardContext(guardTarget, level)) return;
        int gx = guardTarget.x();
        int gy = guardTarget.y();
        int gz = guardTarget.z();

        if (host.distanceToSqr(gx + 0.5, gy, gz + 0.5) > 25.0D) {
            runtime.moveToTaskPoint(new Vec3(gx + 0.5D, gy, gz + 0.5D), 1.0D);
            return;
        }

        BlockPos wpPos = new BlockPos(gx, gy, gz);
        if (!(level.getBlockEntity(wpPos) instanceof WayPointBlockEntity wpbe)) return;
        BlockPos chestPos = wpbe.getChestPos();
        if (chestPos.getY() <= 0) return;

        
        List<IItemHandler> inHandlers = InventoryHelper.getHandlersFromSide(level, chestPos, taskSide, 0); 
        List<IItemHandler> outHandlers = InventoryHelper.getHandlersFromSide(level, chestPos, taskSide, 1);
        if (inHandlers.isEmpty() && outHandlers.isEmpty()) {
            IItemHandler fallbackHandler = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, chestPos, null);
            if (fallbackHandler == null) {
                net.minecraft.world.level.block.entity.BlockEntity targetBE = level.getBlockEntity(chestPos);
                if (targetBE instanceof net.minecraft.world.Container container) {
                    fallbackHandler = new net.neoforged.neoforge.items.wrapper.InvWrapper(container);
                }
            }
            if (fallbackHandler == null) {
                return;
            }
            inHandlers = List.of(fallbackHandler);
            outHandlers = List.of(fallbackHandler);
        }
        int maxCraft = host.getLevel() / 20 + 1;
        int craftedCount = 0;
        long craftLoopStart = PerformanceTrace.enabled() ? PerformanceTrace.now() : 0L;

        for (int craftIndex = 0; craftIndex < maxCraft; craftIndex++) {
            List<ItemStack> workingRecipeSlots = new ArrayList<>(CRAFTING_WORK_SLOT_COUNT);
            for (int slot = 0; slot < CRAFTING_WORK_SLOT_COUNT; slot++) {
                ItemStack template = recipeSlots.get(slot);
                if (template.isEmpty()) {
                    workingRecipeSlots.add(ItemStack.EMPTY);
                    continue;
                }

                ItemStack workStack = inv.getStackInSlot(CRAFTING_WORK_START_SLOT + slot);
                if (workStack.isEmpty()) {
                    workStack = pullCraftingIngredient(inv, inHandlers, template, checkMeta, checkNbt, checkOre);
                    if (!workStack.isEmpty()) {
                        inv.setStackInSlot(CRAFTING_WORK_START_SLOT + slot, workStack);
                    }
                }

                if (!InventoryHelper.matchTargetItem(workStack, template, checkMeta, checkNbt, checkOre)) {
                    return;
                }

                workingRecipeSlots.add(workStack);
            }

            CraftingInput craftInput = CraftingInput.of(3, 3, workingRecipeSlots);
            var currentRecipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftInput, level);
            if (currentRecipe.isEmpty()) {
                break;
            }

            ItemStack resultPreview = currentRecipe.get().value().assemble(craftInput, level.registryAccess());
            if (resultPreview.isEmpty()
                    || !InventoryHelper.matchTargetItem(resultPreview, resultTemplate, checkMeta, checkNbt, checkOre)) {
                break;
            }

            for (int slot = 0; slot < CRAFTING_WORK_SLOT_COUNT; slot++) {
                ItemStack slotStack = recipeSlots.get(slot);
                if (slotStack.isEmpty()) {
                    continue;
                }

                int workSlot = CRAFTING_WORK_START_SLOT + slot;
                ItemStack consumeStack = inv.getStackInSlot(workSlot);
                if (consumeStack.isEmpty()) {
                    craftIndex = maxCraft;
                    break;
                }

                consumeStack.shrink(1);
                if (consumeStack.isEmpty()) {
                    inv.setStackInSlot(workSlot, ItemStack.EMPTY);
                } else {
                    inv.setStackInSlot(workSlot, consumeStack);
                }
            }

            if (craftIndex >= maxCraft) {
                break;
            }

            ItemStack finalResult = currentRecipe.get().value().assemble(craftInput, level.registryAccess());
            for (IItemHandler h : outHandlers) {
                finalResult = ItemHandlerHelper.insertItemStacked(h, finalResult, false);
                if (finalResult.isEmpty()) break;
            }
            if (!finalResult.isEmpty()) {
                ItemStack remainder = finalResult.copy();
                InventoryHelper.moveItemstackToInv(inv, remainder, null);
                finalResult = remainder;
            }
            if (!finalResult.isEmpty()) {
                level.addFreshEntity(new ItemEntity(level, chestPos.getX() + 0.5, chestPos.getY() + 1.0, chestPos.getZ() + 0.5, finalResult));
            }

            for (ItemStack remainStack : currentRecipe.get().value().getRemainingItems(craftInput)) {
                if (remainStack.isEmpty()) {
                    continue;
                }
                ItemStack remaining = remainStack.copy();
                if (InventoryHelper.moveItemstackToInv(inv, remaining, craftWorkingSlots())) {
                    continue;
                }
                for (IItemHandler h : outHandlers) {
                    remaining = ItemHandlerHelper.insertItemStacked(h, remaining, false);
                    if (remaining.isEmpty()) break;
                }
                if (!remaining.isEmpty()) {
                    InventoryHelper.moveItemstackToInv(inv, remaining, null);
                }
                if (!remaining.isEmpty()) {
                    level.addFreshEntity(new ItemEntity(level, chestPos.getX() + 0.5, chestPos.getY() + 1.0, chestPos.getZ() + 0.5, remaining));
                }
            }

            craftedCount++;
        }
        if (PerformanceTrace.enabled()) {
            PerformanceTrace.logTaskStage(host, "crafting", "craftLoop", PerformanceTrace.elapsed(craftLoopStart),
                    "crafted=" + craftedCount + " maxCraft=" + maxCraft + " recipeResult=" + resultTemplate);
        }

        if (craftedCount > 0) {
            host.startCustomSwing();
            host.addShipExp(Config.expGainTask[3]);
            host.setFuel(host.getFuel() - Config.consumeGrudgeTask[3]);
            host.addMorale(-10);
            if (host.getRandom().nextInt(5) == 0) {
                host.applyParticleEmotion(host.getRandom().nextInt(5));
            }
        }
    }

    private static boolean isWaypointGuardContext(ShipGuardTarget guardTarget, Level level) {
        if (!guardTarget.isBlock()) {
            return false;
        }

        int guardedDimension = guardTarget.dimensionId();
        int currentDimension = getLegacyDimensionId(level);
        return guardedDimension == currentDimension || guardedDimension == Integer.MIN_VALUE;
    }

    private static int[] craftWorkingSlots() {
        int[] slots = new int[CRAFTING_WORK_SLOT_COUNT];
        for (int i = 0; i < CRAFTING_WORK_SLOT_COUNT; i++) {
            slots[i] = CRAFTING_WORK_START_SLOT + i;
        }
        return slots;
    }

    private static ItemStack pullCraftingIngredient(IItemHandler shipInv, List<IItemHandler> inHandlers, ItemStack template,
                                                    boolean checkMeta, boolean checkNbt, boolean checkOre) {
        ItemStack existing = InventoryHelper.getAndRemoveItem(
                shipInv, template, 1, checkMeta, checkNbt, checkOre, craftWorkingSlots());
        if (!existing.isEmpty()) {
            return existing;
        }

        for (IItemHandler handler : inHandlers) {
            ItemStack pulled = InventoryHelper.getAndRemoveItem(handler, template, 1, checkMeta, checkNbt, checkOre, null);
            if (!pulled.isEmpty()) {
                return pulled;
            }
        }

        return ItemStack.EMPTY;
    }

    private static IItemHandler singleSlotView(IItemHandler handler, int slot) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return 1;
            }

            @Override
            public ItemStack getStackInSlot(int viewSlot) {
                return viewSlot == 0 ? handler.getStackInSlot(slot) : ItemStack.EMPTY;
            }

            @Override
            public ItemStack insertItem(int viewSlot, ItemStack stack, boolean simulate) {
                if (viewSlot != 0) {
                    return stack;
                }
                return handler.insertItem(slot, stack, simulate);
            }

            @Override
            public ItemStack extractItem(int viewSlot, int amount, boolean simulate) {
                if (viewSlot != 0) {
                    return ItemStack.EMPTY;
                }
                return handler.extractItem(slot, amount, simulate);
            }

            @Override
            public int getSlotLimit(int viewSlot) {
                return viewSlot == 0 ? handler.getSlotLimit(slot) : 0;
            }

            @Override
            public boolean isItemValid(int viewSlot, ItemStack stack) {
                return viewSlot == 0 && handler.isItemValid(slot, stack);
            }
        };
    }
}
