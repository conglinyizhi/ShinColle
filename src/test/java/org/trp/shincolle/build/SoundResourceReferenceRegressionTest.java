package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private static final Map<String, Path> MAINTAINED_LANGS = new LinkedHashMap<>(Map.of(
            "en_us", Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            "ja_jp", Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            "zh_cn", Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            "zh_tw", Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
    ));
    private static final Path MOD_SOUNDS_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModSounds.kt");

    private static final Pattern SOUND_EVENT_KEY_PATTERN =
            Pattern.compile("\"([a-z0-9_./-]+)\"\\s*:\\s*\\{");
    private static final Pattern SOUND_FILE_PATTERN =
            Pattern.compile("\"(shincolle:[a-z0-9_./-]+)\"");
    private static final Pattern SUBTITLE_PATTERN =
            Pattern.compile("\"subtitle\"\\s*:\\s*\"([a-z0-9_./-]+)\"");
    private static final Pattern DIRECT_REGISTER_PATTERN =
            Pattern.compile("register\\(\"([a-z0-9_./-]+)\"\\)");

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
    void maintainedLanguagesShouldCoverEverySoundSubtitleKey() throws IOException {
        String sounds = Files.readString(SOUNDS_JSON);
        Set<String> subtitleKeys = readSubtitleKeys(sounds);

        for (Map.Entry<String, Path> entry : MAINTAINED_LANGS.entrySet()) {
            Set<String> langKeys = readKeys(entry.getValue());
            Set<String> missing = new TreeSet<>(subtitleKeys);
            missing.removeAll(langKeys);

            assertTrue(missing.isEmpty(),
                    () -> "Language file " + entry.getKey()
                            + " should define every sound subtitle key, missing: "
                            + String.join(", ", missing));
        }
    }

    @Test
    void modSoundsRegistrationsShouldRemainBackedBySoundsJsonEntries() throws IOException {
        String modSounds = Files.readString(MOD_SOUNDS_SOURCE);
        assertTrue(modSounds.contains("for (int i = 0; i < 24; i++) {\n            sounds.add(register(\"ship-time\" + i));\n        }"),
                "ModSounds should keep generating the 24 hourly ship-time events");

        Set<String> registered = readRegisteredSoundEvents(modSounds);
        Set<String> definedEvents = readDefinedSoundEvents();
        Set<String> missingDefinitions = new TreeSet<>(registered);
        missingDefinitions.removeAll(definedEvents);

        assertTrue(missingDefinitions.isEmpty(),
                () -> "Every registered sound event should map to a sounds.json entry, missing: "
                        + String.join(", ", missingDefinitions));
    }

    @Test
    void soundsJsonEntriesShouldStayWithinRegisteredOrResourceOnlyAllowlist() throws IOException {
        String modSounds = Files.readString(MOD_SOUNDS_SOURCE);
        Set<String> registered = readRegisteredSoundEvents(modSounds);
        Set<String> resourceOnly = readDefinedSoundEvents();
        resourceOnly.removeAll(registered);

        Set<String> expectedResourceOnly = Set.of(
                "ship-bell",
                "ship-garuru",
                "ship-hitmetal",
                "ship-jet",
                "ship-laser"
        );

        assertTrue(resourceOnly.equals(expectedResourceOnly),
                () -> "sounds.json entries without ModSounds registrations changed unexpectedly, found: "
                        + String.join(", ", resourceOnly));
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

    private static Set<String> readSubtitleKeys(String sounds) {
        Set<String> keys = new TreeSet<>();
        Matcher matcher = SUBTITLE_PATTERN.matcher(sounds);
        while (matcher.find()) {
            keys.add(matcher.group(1));
        }
        return keys;
    }

    private static Set<String> readRegisteredSoundEvents(String modSounds) {
        Set<String> registered = new TreeSet<>();
        Matcher matcher = DIRECT_REGISTER_PATTERN.matcher(modSounds);
        while (matcher.find()) {
            registered.add(matcher.group(1));
        }
        for (int hour = 0; hour < 24; hour++) {
            registered.add("ship-time" + hour);
        }
        return registered;
    }

    private static Set<String> readDefinedSoundEvents() throws IOException {
        String sounds = Files.readString(SOUNDS_JSON);
        Set<String> events = new TreeSet<>();
        Matcher matcher = SOUND_EVENT_KEY_PATTERN.matcher(sounds);
        while (matcher.find()) {
            events.add(matcher.group(1));
        }
        return events;
    }
}
