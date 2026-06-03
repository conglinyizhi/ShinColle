package org.trp.shincolle.block;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CraneBlockEntitySyncRegressionTest {
    private static final Path CRANE_BE =
            Path.of("src/main/java/org/trp/shincolle/block/entity/CraneBlockEntity.java");

    @Test
    void craneBlockEntityShouldSkipNoopSyncWrites() throws IOException {
        String source = Files.readString(CRANE_BE);

        assertTrue(source.contains("if (this.remainedPower == val) {\n            return;\n        }"),
                "Crane power writes should not sync when the stored power is unchanged");
        assertTrue(source.contains("if (this.isActive == val) {\n            return;\n        }"),
                "Crane active writes should not sync when the active flag is unchanged");
        assertTrue(source.contains("if (this.checkMetadata == val) {\n            return;\n        }"),
                "Crane metadata-filter writes should not sync when the flag is unchanged");
        assertTrue(source.contains("if (this.checkOredict == val) {\n            return;\n        }"),
                "Crane ore-dict-filter writes should not sync when the flag is unchanged");
        assertTrue(source.contains("if (this.checkNbt == val) {\n            return;\n        }"),
                "Crane NBT-filter writes should not sync when the flag is unchanged");
        assertTrue(source.contains("if (this.enabLoad == val) {\n            return;\n        }"),
                "Crane load-toggle writes should not sync when the flag is unchanged");
        assertTrue(source.contains("if (this.enabUnload == val) {\n            return;\n        }"),
                "Crane unload-toggle writes should not sync when the flag is unchanged");
        assertTrue(source.contains("if (this.craneMode == val) {\n            return;\n        }"),
                "Crane mode writes should not sync when the selected mode is unchanged");
        assertTrue(source.contains("if (this.modeItem == val) {\n            return;\n        }"),
                "Crane item-mode bitmap writes should not sync when the bitmap is unchanged");
        assertTrue(source.contains("int next = val ? modeItem | (1 << id) : modeItem & ~(1 << id);"),
                "Crane slot item-mode writes should compute the next bitmap before syncing");
        assertTrue(source.contains("if (this.modeItem == next) {\n            return;\n        }"),
                "Crane slot item-mode writes should not sync when the requested bit is already in the target state");
        assertTrue(source.contains("if (this.modeRedstone == val) {\n            return;\n        }"),
                "Crane redstone-mode writes should not sync when the mode is unchanged");
        assertTrue(source.contains("if (this.modeLiquid == val) {\n            return;\n        }"),
                "Crane liquid-mode writes should not sync when the mode is unchanged");
        assertTrue(source.contains("if (this.modeEnergy == val) {\n            return;\n        }"),
                "Crane energy-mode writes should not sync when the mode is unchanged");
        assertTrue(source.contains("if (this.lastPos.equals(next)) {\n            return;\n        }"),
                "Crane previous waypoint writes should not sync when the position is unchanged");
        assertTrue(source.contains("if (this.nextPos.equals(next)) {\n            return;\n        }"),
                "Crane next waypoint writes should not sync when the position is unchanged");
        assertTrue(source.contains("if (this.chestPos.equals(next) && this.isPaired == nextPaired) {\n            return;\n        }"),
                "Crane paired chest writes should not sync when the position and pairing flag are unchanged");
        assertTrue(source.contains("if (java.util.Objects.equals(this.ownerUUID, uuid)) {\n            return;\n        }"),
                "Crane owner UUID writes should not sync when the owner is unchanged");
        assertTrue(source.contains("String next = name == null ? \"\" : name;"),
                "Crane owner-name writes should normalize null to an empty string");
        assertTrue(source.contains("if (this.ownerName.equals(next)) {\n            return;\n        }"),
                "Crane owner-name writes should not sync when the normalized name is unchanged");
    }
}
