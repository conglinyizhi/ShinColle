package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ModIntegrationRunConfigRegressionTest {
    private static final Path BUILD_GRADLE = Path.of("build.gradle");

    @Test
    void modIntegrationClientShouldSyncRuntimeModsIntoRunModsDirectory() throws IOException {
        String buildScript = Files.readString(BUILD_GRADLE);

        assertTrue(buildScript.contains("def syncModIntegrationMods = tasks.register(\"syncModIntegrationMods\", Sync) {"),
                "build.gradle should define a dedicated sync task that copies integration mods into run/mods");
        assertTrue(buildScript.contains("from(configurations.modIntegrationRuntimeOnly)"),
                "build.gradle should copy the integration runtime configuration into run/mods");
        assertTrue(buildScript.contains("into(layout.projectDirectory.dir(\"run/mods\"))"),
                "build.gradle should sync integration mods into the run/mods directory");
        assertTrue(buildScript.contains("taskBefore(syncModIntegrationMods)"),
                "modIntegrationClient should run the mod sync task before launch so external integration jars are discovered as mods");
    }
}
