package org.trp.shincolle.build

import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ConfigSpecBuilderArchitectureRegressionTest {
    private val CONFIG_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/Config.kt")
    private val CONFIG_SPEC_BUILDER_SOURCE: Path = Path.of("src/main/java/org/trp/shincolle/ConfigSpecBuilder.kt")

    @Test
    fun configShouldDelegateSpecRegistrationToDedicatedBuilder() {
        val configSource = Files.readString(CONFIG_SOURCE)
        val builderSource = Files.readString(CONFIG_SPEC_BUILDER_SOURCE)

        assertTrue(configSource.contains("ConfigSpecBuilder.buildCommonSpec()")) {
            "Config should delegate common spec registration to ConfigSpecBuilder"
        }
        assertTrue(configSource.contains("ConfigSpecBuilder.buildClientSpec()")) {
            "Config should delegate client spec registration to ConfigSpecBuilder"
        }
        assertTrue(builderSource.contains("internal object ConfigSpecBuilder")) {
            "Config spec registration should live in a dedicated builder object"
        }
        assertTrue(builderSource.contains("fun buildCommonSpec()")) {
            "ConfigSpecBuilder should keep a dedicated common-spec entrypoint"
        }
        assertTrue(builderSource.contains("fun buildClientSpec()")) {
            "ConfigSpecBuilder should keep a dedicated client-spec entrypoint"
        }
        assertTrue(builderSource.contains("define(\"modernKitNotifyWhenMaxed\", Config.modernKitNotifyWhenMaxed)")) {
            "Ship interaction config registration should remain in the dedicated config spec builder"
        }
        assertTrue(builderSource.contains("define(\"debugLogging\", Config.debugLogging)")) {
            "Debug config registration should remain in the dedicated config spec builder"
        }
        assertTrue(builderSource.contains("defineList<String?>(\n                \"miningEntries\"")) {
            "List-based common config registration should remain in the dedicated config spec builder"
        }
        assertTrue(builderSource.contains("defineList<String?>(\n                \"customSoundRates\"")) {
            "List-based client config registration should remain in the dedicated config spec builder"
        }
    }
}
