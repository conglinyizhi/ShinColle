package org.trp.shincolle.book;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PatchouliManualContentRegressionTest {
    private static final Path EN_AUTOMATION = Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/items/automation_tools.json");
    private static final Path EN_FLEET_CONTROL = Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/systems/fleet_control.json");
    private static final Path EN_FLEET_SUPPORT = Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual/en_us/entries/items/fleet_support_tools.json");
    private static final Path ZH_CN_LANG = Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json");

    @Test
    void automationEntryDocumentsMatchingSwitchesAndSideMatrix() throws Exception {
        String json = Files.readString(EN_AUTOMATION);
        assertTrue(json.contains("patchouli.shincolle.entry.automation_tools.page3.title"));
        assertTrue(json.contains("patchouli.shincolle.entry.automation_tools.page4.title"));
        assertTrue(json.contains("patchouli.shincolle.entry.automation_tools.page5.title"));
    }

    @Test
    void fleetEntriesDocumentScepterUsageInSimplifiedChinese() throws Exception {
        String fleetControl = Files.readString(EN_FLEET_CONTROL);
        String fleetSupport = Files.readString(EN_FLEET_SUPPORT);
        String zhCn = Files.readString(ZH_CN_LANG);

        assertTrue(fleetControl.contains("patchouli.shincolle.entry.fleet_control.page3.title"));
        assertTrue(fleetSupport.contains("patchouli.shincolle.entry.fleet_support_tools.page4.title"));
        assertTrue(zhCn.contains("patchouli.shincolle.entry.fleet_control.page3.text"));
        assertTrue(zhCn.contains("patchouli.shincolle.entry.fleet_support_tools.page4.text"));
        assertTrue(zhCn.contains("patchouli.shincolle.entry.automation_tools.page4.text"));
    }
}

