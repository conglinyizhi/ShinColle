package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipPathFinderTerrainRegressionTest {
    private static final Path PATH_FINDER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/base/path/ShipLegacyPathFinder.kt");

    @Test
    void pathFinderShouldNotTreatStairsAndClimbablesAsHardBlockedTiles() throws IOException {
        String source = Files.readString(PATH_FINDER_SOURCE);

        assertTrue(source.contains("sawOpenable = true;\n                        continue;"),
                "Legacy path finder should treat stairs and climbables as traversable candidates");
        assertTrue(source.contains("return LegacyPathType.OPENABLE;"),
                "Legacy path finder should keep a single traversable openable terrain type");
        assertFalse(source.contains("STAIR_OR_LADDER"),
                "Legacy path finder should not keep dead terrain states for stairs and ladders");
        assertFalse(source.contains("return LegacyPathType.STAIR_OR_LADDER;"),
                "Legacy path finder should no longer reject stairs and ladders as immediately unpathable");
    }

    @Test
    void pathFinderShouldOnlyReadFreshPathOptions() throws IOException {
        String source = Files.readString(PATH_FINDER_SOURCE);

        assertTrue(source.contains("int optionCount = findPathOptions(host, current, end, range, sizeX, sizeY, sizeZ);"),
                "Path finder should track how many reusable path option slots are fresh");
        assertTrue(source.contains("for (int i = 0; i < optionCount; i++)"),
                "Path finder should only read path options populated during the current expansion");
        assertFalse(source.contains("for (ShipLegacyPathPoint next : this.pathOptions)"),
                "Path finder should not iterate stale path option slots from earlier expansions");
    }
}
