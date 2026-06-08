package org.trp.shincolle.build

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.junit.jupiter.api.Assertions.assertTrue

class ModelTextureReferenceRegressionTest {
    private val MODEL_ROOT: Path = Path.of("src/main/resources/assets/shincolle/models")
    private val ASSET_ROOT: Path = Path.of("src/main/resources/assets")
    private val TEXTURES_BLOCK_PATTERN: Pattern =
            Pattern.compile("\"textures\"\\s*:\\s*\\{(.*?)\\}", Pattern.DOTALL)
    private val TEXTURE_VALUE_PATTERN: Pattern =
            Pattern.compile("\"[^\"]+\"\\s*:\\s*\"((?:minecraft|shincolle):[^\"#/][^\"]*)\"")

    @Test
    fun modelTextureReferencesShouldResolveToExistingTextureFiles() {
        val missing = ArrayList<String>()

        Files.walk(MODEL_ROOT).use { stream ->
            for (json in stream
                    .filter(Files::isRegularFile)
                    .filter { it.toString().endsWith(".json") }
                    .toList()) {
                val content = Files.readString(json)
                val texturesBlockMatcher = TEXTURES_BLOCK_PATTERN.matcher(content)
                while (texturesBlockMatcher.find()) {
                    val texturesBlock = texturesBlockMatcher.group(1)!!
                    val matcher = TEXTURE_VALUE_PATTERN.matcher(texturesBlock)
                    while (matcher.find()) {
                        val resourceLocation = matcher.group(1)!!
                        if (!resourceLocation.startsWith("shincolle:")) {
                            continue
                        }
                        val resolved = resolveTexturePath(resourceLocation)
                        if (!Files.exists(resolved)) {
                            missing.add(MODEL_ROOT.relativize(json).toString() + " -> " + resourceLocation)
                        }
                    }
                }
            }
        }

        assertTrue(missing.isEmpty()) {
            "Model texture references must resolve to real texture files, missing: " + missing.joinToString(", ")
        }
    }

    private fun resolveTexturePath(resourceLocation: String): Path {
        val parts = resourceLocation.split(":", limit = 2)
        return ASSET_ROOT.resolve(parts[0]).resolve("textures").resolve(parts[1] + ".png")
    }
}
