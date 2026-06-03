package org.trp.shincolle.block;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipyardBlockEntitySyncRegressionTest {
    private static final Path VOL_CORE_BE =
            Path.of("src/main/java/org/trp/shincolle/block/entity/VolCoreBlockEntity.java");
    private static final Path SMALL_SHIPYARD_BE =
            Path.of("src/main/java/org/trp/shincolle/block/entity/SmallShipyardBlockEntity.java");
    private static final Path LARGE_SHIPYARD_BE =
            Path.of("src/main/java/org/trp/shincolle/block/entity/LargeShipyardBlockEntity.java");

    @Test
    void volCoreShouldSkipNoopSyncWrites() throws IOException {
        String source = Files.readString(VOL_CORE_BE);

        assertTrue(source.contains("if (this.remainedPower == remainedPower) {\n            return;\n        }"),
                "VolCore power writes should not sync when the stored power is unchanged");
        assertTrue(source.contains("if (this.btnActive == btnActive) {\n            return;\n        }"),
                "VolCore active-button writes should not sync when the toggle state is unchanged");
    }

    @Test
    void smallShipyardShouldSkipNoopBuildTypeWrites() throws IOException {
        String source = Files.readString(SMALL_SHIPYARD_BE);

        assertTrue(source.contains("boolean repeatBuild = clamped == 3 || clamped == 4;"),
                "Small shipyard should keep distinguishing repeat-build modes when deciding sync semantics");
        assertTrue(source.contains("int[] nextRecord = repeatBuild ? getCurrentMaterialAmount() : this.buildRecord;"),
                "Small shipyard repeat-build writes should compare the captured material record before syncing");
        assertTrue(source.contains("if (this.buildType == clamped\n                && (!repeatBuild || java.util.Arrays.equals(this.buildRecord, nextRecord))) {\n            return;\n        }"),
                "Small shipyard should not sync when the clamped build type and repeat-build record are unchanged");
    }

    @Test
    void largeShipyardShouldSkipNoopModeWrites() throws IOException {
        String source = Files.readString(LARGE_SHIPYARD_BE);

        assertTrue(source.contains("int next = Math.max(0, Math.min(buildType, 4));"),
                "Large shipyard build type writes should clamp before syncing");
        assertTrue(source.contains("if (this.buildType == next) {\n            return;\n        }"),
                "Large shipyard build type writes should not sync when the clamped type is unchanged");
        assertTrue(source.contains("int next = invMode <= 0 ? 0 : 1;"),
                "Large shipyard inventory mode writes should clamp before syncing");
        assertTrue(source.contains("if (this.invMode == next) {\n            return;\n        }"),
                "Large shipyard inventory mode writes should not sync when the clamped mode is unchanged");
        assertTrue(source.contains("int next = Math.max(0, Math.min(selectMat, MAT_COUNT - 1));"),
                "Large shipyard selected material writes should clamp before syncing");
        assertTrue(source.contains("if (this.selectMat == next) {\n            return;\n        }"),
                "Large shipyard selected material writes should not sync when the clamped value is unchanged");
    }
}
