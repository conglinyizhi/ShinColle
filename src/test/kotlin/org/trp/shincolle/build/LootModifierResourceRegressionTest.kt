package org.trp.shincolle.build

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.LinkedHashSet
import java.util.TreeSet
import java.util.regex.Matcher
import java.util.regex.Pattern
import org.junit.jupiter.api.Assertions.assertTrue

class LootModifierResourceRegressionTest {
    private val LOOT_MODIFIER_ROOT: Path =
        Path.of("src/main/resources/data/shincolle/loot_modifiers")
    private val GLOBAL_LOOT_MODIFIERS: Path =
        Path.of("src/main/resources/data/neoforge/loot_modifiers/global_loot_modifiers.json")

    private val TYPE_PATTERN: Pattern =
        Pattern.compile("\"type\"\\s*:\\s*\"([^\"]+)\"")
    private val CATEGORY_PATTERN: Pattern =
        Pattern.compile("\"category\"\\s*:\\s*\"([^\"]+)\"")
    private val LOOT_TABLE_ID_PATTERN: Pattern =
        Pattern.compile("\"loot_table_id\"\\s*:\\s*\"([^\"]+)\"")
    private val ENTRY_PATTERN: Pattern =
        Pattern.compile("\"(shincolle:[^\"]+)\"")

    private val EXPECTED_FILE_CATEGORIES = mapOf(
        "chest_inject_spawn_bonus.json" to "id:0",
        "chest_inject_igloo.json" to "id:1",
        "chest_inject_dungeon.json" to "id:2",
        "chest_inject_village.json" to "id:3",
        "chest_inject_mineshaft.json" to "id:4",
        "chest_inject_pyramid.json" to "id:5",
        "chest_inject_jungle_temple.json" to "id:6",
        "chest_inject_nether_bridge.json" to "id:7",
        "chest_inject_stronghold.json" to "id:8",
        "chest_inject_end_city.json" to "id:9"
    )

    private val EXPECTED_FILE_TARGETS = mapOf(
        "chest_inject_spawn_bonus.json" to setOf("minecraft:chests/spawn_bonus_chest"),
        "chest_inject_igloo.json" to setOf("minecraft:chests/igloo_chest"),
        "chest_inject_dungeon.json" to setOf("minecraft:chests/simple_dungeon"),
        "chest_inject_village.json" to setOf(
            "minecraft:chests/village/village_armorer",
            "minecraft:chests/village/village_butcher",
            "minecraft:chests/village/village_cartographer",
            "minecraft:chests/village/village_desert_house",
            "minecraft:chests/village/village_fisher",
            "minecraft:chests/village/village_fletcher",
            "minecraft:chests/village/village_mason",
            "minecraft:chests/village/village_plains_house",
            "minecraft:chests/village/village_savanna_house",
            "minecraft:chests/village/village_shepherd",
            "minecraft:chests/village/village_snowy_house",
            "minecraft:chests/village/village_taiga_house",
            "minecraft:chests/village/village_tannery",
            "minecraft:chests/village/village_temple",
            "minecraft:chests/village/village_toolsmith",
            "minecraft:chests/village/village_weaponsmith"
        ),
        "chest_inject_mineshaft.json" to setOf("minecraft:chests/abandoned_mineshaft"),
        "chest_inject_pyramid.json" to setOf("minecraft:chests/desert_pyramid"),
        "chest_inject_jungle_temple.json" to setOf("minecraft:chests/jungle_temple"),
        "chest_inject_nether_bridge.json" to setOf("minecraft:chests/nether_bridge"),
        "chest_inject_stronghold.json" to setOf(
            "minecraft:chests/stronghold_corridor",
            "minecraft:chests/stronghold_crossing",
            "minecraft:chests/stronghold_library"
        ),
        "chest_inject_end_city.json" to setOf("minecraft:chests/end_city_treasure")
    )

    private val EXPECTED_GLOBAL_ENTRIES = setOf(
        "shincolle:chest_inject_spawn_bonus",
        "shincolle:chest_inject_igloo",
        "shincolle:chest_inject_dungeon",
        "shincolle:chest_inject_village",
        "shincolle:chest_inject_mineshaft",
        "shincolle:chest_inject_pyramid",
        "shincolle:chest_inject_jungle_temple",
        "shincolle:chest_inject_nether_bridge",
        "shincolle:chest_inject_stronghold",
        "shincolle:chest_inject_end_city"
    )

    @Test
    fun lootModifierFilesShouldStayWithinKnownLegacyChestInjectionSet() {
        val actual = TreeSet<String>()
        val stream = Files.list(LOOT_MODIFIER_ROOT)
        try {
            stream
                .filter { path -> Files.isRegularFile(path) }
                .filter { file -> file.toString().endsWith(".json") }
                .forEach { path ->
                    actual.add(path.fileName.toString())
                }
        } finally {
            stream.close()
        }

        assertTrue(actual == TreeSet(EXPECTED_FILE_CATEGORIES.keys)) {
            "Legacy chest loot modifier file set changed unexpectedly, found: " +
                actual.joinToString(", ")
        }
    }

    @Test
    fun lootModifierFilesShouldKeepExpectedTypeCategoryAndLootTableTargets() {
        val issues = ArrayList<String>()

        for ((fileName, expectedCategory) in EXPECTED_FILE_CATEGORIES) {
            val file = LOOT_MODIFIER_ROOT.resolve(fileName)
            val content = Files.readString(file)

            val type = readFirst(TYPE_PATTERN, content)
            if ("shincolle:legacy_chest_loot" != type) {
                issues.add(fileName + " should keep type shincolle:legacy_chest_loot but was " + type)
            }

            val category = readFirst(CATEGORY_PATTERN, content)
            if (expectedCategory != category) {
                issues.add(fileName + " should keep category " + expectedCategory + " but was " + category)
            }

            val lootTables = readAll(LOOT_TABLE_ID_PATTERN, content)
            if (lootTables != EXPECTED_FILE_TARGETS[fileName]) {
                issues.add(fileName + " should keep loot table targets " +
                    TreeSet(EXPECTED_FILE_TARGETS[fileName]!!).joinToString(", ")
                    + " but found " +
                    TreeSet(lootTables).joinToString(", "))
            }
        }

        assertTrue(issues.isEmpty()) {
            "Legacy chest loot modifier resources changed unexpectedly: " + issues.joinToString(", ")
        }
    }

    @Test
    fun globalLootModifierIndexShouldListEveryLegacyChestInjectionFile() {
        val content = Files.readString(GLOBAL_LOOT_MODIFIERS)
        val entries = readAll(ENTRY_PATTERN, content)
        val expected = TreeSet(EXPECTED_GLOBAL_ENTRIES)

        assertTrue(content.contains("\"replace\": false")) {
            "Global loot modifier index should keep append semantics with replace=false"
        }
        assertTrue(entries == expected) {
            "Global loot modifier index changed unexpectedly, found: " +
                TreeSet(entries).joinToString(", ")
        }
    }

    @Test
    fun everyIndexedLootModifierShouldResolveToAnExistingJsonFile() {
        val missing = LinkedHashSet<String>()
        for (entry in EXPECTED_GLOBAL_ENTRIES) {
            val path = entry.substring("shincolle:".length) + ".json"
            if (!Files.exists(LOOT_MODIFIER_ROOT.resolve(path))) {
                missing.add(entry)
            }
        }

        assertTrue(missing.isEmpty()) {
            "Every indexed global loot modifier should resolve to a json file, missing: " +
                missing.joinToString(", ")
        }
    }

    private fun readFirst(pattern: Pattern, content: String): String? {
        val matcher: Matcher = pattern.matcher(content)
        return if (matcher.find()) matcher.group(1)!! else null
    }

    private fun readAll(pattern: Pattern, content: String): Set<String> {
        val values = TreeSet<String>()
        val matcher: Matcher = pattern.matcher(content)
        while (matcher.find()) {
            values.add(matcher.group(1)!!)
        }
        return values
    }
}
