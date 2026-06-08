package org.trp.shincolle.build

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class GradleJavaHomeRegressionTest {
    private val GRADLE_PROPERTIES: Path = Path.of("gradle.properties")
    private val BUILD_GRADLE: Path = Path.of("build.gradle")

    @Test
    fun buildShouldUseToolchainsInsteadOfHardcodedJavaHome() {
        val gradleProperties = Files.readString(GRADLE_PROPERTIES)
        val buildGradle = Files.readString(BUILD_GRADLE)

        assertFalse(gradleProperties.contains("org.gradle.java.home")) {
            "gradle.properties must not hardcode org.gradle.java.home because CI JAVA_HOME paths vary by runner"
        }
        assertTrue(buildGradle.contains("java.toolchain.languageVersion = JavaLanguageVersion.of(21)")) {
            "build.gradle should keep the Java 21 toolchain requirement"
        }
    }
}
