package org.trp.shincolle.block;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WayPointBlockEntityPersistenceRegressionTest {
    private static final Path WAYPOINT_BLOCK_ENTITY =
            Path.of("src/main/java/org/trp/shincolle/block/entity/WayPointBlockEntity.java");

    @Test
    void waypointBlockEntityShouldPersistRoutingAndOwnerState() throws IOException {
        String source = Files.readString(WAYPOINT_BLOCK_ENTITY);

        assertTrue(source.contains("private BlockPos lastPos = BlockPos.ZERO;"),
                "Waypoint block entity should keep persisting the previous waypoint link");
        assertTrue(source.contains("private BlockPos nextPos = BlockPos.ZERO;"),
                "Waypoint block entity should keep persisting the next waypoint link");
        assertTrue(source.contains("private BlockPos chestPos = BlockPos.ZERO;"),
                "Waypoint block entity should keep persisting the paired container link");
        assertTrue(source.contains("private int wpStayTime = 0;"),
                "Waypoint block entity should keep persisting the configured stay-time index");
        assertTrue(source.contains("private UUID ownerUUID = null;"),
                "Waypoint block entity should keep persisting the owner UUID");
        assertTrue(source.contains("private String ownerName = \"\";"),
                "Waypoint block entity should keep persisting the owner name");
        assertTrue(source.contains("tag.put(\"lastPos\", NbtUtils.writeBlockPos(lastPos));"),
                "Waypoint block entity NBT should save the previous waypoint link");
        assertTrue(source.contains("tag.put(\"nextPos\", NbtUtils.writeBlockPos(nextPos));"),
                "Waypoint block entity NBT should save the next waypoint link");
        assertTrue(source.contains("tag.put(\"chestPos\", NbtUtils.writeBlockPos(chestPos));"),
                "Waypoint block entity NBT should save the paired container link");
        assertTrue(source.contains("tag.putInt(\"wpStayTime\", wpStayTime);"),
                "Waypoint block entity NBT should save the stay-time index");
        assertTrue(source.contains("tag.putUUID(\"ownerUUID\", ownerUUID);"),
                "Waypoint block entity NBT should save the owner UUID when present");
        assertTrue(source.contains("tag.putString(\"ownerName\", ownerName);"),
                "Waypoint block entity NBT should save the owner name");
    }

    @Test
    void waypointBlockEntityShouldSkipNoopSyncWrites() throws IOException {
        String source = Files.readString(WAYPOINT_BLOCK_ENTITY);

        assertTrue(source.contains("if (this.lastPos.equals(next)) {\n            return;\n        }"),
                "Waypoint previous-link writes should not sync when the position is unchanged");
        assertTrue(source.contains("if (this.nextPos.equals(next)) {\n            return;\n        }"),
                "Waypoint next-link writes should not sync when the position is unchanged");
        assertTrue(source.contains("if (this.chestPos.equals(next)) {\n            return;\n        }"),
                "Waypoint paired-container writes should not sync when the position is unchanged");
        assertTrue(source.contains("if (java.util.Objects.equals(this.ownerUUID, uuid)) {\n            return;\n        }"),
                "Waypoint owner UUID writes should not sync when the owner is unchanged");
        assertTrue(source.contains("String next = name == null ? \"\" : name;"),
                "Waypoint owner-name writes should normalize null to an empty string");
        assertTrue(source.contains("if (this.ownerName.equals(next)) {\n            return;\n        }"),
                "Waypoint owner-name writes should not sync when the normalized name is unchanged");
        assertTrue(source.contains("int next = (wpStayTime + 1) % 17;"),
                "Waypoint stay-time cycling should compute the next state before syncing");
    }
}
