package org.trp.shincolle.build

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class ClientConfigCoverageRegressionTest {
    private data class ExposedClientConfig(val translationKey: String, val configField: String)

    private data class HiddenClientConfig(val configField: String, val runtimeEvidence: String)

    private val CONFIG_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/Config.kt")
    private val CONFIG_SCREEN_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/client/gui/ShincolleConfigScreen.kt")
    private val EN_US_LANG: Path =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json")

    private val EXPOSED_CONFIGS: List<ExposedClientConfig> = listOf(
            ExposedClientConfig("config.shincolle.canTimeKeeping", "SHIP_CAN_TIMEKEEPING"),
            ExposedClientConfig("config.shincolle.volumeTimeKeeping", "SHIP_VOLUME_TIMEKEEPING"),
            ExposedClientConfig("config.shincolle.volumeShip", "SHIP_VOLUME_GENERAL"),
            ExposedClientConfig("config.shincolle.volumeAttack", "SHIP_VOLUME_ATTACK"),
            ExposedClientConfig("config.shincolle.scaleHeldItem", "CLIENT_SCALE_HELD_ITEM"),
            ExposedClientConfig("config.shincolle.offsetHeldItemX", "CLIENT_OFFSET_HELD_ITEM_X"),
            ExposedClientConfig("config.shincolle.offsetHeldItemY", "CLIENT_OFFSET_HELD_ITEM_Y"),
            ExposedClientConfig("config.shincolle.offsetHeldItemZ", "CLIENT_OFFSET_HELD_ITEM_Z"),
            ExposedClientConfig("config.shincolle.useMiSansFont", "USE_MISANS_FONT"),
            ExposedClientConfig("config.shincolle.miSansOnlyForLegacyLogs", "MISANS_ONLY_LEGACY_LOGS")
    )

    private val HIDDEN_CONFIGS: List<HiddenClientConfig> = listOf(
            HiddenClientConfig("CUSTOM_SOUND_RATES",
                    "customSoundRates = parseCustomSoundRates(CUSTOM_SOUND_RATES.get());")
    )

    @Test
    fun scalarClientConfigDefinitionsShouldRemainCoveredByConfigScreen() {
        val config = Files.readString(CONFIG_SOURCE)
        val configScreen = Files.readString(CONFIG_SCREEN_SOURCE)
        val enUs = Files.readString(EN_US_LANG)

        for (expectation in EXPOSED_CONFIGS) {
            assertTrue(config.contains(expectation.configField)) {
                "Expected Config to keep defining " + expectation.configField
            }
            assertTrue(configScreen.contains("Component.translatable(\"" + expectation.translationKey + "\")")
                            && configScreen.contains("Config." + expectation.configField + ".get()")
                            && configScreen.contains("Config." + expectation.configField + "::set")) {
                "Expected ShincolleConfigScreen to keep exposing " + expectation.configField
            }
            assertTrue(enUs.contains("\"" + expectation.translationKey + "\"")) {
                "Expected en_us to keep defining " + expectation.translationKey
            }
        }
    }

    @Test
    fun listBasedClientConfigShouldRemainDocumentedAsIntentionallyHiddenFromConfigScreen() {
        val config = Files.readString(CONFIG_SOURCE)
        val configScreen = Files.readString(CONFIG_SCREEN_SOURCE)

        for (expectation in HIDDEN_CONFIGS) {
            assertTrue(config.contains(expectation.configField)) {
                "Expected Config to keep defining " + expectation.configField
            }
            assertTrue(config.contains(expectation.runtimeEvidence)) {
                "Expected Config.onLoad to keep syncing " + expectation.configField
            }
            assertFalse(configScreen.contains("Config." + expectation.configField + ".get()")) {
                "Expected ShincolleConfigScreen to keep " + expectation.configField +
                        " out of the current scalar-focused UI"
            }
        }
    }
}
