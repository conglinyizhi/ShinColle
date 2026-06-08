package org.trp.shincolle.build

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.junit.jupiter.api.Assertions.assertTrue

class ModelReferenceChainRegressionTest {
    private val BLOCKSTATE_ROOT: Path = Path.of("src/main/resources/assets/shincolle/blockstates")
    private val MODEL_ROOT: Path = Path.of("src/main/resources/assets/shincolle/models")
    private val ASSET_ROOT: Path = Path.of("src/main/resources/assets")
    private val BLOCKSTATE_MODEL_PATTERN: Pattern =
            Pattern.compile("\"model\"\\s*:\\s*\"(shincolle:block/[^\"]+)\"")
    private val MODEL_PARENT_PATTERN: Pattern =
            Pattern.compile("\"parent\"\\s*:\\s*\"(shincolle:[^\"]+)\"")
    private val OVERRIDE_MODEL_PATTERN: Pattern =
            Pattern.compile("\"model\"\\s*:\\s*\"(shincolle:[^\"]+)\"")

    @Test
    fun blockstateModelReferencesShouldResolveToExistingModelFiles() {
        val missing = ArrayList<String>()

        Files.walk(BLOCKSTATE_ROOT).use { stream ->
            for (json in stream
                    .filter(Files::isRegularFile)
                    .filter { it.toString().endsWith(".json") }
                    .toList()) {
                val content = Files.readString(json)
                val matcher = BLOCKSTATE_MODEL_PATTERN.matcher(content)
                while (matcher.find()) {
                    val resourceLocation = matcher.group(1)!!
                    val resolved = resolveModelPath(resourceLocation)
                    if (!Files.exists(resolved)) {
                        missing.add(BLOCKSTATE_ROOT.relativize(json).toString() + " -> " + resourceLocation)
                    }
                }
            }
        }

        assertTrue(missing.isEmpty()) {
            "Blockstate model references must resolve to real model files, missing: " + missing.joinToString(", ")
        }
    }

    @Test
    fun customModelParentAndOverrideReferencesShouldResolveToExistingModelFiles() {
        val missing = ArrayList<String>()

        Files.walk(MODEL_ROOT).use { stream ->
            for (json in stream
                    .filter(Files::isRegularFile)
                    .filter { it.toString().endsWith(".json") }
                    .toList()) {
                val content = Files.readString(json)

                val parentMatcher = MODEL_PARENT_PATTERN.matcher(content)
                while (parentMatcher.find()) {
                    val resourceLocation = parentMatcher.group(1)!!
                    val resolved = resolveModelPath(resourceLocation)
                    if (!Files.exists(resolved)) {
                        missing.add(MODEL_ROOT.relativize(json).toString() + " parent -> " + resourceLocation)
                    }
                }

                val overrideMatcher = OVERRIDE_MODEL_PATTERN.matcher(content)
                while (overrideMatcher.find()) {
                    val resourceLocation = overrideMatcher.group(1)!!
                    val offset = if (overrideMatcher.start() - 9 < 0) 0 else overrideMatcher.start() - 9
                    if (content.regionMatches(offset, "\"parent\"", 0, 8)) {
                        continue
                    }
                    if (!resourceLocation.startsWith("shincolle:")) {
                        continue
                    }
                    val resolved = resolveModelPath(resourceLocation)
                    if (!Files.exists(resolved)) {
                        missing.add(MODEL_ROOT.relativize(json).toString() + " override -> " + resourceLocation)
                    }
                }
            }
        }

        assertTrue(missing.isEmpty()) {
            "Custom model parent and override references must resolve to real model files, missing: " + missing.joinToString(", ")
        }
    }

    private fun resolveModelPath(resourceLocation: String): Path {
        val parts = resourceLocation.split(":", limit = 2)
        return ASSET_ROOT.resolve(parts[0]).resolve("models").resolve(parts[1] + ".json")
    }
}
