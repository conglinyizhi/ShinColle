package org.trp.shincolle.entity;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipFishingHookLifecycleRegressionTest {
    private static final Path TASK_HELPER_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/utility/TaskHelper.java");
    private static final Path FISH_HOOK_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/entity/EntityShipFishingHook.java");

    @Test
    void fishingTaskAndHookEntityShouldShareLegacyTimeoutThreshold() throws IOException {
        String taskHelper = Files.readString(TASK_HELPER_SOURCE);
        String hook = Files.readString(FISH_HOOK_SOURCE);

        assertTrue(taskHelper.contains("host.getFishHook().tickCount > Config.tickFishingMin + Config.tickFishingMax)"),
                "Fishing task should clear hooks at the legacy max wait threshold");
        assertTrue(hook.contains("if (this.tickCount > Config.tickFishingMin + Config.tickFishingMax)"),
                "Fishing hook entity should use the same legacy max wait threshold");
        assertTrue(hook.contains("this.host.setFishHook(null);"),
                "Fishing hook removal should clear the host-side hook reference");
    }
}
