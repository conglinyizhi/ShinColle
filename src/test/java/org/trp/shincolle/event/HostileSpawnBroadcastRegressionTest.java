package org.trp.shincolle.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HostileSpawnBroadcastRegressionTest {
    private static final Path HOSTILE_SPAWN_MANAGER =
            Path.of("src/main/java/org/trp/shincolle/event/HostileSpawnManager.java");

    @Test
    void bossSpawnBroadcastShouldRequireAtLeastOneSuccessfulSpawn() throws IOException {
        String source = Files.readString(HOSTILE_SPAWN_MANAGER);

        assertTrue(source.contains("int spawnedCount = 0;"),
                "Boss spawn flow should track how many hostile ships were actually spawned");
        assertTrue(source.contains("if (spawnRandomHostileShip(level, random, scaleLevel,\n                        spawnX + random.nextInt(3),\n                        spawnY + 0.5D,\n                        spawnZ + random.nextInt(3))) {\n                    spawnedCount++;\n                }"),
                "Boss fleet generation should only count boss spawns that actually succeeded");
        assertTrue(source.contains("if (spawnRandomHostileShip(level, random, random.nextInt(2),\n                        spawnX + random.nextInt(3),\n                        spawnY + 0.5D,\n                        spawnZ + random.nextInt(3))) {\n                    spawnedCount++;\n                }"),
                "Boss fleet generation should only count minion spawns that actually succeeded");
        assertTrue(source.contains("if (spawnedCount > 0) {\n                broadcastBossSpawn(level, random, spawnX, spawnY, spawnZ);\n            }"),
                "Boss spawn broadcast should only fire when at least one hostile ship was actually added to the world");
    }
}
