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

class LayerDefinitionRegistrationRegressionTest {
    private static final Path MODEL_ROOT =
            Path.of("src/main/java/org/trp/shincolle/client/model");
    private static final Path CLIENT_EVENT_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.kt");
    private static final Pattern MODEL_NAME_PATTERN =
            Pattern.compile("class\\s+(Model[A-Za-z0-9_]+)");

    @Test
    void modelsWithLayerLocationsShouldKeepClientLayerDefinitionRegistrations() throws IOException {
        String clientEvents = Files.readString(CLIENT_EVENT_SOURCE);
        List<String> missing = new ArrayList<>();

        try (var stream = Files.walk(MODEL_ROOT)) {
            for (Path file : (Iterable<Path>) stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".kt"))::iterator) {
                String source = Files.readString(file);
                if (!source.contains("LAYER_LOCATION") || !source.contains("createBodyLayer")) {
                    continue;
                }

                Matcher matcher = MODEL_NAME_PATTERN.matcher(source);
                if (!matcher.find()) {
                    continue;
                }
                String modelName = matcher.group(1);
                String registration = "event.registerLayerDefinition(" + modelName + ".LAYER_LOCATION, "
                        + modelName + "::createBodyLayer);";
                if (!clientEvents.contains(registration)) {
                    missing.add(modelName);
                }
            }
        }

        assertTrue(missing.isEmpty(),
                () -> "Every model with a layer location should keep a client layer definition registration: "
                        + String.join(", ", missing));
    }
}
