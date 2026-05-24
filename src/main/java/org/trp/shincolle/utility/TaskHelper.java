package org.trp.shincolle.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.trp.shincolle.Config;
import org.trp.shincolle.block.entity.WayPointBlockEntity;
import org.trp.shincolle.entity.EntityShipFishingHook;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.init.ModItems;
import org.trp.shincolle.menu.ShipContainerMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskHelper {
    
    private TaskHelper() {}

    public static void onUpdateTask(EntityShipBase host) {
        if (host.getIsSitting() || !host.isAlive()) {
            return;
        }
        int taskId = host.getStateMinor(ShipContainerMenu.STATE_MINOR_TASK_ID);
        switch (taskId) {
            case 1: 
                onUpdateCooking(host);
                break;
            case 2: 
                onUpdateFishing(host);
                break;
            case 3: 
                onUpdateMining(host);
                break;
            case 4: 
                onUpdateCrafting(host);
                break;
            default:
                break;
        }
    }

    public static void onUpdateCooking(EntityShipBase host) {
        if (host == null || host.level().isClientSide) return;
        ItemStack mainStack = host.getHeldItemMainhandSlot();
        ItemStack offhandStack = host.getHeldItemOffhandSlot();
        if (mainStack.isEmpty()) return;

        final net.minecraft.world.item.Item originalMainItem = mainStack.getItem();
        final net.minecraft.world.item.Item originalOffhandItem = offhandStack.getItem();

        Level level = host.level();
        int gx = host.getGuardedPos(0);
        int gy = host.getGuardedPos(1);
        int gz = host.getGuardedPos(2);
        if (gy <= 0) return;
        if (!isWaypointGuardContext(host, level)) return;

        BlockPos wpPos = new BlockPos(gx, gy, gz);
        if (host.distanceToSqr(gx + 0.5, gy, gz + 0.5) > 25.0D) {
            host.getNavigation().moveTo(gx + 0.5D, gy, gz + 0.5D, 1.0D);
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
                var smeltingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(mainStack), level);
                if (smeltingRecipe.isPresent()) {
                    int canFit = 0;
                    for (IItemHandler handler : inHandlers) {
                    ItemStack remainderSim = handler.insertItem(0, mainStack, true);
                        canFit += mainStack.getCount() - remainderSim.getCount();
                    }

                    if (canFit > 0) {
                        ItemStack material = InventoryHelper.getAndRemoveItem(host.getInventory(), mainStack, canFit, checkMeta, checkNbt, checkOre, null);
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
            }

            offhandStack = host.getHeldItemOffhandSlot();
            if (!offhandStack.isEmpty() && !fuelHandlers.isEmpty()) {
                int canFit = 0;
                for (IItemHandler handler : fuelHandlers) {
                    ItemStack remainderSim = handler.insertItem(0, offhandStack, true);
                    canFit += offhandStack.getCount() - remainderSim.getCount();
                }

                if (canFit > 0) {
                    ItemStack fuel = InventoryHelper.getAndRemoveItem(host.getInventory(), offhandStack, canFit, checkMeta, checkNbt, checkOre, null);
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
                    if (!inOutputSlot.is(originalMainItem) && !inOutputSlot.is(originalOffhandItem)) {
                        boolean isPotentialMaterial = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(inOutputSlot), level).isPresent();
                        if (!isPotentialMaterial) {
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
        Level level = host.level();
        ItemStack rod = host.getHeldItemMainhandSlot();
        if (rod.isEmpty() || rod.getItem() != Items.FISHING_ROD) return;

        int gx = host.getGuardedPos(0);
        int gy = host.getGuardedPos(1);
        int gz = host.getGuardedPos(2);
        if (gy <= 0) return;
        if (!isWaypointGuardContext(host, level)) return;

        if (host.distanceToSqr(gx + 0.5, gy, gz + 0.5) > 10.0D) {
            host.getNavigation().moveTo(gx + 0.5D, gy, gz + 0.5D, 1.0D);
            return;
        }

        if (Math.abs(host.getDeltaMovement().x) > 0.1D || Math.abs(host.getDeltaMovement().z) > 0.1D || host.getDeltaMovement().y > 0.1D) return;


        BlockPos waterPos = null;
        boolean hasWater = false;
        BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos();
        for (int dy = 0; dy >= -3; dy--) {
            for (int dx = -5; dx <= 5; dx++) {
                for (int dz = -5; dz <= 5; dz++) {
                    mutPos.set(host.getX() + dx, host.getY() + dy, host.getZ() + dz);
                    
                    if (level.getFluidState(mutPos).is(net.minecraft.tags.FluidTags.WATER) &&
                            level.getBlockState(mutPos.above()).isAir()) {
                        hasWater = true;
                        waterPos = mutPos.immutable();
                        break;
                    }
                }
                if (hasWater) break;
            }
            if (hasWater) break;
        }
        if (!hasWater) return;

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
                LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(net.minecraft.world.level.storage.loot.BuiltInLootTables.FISHING);
                LootParams params = (new LootParams.Builder(serverLevel))
                        .withParameter(LootContextParams.ORIGIN, host.position())
                        .withParameter(LootContextParams.TOOL, rod)
                        .withParameter(LootContextParams.THIS_ENTITY, host)
                        .create(LootContextParamSets.FISHING);
                
                List<ItemStack> items = lootTable.getRandomItems(params);
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
                host.addMorale(50);
                host.applyParticleEmotion(host.getRandom().nextInt(5));
            } else if (host.getFishHook().tickCount > Config.tickFishingMin + Config.tickFishingMax + 20) {
                host.getFishHook().discard();
            }
        }
    }

    public static void onUpdateMining(EntityShipBase host) {
        if (host == null) return;
        Level level = host.level();
        ItemStack pickaxe = host.getHeldItemMainhandSlot();
        if (pickaxe.isEmpty() || !pickaxe.is(net.minecraft.tags.ItemTags.PICKAXES)) return;

        if (Math.abs(host.getDeltaMovement().x) > 0.1D || Math.abs(host.getDeltaMovement().z) > 0.1D || host.getDeltaMovement().y > 0.1D) return;

        if ((host.tickCount & 63) == 0) {
            host.getNavigation().moveTo(host.getX() + host.getRandom().nextInt(9) - 4.0D, host.getY() + host.getRandom().nextInt(5) - 2.0D, host.getZ() + host.getRandom().nextInt(9) - 4.0D, 1.0D);
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
            if (canMine) {
                ItemStack result = rollMiningDrop(host).orElseGet(() -> new ItemStack(Items.COBBLESTONE));
                ItemHandlerHelper.insertItemStacked(host.getInventory(), result, false);
                
                host.addShipExp(Config.expGainTask[2]);
                host.setFuel(host.getFuel() - Config.consumeGrudgeTask[2]);
                host.addMorale(100);
                host.applyParticleEmotion(host.getRandom().nextInt(5));
                host.startCustomSwing();
                host.setStateTimer(15, host.tickCount);
            }
        }
    }

    private static Optional<ItemStack> rollMiningDrop(EntityShipBase host) {
        if (Config.miningEntries.isEmpty()) {
            return Optional.empty();
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
            return Optional.empty();
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
        float scaledAmount = amount * (1.0F + chosen.enchantFactor() * fortuneLevel);
        int finalAmount = Math.max(1, Math.round(scaledAmount));
        return Optional.of(new ItemStack(chosen.item(), finalAmount));
    }

    private static boolean matchesDimension(Config.MiningEntry entry, int dimensionId, Level level) {
        if ("*".equals(entry.dimensionPath())) return true;
        if (entry.dimensionId() != Integer.MIN_VALUE) {
            return entry.dimensionId() == dimensionId;
        }
        return level.dimension().location().toString().equals(entry.dimensionPath());
    }

    private static boolean matchesBiome(Config.MiningEntry entry, String biomePath) {
        return "*".equals(entry.biomePath()) || biomePath.equals(entry.biomePath());
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

    public static void onUpdateCrafting(EntityShipBase host) {
        if (host == null || host.level().isClientSide) return;

        
        IItemHandler inv = host.getInventory();
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

        int gx = host.getGuardedPos(0);
        int gy = host.getGuardedPos(1);
        int gz = host.getGuardedPos(2);
        if (gy <= 0) return;
        if (!isWaypointGuardContext(host, level)) return;

        if (host.distanceToSqr(gx + 0.5, gy, gz + 0.5) > 25.0D) {
            host.getNavigation().moveTo(gx + 0.5D, gy, gz + 0.5D, 1.0D);
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

        for (int i = 0; i < uniqueMaterials.size(); i++) {
            ItemStack temp = uniqueMaterials.get(i);
            int needed = counts.get(i) * maxCraft;
            int has = InventoryHelper.calcItemStackAmount(inv, temp, checkMeta, checkNbt, checkOre);
            
            if (has < needed) {
                int pullCount = needed - has;
                for (IItemHandler h : inHandlers) {
                    ItemStack pulled = InventoryHelper.getAndRemoveItem(h, temp, pullCount, checkMeta, checkNbt, checkOre, null);
                    if (!pulled.isEmpty()) {
                        InventoryHelper.moveItemstackToInv(inv, pulled, null);
                        pullCount -= pulled.getCount();
                        if (pullCount <= 0) break;
                    }
                }
            }
            
            if (InventoryHelper.calcItemStackAmount(inv, temp, checkMeta, checkNbt, checkOre) < needed) {
                return; 
            }
        }

        for (int craftIndex = 0; craftIndex < maxCraft; craftIndex++) {
            CraftingInput craftInput = CraftingInput.of(3, 3, recipeSlots);
            var currentRecipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftInput, level);
            if (currentRecipe.isEmpty()) {
                break;
            }

            ItemStack resultPreview = currentRecipe.get().value().assemble(craftInput, level.registryAccess());
            if (resultPreview.isEmpty()
                    || !InventoryHelper.matchTargetItem(resultPreview, resultTemplate, checkMeta, checkNbt, checkOre)) {
                break;
            }

            for (ItemStack slotStack : recipeSlots) {
                if (!slotStack.isEmpty()) {
                    if (InventoryHelper.calcItemStackAmount(inv, slotStack, checkMeta, checkNbt, checkOre) <= 0) {
                        craftIndex = maxCraft;
                        break;
                    }
                }
            }

            if (craftIndex >= maxCraft) {
                break;
            }

            for (ItemStack slotStack : recipeSlots) {
                if (!slotStack.isEmpty()) {
                    InventoryHelper.getAndRemoveItem(inv, slotStack, 1, checkMeta, checkNbt, checkOre, null);
                }
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

        if (craftedCount > 0) {
            host.startCustomSwing();
            host.addShipExp(Config.expGainTask[3] * craftedCount);
            host.setFuel(host.getFuel() - Config.consumeGrudgeTask[3] * craftedCount);
            host.addMorale(-10 * craftedCount);
            if (host.getRandom().nextInt(5) == 0) {
                host.applyParticleEmotion(host.getRandom().nextInt(5));
            }
        }
    }

    private static boolean isWaypointGuardContext(EntityShipBase host, Level level) {
        if (host.getGuardedPos(4) != 1) {
            return false;
        }

        int guardedDimension = host.getGuardedPos(3);
        int currentDimension = getLegacyDimensionId(level);
        return guardedDimension == currentDimension || guardedDimension == Integer.MIN_VALUE;
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
