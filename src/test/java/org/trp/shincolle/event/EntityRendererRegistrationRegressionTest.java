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

class EntityRendererRegistrationRegressionTest {
    private static final Path MOD_ENTITIES_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModEntities.java");
    private static final Path CLIENT_EVENT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.java");
    private static final Pattern ENTITY_FIELD_PATTERN = Pattern.compile(
            "public static final DeferredHolder<EntityType<\\?>, EntityType<[^>]+>>\\s+([A-Z0-9_]+)\\s*=\\s*ENTITY_TYPES\\.register\\(\"([a-z0-9_]+)\"");

    @Test
    void registeredEntitiesShouldKeepClientRendererRegistrations() throws IOException {
        String modEntities = Files.readString(MOD_ENTITIES_SOURCE);
        String clientEvents = Files.readString(CLIENT_EVENT_SOURCE);
        List<String> missing = new ArrayList<>();

        Matcher matcher = ENTITY_FIELD_PATTERN.matcher(modEntities);
        while (matcher.find()) {
            String fieldName = matcher.group(1);
            String registryId = matcher.group(2);
            String registration = "event.registerEntityRenderer(ModEntities." + fieldName + ".get(),";
            if (!clientEvents.contains(registration)) {
                missing.add(fieldName + " (" + registryId + ")");
            }
        }

        assertTrue(missing.isEmpty(),
                () -> "Every registered entity should keep a client renderer registration: "
                        + String.join(", ", missing));
    }
}
