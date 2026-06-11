package org.trp.shincolle.build

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ModBootstrapArchitectureRegressionTest {
    private val MOD_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/Shincolle.kt")
    private val BOOTSTRAP_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/init/ModBootstrap.kt")

    @Test
    fun shincolleShouldDelegateRegistryAndConfigBootstrapToDedicatedCoordinator() {
        val modSource = Files.readString(MOD_SOURCE)
        val bootstrapSource = Files.readString(BOOTSTRAP_SOURCE)

        assertTrue(modSource.contains("ModBootstrap.initialize(modEventBus, modContainer)")) {
            "Shincolle entrypoint should delegate mod bootstrap work to ModBootstrap"
        }
        assertTrue(bootstrapSource.contains("object ModBootstrap")) {
            "Registry/config bootstrap should live in a dedicated coordinator"
        }
        assertTrue(bootstrapSource.contains("fun initialize(modEventBus: IEventBus, modContainer: ModContainer)")) {
            "ModBootstrap should keep a single initialize entrypoint"
        }
        assertTrue(bootstrapSource.contains("private fun registerConfigs(modContainer: ModContainer)")) {
            "ModBootstrap should keep config registration isolated from the mod entrypoint"
        }
        assertTrue(bootstrapSource.contains("private fun registerDeferredContent(modEventBus: IEventBus)")) {
            "ModBootstrap should keep deferred register wiring isolated from the mod entrypoint"
        }
        assertTrue(bootstrapSource.contains("private fun registerCapabilities(modEventBus: IEventBus)")) {
            "ModBootstrap should keep capability listener wiring isolated from the mod entrypoint"
        }
        assertTrue(bootstrapSource.contains("modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC)")) {
            "ModBootstrap should keep common config registration"
        }
        assertTrue(bootstrapSource.contains("ModItems.ITEMS.register(modEventBus)")) {
            "ModBootstrap should keep item registry wiring"
        }
        assertTrue(bootstrapSource.contains("ModMemoryModules.MEMORY_MODULE_TYPES.register(modEventBus)")) {
            "ModBootstrap should keep memory-module registry wiring"
        }
        assertTrue(bootstrapSource.contains("ModCapabilityEvents.registerCapabilities(event)")) {
            "ModBootstrap should keep capability registration delegated through ModCapabilityEvents"
        }
    }
}
