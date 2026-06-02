package org.trp.shincolle.build;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigScreenBindingRegressionTest {
    private static final Path CONFIG_SCREEN_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/client/gui/ShincolleConfigScreen.java");
    private static final Pattern CONFIG_GET_PATTERN =
            Pattern.compile("Config\\.([A-Z0-9_]+)\\.get\\(\\)");
    private static final Pattern CONFIG_SET_PATTERN =
            Pattern.compile("Config\\.([A-Z0-9_]+)::set");

    @Test
    void everyConfigValueShownInConfigScreenShouldKeepItsSaveBinding() throws IOException {
        String configScreen = Files.readString(CONFIG_SCREEN_SOURCE);
        Set<String> referencedFields = collectMatches(configScreen, CONFIG_GET_PATTERN);
        Set<String> savedFields = collectMatches(configScreen, CONFIG_SET_PATTERN);
        Set<String> missingSaveBindings = new TreeSet<>(referencedFields);
        missingSaveBindings.removeAll(savedFields);

        assertTrue(missingSaveBindings.isEmpty(),
                () -> "Every Config field shown in ShincolleConfigScreen should keep a matching save consumer: "
                        + String.join(", ", missingSaveBindings));
        assertTrue(configScreen.contains("builder.setSavingRunnable(() -> {"),
                "ShincolleConfigScreen should keep an explicit saving runnable");
        assertTrue(configScreen.contains("Config.SPEC.save();"),
                "ShincolleConfigScreen should keep saving the common config spec");
        assertTrue(configScreen.contains("Config.CLIENT_SPEC.save();"),
                "ShincolleConfigScreen should keep saving the client config spec");
    }

    private static Set<String> collectMatches(String source, Pattern pattern) {
        Set<String> matches = new TreeSet<>();
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        return matches;
    }
}
