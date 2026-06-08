package org.trp.shincolle.event

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

import org.junit.jupiter.api.Assertions.assertTrue

class TooltipComponentFactoryRegistrationRegressionTest {
    private data class TooltipFactoryExpectation(
            val tooltipDataClassName: String,
            val tooltipDataSource: Path,
            val clientTooltipClassName: String,
            val producerSource: Path
    )

    private val CLIENT_EVENT_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.kt")

    private val TOOLTIP_FACTORIES = listOf(
            TooltipFactoryExpectation(
                    "ScaledTextTooltipData",
                    Path.of("src/main/java/org/trp/shincolle/item/ScaledTextTooltipData.kt"),
                    "ScaledTextClientTooltip",
                    Path.of("src/main/java/org/trp/shincolle/item/LegacyEquipItem.kt")
            )
    )

    @Test
    fun tooltipComponentsProducedByItemsShouldKeepClientFactories() {
        val clientEvents = Files.readString(CLIENT_EVENT_SOURCE)

        for (expectation in TOOLTIP_FACTORIES) {
            val tooltipDataSource = Files.readString(expectation.tooltipDataSource)
            val producerSource = Files.readString(expectation.producerSource)

            assertTrue(tooltipDataSource.contains("implements TooltipComponent")) {
                    expectation.tooltipDataClassName + " should remain a tooltip data component"
            }
            assertTrue(producerSource.contains("new " + expectation.tooltipDataClassName + "(")) {
                    "Expected " + expectation.producerSource.fileName +
                            " to keep producing " + expectation.tooltipDataClassName
            }
            assertTrue(clientEvents.contains("event.register(" + expectation.tooltipDataClassName + ".class, " +
                            expectation.clientTooltipClassName + "::new);")) {
                    expectation.tooltipDataClassName +
                            " should keep a client tooltip factory registration to " +
                            expectation.clientTooltipClassName
            }
        }
    }
}
