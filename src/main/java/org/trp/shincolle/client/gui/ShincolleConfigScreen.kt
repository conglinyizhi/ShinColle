package org.trp.shincolle.client.gui

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.neoforged.fml.ModList
import org.trp.shincolle.Config
import org.trp.shincolle.Shincolle
import java.util.function.Consumer

/**
 * Entry point for the Cloth Config based settings screen.
 * Uses lazy loading so Cloth Config is NOT required at runtime.
 */
object ShincolleConfigScreen {
    private var checked = false
    var isAvailable: Boolean = false
        get() {
            if (!checked) {
                field = ModList.get().isLoaded("cloth_config")
                checked = true
            }
            return field
        }
        private set

    /**
     * Creates the config screen, or null if Cloth Config is not installed.
     */
    fun tryCreate(parent: Screen?): Screen? {
        if (!isAvailable) {
            Shincolle.LOGGER.warn("[Shincolle] Cloth Config is not installed, cannot open config screen")
            return null
        }
        return LazyScreen.create(parent)
    }

    /**
     * Separate inner class ensures Cloth Config classes are only loaded when first accessed.
     */
    private object LazyScreen {
        fun create(parent: Screen?): Screen {
            val builder: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
                me.shedaniel.clothconfig2.api.ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.translatable("config.shincolle.title"))

            val entryBuilder: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
                builder.entryBuilder()

            // === Ship EXP & Level ===
            val expCat: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
                builder.getOrCreateCategory(
                    Component.translatable("config.shincolle.ship_exp")
                )

            expCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.expModifier"),
                    Config.SHIP_EXP_MODIFIER.get()
                )
                    .setDefaultValue(1000).setSaveConsumer({ value: T? -> Config.SHIP_EXP_MODIFIER.set(value) }).build()
            )
            expCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.expGainMelee"),
                    Config.SHIP_EXP_GAIN_MELEE.get()
                )
                    .setDefaultValue(0).setSaveConsumer({ value: T? -> Config.SHIP_EXP_GAIN_MELEE.set(value) }).build()
            )
            expCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.expGainKill"),
                    Config.SHIP_EXP_GAIN_KILL.get()
                )
                    .setDefaultValue(8).setSaveConsumer({ value: T? -> Config.SHIP_EXP_GAIN_KILL.set(value) }).build()
            )
            expCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.expGainLightAttack"),
                    Config.SHIP_EXP_GAIN_LIGHT_ATTACK.get()
                )
                    .setDefaultValue(0).setSaveConsumer({ value: T? -> Config.SHIP_EXP_GAIN_LIGHT_ATTACK.set(value) })
                    .build()
            )
            expCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.expGainHeavyAttack"),
                    Config.SHIP_EXP_GAIN_HEAVY_ATTACK.get()
                )
                    .setDefaultValue(0).setSaveConsumer({ value: T? -> Config.SHIP_EXP_GAIN_HEAVY_ATTACK.set(value) })
                    .build()
            )
            expCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.expGainLightAircraft"),
                    Config.SHIP_EXP_GAIN_LIGHT_AIRCRAFT.get()
                )
                    .setDefaultValue(0).setSaveConsumer({ value: T? -> Config.SHIP_EXP_GAIN_LIGHT_AIRCRAFT.set(value) })
                    .build()
            )
            expCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.expGainHeavyAircraft"),
                    Config.SHIP_EXP_GAIN_HEAVY_AIRCRAFT.get()
                )
                    .setDefaultValue(0).setSaveConsumer({ value: T? -> Config.SHIP_EXP_GAIN_HEAVY_AIRCRAFT.set(value) })
                    .build()
            )
            expCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.maxLevelNormal"),
                    Config.SHIP_MAX_LEVEL_NORMAL.get()
                )
                    .setDefaultValue(100).setSaveConsumer({ value: T? -> Config.SHIP_MAX_LEVEL_NORMAL.set(value) })
                    .build()
            )
            expCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.maxLevelMarried"),
                    Config.SHIP_MAX_LEVEL_MARRIED.get()
                )
                    .setDefaultValue(150).setSaveConsumer({ value: T? -> Config.SHIP_MAX_LEVEL_MARRIED.set(value) })
                    .build()
            )
            expCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.trainingBookLevelMin"),
                    Config.TRAINING_BOOK_LEVEL_MIN.get()
                )
                    .setDefaultValue(1).setSaveConsumer({ value: T? -> Config.TRAINING_BOOK_LEVEL_MIN.set(value) })
                    .build()
            )
            expCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.trainingBookLevelMax"),
                    Config.TRAINING_BOOK_LEVEL_MAX.get()
                )
                    .setDefaultValue(5).setSaveConsumer({ value: T? -> Config.TRAINING_BOOK_LEVEL_MAX.set(value) })
                    .build()
            )

            // === Fuel Consumption ===
            val fuelCat: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
                builder.getOrCreateCategory(
                    Component.translatable("config.shincolle.fuel")
                )

            fuelCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.fuelDecayInterval"),
                    Config.FUEL_DECAY_INTERVAL.get()
                )
                    .setDefaultValue(100).setSaveConsumer({ value: T? -> Config.FUEL_DECAY_INTERVAL.set(value) })
                    .build()
            )
            fuelCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.fuelMoveDecayFactor"),
                    Config.FUEL_MOVE_DECAY_FACTOR.get()
                )
                    .setDefaultValue(1).setSaveConsumer({ value: T? -> Config.FUEL_MOVE_DECAY_FACTOR.set(value) })
                    .build()
            )
            fuelCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.fuelConsumeDD"),
                    Config.FUEL_CONSUME_DD.get()
                )
                    .setDefaultValue(1).setSaveConsumer({ value: T? -> Config.FUEL_CONSUME_DD.set(value) }).build()
            )
            fuelCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.fuelConsumeCL"),
                    Config.FUEL_CONSUME_CL.get()
                )
                    .setDefaultValue(1).setSaveConsumer({ value: T? -> Config.FUEL_CONSUME_CL.set(value) }).build()
            )
            fuelCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.fuelConsumeCA"),
                    Config.FUEL_CONSUME_CA.get()
                )
                    .setDefaultValue(2).setSaveConsumer({ value: T? -> Config.FUEL_CONSUME_CA.set(value) }).build()
            )
            fuelCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.fuelConsumeCAV"),
                    Config.FUEL_CONSUME_CAV.get()
                )
                    .setDefaultValue(2).setSaveConsumer({ value: T? -> Config.FUEL_CONSUME_CAV.set(value) }).build()
            )
            fuelCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.fuelConsumeCLT"),
                    Config.FUEL_CONSUME_CLT.get()
                )
                    .setDefaultValue(2).setSaveConsumer({ value: T? -> Config.FUEL_CONSUME_CLT.set(value) }).build()
            )
            fuelCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.fuelConsumeCVL"),
                    Config.FUEL_CONSUME_CVL.get()
                )
                    .setDefaultValue(3).setSaveConsumer({ value: T? -> Config.FUEL_CONSUME_CVL.set(value) }).build()
            )
            fuelCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.fuelConsumeCV"),
                    Config.FUEL_CONSUME_CV.get()
                )
                    .setDefaultValue(3).setSaveConsumer({ value: T? -> Config.FUEL_CONSUME_CV.set(value) }).build()
            )
            fuelCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.fuelConsumeBB"),
                    Config.FUEL_CONSUME_BB.get()
                )
                    .setDefaultValue(4).setSaveConsumer({ value: T? -> Config.FUEL_CONSUME_BB.set(value) }).build()
            )
            fuelCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.fuelConsumeBBV"),
                    Config.FUEL_CONSUME_BBV.get()
                )
                    .setDefaultValue(4).setSaveConsumer({ value: T? -> Config.FUEL_CONSUME_BBV.set(value) }).build()
            )
            fuelCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.fuelConsumeSS"),
                    Config.FUEL_CONSUME_SS.get()
                )
                    .setDefaultValue(1).setSaveConsumer({ value: T? -> Config.FUEL_CONSUME_SS.set(value) }).build()
            )
            fuelCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.fuelConsumeAP"),
                    Config.FUEL_CONSUME_AP.get()
                )
                    .setDefaultValue(1).setSaveConsumer({ value: T? -> Config.FUEL_CONSUME_AP.set(value) }).build()
            )
            fuelCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.fuelConsumeActionLight"),
                    Config.FUEL_CONSUME_ACTION_LIGHT.get()
                )
                    .setDefaultValue(0).setSaveConsumer({ value: T? -> Config.FUEL_CONSUME_ACTION_LIGHT.set(value) })
                    .build()
            )
            fuelCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.fuelConsumeActionHeavy"),
                    Config.FUEL_CONSUME_ACTION_HEAVY.get()
                )
                    .setDefaultValue(0).setSaveConsumer({ value: T? -> Config.FUEL_CONSUME_ACTION_HEAVY.set(value) })
                    .build()
            )
            fuelCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.fuelConsumeActionLightAircraft"),
                    Config.FUEL_CONSUME_ACTION_LIGHT_AIRCRAFT.get()
                )
                    .setDefaultValue(0)
                    .setSaveConsumer({ value: T? -> Config.FUEL_CONSUME_ACTION_LIGHT_AIRCRAFT.set(value) }).build()
            )
            fuelCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.fuelConsumeActionHeavyAircraft"),
                    Config.FUEL_CONSUME_ACTION_HEAVY_AIRCRAFT.get()
                )
                    .setDefaultValue(0)
                    .setSaveConsumer({ value: T? -> Config.FUEL_CONSUME_ACTION_HEAVY_AIRCRAFT.set(value) }).build()
            )

            // === Task Automation ===
            val taskCat: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
                builder.getOrCreateCategory(
                    Component.translatable("config.shincolle.task")
                )

            taskCat.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.translatable("config.shincolle.taskEnableCooking"),
                    Config.TASK_ENABLE_COOKING.get()
                )
                    .setSaveConsumer({ value: T? -> Config.TASK_ENABLE_COOKING.set(value) }).build()
            )
            taskCat.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.translatable("config.shincolle.taskEnableFishing"),
                    Config.TASK_ENABLE_FISHING.get()
                )
                    .setSaveConsumer({ value: T? -> Config.TASK_ENABLE_FISHING.set(value) }).build()
            )
            taskCat.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.translatable("config.shincolle.taskEnableMining"),
                    Config.TASK_ENABLE_MINING.get()
                )
                    .setSaveConsumer({ value: T? -> Config.TASK_ENABLE_MINING.set(value) }).build()
            )
            taskCat.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.translatable("config.shincolle.taskEnableCrafting"),
                    Config.TASK_ENABLE_CRAFTING.get()
                )
                    .setSaveConsumer({ value: T? -> Config.TASK_ENABLE_CRAFTING.set(value) }).build()
            )
            taskCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.tickFishingMin"),
                    Config.TICK_FISHING_MIN.get()
                )
                    .setDefaultValue(200).setSaveConsumer({ value: T? -> Config.TICK_FISHING_MIN.set(value) }).build()
            )
            taskCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.tickFishingMax"),
                    Config.TICK_FISHING_MAX.get()
                )
                    .setDefaultValue(600).setSaveConsumer({ value: T? -> Config.TICK_FISHING_MAX.set(value) }).build()
            )
            taskCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.tickMiningMin"),
                    Config.TICK_MINING_MIN.get()
                )
                    .setDefaultValue(60).setSaveConsumer({ value: T? -> Config.TICK_MINING_MIN.set(value) }).build()
            )
            taskCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.tickMiningMax"),
                    Config.TICK_MINING_MAX.get()
                )
                    .setDefaultValue(200).setSaveConsumer({ value: T? -> Config.TICK_MINING_MAX.set(value) }).build()
            )

            // === Hostile Ships ===
            val hostileCat: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
                builder.getOrCreateCategory(
                    Component.translatable("config.shincolle.hostile")
                )

            hostileCat.addEntry(
                formattedDouble(
                    entryBuilder,
                    Component.translatable("config.shincolle.hostileDropGrudgeRate"),
                    Config.HOSTILE_DROP_GRUDGE_RATE.get(),
                    0.0,
                    Consumer { value: Double? -> Config.HOSTILE_DROP_GRUDGE_RATE.set(value) })
            )
            hostileCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.hostileDeathMaxTicks"),
                    Config.HOSTILE_DEATH_MAX_TICKS.get()
                )
                    .setDefaultValue(300).setSaveConsumer({ value: T? -> Config.HOSTILE_DEATH_MAX_TICKS.set(value) })
                    .build()
            )
            hostileCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.hostileDespawnBossTicks"),
                    Config.HOSTILE_DESPAWN_BOSS_TICKS.get()
                )
                    .setDefaultValue(24000)
                    .setSaveConsumer({ value: T? -> Config.HOSTILE_DESPAWN_BOSS_TICKS.set(value) })
                    .build()
            )
            hostileCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.hostileDespawnMinionTicks"),
                    Config.HOSTILE_DESPAWN_MINION_TICKS.get()
                )
                    .setDefaultValue(72000)
                    .setSaveConsumer({ value: T? -> Config.HOSTILE_DESPAWN_MINION_TICKS.set(value) })
                    .build()
            )
            hostileCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.hostileBossCooldownTicks"),
                    Config.HOSTILE_BOSS_COOLDOWN_TICKS.get()
                )
                    .setDefaultValue(72000)
                    .setSaveConsumer({ value: T? -> Config.HOSTILE_BOSS_COOLDOWN_TICKS.set(value) })
                    .build()
            )
            hostileCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.hostileSpawnBossCount"),
                    Config.HOSTILE_SPAWN_BOSS_COUNT.get()
                )
                    .setDefaultValue(3).setSaveConsumer({ value: T? -> Config.HOSTILE_SPAWN_BOSS_COUNT.set(value) })
                    .build()
            )
            hostileCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.hostileSpawnMinionCount"),
                    Config.HOSTILE_SPAWN_MINION_COUNT.get()
                )
                    .setDefaultValue(8).setSaveConsumer({ value: T? -> Config.HOSTILE_SPAWN_MINION_COUNT.set(value) })
                    .build()
            )
            hostileCat.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.translatable("config.shincolle.hostileSpawnRequireRing"),
                    Config.HOSTILE_SPAWN_REQUIRE_RING.get()
                )
                    .setSaveConsumer({ value: T? -> Config.HOSTILE_SPAWN_REQUIRE_RING.set(value) }).build()
            )
            hostileCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.hostileMobSpawnMax"),
                    Config.HOSTILE_MOB_SPAWN_MAX.get()
                )
                    .setDefaultValue(100).setSaveConsumer({ value: T? -> Config.HOSTILE_MOB_SPAWN_MAX.set(value) })
                    .build()
            )
            hostileCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.hostileMobSpawnChancePercent"),
                    Config.HOSTILE_MOB_SPAWN_CHANCE_PERCENT.get()
                )
                    .setDefaultValue(20)
                    .setSaveConsumer({ value: T? -> Config.HOSTILE_MOB_SPAWN_CHANCE_PERCENT.set(value) }).build()
            )
            hostileCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.hostileMobSpawnGroups"),
                    Config.HOSTILE_MOB_SPAWN_GROUPS.get()
                )
                    .setDefaultValue(1).setSaveConsumer({ value: T? -> Config.HOSTILE_MOB_SPAWN_GROUPS.set(value) })
                    .build()
            )
            hostileCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.hostileMobSpawnGroupMin"),
                    Config.HOSTILE_MOB_SPAWN_GROUP_MIN.get()
                )
                    .setDefaultValue(1).setSaveConsumer({ value: T? -> Config.HOSTILE_MOB_SPAWN_GROUP_MIN.set(value) })
                    .build()
            )
            hostileCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.hostileMobSpawnGroupMax"),
                    Config.HOSTILE_MOB_SPAWN_GROUP_MAX.get()
                )
                    .setDefaultValue(1).setSaveConsumer({ value: T? -> Config.HOSTILE_MOB_SPAWN_GROUP_MAX.set(value) })
                    .build()
            )

            // === Ship Interaction ===
            val interactCat: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
                builder.getOrCreateCategory(
                    Component.translatable("config.shincolle.interaction")
                )

            interactCat.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.translatable("config.shincolle.modernKitNotifyWhenMaxed"),
                    Config.MODERN_KIT_NOTIFY_WHEN_MAXED.get()
                )
                    .setSaveConsumer({ value: T? -> Config.MODERN_KIT_NOTIFY_WHEN_MAXED.set(value) }).build()
            )
            interactCat.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.translatable("config.shincolle.modernKitNotifyWhenMaxedActionBar"),
                    Config.MODERN_KIT_NOTIFY_WHEN_MAXED_ACTION_BAR.get()
                )
                    .setSaveConsumer({ value: T? -> Config.MODERN_KIT_NOTIFY_WHEN_MAXED_ACTION_BAR.set(value) }).build()
            )
            interactCat.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.translatable("config.shincolle.canTeleport"),
                    Config.SHIP_CAN_TELEPORT.get()
                )
                    .setSaveConsumer({ value: T? -> Config.SHIP_CAN_TELEPORT.set(value) }).build()
            )
            interactCat.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.translatable("config.shincolle.freezeWhenGuiOpen"),
                    Config.SHIP_FREEZE_WHEN_GUI_OPEN.get()
                )
                    .setSaveConsumer({ value: T? -> Config.SHIP_FREEZE_WHEN_GUI_OPEN.set(value) }).build()
            )
            interactCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.buffDuration"),
                    Config.SHIP_BUFF_DURATION.get()
                )
                    .setDefaultValue(600).setSaveConsumer({ value: T? -> Config.SHIP_BUFF_DURATION.set(value) }).build()
            )

            // === Debug ===
            val debugCat: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
                builder.getOrCreateCategory(
                    Component.translatable("config.shincolle.debug")
                )

            debugCat.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.translatable("config.shincolle.debugLogging"),
                    Config.DEBUG_LOGGING.get()
                )
                    .setSaveConsumer({ value: T? -> Config.DEBUG_LOGGING.set(value) }).build()
            )
            debugCat.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.translatable("config.shincolle.debugPerformanceLogging"),
                    Config.DEBUG_PERFORMANCE_LOGGING.get()
                )
                    .setSaveConsumer({ value: T? -> Config.DEBUG_PERFORMANCE_LOGGING.set(value) }).build()
            )
            debugCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.debugPerfSlowShipTickMs"),
                    Config.DEBUG_PERF_SLOW_SHIP_TICK_MS.get()
                )
                    .setDefaultValue(10)
                    .setSaveConsumer({ value: T? -> Config.DEBUG_PERF_SLOW_SHIP_TICK_MS.set(value) })
                    .build()
            )
            debugCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.debugPerfSlowTaskTickMs"),
                    Config.DEBUG_PERF_SLOW_TASK_TICK_MS.get()
                )
                    .setDefaultValue(10)
                    .setSaveConsumer({ value: T? -> Config.DEBUG_PERF_SLOW_TASK_TICK_MS.set(value) })
                    .build()
            )
            debugCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.debugPerfSlowBlockEntityTickMs"),
                    Config.DEBUG_PERF_SLOW_BLOCK_ENTITY_TICK_MS.get()
                )
                    .setDefaultValue(5)
                    .setSaveConsumer({ value: T? -> Config.DEBUG_PERF_SLOW_BLOCK_ENTITY_TICK_MS.set(value) }).build()
            )
            debugCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.debugPerfSlowProjectileTickMs"),
                    Config.DEBUG_PERF_SLOW_PROJECTILE_TICK_MS.get()
                )
                    .setDefaultValue(5)
                    .setSaveConsumer({ value: T? -> Config.DEBUG_PERF_SLOW_PROJECTILE_TICK_MS.set(value) }).build()
            )
            debugCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.debugPerfSlowServerTickMs"),
                    Config.DEBUG_PERF_SLOW_SERVER_TICK_MS.get()
                )
                    .setDefaultValue(40)
                    .setSaveConsumer({ value: T? -> Config.DEBUG_PERF_SLOW_SERVER_TICK_MS.set(value) })
                    .build()
            )
            debugCat.addEntry(
                entryBuilder.startIntField(
                    Component.translatable("config.shincolle.debugPerfMinLogIntervalTicks"),
                    Config.DEBUG_PERF_MIN_LOG_INTERVAL_TICKS.get()
                )
                    .setDefaultValue(20)
                    .setSaveConsumer({ value: T? -> Config.DEBUG_PERF_MIN_LOG_INTERVAL_TICKS.set(value) }).build()
            )

            // === Ship Sound ===
            val soundCat: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
                builder.getOrCreateCategory(
                    Component.translatable("config.shincolle.sound")
                )

            soundCat.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.translatable("config.shincolle.canTimeKeeping"),
                    Config.SHIP_CAN_TIMEKEEPING.get()
                )
                    .setSaveConsumer({ value: T? -> Config.SHIP_CAN_TIMEKEEPING.set(value) }).build()
            )
            soundCat.addEntry(
                formattedDouble(
                    entryBuilder,
                    Component.translatable("config.shincolle.volumeTimeKeeping"),
                    Config.SHIP_VOLUME_TIMEKEEPING.get(),
                    1.0,
                    Consumer { value: Double? -> Config.SHIP_VOLUME_TIMEKEEPING.set(value) })
            )
            soundCat.addEntry(
                formattedDouble(
                    entryBuilder,
                    Component.translatable("config.shincolle.volumeShip"),
                    Config.SHIP_VOLUME_GENERAL.get(),
                    0.6,
                    Consumer { value: Double? -> Config.SHIP_VOLUME_GENERAL.set(value) })
            )
            soundCat.addEntry(
                formattedDouble(
                    entryBuilder,
                    Component.translatable("config.shincolle.volumeAttack"),
                    Config.SHIP_VOLUME_ATTACK.get(),
                    0.7,
                    Consumer { value: Double? -> Config.SHIP_VOLUME_ATTACK.set(value) })
            )
            // === Client Visual ===
            val visualCat: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
                builder.getOrCreateCategory(
                    Component.translatable("config.shincolle.visual")
                )

            visualCat.addEntry(
                formattedDouble(
                    entryBuilder,
                    Component.translatable("config.shincolle.scaleHeldItem"),
                    Config.CLIENT_SCALE_HELD_ITEM.get(),
                    1.0,
                    Consumer { value: Double? -> Config.CLIENT_SCALE_HELD_ITEM.set(value) })
            )
            visualCat.addEntry(
                formattedDouble(
                    entryBuilder,
                    Component.translatable("config.shincolle.offsetHeldItemX"),
                    Config.CLIENT_OFFSET_HELD_ITEM_X.get(),
                    0.0,
                    Consumer { value: Double? -> Config.CLIENT_OFFSET_HELD_ITEM_X.set(value) })
            )
            visualCat.addEntry(
                formattedDouble(
                    entryBuilder,
                    Component.translatable("config.shincolle.offsetHeldItemY"),
                    Config.CLIENT_OFFSET_HELD_ITEM_Y.get(),
                    0.0,
                    Consumer { value: Double? -> Config.CLIENT_OFFSET_HELD_ITEM_Y.set(value) })
            )
            visualCat.addEntry(
                formattedDouble(
                    entryBuilder,
                    Component.translatable("config.shincolle.offsetHeldItemZ"),
                    Config.CLIENT_OFFSET_HELD_ITEM_Z.get(),
                    0.0,
                    Consumer { value: Double? -> Config.CLIENT_OFFSET_HELD_ITEM_Z.set(value) })
            )
            visualCat.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.translatable("config.shincolle.useMiSansFont"),
                    Config.USE_MISANS_FONT.get()
                )
                    .setSaveConsumer({ value: T? -> Config.USE_MISANS_FONT.set(value) }).build()
            )
            visualCat.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.translatable("config.shincolle.miSansOnlyForLegacyLogs"),
                    Config.MISANS_ONLY_LEGACY_LOGS.get()
                )
                    .setSaveConsumer({ value: T? -> Config.MISANS_ONLY_LEGACY_LOGS.set(value) }).build()
            )
            builder.setSavingRunnable({
                Config.SPEC.save()
                Config.CLIENT_SPEC.save()
            })

            return builder.build()
        }

        fun formattedDouble(
            eb: me.shedaniel.clothconfig2.api.ConfigEntryBuilder,
            name: Component?, value: Double, defaultValue: Double,
            saver: Consumer<Double?>?
        ): me.shedaniel.clothconfig2.gui.entries.DoubleListEntry {
            val entry: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
                eb.startDoubleField(name, value)
                    .setDefaultValue(defaultValue)
                    .setSaveConsumer(saver)
                    .build()
            // Format to avoid floating-point display artifacts (0.699999... instead of 0.7)
            val formatted = String.format("%.4f", value).replace("\\.?0*$".toRegex(), "")
            entry.setValue(formatted)
            return entry
        }
    }
}
