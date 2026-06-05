package org.trp.shincolle

import com.mojang.logging.LogUtils
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import org.slf4j.Logger
import org.trp.shincolle.event.ModCapabilityEvents
import org.trp.shincolle.init.*
import org.trp.shincolle.menu.ModMenus
import java.util.function.Consumer

@Mod(Shincolle.Companion.MODID)
class Shincolle(modEventBus: IEventBus, modContainer: ModContainer) {
    init {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC)
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC)

        ModItems.ITEMS.register(modEventBus)
        ModDataComponents.DATA_COMPONENTS.register(modEventBus)
        ModBlocks.BLOCKS.register(modEventBus)
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus)
        ModTabs.CREATIVE_MODE_TABS.register(modEventBus)
        ModEntities.ENTITY_TYPES.register(modEventBus)
        ModMemoryModules.MEMORY_MODULE_TYPES.register(modEventBus)
        ModMenus.MENUS.register(modEventBus)
        ModParticles.PARTICLES.register(modEventBus)
        ModSounds.SOUND_EVENTS.register(modEventBus)
        ModDataAttachments.ATTACHMENT_TYPES.register(modEventBus)
        ModLootModifiers.LOOT_MODIFIERS.register(modEventBus)
        modEventBus.addListener<RegisterCapabilitiesEvent?>(Consumer { event: RegisterCapabilitiesEvent? ->
            ModCapabilityEvents.registerCapabilities(
                event
            )
        })

        // Config screen registered in ClientModEventBusEvents (client-only)
    }

    companion object {
        const val MODID: String = "shincolle"
        @JvmField
        val LOGGER: Logger = LogUtils.getLogger()

        @JvmStatic
        fun debugLog(message: String?, vararg args: Any?) {
            if (!Config.debugLogging) {
                return
            }
            LOGGER.info("[ShinColleDebug] " + message, *args)
        }

        @JvmStatic
        fun diagnosticLog(message: String?, vararg args: Any?) {
            LOGGER.info("[ShinColleDiag] " + message, *args)
        }

        @JvmStatic
        fun perfLog(message: String?, vararg args: Any?) {
            if (!Config.debugPerformanceLogging) {
                return
            }
            LOGGER.info("[ShinCollePerf] " + message, *args)
        }
    }
}
