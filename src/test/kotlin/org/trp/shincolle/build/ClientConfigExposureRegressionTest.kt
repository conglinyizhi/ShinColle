package org.trp.shincolle.build

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class ClientConfigExposureRegressionTest {
    private data class ClientConfigExpectation(
            val translationKey: String,
            val configField: String,
            val runtimeEvidence: String
    )

    private val CONFIG_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/Config.kt")
    private val CONFIG_SCREEN_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/client/gui/ShincolleConfigScreen.kt")
    private val EN_US_LANG: Path =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json")

    private val EXPECTATIONS: List<ClientConfigExpectation> = listOf(
            ClientConfigExpectation("config.shincolle.volumeTimeKeeping", "SHIP_VOLUME_TIMEKEEPING",
                    "volumeTimeKeeping = SHIP_VOLUME_TIMEKEEPING.get().floatValue();"),
            ClientConfigExpectation("config.shincolle.volumeShip", "SHIP_VOLUME_GENERAL",
                    "volumeShip = SHIP_VOLUME_GENERAL.get().floatValue();"),
            ClientConfigExpectation("config.shincolle.volumeAttack", "SHIP_VOLUME_ATTACK",
                    "volumeAttack = SHIP_VOLUME_ATTACK.get().floatValue();"),
            ClientConfigExpectation("config.shincolle.scaleHeldItem", "CLIENT_SCALE_HELD_ITEM",
                    "scaleHeldItem = CLIENT_SCALE_HELD_ITEM.get().floatValue();"),
            ClientConfigExpectation("config.shincolle.offsetHeldItemX", "CLIENT_OFFSET_HELD_ITEM_X",
                    "offsetHeldItemX = CLIENT_OFFSET_HELD_ITEM_X.get().floatValue();"),
            ClientConfigExpectation("config.shincolle.offsetHeldItemY", "CLIENT_OFFSET_HELD_ITEM_Y",
                    "offsetHeldItemY = CLIENT_OFFSET_HELD_ITEM_Y.get().floatValue();"),
            ClientConfigExpectation("config.shincolle.offsetHeldItemZ", "CLIENT_OFFSET_HELD_ITEM_Z",
                    "offsetHeldItemZ = CLIENT_OFFSET_HELD_ITEM_Z.get().floatValue();"),
            ClientConfigExpectation("config.shincolle.useMiSansFont", "USE_MISANS_FONT",
                    "useMiSansFont = USE_MISANS_FONT.get();"),
            ClientConfigExpectation("config.shincolle.miSansOnlyForLegacyLogs", "MISANS_ONLY_LEGACY_LOGS",
                    "miSansOnlyForLegacyLogs = MISANS_ONLY_LEGACY_LOGS.get();")
    )

    @Test
    fun importantClientConfigEntriesShouldRemainExposedInConfigScreen() {
        val config = Files.readString(CONFIG_SOURCE)
        val configScreen = Files.readString(CONFIG_SCREEN_SOURCE)
        val enUs = Files.readString(EN_US_LANG)

        for (expectation in EXPECTATIONS) {
            assertTrue(config.contains("public static final ModConfigSpec.")
                            && config.contains(expectation.configField)) {
                "Expected Config to keep defining " + expectation.configField
            }
            assertTrue(config.contains(expectation.runtimeEvidence)) {
                "Expected Config.onLoad to keep syncing " + expectation.configField
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
}
