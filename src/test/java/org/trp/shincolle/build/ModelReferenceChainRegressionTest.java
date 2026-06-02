package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelReferenceChainRegressionTest {
    private static final Path BLOCKSTATE_ROOT = Path.of("src/main/resources/assets/shincolle/blockstates");
    private static final Path MODEL_ROOT = Path.of("src/main/resources/assets/shincolle/models");
    private static final Path ASSET_ROOT = Path.of("src/main/resources/assets");
    private static final Pattern BLOCKSTATE_MODEL_PATTERN =
            Pattern.compile("\"model\"\\s*:\\s*\"(shincolle:block/[^\"]+)\"");
    private static final Pattern MODEL_PARENT_PATTERN =
            Pattern.compile("\"parent\"\\s*:\\s*\"(shincolle:[^\"]+)\"");
    private static final Pattern OVERRIDE_MODEL_PATTERN =
            Pattern.compile("\"model\"\\s*:\\s*\"(shincolle:[^\"]+)\"");

    @Test
    void blockstateModelReferencesShouldResolveToExistingModelFiles() throws IOException {
        List<String> missing = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(BLOCKSTATE_ROOT)) {
            for (Path json : (Iterable<Path>) stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))::iterator) {
                String content = Files.readString(json);
                Matcher matcher = BLOCKSTATE_MODEL_PATTERN.matcher(content);
                while (matcher.find()) {
                    String resourceLocation = matcher.group(1);
                    Path resolved = resolveModelPath(resourceLocation);
                    if (!Files.exists(resolved)) {
                        missing.add(BLOCKSTATE_ROOT.relativize(json) + " -> " + resourceLocation);
                    }
                }
            }
        }

        assertTrue(missing.isEmpty(),
                () -> "Blockstate model references must resolve to real model files, missing: " + String.join(", ", missing));
    }

    @Test
    void customModelParentAndOverrideReferencesShouldResolveToExistingModelFiles() throws IOException {
        List<String> missing = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(MODEL_ROOT)) {
            for (Path json : (Iterable<Path>) stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))::iterator) {
                String content = Files.readString(json);

                Matcher parentMatcher = MODEL_PARENT_PATTERN.matcher(content);
                while (parentMatcher.find()) {
                    String resourceLocation = parentMatcher.group(1);
                    Path resolved = resolveModelPath(resourceLocation);
                    if (!Files.exists(resolved)) {
                        missing.add(MODEL_ROOT.relativize(json) + " parent -> " + resourceLocation);
                    }
                }

                Matcher overrideMatcher = OVERRIDE_MODEL_PATTERN.matcher(content);
                while (overrideMatcher.find()) {
                    String resourceLocation = overrideMatcher.group(1);
                    if (content.regionMatches(overrideMatcher.start() - 9 < 0 ? 0 : overrideMatcher.start() - 9, "\"parent\"", 0, 8)) {
                        continue;
                    }
                    if (!resourceLocation.startsWith("shincolle:")) {
                        continue;
                    }
                    Path resolved = resolveModelPath(resourceLocation);
                    if (!Files.exists(resolved)) {
                        missing.add(MODEL_ROOT.relativize(json) + " override -> " + resourceLocation);
                    }
                }
            }
        }

        assertTrue(missing.isEmpty(),
                () -> "Custom model parent and override references must resolve to real model files, missing: " + String.join(", ", missing));
    }

    private static Path resolveModelPath(String resourceLocation) {
        String[] parts = resourceLocation.split(":", 2);
        return ASSET_ROOT.resolve(parts[0]).resolve("models").resolve(parts[1] + ".json");
    }
}
