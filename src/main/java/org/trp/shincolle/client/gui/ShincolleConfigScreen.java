package org.trp.shincolle.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import org.trp.shincolle.Config;
import org.trp.shincolle.Shincolle;

/**
 * Entry point for the Cloth Config based settings screen.
 * Uses lazy loading so Cloth Config is NOT required at runtime.
 */
public class ShincolleConfigScreen {

    private static boolean checked = false;
    private static boolean available = false;

    public static boolean isAvailable() {
        if (!checked) {
            available = ModList.get().isLoaded("cloth_config");
            checked = true;
        }
        return available;
    }

    /**
     * Creates the config screen, or null if Cloth Config is not installed.
     */
    public static Screen tryCreate(Screen parent) {
        if (!isAvailable()) {
            Shincolle.LOGGER.warn("[Shincolle] Cloth Config is not installed, cannot open config screen");
            return null;
        }
        return LazyScreen.create(parent);
    }

    /**
     * Separate inner class ensures Cloth Config classes are only loaded when first accessed.
     */
    private static class LazyScreen {
        static Screen create(Screen parent) {
            var builder = me.shedaniel.clothconfig2.api.ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.translatable("config.shincolle.title"));

            var entryBuilder = builder.entryBuilder();

            // === Ship EXP & Level ===
            var expCat = builder.getOrCreateCategory(Component.translatable("config.shincolle.ship_exp"));

            expCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.expModifier"), Config.SHIP_EXP_MODIFIER.get())
                    .setDefaultValue(1000).setSaveConsumer(Config.SHIP_EXP_MODIFIER::set).build());
            expCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.expGainMelee"), Config.SHIP_EXP_GAIN_MELEE.get())
                    .setDefaultValue(0).setSaveConsumer(Config.SHIP_EXP_GAIN_MELEE::set).build());
            expCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.expGainKill"), Config.SHIP_EXP_GAIN_KILL.get())
                    .setDefaultValue(8).setSaveConsumer(Config.SHIP_EXP_GAIN_KILL::set).build());
            expCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.expGainLightAttack"), Config.SHIP_EXP_GAIN_LIGHT_ATTACK.get())
                    .setDefaultValue(0).setSaveConsumer(Config.SHIP_EXP_GAIN_LIGHT_ATTACK::set).build());
            expCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.expGainHeavyAttack"), Config.SHIP_EXP_GAIN_HEAVY_ATTACK.get())
                    .setDefaultValue(0).setSaveConsumer(Config.SHIP_EXP_GAIN_HEAVY_ATTACK::set).build());
            expCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.expGainLightAircraft"), Config.SHIP_EXP_GAIN_LIGHT_AIRCRAFT.get())
                    .setDefaultValue(0).setSaveConsumer(Config.SHIP_EXP_GAIN_LIGHT_AIRCRAFT::set).build());
            expCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.expGainHeavyAircraft"), Config.SHIP_EXP_GAIN_HEAVY_AIRCRAFT.get())
                    .setDefaultValue(0).setSaveConsumer(Config.SHIP_EXP_GAIN_HEAVY_AIRCRAFT::set).build());
            expCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.maxLevelNormal"), Config.SHIP_MAX_LEVEL_NORMAL.get())
                    .setDefaultValue(100).setSaveConsumer(Config.SHIP_MAX_LEVEL_NORMAL::set).build());
            expCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.maxLevelMarried"), Config.SHIP_MAX_LEVEL_MARRIED.get())
                    .setDefaultValue(150).setSaveConsumer(Config.SHIP_MAX_LEVEL_MARRIED::set).build());
            expCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.trainingBookLevelMin"), Config.TRAINING_BOOK_LEVEL_MIN.get())
                    .setDefaultValue(1).setSaveConsumer(Config.TRAINING_BOOK_LEVEL_MIN::set).build());
            expCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.trainingBookLevelMax"), Config.TRAINING_BOOK_LEVEL_MAX.get())
                    .setDefaultValue(5).setSaveConsumer(Config.TRAINING_BOOK_LEVEL_MAX::set).build());

            // === Fuel Consumption ===
            var fuelCat = builder.getOrCreateCategory(Component.translatable("config.shincolle.fuel"));

            fuelCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.fuelDecayInterval"), Config.FUEL_DECAY_INTERVAL.get())
                    .setDefaultValue(100).setSaveConsumer(Config.FUEL_DECAY_INTERVAL::set).build());
            fuelCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.fuelMoveDecayFactor"), Config.FUEL_MOVE_DECAY_FACTOR.get())
                    .setDefaultValue(1).setSaveConsumer(Config.FUEL_MOVE_DECAY_FACTOR::set).build());
            fuelCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.fuelConsumeDD"), Config.FUEL_CONSUME_DD.get())
                    .setDefaultValue(1).setSaveConsumer(Config.FUEL_CONSUME_DD::set).build());
            fuelCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.fuelConsumeCL"), Config.FUEL_CONSUME_CL.get())
                    .setDefaultValue(1).setSaveConsumer(Config.FUEL_CONSUME_CL::set).build());
            fuelCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.fuelConsumeCA"), Config.FUEL_CONSUME_CA.get())
                    .setDefaultValue(2).setSaveConsumer(Config.FUEL_CONSUME_CA::set).build());
            fuelCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.fuelConsumeCAV"), Config.FUEL_CONSUME_CAV.get())
                    .setDefaultValue(2).setSaveConsumer(Config.FUEL_CONSUME_CAV::set).build());
            fuelCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.fuelConsumeCLT"), Config.FUEL_CONSUME_CLT.get())
                    .setDefaultValue(2).setSaveConsumer(Config.FUEL_CONSUME_CLT::set).build());
            fuelCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.fuelConsumeCVL"), Config.FUEL_CONSUME_CVL.get())
                    .setDefaultValue(3).setSaveConsumer(Config.FUEL_CONSUME_CVL::set).build());
            fuelCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.fuelConsumeCV"), Config.FUEL_CONSUME_CV.get())
                    .setDefaultValue(3).setSaveConsumer(Config.FUEL_CONSUME_CV::set).build());
            fuelCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.fuelConsumeBB"), Config.FUEL_CONSUME_BB.get())
                    .setDefaultValue(4).setSaveConsumer(Config.FUEL_CONSUME_BB::set).build());
            fuelCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.fuelConsumeBBV"), Config.FUEL_CONSUME_BBV.get())
                    .setDefaultValue(4).setSaveConsumer(Config.FUEL_CONSUME_BBV::set).build());
            fuelCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.fuelConsumeSS"), Config.FUEL_CONSUME_SS.get())
                    .setDefaultValue(1).setSaveConsumer(Config.FUEL_CONSUME_SS::set).build());
            fuelCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.fuelConsumeAP"), Config.FUEL_CONSUME_AP.get())
                    .setDefaultValue(1).setSaveConsumer(Config.FUEL_CONSUME_AP::set).build());
            fuelCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.fuelConsumeActionLight"), Config.FUEL_CONSUME_ACTION_LIGHT.get())
                    .setDefaultValue(0).setSaveConsumer(Config.FUEL_CONSUME_ACTION_LIGHT::set).build());
            fuelCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.fuelConsumeActionHeavy"), Config.FUEL_CONSUME_ACTION_HEAVY.get())
                    .setDefaultValue(0).setSaveConsumer(Config.FUEL_CONSUME_ACTION_HEAVY::set).build());
            fuelCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.fuelConsumeActionLightAircraft"), Config.FUEL_CONSUME_ACTION_LIGHT_AIRCRAFT.get())
                    .setDefaultValue(0).setSaveConsumer(Config.FUEL_CONSUME_ACTION_LIGHT_AIRCRAFT::set).build());
            fuelCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.fuelConsumeActionHeavyAircraft"), Config.FUEL_CONSUME_ACTION_HEAVY_AIRCRAFT.get())
                    .setDefaultValue(0).setSaveConsumer(Config.FUEL_CONSUME_ACTION_HEAVY_AIRCRAFT::set).build());

            // === Task Automation ===
            var taskCat = builder.getOrCreateCategory(Component.translatable("config.shincolle.task"));

            taskCat.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.shincolle.taskEnableCooking"), Config.TASK_ENABLE_COOKING.get())
                    .setSaveConsumer(Config.TASK_ENABLE_COOKING::set).build());
            taskCat.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.shincolle.taskEnableFishing"), Config.TASK_ENABLE_FISHING.get())
                    .setSaveConsumer(Config.TASK_ENABLE_FISHING::set).build());
            taskCat.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.shincolle.taskEnableMining"), Config.TASK_ENABLE_MINING.get())
                    .setSaveConsumer(Config.TASK_ENABLE_MINING::set).build());
            taskCat.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.shincolle.taskEnableCrafting"), Config.TASK_ENABLE_CRAFTING.get())
                    .setSaveConsumer(Config.TASK_ENABLE_CRAFTING::set).build());
            taskCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.tickFishingMin"), Config.TICK_FISHING_MIN.get())
                    .setDefaultValue(200).setSaveConsumer(Config.TICK_FISHING_MIN::set).build());
            taskCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.tickFishingMax"), Config.TICK_FISHING_MAX.get())
                    .setDefaultValue(600).setSaveConsumer(Config.TICK_FISHING_MAX::set).build());
            taskCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.tickMiningMin"), Config.TICK_MINING_MIN.get())
                    .setDefaultValue(60).setSaveConsumer(Config.TICK_MINING_MIN::set).build());
            taskCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.tickMiningMax"), Config.TICK_MINING_MAX.get())
                    .setDefaultValue(200).setSaveConsumer(Config.TICK_MINING_MAX::set).build());

            // === Hostile Ships ===
            var hostileCat = builder.getOrCreateCategory(Component.translatable("config.shincolle.hostile"));

            hostileCat.addEntry(entryBuilder.startDoubleField(Component.translatable("config.shincolle.hostileDropGrudgeRate"), Config.HOSTILE_DROP_GRUDGE_RATE.get())
                    .setDefaultValue(0.0D).setSaveConsumer(Config.HOSTILE_DROP_GRUDGE_RATE::set).build());
            hostileCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.hostileDeathMaxTicks"), Config.HOSTILE_DEATH_MAX_TICKS.get())
                    .setDefaultValue(300).setSaveConsumer(Config.HOSTILE_DEATH_MAX_TICKS::set).build());
            hostileCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.hostileDespawnBossTicks"), Config.HOSTILE_DESPAWN_BOSS_TICKS.get())
                    .setDefaultValue(24000).setSaveConsumer(Config.HOSTILE_DESPAWN_BOSS_TICKS::set).build());
            hostileCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.hostileDespawnMinionTicks"), Config.HOSTILE_DESPAWN_MINION_TICKS.get())
                    .setDefaultValue(72000).setSaveConsumer(Config.HOSTILE_DESPAWN_MINION_TICKS::set).build());
            hostileCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.hostileBossCooldownTicks"), Config.HOSTILE_BOSS_COOLDOWN_TICKS.get())
                    .setDefaultValue(72000).setSaveConsumer(Config.HOSTILE_BOSS_COOLDOWN_TICKS::set).build());
            hostileCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.hostileSpawnBossCount"), Config.HOSTILE_SPAWN_BOSS_COUNT.get())
                    .setDefaultValue(3).setSaveConsumer(Config.HOSTILE_SPAWN_BOSS_COUNT::set).build());
            hostileCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.hostileSpawnMinionCount"), Config.HOSTILE_SPAWN_MINION_COUNT.get())
                    .setDefaultValue(8).setSaveConsumer(Config.HOSTILE_SPAWN_MINION_COUNT::set).build());
            hostileCat.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.shincolle.hostileSpawnRequireRing"), Config.HOSTILE_SPAWN_REQUIRE_RING.get())
                    .setSaveConsumer(Config.HOSTILE_SPAWN_REQUIRE_RING::set).build());
            hostileCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.hostileMobSpawnMax"), Config.HOSTILE_MOB_SPAWN_MAX.get())
                    .setDefaultValue(100).setSaveConsumer(Config.HOSTILE_MOB_SPAWN_MAX::set).build());
            hostileCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.hostileMobSpawnChancePercent"), Config.HOSTILE_MOB_SPAWN_CHANCE_PERCENT.get())
                    .setDefaultValue(20).setSaveConsumer(Config.HOSTILE_MOB_SPAWN_CHANCE_PERCENT::set).build());

            // === Ship Interaction ===
            var interactCat = builder.getOrCreateCategory(Component.translatable("config.shincolle.interaction"));

            interactCat.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.shincolle.modernKitNotifyWhenMaxed"), Config.MODERN_KIT_NOTIFY_WHEN_MAXED.get())
                    .setSaveConsumer(Config.MODERN_KIT_NOTIFY_WHEN_MAXED::set).build());
            interactCat.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.shincolle.modernKitNotifyWhenMaxedActionBar"), Config.MODERN_KIT_NOTIFY_WHEN_MAXED_ACTION_BAR.get())
                    .setSaveConsumer(Config.MODERN_KIT_NOTIFY_WHEN_MAXED_ACTION_BAR::set).build());
            interactCat.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.shincolle.canTeleport"), Config.SHIP_CAN_TELEPORT.get())
                    .setSaveConsumer(Config.SHIP_CAN_TELEPORT::set).build());

            // === Debug ===
            var debugCat = builder.getOrCreateCategory(Component.translatable("config.shincolle.debug"));

            debugCat.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.shincolle.debugLogging"), Config.DEBUG_LOGGING.get())
                    .setSaveConsumer(Config.DEBUG_LOGGING::set).build());
            debugCat.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.shincolle.debugPerformanceLogging"), Config.DEBUG_PERFORMANCE_LOGGING.get())
                    .setSaveConsumer(Config.DEBUG_PERFORMANCE_LOGGING::set).build());
            debugCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.debugPerfSlowShipTickMs"), Config.DEBUG_PERF_SLOW_SHIP_TICK_MS.get())
                    .setDefaultValue(10).setSaveConsumer(Config.DEBUG_PERF_SLOW_SHIP_TICK_MS::set).build());
            debugCat.addEntry(entryBuilder.startIntField(Component.translatable("config.shincolle.debugPerfSlowTaskTickMs"), Config.DEBUG_PERF_SLOW_TASK_TICK_MS.get())
                    .setDefaultValue(10).setSaveConsumer(Config.DEBUG_PERF_SLOW_TASK_TICK_MS::set).build());

            // === Ship Sound ===
            var soundCat = builder.getOrCreateCategory(Component.translatable("config.shincolle.sound"));

            soundCat.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.shincolle.canTimeKeeping"), Config.SHIP_CAN_TIMEKEEPING.get())
                    .setSaveConsumer(Config.SHIP_CAN_TIMEKEEPING::set).build());
            soundCat.addEntry(entryBuilder.startDoubleField(Component.translatable("config.shincolle.volumeTimeKeeping"), Config.SHIP_VOLUME_TIMEKEEPING.get())
                    .setDefaultValue(1.0D).setSaveConsumer(Config.SHIP_VOLUME_TIMEKEEPING::set).build());
            soundCat.addEntry(entryBuilder.startDoubleField(Component.translatable("config.shincolle.volumeShip"), Config.SHIP_VOLUME_GENERAL.get())
                    .setDefaultValue(1.0D).setSaveConsumer(Config.SHIP_VOLUME_GENERAL::set).build());
            soundCat.addEntry(entryBuilder.startDoubleField(Component.translatable("config.shincolle.volumeAttack"), Config.SHIP_VOLUME_ATTACK.get())
                    .setDefaultValue(1.0D).setSaveConsumer(Config.SHIP_VOLUME_ATTACK::set).build());

            // === Client Visual ===
            var visualCat = builder.getOrCreateCategory(Component.translatable("config.shincolle.visual"));

            visualCat.addEntry(entryBuilder.startDoubleField(Component.translatable("config.shincolle.scaleHeldItem"), Config.CLIENT_SCALE_HELD_ITEM.get())
                    .setDefaultValue(1.0D).setSaveConsumer(Config.CLIENT_SCALE_HELD_ITEM::set).build());
            visualCat.addEntry(entryBuilder.startDoubleField(Component.translatable("config.shincolle.offsetHeldItemX"), Config.CLIENT_OFFSET_HELD_ITEM_X.get())
                    .setDefaultValue(0.0D).setSaveConsumer(Config.CLIENT_OFFSET_HELD_ITEM_X::set).build());
            visualCat.addEntry(entryBuilder.startDoubleField(Component.translatable("config.shincolle.offsetHeldItemY"), Config.CLIENT_OFFSET_HELD_ITEM_Y.get())
                    .setDefaultValue(0.0D).setSaveConsumer(Config.CLIENT_OFFSET_HELD_ITEM_Y::set).build());
            visualCat.addEntry(entryBuilder.startDoubleField(Component.translatable("config.shincolle.offsetHeldItemZ"), Config.CLIENT_OFFSET_HELD_ITEM_Z.get())
                    .setDefaultValue(0.0D).setSaveConsumer(Config.CLIENT_OFFSET_HELD_ITEM_Z::set).build());

            builder.setSavingRunnable(() -> {
                Config.SPEC.save();
                Config.CLIENT_SPEC.save();
            });

            return builder.build();
        }
    }
}
