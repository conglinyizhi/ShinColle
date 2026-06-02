package org.trp.shincolle.book;

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

class PatchouliResourceReferenceRegressionTest {
    private static final Path PATCHOULI_ROOT =
            Path.of("src/main/resources/assets/shincolle/patchouli_books/shincolle_manual");
    private static final Path ASSET_ROOT =
            Path.of("src/main/resources/assets");
    private static final Pattern IMAGE_PATTERN =
            Pattern.compile("\"([a-z0-9_]+:textures/[^\"]+)\"");

    @Test
    void patchouliImageReferencesShouldResolveToExistingAssetFiles() throws IOException {
        List<String> missing = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(PATCHOULI_ROOT)) {
            for (Path json : (Iterable<Path>) stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))::iterator) {
                String content = Files.readString(json);
                Matcher matcher = IMAGE_PATTERN.matcher(content);
                while (matcher.find()) {
                    String resourceLocation = matcher.group(1);
                    Path resolved = resolveAssetPath(resourceLocation);
                    if (!Files.exists(resolved)) {
                        missing.add(PATCHOULI_ROOT.relativize(json) + " -> " + resourceLocation);
                    }
                }
            }
        }

        assertTrue(missing.isEmpty(),
                () -> "Patchouli image references must resolve to real files, missing: " + String.join(", ", missing));
    }

    private static Path resolveAssetPath(String resourceLocation) {
        String[] parts = resourceLocation.split(":", 2);
        return ASSET_ROOT.resolve(parts[0]).resolve(parts[1]);
    }
}
