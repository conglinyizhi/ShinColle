package org.trp.shincolle.init;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
    private static final List<String> ENTITY_KEYS = List.of(
            "entity.shincolle.abyss_missile",
            "entity.shincolle.carrier_w_demon",
            "entity.shincolle.mount_is_h",
            "entity.shincolle.mount_mi_h",
            "entity.shincolle.mount_su_h",
            "entity.shincolle.projectile_beam",
            "entity.shincolle.ship_fishing_hook",
            "entity.shincolle.ship_grudge",
            "entity.shincolle.takoyaki"
    );

    @Test
    void registeredEntityNamesShouldRemainLocalizedInMaintainedLanguages() throws IOException {
        String modEntities = Files.readString(MOD_ENTITIES_SOURCE);

        for (String key : ENTITY_KEYS) {
            assertRegisteredEntitySourceStillUses(key, modEntities);
            assertLocalizedInMaintainedLanguages(key);
        }
    }

    private static void assertRegisteredEntitySourceStillUses(String key, String modEntities) {
        String entityId = key.substring("entity.shincolle.".length());
        assertTrue(modEntities.contains("register(\"" + entityId + "\""),
                () -> "Expected ModEntities to keep registering entity " + entityId);
    }

    private static void assertLocalizedInMaintainedLanguages(String key) throws IOException {
        for (Path languageSource : LANGUAGE_SOURCES) {
            String source = Files.readString(languageSource);
            assertTrue(source.contains("\"" + key + "\""),
                    () -> "Expected maintained languages to define " + key);
        }
    }
}
