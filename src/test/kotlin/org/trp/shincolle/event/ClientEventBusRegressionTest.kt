package org.trp.shincolle.event

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class ClientEventBusRegressionTest {

    private val CLIENT_EVENT_BUS_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.kt")

    @Test
    fun clientModEventsShouldStayOnClientModBus() {
        val source = Files.readString(CLIENT_EVENT_BUS_SOURCE)

        assertTrue(source.contains("@EventBusSubscriber(")) { "ClientModEventBusEvents must remain an event bus subscriber" }
        assertFalse(source.contains("bus =")) { "ClientModEventBusEvents should not specify bus (defaults to MOD bus)" }
        assertTrue(source.contains("value = Dist.CLIENT")) { "ClientModEventBusEvents must remain client-only" }
        assertTrue(source.contains("RegisterClientTooltipComponentFactoriesEvent")) {
            "Tooltip component registration event should stay in ClientModEventBusEvents"
        }
        assertTrue(source.contains("event.register(ScaledTextTooltipData.class, ScaledTextClientTooltip::new);")) {
            "Scaled tooltip registration should stay in ClientModEventBusEvents"
        }
    }
}
