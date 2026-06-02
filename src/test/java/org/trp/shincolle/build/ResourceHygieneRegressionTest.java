package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ResourceHygieneRegressionTest {
    private static final Path RESOURCE_ROOT = Path.of("src/main/resources/assets/shincolle");

    @Test
    void resourcesShouldNotContainKnownDesktopIniJunkFiles() throws IOException {
        try (Stream<Path> stream = Files.walk(RESOURCE_ROOT)) {
            boolean hasDesktopIni = stream
                    .filter(Files::isRegularFile)
                    .anyMatch(path -> path.getFileName().toString().equalsIgnoreCase("desktop.ini"));
            assertFalse(hasDesktopIni,
                    "Resource tree must not contain desktop.ini junk files from Windows explorer");
        }
    }

    @Test
    void resourcesShouldNotContainCaseOnlyDuplicatePaths() throws IOException {
        Map<String, Path> seen = new HashMap<>();

        try (Stream<Path> stream = Files.walk(RESOURCE_ROOT)) {
            for (Path path : (Iterable<Path>) stream.filter(Files::isRegularFile)::iterator) {
                String relative = RESOURCE_ROOT.relativize(path).toString().replace('\\', '/');
                String folded = relative.toLowerCase(Locale.ROOT);
                Path previous = seen.putIfAbsent(folded, path);
                assertFalse(previous != null && !previous.equals(path),
                        () -> "Resource tree must not contain case-only duplicate files: "
                                + RESOURCE_ROOT.relativize(previous) + " and " + relative);
            }
        }
    }
}
