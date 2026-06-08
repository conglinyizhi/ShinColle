package org.trp.shincolle.client

import org.junit.jupiter.api.Test

import java.nio.file.Files
import java.nio.file.Path

import org.junit.jupiter.api.Assertions.assertTrue

class DeskDiplomacyScreenRegressionTest {
    private val SCREEN_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/client/screen/DeskScreen.kt")
    private val JA_LANG_SOURCE: Path =
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json")
    private val ZH_LANG_SOURCE: Path =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json")
    private val ZH_TW_LANG_SOURCE: Path =
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
    private val EN_LANG_SOURCE: Path =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json")
    private val DIPLOMACY_KEYS = listOf(
            "chat.shincolle.team.ally_added",
            "chat.shincolle.team.ally_missing",
            "chat.shincolle.team.ally_removed",
            "chat.shincolle.team.ally_unchanged",
            "chat.shincolle.team.hostile_added",
            "chat.shincolle.team.hostile_missing",
            "chat.shincolle.team.hostile_removed",
            "chat.shincolle.team.hostile_unchanged",
            "gui.shincolle.desk.title",
            "gui.shincolle.team.back",
            "gui.shincolle.team.diplomacy_hint",
            "gui.shincolle.team.state.ally",
            "gui.shincolle.team.state.hostile",
            "gui.shincolle.team.state.neutral"
    )

    @Test
    fun diplomacyScreenShouldKeepExplicitButtonsAndSelectionState() {
        val screenSource: String = Files.readString(SCREEN_SOURCE)

        assertTrue(screenSource.contains("drawDiplomacyButtons(guiGraphics);")) {
            "Desk diplomacy screen should render explicit action buttons"
        }
        assertTrue(screenSource.contains("private void selectDiplomacyEntry(PlayerEntry selected) {")) {
            "Desk diplomacy screen should keep explicit row selection state"
        }
        assertTrue(screenSource.contains("entry.selected = entry == selected;")) {
            "Desk diplomacy screen should mark exactly one selected entry"
        }
        assertTrue(screenSource.contains("setDeskFunction(0);")) {
            "Desk diplomacy screen should allow returning to the desk main page"
        }
    }

    @Test
    fun diplomacyScreenShouldExposeDiplomacyLabelsInMaintainedLanguages() {
        val languageSources = listOf(
                Files.readString(EN_LANG_SOURCE),
                Files.readString(JA_LANG_SOURCE),
                Files.readString(ZH_LANG_SOURCE),
                Files.readString(ZH_TW_LANG_SOURCE)
        )

        for (key in DIPLOMACY_KEYS) {
            for (source in languageSources) {
                assertTrue(source.contains("\"" + key + "\"")) {
                    "Expected maintained languages to define " + key
                }
            }
        }
    }
}
