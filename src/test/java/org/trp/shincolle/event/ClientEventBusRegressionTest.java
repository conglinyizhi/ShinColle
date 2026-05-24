package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientEventBusRegressionTest {

    private static final Path CLIENT_EVENT_BUS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.java");

    @Test
    void clientModEventsShouldStayOnClientModBus() throws IOException {
        String source = Files.readString(CLIENT_EVENT_BUS_SOURCE);

        assertTrue(source.contains("@EventBusSubscriber("), "ClientModEventBusEvents must remain an event bus subscriber");
        assertTrue(source.contains("bus = Bus.MOD"), "ClientModEventBusEvents must stay on the MOD bus");
        assertTrue(source.contains("value = Dist.CLIENT"), "ClientModEventBusEvents must remain client-only");
        assertTrue(source.contains("RegisterClientTooltipComponentFactoriesEvent"),
                "Tooltip component registration event should stay in ClientModEventBusEvents");
        assertTrue(source.contains("event.register(ScaledTextTooltipData.class, ScaledTextClientTooltip::new);"),
                "Scaled tooltip registration should stay in ClientModEventBusEvents");
    }
}
