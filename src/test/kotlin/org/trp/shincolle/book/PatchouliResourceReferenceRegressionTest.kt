package org.trp.shincolle.book

import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertTrue

class PatchouliResourceReferenceRegressionTest {
    private val PATCHOULI_ROOT: Path =
            Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual")
    private val ASSET_ROOT: Path =
            Path.of("src/main/resources/assets")
    private val IMAGE_PATTERN: Pattern =
            Pattern.compile("\"([a-z0-9_]+:textures/[^\"]+)\"")

    @Test
    fun patchouliImageReferencesShouldResolveToExistingAssetFiles() {
        val missing = ArrayList<String>()

        Files.walk(PATCHOULI_ROOT).use { stream ->
            for (json in Iterable { stream.filter(Files::isRegularFile).filter { path -> path.toString().endsWith(".json") }.iterator() }) {
                val content = Files.readString(json)
                val matcher = IMAGE_PATTERN.matcher(content)
                while (matcher.find()) {
                    val resourceLocation = matcher.group(1)!!
                    val resolved = resolveAssetPath(resourceLocation)
                    if (!Files.exists(resolved)) {
                        missing.add(PATCHOULI_ROOT.relativize(json).toString() + " -> " + resourceLocation)
                    }
                }
            }
        }

        assertTrue(missing.isEmpty()) {
            "Patchouli image references must resolve to real files, missing: " + missing.joinToString(", ")
        }
    }

    private fun resolveAssetPath(resourceLocation: String): Path {
        val parts = resourceLocation.split(":", limit = 2)
        return ASSET_ROOT.resolve(parts[0]).resolve(parts[1])
    }
}
