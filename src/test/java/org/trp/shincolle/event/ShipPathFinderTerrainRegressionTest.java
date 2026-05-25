package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipPathFinderTerrainRegressionTest {
    private static final Path PATH_FINDER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/path/ShipLegacyPathFinder.java");

    @Test
    void pathFinderShouldNotTreatStairsAndClimbablesAsHardBlockedTiles() throws IOException {
        String source = Files.readString(PATH_FINDER_SOURCE);

        assertTrue(source.contains("sawOpenable = true;\n                        continue;"),
                "Legacy path finder should treat stairs and climbables as traversable candidates");
        assertFalse(source.contains("return LegacyPathType.STAIR_OR_LADDER;"),
                "Legacy path finder should no longer reject stairs and ladders as immediately unpathable");
    }
}
