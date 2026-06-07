package org.trp.shincolle.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipInventoryAiToggleRegressionTest {
    private static final Path SCREEN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/screen/ShipInventoryScreen.kt");
    private static final Path MENU_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/menu/ShipContainerMenu.kt");

    @Test
    void aiSettingsPagesShouldRegisterLegacyToggleButtons() throws IOException {
        String screen = Files.readString(SCREEN_SOURCE);

        assertTrue(screen.contains("addToggle(SETTINGS_TAB_1, TOGGLE_X, TOGGLE_ROW_1_Y, () -> this.canMelee,"),
                "AI page 1 should register the melee toggle button");
        assertTrue(screen.contains("addToggle(SETTINGS_TAB_3, TOGGLE_X, TOGGLE_ROW_1_Y, () -> this.passiveAttack,"),
                "AI page 3 should register the passive-attack toggle button");
        assertTrue(screen.contains("addToggle(SETTINGS_TAB_4, TOGGLE_X, TOGGLE_ROW_1_Y, () -> this.pickItem,"),
                "AI page 4 should register the item-pickup toggle button");
        assertTrue(screen.contains("addToggle(SETTINGS_TAB_6, TOGGLE_X, TOGGLE_ROW_1_Y, () -> this.appearance,"),
                "AI page 6 should register the appearance toggle button");
        assertTrue(screen.contains(".uv(-1, -1)"),
                "AI toggle buttons should stay as transparent click layers without hover sprites");
        assertTrue(screen.contains(".hoverUv(-1, -1)"),
                "AI toggle buttons should not depend on missing hover or focus sprites");
        assertTrue(screen.contains("case 1 -> drawAiPage1ToggleMarks(g);"),
                "AI page 1 should render toggle state marks separately from the click layer");
        assertTrue(screen.contains("case 3 -> drawAiPage3ToggleMarks(g);"),
                "AI page 3 should render toggle state marks separately from the click layer");
        assertTrue(screen.contains("case 4 -> drawAiPage4ToggleMarks(g);"),
                "AI page 4 should render toggle state marks separately from the click layer");
        assertTrue(screen.contains("toggleVisibilitySuppliers.get(i).getAsBoolean()"),
                "Toggle visibility should still honor per-button availability gates");
    }

    @Test
    void toggleButtonsShouldBroadcastFullStateImmediately() throws IOException {
        String menu = Files.readString(MENU_SOURCE);

        assertTrue(menu.contains("case TOGGLE_BUTTON_LIGHT_ATTACK -> {\n                if (ship.isStateGuiBtn1()) {\n                    ship.setStateLightAttack(!ship.isStateLightAttack());\n                    lightAttackSynced = ship.isStateLightAttack();\n                    this.broadcastFullState();"),
                "Light attack toggles should broadcast immediately so the client state refreshes on click");
        assertTrue(menu.contains("case TOGGLE_BUTTON_HEAVY_ATTACK -> {\n                if (ship.isStateGuiBtn2()) {\n                    ship.setStateHeavyAttack(!ship.isStateHeavyAttack());\n                    heavyAttackSynced = ship.isStateHeavyAttack();\n                    this.broadcastFullState();"),
                "Heavy attack toggles should broadcast immediately so the client state refreshes on click");
        assertTrue(menu.contains("case TOGGLE_BUTTON_LIGHT_AIRCRAFT -> {\n                if (ship.isStateGuiBtn3()) {\n                    ship.setStateLightAircraftAttack(!ship.isStateLightAircraftAttack());\n                    lightAircraftAttackSynced = ship.isStateLightAircraftAttack();\n                    this.broadcastFullState();"),
                "Light aircraft toggles should broadcast immediately so the client state refreshes on click");
        assertTrue(menu.contains("case TOGGLE_BUTTON_HEAVY_AIRCRAFT -> {\n                if (ship.isStateGuiBtn4()) {\n                    ship.setStateHeavyAircraftAttack(!ship.isStateHeavyAircraftAttack());\n                    heavyAircraftAttackSynced = ship.isStateHeavyAircraftAttack();\n                    this.broadcastFullState();"),
                "Heavy aircraft toggles should broadcast immediately so the client state refreshes on click");
    }
}
