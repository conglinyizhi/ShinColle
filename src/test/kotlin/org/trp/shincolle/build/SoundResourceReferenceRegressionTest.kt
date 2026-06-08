package org.trp.shincolle.build

import org.junit.jupiter.api.Test

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.TreeSet
import java.util.regex.Pattern
import java.util.stream.Collectors

import org.junit.jupiter.api.Assertions.assertTrue

class SoundResourceReferenceRegressionTest {
    private val SOUNDS_JSON: Path =
            Path.of("src/main/resources/assets/shincolle/sounds.json")
    private val SOUND_ROOT: Path =
            Path.of("src/main/resources/assets/shincolle/sounds")
    private val MAINTAINED_LANGS: Map<String, Path> = linkedMapOf(
            "en_us" to Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            "ja_jp" to Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            "zh_cn" to Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            "zh_tw" to Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
    )
    private val MOD_SOUNDS_SOURCE: Path =
            Path.of("src/main/java/org/trp/shincolle/init/ModSounds.kt")

    private val SOUND_EVENT_KEY_PATTERN: Pattern =
            Pattern.compile("\"([a-z0-9_./-]+)\"\\s*:\\s*\\{")
    private val SOUND_FILE_PATTERN: Pattern =
            Pattern.compile("\"(shincolle:[a-z0-9_./-]+)\"")
    private val SUBTITLE_PATTERN: Pattern =
            Pattern.compile("\"subtitle\"\\s*:\\s*\"([a-z0-9_./-]+)\"")
    private val DIRECT_REGISTER_PATTERN: Pattern =
            Pattern.compile("register\\(\"([a-z0-9_./-]+)\"\\)")

    @Test
    fun soundsJsonShouldReferenceExistingShincolleOggFiles() {
        val sounds = Files.readString(SOUNDS_JSON)
        val missing = ArrayList<String>()

        val matcher = SOUND_FILE_PATTERN.matcher(sounds)
        while (matcher.find()) {
            val resourceLocation = matcher.group(1)!!
            val resolved = resolveSoundPath(resourceLocation)
            if (!Files.exists(resolved)) {
                missing.add(resourceLocation)
            }
        }

        assertTrue(missing.isEmpty()) {
            "sounds.json should only reference existing ShinColle sound files, missing: " +
                    missing.joinToString(", ")
        }
    }

    @Test
    fun maintainedLanguagesShouldCoverEverySoundSubtitleKey() {
        val sounds = Files.readString(SOUNDS_JSON)
        val subtitleKeys = readSubtitleKeys(sounds)

        for (entry in MAINTAINED_LANGS) {
            val langKeys = readKeys(entry.value)
            val missing = TreeSet(subtitleKeys)
            missing.removeAll(langKeys)

            assertTrue(missing.isEmpty()) {
                "Language file " + entry.key +
                        " should define every sound subtitle key, missing: " +
                        missing.joinToString(", ")
            }
        }
    }

    @Test
    fun modSoundsRegistrationsShouldRemainBackedBySoundsJsonEntries() {
        val modSounds = Files.readString(MOD_SOUNDS_SOURCE)
        assertTrue(modSounds.contains("for (int i = 0; i < 24; i++) {\n            sounds.add(register(\"ship-time\" + i));\n        }")) {
            "ModSounds should keep generating the 24 hourly ship-time events"
        }

        val registered = readRegisteredSoundEvents(modSounds)
        val definedEvents = readDefinedSoundEvents()
        val missingDefinitions = TreeSet(registered)
        missingDefinitions.removeAll(definedEvents)

        assertTrue(missingDefinitions.isEmpty()) {
            "Every registered sound event should map to a sounds.json entry, missing: " +
                    missingDefinitions.joinToString(", ")
        }
    }

    @Test
    fun soundsJsonEntriesShouldStayWithinRegisteredOrResourceOnlyAllowlist() {
        val modSounds = Files.readString(MOD_SOUNDS_SOURCE)
        val registered = readRegisteredSoundEvents(modSounds)
        val resourceOnly = readDefinedSoundEvents()
        resourceOnly.removeAll(registered)

        val expectedResourceOnly: Set<String> = setOf(
                "ship-bell",
                "ship-garuru",
                "ship-hitmetal",
                "ship-jet",
                "ship-laser"
        )

        assertTrue(resourceOnly.equals(expectedResourceOnly)) {
            "sounds.json entries without ModSounds registrations changed unexpectedly, found: " +
                    resourceOnly.joinToString(", ")
        }
    }

    private fun resolveSoundPath(resourceLocation: String): Path {
        val parts = resourceLocation.split(":", limit = 2)
        return SOUNDS_JSON.parent.resolve("sounds").resolve(parts[1] + ".ogg")
    }

    private fun readKeys(file: Path): Set<String> {
        return Files.readAllLines(file).stream()
                .map(String::trim)
                .filter { it.startsWith("\"") }
                .map { it.substring(1, it.indexOf('"', 1)) }
                .collect(Collectors.toSet())
    }

    private fun readSubtitleKeys(sounds: String): Set<String> {
        val keys = TreeSet<String>()
        val matcher = SUBTITLE_PATTERN.matcher(sounds)
        while (matcher.find()) {
            keys.add(matcher.group(1)!!)
        }
        return keys
    }

    private fun readRegisteredSoundEvents(modSounds: String): Set<String> {
        val registered = TreeSet<String>()
        val matcher = DIRECT_REGISTER_PATTERN.matcher(modSounds)
        while (matcher.find()) {
            registered.add(matcher.group(1)!!)
        }
        for (hour in 0 until 24) {
            registered.add("ship-time$hour")
        }
        return registered
    }

    private fun readDefinedSoundEvents(): MutableSet<String> {
        val sounds = Files.readString(SOUNDS_JSON)
        val events = TreeSet<String>()
        val matcher = SOUND_EVENT_KEY_PATTERN.matcher(sounds)
        while (matcher.find()) {
            events.add(matcher.group(1)!!)
        }
        return events
    }
}
