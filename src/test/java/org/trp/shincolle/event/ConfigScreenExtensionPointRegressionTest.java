package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigScreenExtensionPointRegressionTest {
    private static final Path CLIENT_EVENT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.kt");
    private static final Path CONFIG_SCREEN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/gui/ShincolleConfigScreen.kt");

    @Test
    void clientSetupShouldKeepOptionalConfigScreenExtensionPoint() throws IOException {
        String clientEvents = Files.readString(CLIENT_EVENT_SOURCE);
        String configScreen = Files.readString(CONFIG_SCREEN_SOURCE);

        assertTrue(clientEvents.contains("c.registerExtensionPoint("),
                "Client setup should keep registering the config screen extension point");
        assertTrue(clientEvents.contains("net.neoforged.neoforge.client.gui.IConfigScreenFactory.class"),
                "Config screen extension point should keep using IConfigScreenFactory");
        assertTrue(clientEvents.contains("ShincolleConfigScreen.tryCreate(parentScreen)")
                        || clientEvents.contains("org.trp.shincolle.client.gui.ShincolleConfigScreen.tryCreate(parentScreen)"),
                "Config screen extension point should keep delegating to ShincolleConfigScreen.tryCreate");

        assertTrue(configScreen.contains("public static Screen tryCreate(Screen parent) {"),
                "ShincolleConfigScreen should keep exposing tryCreate");
        assertTrue(configScreen.contains("ModList.get().isLoaded(\"cloth_config\")"),
                "Config screen availability should keep checking the optional cloth_config dependency");
        assertTrue(configScreen.contains("return null;"),
                "tryCreate should keep returning null when cloth_config is unavailable");
        assertTrue(configScreen.contains("return LazyScreen.create(parent);"),
                "tryCreate should keep lazily creating the Cloth Config screen when available");
    }
}
