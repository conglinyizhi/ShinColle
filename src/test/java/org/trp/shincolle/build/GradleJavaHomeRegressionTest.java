package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GradleJavaHomeRegressionTest {
    private static final Path GRADLE_PROPERTIES = Path.of("gradle.properties");
    private static final Path BUILD_GRADLE = Path.of("build.gradle");

    @Test
    void buildShouldUseToolchainsInsteadOfHardcodedJavaHome() throws IOException {
        String gradleProperties = Files.readString(GRADLE_PROPERTIES);
        String buildGradle = Files.readString(BUILD_GRADLE);

        assertFalse(gradleProperties.contains("org.gradle.java.home"),
                "gradle.properties must not hardcode org.gradle.java.home because CI JAVA_HOME paths vary by runner");
        assertTrue(buildGradle.contains("java.toolchain.languageVersion = JavaLanguageVersion.of(21)"),
                "build.gradle should keep the Java 21 toolchain requirement");
        assertTrue(buildGradle.contains("jvmToolchain(21)"),
                "build.gradle should keep the Kotlin JVM toolchain requirement");
    }
}
