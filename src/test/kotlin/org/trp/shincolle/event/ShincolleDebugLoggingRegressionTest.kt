package org.trp.shincolle.event

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

class ShincolleDebugLoggingRegressionTest {
    private val CONFIG_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/Config.kt")
    private val MOD_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle/Shincolle.kt")
    private val MAIN_SOURCE: Path =
        Path.of("src/main/java/org/trp/shincolle")

    @Test
    fun debugLoggingShouldBeConfigGatedAndDisabledByDefault() {
        val config = Files.readString(CONFIG_SOURCE)
        val mod = Files.readString(MOD_SOURCE)

        assertTrue(config.contains("public static boolean debugLogging = false;")) {
            "Verbose diagnostics should be disabled during normal gameplay"
        }
        assertTrue(config.contains(".define(\"debugLogging\", debugLogging)")) {
            "Debug logging should be exposed as a common config option"
        }
        assertTrue(mod.contains("public static void debugLog(String message, Object... args)")) {
            "Debug diagnostics should go through one helper"
        }
        assertTrue(mod.contains("if (!Config.debugLogging)")) {
            "Debug helper should suppress diagnostics unless explicitly enabled"
        }
    }

    @Test
    fun shincolleDebugPrefixShouldOnlyAppearInCentralHelper() {
        var directPrefixCount: Long
        Files.walk(MAIN_SOURCE).use { paths ->
            directPrefixCount = paths
                .filter { path -> path.toString().endsWith(".kt") }
                .map { path ->
                    try {
                        Files.readString(path)
                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    }
                }
                .filter { source -> source.contains("[ShinColleDebug]") }
                .count()
        }

        assertEquals(1, directPrefixCount) {
            "Only the central Shincolle.debugLog helper should write the debug prefix"
        }
    }
}
