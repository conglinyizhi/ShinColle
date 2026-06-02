package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TooltipComponentFactoryRegistrationRegressionTest {
    private record TooltipFactoryExpectation(
            String tooltipDataClassName,
            Path tooltipDataSource,
            String clientTooltipClassName,
            Path producerSource
    ) {
    }

    private static final Path CLIENT_EVENT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.java");

    private static final List<TooltipFactoryExpectation> TOOLTIP_FACTORIES = List.of(
            new TooltipFactoryExpectation(
                    "ScaledTextTooltipData",
                    Path.of("src/main/java/org/trp/shincolle/item/ScaledTextTooltipData.java"),
                    "ScaledTextClientTooltip",
                    Path.of("src/main/java/org/trp/shincolle/item/LegacyEquipItem.java")
            )
    );

    @Test
    void tooltipComponentsProducedByItemsShouldKeepClientFactories() throws IOException {
        String clientEvents = Files.readString(CLIENT_EVENT_SOURCE);

        for (TooltipFactoryExpectation expectation : TOOLTIP_FACTORIES) {
            String tooltipDataSource = Files.readString(expectation.tooltipDataSource());
            String producerSource = Files.readString(expectation.producerSource());

            assertTrue(tooltipDataSource.contains("implements TooltipComponent"),
                    () -> expectation.tooltipDataClassName() + " should remain a tooltip data component");
            assertTrue(producerSource.contains("new " + expectation.tooltipDataClassName() + "("),
                    () -> "Expected " + expectation.producerSource().getFileName()
                            + " to keep producing " + expectation.tooltipDataClassName());
            assertTrue(clientEvents.contains("event.register(" + expectation.tooltipDataClassName() + ".class, "
                            + expectation.clientTooltipClassName() + "::new);"),
                    () -> expectation.tooltipDataClassName()
                            + " should keep a client tooltip factory registration to "
                            + expectation.clientTooltipClassName());
        }
    }
}
