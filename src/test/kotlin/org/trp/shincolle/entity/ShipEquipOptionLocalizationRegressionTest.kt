package org.trp.shincolle.entity

import org.junit.jupiter.api.Test

import java.nio.file.Files
import java.nio.file.Path

import org.junit.jupiter.api.Assertions.assertTrue

class ShipEquipOptionLocalizationRegressionTest {
    private val EQUIP_OPTION_SOURCES: List<Path> = listOf(
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
    )
    private val LANGUAGE_SOURCES: List<Path> = listOf(
            Path.of("src/main/resources/assets/shincolle/lang/en_us.json"),
            Path.of("src/main/resources/assets/shincolle/lang/ja_jp.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_cn.json"),
            Path.of("src/main/resources/assets/shincolle/lang/zh_tw.json")
    )
    private val EQUIP_OPTION_KEYS: List<String> = listOf(
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
    )

    @Test
    fun shipEquipOptionLabelsShouldRemainLocalizedInMaintainedLanguages() {
        for (key in EQUIP_OPTION_KEYS) {
            assertUsedByEquipOptionSource(key)
            assertLocalizedInMaintainedLanguages(key)
        }
    }

    private fun assertUsedByEquipOptionSource(key: String) {
        var found = false
        for (sourcePath in EQUIP_OPTION_SOURCES) {
            val source = Files.readString(sourcePath)
            if (source.contains("\"" + key + "\"")) {
                found = true
                break
            }
        }
        assertTrue(found) { "Expected at least one ship equip option source to keep using $key" }
    }

    private fun assertLocalizedInMaintainedLanguages(key: String) {
        for (languageSource in LANGUAGE_SOURCES) {
            val source = Files.readString(languageSource)
            assertTrue(source.contains("\"" + key + "\"")) {
                "Expected maintained languages to define $key"
            }
        }
    }
}
