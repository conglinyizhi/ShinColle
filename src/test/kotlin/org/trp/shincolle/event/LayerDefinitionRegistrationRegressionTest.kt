package org.trp.shincolle.event

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.junit.jupiter.api.Assertions.assertTrue

class LayerDefinitionRegistrationRegressionTest {
    private val MODEL_ROOT: Path =
            Path.of("src/main/java/org/trp/shincolle/client/model")
    private val CLIENT_EVENT_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/event/ClientModEventBusEvents.kt")
    private val MODEL_NAME_PATTERN: Pattern =
            Pattern.compile("class\\s+(Model[A-Za-z0-9_]+)")

    @Test
    fun modelsWithLayerLocationsShouldKeepClientLayerDefinitionRegistrations() {
        val clientEvents = Files.readString(CLIENT_EVENT_SOURCE)
        val missing = ArrayList<String>()

        Files.walk(MODEL_ROOT).use { stream ->
            for (file in stream
                    .filter { Files.isRegularFile(it) }
                    .filter { it.toString().endsWith(".kt") }
                    .iterator()) {
                val source = Files.readString(file)
                if (!source.contains("LAYER_LOCATION") || !source.contains("createBodyLayer")) {
                    continue
                }

                val matcher = MODEL_NAME_PATTERN.matcher(source)
                if (!matcher.find()) {
                    continue
                }
                val modelName = matcher.group(1)!!
                val registration = "event.registerLayerDefinition(" + modelName + ".LAYER_LOCATION, " +
                        modelName + "::createBodyLayer);"
                if (!clientEvents.contains(registration)) {
                    missing.add(modelName)
                }
            }
        }

        assertTrue(missing.isEmpty()) {
            "Every model with a layer location should keep a client layer definition registration: " +
                    missing.joinToString(", ")
        }
    }
}
