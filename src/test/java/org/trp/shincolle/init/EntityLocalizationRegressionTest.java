package org.trp.shincolle.init;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityLocalizationRegressionTest {
    private static final Path MOD_ENTITIES_SOURCE =
            Path.of("src/main/java/org/trp/shincolle/init/ModEntities.java");
    private static final List<Path> LANGUAGE_SOURCES = List.of(
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
    );
    private static final Pattern ENTITY_REGISTRATION_PATTERN =
            Pattern.compile("ENTITY_TYPES\\.register\\(\"([a-z0-9_]+)\"");

    @Test
    void registeredEntityNamesShouldRemainLocalizedInMaintainedLanguages() throws IOException {
        String modEntities = Files.readString(MOD_ENTITIES_SOURCE);
        List<String> entityIds = readRegisteredEntityIds(modEntities);

        assertTrue(!entityIds.isEmpty(),
                "Expected ModEntities to keep declaring registered entity ids");

        for (String entityId : entityIds) {
            String key = "entity.shincolle." + entityId;
            assertLocalizedInMaintainedLanguages(key);
        }
    }

    private static List<String> readRegisteredEntityIds(String modEntities) {
        List<String> entityIds = new ArrayList<>();
        Matcher matcher = ENTITY_REGISTRATION_PATTERN.matcher(modEntities);
        while (matcher.find()) {
            entityIds.add(matcher.group(1));
        }
        return entityIds;
    }

    private static void assertLocalizedInMaintainedLanguages(String key) throws IOException {
        for (Path languageSource : LANGUAGE_SOURCES) {
            String source = Files.readString(languageSource);
            assertTrue(source.contains("\"" + key + "\""),
                    () -> "Expected maintained languages to define " + key);
        }
    }
}
