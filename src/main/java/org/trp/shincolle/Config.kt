@file:Suppress("DEPRECATION", "UNCHECKED_CAST")
package org.trp.shincolle

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.config.ModConfigEvent
import net.neoforged.neoforge.common.ModConfigSpec
import net.neoforged.neoforge.common.ModConfigSpec.IntValue
import java.util.*
import java.util.List
import kotlin.math.max
import kotlin.math.min

@EventBusSubscriber(modid = Shincolle.MODID)
object Config {
    val BUILDER: ModConfigSpec.Builder = ModConfigSpec.Builder()
    val CLIENT_BUILDER: ModConfigSpec.Builder = ModConfigSpec.Builder()

    lateinit var SHIP_EXP_MODIFIER: IntValue
    lateinit var SHIP_EXP_GAIN_MELEE: IntValue
    lateinit var SHIP_EXP_GAIN_KILL: IntValue
    lateinit var SHIP_EXP_GAIN_LIGHT_ATTACK: IntValue
    lateinit var SHIP_EXP_GAIN_HEAVY_ATTACK: IntValue
    lateinit var SHIP_EXP_GAIN_LIGHT_AIRCRAFT: IntValue
    lateinit var SHIP_EXP_GAIN_HEAVY_AIRCRAFT: IntValue
    lateinit var SHIP_MAX_LEVEL_NORMAL: IntValue
    lateinit var SHIP_MAX_LEVEL_MARRIED: IntValue
    lateinit var TRAINING_BOOK_LEVEL_MIN: IntValue
    lateinit var TRAINING_BOOK_LEVEL_MAX: IntValue
    lateinit var DEBUG_LOGGING: ModConfigSpec.BooleanValue
    lateinit var DEBUG_PERFORMANCE_LOGGING: ModConfigSpec.BooleanValue
    lateinit var DEBUG_PERF_SLOW_SHIP_TICK_MS: IntValue
    lateinit var DEBUG_PERF_SLOW_TASK_TICK_MS: IntValue
    lateinit var DEBUG_PERF_SLOW_BLOCK_ENTITY_TICK_MS: IntValue
    lateinit var DEBUG_PERF_SLOW_PROJECTILE_TICK_MS: IntValue
    lateinit var DEBUG_PERF_SLOW_SERVER_TICK_MS: IntValue
    lateinit var DEBUG_PERF_MIN_LOG_INTERVAL_TICKS: IntValue
    lateinit var MODERN_KIT_NOTIFY_WHEN_MAXED: ModConfigSpec.BooleanValue
    lateinit var MODERN_KIT_NOTIFY_WHEN_MAXED_ACTION_BAR: ModConfigSpec.BooleanValue
    lateinit var FUEL_DECAY_INTERVAL: IntValue
    lateinit var FUEL_MOVE_DECAY_FACTOR: IntValue
    lateinit var FUEL_CONSUME_DD: IntValue
    lateinit var FUEL_CONSUME_CL: IntValue
    lateinit var FUEL_CONSUME_CA: IntValue
    lateinit var FUEL_CONSUME_CAV: IntValue
    lateinit var FUEL_CONSUME_CLT: IntValue
    lateinit var FUEL_CONSUME_CVL: IntValue
    lateinit var FUEL_CONSUME_CV: IntValue
    lateinit var FUEL_CONSUME_BB: IntValue
    lateinit var FUEL_CONSUME_BBV: IntValue
    lateinit var FUEL_CONSUME_SS: IntValue
    lateinit var FUEL_CONSUME_AP: IntValue
    lateinit var FUEL_CONSUME_ACTION_LIGHT: IntValue
    lateinit var FUEL_CONSUME_ACTION_HEAVY: IntValue
    lateinit var FUEL_CONSUME_ACTION_LIGHT_AIRCRAFT: IntValue
    lateinit var FUEL_CONSUME_ACTION_HEAVY_AIRCRAFT: IntValue

    lateinit var TICK_FISHING_MIN: IntValue
    lateinit var TICK_FISHING_MAX: IntValue
    lateinit var TICK_MINING_MIN: IntValue
    lateinit var TICK_MINING_MAX: IntValue
    lateinit var TASK_ENABLE_COOKING: ModConfigSpec.BooleanValue
    lateinit var TASK_ENABLE_FISHING: ModConfigSpec.BooleanValue
    lateinit var TASK_ENABLE_MINING: ModConfigSpec.BooleanValue
    lateinit var TASK_ENABLE_CRAFTING: ModConfigSpec.BooleanValue
    lateinit var SMALL_SHIPYARD_POWER_MAX: IntValue
    lateinit var SMALL_SHIPYARD_BUILD_SPEED: IntValue
    lateinit var SMALL_SHIPYARD_INSTANT_TICKS: IntValue
    lateinit var SMALL_SHIPYARD_FUEL_MAGNIFICATION: ModConfigSpec.DoubleValue
    lateinit var LARGE_SHIPYARD_POWER_MAX: IntValue
    lateinit var LARGE_SHIPYARD_BUILD_SPEED: IntValue
    lateinit var LARGE_SHIPYARD_INSTANT_TICKS: IntValue
    lateinit var LARGE_SHIPYARD_FUEL_MAGNIFICATION: ModConfigSpec.DoubleValue
    lateinit var RING_ABILITY_WATER_BREATHING: IntValue
    lateinit var RING_ABILITY_SWIM_FLIGHT: IntValue
    lateinit var RING_ABILITY_UNDERWATER_DIG_CAP: IntValue
    lateinit var RING_ABILITY_UNDERWATER_FOG_CAP: IntValue
    lateinit var RING_ABILITY_FIRE_IMMUNITY: IntValue
    lateinit var DRUM_LIQUID_BASE_RATE: IntValue
    lateinit var DRUM_LIQUID_ENCHANT_RATE: IntValue
    lateinit var DRUM_ENERGY_BASE_RATE: IntValue
    lateinit var DRUM_ENERGY_ENCHANT_RATE: IntValue
    lateinit var PAIR_DIST_CHEST: IntValue
    lateinit var PAIR_DIST_WAYPOINT: IntValue
    lateinit var SHIP_CAN_TELEPORT: ModConfigSpec.BooleanValue
    lateinit var SHIP_FREEZE_WHEN_GUI_OPEN: ModConfigSpec.BooleanValue
    lateinit var ENABLE_FIRING_LINE_CHECK: ModConfigSpec.BooleanValue
    lateinit var CRUISE_SPEED_FACTOR: ModConfigSpec.DoubleValue
    lateinit var SHIP_BUFF_DURATION: IntValue
    lateinit var CRANE_TANK_CAPACITY: IntValue
    lateinit var VOLCORE_POWER_MAX: IntValue
    lateinit var VOLCORE_CONSUME_SPEED: IntValue
    lateinit var VOLCORE_FUEL_MAGNITUDE: IntValue
    lateinit var MINING_ENTRIES: ModConfigSpec.ConfigValue<MutableList<out String?>?>
    lateinit var LOOT_ENTRIES: ModConfigSpec.ConfigValue<MutableList<out String?>?>
    val EXP_GAIN_TASK: Array<IntValue?> = arrayOfNulls<IntValue>(4)
    val CONSUME_GRUDGE_TASK: Array<IntValue?> = arrayOfNulls<IntValue>(4)

    lateinit var HOSTILE_DROP_GRUDGE_RATE: ModConfigSpec.DoubleValue
    lateinit var HOSTILE_DEATH_MAX_TICKS: IntValue
    lateinit var HOSTILE_DESPAWN_BOSS_TICKS: IntValue
    lateinit var HOSTILE_DESPAWN_MINION_TICKS: IntValue
    lateinit var HOSTILE_BOSS_COOLDOWN_TICKS: IntValue
    lateinit var HOSTILE_SPAWN_BOSS_COUNT: IntValue
    lateinit var HOSTILE_SPAWN_MINION_COUNT: IntValue
    lateinit var HOSTILE_SPAWN_REQUIRE_RING: ModConfigSpec.BooleanValue
    lateinit var HOSTILE_MOB_SPAWN_MAX: IntValue
    lateinit var HOSTILE_MOB_SPAWN_CHANCE_PERCENT: IntValue
    lateinit var HOSTILE_MOB_SPAWN_GROUPS: IntValue
    lateinit var HOSTILE_MOB_SPAWN_GROUP_MIN: IntValue
    lateinit var HOSTILE_MOB_SPAWN_GROUP_MAX: IntValue

    lateinit var SHIP_CAN_TIMEKEEPING: ModConfigSpec.BooleanValue
    lateinit var SHIP_VOLUME_TIMEKEEPING: ModConfigSpec.DoubleValue
    lateinit var SHIP_VOLUME_GENERAL: ModConfigSpec.DoubleValue
    lateinit var SHIP_VOLUME_ATTACK: ModConfigSpec.DoubleValue
    lateinit var CUSTOM_SOUND_RATES: ModConfigSpec.ConfigValue<MutableList<out String?>?>

    lateinit var CLIENT_SCALE_HELD_ITEM: ModConfigSpec.DoubleValue
    lateinit var CLIENT_OFFSET_HELD_ITEM_X: ModConfigSpec.DoubleValue
    lateinit var CLIENT_OFFSET_HELD_ITEM_Y: ModConfigSpec.DoubleValue
    lateinit var CLIENT_OFFSET_HELD_ITEM_Z: ModConfigSpec.DoubleValue

    lateinit var USE_MISANS_FONT: ModConfigSpec.BooleanValue
    lateinit var MISANS_ONLY_LEGACY_LOGS: ModConfigSpec.BooleanValue
    lateinit var SPEC: ModConfigSpec
    lateinit var CLIENT_SPEC: ModConfigSpec

    @JvmField
    var shipExpModifier: Int = 20
    @JvmField
    var shipExpGainMelee: Int = 4
    @JvmField
    var shipExpGainKill: Int = 8
    var shipExpGainLightAttack: Int = 8
    var shipExpGainHeavyAttack: Int = 24
    var shipExpGainLightAircraft: Int = 16
    var shipExpGainHeavyAircraft: Int = 48
    @JvmField
    var shipMaxLevelNormal: Int = 100
    @JvmField
    var shipMaxLevelMarried: Int = 150
    @JvmField
    var trainingBookLevelMin: Int = 5
    @JvmField
    var trainingBookLevelMax: Int = 10
    var debugLogging: Boolean = false
    @JvmField
    var debugPerformanceLogging: Boolean = false
    @JvmField
    var debugPerfSlowShipTickMs: Int = 10
    @JvmField
    var debugPerfSlowTaskTickMs: Int = 5
    @JvmField
    var debugPerfSlowBlockEntityTickMs: Int = 5
    @JvmField
    var debugPerfSlowProjectileTickMs: Int = 5
    @JvmField
    var debugPerfSlowServerTickMs: Int = 40
    @JvmField
    var debugPerfMinLogIntervalTicks: Int = 20
    @JvmField
    var modernKitNotifyWhenMaxed: Boolean = true
    @JvmField
    var modernKitNotifyWhenMaxedActionBar: Boolean = true
    @JvmField
    var fuelDecayInterval: Int = 128
    @JvmField
    var fuelMoveDecayFactor: Int = 3
    @JvmField
    var fuelConsumeDD: Int = 5
    @JvmField
    var fuelConsumeCL: Int = 7
    @JvmField
    var fuelConsumeCA: Int = 8
    var fuelConsumeCAV: Int = 9
    var fuelConsumeCLT: Int = 8
    var fuelConsumeCVL: Int = 11
    @JvmField
    var fuelConsumeCV: Int = 12
    @JvmField
    var fuelConsumeBB: Int = 15
    @JvmField
    var fuelConsumeBBV: Int = 14
    @JvmField
    var fuelConsumeSS: Int = 4
    @JvmField
    var fuelConsumeAP: Int = 3
    @JvmField
    var fuelConsumeActionLight: Int = 4
    @JvmField
    var fuelConsumeActionHeavy: Int = 8
    @JvmField
    var fuelConsumeActionLightAircraft: Int = 6
    @JvmField
    var fuelConsumeActionHeavyAircraft: Int = 12

    @JvmField
    var tickFishingMin: Int = 100
    @JvmField
    var tickFishingMax: Int = 300
    @JvmField
    var tickMiningMin: Int = 100
    @JvmField
    var tickMiningMax: Int = 200
    @JvmField
    var taskEnable: BooleanArray = booleanArrayOf(true, true, true, true)
    @JvmField
    var smallShipyardPowerMax: Int = 460800
    @JvmField
    var smallShipyardBuildSpeed: Int = 48
    @JvmField
    var smallShipyardInstantTicks: Int = 2400
    @JvmField
    var smallShipyardFuelMagnification: Float = 1.0f
    @JvmField
    var largeShipyardPowerMax: Int = 1382400
    @JvmField
    var largeShipyardBuildSpeed: Int = 48
    @JvmField
    var largeShipyardInstantTicks: Int = 1200
    @JvmField
    var largeShipyardFuelMagnification: Float = 1.0f
    @JvmField
    var ringAbilityWaterBreathing: Int = 0
    @JvmField
    var ringAbilitySwimFlight: Int = 6
    @JvmField
    var ringAbilityUnderwaterDigCap: Int = 30
    @JvmField
    var ringAbilityUnderwaterFogCap: Int = 20
    @JvmField
    var ringAbilityFireImmunity: Int = 12
    @JvmField
    var drumLiquidBaseRate: Int = 40
    @JvmField
    var drumLiquidEnchantRate: Int = 5
    @JvmField
    var drumEnergyBaseRate: Int = 400
    @JvmField
    var drumEnergyEnchantRate: Int = 100
    @JvmField
    var pairDistChest: Int = 16
    @JvmField
    var pairDistWaypoint: Int = 48
    @JvmField
    var canTeleport: Boolean = true
    @JvmField
    var enableFiringLineCheck: Boolean = false
    @JvmField
    var cruiseSpeedFactor: Double = 0.3
    @JvmField
    var craneTankCapacity: Int = 2048000
    @JvmField
    var volCorePowerMax: Int = 9600
    @JvmField
    var volCoreConsumeSpeed: Int = 16
    @JvmField
    var volCoreFuelMagnitude: Int = 240
    @JvmField
    var miningEntries: MutableList<MiningEntry?> = mutableListOf<MiningEntry?>()
    @JvmField
    var lootEntries: MutableList<LootEntry?> = mutableListOf<LootEntry?>()
    @JvmField
    var expGainTask: IntArray = intArrayOf(10, 10, 20, 5)
    @JvmField
    var consumeGrudgeTask: IntArray = intArrayOf(5, 5, 20, 10)

    @JvmField
    var hostileDropGrudgeRate: Float = 1.0f
    var hostileDeathMaxTicks: Int = 400
    var hostileDespawnBossTicks: Int = 12000
    @JvmField
    var hostileDespawnMinionTicks: Int = 600
    @JvmField
    var hostileBossCooldownTicks: Int = 4800
    @JvmField
    var hostileSpawnBossCount: Int = 2
    @JvmField
    var hostileSpawnMinionCount: Int = 4
    @JvmField
    var hostileSpawnRequireRing: Boolean = true
    @JvmField
    var hostileMobSpawnMax: Int = 50
    @JvmField
    var hostileMobSpawnChancePercent: Int = 10
    @JvmField
    var hostileMobSpawnGroups: Int = 1
    @JvmField
    var hostileMobSpawnGroupMin: Int = 1
    @JvmField
    var hostileMobSpawnGroupMax: Int = 1

    @JvmField
    var canTimeKeeping: Boolean = true
    @JvmField
    var volumeTimeKeeping: Float = 1.0f
    @JvmField
    var volumeShip: Float = 0.6f
    @JvmField
    var volumeAttack: Float = 0.7f
    @JvmField
    var customSoundRates: MutableMap<Int?, EnumMap<ShipCustomSoundType, Float?>?> =
        mutableMapOf<Int?, EnumMap<ShipCustomSoundType, Float?>?>()

    @JvmField
    var scaleHeldItem: Float = 1.0f
    @JvmField
    var offsetHeldItemX: Float = 0.0f
    @JvmField
    var offsetHeldItemY: Float = 0.0f
    @JvmField
    var offsetHeldItemZ: Float = 0.0f
    @JvmField
    var useMiSansFont: Boolean = true
    @JvmField
    var miSansOnlyForLegacyLogs: Boolean = true

    init {
        ConfigSpecBuilder.buildCommonSpec()
        ConfigSpecBuilder.buildClientSpec()
    }

    @JvmStatic
    @SubscribeEvent
    fun onLoad(event: ModConfigEvent) {
        if (event.getConfig().getSpec() === SPEC) {
            shipExpModifier = SHIP_EXP_MODIFIER.get()
            shipExpGainMelee = SHIP_EXP_GAIN_MELEE.get()
            shipExpGainKill = SHIP_EXP_GAIN_KILL.get()
            shipExpGainLightAttack = SHIP_EXP_GAIN_LIGHT_ATTACK.get()
            shipExpGainHeavyAttack = SHIP_EXP_GAIN_HEAVY_ATTACK.get()
            shipExpGainLightAircraft = SHIP_EXP_GAIN_LIGHT_AIRCRAFT.get()
            shipExpGainHeavyAircraft = SHIP_EXP_GAIN_HEAVY_AIRCRAFT.get()
            shipMaxLevelNormal = SHIP_MAX_LEVEL_NORMAL.get()
            shipMaxLevelMarried = max(shipMaxLevelNormal, SHIP_MAX_LEVEL_MARRIED.get())
            trainingBookLevelMin = TRAINING_BOOK_LEVEL_MIN.get()
            trainingBookLevelMax = max(trainingBookLevelMin, TRAINING_BOOK_LEVEL_MAX.get())
            debugLogging = DEBUG_LOGGING.get()
            debugPerformanceLogging = DEBUG_PERFORMANCE_LOGGING.get()
            debugPerfSlowShipTickMs = DEBUG_PERF_SLOW_SHIP_TICK_MS.get()
            debugPerfSlowTaskTickMs = DEBUG_PERF_SLOW_TASK_TICK_MS.get()
            debugPerfSlowBlockEntityTickMs = DEBUG_PERF_SLOW_BLOCK_ENTITY_TICK_MS.get()
            debugPerfSlowProjectileTickMs = DEBUG_PERF_SLOW_PROJECTILE_TICK_MS.get()
            debugPerfSlowServerTickMs = DEBUG_PERF_SLOW_SERVER_TICK_MS.get()
            debugPerfMinLogIntervalTicks = DEBUG_PERF_MIN_LOG_INTERVAL_TICKS.get()
            modernKitNotifyWhenMaxed = MODERN_KIT_NOTIFY_WHEN_MAXED.get()
            modernKitNotifyWhenMaxedActionBar = MODERN_KIT_NOTIFY_WHEN_MAXED_ACTION_BAR.get()

            fuelDecayInterval = FUEL_DECAY_INTERVAL.get()
            fuelMoveDecayFactor = FUEL_MOVE_DECAY_FACTOR.get()
            fuelConsumeDD = FUEL_CONSUME_DD.get()
            fuelConsumeCL = FUEL_CONSUME_CL.get()
            fuelConsumeCA = FUEL_CONSUME_CA.get()
            fuelConsumeCAV = FUEL_CONSUME_CAV.get()
            fuelConsumeCLT = FUEL_CONSUME_CLT.get()
            fuelConsumeCVL = FUEL_CONSUME_CVL.get()
            fuelConsumeCV = FUEL_CONSUME_CV.get()
            fuelConsumeBB = FUEL_CONSUME_BB.get()
            fuelConsumeBBV = FUEL_CONSUME_BBV.get()
            fuelConsumeSS = FUEL_CONSUME_SS.get()
            fuelConsumeAP = FUEL_CONSUME_AP.get()
            fuelConsumeActionLight = FUEL_CONSUME_ACTION_LIGHT.get()
            fuelConsumeActionHeavy = FUEL_CONSUME_ACTION_HEAVY.get()
            fuelConsumeActionLightAircraft = FUEL_CONSUME_ACTION_LIGHT_AIRCRAFT.get()
            fuelConsumeActionHeavyAircraft = FUEL_CONSUME_ACTION_HEAVY_AIRCRAFT.get()

            tickFishingMin = TICK_FISHING_MIN.get()
            tickFishingMax = TICK_FISHING_MAX.get()
            tickMiningMin = TICK_MINING_MIN.get()
            tickMiningMax = TICK_MINING_MAX.get()
            taskEnable[0] = TASK_ENABLE_COOKING.get()
            taskEnable[1] = TASK_ENABLE_FISHING.get()
            taskEnable[2] = TASK_ENABLE_MINING.get()
            taskEnable[3] = TASK_ENABLE_CRAFTING.get()
            smallShipyardPowerMax = SMALL_SHIPYARD_POWER_MAX.get()
            smallShipyardBuildSpeed = SMALL_SHIPYARD_BUILD_SPEED.get()
            smallShipyardInstantTicks = SMALL_SHIPYARD_INSTANT_TICKS.get()
            smallShipyardFuelMagnification = SMALL_SHIPYARD_FUEL_MAGNIFICATION.get().toFloat()
            largeShipyardPowerMax = LARGE_SHIPYARD_POWER_MAX.get()
            largeShipyardBuildSpeed = LARGE_SHIPYARD_BUILD_SPEED.get()
            largeShipyardInstantTicks = LARGE_SHIPYARD_INSTANT_TICKS.get()
            largeShipyardFuelMagnification = LARGE_SHIPYARD_FUEL_MAGNIFICATION.get().toFloat()
            ringAbilityWaterBreathing = RING_ABILITY_WATER_BREATHING.get()
            ringAbilitySwimFlight = RING_ABILITY_SWIM_FLIGHT.get()
            ringAbilityUnderwaterDigCap = RING_ABILITY_UNDERWATER_DIG_CAP.get()
            ringAbilityUnderwaterFogCap = RING_ABILITY_UNDERWATER_FOG_CAP.get()
            ringAbilityFireImmunity = RING_ABILITY_FIRE_IMMUNITY.get()
            drumLiquidBaseRate = DRUM_LIQUID_BASE_RATE.get()
            drumLiquidEnchantRate = DRUM_LIQUID_ENCHANT_RATE.get()
            drumEnergyBaseRate = DRUM_ENERGY_BASE_RATE.get()
            drumEnergyEnchantRate = DRUM_ENERGY_ENCHANT_RATE.get()
            pairDistChest = PAIR_DIST_CHEST.get()
            pairDistWaypoint = PAIR_DIST_WAYPOINT.get()
            canTeleport = SHIP_CAN_TELEPORT.get()
            enableFiringLineCheck = ENABLE_FIRING_LINE_CHECK.get()
            cruiseSpeedFactor = CRUISE_SPEED_FACTOR.get()
            craneTankCapacity = CRANE_TANK_CAPACITY.get()
            volCorePowerMax = VOLCORE_POWER_MAX.get()
            volCoreConsumeSpeed = VOLCORE_CONSUME_SPEED.get()
            volCoreFuelMagnitude = VOLCORE_FUEL_MAGNITUDE.get()
            miningEntries = Config.parseMiningEntries(MINING_ENTRIES.get() as List<out String?>?)
            lootEntries = Config.parseLootEntries(LOOT_ENTRIES.get() as List<out String?>?)
            for (i in 0..3) {
                expGainTask[i] = EXP_GAIN_TASK[i]!!.get()
                consumeGrudgeTask[i] = CONSUME_GRUDGE_TASK[i]!!.get()
            }

            hostileDropGrudgeRate = HOSTILE_DROP_GRUDGE_RATE.get().toFloat()
            hostileDeathMaxTicks = HOSTILE_DEATH_MAX_TICKS.get()
            hostileDespawnBossTicks = HOSTILE_DESPAWN_BOSS_TICKS.get()
            hostileDespawnMinionTicks = HOSTILE_DESPAWN_MINION_TICKS.get()
            hostileBossCooldownTicks = HOSTILE_BOSS_COOLDOWN_TICKS.get()
            hostileSpawnBossCount = HOSTILE_SPAWN_BOSS_COUNT.get()
            hostileSpawnMinionCount = HOSTILE_SPAWN_MINION_COUNT.get()
            hostileSpawnRequireRing = HOSTILE_SPAWN_REQUIRE_RING.get()
            hostileMobSpawnMax = HOSTILE_MOB_SPAWN_MAX.get()
            hostileMobSpawnChancePercent = HOSTILE_MOB_SPAWN_CHANCE_PERCENT.get()
            hostileMobSpawnGroups = max(1, HOSTILE_MOB_SPAWN_GROUPS.get())
            hostileMobSpawnGroupMin = max(1, HOSTILE_MOB_SPAWN_GROUP_MIN.get())
            hostileMobSpawnGroupMax = max(hostileMobSpawnGroupMin, HOSTILE_MOB_SPAWN_GROUP_MAX.get())
            return
        }

        if (event.getConfig().getSpec() === CLIENT_SPEC) {
            canTimeKeeping = SHIP_CAN_TIMEKEEPING.get()
            volumeTimeKeeping = SHIP_VOLUME_TIMEKEEPING.get().toFloat()
            volumeShip = SHIP_VOLUME_GENERAL.get().toFloat()
            volumeAttack = SHIP_VOLUME_ATTACK.get().toFloat()
            customSoundRates = parseCustomSoundRates(CUSTOM_SOUND_RATES.get())

            scaleHeldItem = CLIENT_SCALE_HELD_ITEM.get().toFloat()
            offsetHeldItemX = CLIENT_OFFSET_HELD_ITEM_X.get().toFloat()
            offsetHeldItemY = CLIENT_OFFSET_HELD_ITEM_Y.get().toFloat()
            offsetHeldItemZ = CLIENT_OFFSET_HELD_ITEM_Z.get().toFloat()

            useMiSansFont = USE_MISANS_FONT.get()
            miSansOnlyForLegacyLogs = MISANS_ONLY_LEGACY_LOGS.get()
        }
    }

    internal fun defaultMiningEntries(): MutableList<String?> {
        val entries: MutableList<String?> = ArrayList<String?>()
        entries.add("*,*,minecraft:cobblestone,0,100,1,4,1,256,0,0")

        entries.add("minecraft:overworld,*,minecraft:cobblestone,0,4000,1,4,1,256,0,0")
        entries.add("minecraft:overworld,*,minecraft:stone,0,500,1,1,1,256,0,0")
        entries.add("minecraft:overworld,*,minecraft:granite,0,500,1,1,1,256,0,0")
        entries.add("minecraft:overworld,*,minecraft:diorite,0,500,1,1,1,256,0,0")
        entries.add("minecraft:overworld,*,minecraft:andesite,0,500,1,1,1,256,0,0")
        entries.add("minecraft:overworld,*,minecraft:dirt,0,500,1,1,1,256,0,0")
        entries.add("minecraft:overworld,*,minecraft:sand,0,500,1,1,1,256,0,0")
        entries.add("minecraft:overworld,*,minecraft:gravel,0,200,1,1,1,256,0,0")
        entries.add("minecraft:overworld,*,minecraft:obsidian,0,200,1,1,40,24,3,0")
        entries.add("minecraft:overworld,*,minecraft:flint,0,250,1,1,1,256,0,0")
        entries.add("minecraft:overworld,*,minecraft:gunpowder,0,400,1,1,40,64,0,0")
        entries.add("minecraft:overworld,*,minecraft:bone,0,400,1,1,1,64,0,0")
        entries.add("minecraft:overworld,*,minecraft:coal,0,500,1,3,1,100,1,150")
        entries.add("minecraft:overworld,*,minecraft:redstone,0,500,1,3,20,15,2,150")
        entries.add("minecraft:overworld,*,minecraft:iron_ore,0,350,1,2,1,64,2,100")
        entries.add("minecraft:overworld,*,shincolle:abyss_polymetal,0,350,1,3,1,64,2,100")
        entries.add("minecraft:overworld,*,minecraft:gold_ore,0,100,1,1,30,32,2,100")
        entries.add("minecraft:overworld,*,minecraft:lapis_lazuli,0,200,1,3,30,30,2,150")
        entries.add("minecraft:overworld,*,minecraft:diamond,0,50,1,1,60,16,3,100")
        entries.add("minecraft:overworld,*,minecraft:emerald,0,80,1,1,40,32,3,100")
        entries.add("minecraft:overworld,*,shincolle:marriagering,0,25,1,1,1,16,3,0")

        entries.add("minecraft:overworld,minecraft:warm_ocean,minecraft:prismarine_shard,0,500,1,4,30,128,0,0")
        entries.add("minecraft:overworld,minecraft:warm_ocean,minecraft:prismarine_crystals,0,200,1,3,60,128,2,100")
        entries.add("minecraft:overworld,minecraft:warm_ocean,shincolle:abyss_polymetal,0,500,1,3,1,64,2,100")
        entries.add("minecraft:overworld,minecraft:warm_ocean,minecraft:sponge,0,200,1,1,80,128,0,100")
        entries.add("minecraft:overworld,minecraft:deep_ocean,minecraft:prismarine_shard,0,500,1,4,30,128,0,0")
        entries.add("minecraft:overworld,minecraft:deep_ocean,minecraft:prismarine_crystals,0,200,1,3,60,128,2,100")
        entries.add("minecraft:overworld,minecraft:deep_ocean,shincolle:abyss_polymetal,0,500,1,3,1,64,2,100")
        entries.add("minecraft:overworld,minecraft:deep_ocean,minecraft:sponge,0,200,1,1,80,128,0,100")
        entries.add("minecraft:overworld,minecraft:mushroom_fields,minecraft:clay_ball,0,500,1,4,30,128,0,0")
        entries.add("minecraft:overworld,minecraft:mushroom_fields,minecraft:mycelium,0,500,1,1,50,128,0,0")
        entries.add("minecraft:overworld,minecraft:frozen_ocean,minecraft:packed_ice,0,1000,1,4,1,256,2,0")
        entries.add("minecraft:overworld,minecraft:deep_frozen_ocean,minecraft:packed_ice,0,1000,1,4,1,256,2,0")
        entries.add("minecraft:overworld,minecraft:frozen_river,minecraft:packed_ice,0,1000,1,4,1,256,2,0")

        entries.add("minecraft:the_nether,*,minecraft:netherrack,0,4500,1,4,1,256,0,0")
        entries.add("minecraft:the_nether,*,minecraft:nether_bricks,0,1000,1,1,1,256,0,0")
        entries.add("minecraft:the_nether,*,minecraft:soul_sand,0,1000,1,1,1,256,0,0")
        entries.add("minecraft:the_nether,*,minecraft:gravel,0,1000,1,1,1,256,0,0")
        entries.add("minecraft:the_nether,*,minecraft:magma_block,0,500,1,1,40,256,3,0")
        entries.add("minecraft:the_nether,*,minecraft:flint,0,500,1,1,1,256,0,0")
        entries.add("minecraft:the_nether,*,shincolle:marriagering,0,50,1,1,1,256,3,0")
        entries.add("minecraft:the_nether,*,minecraft:quartz,0,1000,1,3,1,256,2,150")
        entries.add("minecraft:the_nether,*,minecraft:glowstone_dust,0,500,1,2,1,256,0,100")
        entries.add("minecraft:the_nether,*,minecraft:ghast_tear,0,50,1,1,90,256,3,100")
        entries.add("minecraft:the_nether,*,minecraft:blaze_rod,0,80,1,1,60,256,3,100")

        entries.add("minecraft:the_end,*,minecraft:end_stone,0,4000,1,4,1,256,0,0")
        entries.add("minecraft:the_end,*,minecraft:ender_pearl,0,200,1,1,40,256,3,100")
        entries.add("minecraft:the_end,*,minecraft:chorus_fruit,0,200,1,3,60,256,3,100")
        entries.add("minecraft:the_end,*,shincolle:marriagering,0,25,1,1,1,256,3,0")
        return entries
    }

    internal fun defaultCustomSoundRates(): MutableList<String?> {
        val entries: MutableList<String?> = ArrayList<String?>()
        entries.add("54,25,0,25,0,50,0,50,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0")
        entries.add("56,50,50,50,100,0,0,50,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0")
        entries.add("60,25,50,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0")
        entries.add("62,0,35,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0")
        return entries
    }

    internal fun isValidCustomSoundRate(rawEntry: String): Boolean {
        val parts =
            rawEntry.replace("\\s".toRegex(), "").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (parts.size != 33) {
            return false
        }

        try {
            parts[0].toInt()
            for (i in 1..<parts.size) {
                val percent = parts[i].toInt()
                if (percent < 0 || percent > 100) {
                    return false
                }
            }
            return true
        } catch (exception: NumberFormatException) {
            return false
        }
    }

    internal fun parseCustomSoundRates(rawEntries: MutableList<out String?>?): MutableMap<Int?, EnumMap<ShipCustomSoundType, Float?>?> {
        if (rawEntries == null || rawEntries.isEmpty()) {
            return mutableMapOf<Int?, EnumMap<ShipCustomSoundType, Float?>?>()
        }

        val parsed: MutableMap<Int?, EnumMap<ShipCustomSoundType, Float?>?> =
            HashMap<Int?, EnumMap<ShipCustomSoundType, Float?>?>()
        val soundTypes: Array<ShipCustomSoundType> = ShipCustomSoundType.entries.toTypedArray()

        for (rawEntry in rawEntries) {
            if (rawEntry == null || rawEntry.isBlank()) {
                continue
            }

            val parts =
                rawEntry.replace("\\s".toRegex(), "").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size != soundTypes.size + 1) {
                continue
            }

            try {
                val shipClass = parts[0].toInt()
                val rates = EnumMap<ShipCustomSoundType, Float?>(ShipCustomSoundType::class.java)
                for (i in soundTypes.indices) {
                    val percent = parts[i + 1].toInt()
                    if (percent > 0) {
                        rates.put(soundTypes[i], min(100, percent) / 100.0f)
                    }
                }
                if (!rates.isEmpty()) {
                    parsed.put(shipClass, rates)
                }
            } catch (ignored: NumberFormatException) {
            }
        }

        return Collections.unmodifiableMap<Int?, EnumMap<ShipCustomSoundType, Float?>?>(parsed)
    }

    internal fun defaultLootEntries(): MutableList<String?> {
        val entries: MutableList<String?> = ArrayList<String?>()
        entries.add("0,shincolle:grudge,0,1,100,10,15")
        entries.add("0,shincolle:destroyer_i_spawn_egg,0,2,100,1,1")
        entries.add("0,shincolle:ammo,0,1,100,5,8")

        entries.add("1,shincolle:grudge,0,1,100,5,8")
        entries.add("1,shincolle:destroyer_i_spawn_egg,0,1,100,1,1")
        entries.add("1,shincolle:ammo,0,1,100,2,3")
        entries.add("1,shincolle:instantconmat,0,1,100,3,5")

        entries.add("2,shincolle:marriagering,0,4,70,1,1")
        entries.add("2,shincolle:trainingbook,0,4,80,1,3")
        entries.add("2,shincolle:destroyer_i_spawn_egg,0,3,100,1,1")
        entries.add("2,shincolle:destroyer_ro_spawn_egg,0,3,100,1,1")
        entries.add("2,shincolle:destroyer_akatsuki_spawn_egg,0,1,80,1,1")
        entries.add("2,shincolle:destroyer_hibiki_spawn_egg,0,1,80,1,1")

        entries.add("3,shincolle:instantconmat,0,20,100,10,20")
        entries.add("3,shincolle:abyssium,0,10,100,5,10")
        entries.add("3,shincolle:polymetal,0,10,100,5,10")
        entries.add("3,shincolle:destroyer_i_spawn_egg,0,5,100,1,1")

        entries.add("4,shincolle:trainingbook,0,6,80,1,3")
        entries.add("4,shincolle:marriagering,0,6,70,1,1")
        entries.add("4,shincolle:destroyer_i_spawn_egg,0,3,100,1,1")
        entries.add("4,shincolle:destroyer_ro_spawn_egg,0,3,100,1,1")
        entries.add("4,shincolle:equipcannon,-1,8,100,1,1")
        entries.add("4,shincolle:equipairplane,-1,8,100,1,1")
        entries.add("4,shincolle:equiptorpedo,-1,8,100,1,1")

        entries.add("5,shincolle:trainingbook,0,6,80,1,3")
        entries.add("5,shincolle:marriagering,0,6,70,1,1")
        entries.add("5,shincolle:destroyer_i_spawn_egg,0,3,100,1,1")
        entries.add("5,shincolle:destroyer_ro_spawn_egg,0,3,100,1,1")
        entries.add("5,shincolle:equipcannon,-1,8,100,1,1")
        entries.add("5,shincolle:equipairplane,-1,8,100,1,1")
        entries.add("5,shincolle:equiptorpedo,-1,8,100,1,1")

        entries.add("6,shincolle:marriagering,0,4,70,1,1")
        entries.add("6,shincolle:destroyer_ro_spawn_egg,0,2,100,1,1")
        entries.add("6,shincolle:destroyer_akatsuki_spawn_egg,0,1,80,1,1")
        entries.add("6,shincolle:destroyer_hibiki_spawn_egg,0,1,80,1,1")

        entries.add("7,shincolle:trainingbook,0,4,80,1,3")
        entries.add("7,shincolle:instantconmat,0,4,100,10,12")
        entries.add("7,shincolle:marriagering,0,4,70,1,1")
        entries.add("7,shincolle:abyssium,0,4,100,5,15")
        entries.add("7,shincolle:polymetal,0,4,100,5,15")

        entries.add("8,shincolle:trainingbook,0,6,80,1,3")
        entries.add("8,shincolle:marriagering,0,6,70,1,1")
        entries.add("8,shincolle:destroyer_i_spawn_egg,0,3,100,1,1")
        entries.add("8,shincolle:destroyer_ro_spawn_egg,0,3,100,1,1")
        entries.add("8,shincolle:equipcannon,-1,8,100,1,1")
        entries.add("8,shincolle:equipairplane,-1,8,100,1,1")
        entries.add("8,shincolle:equiptorpedo,-1,8,100,1,1")

        entries.add("9,shincolle:trainingbook,0,6,80,1,3")
        entries.add("9,shincolle:marriagering,0,6,70,1,1")
        entries.add("9,shincolle:destroyer_i_spawn_egg,0,3,100,1,1")
        entries.add("9,shincolle:destroyer_ro_spawn_egg,0,3,100,1,1")
        entries.add("9,shincolle:equipcannon,-1,8,100,1,1")
        entries.add("9,shincolle:equipairplane,-1,8,100,1,1")
        entries.add("9,shincolle:equiptorpedo,-1,8,100,1,1")
        return entries
    }

    internal fun isValidMiningEntry(line: String): Boolean {
        val parts = line.replace(" ", "").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (parts.size != 11) return false
        if (parts[2].isBlank()) return false
        try {
            parts[3].toInt()
            parts[4].toInt()
            parts[5].toInt()
            parts[6].toInt()
            parts[7].toInt()
            parts[8].toInt()
            parts[9].toInt()
            parts[10].toInt()
            return true
        } catch (ex: NumberFormatException) {
            return false
        }
    }

    internal fun isValidLootEntry(line: String): Boolean {
        val parts = line.replace(" ", "").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (parts.size != 7 || parts[1].isBlank()) return false
        try {
            parts[0].toInt()
            parts[2].toInt()
            parts[3].toInt()
            parts[4].toInt()
            parts[5].toInt()
            parts[6].toInt()
            return true
        } catch (ex: NumberFormatException) {
            return false
        }
    }

    private fun parseMiningEntries(rawEntries: List<out String?>?): MutableList<MiningEntry?> {
        val parsed: MutableList<MiningEntry?> = ArrayList<MiningEntry?>()
        if (rawEntries == null) return parsed
        for (raw in rawEntries) {
            if (raw == null) continue
            val parts = raw.replace(" ", "").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size != 11) continue
            val dimensionId = parseDimensionId(parts[0])
            val dimensionPath = parts[0]
            val biomeId = parseBiomeId(parts[1])
            val biomePath = parts[1]
            try {
                val itemMeta = parts[3].toInt()
                val item = resolveConfigItem(parts[2], itemMeta, Items.AIR)
                if (item === Items.AIR) continue
                parsed.add(
                    MiningEntry(
                        dimensionId,
                        dimensionPath,
                        biomeId,
                        biomePath,
                        item,
                        itemMeta,
                        max(1, parts[4].toInt()),
                        max(1, parts[5].toInt()),
                        max(1, parts[6].toInt()),
                        parts[7].toInt(),
                        parts[8].toInt(),
                        max(0, parts[9].toInt()),
                        parts[10].toInt() * 0.01f
                    )
                )
            } catch (ignored: NumberFormatException) {
            }
        }
        return List.copyOf<MiningEntry?>(parsed)
    }

    private fun parseLootEntries(rawEntries: List<out String?>?): MutableList<LootEntry?> {
        val parsed: MutableList<LootEntry?> = ArrayList<LootEntry?>()
        if (rawEntries == null) return parsed
        for (raw in rawEntries) {
            if (raw == null) continue
            val parts = raw.replace(" ", "").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size != 7) continue
            try {
                val itemMeta = parts[2].toInt()
                val item = resolveConfigItem(parts[1], itemMeta, Items.AIR)
                if (item === Items.AIR) continue
                parsed.add(
                    LootEntry(
                        parts[0].toInt(),
                        item,
                        itemMeta,
                        max(1, parts[3].toInt()),
                        parts[4].toInt() * 0.01f,
                        max(1, parts[5].toInt()),
                        max(1, parts[6].toInt())
                    )
                )
            } catch (ignored: NumberFormatException) {
            }
        }
        return List.copyOf<LootEntry?>(parsed)
    }

    private fun parseDimensionId(value: String): Int {
        return when (value) {
            "999999", "*" -> 999999
            "0", "minecraft:overworld" -> 0
            "-1", "minecraft:the_nether" -> -1
            "1", "minecraft:the_end" -> 1
            else -> Int.MIN_VALUE
        }
    }

    private fun parseBiomeId(value: String): Int {
        return when (value) {
            "-999999", "*" -> -999999
            else -> {
                try {
                    value.toInt()
                } catch (ex: NumberFormatException) {
                    Int.MIN_VALUE
                }
            }
        }
    }

    private fun resolveConfigItem(rawId: String, itemMeta: Int, fallback: Item?): Item? {
        val mappedId = mapLegacyConfigItemId(rawId, itemMeta)
        val id = ResourceLocation.tryParse(mappedId)
        if (id == null) {
            return fallback
        }
        return BuiltInRegistries.ITEM.getOptional(id).orElse(fallback)
    }

    private fun mapLegacyConfigItemId(rawId: String, itemMeta: Int): String {
        val id = rawId.trim { it <= ' ' }.lowercase()
        return when (id) {
            "shincolle:abyssmetal" -> if (itemMeta == 1) "shincolle:abyss_polymetal" else "shincolle:abyss_metal"
            "shincolle:blockabyssium" -> "shincolle:abyssium"
            "shincolle:blockpolymetal" -> "shincolle:polymetal"
            "shincolle:blockpolymetalgravel" -> "shincolle:polymetal_gravel"
            "shincolle:blockpolymetalore" -> "shincolle:polymetal_ore"
            "minecraft:stone" -> when (itemMeta) {
                1 -> "minecraft:granite"
                3 -> "minecraft:diorite"
                5 -> "minecraft:andesite"
                else -> id
            }

            "minecraft:dye" -> if (itemMeta == 4) "minecraft:lapis_lazuli" else id
            "minecraft:magma" -> "minecraft:magma_block"
            "minecraft:nether_brick" -> "minecraft:nether_bricks"
            else -> id
        }
    }

    @JvmRecord
    data class MiningEntry(
        @JvmField val dimensionId: Int,
        @JvmField val dimensionPath: String?,
        @JvmField val biomeId: Int,
        @JvmField val biomePath: String?,
        @JvmField val item: Item?,
        @JvmField val itemMeta: Int,
        @JvmField val weight: Int,
        @JvmField val min: Int,
        @JvmField val max: Int,
        @JvmField val minShipLevel: Int,
        @JvmField val maxY: Int,
        @JvmField val minToolLevel: Int,
        @JvmField val enchantFactor: Float
    )

    @JvmRecord
    data class LootEntry(
        @JvmField val chestId: Int,
        @JvmField val item: Item?,
        @JvmField val itemMeta: Int,
        @JvmField val weight: Int,
        @JvmField val chance: Float,
        @JvmField val min: Int,
        @JvmField val max: Int
    )

    enum class ShipCustomSoundType(configKey: String, soundPath: String) {
        IDLE("idle", "ship-idle"),
        ATTACK("attack", "ship-hit"),
        HURT("hurt", "ship-hurt"),
        DEAD("dead", "ship-death"),
        MARRY("marry", "ship-marry"),
        KNOCKBACK("knockback", "ship-knockback"),
        ITEM("item", "ship-item"),
        FEED("feed", "ship-feed"),
        TIMEKEEP00("time0", "ship-time0"),
        TIMEKEEP01("time1", "ship-time1"),
        TIMEKEEP02("time2", "ship-time2"),
        TIMEKEEP03("time3", "ship-time3"),
        TIMEKEEP04("time4", "ship-time4"),
        TIMEKEEP05("time5", "ship-time5"),
        TIMEKEEP06("time6", "ship-time6"),
        TIMEKEEP07("time7", "ship-time7"),
        TIMEKEEP08("time8", "ship-time8"),
        TIMEKEEP09("time9", "ship-time9"),
        TIMEKEEP10("time10", "ship-time10"),
        TIMEKEEP11("time11", "ship-time11"),
        TIMEKEEP12("time12", "ship-time12"),
        TIMEKEEP13("time13", "ship-time13"),
        TIMEKEEP14("time14", "ship-time14"),
        TIMEKEEP15("time15", "ship-time15"),
        TIMEKEEP16("time16", "ship-time16"),
        TIMEKEEP17("time17", "ship-time17"),
        TIMEKEEP18("time18", "ship-time18"),
        TIMEKEEP19("time19", "ship-time19"),
        TIMEKEEP20("time20", "ship-time20"),
        TIMEKEEP21("time21", "ship-time21"),
        TIMEKEEP22("time22", "ship-time22"),
        TIMEKEEP23("time23", "ship-time23");

        private val configKey: String?
        private val soundPath: String?

        init {
            this.configKey = configKey
            this.soundPath = soundPath
        }

        fun configKey(): String? {
            return this.configKey
        }

        fun soundPath(): String? {
            return this.soundPath
        }

        companion object {
            @JvmStatic
            fun timeKeeping(hour: Int): ShipCustomSoundType? {
                return ShipCustomSoundType.entries[8 + Math.floorMod(hour, 24)]
            }
        }
    }
}
