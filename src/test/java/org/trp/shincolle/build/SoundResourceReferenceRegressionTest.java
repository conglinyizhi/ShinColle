package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SoundResourceReferenceRegressionTest {
    private static final Path SOUNDS_JSON =
            Path.of("src/main/resources/assets/shincolle/sounds.json");
    private static final Path SOUND_ROOT =
            Path.of("src/main/resources/assets/shincolle/sounds");
    private static final Path EN_US_LANG =
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json");
    private static final Path MOD_SOUNDS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModSounds.java");

    private static final Pattern SOUND_EVENT_KEY_PATTERN =
            Pattern.compile("\"([a-z0-9_./-]+)\"\\s*:\\s*\\{");
    private static final Pattern SOUND_FILE_PATTERN =
            Pattern.compile("\"(shincolle:[a-z0-9_./-]+)\"");
    private static final Pattern SUBTITLE_PATTERN =
            Pattern.compile("\"subtitle\"\\s*:\\s*\"([a-z0-9_./-]+)\"");

    @Test
    void soundsJsonShouldReferenceExistingShincolleOggFiles() throws IOException {
        String sounds = Files.readString(SOUNDS_JSON);
        List<String> missing = new ArrayList<>();

        Matcher matcher = SOUND_FILE_PATTERN.matcher(sounds);
        while (matcher.find()) {
            String resourceLocation = matcher.group(1);
            Path resolved = resolveSoundPath(resourceLocation);
            if (!Files.exists(resolved)) {
                missing.add(resourceLocation);
            }
        }

        assertTrue(missing.isEmpty(),
                () -> "sounds.json should only reference existing ShinColle sound files, missing: "
                        + String.join(", ", missing));
    }

    @Test
    void englishLanguageShouldCoverEverySoundSubtitleKey() throws IOException {
        String sounds = Files.readString(SOUNDS_JSON);
        Set<String> englishKeys = readKeys(EN_US_LANG);
        Set<String> missing = new TreeSet<>();

        Matcher matcher = SUBTITLE_PATTERN.matcher(sounds);
        while (matcher.find()) {
            String key = matcher.group(1);
            if (!englishKeys.contains(key)) {
                missing.add(key);
            }
        }

        assertTrue(missing.isEmpty(),
                () -> "English language file should define every sound subtitle key, missing: "
                        + String.join(", ", missing));
    }

    @Test
    void modSoundsRegistrationsShouldRemainBackedBySoundsJsonEntries() throws IOException {
        String modSounds = Files.readString(MOD_SOUNDS_SOURCE);
        String sounds = Files.readString(SOUNDS_JSON);

        Set<String> definedEvents = new TreeSet<>();
        Matcher eventMatcher = SOUND_EVENT_KEY_PATTERN.matcher(sounds);
        while (eventMatcher.find()) {
            definedEvents.add(eventMatcher.group(1));
        }

        List<String> expectedEvents = List.of(
                "ship-idle",
                "ship-hurt",
                "ship-death",
                "ship-firelight",
                "ship-explode",
                "ship-fireheavy",
                "ship-hit",
                "ship-levelup",
                "ship-machinegun",
                "ship-aircraft",
                "ship-marry",
                "ship-feed",
                "ship-knockback",
                "ship-item",
                "ship-ap_phase1",
                "ship-ap_phase2",
                "ship-ap_attack",
                "ship-yamato_ready",
                "ship-yamato_shot"
        );

        for (String event : expectedEvents) {
            assertTrue(modSounds.contains("register(\"" + event + "\")"),
                    () -> "ModSounds should keep registering sound event " + event);
            assertTrue(definedEvents.contains(event),
                    () -> "sounds.json should keep defining sound event " + event);
        }

        for (int hour = 0; hour < 24; hour++) {
            String event = "ship-time" + hour;
            assertTrue(definedEvents.contains(event),
                    () -> "sounds.json should keep defining hourly sound event " + event);
        }
        assertTrue(modSounds.contains("for (int i = 0; i < 24; i++) {\n            sounds.add(register(\"ship-time\" + i));\n        }"),
                "ModSounds should keep generating the 24 hourly ship-time events");
    }

    private static Path resolveSoundPath(String resourceLocation) {
        String[] parts = resourceLocation.split(":", 2);
        return SOUNDS_JSON.getParent().resolve("sounds").resolve(parts[1] + ".ogg");
    }

    private static Set<String> readKeys(Path file) throws IOException {
        return Files.readAllLines(file).stream()
                .map(String::trim)
                .filter(line -> line.startsWith("\""))
                .map(line -> line.substring(1, line.indexOf('"', 1)))
                .collect(Collectors.toSet());
    }
}
