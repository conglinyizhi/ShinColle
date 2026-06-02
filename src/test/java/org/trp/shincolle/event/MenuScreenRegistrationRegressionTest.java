package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuScreenRegistrationRegressionTest {
    private static final Path MOD_MENUS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/menu/ModMenus.java");
    private static final Path CLIENT_EVENT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.java");
    private static final Pattern MENU_FIELD_PATTERN = Pattern.compile(
            "public static final DeferredHolder<MenuType<\\?>, MenuType<[^>]+>>\\s+([A-Z0-9_]+)\\s*=\\s*MENUS\\.register",
            Pattern.MULTILINE);

    @Test
    void registeredMenusShouldKeepClientScreenRegistrations() throws IOException {
        String modMenus = Files.readString(MOD_MENUS_SOURCE);
        String clientEvents = Files.readString(CLIENT_EVENT_SOURCE);
        List<String> missing = new ArrayList<>();

        Matcher matcher = MENU_FIELD_PATTERN.matcher(modMenus);
        while (matcher.find()) {
            String fieldName = matcher.group(1);
            String registration = "event.register(ModMenus." + fieldName + ".get(),";
            if (!clientEvents.contains(registration)) {
                missing.add(fieldName);
            }
        }

        assertTrue(missing.isEmpty(),
                () -> "Every registered menu should keep a client screen registration: "
                        + String.join(", ", missing));
    }
}
