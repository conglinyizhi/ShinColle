package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ParticleResourceReferenceRegressionTest {
    private static final Path PARTICLE_JSON_ROOT =
            Path.of("src/main/resources/assets/shincolle/particles");
    private static final Path PARTICLE_TEXTURE_ROOT =
            Path.of("src/main/resources/assets/shincolle/textures/particle");
    private static final Path MOD_PARTICLES_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModParticles.kt");

    private static final Pattern REGISTERED_PARTICLE_PATTERN =
            Pattern.compile("PARTICLES\\.register\\(\"([a-z0-9_]+)\"");
    private static final Pattern TEXTURE_ENTRY_PATTERN =
            Pattern.compile("\"((?:minecraft|shincolle):[^\"]+)\"");

    private static final Set<String> EXPECTED_PARTICLE_JSONS = Set.of(
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
    );

    @Test
    void particleJsonFilesShouldStayWithinKnownResourceSet() throws IOException {
        Set<String> actual = listParticleJsonNames();
        assertTrue(actual.equals(EXPECTED_PARTICLE_JSONS),
                () -> "Particle JSON resource set changed unexpectedly, found: "
                        + String.join(", ", new TreeSet<>(actual)));
    }

    @Test
    void particleJsonFilesShouldResolveToRegisteredParticleTypes() throws IOException {
        Set<String> registered = readRegisteredParticleNames();
        Set<String> actual = listParticleJsonNames();
        Set<String> missing = new TreeSet<>(actual);
        missing.removeAll(registered);

        assertTrue(missing.isEmpty(),
                () -> "Every particle JSON file should map to a registered particle type, missing registrations: "
                        + String.join(", ", missing));
    }

    @Test
    void registeredParticlesShouldStayWithinKnownJsonOrProviderOnlyAllowlist() throws IOException {
        Set<String> registered = readRegisteredParticleNames();
        Set<String> withoutJson = new TreeSet<>(registered);
        withoutJson.removeAll(EXPECTED_PARTICLE_JSONS);

        Set<String> expectedProviderOnly = Set.of(
                "particle_beam",
                "particle_chi",
                "particle_craning",
                "particle_cube",
                "particle_91type",
                "particle_lightning",
                "particleheal_sparkle",
                "particle_sparkle",
                "particle_waypoint_line_red"
        );

        assertTrue(withoutJson.equals(expectedProviderOnly),
                () -> "Registered particles without JSON resources changed unexpectedly, found: "
                        + String.join(", ", withoutJson));
    }

    @Test
    void shincolleParticleTextureReferencesShouldResolveToExistingTextures() throws IOException {
        List<String> missing = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(PARTICLE_JSON_ROOT)) {
            for (Path json : (Iterable<Path>) stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))::iterator) {
                String content = Files.readString(json);
                Matcher matcher = TEXTURE_ENTRY_PATTERN.matcher(content);
                while (matcher.find()) {
                    String resourceLocation = matcher.group(1);
                    if (!resourceLocation.startsWith("shincolle:")) {
                        continue;
                    }
                    Path resolved = resolveParticleTexture(resourceLocation);
                    if (!Files.exists(resolved)) {
                        missing.add(PARTICLE_JSON_ROOT.relativize(json) + " -> " + resourceLocation);
                    }
                }
            }
        }

        assertTrue(missing.isEmpty(),
                () -> "Particle JSON files should only reference existing ShinColle particle textures, missing: "
                        + String.join(", ", missing));
    }

    private static Set<String> listParticleJsonNames() throws IOException {
        try (Stream<Path> stream = Files.walk(PARTICLE_JSON_ROOT)) {
            return stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(path -> {
                        String fileName = path.getFileName().toString();
                        return fileName.substring(0, fileName.length() - 5);
                    })
                    .collect(Collectors.toCollection(TreeSet::new));
        }
    }

    private static Set<String> readRegisteredParticleNames() throws IOException {
        String source = Files.readString(MOD_PARTICLES_SOURCE);
        Set<String> registered = new TreeSet<>();
        Matcher matcher = REGISTERED_PARTICLE_PATTERN.matcher(source);
        while (matcher.find()) {
            registered.add(matcher.group(1));
        }
        return registered;
    }

    private static Path resolveParticleTexture(String resourceLocation) {
        String[] parts = resourceLocation.split(":", 2);
        return PARTICLE_TEXTURE_ROOT.resolve(parts[1] + ".png");
    }
}
