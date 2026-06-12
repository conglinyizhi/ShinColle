package org.trp.shincolle.init

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.data.event.GatherDataEvent
import org.trp.shincolle.Config
import org.trp.shincolle.datagen.ShincolleItemModelProvider
import org.trp.shincolle.event.ModCapabilityEvents
import org.trp.shincolle.menu.ModMenus
import java.util.function.Consumer

object ModBootstrap {
    fun initialize(modEventBus: IEventBus, modContainer: ModContainer) {
        registerConfigs(modContainer)
        registerDeferredContent(modEventBus)
        registerCapabilities(modEventBus)
        registerDataProviders(modEventBus)
    }

    private fun registerConfigs(modContainer: ModContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC)
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC)
    }

    private fun registerDeferredContent(modEventBus: IEventBus) {
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
    }

    private fun registerCapabilities(modEventBus: IEventBus) {
        modEventBus.addListener<RegisterCapabilitiesEvent>(Consumer { event ->
            ModCapabilityEvents.registerCapabilities(event)
        })
    }

    private fun registerDataProviders(modEventBus: IEventBus) {
        modEventBus.addListener<GatherDataEvent>(Consumer { event ->
            if (event.includeClient()) {
                val generator = event.generator
                generator.addProvider(
                    true,
                    ShincolleItemModelProvider(generator.packOutput, event.existingFileHelper)
                )
            }
        })
    }
}
