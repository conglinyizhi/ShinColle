package org.trp.shincolle.client

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.nio.file.Files
import java.nio.file.Path

class ShipInventoryTooltipRegressionTest {
    private val SCREEN_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/client/screen/ShipInventoryScreen.kt")
    private val MAINTAINED_LANGS: List<Path> = listOf(
        Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
        Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
        Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
        Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
    )

    private val PAGE6_LABEL_KEYS: List<String> = listOf(
        "gui.shincolle.equip",
        "gui.shincolle.showhelditem",
        "gui.shincolle.equip.mount"
    )

    private val PAGE6_TOOLTIP_KEYS: List<String> = listOf(
        "gui.shincolle.showhelditem",
        "gui.shincolle.equip.mount"
    )

    private val PAGE7_TOOLTIP_KEYS: List<String> = listOf(
        "gui.shincolle.ai.cooking",
        "gui.shincolle.ai.fishing",
        "gui.shincolle.ai.mining",
        "gui.shincolle.ai.crafting",
        "gui.shincolle.crane.usemeta",
        "gui.shincolle.crane.useoredict",
        "gui.shincolle.crane.usenbt"
    )

    private val PAGE8_TOOLTIP_KEYS: List<String> = listOf(
        "gui.shincolle.ai.inputside",
        "gui.shincolle.ai.outputside",
        "gui.shincolle.ai.fuelside"
    )

    @Test
    fun shipInventoryScreenShouldKeepHoverTooltipEntrypointsForSettingsPages() {
        val screen = Files.readString(SCREEN_SOURCE)

        assertTrue(screen.contains("renderLegacyHoverTooltips(guiGraphics, mouseX, mouseY);")) {
            "Ship inventory render pass should keep the legacy hover tooltip bridge"
        }
        assertTrue(screen.contains("if (this.activeSettingsTab == SETTINGS_TAB_7) { renderAIPage7Tooltips(g, mx, my); return; }")) {
            "Settings page 7 should keep its hover tooltip dispatcher"
        }
        assertTrue(screen.contains("if (this.activeSettingsTab == SETTINGS_TAB_8) { renderAIPage8Tooltips(g, mx, my); return; }")) {
            "Settings page 8 should keep its hover tooltip dispatcher"
        }
        assertTrue(screen.contains("renderFixedToggleTooltips(g, mx, my);")) {
            "Fixed toggle tooltips should still be rendered for the appearance page"
        }
    }

    @Test
    fun shipInventoryScreenShouldKeepAppearanceToggleHoverTooltips() {
        val screen = Files.readString(SCREEN_SOURCE)

        assertTrue(screen.contains("if (this.activeSettingsTab == SETTINGS_TAB_6) {")) {
            "Settings page 6 should keep its fixed toggle tooltip block"
        }
        assertTrue(screen.contains("drawCenteredLabel(guiGraphics, tr(\"gui.shincolle.showhelditem\"), 212, 133);")) {
            "Settings page 6 should keep the show-held-item label"
        }
        assertTrue(screen.contains("drawCenteredLabel(guiGraphics, tr(\"gui.shincolle.equip.mount\"), 212, 146);")) {
            "Settings page 6 should keep the mount label"
        }
        assertTrue(screen.contains("TooltipBuilder.of(\"gui.shincolle.showhelditem\").renderIfNotEmpty(g, this.font, mx, my);")) {
            "Show-held-item toggle should keep its hover tooltip"
        }
        assertTrue(screen.contains("TooltipBuilder.of(\"gui.shincolle.equip.mount\").renderIfNotEmpty(g, this.font, mx, my);")) {
            "Mount toggle should keep its hover tooltip"
        }
        assertTrue(screen.contains("g.renderComponentTooltip(this.font, List.of(this.menu.getEquipOptionLabel(i)), mx, my);")) {
            "Appearance option buttons should keep showing the localized option label on hover"
        }

        for (lang in MAINTAINED_LANGS) {
            val content = Files.readString(lang)
            for (key in PAGE6_LABEL_KEYS) {
                assertTrue(content.contains("\"" + key + "\"")) {
                    "Expected " + lang.fileName + " to keep defining " + key
                }
            }
            for (key in PAGE6_TOOLTIP_KEYS) {
                assertTrue(content.contains("\"" + key + "\"")) {
                    "Expected " + lang.fileName + " to keep defining " + key
                }
            }
        }
    }

    @Test
    fun shipInventoryScreenShouldKeepAutomationTooltipTranslations() {
        val screen = Files.readString(SCREEN_SOURCE)

        assertTrue(screen.contains("String key = switch (idx) { case 1 -> \"gui.shincolle.ai.cooking\"; case 2 -> \"gui.shincolle.ai.fishing\"; case 3 -> \"gui.shincolle.ai.mining\"; case 4 -> \"gui.shincolle.ai.crafting\"; default -> null; };")) {
            "Settings page 7 should keep mapping task buttons to their localized hover tooltips"
        }
        assertTrue(screen.contains("if (key != null) TooltipBuilder.of(key).renderIfNotEmpty(g, this.font, mx, my);")) {
            "Settings page 7 should keep rendering the selected task tooltip key"
        }

        for (key in listOf(
            "gui.shincolle.crane.usemeta",
            "gui.shincolle.crane.useoredict",
            "gui.shincolle.crane.usenbt",
            "gui.shincolle.ai.inputside",
            "gui.shincolle.ai.outputside",
            "gui.shincolle.ai.fuelside"
        )) {
            assertTrue(screen.contains("TooltipBuilder.of(\"" + key + "\")")) {
                "Expected ShipInventoryScreen to keep using tooltip key " + key
            }
        }

        for (lang in MAINTAINED_LANGS) {
            val content = Files.readString(lang)
            for (key in PAGE7_TOOLTIP_KEYS) {
                assertTrue(content.contains("\"" + key + "\"")) {
                    "Expected " + lang.fileName + " to keep defining " + key
                }
            }
            for (key in PAGE8_TOOLTIP_KEYS) {
                assertTrue(content.contains("\"" + key + "\"")) {
                    "Expected " + lang.fileName + " to keep defining " + key
                }
            }
        }
    }
}
