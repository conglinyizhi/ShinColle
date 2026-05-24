package org.trp.shincolle.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DeskDiplomacyScreenRegressionTest {
    private static final Path SCREEN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/screen/DeskScreen.java");
    private static final Path ZH_LANG_SOURCE =
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json");
    private static final Path EN_LANG_SOURCE =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");

    @Test
    void diplomacyScreenShouldKeepExplicitButtonsAndSelectionState() throws IOException {
        String screenSource = Files.readString(SCREEN_SOURCE);

        assertTrue(screenSource.contains("drawDiplomacyButtons(guiGraphics);"),
                "Desk diplomacy screen should render explicit action buttons");
        assertTrue(screenSource.contains("private void selectDiplomacyEntry(PlayerEntry selected) {"),
                "Desk diplomacy screen should keep explicit row selection state");
        assertTrue(screenSource.contains("entry.selected = entry == selected;"),
                "Desk diplomacy screen should mark exactly one selected entry");
        assertTrue(screenSource.contains("setDeskFunction(0);"),
                "Desk diplomacy screen should allow returning to the desk main page");
    }

    @Test
    void diplomacyScreenShouldExposeBackLabelInLanguages() throws IOException {
        String zh = Files.readString(ZH_LANG_SOURCE);
        String en = Files.readString(EN_LANG_SOURCE);

        assertTrue(zh.contains("\"gui.shincolle.team.back\": \"返回\""),
                "Chinese localization should include the diplomacy back button");
        assertTrue(en.contains("\"gui.shincolle.team.back\": \"Back\""),
                "English localization should include the diplomacy back button");
    }
}
