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

class ModelTextureReferenceRegressionTest {
    private static final Path MODEL_ROOT = Path.of("src/main/resources/assets/shincolle/models");
    private static final Path ASSET_ROOT = Path.of("src/main/resources/assets");
    private static final Pattern TEXTURES_BLOCK_PATTERN =
            Pattern.compile("\"textures\"\\s*:\\s*\\{(.*?)\\}", Pattern.DOTALL);
    private static final Pattern TEXTURE_VALUE_PATTERN =
            Pattern.compile("\"[^\"]+\"\\s*:\\s*\"((?:minecraft|shincolle):[^\"#/][^\"]*)\"");

    @Test
    void modelTextureReferencesShouldResolveToExistingTextureFiles() throws IOException {
        List<String> missing = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(MODEL_ROOT)) {
            for (Path json : (Iterable<Path>) stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))::iterator) {
                String content = Files.readString(json);
                Matcher texturesBlockMatcher = TEXTURES_BLOCK_PATTERN.matcher(content);
                while (texturesBlockMatcher.find()) {
                    String texturesBlock = texturesBlockMatcher.group(1);
                    Matcher matcher = TEXTURE_VALUE_PATTERN.matcher(texturesBlock);
                    while (matcher.find()) {
                        String resourceLocation = matcher.group(1);
                        if (!resourceLocation.startsWith("shincolle:")) {
                            continue;
                        }
                        Path resolved = resolveTexturePath(resourceLocation);
                        if (!Files.exists(resolved)) {
                            missing.add(MODEL_ROOT.relativize(json) + " -> " + resourceLocation);
                        }
                    }
                } 
            }
        }

        assertTrue(missing.isEmpty(),
                () -> "Model texture references must resolve to real texture files, missing: " + String.join(", ", missing));
    }

    private static Path resolveTexturePath(String resourceLocation) {
        String[] parts = resourceLocation.split(":", 2);
        return ASSET_ROOT.resolve(parts[0]).resolve("textures").resolve(parts[1] + ".png");
    }
}
