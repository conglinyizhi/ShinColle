package org.trp.shincolle.build

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.TreeSet
import java.util.regex.Pattern
import java.util.stream.Collectors

import org.junit.jupiter.api.Assertions.assertTrue

class ParticleResourceReferenceRegressionTest {
    private val PARTICLE_JSON_ROOT: Path =
            Path.of("src/main/resources/assets/shincolle/particles")
    private val PARTICLE_TEXTURE_ROOT: Path =
            Path.of("src/main/resources/assets/shincolle/textures/particle")
    private val MOD_PARTICLES_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/init/ModParticles.kt")

    private val REGISTERED_PARTICLE_PATTERN: Pattern =
            Pattern.compile("PARTICLES\\.register\\(\"([a-z0-9_]+)\"")
    private val TEXTURE_ENTRY_PATTERN: Pattern =
            Pattern.compile("\"((?:minecraft|shincolle):[^\"]+)\"")

    private val EXPECTED_PARTICLE_JSONS: Set<String> = setOf(
            "particle_goddess",
            "particle_spray",
            "particle_spray_red",
            "particle_waypoint",
            "particle_waypoint_line",
            "particle_waypoint_line_purple",
            "particleemotion",
            "particleteam",
            "particleteam_selected",
            "particleteam_selected_red",
            "particleteam_selected_yellow",
            "particleteam_target",
            "particleteam_target_entity",
            "particletexts"
    )

    @Test
    fun particleJsonFilesShouldStayWithinKnownResourceSet() {
        val actual = listParticleJsonNames()
        assertTrue(actual.equals(EXPECTED_PARTICLE_JSONS)) {
            "Particle JSON resource set changed unexpectedly, found: " +
                    TreeSet(actual).joinToString(", ")
        }
    }

    @Test
    fun particleJsonFilesShouldResolveToRegisteredParticleTypes() {
        val registered = readRegisteredParticleNames()
        val actual = listParticleJsonNames()
        val missing = TreeSet(actual)
        missing.removeAll(registered)

        assertTrue(missing.isEmpty()) {
            "Every particle JSON file should map to a registered particle type, missing registrations: " +
                    missing.joinToString(", ")
        }
    }

    @Test
    fun registeredParticlesShouldStayWithinKnownJsonOrProviderOnlyAllowlist() {
        val registered = readRegisteredParticleNames()
        val withoutJson = TreeSet(registered)
        withoutJson.removeAll(EXPECTED_PARTICLE_JSONS)

        val expectedProviderOnly: Set<String> = setOf(
                "particle_beam",
                "particle_chi",
                "particle_craning",
                "particle_cube",
                "particle_91type",
                "particle_lightning",
                "particleheal_sparkle",
                "particle_sparkle",
                "particle_waypoint_line_red"
        )

        assertTrue(withoutJson.equals(expectedProviderOnly)) {
            "Registered particles without JSON resources changed unexpectedly, found: " +
                    withoutJson.joinToString(", ")
        }
    }

    @Test
    fun shincolleParticleTextureReferencesShouldResolveToExistingTextures() {
        val missing = ArrayList<String>()

        Files.walk(PARTICLE_JSON_ROOT).use { stream ->
            for (json in stream
                    .filter(Files::isRegularFile)
                    .filter { it.toString().endsWith(".json") }
                    .toList()) {
                val content = Files.readString(json)
                val matcher = TEXTURE_ENTRY_PATTERN.matcher(content)
                while (matcher.find()) {
                    val resourceLocation = matcher.group(1)!!
                    if (!resourceLocation.startsWith("shincolle:")) {
                        continue
                    }
                    val resolved = resolveParticleTexture(resourceLocation)
                    if (!Files.exists(resolved)) {
                        missing.add(PARTICLE_JSON_ROOT.relativize(json).toString() + " -> " + resourceLocation)
                    }
                }
            }
        }

        assertTrue(missing.isEmpty()) {
            "Particle JSON files should only reference existing ShinColle particle textures, missing: " +
                    missing.joinToString(", ")
        }
    }

    private fun listParticleJsonNames(): Set<String> {
        Files.walk(PARTICLE_JSON_ROOT).use { stream ->
            return stream.filter(Files::isRegularFile)
                    .filter { it.toString().endsWith(".json") }
                    .map { path ->
                        val fileName = path.fileName.toString()
                        fileName.substring(0, fileName.length - 5)
                    }
                    .collect(Collectors.toCollection { TreeSet() })
        }
    }

    private fun readRegisteredParticleNames(): Set<String> {
        val source = Files.readString(MOD_PARTICLES_SOURCE)
        val registered = TreeSet<String>()
        val matcher = REGISTERED_PARTICLE_PATTERN.matcher(source)
        while (matcher.find()) {
            registered.add(matcher.group(1)!!)
        }
        return registered
    }

    private fun resolveParticleTexture(resourceLocation: String): Path {
        val parts = resourceLocation.split(":", limit = 2)
        return PARTICLE_TEXTURE_ROOT.resolve(parts[1] + ".png")
    }
}
