package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ModIntegrationRunConfigRegressionTest {
    private static final Path BUILD_GRADLE = Path.of("build.gradle");

    @Test
    void modIntegrationClientShouldUseDedicatedRunDirectoryForIntegrationMods() throws IOException {
        String buildScript = Files.readString(BUILD_GRADLE);

        assertTrue(buildScript.contains("def modIntegrationRunDir = layout.projectDirectory.dir(\"run-mod-integration\")"),
                "build.gradle should define a dedicated run directory for the integration client");
        assertTrue(buildScript.contains("def syncModIntegrationMods = tasks.register(\"syncModIntegrationMods\", Sync) {"),
                "build.gradle should define a dedicated sync task that copies integration mods into run/mods");
        assertTrue(buildScript.contains("from(configurations.modIntegrationRuntimeOnly)"),
                "build.gradle should copy the integration runtime configuration into run/mods");
        assertTrue(buildScript.contains("into(modIntegrationRunDir.dir(\"mods\"))"),
                "build.gradle should sync integration mods into the dedicated integration mods directory");
        assertTrue(buildScript.contains("def cleanModIntegrationMods = tasks.register(\"cleanModIntegrationMods\", Delete) {"),
                "build.gradle should define a cleanup task for integration mods leaked into the default run directory");
        assertTrue(buildScript.contains("gameDirectory = modIntegrationRunDir.asFile"),
                "modIntegrationClient should launch from the dedicated integration run directory");
        assertTrue(buildScript.contains("taskBefore(syncModIntegrationMods)"),
                "modIntegrationClient should run the mod sync task before launch so external integration jars are discovered as mods");
        assertTrue(buildScript.contains("taskBefore(cleanModIntegrationMods)"),
                "default client and server runs should clean leaked integration mods before launch");
    }
}
