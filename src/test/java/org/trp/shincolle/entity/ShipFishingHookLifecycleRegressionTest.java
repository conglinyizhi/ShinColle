package org.trp.shincolle.entity;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipFishingHookLifecycleRegressionTest {
    private static final Path TASK_HELPER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/utility/TaskHelper.kt");
    private static final Path FISH_HOOK_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/EntityShipFishingHook.kt");

    @Test
    void fishingTaskAndHookEntityShouldShareLegacyTimeoutThreshold() throws IOException {
        String taskHelper = Files.readString(TASK_HELPER_SOURCE);
        String hook = Files.readString(FISH_HOOK_SOURCE);

        assertTrue(taskHelper.contains("host.getFishHook().tickCount > Config.tickFishingMin + Config.tickFishingMax)"),
                "Fishing task should clear hooks at the legacy max wait threshold");
        assertTrue(hook.contains("if (this.tickCount > Config.tickFishingMin + Config.tickFishingMax)"),
                "Fishing hook entity should use the same legacy max wait threshold");
        assertTrue(hook.contains("if (this.host != null && (!this.host.isAlive() || this.host.isRemoved())) {\n            this.host = null;\n        }"),
                "Fishing hook host lookup should clear stale cached host references when the ship dies or is removed");
        assertTrue(hook.contains("if (e instanceof EntityShipBase ship && ship.isAlive() && !ship.isRemoved()) {"),
                "Fishing hook client-side host sync should only latch onto live, non-removed ships");
        assertTrue(hook.contains("if (this.host == null || !this.host.isAlive() || this.host.isRemoved()) {\n                this.discard();\n                return;\n            }"),
                "Fishing hook server tick should discard the hook when its host ship is gone, dead, or already removed");
        assertTrue(hook.contains("this.host.setFishHook(null);"),
                "Fishing hook removal should clear the host-side hook reference");
    }
}
