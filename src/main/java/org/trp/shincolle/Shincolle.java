package org.trp.shincolle;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.trp.shincolle.event.ModCapabilityEvents;
import org.trp.shincolle.init.*;
import org.trp.shincolle.menu.ModMenus;
import org.slf4j.Logger;

@Mod(Shincolle.MODID)
public class Shincolle {
    public static final String MODID = "shincolle";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void debugLog(String message, Object... args) {
        if (!Config.debugLogging) {
            return;
        }
        LOGGER.info("[ShinColleDebug] " + message, args);
    }

    public static void diagnosticLog(String message, Object... args) {
        LOGGER.info("[ShinColleDiag] " + message, args);
    }

    public static void perfLog(String message, Object... args) {
        if (!Config.debugPerformanceLogging) {
            return;
        }
        LOGGER.info("[ShinCollePerf] " + message, args);
    }

    public Shincolle(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

        ModItems.ITEMS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModDataAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);
        modEventBus.addListener(ModCapabilityEvents::registerCapabilities);
    }
}
