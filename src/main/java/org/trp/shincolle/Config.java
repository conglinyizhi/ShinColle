package org.trp.shincolle;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@EventBusSubscriber(modid = Shincolle.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue SHIP_EXP_MODIFIER;
    private static final ModConfigSpec.IntValue SHIP_EXP_GAIN_MELEE;
    private static final ModConfigSpec.IntValue SHIP_EXP_GAIN_KILL;
    private static final ModConfigSpec.IntValue SHIP_EXP_GAIN_LIGHT_ATTACK;
    private static final ModConfigSpec.IntValue SHIP_EXP_GAIN_HEAVY_ATTACK;
    private static final ModConfigSpec.IntValue SHIP_EXP_GAIN_LIGHT_AIRCRAFT;
    private static final ModConfigSpec.IntValue SHIP_EXP_GAIN_HEAVY_AIRCRAFT;
    private static final ModConfigSpec.IntValue SHIP_MAX_LEVEL_NORMAL;
    private static final ModConfigSpec.IntValue SHIP_MAX_LEVEL_MARRIED;
    private static final ModConfigSpec.IntValue TRAINING_BOOK_LEVEL_MIN;
    private static final ModConfigSpec.IntValue TRAINING_BOOK_LEVEL_MAX;
    private static final ModConfigSpec.BooleanValue DEBUG_LOGGING;
    private static final ModConfigSpec.BooleanValue DEBUG_PERFORMANCE_LOGGING;
    private static final ModConfigSpec.IntValue DEBUG_PERF_SLOW_SHIP_TICK_MS;
    private static final ModConfigSpec.IntValue DEBUG_PERF_SLOW_TASK_TICK_MS;
    private static final ModConfigSpec.IntValue DEBUG_PERF_SLOW_BLOCK_ENTITY_TICK_MS;
    private static final ModConfigSpec.IntValue DEBUG_PERF_SLOW_PROJECTILE_TICK_MS;
    private static final ModConfigSpec.IntValue DEBUG_PERF_SLOW_SERVER_TICK_MS;
    private static final ModConfigSpec.IntValue DEBUG_PERF_MIN_LOG_INTERVAL_TICKS;
    private static final ModConfigSpec.BooleanValue MODERN_KIT_NOTIFY_WHEN_MAXED;
    private static final ModConfigSpec.BooleanValue MODERN_KIT_NOTIFY_WHEN_MAXED_ACTION_BAR;
    private static final ModConfigSpec.IntValue FUEL_DECAY_INTERVAL;
    private static final ModConfigSpec.IntValue FUEL_MOVE_DECAY_FACTOR;
    private static final ModConfigSpec.IntValue FUEL_CONSUME_DD;
    private static final ModConfigSpec.IntValue FUEL_CONSUME_CL;
    private static final ModConfigSpec.IntValue FUEL_CONSUME_CA;
    private static final ModConfigSpec.IntValue FUEL_CONSUME_CAV;
    private static final ModConfigSpec.IntValue FUEL_CONSUME_CLT;
    private static final ModConfigSpec.IntValue FUEL_CONSUME_CVL;
    private static final ModConfigSpec.IntValue FUEL_CONSUME_CV;
    private static final ModConfigSpec.IntValue FUEL_CONSUME_BB;
    private static final ModConfigSpec.IntValue FUEL_CONSUME_BBV;
    private static final ModConfigSpec.IntValue FUEL_CONSUME_SS;
    private static final ModConfigSpec.IntValue FUEL_CONSUME_AP;
    private static final ModConfigSpec.IntValue FUEL_CONSUME_ACTION_LIGHT;
    private static final ModConfigSpec.IntValue FUEL_CONSUME_ACTION_HEAVY;
    private static final ModConfigSpec.IntValue FUEL_CONSUME_ACTION_LIGHT_AIRCRAFT;
    private static final ModConfigSpec.IntValue FUEL_CONSUME_ACTION_HEAVY_AIRCRAFT;
    
    private static final ModConfigSpec.IntValue TICK_FISHING_MIN;
    private static final ModConfigSpec.IntValue TICK_FISHING_MAX;
    private static final ModConfigSpec.IntValue TICK_MINING_MIN;
    private static final ModConfigSpec.IntValue TICK_MINING_MAX;
    private static final ModConfigSpec.BooleanValue TASK_ENABLE_COOKING;
    private static final ModConfigSpec.BooleanValue TASK_ENABLE_FISHING;
    private static final ModConfigSpec.BooleanValue TASK_ENABLE_MINING;
    private static final ModConfigSpec.BooleanValue TASK_ENABLE_CRAFTING;
    private static final ModConfigSpec.IntValue SMALL_SHIPYARD_POWER_MAX;
    private static final ModConfigSpec.IntValue SMALL_SHIPYARD_BUILD_SPEED;
    private static final ModConfigSpec.IntValue SMALL_SHIPYARD_INSTANT_TICKS;
    private static final ModConfigSpec.DoubleValue SMALL_SHIPYARD_FUEL_MAGNIFICATION;
    private static final ModConfigSpec.IntValue LARGE_SHIPYARD_POWER_MAX;
    private static final ModConfigSpec.IntValue LARGE_SHIPYARD_BUILD_SPEED;
    private static final ModConfigSpec.IntValue LARGE_SHIPYARD_INSTANT_TICKS;
    private static final ModConfigSpec.DoubleValue LARGE_SHIPYARD_FUEL_MAGNIFICATION;
    private static final ModConfigSpec.IntValue RING_ABILITY_WATER_BREATHING;
    private static final ModConfigSpec.IntValue RING_ABILITY_SWIM_FLIGHT;
    private static final ModConfigSpec.IntValue RING_ABILITY_UNDERWATER_DIG_CAP;
    private static final ModConfigSpec.IntValue RING_ABILITY_UNDERWATER_FOG_CAP;
    private static final ModConfigSpec.IntValue RING_ABILITY_FIRE_IMMUNITY;
    private static final ModConfigSpec.IntValue DRUM_LIQUID_BASE_RATE;
    private static final ModConfigSpec.IntValue DRUM_LIQUID_ENCHANT_RATE;
    private static final ModConfigSpec.IntValue DRUM_ENERGY_BASE_RATE;
    private static final ModConfigSpec.IntValue DRUM_ENERGY_ENCHANT_RATE;
    private static final ModConfigSpec.IntValue PAIR_DIST_CHEST;
    private static final ModConfigSpec.IntValue PAIR_DIST_WAYPOINT;
    private static final ModConfigSpec.BooleanValue SHIP_CAN_TELEPORT;
    private static final ModConfigSpec.IntValue CRANE_TANK_CAPACITY;
    private static final ModConfigSpec.IntValue VOLCORE_POWER_MAX;
    private static final ModConfigSpec.IntValue VOLCORE_CONSUME_SPEED;
    private static final ModConfigSpec.IntValue VOLCORE_FUEL_MAGNITUDE;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> MINING_ENTRIES;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> LOOT_ENTRIES;
    private static final ModConfigSpec.IntValue[] EXP_GAIN_TASK = new ModConfigSpec.IntValue[4];
    private static final ModConfigSpec.IntValue[] CONSUME_GRUDGE_TASK = new ModConfigSpec.IntValue[4];

        private static final ModConfigSpec.DoubleValue HOSTILE_DROP_GRUDGE_RATE;
        private static final ModConfigSpec.IntValue HOSTILE_DEATH_MAX_TICKS;
        private static final ModConfigSpec.IntValue HOSTILE_DESPAWN_BOSS_TICKS;
        private static final ModConfigSpec.IntValue HOSTILE_DESPAWN_MINION_TICKS;
        private static final ModConfigSpec.IntValue HOSTILE_BOSS_COOLDOWN_TICKS;
        private static final ModConfigSpec.IntValue HOSTILE_SPAWN_BOSS_COUNT;
        private static final ModConfigSpec.IntValue HOSTILE_SPAWN_MINION_COUNT;
        private static final ModConfigSpec.BooleanValue HOSTILE_SPAWN_REQUIRE_RING;
        private static final ModConfigSpec.IntValue HOSTILE_MOB_SPAWN_MAX;
        private static final ModConfigSpec.IntValue HOSTILE_MOB_SPAWN_CHANCE_PERCENT;
        private static final ModConfigSpec.IntValue HOSTILE_MOB_SPAWN_GROUPS;
        private static final ModConfigSpec.IntValue HOSTILE_MOB_SPAWN_GROUP_MIN;
    private static final ModConfigSpec.IntValue HOSTILE_MOB_SPAWN_GROUP_MAX;

    private static final ModConfigSpec.BooleanValue SHIP_CAN_TIMEKEEPING;
    private static final ModConfigSpec.DoubleValue SHIP_VOLUME_TIMEKEEPING;
    private static final ModConfigSpec.DoubleValue SHIP_VOLUME_GENERAL;
    private static final ModConfigSpec.DoubleValue SHIP_VOLUME_ATTACK;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> CUSTOM_SOUND_RATES;

    private static final ModConfigSpec.DoubleValue CLIENT_SCALE_HELD_ITEM;
    private static final ModConfigSpec.DoubleValue CLIENT_OFFSET_HELD_ITEM_X;
    private static final ModConfigSpec.DoubleValue CLIENT_OFFSET_HELD_ITEM_Y;
    private static final ModConfigSpec.DoubleValue CLIENT_OFFSET_HELD_ITEM_Z;

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec CLIENT_SPEC;

    public static int shipExpModifier = 20;
    public static int shipExpGainMelee = 4;
    public static int shipExpGainKill = 8;
    public static int shipExpGainLightAttack = 8;
    public static int shipExpGainHeavyAttack = 24;
    public static int shipExpGainLightAircraft = 16;
    public static int shipExpGainHeavyAircraft = 48;
    public static int shipMaxLevelNormal = 100;
    public static int shipMaxLevelMarried = 150;
    public static int trainingBookLevelMin = 5;
    public static int trainingBookLevelMax = 10;
    public static boolean debugLogging = false;
    public static boolean debugPerformanceLogging = false;
    public static int debugPerfSlowShipTickMs = 10;
    public static int debugPerfSlowTaskTickMs = 5;
    public static int debugPerfSlowBlockEntityTickMs = 5;
    public static int debugPerfSlowProjectileTickMs = 5;
    public static int debugPerfSlowServerTickMs = 40;
    public static int debugPerfMinLogIntervalTicks = 20;
    public static boolean modernKitNotifyWhenMaxed = true;
    public static boolean modernKitNotifyWhenMaxedActionBar = true;
    public static int fuelDecayInterval = 128;
    public static int fuelMoveDecayFactor = 3;
    public static int fuelConsumeDD = 5;
    public static int fuelConsumeCL = 7;
    public static int fuelConsumeCA = 8;
    public static int fuelConsumeCAV = 9;
    public static int fuelConsumeCLT = 8;
    public static int fuelConsumeCVL = 11;
    public static int fuelConsumeCV = 12;
    public static int fuelConsumeBB = 15;
    public static int fuelConsumeBBV = 14;
    public static int fuelConsumeSS = 4;
    public static int fuelConsumeAP = 3;
    public static int fuelConsumeActionLight = 4;
    public static int fuelConsumeActionHeavy = 8;
    public static int fuelConsumeActionLightAircraft = 6;
    public static int fuelConsumeActionHeavyAircraft = 12;
    
    public static int tickFishingMin = 100;
    public static int tickFishingMax = 300;
    public static int tickMiningMin = 100;
    public static int tickMiningMax = 200;
    public static boolean[] taskEnable = {true, true, true, true};
    public static int smallShipyardPowerMax = 460800;
    public static int smallShipyardBuildSpeed = 48;
    public static int smallShipyardInstantTicks = 2400;
    public static float smallShipyardFuelMagnification = 1.0F;
    public static int largeShipyardPowerMax = 1382400;
    public static int largeShipyardBuildSpeed = 48;
    public static int largeShipyardInstantTicks = 1200;
    public static float largeShipyardFuelMagnification = 1.0F;
    public static int ringAbilityWaterBreathing = 0;
    public static int ringAbilitySwimFlight = 6;
    public static int ringAbilityUnderwaterDigCap = 30;
    public static int ringAbilityUnderwaterFogCap = 20;
    public static int ringAbilityFireImmunity = 12;
    public static int drumLiquidBaseRate = 40;
    public static int drumLiquidEnchantRate = 5;
    public static int drumEnergyBaseRate = 400;
    public static int drumEnergyEnchantRate = 100;
    public static int pairDistChest = 16;
    public static int pairDistWaypoint = 48;
    public static boolean canTeleport = true;
    public static int craneTankCapacity = 2048000;
    public static int volCorePowerMax = 9600;
    public static int volCoreConsumeSpeed = 16;
    public static int volCoreFuelMagnitude = 240;
    public static List<MiningEntry> miningEntries = Collections.emptyList();
    public static List<LootEntry> lootEntries = Collections.emptyList();
    public static int[] expGainTask = {10, 10, 20, 5};
    public static int[] consumeGrudgeTask = {5, 5, 20, 10};

        public static float hostileDropGrudgeRate = 1.0F;
        public static int hostileDeathMaxTicks = 400;
        public static int hostileDespawnBossTicks = 12000;
        public static int hostileDespawnMinionTicks = 600;
        public static int hostileBossCooldownTicks = 4800;
        public static int hostileSpawnBossCount = 2;
        public static int hostileSpawnMinionCount = 4;
        public static boolean hostileSpawnRequireRing = true;
        public static int hostileMobSpawnMax = 50;
    public static int hostileMobSpawnChancePercent = 10;
    public static int hostileMobSpawnGroups = 1;
    public static int hostileMobSpawnGroupMin = 1;
    public static int hostileMobSpawnGroupMax = 1;

    public static boolean canTimeKeeping = true;
    public static float volumeTimeKeeping = 1.0F;
    public static float volumeShip = 0.6F;
    public static float volumeAttack = 0.7F;
    public static Map<Integer, EnumMap<ShipCustomSoundType, Float>> customSoundRates = Collections.emptyMap();

    public static float scaleHeldItem = 1.0F;
    public static float offsetHeldItemX = 0.0F;
    public static float offsetHeldItemY = 0.0F;
    public static float offsetHeldItemZ = 0.0F;

    public record MiningEntry(
            int dimensionId,
            String dimensionPath,
            int biomeId,
            String biomePath,
            Item item,
            int itemMeta,
            int weight,
            int min,
            int max,
            int minShipLevel,
            int maxY,
            int minToolLevel,
            float enchantFactor
    ) {}

    public record LootEntry(
            int chestId,
            Item item,
            int itemMeta,
            int weight,
            float chance,
            int min,
            int max
    ) {}

    public enum ShipCustomSoundType {
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

        private final String configKey;
        private final String soundPath;

        ShipCustomSoundType(String configKey, String soundPath) {
            this.configKey = configKey;
            this.soundPath = soundPath;
        }

        public String configKey() {
            return this.configKey;
        }

        public String soundPath() {
            return this.soundPath;
        }

        public static ShipCustomSoundType timeKeeping(int hour) {
            return values()[8 + Math.floorMod(hour, 24)];
        }
    }

    static {
        BUILDER.comment("Ship EXP and level settings").push("ship_exp");

        SHIP_EXP_MODIFIER = BUILDER
                .comment("EXP required for next level = currentLevel * value + value")
                .defineInRange("expModifier", shipExpModifier, 1, 10000);

        SHIP_EXP_GAIN_MELEE = BUILDER
                .comment("EXP gained when ship performs melee attack")
                .defineInRange("expGainMelee", shipExpGainMelee, 0, 10000);

        SHIP_EXP_GAIN_KILL = BUILDER
                .comment("EXP gained when ship kills an enemy")
                .defineInRange("expGainKill", shipExpGainKill, 0, 10000);

        SHIP_EXP_GAIN_LIGHT_ATTACK = BUILDER
                .comment("EXP gained when ship performs light ammo attack")
                .defineInRange("expGainLightAttack", shipExpGainLightAttack, 0, 10000);

        SHIP_EXP_GAIN_HEAVY_ATTACK = BUILDER
                .comment("EXP gained when ship performs heavy ammo attack")
                .defineInRange("expGainHeavyAttack", shipExpGainHeavyAttack, 0, 10000);

        SHIP_EXP_GAIN_LIGHT_AIRCRAFT = BUILDER
                .comment("EXP gained when ship launches light aircraft attack")
                .defineInRange("expGainLightAircraft", shipExpGainLightAircraft, 0, 10000);

        SHIP_EXP_GAIN_HEAVY_AIRCRAFT = BUILDER
                .comment("EXP gained when ship launches heavy aircraft attack")
                .defineInRange("expGainHeavyAircraft", shipExpGainHeavyAircraft, 0, 10000);

        SHIP_MAX_LEVEL_NORMAL = BUILDER
                .comment("Max ship level for non-married ships")
                .defineInRange("maxLevelNormal", shipMaxLevelNormal, 1, 150);

        SHIP_MAX_LEVEL_MARRIED = BUILDER
                .comment("Max ship level for married ships")
                .defineInRange("maxLevelMarried", shipMaxLevelMarried, 1, 150);

        TRAINING_BOOK_LEVEL_MIN = BUILDER
                .comment("Training book minimum level gain")
                .defineInRange("trainingBookLevelMin", trainingBookLevelMin, 1, 50);

        TRAINING_BOOK_LEVEL_MAX = BUILDER
                .comment("Training book maximum level gain")
                .defineInRange("trainingBookLevelMax", trainingBookLevelMax, 1, 50);

        BUILDER.pop();

        BUILDER.comment("Debug and diagnostics settings").push("debug");
        DEBUG_LOGGING = BUILDER
                .comment("Enable verbose ShinColle debug diagnostics. Keep disabled during normal gameplay.")
                .define("debugLogging", debugLogging);
        DEBUG_PERFORMANCE_LOGGING = BUILDER
                .comment("Enable ShinColle performance diagnostics for slow server ticks and timed module/function traces.")
                .define("debugPerformanceLogging", debugPerformanceLogging);
        DEBUG_PERF_SLOW_SHIP_TICK_MS = BUILDER
                .comment("Log a ship server tick when ShinColle ship logic takes at least this many milliseconds.")
                .defineInRange("debugPerfSlowShipTickMs", debugPerfSlowShipTickMs, 1, 1000);
        DEBUG_PERF_SLOW_TASK_TICK_MS = BUILDER
                .comment("Log a ship task update when task automation takes at least this many milliseconds.")
                .defineInRange("debugPerfSlowTaskTickMs", debugPerfSlowTaskTickMs, 1, 1000);
        DEBUG_PERF_SLOW_BLOCK_ENTITY_TICK_MS = BUILDER
                .comment("Log a ShinColle block entity tick when it takes at least this many milliseconds.")
                .defineInRange("debugPerfSlowBlockEntityTickMs", debugPerfSlowBlockEntityTickMs, 1, 1000);
        DEBUG_PERF_SLOW_PROJECTILE_TICK_MS = BUILDER
                .comment("Log aircraft/projectile server logic when it takes at least this many milliseconds.")
                .defineInRange("debugPerfSlowProjectileTickMs", debugPerfSlowProjectileTickMs, 1, 1000);
        DEBUG_PERF_SLOW_SERVER_TICK_MS = BUILDER
                .comment("Log per-server-tick ShinColle aggregate time when it reaches this many milliseconds.")
                .defineInRange("debugPerfSlowServerTickMs", debugPerfSlowServerTickMs, 1, 1000);
        DEBUG_PERF_MIN_LOG_INTERVAL_TICKS = BUILDER
                .comment("Minimum ticks between repeated performance logs for the same object and trace key.")
                .defineInRange("debugPerfMinLogIntervalTicks", debugPerfMinLogIntervalTicks, 1, 1200);
        BUILDER.pop();

        BUILDER.comment("Ship interaction feedback settings").push("ship_interaction");

        MODERN_KIT_NOTIFY_WHEN_MAXED = BUILDER
                .comment("Show a player-facing message when a ship can no longer gain modernization bonuses")
                .define("modernKitNotifyWhenMaxed", modernKitNotifyWhenMaxed);

        MODERN_KIT_NOTIFY_WHEN_MAXED_ACTION_BAR = BUILDER
                .comment("Show the maxed modernization message in the action bar instead of chat")
                .define("modernKitNotifyWhenMaxedActionBar", modernKitNotifyWhenMaxedActionBar);

        BUILDER.pop();

        BUILDER.comment("Grudge/Fuel consumption settings").push("fuel");

        FUEL_DECAY_INTERVAL = BUILDER
                .comment("Interval in ticks between fuel decay checks")
                .defineInRange("decayInterval", fuelDecayInterval, 1, 10000);

        FUEL_MOVE_DECAY_FACTOR = BUILDER
                .comment("Movement fuel consumption factor (consumption = distance * factor)")
                .defineInRange("moveDecayFactor", fuelMoveDecayFactor, 0, 1000);

        FUEL_CONSUME_DD = BUILDER.defineInRange("consumeDD", fuelConsumeDD, 0, 1000);
        FUEL_CONSUME_CL = BUILDER.defineInRange("consumeCL", fuelConsumeCL, 0, 1000);
        FUEL_CONSUME_CA = BUILDER.defineInRange("consumeCA", fuelConsumeCA, 0, 1000);
        FUEL_CONSUME_CAV = BUILDER.defineInRange("consumeCAV", fuelConsumeCAV, 0, 1000);
        FUEL_CONSUME_CLT = BUILDER.defineInRange("consumeCLT", fuelConsumeCLT, 0, 1000);
        FUEL_CONSUME_CVL = BUILDER.defineInRange("consumeCVL", fuelConsumeCVL, 0, 1000);
        FUEL_CONSUME_CV = BUILDER.defineInRange("consumeCV", fuelConsumeCV, 0, 1000);
        FUEL_CONSUME_BB = BUILDER.defineInRange("consumeBB", fuelConsumeBB, 0, 1000);
        FUEL_CONSUME_BBV = BUILDER.defineInRange("consumeBBV", fuelConsumeBBV, 0, 1000);
        FUEL_CONSUME_SS = BUILDER.defineInRange("consumeSS", fuelConsumeSS, 0, 1000);
        FUEL_CONSUME_AP = BUILDER.defineInRange("consumeAP", fuelConsumeAP, 0, 1000);

        FUEL_CONSUME_ACTION_LIGHT = BUILDER.defineInRange("consumeActionLight", fuelConsumeActionLight, 0, 1000);
        FUEL_CONSUME_ACTION_HEAVY = BUILDER.defineInRange("consumeActionHeavy", fuelConsumeActionHeavy, 0, 1000);
        FUEL_CONSUME_ACTION_LIGHT_AIRCRAFT = BUILDER.defineInRange("consumeActionLightAircraft", fuelConsumeActionLightAircraft, 0, 1000);
        FUEL_CONSUME_ACTION_HEAVY_AIRCRAFT = BUILDER.defineInRange("consumeActionHeavyAircraft", fuelConsumeActionHeavyAircraft, 0, 1000);

        BUILDER.pop();

        BUILDER.comment("Task Automation settings").push("task");
        TICK_FISHING_MIN = BUILDER.defineInRange("tickFishingMin", tickFishingMin, 1, 10000);
        TICK_FISHING_MAX = BUILDER.defineInRange("tickFishingMax", tickFishingMax, 1, 10000);
        TICK_MINING_MIN = BUILDER.defineInRange("tickMiningMin", tickMiningMin, 1, 10000);
        TICK_MINING_MAX = BUILDER.defineInRange("tickMiningMax", tickMiningMax, 1, 10000);
        TASK_ENABLE_COOKING = BUILDER.define("enableCooking", taskEnable[0]);
        TASK_ENABLE_FISHING = BUILDER.define("enableFishing", taskEnable[1]);
        TASK_ENABLE_MINING = BUILDER.define("enableMining", taskEnable[2]);
        TASK_ENABLE_CRAFTING = BUILDER.define("enableCrafting", taskEnable[3]);
        SMALL_SHIPYARD_POWER_MAX = BUILDER
                .comment("Small shipyard max fuel storage")
                .defineInRange("smallShipyardPowerMax", smallShipyardPowerMax, 1, 100000000);
        SMALL_SHIPYARD_BUILD_SPEED = BUILDER
                .comment("Small shipyard build progress per tick while active")
                .defineInRange("smallShipyardBuildSpeed", smallShipyardBuildSpeed, 1, 1000000);
        SMALL_SHIPYARD_INSTANT_TICKS = BUILDER
                .comment("Build ticks skipped per instant construction material in the small shipyard")
                .defineInRange("smallShipyardInstantTicks", smallShipyardInstantTicks, 0, 1000000);
        SMALL_SHIPYARD_FUEL_MAGNIFICATION = BUILDER
                .comment("Small shipyard fuel magnification multiplier")
                .defineInRange("smallShipyardFuelMagnification", (double) smallShipyardFuelMagnification, 0.0D, 1000.0D);
        LARGE_SHIPYARD_POWER_MAX = BUILDER
                .comment("Large shipyard max fuel storage")
                .defineInRange("largeShipyardPowerMax", largeShipyardPowerMax, 1, 100000000);
        LARGE_SHIPYARD_BUILD_SPEED = BUILDER
                .comment("Large shipyard build progress per tick while active")
                .defineInRange("largeShipyardBuildSpeed", largeShipyardBuildSpeed, 1, 1000000);
        LARGE_SHIPYARD_INSTANT_TICKS = BUILDER
                .comment("Build ticks skipped per instant construction material in the large shipyard")
                .defineInRange("largeShipyardInstantTicks", largeShipyardInstantTicks, 0, 1000000);
        LARGE_SHIPYARD_FUEL_MAGNIFICATION = BUILDER
                .comment("Large shipyard fuel magnification multiplier")
                .defineInRange("largeShipyardFuelMagnification", (double) largeShipyardFuelMagnification, 0.0D, 1000.0D);
        RING_ABILITY_WATER_BREATHING = BUILDER
                .comment("Married ship count needed for passive water breathing, negative disables")
                .defineInRange("ringAbilityWaterBreathing", ringAbilityWaterBreathing, -1, 1000);
        RING_ABILITY_SWIM_FLIGHT = BUILDER
                .comment("Legacy swim flight threshold. Grants temporary flight while the active ring owner is in water, negative disables")
                .defineInRange("ringAbilitySwimFlight", ringAbilitySwimFlight, -1, 1000);
        RING_ABILITY_UNDERWATER_DIG_CAP = BUILDER
                .comment("Maximum married ship count contributing to underwater dig speed, 0 disables")
                .defineInRange("ringAbilityUnderwaterDigCap", ringAbilityUnderwaterDigCap, 0, 1000);
        RING_ABILITY_UNDERWATER_FOG_CAP = BUILDER
                .comment("Legacy underwater fog reduction threshold. 0 removes water fog, positive values scale fog reduction by married ship count, negative disables")
                .defineInRange("ringAbilityUnderwaterFogCap", ringAbilityUnderwaterFogCap, -1, 1000);
        RING_ABILITY_FIRE_IMMUNITY = BUILDER
                .comment("Married ship count needed for active ring fire immunity, negative disables")
                .defineInRange("ringAbilityFireImmunity", ringAbilityFireImmunity, -1, 1000);
        DRUM_LIQUID_BASE_RATE = BUILDER
                .comment("Crane liquid transfer base rate per liquid drum in mB/t")
                .defineInRange("drumLiquidBaseRate", drumLiquidBaseRate, 0, 1000000);
        DRUM_LIQUID_ENCHANT_RATE = BUILDER
                .comment("Additional crane liquid transfer rate per enchantment level in mB/t")
                .defineInRange("drumLiquidEnchantRate", drumLiquidEnchantRate, 0, 1000000);
        DRUM_ENERGY_BASE_RATE = BUILDER
                .comment("Crane energy transfer base rate per energy drum in FE/t")
                .defineInRange("drumEnergyBaseRate", drumEnergyBaseRate, 0, 1000000);
        DRUM_ENERGY_ENCHANT_RATE = BUILDER
                .comment("Additional crane energy transfer rate per enchantment level in FE/t")
                .defineInRange("drumEnergyEnchantRate", drumEnergyEnchantRate, 0, 1000000);
        PAIR_DIST_CHEST = BUILDER
                .comment("Max pairing distance between waypoint and chest/crane")
                .defineInRange("pairDistChest", pairDistChest, 0, 64);
        PAIR_DIST_WAYPOINT = BUILDER
                .comment("Max pairing distance between waypoints")
                .defineInRange("pairDistWaypoint", pairDistWaypoint, 0, 64);
        SHIP_CAN_TELEPORT = BUILDER
                .comment("Can ship teleport to owner or guarding position if too far away")
                .define("canTeleport", canTeleport);
        CRANE_TANK_CAPACITY = BUILDER
                .comment("Crane internal fluid tank capacity in mB")
                .defineInRange("craneTankCapacity", craneTankCapacity, 1, 1000000000);
        VOLCORE_POWER_MAX = BUILDER
                .comment("Volcano Core max fuel storage")
                .defineInRange("volCorePowerMax", volCorePowerMax, 1, 100000000);
        VOLCORE_CONSUME_SPEED = BUILDER
                .comment("Volcano Core power consumed every 16 ticks while active")
                .defineInRange("volCoreConsumeSpeed", volCoreConsumeSpeed, 1, 1000000);
        VOLCORE_FUEL_MAGNITUDE = BUILDER
                .comment("Volcano Core fuel value per grudge item")
                .defineInRange("volCoreFuelMagnitude", volCoreFuelMagnitude, 1, 1000000);
        MINING_ENTRIES = BUILDER
                .comment("Mining table entries: dimension, biome, item, meta, weight, min, max, shipLevel, maxY, toolLevel, enchantPercent. Use '*' for all dimensions/biomes or legacy numeric dimension ids like 0/-1/1.")
                .defineList("miningEntries", defaultMiningEntries(), value -> value instanceof String str && isValidMiningEntry(str));
        LOOT_ENTRIES = BUILDER
                .comment("Legacy chest loot entries: chestId, item, meta, weight, chancePercent, min, max. chestId: 0 spawn, 1 igloo, 2 dungeon, 3 village, 4 mineshaft, 5 pyramid, 6 jungle temple, 7 nether bridge, 8 stronghold, 9 end city.")
                .defineList("lootEntries", defaultLootEntries(), value -> value instanceof String str && isValidLootEntry(str));
        for(int i=0; i<4; i++) {
            EXP_GAIN_TASK[i] = BUILDER.defineInRange("expGainTask" + i, expGainTask[i], 0, 10000);
            CONSUME_GRUDGE_TASK[i] = BUILDER.defineInRange("consumeGrudgeTask" + i, consumeGrudgeTask[i], 0, 10000);
        }
        BUILDER.pop();

        BUILDER.comment("Legacy hostile ship spawn/death/drop settings").push("hostile");

        HOSTILE_DROP_GRUDGE_RATE = BUILDER
                .comment("Grudge drop rate for hostile entities (fixed + chance, e.g. 5.5 = 5 guaranteed + 50% for +1)")
                .defineInRange("dropGrudgeRate", hostileDropGrudgeRate, 0.0D, 64.0D);

        HOSTILE_DEATH_MAX_TICKS = BUILDER
                .comment("Hostile ship death animation ticks before final sink processing")
                .defineInRange("deathMaxTicks", hostileDeathMaxTicks, 0, 3600);

        HOSTILE_DESPAWN_BOSS_TICKS = BUILDER
                .comment("Hostile boss despawn ticks, -1 disables despawn")
                .defineInRange("despawnBossTicks", hostileDespawnBossTicks, -1, 1728000);

        HOSTILE_DESPAWN_MINION_TICKS = BUILDER
                .comment("Hostile minion despawn ticks, -1 disables despawn")
                .defineInRange("despawnMinionTicks", hostileDespawnMinionTicks, -1, 1728000);

        HOSTILE_BOSS_COOLDOWN_TICKS = BUILDER
                .comment("Boss fleet spawn cooldown ticks")
                .defineInRange("bossCooldownTicks", hostileBossCooldownTicks, 20, 1728000);

        HOSTILE_SPAWN_BOSS_COUNT = BUILDER
                .comment("Boss ships per boss fleet spawn")
                .defineInRange("spawnBossCount", hostileSpawnBossCount, 1, 10);

        HOSTILE_SPAWN_MINION_COUNT = BUILDER
                .comment("Minion ships per boss fleet spawn")
                .defineInRange("spawnMinionCount", hostileSpawnMinionCount, 1, 10);

        HOSTILE_SPAWN_REQUIRE_RING = BUILDER
                .comment("Require player inventory to have a marriage ring for regular hostile mob spawns")
                .define("spawnRequireRing", hostileSpawnRequireRing);

        HOSTILE_MOB_SPAWN_MAX = BUILDER
                .comment("Maximum number of non-boss hostile ships loaded in a level for regular spawn checks")
                .defineInRange("mobSpawnMax", hostileMobSpawnMax, 0, 10000);

        HOSTILE_MOB_SPAWN_CHANCE_PERCENT = BUILDER
                .comment("Regular hostile spawn chance percent per check")
                .defineInRange("mobSpawnChancePercent", hostileMobSpawnChancePercent, 0, 100);

        HOSTILE_MOB_SPAWN_GROUPS = BUILDER
                .comment("Regular hostile spawn group count per successful spawn check")
                .defineInRange("mobSpawnGroups", hostileMobSpawnGroups, 1, 16);

        HOSTILE_MOB_SPAWN_GROUP_MIN = BUILDER
                .comment("Minimum hostile ship count per regular spawn group")
                .defineInRange("mobSpawnGroupMin", hostileMobSpawnGroupMin, 1, 16);

        HOSTILE_MOB_SPAWN_GROUP_MAX = BUILDER
                .comment("Maximum hostile ship count per regular spawn group")
                .defineInRange("mobSpawnGroupMax", hostileMobSpawnGroupMax, 1, 16);

        BUILDER.pop();

        CLIENT_BUILDER.comment("Ship sound and timekeeping settings").push("ship_sound");
        SHIP_CAN_TIMEKEEPING = CLIENT_BUILDER
                .comment("Play ship timekeeping voice every Minecraft hour when enabled in ship GUI")
                .define("canTimeKeeping", canTimeKeeping);
        SHIP_VOLUME_TIMEKEEPING = CLIENT_BUILDER
                .comment("Timekeeping voice volume multiplier")
                .defineInRange("volumeTimeKeeping", volumeTimeKeeping, 0.0D, 10.0D);
        SHIP_VOLUME_GENERAL = CLIENT_BUILDER
                .comment("General ship voice volume multiplier")
                .defineInRange("volumeShip", volumeShip, 0.0D, 10.0D);
        SHIP_VOLUME_ATTACK = CLIENT_BUILDER
                .comment("Attack sound volume multiplier")
                .defineInRange("volumeAttack", volumeAttack, 0.0D, 10.0D);
        CUSTOM_SOUND_RATES = CLIENT_BUILDER
                .comment("Custom ship voice rates: shipClass,idle,attack,hurt,dead,marry,knockback,item,feed,timekeep00~timekeep23. Value is 0-100 percent.")
                .defineList("customSoundRates", defaultCustomSoundRates(), value -> value instanceof String str && isValidCustomSoundRate(str));
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.comment("Client side settings").push("client");
        CLIENT_SCALE_HELD_ITEM = CLIENT_BUILDER
                .comment("Held item scale")
                .defineInRange("scaleHeldItem", 1.0D, 0.0D, 10.0D);
        CLIENT_OFFSET_HELD_ITEM_X = CLIENT_BUILDER
                .comment("Held item offset X")
                .defineInRange("offsetHeldItemX", 0.0D, -10.0D, 10.0D);
        CLIENT_OFFSET_HELD_ITEM_Y = CLIENT_BUILDER
                .comment("Held item offset Y")
                .defineInRange("offsetHeldItemY", 0.0D, -10.0D, 10.0D);
        CLIENT_OFFSET_HELD_ITEM_Z = CLIENT_BUILDER
                .comment("Held item offset Z")
                .defineInRange("offsetHeldItemZ", 0.0D, -10.0D, 10.0D);
        CLIENT_BUILDER.pop();

        SPEC = BUILDER.build();
        CLIENT_SPEC = CLIENT_BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            shipExpModifier = SHIP_EXP_MODIFIER.get();
            shipExpGainMelee = SHIP_EXP_GAIN_MELEE.get();
            shipExpGainKill = SHIP_EXP_GAIN_KILL.get();
            shipExpGainLightAttack = SHIP_EXP_GAIN_LIGHT_ATTACK.get();
            shipExpGainHeavyAttack = SHIP_EXP_GAIN_HEAVY_ATTACK.get();
            shipExpGainLightAircraft = SHIP_EXP_GAIN_LIGHT_AIRCRAFT.get();
            shipExpGainHeavyAircraft = SHIP_EXP_GAIN_HEAVY_AIRCRAFT.get();
            shipMaxLevelNormal = SHIP_MAX_LEVEL_NORMAL.get();
            shipMaxLevelMarried = Math.max(shipMaxLevelNormal, SHIP_MAX_LEVEL_MARRIED.get());
            trainingBookLevelMin = TRAINING_BOOK_LEVEL_MIN.get();
            trainingBookLevelMax = Math.max(trainingBookLevelMin, TRAINING_BOOK_LEVEL_MAX.get());
            debugLogging = DEBUG_LOGGING.get();
            debugPerformanceLogging = DEBUG_PERFORMANCE_LOGGING.get();
            debugPerfSlowShipTickMs = DEBUG_PERF_SLOW_SHIP_TICK_MS.get();
            debugPerfSlowTaskTickMs = DEBUG_PERF_SLOW_TASK_TICK_MS.get();
            debugPerfSlowBlockEntityTickMs = DEBUG_PERF_SLOW_BLOCK_ENTITY_TICK_MS.get();
            debugPerfSlowProjectileTickMs = DEBUG_PERF_SLOW_PROJECTILE_TICK_MS.get();
            debugPerfSlowServerTickMs = DEBUG_PERF_SLOW_SERVER_TICK_MS.get();
            debugPerfMinLogIntervalTicks = DEBUG_PERF_MIN_LOG_INTERVAL_TICKS.get();
            modernKitNotifyWhenMaxed = MODERN_KIT_NOTIFY_WHEN_MAXED.get();
            modernKitNotifyWhenMaxedActionBar = MODERN_KIT_NOTIFY_WHEN_MAXED_ACTION_BAR.get();

            fuelDecayInterval = FUEL_DECAY_INTERVAL.get();
            fuelMoveDecayFactor = FUEL_MOVE_DECAY_FACTOR.get();
            fuelConsumeDD = FUEL_CONSUME_DD.get();
            fuelConsumeCL = FUEL_CONSUME_CL.get();
            fuelConsumeCA = FUEL_CONSUME_CA.get();
            fuelConsumeCAV = FUEL_CONSUME_CAV.get();
            fuelConsumeCLT = FUEL_CONSUME_CLT.get();
            fuelConsumeCVL = FUEL_CONSUME_CVL.get();
            fuelConsumeCV = FUEL_CONSUME_CV.get();
            fuelConsumeBB = FUEL_CONSUME_BB.get();
            fuelConsumeBBV = FUEL_CONSUME_BBV.get();
            fuelConsumeSS = FUEL_CONSUME_SS.get();
            fuelConsumeAP = FUEL_CONSUME_AP.get();
            fuelConsumeActionLight = FUEL_CONSUME_ACTION_LIGHT.get();
            fuelConsumeActionHeavy = FUEL_CONSUME_ACTION_HEAVY.get();
            fuelConsumeActionLightAircraft = FUEL_CONSUME_ACTION_LIGHT_AIRCRAFT.get();
            fuelConsumeActionHeavyAircraft = FUEL_CONSUME_ACTION_HEAVY_AIRCRAFT.get();

            tickFishingMin = TICK_FISHING_MIN.get();
            tickFishingMax = TICK_FISHING_MAX.get();
            tickMiningMin = TICK_MINING_MIN.get();
            tickMiningMax = TICK_MINING_MAX.get();
            taskEnable[0] = TASK_ENABLE_COOKING.get();
            taskEnable[1] = TASK_ENABLE_FISHING.get();
            taskEnable[2] = TASK_ENABLE_MINING.get();
            taskEnable[3] = TASK_ENABLE_CRAFTING.get();
            smallShipyardPowerMax = SMALL_SHIPYARD_POWER_MAX.get();
            smallShipyardBuildSpeed = SMALL_SHIPYARD_BUILD_SPEED.get();
            smallShipyardInstantTicks = SMALL_SHIPYARD_INSTANT_TICKS.get();
            smallShipyardFuelMagnification = SMALL_SHIPYARD_FUEL_MAGNIFICATION.get().floatValue();
            largeShipyardPowerMax = LARGE_SHIPYARD_POWER_MAX.get();
            largeShipyardBuildSpeed = LARGE_SHIPYARD_BUILD_SPEED.get();
            largeShipyardInstantTicks = LARGE_SHIPYARD_INSTANT_TICKS.get();
            largeShipyardFuelMagnification = LARGE_SHIPYARD_FUEL_MAGNIFICATION.get().floatValue();
            ringAbilityWaterBreathing = RING_ABILITY_WATER_BREATHING.get();
            ringAbilitySwimFlight = RING_ABILITY_SWIM_FLIGHT.get();
            ringAbilityUnderwaterDigCap = RING_ABILITY_UNDERWATER_DIG_CAP.get();
            ringAbilityUnderwaterFogCap = RING_ABILITY_UNDERWATER_FOG_CAP.get();
            ringAbilityFireImmunity = RING_ABILITY_FIRE_IMMUNITY.get();
            drumLiquidBaseRate = DRUM_LIQUID_BASE_RATE.get();
            drumLiquidEnchantRate = DRUM_LIQUID_ENCHANT_RATE.get();
            drumEnergyBaseRate = DRUM_ENERGY_BASE_RATE.get();
            drumEnergyEnchantRate = DRUM_ENERGY_ENCHANT_RATE.get();
            pairDistChest = PAIR_DIST_CHEST.get();
            pairDistWaypoint = PAIR_DIST_WAYPOINT.get();
            canTeleport = SHIP_CAN_TELEPORT.get();
            craneTankCapacity = CRANE_TANK_CAPACITY.get();
            volCorePowerMax = VOLCORE_POWER_MAX.get();
            volCoreConsumeSpeed = VOLCORE_CONSUME_SPEED.get();
            volCoreFuelMagnitude = VOLCORE_FUEL_MAGNITUDE.get();
            miningEntries = parseMiningEntries(MINING_ENTRIES.get());
            lootEntries = parseLootEntries(LOOT_ENTRIES.get());
            for (int i = 0; i < 4; i++) {
                expGainTask[i] = EXP_GAIN_TASK[i].get();
                consumeGrudgeTask[i] = CONSUME_GRUDGE_TASK[i].get();
            }

            hostileDropGrudgeRate = HOSTILE_DROP_GRUDGE_RATE.get().floatValue();
            hostileDeathMaxTicks = HOSTILE_DEATH_MAX_TICKS.get();
            hostileDespawnBossTicks = HOSTILE_DESPAWN_BOSS_TICKS.get();
            hostileDespawnMinionTicks = HOSTILE_DESPAWN_MINION_TICKS.get();
            hostileBossCooldownTicks = HOSTILE_BOSS_COOLDOWN_TICKS.get();
            hostileSpawnBossCount = HOSTILE_SPAWN_BOSS_COUNT.get();
            hostileSpawnMinionCount = HOSTILE_SPAWN_MINION_COUNT.get();
            hostileSpawnRequireRing = HOSTILE_SPAWN_REQUIRE_RING.get();
            hostileMobSpawnMax = HOSTILE_MOB_SPAWN_MAX.get();
            hostileMobSpawnChancePercent = HOSTILE_MOB_SPAWN_CHANCE_PERCENT.get();
            hostileMobSpawnGroups = Math.max(1, HOSTILE_MOB_SPAWN_GROUPS.get());
            hostileMobSpawnGroupMin = Math.max(1, HOSTILE_MOB_SPAWN_GROUP_MIN.get());
            hostileMobSpawnGroupMax = Math.max(hostileMobSpawnGroupMin, HOSTILE_MOB_SPAWN_GROUP_MAX.get());
            return;
        }

        if (event.getConfig().getSpec() == CLIENT_SPEC) {
            canTimeKeeping = SHIP_CAN_TIMEKEEPING.get();
            volumeTimeKeeping = SHIP_VOLUME_TIMEKEEPING.get().floatValue();
            volumeShip = SHIP_VOLUME_GENERAL.get().floatValue();
            volumeAttack = SHIP_VOLUME_ATTACK.get().floatValue();
            customSoundRates = parseCustomSoundRates(CUSTOM_SOUND_RATES.get());

            scaleHeldItem = CLIENT_SCALE_HELD_ITEM.get().floatValue();
            offsetHeldItemX = CLIENT_OFFSET_HELD_ITEM_X.get().floatValue();
            offsetHeldItemY = CLIENT_OFFSET_HELD_ITEM_Y.get().floatValue();
            offsetHeldItemZ = CLIENT_OFFSET_HELD_ITEM_Z.get().floatValue();
        }
    }

    private static List<String> defaultMiningEntries() {
        List<String> entries = new ArrayList<>();
        entries.add("*,*,minecraft:cobblestone,0,100,1,4,1,256,0,0");

        entries.add("minecraft:overworld,*,minecraft:cobblestone,0,4000,1,4,1,256,0,0");
        entries.add("minecraft:overworld,*,minecraft:stone,0,500,1,1,1,256,0,0");
        entries.add("minecraft:overworld,*,minecraft:granite,0,500,1,1,1,256,0,0");
        entries.add("minecraft:overworld,*,minecraft:diorite,0,500,1,1,1,256,0,0");
        entries.add("minecraft:overworld,*,minecraft:andesite,0,500,1,1,1,256,0,0");
        entries.add("minecraft:overworld,*,minecraft:dirt,0,500,1,1,1,256,0,0");
        entries.add("minecraft:overworld,*,minecraft:sand,0,500,1,1,1,256,0,0");
        entries.add("minecraft:overworld,*,minecraft:gravel,0,200,1,1,1,256,0,0");
        entries.add("minecraft:overworld,*,minecraft:obsidian,0,200,1,1,40,24,3,0");
        entries.add("minecraft:overworld,*,minecraft:flint,0,250,1,1,1,256,0,0");
        entries.add("minecraft:overworld,*,minecraft:gunpowder,0,400,1,1,40,64,0,0");
        entries.add("minecraft:overworld,*,minecraft:bone,0,400,1,1,1,64,0,0");
        entries.add("minecraft:overworld,*,minecraft:coal,0,500,1,3,1,100,1,150");
        entries.add("minecraft:overworld,*,minecraft:redstone,0,500,1,3,20,15,2,150");
        entries.add("minecraft:overworld,*,minecraft:iron_ore,0,350,1,2,1,64,2,100");
        entries.add("minecraft:overworld,*,shincolle:abyss_polymetal,0,350,1,3,1,64,2,100");
        entries.add("minecraft:overworld,*,minecraft:gold_ore,0,100,1,1,30,32,2,100");
        entries.add("minecraft:overworld,*,minecraft:lapis_lazuli,0,200,1,3,30,30,2,150");
        entries.add("minecraft:overworld,*,minecraft:diamond,0,50,1,1,60,16,3,100");
        entries.add("minecraft:overworld,*,minecraft:emerald,0,80,1,1,40,32,3,100");
        entries.add("minecraft:overworld,*,shincolle:marriagering,0,25,1,1,1,16,3,0");

        entries.add("minecraft:overworld,minecraft:warm_ocean,minecraft:prismarine_shard,0,500,1,4,30,128,0,0");
        entries.add("minecraft:overworld,minecraft:warm_ocean,minecraft:prismarine_crystals,0,200,1,3,60,128,2,100");
        entries.add("minecraft:overworld,minecraft:warm_ocean,shincolle:abyss_polymetal,0,500,1,3,1,64,2,100");
        entries.add("minecraft:overworld,minecraft:warm_ocean,minecraft:sponge,0,200,1,1,80,128,0,100");
        entries.add("minecraft:overworld,minecraft:deep_ocean,minecraft:prismarine_shard,0,500,1,4,30,128,0,0");
        entries.add("minecraft:overworld,minecraft:deep_ocean,minecraft:prismarine_crystals,0,200,1,3,60,128,2,100");
        entries.add("minecraft:overworld,minecraft:deep_ocean,shincolle:abyss_polymetal,0,500,1,3,1,64,2,100");
        entries.add("minecraft:overworld,minecraft:deep_ocean,minecraft:sponge,0,200,1,1,80,128,0,100");
        entries.add("minecraft:overworld,minecraft:mushroom_fields,minecraft:clay_ball,0,500,1,4,30,128,0,0");
        entries.add("minecraft:overworld,minecraft:mushroom_fields,minecraft:mycelium,0,500,1,1,50,128,0,0");
        entries.add("minecraft:overworld,minecraft:frozen_ocean,minecraft:packed_ice,0,1000,1,4,1,256,2,0");
        entries.add("minecraft:overworld,minecraft:deep_frozen_ocean,minecraft:packed_ice,0,1000,1,4,1,256,2,0");
        entries.add("minecraft:overworld,minecraft:frozen_river,minecraft:packed_ice,0,1000,1,4,1,256,2,0");

        entries.add("minecraft:the_nether,*,minecraft:netherrack,0,4500,1,4,1,256,0,0");
        entries.add("minecraft:the_nether,*,minecraft:nether_bricks,0,1000,1,1,1,256,0,0");
        entries.add("minecraft:the_nether,*,minecraft:soul_sand,0,1000,1,1,1,256,0,0");
        entries.add("minecraft:the_nether,*,minecraft:gravel,0,1000,1,1,1,256,0,0");
        entries.add("minecraft:the_nether,*,minecraft:magma_block,0,500,1,1,40,256,3,0");
        entries.add("minecraft:the_nether,*,minecraft:flint,0,500,1,1,1,256,0,0");
        entries.add("minecraft:the_nether,*,shincolle:marriagering,0,50,1,1,1,256,3,0");
        entries.add("minecraft:the_nether,*,minecraft:quartz,0,1000,1,3,1,256,2,150");
        entries.add("minecraft:the_nether,*,minecraft:glowstone_dust,0,500,1,2,1,256,0,100");
        entries.add("minecraft:the_nether,*,minecraft:ghast_tear,0,50,1,1,90,256,3,100");
        entries.add("minecraft:the_nether,*,minecraft:blaze_rod,0,80,1,1,60,256,3,100");

        entries.add("minecraft:the_end,*,minecraft:end_stone,0,4000,1,4,1,256,0,0");
        entries.add("minecraft:the_end,*,minecraft:ender_pearl,0,200,1,1,40,256,3,100");
        entries.add("minecraft:the_end,*,minecraft:chorus_fruit,0,200,1,3,60,256,3,100");
        entries.add("minecraft:the_end,*,shincolle:marriagering,0,25,1,1,1,256,3,0");
        return entries;
    }

    private static List<String> defaultCustomSoundRates() {
        List<String> entries = new ArrayList<>();
        entries.add("54,25,0,25,0,50,0,50,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
        entries.add("56,50,50,50,100,0,0,50,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
        entries.add("60,25,50,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
        entries.add("62,0,35,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
        return entries;
    }

    private static boolean isValidCustomSoundRate(String rawEntry) {
        String[] parts = rawEntry.replaceAll("\\s", "").split(",");
        if (parts.length != 33) {
            return false;
        }

        try {
            Integer.parseInt(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                int percent = Integer.parseInt(parts[i]);
                if (percent < 0 || percent > 100) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private static Map<Integer, EnumMap<ShipCustomSoundType, Float>> parseCustomSoundRates(List<? extends String> rawEntries) {
        if (rawEntries == null || rawEntries.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Integer, EnumMap<ShipCustomSoundType, Float>> parsed = new java.util.HashMap<>();
        ShipCustomSoundType[] soundTypes = ShipCustomSoundType.values();

        for (String rawEntry : rawEntries) {
            if (rawEntry == null || rawEntry.isBlank()) {
                continue;
            }

            String[] parts = rawEntry.replaceAll("\\s", "").split(",");
            if (parts.length != soundTypes.length + 1) {
                continue;
            }

            try {
                int shipClass = Integer.parseInt(parts[0]);
                EnumMap<ShipCustomSoundType, Float> rates = new EnumMap<>(ShipCustomSoundType.class);
                for (int i = 0; i < soundTypes.length; i++) {
                    int percent = Integer.parseInt(parts[i + 1]);
                    if (percent > 0) {
                        rates.put(soundTypes[i], Math.min(100, percent) / 100.0F);
                    }
                }
                if (!rates.isEmpty()) {
                    parsed.put(shipClass, rates);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return Collections.unmodifiableMap(parsed);
    }

    private static List<String> defaultLootEntries() {
        List<String> entries = new ArrayList<>();
        entries.add("0,shincolle:grudge,0,1,100,10,15");
        entries.add("0,shincolle:destroyer_i_spawn_egg,0,2,100,1,1");
        entries.add("0,shincolle:ammo,0,1,100,5,8");

        entries.add("1,shincolle:grudge,0,1,100,5,8");
        entries.add("1,shincolle:destroyer_i_spawn_egg,0,1,100,1,1");
        entries.add("1,shincolle:ammo,0,1,100,2,3");
        entries.add("1,shincolle:instantconmat,0,1,100,3,5");

        entries.add("2,shincolle:marriagering,0,4,70,1,1");
        entries.add("2,shincolle:trainingbook,0,4,80,1,3");
        entries.add("2,shincolle:destroyer_i_spawn_egg,0,3,100,1,1");
        entries.add("2,shincolle:destroyer_ro_spawn_egg,0,3,100,1,1");
        entries.add("2,shincolle:destroyer_akatsuki_spawn_egg,0,1,80,1,1");
        entries.add("2,shincolle:destroyer_hibiki_spawn_egg,0,1,80,1,1");

        entries.add("3,shincolle:instantconmat,0,20,100,10,20");
        entries.add("3,shincolle:abyssium,0,10,100,5,10");
        entries.add("3,shincolle:polymetal,0,10,100,5,10");
        entries.add("3,shincolle:destroyer_i_spawn_egg,0,5,100,1,1");

        entries.add("4,shincolle:trainingbook,0,6,80,1,3");
        entries.add("4,shincolle:marriagering,0,6,70,1,1");
        entries.add("4,shincolle:destroyer_i_spawn_egg,0,3,100,1,1");
        entries.add("4,shincolle:destroyer_ro_spawn_egg,0,3,100,1,1");
        entries.add("4,shincolle:equipcannon,-1,8,100,1,1");
        entries.add("4,shincolle:equipairplane,-1,8,100,1,1");
        entries.add("4,shincolle:equiptorpedo,-1,8,100,1,1");

        entries.add("5,shincolle:trainingbook,0,6,80,1,3");
        entries.add("5,shincolle:marriagering,0,6,70,1,1");
        entries.add("5,shincolle:destroyer_i_spawn_egg,0,3,100,1,1");
        entries.add("5,shincolle:destroyer_ro_spawn_egg,0,3,100,1,1");
        entries.add("5,shincolle:equipcannon,-1,8,100,1,1");
        entries.add("5,shincolle:equipairplane,-1,8,100,1,1");
        entries.add("5,shincolle:equiptorpedo,-1,8,100,1,1");

        entries.add("6,shincolle:marriagering,0,4,70,1,1");
        entries.add("6,shincolle:destroyer_ro_spawn_egg,0,2,100,1,1");
        entries.add("6,shincolle:destroyer_akatsuki_spawn_egg,0,1,80,1,1");
        entries.add("6,shincolle:destroyer_hibiki_spawn_egg,0,1,80,1,1");

        entries.add("7,shincolle:trainingbook,0,4,80,1,3");
        entries.add("7,shincolle:instantconmat,0,4,100,10,12");
        entries.add("7,shincolle:marriagering,0,4,70,1,1");
        entries.add("7,shincolle:abyssium,0,4,100,5,15");
        entries.add("7,shincolle:polymetal,0,4,100,5,15");

        entries.add("8,shincolle:trainingbook,0,6,80,1,3");
        entries.add("8,shincolle:marriagering,0,6,70,1,1");
        entries.add("8,shincolle:destroyer_i_spawn_egg,0,3,100,1,1");
        entries.add("8,shincolle:destroyer_ro_spawn_egg,0,3,100,1,1");
        entries.add("8,shincolle:equipcannon,-1,8,100,1,1");
        entries.add("8,shincolle:equipairplane,-1,8,100,1,1");
        entries.add("8,shincolle:equiptorpedo,-1,8,100,1,1");

        entries.add("9,shincolle:trainingbook,0,6,80,1,3");
        entries.add("9,shincolle:marriagering,0,6,70,1,1");
        entries.add("9,shincolle:destroyer_i_spawn_egg,0,3,100,1,1");
        entries.add("9,shincolle:destroyer_ro_spawn_egg,0,3,100,1,1");
        entries.add("9,shincolle:equipcannon,-1,8,100,1,1");
        entries.add("9,shincolle:equipairplane,-1,8,100,1,1");
        entries.add("9,shincolle:equiptorpedo,-1,8,100,1,1");
        return entries;
    }

    private static boolean isValidMiningEntry(String line) {
        String[] parts = line.replace(" ", "").split(",");
        if (parts.length != 11) return false;
        if (parts[2].isBlank()) return false;
        try {
            Integer.parseInt(parts[3]);
            Integer.parseInt(parts[4]);
            Integer.parseInt(parts[5]);
            Integer.parseInt(parts[6]);
            Integer.parseInt(parts[7]);
            Integer.parseInt(parts[8]);
            Integer.parseInt(parts[9]);
            Integer.parseInt(parts[10]);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private static boolean isValidLootEntry(String line) {
        String[] parts = line.replace(" ", "").split(",");
        if (parts.length != 7 || parts[1].isBlank()) return false;
        try {
            Integer.parseInt(parts[0]);
            Integer.parseInt(parts[2]);
            Integer.parseInt(parts[3]);
            Integer.parseInt(parts[4]);
            Integer.parseInt(parts[5]);
            Integer.parseInt(parts[6]);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private static List<MiningEntry> parseMiningEntries(List<? extends String> rawEntries) {
        List<MiningEntry> parsed = new ArrayList<>();
        for (String raw : rawEntries) {
            String[] parts = raw.replace(" ", "").split(",");
            if (parts.length != 11) continue;
            int dimensionId = parseDimensionId(parts[0]);
            String dimensionPath = parts[0];
            int biomeId = parseBiomeId(parts[1]);
            String biomePath = parts[1];
            try {
                int itemMeta = Integer.parseInt(parts[3]);
                Item item = resolveConfigItem(parts[2], itemMeta, Items.AIR);
                if (item == Items.AIR) continue;
                parsed.add(new MiningEntry(
                        dimensionId,
                        dimensionPath,
                        biomeId,
                        biomePath,
                        item,
                        itemMeta,
                        Math.max(1, Integer.parseInt(parts[4])),
                        Math.max(1, Integer.parseInt(parts[5])),
                        Math.max(1, Integer.parseInt(parts[6])),
                        Integer.parseInt(parts[7]),
                        Integer.parseInt(parts[8]),
                        Math.max(0, Integer.parseInt(parts[9])),
                        Integer.parseInt(parts[10]) * 0.01F
                ));
            } catch (NumberFormatException ignored) {
            }
        }
        return List.copyOf(parsed);
    }

    private static List<LootEntry> parseLootEntries(List<? extends String> rawEntries) {
        List<LootEntry> parsed = new ArrayList<>();
        for (String raw : rawEntries) {
            String[] parts = raw.replace(" ", "").split(",");
            if (parts.length != 7) continue;
            try {
                int itemMeta = Integer.parseInt(parts[2]);
                Item item = resolveConfigItem(parts[1], itemMeta, Items.AIR);
                if (item == Items.AIR) continue;
                parsed.add(new LootEntry(
                        Integer.parseInt(parts[0]),
                        item,
                        itemMeta,
                        Math.max(1, Integer.parseInt(parts[3])),
                        Integer.parseInt(parts[4]) * 0.01F,
                        Math.max(1, Integer.parseInt(parts[5])),
                        Math.max(1, Integer.parseInt(parts[6]))
                ));
            } catch (NumberFormatException ignored) {
            }
        }
        return List.copyOf(parsed);
    }

    private static int parseDimensionId(String value) {
        return switch (value) {
            case "999999", "*" -> 999999;
            case "0", "minecraft:overworld" -> 0;
            case "-1", "minecraft:the_nether" -> -1;
            case "1", "minecraft:the_end" -> 1;
            default -> Integer.MIN_VALUE;
        };
    }

    private static int parseBiomeId(String value) {
        return switch (value) {
            case "-999999", "*" -> -999999;
            default -> {
                try {
                    yield Integer.parseInt(value);
                } catch (NumberFormatException ex) {
                    yield Integer.MIN_VALUE;
                }
            }
        };
    }

    private static Item resolveConfigItem(String rawId, int itemMeta, Item fallback) {
        String mappedId = mapLegacyConfigItemId(rawId, itemMeta);
        ResourceLocation id = ResourceLocation.tryParse(mappedId);
        if (id == null) {
            return fallback;
        }
        return BuiltInRegistries.ITEM.getOptional(id).orElse(fallback);
    }

    private static String mapLegacyConfigItemId(String rawId, int itemMeta) {
        String id = rawId.trim().toLowerCase(Locale.ROOT);
        return switch (id) {
            case "shincolle:abyssmetal" -> itemMeta == 1 ? "shincolle:abyss_polymetal" : "shincolle:abyss_metal";
            case "shincolle:blockabyssium" -> "shincolle:abyssium";
            case "shincolle:blockpolymetal" -> "shincolle:polymetal";
            case "shincolle:blockpolymetalgravel" -> "shincolle:polymetal_gravel";
            case "shincolle:blockpolymetalore" -> "shincolle:polymetal_ore";
            case "minecraft:stone" -> switch (itemMeta) {
                case 1 -> "minecraft:granite";
                case 3 -> "minecraft:diorite";
                case 5 -> "minecraft:andesite";
                default -> id;
            };
            case "minecraft:dye" -> itemMeta == 4 ? "minecraft:lapis_lazuli" : id;
            case "minecraft:magma" -> "minecraft:magma_block";
            case "minecraft:nether_brick" -> "minecraft:nether_bricks";
            default -> id;
        };
    }
}
