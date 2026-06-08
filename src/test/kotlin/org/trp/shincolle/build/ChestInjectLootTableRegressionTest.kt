package org.trp.shincolle.build

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.TreeSet
import java.util.regex.Pattern
import java.util.stream.Stream

class ChestInjectLootTableRegressionTest {
    private val CHEST_LOOT_ROOT: Path =
            Path.of("src/main/resources/data/shincolle/loot_table/inject/chests")
    private val LEGACY_CHEST_LOOT_MODIFIER: Path =
            Path.of("src/main/java/org/trp/shincolle/loot/LegacyChestLootModifier.kt")
    private val MOD_ITEMS: Path =
            Path.of("src/main/java/org/trp/shincolle/init/ModItems.kt")
    private val CONFIG: Path =
            Path.of("src/main/java/org/trp/shincolle/Config.kt")

    private val TYPE_PATTERN: Pattern =
            Pattern.compile("\"type\"\\s*:\\s*\"([^\"]+)\"")
    private val NAME_PATTERN: Pattern =
            Pattern.compile("\"name\"\\s*:\\s*\"(shincolle:[^\"]+)\"")
    private val POOL_NAME_PATTERN: Pattern =
            Pattern.compile("\"name\"\\s*:\\s*\"([a-z0-9_]+)\"")
    private val CHANCE_PATTERN: Pattern =
            Pattern.compile("\"chance\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)")

    private val EXPECTED_CHEST_LOOT_FILES: Set<String> = setOf(
            "basic.json",
            "mid.json",
            "high.json",
            "trial.json"
    )

    private val EXPECTED_POOL_PREFIX: Map<String, String> = mapOf(
            "basic.json" to "shincolle_basic_",
            "mid.json" to "shincolle_mid_",
            "high.json" to "shincolle_high_",
            "trial.json" to "shincolle_trial_"
    )

    private val EXPECTED_ITEMS: Map<String, Set<String>> = mapOf(
            "basic.json" to setOf(
                    "shincolle:grudge",
                    "shincolle:ammo",
                    "shincolle:abyss_metal",
                    "shincolle:abyss_polymetal",
                    "shincolle:ammo1",
                    "shincolle:ammo2",
                    "shincolle:instantconmat",
                    "shincolle:destroyer_i_spawn_egg",
                    "shincolle:destroyer_ro_spawn_egg",
                    "shincolle:destroyer_ha_spawn_egg",
                    "shincolle:destroyer_ni_spawn_egg",
                    "shincolle:trainingbook",
                    "shincolle:marriagering"
            ),
            "mid.json" to setOf(
                    "shincolle:grudge",
                    "shincolle:ammo",
                    "shincolle:ammo1",
                    "shincolle:ammo2",
                    "shincolle:ammo3",
                    "shincolle:abyss_metal",
                    "shincolle:abyss_polymetal",
                    "shincolle:polymetal_gravel",
                    "shincolle:instantconmat",
                    "shincolle:heavy_cruiser_ri_spawn_egg",
                    "shincolle:heavy_cruiser_ne_spawn_egg",
                    "shincolle:cruiser_tenryuu_spawn_egg",
                    "shincolle:cruiser_tatsuta_spawn_egg",
                    "shincolle:subm_ka_spawn_egg",
                    "shincolle:subm_yo_spawn_egg",
                    "shincolle:subm_so_spawn_egg",
                    "shincolle:trainingbook",
                    "shincolle:marriagering"
            ),
            "high.json" to setOf(
                    "shincolle:grudge",
                    "shincolle:ammo",
                    "shincolle:ammo1",
                    "shincolle:ammo2",
                    "shincolle:ammo3",
                    "shincolle:abyss_metal",
                    "shincolle:abyss_polymetal",
                    "shincolle:polymetal",
                    "shincolle:polymetal_gravel",
                    "shincolle:instantconmat",
                    "shincolle:carrier_wo_spawn_egg",
                    "shincolle:battleship_ru_spawn_egg",
                    "shincolle:battleship_ta_spawn_egg",
                    "shincolle:battleship_re_spawn_egg",
                    "shincolle:transport_wa_spawn_egg",
                    "shincolle:northern_hime_spawn_egg",
                    "shincolle:pointer_item",
                    "shincolle:trainingbook",
                    "shincolle:marriagering"
            ),
            "trial.json" to setOf(
                    "shincolle:ammo",
                    "shincolle:ammo1",
                    "shincolle:ammo2",
                    "shincolle:ammo3",
                    "shincolle:grudge",
                    "shincolle:abyss_metal",
                    "shincolle:abyss_polymetal",
                    "shincolle:instantconmat",
                    "shincolle:destroyer_shimakaze_spawn_egg",
                    "shincolle:cruiser_takao_spawn_egg",
                    "shincolle:cruiser_atago_spawn_egg",
                    "shincolle:battleship_nagato_spawn_egg",
                    "shincolle:battleship_yamato_spawn_egg",
                    "shincolle:subm_u511_spawn_egg",
                    "shincolle:subm_ro500_spawn_egg",
                    "shincolle:trainingbook",
                    "shincolle:marriagering"
            )
    )

    @Test
    fun chestInjectLootTableFilesShouldStayWithinKnownLegacySet() {
        val actual = listJsonNames(CHEST_LOOT_ROOT)
        assertTrue(actual.equals(TreeSet(EXPECTED_CHEST_LOOT_FILES))) {
            "Chest inject loot table file set changed unexpectedly, found: " +
                    actual.joinToString(", ")
        }
    }

    @Test
    fun chestInjectLootTablesShouldKeepExpectedStructureAndResolvableDrops() {
        val modItems = Files.readString(MOD_ITEMS)
        val issues = ArrayList<String>()

        for (fileName in EXPECTED_CHEST_LOOT_FILES) {
            val content = Files.readString(CHEST_LOOT_ROOT.resolve(fileName))
            val type = readFirst(TYPE_PATTERN, content)
            val itemNames = readAll(NAME_PATTERN, content)
            val poolNames = readAll(POOL_NAME_PATTERN, content)
            val chances = readNumericStrings(CHANCE_PATTERN, content)

            if ("minecraft:chest" != type) {
                issues.add(fileName + " should keep loot table type minecraft:chest but was " + type)
            }
            if (!itemNames.equals(TreeSet(EXPECTED_ITEMS[fileName]!!))) {
                issues.add(fileName + " should keep expected drop set, found: " +
                        itemNames.joinToString(", "))
            }
            for (itemName in itemNames) {
                if (!itemRegistrationExists(modItems, itemName)) {
                    issues.add(fileName + " references missing registered item " + itemName)
                }
            }
            if (!poolNames.contains(EXPECTED_POOL_PREFIX[fileName]!! + "common")
                    || !poolNames.contains(EXPECTED_POOL_PREFIX[fileName]!! + "rare")) {
                issues.add(fileName + " should keep common/rare pool names with prefix " + EXPECTED_POOL_PREFIX[fileName]!!)
            }
            if (poolNames.size != 2) {
                issues.add(fileName + " should keep exactly 2 named pools but found " + poolNames.size)
            }
            if (chances.size != 2) {
                issues.add(fileName + " should keep exactly 2 random chance gates but found " + chances.size)
            }
        }

        assertTrue(issues.isEmpty()) {
            "Chest inject loot tables changed unexpectedly: " + issues.joinToString(", ")
        }
    }

    @Test
    fun legacyChestLootModifierShouldKeepCurrentCategoryRanges() {
        val source = Files.readString(LEGACY_CHEST_LOOT_MODIFIER)
        val config = Files.readString(CONFIG)

        assertTrue(source.contains("case \"basic\" -> chestId >= 0 && chestId <= 3;")) {
            "basic chest category should continue covering chestId 0..3"
        }
        assertTrue(source.contains("case \"mid\" -> chestId >= 4 && chestId <= 8;")) {
            "mid chest category should continue covering chestId 4..8"
        }
        assertTrue(source.contains("case \"high\" -> chestId == 9;")) {
            "high chest category should continue mapping only chestId 9"
        }
        assertTrue(source.contains("case \"trial\" -> chestId >= 7 && chestId <= 9;")) {
            "trial chest category should continue covering chestId 7..9"
        }

        for (chestId in 0..9) {
            assertTrue(config.contains("entries.add(\"" + chestId + ",shincolle:")) {
                "Default legacy loot config should keep entries for chestId " + chestId
            }
        }
    }

    private fun itemRegistrationExists(modItems: String, resourceLocation: String): Boolean {
        val path = resourceLocation.substring("shincolle:".length)
        return modItems.contains("ITEMS.register(\"" + path + "\"")
    }

    private fun listJsonNames(root: Path): Set<String> {
        Files.list(root).use { stream ->
            val names = TreeSet<String>()
            for (path in stream
                    .filter(Files::isRegularFile)
                    .filter { file -> file.toString().endsWith(".json") }
                    .toList()) {
                names.add(path.fileName.toString())
            }
            return names
        }
    }

    private fun readFirst(pattern: Pattern, content: String): String? {
        val matcher = pattern.matcher(content)
        return if (matcher.find()) matcher.group(1)!! else null
    }

    private fun readAll(pattern: Pattern, content: String): Set<String> {
        val values = TreeSet<String>()
        val matcher = pattern.matcher(content)
        while (matcher.find()) {
            values.add(matcher.group(1)!!)
        }
        return values
    }

    private fun readNumericStrings(pattern: Pattern, content: String): Set<String> {
        val values = TreeSet<String>()
        val matcher = pattern.matcher(content)
        while (matcher.find()) {
            values.add(matcher.group(1)!!)
        }
        return values
    }
}
