@file:Suppress("DEPRECATION", "UNCHECKED_CAST")

package org.trp.shincolle

import net.neoforged.neoforge.common.ModConfigSpec
import net.neoforged.neoforge.common.ModConfigSpec.IntValue
import java.util.function.Predicate

internal object ConfigSpecBuilder {
    fun buildCommonSpec() {
        Config.BUILDER.comment("Ship EXP and level settings").push("ship_exp")

        Config.SHIP_EXP_MODIFIER = Config.BUILDER
            .comment("EXP required for next level = currentLevel * value + value")
            .defineInRange("expModifier", Config.shipExpModifier, 1, 10000)

        Config.SHIP_EXP_GAIN_MELEE = Config.BUILDER
            .comment("EXP gained when ship performs melee attack")
            .defineInRange("expGainMelee", Config.shipExpGainMelee, 0, 10000)

        Config.SHIP_EXP_GAIN_KILL = Config.BUILDER
            .comment("EXP gained when ship kills an enemy")
            .defineInRange("expGainKill", Config.shipExpGainKill, 0, 10000)

        Config.SHIP_EXP_GAIN_LIGHT_ATTACK = Config.BUILDER
            .comment("EXP gained when ship performs light ammo attack")
            .defineInRange("expGainLightAttack", Config.shipExpGainLightAttack, 0, 10000)

        Config.SHIP_EXP_GAIN_HEAVY_ATTACK = Config.BUILDER
            .comment("EXP gained when ship performs heavy ammo attack")
            .defineInRange("expGainHeavyAttack", Config.shipExpGainHeavyAttack, 0, 10000)

        Config.SHIP_EXP_GAIN_LIGHT_AIRCRAFT = Config.BUILDER
            .comment("EXP gained when ship launches light aircraft attack")
            .defineInRange("expGainLightAircraft", Config.shipExpGainLightAircraft, 0, 10000)

        Config.SHIP_EXP_GAIN_HEAVY_AIRCRAFT = Config.BUILDER
            .comment("EXP gained when ship launches heavy aircraft attack")
            .defineInRange("expGainHeavyAircraft", Config.shipExpGainHeavyAircraft, 0, 10000)

        Config.SHIP_MAX_LEVEL_NORMAL = Config.BUILDER
            .comment("Max ship level for non-married ships")
            .defineInRange("maxLevelNormal", Config.shipMaxLevelNormal, 1, 150)

        Config.SHIP_MAX_LEVEL_MARRIED = Config.BUILDER
            .comment("Max ship level for married ships")
            .defineInRange("maxLevelMarried", Config.shipMaxLevelMarried, 1, 150)

        Config.TRAINING_BOOK_LEVEL_MIN = Config.BUILDER
            .comment("Training book minimum level gain")
            .defineInRange("trainingBookLevelMin", Config.trainingBookLevelMin, 1, 50)

        Config.TRAINING_BOOK_LEVEL_MAX = Config.BUILDER
            .comment("Training book maximum level gain")
            .defineInRange("trainingBookLevelMax", Config.trainingBookLevelMax, 1, 50)

        Config.BUILDER.pop()

        Config.BUILDER.comment("Debug and diagnostics settings").push("debug")
        Config.DEBUG_LOGGING = Config.BUILDER
            .comment("Enable verbose ShinColle debug diagnostics. Keep disabled during normal gameplay.")
            .define("debugLogging", Config.debugLogging)
        Config.DEBUG_PERFORMANCE_LOGGING = Config.BUILDER
            .comment("Enable ShinColle performance diagnostics for slow server ticks and timed module/function traces.")
            .define("debugPerformanceLogging", Config.debugPerformanceLogging)
        Config.DEBUG_PERF_SLOW_SHIP_TICK_MS = Config.BUILDER
            .comment("Log a ship server tick when ShinColle ship logic takes at least this many milliseconds.")
            .defineInRange("debugPerfSlowShipTickMs", Config.debugPerfSlowShipTickMs, 1, 1000)
        Config.DEBUG_PERF_SLOW_TASK_TICK_MS = Config.BUILDER
            .comment("Log a ship task update when task automation takes at least this many milliseconds.")
            .defineInRange("debugPerfSlowTaskTickMs", Config.debugPerfSlowTaskTickMs, 1, 1000)
        Config.DEBUG_PERF_SLOW_BLOCK_ENTITY_TICK_MS = Config.BUILDER
            .comment("Log a ShinColle block entity tick when it takes at least this many milliseconds.")
            .defineInRange("debugPerfSlowBlockEntityTickMs", Config.debugPerfSlowBlockEntityTickMs, 1, 1000)
        Config.DEBUG_PERF_SLOW_PROJECTILE_TICK_MS = Config.BUILDER
            .comment("Log aircraft/projectile server logic when it takes at least this many milliseconds.")
            .defineInRange("debugPerfSlowProjectileTickMs", Config.debugPerfSlowProjectileTickMs, 1, 1000)
        Config.DEBUG_PERF_SLOW_SERVER_TICK_MS = Config.BUILDER
            .comment("Log per-server-tick ShinColle aggregate time when it reaches this many milliseconds.")
            .defineInRange("debugPerfSlowServerTickMs", Config.debugPerfSlowServerTickMs, 1, 1000)
        Config.DEBUG_PERF_MIN_LOG_INTERVAL_TICKS = Config.BUILDER
            .comment("Minimum ticks between repeated performance logs for the same object and trace key.")
            .defineInRange("debugPerfMinLogIntervalTicks", Config.debugPerfMinLogIntervalTicks, 1, 1200)
        Config.BUILDER.pop()

        Config.BUILDER.comment("Ship interaction feedback settings").push("ship_interaction")

        Config.MODERN_KIT_NOTIFY_WHEN_MAXED = Config.BUILDER
            .comment("Show a player-facing message when a ship can no longer gain modernization bonuses")
            .define("modernKitNotifyWhenMaxed", Config.modernKitNotifyWhenMaxed)

        Config.MODERN_KIT_NOTIFY_WHEN_MAXED_ACTION_BAR = Config.BUILDER
            .comment("Show the maxed modernization message in the action bar instead of chat")
            .define("modernKitNotifyWhenMaxedActionBar", Config.modernKitNotifyWhenMaxedActionBar)

        Config.SHIP_FREEZE_WHEN_GUI_OPEN = Config.BUILDER
            .comment("Freeze ship movement when its GUI is open. Ship can still attack in place.")
            .define("freezeWhenGuiOpen", true)

        Config.ENABLE_FIRING_LINE_CHECK = Config.BUILDER
            .comment("Check for nearby blocks before firing heavy attacks. Prevents self-damage from missiles exploding on walls.")
            .define("enableFiringLineCheck", Config.enableFiringLineCheck)
        Config.CRUISE_SPEED_FACTOR = Config.BUILDER
            .comment("Movement speed multiplier for all ships. Higher = faster. (default: 0.3)")
            .defineInRange("cruiseSpeedFactor", 0.3, 0.05, 5.0)

        Config.SHIP_BUFF_DURATION = Config.BUILDER
            .comment("Duration (ticks) for ship-granted owner buffs like night vision. 600 = 30 seconds.")
            .defineInRange("buffDuration", 600, 20, 72000)

        Config.BUILDER.pop()

        Config.BUILDER.comment("Grudge/Fuel consumption settings").push("fuel")

        Config.FUEL_DECAY_INTERVAL = Config.BUILDER
            .comment("Interval in ticks between fuel decay checks")
            .defineInRange("decayInterval", Config.fuelDecayInterval, 1, 10000)

        Config.FUEL_MOVE_DECAY_FACTOR = Config.BUILDER
            .comment("Movement fuel consumption factor (consumption = distance * factor)")
            .defineInRange("moveDecayFactor", Config.fuelMoveDecayFactor, 0, 1000)

        Config.FUEL_CONSUME_DD = Config.BUILDER.defineInRange("consumeDD", Config.fuelConsumeDD, 0, 1000)
        Config.FUEL_CONSUME_CL = Config.BUILDER.defineInRange("consumeCL", Config.fuelConsumeCL, 0, 1000)
        Config.FUEL_CONSUME_CA = Config.BUILDER.defineInRange("consumeCA", Config.fuelConsumeCA, 0, 1000)
        Config.FUEL_CONSUME_CAV = Config.BUILDER.defineInRange("consumeCAV", Config.fuelConsumeCAV, 0, 1000)
        Config.FUEL_CONSUME_CLT = Config.BUILDER.defineInRange("consumeCLT", Config.fuelConsumeCLT, 0, 1000)
        Config.FUEL_CONSUME_CVL = Config.BUILDER.defineInRange("consumeCVL", Config.fuelConsumeCVL, 0, 1000)
        Config.FUEL_CONSUME_CV = Config.BUILDER.defineInRange("consumeCV", Config.fuelConsumeCV, 0, 1000)
        Config.FUEL_CONSUME_BB = Config.BUILDER.defineInRange("consumeBB", Config.fuelConsumeBB, 0, 1000)
        Config.FUEL_CONSUME_BBV = Config.BUILDER.defineInRange("consumeBBV", Config.fuelConsumeBBV, 0, 1000)
        Config.FUEL_CONSUME_SS = Config.BUILDER.defineInRange("consumeSS", Config.fuelConsumeSS, 0, 1000)
        Config.FUEL_CONSUME_AP = Config.BUILDER.defineInRange("consumeAP", Config.fuelConsumeAP, 0, 1000)

        Config.FUEL_CONSUME_ACTION_LIGHT = Config.BUILDER.defineInRange("consumeActionLight", Config.fuelConsumeActionLight, 0, 1000)
        Config.FUEL_CONSUME_ACTION_HEAVY = Config.BUILDER.defineInRange("consumeActionHeavy", Config.fuelConsumeActionHeavy, 0, 1000)
        Config.FUEL_CONSUME_ACTION_LIGHT_AIRCRAFT =
            Config.BUILDER.defineInRange("consumeActionLightAircraft", Config.fuelConsumeActionLightAircraft, 0, 1000)
        Config.FUEL_CONSUME_ACTION_HEAVY_AIRCRAFT =
            Config.BUILDER.defineInRange("consumeActionHeavyAircraft", Config.fuelConsumeActionHeavyAircraft, 0, 1000)

        Config.BUILDER.pop()

        Config.BUILDER.comment("Task Automation settings").push("task")
        Config.TICK_FISHING_MIN = Config.BUILDER.defineInRange("tickFishingMin", Config.tickFishingMin, 1, 10000)
        Config.TICK_FISHING_MAX = Config.BUILDER.defineInRange("tickFishingMax", Config.tickFishingMax, 1, 10000)
        Config.TICK_MINING_MIN = Config.BUILDER.defineInRange("tickMiningMin", Config.tickMiningMin, 1, 10000)
        Config.TICK_MINING_MAX = Config.BUILDER.defineInRange("tickMiningMax", Config.tickMiningMax, 1, 10000)
        Config.TASK_ENABLE_COOKING = Config.BUILDER.define("enableCooking", Config.taskEnable[0])
        Config.TASK_ENABLE_FISHING = Config.BUILDER.define("enableFishing", Config.taskEnable[1])
        Config.TASK_ENABLE_MINING = Config.BUILDER.define("enableMining", Config.taskEnable[2])
        Config.TASK_ENABLE_CRAFTING = Config.BUILDER.define("enableCrafting", Config.taskEnable[3])
        Config.SMALL_SHIPYARD_POWER_MAX = Config.BUILDER
            .comment("Small shipyard max fuel storage")
            .defineInRange("smallShipyardPowerMax", Config.smallShipyardPowerMax, 1, 100000000)
        Config.SMALL_SHIPYARD_BUILD_SPEED = Config.BUILDER
            .comment("Small shipyard build progress per tick while active")
            .defineInRange("smallShipyardBuildSpeed", Config.smallShipyardBuildSpeed, 1, 1000000)
        Config.SMALL_SHIPYARD_INSTANT_TICKS = Config.BUILDER
            .comment("Build ticks skipped per instant construction material in the small shipyard")
            .defineInRange("smallShipyardInstantTicks", Config.smallShipyardInstantTicks, 0, 1000000)
        Config.SMALL_SHIPYARD_FUEL_MAGNIFICATION = Config.BUILDER
            .comment("Small shipyard fuel magnification multiplier")
            .defineInRange("smallShipyardFuelMagnification", Config.smallShipyardFuelMagnification.toDouble(), 0.0, 1000.0)
        Config.LARGE_SHIPYARD_POWER_MAX = Config.BUILDER
            .comment("Large shipyard max fuel storage")
            .defineInRange("largeShipyardPowerMax", Config.largeShipyardPowerMax, 1, 100000000)
        Config.LARGE_SHIPYARD_BUILD_SPEED = Config.BUILDER
            .comment("Large shipyard build progress per tick while active")
            .defineInRange("largeShipyardBuildSpeed", Config.largeShipyardBuildSpeed, 1, 1000000)
        Config.LARGE_SHIPYARD_INSTANT_TICKS = Config.BUILDER
            .comment("Build ticks skipped per instant construction material in the large shipyard")
            .defineInRange("largeShipyardInstantTicks", Config.largeShipyardInstantTicks, 0, 1000000)
        Config.LARGE_SHIPYARD_FUEL_MAGNIFICATION = Config.BUILDER
            .comment("Large shipyard fuel magnification multiplier")
            .defineInRange("largeShipyardFuelMagnification", Config.largeShipyardFuelMagnification.toDouble(), 0.0, 1000.0)
        Config.RING_ABILITY_WATER_BREATHING = Config.BUILDER
            .comment("Married ship count needed for passive water breathing, negative disables")
            .defineInRange("ringAbilityWaterBreathing", Config.ringAbilityWaterBreathing, -1, 1000)
        Config.RING_ABILITY_SWIM_FLIGHT = Config.BUILDER
            .comment("Legacy swim flight threshold. Grants temporary flight while the active ring owner is in water, negative disables")
            .defineInRange("ringAbilitySwimFlight", Config.ringAbilitySwimFlight, -1, 1000)
        Config.RING_ABILITY_UNDERWATER_DIG_CAP = Config.BUILDER
            .comment("Maximum married ship count contributing to underwater dig speed, 0 disables")
            .defineInRange("ringAbilityUnderwaterDigCap", Config.ringAbilityUnderwaterDigCap, 0, 1000)
        Config.RING_ABILITY_UNDERWATER_FOG_CAP = Config.BUILDER
            .comment("Legacy underwater fog reduction threshold. 0 removes water fog, positive values scale fog reduction by married ship count, negative disables")
            .defineInRange("ringAbilityUnderwaterFogCap", Config.ringAbilityUnderwaterFogCap, -1, 1000)
        Config.RING_ABILITY_FIRE_IMMUNITY = Config.BUILDER
            .comment("Married ship count needed for active ring fire immunity, negative disables")
            .defineInRange("ringAbilityFireImmunity", Config.ringAbilityFireImmunity, -1, 1000)
        Config.DRUM_LIQUID_BASE_RATE = Config.BUILDER
            .comment("Crane liquid transfer base rate per liquid drum in mB/t")
            .defineInRange("drumLiquidBaseRate", Config.drumLiquidBaseRate, 0, 1000000)
        Config.DRUM_LIQUID_ENCHANT_RATE = Config.BUILDER
            .comment("Additional crane liquid transfer rate per enchantment level in mB/t")
            .defineInRange("drumLiquidEnchantRate", Config.drumLiquidEnchantRate, 0, 1000000)
        Config.DRUM_ENERGY_BASE_RATE = Config.BUILDER
            .comment("Crane energy transfer base rate per energy drum in FE/t")
            .defineInRange("drumEnergyBaseRate", Config.drumEnergyBaseRate, 0, 1000000)
        Config.DRUM_ENERGY_ENCHANT_RATE = Config.BUILDER
            .comment("Additional crane energy transfer rate per enchantment level in FE/t")
            .defineInRange("drumEnergyEnchantRate", Config.drumEnergyEnchantRate, 0, 1000000)
        Config.PAIR_DIST_CHEST = Config.BUILDER
            .comment("Max pairing distance between waypoint and chest/crane")
            .defineInRange("pairDistChest", Config.pairDistChest, 0, 64)
        Config.PAIR_DIST_WAYPOINT = Config.BUILDER
            .comment("Max pairing distance between waypoints")
            .defineInRange("pairDistWaypoint", Config.pairDistWaypoint, 0, 64)
        Config.SHIP_CAN_TELEPORT = Config.BUILDER
            .comment("Can ship teleport to owner or guarding position if too far away")
            .define("canTeleport", Config.canTeleport)
        Config.CRANE_TANK_CAPACITY = Config.BUILDER
            .comment("Crane internal fluid tank capacity in mB")
            .defineInRange("craneTankCapacity", Config.craneTankCapacity, 1, 1000000000)
        Config.VOLCORE_POWER_MAX = Config.BUILDER
            .comment("Volcano Core max fuel storage")
            .defineInRange("volCorePowerMax", Config.volCorePowerMax, 1, 100000000)
        Config.VOLCORE_CONSUME_SPEED = Config.BUILDER
            .comment("Volcano Core power consumed every 16 ticks while active")
            .defineInRange("volCoreConsumeSpeed", Config.volCoreConsumeSpeed, 1, 1000000)
        Config.VOLCORE_FUEL_MAGNITUDE = Config.BUILDER
            .comment("Volcano Core fuel value per grudge item")
            .defineInRange("volCoreFuelMagnitude", Config.volCoreFuelMagnitude, 1, 1000000)
        Config.MINING_ENTRIES = Config.BUILDER
            .comment("Mining table entries: dimension, biome, item, meta, weight, min, max, shipLevel, maxY, toolLevel, enchantPercent. Use '*' for all dimensions/biomes or legacy numeric dimension ids like 0/-1/1.")
            .defineList<String?>(
                "miningEntries",
                Config.defaultMiningEntries(),
                Predicate { value: Any? -> value is String && Config.isValidMiningEntry(value) })
        Config.LOOT_ENTRIES = Config.BUILDER
            .comment("Legacy chest loot entries: chestId, item, meta, weight, chancePercent, min, max. chestId: 0 spawn, 1 igloo, 2 dungeon, 3 village, 4 mineshaft, 5 pyramid, 6 jungle temple, 7 nether bridge, 8 stronghold, 9 end city.")
            .defineList<String?>(
                "lootEntries",
                Config.defaultLootEntries(),
                Predicate { value: Any? -> value is String && Config.isValidLootEntry(value) })
        for (i in 0..3) {
            Config.EXP_GAIN_TASK[i] = Config.BUILDER.defineInRange("expGainTask$i", Config.expGainTask[i], 0, 10000)
            Config.CONSUME_GRUDGE_TASK[i] = Config.BUILDER.defineInRange("consumeGrudgeTask$i", Config.consumeGrudgeTask[i], 0, 10000)
        }
        Config.BUILDER.pop()

        Config.BUILDER.comment("Legacy hostile ship spawn/death/drop settings").push("hostile")

        Config.HOSTILE_DROP_GRUDGE_RATE = Config.BUILDER
            .comment("Grudge drop rate for hostile entities (fixed + chance, e.g. 5.5 = 5 guaranteed + 50% for +1)")
            .defineInRange("dropGrudgeRate", Config.hostileDropGrudgeRate.toDouble(), 0.0, 64.0)

        Config.HOSTILE_DEATH_MAX_TICKS = Config.BUILDER
            .comment("Hostile ship death animation ticks before final sink processing")
            .defineInRange("deathMaxTicks", Config.hostileDeathMaxTicks, 0, 3600)

        Config.HOSTILE_DESPAWN_BOSS_TICKS = Config.BUILDER
            .comment("Hostile boss despawn ticks, -1 disables despawn")
            .defineInRange("despawnBossTicks", Config.hostileDespawnBossTicks, -1, 1728000)

        Config.HOSTILE_DESPAWN_MINION_TICKS = Config.BUILDER
            .comment("Hostile minion despawn ticks, -1 disables despawn")
            .defineInRange("despawnMinionTicks", Config.hostileDespawnMinionTicks, -1, 1728000)

        Config.HOSTILE_BOSS_COOLDOWN_TICKS = Config.BUILDER
            .comment("Boss fleet spawn cooldown ticks")
            .defineInRange("bossCooldownTicks", Config.hostileBossCooldownTicks, 20, 1728000)

        Config.HOSTILE_SPAWN_BOSS_COUNT = Config.BUILDER
            .comment("Boss ships per boss fleet spawn")
            .defineInRange("spawnBossCount", Config.hostileSpawnBossCount, 1, 10)

        Config.HOSTILE_SPAWN_MINION_COUNT = Config.BUILDER
            .comment("Minion ships per boss fleet spawn")
            .defineInRange("spawnMinionCount", Config.hostileSpawnMinionCount, 1, 10)

        Config.HOSTILE_SPAWN_REQUIRE_RING = Config.BUILDER
            .comment("Require player inventory to have a marriage ring for regular hostile mob spawns")
            .define("spawnRequireRing", Config.hostileSpawnRequireRing)

        Config.HOSTILE_MOB_SPAWN_MAX = Config.BUILDER
            .comment("Maximum number of non-boss hostile ships loaded in a level for regular spawn checks")
            .defineInRange("mobSpawnMax", Config.hostileMobSpawnMax, 0, 10000)

        Config.HOSTILE_MOB_SPAWN_CHANCE_PERCENT = Config.BUILDER
            .comment("Regular hostile spawn chance percent per check")
            .defineInRange("mobSpawnChancePercent", Config.hostileMobSpawnChancePercent, 0, 100)

        Config.HOSTILE_MOB_SPAWN_GROUPS = Config.BUILDER
            .comment("Regular hostile spawn group count per successful spawn check")
            .defineInRange("mobSpawnGroups", Config.hostileMobSpawnGroups, 1, 16)

        Config.HOSTILE_MOB_SPAWN_GROUP_MIN = Config.BUILDER
            .comment("Minimum hostile ship count per regular spawn group")
            .defineInRange("mobSpawnGroupMin", Config.hostileMobSpawnGroupMin, 1, 16)

        Config.HOSTILE_MOB_SPAWN_GROUP_MAX = Config.BUILDER
            .comment("Maximum hostile ship count per regular spawn group")
            .defineInRange("mobSpawnGroupMax", Config.hostileMobSpawnGroupMax, 1, 16)

        Config.BUILDER.pop()

        Config.SPEC = Config.BUILDER.build()
    }

    fun buildClientSpec() {
        Config.CLIENT_BUILDER.comment("Ship sound and timekeeping settings").push("ship_sound")
        Config.SHIP_CAN_TIMEKEEPING = Config.CLIENT_BUILDER
            .comment("Play ship timekeeping voice every Minecraft hour when enabled in ship GUI")
            .define("canTimeKeeping", Config.canTimeKeeping)
        Config.SHIP_VOLUME_TIMEKEEPING = Config.CLIENT_BUILDER
            .comment("Timekeeping voice volume multiplier")
            .defineInRange("volumeTimeKeeping", Config.volumeTimeKeeping.toDouble(), 0.0, 10.0)
        Config.SHIP_VOLUME_GENERAL = Config.CLIENT_BUILDER
            .comment("General ship voice volume multiplier")
            .defineInRange("volumeShip", Config.volumeShip.toDouble(), 0.0, 10.0)
        Config.SHIP_VOLUME_ATTACK = Config.CLIENT_BUILDER
            .comment("Attack sound volume multiplier")
            .defineInRange("volumeAttack", Config.volumeAttack.toDouble(), 0.0, 10.0)
        Config.CUSTOM_SOUND_RATES = Config.CLIENT_BUILDER
            .comment("Custom ship voice rates: shipClass,idle,attack,hurt,dead,marry,knockback,item,feed,timekeep00~timekeep23. Value is 0-100 percent.")
            .defineList<String?>(
                "customSoundRates",
                Config.defaultCustomSoundRates(),
                Predicate { value: Any? -> value is String && Config.isValidCustomSoundRate(value) })
        Config.CLIENT_BUILDER.pop()

        Config.CLIENT_BUILDER.comment("Client side settings").push("client")
        Config.CLIENT_SCALE_HELD_ITEM = Config.CLIENT_BUILDER
            .comment("Held item scale")
            .defineInRange("scaleHeldItem", 1.0, 0.0, 10.0)
        Config.CLIENT_OFFSET_HELD_ITEM_X = Config.CLIENT_BUILDER
            .comment("Held item offset X")
            .defineInRange("offsetHeldItemX", 0.0, -10.0, 10.0)
        Config.CLIENT_OFFSET_HELD_ITEM_Y = Config.CLIENT_BUILDER
            .comment("Held item offset Y")
            .defineInRange("offsetHeldItemY", 0.0, -10.0, 10.0)
        Config.CLIENT_OFFSET_HELD_ITEM_Z = Config.CLIENT_BUILDER
            .comment("Held item offset Z")
            .defineInRange("offsetHeldItemZ", 0.0, -10.0, 10.0)

        Config.CLIENT_BUILDER.comment("MiSans font settings").push("misans_font")
        Config.USE_MISANS_FONT = Config.CLIENT_BUILDER
            .comment("Use MiSans font for in-game text rendering")
            .define("useMiSansFont", true)
        Config.MISANS_ONLY_LEGACY_LOGS = Config.CLIENT_BUILDER
            .comment("Only apply MiSans font to legacy deep-sea log books (desk book & held item)")
            .define("miSansOnlyForLegacyLogs", true)
        Config.CLIENT_BUILDER.pop()
        Config.CLIENT_BUILDER.pop()

        Config.CLIENT_SPEC = Config.CLIENT_BUILDER.build()
    }
}
