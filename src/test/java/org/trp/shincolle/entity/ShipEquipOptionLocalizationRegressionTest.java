package org.trp.shincolle.entity;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipEquipOptionLocalizationRegressionTest {
    private static final List<Path> EQUIP_OPTION_SOURCES = List.of(
            Path.of("src/main/java/org/trp/shincolle/entity/EntityCarrierWo.kt"),
            Path.of("src/main/java/org/trp/shincolle/entity/EntityCarrierAkagi.kt"),
            Path.of("src/main/java/org/trp/shincolle/entity/EntityCarrierKaga.kt"),
            Path.of("src/main/java/org/trp/shincolle/entity/EntityAirfieldHime.kt"),
            Path.of("src/main/java/org/trp/shincolle/entity/EntityDestroyerShimakaze.kt"),
            Path.of("src/main/java/org/trp/shincolle/entity/EntityDestroyerAkatsuki.kt"),
            Path.of("src/main/java/org/trp/shincolle/entity/EntityDestroyerI.kt"),
            Path.of("src/main/java/org/trp/shincolle/entity/EntityDestroyerHa.kt"),
            Path.of("src/main/java/org/trp/shincolle/entity/EntityDestroyerNi.kt"),
            Path.of("src/main/java/org/trp/shincolle/entity/EntityDestroyerRo.kt"),
            Path.of("src/main/java/org/trp/shincolle/entity/EntityBattleshipRu.kt"),
            Path.of("src/main/java/org/trp/shincolle/entity/EntityBattleshipTa.kt")
    );
    private static final List<Path> LANGUAGE_SOURCES = List.of(
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
    );
    private static final List<String> EQUIP_OPTION_KEYS = List.of(
            "gui.shincolle.equip.armor",
            "gui.shincolle.equip.back_quiver",
            "gui.shincolle.equip.bow",
            "gui.shincolle.equip.breastplate",
            "gui.shincolle.equip.cat_parts",
            "gui.shincolle.equip.cloakneck",
            "gui.shincolle.equip.deck_hand",
            "gui.shincolle.equip.eye_effect",
            "gui.shincolle.equip.equipbase",
            "gui.shincolle.equip.glowequipbase",
            "gui.shincolle.equip.head_ornament",
            "gui.shincolle.equip.neck",
            "gui.shincolle.equip.pose1",
            "gui.shincolle.equip.pose2",
            "gui.shincolle.equip.rensouhou_type",
            "gui.shincolle.equip.shoulder_searchlight",
            "gui.shincolle.equip.skirt",
            "gui.shincolle.equip.staff"
    );

    @Test
    void shipEquipOptionLabelsShouldRemainLocalizedInMaintainedLanguages() throws IOException {
        for (String key : EQUIP_OPTION_KEYS) {
            assertUsedByEquipOptionSource(key);
            assertLocalizedInMaintainedLanguages(key);
        }
    }

    private static void assertUsedByEquipOptionSource(String key) throws IOException {
        boolean found = false;
        for (Path sourcePath : EQUIP_OPTION_SOURCES) {
            String source = Files.readString(sourcePath);
            if (source.contains("\"" + key + "\"")) {
                found = true;
                break;
            }
        }
        assertTrue(found, () -> "Expected at least one ship equip option source to keep using " + key);
    }

    private static void assertLocalizedInMaintainedLanguages(String key) throws IOException {
        for (Path languageSource : LANGUAGE_SOURCES) {
            String source = Files.readString(languageSource);
            assertTrue(source.contains("\"" + key + "\""),
                    () -> "Expected maintained languages to define " + key);
        }
    }
}
