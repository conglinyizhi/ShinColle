package org.trp.shincolle.client

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.nio.file.Files
import java.nio.file.Path

class ShipyardTooltipRegressionTest {
    private val SMALL_SCREEN_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/client/screen/SmallShipyardScreen.kt")
    private val LARGE_SCREEN_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/client/screen/LargeShipyardScreen.kt")
    private val EN_US_LANG: Path =
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val JA_JP_LANG: Path =
        Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json")
    private val ZH_CN_LANG: Path =
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")
    private val ZH_TW_LANG: Path =
        Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")

    private val SHIPYARD_TOOLTIP_KEYS: List<String> = listOf(
        "gui.shincolle.shipyard.fuel_slot",
        "gui.shincolle.shipyard.instant_tip"
    )

    @Test
    fun bothShipyardScreensShouldKeepFuelSlotTooltipContent() {
        val small = Files.readString(SMALL_SCREEN_SOURCE)
        val large = Files.readString(LARGE_SCREEN_SOURCE)
        val languageSources: List<String> = listOf(
            Files.readString(EN_US_LANG),
            Files.readString(JA_JP_LANG),
            Files.readString(ZH_CN_LANG),
            Files.readString(ZH_TW_LANG)
        )

        assertTrue(small.contains("if (inside(mouseX - this.leftPos, mouseY - this.topPos, 8, 53, 26, 71)) {")) {
            "Small shipyard should keep the fuel-slot hover hitbox"
        }
        assertTrue(large.contains("if (insideInclusive(mx, my, 151, 95, 169, 113)) {")) {
            "Large shipyard should keep the fuel-slot hover hitbox"
        }

        for (source in listOf(small, large)) {
            assertTrue(source.contains("guiGraphics.renderComponentTooltip(this.font, java.util.List.of(")) {
                "Shipyard screens should keep the two-line component tooltip renderer"
            }
            assertTrue(source.contains("Component.translatable(\"gui.shincolle.shipyard.fuel_slot\").withStyle(net.minecraft.ChatFormatting.GOLD)")) {
                "Shipyard screens should keep the highlighted fuel-slot label"
            }
            assertTrue(source.contains("Component.translatable(\"gui.shincolle.shipyard.instant_tip\").withStyle(net.minecraft.ChatFormatting.GRAY)")) {
                "Shipyard screens should keep the instant-construction explanation"
            }
        }

        for (key in SHIPYARD_TOOLTIP_KEYS) {
            for (source in languageSources) {
                assertTrue(source.contains("\"" + key + "\"")) {
                    "Expected maintained languages to keep defining " + key
                }
            }
        }
    }

    @Test
    fun shipyardScreensShouldKeepPowerTooltipReadout() {
        val small = Files.readString(SMALL_SCREEN_SOURCE)
        val large = Files.readString(LARGE_SCREEN_SOURCE)

        assertTrue(small.contains("if (inside(mouseX - this.leftPos, mouseY - this.topPos, 9, 17, 23, 49)) {")) {
            "Small shipyard should keep the power-bar hover hitbox"
        }
        assertTrue(small.contains("guiGraphics.renderTooltip(this.font, Component.literal(String.valueOf(this.menu.getPowerRemained())), mouseX, mouseY);")) {
            "Small shipyard should keep showing remaining power on hover"
        }
        assertTrue(large.contains("if (inside(mx, my, 8, 19, 22, 84)) {")) {
            "Large shipyard should keep the power-bar hover hitbox"
        }
        assertTrue(large.contains("guiGraphics.renderTooltip(this.font, Component.literal(String.valueOf(this.menu.getPowerRemained())), mouseX, mouseY);")) {
            "Large shipyard should keep showing remaining power on hover"
        }
    }
}
