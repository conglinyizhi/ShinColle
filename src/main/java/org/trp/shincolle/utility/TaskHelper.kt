package org.trp.shincolle.utility

import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.tags.FluidTags
import net.minecraft.tags.ItemTags
import net.minecraft.world.Container
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.*
import net.minecraft.world.item.crafting.*
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.storage.loot.BuiltInLootTables
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemHandlerHelper
import net.neoforged.neoforge.items.wrapper.InvWrapper
import org.trp.shincolle.Config
import org.trp.shincolle.Config.MiningEntry
import org.trp.shincolle.block.entity.WayPointBlockEntity
import org.trp.shincolle.entity.EntityShipFishingHook
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.base.ShipGuardTarget
import org.trp.shincolle.entity.base.ShipTaskRuntime
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.item.*
import org.trp.shincolle.menu.ShipContainerMenu
import org.trp.shincolle.utility.InventoryHelper.getAndRemoveItem
import org.trp.shincolle.utility.InventoryHelper.getHandlersFromSide
import org.trp.shincolle.utility.InventoryHelper.matchTargetItem
import org.trp.shincolle.utility.InventoryHelper.moveItemstackToInv
import org.trp.shincolle.utility.PerformanceTrace.addTaskTime
import org.trp.shincolle.utility.PerformanceTrace.elapsed
import org.trp.shincolle.utility.PerformanceTrace.enabled
import org.trp.shincolle.utility.PerformanceTrace.logSlowTaskTick
import org.trp.shincolle.utility.PerformanceTrace.logTaskStage
import org.trp.shincolle.utility.PerformanceTrace.now
import org.trp.shincolle.utility.RecipePaperData.hasAnyRecipeIngredient
import org.trp.shincolle.utility.RecipePaperData.loadRecipeGrid
import java.util.List
import java.util.function.Function
import java.util.function.IntFunction
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object TaskHelper {
    private const val CRAFTING_WORK_START_SLOT = 12
    private const val CRAFTING_WORK_SLOT_COUNT = 9
    private const val HELD_MAINHAND_SLOT = 22
    private const val HELD_OFFHAND_SLOT = 23
    private val HELD_ITEM_SLOTS = intArrayOf(HELD_MAINHAND_SLOT, HELD_OFFHAND_SLOT)
    private const val LEGACY_GENERAL_WORLD_ID = 999999
    private val LEGACY_GENERAL_BIOME_ID_NUM = -999999
    private const val LEGACY_GENERAL_BIOME_ID = "-999999"
    private val TASK_NAMES = arrayOf<String>("none", "cooking", "fishing", "mining", "crafting")

    @JvmStatic
    fun onUpdateTask(host: EntityShipBase) {
        if (host.isInSittingPose || !host.isAlive || host.isNoFuel) {
            host.taskRuntime.clearTask()
            return
        }
        val taskId = host.getStateMinor(ShipContainerMenu.STATE_MINOR_TASK_ID)
        val runtime = host.taskRuntime
        val tracing = enabled()
        val start = if (tracing) now() else 0L
        try {
            runtime.beginTaskTick(taskId)
            when (taskId) {
                1 -> if (isTaskEnabled(0)) {
                    onUpdateCooking(host, runtime)
                } else {
                    runtime.clearTask()
                }

                2 -> if (isTaskEnabled(1)) {
                    onUpdateFishing(host, runtime)
                } else {
                    runtime.clearTask()
                }

                3 -> if (isTaskEnabled(2)) {
                    onUpdateMining(host, runtime)
                } else {
                    runtime.clearTask()
                }

                4 -> if (isTaskEnabled(3)) {
                    onUpdateCrafting(host, runtime)
                } else {
                    runtime.clearTask()
                }

                else -> runtime.clearTask()
            }
        } finally {
            if (tracing) {
                val elapsed = elapsed(start)
                addTaskTime(elapsed)
                logSlowTaskTick(host, taskName(taskId), taskId, elapsed)
            }
        }
    }

    private fun isTaskEnabled(index: Int): Boolean {
        return index >= 0 && index < Config.taskEnable.size && Config.taskEnable[index]
    }

    private fun taskName(taskId: Int): String {
        return if (taskId >= 0 && taskId < TASK_NAMES.size) TASK_NAMES[taskId] else "unknown"
    }

    private fun invalidateTask(runtime: ShipTaskRuntime?) {
        if (runtime != null) {
            runtime.clearTask()
        }
    }

    fun onUpdateCooking(host: EntityShipBase?) {
        if (host == null) return
        onUpdateCooking(host, host.taskRuntime)
    }

    private fun onUpdateCooking(host: EntityShipBase?, runtime: ShipTaskRuntime) {
        if (host == null || host.level().isClientSide) return
        val mainStack = host.heldItemMainhandSlot
        var offhandStack = host.heldItemOffhandSlot
        if (mainStack.isEmpty()) {
            invalidateTask(runtime)
            return
        }

        val level = host.level()
        val resultStack = level.getRecipeManager()
            .getRecipeFor<SingleRecipeInput, SmeltingRecipe>(RecipeType.SMELTING, SingleRecipeInput(mainStack), level)
            .map<ItemStack>(Function { recipe: RecipeHolder<SmeltingRecipe> ->
                recipe.value().assemble(SingleRecipeInput(mainStack), level.registryAccess())
            })
            .orElse(ItemStack.EMPTY)
        if (resultStack.isEmpty()) {
            invalidateTask(runtime)
            return
        }

        val guardTarget = host.guardTarget
        if (!isWaypointGuardContext(guardTarget, level)) {
            invalidateTask(runtime)
            return
        }
        val gx = guardTarget.x
        val gy = guardTarget.y
        val gz = guardTarget.z

        val wpPos = BlockPos(gx, gy, gz)
        val wpbe = level.getBlockEntity(wpPos)
        if (wpbe is WayPointBlockEntity) {
            val chestPos: BlockPos = wpbe.chestPos ?: BlockPos.ZERO
            if (chestPos.y <= 0) {
                invalidateTask(runtime)
                return
            }
            if (host.distanceToSqr(
                    chestPos.x.toDouble(),
                    chestPos.y.toDouble(),
                    chestPos.z.toDouble()
                ) > 25.0
            ) {
                runtime.moveToTaskPoint(Vec3(gx + 0.5, gy.toDouble(), gz + 0.5), 1.0)
                return
            }

            val targetBE = level.getBlockEntity(chestPos)
            if (targetBE == null) {
                invalidateTask(runtime)
                return
            }
            val taskSide = host.getStateMinor(ShipContainerMenu.STATE_MINOR_TASK_SIDE)
            val checkMeta = (taskSide and (1 shl 18)) != 0
            val checkOre = (taskSide and (1 shl 19)) != 0
            val checkNbt = (taskSide and (1 shl 20)) != 0
            var inHandlers: MutableList<IItemHandler> = getHandlersFromSide(level, chestPos, taskSide, 0)
            var outHandlers: MutableList<IItemHandler> = getHandlersFromSide(level, chestPos, taskSide, 1)
            var fuelHandlers: MutableList<IItemHandler> = getHandlersFromSide(level, chestPos, taskSide, 2)

            if (inHandlers.isEmpty() && fuelHandlers.isEmpty() && outHandlers.isEmpty()) {
                val fallbackHandler: IItemHandler?
                if (targetBE is Container) {
                    fallbackHandler = InvWrapper(targetBE)
                } else {
                    fallbackHandler =
                        level.getCapability<IItemHandler?, Direction?>(Capabilities.ItemHandler.BLOCK, chestPos, null)
                }
                if ((targetBE !is WorldlyContainer) || fallbackHandler == null || fallbackHandler.getSlots() != 3) {
                    invalidateTask(runtime)
                    return
                }
                inHandlers = mutableListOf<IItemHandler>(singleSlotView(fallbackHandler, 0))
                fuelHandlers = mutableListOf<IItemHandler>(singleSlotView(fallbackHandler, 1))
                outHandlers = mutableListOf<IItemHandler>(singleSlotView(fallbackHandler, 2))
            }

            var swing = false

            if (!mainStack.isEmpty()) {
                var canFit = 0
                for (handler in inHandlers) {
                    canFit += simulateInsertAcrossHandler(handler, mainStack)
                }

                if (canFit > 0) {
                    val material = getAndRemoveItem(
                        host.inventory!!,
                        mainStack,
                        canFit,
                        checkMeta,
                        checkNbt,
                        checkOre,
                        HELD_ITEM_SLOTS
                    )
                    if (!material.isEmpty()) {
                        var remaining = material
                        for (handler in inHandlers) {
                            if (remaining.isEmpty()) break
                            remaining = insertAcrossHandler(handler, remaining, false)
                        }
                        if (remaining.getCount() < material.getCount()) {
                            swing = true
                        }
                        if (!remaining.isEmpty()) {
                            moveItemstackToInv(host.inventory!!, remaining, null)
                        }
                    }
                }
            }

            offhandStack = host.heldItemOffhandSlot
            if (!offhandStack.isEmpty() && !fuelHandlers.isEmpty()) {
                var canFit = 0
                for (handler in fuelHandlers) {
                    canFit += simulateInsertAcrossHandler(handler, offhandStack)
                }

                if (canFit > 0) {
                    val fuel = getAndRemoveItem(
                        host.inventory!!,
                        offhandStack,
                        canFit,
                        checkMeta,
                        checkNbt,
                        checkOre,
                        HELD_ITEM_SLOTS
                    )
                    if (!fuel.isEmpty()) {
                        var remaining = fuel
                        for (handler in fuelHandlers) {
                            if (remaining.isEmpty()) break
                            remaining = insertAcrossHandler(handler, remaining, false)
                        }
                        if (remaining.getCount() < fuel.getCount()) {
                            swing = true
                        }
                        if (!remaining.isEmpty()) moveItemstackToInv(host.inventory!!, remaining, null)
                    }
                }
            }

            var tookOutput = false
            for (handler in outHandlers) {
                for (slot in 0..<handler.getSlots()) {
                    val inOutputSlot = handler.getStackInSlot(slot)
                    if (inOutputSlot.isEmpty()) {
                        continue
                    }
                    if (matchTargetItem(inOutputSlot, resultStack, checkMeta, checkNbt, checkOre)) {
                        val remainderSim =
                            ItemHandlerHelper.insertItemStacked(host.inventory!!, inOutputSlot.copy(), true)
                        val movable = inOutputSlot.getCount() - remainderSim.getCount()
                        if (movable <= 0) {
                            continue
                        }
                        val taken = handler.extractItem(slot, movable, false)
                        if (!taken.isEmpty()) {
                            ItemHandlerHelper.insertItemStacked(host.inventory!!, taken, false)
                            swing = true
                            tookOutput = true

                            host.addShipExp(Config.expGainTask[0])
                            host.fuel -= Config.consumeGrudgeTask[0]
                            host.addMorale(100)

                            val failChance =
                                (Config.shipMaxLevelNormal - host.level) / Config.shipMaxLevelNormal.toFloat() * 0.2f + 0.05f
                            if (host.random.nextFloat() < failChance) {
                                val entity = ItemEntity(
                                    level,
                                    chestPos.x + 0.5,
                                    chestPos.y + 1.0,
                                    chestPos.z + 0.5,
                                    ItemStack(
                                        Items.CHARCOAL
                                    )
                                )
                                level.addFreshEntity(entity)
                                host.applyParticleEmotion(6)
                            }
                            break
                        }
                    }
                }
                if (tookOutput) {
                    break
                }
            }

            if (swing) {
                host.startCustomSwing()
                if (host.random.nextInt(5) == 0) {
                    host.applyParticleEmotion(host.random.nextInt(5))
                }
            }
            return
        }

        invalidateTask(runtime)
    }

    fun onUpdateFishing(host: EntityShipBase?) {
        if (host == null) return
        onUpdateFishing(host, host.taskRuntime)
    }

    private fun onUpdateFishing(host: EntityShipBase?, runtime: ShipTaskRuntime) {
        if (host == null) return
        val level = host.level()
        val rod = host.heldItemMainhandSlot
        if (rod.isEmpty() || rod.item !== Items.FISHING_ROD) {
            invalidateTask(runtime)
            return
        }

        val guardTarget = host.guardTarget
        if (!isWaypointGuardContext(guardTarget, level)) {
            invalidateTask(runtime)
            return
        }
        val gx = guardTarget.x
        val gy = guardTarget.y
        val gz = guardTarget.z

        if (host.distanceToSqr(gx + 0.5, gy.toDouble(), gz + 0.5) > 10.0) {
            runtime.moveToTaskPoint(Vec3(gx + 0.5, gy.toDouble(), gz + 0.5), 1.0)
            return
        }

        if (abs(host.deltaMovement.x) > 0.1 || abs(host.deltaMovement.z) > 0.1 || host.deltaMovement.y > 0.1) return


        var stageStart = if (enabled()) now() else 0L
        val waterPos = findNearbyFishingWater(host, 5, 3)
        if (enabled()) {
            logTaskStage(
                host, "fishing", "findWater", elapsed(stageStart),
                "found=" + (waterPos != null) + " radius=5 depth=3"
            )
        }
        if (waterPos == null) {
            invalidateTask(runtime)
            return
        }

        val fishHook = host.fishHook
        if (fishHook == null || fishHook.isRemoved) {
            host.startCustomSwing()
            if (!level.isClientSide) {
                val hook = EntityShipFishingHook(level, host)
                hook.setPos(
                    waterPos.x + 0.1 + host.random.nextDouble() * 0.8,
                    waterPos.y + 1.0,
                    waterPos.z + 0.1 + host.random.nextDouble() * 0.8
                )
                level.addFreshEntity(hook)
                host.applyParticleEmotion(host.random.nextInt(4) + 1)
            }
            return
        }

        if (level is ServerLevel) {
        val serverLevel = level as ServerLevel
            if (fishHook.tickCount > Config.tickFishingMin + host.random
                    .nextInt(Config.tickFishingMax)
            ) {
                host.startCustomSwing()
                stageStart = if (enabled()) now() else 0L
                val lootTable = level.server.reloadableRegistries().getLootTable(BuiltInLootTables.FISHING)
                val params = (LootParams.Builder(level))
                    .withParameter<Vec3>(LootContextParams.ORIGIN, host.position())
                    .withParameter<ItemStack>(LootContextParams.TOOL, rod)
                    .withParameter<Entity>(LootContextParams.THIS_ENTITY, host)
                    .withLuck(getFishingLuck(host, level))
                    .create(LootContextParamSets.FISHING)

                val items: MutableList<ItemStack> = lootTable.getRandomItems(params)
                if (enabled()) {
                    logTaskStage(
                        host, "fishing", "lootRoll", elapsed(stageStart),
                        "items=" + items.size + " hookTicks=" + fishHook.tickCount
                    )
                }
                for (stack in items) {
                    val remainder = ItemHandlerHelper.insertItemStacked(host.inventory!!, stack, false)
                    if (!remainder.isEmpty()) {
                        val entity = ItemEntity(level, host.x, host.y, host.z, remainder)
                        level.addFreshEntity(entity)
                    }
                }

                fishHook.discard()
                host.addShipExp(Config.expGainTask[1])
                host.fuel -= Config.consumeGrudgeTask[1]
                host.addMorale(300)
                host.applyParticleEmotion(host.random.nextInt(5))
            } else if (fishHook.tickCount > Config.tickFishingMin + Config.tickFishingMax) {
                fishHook.discard()
            }
        }
    }

    fun onUpdateMining(host: EntityShipBase?) {
        if (host == null) return
        onUpdateMining(host, host.taskRuntime)
    }

    private fun onUpdateMining(host: EntityShipBase?, runtime: ShipTaskRuntime) {
        if (host == null) return
        val level = host.level()
        val pickaxe = host.heldItemMainhandSlot
        if (pickaxe.isEmpty() || !pickaxe.`is`(ItemTags.PICKAXES)) {
            invalidateTask(runtime)
            return
        }

        if (abs(host.deltaMovement.x) > 0.1 || abs(host.deltaMovement.z) > 0.1 || host.deltaMovement.y > 0.1) return

        if ((host.tickCount and 63) == 0) {
            runtime.moveTo(
                Vec3(
                    host.x + host.random.nextInt(9) - 4.0,
                    host.y + host.random.nextInt(5) - 2.0,
                    host.z + host.random.nextInt(9) - 4.0
                ), 1.0
            )
            return
        }

        if (host.random.nextInt(5) > 2) {
            host.startCustomSwing()
            if (!level.isClientSide && host.random.nextInt(10) > 8) {
                host.applyParticleEmotion(host.random.nextInt(5))
            }
        }

        if (!level.isClientSide && (host.tickCount and 31) == 0 && host.tickCount - host.getStateTimer(15) > Config.tickMiningMin + host.random
                .nextInt(
                    Config.tickMiningMax
                )
        ) {
            val xl = host.x.toInt()
            val yl = host.y.toInt()
            val zl = host.z.toInt()
            var stoneCount = 0
            var canMine = false
            val mutPos = MutableBlockPos()
            val scanStart = if (enabled()) now() else 0L
            var dy = -3
            while (dy < 5 && !canMine) {
                var dx = -3
                while (dx < 4 && !canMine) {
                    for (dz in -3..3) {
                        mutPos.set(xl + dx, yl + dy, zl + dz)
                        if (level.getBlockState(mutPos).`is`(BlockTags.MINEABLE_WITH_PICKAXE)) {
                            if (++stoneCount > 120) {
                                canMine = true
                                break
                            }
                        }
                    }
                    ++dx
                }
                ++dy
            }
            if (enabled()) {
                logTaskStage(
                    host, "mining", "scanStone", elapsed(scanStart),
                    "stoneCount=" + stoneCount + " canMine=" + canMine + " center=" + xl + "," + yl + "," + zl
                )
            }
            if (canMine) {
                val rollStart = if (enabled()) now() else 0L
                val drop = rollMiningDrop(host)
                if (enabled()) {
                    logTaskStage(
                        host, "mining", "rollDrop", elapsed(rollStart),
                        ("candidates=" + drop.candidates + " totalWeight=" + drop.totalWeight
                                + " fallback=" + drop.fallback + " result=" + drop.stack)
                    )
                }
                val result = drop.stack
                if (!result.isEmpty()) {
                    val remainder = ItemHandlerHelper.insertItemStacked(host.inventory!!, result, false)
                    if (!remainder.isEmpty()) {
                        level.addFreshEntity(ItemEntity(level, host.x, host.y, host.z, remainder))
                    }
                }

                host.addShipExp(Config.expGainTask[2])
                host.fuel -= Config.consumeGrudgeTask[2]
                host.addMorale(-200)
                host.applyParticleEmotion(host.random.nextInt(5))
                host.startCustomSwing()
                host.setStateTimer(15, host.tickCount)
            }
        }
    }

    private fun rollMiningDrop(host: EntityShipBase): MiningDropResult {
        if (Config.miningEntries.isEmpty()) {
            return MiningDropResult.createFallback()
        }

        val level = host.level()
        val shipLevel = host.level
        val y = host.blockPosition().y
        val toolLevel = getToolLevel(host.heldItemMainhandSlot)
        val biomeId = getLegacyBiomeId(level, host.blockPosition())
        val biomePath = level.getBiome(host.blockPosition())
            .unwrapKey()
            .map<String>(Function { key: ResourceKey<Biome>? -> key!!.location().toString() })
            .orElse("*")
        val dimensionId = getLegacyDimensionId(level)

        val candidates: MutableList<MiningEntry> = ArrayList<MiningEntry>()
        var totalWeight = 0
        for (entry in Config.miningEntries) {
            if (!TaskHelper.matchesDimension(entry!!, dimensionId, level)) continue
            if (!TaskHelper.matchesBiome(entry, biomeId, biomePath)) continue
            if (shipLevel < entry.minShipLevel) continue
            if (y > entry.maxY) continue
            if (toolLevel < entry.minToolLevel) continue
            candidates.add(entry)
            totalWeight += entry.weight
        }

        if (candidates.isEmpty() || totalWeight <= 0) {
            return MiningDropResult.createFallback(candidates.size, totalWeight)
        }

        var roll = host.random.nextInt(totalWeight)
        var chosen = candidates.get(0)
        for (entry in candidates) {
            roll -= entry.weight
            if (roll < 0) {
                chosen = entry
                break
            }
        }

        var amount = chosen.min
        if (chosen.max > chosen.min) {
            amount += host.random.nextInt(chosen.max - chosen.min + 1)
        }

        val fortuneLevel = getFortuneLevel(host.heldItemMainhandSlot, level)
        val luckLevel = getLuckLevel(host)
        val scaledAmount = amount * (1.0f + chosen.enchantFactor * (fortuneLevel + luckLevel))
        val finalAmount = max(1, Math.round(scaledAmount))
        return MiningDropResult(createMiningResultStack(chosen, finalAmount, host), candidates.size, totalWeight, false)
    }

    private fun matchesDimension(entry: MiningEntry, dimensionId: Int, level: Level): Boolean {
        if ("*" == entry.dimensionPath || entry.dimensionId == LEGACY_GENERAL_WORLD_ID) return true
        if (entry.dimensionId != Int.MIN_VALUE) {
            return entry.dimensionId == dimensionId
        }
        return level.dimension().location().toString() == entry.dimensionPath
    }

    private fun matchesBiome(entry: MiningEntry, biomeId: Int, biomePath: String): Boolean {
        if ("*" == entry.biomePath
            || LEGACY_GENERAL_BIOME_ID == entry.biomePath
            || entry.biomeId == LEGACY_GENERAL_BIOME_ID_NUM
        ) {
            return true
        }
        if (entry.biomeId != Int.MIN_VALUE) {
            return entry.biomeId == biomeId
        }
        return biomePath == entry.biomePath
    }

    private fun getLegacyDimensionId(level: Level): Int {
        val key = level.dimension().location().toString()
        return when (key) {
            "minecraft:overworld" -> 0
            "minecraft:the_nether" -> -1
            "minecraft:the_end" -> 1
            else -> Int.MIN_VALUE
        }
    }

    private fun getLegacyBiomeId(level: Level, pos: BlockPos): Int {
        return level.getBiome(pos)
            .unwrapKey()
            .map<Int?>(Function { key: ResourceKey<Biome?>? ->
                when (key!!.location().toString()) {
                    "minecraft:ocean" -> 0
                    "minecraft:plains" -> 1
                    "minecraft:desert" -> 2
                    "minecraft:windswept_hills" -> 3
                    "minecraft:forest" -> 4
                    "minecraft:taiga" -> 5
                    "minecraft:swamp" -> 6
                    "minecraft:river" -> 7
                    "minecraft:nether_wastes" -> 8
                    "minecraft:the_end" -> 9
                    "minecraft:frozen_ocean" -> 10
                    "minecraft:frozen_river" -> 11
                    "minecraft:snowy_plains" -> 12
                    "minecraft:snowy_slopes" -> 13
                    "minecraft:mushroom_fields" -> 14
                    "minecraft:beach" -> 16
                    "minecraft:jungle" -> 21
                    "minecraft:sparse_jungle" -> 23
                    "minecraft:deep_ocean" -> 24
                    "minecraft:stony_shore" -> 25
                    "minecraft:snowy_beach" -> 26
                    "minecraft:birch_forest" -> 27
                    "minecraft:dark_forest" -> 29
                    "minecraft:snowy_taiga" -> 30
                    "minecraft:old_growth_pine_taiga" -> 32
                    "minecraft:windswept_forest" -> 34
                    "minecraft:savanna" -> 35
                    "minecraft:savanna_plateau" -> 36
                    "minecraft:badlands" -> 37
                    "minecraft:wooded_badlands" -> 38
                    "minecraft:small_end_islands" -> 40
                    "minecraft:end_midlands" -> 41
                    "minecraft:end_highlands" -> 42
                    "minecraft:end_barrens" -> 43
                    "minecraft:warm_ocean" -> 44
                    "minecraft:lukewarm_ocean" -> 45
                    "minecraft:cold_ocean" -> 46
                    "minecraft:deep_lukewarm_ocean" -> 48
                    "minecraft:deep_cold_ocean" -> 49
                    "minecraft:deep_frozen_ocean" -> 50
                    "minecraft:sunflower_plains" -> 129
                    "minecraft:desert_lakes" -> 130
                    "minecraft:flower_forest" -> 132
                    "minecraft:ice_spikes" -> 140
                    "minecraft:modified_jungle" -> 149
                    "minecraft:modified_jungle_edge" -> 151
                    "minecraft:tall_birch_forest" -> 155
                    "minecraft:dark_forest_hills" -> 157
                    "minecraft:giant_spruce_taiga" -> 160
                    "minecraft:shattered_savanna" -> 163
                    "minecraft:eroded_badlands" -> 165
                    "minecraft:bamboo_jungle" -> 168
                    "minecraft:soul_sand_valley" -> 170
                    "minecraft:crimson_forest" -> 171
                    "minecraft:warped_forest" -> 172
                    "minecraft:basalt_deltas" -> 173
                    "minecraft:dripstone_caves" -> 174
                    "minecraft:lush_caves" -> 175
                    "minecraft:meadow" -> 177
                    "minecraft:grove" -> 179
                    "minecraft:jagged_peaks", "minecraft:frozen_peaks", "minecraft:stony_peaks" -> 182
                    "minecraft:mangrove_swamp" -> 184
                    "minecraft:deep_dark" -> 185
                    "minecraft:cherry_grove" -> 186
                    "minecraft:pale_garden" -> 187
                    else -> Int.MIN_VALUE
                }
            })
            .orElse(Int.MIN_VALUE)
    }

    private fun createMiningResultStack(entry: MiningEntry, count: Int, host: EntityShipBase): ItemStack {
        var stack = createLegacyVariantStack(entry.item, entry.itemMeta, host)
        if (stack.isEmpty()) {
            stack = ItemStack(entry.item)
        }
        stack.setCount(count)
        return stack
    }

    private fun createLegacyVariantStack(item: Item?, itemMeta: Int, host: EntityShipBase): ItemStack {
        if (item is LegacyEquipItem) {
            return createVariantStack(
                itemMeta, item.variantCount, host,
                IntFunction { variant: Int -> item.createVariantStack(variant) })
        }
        if (item is CombatRationItem) {
            return createVariantStack(
                itemMeta, item.variantCount, host,
                IntFunction { variant: Int -> item.createVariantStack(variant) })
        }
        if (item is ShipTankItem) {
            return createVariantStack(
                itemMeta, item.variantCount, host,
                IntFunction { variant: Int -> item.createVariantStack(variant) })
        }
        if (item is GrudgeItem) {
            return createVariantStack(
                itemMeta,
                2,
                host,
                IntFunction { variant: Int -> item.createVariantStack(variant) })
        }
        if (item is AbyssNuggetItem) {
            return createVariantStack(
                itemMeta,
                2,
                host,
                IntFunction { variant: Int -> item.createVariantStack(variant) })
        }
        return ItemStack.EMPTY
    }

    private fun createVariantStack(
        itemMeta: Int, variantCount: Int, host: EntityShipBase,
        stackFactory: IntFunction<ItemStack>
    ): ItemStack {
        if (variantCount <= 1) {
            return stackFactory.apply(0)
        }

        val variant = if (itemMeta <= 0)
            host.random.nextInt(variantCount)
        else min(itemMeta, variantCount - 1)
        return stackFactory.apply(variant)
    }

    private fun getToolLevel(pickaxe: ItemStack): Int {
        if (pickaxe.isEmpty()) {
            return 0
        }
        if (pickaxe.item is TieredItem) {
            val tieredItem = pickaxe.item as TieredItem
            val tier: Tier = tieredItem.tier
            if (tier === Tiers.STONE) {
                return 1
            }
            if (tier === Tiers.IRON) {
                return 2
            }
            if (tier === Tiers.DIAMOND || tier === Tiers.NETHERITE) {
                return 3
            }
        }
        return 0
    }

    private fun getFortuneLevel(pickaxe: ItemStack, level: Level): Int {
        if (pickaxe.isEmpty()) {
            return 0
        }

        val fortune: Holder<Enchantment?> = level.registryAccess()
            .lookupOrThrow<Enchantment?>(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.FORTUNE)
        return pickaxe.getEnchantmentLevel(fortune)
    }

    private fun getLuckLevel(host: EntityShipBase): Int {
        val effect = host.getEffect(MobEffects.LUCK)
        return if (effect == null) 0 else effect.getAmplifier() + 1
    }

    private fun getFishingLuck(host: EntityShipBase, level: Level): Float {
        return (max(
            getLuckOfTheSeaLevel(host.heldItemMainhandSlot, level),
            getLuckOfTheSeaLevel(host.heldItemOffhandSlot, level)
        ) + getLuckLevel(host)
                + host.level / Config.shipMaxLevelNormal.toFloat() * 1.5f)
    }

    private fun getLuckOfTheSeaLevel(stack: ItemStack, level: Level): Int {
        if (stack.isEmpty()) {
            return 0
        }

        val luckOfTheSea: Holder<Enchantment?> = level.registryAccess()
            .lookupOrThrow<Enchantment?>(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.LUCK_OF_THE_SEA)
        return stack.getEnchantmentLevel(luckOfTheSea)
    }

    private fun findNearbyFishingWater(host: EntityShipBase, radius: Int, depth: Int): BlockPos? {
        val level = host.level()
        val pos = MutableBlockPos()
        for (dy in 1 downTo -3 + 1) {
            for (dx in -radius..radius) {
                for (dz in -radius..radius) {
                    if (dx == 0 && dz == 0) {
                        continue
                    }
                    pos.set(host.x + dx, host.y + dy, host.z + dz)
                    if (isWaterWithDepth(level, pos, depth)) {
                        return pos.immutable()
                    }
                }
            }
        }
        return null
    }

    private fun isWaterWithDepth(level: Level, pos: BlockPos, depth: Int): Boolean {
        if (!level.getFluidState(pos).`is`(FluidTags.WATER)) {
            return false
        }
        for (dy in 1..<depth) {
            if (!level.getFluidState(pos.below(dy)).`is`(FluidTags.WATER)) {
                return false
            }
        }
        return true
    }

    fun onUpdateCrafting(host: EntityShipBase?) {
        if (host == null) return
        onUpdateCrafting(host, host.taskRuntime)
    }

    private fun onUpdateCrafting(host: EntityShipBase?, runtime: ShipTaskRuntime) {
        if (host == null || host.level().isClientSide) return


        val inv = host.inventory!!
        val recipePaper = host.heldItemMainhandSlot
        if (recipePaper.isEmpty() || !recipePaper.`is`(ModItems.RECIPE_PAPER.get())) {
            invalidateTask(runtime)
            return
        }

        val level = host.level()
        val recipeGrid: Array<ItemStack?> = loadRecipeGrid(recipePaper, level.registryAccess())
        val recipeSlots: MutableList<ItemStack> = ArrayList<ItemStack>(9)
        val materials: MutableList<ItemStack> = ArrayList<ItemStack>()
        for (i in 0..8) {
            val stack = recipeGrid[i] ?: ItemStack.EMPTY
            recipeSlots.add(stack)
            if (!stack.isEmpty()) {
                materials.add(stack)
            }
        }

        if (!hasAnyRecipeIngredient(recipeSlots)) {
            invalidateTask(runtime)
            return
        }

        val recipeInput = CraftingInput.of(3, 3, recipeSlots)
        val recipe = level.getRecipeManager()
            .getRecipeFor<CraftingInput?, CraftingRecipe?>(RecipeType.CRAFTING, recipeInput, level)
        if (recipe.isEmpty()) {
            invalidateTask(runtime)
            return
        }

        val resultTemplate = recipe.get().value().assemble(recipeInput, level.registryAccess())
        if (resultTemplate.isEmpty()) {
            invalidateTask(runtime)
            return
        }


        val uniqueMaterials: MutableList<ItemStack> = ArrayList<ItemStack>()
        val counts: MutableList<Int> = ArrayList<Int>()
        val taskSide = host.getStateMinor(ShipContainerMenu.STATE_MINOR_TASK_SIDE)
        val checkMeta = (taskSide and (1 shl 18)) != 0
        val checkOre = (taskSide and (1 shl 19)) != 0
        val checkNbt = (taskSide and (1 shl 20)) != 0

        for (m in materials) {
            var found = false
            for (i in uniqueMaterials.indices) {
                if (InventoryHelper.matchTargetItem(uniqueMaterials[i], m, checkMeta, checkNbt, checkOre)) {
                    counts[i] = counts[i] + 1
                    found = true
                    break
                }
            }
            if (!found) {
                uniqueMaterials.add(m.copy())
                counts.add(1)
            }
        }

        val guardTarget = host.guardTarget
        if (!isWaypointGuardContext(guardTarget, level)) {
            invalidateTask(runtime)
            return
        }
        val gx = guardTarget.x
        val gy = guardTarget.y
        val gz = guardTarget.z

        val wpPos = BlockPos(gx, gy, gz)
        val wpbe = level.getBlockEntity(wpPos)
        if (wpbe !is WayPointBlockEntity) {
            invalidateTask(runtime)
            return
        }
        val chestPos: BlockPos = wpbe.chestPos ?: BlockPos.ZERO
        if (chestPos.y <= 0) {
            invalidateTask(runtime)
            return
        }
        if (host.distanceToSqr(
                chestPos.x.toDouble(),
                chestPos.y.toDouble(),
                chestPos.z.toDouble()
            ) > 25.0
        ) {
            runtime.moveToTaskPoint(Vec3(gx + 0.5, gy.toDouble(), gz + 0.5), 1.0)
            return
        }


        var inHandlers: MutableList<IItemHandler> = getHandlersFromSide(level, chestPos, taskSide, 0)
        var outHandlers: MutableList<IItemHandler> = getHandlersFromSide(level, chestPos, taskSide, 1)
        if (inHandlers.isEmpty() && outHandlers.isEmpty()) {
            val targetBE = level.getBlockEntity(chestPos)
            if (targetBE !is Container) {
                invalidateTask(runtime)
                return
            }
            val fallbackHandler: IItemHandler = InvWrapper(targetBE)
            inHandlers = mutableListOf<IItemHandler>(fallbackHandler)
            outHandlers = mutableListOf<IItemHandler>(fallbackHandler)
        }
        val maxCraft = host.level / 20 + 1
        var craftedCount = 0
        val craftLoopStart = if (enabled()) now() else 0L

        var craftIndex = 0
        while (craftIndex < maxCraft) {
            val workingRecipeSlots: MutableList<ItemStack> = ArrayList<ItemStack>(CRAFTING_WORK_SLOT_COUNT)
            for (slot in 0..<CRAFTING_WORK_SLOT_COUNT) {
                val template = recipeSlots.get(slot)
                if (template.isEmpty()) {
                    workingRecipeSlots.add(ItemStack.EMPTY)
                    continue
                }

                var workStack = inv.getStackInSlot(CRAFTING_WORK_START_SLOT + slot)
                if (workStack.isEmpty()) {
                    workStack = pullCraftingIngredient(inHandlers, template, checkMeta, checkNbt, checkOre)
                    if (!workStack.isEmpty()) {
                        inv.setStackInSlot(CRAFTING_WORK_START_SLOT + slot, workStack)
                    }
                }

                if (!matchTargetItem(workStack, template, checkMeta, checkNbt, checkOre)) {
                    craftIndex = maxCraft
                    break
                }

                workingRecipeSlots.add(workStack)
            }

            val craftInput = CraftingInput.of(3, 3, workingRecipeSlots)
            val currentRecipe = level.getRecipeManager()
                .getRecipeFor<CraftingInput?, CraftingRecipe?>(RecipeType.CRAFTING, craftInput, level)
            if (currentRecipe.isEmpty()) {
                break
            }

            val resultPreview = currentRecipe.get().value().assemble(craftInput, level.registryAccess())
            if (resultPreview.isEmpty()
                || !matchTargetItem(resultPreview, resultTemplate, checkMeta, checkNbt, checkOre)
            ) {
                break
            }

            for (slot in 0..<CRAFTING_WORK_SLOT_COUNT) {
                val slotStack = recipeSlots.get(slot)
                if (slotStack.isEmpty()) {
                    continue
                }

                val workSlot = CRAFTING_WORK_START_SLOT + slot
                val consumeStack = inv.getStackInSlot(workSlot)
                if (consumeStack.isEmpty()) {
                    craftIndex = maxCraft
                    break
                }

                consumeStack.shrink(1)
                if (consumeStack.isEmpty()) {
                    inv.setStackInSlot(workSlot, ItemStack.EMPTY)
                } else {
                    inv.setStackInSlot(workSlot, consumeStack)
                }
            }

            if (craftIndex >= maxCraft) {
                break
            }

            var finalResult = currentRecipe.get().value().assemble(craftInput, level.registryAccess())
            for (h in outHandlers) {
                finalResult = ItemHandlerHelper.insertItemStacked(h, finalResult, false)
                if (finalResult.isEmpty()) break
            }
            if (!finalResult.isEmpty()) {
                level.addFreshEntity(
                    ItemEntity(
                        level,
                        chestPos.x + 0.5,
                        chestPos.y + 1.0,
                        chestPos.z + 0.5,
                        finalResult
                    )
                )
            }

            for (remainStack in currentRecipe.get().value().getRemainingItems(craftInput)) {
                if (remainStack.isEmpty()) {
                    continue
                }
                var remaining = remainStack.copy()
                for (h in outHandlers) {
                    remaining = ItemHandlerHelper.insertItemStacked(h, remaining, false)
                    if (remaining.isEmpty()) break
                }
                if (!remaining.isEmpty()) {
                    level.addFreshEntity(
                        ItemEntity(
                            level,
                            chestPos.x + 0.5,
                            chestPos.y + 1.0,
                            chestPos.z + 0.5,
                            remaining
                        )
                    )
                }
            }

            craftedCount++
            craftIndex++
        }
        if (enabled()) {
            logTaskStage(
                host, "crafting", "craftLoop", elapsed(craftLoopStart),
                "crafted=" + craftedCount + " maxCraft=" + maxCraft + " recipeResult=" + resultTemplate
            )
        }

        if (craftedCount > 0) {
            host.startCustomSwing()
            host.addShipExp(Config.expGainTask[3])
            host.fuel -= Config.consumeGrudgeTask[3]
            host.addMorale(-10)
            if (host.random.nextInt(5) == 0) {
                host.applyParticleEmotion(host.random.nextInt(5))
            }
        }
    }

    private fun isWaypointGuardContext(guardTarget: ShipGuardTarget, level: Level): Boolean {
        if (!guardTarget.isBlock) {
            return false
        }

        val guardedDimension = guardTarget.dimensionId
        val currentDimension = getLegacyDimensionId(level)
        return guardedDimension == currentDimension || guardedDimension == Int.MIN_VALUE
    }

    private fun pullCraftingIngredient(
        inHandlers: MutableList<IItemHandler>, template: ItemStack,
        checkMeta: Boolean, checkNbt: Boolean, checkOre: Boolean
    ): ItemStack {
        for (handler in inHandlers) {
            val pulled = getAndRemoveItem(handler, template, 1, checkMeta, checkNbt, checkOre, null)
            if (!pulled.isEmpty()) {
                return pulled
            }
        }

        return ItemStack.EMPTY
    }

    private fun simulateInsertAcrossHandler(handler: IItemHandler?, stack: ItemStack): Int {
        if (handler == null || stack.isEmpty()) {
            return 0
        }

        return stack.getCount() - insertAcrossHandler(handler, stack, true).getCount()
    }

    private fun insertAcrossHandler(handler: IItemHandler?, stack: ItemStack, simulate: Boolean): ItemStack {
        if (handler == null || stack.isEmpty()) {
            return stack
        }

        var remaining = stack.copy()
        var slot = 0
        while (slot < handler.getSlots() && !remaining.isEmpty()) {
            remaining = handler.insertItem(slot, remaining, simulate)
            slot++
        }
        return remaining
    }

    private fun singleSlotView(handler: IItemHandler, slot: Int): IItemHandler {
        return object : IItemHandler {
            override fun getSlots(): Int {
                return 1
            }

            override fun getStackInSlot(viewSlot: Int): ItemStack {
                return if (viewSlot == 0) handler.getStackInSlot(slot) else ItemStack.EMPTY
            }

            override fun insertItem(viewSlot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                if (viewSlot != 0) {
                    return stack
                }
                return handler.insertItem(slot, stack, simulate)
            }

            override fun extractItem(viewSlot: Int, amount: Int, simulate: Boolean): ItemStack {
                if (viewSlot != 0) {
                    return ItemStack.EMPTY
                }
                return handler.extractItem(slot, amount, simulate)
            }

            override fun getSlotLimit(viewSlot: Int): Int {
                return if (viewSlot == 0) handler.getSlotLimit(slot) else 0
            }

            override fun isItemValid(viewSlot: Int, stack: ItemStack): Boolean {
                return viewSlot == 0 && handler.isItemValid(slot, stack)
            }
        }
    }

    @JvmRecord
    private data class MiningDropResult(
        val stack: ItemStack,
        val candidates: Int,
        val totalWeight: Int,
        val fallback: Boolean
    ) {
        companion object {
            internal fun createFallback(candidates: Int = 0, totalWeight: Int = 0): MiningDropResult {
                return MiningDropResult(ItemStack.EMPTY, candidates, totalWeight, true)
            }
        }
    }
}
