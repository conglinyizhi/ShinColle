package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockEntityRendererRegistrationRegressionTest {
    private static final Path CLIENT_EVENT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.java");

    private static final List<String> BLOCK_ENTITIES_REQUIRING_RENDERERS = List.of(
            "SMALL_SHIPYARD",
            "LARGE_SHIPYARD",
            "DESK"
    );

    @Test
    void blockEntitiesWithCustomVisualsShouldKeepRendererRegistrations() throws IOException {
        String clientEvents = Files.readString(CLIENT_EVENT_SOURCE);

        for (String fieldName : BLOCK_ENTITIES_REQUIRING_RENDERERS) {
            String registration = "event.registerBlockEntityRenderer(ModBlockEntities." + fieldName + ".get(),";
            assertTrue(clientEvents.contains(registration),
                    () -> "Block entity " + fieldName + " should keep a client renderer registration");
        }
    }
}
